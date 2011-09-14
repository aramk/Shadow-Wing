/* SWEN20003 Object Oriented Software Development
 * PanelBar Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public abstract class PanelBar {
	/** X screen coordinate */
	private double x = 0;
	/** Y screen coordinate */
	private double y = 0;
	/** Value to draw in bar */
	private double value;
	/** Maximum value to draw in bar */
	private double maxValue;
	/** Visibility of bar */
	private boolean isVisible = true;
	/** Parent of bar */
	private Unit parent;
	/** Maximum bar width */
	public static final int MAX_BAR_WIDTH = 220;
	/** Bar fill height */
	public static final int BAR_FILL_HEIGHT = 9;
	/** Bar height */
	public static final int BAR_HEIGHT = 11;
	/** An empty value */
	public static final int NO_VALUE = 0;

	/** The colour scheme */
	private Color[] colour;
	/** Number to draw on top */
	private NumberSmall number;

	/**
	 * Creates a new PanelBar attached to a Unit
	 * @param unit
	 * @param x
	 * @param y
	 * @throws SlickException
	 */
	public PanelBar(Unit unit, double x, double y) throws SlickException {
		parent = unit;
		this.x = x;
		this.y = y;
		// define a number in the center of the PanelBar
		number = new NumberSmall(
				x + (double) Game.barBackground.getWidth() / 2, y
						+ (double) Game.barBackground.getHeight() / 2);
	}

	/** Updates the value */
	public abstract void update();

	public void render(Graphics g) throws SlickException {
		// update the value
		update();
		// ensure we don't access invalid memory, or run into a math exception
		if (value < NO_VALUE) {
			value = NO_VALUE;
		}
		if (maxValue == NO_VALUE || value > maxValue) {
			System.err.println("Error: value/maxValue incorrect");
			return;
		}
		if (colour == null) {
			System.err.println("Error: colour undefined");
			return;
		}
		// calculate the colourIndex for the bar based the value and maxValue
		int colourIndex = (int) ((colour.length - 1) - (value
				* (colour.length - 1) / maxValue));
		// draw the bar
		Game.barBackground.draw((float) x, (float) y);
		g.setColor(colour[colourIndex]);
		// width of the bar representing value
		double width = (value / maxValue) * MAX_BAR_WIDTH;
		g.fillRect((float) x + 1, (float) y + 1, (float) width, BAR_FILL_HEIGHT);
		Game.barOverlay.draw((float) x + 1, (float) y + 1, (float) width,
				BAR_FILL_HEIGHT);
		// draw the number
		number.render((int) value, true);
	}

	/** Set X coordinate */
	public void setX(double x) {
		this.x = x;
	}

	/** Get X coordinate */
	public double getX() {
		return x;
	}

	/** Set Y coordinate */
	public void setY(double y) {
		this.y = y;
	}

	/** Get Y coordinate */
	public double getY() {
		return y;
	}

	/** Set Parent Unit */
	public void setParent(Unit parent) {
		this.parent = parent;
	}

	/** Get Parent Unit */
	public Unit getParent() {
		return parent;
	}

	/** Set bar value */
	public void setValue(double value) {
		this.value = value;
	}

	/** Get bar value */
	public double getValue() {
		return value;
	}

	/** Set maximum bar value */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	/** Get maximum bar value */
	public double getMaxValue() {
		return maxValue;
	}

	/** Set colour scheme */
	public void setColour(Color[] colour) {
		this.colour = colour;
	}

	/** Get colour scheme */
	public Color[] getColour() {
		return colour;
	}

	/** Set Visibility */
	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/** Get Visibility */
	public boolean getIsVisible() {
		return isVisible;
	}
}
