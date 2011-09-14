/* SWEN20003 Object Oriented Software Development
 * Repair Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Repair extends Item {
	
	/**
	 * Creates a new Repair Item 
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public Repair(double x, double y, World world) throws SlickException {
		super(x, y, world);
		// repair animation
		int duration = 75;
		Image[] repair = { Game.repair, Game.repair2,
				Game.repair3 };
		setGraphic(new GameGraphic(0, 0, repair, duration, true, true, true));
	}

	@Override
	public void itemAction(Unit unit) {
		unit.setShield(unit.getFullShield());
	}
}
