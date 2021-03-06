package circlesvssquares;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import pbox2d.PBox2D;
import processing.core.PConstants;

class Ground extends Box2DObjectNode {

    // a boundary is a simple rectangle with x,y,width,and height
    float w;
    float h;

    Ground(Vec2 pos_, float w, float h, PBox2D box2d) {
        super(pos_, box2d);
        this.w = w;
        this.h = h;

        // create the body
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(pos_);
        body = box2d.createBody(bd);
        
        this.updateBody();

        body.setUserData(this);
        
        objectList.add(this);
    }
    
    public void updateBody() {
        Fixture f = this.body.getFixtureList();
        if (f != null) body.destroyFixture(f);
        
        // define the polygon
        PolygonShape sd = new PolygonShape();
        // figure out the box2d coordinates
        float box2dw = box2d.scalarPixelsToWorld(w/2);
        float box2dh = box2d.scalarPixelsToWorld(h/2);
        // we're just a box
        sd.setAsBox(box2dw, box2dh);
        
        // attached the shape to the body using a fixture
        body.createFixture(sd,1);
    }

    // draw the boundary, if it were at an angle we'd have to do something fancier
    @Override
    public void display(float width, float height) {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.pushStyle();
        cvs.fill(0);
        cvs.stroke(0);
        cvs.rectMode(PConstants.CENTER);
        
        Vec2 pos = this.getGraphicsPosition();
        cvs.rect(pos.x,pos.y,w,h);
        cvs.popStyle();
    }

    @Override
    public void update() {
    }
}
