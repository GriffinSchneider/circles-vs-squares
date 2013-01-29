package circlesvssquares;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import pbox2d.PBox2D;
import processing.core.PConstants;

class Bullet extends Node {

    public static Bullet createSimpleBullet(float x, float y, PBox2D box2d) {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        Bullet bullet = new Bullet(x, y, 10, 10, box2d);
        cvs.objectList.add(bullet);
        return bullet;
    }
    
    // a boundary is a simple rectangle with x,y,width,and height
    float w;
    float h;

    // but we also have to make a body for box2d to know about it
    Body body;
    PBox2D box2d;
    
    float lifeSpan = 500;

    Bullet(float x,float y, float w, float h, PBox2D box2d) {
        super(x, y);
        this.w = w;
        this.h = h;
        this.box2d = box2d;

        // define the polygon
        PolygonShape sd = new PolygonShape();
        // figure out the box2d coordinates
        float box2dw = box2d.scalarPixelsToWorld(w/2);
        float box2dh = box2d.scalarPixelsToWorld(h/2);
        // we're just a box
        sd.setAsBox(box2dw, box2dh);

        // create the body
        BodyDef bd = new BodyDef();
        bd.type = BodyType.KINEMATIC;
        bd.position.set(box2d.coordPixelsToWorld(x,y));
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
        Vec2 pos = box2d.getBodyPixelCoord(body);
        this.x = pos.x;
        this.y = pos.y;
        
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.fill(255, 160, 0, lifeSpan);
        cvs.stroke(0);
        cvs.rectMode(PConstants.CENTER);
        cvs.rect(x,y,w,h);
    }
    
    public void destroy() {
        box2d.destroyBody(this.body);
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.objectList.remove(this);
    }

    @Override
    public void update() {
        lifeSpan--;
        if (lifeSpan <= 0) {
            this.destroy();
        }
    }

    public void fireAtTarget(Node target) {
        float angle = (float) Math.atan2(this.y - target.y, target.x - this.x);
        float speed = 10;
        
        this.body.setLinearVelocity(new Vec2((float) (speed * Math.cos(angle)), (float) (speed * Math.sin(angle))));
    }
}
