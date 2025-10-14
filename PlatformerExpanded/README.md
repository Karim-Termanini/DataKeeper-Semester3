# Platformer DATA KEEPER Expanded Edition

## Overview

A 2D survival arena fighter game with progressive level system, multiple enemy types, and dynamic combat mechanics.

## Features

### Core Gameplay
- **Survival Mode**: Survive for a set duration while fighting waves of enemies
- **Progressive Levels**: 3+ levels with increasing difficulty
- **Multiple Enemy Types**:
  - **Chaser**: Fast melee attackers that chase the player
  - **Shooter**: Ranged enemies that maintain distance and fire projectiles
  - **Tank**: Slow, high-HP enemies with heavy damage
- **Portal System**: Complete the timer to spawn exit portal and advance to next level

### Player Abilities
- **Movement**: WASD controls with smooth animations
- **Jump**: Space bar (double jump available)
- **Dash**: Shift key for quick evasion
- **Slide**: S key for low-profile movement with damage
- **Combo Attacks**: E key for ground combos with increasing damage
- **Air Attack**: W key for aerial combat
- **Combat System**: Combo multipliers, invincibility frames, and strategic positioning

### Level Progression
- **Level 1**: 30 seconds, 5 Chaser enemies
- **Level 2**: 45 seconds, 8 enemies (5 Chasers + 3 Shooters)
- **Level 3**: 60 seconds, 12 enemies (5 Chasers + 4 Shooters + 3 Tanks)
- **Level 4+**: Dynamically scaled difficulty

### UI Features
- Real-time health bar with color indicators
- Countdown timer with visual warnings
- Level number and enemy counter
- Combo display
- Level complete screen with stats
- Game over screen with restart option

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
- Terminal/Command Prompt access

### Compilation
```bash
cd PlatformerExpanded
javac -d out src/**/*.java
```

### Running
```bash
cd PlatformerExpanded
java -cp out:res main.Main
```

### Alternative: Using IDE
1. Open the `PlatformerExpanded` folder in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Mark `src` as source root
3. Mark `res` as resources root
4. Run `main.Main`

## Project Structure

```
PlatformerExpanded/
├── src/
│   ├── entities/
│   │   ├── Character.java          # Interface for all characters
│   │   ├── GameCharacter.java      # Abstract base class
│   │   ├── Player.java             # Player character with all abilities
│   │   ├── Enemy.java              # Original enemy (legacy)
│   │   ├── ChaserEnemy.java        # Fast melee enemy
│   │   ├── ShooterEnemy.java       # Ranged projectile enemy
│   │   ├── TankEnemy.java          # Heavy damage tank enemy
│   │   └── Projectile.java         # Projectile for shooter enemies
│   ├── levels/
│   │   ├── LevelConfig.java        # Level configuration data
│   │   ├── LevelManager.java       # Manages level progression
│   │   ├── Portal.java             # Exit portal for level completion
│   │   └── SpawnManager.java       # Handles enemy spawning
│   ├── gameplay/
│   │   ├── GameState.java          # Game state enumeration
│   │   └── SurvivalTimer.java      # Countdown timer system
│   ├── ui/
│   │   ├── HUD.java                # Heads-up display
│   │   ├── LevelCompleteScreen.java # Victory screen
│   │   └── GameOverScreen.java     # Defeat screen
│   ├── inputs/
│   │   ├── KeyboardInputs.java     # Keyboard input handler
│   │   └── MouseInputs.java        # Mouse input handler
│   ├── main/
│   │   ├── Main.java               # Entry point
│   │   ├── Game.java               # Game loop
│   │   ├── GamePanel.java          # Main game panel with rendering
│   │   └── GameWindow.java         # Window setup
│   └── utils/
│       └── AnimationManager.java   # Animation utilities
└── res/
    ├── Enemy/
    │   └── glitsoul/               # Enemy sprite sheets
    └── Fighter sprites/            # Player sprite sheets
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

### Combat System
- **Combo System**: Chain attacks for increased damage
- **Invincibility Frames**: Brief protection during certain actions
- **Jump Over**: Jump on enemies to avoid damage
- **Slide Attack**: Low-profile movement with damage
- **Projectile Dodging**: Avoid shooter enemy projectiles

### Enemy AI
- **Chasers**: Direct pursuit, melee attacks when in range
- **Shooters**: Maintain distance, fire projectiles at player
- **Tanks**: Slow advance, heavy damage, damage reduction

## Customization

### Adding New Levels
Edit `LevelConfig.java` to add new level configurations:
```java
public static LevelConfig getLevel4() {
    return new LevelConfig(4, 75, 8, 6, 4, 3.5f);
}
```

### Adjusting Difficulty
Modify values in `LevelConfig.java`:
- `duration`: Survival time in seconds
- `chaserCount`, `shooterCount`, `tankCount`: Enemy quantities
- `spawnInterval`: Time between enemy spawns

### Enemy Stats
Adjust in respective enemy classes:
- `health`: Enemy health points
- `speed`: Movement speed
- `ATTACK_DAMAGE`: Damage dealt to player
- `ATTACK_COOLDOWN_MAX`: Time between attacks

## Technical Details

### Performance
- Target FPS: 120
- Resolution: 1800x1000
- Arena Size: 2000x1000

### Camera System
- Smooth camera following player
- Bounded to arena limits
- UI rendered without camera offset

### Animation System
- Frame-based sprite animation
- Variable animation speeds per action
- Automatic frame cycling and completion handling

## Known Features
- Combo system with damage multipliers
- Double jump mechanics
- Enemy type visual distinction (color overlays)
- Pulsing portal animation
- Health bar color indicators
- Timer color warnings

## Credits
- Original game concept and base mechanics
- Expanded with level system, multiple enemy types, and survival mode
- Sprite assets from original project

## Version
**Expanded Edition v1.0**
- Full level progression system
- 3 enemy types
- Survival timer mechanics
- Portal system
- Complete UI overhaul

