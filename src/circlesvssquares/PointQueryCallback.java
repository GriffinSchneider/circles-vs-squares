package circlesvssquares;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

public class PointQueryCallback implements QueryCallback {
    private Vec2 pos;
    private Body selectedBody = null;
    
    public PointQueryCallback(Vec2 pos_) {
        this.pos = pos_;
    }
  
    @Override
    public boolean reportFixture(Fixture fixture) {
        this.selectedBody = fixture.getBody();
        return false;
    }
    
    public Body getSelectedBody() {
        return this.selectedBody;
    }
}
