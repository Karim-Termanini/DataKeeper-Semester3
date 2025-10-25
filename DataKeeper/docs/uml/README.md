# UML für DATA KEEPER

Diese Mappe enthält die verpflichtenden Diagramme gemäß Aufgabenstellung:

- `class-diagram.puml`: Klassendiagramm mit GUI- und Application-Schicht (Entities + Control) sowie zentralen Beziehungen.
- `sequence-start-game.puml`: Sequenzdiagramm für den Use Case „Spiel starten“ (vom Hauptmenü bis Levelstart).

## Rendern (z. B. in VS Code)

1) Installiere ein PlantUML-Plugin oder verwende die CLI.
2) Öffne die `.puml`-Datei und nutze die Vorschau bzw. exportiere als PNG/SVG.

Optional über CLI (falls PlantUML und Java vorhanden):

```bash
# erzeugt PNG neben der Datei
plantuml docs/uml/class-diagram.puml
plantuml docs/uml/sequence-start-game.puml
```

## Troubleshooting

Wenn die Vorschau beim Klassendiagramm fehlschlägt mit einer Meldung wie
`Cannot run program "/opt/local/bin/dot"`:

- Ursache: Klassendiagramme nutzen Graphviz `dot`. Sequenzdiagramme brauchen es nicht – deshalb funktionieren sie auch ohne.
- Fix A (empfohlen, ohne Installation): Stelle die Erweiterung auf Server-Rendering um.
  - VS Code: Settings → PlantUML → Render = `PlantUMLServer`
  - PlantUML: Server = `https://www.plantuml.com/plantuml`
- Fix B (lokal rendern): Graphviz installieren und den Pfad setzen.
  - Ubuntu/Debian: `sudo apt-get install -y graphviz`
  - Fedora: `sudo dnf install graphviz`
  - Arch: `sudo pacman -S graphviz`
  - Danach in den Einstellungen: PlantUML: Graphviz Dot = `/usr/bin/dot`

## Hinweise

- Diagramme spiegeln die aktuelle Codebasis in `src/` wider (Stand: YYYY-MM-DD) und trennen zwischen:
  - GUI (ui/main/inputs): Anzeige, Eingaben, kein Spielablauf.
  - Application – Control (levels/gameplay): Steuert Ablauf (LevelManager, SpawnManager, Timer, Portal, GameState).
  - Application – Entities (entities): Daten + Verhalten der Spielobjekte (Player, Enemy, Boss, GameCharacter/Character).
  - Utilities/Audio: SoundManager, Constants, SaveManager, EnemyPool.
- `LevelManager` fungiert als zentrale Fassade/Singleton zwischen GUI und Logik. GUI ruft nur seine Methoden für Levelstart/-wechsel usw. auf.
- Das Sequenzdiagramm zeigt die reale Startkette aus `GamePanel.startGameFromMenu()`.

Wenn du Anpassungen (z. B. zusätzliche Klassen/Methoden) vornimmst, sag mir Bescheid – ich aktualisiere die UML direkt.
