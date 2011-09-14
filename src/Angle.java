/* SWEN20003 Object Oriented Software Development
 * Angle Class
 * Author: Aram Kocharyan <aramk>
 */

public class Angle {

	// Various angle definitions
	public static final double UP = Math.PI * 0.5;
	public static final double DOWN = Math.PI * 1.5;
	public static final double LEFT = Math.PI;
	public static final double RIGHT = 0;
	public static final double NONE = -1;
	public static final double TOP_RIGHT_DEGREES = 45;
	public static final double TOP_LEFT_DEGREES = 135;
	public static final double BOTTOM_LEFT_DEGREES = 225;
	public static final double BOTTOM_RIGHT_DEGREES = 315;
	public static final double TOP_DEGREES = 90;
	public static final double BOTTOM_DEGREES = 270;
	public static final double LEFT_DEGREES = 180;
	public static final double RIGHT_DEGREES = 0;
	
	/** Angle in radians */
	double theta;

	/**
	 * Creates a new Angel
	 * @param theta
	 */
	public Angle(double theta) {
		this.theta = theta;
	}

	/** Sets the angle in radians */
	public void setRadians(double theta) {
		this.theta = theta;
	}

	/** Sets the angle in degrees */
	public void setDegrees(double degrees) {
		this.theta = Math.toRadians(degrees);
	}

	/** Gets the Radians for use with Slick Images, uses clockwise angles. */
	public double getRadians() {
		// slick uses clockwise angles, so we need to reverse direction
		return -theta;
	}

	/** Gets conventional anti-clockwise angle in Radians */
	public double getRadiansCCW() {
		return theta;
	}

	/** Gets the Degrees for use with Slick Images, uses clockwise angles. */
	public double getDegrees() {
		double temp = Math.toDegrees(-theta);
		// for negative Radians, convert to degrees
		if (temp < 0) {
			temp = 360 + temp;
		}
		return temp;
	}

	/** Gets conventional anti-clockwise angle in Degrees */
	public double getDegreesCCW() {
		double temp = Math.toDegrees(theta);
		// for negative Radians, convert to degrees
		if (temp < 0) {
			temp = 360 + temp;
		}
		return temp;
	}

	/** Gets the unsigned X ratio from the Unit Circle for a given Angle */
	public static double unitCircleX(Angle angle) {
		if (angle != null) {
			return Math.abs(unitCircleXSigned(angle));
		}
		return 0;
	}

	/** Gets the Y unsigned ratio from the Unit Circle for a given Angle */
	public static double unitCircleY(Angle angle) {
		if (angle != null) {
			return Math.abs(unitCircleYSigned(angle));
		}
		return 0;
	}

	/** Gets the signed X ratio from the Unit Circle for a given Angle */
	public static double unitCircleXSigned(Angle angle) {
		if (angle != null) {
			return Math.cos(angle.getRadians());
		}
		return 0;
	}

	/** Gets the signed Y ratio from the Unit Circle for a given Angle */
	public static double unitCircleYSigned(Angle angle) {
		if (angle != null) {
			return Math.sin(angle.getRadians());
		}
		return 0;
	}
}
