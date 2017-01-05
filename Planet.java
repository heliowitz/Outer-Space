import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import greenfoot.*;

/**
 * Planet is a CelestialBody that orbits a Star and potentially holds a
 * civilization. Note that this Actor must orbit a Star upon being added to the
 * world, and thus will ignore assigned positions. Multiple instances may be
 * created.
 * <p>
 * By Kardashev Type I, Probes and Satellites are developed. By Type II,
 * Destroyers are developed. By Type III, shielding is developed. By Type IV,
 * planetary thrusters are developed and the civilization gains independence
 * from the system, ascending to other systems to escape the inevitable collapse
 * of the local sun.
 * 
 * @author Teddy Zhu
 * @version Mar. 5, 2014
 */
public class Planet extends CelestialBody {

	// INSTANCE VARIABLES--------------------------------------------------

	// Resources relating to civilization advance, surrounding shielding, and
	// physical health.
	private long resource, shield, maxShield, health, maxHealth;
	// Evolution of life and Kardashev scale.
	private int evolution, maxEvo, civStage;
	// Flag for having fully evolved life, and one for whether the planet can
	// develop life at all.
	private boolean hasCiv, allowLife;
	// Flags for Planetary motion; whether the Planet can move independently,
	// whether it is affected by gravity, and whether it has left the system.
	private boolean allowFreedom, allowGrav, ascended;
	// Name of the planet.
	private String name;
	// Assets associated with this Planet.
	private ArrayList<Satellite> satellites;
	private ArrayList<Destroyer> destroyers;
	private ArrayList<Probe> probes;
	private Moon[] moons;
	private Star sun;
	private Log status;
	private Glow glow;
	// Universe.
	SolSystem world;
	// Orbital path data.
	// Orbital variation for each planet. Angular position on the orbital path
	// and its rate of change, and angular orientation of the path itself and
	// its rate of change, along with radii of the orbital path ellipse.
	private double orbitPos, orbitSpeed, pathPos, pathSpeed, orbitRadX,
			orbitRadY;
	// Counter for periodic activity.
	private long counter = 0;
	// Health and Shielding bar.
	private StatusSet bars;

	// CONSTANTS-----------------------------------------------------------

	// Possible Planet display images.
	private static final GreenfootImage[] IMGS;
	// Resource way-points that mark a Kardashev scale advancement. Every number
	// is the amount of resource needed to advance from the stage of its index.
	private static final long[] STAGE_RESOURCE_FLAGS = new long[] { 2500,
			22000, 80000, 260000 };
	static {
		IMGS = new GreenfootImage[] { new GreenfootImage("images/planet1.gif"),
				new GreenfootImage("images/planet2.gif"),
				new GreenfootImage("images/planet3.gif"),
				new GreenfootImage("images/planet4.gif"),
				new GreenfootImage("images/planet5.gif") };
	}

	// CONSTRUCTORS-------------------------------------------------------

	/**
	 * Constructs a Planet, with an circular orbit around the given Star.
	 * 
	 * @param sun
	 *            The sun of the system the Planet belongs to.
	 * @param m
	 *            The mass of this Planet.
	 * @param supportsLife
	 *            Whether the Planet is capable of developing a civilization.
	 */
	public Planet(Star sun, Double m, Boolean supportsLife) {
		// Random image.
		setImage(IMGS[Greenfoot.getRandomNumber(IMGS.length)]);
		// Highlighting effect.
		glow = new Glow(this, getImage().getWidth() * 1.5f);
		// Statistics.
		resource = 100;
		maxShield = 0;
		shield = maxShield;
		// Mass dependent formula.
		maxHealth = (long) (Math.log(m) / Math.log(1.006));
		health = maxHealth;
		evolution = 0;
		// Random evolution limit.
		maxEvo = Greenfoot.getRandomNumber(800) + 700;
		// -1 marks un-evolved.
		civStage = -1;
		hasCiv = false;
		// Motion is constrained to a mathematical orbit.
		allowFreedom = false;
		allowGrav = false;
		ascended = false;
		mass = m;
		allowLife = supportsLife;
		name = generateName();
		// Asset reference lists.
		satellites = new ArrayList<Satellite>(0);
		destroyers = new ArrayList<Destroyer>(0);
		probes = new ArrayList<Probe>(0);
		moons = new Moon[0];
		this.sun = sun;
		// Set up GUI objects.
		bars = getNewBar("health");
		status = new Log();
		// Orbit, default circular.
		orbitPos = 0;
		orbitSpeed = 0.5;
		pathPos = 0;
		pathSpeed = 0;
		orbitRadX = 100;
		orbitRadY = 100;
	}

	/**
	 * Constructs a Planet with a certain number of Moons, with a circular orbit
	 * around the given Star.
	 * 
	 * @param sun
	 *            The sun of the system the Planet belongs to.
	 * @param numMoons
	 *            The number of Moons the Planet should own.
	 * @param m
	 *            The mass of this Planet.
	 * @param supportsLife
	 *            Whether the Planet is capable of developing a civilization.
	 */
	public Planet(Star sun, Integer numMoons, Double m, Boolean supportsLife) {
		this(sun, m, supportsLife);
		moons = new Moon[numMoons];
	}

	// INTERFACE----------------------------------------------------

	/**
	 * Sets up the orbital behavior of this planet around the sun.
	 * 
	 * @param orbitPos
	 *            The initial angular position of the planet on the path.
	 * @param orbitSpeed
	 *            How quickly the planet orbits.
	 * @param pathPos
	 *            The initial angular orientation of the path itself.
	 * @param pathSpeed
	 *            How quickly the path rotates.
	 * @param orbitRadX
	 *            The horizontal radius of the path. Negatives are ignored.
	 * @param orbitRadY
	 *            The vertical radius of the path. Negatives are ignored.
	 */
	public void setOrbit(double orbitPos, double orbitSpeed, double pathPos,
			double pathSpeed, double orbitRadX, double orbitRadY) {
		// Position and orientations are adjusted to equivalent angles within
		// 360 degrees.
		this.orbitPos = Helper.correctToRangeWithExcess(orbitPos, 0.0, 360.0);
		this.orbitSpeed = orbitSpeed;
		this.pathPos = Helper.correctToRangeWithExcess(pathPos, 0.0, 360.0);
		this.pathSpeed = pathSpeed;
		// Magnitudes taken.
		this.orbitRadX = Math.abs(orbitRadX);
		this.orbitRadY = Math.abs(orbitRadY);
		// Set position to point on orbital path.
		orbit(this, sun, this.orbitPos, this.orbitRadX, this.orbitRadY,
				this.pathPos);
	}

	/**
	 * Retrieves the resource of the Planet's civilization.
	 * 
	 * @return long The resource of this Planet's civilization.
	 */
	public long getResource() {
		return resource;
	}

	/**
	 * Changes the resource of the civilization on this Planet by the given
	 * amount.
	 * 
	 * @param change
	 *            The amount of change desired, as a long.
	 */
	public void changeResource(long change) {
		resource += change;
		// No upper limit, but cannot be negative.
		if (resource < 0) {
			resource = 0;
		}
	}

	/**
	 * Retrieves the Planet's shields.
	 * 
	 * @return long The current shielding.
	 */
	public long getShield() {
		return shield;
	}

	/**
	 * Changes the shielding by the given amount.
	 * 
	 * @param change
	 *            The amount of change desired, as a long.
	 */
	public void changeShield(long change) {
		shield += change;
		shield = Helper.correctToRange(shield, 0, maxShield);
	}

	/**
	 * Retrieves the maximum shield capacity.
	 * 
	 * @return long The maximum shielding.
	 */
	public long getMaxShield() {
		return maxShield;
	}

	/**
	 * Sets the maximum shield capacity.
	 * 
	 * @param max
	 *            The new maximum, as a long. Negatives are ignored.
	 */
	public void setMaxShield(long max) {
		maxShield = Math.abs(max);
	}

	/**
	 * Retrieves the current health.
	 * 
	 * @return long Current health.
	 */
	public long getHealth() {
		return health;
	}

	/**
	 * Changes the health by the given amount.
	 * 
	 * @param change
	 *            The amount of change desired, as a long.
	 */
	public void changeHealth(long change) {
		health += change;
		health = Helper.correctToRange(health, 0, maxHealth);
	}

	/**
	 * Retrieves the maximum health.
	 * 
	 * @return long Maximum health.
	 */
	public long getMaxHealth() {
		return maxHealth;
	}

	/**
	 * Sets the maximum health.
	 * 
	 * @param max
	 *            The new maximum. Negatives are ignored.
	 */
	public void setMaxHealth(long max) {
		maxHealth = Math.abs(max);
	}

	/**
	 * Retrieves the current evolution progress.
	 * 
	 * @return int Current evolution.
	 */
	public int getEvo() {
		return evolution;
	}

	/**
	 * Changes the current evolution progress by the given amount.
	 * 
	 * @param change
	 *            The amount of change desired.
	 */
	public void changeEvo(int change) {
		evolution += change;
		evolution = (int) Helper.correctToRange(evolution, 0, maxEvo);
	}

	/**
	 * Retrieves the maximum evolution; the amount needed to develop
	 * civilization.
	 * 
	 * @return int Maximum evolution.
	 */
	public int getMaxEvo() {
		return maxEvo;
	}

	/**
	 * Sets the maximum evolution needed to develop civilization.
	 * 
	 * @param max
	 *            The new maximum. Negatives are ignored.
	 */
	public void setMaxEvo(int max) {
		maxEvo = Math.abs(max);
	}

	/**
	 * Checks if a civilization has evolved on this planet.
	 * 
	 * @return boolean True if a civilization has evolved on the Planet,
	 *         otherwise false.
	 */
	public boolean hasCiv() {
		return hasCiv;
	}

	/**
	 * Retrieves the Kardashev civilization stage.
	 * 
	 * @return int Civilization stage. Between -1 and 4 inclusive. -1 indicates
	 *         no civilization.
	 */
	public int getCivStage() {
		return civStage;
	}

	/**
	 * Damages the Planet physically, accounting for shield and health. Any
	 * attack on a Planet should call this.
	 * 
	 * @param dmg
	 *            Amount of damage. Negatives are ignored.
	 */
	public void attack(long dmg) {
		shield -= Math.abs(dmg);
		if (shield < 0) {
			health -= -shield;
			shield = 0;
		}
	}

	/**
	 * Retrieves the Planet's name.
	 * 
	 * @return String Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Decreases the resource by a given amount.
	 * 
	 * @param exploit
	 *            Magnitude of decrease.
	 * @return long The amount of resource remaining.
	 */
	public long exploit(long exploit) {
		changeResource(-Math.abs(exploit));
		return resource;
	}

	/**
	 * Shows the status log of this Planet and highlights the Planet.
	 */
	public void showLog() {
		highlight();
		status.show();
	}

	/**
	 * Hides the status log of this Planet and un-highlights the Planet.
	 */
	public void hideLog() {
		unhighlight();
		status.hide();
	}

	/**
	 * Updates the Planet's status log with all appropriate data.
	 */
	public void updateLog() {
		// Roman numerals to be shown, corresponding to index.
		String[] romanNum = new String[] { "0", "I", "II", "III", "IV", "V",
				"VI", "VII", "VIII", "IX", "X" };
		// Text representing civStage.
		String kardashevType;
		// If the Planet hasn't been removed;
		if (getWorld() != null) {
			// If there is a civilization with a valid stage;
			if (civStage >= 0 && civStage < romanNum.length) {
				// Output Roman numeral of stage.
				kardashevType = "Kardashev Type " + romanNum[civStage];
			} else {
				// Otherwise it's stage -1 and has no civilization.
				kardashevType = "Unevolved";
			}
			// Output the full report.
			status.update(name + ": ", "Evolved: " + evolution + "/" + maxEvo,
					resource + " EP", kardashevType, "Health: " + health + "/"
							+ maxHealth + " HP", "Shielding: " + shield + "/"
							+ maxShield);
			// If the Planet has been removed,
		} else {
			// Check if it has ascended and respond accordingly.
			if (ascended) {
				status.update("Ascended beyond system.");
				// If it did not ascend, it was destroyed somehow.
			} else {
				status.update("Destroyed.");
			}
		}
	}

	/**
	 * A one-time call to break the mathematically fixed orbit of the Planet and
	 * leave it susceptible to gravity. Generally called to allow it to spiral
	 * into the sun. Will do nothing if orbit is already broken.
	 */
	public void breakOrbit() {
		if (!allowGrav) {
			allowGrav = true;
			// Initial velocity is tangential to current orbital path, for
			// smooth transition.
			setVelocity(getOrbitalTangent(5));
		}
	}

	/**
	 * Destroys the Planet and all of its owned assets, removing them from the
	 * World.
	 */
	@SuppressWarnings("unchecked")
	public void destroy() {
		// Removing Ships, Moons, and health/shielding bars.
		satellites = (ArrayList<Satellite>) Helper.clearReferences(satellites);
		destroyers = (ArrayList<Destroyer>) Helper.clearReferences(destroyers);
		probes = (ArrayList<Probe>) Helper.clearReferences(probes);
		for (int i = 0; i < moons.length; i++) {
			world.removeObject(moons[i]);
		}
		world.removeObject(bars);
		// Removing self.
		if (getWorld() != null) {
			world.removeObject(this);
		}
	}

	// PRIVATE METHODS-----------------------------------------------

	/**
	 * Retrieves a velocity vector with the given magnitude tangential to the
	 * current point on the mathematical orbit. Should only be called once per
	 * Planet.
	 * 
	 * @param magnitude
	 *            Magnitude of Vector.
	 * @return Vector Tangential velocity.
	 */
	private Vector getOrbitalTangent(double magnitude) {
		// Set up vector and magnitude.
		Vector tangent = new Vector();
		tangent.setMagnitude(Math.abs(magnitude));
		// Record the current position on orbit.
		int prevX = getX();
		int prevY = getY();
		// Set position to an arbitrarily small distance away on the orbital
		// course.
		// Angular magnitude is 2 and signum allows the current orbiting
		// direction to be maintained.
		orbit(this, sun, orbitPos + 2 * Math.signum(orbitSpeed), orbitRadX,
				orbitRadY, pathPos);
		// Create a Vector from the linear direction between the previous
		// position and the current one.
		tangent.setDirection(Helper.getAngle(prevX, prevY, getX(), getY()));
		return tangent;
	}

	/**
	 * Sets the position of an Actor to some point on an elliptical orbital path
	 * around a source. Used to create orbital motion by being called in an
	 * act() method with a changing degree angle position.
	 * 
	 * @param obj
	 *            The orbiting Actor.
	 * @param src
	 *            The source Actor to be orbited.
	 * @param ellipseDeg
	 *            The angular position, in degrees, of the Planet on the path,
	 *            to be set.
	 * @param radX
	 *            The horizontal radius.
	 * @param radY
	 *            The vertical radius.
	 * @param pathRotation
	 *            The angular orientation, in degrees, of the orbital path, to
	 *            be set.
	 */
	private void orbit(Actor obj, Actor src, double ellipseDeg, double radX,
			double radY, double pathRotation) {
		// Convert degrees into radian measure for trigonometric functions.
		double radian = (ellipseDeg * Math.PI / 180.0);
		double rotation = (pathRotation * Math.PI / 180.0);
		// Coordinates of the specified point on the orbital path.
		double coordX = (src.getX() + (Math.cos(radian) * radX));
		double coordY = (src.getY() + (Math.sin(radian) * radY));
		// Rotate this point about the source to newly transformed coordinates,
		// which rotates the orbital path.
		double coordXRotated = (coordX - src.getX()) * Math.cos(rotation)
				- (coordY - src.getY()) * Math.sin(rotation) + src.getX();
		double coordYRotated = (coordX - src.getX()) * Math.sin(rotation)
				+ (coordY - src.getY()) * Math.cos(rotation) + src.getY();
		// Set the location.
		obj.setLocation((int) Math.round(coordXRotated),
				(int) Math.round(coordYRotated));
	}

	/**
	 * Moves the Planet in a gradually rotating orbital about the sun. If
	 * permitted, allows gravity effects or independent motion.
	 */
	private void move() {
		// If independent, the speed Vector will have been set already as a
		// constant linear velocity away from system, so follow that as the only
		// behavior.
		this.getImage().rotate(2);
		if (allowFreedom) {
			setLocation(getX() + speed.getX(), getY() + speed.getY());
			// Independence overrides all other behaviors.
			// Here, if orbit is broken and gravity is on, leave physics
			// behaviors to superclass engine.
			
		} else if (allowGrav) {
			super.act();
			// Otherwise, orbit mathematically as usual.
		} else {
			// Orbit about the sun.
			orbit(this, sun, orbitPos, orbitRadX, orbitRadY, pathPos);
			// Change orbital position and path rotation by set speed.
			orbitPos += orbitSpeed;
			pathPos += pathSpeed;
			// Constrain degree angles to within a full circle.
			orbitPos = Helper.correctToRangeWithExcess(orbitPos, 0.0, 360.0);
			pathPos = Helper.correctToRangeWithExcess(pathPos, 0.0, 360.0);
		}
	}

	/**
	 * Generates a Planet name composed of three letters and four numbers
	 * separated by a dash.
	 * 
	 * @return String The Planet name.
	 */
	private String generateName() {
		String name = "";
		// Name is three random letters and a four digit number separated by a
		// dash. ASCII codes are used for characters.
		for (int i = 0; i < 3; i++) {
			// Letters.
			name += Character
					.toString((char) (Greenfoot.getRandomNumber(26) + 65));
		}
		// Dash.
		name += Character.toString('-');
		for (int i = 0; i < 4; i++) {
			// Numbers.
			name += Character
					.toString((char) (Greenfoot.getRandomNumber(10) + 48));
		}
		return name;
	}

	/**
	 * Evolves life towards basic civilization for habitable Planets.
	 */
	private void evolveLife() {
		// If civilization hasn't developed and is able to;
		if (!hasCiv && allowLife) {
			// If evolution has completed then;
			if (evolution >= maxEvo) {
				// Civilization has developed to stage zero.
				evolution = maxEvo;
				hasCiv = true;
				civStage = 0;
				world.prompt(name + " has developed life.");
				// Otherwise continue evolving.
			} else {
				evolution++;
			}
		}
	}

	/**
	 * Checks if resource checkpoints have been reached for stage increases.
	 */
	private void checkStageUp() {
		// If a civilization is present and has not ascended beyond the solar
		// system, check for increase or decrease.
		if (hasCiv && civStage < STAGE_RESOURCE_FLAGS.length) {
			// Check if resource is high enough, meeting that needed for the
			// current level to level up, to warrant a stage increase.
			if (resource >= STAGE_RESOURCE_FLAGS[civStage]) {
				civStage++;
				world.prompt(name + " is now Stage " + civStage + ".");
				// In the event that, for whatever reason, resource falls below
				// that required for the previous stage to level up to the
				// current, decrease stage. Evolutionary stage -1 cannot be
				// decreased to, only increased from.
			} else if (civStage > 0
					&& resource < STAGE_RESOURCE_FLAGS[civStage - 1]) {
				civStage--;
				world.prompt(name + " has been sent back to Stage " + civStage
						+ ".");
			}
		}
	}

	/**
	 * Spawns a Ship of the specified type at this Planet.
	 * 
	 * @param ship
	 *            The Class of the Ship needed. If any other Class is passed,
	 *            nothing will happen.
	 */
	private void spawnShip(Class<? extends Ship> ship) {
		// If Probe is either the same as, or is a superclass of, the passed
		// Class parameter;
		if (Probe.class.isAssignableFrom(ship)) {
			// Add a new Probe to the reference list and;
			probes.add(new Probe(this, 120 * civStage));
			// This most recent addition must be the final element and have
			// index size-1 because the references are trimmed down to clear
			// off all empty spaces every act() iteration. Thus the most
			// recent addition must have expanded the size.
			// Now add it to the world.
			world.addObject(probes.get(probes.size() - 1), getX(), getY());
		}
		// Do the same with remaining possibilities.
		else if (Satellite.class.isAssignableFrom(ship)) {
			satellites.add(new Satellite(this, 500 * civStage));
			world.addObject(satellites.get(satellites.size() - 1), getX(),
					getY());
		} else if (Destroyer.class.isAssignableFrom(ship)) {
			destroyers.add(new Destroyer(this, 350 * civStage));
			world.addObject(destroyers.get(destroyers.size() - 1), getX(),
					getY());
		}
	}

	/**
	 * Cleans up the reference lists by removing all destroyed Ships and
	 * shortening the list to remove empty spots.
	 */
	@SuppressWarnings("unchecked")
	private void updateRefs() {
		probes = (ArrayList<Probe>) Helper
				.cleanReferences((ArrayList<? extends Actor>) probes);
		satellites = (ArrayList<Satellite>) Helper
				.cleanReferences((ArrayList<? extends Actor>) satellites);
		destroyers = (ArrayList<Destroyer>) Helper
				.cleanReferences((ArrayList<? extends Actor>) destroyers);
	}

	/**
	 * Checks if certain stages have been reached by the civilization, and
	 * activates the skills assigned to these stages.
	 */
	private void checkAndActivateSkills() {
		if (hasCiv) {
			// Needed to coordinate periodic activities.
			counter++;
			// For a minimum of stage 1; every 500 frames;
			if (civStage >= 1 && counter % 500 == 0) {
				// Maximum number of this Ship;
				if (probes.size() < civStage * 3) {
					spawnShip(Probe.class);
				}
				if (satellites.size() < civStage + 1) {
					spawnShip(Satellite.class);
				}
			}
			// For a minimum of stage 2; every 700 frames;
			if (civStage >= 2 && counter % 700 == 0) {
				// Maximum number.
				if (destroyers.size() < civStage * 2) {
					spawnShip(Destroyer.class);
				}
				// Reset counter as this condition has highest modulus.
				counter = 0;
			}
			// For a minimum of stage 3;
			// If there is no shield;
			if (civStage >= 3 && maxShield == 0) {
				// Set up a shield. One-time run.
				setMaxShield((long) (20 * Math.sqrt(resource)) + 50L);
				shield = maxShield;
				// Create a new set of bars with shielding showing.
				world.removeObject(bars);
				bars = getNewBar("shield");
				bars.toggleGradient(0, true);
				bars.toggleGradient(1, true);
				world.addObject(bars, getX(), getY());
			}
			// For a minimum of stage 4; one-time run.
			if (civStage >= 4 && !allowFreedom) {
				// Become independent.
				allowFreedom = true;
				// If gravity is active, there is no orbit to get a tangent from
				// so;
				if (allowGrav) {
					// Set the speed to aim towards the sun and;
					setVelocity(new Vector(5, Helper.getAngle(getX(), getY(),
							sun.getX(), sun.getY()), false));
					// Reverse it to turn away.
					speed.reverseDirection();
				} else {
					// Otherwise get a tangential velocity and use that.
					setVelocity(getOrbitalTangent(5));
				}
			}
		} else {
			// Uncivilized Planets need no counter.
			counter = 0;
		}
	}

	/**
	 * Increases resource gradually over time and regenerates health or shield
	 * as appropriate.
	 */
	private void growAndRegen() {
		// Passive growth and replenishing of resources.
		if (hasCiv) {
			// Growth bonus to inhabited planets.
			resource += Math.pow(4, civStage + 1);
			// Healing allowed.
			changeHealth(5);
		} else {
			// Slow growth for uncivilized planets.
			resource++;
		}
		// Healing shield allowed as soon as one is available.
		changeShield(1);
	}

	/**
	 * Leeches resource from lower-level civilizations within proximity.
	 */
	@SuppressWarnings("unchecked")
	private void graze() {
		// If this civilization is at least stage 1;
		if (hasCiv && civStage >= 1) {
			// Collect all planets within a certain range that is dependent on
			// amount of resource.
			ArrayList<Planet> planets = new ArrayList<Planet>(
					getObjectsInRange(
							(int) Math.ceil(20 * Math.log10(resource)),
							Planet.class));
			// If some are found,
			if (planets.size() > 0) {
				// Go through the list and,
				for (Planet p : planets) {
					// Only if this civilization is higher level,
					if (civStage > p.getCivStage()) {
						// Calculate the magnitude of the rate of transfer per
						// act() iteration.
						long transfer = (long) Math.pow(2,
								Math.abs(civStage - p.getCivStage()));
						// Transfer resource to self.
						resource += transfer;
						// Drain other planet.
						p.changeResource(-transfer);
					}
				}
			}
		}
	}

	/**
	 * Checks for destruction of life or the Planet when health is zero.
	 */
	@SuppressWarnings("unchecked")
	private void checkDeath() {
		if (health <= 0) {
			// For both cases, remove all ships.
			satellites = (ArrayList<Satellite>) Helper
					.clearReferences(satellites);
			destroyers = (ArrayList<Destroyer>) Helper
					.clearReferences(destroyers);
			probes = (ArrayList<Probe>) Helper.clearReferences(probes);
			if (hasCiv) {
				// When wiping out civilization, send it back to evolution
				// stages.
				maxShield = 0;
				shield = maxShield;
				maxHealth = (long) (Math.log(mass) / Math.log(1.006));
				health = maxHealth;
				evolution = 0;
				maxEvo = Greenfoot.getRandomNumber(800) + 700;
				civStage = -1;
				hasCiv = false;
				// Cut down current resource.
				resource /= 3;
				counter = 0;
				// Reset bars to only show health and not shield.
				world.removeObject(bars);
				bars = getNewBar("health");
				bars.toggleGradient(0, true);
				world.addObject(bars, getX(), getY());
				world.prompt("All life was wiped out on " + name + ".");
			} else {
				// If health hits zero on one with no life, destroy the Planet
				// and create an asteroid explosion.
				world.spawnAsteroidsAt(getX(), getY());
				world.prompt(name + " was destroyed.");
				destroy();
				return;
			}
		}
	}

	/**
	 * Checks if Stage Four was reached and Planet has left the system and acts
	 * appropriately.
	 */
	private void checkAscension() {
		// If on course to leave and has reached a distance of 1000 away from
		// sun off screen,
		if (civStage >= 4 && getDistance(this, sun) >= 1000) {
			// Note ascension and remove everything.
			ascended = true;
			world.prompt(name + " has ascended beyond the system.");
			destroy();
		}
	}

	/**
	 * Updates status bars with status.
	 */
	private void linkBars() {
		// Follow.
		bars.setLocation(getX(), getY());
		// Health, as well as shield if it exists.
		bars.setMax(0, maxHealth);
		bars.updateTo(0, health);
		if (maxShield > 0) {
			bars.setMax(1, maxShield);
			bars.updateTo(1, shield);
		}
	}

	/**
	 * Retrieves a new set of status bars containing either just health or both
	 * health and shield.
	 * 
	 * @param type
	 *            Either "health" or "shield" for the respective bar set.
	 * @return StatusSet The bars needed, null if type was invalid.
	 */
	private StatusSet getNewBar(String type) {
		if (type.toLowerCase().equals("health")) {
			// Health bar only.
			return new StatusSet(new double[] { health },
					new double[] { maxHealth },
					(int) (getImage().getWidth() * 0.6), new double[] { 5 },
					new Color[] { new Color(53, 237, 11, 210) },
					new Color[] { new Color(237, 33, 70, 80) });
		} else if (type.toLowerCase().equals("shield")) {
			// Health and shield bars.
			return new StatusSet(new double[] { health, shield }, new double[] {
					maxHealth, maxShield },
					(int) (getImage().getWidth() * 0.6), new double[] { 5, 5 },
					new Color[] { new Color(53, 237, 11, 210),
							new Color(51, 123, 224, 210) }, new Color[] {
							new Color(237, 33, 70, 80),
							new Color(32, 134, 147, 80) });
		}
		return null;
	}

	/**
	 * Highlights the Planet to show it is selected.
	 */
	private void highlight() {
		// If Planet exists and glow does not, add glow.
		if (getWorld() != null && glow.getWorld() == null) {
			world.addObject(glow, getX(), getY());
		}
	}

	/**
	 * Un-highlights the selected Planet.
	 */
	private void unhighlight() {
		// If Planet exists and glow does, remove glow.
		if (getWorld() != null && glow.getWorld() != null) {
			world.removeObject(glow);
		}
	}

	// GREENFOOT STRUCTURES------------------------------------------

	/**
	 * Runs every frame; standard activities.
	 */
	public void act() {
		evolveLife();
		checkStageUp();
		growAndRegen();
		updateRefs();
		move();
		graze();
		checkAndActivateSkills();
		linkBars();
		checkAscension();
		checkDeath();
	}

	/**
	 * Initializes owned assets with self.
	 */
	public void addedToWorld(World w) {
		// Retrieve world reference for ease of use.
		world = (SolSystem) getWorld();
		// Set position to point on orbital path.
		orbit(this, sun, orbitPos, orbitRadX, orbitRadY, pathPos);
		// Add status log with self.
		world.addObject(status, 820, 520);
		status.hide();
		// Add moons with self. The size will have been setup by the
		// constructor.
		for (int i = 0; i < moons.length; i++) {
			moons[i] = new Moon(10000, 1e10, this, (i + 1) * 30);
			world.addObject(moons[i], getX(), getY());
		}
		// Add bars.
		bars.toggleGradient(0, true);
		world.addObject(bars, getX(), getY());
	}

	/**
	 * Glow is an Actor intended solely for use by Planet to indicate which
	 * Planet is currently selected when the Planet's status log is called up.
	 * It highlights the Planet with a yellow glow. Granted a separate layer by
	 * SolSystem. Nested class, multiple instances may be created.
	 * 
	 * @author Teddy Zhu
	 * @version Mar. 22, 2014
	 */
	public class Glow extends Actor {

		// Display image for glow effect.
		private GreenfootImage display;
		// Actor to be followed, should be Planet.
		private Actor target;

		/**
		 * Constructs a yellow Glow effect of given radius that follows the
		 * given Actor.
		 * 
		 * @param target
		 *            Actor to be followed.
		 * @param rad
		 *            Glow radius.
		 */
		public Glow(Actor target, float rad) {
			this.target = target;
			// Set bounding image for gradient.
			display = new GreenfootImage((int) Math.ceil(2f * rad),
					(int) Math.ceil(2f * rad));
			// Get drawing materials and canvas.
			Graphics2D img = display.getAwtImage().createGraphics();
			// Make gradient.
			RadialGradientPaint grad = new RadialGradientPaint(
			// Coordinates of center.
					rad, rad,
					// Bounding radius, outer.
					rad,
					// Key-frame radius positions as a proportion of
					// bounding radius. First color is at inner radius,
					// second at outer.
					new float[] { 0f, 1.0f },
					// Colors to be interpolated between for gradient.
					new Color[] { new Color(247, 247, 89, 255),
							new Color(247, 247, 89, 0) });
			// Use gradient.
			img.setPaint(grad);
			// Fill in a circle of this gradient.
			img.fill(new Ellipse2D.Double(0, 0, 2f * rad, 2f * rad));
			// Clear resources.
			img.dispose();
			setImage(display);
		}

		/**
		 * Runs every frame. Follows Actor target.
		 */
		public void act() {
			// If an Actor was provided and is not removed, follow it.
			if (target != null && target.getWorld() != null) {
				setLocation(target.getX(), target.getY());
			} else {
				// Otherwise remove it.
				getWorld().removeObject(this);
			}
		}
	}
}
