package circlesvssquares;

import java.awt.Frame;

import processing.core.PApplet;

public abstract class Scene {
    protected CirclesVsSquares app;
    
    public Scene(CirclesVsSquares app_) {
        this.app = app_;
    }
    
    public abstract void init();
    
    public void draw() {
        Button.displayButtons();
    }
    
    public void update() {
        Button.updateButtons();
    }
    
    public void cleanUp() {
        Button.removeButtons();
    }
}
