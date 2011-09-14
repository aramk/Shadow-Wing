/* SWEN20003 Object Oriented Software Development
 * Number Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * The status panel. Renders itself to a fixed position at the bottom of the
 * screen, displaying the given stats for the object. (See the render() method.)
 */
public abstract class Number {
	/** The X Y coordinates */
	private double x, y;
	/** The number to render */
	private int number;
	/** The number Images */
	private Image[] images;

	/**
	 * Creates a blank number
	 * @param x
	 * @param y
	 * @throws SlickException
	 */
	public Number(double x, double y) throws SlickException {
		setX(x);
		setY(y);
	}
	/**
	 * Renders the number, either centered or not.
	 * @param number
	 * 				The number to render
	 * @param centered
	 * 				True for centered
	 * @throws SlickException
	 */
	public void render(int number, boolean centered) throws SlickException {
		// set the number to render
		setNumber(number);
		// determine the length of digits
		double count = Integer.toString(number).length();
		// initialise the drawing X and Y coordinates
		double drawX = x, drawY = y;
		// if centered, adjust the draw coordinates
		if (centered) {
			drawY -= getHeight() / 2;
			drawX -= (getWidth() * (count)) / 2;
		}
		// if the images for the Number are all defined
		if (getImages() != null && getImages().length == 10) {
			// for every digit we need to render
			for (int i = 0; i < count; i++) {
				int digit = Integer.parseInt(Integer.toString(number)
						.substring(i, i + 1));
				// render the current digit
				// cast to int for clarity on screen
				images[digit].draw((int) (drawX + i * getWidth()),
						(int) (drawY));
			}
		}
	}

	/** Gets the X position */
	public double getX() {
		return x;
	}

	/** Sets the X position */
	public void setX(double x) {
		this.x = x;
	}

	/** Gets the Y position */
	public double getY() {
		return x;
	}

	/** Sets the X position */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Sets the number for reference later. Note that other classes
	 * set the number when they need to render it, but can access the drawn
	 * number at any time afterwards.
	 * @param number
	 */
	private void setNumber(int number) {
		// only accept positive values
		this.number = Math.abs(number);
	}

	/** Gets the drawn number */
	public int getNumber() {
		return number;
	}

	/** Sets the array of images to use for drawing the numbers */
	public void setImages(Image[] images) {
		this.images = images;
	}

	/** Gets the array of images to use for drawing the numbers */
	public Image[] getImages() {
		return images;
	}

	/** Gets the total width of the number in pixels */
	public int getTotalWidth() {
		return getWidth() * Integer.toString(number).length();
	}

	/** Gets the width of one digit in pixels. Assumes all number images have 
	 * the same width */
	public int getWidth() {
		if (getImages() != null) {
			return getImages()[0].getWidth();
		}
		return 0;
	}

	/** Gets the height of one digit in pixels. Assumes all number images have 
	 * the same height */
	public int getHeight() {
		if (getImages() != null) {
			return getImages()[0].getHeight();
		}
		return 0;
	}
}
