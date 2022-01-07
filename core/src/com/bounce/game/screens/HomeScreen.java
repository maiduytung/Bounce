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

public class HomeScreen implements Screen {

    private Bounce game;
    private Stage stage;

    public HomeScreen(Bounce game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(GameManager.WINDOW_WIDTH / 2, GameManager.WINDOW_HEIGHT / 2));

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/Fixedsys500c.fnt"));
        font.getData().setScale(GameManager.SCALE/2);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        final Label.LabelStyle selectStyle = new Label.LabelStyle(font, Color.GREEN);

        GameManager.instance.playMusic("homemusic.ogg");

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.center().top();

        Image bgImg = new Image(new Texture("background.png"));
        Image logoImg = new Image(new Texture("logo.png"));
        logoImg.setSize(45.0f*GameManager.SCALE, 15.0f*GameManager.SCALE);

        table.setFillParent(true);
        table.setBackground(bgImg.getDrawable());

        table.padTop(32.0f);
        table.add(logoImg).expandX().size(logoImg.getWidth(), logoImg.getHeight()).center();



        final Label playLabel = new Label("PLAY", style);
        playLabel.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                playLabel.setStyle(selectStyle);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new LevelScreen(game));
            }
        });

        final Label highscoreLabel = new Label("HIGHSCORE", style);
        highscoreLabel.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                highscoreLabel.setStyle(selectStyle);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new HighScoreScreen(game));
            }
        });

        final Label exitLabel = new Label("EXIT", style);
        exitLabel.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                exitLabel.setStyle(selectStyle);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });

        table.row();
        table.add(playLabel).padTop(16.0f*GameManager.SCALE);
        table.row();
        table.add(highscoreLabel);
        table.row();
        table.add(exitLabel);
        table.row();

        final Table muteTable = new Table();
        muteTable.bottom().right();
        muteTable.setFillParent(true);

        final Image unmuteImg = new Image(new Texture("icon/audio.png"));
        final Image muteImg = new Image(new Texture("icon/mute.png"));
        final Image audioImg = new Image();
        audioImg.setDrawable(unmuteImg.getDrawable());
        audioImg.setSize(10.0f*GameManager.SCALE, 10.0f*GameManager.SCALE);
        audioImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                audioImg.setSize(8.0f*GameManager.SCALE, 8.0f*GameManager.SCALE);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!GameManager.instance.isMute()) {
                    audioImg.setDrawable(muteImg.getDrawable());
                    audioImg.setSize(10.0f * GameManager.SCALE, 10.0f * GameManager.SCALE);
                    GameManager.instance.setMute(true);
                } else {
                    audioImg.setDrawable(unmuteImg.getDrawable());
                    audioImg.setSize(10.0f * GameManager.SCALE, 10.0f * GameManager.SCALE);
                    GameManager.instance.setMute(false);
                }
            }
        });

        muteTable.add(audioImg).size(audioImg.getWidth(), audioImg.getHeight());

        stage.addActor(table);
        stage.addActor(muteTable);

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
