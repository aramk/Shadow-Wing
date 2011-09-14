/* SWEN20003 Object Oriented Software Development
 * Unit Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public abstract class Unit extends Object {
	
	/** The minimum coolDown */
	public static final int MIN_COOLDOWN = 0;
	/** The maximum coolDown */
	public static final int MAX_COOLDOWN = 300;
	/** The minimum firePower */
	public static final int MIN_FIREPOWER = 0;
	/** The maximum firePower */
	public static final int MAX_FIREPOWER = 3;
	/** The firePower multiplier */
	public static final int FIREPOWER_CONSTANT = 80;
	/** The firePower */
	private int firePower = MIN_FIREPOWER;
	/** The coolDown */
	private int coolDown = MIN_COOLDOWN;
	/** The explosion animation */
	private GameGraphic boom;

	public Unit() throws SlickException {
		// define the explosion animation for Units
		int duration = 50;
		Image[] boomImage = { Game.explosion, Game.explosion2, Game.explosion3,
				Game.explosion4, Game.explosion5, Game.explosion6,
				Game.explosion7, Game.explosion8, Game.explosion9,
				Game.explosion10, Game.explosion11, Game.explosion12,
				Game.explosion13, Game.explosion14, Game.explosion15,
				Game.explosion16, Game.explosion17,

		};
		// assign animation and hide
		boom = new GameGraphic(0, 0, boomImage, duration, true, false, false);
		boom.turnOff();
	}

	@Override
	public abstract void AI(int delta, World world, Camera camera)
			throws SlickException;

	/**
	 * Shoot command for Unit
	 * @param world
	 * @throws SlickException
	 */
	public void shoot(World world) throws SlickException {
		// only shoot if our coolDown is minimum
		if (getCoolDown() != MIN_COOLDOWN) {
			return;
		}
		
		// create a new missile
		Missile missile = createMissile();
		// if missile is null, we shouldn't be able to shoot
		if (missile == null) {
			return;
		}

		// check if the missile can be fired
		if (!checkMissileBlocked(world, missile)) {
			// play the laser sound with a random pitch
			world.getGame().playSound(Game.laserSound,
					(float) (0.5 - Math.random() / 4),
					Game.SOUND_EFFECT_VOLUME);
			// add the missile to the world
			world.addObject(missile);
			// set the coolDown
			setCoolDown(MAX_COOLDOWN - (FIREPOWER_CONSTANT * getFirePower()));
		}
	}
	
	/**
	 * Based on the angle the missile is fired, ensures it does not enter a block
	 * @param world
	 * @param missile
	 * @return True if we are trying to shoot in a direction that is blocked
	 */
	private boolean checkMissileBlocked(World world, Missile missile) {
		// determines whether there is a block in the direction of the missile
		boolean blockShootUp = false, blockShootDown = false,
			blockShootLeft = false, blockShootRight = false;
		double[] checkArray;
		// The radius to check is the radius of the Unit plus the buffer zone
		double radius = getRadius() + Missile.BUFFER_ZONE;
		// The Unit Circle ratios that determine the starting position of the
		// missile around the radius of the Unit
		double stepRatioX = Angle.unitCircleXSigned(missile.getGraphic()
				.getAngle());
		double stepRatioY = Angle.unitCircleYSigned(missile.getGraphic()
				.getAngle());
		// The angle of the missile in conventional degrees
		double degrees = missile.getGraphic().getAngle().getDegreesCCW();

		// if shooting up, check block in that direction
		if (degrees >= Angle.TOP_RIGHT_DEGREES && degrees <= Angle.TOP_LEFT_DEGREES) {
			checkArray = world.checkTile(getLeft(), getTop(), getRight(),
					Game.DIR_NONE, Game.DIR_UP);
			blockShootUp = checkArray[World.BLOCK_CHECK] == World.BLOCK_AHEAD
					&& getY() + radius * stepRatioY < checkArray[World.BLOCK_POSITION];
		}
		// if shooting left, check block in that direction
		if (degrees >= Angle.TOP_LEFT_DEGREES && degrees <= Angle.BOTTOM_LEFT_DEGREES) {
			checkArray = world.checkTile(getLeft(), getTop(), getBottom(),
					Game.DIR_LEFT, Game.DIR_NONE);
			blockShootLeft = checkArray[World.BLOCK_CHECK] == World.BLOCK_AHEAD
					&& getX() + radius * stepRatioX < checkArray[World.BLOCK_POSITION];
		}
		// if shooting down, check block in that direction
		if (degrees >= Angle.BOTTOM_LEFT_DEGREES && degrees <= Angle.BOTTOM_RIGHT_DEGREES) {
			checkArray = world.checkTile(getLeft(), getBottom(), getRight(),
					Game.DIR_NONE, Game.DIR_DOWN);
			blockShootDown = checkArray[World.BLOCK_CHECK] == World.BLOCK_AHEAD
					&& getY() + radius * stepRatioY > checkArray[World.BLOCK_POSITION];
		}
		// if shooting right, check block in that direction
		if (degrees >= Angle.BOTTOM_RIGHT_DEGREES || degrees <= Angle.TOP_RIGHT_DEGREES) {
			checkArray = world.checkTile(getRight(), getTop(), getBottom(),
					Game.DIR_RIGHT, Game.DIR_NONE);
			blockShootRight = checkArray[World.BLOCK_CHECK] == World.BLOCK_AHEAD
					&& getX() + radius * stepRatioX > checkArray[World.BLOCK_POSITION];
		}

		// if any direction is blocked and we are shooting in that direction,
		// return True
		return blockShootUp || blockShootDown || blockShootLeft
				|| blockShootRight;
	}

	/**
	 * Creates and returns a missile. By default, a Unit can't shoot so it will
	 * return NULL. This is overridden by sub-classes to return specific missiles. 
	 * @return Either null or a specific sub-class of Missile defined by sub-classes of Unit.
	 * @throws SlickException
	 */
	public Missile createMissile() throws SlickException {
		return null;
	}

	/**
	 * Runs the explosion effect before destroying the Unit
	 * @param world
	 * @throws SlickException
	 */
	public void explode(World world) throws SlickException {
		// if the animation is stopped (has not been played yet)
		if (boom.getAnimation().isStopped()) {
			// turn off all other graphics
			turnOffGraphics();
			// animate the explosion
			boom.turnOn();
			addGraphic(boom);
			// play the explosion sound
			world.getGame().playSound(Game.explosionSound,
					(float) (0.8 - Math.random() / 5),
					Game.SOUND_EFFECT_VOLUME);
		}
		// if we reach the last frame of the explosion, remove the object
		// entirely
		if (boom.getIsLastFrame()) {
			world.removeObject(this);
		}
	}

	/** Sets the explosion animation */
	public void setBoom(GameGraphic boom) {
		this.boom = boom;
	}

	/** Gets the explosion animation */
	public GameGraphic getBoom() {
		return boom;
	}

	/** Gets the FirePower */
	public int getFirePower() {
		return firePower;
	}

	/** Sets the FirePower */
	public void setFirePower(int firePower) {
		if (firePower >= MIN_FIREPOWER && firePower <= MAX_FIREPOWER) {
			this.firePower = firePower;
		} else {
			System.err.printf("Invalid firePower for %s %n", this.getClass());
		}
	}

	/** Cools down the Unit coolDown */
	public void coolDown(int delta) {
		if (getCoolDown() >= delta) {
			setCoolDown(getCoolDown() - delta);
		} else {
			setCoolDown(0);
		}
	}
	
	/** Gets the coolDown */
	public int getCoolDown() {
		return coolDown;
	}

	/** Sets the coolDown */
	public void setCoolDown(int coolDown) {
		if (coolDown >= MIN_COOLDOWN) {
			this.coolDown = coolDown;
		} else {
			System.err.printf("Invalid coolDown for %s %n", this.getClass());
		}
	}

	/** Calculates the distance from the centre of the Unit to the furthest edge */
	public double getRadius() {
		return Math.sqrt(Math.pow(getWidth() / 2, 2)
				+ Math.pow(getHeight() / 2, 2));
	}
}
