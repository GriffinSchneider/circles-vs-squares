package circlesvssquares;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import pbox2d.PBox2D;

import circlesvssquares.PointQueryCallback.PointQueryCallbackFilter;

public class GameScene extends Scene {
    private static final float WORLD_GRAVITY = -110;
    public static final int MAX_LEVELS = 5;
    
    private boolean editMode = false;
    public boolean isEditMode() {
        return editMode;
    }

    // A reference to our box2d world
    private PBox2D box2d;
    
    float zoom = 1;
    Zoom zoomType = Zoom.NONE;
    enum Zoom {
        NONE,
        IN,
        OUT
    };
    
    Ground target = null;
    Vec2 targetPoint = null;
    
    EndPoint endPoint = null;
    
    GameMode currentType = GameMode.NONE;
    
    Player player = null;

    public ParallaxBackground background = null;
    
    MouseJoint mouseJoint;
    
    private int currentLevel;
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    enum GameMode {
        NONE,
        PHYSICS,
        DELETE,
        GROUND,
        EASY_ENEMY,
        END_POINT
    };

    public GameScene(CirclesVsSquares app_, int level, boolean levelEdit_) {
        super(app_);
        this.currentLevel = level;
        editMode = levelEdit_;
    }
    
    public void init() {
        // Initialize box2d physics and create the world
        box2d = new PBox2D(this.app);
        box2d.createWorld();
        box2d.setGravity(0, WORLD_GRAVITY);

        // Add a listener to listen for collisions!
        box2d.world.setContactListener(new CustomListener());

        if (editMode) {
            this.app.addMouseWheelListener(new MouseWheelListener() { 
                public void mouseWheelMoved(MouseWheelEvent mwe) { 
                    mouseWheel(mwe.getWheelRotation());
                }}); 
        }
        
        Vec2 playerPos = new Vec2(220, 150);
        this.player = new Player(playerPos, box2d);
        
        if (editMode) createDebugUI();
        else currentType = GameMode.PHYSICS;
        
        if (editMode) LevelEditor.loadLevel("levels/test.json", box2d);
        else LevelEditor.loadLevel("levels/level" + currentLevel + ".json", box2d);

        this.background = new ParallaxBackground(this.app);
    }
    
    void mouseWheel(int delta) {
        if (delta < 0) zoomType = Zoom.OUT;
        else zoomType = Zoom.IN;
    }
    
    private Body getBodyAtPoint(Vec2 point) {
        return this.getBodyAtPoint(point, 0.00001f, null);
    }

    private Body getBodyAtPoint(Vec2 point, float aabbSize, PointQueryCallbackFilter filter) {
        AABB aabb = new AABB(point.sub(new Vec2(aabbSize, aabbSize)), 
                             point.add(new Vec2(aabbSize, aabbSize)));
                        
        PointQueryCallback callback = new PointQueryCallback(filter);
        box2d.world.queryAABB(callback, aabb);
                        
        return callback.getSelectedBody();
    }

    public void resetPlayer() {
        player.r = Player.PLAYER_INITIAL_RADIUS;
        player.makeShape();
        player.setPhysicsPosition(new Vec2(-player.r, player.r));
        this.background.reset();
    }
    
    @Override
    public void update() {
        ArrayList<Box2DObjectNode> objectList = Box2DObjectNode.objectList;
        Vec2 mousePos = box2d.coordPixelsToWorld(new Vec2(this.app.mouseX, this.app.mouseY)).mul(1/zoom) .add(player.getPhysicsPosition());
        
        if (editMode) {
            // These keys can be used as an alternative to the mouse wheel
            if (zoomType == Zoom.IN || this.app.checkKey("=") || this.app.checkKey("+")) {
                zoom += 0.1f;
            }
            if (zoomType == Zoom.OUT || this.app.checkKey("-")) {
                zoom -= 0.1f;
                if (zoom <= 0) zoom = 0.1f;
            }
            zoomType = Zoom.NONE;
            
            // Check to see if a button was clicked on so multiple click commands do occur
            boolean wasClicked = Button.updateButtons();
            
            if (this.app.checkKey("R")) {
                this.resetPlayer();
            }
            
            if (!wasClicked) {            
                switch (currentType) {
                case NONE:
                case PHYSICS:
                    break;
                case DELETE:
                    if (this.app.mouseClick) {
                        Body selectedBody = this.getBodyAtPoint(mousePos);
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
                        targetPoint = mousePos;
                        target = new Ground(targetPoint, 0, 0, box2d);
                    }
                    // Drag and drop ground object
                    else if (target != null) {
                        Vec2 diff = targetPoint.sub(mousePos);
                        target.setPhysicsPosition(mousePos.add(diff.mul(0.5f)));
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
                        new Enemy(mousePos, 25, 25, box2d);
                    }
                    break;
                case END_POINT:
                    if (this.app.mouseClick) {
                        if (endPoint != null) this.endPoint.destroy();
                        this.endPoint = new EndPoint(mousePos, 25, box2d);
                    }
                    break;
                }
            }
        } else {
         // Mouse was clicked, create the mouse joint
            if (this.app.mouseClick) {
                if (this.mouseJoint != null) {
                    box2d.world.destroyJoint(this.mouseJoint);
                    this.mouseJoint = null;
                }
                Body selectedBody = this.getBodyAtPoint(mousePos, 3.0f, new PointQueryCallbackFilter() {
                    @Override
                    public boolean filter(Body body) {
                        return body.getUserData() != null && body.getUserData() instanceof Bullet;
                    }
                });
                if (selectedBody != null && selectedBody.getUserData() != null && ((Box2DObjectNode)selectedBody.getUserData()).isInSlowField) {
                    MouseJointDef def = new MouseJointDef();
                    def.bodyA = box2d.getGroundBody();
                    def.bodyB = selectedBody;
                    def.maxForce = 5000.0f;
                    def.target.set(mousePos);
                    def.frequencyHz = 5.0f;
                    this.mouseJoint = (MouseJoint)box2d.createJoint(def);
                }
            // Mouse was held down, and we're dragging a body. Update the mouse joint target.
            } else if (this.mouseJoint != null && ((Box2DObjectNode)this.mouseJoint.getBodyB().getUserData()).isInSlowField && this.app.mousePressed) {
                this.mouseJoint.setTarget(mousePos);
            // No mouse button pressed. Destroy the mouse joint if there is one.
            } else {
                if (this.mouseJoint != null) {
                    box2d.world.destroyJoint(this.mouseJoint);
                    this.mouseJoint = null;
                }
            }
        }

        // Move the player if movement keys are held down
        Player.MovementDirection direction = Player.MovementDirection.NONE;
        if (this.app.checkKey("D")) {
            direction = Player.MovementDirection.RIGHT;
            
            if (currentType != GameMode.PHYSICS) player.setPhysicsPosition(player.getPhysicsPosition().add(new Vec2(1, 0)));
        } else if (this.app.checkKey("A")) {
            direction = Player.MovementDirection.LEFT;
            
            if (currentType != GameMode.PHYSICS) player.setPhysicsPosition(player.getPhysicsPosition().add(new Vec2(-1, 0)));
        }
        player.movePlayer(direction);

        // Attempt to jump if the jump key is held down
        if (this.app.checkKey("W")) {
            player.jumpIfPossible();
            if (currentType != GameMode.PHYSICS) player.setPhysicsPosition(player.getPhysicsPosition().add(new Vec2(0, 1)));
        }
        
        if (this.app.checkKey("S")) {
            if (currentType != GameMode.PHYSICS) player.setPhysicsPosition(player.getPhysicsPosition().add(new Vec2(0, -1)));
        }

        if (this.app.checkKey("F")) {
            player.activateSlowField();
        }

        player.update();

        if (currentType == GameMode.PHYSICS) {
        
            for (int i = objectList.size()-1; i >=0; i--) {
                Box2DObjectNode n = objectList.get(i);
                n.update();
            }

            // Step the physics simulation
            box2d.step();
        }
        
        Box2DObjectNode.clearToRemove();
    }
        
    @Override
    public void draw() {
        ArrayList<Box2DObjectNode> objectList = Box2DObjectNode.objectList;
        
        float screenWidth = this.app.width * (1 / zoom);
        float screenHeight = this.app.height * (1 / zoom);
        
        this.background.display(screenWidth, screenHeight, player.getGraphicsPosition());
        
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
        
        super.draw();
        this.app.mouseClick = false;
    }
    
    @Override
    public void cleanUp() {
        super.cleanUp();
        Box2DObjectNode.clearObjects();
    }
    
    void createDebugUI() {
        float buttonWidth = 70;
        float buttonNum = 0;
        
        Button saveButton = Button.createButton(new Vec2(), buttonWidth, 30, new ButtonCallback() {
            @Override
            public void call() {
                LevelEditor.saveLevel(Box2DObjectNode.objectList);
            }
        });
        saveButton.text = "Save";
        buttonNum++;

        Button loadButton = Button.createButton(new Vec2(buttonWidth*buttonNum, 0), buttonWidth, 30, new ButtonCallback() {
            @Override
            public void call() {
                Box2DObjectNode.clearObjects();
                LevelEditor.loadLevel(null, box2d);
            }
        });
        loadButton.text = "Load";
        buttonNum++;
        
        Button clearButton = Button.createButton(new Vec2(buttonWidth*buttonNum, 0), buttonWidth, 30, new ButtonCallback() {
            @Override
            public void call() {
                Box2DObjectNode.clearObjects();
            }
        });
        clearButton.text = "Clear";
        buttonNum++;

        Button physicsButton = Button.createCheckBox(new Vec2(buttonWidth*buttonNum, 0), buttonWidth, 30, new ButtonCallback() {
            @Override
            public void call() {
                if (this.isDown) {
                    currentType = GameMode.PHYSICS;
                } else {
                    currentType = GameMode.NONE;
                }
            }
        });
        physicsButton.text = "Physics";
        buttonNum++;
        
        Button deleteButton = Button.createCheckBox(new Vec2(buttonWidth*buttonNum, 0), buttonWidth, 30, new ButtonCallback() {
            @Override
            public void call() {
                if (this.isDown) {
                    currentType = GameMode.DELETE;
                } else {
                    currentType = GameMode.NONE;
                }
            }
        });
        deleteButton.text = "Delete";
        buttonNum++;
        
        Button groundButton = Button.createCheckBox(new Vec2(buttonWidth*buttonNum, 0), buttonWidth, 30, new ButtonCallback() {
            @Override
            public void call() {
                if (this.isDown) {
                    currentType = GameMode.GROUND;
                } else {
                    currentType = GameMode.NONE;
                }
            }
        });
        groundButton.text = "Ground";
        buttonNum++;
        
        Button enemyButton = Button.createCheckBox(new Vec2(buttonWidth*buttonNum, 0), buttonWidth, 30, new ButtonCallback() {
            @Override
            public void call() {
                if (this.isDown) {
                    currentType = GameMode.EASY_ENEMY;
                } else {
                    currentType = GameMode.NONE;
                }
            }
        });
        enemyButton.text = "Enemy";
        buttonNum++;
        
        Button endButton = Button.createCheckBox(new Vec2(buttonWidth*buttonNum, 0), buttonWidth, 30, new ButtonCallback() {
            @Override
            public void call() {
                if (this.isDown) {
                    currentType = GameMode.END_POINT;
                } else {
                    currentType = GameMode.NONE;
                }
            }
        });
        endButton.text = "End Point";
        buttonNum++;
    }
}
