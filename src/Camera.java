/* SWEN20003 Object Oriented Software Development
 * Camera Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.SlickException;

/**
 * Camera class for storing X Y coordinates and speed for the camera, as well as
 * moving the camera.
 */
public class Camera {

	/** The speed of the camera when paused */
	private static final int NO_SPEED = 0;
	/** The speed of the camera */
	private double speed;
	/** The last non-zero speed of the camera */
	private double lastSpeed;
	/** The step movement of the camera in the Y direction this update round */
	private double stepCamera;
	/** The left x coordinate of the camera (pixels). */
	private double left;
	/** The top y coordinate of the camera (pixels). */
	private double top;
	/** Whether the camera is paused */
	private boolean isPaused = false;

	/**
	 * Creates a new camera for the world based on the player's coordinates.
	 * @param world
	 * @param player
	 */
	public Camera(World world, Player player) {
		// initial speed
		speed = 0.25;
		lastSpeed = speed;
		// initial left
		left = world.getMapScreenDiff() / 2;
		// set up top based on player Y
		setTop(world, player);
	}

	/** The left x coordinate of the camera (pixels). */
	public double getLeft() {
		return left;
	}

	/** The right x coordinate of the camera (pixels). */
	public double getRight() {
		return left + Game.playwidth();
	}

	/** The top y coordinate of the camera (pixels). */
	public double getTop() {
		return top;
	}

	/** Set top based on player Y coordinate */
	public void setTop(World world, Player player) {
		top = player.getY()
			- (Game.playheight() - world.getTileHeight() - Panel.PANEL_HEIGHT);
	}

	/** The bottom y coordinate of the camera (pixels). */
	public double getBottom() {
		return top + Game.playheight() - Panel.PANEL_HEIGHT;
	}

	/** Gets Camera Speed */
	public double getSpeed() {
		return speed;
	}

	/** Sets Camera Speed */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/** Sets the camera movement step this update round in the Y direction */
	public void setStep(double delta) {
		stepCamera = speed * delta;
	}

	/** Gets the camera movement step this update round in the Y direction */
	public double getStep() {
		return stepCamera;
	}

	/** Pauses the camera movement */
	public void pause() {
		isPaused = true;
		lastSpeed = speed;
		setSpeed(NO_SPEED);
	}

	/** Resumes the camera movement */
	public void resume() {
		isPaused = false;
		setSpeed(lastSpeed);
	}

	/** Toggles the camera movement on and off */
	public void togglePause() {
		if (isPaused) {
			resume();
		} else {
			pause();
		}
	}

	/**
	 * Moves the camera and pauses the map when it has a Y less than 0
	 * @throws SlickException
	 */
	public void move(World world, int delta) throws SlickException {
		// set the step for this update round
		setStep(delta);
		// set the left based on the player's offset from the screen centre
		// this allows for horizontal scrolling of the map
		left = world.getPlayerMapOffset() + world.getMapScreenDiff() / 2;
		// if we are about to reach a Y of 0
		if (top - stepCamera < World.MAP_TOP_Y) {
			// pauses camera and sets the top to 0
			pause();
			top = World.MAP_TOP_Y;
		// otherwise move the camera up
		} else {
			top -= stepCamera;
		}
	}
}
