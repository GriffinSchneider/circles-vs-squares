package circlesvssquares;

import org.jbox2d.common.Vec2;

import processing.core.PApplet;
import processing.core.PConstants;

public class AboutScene extends Scene {

    private ParallaxBackground background;

    public AboutScene(CirclesVsSquares app_) {
        super(app_);
    }

    @Override
    public void draw() {
        this.background.display(this.app.width, this.app.height, 5, 1f * PApplet.sin((float)this.app.frameCount / 50f));
        
        super.draw();
        
        this.app.textSize(50);
        this.app.textAlign(PConstants.CENTER);
        this.app.text("About", this.app.width/2, this.app.height/2 - 100);
        this.app.textSize(30);
        this.app.text("Programmers:\n Griffin Schneider \n TJ Higgins", this.app.width/2, this.app.height/2);
    }

    @Override
    public void init() {
        Button backButton = Button.createButton(new Vec2(30, 30), 100, 30, new ButtonCallback() {
            @Override
            public void call() {
                app.changeScene(new MenuScene(app));
            }
        });
        backButton.text = "<- Back";
        this.background = new ParallaxBackground(this.app);
    }
}
