package circlesvssquares;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;

import org.jbox2d.common.Vec2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    
    boolean[] keys = new boolean[526];
    boolean mousePressed = false;
    boolean mouseClick = false;
    
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

        // Create the UI
        if (DEBUG) createDebugUI();
        
        
        toRemoveList = new ArrayList<Node>();
        objectList = new ArrayList<Node>();
        objectList.add(new Ground(200, 200, 300, 25, box2d));
        objectList.add(new Ground(400, 300, 100, 25, box2d));
        
        Vec2 enemyPos = box2d.coordPixelsToWorld( new Vec2(400, 250) );
        objectList.add(new Enemy(enemyPos, 25, 25, box2d));
    }
    
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
    public void mousePressed() {
        mousePressed = true;
        mouseClick = true;
    }
    
    @Override
    public void mouseReleased() {
        mousePressed = false;
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
        
        Button.updateButtons();
        
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

        Button.displayButtons(width, height);
        
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
        
        mouseClick = false;
    }
    
    void saveLevel() {
        JSONArray level = new JSONArray();
        // Save objects
        for (int i = objectList.size()-1; i >=0; i--) {
            Node n = objectList.get(i);
            if (n.getClass() != Bullet.class) {
                JSONObject object = new JSONObject();
                //object.put("pos", n.pos);
                object.put("class", n.getClass().toString());
                
                if (n.getClass() == Ground.class) {
                    Ground g = (Ground) n;
                    object.put("w", g.w);
                    object.put("h", g.h);
                }
                
                level.add(object);
            }
        }
        
        println(level);

        try {
            FileWriter file = new FileWriter("./test.json");
            file.write(level.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    void loadLevel() {
        JSONParser parser = new JSONParser();
        
        try {
            Object list = parser.parse(new FileReader("./test.json"));
            JSONArray level = (JSONArray) list;
            for (Object obj : level.toArray()) {
                JSONObject node =  (JSONObject) obj;
                
                String sClass = (String) node.get("class");
                if (sClass.equals(Enemy.class.toString())) {
                    println(node.get("class"));
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    void createDebugUI() {
        Button saveButton = Button.createButton(new Vec2(), 60, 30, new Callable() {
            @Override
            public Object call() throws Exception {
                saveLevel();
                return null;
            }
        });
        saveButton.text = "Save";
        
        Button loadButton = Button.createButton(new Vec2(60, 0), 60, 30, new Callable() {
            @Override
            public Object call() throws Exception {
                loadLevel();
                return null;
            }
        });
        loadButton.text = "Load";
        
        Button groundButton = Button.createCheckBox(new Vec2(120, 0), 60, 30, new Callable() {
            @Override
            public Object call() throws Exception {
                
                return null;
            }
        });
        groundButton.text = "Ground";
    }
}
