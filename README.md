# maze-explorer

A 2D tile-based maze exploration game built in Java Swing. The player navigates a scrolling maze as an animated penguin, avoiding 100 AI-controlled monsters while collecting keys, unlocking chests, and using portals to travel between maps. The project relies on a custom rendering pipeline and a real-time game loop managing sprite animations, entity collisions, and map rendering.

## 🚀 Overview
A desktop game where players navigate complex mazes while avoiding enemies. The game features a main maze and a secondary mini-maze accessed via portals. Players must find a hidden key in the mini-maze to unlock the exit door in the main maze. It includes collectibles like chests that grant power-ups, AI-controlled enemies with line-of-sight tracking, and an animated sprite-based rendering system.

## ✨ Features
- Tile-based maps loaded from external text files.
- Dynamic scrolling viewport that keeps the player centered on the screen.
- 100 AI monsters with proximity-based line-of-sight pursuit (within 5 tiles) and random wandering.
- Portal system for teleporting between the main map and sub-maps.
- Collectible items: keys for exiting and chests for double movement speed.
- Hidden key sequence detection (cheat code activation).
- Real-time movement and sprite sheet animation for player and monsters.

## ⭐ Technical Highlights
- **Real-Time Game Loop:** Replaced turn-based movement with a continuous 120ms tick rate using `javax.swing.Timer`, handling AI updates and screen repaints independently of user input.
- **Dynamic Viewport Camera:** Computes a 25x25 tile grid relative to the player's coordinate position on every frame, culling off-screen entities to maintain performance.
- **Proximity-Based Enemy AI:** Evaluates distance to trigger a line-of-sight pursuit algorithm when within 5 tiles; otherwise, defaults to constrained random walk generation, maintaining challenge without overwhelming the player.
- **Secret Sequence Parser:** Implements a rolling index matcher array tracking user keyboard events to activate hidden cheat codes without interrupting the main input processing.

## 🛠️ Technical Implementation
The game logic, rendering, and input handling are contained within a single `Maze` class extending `JPanel` (~630 lines of code).
- **Architecture:** The application initializes a `JFrame` and attaches `KeyListener` to process input. The main update cycle is driven by an asynchronous `javax.swing.Timer`.
- **Data Structures:** 2D String arrays store the map state. External text files represent tiles as characters (e.g., `#` for walls, `M` for monsters, `P` for portals).
- **Rendering Pipeline:** Within `paintComponent`, the background map tiles are rendered first based on camera offset. Collectibles (chests, portals) and monsters are layered next, followed by the active player sprite, and finally text overlays for game status.
- **Sprite Animation:** Extracted sequences from `.png` sprite sheets (`BufferedImage.getSubimage`). An incrementing counter modulo determines the current frame for character walk cycles.

## 🧠 Design Decisions & Challenges
- **External Map Storage:** Maps are loaded from text files (`maze.txt`, `mini-maze.txt`) rather than hardcoded arrays. This separates content from logic and allows for immediate map editing without recompiling the Java code.
- **Real-Time vs. Turn-Based:** The game shifted from turn-based logic to a real-time game loop via `javax.swing.Timer`. This created a more active gameplay experience but required decoupling the enemy AI update sequence from the player's keypress events.
- **AI Pursuit Balancing:** The line-of-sight algorithm restricts pursuit to a 5-tile radius. This constraint prevents all 100 monsters on the map from pathfinding to the player simultaneously, reducing computational load and keeping the game balanced.
- **Single-Class Architecture:** The entire logic and rendering pipeline was implemented within one class. While this simplified state sharing between rendering and game logic during early development, it results in tight coupling.

## 📁 Project Structure
- `Maze.java`: Main application containing rendering, game loop, and input handling.
- `maze.txt`: Text representation of the primary game level.
- `mini-maze.txt`: Text representation of the sub-map accessible via portals.
- `Images/`: Directory containing sprite sheets and item textures.
- `Monsters/`: Directory for additional enemy assets.

## ▶️ Running the Project
**Requirements:** Java Development Kit (JDK) 8 or higher.

1. Clone the repository and navigate to the project root:
   ```bash
   git clone https://github.com/MahitP/maze-explorer.git
   cd maze-explorer
   ```
2. Compile the Java file:
   ```bash
   javac Maze.java
   ```
3. Run the application:
   ```bash
   java Maze
   ```

**Controls:**
- **Arrow Keys:** Move player
- **P:** Pause/Unpause game
- **R:** Restart game after game over

## 🔮 Future Improvements
- Refactor the codebase into a Model-View-Controller (MVC) architecture to separate the rendering pipeline from entity logic.
- Implement additional levels and dynamic map loading.
- Add sound effects for collecting items and taking damage.
- Add an overlay minimap UI component.
- Implement varying difficulty settings and monster speed adjustments.

## Author
Mahit Pulavarthi — [github.com/MahitP](https://github.com/MahitP)
