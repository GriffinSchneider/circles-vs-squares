package circlesvssquares;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

 class CustomListener implements ContactListener {
  CustomListener() {
  }

  // This function is called when a new collision occurs
   public void beginContact(Contact cp) {
    // Get both fixtures
    Fixture f1 = cp.getFixtureA();
    Fixture f2 = cp.getFixtureB();
    // Get both bodies
    Body b1 = f1.getBody();
    Body b2 = f2.getBody();
    // Get our objects that reference these bodies
    Object o1 = b1.getUserData();
    Object o2 = b2.getUserData();


	Player p = null;
	Object other = null;
    if (o1.getClass() == Player.class) {
    	p = (Player) o1;
    	other = o2;
    } 
    else if (o2.getClass() == Player.class) {
    	p = (Player) o2;
    	other = o1;
    } 
    
    if (p != null && other != null) {
    	if (other.getClass() == Boundary.class) {
    		p.canMove = true;
    	}
    }
  }

   public void endContact(Contact cp) {
    // Get both fixtures
    Fixture f1 = cp.getFixtureA();
    Fixture f2 = cp.getFixtureB();
    // Get both bodies
    Body b1 = f1.getBody();
    Body b2 = f2.getBody();
    // Get our objects that reference these bodies
    Object o1 = b1.getUserData();
    Object o2 = b2.getUserData();


	Player p = null;
	Object other = null;
    if (o1.getClass() == Player.class) {
    	p = (Player) o1;
    	other = o2;
    } 
    else if (o2.getClass() == Player.class) {
    	p = (Player) o2;
    	other = o1;
    } 
    
    if (p != null && other != null) {
    	if (other.getClass() == Boundary.class) {
    		p.canMove = false;
    	}
    }
  }

   public void preSolve(Contact contact, Manifold oldManifold) {
    // TODO Auto-generated method stub
  }

   public void postSolve(Contact contact, ContactImpulse impulse) {
    // TODO Auto-generated method stub
  }
}
