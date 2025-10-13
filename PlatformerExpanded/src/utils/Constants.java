package utils;

public class Constants {
    // Level dimensions
    public static final int LEVEL_WIDTH = 4000;
    public static final int LEVEL_HEIGHT = 1000;
    public static final int GROUND_LEVEL = 710;

    // Player-specific animation constants
    public static class PlayerActions {
        public static final int IDLE = 0;
        public static final int RUN = 1;
        public static final int JUMP = 2;
        public static final int HIT = 3;
        public static final int DEATH = 4;
        public static final int AIR_ATTACK = 5;
        public static final int COMBO = 6;
        public static final int DASH = 7;
        public static final int SLIDE = 8;
    }

    // Enemy-specific animation constants
    public static class EnemyActions {
        public static final int IDLE = 0;
        public static final int WALK = 1;
        public static final int FIGHT = 2;
        public static final int HIT = 3;
        public static final int DEATH = 4;
    }

    // Camera
    public static final int CAMERA_OFFSET_X = 400;
    public static final int CAMERA_OFFSET_Y = 300;
    
    // Portal
    public static final int PORTAL_X = 930;
    public static final int PORTAL_Y = 450;
    
    // FPS
    public static final int TARGET_FPS = 120;

    // Attack constants
    public static final int HIT_COOLDOWN = 30; // Frames
    public static final int ATTACK_COOLDOWN = 60; // Frames
    public static final int ATTACK_RANGE = 150; // Pixels
}
