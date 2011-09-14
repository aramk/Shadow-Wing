/* SWEN20003 Object Oriented Software Development
 * Shield Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Shield extends Item {
	
	/**
	 * Creates a new Shield Item
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public Shield(double x, double y, World world) throws SlickException {
		super(x, y, world);
		// shield animation
		int duration = 75;
		Image[] shield = { Game.shield, Game.shield2,
				Game.shield3 };
		setGraphic(new GameGraphic(0, 0, shield, duration, true, true, true));
	}

	@Override
	public void itemAction(Unit unit) {
		unit.setFullShield(unit.getFullShield() + 40);
	}
}
