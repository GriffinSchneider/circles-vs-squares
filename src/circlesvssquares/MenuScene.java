package circlesvssquares;

import org.jbox2d.common.Vec2;

public class MenuScene extends Scene {

    public MenuScene(CirclesVsSquares app_) {
        super(app_);
    }

    @Override
    public void draw() {
        this.app.background(255);
        super.draw();
        this.app.textSize(50);
        this.app.text("Circle vs Squares", this.app.width/2, this.app.height/2);
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
    }

    @Override
    public void init() {
        Button playButton = Button.createButton(new Vec2(this.app.width/2, this.app.height/2), 100, 30, new ButtonCallback() {
            @Override
            public void call() {
                CirclesVsSquares cvs = CirclesVsSquares.instance();
                cvs.changeScene(new GameScene(app, 1, false));
            }
        });
        playButton.text = "Play";
        
        Button levelButton = Button.createButton(new Vec2(this.app.width/2, this.app.height/2 + 60), 100, 30, new ButtonCallback() {
            @Override
            public void call() {
                CirclesVsSquares cvs = CirclesVsSquares.instance();
                cvs.changeScene(new GameScene(app, 1, true));
            }
        });
        levelButton.text = "Level Editor";
    }
}
