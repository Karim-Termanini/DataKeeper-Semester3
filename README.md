# DATA KEEPER
<img width="1792" height="991" alt="image" src="https://github.com/user-attachments/assets/b023a144-02fc-4ed8-b1d4-156f4dc2a538" />

## Überblick
Ein 2D-Überlebens-Data Keeper-Kampfspiel mit progressivem Levelsystem, mehreren Gegnertypen und dynamischen Kampfmechaniken.

## Funktionen

### Kernspielprinzip
- **Überlebensmodus**: Überlebe eine festgelegte Zeit während Wellen von Gegnern
- **Progressives Levelsystem**: 3+ Level mit steigendem Schwierigkeitsgrad
- **Mehrere Gegnertypen**:
  - **Verfolger**: Schnelle Nahkampfangreifer, die den Spieler jagen
  - **Schütze**: Fernkampfgegner, die Abstand halten und Projektilfeuer abgeben
  - **Panzer**: Langsame Gegner mit hohen HP und schwerem Schaden
- **Portalsystem**: Schließe den Timer ab, um ein Ausstiegsportal zu spawnen und zum nächsten Level zu gelangen

### Spielerfähigkeiten
- **Bewegung**: WASD-Steuerung mit flüssigen Animationen
- **Springen**: Leertaste (Doppelsprung verfügbar)
- **Ausweichen**: Umschalttaste für schnelles Ausweichen
- **Rutschen**: S-Taste für Bewegung in niedriger Haltung mit Schaden
- **Kombo-Angriffe**: E-Taste für Bodenkombos mit steigendem Schaden
- **Luftangriff**: W-Taste für Luftkampf
- **Kampfsystem**: Kombo-Multiplikatoren, Unverwundbarkeitsframes und strategische Positionierung

### Level-Fortschritt
- **Level 1**: 30 Sekunden, 5 Verfolger-Gegner
- **Level 2**: 45 Sekunden, 8 Gegner (5 Verfolger + 3 Schützen)
- **Level 3**: 60 Sekunden, 12 Gegner (5 Verfolger + 4 Schützen + 3 Panzer)
- **Level 4+**: Dynamisch skalierter Schwierigkeitsgrad

### UI-Funktionen
- Echtzeit-Gesundheitsleiste mit Farbindikatoren
- Countdown-Timer mit visuellen Warnungen
- Levelnummer und Gegnerzähler
- Komboanzeige
- Levelabschlussbildschirm mit Statistiken
- Spielende-Bildschirm mit Neustart-Option

## Steuerung

### Bewegung
- **A**: Nach links bewegen
- **D**: Nach rechts bewegen
- **Leertaste**: Springen (zweimal drücken für Doppelsprung)
- **Umschalt**: Ausweichen
- **S**: Rutschen

### Kampf
- **E**: Angreifen / Portal betreten
- **W**: Luftangriff (während des Sprungs)

### Spielablauf
- **Eingabe**: Zum nächsten Level fortfahren (auf Levelabschlussbildschirm)
- **R**: Spiel neustarten (auf Spielende-Bildschirm)

## Kompilierung und Ausführung

### Voraussetzungen
- Java Development Kit (JDK) 8 oder höher
- Terminal/Command Prompt Zugriff

### Kompilierung
```bash
cd DataKeeper
javac -d out src/**/*.java
```

### Ausführung
```bash
cd DataKeeper
java -cp out:res main.Main
```

### Alternative: Verwendung einer IDE
1. Öffnen Sie den `DataKeeper`-Ordner in Ihrer IDE (IntelliJ IDEA, Eclipse, etc.)
2. Markieren Sie `src` als Quellverzeichnis
3. Markieren Sie `res` als Ressourcenverzeichnis
4. Führen Sie `main.Main` aus

## JavaDoc generieren
Sie können API-Dokumentation (JavaDoc) lokal generieren:

Option A - Skript
```bash
./javadoc.sh
```

Option B - Direkter Befehl
```bash
javac -d docs/javadoc -sourcepath src \
-subpackages main:entities:levels:gameplay:ui:audio:utils:inputs \
-author -version
```

Die Ausgabe befindet sich in `docs/javadoc/index.html`.

## Projektstruktur
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

## Spielmechaniken

### Überlebens-Timer
- Jedes Level hat eine bestimmte Dauer
- Timer zählt in Echtzeit herunter
- Wenn der Timer 0 erreicht:
  - Gegner-Spawning stoppt
  - Alle verbleibenden Gegner verschwinden
  - Ausstiegsportal erscheint in der Arenamitte

### Gegner-Spawning
- Gegner spawnen in Intervallen basierend auf Level-Konfiguration
- Spawn-Positionen sind an Arena-Rändern zufällig
- Spawning setzt sich fort bis Level-Gegnerlimit erreicht ist
- Spawning stoppt wenn Timer abgeschlossen ist

### Kampfsystem
- **Kombo-System**: Kettenangriffe für erhöhten Schaden
- **Unverwundbarkeitsframes**: Kurzer Schutz während bestimmter Aktionen
- **Überspringen**: Über Gegner springen um Schaden zu vermeiden
- **Rutschangriff**: Bewegung in niedriger Haltung mit Schaden
- **Projektil-Ausweichen**: Schützengegner-Projektilen ausweichen

### Gegner-KI
- **Verfolger**: Direkte Verfolgung, Nahkampfangriffe in Reichweite
- **Schützen**: Abstand halten, feuern Projektile auf Spieler
- **Panzer**: Langsamer Vormarsch, schwerer Schaden, Schadensreduktion

## Anpassung

### Neue Level hinzufügen
Bearbeiten Sie `LevelConfig.java` um neue Level-Konfigurationen hinzuzufügen:
```java
public static LevelConfig getLevel4() {
    return new LevelConfig(4, 75, 8, 6, 4, 3.5f);
}
```

### Schwierigkeitsgrad anpassen
Modifizieren Sie Werte in `LevelConfig.java`:
- `duration`: Überlebenszeit in Sekunden
- `chaserCount`, `shooterCount`, `tankCount`: Gegneranzahlen
- `spawnInterval`: Zeit zwischen Gegner-Spawns

### Gegner-Statistiken
Anpassen in jeweiligen Gegnerklassen:
- `health`: Gegner-Gesundheitspunkte
- `speed`: Bewegungsgeschwindigkeit
- `ATTACK_DAMAGE`: Dem Spieler zugefügter Schaden
- `ATTACK_COOLDOWN_MAX`: Zeit zwischen Angriffen

## Technische Details

### Leistung
- Ziel-FPS: 120
- Auflösung: 1800x1000
- Arena-Größe: 2000x1000

### Kamerasystem
- Sanfte Kamera folgt dem Spieler
- An Arena-Grenzen gebunden
- UI wird ohne Kameraversatz gerendert

### Animationssystem
- Frame-basierte Sprite-Animation
- Variable Animationsgeschwindigkeiten pro Aktion
- Automatisches Frame-Cycling und Abschlussbehandlung

## Bekannte Funktionen
- Kombosystem mit Schadensmultiplikatoren
- Doppelsprung-Mechaniken
- Visuelle Unterscheidung der Gegnertypen (Farbüberlagerungen)
- Pulsierende Portal-Animation
- Gesundheitsleiste mit Farbindikatoren
- Timer-Farbwarnungen

## Credits
- Ursprüngliches Spielkonzept und Basis-Mechaniken
- Erweitert mit Levelsystem, mehreren Gegnertypen und Überlebensmodus
- Sprite-Assets vom Originalprojekt

## Version
**Erweiterte Edition v1.0**
- Vollständiges Level-Fortschrittssystem
- 3 Gegnertypen
- Überlebens-Timer-Mechaniken
- Portalsystem
- Komplette UI-Überarbeitung




# Algorithmen – Kurzbeschreibung

Diese Seite fasst die wichtigsten Abläufe im Spiel kurz und einfach zusammen. Kein Deep-Dive – nur das Nötigste zum Verstehen und Erklären.

## Spielersteuerung (Player)
- Eingaben: A/D bewegen, SPACE springen, E angreifen/Portal, S rutschen, SHIFT dashen.
- Bewegung: horizontale Geschwindigkeit konstant; bei JUMP vertikale Geschwindigkeit (airSpeed) mit Gravitation.
- Sprung: reduzierte Sprünge (jumpsLeft), Boden-Erkennung mit Hysterese (offGroundFrames) vermeidet Flackern.
- Dash/Slide: temporärer Zustand mit eigener Dauer/Speed; blockiert andere Aktionen kurz.
- Angriff: Nahkampf-Hitbox vor dem Spieler, Schaden nach Aktionsart (Combo/Air/Slide) + Level-Bonus.
- Trefferfenster: pro Gegner kurzer Cooldown damit ein Schlag nicht mehrfach pro Frame zählt.
- Schaden/Unverwundbarkeit: kurze iFrames nach Schaden; Zustandswechsel zu HIT/DEATH.
- Combo-Zähler: zählt erfolgreiche Treffer in Zeitfenster; spielt passende SFX.

## Kamera
- Zielposition = Spielerposition – Offset.
- Optionales sanftes Nachziehen (Lerp) mit Clamping an Levelgrenzen.
- Optionaler Screen‑Shake bei Effekten.

## Kollision & Schaden
- Körper-Kollision: wenn Hitboxen von Player/Enemy schneiden.
- Angriffs-Kollision: vergleicht Angriffs-Hitbox (Player/Enemy/Boss) mit Ziel-Hitbox.
- Rückstoß/Positionskorrektur: kleine Verschiebung beim Stoß; Sonderfall: Über‑den‑Gegner‑springen.
- Boss: eingehender Schaden reduziert (Balancing) und eigener AOE/Bewegungs-Hitboxen je Attacke.

## Gegner‑KI (Enemy)
- Zielverfolgung: bestimmt Richtung zum Spieler und läuft innerhalb Grenzen.
- Angriffsentscheidung: in Reichweite und Cooldown=0 → Angriff; sonst IDLE/WALK.
- Zustandshandling: HIT/DEATH blockieren andere Aktionen; Animationstakte je Zustand.

## Boss‑Logik
- Phasen: Phase 2 unter 50% HP, schnellere Telegraphen/Angriffe.
- Angriffszyklus: Timer plant Telegraph → kurzer Warn‑Sound/Glow → Attacke (DASH/WAVE/JUMP) → Abklingzeit.


## Spawning & Level
- LevelManager (Singleton): hält LevelNummer, Timer, Config, Portal; steuert GameState.
- SpawnManager: spawnt Gegner nach Intervall bis Cap aktiv; Bosslevel spawnt einen Boss.
- Gegner‑Pool: EnemyPool liefert/recicelt Instanzen um Garbage zu reduzieren.
- Portal: wird nach Timerende aktiviert (für Levelabschluss).

## Audio
- SoundManager (Singleton): lädt/cached Clips, spielt Musik/SFX.
- Fallback‑SFX/Musik: generiert Töne bei fehlenden Dateien.
- Ducking: senkt Musiklautstärke kurz nach lauten SFX; Combat‑Layer bei hoher Intensität.

## HUD & Timer
- HUD zeigt HP, Timer, Level, Gegnerzahlen, Combo.
- SurvivalTimer zählt herunter; Farben ändern sich bei niedriger Restzeit.

## Performance/Robustheit
- Objekt‑Pool für Gegner, Synchrone Iteration für Effekte (Sparks) um ConcurrentModification zu vermeiden.
- Debug‑Logs optional über Flag; Asset‑Fallbacks verhindern Abstürze.






# DATA KEEPER — Release Build

## Ausführung (JAR)
1. Erstellen Sie die Release-JAR

```sh
./package.sh
```

2. Führen Sie sie aus

```sh
java -jar dist/DATA_KEEPER.jar
```

## Hinweise
- Ressourcen sind in der JAR eingebettet; das Spiel sucht auch nach Dateien unter `res/` im Dateisystem, falls vorhanden.
- Wenn Sie ein benutzerdefiniertes Boss-Hintergrundbild verwenden möchten, legen Sie es vor dem Packagieren unter `res/level4-Backgrounds/boss.png` ab.
- Audio-Lautstärken können im Hauptmenü angepasst werden. Warnpiepser für Boss-Telegraphs sind absichtlich leiser.

## Fehlerbehebung
- Falls Audio auf einigen Linux-Systemen fehlschlägt (headless Audio-Treiber), wird das Spiel wo möglich auf synthetisierte SFX/Musik zurückgreifen.
- Für bessere Performance schließen Sie andere rechenintensive Anwendungen während des Spielens.



# JavaDoc & Code-Konventionen

Diese Seite erklärt kurz, wie die JavaDoc erzeugt wird und welche Konventionen im Projekt angewendet werden.

## JavaDoc erzeugen
Voraussetzung: JDK ist installiert (javac, javadoc).

Option A – Skript (Linux/macOS):

```bash
./compile.sh && javadoc \
  -d docs/javadoc \
  -sourcepath src \
  -subpackages main:entities:levels:gameplay:ui:audio:utils:inputs \
  -author -version
```

Option B – Direktbefehl:

```bash
javadoc -d docs/javadoc -sourcepath src \
  -subpackages main:entities:levels:gameplay:ui:audio:utils:inputs \
  -author -version
```

Die generierte Dokumentation liegt danach unter `docs/javadoc`.

## Java-Code-Konventionen
- Benennung: Klassen `CamelCase` (Substantive), Methoden/Variablen `camelCase`.
- Sichtbarkeit: Felder möglichst `private`, Zugriff über Getter/Methoden wo sinnvoll.
- Final, Konstante: `static final` für Konstanten (siehe `utils.Constants`).
- Pakete: kleinbuchstabig, thematisch gruppiert (z. B. `entities`, `levels`).
- Kommentare: JavaDoc für public Klassen/Methoden; kurze Inline-Kommentare bei Logikstellen.
- Imports: spezifische Imports, keine Wildcards wenn möglich (Ausnahmen bei Swing/AWT ggf. toleriert).
- Formatierung: Einrückung 4 Leerzeichen, Zeilenlänge ~120 Zeichen.
- Fehlerbehandlung: Loggen (nur bei DEBUG) und Fallbacks statt Abbruch (siehe Audio/Assets).

## Hinweise
- `package-info.java` ist pro Paket vorhanden und beschreibt den Zweck kurz.
- Ressourcen (Bilder/Sounds) werden aus `res/` geladen; das beeinflusst die JavaDoc nicht.
- Für PlantUML‑Diagramme siehe `docs/uml`.



