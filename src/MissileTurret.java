/* SWEN20003 Object Oriented Software Development
 * MissileTurret Class
 * Author: Aram Kocharyan <aramk>
 * Print Margin: 120 chars
 */

import org.newdawn.slick.SlickException;

public class MissileTurret extends MissileEnemy {

	/**
	 * Creates a MissileEnemy with movement disabled to allow for any Missile
	 * angle to be defined
	 * @param unit
	 * @param angle
	 * @throws SlickException
	 */
	public MissileTurret(Unit unit, Angle angle) throws SlickException {
		super(unit, angle);
		// disable movement
		setDisableMove(true);
	}

}
