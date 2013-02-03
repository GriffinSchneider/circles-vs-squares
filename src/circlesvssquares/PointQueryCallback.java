package circlesvssquares;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

public class PointQueryCallback implements QueryCallback {
    private Body selectedBody = null;
    private PointQueryCallbackFilter filter = null;
    
    public interface PointQueryCallbackFilter {
        public boolean filter(Body body);
    }
    
    public PointQueryCallback(PointQueryCallbackFilter filter) {
        this.filter = filter;
    }
  
    @Override
    public boolean reportFixture(Fixture fixture) {
        Body body = fixture.getBody();
        if (this.filter == null || this.filter.filter(body)) {
            this.selectedBody = fixture.getBody();
            return false;
        }
        return true;
    }
    
    public Body getSelectedBody() {
        return this.selectedBody;
    }
}