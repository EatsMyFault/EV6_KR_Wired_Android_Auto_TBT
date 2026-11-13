package hmgapi.mods.ev6kr.aa;

import android.content.Context;
import android.os.Handler;

import java.lang.reflect.Field;

import hmgapi.hook.Car;
import hmgapi.hook.Cluster;
import hmgapi.hook.LoadedApp;
import hmgapi.hook.MethodHook;
import hmgapi.hook.MethodHook.Param;
import hmgapi.hook.ModContext;
import hmgapi.hook.ModEntry;
import hmgapi.hook.Points;

public final class WiredAndroidAutoTbt extends ModEntry {
    private static final String GPM = "com.google.android.projection.sink";
    private static final String LOG_PREFIX = "AA_TBT_010 ";
    private static final int HUD_NO_POINT = 65535;
    private static final long FRESHNESS_MS = 8000L;

    private volatile Session session;

    @Override
    public void appLoaded(ModContext mod, LoadedApp app) {
        if (!GPM.equals(app.packageName)) return;
        int spec = Car.stepOfSpec();
        if (spec >= 999) {
            mod.log(LOG_PREFIX + "GPM_SKIPPED native_tbt stepOfSpec=" + spec);
            return;
        }

        ClassLoader loader = app.classLoader;
        try {
            Object image = Points.get(loader, "gpm.cluster_type.image", null);
            Object enums = Points.get(loader, "gpm.cluster_type.enum", null);
            Object focus = Points.get(loader, "gpm.receiver.focus_native", null);
            Field receiver = Points.field(loader, "gpm.navigation_status.receiver");
            if (!(image instanceof Enum<?>) || !(enums instanceof Enum<?>) || image == enums
                    || !(focus instanceof Integer)) {
                mod.log(LOG_PREFIX + "GPM_SKIPPED cluster_or_focus_constants_unavailable");
                return;
            }
            Session current = new Session(mod, app.context, receiver, (Integer) focus);
            session = current;
            int hooks = observe(mod, loader, "gpm.sensor_source.init", current::sensor)
                    + observe(mod, loader, "gpm.navigation_status.status", current::status)
                    + observe(mod, loader, "gpm.navigation_status.next_turn", current::turn)
                    + observe(mod, loader, "gpm.navigation_status.next_turn_distance", current::distance)
                    + observe(mod, loader, "gpm.receiver.send_navigation_focus", current::focus)
                    + observe(mod, loader, "gpm.receiver.destroy", p -> current.end(p.thisObject, "receiver"))
                    + observe(mod, loader, "gpm.receiver_service.disconnected", p -> current.end(null, "service"));
            if (hooks == 7) {
                hooks += register(mod, loader, "gpm.navigation_status.init", new MethodHook() {
                    @Override
                    protected void beforeHookedMethod(Param p) {
                        if (p.args[3] == image) {
                            p.args[3] = enums;
                            current.log("AA_FORMAT from=IMAGE to=ENUM");
                        }
                    }

                    @Override
                    protected void afterHookedMethod(Param p) {
                        current.init(p, p.args[3] == enums);
                    }
                });
            }
            current.log("GPM_READY hooks=" + hooks + "/8 stepOfSpec=" + spec);
        } catch (RuntimeException | LinkageError e) {
            mod.error(LOG_PREFIX + "GPM_SETUP_FAILED " + e.getClass().getSimpleName(), null);
        }
    }

    @Override
    public void onEnabled(ModContext mod, boolean enabled) {
        Session current = session;
        if (current != null) current.enabledChanged(enabled);
    }

    private interface Callback {
        void record(Param p);
    }

    private static int observe(ModContext mod, ClassLoader loader, String point, Callback callback) {
        return register(mod, loader, point, new MethodHook() {
            @Override
            protected void afterHookedMethod(Param p) {
                if (!p.hasThrowable()) callback.record(p);
            }
        });
    }

    private static int register(ModContext mod, ClassLoader loader, String point, MethodHook hook) {
        try {
            mod.hook(loader, point, hook);
            return 1;
        } catch (RuntimeException | LinkageError e) {
            mod.error(LOG_PREFIX + "GPM_HOOK_FAILED point=" + point
                    + " error=" + e.getClass().getSimpleName(), null);
            return 0;
        }
    }

    private static final class Session implements AaGuidance.Output {
        private final ModContext mod;
        private final Context context;
        private final Field receiver;
        private final int nativeFocus;
        private final Handler handler;
        private final AaGuidance guidance = new AaGuidance(this);
        private final Runnable expire = () -> {
            log("AA_DISPLAY_EXPIRED");
            guidance.expire();
        };
        private volatile int epoch;

        private Cluster cluster;
        private int shownIcon = -1;
        private int shownMeters = -1;
        private int turnCount;
        private int distanceCount;
        private String lastTurn;
        private boolean sawInit;

        Session(ModContext mod, Context context, Field receiver, int nativeFocus) {
            this.mod = mod;
            this.context = context;
            this.receiver = receiver;
            this.nativeFocus = nativeFocus;
            this.handler = new Handler(context.getMainLooper());
        }

        void sensor(Param p) {
            log("AA_SENSOR_INIT result=" + Boolean.TRUE.equals(p.getResult()));
        }

        void init(Param p, boolean enumFormat) {
            Object clusterType = p.args[3];
            String format = clusterType instanceof Enum<?> ? ((Enum<?>) clusterType).name() : "unknown";
            Object owner = owner(p);
            boolean accepted = !p.hasThrowable() && Boolean.TRUE.equals(p.getResult());
            post(() -> {
                log("AA_NAV_INIT sent=" + format + " result=" + accepted + " receiver=" + (owner != null));
                sawInit = true;
                turnCount = 0;
                distanceCount = 0;
                lastTurn = null;
                if (enumFormat && accepted && owner != null) guidance.start(owner);
                else guidance.reset();
            });
        }

        void status(Param p) {
            Object owner = owner(p);
            int code = (Integer) p.args[0];
            post(() -> {
                log("AA_STATUS code=" + code);
                guidance.status(owner, code);
            });
        }

        void turn(Param p) {
            Object owner = owner(p);
            int side = (Integer) p.args[1];
            int event = (Integer) p.args[2];
            int angle = (Integer) p.args[4];
            int number = (Integer) p.args[5];
            // Extended events are not the next maneuver.
            if (event == 32 || event == 33 || event == 34 || (event >= 64 && event <= 67)) return;
            post(() -> {
                String key = "side=" + side + " event=" + event + " angle=" + angle + " number=" + number;
                if (sample(++turnCount) || !key.equals(lastTurn)) log("AA_TURN count=" + turnCount + " " + key);
                lastTurn = key;
                guidance.turn(owner, event, side, angle, number);
            });
        }

        void distance(Param p) {
            int flags = (Integer) p.args[3];
            if ((flags & 0x300) != 0) return; // Extended route-total and upcoming-turn distances.
            Object owner = owner(p);
            int meters = (Integer) p.args[0];
            post(() -> {
                if (sample(++distanceCount)) log("AA_DISTANCE count=" + distanceCount + " meters=" + meters);
                guidance.distance(owner, meters);
            });
        }

        void focus(Param p) {
            Object owner = p.thisObject;
            boolean nativeNav = ((Integer) p.args[0]) == nativeFocus;
            post(() -> {
                log("AA_FOCUS native=" + nativeNav);
                guidance.focus(owner, nativeNav);
            });
        }

        void end(Object owner, String source) {
            post(() -> {
                guidance.end(owner);
                if (!sawInit) return;
                log("AA_DISCONNECTED source=" + source + " turns=" + turnCount + " distances=" + distanceCount);
                sawInit = false;
            });
        }

        void enabledChanged(boolean enabled) {
            epoch++;
            handler.post(() -> {
                guidance.reset();
                sawInit = false;
                log("AA_MOD enabled=" + enabled);
            });
        }

        private Object owner(Param p) {
            try {
                return receiver.get(p.thisObject);
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        private void post(Runnable action) {
            int queuedEpoch = epoch;
            handler.post(() -> {
                if (epoch == queuedEpoch && mod.enabled()) action.run();
            });
        }

        @Override
        public boolean show(AaManeuver turn, int meters) {
            if (!mod.enabled() || Car.carPlayGuiding(context)) return false;
            if (cluster == null) cluster = Cluster.of(context);
            if (cluster == null) {
                log("AA_DISPLAY_UNAVAILABLE cluster_service");
                return false;
            }

            int unit = meters < 1000 ? Cluster.UNIT_M : Cluster.UNIT_KM;
            float distance = meters < 1000 ? meters : meters / 1000f;
            boolean newIcon = shownIcon != turn.icon;
            if (newIcon || shownMeters != meters) {
                cluster.tbtInfo(turn.icon, 0, 0f, distance, 0, 0, unit, 0, -1);
                if (turn.icon >= 64) {
                    if (newIcon) cluster.turnListNew(new byte[] {(byte) 0xF2, 1, (byte) turn.icon, 0, 0});
                    int whole = meters < 1000 ? meters : meters / 1000;
                    int tenths = meters < 1000 ? 0 : Math.min(9, (meters % 1000 + 50) / 100);
                    cluster.tbtSubInfo2C(unit, tenths, whole, 0, 0, HUD_NO_POINT, 0, 0, HUD_NO_POINT);
                }
                if (newIcon) log("AA_DISPLAY_SENT icon=" + turn.icon + " meters=" + meters);
                shownIcon = turn.icon;
                shownMeters = meters;
            }
            handler.removeCallbacks(expire);
            handler.postDelayed(expire, FRESHNESS_MS);
            return true;
        }

        @Override
        public void clear() {
            handler.removeCallbacks(expire);
            if (shownIcon >= 0 && !Car.carPlayGuiding(context)) {
                cluster.tbtInfo(8, 0, 0f, 0f, -3, 0, 15, 0, -1);
                if (shownIcon >= 64) {
                    cluster.tbtSubInfo2C(15, 15, HUD_NO_POINT, 15, 15, HUD_NO_POINT, 15, 15, HUD_NO_POINT);
                    cluster.turnListNew(new byte[] {(byte) 0xF2, 0});
                    cluster.turnListNew(new byte[] {(byte) 0xF2, 0});
                }
                log("AA_DISPLAY_CLEARED");
            }
            shownIcon = -1;
            shownMeters = -1;
        }

        void log(String message) {
            mod.log(LOG_PREFIX + message);
        }

        private static boolean sample(int count) {
            return count <= 3 || count % 20 == 0;
        }
    }
}
