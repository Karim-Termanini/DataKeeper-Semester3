package utils;

public class Constants {
    // Level dimensions
    public static final int LEVEL_WIDTH = 4000;
    public static final int LEVEL_HEIGHT = 1000;
    public static final int GROUND_LEVEL = 710;
    
//    // Character dimensions
//    public static final int CHARACTER_WIDTH = 140;
//    public static final int CHARACTER_HEIGHT = 140;
//    
//    // Physics
//    public static final float GRAVITY = 0.8f;
//    public static final float MAX_FALL_SPEED = 15.0f;
    
    // Animation actions (shared between player and enemies)
    public static class Actions {
        public static final int IDLE = 0;
//        public static final int RUN = 1;
//        public static final int JUMP = 2;
        public static final int HIT = 3;
        public static final int DEATH = 4;
//        public static final int AIR_ATTACK = 5;
//        public static final int COMBO = 6;
//        public static final int DASH = 7;
//        public static final int SLIDE = 8;
        public static final int WALK = 9; // تعديل القيمة لتكون فريدة
        public static final int FIGHT = 10; // تعديل القيمة لتكون فريدة
    }
    
//    // Player specific actions
//    public static class PlayerActions extends Actions {
//    }

    // Camera
    public static final int CAMERA_OFFSET_X = 400;
    public static final int CAMERA_OFFSET_Y = 300;
    
    // Portal
    public static final int PORTAL_X = 930;
    public static final int PORTAL_Y = 450;
//    public static final int PORTAL_WIDTH = 100;
//    public static final int PORTAL_HEIGHT = 150;
    
    // FPS
    public static final int TARGET_FPS = 120;
//    public static final double FRAME_TIME = 1.0 / TARGET_FPS;

    // Attack constants
    public static final int HIT_COOLDOWN = 30;
    public static final int ATTACK_COOLDOWN = 60;
    public static final int ATTACK_RANGE = 150;
}
