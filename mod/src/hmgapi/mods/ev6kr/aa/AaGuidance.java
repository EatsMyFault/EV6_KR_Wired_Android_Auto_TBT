package hmgapi.mods.ev6kr.aa;

/** Owns the displayed maneuver for one negotiated Android Auto receiver. */
final class AaGuidance {
    private static final int ACTIVE = 1;
    private static final int MAX_DISTANCE_METERS = 4_095_999;
    private static final long RECOVERY_WINDOW_MS = 10_000L;

    interface Output {
        boolean show(AaManeuver turn, int meters);
        void clear(String reason);
    }

    private final Output output;
    private Object receiver;
    private Object focusReceiver;
    private boolean nativeFocus;
    private boolean shown;
    private int status;
    private AaManeuver maneuver;
    private int maneuverEvent;
    private int maneuverNumber;
    private int meters = -1;
    private long recoverUntilMs;

    AaGuidance(Output output) {
        this.output = output;
    }

    void start(Object receiver) {
        expire("receiver_init");
        this.receiver = receiver;
        status = 0;
        if (focusReceiver != receiver) {
            focusReceiver = null;
            nativeFocus = false;
        }
    }

    void status(Object receiver, int code) {
        if (!owns(receiver) || status == code) return;
        status = code;
        if (code == ACTIVE) publish();
        else expire(code == 3 ? "rerouting" : "inactive_status");
    }

    void turn(Object receiver, int event, int side, int angle, int number) {
        if (!canReceive(receiver)) return;
        AaManeuver next = AaManeuver.from(event, side, angle, number);
        if (next == null) {
            expire("unsupported_turn");
            return;
        }
        if (maneuver != null && maneuver.icon == next.icon && maneuver.direction == next.direction
                && maneuverEvent == event && maneuverNumber == number) {
            if (recoverUntilMs != 0) {
                recoverUntilMs = 0;
                meters = -1;
            }
            return;
        }
        expire("turn_changed");
        maneuver = next;
        maneuverEvent = event;
        maneuverNumber = number;
    }

    void distance(Object receiver, int meters, long nowMs) {
        if (!canReceive(receiver) || maneuver == null) return;
        if (meters < 0 || meters > MAX_DISTANCE_METERS) {
            expire("invalid_distance");
            return;
        }
        // A farther point may be a new turn. Wait for its own maneuver before drawing it.
        if (recoverUntilMs != 0 && (nowMs >= recoverUntilMs || meters > this.meters)) {
            expire("invalid_recovery");
            return;
        }
        recoverUntilMs = 0;
        this.meters = meters;
        publish();
    }

    void focus(Object receiver, boolean nativeFocus) {
        if (receiver == null || (this.receiver != null && !owns(receiver))) return;
        if (focusReceiver != receiver || this.nativeFocus != nativeFocus) {
            focusReceiver = receiver;
            this.nativeFocus = nativeFocus;
            if (nativeFocus) expire("native_focus");
        }
    }

    void end(Object receiver) {
        if (receiver == null || owns(receiver)) reset("disconnect");
        else if (focusReceiver == receiver) {
            focusReceiver = null;
            nativeFocus = false;
        }
    }

    void reset() {
        reset("reset");
    }

    private void reset(String reason) {
        expire(reason);
        receiver = null;
        focusReceiver = null;
        nativeFocus = false;
        status = 0;
    }

    void hide(long nowMs) {
        if (!shown) return;
        recoverUntilMs = nowMs + RECOVERY_WINDOW_MS;
        clear("distance_timeout");
    }

    private void expire(String reason) {
        maneuver = null;
        meters = -1;
        recoverUntilMs = 0;
        clear(reason);
    }

    boolean owns(Object receiver) {
        return receiver != null && this.receiver == receiver;
    }

    private boolean canReceive(Object receiver) {
        return owns(receiver) && !nativeFocus && (status == 0 || status == ACTIVE);
    }

    private void publish() {
        if (nativeFocus || status != ACTIVE || maneuver == null || meters < 0) return;
        if (output.show(maneuver, meters)) shown = true;
        else expire("output_unavailable");
    }

    private void clear(String reason) {
        if (!shown) return;
        shown = false;
        output.clear(reason);
    }
}
