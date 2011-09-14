/* SWEN20003 Object Oriented Software Development
 * MissileBoss Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.SlickException;

public class MissileBoss extends Missile {

	/**
	 * Creates a missile with the Boss Missile graphic and inertia.
	 * @param unit
	 * @param angle
	 * @throws SlickException
	 */
	public MissileBoss(Unit unit, Angle angle) throws SlickException {
		super(unit, angle);
		setGraphic(new GameGraphic(0, 0, Game.missileBoss.copy()));
		getGraphic().setAngle(angle);
		// for inertia:
		// set the velocity X of the missile to that of the parent
		setVelocityX(unit.getVelocityX());
	}

}
