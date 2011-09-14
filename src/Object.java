/* SWEN20003 Object Oriented Software Development
 * Object Class
 * Author: Aram Kocharyan <aramk>
 */

//import org.newdawn.slick.Image;

import org.newdawn.slick.SlickException;

/** Object class adds abstract AI method for intelligent decisions */
public abstract class Object extends GameObject {
	public static final int NO_SHIELD = 0, NO_DAMAGE = 0;
	// initial values
	/** Object damage when colliding */
	private int damage = 0;
	/** Object current shield */
	private int shield = 0;
	/** Object maximum shield */
	private int fullShield = 0;
	/** Object score added to Player total when destroyed */
	private int score = 0;

	/**
	 * Artificial Intelligence for objects to implement custom movement and
	 * behaviour.
	 */
	public abstract void AI(int delta, World world, Camera camera)
			throws SlickException;

	/** Gets the Object shield */
	public int getShield() {
		return shield;
	}

	/** Sets the Object shield */
	public void setShield(int shield) {
		if (shield >= NO_SHIELD) {
			this.shield = shield;
		} else {
			System.err.printf("Invalid shield for %s %n", this.getClass());
		}
	}

	/** Gets the Object maximum shield */
	public int getFullShield() {
		return fullShield;
	}

	/** Sets the Object maximum shield */
	public void setFullShield(int fullShield) {
		if (fullShield >= NO_SHIELD) {
			this.fullShield = fullShield;
		} else {
			System.err.printf("Invalid fullShield for %s %n", this.getClass());
		}
	}

	/** Gets the Object damage */
	public int getDamage() {
		return damage;
	}

	/** Sets the Object damage */
	public void setDamage(int damage) {
		if (damage >= NO_DAMAGE) {
			this.damage = damage;
		} else {
			System.err.printf("Invalid damage for %s %n", this.getClass());
		}
	}

	/** Sets the Object score given to the Player when destroyed */
	public void setScore(int score) {
		this.score = score;
	}

	/** Gets the Object score given to the Player when destroyed */
	public int getScore() {
		return score;
	}
}
