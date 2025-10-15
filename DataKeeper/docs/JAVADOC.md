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
