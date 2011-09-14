/* SWEN20003 Object Oriented Software Development
 * Player Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Player class that contains methods for controlling the movement and rendering
 * the player
 */
public class Player extends Unit {

	// define the position of the player's panelBars
	// TODO sort this out
	/** Definition of one live remaining */
	public static final int ONE_LIFE = 1;
	/** Shield bar X coordinate */
	private static final int SHIELD_X = 72;
	/** FirePower bar X coordinate */
	private static final int FIREPOWER_X = 476;
	/** Distance between Panel and bar */
	private static final int BAR_MARGIN_Y = Math.abs(Panel.PANEL_HEIGHT
			- PanelBar.BAR_FILL_HEIGHT) / 2;
	/** Bar Y coordinate */
	private static final int BAR_Y = Game.playheight() - PanelBar.BAR_HEIGHT
			- BAR_MARGIN_Y;
	/** Score X coordinate */
	private static final int SCORE_X = 0;
	/** Score and Lives Y coordinate */
	private static final int NUMBER_Y = Game.playheight()
			- Panel.PANEL_HEIGHT - Game.large0.getHeight()
			- Game.score.getHeight();
	/** Total Score of Player */
	private int totalScore = 0;
	/** Lives of Player */
	private int lives = 5;
	/** Number for score */
	private NumberLarge scoreNumber;
	/** Number for lives */
	private NumberLarge livesNumber;

	/**
	 * Creates a new Player
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public Player(double x, double y, World world) throws SlickException {
		setX(x);
		setY(y);
		setMaxSpeed(0.4);
		setFullShield(100);
		setShield(getFullShield());
		setDamage(10);
		setFirePower(0);
		
		int[] duration = { 40, 60, 80 };

		// ship shadow
		double tempHeight = Game.player.getHeight() * Game.HITBOX_PRECISION;
		addGraphic(new GameGraphic(0, tempHeight / 2, Game.player_shadow));
		// booster shadow
		Image[] boosterShadowImage = { Game.booster_shadow,
				Game.booster_shadow2, Game.booster_shadow3 };
		GameGraphic boosterShadow = new GameGraphic(0, 47 + tempHeight / 2,
				boosterShadowImage, 50, true, true, true);
		addGraphic(boosterShadow);
		// ship left
		Image[] left = { Game.player, Game.playerL1, Game.playerL2 };
		setAnimThis(world, Game.animDirection.ANIM_LEFT, new GameGraphic(0, 0,
				left, duration, true, false, true));
		// ship right
		Image[] right = { Game.player, Game.playerR1, Game.playerR2 };
		setAnimThis(world, Game.animDirection.ANIM_RIGHT, new GameGraphic(0, 0,
				right, duration, true, false, true));
		// ship normal
		setGraphic(new GameGraphic(0, 0, Game.player));
		// booster large
		Image[] up = { Game.booster_large, Game.booster_large2,
				Game.booster_large3 };
		setAnimThis(world, Game.animDirection.ANIM_UP, new GameGraphic(0, 50,
				up, duration, true, false, true));
		// booster smaller
		Image[] down = { Game.booster3, Game.booster_smaller, Game.booster3 };
		setAnimThis(world, Game.animDirection.ANIM_DOWN, new GameGraphic(0, 47,
				down, duration, true, false, true));
		// booster normal
		Image[] booster = { Game.booster, Game.booster2, Game.booster3 };
		setAnimThis(world, Game.animDirection.ANIM_NONE, new GameGraphic(0, 47,
				booster, duration, true, false, true));
		
		// create a shield bar
		PanelBar shieldBar = new ShieldBar(this, SHIELD_X, BAR_Y);
		world.getPanel().addBar(shieldBar);
		// create a firePower bar
		PanelBar firePowerBar = new FirePowerBar(this, FIREPOWER_X, BAR_Y);
		world.getPanel().addBar(firePowerBar);
		// create the score board
		scoreNumber = new NumberLarge(SCORE_X, NUMBER_Y);
		livesNumber = new NumberLarge(Game.playwidth(), NUMBER_Y);
	}

	@Override
	public void AI(int delta, World world, Camera camera) throws SlickException {
		// once we reach the top of the map, if the boss has been destroyed or
		// doesn't exist
		if (camera.getTop() == 0
				&& (world.getBoss() == null || world.getBoss().getIsDestroyed())) {
			// if we are not on the screen (from moving off), declare the game
			// over
			if (!getIsOnScreen(camera)) {
				world.getGame().gameOver();
				// otherwise, move us off the screen
			} else {
				setNoClip(true);
				setDisableMove(true);
				setMoveX(Game.DIR_NONE);
				setMoveY(Game.DIR_UP);
				accelerate(delta);
				setStepX(delta);
				setStepY(camera, delta);
				move(delta, world, camera);
			}
			// if we haven't reached the top of the map and defeated the boss,
			// resort to regular movement
		} else {
			// if player is below the map from being pushed, load the checkpoint
			if (!getNoClip() && getTop() >= camera.getBottom()) {
				world.goToCheckPoint();
			}
			// just cools down automatically, shooting is handled through input
			coolDown(delta);
			move(delta, world, camera);
		}
	}

	@Override
	public Missile createMissile() throws SlickException {
		return new MissilePlayer(this, new Angle(Angle.UP));
	}

	/** Set the total score */
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	/** Add to the total score */
	public void addTotalScore(int score) {
		this.totalScore += score;
	}

	/** Get the total score */
	public int getTotalScore() {
		return totalScore;
	}

	/** Render the scores and lives numbers */
	public void renderScoreAndLives() throws SlickException {
		scoreNumber.render(getTotalScore(), false);
		// adjust the lives to be right aligned
		livesNumber.setX(Game.playwidth() - livesNumber.getTotalWidth());
		livesNumber.render(getLives(), false);
		Game.score.draw(SCORE_X, NUMBER_Y + Game.score.getHeight());
		Game.lives.draw(Game.playwidth() - Game.lives.getWidth(), NUMBER_Y
				+ Game.lives.getHeight());
	}

	/** Set the number of lives */
	public void setLives(int lives) {
		if (lives >= 0) {
			this.lives = lives;
		} else {
			this.lives = 0;
		}
	}

	/** Get the number of lives */
	public int getLives() {
		return lives;
	}
}
