/* SWEN20003 Object Oriented Software Development
 * Panel Class
 * Author: Aram Kocharyan <aramk>
 * Print Margin: 120 chars
 */

import java.util.ArrayList;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * The status panel. Renders itself to a fixed position at the bottom of the
 * screen, displaying the given stats for the object. (See the render() method.)
 */
public class Panel {

	/** Collection of PanelBars */
	private ArrayList<PanelBar> barList = new ArrayList<PanelBar>();
	/** Collection of PanelBars to remove */
	private ArrayList<PanelBar> barRemoveList = new ArrayList<PanelBar>();
	/** Height of the status panel in pixels. */
	public static final int PANEL_HEIGHT = 20;

	/** position of the panel */
	private static final int panelX = 0, panelY = Game.playheight()
			- PANEL_HEIGHT;

	/** Creates a new Panel. */
	public Panel() {
	}

	/**
	 * Renders the status panel for the object.
	 * 
	 * @param g
	 *            The current Slick graphics context.
	 * @param object
	 *            The object.
	 * @throws SlickException
	 */
	public void render(Graphics g) throws SlickException {
		// draw panel
		Game.panel.draw(panelX, panelY);

		// for all the PanelBars
		for (int i = 0; i < barList.size(); i++) {
			PanelBar bar = barList.get(i);
			// if the parent of the PanelBar is not destroyed and PanelBar is
			// visible
			if (!bar.getParent().getIsDestroyed() && bar.getIsVisible()) {
				// render the PanelBar
				bar.render(g);
			} else if (bar.getParent().getIsDestroyed()) {
				// remove the PanelBar if the parent is destroyed
				removeBar(bar);
			}
		}

		// remove any bars no longer needed
		for (PanelBar bar : barRemoveList) {
			barList.remove(bar);
		}
		barRemoveList.clear();
	}

	/**
	 * Adds a PanelBar to the Panel
	 * @param bar
	 * @throws SlickException
	 */
	public void addBar(PanelBar bar) throws SlickException {
		if (bar != null) {
			barList.add(bar);
		}
	}

	/**
	 * Removes a PanelBar from the Panel
	 * @param bar
	 */
	public void removeBar(PanelBar bar) {
		if (bar != null) {
			barRemoveList.add(bar);
		}
	}

}
