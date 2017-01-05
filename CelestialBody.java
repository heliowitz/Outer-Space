import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList; //import arraylist

/**
 * 
 * THis is the superclass of the celestial bodies, which gives basic attributes
 * to different bodies
 * 
 * @author Gaven Ma
 * @version 1.0
 */
public class CelestialBody extends SmoothMover {
	protected double mass; // the mass of the bodies in kg
	protected Vector gForce = new Vector(); // this is the gravity force that a
											// celectial body experiences
	protected Vector acceleration = new Vector(); // this is the acceceleration
													// due to gravity
	protected Vector speed = new Vector(); // this is the speed of the celestial
											// body due to gravity
	protected ArrayList<CelestialBody> cList = new ArrayList<CelestialBody>(); // create
																				// a
																				// new
																				// arraylist
	protected double attackPower = 0; // the attacking power of a celestial
										// body. Initialized to be 0.
	protected double resource; // the amount of resource left

	/**
	 * Act - do whatever the CelestialBody wants to do. This method is called
	 * whenever the 'Act' or 'Run' button gets pressed in the environment.
	 */
	public void act() {
		this.setLocation(this.getX() + speed.getX(), this.getY() + speed.getY());
		// move the celestial body due
		// to velocity

		cList = (ArrayList<CelestialBody>) this.getObjectsInRange(getWorld().getWidth(),
				CelestialBody.class);
		for (CelestialBody c : cList) {
			double distance = getDistance(c, this); // return the distance
			gForce.setMagnitude((6.67384 * Math.pow(10, -11) * c.getMass() * this
					.getMass()) / (Math.pow(distance * 1000, 2))); // set the
																	// magnitude
																	// of the
			// vector according to Newton's
			// gravity equation
			/**
			this.turnTowards(c.getX(), c.getY());
			gForce.setDirection(360 - this.getRotation());*/ // set the direction
															// of the
															// vector
			gForce.setDirection(Helper.getAngle(getX(), getY(), c.getX(), c.getY()));
			
			acceleration = gForce.duplicate();
			acceleration.setMagnitude(gForce.getMagnitude() / mass); // set the
																		// magnitude
																		// of
																		// acceleration
																		// due
																		// to
																		// equation
																		// f=ma;
			speed.setX(speed.getX() + acceleration.getX()); // update the
															// velocity basing
															// on acceleration
			speed.setY(speed.getY() + acceleration.getY()); // update the
															// velocity basing
															// on acceleration
		}
	}

	/**
	 * This is a method that gets distance between two actors
	 * 
	 * @param a
	 *            The first actor
	 * @param b
	 *            The second actor
	 * @return double the distance between two actors
	 */
	protected double getDistance(Actor a, Actor b) {
		return Math.sqrt(Math.pow(a.getX() - b.getX(), 2)
				+ Math.pow(a.getY() - b.getY(), 2)); // return the distance
	}

	/**
	 * A method returns mass
	 * 
	 * @return double The mass
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * This method gets the current attacking power of the sun
	 * 
	 * @return double The current attacking power of the sun
	 */
	public double getAttackPower() {
		return attackPower;
	}

	/**
	 * This method reduces the attack power to a certain specified percentage
	 * 
	 * @param percentage
	 *            The percentage to which the user wants the attack power to
	 *            reduce
	 */
	public void reduceAttackPower(double percentage) {
		attackPower = attackPower * percentage;
	}

	/**
	 * This method gets resource from sun and returns the current value of
	 * natural resource to the user (parameter set to 0 to get the current value
	 * of resource)
	 * 
	 * @ param exploit The amount of resource that is to be exploited @ return
	 * double The remaining amout of resource
	 * 
	 */

	public double exploit(double exploit) {
		this.resource = this.resource - exploit; // get resource from it
		return resource; // return the current remianing resource
	}

	/**
	 * This method gets resource from sun (percentage instead of actual value)
	 * and returns the current value of natural resource to the user
	 * 
	 * @param exploit
	 *            The percentage of resource that is to be exploited
	 * @param trueFalse
	 *            The boolean used to differentiate this method from the method
	 *            above. User could input either true or false
	 * @return double The remaining amout of resource
	 * 
	 */
	public double exploit(double percentage, boolean trueFalse) {
		this.resource = this.resource * percentage; // get resource from it
		return resource; // return the current remianing resource
	}

	/**
	 * This method initialized the velocity of the celestial body
	 * 
	 * @param speed
	 *            The vector that is intended to be used as the velocity of the
	 *            celestial body
	 */
	public void setVelocity(Vector speed) {
		this.speed = speed; // set the speed
	}

}