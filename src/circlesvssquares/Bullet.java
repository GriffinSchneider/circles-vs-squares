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

    enum BulletType {
        Simple,
        Cluster
    }
    
    public static Bullet createSimpleBullet(Vec2 pos, PBox2D box2d) {
        return new Bullet(pos, 20, 20, box2d, BulletType.Simple);
    }
    
    public static Bullet createClusterBullet(Vec2 pos, PBox2D box2d) {
        return new Bullet(pos, 30, 30, box2d, BulletType.Cluster);
    }
    
    BulletType type;
    
    // a boundary is a simple rectangle with x,y,width,and height
    float w;
    float h;
    
    float lifeSpan;

    Bullet(Vec2 pos, float w, float h, PBox2D box2d, BulletType type) {
        super(pos, box2d);
        this.type = type;
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
        fd.friction = 0.35f;
        fd.restitution = 0.2f;

        
        switch(this.type) {
        default:
        case Simple:
            lifeSpan = 300;
            break;
        case Cluster:
            lifeSpan = 15;
            break;
        }
        
        // attached the shape to the body using a fixture
        body.createFixture(fd);

        body.setUserData(this);
        
        objectList.add(this);
    }

    // draw the boundary, if it were at an angle we'd have to do something fancier
    @Override
    public void display(float width, float height) {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.pushStyle();
        switch(this.type) {
        case Simple:
            cvs.fill(255, 160, 100, lifeSpan);
            break;
        case Cluster:
            cvs.fill(255, 160, 0);
            break;
        }
        cvs.stroke(0);
        cvs.rectMode(PConstants.CENTER);
        cvs.rect(this.getGraphicsPosition().x,this.getGraphicsPosition().y,w,h);
        cvs.popStyle();
    }

    @Override
    public void update() {
        if (this.isInSlowField) {
            CirclesVsSquares cvs = CirclesVsSquares.instance();
            GameScene scene = (GameScene) cvs.getCurrentScene();
            Player player = scene.player;
            
            Vec2 vel = this.body.getLinearVelocity();
            float newXVel = vel.x > 0 ? Math.min(vel.x, 0.5f) : Math.max(vel.x, -0.5f);
            float newYVel = vel.y > 0 ? Math.min(vel.y, 0.5f) : Math.max(vel.y, -0.5f);
            // Cap speed
            this.body.setLinearVelocity(new Vec2(newXVel, newYVel));
            // Drag with velocity of slow field
            Vec2 slowFieldDrag = player.body.getLinearVelocity().mul(Player.PLAYER_SLOW_FIELD_DRAG_COEFFICIENT);
            this.body.applyForce(slowFieldDrag, this.getPhysicsPosition());
        }
        
        switch(this.type) {
        case Simple:
            lifeSpan--;
            if (lifeSpan <= 0) {
                this.destroy();
            }
            break;
        case Cluster:
            lifeSpan--;
            if (lifeSpan <= 0) {
                CirclesVsSquares cvs = CirclesVsSquares.instance();
                GameScene scene = (GameScene) cvs.getCurrentScene();
                Player player = scene.player;
                
                int i;
                for (i = 0; i < 2; i++) {
                    Bullet bullet = Bullet.createSimpleBullet(this.getPhysicsPosition(), box2d);
                    bullet.fireAtTarget(player.body, 50);
                }
                
                this.destroy();
            }
            break;
        }
    }

    public void fireAtTarget(Body target, float speed) {
        Vec2 vecToTarget = target.getWorldCenter().sub(this.body.getWorldCenter());
        Vec2 unitToTarget = vecToTarget.mul(1.0f / vecToTarget.length());
        this.body.setLinearVelocity(unitToTarget.mul(speed));
    }
}

