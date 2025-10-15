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
- Telegraph‑Visuals: Pfeلات/دوائر متقطعة/Reticle أرضي + ومضة بدء الهجوم.
- Physik beim Sprung: vy + Gravitation، هبوط على خط baseline، AOE عند الاصطدام بالأرض.

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
