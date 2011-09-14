/* SWEN20003 Object Oriented Software Development
 * Shadow Wing Game
 * Author: Aram Kocharyan <aramk>
 */

import java.io.File;
import java.io.IOException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.tiled.TiledMap;

/**
 * Main class for the Role-Playing Game engine. Handles initialisation, input
 * and rendering.
 */
public class Game extends BasicGame {
	/** Location of the "assets" directory. */
	public static final String ASSETS_PATH = "assets/";

	/** Location of the "units" directory. */
	public static final String UNITS_PATH = ASSETS_PATH + "units/";

	/** Location of the "panel" directory. */
	public static final String PANEL_PATH = ASSETS_PATH + "panel/";

	/** Location of the "items" directory. */
	public static final String ITEMS_PATH = ASSETS_PATH + "items/";

	/** Location of the "numbers" directory. */
	public static final String NUMBERS_PATH = Game.ASSETS_PATH + "numbers/";

	/** Location of the "sound" directory. */
	public static final String SOUND_PATH = Game.ASSETS_PATH + "sound/";

	/** debugging switch */
	public static final boolean DEBUG = true;

	/** sound effect volume */
	public static final float SOUND_EFFECT_VOLUME = 0.4f;

	/** draw frame rate */
	public static final int SCREEN_FRAME_RATE = 60;

	/** hit-box precision that defines dimensions constant */
	public static final double HITBOX_PRECISION = 0.75;

	/** used to notify an unset double or int, for example width or height */
	public static final int UNDEFINED = -1;

	/** The screen X coordinate for the top of the screen */
	public static final int SCREEN_LEFT_X = 0;
	
	/** The screen Y coordinate for the Menu */
	public static final int SCREEN_TOP_Y = 0;
	
	/** whether the game has loaded all resources */
	private boolean loaded = false;

	/** Game Container */
	private static AppGameContainer app;

	/** if game is full-screen */
	private boolean isFullScreen = false;
	
	/** if game is over */
	private boolean isGameOver = false;

	/** if sound should be played */
	private boolean isSoundEnabled = true;
	
	/** if sound was enabled before we paused */
	private boolean isLastSoundEnabled = true;
	
	/** directions for use when assigning animations */
	public static enum animDirection {
		ANIM_NONE, ANIM_UP, ANIM_DOWN, ANIM_LEFT, ANIM_RIGHT;
	}

	/** directions for use when checking movement */
	public static int DIR_NONE = 0, DIR_UP = -1, DIR_DOWN = 1, DIR_LEFT = -1,
			DIR_RIGHT = 1;

	/** allowed input types from config.txt */
	public static enum inputType {
		PLAYER, FIGHTER, DRONE, ASTEROID, BOSS, TURRET, REPAIR, SHIELD, FIREPOWER, CHECKPOINT;
	}

	/** The game state. */
	private World world;
	/** Screen width, in pixels. */
	private static final int screenwidth = 800;
	/** Screen height, in pixels. */
	private static final int screenheight = 600;
	/** The next resource to load */
	private DeferredResource nextResource;
	/** The game menu */
	private Menu gameMenu;

	// GAME RESOURCES

	/** The game map */
	protected static TiledMap map;

	/** The game image resources */
	protected static Image darkOverlay, paused, gameOver, asteroid, boss,
			boss2, boss3, drone, drone2, drone3, drone4, drone5, drone6,
			drone7, drone8, drone9, fighter, boosterFighter, boosterFighter2,
			boosterFighter3, firePower, firePower2, firePower3, missileBoss,
			missileBoss2, missileBoss3, missileEnemy, missilePlayer, num0, num1,
			num2, num3, num4, num5, num6, num7, num8, num9, panel, barBackground,
			barOverlay, repair, repair2, repair3, shield, shield2, shield3,
			background, player, player_shadow, booster_shadow, booster_shadow2,
			booster_shadow3, playerL1, playerL2, playerR1, playerR2,
			booster_smaller, booster_large, booster_large2, booster_large3,
			booster, booster2, booster3, explosion, explosion2, explosion3,
			explosion4, explosion5, explosion6, explosion7, explosion8,
			explosion9, explosion10, explosion11, explosion12, explosion13,
			explosion14, explosion15, explosion16, explosion17, menu, pointer,
			score, lives, large0, large1, large2, large3, large4, large5,
			large6, large7, large8, large9, turretBase, turret, turret2, turret3,
			pointerOn, pointerOff;

	/** The game music */
	protected static Music gameMusic;

	/** The game sounds */
	protected static Sound explosionSound, laserSound;

	/** Create a new Game object. */
	public Game() {
		super("Shadow Wing");
	}

	/**
	 * Initialise the game state and load all resources. Classes other than game
	 * should not be required to load resources from the hard drive. Rather,
	 * they are loaded here for convenience and efficiency to be referenced when
	 * needed.
	 * 
	 * @param gc
	 *            The Slick game container object.
	 */
	@Override
	public void init(GameContainer gc) throws SlickException {
		LoadingList.setDeferredLoading(true);
		String path;

		// MENUS AND MAIN
		path = Game.ASSETS_PATH;
		map = new TiledMap(path + "map.tmx", path);
		darkOverlay = newImage(path + "panel/dark_overlay");
		paused = newImage(path + "panel/paused");
		gameOver = newImage(path + "panel/gameover");
		background = newImage(path + "background");

		// UNITS
		path = Game.UNITS_PATH;
		asteroid = newImage(path + "asteroid/asteroid");
		boss = newImage(path + "boss/boss");
		boss2 = newImage(path + "boss/boss2");
		boss3 = newImage(path + "boss/boss3");
		drone = newImage(path + "drone/drone");
		drone2 = newImage(path + "drone/drone2");
		drone3 = newImage(path + "drone/drone3");
		drone4 = newImage(path + "drone/drone4");
		drone5 = newImage(path + "drone/drone5");
		drone6 = newImage(path + "drone/drone6");
		drone7 = newImage(path + "drone/drone7");
		drone8 = newImage(path + "drone/drone8");
		drone9 = newImage(path + "drone/drone9");
		fighter = newImage(path + "fighter/fighter");
		boosterFighter = newImage(path + "fighter/booster_fighter");
		boosterFighter2 = newImage(path + "fighter/booster_fighter2");
		boosterFighter3 = newImage(path + "fighter/booster_fighter3");
		missileBoss = newImage(path + "missile/missileBoss");
		missileBoss2 = newImage(path + "missile/missileBoss2");
		missileBoss3 = newImage(path + "missile/missileBoss3");
		missileEnemy = newImage(path + "missile/missileEnemy");
		missilePlayer = newImage(path + "missile/missilePlayer");
		player = newImage(path + "player/player");
		player_shadow = newImage(path + "player/player_shadow");
		booster_shadow = newImage(path + "player/booster_shadow");
		booster_shadow2 = newImage(path + "player/booster2_shadow");
		booster_shadow3 = newImage(path + "player/booster3_shadow");
		playerL1 = newImage(path + "player/player_L1");
		playerL2 = newImage(path + "player/player_L2");
		playerR1 = newImage(path + "player/player_R1");
		playerR2 = newImage(path + "player/player_R2");
		booster_smaller = newImage(path + "player/booster_smaller");
		booster_large = newImage(path + "player/booster_large");
		booster_large2 = newImage(path + "player/booster_large2");
		booster_large3 = newImage(path + "player/booster_large3");
		booster = newImage(path + "player/booster");
		booster2 = newImage(path + "player/booster2");
		booster3 = newImage(path + "player/booster3");
		explosion = newImage(path + "explosion/explosion");
		explosion2 = newImage(path + "explosion/explosion2");
		explosion3 = newImage(path + "explosion/explosion3");
		explosion4 = newImage(path + "explosion/explosion4");
		explosion5 = newImage(path + "explosion/explosion5");
		explosion6 = newImage(path + "explosion/explosion6");
		explosion7 = newImage(path + "explosion/explosion7");
		explosion8 = newImage(path + "explosion/explosion8");
		explosion9 = newImage(path + "explosion/explosion9");
		explosion10 = newImage(path + "explosion/explosion10");
		explosion11 = newImage(path + "explosion/explosion11");
		explosion12 = newImage(path + "explosion/explosion12");
		explosion13 = newImage(path + "explosion/explosion13");
		explosion14 = newImage(path + "explosion/explosion14");
		explosion15 = newImage(path + "explosion/explosion15");
		explosion16 = newImage(path + "explosion/explosion16");
		explosion17 = newImage(path + "explosion/explosion17");
		turretBase = newImage(path + "turret/turret_base");
		turret = newImage(path + "turret/turret");
		turret2 = newImage(path + "turret/turret2");
		turret3 = newImage(path + "turret/turret3");		

		// ITEMS
		path = Game.ITEMS_PATH;
		firePower = newImage(path + "firepower");
		firePower2 = newImage(path + "firepower2");
		firePower3 = newImage(path + "firepower3");
		repair = newImage(path + "repair");
		repair2 = newImage(path + "repair2");
		repair3 = newImage(path + "repair3");
		shield = newImage(path + "shield");
		shield2 = newImage(path + "shield2");
		shield3 = newImage(path + "shield3");

		// MENU AND PANEL
		path = Game.PANEL_PATH;
		panel = newImage(path + "panel");
		barBackground = newImage(path + "bar_bgd");
		barOverlay = newImage(path + "bar_overlay");
		menu = newImage(path + "menu");
		pointer = newImage(path + "pointer");
		score = newImage(path + "score");
		lives = newImage(path + "lives");
		pointerOn = newImage(path + "on");
		pointerOff = newImage(path + "off");

		// NUMBERS
		path = Game.NUMBERS_PATH;
		num0 = newImage(path + "0");
		num1 = newImage(path + "1");
		num2 = newImage(path + "2");
		num3 = newImage(path + "3");
		num4 = newImage(path + "4");
		num5 = newImage(path + "5");
		num6 = newImage(path + "6");
		num7 = newImage(path + "7");
		num8 = newImage(path + "8");
		num9 = newImage(path + "9");
		large0 = newImage(path + "large0");
		large1 = newImage(path + "large1");
		large2 = newImage(path + "large2");
		large3 = newImage(path + "large3");
		large4 = newImage(path + "large4");
		large5 = newImage(path + "large5");
		large6 = newImage(path + "large6");
		large7 = newImage(path + "large7");
		large8 = newImage(path + "large8");
		large9 = newImage(path + "large9");

		// SOUND
		path = Game.SOUND_PATH;
		// attempt to load the music file
		File musicFile = new File(path + "golden_wings.ogg");
		// if it exists, load the music
		if (musicFile.exists()) {
			gameMusic = new Music(musicFile.getPath());
		}
		explosionSound = new Sound(path + "barrel_death.ogg");
		laserSound = new Sound(path + "laser2.ogg");
		

		// Instantiate world
		world = new World(this, map);

		// Create a menu
		gameMenu = new Menu();
	}

	/**
	 * Update the game state for a frame.
	 * 
	 * @param gc
	 *            The Slick game container object.
	 * @param delta
	 *            Time passed since last frame (milliseconds).
	 */
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {

		// if resources have not loaded
		if (!loaded) {
			// load resources
			if (nextResource != null) {
				try {
					nextResource.load();
				} catch (IOException e) {
					throw new SlickException("Failed to load: "
							+ nextResource.getDescription(), e);
				}
				nextResource = null;
			}
			// load the next resource
			if (LoadingList.get().getRemainingResources() > 0) {
				nextResource = LoadingList.get().getNext();
			} else {
				// there are no more resources left
				if (!loaded) {
					// we are done loading
					loaded = true;
					// optimise game draw settings
					gc.setVSync(true);
					gc.setUpdateOnlyWhenVisible(true);
					gc.setTargetFrameRate(SCREEN_FRAME_RATE);
					// play the game music
					if (gameMusic != null) {
						gameMusic.loop();
					}
				}
			}
		} else {
			// Get data about the current input (keyboard state).
			Input input = gc.getInput();
			// Update the player's movement direction based on keyboard presses.
			double dirX = DIR_NONE;
			double dirY = DIR_NONE;
			// used to pass shoot command to player
			boolean playerShoot = false;
			// key downs for player movement
			if (input.isKeyDown(Input.KEY_DOWN)) {
				dirY = DIR_DOWN;
			}
			if (input.isKeyDown(Input.KEY_UP)) {
				dirY = DIR_UP;
			}
			if (input.isKeyDown(Input.KEY_LEFT))
				dirX = DIR_LEFT;
			if (input.isKeyDown(Input.KEY_RIGHT))
				dirX = DIR_RIGHT;
			// key presses for the menu
			if (input.isKeyPressed(Input.KEY_DOWN)
					&& (gameMenu.getState() == Menu.state.MENU_SHOW)) {
				// move the menu pointer down
				gameMenu.movePointerDown();
			}
			if (input.isKeyPressed(Input.KEY_UP)
					&& (gameMenu.getState() == Menu.state.MENU_SHOW)) {
				// move the menu pointer up
				gameMenu.movePointerUp();
			}
			// toggle full screen
			if (input.isKeyPressed(Input.KEY_F)) {
				toggleFullScreen();
			}
			// show and hide menu
			if (input.isKeyPressed(Input.KEY_ESCAPE)) {
				// if the menu is visible and we have started the game
				if (gameMenu.getState() == Menu.state.MENU_SHOW
						&& world.getIsStarted()) {
					// if the game is not over, hide the menu
					if (!isGameOver) {
						app.resume();
						gameMenu.setState(Menu.state.MENU_HIDE);
					// if it is, show the game over screen again
					} else {
						gameMenu.setState(Menu.state.MENU_GAMEOVER);
					}
				// show the menu if it is hidden or the game is over
				} else if (gameMenu.getState() == Menu.state.MENU_HIDE
						|| gameMenu.getState() == Menu.state.MENU_GAMEOVER) {
					app.pause();
					gameMenu.setState(Menu.state.MENU_SHOW);
				}
			}
			// menu enter key press to trigger an action
			if (input.isKeyPressed(Input.KEY_ENTER)
					&& gameMenu.getState() == Menu.state.MENU_SHOW) {
				gameMenu.pointerAction(this);
			}
			// toggle pause
			if (input.isKeyPressed(Input.KEY_P)) {
				togglePause();
			}
			// toggle sound in all menus except pause
			if (input.isKeyPressed(Input.KEY_S)
					&& gameMenu.getState() != Menu.state.MENU_PAUSE) {
				toggleSound();
			}
			// start a new game
			if (input.isKeyPressed(Input.KEY_N)) {
				start();
			}
			// stop the camera, only when debugging
			if (world != null && DEBUG && input.isKeyPressed(Input.KEY_C)) {
				world.getCamera().togglePause();
			}
			// noclip, only when debugging
			if (world != null && DEBUG && input.isKeyPressed(Input.KEY_G)) {
				if (world.getPlayer().getNoClip()) {
					world.getPlayer().setNoClip(false);
				} else {
					world.getPlayer().setNoClip(true);
				}
			}
			// if focus is lost, pause the game
			if (!gc.hasFocus() && gameMenu.getState() == Menu.state.MENU_HIDE) {
				pause();
			}
			// capture and send the player shoot command
			if (input.isKeyDown(Input.KEY_SPACE)
					|| input.isKeyDown(Input.KEY_LCONTROL)
					|| input.isKeyPressed(Input.KEY_RCONTROL)) {
				playerShoot = true;
			} else {
				playerShoot = false;
			}
			// update the world if not paused
			if (world != null && !app.isPaused()) {
				world.update(dirX, dirY, delta, playerShoot);
			}
		}

	}

	/**
	 * If the world has started for the first time, then this will restart the
	 * world. If the world has not yet started, it will start it.
	 * 
	 * @throws SlickException
	 */
	public void start() throws SlickException {
		if (world.getIsStarted()) {
			world = null;
			world = new World(this, map);
			world.setIsStarted(true);
		} else {
			world.setIsStarted(true);
		}
		gameMenu.setState(Menu.state.MENU_HIDE);
		isGameOver = false;
		app.resume();
	}

	/**
	 * Returns a PNG image given the filename
	 * 
	 * @param fileName
	 * @return A Slick Image
	 * @throws SlickException
	 */
	public Image newImage(String fileName) throws SlickException {
		return new Image(fileName + ".png");
	}

	/**
	 * Render the entire screen, so it reflects the current game state.
	 * 
	 * @param gc
	 *            The Slick game container object.
	 * @param g
	 *            The Slick graphics object, used for drawing.
	 */
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {

		// if we have not yet loaded
		if (!loaded) {
			// draw the loading bar
			int total = LoadingList.get().getTotalResources();
			int loaded = LoadingList.get().getTotalResources()
					- LoadingList.get().getRemainingResources();
			int width = playwidth() / 2;
			int height = 20;
			float bar = loaded / (float) total;
			g.fillRect(playwidth() / 4, (playheight() - height) / 2, bar
					* width, height);
			g.drawRect(playwidth() / 4, (playheight() - height) / 2, width,
					height);
		} else {
			// once loaded, render the world
			if (world != null) {
				world.render(g);
			}
			// render the menu
			gameMenu.render(this);
		}

	}

	/** Width of the play area, in pixels. */
	public static int playwidth() {
		return screenwidth;
	}

	/** Height of the play area, in pixels. */
	public static int playheight() {
		return screenheight;
	}

	/**
	 * Toggles the full-screen on and off
	 * @throws SlickException
	 */
	public void toggleFullScreen() throws SlickException {
		if (isFullScreen) {
			isFullScreen = false;
		} else {
			isFullScreen = true;
		}
		pause();
		app.setDisplayMode(screenwidth, screenheight, isFullScreen);
	}
	/** Toggles pause */
	public void togglePause() {
		if (!app.isPaused()) {
			pause();
		} else {
			resume();
		}
	}
	
	/** Pauses the game */
	public void pause() {
		// if menu is hidden, pause the game
		if (gameMenu.getState() == Menu.state.MENU_HIDE) {
			gameMenu.setState(Menu.state.MENU_PAUSE);
			app.pause();
			// disable sounds
			setIsSoundEnabled(false);
		} else {
			System.err.printf("Can't pause in %s menu state %n", gameMenu
					.getState().toString());
		}
	}

	/** Resumes the game */
	public void resume() {
		// if pause menu is visible
		if (gameMenu.getState() == Menu.state.MENU_PAUSE) {
			// hide the pause menu, resume the game
			gameMenu.setState(Menu.state.MENU_HIDE);
			app.resume();
			// revert to last sound setting
			setIsSoundEnabled(isLastSoundEnabled);
		} else {
			System.err.printf("Can't resume in %s menu state %n", gameMenu
					.getState().toString());
		}
	}
	
	/** Sets whether sound is enabled */
	public void setIsSoundEnabled(boolean isSoundEnabled) {
		// remember the last sound setting
		isLastSoundEnabled = getIsSoundEnabled();
		// change the sound setting
		this.isSoundEnabled = isSoundEnabled;
		if (isSoundEnabled && gameMusic != null) {
			gameMusic.resume();
		} else if (!isSoundEnabled && gameMusic != null) {
			gameMusic.pause();
		}
	}
	
	/** Gets whether sound is enabled */
	public boolean getIsSoundEnabled() {
		return isSoundEnabled;
	}
	
	/** Toggles sound */
	public void toggleSound() {
		if (isSoundEnabled) {
			setIsSoundEnabled(false);
		} else {
			setIsSoundEnabled(true);
		}
	}
	
	/** Plays sound resource within Game from any class
	 * @param pitch 
	 * @param volume 
	 * */
	public void playSound(Sound sound, float pitch, float volume) {
		if (isSoundEnabled) {
			sound.play(pitch, volume);
		}
	}

	/**
	 * Start-up method. Creates the game and runs it.
	 * 
	 * @param args
	 *            Command-line arguments (ignored).
	 */
	public static void main(String[] args) throws SlickException {

		app = new AppGameContainer(new Game());
		// show FPS if debugging
		if (Game.DEBUG) {
			app.setShowFPS(true);
		} else {
			app.setShowFPS(false);
		}
		app.setDisplayMode(screenwidth, screenheight, false);
		app.start();
	}

	/** Ends the game */
	public void gameOver() {
		isGameOver = true;
		app.pause();
		gameMenu.setState(Menu.state.MENU_GAMEOVER);
	}
	
	/** Sets full-screen */
	public void setFullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;
	}

	/** Gets full-screen */
	public boolean getIsFullScreen() {
		return isFullScreen;
	}

	/** Checks whether the game is paused */
	public boolean getIsPaused() {
		return app.isPaused();
	}
	
	/** Checks whether the game is paused */
	public boolean getIsGameOver() {
		return isGameOver;
	}

	/** Returns the current world */
	public World getWorld() {
		return world;
	}
	
}
