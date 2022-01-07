package com.bounce.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bounce.game.Bounce;
import com.bounce.game.gamesys.GameManager;

public class LevelScreen implements Screen {

    private Bounce game;
    private Stage stage;

    public LevelScreen(Bounce game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(GameManager.WINDOW_WIDTH / 2, GameManager.WINDOW_HEIGHT / 2));

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/Fixedsys500c.fnt"));
        font.getData().setScale(GameManager.SCALE/2);
        final Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        final Label.LabelStyle selectStyle = new Label.LabelStyle(font, Color.GREEN);

        GameManager.instance.playMusic("homemusic.ogg");

        Gdx.input.setInputProcessor(stage);

        // region background and logo
        Table table = new Table();
        table.top();

        Image bgImg = new Image(new Texture("background.png"));
        Image logoImg = new Image(new Texture("logo.png"));
        logoImg.setSize(45.0f*GameManager.SCALE, 15.0f*GameManager.SCALE);

        table.setFillParent(true);
        table.setBackground(bgImg.getDrawable());

        table.padTop(32.0f);
        table.add(logoImg).expandX().size(logoImg.getWidth(), logoImg.getHeight());
        stage.addActor(table);
        //endregion

        //region Data
        Table dataTable = new Table();
        dataTable.top().padTop(16.0f);
        dataTable.setFillParent(true);

        final Label level1Label = new Label("Level 1", style);
        final Label level2Label = new Label("Level 2", style);
        
        final Image level1Img = new Image(new Texture("icon/level1.png"));
        level1Img.setSize(15.0f*GameManager.SCALE, 15.0f*GameManager.SCALE);
        level1Img.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                level1Label.setStyle(selectStyle);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new PlayScreen(game,1));
            }
        });

        final Image level2Img = new Image(new Texture("icon/lock.png"));
        level2Img.setSize(15.0f * GameManager.SCALE, 15.0f * GameManager.SCALE);

        if (GameManager.instance.checkUnlock(2)) {

            final Image level2ImgUnlock = new Image(new Texture("icon/level2.png"));
            level2Img.setDrawable(level2ImgUnlock.getDrawable());
            level2Img.setSize(15.0f * GameManager.SCALE, 15.0f * GameManager.SCALE);
            level2Img.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    level2Label.setStyle(selectStyle);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    game.setScreen(new WinScreen(game));
                }
            });
        }

        dataTable.padTop(32.0f);
        dataTable.add(level1Label).padTop(16.0f*GameManager.SCALE).expandX();
        dataTable.add(level2Label).padTop(16.0f*GameManager.SCALE).expandX();
        dataTable.row();
        dataTable.add(level1Img).size(level1Img.getWidth(), level1Img.getHeight()).expandX().padTop(16.0f);
        dataTable.add(level2Img).size(level2Img.getWidth(), level2Img.getHeight()).expandX().padTop(16.0f);

        stage.addActor(dataTable);
        //endregion Data

        final Table backTable = new Table();
        backTable.bottom();
        backTable.setFillParent(true);

        final Label backLabel = new Label("BACK", style);
        backLabel.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                backLabel.setStyle(selectStyle);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new HomeScreen(game));
            }
        });

        backTable.add(backLabel);

        stage.addActor(backTable);

    }

    @Override
    public void render(float delta) {
        stage.draw();
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
