/* SWEN20003 Object Oriented Software Development
 * ShieldBar Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

public class ShieldBar extends PanelBar {
	
	/**
	 * Creates a new ShieldBar for a Unit
	 * @param unit
	 * @param x
	 * @param y
	 * @throws SlickException
	 */
	public ShieldBar(Unit unit, double x, double y) throws SlickException {
		super(unit, x, y);
		// define the custom colour scheme
		Color[] colour = { new Color(0.46f, 0.70f, 0.07f),
				new Color(0.75f, 0.85f, 0.21f), new Color(0.98f, 0.93f, 0.08f),
				new Color(1.00f, 0.73f, 0.08f), new Color(1.00f, 0.58f, 0.08f),
				new Color(1.00f, 0.36f, 0.08f), new Color(1.00f, 0.08f, 0.08f),
				new Color(0.71f, 0.04f, 0.36f), new Color(0.67f, 0.03f, 0.58f),
				new Color(0.22f, 0.02f, 0.64f), new Color(0f, 0f, 0f) };
		setColour(colour);
	}

	@Override
	public void update() {
		// update the value to the parent's shield
		if (getParent() != null) {
			setValue(getParent().getShield());
			setMaxValue(getParent().getFullShield());
		}
	}

}
