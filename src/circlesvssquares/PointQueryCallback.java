package circlesvssquares;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

public class PointQueryCallback implements QueryCallback {
    private Vec2 pos;
    private Body selectedBody = null;
    private PointQueryCallbackFilter filter = null;
    
    public interface PointQueryCallbackFilter {
        public boolean filter(Body body);
    }

    public PointQueryCallback(Vec2 pos_) {
        this.pos = pos_;
    }
    
    public PointQueryCallback(Vec2 pos_, PointQueryCallbackFilter filter) {
        this.pos = pos_;
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