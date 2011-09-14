/* SWEN20003 Object Oriented Software Development
 * Fighter Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Fighter extends Unit {
	
	/**
	 * Creates a Fighter.
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public Fighter(double x, double y, World world) throws SlickException {
		setX(x);
		setY(y);
		setMaxSpeed(0.2);
		setFullShield(24);
		setShield(getFullShield());
		setDamage(9);
		setFirePower(0);
		setMoveY(Game.DIR_DOWN);
		setDisableVelocity(true);
		setScore(20);
		int[] duration = { 25, 50, 75 };
		// booster normal
		Image[] booster = { Game.boosterFighter, Game.boosterFighter2,
				Game.boosterFighter3 };
		setAnimThis(world, Game.animDirection.ANIM_NONE, new GameGraphic(1,
				-35, booster, duration, true, true, false));

		// ship normal
		GameGraphic ship = new GameGraphic(0, 0, Game.fighter);
		setGraphic(ship);
	}

	@Override
	public void AI(int delta, World world, Camera camera) throws SlickException {
		if (getIsOnScreen(camera)) {
			// constantly shoots
			move(delta, world, camera);
			shoot(world);
			coolDown(delta);
		}
	}

	@Override
	public Missile createMissile() throws SlickException {
		return new MissileEnemy(this, new Angle(Angle.DOWN));
	}
}
