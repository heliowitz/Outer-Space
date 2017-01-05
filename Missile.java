import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Missile is a class that is used in order to allow ships to attack either planets or each other
 * Will also check to see if missiles hit planets
 * Missile Collision Detection for other ships is contained in the Ship class / subclasses
 * 
 * 
 * @author Jack Ding
 * @version March 2014
 */
public class Missile extends Actor {
    
    private int angle; //angle of the missile
    private Planet owner; //the Planet that is friendly to the missile
    private boolean remove = false; //boolean if missile should be removed

    /**
     * Constructs a missle
     * 
     * @param rotation      the specific rotation of the actor that the missile belongs to
     * @param owner         the specific planet that the missile will not affect as it is friendly
     */
    public Missile(int rotation, Planet owner) {
        this.angle = rotation; //assigns rotation parameter to angle

        this.owner = owner; //assigns owner parameter to owner
        // Set image.
        setImage(new GreenfootImage("images/bullet.png")); //sets the image 
    }

    /**
     * Act will do whatever the Missile wants to do
     * Will set the rotation to the correct angle 
     * Will check for the collision with unfriendly planets
     * Will remove if necessary from the world
     */
    public void act() {
   
        setRotation(angle); //sets to rotation to angle

        move(5); //moves the missile foward in the direction its facing

        checkPlanetCollision(); //calls checkPlanetCollision method to see if it hits a planet
        checkRid(); //calls checkRid method to see if it should be removed 
        

    }

    /**
     * Checks to see if the Missile is at the edge of the world
     * Will arrange to have the missile removed from the world
     * 
     * @return boolean      if missile is at the world or not
     */
    public boolean atWorldEdge() {
        int maxX = getWorld().getBackground().getWidth(); //set integer maxX to width of background
        int maxY = getWorld().getBackground().getHeight(); //set integer maxY to height of background
        if (getX() <= 0 || getX() >= maxX - 1) { //if coordinate is outside width
            return true; //return as true
        }
        if (getY() <= 0 || getY() >= maxY - 1) { //if coordinate is outside height
            return true; //return as true
        }
        return false; //otherwise return as false

    }

    /**
     * Removes the missile from the world
     */
    public void removeMe() {
        getWorld().removeObject(this); //removes the missile
    }

    /**
     * if atWorldEdge() returns as true, arranges to set remove to true 
     * will eventually have missile from the world
     */
    public void checkRid() {
        if (atWorldEdge()) { //if its at the world edge
            remove = true; //set remove to true
        }
    }
    
    /**
     * Gets the friendly planet so that missiles wont harm friendlies
     * 
     * @return Planet   the friendly planet
     */
    public Planet getOwnerMissile()
    {
        return owner; //returns the owner
    }
    
    /**
     * Will check to see if the missile collides with an unfriendly Planet
     * If so, will cause planet damage 
     * Will also set remove to true to have it removed from world
     */
    public void checkPlanetCollision()
    {
        Planet p = (Planet) getOneIntersectingObject (Planet.class); //creates a Planet that intersects the missile
        
        if (p !=null && p != owner) //if p exists and if p isn't friendly
        {
            p.attack(-30); //attack the planet with 30 dmg
            remove = true; //set remove to true
           }
    }

}