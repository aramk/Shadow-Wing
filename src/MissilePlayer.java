/* SWEN20003 Object Oriented Software Development
 * MissilePlayer Class
 * Author: Aram Kocharyan <aramk>
 * Print Margin: 120 chars
 */

import org.newdawn.slick.SlickException;

public class MissilePlayer extends Missile {

	/**
	 * Creates a Missile with the Player Missile graphic and inertia.
	 * @param unit
	 * @param angle
	 * @throws SlickException
	 */
	public MissilePlayer(Unit unit, Angle angle) throws SlickException {
		super(unit, angle);
		setGraphic(new GameGraphic(0, 0, Game.missilePlayer.copy()));
		getGraphic().setAngle(angle);
		// for inertia:
		// set the velocity X of the missile to that of the parent
		setVelocityX(unit.getVelocityX());
	}

}
