/* SWEN20003 Object Oriented Software Development
 * Boss Class
 * Author: Aram Kocharyan <aramk>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Boss extends Unit {
	/** Left X boundary before moving right */
	private static final int LEFT_BOUNDARY = 288;
	/** Right X boundary before moving left */
	private static final int RIGHT_BOUNDARY = 576;
	/** The Y position of the ShieldBar */
	private static final int SHIELD_Y = 10;
	/** The ShieldBar */
	public PanelBar bar;

	/**
	 * Creates a new Boss.
	 * @param x
	 * @param y
	 * @param world
	 * @throws SlickException
	 */
	public Boss(double x, double y, World world) throws SlickException {
		setX(x);
		setY(y);
		setMaxSpeed(0.2);
		setAcceleration(0.0008);
		setFullShield(240);
		setShield(getFullShield());
		setDamage(100);
		setFirePower(3);
		setMoveX(Game.DIR_UP);
		setScore(100);
		int[] duration = { 80, 60, 40 };
		// boss animation
		Image[] bossImage = { Game.boss, Game.boss2, Game.boss3 };
		GameGraphic boss = new GameGraphic(0, 0, bossImage, duration, true,
				true, true);
		setGraphic(boss);
		// create a shield bar
		bar = new ShieldBar(this, Game.playwidth() / 2 - PanelBar.MAX_BAR_WIDTH
				/ 2, SHIELD_Y);
		bar.setIsVisible(false);
		world.getPanel().addBar(bar);
	}

	@Override
	public void AI(int delta, World world, Camera camera) throws SlickException {
		if (getIsOnScreen(camera)) {
			// while the boss is on the screen, the health bar is visible
			bar.setIsVisible(true);
			// move left and right
			if (getX() <= LEFT_BOUNDARY) {
				setMoveX(Game.DIR_RIGHT);
			} else if (getX() >= RIGHT_BOUNDARY) {
				setMoveX(Game.DIR_LEFT);
			}
			move(delta, world, camera);
			shoot(world);
			coolDown(delta);
		} else {
			bar.setIsVisible(false);
		}
	}

	@Override
	public Missile createMissile() throws SlickException {
		return new MissileBoss(this, new Angle(Angle.DOWN));
	}
}
