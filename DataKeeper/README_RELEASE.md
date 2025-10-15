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
