package circlesvssquares;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import pbox2d.PBox2D;
import processing.core.PConstants;

class Bullet extends Box2DObjectNode {

    public static Bullet createSimpleBullet(Vec2 pos, PBox2D box2d) {
        Bullet bullet = new Bullet(pos, 10, 10, box2d);
        objectList.add(bullet);
        return bullet;
    }
    
    // a boundary is a simple rectangle with x,y,width,and height
    float w;
    float h;
    
    float lifeSpan = 400;

    Bullet(Vec2 pos, float w, float h, PBox2D box2d) {
        super(pos, box2d);
        this.w = w;
        this.h = h;

        // define the polygon
        PolygonShape sd = new PolygonShape();
        // figure out the box2d coordinates
        float box2dw = box2d.scalarPixelsToWorld(w/2);
        float box2dh = box2d.scalarPixelsToWorld(h/2);
        // we're just a box
        sd.setAsBox(box2dw, box2dh);

        // create the body
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(pos);
        body = box2d.createBody(bd);

        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        fd.density = 3;
        fd.friction = 1;
        fd.restitution = 0.1f;

        // attached the shape to the body using a fixture
        body.createFixture(fd);

        body.setUserData(this);
    }

    // draw the boundary, if it were at an angle we'd have to do something fancier
    @Override
    public void display(float width, float height) {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.fill(255, 160, 0, lifeSpan);
        cvs.stroke(0);
        cvs.rectMode(PConstants.CENTER);
        cvs.rect(this.getGraphicsPosition().x,this.getGraphicsPosition().y,w,h);
    }

    @Override
    public void update() {
        if (this.isInSlowField) {
            Vec2 vel = this.body.getLinearVelocity();
            float newXVel = vel.x > 0 ? Math.min(vel.x, 0.5f) : Math.max(vel.x, -0.5f);
            float newYVel = vel.y > 0 ? Math.min(vel.y, 0.5f) : Math.max(vel.y, -0.5f);
            // Cap speed
            this.body.setLinearVelocity(new Vec2(newXVel, newYVel));
            // Drag with velocity of slow field
            Vec2 slowFieldDrag = Player.current.body.getLinearVelocity().mul(Player.PLAYER_SLOW_FIELD_DRAG_COEFFICIENT);
            this.body.applyForce(slowFieldDrag, this.getPhysicsPosition());
        }
        
        lifeSpan--;
        if (lifeSpan <= 0) {
            this.destroy();
        }
    }

    public void fireAtTarget(Body target, float speed) {
        Vec2 vecToTarget = target.getWorldCenter().sub(this.body.getWorldCenter());
        Vec2 unitToTarget = vecToTarget.mul(1.0f / vecToTarget.length());
        this.body.setLinearVelocity(unitToTarget.mul(speed));
    }
}
