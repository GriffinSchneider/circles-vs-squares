package circlesvssquares;

import java.awt.Color;
import java.util.concurrent.Callable;

import org.jbox2d.common.Vec2;

import processing.core.PConstants;

public class Button extends Node {

    float w, 
          h;
    Color fill,
          fillDown,
          stroke;
    boolean isDown;
    
    private Callable callback;
    
    Button(Vec2 pos_, float w_, float h_, Callable c_) {
        super(pos_);
        
        this.w = w_;
        this.h = h_;
        this.fill = Color.BLUE;
        this.fillDown = Color.CYAN;
        this.stroke = Color.BLACK;
        this.isDown = false;
        this.callback = c_;
    }

    @Override
    public void display(float width, float height) {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        
        if (this.isDown) cvs.fill(this.fillDown.getRGB());
        else cvs.fill(this.fill.getRGB());
        
        cvs.stroke(this.stroke.getRGB());
        cvs.rectMode(PConstants.CORNER);
        cvs.rect(pos.x, pos.y, w, h);
    }

    @Override
    public void update() {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        if (cvs.mouseClick) {
            if(cvs.mouseX>pos.x && cvs.mouseX <pos.x+w && 
                    cvs.mouseY>pos.y && cvs.mouseY <pos.y+h) {
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
        // TODO Auto-generated method stub
        
    }
}
