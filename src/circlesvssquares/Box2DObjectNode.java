package circlesvssquares;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import pbox2d.PBox2D;

public abstract class Box2DObjectNode {
    
    protected static ArrayList<Box2DObjectNode> objectList = new ArrayList<Box2DObjectNode>();
    protected static ArrayList<Box2DObjectNode> toRemoveList = new ArrayList<Box2DObjectNode>();
    
    public static void clearObjects() {
        while (objectList.size() > 0) {
            objectList.get(0).destroy();
        }
    }
    
    public static void clearToRemove() {
        for (int i = toRemoveList.size()-1; i >=0; i--) {
            Box2DObjectNode n = toRemoveList.get(i);
            n.destroy();
        }
        toRemoveList.clear();
    }
    
    public boolean isInSlowField;
    
    public Body body;
    PBox2D box2d;
    
    Box2DObjectNode(Vec2 pos_, PBox2D box2d) {
        this.box2d = box2d;
    } 

    public void setPhysicsPosition(Vec2 pos_) {
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
        objectList.remove(this);
    }
    
    public void collisionBegan(Contact cp) {
        return;
    }

    public void collisionEnded(Contact cp) {
        return;
    }

    
    public abstract void display(float width, float height);
    public abstract void update();
}
