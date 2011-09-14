/* SWEN20003 Object Oriented Software Development
 * Missile Class
 * Author: Aram Kocharyan <aramk>
 */

//import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public abstract class Missile extends Object {
	/** The parent of the Missile */
	private Unit parent;
	/** Whether we have already initialised the Missile's trajectory */
	private boolean initial = false;
	/** Determines the position of the Missile */
	double stepRatioX = 0, stepRatioY = 0;
	/** The extra space between the radius of the parent and the position of the
	 *  missile. */
	public static double BUFFER_ZONE = 5;

	/**
	 * Creates a new Missile
	 * @param unit
	 * @param angle
	 * @throws SlickException
	 */
	public Missile(Unit unit, Angle angle) throws SlickException {
		setMaxSpeed(0.7);
		setFullShield(1);
		setShield(getFullShield());
		setDamage(8);
		setDestroyOnBlock(true);
		setParent(unit);
		// set the height and width as 0 to ensure Missile is a point
		setCustomWidth(0);
		setCustomHeight(0);
		// deceleration for inertia effect
		setDeceleration(0.0001);
		// the radius of the parent plus the buffer zone
		double radius = unit.getRadius() + BUFFER_ZONE;
		// the ratio between X and Y from the Unit Circle that determines the
		// starting position of the Missile
		double stepRatioX = Angle.unitCircleXSigned(angle);
		double stepRatioY = Angle.unitCircleYSigned(angle);
		// set the starting positions
		setX(unit.getX() + radius * stepRatioX);
		setY(unit.getY() + radius * stepRatioY);
	}

	@Override
	public void AI(int delta, World world, Camera camera) throws SlickException {
		if (getIsOnScreen(camera)) {
			// if the we have a graphic for the missile with a defined angle
			if (getGraphic() != null && getGraphic().getAngle() != null) {
				// if we have not yet determined the trajectory
				if (initial == false) {
					// get the angle in conventional degrees
					double degrees = getGraphic().getAngle().getDegreesCCW();
					// determine the direction the missile is traveling in from
					// the angle it has been fired at
					if (degrees > Angle.RIGHT_DEGREES && degrees < Angle.LEFT_DEGREES) {
						setMoveY(Game.DIR_UP);
					} else if (degrees > Angle.LEFT_DEGREES) {
						setMoveY(Game.DIR_DOWN);
					} else if (degrees == Angle.RIGHT_DEGREES || degrees == Angle.LEFT_DEGREES) {
						setMoveY(Game.DIR_NONE);
					}
					if (degrees > Angle.BOTTOM_DEGREES || degrees < Angle.TOP_DEGREES) {
						setMoveX(Game.DIR_RIGHT);
					} else if (degrees > Angle.TOP_DEGREES && degrees < Angle.BOTTOM_DEGREES) {
						setMoveX(Game.DIR_LEFT);
					} else if (degrees == Angle.TOP_DEGREES || degrees == Angle.BOTTOM_DEGREES) {
						setMoveX(Game.DIR_NONE);
					}
					// if the missile is moving in an X Y direction, ensure it
					// has maximum initial velocity
					if (getMoveX() != Game.DIR_NONE) {
						setVelocityX(getMoveX() * getMaxSpeed());
					}
					if (getMoveY() != Game.DIR_NONE) {
						setVelocityY(getMoveY() * getMaxSpeed());
					}
					// if movement has been disabled because the Unit can change
					// the angle of the missile (Turret) to any angle it chooses,
					// not just UP, DOWN, LEFT and RIGHT
					if (getDisableMove()) {
						// calculate the ratio between X and Y movements to
						// ensure the missile travels in the correct angle
						stepRatioX = Angle.unitCircleX(getGraphic().getAngle());
						stepRatioY = Angle.unitCircleY(getGraphic().getAngle());
					}
					// we have set up the initial trajectory
					initial = true;
				}
				// if we have disabled movement for Units that fire Missiles
				// in any direction
				if (getDisableMove()) {
					// perform routine acceleration and step sizes
					accelerate(delta);
					setStepX(delta);
					setStepY(camera, delta);
					// now alter the step sizes to the ratio given by the Unit Circle
					// to ensure the missile travels in-line with it's angle
					setForceStepX(getStepX() * stepRatioX);
					setForceStepY(getStepY() * stepRatioY);
				}
			}
			// once trajectory has been decided, continue moving
			move(delta, world, camera);
		} else {
			// once off screen, destroy the missile
			world.removeObject(this);
		}
	}

	/** Sets the parent of the Missile */
	public void setParent(Unit parent) {
		this.parent = parent;
	}

	/** Gets the parent of the Missile */
	public Unit getParent() {
		return parent;
	}
}
