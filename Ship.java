import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List; //import java list utilities

/**
 * Ship is the superclass that controls all non-celestial bodies
 * Includes the probes, satellies, and destroyers
 * Allows bodies to shoot enemies, detect them, and remove them if necessary
 * 
 * @author Jack Ding
 * @version March 2014
 */
public abstract class Ship extends Actor {
   

    protected int shootCounter = 0; //int controls the rate at which the ships shoot 

   
    protected Planet owner; //Planet identifies which planet each ship belongs to, identifies friendlies

    protected HealthBar hp; //Healthbar hp represents the health of each ship
    protected int maxHP; //int ship's maximum possible health
    protected int currHP; //int ship's current health

    
    protected Destroyer nearestDestroyer; //the closest Destroyer to the ship
    protected int range; //range of seeking for different enemies
    
    protected double destroyerDistance; //distance of the Destroyer currently being analyzed
    protected double nearestDistanceDestroyer; //distance of the closest Destroyer so far
    
    /**
     * Constructs a ship with an assigned home base of Planet
     * 
     * @param owner     the planet that the ship belongs to
     */
    public Ship(Planet owner) {
        this.owner = owner; //assigns owner to owner
    }

    /**
     * Allows the ship to shoot enemies
     * adds a missile to the world with the set rotation and location
     */
    protected void shoot() {

        if (shootCounter >= 30) { //if shootCounter reaches 30

            Missile mA = new Missile(this.getRotation(), owner); //creates new missile at ship's rotation


            getWorld().addObject(mA, getX(), getY()); //adds object to object location

            shootCounter = 0; //sets shootCounter back to 0
        }

    }

   
    /**
     * Calculates the distance between the actor and another actor
     * 
     * @param actor     the actor whos distance to calculate to 
     * 
     * @return double   the hypotenuse between the two actors
     */
    protected double getDistance(Actor actor) {
        
        return Math.hypot(actor.getX() - getX(), actor.getY() - getY()); //returns the hypotenuse of the x and y distances
    }
    
    /**
     * Detects the collision of missiles with Ship actors
     * will remove the missile after colliding
     * will cause damage to the ship HP
     */
    protected void missileCollision() {
        Missile m = (Missile) getOneIntersectingObject(Missile.class); //creates Missile m that intersects the ship

        if (m != null && m.getOwnerMissile() != owner) { //if m exists and is unfriendly
            hp.update(-25); //cause dmg to hp by 25
             m.removeMe(); //removes missile
           
        }
    }
    
    /**
     * Checks to see if the health is satisfactory
     * if the health reach 0 remove the ship
     */
    protected void checkHealth()
    {
        if (hp.getHealth() <= 0) //if health reaches 0
        {
            removeMe(); //call removeMe() method to remove
        }
    }
    
    /**
     * removes the actor from the world
     */
    protected void removeMe() {
        getWorld().removeObject(this); //removes ship
    }
    
    /**
     * Finds a list of all Destroyers in a certain range 
     * Will utilize the getDistance() method in order to determine the closest actor
     * Used to track which enemies should be attacked
     * 
     * @param range     the desired tracking radius for Destroyers
     */
    protected void detectEnemies(int range) {
        List<Destroyer> enemies = getObjectsInRange(range, Destroyer.class); //creates list of Destroyers in range 

        if (enemies != null) { //if the list exists
            nearestDestroyer = null; //set nearestDestroyer to null

            for (int i = 0; i < enemies.size(); i++) { //cycles through all elements in the list
                destroyerDistance = getDistance(enemies.get(i)); //find the distance of the element being analyzed

                if ((enemies.get(i)).getOwner() != owner //if Destroyer is unfriendly and is closer than previous Destroyers
                        && destroyerDistance < nearestDistanceDestroyer) {
                    nearestDistanceDestroyer = destroyerDistance; //reassigns the shortest Destroyer distance

                    nearestDestroyer = enemies.get(i); //reassigns the nearest Destroyer
                }
            }

            nearestDistanceDestroyer = range; //resets nearest distance back to original range
            

        }

    }
    
    /**
     * Gets the owner in order to find out which planet is friendly and which aren't
     * 
     * @return Planet   the friendly planet 
     */
    protected Planet getOwner()
    {
        return owner; //gets the Planet owner 
    }
    
}