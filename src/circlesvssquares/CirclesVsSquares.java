package circlesvssquares;

import java.awt.event.KeyEvent;

import processing.core.PApplet;

public class CirclesVsSquares extends PApplet {

    private static final long serialVersionUID = 7397694443868429500L;

    private static CirclesVsSquares instance;
    public static CirclesVsSquares instance() {
        return instance; 
    }

    public static void main(String[] args) {
        PApplet.main(new String[] { CirclesVsSquares.class.getName() });
    }

    public boolean[] keys = new boolean[526];
    boolean mousePressed = false;
    boolean mouseClick = false;
    
    public boolean checkKey(String k) {
        for(int i = 0; i < keys.length; i++) {
            if(KeyEvent.getKeyText(i).toLowerCase().equals(k.toLowerCase())) {
                return keys[i];
            }
        }
        return false;
    }
    
    private Scene currentScene = null;
    private Scene nextScene = null;
    
    public Scene getCurrentScene() {
        return currentScene;
    }
    
    @Override
    public void setup() {
        instance = this;
        size(1000, 500);
        smooth();
        
        nextScene = new MenuScene(this);
    }

    @Override
    public void draw() {
        if (nextScene != null) {
            if (currentScene != null) currentScene.cleanUp();
            currentScene = nextScene;
            currentScene.init();
            nextScene = null;
        }
        
        if (currentScene != null) {
            currentScene.update();
            currentScene.draw();
        }
    }
    
    @Override
    public void keyPressed() { 
        keys[keyCode] = true;
    }

    @Override
    public void keyReleased() { 
        keys[keyCode] = false; 
    }

    @Override
    public void mousePressed() {
        mousePressed = true;
        mouseClick = true;
    }

    @Override
    public void mouseReleased() {
        mousePressed = false;
    }
    
    public void changeScene(Scene scene) {
        nextScene = scene;
    }
}
