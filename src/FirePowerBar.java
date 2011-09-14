/* SWEN20003 Object Oriented Software Development
 * Panel Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

public class FirePowerBar extends PanelBar {
	/** Multiplier to convert ratio to percentage */
	private static final int PERCENT = 100;

	/**
	 * Creates a new FirePower PanelBar
	 * @param unit
	 * @param x
	 * @param y
	 * @throws SlickException
	 */
	public FirePowerBar(Unit unit, double x, double y) throws SlickException {
		super(unit, x, y);
		// set a new colour scheme
		Color[] colour = { new Color(0.32f, 0.02f, 0.02f),
				new Color(0.50f, 0.04f, 0.04f), new Color(0.72f, 0.06f, 0.06f),
				new Color(1.00f, 0.08f, 0.08f), new Color(1.00f, 0.36f, 0.08f),
				new Color(1.00f, 0.58f, 0.08f), new Color(1.00f, 0.73f, 0.08f),
				new Color(0.98f, 0.93f, 0.08f), new Color(1.00f, 1.00f, 0.6f) };
		setColour(colour);
	}

	@Override
	public void update() {
		// display the coolDown from the maximum coolDown in percentage form
		if (getParent() != null) {
			setValue(((double) Unit.MAX_COOLDOWN - getParent().getCoolDown())
					/ Unit.MAX_COOLDOWN * PERCENT);
			setMaxValue(PERCENT);
		}
	}
}
