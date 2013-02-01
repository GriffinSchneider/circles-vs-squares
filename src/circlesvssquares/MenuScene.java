package circlesvssquares;

import org.jbox2d.common.Vec2;

public class MenuScene extends Scene {

    public MenuScene(CirclesVsSquares app_) {
        super(app_);
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
    }

    @Override
    public void init() {
        Button playButton = Button.createButton(new Vec2(this.app.width/2, this.app.height/2), 100, 100, new ButtonCallback() {
            @Override
            public void call() {
                CirclesVsSquares cvs = CirclesVsSquares.instance();
                cvs.changeScene(new GameScene(app));
            }
        });
        playButton.text = "Play";
    }
}
