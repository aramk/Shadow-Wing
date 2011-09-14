/* SWEN20003 Object Oriented Software Development
 * Drone Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Drone extends Unit {
	
	/**
	 * Creates a new Drone.
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public Drone(double x, double y, World world) throws SlickException {
		setX(x);
		setY(y);
		setMaxSpeed(0.2);
		setFullShield(16);
		setShield(getFullShield());
		setDamage(8);
		setFirePower(0);
		setDisableMove(true);
		setDisableVelocity(true);
		setScore(15);
		// create the drone animation
		int duration = 25;
		Image[] drone = { Game.drone, Game.drone2, Game.drone3, Game.drone4,
				Game.drone5, Game.drone6, Game.drone7, Game.drone8,
				Game.drone9, };
		setGraphic(new GameGraphic(0, 0, drone, duration, true, true, true));
	}

	@Override
	public void AI(int delta, World world, Camera camera) throws SlickException {
		if (getIsOnScreen(camera)) {
			// calculate distance from player
			double distX, distY, distTotal;
			distX = world.getPlayer().getX() - getX();
			distY = world.getPlayer().getY() - getY();
			distTotal = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

			// determine movement towards player
			if (distX > 0) {
				setMoveX(Game.DIR_RIGHT);
			} else if (distX < 0) {
				setMoveX(Game.DIR_LEFT);
			} else {
				setMoveX(0);
			}
			if (distY > 0) {
				setMoveY(Game.DIR_DOWN);
			} else if (distY < 0) {
				setMoveY(Game.DIR_UP);
			} else {
				setMoveY(0);
			}

			// first calculate the step movements if we were to move in a linear
			// direction
			setStepX(delta);
			setStepY(camera, delta);

			// if our movements will pass the player, adjust it so that it
			// doesn't
			if (Math.abs(getStepX()) > Math.abs(distX)) {
				setForceStepX(distX);
			}
			if (Math.abs(getStepY()) > Math.abs(distY)) {
				setForceStepY(distY);
			}

			// only move if we need to, be careful of dividing by zero
			// movement is determined by the ratio of our distance in X and Y
			// and the total distance from the player
			if (distTotal != 0) {
				if (getStepX() != Game.DIR_NONE) {
					setForceStepX(Math.abs((distX / distTotal)) * getStepX());
				}
				if (getStepY() != Game.DIR_NONE) {
					setForceStepY(Math.abs((distY / distTotal)) * getStepY());
				}
			}
			move(delta, world, camera);
		}
	}
}
