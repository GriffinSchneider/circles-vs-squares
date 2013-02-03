package circlesvssquares;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import pbox2d.PBox2D;
import processing.core.PConstants;

class EndPoint extends Box2DObjectNode {

    // a boundary is a simple rectangle with x,y,width,and height
    float r;

    EndPoint(Vec2 pos_, float r, PBox2D box2d) {
        super(pos_, box2d);
        this.r = r;

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
        
        CircleShape cs = new CircleShape();
        cs.m_radius = box2d.scalarPixelsToWorld(r);
        
        // attached the shape to the body using a fixture
        body.createFixture(cs,1);
    }

    // draw the boundary, if it were at an angle we'd have to do something fancier
    @Override
    public void display(float width, float height) {
        CirclesVsSquares cvs = CirclesVsSquares.instance();
        cvs.fill(0, 0, 255);
        cvs.stroke(0);
        cvs.rectMode(PConstants.CENTER);
        
        Vec2 pos = this.getGraphicsPosition();
        cvs.ellipse(pos.x, pos.y, r*2, r*2);
    }

    @Override
    public void update() {
    }
}
