/* SWEN20003 Object Oriented Software Development
 * GameObject Class
 * Author: Aram Kocharyan <aramk>
 */

import java.util.ArrayList;
import org.newdawn.slick.SlickException;

/**
 * GameObject superclass that controls basic movement of Game objects 
 */
public abstract class GameObject {
	/** X coordinate WRT the map */
	private double x = 0;
	/** Y coordinate WRT the map */
	private double y = 0;
	/** Maximum possible speed */
	private double maxSpeed = 0;
	/** Rate of acceleration */
	private double accel = 0.002;
	/** Rate of deceleration */
	private double decel = 0.001;
	/** Velocity in X direction */
	private double velocityX = 0;
	/** Velocity in Y direction */
	private double velocityY = 0;
	/** Movement input in X direction */
	private double moveX = 0;
	/** Movement input in Y direction */
	private double moveY = 0;
	/** Real movement in X direction */
	private double realMoveX = 0;
	/** Real movement in Y direction */
	private double realMoveY = 0;
	/** Movement step in X direction during update */
	private double stepX = 0;
	/** Movement step in Y direction during update */
	private double stepY = 0;
	/** Acceleration time used in acceleration function for X direction */
	private double accelTimeX;
	/** Acceleration time used in acceleration function for Y direction */
	private double accelTimeY;
	/** Custom set width */
	private double customWidth = Game.UNDEFINED;
	/** Custom set height */
	private double customHeight = Game.UNDEFINED;
	/** Whether is destroyed */
	private boolean isDestroyed = false;
	/** Whether to ignore blocks and collisions */
	private boolean noClip = false;
	/** Whether to disable automatic step movement and acceleration */
	private boolean disableMove = false;
	/** Whether is strafing left */
	private boolean isStrafingLeft = false;
	/** Whether is strafing right */
	private boolean isStrafingRight = false;
	/** Whether to destroy when hitting a block */
	private boolean destroyOnBlock = false;
	/** Whether to disable effects of acceleration */
	private boolean disableVelocity = false;
	// the object graphic and movement animations
	private GameGraphic graphic, animUp, animDown, animLeft, animRight,
			animNone;

	/** Attached GameGraphics to the object */
	private ArrayList<GameGraphic> graphicList = new ArrayList<GameGraphic>();
	/** GameGraphics that need to be removed from the object */
	private ArrayList<GameGraphic> graphicRemoveList = new ArrayList<GameGraphic>();

	/**
	 * The key movement function that controls all movement for GameObjects
	 * @param delta
	 * 				The time since last frame in milliseconds
	 * @param world
	 * 				The world that the object resides in 
	 * @param camera
	 * 				The camera belonging to the world
	 * @throws SlickException
	 */
	public void move(int delta, World world, Camera camera)
			throws SlickException {
		// if we are overriding movement through the AI(), disable auto movement
		if (!disableMove) {
			// accelerate or decelerate based on input
			accelerate(delta);
			// set the step size for X Y
			setStepX(delta);
			setStepY(camera, delta);
		}

		// check object collision
		world.checkObjectCollsion(this);
		// animate any movements
		animateMove();

		// set checkArray as a blank array
		double[] checkArray = { World.NO_BLOCK_AHEAD, World.NO_BLOCK_AHEAD };
		// if true, blockX and blockY determine whether we should stop the
		// player from moving in that direction
		boolean blockX = false, blockY = false;
		// whether we are being pushed down by a block
		boolean blockPush = false;

		// BEGIN CODE FOR THE BLOCK PUSHING THE OBJECT
		if (!noClip) {
			// check tile in front
			checkArray = world.checkTile(getLeft(), getTop(), getRight(),
					Game.DIR_NONE, Game.DIR_UP);
			// if it is blocked
			if (checkArray[World.BLOCK_CHECK] == World.BLOCK_AHEAD) {
				// if we are expecting a block off the screen, make sure we
				// don't act on it until it is on screen, prevents us from
				// jumping off the map for a split second
				if (realMoveY == Game.DIR_UP
						&& checkArray[World.BLOCK_POSITION] < camera.getTop()
								- camera.getStep()) {
					checkArray[World.BLOCK_POSITION] = camera.getTop()
							- camera.getStep();
				}

				// if we are going to be pushed down by a block when the camera
				// moves down and we are moving down, move us down beforehand
				if ((getIsPlayer() && realMoveY == Game.DIR_DOWN && getTop()
						+ (-camera.getStep()) < checkArray[World.BLOCK_POSITION])) {
					// due to acceleration, if we are being pushed by a block
					// and press down, we want to ensure
					// that we do not accelerate through the block, since our
					// initial velocity of 0 is less than -camera.getStep() and
					// thus our stepY is negative for the first few movements
					if (stepY < 0) {
						stepY = Math.abs(stepY);
					}
					// if we are below the bottom of the screen, let the block
					// push us off the screen
					if (getBottom() >= camera.getBottom()) {
						y = checkArray[World.BLOCK_POSITION] + getHeight() / 2;
						blockPush = true;
					}
				}

				// if we are not moving down (up or none) and we are going to
				// move into a block, let it push us down
				if (realMoveY != Game.DIR_DOWN
						&& getTop() + stepY < checkArray[World.BLOCK_POSITION]) {
					y = checkArray[World.BLOCK_POSITION] + getHeight() / 2;
					blockPush = true;
					// if we need to destroy the object when it hits a block,
					// do so
					if (destroyOnBlock) {
						world.removeObject(this);
						return;
					}
				}
			}
		}
		// END CODE FOR THE BLOCK PUSHING THE OBJECT

		// BEGIN CODE FOR BLOCKING SCREEN EDGES
		if (getIsPlayer() && !noClip) {
			// block left screen edge
			if (realMoveX == Game.DIR_LEFT
					&& getLeft() + stepX < camera.getLeft()) {
				x = camera.getLeft() + getWidth() / 2;
				blockX = true;
			}
			// block right screen edge
			if (realMoveX == Game.DIR_RIGHT
					&& getRight() + stepX > camera.getRight()) {
				x = camera.getRight() - getWidth() / 2;
				blockX = true;
			}
			// block top screen edge, given we are not being pushed by a block
			// already
			if (realMoveY == Game.DIR_UP && !blockPush
					&& getTop() + stepY < camera.getTop() - camera.getStep()) {
				y = camera.getTop() - camera.getStep() + getHeight() / 2;
				blockY = true;
			}
			// block bottom screen edge, given we are not being pushed by a block
			// off the screen
			if (realMoveY == Game.DIR_DOWN
					&& !blockPush
					&& getBottom() <= camera.getBottom()
					&& getBottom() + stepY > camera.getBottom()
							- camera.getStep()) {
				blockY = true;
				y = camera.getBottom() - camera.getStep() - getHeight() / 2;
			}
		}
		// END CODE FOR BLOCKING SCREEN EDGES

		// BEGIN CODE FOR OBJECT MOVEMENT
		// if we are moving left or right
		if (realMoveX != Game.DIR_NONE && !blockX && !noClip) {
			// check the tile in that direction
			if (realMoveX == Game.DIR_LEFT) {
				checkArray = world.checkTile(getLeft(), getTop(), getBottom(),
						realMoveX, Game.DIR_NONE);
			} else if (realMoveX == Game.DIR_RIGHT) {
				checkArray = world.checkTile(getRight(), getTop(), getBottom(),
						realMoveX, Game.DIR_NONE);
			}
			// if it is blocked and we are going to run into it, make sure we
			// only reach the edge
			if (checkArray[World.BLOCK_CHECK] == World.BLOCK_AHEAD) {
				if (realMoveX == Game.DIR_LEFT
						&& getLeft() + stepX < checkArray[World.BLOCK_POSITION]
						|| realMoveX == Game.DIR_RIGHT
						&& getRight() + stepX > checkArray[World.BLOCK_POSITION]) {
					x = checkArray[World.BLOCK_POSITION] - realMoveX
							* getWidth() / 2;
					// set blockX to true to prevent us from moving in the
					// direction of the blocked tile
					blockX = true;
					// if we need to destroy the object when it hits a block
					if (destroyOnBlock) {
						world.removeObject(this);
						return;
					}
				}
			}
		}

		// if we are allowed to move either left or right, do so
		// NOTE: we must check in X direction and then make the move if allowed,
		// before we can check Y and move in that direction. The reason being
		// that if we don't we risk moving in both X and Y directions diagonally
		// through a block
		if (!blockX || noClip) {
			x += stepX;
		}

		// if we are moving up or down and not being pushed by a block
		if (realMoveY != Game.DIR_NONE && !blockY && !blockPush && !noClip) {
			// check the tile in that direction
			if (realMoveY == Game.DIR_UP) {
				checkArray = world.checkTile(getLeft(), getTop(), getRight(),
						Game.DIR_NONE, realMoveY);
			} else if (realMoveY == Game.DIR_DOWN) {
				checkArray = world.checkTile(getLeft(), getBottom(),
						getRight(), Game.DIR_NONE, realMoveY);
			}

			// if it is blocked and we are going to run into it, make sure we
			// only reach the edge
			if (checkArray[World.BLOCK_CHECK] == World.BLOCK_AHEAD) {
				if (realMoveY == Game.DIR_UP
						&& !blockPush
						&& getTop() + stepY < checkArray[World.BLOCK_POSITION]
						|| realMoveY == Game.DIR_DOWN
						&& getBottom() + stepY > checkArray[World.BLOCK_POSITION]) {
					y = checkArray[World.BLOCK_POSITION] - realMoveY
							* getHeight() / 2;
					// set blockY to true to prevent us from moving in the
					// direction of the blocked tile
					blockY = true;
					// if we need to destroy the object when it hits a block
					if (destroyOnBlock) {
						world.removeObject(this);
						return;
					}
				}
			}
		}

		// define temporary booleans to check if we are above or below the
		// screen edge
		boolean aboveBottom = getBottom() < camera.getBottom()
				- camera.getStep();
		boolean belowBottom = getBottom() > camera.getBottom();

		if (noClip
				// we are allowed to auto-move with the camera if we are above
				// the bottom of the screen and are not being blocked/pushed
				|| realMoveY == Game.DIR_NONE && !blockY && !blockPush
				&& !belowBottom
				// or if we are moving up and are not being blocked/pushed
				|| realMoveY == Game.DIR_UP && !blockY
				&& !blockPush
				// or if we are moving down and are not blocked and above the
				// bottom of the screen
				|| realMoveY == Game.DIR_DOWN && !blockY
				&& (aboveBottom && getIsPlayer() || !getIsPlayer())) {
			y += stepY;
		}
		// END CODE FOR OBJECT MOVEMENT
	}
	
	/** 
	 * Controls acceleration and deceleration based on current movement inputs
	 * @param delta
	 */
	public void accelerate(int delta) {

		// if we have disabled velocity, we can't accelerate or decelerate
		if (disableVelocity) {
			return;
		}

		// the amount of acceleration time to add, based on the rate of
		// acceleration
		double addTime = (accel * delta);
		// the amount of acceleration time to remove, based on the rate of
		// deceleration
		double removeTime = (decel * delta);

		// if moving left
		if (moveX == Game.DIR_LEFT) {
			// ensure we don't go beyond our maximum speed
			if (accelTimeX - addTime < -maxSpeed) {
				velocityX = easeOut(-maxSpeed);
			} else {
				// accelerate via the easeOut() method
				accelTimeX -= addTime;
				velocityX = easeOut(accelTimeX);
			}
		// if moving right
		} else if (moveX == Game.DIR_RIGHT) {
			// ensure we don't go beyond our maximum speed
			if (accelTimeX + addTime > maxSpeed) {
				velocityX = easeOut(maxSpeed);
			} else {
				// accelerate via the easeOut() method
				accelTimeX += addTime;
				velocityX = easeOut(accelTimeX);
			}
		// if not moving and we have some velocity in X direction
		} else if (moveX == Game.DIR_NONE && velocityX != 0) {
			// decelerate back to zero velocity, based on whether we have
			// negative accelTime or positive
			if (accelTimeX < 0) {
				if (accelTimeX + removeTime > 0) {
					accelTimeX = 0;
				} else {
					accelTimeX += removeTime;
				}
			} else if (accelTimeX > 0) {
				if (accelTimeX - removeTime < 0) {
					accelTimeX = 0;
				} else {
					accelTimeX -= removeTime;
				}
			}
			velocityX = easeOut(accelTimeX);
		}
		
		// if moving up
		if (moveY == Game.DIR_UP) {
			// ensure we don't go beyond our maximum speed
			if (accelTimeY - addTime < -maxSpeed) {
				velocityY = easeOut(-maxSpeed);
			} else {
				accelTimeY -= addTime;
				velocityY = easeOut(accelTimeY);
			}
		// if moving down
		} else if (moveY == Game.DIR_DOWN) {
			// ensure we don't go beyond our maximum speed
			if (accelTimeY + addTime > maxSpeed) {
				velocityY = easeOut(maxSpeed);
			} else {
				accelTimeY += addTime;
				velocityY = easeOut(accelTimeY);
			}
		// if not moving and we have some velocity in Y direction
		} else if (moveY == Game.DIR_NONE && velocityY != 0) {
			// decelerate back to zero velocity, based on whether we have
			// negative accelTime or positive
			if (accelTimeY < 0) {
				if (accelTimeY + removeTime > 0) {
					accelTimeY = 0;
				} else {
					accelTimeY += removeTime;
				}
			} else if (accelTimeY > 0) {
				if (accelTimeY - removeTime < 0) {
					accelTimeY = 0;
				} else {
					accelTimeY -= removeTime;
				}
			}
			velocityY = easeOut(accelTimeY);
		}

		// update the real movement directions.
		// this will ensure that we always know which direction we are moving
		// in through acceleration, which is different from movement input
		// when we are decelerating (input is DIR_NONE, but we are still in motion)
		if (accelTimeX < 0) {
			realMoveX = Game.DIR_LEFT;
		} else if (accelTimeX > 0) {
			realMoveX = Game.DIR_RIGHT;
		} else {
			realMoveX = Game.DIR_NONE;
		}
		if (accelTimeY < 0) {
			realMoveY = Game.DIR_UP;
		} else if (accelTimeY > 0) {
			realMoveY = Game.DIR_DOWN;
		} else {
			realMoveY = Game.DIR_NONE;
		}
	}

	/** 
	 * The acceleration function, which forms an easing out S shape in graph form,
	 * and defines the velocity at a given acceleration time
	 * @param x
	 * 			Any X value between negative and positive maximum speeds
	 * 			representing acceleration time
	 * @return The velocity at any given acceleration time 
	 */
	private double easeOut(double x) {
		if (Math.abs(x) > maxSpeed) {
			System.err.println("Invalid X argument for easeOut()");
		}
		return Math.sqrt(2 * maxSpeed * Math.abs(x) - Math.pow(x, 2))
				* Math.signum(x);
	}

	/** 
	 * The inverse acceleration function that defines the acceleration time
	 * required to obtain a given velocity
	 * @param x
	 * 			Any X value between negative and positive maximum speeds
	 * 			representing velocity
	 * @return The acceleration time required to obtain such a velocity 
	 */
	private double easeIn(double x) {
		if (Math.abs(x) > maxSpeed) {
			System.err.println("Invalid X argument for easeIn()");
		}
		return (maxSpeed - Math.sqrt(Math.pow(maxSpeed, 2) - Math.pow(x, 2)))
				* Math.signum(x);
	}

	/** 
	 * Assigns a GameGraphic object containing an animation to a direction of
	 * movement to animate.
	 * @param world
	 * 				The world in which the object resides in
	 * @param direction
	 * 				The direction to assign the animation movement
	 * @param animSource
	 * 				The animation to assign
	 */
	public void setAnimThis(World world, Game.animDirection direction,
			GameGraphic animSource) {
		// stop, set up looping and make invisible
		animSource.setUpAnim();
		// attach the animation to the GameObject
		addGraphic(animSource);

		// assign the animation to a GameGraphic based on direction
		switch (direction) {
		// used for standard booster
		case ANIM_NONE:
			animNone = animSource;
			break;
		// used for up booster
		case ANIM_UP:
			animUp = animSource;
			break;
		// used for down booster
		case ANIM_DOWN:
			animDown = animSource;
			break;
		// used for left strafe
		case ANIM_LEFT:
			animLeft = animSource;
			break;
		// used for right strafe
		case ANIM_RIGHT:
			animRight = animSource;
			break;
		}
	}

	/** Uses defined animations to animate movements based on movement input */
	private void animateMove() {
		// if moving left or right
		if (animLeft != null && animRight != null
				&& animLeft.getAnimation() != null
				&& animRight.getAnimation() != null) {
			// moving left
			if (moveX == Game.DIR_LEFT) {
				this.setIsVisible(false);
				animLeft.setIsVisible(true);
				animRight.setIsVisible(false);
				// strafing is an animation, so we must ensure we play and stop
				// it
				if (!isStrafingLeft) {
					animLeft.getAnimation().restart();
					animLeft.getAnimation().start();
					isStrafingLeft = true;
				}
				// on the last frame, stop the animation
				if (animLeft.getIsLastFrame()) {
					animLeft.getAnimation().stop();
				}
				// moving right
			} else if (moveX == Game.DIR_RIGHT) {
				this.setIsVisible(false);
				animLeft.setIsVisible(false);
				animRight.setIsVisible(true);
				// strafing is an animation, so we must ensure we play and stop
				// it
				if (!isStrafingRight) {
					animRight.getAnimation().restart();
					animRight.getAnimation().start();
					isStrafingRight = true;
				}
				// on the last frame, stop the animation
				if (animRight.getIsLastFrame()) {
					animRight.getAnimation().stop();
				}
				// not moving either left or right
			} else if (moveX == Game.DIR_NONE) {
				// if we were strafing and have stopped, continue animation back
				// to static graphic
				if (animLeft.getIsVisible()) {
					if (animLeft.getAnimation().isStopped()) {
						animLeft.getAnimation().start();
					}
					// once we return from strafing left, change back to static
					// graphic
					if (animLeft.getAnimation().getFrame() == 0) {
						animLeft.setIsVisible(false);
						this.setIsVisible(true);
						animLeft.getAnimation().stop();
						animLeft.getAnimation().restart();
						isStrafingLeft = false;
					}
				}
				// if we were strafing and have stopped, continue animation back
				// to static graphic
				if (animRight.getIsVisible()) {
					if (animRight.getAnimation().isStopped()) {
						animRight.getAnimation().start();
					}
					// once we return from strafing right, change back to static
					// graphic
					if (animRight.getAnimation().getFrame() == 0) {
						animRight.setIsVisible(false);
						this.setIsVisible(true);
						animRight.getAnimation().stop();
						animRight.getAnimation().restart();
						isStrafingRight = false;
					}
				}
			}
		}

		// if moving up or down, animate either up, down or standard animation
		if (animUp != null && animDown != null
				&& animUp.getAnimation() != null
				&& animUp.getAnimation() != null) {
			if (moveY == Game.DIR_UP) {
				animUp.turnOn();
				animDown.turnOff();
				animNone.turnOff();
			} else if (moveY == Game.DIR_DOWN) {
				animUp.turnOff();
				animDown.turnOn();
				animNone.turnOff();
			} else if (moveY == Game.DIR_NONE) {
				animUp.turnOff();
				animDown.turnOff();
				animNone.turnOn();
			}
			// if we don't specify any up down animation, just use the standard
			// animation (if defined)
		} else if (animNone != null) {
			animNone.turnOn();
		}
	}

	/**
	 * Forces all GameGraphics assigned to the object to become invisible and
	 * also stop playing if animations.
	 */
	public void turnOffGraphics() {
		for (int j = 0; j < graphicList.size(); j++) {
			GameGraphic graphic = graphicList.get(j);
			if (graphic != null) {
				graphic.turnOff();
			}
		}
	}

	/**
	 * Forces all GameGraphics assigned to the object to become visible and also
	 * start playing if animations.
	 */
	public void turnOnGraphics() {
		for (int j = 0; j < graphicList.size(); j++) {
			GameGraphic graphic = graphicList.get(j);
			if (graphic != null) {
				graphic.turnOn();
			}
		}
	}

	/**
	 * Adds a GameGraphic to the GameObject
	 * @param graphic
	 */
	public void addGraphic(GameGraphic graphic) {
		if (graphic != null) {
			graphicList.add(graphic);
		}
	}

	/**
	 * Removes a GameGraphic from the GameObject
	 * @param graphic
	 */
	public void removeGraphic(GameGraphic graphic) {
		if (graphic != null) {
			graphicRemoveList.add(graphic);
		}
	}

	/**
	 * Update the GameGraphics attached to the GameObject
	 * @param camera
	 */
	public void updateGraphics(Camera camera) {
		// update the graphics
		for (int j = 0; j < graphicList.size(); j++) {
			GameGraphic graphic = graphicList.get(j);
			// update the graphic when the object is on the screen and the
			// graphic is visible or if the object is a Unit and it is
			// destroyed (during explosion)
			if (getIsOnScreen(camera) && graphic.getIsVisible()
					|| this instanceof Unit && getIsDestroyed()) {
				graphic.move(camera, this);
			}
		}
		// remove graphics once not in use, seldom needed since the graphics are
		// contained in the object only needed when we want to dynamically
		// assign and remove graphics from an object
		for (GameGraphic object : graphicRemoveList) {
			graphicList.remove(object);
		}
		graphicRemoveList.clear();
	}

	/**
	 * Render the GameGraphics attached to the GameObject
	 * @param game
	 * @param camera
	 */
	public void renderGraphics(Game game, Camera camera) {
		for (int j = 0; j < graphicList.size(); j++) {
			GameGraphic graphic = graphicList.get(j);
			// render the graphics if they are on the screen and visible
			if (getIsOnScreen(camera) && graphic.getIsVisible()) {
				graphic.drawCentered(game, camera, this);
			}
		}
	}

	/** Gets the main GameGraphic attached to the GameObject */
	public GameGraphic getGraphic() {
		return graphic;
	}

	/** Sets the main GameGraphic attached to the GameObject */
	public void setGraphic(GameGraphic graphic) {
		this.graphic = graphic;
		addGraphic(graphic);
	}

	/** Gets the distance to be moved this update round in X direction */
	public double getStepX() {
		return stepX;
	}

	/** Sets the distance to be moved this update round in X direction */
	public void setStepX(double delta) {
		// if velocity has been disabled, then use the maximum speed
		if (disableVelocity) {
			velocityX = moveX * maxSpeed;
		}
		this.stepX = velocityX * delta;
	}

	/** Gets the distance to be moved this update round in Y direction */
	public double getStepY() {
		return stepY;
	}

	/** Sets the distance to be moved this update round in Y direction */
	public void setStepY(Camera camera, double delta) {
		// if velocity has been disabled, then use the maximum speed
		if (disableVelocity) {
			velocityY = moveY * maxSpeed;
		}
		// the player has automatic camera movement
		if (getIsPlayer()) {
			this.stepY = velocityY * delta - camera.getStep();
		} else {
			this.stepY = velocityY * delta;
		}
	}

	/** 
	 * Forcefully sets the distance to be moved this update round in X direction
	 * @param stepX
	 */
	public void setForceStepX(double stepX) {
		this.stepX = stepX;
	}

	/**
	 * Forcefully sets the distance to be moved this update round in Y direction
	 * @param stepY
	 */
	public void setForceStepY(double stepY) {
		this.stepY = stepY;
	}
	
	/** Gets left X coordinate of GameObject */
	public double getLeft() {
		return x - getWidth() / 2;
	}

	/** Gets right X coordinate of GameObject */
	public double getRight() {
		return getLeft() + getWidth();
	}

	/** Gets top Y coordinate of GameObject */
	public double getTop() {
		return y - getHeight() / 2;
	}

	/** Gets bottom Y coordinate of GameObject */
	public double getBottom() {
		return getTop() + getHeight();
	}

	/** Gets GameObject X coordinate */
	public double getX() {
		return x;
	}

	/** Gets GameObject Y coordinate */
	public double getY() {
		return y;
	}

	/** Gets GameObject maximum speed */
	public double getMaxSpeed() {
		return maxSpeed;
	}

	/** Sets GameObject maximum speed */
	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	/** Gets current GameObject velocity in X direction */
	public double getVelocityX() {
		return velocityX;
	}
	
	/** Gets current GameObject velocity in Y direction */
	public double getVelocityY() {
		return velocityY;
	}

	/**
	 * Sets the velocity in the X direction, if the GameObject is capable of
	 * such a velocity.
	 * @param velocityX
	 */
	public void setVelocityX(double velocityX) {
		// if we aren't capable of such a velocity
		if (velocityX > maxSpeed) {
			System.err.printf(
					"Can't increase velocity of %s to %f, past maxSpeed %f %n",
					getClass(), velocityX, maxSpeed);
		} else {
			// using the inverse velocity function easeIn, we feed in the velocity
			// we want, it gives us what accelTime will achieve this.
			accelTimeX = easeIn(velocityX);
			this.velocityX = velocityX;
		}
	}

	/**
	 * Sets the velocity in the Y direction, if the GameObject is capable of
	 * such a velocity.
	 * @param velocityY
	 */
	public void setVelocityY(double velocityY) {
		// if we aren't capable of such a velocity
		if (velocityY > maxSpeed) {
			System.err.printf(
					"Can't increase velocity of %s to %f, past maxSpeed %f %n",
					getClass(), velocityY, maxSpeed);
		} else {
			// using the inverse velocity function easeIn, we feed in the velocity
			// we want, it gives us what accelTime will achieve this.
			accelTimeY = easeIn(velocityY);
			this.velocityY = velocityY;
		}
	}

	/** Gets the acceleration rate */
	public double getAcceleration() {
		return accel;
	}

	/** Sets the acceleration rate */
	public void setAcceleration(double accel) {
		this.accel = accel;
	}

	/** Gets the deceleration rate */
	public double getDeceleration() {
		return decel;
	}

	/** Sets the deceleration rate */
	public void setDeceleration(double decel) {
		this.decel = decel;
	}	

	/** Sets GameObject X */
	public void setX(double x) {
		this.x = x;
	}

	/** Sets GameObject Y */
	public void setY(double y) {
		this.y = y;
	}

	/** Gets height of GameObject */
	public double getHeight() {
		// return the custom set height if defined
		if (customHeight != Game.UNDEFINED) {
			return customHeight * Game.HITBOX_PRECISION;
		// otherwise return the height of the graphic
		} else if (graphic != null) {
			return graphic.getHeight() * Game.HITBOX_PRECISION;
		} else {
			return 0;
		}
	}

	/** Gets width of GameObject */
	public double getWidth() {
		// return the custom set width if defined
		if (customWidth != Game.UNDEFINED) {
			return customWidth * Game.HITBOX_PRECISION;
		// otherwise return the width of the graphic
		} else if (graphic != null) {
			return graphic.getWidth() * Game.HITBOX_PRECISION;
		} else {
			return 0;
		}
	}

	/** Checks if the GameObject is an instance of the Player class */
	public boolean getIsPlayer() {
		return this instanceof Player;
	}

	/** Checks if the GameObject is on the screen */
	public boolean getIsOnScreen(Camera camera) {
		if ((getBottom() > camera.getTop() && getTop() < camera.getBottom())) {
			return true;
		} else {
			return false;
		}
	}

	/** Checks if the GameObject is visible */
	public boolean getIsVisible() {
		return this.graphic.getIsVisible();
	}

	/** Sets the GameObject visibility */
	public void setIsVisible(boolean isVisible) {
		this.graphic.setIsVisible(isVisible);
	}

	/** Checks if the GameObject is destroyed */
	public boolean getIsDestroyed() {
		return isDestroyed;
	}

	/** Set to true if the GameObject is destroyed */
	public void setIsDestroyed(boolean isDestroyed) {
		this.isDestroyed = isDestroyed;
	}

	/** Checks if No Clip is enabled */
	public boolean getNoClip() {
		return noClip;
	}

	/** Sets No Clip */
	public void setNoClip(boolean noClip) {
		this.noClip = noClip;
	}

	/** Checks if movement is disabled */
	public boolean getDisableMove() {
		return disableMove;
	}

	/** Sets if movement is disabled */
	public void setDisableMove(boolean disableMove) {
		this.disableMove = disableMove;
	}

	/** Gets the movement direction in X based on input */
	public double getMoveX() {
		return moveX;
	}

	/** Sets the movement direction in X based on input */
	public void setMoveX(double moveX) {
		// if we have disabled velocity or we are not stationary
		if (disableVelocity || moveX != 0) {
			// update the real movement direction
			realMoveX = moveX;
		}
		this.moveX = moveX;
	}

	/** Gets the movement direction in Y based on input */
	public double getMoveY() {
		return moveY;
	}

	/** Sets the movement direction in X based on input */
	public void setMoveY(double moveY) {
		// if we have disabled velocity or we are not stationary
		if (disableVelocity || moveY != 0) {
			// update the real movement direction
			realMoveY = moveY;
		}
		this.moveY = moveY;
	}

	/** Sets the real movement direction in X based on actual movement */
	public void setRealMoveX(double realMoveX) {
		this.realMoveX = realMoveX;
	}

	/** Gets the real movement direction in X based on actual movement */
	public double getRealMoveX() {
		return realMoveX;
	}
	
	/** Sets the real movement direction in Y based on actual movement */
	public void setRealMoveY(double realMoveY) {
		this.realMoveY = realMoveY;
	}

	/** Gets the real movement direction in Y based on actual movement */
	public double getRealMoveY() {
		return realMoveY;
	}

	/** Sets whether the GameObject is destroyed when hitting a block */
	public void setDestroyOnBlock(boolean destroyOnBlock) {
		this.destroyOnBlock = destroyOnBlock;
	}

	/** Checks whether the GameObject is destroyed when hitting a block */
	public boolean getDestroyOnBlock() {
		return destroyOnBlock;
	}

	/** Sets whether the GameObject has velocity */
	public void setDisableVelocity(boolean disableVelocity) {
		this.disableVelocity = disableVelocity;
	}

	/** Checks whether the GameObject has velocity */
	public boolean isDisableVelocity() {
		return disableVelocity;
	}

	/** Sets GameObject custom width */
	public void setCustomWidth(double customWidth) {
		this.customWidth = customWidth;
	}

	/** Gets GameObject custom width */
	public double getCustomWidth() {
		return customWidth;
	}

	/** Sets GameObject custom height */
	public void setCustomHeight(double customHeight) {
		this.customHeight = customHeight;
	}

	/** Gets GameObject custom height */
	public double getCustomHeight() {
		return customHeight;
	}

}
