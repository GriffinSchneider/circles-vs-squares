package circlesvssquares;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import pbox2d.PBox2D;

public abstract class Box2DObjectNode extends Node {
    
    private Vec2 pos;
    
    public Body body;
    PBox2D box2d;
    
    Box2DObjectNode(Vec2 pos, PBox2D box2d) {
        super(pos);
        this.box2d = box2d;
    } 

    public Vec2 getPhysicsPosition() {
        return this.body.getWorldCenter();
    }

    public Vec2 getGraphicsPosition() {
        return box2d.getBodyPixelCoord(this.body);
    }

}
