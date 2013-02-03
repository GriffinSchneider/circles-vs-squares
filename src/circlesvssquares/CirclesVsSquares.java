package circlesvssquares;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import pbox2d.PBox2D;

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
    boolean mousePressed;
    boolean mouseClick;
    
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
        
        resetValues();
        nextScene = new MenuScene(this);
        
        this.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                resetValues();
            }
            
        });
    }

    private void resetValues() {
        this.mouseClick = false;
        this.mousePressed = false;
    }
    
    @Override
    public void draw() {
        if (nextScene != null) {
            if (currentScene != null) currentScene.cleanUp();
            currentScene = nextScene;
            
            // Reset mouseClick to avoid double clicks
            resetValues();
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
