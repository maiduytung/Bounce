package com.bounce.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bounce.game.gamesys.GameManager;

public class Hud implements Disposable {

    private Stage stage;

    private int timeLeft;

    private Label characterCountLabel;
    private Label timeLabel;
    private Label levelLabel;

    private Label coinCountLabel;

    private float accumulator;

    private BitmapFont font;

    public Hud(SpriteBatch batch) {

        Viewport viewport = new FitViewport(GameManager.WINDOW_WIDTH / 2f, GameManager.WINDOW_HEIGHT / 2f, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        timeLeft = 300;

        font = new BitmapFont(Gdx.files.internal("fonts/Fixedsys500c.fnt"));
        font.getData().setScale(GameManager.SCALE/3);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        Image chickenImg = new Image(new Texture("icon/chicken.png"));
        chickenImg.setSize(6.0f*GameManager.SCALE, 6.0f*GameManager.SCALE);

        Label timeTextLabel = new Label("TIME", style);
        Label levelTextLabel = new Label("WORLD", style);
        Label coinTextLabel = new Label("COIN", style);

        characterCountLabel = new Label("3", style);
        timeLabel = new Label(intToString(timeLeft, 3), style);
        levelLabel = new Label("1", style);
        coinCountLabel = new Label(intToString(GameManager.instance.getCoins(), 2), style);

        Table table = new Table();
        table.top();
        table.setFillParent(true);
        table.padTop(2.0f);

        table.add().expandX();
        table.add(chickenImg).size(chickenImg.getWidth(), chickenImg.getHeight()).expandX();
        table.add(coinTextLabel).expandX();
        table.add(levelTextLabel).expandX();
        table.add(timeTextLabel).expandX();
        table.add().expandX();

        table.row();

        table.add().expandX();
        table.add(characterCountLabel).expandX();
        table.add(coinCountLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(timeLabel).expandX();

        stage.addActor(table);

        accumulator = 0;
    }

    public void setLevel(String level) {
        levelLabel.setText(level);
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void draw() {
        characterCountLabel.setText("x" + intToString(GameManager.instance.getCharacter(),1));
        stage.draw();

    }

    public void update(float delta) {
        accumulator += delta;
        if (accumulator > 1.0f) {
            if (timeLeft > 0)
                timeLeft -= 1;
            accumulator -= 1.0f;
            timeLabel.setText(intToString(timeLeft, 3));
        }

        coinCountLabel.setText(intToString(GameManager.instance.getCoins(), 2));

        stage.act(delta);

    }

    private String intToString(int value, int length) {
        String valueStr = Integer.toString(value);
        StringBuilder result = new StringBuilder();
        if (valueStr.length() < length) {
            for (int i = 0; i < length - valueStr.length(); i++) {
                result.append(0);
            }
        }
        result.append(valueStr);
        return result.toString();
    }

    @Override
    public void dispose() {
        font.dispose();
        stage.dispose();
    }
}
