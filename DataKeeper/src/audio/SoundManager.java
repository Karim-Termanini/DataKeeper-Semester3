package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private final Map<String, Clip> soundClips;
    private Clip backgroundMusic;
    private Clip combatLayer; // secondary intensity layer for gameplay
    private Clip menuMusic;
    private Clip bossMusic;
    private boolean soundEnabled;
    private boolean musicEnabled;
    private float sfxVolume;
    private float musicVolume;
    private long duckUntilMs;

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
        loadSound("player_death", "/sounds/player/player-death.wav");
        loadSound("enemy_death", "/sounds/enemy/enemy-death.wav");
        loadSound("hit", "/sounds/enemy/hit.wav");
        loadSound("portal", "/sounds/portal.wav");
        soundEnabled = utils.SaveManager.loadSoundEnabled();
        musicEnabled = soundEnabled; // tie both by default
        sfxVolume = utils.SaveManager.loadSfxVolume();
        musicVolume = utils.SaveManager.loadMusicVolume();
        loadBackgroundMusic();
        loadMenuMusic();
        loadBossMusic();
        ensureMenuSfx();
        ensureGameplayFallbacks();
    }

    private void loadSound(String name, String path) {
        try {
            // Try filesystem under ./res first
            java.io.File file = new java.io.File("res" + path);
            AudioInputStream audioStream;
            if (file.exists()) {
                audioStream = AudioSystem.getAudioInputStream(file);
            } else {
                InputStream audioSrc = getClass().getResourceAsStream(path);
                if (audioSrc == null) {
                    System.err.println("❌ Sound not found: " + path);
                    return;
                }
                InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
                audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            }
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundClips.put(name, clip);
            if (utils.Constants.DEBUG_LOGS) {
                System.out.println("✅ Loaded sound: " + name);
            }
        } catch (Exception e) {
            if (utils.Constants.DEBUG_LOGS) {
                System.err.println("❌ Error loading sound " + name + ": " + e.getMessage());
            }
        }
    }

    private void loadBackgroundMusic() {
        String path = "/sounds/background/backgroundMusic.wav";
        try {
            java.io.File file = new java.io.File("res" + path);
            AudioInputStream audioStream;
            if (file.exists()) {
                audioStream = AudioSystem.getAudioInputStream(file);
            } else {
                InputStream audioSrc = getClass().getResourceAsStream(path);
                if (audioSrc == null) {
                    System.err.println("❌ Music not found: " + path);
                    return;
                }
                InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
                audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            }
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            float musicVolume = 0.5f;
            setVolume(backgroundMusic, musicVolume);
            if (utils.Constants.DEBUG_LOGS) {
                System.out.println("✅ Loaded background music");
            }
        } catch (Exception e) {
            if (utils.Constants.DEBUG_LOGS) {
                System.err.println("❌ Error loading music: " + e.getMessage());
            }
        }
    }

    private void loadMenuMusic() {
        String path = "/sounds/background/menuMusic.wav";
        try {
            java.io.File file = new java.io.File("res" + path);
            AudioInputStream audioStream;
            if (file.exists()) {
                audioStream = AudioSystem.getAudioInputStream(file);
            } else {
                InputStream audioSrc = getClass().getResourceAsStream(path);
                if (audioSrc == null) {
                    // No file, synthesize a soft digital pad/arpeggio
                    menuMusic = createDigitalMenuClip();
                    return;
                }
                InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
                audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            }
            menuMusic = AudioSystem.getClip();
            menuMusic.open(audioStream);
            setVolume(menuMusic, musicVolume);
        } catch (Exception e) {
            // Fallback: synthesize if loading failed
            menuMusic = createDigitalMenuClip();
        }
    }

    private void loadBossMusic() {
        String path = "/sounds/background/bossMusic.wav";
        try {
            java.io.File file = new java.io.File("res" + path);
            AudioInputStream audioStream;
            if (file.exists()) {
                audioStream = AudioSystem.getAudioInputStream(file);
            } else {
                InputStream audioSrc = getClass().getResourceAsStream(path);
                if (audioSrc == null) {
                    // synthesize a tense boss loop if no file
                    bossMusic = createBossClip();
                    return;
                }
                InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
                audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            }
            bossMusic = AudioSystem.getClip();
            bossMusic.open(audioStream);
            setVolume(bossMusic, musicVolume);
        } catch (Exception e) {
            bossMusic = createBossClip();
        }
    }

    private void ensureMenuSfx() {
        // If custom files are not present, generate simple tones and cache them in-memory clips.
        if (!soundClips.containsKey("menu_move")) {
            soundClips.put("menu_move", createToneClip(660, 50));
        }
        if (!soundClips.containsKey("menu_select")) {
            soundClips.put("menu_select", createToneClip(880, 90));
        }
    }
    
    private void ensureGameplayFallbacks() {
        // Styles (chosen):
        // - attack: laser/pulse (square chirp)
        // - enemy_death: crunchy (bitcrushed/noise crunch)
        // - hit confirm: soft click
        // - slide: whoosh
        if (!soundClips.containsKey("attack")) {
            Clip atk = createSquareChirp(700, 1900, 95);
            if (atk == null) atk = createPercussiveClick(1500, 60);
            soundClips.put("attack", atk);
        }
        if (!soundClips.containsKey("hurt")) {
            soundClips.put("hurt", createNoiseBurst(120, 0.25f));
        }
        if (!soundClips.containsKey("jump")) {
            soundClips.put("jump", createSweep(400, 800, 120));
        }
        if (!soundClips.containsKey("hit")) {
            soundClips.put("hit", createPercussiveClick(900, 60));
        }
        if (!soundClips.containsKey("enemy_death")) {
            Clip crunchy = createCrunch(260);
            if (crunchy == null) crunchy = createSweep(700, 180, 260);
            soundClips.put("enemy_death", crunchy);
        }
        if (!soundClips.containsKey("player_death")) {
            soundClips.put("player_death", createSweep(300, 80, 400));
        }
        if (!soundClips.containsKey("portal")) {
            soundClips.put("portal", createSweep(300, 1200, 500));
        }
        if (!soundClips.containsKey("footstep_lo")) {
            soundClips.put("footstep_lo", createPercussiveClick(180, 40));
        }
        if (!soundClips.containsKey("footstep_hi")) {
            soundClips.put("footstep_hi", createPercussiveClick(300, 35));
        }
        if (!soundClips.containsKey("slide")) {
            // soft whoosh
            soundClips.put("slide", createSweep(1200, 600, 180));
        }
        if (!soundClips.containsKey("dash")) {
            // sharper whoosh for dash
            soundClips.put("dash", createSweep(1600, 900, 140));
        }

        // Warning beep for boss telegraphs
        if (!soundClips.containsKey("boss_warn")) {
            Clip warn = createToneClip(1200, 120);
            if (warn == null) warn = createPercussiveClick(1200, 120);
            soundClips.put("boss_warn", warn);
        }

        // Combo tiers (progressively brighter laser pulses)
        if (!soundClips.containsKey("combo1")) soundClips.put("combo1", createSquareChirp(600, 1200, 70));
        if (!soundClips.containsKey("combo2")) soundClips.put("combo2", createSquareChirp(900, 1700, 85));
        if (!soundClips.containsKey("combo3")) soundClips.put("combo3", createSquareChirp(1200, 2200, 95));
    }

    private Clip createToneClip(int frequencyHz, int durationMs) {
        try {
            float sampleRate = 44100f;
            int numSamples = (int) (durationMs / 1000f * sampleRate);
            byte[] data = new byte[numSamples];
            for (int i = 0; i < numSamples; i++) {
                double t = i / sampleRate;
                double v = Math.sin(2 * Math.PI * frequencyHz * t);
                // Simple envelope to avoid clicks
                double env = 1.0;
                if (i < 200) env = i / 200.0; // attack
                else if (i > numSamples - 400) env = Math.max(0, (numSamples - i) / 400.0); // release
                int val = (int) (v * env * 127);
                data[i] = (byte) val;
            }
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    private Clip createSweep(int startHz, int endHz, int durationMs) {
        try {
            float sampleRate = 44100f;
            int samples = (int) (durationMs / 1000f * sampleRate);
            byte[] data = new byte[samples];
            for (int i = 0; i < samples; i++) {
                double t = i / sampleRate;
                double f = startHz + (endHz - startHz) * (i / (double) samples);
                double v = Math.sin(2 * Math.PI * f * t);
                // Exponential fade out
                double env = Math.pow(0.9995, i);
                int val = (int) (v * env * 120);
                data[i] = (byte) val;
            }
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;
        } catch (Exception e) { return null; }
    }

    private Clip createNoiseBurst(int durationMs, float amp) {
        try {
            float sampleRate = 44100f;
            int samples = (int) (durationMs / 1000f * sampleRate);
            byte[] data = new byte[samples];
            java.util.Random rnd = new java.util.Random();
            for (int i = 0; i < samples; i++) {
                double env = Math.pow(0.998, i);
                int val = (int) ((rnd.nextFloat() * 2 - 1) * env * (amp * 127));
                data[i] = (byte) val;
            }
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;
        } catch (Exception e) { return null; }
    }

    private Clip createPercussiveClick(int freqHz, int durationMs) {
        try {
            float sampleRate = 44100f;
            int samples = (int) (durationMs / 1000f * sampleRate);
            byte[] data = new byte[samples];
            for (int i = 0; i < samples; i++) {
                double t = i / sampleRate;
                double v = Math.sin(2 * Math.PI * freqHz * t);
                double env = Math.pow(0.995, i);
                int val = (int) (v * env * 127);
                data[i] = (byte) val;
            }
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;
        } catch (Exception e) { return null; }
    }

    // Crunchy = sample-and-hold noise with bitcrush-style artifacts
    private Clip createCrunch(int durationMs) {
        try {
            float sampleRate = 44100f;
            int samples = (int) (durationMs / 1000f * sampleRate);
            byte[] data = new byte[samples];
            java.util.Random rnd = new java.util.Random();
            int hold = 0;
            int step = 8; // lower = more crunchy
            int current = 0;
            for (int i = 0; i < samples; i++) {
                if (hold == 0) {
                    // quantize to 4-bit range
                    int raw = (int)((rnd.nextFloat() * 2 - 1) * 127);
                    current = (raw >> 4) << 4; // 4-bit quantization
                    hold = step;
                } else {
                    hold--;
                }
                double env = Math.pow(0.9985, i);
                data[i] = (byte) (current * env);
            }
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;
        } catch (Exception e) { return null; }
    }


    private Clip createDigitalMenuClip() {
        try {
            float sampleRate = 44100f;
            int bpm = 96;
            int noteMs = 60000 / bpm; // quarter notes
            int bars = 4; int notesPerBar = 4;
            int totalNotes = bars * notesPerBar;
            int totalMs = totalNotes * noteMs;
            int samples = (int) (totalMs / 1000f * sampleRate);
            byte[] data = new byte[samples];
            // Simple slow arpeggio (Am - F - C - G) as soft sine blend
            int[][] chords = new int[][]{{57,60,64},{53,57,60},{48,52,55},{55,59,62}}; // A minor, F, C, G
            for (int n = 0; n < totalNotes; n++) {
                int bar = (n / notesPerBar) % chords.length;
                int[] chord = chords[bar];
                // Play the triad together but very softly
                int start = (int) (n * noteMs / 1000f * sampleRate);
                int len = (int) (noteMs / 1000f * sampleRate);
                for (int i = 0; i < len && (start + i) < samples; i++) {
                    double t = (start + i) / sampleRate;
                    double v = 0;
                    for (int m = 0; m < chord.length; m++) {
                        double freq = 440.0 * Math.pow(2, (chord[m] - 69) / 12.0);
                        v += 0.33 * Math.sin(2 * Math.PI * freq * t);
                    }
                    double trem = 0.97 + 0.03 * Math.sin(2 * Math.PI * 4 * t);
                    v *= trem;
                    double env = 1.0 - (i / (double) len) * 0.1;
                    int val = (int) Math.max(-100, Math.min(100, v * env * 50));
                    data[start + i] = (byte) val;
                }
            }
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            setVolume(clip, 0.2f);
            return clip;
        } catch (Exception e) { return null; }
    }

    private Clip createSquareChirp(int startHz, int endHz, int durationMs) {
        try {
            float sampleRate = 44100f;
            int samples = (int) (durationMs / 1000f * sampleRate);
            byte[] data = new byte[samples];
            for (int i = 0; i < samples; i++) {
                double t = i / sampleRate;
                double f = startHz + (endHz - startHz) * (i / (double) samples);
                double v = Math.signum(Math.sin(2 * Math.PI * f * t));
                double env = Math.pow(0.9992, i);
                int val = (int) (v * env * 120);
                data[i] = (byte) val;
            }
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;
        } catch (Exception e) { return null; }
    }

    public void playSound(String name) {
        if (!soundEnabled) return;
        
        Clip clip = soundClips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            if ("boss_warn".equals(name)) {
                setVolume(clip, Math.max(0f, Math.min(1f, sfxVolume * 0.4f)));
            } else {
                setVolume(clip, sfxVolume);
            }
            clip.start();
        }
    }

    public void playBackgroundMusic() {
        if (!musicEnabled || backgroundMusic == null) return;
        
        if (!backgroundMusic.isRunning()) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
        applyMusicVolume(backgroundMusic);
    }

    // Simple combat layer on top of background to add intensity when active
    public void setCombatLayerActive(boolean active) {
        if (!musicEnabled) return;
        ensureCombatLayer();
        if (combatLayer == null) return;
        if (active) {
            if (!combatLayer.isRunning()) {
                combatLayer.setFramePosition(0);
                combatLayer.loop(Clip.LOOP_CONTINUOUSLY);
            }
            // a bit quieter than main bg
            setVolume(combatLayer, Math.max(0f, Math.min(1f, musicVolume * 0.6f)));
        } else {
            if (combatLayer.isRunning()) combatLayer.stop();
        }
    }

    private void ensureCombatLayer() {
        if (combatLayer != null) return;
        // Try to load from res, else synthesize
        String path = "/sounds/background/combatLayer.wav";
        try {
            java.io.File file = new java.io.File("res" + path);
            AudioInputStream audioStream;
            if (file.exists()) {
                audioStream = AudioSystem.getAudioInputStream(file);
                combatLayer = AudioSystem.getClip();
                combatLayer.open(audioStream);
            } else {
                combatLayer = createDigitalCombatClip();
            }
        } catch (Exception e) {
            combatLayer = createDigitalCombatClip();
        }
    }

    private Clip createDigitalCombatClip() {
        try {
            float sampleRate = 44100f;
            int bpm = 120;
            int noteMs = 60000 / bpm; // quarter
            int bars = 4; int notesPerBar = 8; // busier texture
            int totalNotes = bars * notesPerBar;
            int totalMs = totalNotes * (noteMs / 2);
            int samples = (int) (totalMs / 1000f * sampleRate);
            byte[] data = new byte[samples];
            int[] pattern = new int[]{36, 36, 38, 36, 43, 41, 38, 36}; // simple bassline pattern (MIDI notes)
            for (int n = 0; n < totalNotes; n++) {
                int note = pattern[n % pattern.length];
                int start = (int) (n * (noteMs / 2) / 1000f * sampleRate);
                int len = (int) ((noteMs / 2) / 1000f * sampleRate);
                for (int i = 0; i < len && (start + i) < samples; i++) {
                    double t = (start + i) / sampleRate;
                    double freq = 440.0 * Math.pow(2, (note - 69) / 12.0);
                    double v = 0.6 * Math.sin(2 * Math.PI * freq * t) // bass
                             + 0.2 * Math.signum(Math.sin(2 * Math.PI * (freq*2) * t)); // harmonic grit
                    double env = Math.pow(0.9995, i);
                    int val = (int) Math.max(-110, Math.min(110, v * env * 80));
                    data[start + i] = (byte) val;
                }
            }
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;
        } catch (Exception e) { return null; }
    }

    private void applyMusicVolume(Clip clip) {
        if (clip == null) return;
        float target = musicVolume;
        if (System.currentTimeMillis() < duckUntilMs) {
            target *= 0.5f; // ducking -6dB approx
        }
        setVolume(clip, target);
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public void playMenuMusic() {
        if (!musicEnabled || menuMusic == null) return;
        if (!menuMusic.isRunning()) {
            menuMusic.setFramePosition(0);
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
        applyMusicVolume(menuMusic);
    }

    public void stopMenuMusic() {
        if (menuMusic != null && menuMusic.isRunning()) {
            menuMusic.stop();
        }
    }

    public void playBossMusic() {
        if (!musicEnabled || bossMusic == null) return;
        if (!bossMusic.isRunning()) {
            bossMusic.setFramePosition(0);
            bossMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
        applyMusicVolume(bossMusic);
    }

    public void stopBossMusic() {
        if (bossMusic != null && bossMusic.isRunning()) bossMusic.stop();
    }

    public void playMenuMoveSound() {
        playSound("menu_move");
    }

    public void playMenuSelectSound() {
        playSound("menu_select");
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        this.musicEnabled = enabled;
        utils.SaveManager.saveSoundEnabled(enabled);
        if (!enabled) {
            stopBackgroundMusic();
            stopMenuMusic();
        }
    }

    public float getSfxVolume() { return sfxVolume; }
    public float getMusicVolume() { return musicVolume; }
    public void setSfxVolume(float v) { sfxVolume = clamp01(v); utils.SaveManager.saveSfxVolume(sfxVolume); }
    public void setMusicVolume(float v) { musicVolume = clamp01(v); utils.SaveManager.saveMusicVolume(musicVolume); applyMusicVolume(backgroundMusic); applyMusicVolume(menuMusic); }
    private float clamp01(float v) { return Math.max(0f, Math.min(1f, v)); }

    // Duck music briefly after strong SFX (e.g., hits/deaths)
    public void duckMusic(int ms) {
        duckUntilMs = Math.max(duckUntilMs, System.currentTimeMillis() + ms);
        applyMusicVolume(backgroundMusic);
        applyMusicVolume(menuMusic);
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

    // Boss fallback music: darker pulse + low drone
    private Clip createBossClip() {
        try {
            float sampleRate = 44100f;
            int bpm = 100;
            int noteMs = 60000 / bpm;
            int totalMs = 16 * noteMs;
            int samples = (int) (totalMs / 1000f * sampleRate);
            byte[] data = new byte[samples];
            int[] seq = new int[]{40, 40, 35, 38, 40, 45, 43, 40};
            for (int n = 0; n < seq.length; n++) {
                int start = (int) (n * 2 * noteMs / 1000f * sampleRate);
                int len = (int) (2 * noteMs / 1000f * sampleRate);
                int midi = seq[n];
                for (int i = 0; i < len && (start + i) < samples; i++) {
                    double t = (start + i) / sampleRate;
                    double base = 440.0 * Math.pow(2, (midi - 69) / 12.0);
                    double v = 0.5 * Math.sin(2 * Math.PI * base * t)
                             + 0.2 * Math.signum(Math.sin(2 * Math.PI * (base*2) * t))
                             + 0.1 * Math.sin(2 * Math.PI * 55 * t); // low drone
                    double env = Math.pow(0.9994, i);
                    int val = (int) Math.max(-110, Math.min(110, v * env * 90));
                    data[start + i] = (byte) val;
                }
            }
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;
        } catch (Exception e) { return null; }
    }

    // Convenience API for combo-tier SFX
    public void playComboSound(int tier) {
        if (tier >= 3) playSound("combo3");
        else if (tier == 2) playSound("combo2");
        else playSound("combo1");
    }

}
