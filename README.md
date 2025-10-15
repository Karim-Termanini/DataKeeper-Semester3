# DATA KEEPER

## Überblick
Ein 2D-Überlebens-Arena-Kampfspiel mit progressivem Levelsystem, mehreren Gegnertypen und dynamischen Kampfmechaniken.

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
PlatformerExpanded/
├── src/
│   ├── entities/
│   │   ├── Character.java      # Interface für alle Charaktere
│   │   ├── GameCharacter.java  # Abstrakte Basisklasse
│   │   ├── Player.java         # Spielercharakter mit allen Fähigkeiten
│   │   ├── Enemy.java          # Ursprünglicher Gegner (Legacy)
│   │   ├── ChaserEnemy.java    # Schneller Nahkampfgegner
│   │   ├── ShooterEnemy.java   # Fernkampf-Projektilgegner
│   │   ├── TankEnemy.java      # Schwerer Panzergegner
│   │   └── Projectile.java     # Projektil für Schützengegner
│   ├── levels/
│   │   ├── LevelConfig.java    # Level-Konfigurationsdaten
│   │   ├── LevelManager.java   # Verwaltet Level-Fortschritt
│   │   ├── Portal.java         # Ausstiegsportal für Levelabschluss
│   │   └── SpawnManager.java   # Verwaltet Gegner-Spawning
│   ├── gameplay/
│   │   ├── GameState.java      # Spielzustands-Enumeration
│   │   └── SurvivalTimer.java  # Countdown-Timer-System
│   ├── ui/
│   │   ├── HUD.java            # Heads-up-Display
│   │   ├── LevelCompleteScreen.java # Siegesbildschirm
│   │   └── GameOverScreen.java # Niederlagenbildschirm
│   ├── inputs/
│   │   ├── KeyboardInputs.java # Tastatureingabe-Handler
│   │   └── MouseInputs.java    # Mauseingabe-Handler
│   ├── main/
│   │   ├── Main.java           # Einstiegspunkt
│   │   ├── Game.java           # Spielschleife
│   │   ├── GamePanel.java      # Hauptspielpanel mit Rendering
│   │   └── GameWindow.java     # Fenstereinrichtung
│   └── utils/
│       └── AnimationManager.java # Animations-Utilities
└── res/
    ├── Enemy/
    │   └── glitsoul/           # Gegner-Sprite-Sheets
    └── Fighter sprites/        # Spieler-Sprite-Sheets
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
