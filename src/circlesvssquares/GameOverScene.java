package circlesvssquares;

import org.jbox2d.common.Vec2;

import processing.core.PApplet;
import processing.core.PConstants;

public class GameOverScene extends Scene {

    private ParallaxBackground background;

    public GameOverScene(CirclesVsSquares app_) {
        super(app_);
    }

    @Override
    public void draw() {
        this.background.display(this.app.width, this.app.height, 5, 1f * PApplet.sin((float)this.app.frameCount / 50f));
        
        super.draw();
        
        Session s = Session.instance();
        this.app.textSize(50);
        this.app.textAlign(PConstants.CENTER);
        this.app.text("You Won!", this.app.width/2, this.app.height/2 - 100);
        this.app.textSize(30);
        this.app.text("Restarts used: " + s.resetsUsed + "\nRadius lost: " + s.radiusLost, this.app.width/2, this.app.height/2);
    }

    @Override
    public void init() {
        Button menuButton = Button.createButton(new Vec2(this.app.width/2-100, this.app.height/2+100), 200, 30, new ButtonCallback() {
            @Override
            public void call() {
                // Reset the session values
                Session.reset();
                app.changeScene(new MenuScene(app));
            }
        });
        menuButton.text = "Return to Main Menu";
        this.background = new ParallaxBackground(this.app);
    }
}
