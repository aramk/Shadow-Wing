/* SWEN20003 Object Oriented Software Development
 * Item Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.SlickException;

public abstract class Item extends GameObject {
	
	/**
	 * Creates a new Item
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public Item(double x, double y, World world) throws SlickException {
		setX(x);
		setY(y);
	}

	/**
	 * Performs an action on a Unit
	 * @param unit
	 */
	public abstract void itemAction(Unit unit);
}
