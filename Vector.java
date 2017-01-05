import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Littile tweaks from the Java Vector class to tailor my needs
 * 
 * @author Gaven Ma and Mike Zhao
 * @version 1.02
 */
public class Vector {
	private double dx, dy;
	private double magnitude, direction;

	/**
	 * Creates a vector with magnitude 0
	 */
	public Vector() {
		this.dx = 0;
		this.dy = 0;
		this.magnitude = 0;
		this.direction = 0;
	}

	/**
	 * If cartesian is true, then first is dx, second is dy and constructs a
	 * vector based on cartesian coordinate If cartesian is false, then first is
	 * magnitude, second is direction and constructs a vector based on polar
	 * coordinate
	 */
	public Vector(double first, double second, boolean cartesian) {
		if (cartesian) {
			this.dx = first;
			this.dy = second;
			this.updatePolar();
		} else {
			this.magnitude = first;
			this.direction = second;
			this.updateCartesian();
		}
	}

	// resets the vector to 0 magnitude
	public void reset() {
		this.magnitude = 0;
		this.direction = 0;
		this.updateCartesian();
	}

	public void updateCartesian() {
		this.dx = this.magnitude * Math.cos(Math.toRadians(this.direction));
		this.dy = this.magnitude * Math.sin(Math.toRadians(this.direction));
	}

	public void updatePolar() {
		this.magnitude = Math.sqrt(this.dx * this.dx + this.dy * this.dy);
		this.direction = Math.toDegrees(Math.atan2(this.dy, this.dx));
		if (this.magnitude < 0) {
			this.direction = this.direction + 180;
			if (this.direction >= 360)
				this.direction = this.direction - 360;
			this.magnitude = -this.magnitude;
		}
	}

	public void add(Vector vector) {
		this.dx = this.dx + vector.getX();
		this.dy = this.dy + vector.getY();
		this.updatePolar();
	}

	public static Vector add(Vector v1, Vector v2) {
		return new Vector(v1.getX() + v2.getX(), v1.getY() + v2.getY(), true);
	}

	public static double dotProduct(Vector v1, Vector v2) {
		return (v1.getMagnitude() * v2.getMagnitude() * Math.cos(Math
				.toRadians(v1.getDirection() - v2.getDirection())));
	}

	public void reverseDirection() {
		this.dx = -this.dx;
		this.dy = -this.dy;
		this.updatePolar();
	}

	public void setX(double x) {
		this.dx = x;
		this.updatePolar();
	}

	public void setY(double y) {
		this.dy = y;
		this.updatePolar();
	}

	public void setMagnitude(double m) {
		this.magnitude = m;
		this.updateCartesian();
	}

	public void setDirection(double d) {
		this.direction = d;
		this.updateCartesian();
	}

	public double getX() {
		return this.dx;
	}

	public double getY() {
		return this.dy;
	}

	public double getMagnitude() {
		return this.magnitude;
	}

	public double getDirection() {
		return this.direction;
	}

	public Vector duplicate() {
		Vector copy = new Vector(this.dx, this.dy, true);
		return copy;
	}
}
