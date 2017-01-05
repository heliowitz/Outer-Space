import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List; //import list utilities 
import java.lang.Math; //import math functions 

/**
 * Destroyer is a Ship that is sent out to attack Planets, other destroyers and satellites which are enemies
 * Will detect all local enemies and will track and move toward them 
 * Will upgrade if necessary based on the civilization level of the planet
 *  The Destroyer will gain healing abilities 
 * 
 * @author Jack Ding
 * @version March 2014
 */
public class Destroyer extends Ship {
   
    private double nearestDistance = 1500; //closest distance of planet
    private double distance; //distance of current planet being analyzed
    private Planet nearestEnemy; //closest Planet
    
    private Satellite nearestSat; //cloest Satellite
    private double satDistance; //distance of current satellite being analyzed
    private double nearestSatDistance; //closest distance of Satellite
    
    private int healCounter = 0; //healCounter to determine healing rate 
    
    /**
     * Constructs a Destroyer with a certain health and base planet
     * 
     * @param owner     the base planet that the Destroyer comes from
     * 
     * @param maxHP     the maximum HP the Destroyer can have 
     */
    public Destroyer(Planet owner, int maxHP) {
        super(owner);
        this.maxHP = maxHP; //assigns maxHP to maxHP

        currHP = maxHP; //sets maxHP to currHP

        hp = new HealthBar(maxHP, currHP, this); //new HealthBar
        // Image
        setImage(new GreenfootImage("images/destroyer1.png")); //sets image

    }

    /**
     * adds the hp to the chosen world
     * 
     * @param w     the chosen world to add the HealthBar to
     */
    public void addedToWorld(World w) {

        w.addObject(hp, getX(), getY() - 20); //adds hp slightly above Destroyer

    }

    /**
     * Will do whatever is necessary for the Destroyer to function
     * Will manage the actions of the Destroyer once enemies are detected
     * Checks for health, collisions, upgrades, and healing
     */
    public void act() {

        healCounter++; //increase healCounter
        
        powerUp(); //call powerUp() method

        detectTargets(); //detect local Planets
        detectEnemies (50); //detect local destroyers
        detectSats(); //detect local Satellites
        
        shootCounter = shootCounter + 4; //increase shootCounter

        missileCollision(); //check for collision with Missiles
        
        
        if (nearestDestroyer != null) //if there's a local Destroyer
        {
            turnTowards (nearestDestroyer.getX(), nearestDestroyer.getY()); //turn to that Destroyer
            shoot(); //shoot 
        }
        
        else if (nearestSat != null) //if not, then if there's a local Satellite
        {
             turnTowards (nearestSat.getX(), nearestSat.getY()); //turn to that Satellite
             shoot(); //shoot 
        }
        
        else if (nearestEnemy != null) { //if not, then if there's a local Planet 
            turnTowards(nearestEnemy.getX(), nearestEnemy.getY()); //turn to that Planet

            if (getDistance(nearestEnemy) > 100) { //if distance is greater than 100
                move(2); //move towards that planet

            } 

           if (getDistance(nearestEnemy) <= 115) //if distance is within 115 range
           {
               
               shoot(); //shoot at Planet 
            }
        }
        
        checkHealth(); //check health of the Destroyer 

    }

    /**
     * detects all Planets in the range of 1500 
     * will assign closest Planet to the desired enemy
     * Will be used by Destroyer to track that planet
     */
    public void detectTargets() {
        List<Planet> enemies = getObjectsInRange(1500, Planet.class); //creates list of all Planets in range 1500

        if (enemies != null) { //if the list exists 
            nearestEnemy = null; //set nearestEnemy to null

            for (int i = 0; i < enemies.size(); i++) { //cycle through all Planets
                distance = getDistance(enemies.get(i)); //finds the distance of analyzed Planet

                if (enemies.get(i) != owner && distance < nearestDistance) { //if the Planet is the shortest distance and is not friendly
                    nearestDistance = distance; //reassign the shortest distance value

                    nearestEnemy = enemies.get(i); //reassign the closest Planet 
                }
            }

            nearestDistance = 1500; //resets the nearestDistance value to range of 1500
        }

    }
    
    /**
     * detects all Satellites in a range of 300
     * will assign the closest Satellite to the desired object
     * will be used by teh Destroyer to track that Satellite
     */
    public void detectSats()
    {
        List <Satellite> sats = getObjectsInRange (300, Satellite.class); //creates list of all Satellites in range 300
        
        if (sats != null) //if the list exists
        {
            nearestSat = null; //set nearestSat to null
            
            for (int i = 0 ; i < sats.size(); i++) //cycle through list of all Satellites 
            {
                satDistance = getDistance (sats.get(i)); //find the distance of Satellite being analyzed
                
                if (sats.get(i).getOwner() != owner && satDistance < nearestSatDistance) // if Satellite is closest so far and is not friendly
                {
                    nearestSatDistance = satDistance; //reassign shortest distance to current distance
                    
                    nearestSat = sats.get(i); //reassign closest Satellite value
                }
            }
            
            nearestSatDistance = 300; //reset nearestSatDistance value back to 300 default 
        }
    }
    
    /**
     * Controls if the Destroyer should be upgraded based on the civlization level of the base planet
     * Will give the Destroyer the ability to heal
     * Will change the appearance of the Destroyer 
     */
    public void powerUp()
    {
        if (owner.getCivStage() >= 3) //if the planet's base civilization is 3 or higher
        {
            
            hp.update(maxHP); //recharge the HP to maximum
            
            if (healCounter >= 250) //if the healCounter is over 30
            {
                hp.update(1); //increase the HP by 1
                healCounter = 0; //set healcounter back to 0
            }
            
            setImage(new GreenfootImage("images/destroyer2.png")); //sets image to upgraded destroyer image 
        }
    }

}