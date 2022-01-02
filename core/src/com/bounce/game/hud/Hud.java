package com.bounce.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bounce.game.gamesys.GameManager;

public class Hud implements Disposable {

    private Stage stage;

    private int timeLeft;

    private Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;

    private Label coinCountLabel;

    private float accumulator;

    private BitmapFont font;

    public Hud(SpriteBatch batch) {

        Viewport viewport = new FitViewport(GameManager.WINDOW_WIDTH / 1.5f, GameManager.WINDOW_HEIGHT / 1.5f, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        timeLeft = 300;

        font = new BitmapFont(Gdx.files.internal("fonts/Fixedsys500c.fnt"));
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        Label scoreTextLabel = new Label("SCORE", style);
        Label timeTextLabel = new Label("TIME", style);
        Label levelTextLabel = new Label("WORLD", style);
        Label coinTextLabel = new Label("COIN", style);

        scoreLabel = new Label("", style);
        timeLabel = new Label(intToString(timeLeft, 3), style);
        levelLabel = new Label("1", style);
        coinCountLabel = new Label(intToString(GameManager.instance.getCoins(), 2), style);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        table.add(scoreTextLabel).expandX().padTop(6.0f);
        table.add(coinTextLabel).expandX().padTop(6.0f);
        table.add(levelTextLabel).expandX().padTop(6.0f);
        table.add(timeTextLabel).expandX().padTop(6.0f);

        table.row();

        table.add(scoreLabel).expandX();
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
        scoreLabel.setText(intToString(GameManager.instance.getScore(), 6));
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
