package utils;

public class Constants {
    // Level dimensions
    public static final int LEVEL_WIDTH = 4000;
    public static final int LEVEL_HEIGHT = 1000;
    public static final int GROUND_LEVEL = 710;

    public static class Player {
        public static final int MAX_HEALTH = 200;
        public static final float SPEED = 7.0f;
        public static final float GRAVITY = 0.4f;
        public static final float JUMP_SPEED = -14.0f;
        public static final int INITIAL_JUMPS = 2;

        // Dimensions
        public static final int WIDTH = 140;
        public static final int HEIGHT = 140;

        // Hitbox Offsets
        public static final int HITBOX_X_OFFSET = 30;
        public static final int HITBOX_Y_OFFSET = 20;
        public static final int HITBOX_WIDTH_REDUCTION = 60;
        public static final int HITBOX_HEIGHT_REDUCTION = 40;

        // Attack & Damage
        public static final int ATTACK_RANGE = 60;
        public static final int ATTACK_HEIGHT = 100;
        public static final int COMBO_LVL1_DMG = 20;
        public static final int COMBO_LVL2_DMG = 25;
        public static final int COMBO_LVL3_DMG = 35;
        public static final int AIR_ATTACK_DMG = 30;
        public static final int SLIDE_DMG = 10;
        public static final int DEFAULT_DMG = 5;

        // Timers & Durations
        public static final int DASH_DURATION_FRAMES = 15;
        public static final float DASH_SPEED = 30.0f;
        public static final int JUMP_OVER_PROTECTION_FRAMES = 40;
        public static final int ATTACK_PROTECTION_FRAMES = 15;
        public static final int INVINCIBILITY_FRAMES = 60;
        public static final int COMBO_TIME_WINDOW_MS = 800;
    }

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

    public static class Enemy {
        public static final int HEALTH = 100;
        public static final float SPEED = 1.5f;
        public static final int ATTACK_DAMAGE = 10;
        public static final int INVINCIBILITY_DURATION_FRAMES = 30;
        public static final int WIDTH = 140;
        public static final int HEIGHT = 140;
        public static final int HITBOX_X_OFFSET = 30;
        public static final int HITBOX_Y_OFFSET = 20;
        public static final int HITBOX_WIDTH_REDUCTION = 60;
        public static final int HITBOX_HEIGHT_REDUCTION = 40;
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
    public static final int ATTACK_RANGE = 80; // Pixels
}
