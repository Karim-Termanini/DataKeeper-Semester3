# DATA KEEPER — Release Build

## Ausführen (Endnutzer)
- Voraussetzung: Java 8+ Laufzeit (JRE). „javac“ ist nicht nötig.
- Ein-Klick Start:
	- Windows: Doppelklick auf `dist/Run-DataKeeper.bat` (oder `Run-DataKeeper.vbs` ohne Konsole)
	- Linux: Doppelklick auf `dist/Run-DataKeeper.sh` (Rechtsklick → Eigenschaften → „Als Programm ausführen“ bei Bedarf)
	- macOS: Doppelklick auf `dist/DataKeeper.command` (evtl. Rechtsklick → Öffnen beim ersten Start)
- بديل يدوي: `java -jar dist/DATA_KEEPER.jar`

## Erstellen der JAR (nur für Entwickler)
```sh
./package.sh
```

Die Ressourcen werden in die JAR eingebettet. Zur Laufzeit werden zunächst eingebettete Dateien genutzt; vorhandene Dateien unter `res/` (neben der JAR) können bevorzugt werden.

## JavaDoc ansehen
Öffnen Sie `docs/javadoc/index.html`. Achten Sie darauf, den gesamten Ordner `docs/javadoc/` zusammen zu kopieren, damit CSS/JS korrekt geladen werden.

## Hinweise
- Audio-Lautstärken können im Hauptmenü angepasst werden; Boss-Warnpiepser sind absichtlich leiser.

## Fehlerbehebung
- Wenn `java` nicht gefunden wird: Java Runtime (JRE) installieren und den Befehl erneut ausführen.
- Linux/macOS: Falls Doppelklick لا يعمل، أعطِ صلاحية تنفيذ للملف مرة واحدة:
	```sh
	chmod +x dist/Run-DataKeeper.sh
	chmod +x dist/DataKeeper.command
	```
- إذا فتحت JavaDoc بدون تنسيقات: افتح `docs/javadoc/index.html` بعد نسخ كامل مجلّد `javadoc/` مع المجلدات الفرعية.
