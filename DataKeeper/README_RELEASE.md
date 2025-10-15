# DATA KEEPER — Release Build

## How to run (JAR)
1. Build the release JAR

```sh
./package.sh
```

2. Run it

```sh
java -jar dist/DATA_KEEPER.jar
```

## Notes
- Resources are embedded in the JAR; the game will also look for files under `res/` on the filesystem if present.
- If you want a custom boss background image, put it at `res/level4-Backgrounds/boss.png` before packaging.
- Audio volumes can be adjusted in the main menu. Warning beep for boss telegraphs is intentionally quieter.

## Troubleshooting
- If audio fails on some Linux setups (headless audio drivers), the game will fall back to synthesized SFX/music where possible.
- For better performance, close other heavy apps while playing.
