package com.bounce.game.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bounce.game.gamesys.GameManager;

public class TouchController implements Disposable {

    private Stage stage;

    boolean upPressed, downPressed, leftPressed, rightPressed;

    boolean isPause, isExit;

    public TouchController(SpriteBatch batch) {

        Viewport viewport = new FitViewport(GameManager.V_WIDTH, GameManager.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch(keycode){
                    case Input.Keys.UP:
                        upPressed = true;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = true;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = true;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = true;
                        break;
                }
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch(keycode){
                    case Input.Keys.UP:
                        upPressed = false;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = false;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = false;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = false;
                        break;
                }
                return true;
            }
        });

        Gdx.input.setInputProcessor(stage);

        //region Move

        Table table = new Table();
        table.left().bottom();

        Image upImg = new Image(new Texture("icon/jump.png"));
        upImg.setSize(2.5f, 2.5f);
        upImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Image leftImg = new Image(new Texture("icon/prev.png"));
        leftImg.setSize(2.5f, 2.5f);
        leftImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        Image rightImg = new Image(new Texture("icon/next.png"));
        rightImg.setSize(2.5f, 2.5f);
        rightImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        table.add(leftImg).size(leftImg.getWidth(), leftImg.getHeight()).padLeft(1.0f);
        table.add();
        table.add(rightImg).size(rightImg.getWidth(), rightImg.getHeight()).padLeft(1.0f);
        table.add();
        table.add(upImg).size(upImg.getWidth(), upImg.getHeight()).padLeft(20.0f);
        table.padBottom(2f);

        stage.addActor(table);

        //endregion

        //region Menu
        isPause = false;

        Table menu = new Table();
        menu.top();
        menu.setFillParent(true);
        menu.padTop(0.1f);

        final Image pauseImg = new Image(new Texture("icon/pause.png"));
        pauseImg.setSize(1.1f, 1.1f);
        pauseImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!isPause){
                    GameManager.instance.setTimeScale(0);
                    GameManager.instance.setMute(true);
                    isPause = true;
                } else {
                    GameManager.instance.setTimeScale(1.5f);
                    GameManager.instance.setMute(false);
                    isPause = false;
                }
                return true;
            }
        });

        isExit = false;
        final Image exitImg = new Image(new Texture("icon/exit.png"));
        exitImg.setSize(1.1f, 1.1f);
        exitImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isExit = true;
                return true;
            }

        });

        menu.add(exitImg).size(exitImg.getWidth(),exitImg.getHeight()).expandX();
        menu.add().expandX();
        menu.add().expandX();
        menu.add().expandX();
        menu.add().expandX();
        menu.add(pauseImg).size(pauseImg.getWidth(),pauseImg.getHeight()).expandX();

        stage.addActor(menu);

        //region Menu

    }

    public void draw() {
        stage.draw();
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isExit() {
        return isExit;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
