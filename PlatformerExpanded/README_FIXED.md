# Platformer Arena Fighter - Fixed Version

A 2D survival arena fighter game with progressive level system and melee combat.

## Recent Fixes

### ✅ Issues Resolved
- **Removed Shooter and Tank enemies** - Now only melee enemies (like glitsoul)
- **Reduced movement speeds** - Player: 3.0 (was 5.0), Enemies: 2.0 (was 3.0)
- **Fixed enemy AI** - Enemies now properly attack instead of just sticking to player
- **Fixed ConcurrentModificationException** - Game no longer crashes during rendering
- **Improved attack behavior** - Enemies stop and attack when in range

## Features

### Core Gameplay
- **Survival Mode**: Survive for a set duration while fighting waves of enemies
- **Progressive Levels**: 3+ levels with increasing difficulty
- **Melee Combat**: All enemies fight with hands, no ranged attacks
- **Portal System**: Complete the timer to spawn exit portal and advance to next level

### Enemy Type
- **Chaser Enemy**: Melee attackers that chase and fight the player
  - Speed: 2.0 (slower, more manageable)
  - Health: 100 HP
  - Damage: 10 per hit
  - Attack range: 150 pixels
  - Properly attacks when close instead of just sticking

### Player Abilities
- **Movement**: WASD controls (speed: 3.0)
- **Jump**: Space bar (double jump available)
- **Dash**: Shift key for quick evasion
- **Slide**: S key for low-profile movement with damage
- **Combo Attacks**: E key for ground combos with increasing damage
- **Air Attack**: W key for aerial combat

### Level Progression
- **Level 1**: 30 seconds, 5 enemies
- **Level 2**: 45 seconds, 8 enemies
- **Level 3**: 60 seconds, 12 enemies
- **Level 4+**: Dynamically scaled (more enemies, longer time)

## Controls

### Movement
- **A**: Move Left
- **D**: Move Right
- **Space**: Jump (press twice for double jump)
- **Shift**: Dash
- **S**: Slide

### Combat
- **E**: Attack / Enter Portal
- **W**: Air Attack (while airborne)

### Game Flow
- **Enter**: Continue to next level (on level complete screen)
- **R**: Restart game (on game over screen)

## How to Compile and Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher

### Compilation
```bash
cd PlatformerExpanded
./compile.sh
```

Or on Windows:
```
compile.bat
```

### Running
```bash
./run.sh
```

Or on Windows:
```
run.bat
```

## Game Mechanics

### Survival Timer
- Each level has a specific duration
- Timer counts down in real-time
- When timer reaches 0:
  - Enemy spawning stops
  - All remaining enemies despawn
  - Exit portal appears at arena center

### Enemy Spawning
- Enemies spawn at intervals based on level configuration
- Spawn locations are randomized at arena edges
- Spawning continues until level enemy cap is reached
- Spawning stops when timer completes

### Enemy AI Behavior
- **Detection**: Enemies detect player within 600 pixels
- **Chase**: Move towards player at speed 2.0
- **Attack**: When within 150 pixels, stop and attack
- **Cooldown**: 60 frames between attacks (0.5 seconds at 120 FPS)

### Combat System
- **Combo System**: Chain attacks for increased damage
- **Invincibility Frames**: Brief protection during certain actions
- **Jump Over**: Jump on enemies to avoid damage
- **Slide Attack**: Low-profile movement with damage

## Customization

### Adjusting Difficulty
Modify values in `LevelConfig.java`:
- `duration`: Survival time in seconds
- `chaserCount`: Number of enemies
- `spawnInterval`: Time between enemy spawns

### Enemy Stats
Adjust in `ChaserEnemy.java`:
- `health`: Enemy health points (default: 100)
- `speed`: Movement speed (default: 2.0f)
- `ATTACK_DAMAGE`: Damage dealt to player (default: 10)
- `ATTACK_COOLDOWN_MAX`: Frames between attacks (default: 60)

### Player Stats
Adjust in `Player.java`:
- `getSpeed()`: Movement speed (default: 3.0f)
- `maxHealth`: Maximum health (default: 200)
- `jumpSpeed`: Jump force (default: -14.0f)
- `dashSpeed`: Dash speed (default: 20.0f)

## Technical Details

### Performance
- Target FPS: 120
- Resolution: 1800x1000
- Arena Size: 2000x1000

### Fixed Issues
1. **ConcurrentModificationException**: Used list copies during rendering
2. **Enemy sticking**: Improved AI to stop and attack when in range
3. **Too fast movement**: Reduced player and enemy speeds
4. **No attacking**: Fixed attack trigger logic

## Version
**Fixed Edition v1.1**
- Removed shooter and tank enemies
- Balanced movement speeds
- Fixed enemy AI
- Fixed crash bugs
- Improved combat feel

