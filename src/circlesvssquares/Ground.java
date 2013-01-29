package circlesvssquares;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import pbox2d.PBox2D;
import processing.core.PConstants;

class Ground extends Node {

    // a boundary is a simple rectangle with x,y,width,and height
    float w;
    float h;

    // but we also have to make a body for box2d to know about it
    Body b;

    Ground(float x,float y, float w, float h, PBox2D box2d) {
        super(x, y);
        this.w = w;
        this.h = h;

        // define the polygon
        PolygonShape sd = new PolygonShape();
        // figure out the box2d coordinates
        float box2dw = box2d.scalarPixelsToWorld(w/2);
        float box2dh = box2d.scalarPixelsToWorld(h/2);
        // we're just a box
        sd.setAsBox(box2dw, box2dh);

        // create the body
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(box2d.coordPixelsToWorld(x,y));
        b = box2d.createBody(bd);

        // attached the shape to the body using a fixture
        b.createFixture(sd,1);

        b.setUserData(this);
    }

    // draw the boundary, if it were at an angle we'd have to do something fancier
    public void display(float width, float height) {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.fill(0);
        cvs.stroke(0);
        cvs.rectMode(PConstants.CENTER);
        cvs.rect(x,y,w,h);
    }

}
