package com.bounce.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bounce.game.Bounce;
import com.bounce.game.gamesys.GameManager;

public class WinScreen implements Screen {

    private Bounce game;
    private Stage stage;

    private float countDown;

    public WinScreen(Game game) {
        this.game = (Bounce) game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GameManager.WINDOW_WIDTH / 2, GameManager.WINDOW_HEIGHT /2));

        Image bgImage = new Image(new Texture("winbg.png"));

        Image winImage = new Image(new Texture("youwin.png"));
        winImage.setSize(50f*GameManager.SCALE, 50f*GameManager.SCALE);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.setBackground(bgImage.getDrawable());
        table.add(winImage).size(winImage.getWidth(), winImage.getHeight());

        stage.addActor(table);

        countDown = 1f;

        GameManager.instance.getAssetManager().finishLoading();

    }

    public void update(float delta) {
        countDown -= delta;

        if (countDown < 0.0f) {
            game.setScreen(new LevelScreen(game));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        update(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
