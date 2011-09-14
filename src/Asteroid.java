/* SWEN20003 Object Oriented Software Development
 * Asteroid Class
 * Author: Aram Kocharyan <aramk>
 */

import java.util.Random;
import java.util.Date;
import org.newdawn.slick.SlickException;

public class Asteroid extends Unit {
	/** The starting rotation of Asteroid in degrees */
	private float startRotatation = (float) Angle.RIGHT_DEGREES;
	/** The starting rotation of Asteroid in degrees */
	private float rotateDirection = Game.DIR_NONE;
	private Date currTime = new Date();

	/** 
	 * Creates a new Asteroid
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public Asteroid(double x, double y, World world) throws SlickException {
		setX(x);
		setY(y);
		setMaxSpeed(0.2);
		setFullShield(24);
		setShield(getFullShield());
		setDamage(12);
		setFirePower(0);
		setScore(10);
		setMoveY(Game.DIR_DOWN);
		// asteroid image
		setGraphic(new GameGraphic(0, 0, Game.asteroid.copy()));
		// generate a random number and set seed using initial Y and epoch time
		Random rand = new Random();
		rand.setSeed((long) y * currTime.getTime());
		// start off with random rotation
		startRotatation = rand.nextInt(360);
		getGraphic().getImage().rotate(startRotatation);
		// generate the rotation direction
		rotateDirection = rand.nextInt(2);
		// if 0, set to LEFT, if 1 then already RIGHT
		if (rotateDirection == Game.DIR_NONE) {
			rotateDirection = Game.DIR_LEFT;
		}
	}

	@Override
	public void AI(int delta, World world, Camera camera) throws SlickException {
		if (getIsOnScreen(camera)) {
			// rotate based on the current Y
			getGraphic().getImage().setRotation(
					(float) (rotateDirection * getY() % 360));
			move(delta, world, camera);
		}
	}
}
