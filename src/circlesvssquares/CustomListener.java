package circlesvssquares;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

class CustomListener implements ContactListener {
    CustomListener() {
    }

    // This function is called when a new collision occurs
    @Override
    public void beginContact(Contact cp) {
        Object o1 = cp.getFixtureA().getBody().getUserData();
        Object o2 = cp.getFixtureB().getBody().getUserData();

        if (o1 != null && o1 instanceof Box2DObjectNode) {
            ((Box2DObjectNode)o1).collisionBegan(cp);
        }

        if (o2 != null && o2 instanceof Box2DObjectNode) {
            ((Box2DObjectNode)o2).collisionBegan(cp);
        }
    }

    @Override
    public void endContact(Contact cp) {
        Object o1 = cp.getFixtureA().getBody().getUserData();
        Object o2 = cp.getFixtureB().getBody().getUserData();

        if (o1 != null && o1 instanceof Box2DObjectNode) {
            ((Box2DObjectNode)o1).collisionEnded(cp);
        }

        if (o2 != null && o2 instanceof Box2DObjectNode) {
            ((Box2DObjectNode)o2).collisionEnded(cp);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // TODO Auto-generated method stub
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // TODO Auto-generated method stub
    }
}
