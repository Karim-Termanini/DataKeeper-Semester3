# DATA KEEPER

Dieses README spiegelt den aktuellen Stand des Projekts wider (vereinfachter Gegnertyp, Boss-Level, deutsche UI-Texte). Falls du Abweichungen siehst, sag Bescheid – ich passe es direkt an.

## Überblick
Ein 2D-Überlebensspiel mit drei Standard-Levels und einem Boss-Level. Du überlebst bis der Timer abläuft; dann erscheint ein Portal zum Fortschritt. Gegner sind Nahkampf-„Glitches“ (ein einheitlicher Gegnertyp), im Boss-Level kämpfst du gegen einen Endgegner mit mehreren Angriffsmustern.

## Funktionen

### Kernspielprinzip
- Überlebe die vorgegebene Zeit, während fortlaufend Gegner spawnen
- Progression: Level 1–3 mit zunehmender Dichte, Level 4 als Bosskampf
- Portalsystem: Nach Timerende erscheint ein Portal zum nächsten Level

### Spielerfähigkeiten
- Bewegung: A/D, Sprinten mit SHIFT
- Springen: SPACE
- Rutschen: S (mit Schaden bei Kollision)
- Angriff: E (Boden-Kombo), Spezialangriff in der Luft: W
- Kampfsystem: Kombos, kurze Unverwundbarkeitsfenster, Positionsspiel

### Level-Fortschritt (aktuell im Code)
- Level 1: 60 s, 12 Nahkampf-Gegner, Spawn-Intervall 3.0 s
- Level 2: 75 s, 20 Nahkampf-Gegner, Spawn-Intervall 2.2 s
- Level 3: 90 s, 26 Nahkampf-Gegner, Spawn-Intervall 1.8 s
- Level 4: Boss-Level (telegraphierte Angriffe; Sieg blendet in Victory/Epilog über)

### UI
- HUD mit HP, Timer, Level, Gegnerzahl und Sitzungs-„Besiegt“-Zähler
- Hauptmenü mit Start, Story-Overlay, Levelauswahl (1–4), Audio (SFX/Musik) und Beenden
- Levelabschluss-, Spielende-, Sieg- und Epilog-Bildschirme

## Steuerung (ingame angezeigt)
- A/D: Bewegen
- SPACE: Springen
- E: Angriff / Portal betreten
- W: Luftangriff
- S: Rutschen
- SHIFT: Sprinten/Dash
- ESC: Zurück zum Menü

## Build, Run, Package

Für Entwicklung (lokal kompilieren und starten):
- Linux/macOS: ./build_and_run.sh oder ./compile.sh && ./run.sh
- Windows (CMD): build_and_run.bat oder compile.bat dann run.bat

Für Release (Endnutzer, kein javac nötig):
1) Erstelle die JAR: ./package.sh
2) Starte: java -jar dist/DATA_KEEPER.jar

Wichtig: Endnutzer brauchen nur eine Java-Laufzeit (JRE). „javac“ ist NICHT nötig, solange die JAR verwendet wird. Schicke daher immer die Datei dist/DATA_KEEPER.jar (und ggf. README_RELEASE.md) – nicht die Sourcen.

## JavaDoc
- Generieren: ./javadoc.sh (UTF‑8, de)
- Öffnen: docs/javadoc/index.html
Hinweis: Bitte den gesamten Ordner docs/javadoc kopieren/öffnen, damit CSS/JS (Unterordner resource-files, script-files) gefunden werden. Einzelne HTML-Dateien direkt zu öffnen führt zu fehlendem Styling.

## Projektstruktur (vereinfacht)
```
DataKeeper/
├── src/
│   ├── entities/
│   │   ├── Enemy.java          # Einheitlicher Gegner (Nahkampf)
│   │   ├── Boss.java           # Endgegner mit mehreren Angriffen
│   │   ├── Player.java         # Spieler
│   │   ├── GameCharacter.java  # Abstrakte Basis
│   │   └── Character.java      # Interface
│   ├── levels/
│   │   ├── LevelConfig.java    # Leveldaten (siehe Werte oben)
│   │   ├── LevelManager.java   # Fortschritt & Timer/Portal
│   │   └── SpawnManager.java   # Gegner-Spawning
│   ├── gameplay/ (GameState, SurvivalTimer)
│   ├── ui/       (HUD, Menüs, Overlays)
│   ├── audio/    (SoundManager)
│   ├── inputs/   (KeyboardInputs)
│   └── main/     (Main, Game, GamePanel, GameWindow)
└── res/
    ├── Enemy/glitsoul/...      # Gegner-Frames
    ├── Fighter sprites/...     # Spieler-Frames
    ├── backgrounds/...         # Level-Hintergründe
    └── sounds/...              # Audio
```

## Mechaniken (aktuell)
- Überlebens-Timer pro Level; bei 0 s spawnt ein Portal (Mitte)
- Spawning in Intervallen; Gegnerzahl steigt mit Level
- Kombo/Schaden: echte Treffer erhöhen den Zähler; Musik wird bei Kampfhöhepunkt geduckt
- Boss: Phasenwechsel bei 50% HP; Dash/Welle/Sprung; Telegraphie + Warnsound

## Anpassung
- Levelwerte ändern: `levels/LevelConfig.java` (getLevel1/2/3 oder switch in getLevel)
- Ressourcen austauschen: Bilder/Sounds unter `res/` ersetzen (Dateinamen beibehalten)

## Systemanforderungen
- Java 8+ Laufzeit (JRE) zum Starten der JAR
- Für Entwicklung/Build: JDK 8+ (javac), nur auf Entwicklerrechner nötig

## Lizenz/Credits
- Sprites/Assets gemäß enthaltenen Readmes unter `res/`
- Code & Spielmechanik: Projekt „DATA KEEPER“ (Uni-Abgabe)
