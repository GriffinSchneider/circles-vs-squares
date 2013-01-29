package circlesvssquares;

public abstract class Node {
    float x;
    float y;
    
    Node(float x_, float y_) {
        this.x = x_;
        this.y = y_;
    }
    
    public abstract void display(float width, float height);
    public abstract void update();

    public abstract void destroy();
 }
