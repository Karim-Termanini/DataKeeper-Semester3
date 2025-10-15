package utils;

import java.io.*;

public class SaveManager {
    private static final String STATS_FILE = "stats.dat";
    private static final String SETTINGS_FILE = "settings.dat";

    public static int loadTotalEnemiesDefeated() {
        File f = new File(STATS_FILE);
        if (!f.exists()) return 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            if (line == null) return 0;
            return Integer.parseInt(line.trim());
        } catch (Exception e) {
            System.err.println("Error loading stats: " + e.getMessage());
            return 0;
        }
    }

    public static void saveTotalEnemiesDefeated(int total) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(STATS_FILE, false))) {
            pw.println(total);
        } catch (Exception e) {
            System.err.println("Error saving stats: " + e.getMessage());
        }
    }

    public static void addEnemiesDefeated(int delta) {
        if (delta <= 0) return;
        int current = loadTotalEnemiesDefeated();
        saveTotalEnemiesDefeated(current + delta);
    }

    public static boolean loadSoundEnabled() {
        File f = new File(SETTINGS_FILE);
        if (!f.exists()) return true;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.startsWith("sound=")) {
                    String v = line.substring("sound=".length()).trim();
                    return v.equals("1") || v.equals("true");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading settings: " + e.getMessage());
        }
        return true;
    }

    public static void saveSoundEnabled(boolean enabled) {
        // Preserve extensibility: write key=value lines
        Settings s = loadSettings();
        s.sound = enabled;
        saveSettings(s);
    }

    public static float loadSfxVolume() {
        return loadSettings().sfx;
    }

    public static float loadMusicVolume() {
        return loadSettings().music;
    }

    public static void saveSfxVolume(float v) {
        Settings s = loadSettings();
        s.sfx = clamp01(v);
        saveSettings(s);
    }

    public static void saveMusicVolume(float v) {
        Settings s = loadSettings();
        s.music = clamp01(v);
        saveSettings(s);
    }

    private static float clamp01(float v) { return Math.max(0f, Math.min(1f, v)); }

    private static class Settings {
        boolean sound = true;
        float sfx = 0.8f;
        float music = 0.6f;
    }

    private static Settings loadSettings() {
        Settings s = new Settings();
        File f = new File(SETTINGS_FILE);
        if (!f.exists()) return s;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.startsWith("sound=")) {
                    String v = line.substring(6).trim();
                    s.sound = v.equals("1") || v.equals("true");
                } else if (line.startsWith("sfx=")) {
                    s.sfx = clamp01(Float.parseFloat(line.substring(4).trim()));
                } else if (line.startsWith("music=")) {
                    s.music = clamp01(Float.parseFloat(line.substring(6).trim()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading settings: " + e.getMessage());
        }
        return s;
    }

    private static void saveSettings(Settings s) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SETTINGS_FILE, false))) {
            pw.println("sound=" + (s.sound ? "1" : "0"));
            pw.println("sfx=" + s.sfx);
            pw.println("music=" + s.music);
        } catch (Exception e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
}
