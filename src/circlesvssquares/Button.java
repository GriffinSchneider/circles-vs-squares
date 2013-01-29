package circlesvssquares;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.jbox2d.common.Vec2;

import processing.core.PConstants;

public class Button extends Node {
    private static ArrayList<Button> buttonList = new ArrayList<Button>();
    
    public static void updateButtons() {
        for (int i = buttonList.size()-1; i >=0; i--) {
            Button b = buttonList.get(i);
            b.update();
        }
    }
    
    public static void displayButtons(float width, float height) {
        for (int i = buttonList.size()-1; i >=0; i--) {
            Button b = buttonList.get(i);
            b.display(width, height);
        }
    }
    
    private static void uncheckButtons() {
        for (int i = buttonList.size()-1; i >=0; i--) {
            Button b = buttonList.get(i);
            if (b.isCheckbox) b.isDown = false;
        }
    }
    
    public static Button createButton(Vec2 pos_, float w_, float h_, Callable c_) {
        Button b = new Button(pos_, w_, h_, c_);
        return b;
    }
    
    public static Button createCheckBox(Vec2 pos_, float w_, float h_, Callable c_) {
        Button b = new Button(pos_, w_, h_, c_);
        b.isCheckbox = true;
        return b;
    }
    
    float w, 
          h;
    String text;
    Color fill,
          fillDown,
          stroke;
    private boolean isDown;
    private boolean isCheckbox;
    
    private Callable callback;
    
    Button(Vec2 pos_, float w_, float h_, Callable c_) {
        super(pos_);
        
        this.text = "";
        this.w = w_;
        this.h = h_;
        this.fill = Color.BLUE;
        this.fillDown = Color.CYAN;
        this.stroke = Color.BLACK;
        this.isDown = false;
        this.isCheckbox = false;
        this.callback = c_;
        
        buttonList.add(this);
    }

    @Override
    public void display(float width, float height) {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        
        if (this.isDown) cvs.fill(this.fillDown.getRGB());
        else cvs.fill(this.fill.getRGB());
        
        cvs.stroke(this.stroke.getRGB());
        cvs.rectMode(PConstants.CORNER);
        cvs.rect(pos.x, pos.y, w, h);
        
        cvs.fill(0);
        cvs.text(this.text, pos.x+1, 20);
    }

    @Override
    public void update() {
        if (!isCheckbox) this.isDown = false;
        
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        if (cvs.mouseClick) {
            if(cvs.mouseX>pos.x && cvs.mouseX <pos.x+w && 
                    cvs.mouseY>pos.y && cvs.mouseY <pos.y+h) {
                if (!this.isDown && this.isCheckbox) Button.uncheckButtons();
                
                this.isDown = !this.isDown;
                
                try {
                    callback.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void destroy() {
        buttonList.remove(this);
    }
}
