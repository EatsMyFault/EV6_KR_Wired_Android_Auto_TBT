package hmgapi.mods.ev6kr.aa;

/** Owns the displayed maneuver for one negotiated Android Auto receiver. */
final class AaGuidance {
    private static final int ACTIVE = 1;
    private static final int MAX_DISTANCE_METERS = 4_095_999;

    interface Output {
        boolean show(AaManeuver turn, int meters);
        void clear();
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

    AaGuidance(Output output) {
        this.output = output;
    }

    void start(Object receiver) {
        expire();
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
        else expire();
    }

    void turn(Object receiver, int event, int side, int angle, int number) {
        if (!canReceive(receiver)) return;
        AaManeuver next = AaManeuver.from(event, side, angle, number);
        if (next == null) {
            expire();
            return;
        }
        if (maneuver != null && maneuver.icon == next.icon && maneuver.direction == next.direction
                && maneuverEvent == event && maneuverNumber == number) return;
        expire();
        maneuver = next;
        maneuverEvent = event;
        maneuverNumber = number;
    }

    void distance(Object receiver, int meters) {
        if (!canReceive(receiver) || maneuver == null) return;
        if (meters < 0 || meters > MAX_DISTANCE_METERS) {
            expire();
            return;
        }
        this.meters = meters;
        publish();
    }

    void focus(Object receiver, boolean nativeFocus) {
        if (receiver == null || (this.receiver != null && !owns(receiver))) return;
        if (focusReceiver != receiver || this.nativeFocus != nativeFocus) {
            focusReceiver = receiver;
            this.nativeFocus = nativeFocus;
            if (nativeFocus) expire();
        }
    }

    void end(Object receiver) {
        if (receiver == null || owns(receiver)) reset();
        else if (focusReceiver == receiver) {
            focusReceiver = null;
            nativeFocus = false;
        }
    }

    void reset() {
        expire();
        receiver = null;
        focusReceiver = null;
        nativeFocus = false;
        status = 0;
    }

    void expire() {
        maneuver = null;
        meters = -1;
        clear();
    }

    private boolean owns(Object receiver) {
        return receiver != null && this.receiver == receiver;
    }

    private boolean canReceive(Object receiver) {
        return owns(receiver) && !nativeFocus && (status == 0 || status == ACTIVE);
    }

    private void publish() {
        if (nativeFocus || status != ACTIVE || maneuver == null || meters < 0) return;
        if (output.show(maneuver, meters)) shown = true;
        else expire();
    }

    private void clear() {
        if (!shown) return;
        shown = false;
        output.clear();
    }
}
