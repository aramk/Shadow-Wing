/* SWEN20003 Object Oriented Software Development
 * NumberSmall Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class NumberSmall extends Number {
	
	/**
	 * Creates a Number with small digits
	 * @param x
	 * @param y
	 * @throws SlickException
	 */
	public NumberSmall(double x, double y) throws SlickException {
		super(x, y);
		Image[] images = { Game.num0, Game.num1, Game.num2, Game.num3,
				Game.num4, Game.num5, Game.num6, Game.num7, Game.num8,
				Game.num9 };
		setImages(images);
	}
}
