package circlesvssquares;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import pbox2d.PBox2D;
import processing.core.PApplet;

public class CirclesVsSquares extends PApplet {

    private static final long serialVersionUID = 7397694443868429500L;

    private static CirclesVsSquares instance;
    public static CirclesVsSquares instance() {
        return instance; 
    }
	
    // A reference to our box2d world
    private PBox2D box2d;

    // Just a single box this time
    Player player;

    ArrayList<Boundary> boundaries;

    @Override
    public void setup() {
        instance = this;
        size(640, 400);
        smooth();

        // Initialize box2d physics and create the world
        box2d = new PBox2D(this);
        box2d.createWorld();
	  
        // Add a listener to listen for collisions!
        box2d.world.setContactListener(new CustomListener());

        player = new Player(200, 150, box2d);

        boundaries = new ArrayList<Boundary>();
        boundaries.add(new Boundary(200, 200, 300, 25, box2d));
        boundaries.add(new Boundary(400, 300, 100, 25, box2d));
    }

    boolean[] keys = new boolean[526];

    public boolean checkKey(String k) {
        for(int i = 0; i < keys.length; i++) {
            if(KeyEvent.getKeyText(i).toLowerCase().equals(k.toLowerCase())) {
                return keys[i];
            }
        }
        return false;
    }
	 
    @Override
    public void keyPressed() { 
        keys[keyCode] = true;
    }
	 
    @Override
    public void keyReleased() { 
        keys[keyCode] = false; 
    }

    @Override
    public void draw() {
        background(255);

        if (player.canMove && checkKey("W")) {
            player.body.applyLinearImpulse(new Vec2(0, 40), player.body.getWorldCenter());
        }
        Vec2 velocity = player.body.getLinearVelocity();
        if (checkKey("D")) {
            player.body.setLinearVelocity(new Vec2(10, velocity.y));
        }
        else if (checkKey("A")) {
            player.body.setLinearVelocity(new Vec2(-10, velocity.y));
        }
        else {
            player.body.setLinearVelocity(new Vec2(0, velocity.y));
        }

        // We must always step through time!
        box2d.step();

        for (int i = boundaries.size()-1; i >=0; i--) {
            Boundary b = boundaries.get(i);
            b.display();
        }
	  
        player.display();
    }
}
