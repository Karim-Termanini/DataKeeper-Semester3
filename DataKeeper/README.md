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
.
├── bin
│   ├── audio
│   │   └── SoundManager.class
│   ├── entities
│   │   ├── Boss$AttackType.class
│   │   ├── Boss.class
│   │   ├── Character.class
│   │   ├── Enemy.class
│   │   ├── GameCharacter.class
│   │   ├── Player.class
│   │   └── Projectile.class
│   ├── gameplay
│   │   ├── GameState.class
│   │   └── SurvivalTimer.class
│   ├── inputs
│   │   └── KeyboardInputs.class
│   ├── levels
│   │   ├── LevelConfig.class
│   │   ├── LevelManager.class
│   │   ├── Portal.class
│   │   └── SpawnManager.class
│   ├── main
│   │   ├── Game.class
│   │   ├── GamePanel$1.class
│   │   ├── GamePanel$Spark.class
│   │   ├── GamePanel.class
│   │   ├── GameWindow.class
│   │   └── Main.class
│   ├── ui
│   │   ├── EpilogueScreen.class
│   │   ├── GameOverScreen.class
│   │   ├── HUD.class
│   │   ├── LevelBackgroundRenderer.class
│   │   ├── LevelCompleteScreen.class
│   │   ├── MainMenu.class
│   │   └── VictoryScreen.class
│   └── utils
│       ├── AnimationManager.class
│       ├── Constants$EnemyActions.class
│       ├── Constants$Enemy.class
│       ├── Constants$PlayerActions.class
│       ├── Constants$Player.class
│       ├── Constants.class
│       ├── EnemyPool.class
│       ├── ImageUtils.class
│       ├── SaveManager$Settings.class
│       └── SaveManager.class
├── dist
│   ├── DataKeeper.command
│   ├── DataKeeper.desktop
│   ├── DATA_KEEPER.jar
│   ├── DATA_KEEPER_linux.tar.gz
│   ├── DATA_KEEPER_release.zip
│   ├── manifest.mf
│   ├── README_RELEASE.md
│   ├── Release_ALL.zip
│   ├── Run-DataKeeper.bat
│   ├── Run-DataKeeper.sh
│   ├── Run-DataKeeper.vbs
│   └── TEST.jar
├── docs
│   ├── ALGORITHMS.md
│   ├── javadoc
│   ├── JAVADOC.md
├── out
├── res
│   ├── Enemy
│   │   └── glitsoul
│   │       ├── death
│   │       ├── fight
│   │       ├── hit
│   │       ├── idle
│   │       └── run
│   ├── Fighter sprites
│   │   ├── air_attack
│   │   ├── combo
│   │   ├── dash
│   │   ├── death
│   │   ├── hit
│   │   ├── idle
│   │   ├── jump
│   │   ├── run
│   │   └── slide
│   ├── level1-Backgrounds
│   ├── level2-Backgrounds
│   ├── level3-Backgrounds
│   └── sounds
├── src
│   ├── audio
│   │   ├── package-info.java
│   │   └── SoundManager.java
│   ├── entities
│   │   ├── Boss.java
│   │   ├── Character.java
│   │   ├── Enemy.java
│   │   ├── GameCharacter.java
│   │   ├── package-info.java
│   │   └── Player.java
│   ├── gameplay
│   │   ├── GameState.java
│   │   ├── package-info.java
│   │   └── SurvivalTimer.java
│   ├── inputs
│   │   ├── KeyboardInputs.java
│   │   ├── MouseInputs.java
│   │   └── package-info.java
│   ├── levels
│   │   ├── LevelConfig.java
│   │   ├── LevelManager.java
│   │   ├── package-info.java
│   │   ├── Portal.java
│   │   └── SpawnManager.java
│   ├── main
│   │   ├── Game.java
│   │   ├── GamePanel.java
│   │   ├── GameWindow.java
│   │   ├── Main.java
│   │   └── package-info.java
│   ├── ui
│   │   ├── EpilogueScreen.java
│   │   ├── GameOverScreen.java
│   │   ├── HUD.java
│   │   ├── LevelBackgroundRenderer.java
│   │   ├── LevelCompleteScreen.java
│   │   ├── MainMenu.java
│   │   ├── package-info.java
│   │   └── VictoryScreen.java
│   └── utils
│       ├── AnimationManager.java
│       ├── Constants.java
│       ├── EnemyPool.java
│       ├── ImageUtils.java
│       ├── package-info.java
│       └── SaveManager.java
└── stats.dat
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
