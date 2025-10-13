package audio;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private final Map<String, Clip> soundClips;
    private Clip backgroundMusic;

    private SoundManager() {
        soundClips = new HashMap<>();
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSounds() {
        loadSound("attack", "/sounds/player/attack.wav");
        loadSound("hurt", "/sounds/player/hurt.wav");
        loadSound("jump", "/sounds/player/jump.wav");
        loadSound("kill", "/sounds/player/kill.wav");
        loadSound("player_death", "/sounds/player/player-death.wav");
        loadSound("enemy_death", "/sounds/enemy/enemy-death.wav");
        loadSound("hit", "/sounds/enemy/hit.wav");
        loadSound("portal", "/sounds/portal.wav");
        loadBackgroundMusic();
    }

    private void loadSound(String name, String path) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(path);
            if (audioSrc == null) {
                System.err.println("❌ Sound not found: " + path);
                return;
            }
            
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundClips.put(name, clip);
            System.out.println("✅ Loaded sound: " + name);
        } catch (Exception e) {
            System.err.println("❌ Error loading sound " + name + ": " + e.getMessage());
        }
    }

    private void loadBackgroundMusic() {
        String path = "/sounds/background/backgroundMusic.wav";
        try {
            InputStream audioSrc = getClass().getResourceAsStream(path);
            if (audioSrc == null) {
                System.err.println("❌ Music not found: " + path);
                return;
            }
            
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            float musicVolume = 0.5f;
            setVolume(backgroundMusic, musicVolume);
            System.out.println("✅ Loaded background music");
        } catch (Exception e) {
            System.err.println("❌ Error loading music: " + e.getMessage());
        }
    }

    public void playSound(String name) {
        boolean soundEnabled = true;
        if (!soundEnabled) return;
        
        Clip clip = soundClips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            float soundVolume = 0.7f;
            setVolume(clip, soundVolume);
            clip.start();
        }
    }

    public void playBackgroundMusic() {
        boolean musicEnabled = true;
        if (!musicEnabled || backgroundMusic == null) return;
        
        if (!backgroundMusic.isRunning()) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    private void setVolume(Clip clip, float volume) {
        if (clip == null) return;
        
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        } catch (Exception e) {
        }
    }

}
