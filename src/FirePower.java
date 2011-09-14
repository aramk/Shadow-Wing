/* SWEN20003 Object Oriented Software Development
 * FirePower Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class FirePower extends Item {
	
	/**
	 * Creates a FirePower Item.
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public FirePower(double x, double y, World world) throws SlickException {
		super(x, y, world);
		// firePower animation
		int duration = 75;
		Image[] firePower = { Game.firePower, Game.firePower2,
				Game.firePower3 };
		setGraphic(new GameGraphic(0, 0, firePower, duration, true, true, true));
	}

	@Override
	public void itemAction(Unit unit) {
		// increase firePower
		if (unit.getFirePower() < Unit.MAX_FIREPOWER) {
			unit.setFirePower(unit.getFirePower() + 1);
		}
	}
}
