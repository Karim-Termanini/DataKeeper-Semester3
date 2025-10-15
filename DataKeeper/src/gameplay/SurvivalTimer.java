package gameplay;

public class SurvivalTimer {
    private int totalFrames;
    private int currentFrame;
    private boolean isRunning;
    private boolean isComplete;

    public SurvivalTimer(int durationSeconds) {
        this.totalFrames = durationSeconds * 120; // 120 FPS
        this.currentFrame = 0;
        this.isRunning = false;
        this.isComplete = false;
    }
    public void start() {
        isRunning = true;
        currentFrame = 0;
        isComplete = false;
    }
    public void update() {
        if (isRunning && !isComplete) {
            currentFrame++;
            if (currentFrame >= totalFrames) {
                isComplete = true;
                isRunning = false;
            }
        }
    }
    public int getRemainingSeconds() {
        int remainingFrames = totalFrames - currentFrame;
        return Math.max(0, remainingFrames / 120);
    }
    public boolean isComplete() {
        return isComplete;
    }
}
