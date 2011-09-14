/* SWEN20003 Object Oriented Software Development
 * MissileEnemy Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.SlickException;

public class MissileEnemy extends Missile {

	/**
	 * Creates a Missile with the Enemy Graphic
	 * @param unit
	 * @param angle
	 * @throws SlickException
	 */
	public MissileEnemy(Unit unit, Angle angle) throws SlickException {
		super(unit, angle);
		setGraphic(new GameGraphic(0, 0, Game.missileEnemy.copy()));
		getGraphic().setAngle(angle);
	}

}
