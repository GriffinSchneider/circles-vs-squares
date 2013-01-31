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

    // Magnitude of impulse to apply each frame in the x-direction to make the player move
    private static final float PLAYER_MOVEMENT_IMPULSE = 10;
    // Maximum x-velocity that the player can reach before we stop increasing the velocity
    // due to key presses.
    private static final float PLAYER_MAX_SPEED = 20;
    // Amount to decrease the player's x-velocity each frame if no movement buttons
    // are being pressed.
    private static final float PLAYER_NO_MOVEMENT_DAMPING = 1.3f;
    // Magnitude of impulse in the y-direction to apply to make the player "jump"
    private static final float PLAYER_JUMP_IMPULSE = 60;
    // THe density of the player is always this coefficient times 2 pi times the player's
    // radius squared, so the player body always has equal weight.
    private static final float PLAYER_DENSITY_COEFFIECIENT = 400.0f;

    private static final float PLAYER_SLOW_FIELD_RADIUS = 40.0f;

    public enum MovementDirection {
        LEFT,
        RIGHT,
        NONE,
    }

    Body slowFieldBody;

    int collisionCounter = 0;
    float r;
    boolean radiusChange;
    boolean isSlowFieldActive;

    // Constructor
    Player(Vec2 pos, PBox2D box2d) {
        super(pos, box2d);

        r = 12;
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

    // Drawing the box
    @Override
    public void display(float width, float height) {
        // Get its angle of rotation
        float a = body.getAngle();
        CirclesVsSquares cvs = CirclesVsSquares.instance();

        // Setup transform - 
        cvs.pushMatrix();
        cvs.translate(width/2, height/2);
        cvs.rotate(-a);

        if (this.isSlowFieldActive) {
            cvs.fill(100, 255, 100);
            cvs.ellipse(0, 0, PLAYER_SLOW_FIELD_RADIUS*2, PLAYER_SLOW_FIELD_RADIUS*2);
        }
        
        cvs.fill(0, 255, 0);
        cvs.stroke(0);
        cvs.strokeWeight(1);
        cvs.ellipse(0, 0, r*2, r*2);
        // Let's add a line so we can see the rotation
        cvs.line(0, 0, r, 0);
        cvs.popMatrix();
    }

    public void reset() {
        this.r = 12;
        this.makeShape();
        this.body.setTransform(new Vec2(-r, r), 0);
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
        // Define a body
        BodyDef bd = new BodyDef();

        // Set its position
        bd.position = box2d.coordPixelsToWorld(this.getPhysicsPosition());
        bd.type = BodyType.DYNAMIC;
        Body body = box2d.createBody(bd);
        body.setUserData(this);
        
        // Make the body's shape a circle
        CircleShape cs = new CircleShape();
        cs.m_radius = box2d.scalarPixelsToWorld(PLAYER_SLOW_FIELD_RADIUS);

        FixtureDef fd = new FixtureDef();
        fd.shape = cs;

        // Attach fixture to body
        Fixture f = body.createFixture(fd);
        f.setSensor(true);

        this.isSlowFieldActive = true;
    }

    @Override
    public void update() {
        if (this.radiusChange) {
            if (r <= 2) {
                this.reset();
            }
            else {
                this.makeShape();
                this.radiusChange = false;
            }
        }

        if (this.slowFieldBody != null) {
            this.slowFieldBody.setTransform(this.getPhysicsPosition(), 0);
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
                CirclesVsSquares cvs = CirclesVsSquares.instance();
                cvs.toRemoveList.add((Bullet)otherBody.getUserData());

                this.r--;
                this.radiusChange = true;
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
