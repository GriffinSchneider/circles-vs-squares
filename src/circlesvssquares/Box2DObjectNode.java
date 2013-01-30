package circlesvssquares;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import pbox2d.PBox2D;

public abstract class Box2DObjectNode {
    
    private Vec2 pos;
    
    public Body body;
    PBox2D box2d;
    
    Box2DObjectNode(Vec2 pos_, PBox2D box2d) {
        this.pos = pos_;
        this.box2d = box2d;
    } 

    public void setPhysicsPosition(Vec2 pos_) {
        //pos = pos_;
        body.setTransform(pos_, 0);
    }
    
    public Vec2 getPhysicsPosition() {
        return this.body.getWorldCenter();
    }

    public Vec2 getGraphicsPosition() {
        return box2d.getBodyPixelCoord(this.body);
    }

    public void destroy() {
        box2d.destroyBody(this.body);
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.objectList.remove(this);
    }
    
    public abstract void display(float width, float height);
    public abstract void update();
}
