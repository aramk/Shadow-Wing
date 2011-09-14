/* SWEN20003 Object Oriented Software Development
 * NumberLarge Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class NumberLarge extends Number {
	
	/**
	 * Creates a Number with large digits
	 * @param x
	 * @param y
	 * @throws SlickException
	 */
	public NumberLarge(double x, double y) throws SlickException {
		super(x, y);
		Image[] images = { Game.large0, Game.large1, Game.large2, Game.large3,
				Game.large4, Game.large5, Game.large6, Game.large7,
				Game.large8, Game.large9 };
		setImages(images);
		getImages();
	}
}
