package circlesvssquares;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import pbox2d.PBox2D;

import processing.core.PApplet;

public class CirclesVsSquares extends PApplet {

    private static final long serialVersionUID = 7397694443868429500L;

    private static final float WORLD_GRAVITY = -50;

    private static CirclesVsSquares instance;
    public static CirclesVsSquares instance() {
        return instance; 
    }
    
    public static void main(String[] args) {
        PApplet.main(new String[] { CirclesVsSquares.class.getName() });
    }
	
    // A reference to our box2d world
    private PBox2D box2d;

    // Just a single box this time
    Player player;

    ArrayList<Ground> boundaries;

    @Override
    public void setup() {
        instance = this;
        size(640, 400);
        smooth();

        // Initialize box2d physics and create the world
        box2d = new PBox2D(this);
        box2d.createWorld();
        box2d.setGravity(0, WORLD_GRAVITY);
	  
        // Add a listener to listen for collisions!
        box2d.world.setContactListener(new CustomListener());

        player = new Player(200, 150, box2d);

        boundaries = new ArrayList<Ground>();
        boundaries.add(new Ground(200, 200, 300, 25, box2d));
        boundaries.add(new Ground(400, 300, 100, 25, box2d));
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

        // Move the player if movement keys are held down
        Player.MovementDirection direction = Player.MovementDirection.NONE;
        if (checkKey("D")) {
            direction = Player.MovementDirection.RIGHT;
        } else if (checkKey("A")) {
            direction = Player.MovementDirection.LEFT;
        }
        player.movePlayer(direction);

        // Attempt to jump if the jump key is held down
        if (checkKey("W")) {
            player.jumpIfPossible();
        }

        // Step the physics simulation
        box2d.step();

        for (int i = boundaries.size()-1; i >=0; i--) {
            Ground b = boundaries.get(i);
            b.display();
        }
	  
        player.display();
    }
}
