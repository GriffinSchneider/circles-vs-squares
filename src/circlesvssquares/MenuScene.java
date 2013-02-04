package circlesvssquares;

import org.jbox2d.common.Vec2;

import processing.core.PApplet;
import processing.core.PConstants;

public class MenuScene extends Scene {

    private ParallaxBackground background;

    public MenuScene(CirclesVsSquares app_) {
        super(app_);
    }

    @Override
    public void draw() {
        this.background.display(this.app.width, this.app.height, 5, .7f * PApplet.sin((float)this.app.frameCount / 50f));
        
        super.draw();

        this.app.textSize(50);
        this.app.textAlign(PConstants.CENTER);
        this.app.text("Circles vs. Squares", this.app.width/2, this.app.height/2 - 100);
    }

    @Override
    public void init() {
        this.background = new ParallaxBackground(this.app);
        
        Button playButton = Button.createButton(new Vec2(this.app.width/2 - 50, this.app.height/2), 100, 30, new ButtonCallback() {
            @Override
            public void call() {
                app.changeScene(new GameScene(app, 1, false));
            }
        });
        playButton.text = "Play";
        
        Button levelButton = Button.createButton(new Vec2(this.app.width/2 - 50, this.app.height/2 + 60), 100, 30, new ButtonCallback() {
            @Override
            public void call() {
                app.changeScene(new GameScene(app, 1, true));
            }
        });
        levelButton.text = "Level Editor";
        
        Button aboutButton = Button.createButton(new Vec2(this.app.width/2 - 50, this.app.height/2 + 120), 100, 30, new ButtonCallback() {
            @Override
            public void call() {
                app.changeScene(new AboutScene(app));
            }
        });
        aboutButton.text = "About";
    }
}
