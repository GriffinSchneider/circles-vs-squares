package circlesvssquares;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import pbox2d.PBox2D;
import processing.core.PApplet;

public class GameScene extends Scene {
    private static final float WORLD_GRAVITY = -50;
    private boolean DEBUG = false;

    // A reference to our box2d world
    private PBox2D box2d;
    
    boolean enablePhysics;
    
    float zoom = 1;
    Zoom zoomType = Zoom.NONE;
    enum Zoom {
        NONE,
        IN,
        OUT
    };
    
    Ground target = null;
    Vec2 targetPoint = null;
    
    ObjectTypes currentType = ObjectTypes.NONE;
    
    enum ObjectTypes {
        NONE,
        DELETE,
        GROUND,
        EASY_ENEMY
    };

    public GameScene(CirclesVsSquares app_, boolean levelEdit_) {
        super(app_);
        DEBUG = levelEdit_;
    }
    
    public void init() {
        // Initialize box2d physics and create the world
        box2d = new PBox2D(this.app);
        box2d.createWorld();
        box2d.setGravity(0, WORLD_GRAVITY);

        // Add a listener to listen for collisions!
        box2d.world.setContactListener(new CustomListener());

        enablePhysics = !DEBUG;
        if (DEBUG) {
            this.app.addMouseWheelListener(new MouseWheelListener() { 
                public void mouseWheelMoved(MouseWheelEvent mwe) { 
                    mouseWheel(mwe.getWheelRotation());
                }}); 
        }
      
        resetPlayerAndObjects();
        
        if (DEBUG) createDebugUI();
        
        LevelEditor.loadLevel("../levels/test.json", box2d);
    }
    
    void mouseWheel(int delta) {
        if (delta < 0) zoomType = Zoom.OUT;
        else zoomType = Zoom.IN;
    }
    
    @Override
    public void update() {
        ArrayList<Box2DObjectNode> objectList = Box2DObjectNode.objectList;
        ArrayList<Box2DObjectNode> toRemoveList = Box2DObjectNode.toRemoveList;
        Player player = Player.current;
        
        if (DEBUG) {
            // These keys can be used as an alternative to the mouse wheel
            if (zoomType == Zoom.IN || this.app.checkKey("=") || this.app.checkKey("+")) {
                zoom += 0.1f;
            }
            if (zoomType == Zoom.OUT || this.app.checkKey("-")) {
                zoom -= 0.1f;
                if (zoom <= 0) zoom = 0.1f;
            }
            zoomType = Zoom.NONE;
        }

        if (DEBUG) {
            // Check to see if a button was clicked on so multiple click commands do occur
            boolean wasClicked = Button.updateButtons();
            
            if (this.app.checkKey("R")) {
                player.reset();
            }
            
            if (!wasClicked) {
                Vec2 pos = box2d.coordPixelsToWorld(new Vec2(this.app.mouseX, this.app.mouseY)).mul(1/zoom) .add(player.getPhysicsPosition());
                
                switch (currentType) {
                case NONE:
                    break;
                case DELETE:
                    if (this.app.mouseClick) {
                        AABB aabb = new AABB(pos.sub(new Vec2(0.0001f, 0.0001f)), 
                                pos.add(new Vec2(0.0001f, 0.0001f)));
                        
                        PointQueryCallback callback = new PointQueryCallback(pos);
                        box2d.world.queryAABB(callback, aabb);
                        
                        Body selectedBody = callback.getSelectedBody();
                        if (selectedBody != null) {
                            Box2DObjectNode node = 
                                    (Box2DObjectNode) selectedBody.getUserData();
                            node.destroy();
                        }
                    }
                    break;
                case GROUND:
                    // Create ground object
                    if (this.app.mouseClick && target == null) {
                        targetPoint = pos;
                        target = new Ground(targetPoint, 0, 0, box2d);
                    }
                    // Drag and drop ground object
                    else if (target != null) {
                        Vec2 diff = targetPoint.sub(pos);
                        target.setPhysicsPosition(pos.add(diff.mul(0.5f)));
                        target.w = Math.abs(box2d.scaleFactor * diff.x);
                        target.h = Math.abs(box2d.scaleFactor * diff.y);
                        target.updateBody();
                        if (!this.app.mousePressed) {
                            // If the width or height < 3 destroy the object
                            if (target.w < 3 || target.h < 3) target.destroy();
                            target = null;
                        }
                    }
                    break;
                case EASY_ENEMY:
                    if (this.app.mouseClick) {
                        Enemy e = new Enemy(pos, 25, 25, box2d);
                    }
                    break;
                }
            }
        }

        // Move the player if movement keys are held down
        Player.MovementDirection direction = Player.MovementDirection.NONE;
        if (this.app.checkKey("D")) {
            direction = Player.MovementDirection.RIGHT;
            
            if (!enablePhysics) player.setPhysicsPosition(player.getPhysicsPosition().add(new Vec2(1, 0)));
        } else if (this.app.checkKey("A")) {
            direction = Player.MovementDirection.LEFT;
            
            if (!enablePhysics) player.setPhysicsPosition(player.getPhysicsPosition().add(new Vec2(-1, 0)));
        }
        player.movePlayer(direction);

        // Attempt to jump if the jump key is held down
        if (this.app.checkKey("W")) {
            player.jumpIfPossible();
            if (!enablePhysics) player.setPhysicsPosition(player.getPhysicsPosition().add(new Vec2(0, 1)));
        }
        
        if (this.app.checkKey("S")) {
            if (!enablePhysics) player.setPhysicsPosition(player.getPhysicsPosition().add(new Vec2(0, -1)));
        }

        if (this.app.checkKey("j")) {
            player.activateSlowField();
        }

        player.update();

        if (enablePhysics) {
        
            for (int i = objectList.size()-1; i >=0; i--) {
                Box2DObjectNode n = objectList.get(i);
                n.update();
            }

            // Step the physics simulation
            box2d.step();
        }
        
        // Remove objects after box2d has stepped
        for (int i = toRemoveList.size()-1; i >=0; i--) {
            Box2DObjectNode n = toRemoveList.get(i);
            n.destroy();
        }
        toRemoveList.clear();
    }
        
    @Override
    public void draw() {
        ArrayList<Box2DObjectNode> objectList = Box2DObjectNode.objectList;
        ArrayList<Box2DObjectNode> toRemoveList = Box2DObjectNode.toRemoveList;
        Player player = Player.current;
        
        float screenWidth = this.app.width * (1 / zoom);
        float screenHeight = this.app.height * (1 / zoom);
        
        this.app.background(255);
        
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.pushMatrix();
        cvs.scale(zoom);

        player.display(screenWidth, screenHeight);

        // Translate so that the player body is in the center of the screen,
        // then draw everything.
        cvs.translate(screenWidth/2-player.getGraphicsPosition().x,
                      screenHeight/2-player.getGraphicsPosition().y);
        for (int i = objectList.size()-1; i >=0; i--) {
            Box2DObjectNode n = objectList.get(i);
            n.display(screenWidth, screenHeight);
        }
        cvs.popMatrix();
        
        if (DEBUG) super.draw();
        this.app.mouseClick = false;
    }
    
    @Override
    public void cleanUp() {
        super.cleanUp();
        
        resetPlayerAndObjects();
    }
    
    public void resetPlayerAndObjects() {
        Box2DObjectNode.clearObjects();
        if (Player.current != null) Player.current.destroy();
        Player.current = null;
        
        createPlayer();
    }
    
    public void createPlayer() {
        Vec2 playerPos = new Vec2(220, 150);
        Player.current = new Player(playerPos, box2d);
    }
    
    void createDebugUI() {
        Button saveButton = Button.createButton(new Vec2(), 60, 30, new ButtonCallback() {
            @Override
            public void call() {
                LevelEditor.saveLevel(Box2DObjectNode.objectList);
            }
        });
        saveButton.text = "Save";

        Button loadButton = Button.createButton(new Vec2(60, 0), 60, 30, new ButtonCallback() {
            @Override
            public void call() {
                resetPlayerAndObjects();
                LevelEditor.loadLevel(null, box2d);
            }
        });
        loadButton.text = "Load";
        
        Button clearButton = Button.createButton(new Vec2(120, 0), 60, 30, new ButtonCallback() {
            @Override
            public void call() {
                resetPlayerAndObjects();
            }
        });
        clearButton.text = "Clear";

        Button physicsButton = Button.createCheckBox(new Vec2(180, 0), 60, 30, new ButtonCallback() {
            @Override
            public void call() {
                enablePhysics = this.isDown;
            }
        });
        physicsButton.text = "Physics";
        
        Button deleteButton = Button.createCheckBox(new Vec2(240, 0), 60, 30, new ButtonCallback() {
            @Override
            public void call() {
                if (this.isDown) {
                    currentType = ObjectTypes.DELETE;
                } else {
                    currentType = ObjectTypes.NONE;
                }
            }
        });
        deleteButton.text = "Delete";
        
        Button groundButton = Button.createCheckBox(new Vec2(300, 0), 60, 30, new ButtonCallback() {
            @Override
            public void call() {
                if (this.isDown) {
                    currentType = ObjectTypes.GROUND;
                } else {
                    currentType = ObjectTypes.NONE;
                }
            }
        });
        groundButton.text = "Ground";
        
        Button enemyButton = Button.createCheckBox(new Vec2(360, 0), 60, 30, new ButtonCallback() {
            @Override
            public void call() {
                if (this.isDown) {
                    currentType = ObjectTypes.EASY_ENEMY;
                } else {
                    currentType = ObjectTypes.NONE;
                }
            }
        });
        enemyButton.text = "Enemy";
    }
}
