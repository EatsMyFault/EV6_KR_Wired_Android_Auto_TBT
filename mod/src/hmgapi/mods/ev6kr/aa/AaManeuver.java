package hmgapi.mods.ev6kr.aa;

/**
 * Android Auto maneuver icons for the Korean Gen5W cluster.
 * Icon and angle mappings adapted from HMG-API AaTurns (VysseCodes)
 * and the Korea Projection changes (StopDragon), AGPL-3.0.
 * https://github.com/StopDragon/HMG-API/blob/6b7211051f9d5edddf1b627fc92d71719bb6aa98/mods/wireless-carplay-aa/src/hmgapi/mods/wireless/AaTurns.java
 */
final class AaManeuver {
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int UNSPECIFIED = 3;

    private static final int SLIGHT_TURN = 3;
    private static final int TURN = 4;
    private static final int SHARP_TURN = 5;
    private static final int U_TURN = 6;
    private static final int ON_RAMP = 7;
    private static final int OFF_RAMP = 8;
    private static final int FORK = 9;
    private static final int MERGE = 10;
    private static final int ROUNDABOUT_EXIT = 12;
    private static final int ROUNDABOUT_ENTER_AND_EXIT = 13;
    private static final int STRAIGHT = 14;
    private static final int DESTINATION = 19;

    final int icon;
    final int direction;

    private AaManeuver(int icon, int direction) {
        this.icon = icon;
        this.direction = direction;
    }

    /** The exit number alone cannot determine a roundabout exit angle. */
    static AaManeuver from(int event, int side, int angle, int number) {
        switch (event) {
            case STRAIGHT:
            case MERGE:
                return new AaManeuver(65, 0);
            case SLIGHT_TURN:
                return sided(side, 71, 330, 66, 30);
            case TURN:
                return sided(side, 70, 270, 67, 90);
            case SHARP_TURN:
                return sided(side, 69, 225, 68, 135);
            case U_TURN:
                // AaTurns uses a left-facing U-turn on Korean roads when the side is unspecified.
                return sided(side == UNSPECIFIED ? LEFT : side, 72, 270, 73, 270);
            case ON_RAMP:
                if (side == UNSPECIFIED) return new AaManeuver(160, 0);
                return sided(side, 162, 330, 161, 30);
            case OFF_RAMP:
                if (side == UNSPECIFIED) return new AaManeuver(163, 0);
                return sided(side, 165, 330, 164, 30);
            case FORK:
                return sided(side, 174, 0, 173, 0);
            case ROUNDABOUT_EXIT:
                return sided(side, 33, 0, 32, 0);
            case ROUNDABOUT_ENTER_AND_EXIT:
                return roundabout(angle);
            case DESTINATION:
                return new AaManeuver(3, 0);
            default:
                // In particular, a road-name change alone does not establish a maneuver.
                return null;
        }
    }

    private static AaManeuver sided(int side, int leftIcon, int leftDirection,
            int rightIcon, int rightDirection) {
        if (side == LEFT) return new AaManeuver(leftIcon, leftDirection);
        if (side == RIGHT) return new AaManeuver(rightIcon, rightDirection);
        return null;
    }

    private static AaManeuver roundabout(int angle) {
        if (angle < 0 || angle > 360) return null;
        if (angle < 45 || angle > 315) return new AaManeuver(102, 180);
        if (angle < 90) return new AaManeuver(100, 135);
        if (angle < 135) return new AaManeuver(99, 90);
        if (angle < 180) return new AaManeuver(97, 45);
        if (angle == 180) return new AaManeuver(96, 0);
        if (angle <= 225) return new AaManeuver(106, 315);
        if (angle <= 270) return new AaManeuver(105, 270);
        return new AaManeuver(103, 225);
    }
}
