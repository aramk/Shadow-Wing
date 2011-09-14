/* SWEN20003 Object Oriented Software Development
 * World Class
 * Author: Aram Kocharyan <aramk>
 */

import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.Vector;
import java.util.Collections;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/**
 * Represents the entire game world. (Designed to be instantiated just once for
 * the whole game).
 */
public class World {
	/** The default starting checkpoint for player */
	private static final int START_CHECKPOINT = 13716;
	/** The top Y coordinate of the map */
	public static final int MAP_TOP_Y = 0;
	/** The index in the check array that stores the block check */
	public static final int BLOCK_CHECK = 0;
	/** The index in the check array that stores the block position */
	public static final int BLOCK_POSITION = 1;
	/** No block detected */
	public static final int NO_BLOCK_AHEAD = 0;
	/** Block detected */
	public static final int BLOCK_AHEAD = 1;
	/** Lower boundary of a tile (left/top edge) */
	public static final int LOWER_BOUNDARY = 0;
	/** Upper boundary of a tile (right/bottom edge) */
	public static final int UPPER_BOUNDARY = 1;
	/** Not sitting on the border of a tile */
	public static final int NOT_ON_BORDER = 0;
	/** Value to increase by */
	public static final int INCREMENT = 1;
	/** Value to decrease by */
	public static final int DECREMENT = -1;
	/** The player */
	private Player player;
	/** The boss, when defeated or null the game will finish once the player
	 * reaches the top of the map */
	private Boss boss;
	/** The camera */
	private Camera camera;
	/** The panel */
	private Panel panel;
	/** The map */
	private TiledMap map;
	/** The game that instantiated this world */
	private Game game;
	/** Whether the world has started */
	private boolean isStarted = false;
	/** Collection of player checkPoints */
	private ArrayList<Double> checkPoints = new ArrayList<Double>();
	/** Collection of GameObjects */
	private ArrayList<GameObject> objectList = new ArrayList<GameObject>();
	/** Collection of GameObjects to be removed */
	private ArrayList<GameObject> objectRemoveList = new ArrayList<GameObject>();

	/**
	 * Create a new World object. References a preloaded map resource from Game
	 * and creates the player, panel, game objects etc.
	 * 
	 * @throws InterruptedException
	 */
	public World(Game game, TiledMap map) throws SlickException {
		// reference the preloaded map object passed from Game
		// this way, the map that world uses can be directly changed
		setMap(map);
		// remember the game that instantiated world
		this.game = game;
		// create the panel
		panel = new Panel();
		// create the player and read in the input objects, items and
		// checkpoints
		ObjectFileReader.readInput(this, Game.ASSETS_PATH + "config.txt");
		// if there was an error creating player from input, or if the player is
		// defined off the map, resort to the default configuration
		if (player == null) {
			System.err.println("Error creating player, resorting to default");
			player = new Player(getMapWidthPixels() / 2, START_CHECKPOINT, this);
			addObject(player);
		}
		// add the starting player Y as a checkPoint
		addCheckPoint(player.getY());
		// create a new camera and assign starting coordinates
		camera = new Camera(this, player);
	}

	/**
	 * Update the game state for a frame.
	 * 
	 * @param dir_x
	 *            The player's movement in the x axis
	 * @param dir_y
	 *            The player's movement in the y axis
	 * @param delta
	 *            Time passed since last frame (milliseconds).
	 * @param playerShoot
	 *            The player shoot command when shoot key is pressed
	 * @throws SlickException
	 * @throws Throwable
	 */
	public void update(double dirX, double dirY, int delta, boolean playerShoot)
			throws SlickException {

		// if the game has not yet started, do not update anything
		if (!isStarted) {
			return;
		}

		// set camera step for this update round and send player the movement
		// commands we need to set the correct camera step using the current
		// delta so that the objects are aware how much the camera will move
		// this round
		camera.setStep(delta);
		player.setMoveX(dirX);
		player.setMoveY(dirY);

		// in order to avoid concurrent access to object, which may be removed
		// at whim, I have avoided using a for each loop. by looping over the
		// size of the objectList, I can ensure that any objects added to the
		// end of the list this update() round are updated.
		for (int i = 0; i < objectList.size(); i++) {
			// the current object
			GameObject object = objectList.get(i);
			// if the object is not destroyed
			if (!object.getIsDestroyed()) {
				// if the object is of the Object Class, movement is controlled
				// through AI()
				if (object instanceof Object) {
					((Object) object).AI(delta, this, camera);
					// player shooting command, only called if current object
					// is player to avoid excessive calls
					if (object.getIsPlayer() && playerShoot) {
						player.shoot(this);
					}
					// if the object is not of the Object Class, it has no AI()
					// method, only call move(), provided it is on screen
				} else if (object.getIsOnScreen(camera)) {
					object.move(delta, this, camera);
				}
				// if the object is a Unit and it is destroyed, run the
				// explosion
			} else if (object instanceof Unit && object.getIsDestroyed()) {
				((Unit) object).explode(this);
			}
			// update each object's graphics
			object.updateGraphics(camera);
		}

		// remove objects once they are not in use. used to avoid concurrent
		// access when removing an object that is being iterated over above
		for (GameObject object : objectRemoveList) {
			objectList.remove(object);
		}
		objectRemoveList.clear();

		// move the camera
		camera.move(this, delta);
	}

	/**
	 * renders the map at the camera X and Y, and the tiles starting from the
	 * tile at the top of the screen down to the bottom of the screen height.
	 * Uses dynamic caching to ensure that only the tiles visible on the screen
	 * are drawn
	 */
	public void render(Graphics g) throws SlickException {
		// draw the moving background first, then overlay map onto it
		// if the game is not started, the background is still visible
		drawBackground();

		// if the game has not yet started, do not render anything else
		if (!isStarted) {
			return;
		}

		// map parameters
		int cam_tile_x = (int) camera.getLeft() / map.getTileWidth();
		int cam_offset_x = (int) camera.getLeft() % map.getTileWidth();
		int cam_tile_y = (int) camera.getTop() / map.getTileHeight();
		int cam_offset_y = (int) camera.getTop() % map.getTileHeight();

		// draw the map
		map.render(-cam_offset_x, -cam_offset_y, cam_tile_x, cam_tile_y,
				getMapWidth(), getTileScreenHeight());

		// draw the graphics for each object
		for (int i = 0; i < objectList.size(); i++) {
			GameObject object = objectList.get(i);
			object.renderGraphics(game, camera);
		}

		// render the panel, player score and lives
		panel.render(g);
		player.renderScoreAndLives();
	}

	/**
	 * A private method used only to draw a starry background, can only be
	 * called from within World
	 */
	private void drawBackground() {
		double backgroundSpeed = 0.1;
		// draw the background
		double background_offset_y = camera.getTop() * backgroundSpeed
				% Game.background.getHeight();
		// how many backgrounds to draw for X (repetitions)
		int backgroundCountX = (int) Math.ceil((double) Game.playwidth()
				/ Game.background.getWidth());
		// how many backgrounds to draw for Y (repetitions).
		// remember we need a buffer of +1 to create illusion of movement, as
		// the background will be moving down
		int backgroundCountY = (int) Math.ceil((double) Game.playheight()
				/ Game.background.getHeight()) + 1;
		// for however many times we repeat background for X
		for (int i = 0; i < backgroundCountX; i++) {
			// for however many times we repeat background for Y
			for (int j = 0; j < backgroundCountY; j++) {
				// draw the background for every repetition needed in X Y
				// directions
				Game.background.draw(
						i * Game.background.getWidth(),
						-(float) background_offset_y + j
								* Game.background.getHeight());
			}
		}
	}

	/**
	 * Checks for collisions between two objects. Some collisions are not
	 * relevant or are ignored as required.
	 * 
	 * @param self
	 *            The GameObject that has called the collision check
	 * @throws SlickException
	 */
	public void checkObjectCollsion(GameObject self) throws SlickException {
		// Iterate over all objects. Note that this process is called within
		// move(), but since we have a separate list for removing objects,
		// we do not cause a concurrency exception with the destroy command.
		for (GameObject other : objectList) {
			// don't treat as collision if:
			if (	// we compare with ourselves or a null object (unlikely)
					other == null
					|| self == other
					// we compare with a destroyed object
					|| other.getIsDestroyed()
					// we compare two missiles
					|| self instanceof Missile
					&& other instanceof Missile
					// we compare our missile to ourself - we can't shoot
					// ourselves
					|| self instanceof Missile
					&& ((Missile) self).getParent() == other
					|| other instanceof Missile
					&& ((Missile) other).getParent() == self
					// we compare an Object other than the Player and an Item,
					// so only allow Players to pick up items
					|| !self.getIsPlayer() && other instanceof Item
					|| !other.getIsPlayer() && self instanceof Item
					// either object has noClip enabled
					|| self.getNoClip() || other.getNoClip()) {
				continue;
			}

			// collision check law
			if ((self.getBottom() > other.getTop()
						&& self.getTop() < other.getBottom())
					&& (self.getRight() > other.getLeft() 
						&& self.getLeft() < other.getRight())) {
				// if the Player collides with an Item of any kind, initiate
				// itemAction
				if (self.getIsPlayer() && other instanceof Item) {
					// cast other to an Item class and call the itemAction
					// method
					((Item) other).itemAction((Unit) self);
					removeObject(other);
				} else if (other.getIsPlayer() && self instanceof Item) {
					// cast self to an Item class and call the itemAction method
					((Item) self).itemAction((Unit) other);
					removeObject(self);
				// otherwise we have made all required checks and we can begin
				// damaging the two objects
				} else if (self instanceof Object && other instanceof Object) {
					// begin damaging and destroying due to collision
					damageObjects((Object) self, (Object) other);
				}
			}
		}
	}

	/**
	 * Called when a collision is detected between Objects A and B. Repeatedly
	 * damages both until one is destroyed. Only called by checkObject()
	 * locally.
	 */
	private void damageObjects(Object objectA, Object objectB)
			throws SlickException {
		// repeatedly damage if either have shield
		while (objectA.getShield() > Object.NO_SHIELD
				&& objectB.getShield() > Object.NO_SHIELD) {
			// update the remaining shield 
			updateShield(objectA, objectA.getShield() - objectB.getDamage());
			updateShield(objectB, objectB.getShield() - objectA.getDamage());
		}
		// once either are destroyed, check which 
		checkObjectDestroyed(objectA, objectB);
		checkObjectDestroyed(objectB, objectA);
	}

	/** Updates the players shield to a valid value */
	private void updateShield(Object object, int remainShield) {
		if (remainShield > Object.NO_SHIELD) {
			// update the shield
			object.setShield(remainShield);
		} else {
			// the object has no shield left
			object.setShield(Object.NO_SHIELD);
		}
	}
	
	/** Checks whether an object is destroyed and performs actions */
	private void checkObjectDestroyed(Object self, Object other) {
		if (self.getShield() == Object.NO_SHIELD) {
			// if not the Player, remove the object
			if (!self.getIsPlayer()) {
				removeObject(self);
				// if the destroyer is the player, add score
				if (other.getIsPlayer() && !(self instanceof Missile)
						|| other instanceof Missile
						&& ((Missile) other).getParent() == player) {
					player.addTotalScore(self.getScore());
				}
				// if the Player, load the checkpoint
			} else {
				goToCheckPoint();
			}
		}
	}
	
	/**
	 * Checks the map for a blocking tile with the given arguments
	 * @param x
	 * 			The X position of the object. This is considered the lower X
	 * 			(left edge) value.
	 * @param y
	 * 			The Y position of the object. This is considered the lower Y
	 * 			(top edge) value.
	 * @param xORy
	 * 			Depending on the direction traveled, either an X or Y value.
	 * 			This is considered the upper X (right edge) or Y (bottom edge) value 
	 * @param moveX
	 * 			The X movement direction
	 * @param moveY
	 * 			The Y movement direction
	 * @return A two element array. Element 1 determines if there is a block
	 * in the direction traveled and element 2 gives the coordinate of the
	 * block's wall in the direction being traveled.
	 */
	public double[] checkTile(double x, double y, double xORy, double moveX,
			double moveY) {
		// X and Y of the checked tile
		double tileX, tileY;
		// array to return
		double[] checkArray = { NO_BLOCK_AHEAD, NO_BLOCK_AHEAD };

		// determines whether we want the left/top or right/bottom boundary of
		// a block
		int lowerOrUpper = LOWER_BOUNDARY;
		// used to check if we are on the border of a block and also to store
		// the tileX or tileY (depending on direction)
		double checkBorder = NOT_ON_BORDER;
		
		// while we have not encountered a blocked tile
		while (checkArray[BLOCK_CHECK] == NO_BLOCK_AHEAD) {
			// if moving left or right
			if (moveX != Game.DIR_NONE) {
				// if traveling left, make sure to return the right edge of the
				// blocked tile
				if (moveX == Game.DIR_LEFT) {
					lowerOrUpper = UPPER_BOUNDARY;
				}
				// set checkBorder and tileX and tileY by converting the X and Y
				// in pixels to a tile X and Y
				// if rounding down checkBorder is zero, we are on a border 
				checkBorder = y / getTileHeight();
				// we check the block in the direction we are traveling
				tileX = Math.floor((x + moveX * (getTileWidth() + DECREMENT))
						/ getTileWidth());
				tileY = Math.floor(checkBorder);
				// if our tileY is on the border, we need to increment Y and
				// check again to avoid a false positive block
				if (checkBorder == tileY) {
					// if we haven't reached our upper Y
					if (y != xORy) {
						// increment Y and check again
						y += INCREMENT;
					// if we have reached our upper Y
					} else {
						// decrement Y and check again
						y += DECREMENT;
					}
					continue;
				}
				// check if the tile at X and Y is blocked
				checkArray[BLOCK_CHECK] = checkBlocked(tileX, tileY);
				// return the X value in pixels of the checked tile
				checkArray[BLOCK_POSITION] = (tileX + lowerOrUpper)
						* getTileWidth();
				
				// if the difference between our lower and upper Y values
				// (topEdge to bottomEdge) is greater than the height of a tile
				// we should increment our current Y by the tile height and
				// check this Y position. This prevents larger objects moving
				// through small tiles.
				if ((xORy - y) > getTileHeight()) {
					y += getTileHeight();
				// if we have reached the upper Y value stop checking
				} else if (y >= xORy + DECREMENT) {
					break;
				// the difference between our lower and upper Y is less than
				// a tile, so just check the upper value
				} else {
					y = xORy;
				}
			}
			// if moving up or down, the same process is used
			else if (moveY != Game.DIR_NONE) {
				if (moveY == Game.DIR_UP) {
					lowerOrUpper = UPPER_BOUNDARY;
				}
				checkBorder = x / getTileWidth();
				tileX = Math.floor(checkBorder);
				tileY = Math.floor((y + moveY * (getTileHeight() + DECREMENT))
						/ getTileHeight());
				if (checkBorder == tileX) {
					if (x != xORy) {
						x += INCREMENT;
					} else {
						x += DECREMENT;
					}
					continue;
				}
				checkArray[BLOCK_CHECK] = checkBlocked(tileX, tileY);
				checkArray[BLOCK_POSITION] = (tileY + lowerOrUpper)
						* getTileHeight();
				if ((xORy - x) > getTileWidth()) {
					x += getTileWidth();
				} else if (x >= xORy + DECREMENT) {
					break;
				} else {
					x = xORy;
				}
			}
		}
		// return the checkArray
		return checkArray;
	}

	/**
	 * Checks the blocked property of the tile at tileX and tileY.
	 * @param tileX
	 * 				The X coordinate of tile to check in tiles.
	 * @param tileY
	 * 				The Y coordinate of tile to check in tiles.
	 * @return either 0 for 1 for "not a blocked tile" or a "blocked tile",
	 * depending on the property set in the TiledMap.
	 */
	public double checkBlocked(double tileX, double tileY) {
		int layerIndex = 0;
		// if we pass an invalid argument, don't attempt to return the tile 
		// property. important for when we run into screen edges
		if (tileX < 0 || tileX > getMapWidth() - 1 || tileY < 0
				|| tileY > getMapHeight() - 1) {
			return 0;
		}
		int tileID = map.getTileId((int) tileX, (int) tileY, layerIndex);
		return Integer.parseInt(map.getTileProperty(tileID, "block", "0"));
	}

	/**
	 * Adds a new checkPoint
	 * @param y
	 * 			The Y value of the checkPoint
	 */
	public void addCheckPoint(double y) {
		// if it's a valid checkPoint, add it
		if (y > 0 && y <= START_CHECKPOINT && !checkPoints.contains(y)) {
			checkPoints.add(y);
		}
		// sort the collection of checkPoints
		Collections.sort(checkPoints, Collections.reverseOrder());
	}

	/**
	 * Returns the last checkpoint Y coordinate the player has passed
	 * @param y
	 * 			The Y coordinate of the player
	 * @return
	 * 			The Y coordinate of the last passe checkPoint
	 */
	public double getCheckPoint(double y) {
		// for all checkPoints
		for (double checkPoint : checkPoints) {
			// if current checkPoint is less than player Y (we encounter a
			// checkpoint the player has not reached)
			if (checkPoint < y) {
				// if it isn't the first checkPoint
				if (checkPoint != checkPoints.get(0)) {
					// return the previous checkPoint
					return checkPoints.get(checkPoints.indexOf(checkPoint) - 1);
				} else {
					// otherwise return the first checkPoint (the only one the
					// player has passed)
					return checkPoints.get(0);
				}
			}
		}
		// if we don't encounter any checkpoints the player has not reached,
		// return the last checkPoint
		return checkPoints.get(checkPoints.size() - 1);
	}

	/**
	 * Called when the player is destroyed, loads the new checkpoint and resumes
	 * the camera, or if we are out of lives, declares the game over.
	 */
	public void goToCheckPoint() {
		// if we have more than one life
		if (player.getLives() > Player.ONE_LIFE) {
			// reset the player
			player.setIsDestroyed(false);
			player.setShield(player.getFullShield());
			player.setY(getCheckPoint(player.getY()));
			player.setX(getMapWidthPixels() / 2);
			// reset the camera
			camera.setTop(this, player);
			camera.resume();
			// decrement the lives
			player.setLives(player.getLives() - 1);
		} else {
			// game over
			player.setLives(player.getLives() - 1);
			game.gameOver();
		}
	}

	/**
	 * Checks whether an object is on the map or not.
	 * @param object
	 * 			Any GameObject
	 * @return True or false, depending on whether the object is on the map.
	 */
	public boolean getIsOnMap(GameObject object) {
		if (object.getLeft() < 0 || object.getTop() < 0
				|| object.getBottom() > getMapHeightPixels()
				|| object.getRight() > getMapWidthPixels()) {
			return false;
		} else {
			return true;
		}
	}
	
	/** 
	 * Adds an object to the world.
	 * @param object
	 * 			Any GameObject
	 */
	public void addObject(GameObject object) {
		// if the object isn't null, is on the map and is not destroyed
		if (object != null && getIsOnMap(object) && !object.getIsDestroyed()) {
			// add it to the world
			objectList.add(object);
		} else {
			System.err
					.printf("Error: cannot add object %s at (%f,%f) %n",
							object.getClass(), object.getX(), object.getY());
		}
	}

	/**
	 * Removes an object from the world.
	 * @param object
	 * 			Any GameObject
	 */
	public void removeObject(GameObject object) {
		if (object != null) {
			// if the object isn't a Unit, remove it. If it is, only remove it
			// if it has already been destroyed before.
			// This ensures that the explosion can take place before removing
			// the object and it's graphics
			if (!(object instanceof Unit) || object instanceof Unit
					&& object.getIsDestroyed()) {
				// adds it the the remove list to be removed at end of update()
				objectRemoveList.add(object);
			}
			// set the object as destroyed
			object.setIsDestroyed(true);
		}
	}

	/** Returns the difference between the width of the screen and the map. */
	public double getMapScreenDiff() {
		int mapScreenDiff = getMapWidthPixels() - Game.playwidth();
		// if the map is narrower than the screen, ignore the difference.
		if (mapScreenDiff < 0) {
			mapScreenDiff = 0;
		}
		return mapScreenDiff;
	}

	/** Returns the X offset needed to ensure that the player can access
	 * all parts of the map through horizontal scrolling. The maximum value
	 * returned is the offset between the screen width and the map width
	 * on either side (half of the total offset) */
	public double getPlayerMapOffset() {
		// the player's screen X coordinate
		double screenX = player.getX() - camera.getLeft();
		// the deviation from the centre of the screen
		double deviation = screenX - Game.playwidth() / 2;
		// the maximum possible deviation from the centre of the screen
		double maxDeviation = (Game.playwidth() - player.getWidth()) / 2;
		// return the ratio from 0 to 1 of the deviation of the player from
		// the centre of the screen multiplied by the difference between the
		// screen and the map on either side of the screen (half of total).
		return deviation / maxDeviation * getMapScreenDiff() / 2;
	}

	/** Returns the map for the world. */
	public TiledMap getMap() {
		return map;
	}

	/** Sets the map for the world. */
	public void setMap(TiledMap map) {
		this.map = map;
	}

	/** Get the width of the map in tiles. */
	public int getMapWidth() {
		if (map != null) {
			return map.getWidth();
		}
		return 0;
	}

	/** Get the height of the map in tiles. */
	public int getMapHeight() {
		if (map != null) {
			return map.getHeight();
		}
		return 0;
	}

	/** Get the width of the map in pixels. */
	public int getMapWidthPixels() {
		return getMapWidth() * getTileWidth();
	}

	/** Get the height of the game world in pixels. */
	public int getMapHeightPixels() {
		return getMapHeight() * getTileHeight();
	}

	/** Get the height of a single tile. */
	public int getTileHeight() {
		if (map != null) {
			return map.getTileHeight();
		}
		return 0;
	}

	/** Get the width of a single tile. */
	public int getTileWidth() {
		if (map != null) {
			return map.getTileWidth();
		}
		return 0;
	}

	/** Get the number of tiles that can fit on the screen. */
	public int getTileScreenHeight() {
		return 10;
	}

	/** Set the player */
	public void setPlayer(Player player) {
		this.player = player;
		addObject(player);
	}

	/** Get the player */
	public Player getPlayer() {
		return player;
	}

	/** Set the boss */
	public void setBoss(Boss boss) {
		this.boss = boss;
		addObject(boss);
	}

	/** Get the boss */
	public Boss getBoss() {
		return boss;
	}

	/** Set the camera */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	/** Get the camera */
	public Camera getCamera() {
		return camera;
	}

	/** Set the panel */
	public void setPanel(Panel panel) {
		this.panel = panel;
	}
	
	/** Get the panel */
	public Panel getPanel() {
		return panel;
	}

	/** Returns whether the world has started */
	public boolean getIsStarted() {
		return isStarted;
	}

	/** Sets whether the world has started */
	public void setIsStarted(Boolean isStarted) {
		this.isStarted = isStarted;
	}

	/** Gets the Game that instantiated this world */
	public Game getGame() {
		return game;
	}
}
