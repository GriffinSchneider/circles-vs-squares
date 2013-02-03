package circlesvssquares;


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
