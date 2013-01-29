package circlesvssquares;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import pbox2d.PBox2D;
import processing.core.PConstants;

class Enemy extends Box2DObjectNode {

    // a boundary is a simple rectangle with x,y,width,and height
    float w;
    float h;
    
    float shotCount = 0;
    float shotDelay = 25;

    Enemy(Vec2 pos, float w, float h, PBox2D box2d) {
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
        cvs.fill(255, 0, 0);
        cvs.stroke(0);
        cvs.rectMode(PConstants.CENTER);
        cvs.rect(this.getGraphicsPosition().x,this.getGraphicsPosition().y,w,h);
    }

    @Override
    public void update() {
        shotCount++;
        if (shotCount > shotDelay) {
            shotCount = 0;
            this.shoot();
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    private void shoot() {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        
        // Get a unit vector pointing from us to the player
        Vec2 vecToPlayer = cvs.player.getPhysicsPosition().sub(this.getPhysicsPosition());
        Vec2 unitToPlayer = vecToPlayer.mul(1.0f / vecToPlayer.length());
        
        // Create bullet in the direction of the player, 2.5 units away from us
        Bullet bullet = Bullet.createSimpleBullet(unitToPlayer.mul(2.5f).add(this.getPhysicsPosition()), box2d);
        bullet.fireAtTarget(cvs.player.body, 50);
    }
}
