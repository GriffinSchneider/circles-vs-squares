package circlesvssquares;

import org.jbox2d.common.Vec2;

public abstract class Node {

    public Vec2 pos;
    
    Node(float x, float y) {
        this.pos = new Vec2(x, y);
    }
    
    Node(Vec2 pos) {
        this.pos = pos;
    }
    
    public abstract void display(float width, float height);
    public abstract void update();

    public abstract void destroy();
   
 }
