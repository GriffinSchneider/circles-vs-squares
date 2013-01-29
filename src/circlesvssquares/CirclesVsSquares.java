package circlesvssquares;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import pbox2d.PBox2D;
import processing.core.PApplet;

public class CirclesVsSquares extends PApplet {

    private static final long serialVersionUID = 7397694443868429500L;

    private static final float WORLD_GRAVITY = -50;
    private static final boolean DEBUG = true;

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

    ArrayList<Node> objectList;
    ArrayList<Node> toRemoveList;
    
    float zoom = 1;

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

        if (DEBUG) {
            addMouseWheelListener(new MouseWheelListener() { 
                public void mouseWheelMoved(MouseWheelEvent mwe) { 
                    mouseWheel(mwe.getWheelRotation());
                }}); 
        }
        
        Vec2 playerPos = new Vec2(200, 150);
        player = new Player(playerPos, box2d);

        toRemoveList = new ArrayList<Node>();
        objectList = new ArrayList<Node>();
        objectList.add(new Ground(200, 200, 300, 25, box2d));
        objectList.add(new Ground(400, 300, 100, 25, box2d));
        
        Vec2 enemyPos = box2d.coordPixelsToWorld( new Vec2(400, 250) );
        objectList.add(new Enemy(enemyPos, 25, 25, box2d));
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
    
    void mouseWheel(int delta_) {
        float delta = delta_;
        zoom += delta / 10;
        if (zoom <= 0) {
            zoom = 0.1f;
        }
    }

    @Override
    public void draw() {
        background(255);

        if (DEBUG && checkKey("R")) {
            player.reset();
        }
        
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
        
        player.update();

        for (int i = objectList.size()-1; i >=0; i--) {
            Node n = objectList.get(i);
            n.update();
        }
        
        // Step the physics simulation
        box2d.step();
        // Remove objects after box2d has stepped
        for (int i = toRemoveList.size()-1; i >=0; i--) {
            Node n = toRemoveList.get(i);
            n.destroy();
        }
        toRemoveList.clear();

        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.pushMatrix();
        cvs.scale(zoom);
        
        float swidth = width * (1 / zoom),
              sheight = height * (1 / zoom);
        
        player.display(swidth, sheight);

        cvs.translate(swidth/2-player.getGraphicsPosition().x, sheight/2-player.getGraphicsPosition().y);
        for (int i = objectList.size()-1; i >=0; i--) {
            Node n = objectList.get(i);
            n.display(swidth, sheight);
        }
        cvs.popMatrix();
    }
}
