package circlesvssquares;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

import pbox2d.PBox2D;


class Player extends Box2DObjectNode {

    // Initial radius (in physics units) of the player circle
    public static final float PLAYER_INITIAL_RADIUS = 12.0f;
    // Magnitude of impulse to apply each frame in the x-direction to make the player move
    public static final float PLAYER_MOVEMENT_IMPULSE = 13;
    // Maximum x-velocity that the player can reach before we stop increasing the velocity
    // due to key presses.
    public static final float PLAYER_MAX_SPEED = 50;
    // Amount to decrease the player's x-velocity each frame if no movement buttons
    // are being pressed.
    public static final float PLAYER_NO_MOVEMENT_DAMPING = 8.0f;
    // Magnitude of impulse in the y-direction to apply to make the player "jump"
    public static final float PLAYER_JUMP_IMPULSE = 100;
    // The density of the player is always this coefficient times 2 pi times the player's
    // radius squared, so the player body always has equal weight.
    public static final float PLAYER_DENSITY_COEFFIECIENT = 400.0f;
    // Radius (in graphics units) of the slow field
    public static final float PLAYER_SLOW_FIELD_RADIUS = 120.0f;
    // Amount that objects in the slow field are dragged along with the player's movement.
    // Higher = more drag
    public static final float PLAYER_SLOW_FIELD_DRAG_COEFFICIENT = 25.0f;
    // Duration (in frames) of the slow field
    public static final int PLAYER_SLOW_FIELD_COOLDOWN = 200;
    // Time (in frames) player must wait from the end of one slow field until
    // another can be activated
    public static final int PLAYER_SLOW_FIELD_DURATION = 250;
    
    public enum MovementDirection {
        LEFT,
        RIGHT,
        NONE,
    }

    Body slowFieldBody;

    int collisionCounter = 0;
    int framesSinceSlowField = PLAYER_SLOW_FIELD_COOLDOWN + PLAYER_SLOW_FIELD_DURATION;
    float r;
    boolean radiusChange;
    boolean isSlowFieldActive;

    // Constructor
    Player(Vec2 pos, PBox2D box2d) {
        super(pos, box2d);

        this.r = PLAYER_INITIAL_RADIUS;
        radiusChange = false;

        // Add the box to the box2d world
        makeBody(pos);
        body.setUserData(this);
    }

    // This function removes the particle from the box2d world
    public void killBody() {
        box2d.destroyBody(body);
    }

    public boolean contains(Vec2 targetPos) {
        Vec2 worldPoint = box2d.coordPixelsToWorld(targetPos);
        Fixture f = body.getFixtureList();
        boolean inside = f.testPoint(worldPoint);
        return inside;
    }

    @Override
    public void display(float width, float height) {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        // Get its angle of rotation
        float a = body.getAngle();

        // Setup transform matrix
        cvs.pushMatrix();
        cvs.translate(width/2, height/2);
        cvs.rotate(-a);

        if (this.isSlowFieldActive) {
            // Draw slow field circle
            cvs.fill(100, 255, 100, 150);
            cvs.ellipse(0, 0, PLAYER_SLOW_FIELD_RADIUS*2, PLAYER_SLOW_FIELD_RADIUS*2);
        }
        
        // Draw player body circle
        if (this.framesSinceSlowField > PLAYER_SLOW_FIELD_COOLDOWN + PLAYER_SLOW_FIELD_DURATION ||
            this.isSlowFieldActive) {
            cvs.fill(0, 255, 0);
        } else {
            cvs.fill(150, 200, 150);
        }
        cvs.stroke(0);
        cvs.strokeWeight(1);
        cvs.ellipse(0, 0, r*2, r*2);
        
        // Add a line so we can see rotation
        cvs.line(0, 0, r, 0);
        
        cvs.popMatrix();
    }


    public void makeShape() {
        Fixture f = this.body.getFixtureList();
        if (f != null) this.body.destroyFixture(f);
        
        // Make the body's shape a circle
        CircleShape cs = new CircleShape();
        cs.m_radius = box2d.scalarPixelsToWorld(r);

        FixtureDef fd = new FixtureDef();
        fd.shape = cs;

        fd.density = PLAYER_DENSITY_COEFFIECIENT / ((float)Math.PI * r * r);
        fd.friction = 0.5f;
        fd.restitution = 0.1f;

        // Attach fixture to body
        body.createFixture(fd);
    }

    // This function adds the rectangle to the box2d world
    public void makeBody(Vec2 pos) {
        // Define a body
        BodyDef bd = new BodyDef();

        // Set its position
        bd.position = box2d.coordPixelsToWorld(pos);
        bd.type = BodyType.DYNAMIC;
        body = box2d.createBody(bd);
        
        makeShape();
    }

    public void movePlayer(MovementDirection movementDirection) {
        Vec2 vel = this.body.getLinearVelocity();
        if (movementDirection == MovementDirection.LEFT && vel.x > -PLAYER_MAX_SPEED) {
            this.body.applyLinearImpulse(new Vec2(-PLAYER_MOVEMENT_IMPULSE, 0f), this.body.getWorldCenter());
        } else if (movementDirection == MovementDirection.RIGHT && vel.x < PLAYER_MAX_SPEED) {
            this.body.applyLinearImpulse(new Vec2(PLAYER_MOVEMENT_IMPULSE, 0f), this.body.getWorldCenter());
        } else if (movementDirection == MovementDirection.NONE) {
            if (vel.x > PLAYER_NO_MOVEMENT_DAMPING) {
                this.body.applyLinearImpulse(new Vec2(-PLAYER_NO_MOVEMENT_DAMPING, 0f), this.body.getWorldCenter());
            } else if (vel.x < -PLAYER_NO_MOVEMENT_DAMPING) {
                this.body.applyLinearImpulse(new Vec2(PLAYER_NO_MOVEMENT_DAMPING, 0f), this.body.getWorldCenter());
            } else {
                this.body.applyLinearImpulse(new Vec2(-vel.x, 0f), this.body.getWorldCenter());
            }
        }
    }

    public void jumpIfPossible() {
        if (this.collisionCounter > 0) {
            this.body.applyLinearImpulse(new Vec2(0, PLAYER_JUMP_IMPULSE), this.body.getWorldCenter());
        }
    }

    public void activateSlowField() {
        if (this.framesSinceSlowField > PLAYER_SLOW_FIELD_COOLDOWN + PLAYER_SLOW_FIELD_DURATION) {
            // Define a body
            BodyDef bd = new BodyDef();

            // Set its position
            bd.position = box2d.coordPixelsToWorld(this.getPhysicsPosition());
            bd.type = BodyType.DYNAMIC;
            this.slowFieldBody = box2d.createBody(bd);
            slowFieldBody.setUserData(this);
        
            // Make the body's shape a circle
            CircleShape cs = new CircleShape();
            cs.m_radius = box2d.scalarPixelsToWorld(PLAYER_SLOW_FIELD_RADIUS);

            FixtureDef fd = new FixtureDef();
            fd.shape = cs;

            // Attach fixture to body
            Fixture f = slowFieldBody.createFixture(fd);
            f.setSensor(true);

            this.isSlowFieldActive = true;
            this.framesSinceSlowField = 0;
        }
    }

    public void deactivateSlowField() {
        this.box2d.destroyBody(this.slowFieldBody);
        this.slowFieldBody = null;
        this.isSlowFieldActive = false;
    }

    @Override
    public void update() {
        if (this.radiusChange) {
            if (r <= 2) {
                CirclesVsSquares cvs = CirclesVsSquares.instance();
                ((GameScene)cvs.getCurrentScene()).resetPlayer();
            }
            else {
                this.makeShape();
                this.radiusChange = false;
            }
        }

        this.framesSinceSlowField++;
        if (this.slowFieldBody != null) {
            this.slowFieldBody.setTransform(this.getPhysicsPosition(), 0);
            if (this.framesSinceSlowField == PLAYER_SLOW_FIELD_DURATION) {
                this.deactivateSlowField();
            }
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void collisionBegan(Contact cp) {
        Body b1 = cp.getFixtureA().getBody();
        Body b2 = cp.getFixtureB().getBody();

        // If these are both our bodies, then its the player and slow field.
        // Ignore.
        if (b1.getUserData() == b2.getUserData()) return;

        Body ourBody;
        Body otherBody;
        if (b1.getUserData() == this) {
            ourBody = b1;
            otherBody = b2;
        } else {
            ourBody = b2;
            otherBody = b1;
        }

        if (ourBody == this.body) {
            collisionCounter++;
            if (otherBody.getUserData().getClass() == Bullet.class) {
                toRemoveList.add((Bullet)otherBody.getUserData());

                this.r--;
                this.radiusChange = true;
            }
            else if (otherBody.getUserData().getClass() == EndPoint.class) {
                // Check to see if the player is at the end point
                CirclesVsSquares cvs = CirclesVsSquares.instance();
                GameScene scene = (GameScene) cvs.getCurrentScene();
                // Increment the level
                int nextLevel = scene.getCurrentLevel() + 1;
                // If there are no more levels return to the main menu
                if (nextLevel > GameScene.MAX_LEVELS) cvs.changeScene(new MenuScene(cvs));
                else cvs.changeScene(new GameScene(cvs, nextLevel, scene.isEditMode()));
            }

        } else if (ourBody == this.slowFieldBody) {
            ((Box2DObjectNode)otherBody.getUserData()).isInSlowField = true;
        }
    }

    @Override
    public void collisionEnded(Contact cp) {
        Body b1 = cp.getFixtureA().getBody();
        Body b2 = cp.getFixtureB().getBody();

        // If these are both our bodies, then its the player and slow field.
        // Ignore.
        if (b1.getUserData() == b2.getUserData()) return;

        Body ourBody;
        Body otherBody;
        if (b1.getUserData() == this) {
            ourBody = b1;
            otherBody = b2;
        } else {
            ourBody = b2;
            otherBody = b1;
        }

        if (ourBody == this.body) {
            this.collisionCounter--;
        } else if (ourBody == this.slowFieldBody) {
            ((Box2DObjectNode)otherBody.getUserData()).isInSlowField = false;
        }
    }

}
