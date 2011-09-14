import org.newdawn.slick.SlickException;

/* SWEN20003 Object Oriented Software Development
 * Menu Class
 * Author: Aram Kocharyan <aramk>
 */

public class Menu {
	/** The different states of the Menu */
	public enum state {
		MENU_PAUSE, MENU_GAMEOVER, MENU_SHOW, MENU_HIDE
	}
	/** The screen Y coordinate of the first Menu button */
	private static final int POINTER_Y = 160;
	/** The height of a Menu button */
	private static final int POINTER_HEIGHT = 50;
	/** The index of the first button */
	private static final int MIN_POINTER = 0;
	/** The index of the last button */
	private static final int MAX_POINTER = 3;
	/** The New Game button index */
	private static final int NEW_GAME = 0;
	/** The Full Screen button index */
	private static final int FULLSCREEN = 1;
	/** The Sound button index */
	private static final int SOUND = 2;
	/** The Exit button index */
	private static final int EXIT = 3;
	/** The margin from the screen Y centre that "Game Over" is drawn */
	private static final int GAME_OVER_MARGIN = 40;

	/** The current state of the Menu */
	private state currState;
	/** The score to print at game over */
	private NumberLarge score;
	/** The initially selected button */
	private int pointerPos = NEW_GAME;

	/**
	 * Creates a Menu
	 * @throws SlickException
	 */
	public Menu() throws SlickException {
		// initial state is menu shown
		currState = state.MENU_SHOW;
		// create a new number for the score
		score = new NumberLarge(Game.playwidth() / 2, Game.playheight() / 2);
	}

	/**
	 * Renders the Menu based on the current state
	 * @param game
	 * @throws SlickException
	 */
	public void render(Game game) throws SlickException {
		switch (currState) {
		// render the menu if shown
		case MENU_SHOW:
			Game.menu.draw(Game.SCREEN_LEFT_X, Game.SCREEN_TOP_Y);
			// Draw the button pointer on the currently selected button
			Game.pointer.draw(Game.SCREEN_LEFT_X,
					POINTER_Y + pointerPos * POINTER_HEIGHT);
			// if full screen is selected
			if (pointerPos == FULLSCREEN) {
				// draw the on/off switch
				if (game.getIsFullScreen()) {
					Game.pointerOn.draw(Game.SCREEN_LEFT_X,
							POINTER_Y + pointerPos * POINTER_HEIGHT);
				} else {
					Game.pointerOff.draw(Game.SCREEN_LEFT_X,
							POINTER_Y + pointerPos * POINTER_HEIGHT);
				}
			}
			// if sound is selected
			if (pointerPos == SOUND) {
				// draw the on/off switch
				if (game.getIsSoundEnabled()) {
					Game.pointerOn.draw(Game.SCREEN_LEFT_X,
							POINTER_Y + pointerPos * POINTER_HEIGHT);
				} else {
					Game.pointerOff.draw(Game.SCREEN_LEFT_X,
							POINTER_Y + pointerPos * POINTER_HEIGHT);
				}
			}
			break;
		// render the pause screen
		case MENU_PAUSE:
			Game.darkOverlay.draw(0, 0, Game.playwidth(), Game.playheight());
			Game.paused.drawCentered(Game.playwidth() / 2,
					Game.playheight() / 2);
			break;
		// render the game over screen
		case MENU_GAMEOVER:
			Game.darkOverlay.draw(0, 0, Game.playwidth(), Game.playheight());
			Game.gameOver.drawCentered(Game.playwidth() / 2, Game.playheight()
					/ 2 - GAME_OVER_MARGIN);
			// render the score
			score.render(game.getWorld().getPlayer().getTotalScore(), true);
			break;
		}
	}

	/** Sets the current state */
	public void setState(state currState) {
		this.currState = currState;
	}

	/** Gets the current state */
	public state getState() {
		return currState;
	}

	/** Moves the button pointer up */
	public void movePointerUp() {
		if (pointerPos > MIN_POINTER) {
			pointerPos--;
		}
	}

	/** Moves the button pointer down */
	public void movePointerDown() {
		if (pointerPos < MAX_POINTER) {
			pointerPos++;
		}
	}

	/**
	 * Performs the action attributed to a button
	 * @param game
	 * @throws SlickException
	 */
	public void pointerAction(Game game) throws SlickException {
		switch (pointerPos) {
		// start a new game
		case NEW_GAME:
			game.start();
			break;
		// TODO
		case SOUND:
			game.toggleSound();
			break;
		// toggle full screen
		case FULLSCREEN:
			game.toggleFullScreen();
			break;
		// exit the game
		case EXIT:
			System.exit(0);
			break;
		}
	}
}
