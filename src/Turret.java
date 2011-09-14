/* SWEN20003 Object Oriented Software Development
 * Turret Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Turret extends Unit {
	GameGraphic turret;
	Angle turretAngle;

	/**
	 * Creates a new Turret
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public Turret(double x, double y, World world) throws SlickException {
		setX(x);
		setY(y);
		setFullShield(16);
		setShield(getFullShield());
		setDamage(8);
		setFirePower(0);
		setDisableMove(true);
		setDisableVelocity(true);
		setScore(25);
		
		int[] duration = { 75, 50, 25 };
		// turret base
		Image[] turretImage = { Game.turret.copy(), Game.turret2.copy(),
				Game.turret3.copy() };
		setGraphic(new GameGraphic(0, 0, Game.turretBase));
		// turret shooter
		turret = new GameGraphic(0, 0, turretImage, duration, false, true, true);
		addGraphic(turret);
		// initial angle of turret
		turretAngle = new Angle(0);
	}

	@Override
	public void AI(int delta, World world, Camera camera) throws SlickException {
		if (getIsOnScreen(camera)) {
			// calculate the distance between the turret and the player
			double distX, distY;
			distX = world.getPlayer().getX() - getX();
			distY = getY() - world.getPlayer().getY();
			// set the angle based on this distance
			turretAngle.setRadians(Math.atan2(distY, distX));
			// if we can shoot
			if (getCoolDown() == 0) {
				// shoot
				shoot(world);
				// if the turret shooter animation stops, reset it
				if (turret.getAnimation().isStopped()) {
					turret.getAnimation().setCurrentFrame(GameGraphic.FIRST_FRAME);
					turret.getAnimation().start();
				}
			}
			// set the turret angle
			turret.setAngle(turretAngle);
			// update the animations to the set angle
			turret.getAnimation().update(delta);
			coolDown(delta);
		}
	}

	@Override
	public Missile createMissile() throws SlickException {
		return new MissileTurret(this, turretAngle);
	}
}
