/* SWEN20003 Object Oriented Software Development
 * GameGraphic Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.Animation;

/**
 * A hybrid class that allows either an image or an animation to be defined for
 * a GameObject. This allows us to attach either an image or an animation to any
 * object in the game, and also to iterate over a common class when rendering.
 */
public class GameGraphic {
	/** The index of the first frame in an animation */
	public static final int FIRST_FRAME = 0;
	/** The X coordinate */
	private double x = 0;
	/** The Y coordinate */
	private double y = 0;
	/** The offset in X from the parent GameObject */
	private double offSetX = 0;
	/** The offset in Y from the parent GameObject */
	private double offSetY = 0;
	/** Custom set width, initially undefined */
	private double width = Game.UNDEFINED;
	/** Custom set height, initially undefined */
	private double height = Game.UNDEFINED;
	/** Visibility of GameGraphic */
	private boolean isVisible = true;
	/** For animations, whether it is paused or not */
	private boolean isPaused = false;
	/** Used to remember the state of an animation when Game is paused */
	private boolean isFirstTimePaused = true;
	/** The angle to draw the GameGraphic */
	private Angle angle;

	// animation and image are declared as null, only one is used, depending on
	// whether we are using a static image or a moving animation
	private Animation animation;
	private Image image;

	/**
	 * Creates a standard animation with variable frame durations
	 * @param offSetX
	 * @param offSetY
	 * @param image
	 * @param duration
	 * @param autoUpdate
	 * @param setLooping
	 * @param setPingPong
	 */
	public GameGraphic(double offSetX, double offSetY, Image[] image,
			int[] duration, boolean autoUpdate, boolean setLooping,
			boolean setPingPong) {
		animation = new Animation(image, duration, autoUpdate);
		this.offSetX = offSetX;
		this.offSetY = offSetY;
		animation.setLooping(setLooping);
		animation.setPingPong(setPingPong);
	}

	/**
	 * Creates a standard animation with static frame durations
	 * @param offSetX
	 * @param offSetY
	 * @param image
	 * @param duration
	 * @param autoUpdate
	 * @param setLooping
	 * @param setPingPong
	 */
	public GameGraphic(double offSetX, double offSetY, Image[] image,
			int duration, boolean autoUpdate, boolean setLooping,
			boolean setPingPong) {
		animation = new Animation(image, duration, autoUpdate);
		this.offSetX = offSetX;
		this.offSetY = offSetY;
		animation.setLooping(setLooping);
		animation.setPingPong(setPingPong);
	}

	/**
	 * Creates a static image
	 * @param offSetX
	 * @param offSetY
	 * @param image
	 */
	public GameGraphic(double offSetX, double offSetY, Image image) {
		this.image = image;
		this.offSetX = offSetX;
		this.offSetY = offSetY;
	}

	/**
	 * Creates a static image with custom width and height
	 * @param offSetX
	 * @param offSetY
	 * @param width
	 * @param height
	 * @param image
	 */
	public GameGraphic(double offSetX, double offSetY, double width,
			double height, Image image) {
		this.image = image;
		this.offSetX = offSetX;
		this.offSetY = offSetY;
		if (width < 0) {
			this.width = 0;
		} else {
			this.width = width;
		}
		if (height < 0) {
			this.height = 0;
		} else {
			this.height = height;
		}
	}

	/** Checks if GameGraphic is visible */
	public boolean getIsVisible() {
		return isVisible;
	}

	/** Sets GameGraphic visibility */
	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/** Shows and plays animation */
	public void turnOn() {
		this.setIsVisible(true);
		if (animation != null) {
			animation.start();
		}
	}

	/** Hides and stops animation */
	public void turnOff() {
		this.setIsVisible(false);
		if (animation != null) {
			animation.stop();
		}
	}

	/** Gets GameGraphic height */
	public double getHeight() {
		// if custom height is undefined
		if (height == Game.UNDEFINED) {
			// and image is defined
			if (image != null) {
				// return the image height
				return image.getHeight();
			} else {
				// otherwise use the animation height
				return animation.getHeight();
			}
		} else {
			// return custom height if it is explicitly defined
			return height;
		}
	}

	/** Gets GameGraphic width */
	public double getWidth() {
		// if custom width is undefined
		if (width == Game.UNDEFINED) {
			// and image is defined
			if (image != null) {
				// return the image width
				return image.getWidth();
			} else {
				// otherwise use the animation width
				return animation.getWidth();
			}
		} else {
			// return custom width if it is explicitly defined
			return width;
		}
	}

	/**
	 * Draws the GameGraphic.
	 * @param game
	 * @param camera
	 * @param parent
	 */
	public void drawCentered(Game game, Camera camera, GameObject parent) {
		// the instant the game is paused, remember the state of the animation
		if (game.getIsPaused() && animation != null
				&& isFirstTimePaused == true) {
			isPaused = animation.isStopped();
			animation.stop();
			isFirstTimePaused = false;
		}
		// when we are no longer paused, return to the state of the animation
		if (!game.getIsPaused() && animation != null
				&& isFirstTimePaused == false) {
			isFirstTimePaused = true;
			if (!isPaused) {
				animation.start();
			} else {
				animation.stop();
			}
		}

		// calculate the screen X Y position
		double screenX, screenY;
		screenX = this.x - camera.getLeft();
		screenY = this.y - camera.getTop();
		// if dealing with an animation
		if (image == null) {
			animation.draw((float) (screenX - getWidth() / 2),
					(float) (screenY - getHeight() / 2));
		// if dealing with an image
		} else {
			// default width and height if height and width are left undefined
			if (height == Game.UNDEFINED && width == Game.UNDEFINED) {
				image.drawCentered((float) screenX, (float) screenY);
			// custom width and height
			} else {
				image.draw((float) (screenX - this.width / 2),
						(float) (screenY - (float) this.height / 2),
						(float) width, (float) height);
			}
		}
	}

	/** Returns the attached image. Used to directly access the image. */
	public Image getImage() {
		return image;
	}

	/** Returns the attached image. Used to directly access the image. */
	public Animation getAnimation() {
		return animation;
	}

	/** Checks if the animation is in the last frame */
	public boolean getIsLastFrame() {
		if (animation != null) {
			return animation.getFrame() == animation.getFrameCount() - 1;
		}
		return false;
	}

	/** Sets up animation to be used when assigning animations to movement of
	 * a GameObject */
	public void setUpAnim() {
		if (animation != null) {
			// set invisible, turn on looping and stop
			setIsVisible(false);
			animation.setLooping(true);
			animation.stop();
		}
	}
	
	/**
	 * Moves the animation with the parent, based on the offset X and Y.
	 * @param camera
	 * @param parent
	 */
	public void move(Camera camera, GameObject parent) {
		// defined parent to follow
		if (parent != null) {
			this.x = parent.getX() + offSetX;
			this.y = parent.getY() + offSetY;
		}
	}

	/**
	 * Sets the angle of the GameGraphic
	 * @param angle
	 */
	public void setAngle(Angle angle) {
		this.angle = angle;
		// if dealing with an image
		if (image != null) {
			image.setRotation((float) angle.getDegrees());
		} else {
			// if dealing with an animation and the rotation has changed
			if (angle.getDegrees() != animation.getCurrentFrame().getRotation()) {
				// change the rotation of all the images in the animation
				for (int j = 0; j < animation.getFrameCount(); j++) {
					animation.getImage(j).setRotation(
							(float) angle.getDegrees());
				}
			}
		}
	}

	/** Gets the angle of the GameGraphic */
	public Angle getAngle() {
		return angle;
	}

}
