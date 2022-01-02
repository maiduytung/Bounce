package com.bounce.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bounce.game.Bounce;
import com.bounce.game.actors.Character;
import com.bounce.game.actors.effects.BrickDebris;
import com.bounce.game.actors.effects.Effect;
import com.bounce.game.actors.effects.FlippingCoin;
import com.bounce.game.actors.effects.SpawningEffect;
import com.bounce.game.actors.enemies.Enemy;
import com.bounce.game.actors.items.*;
import com.bounce.game.actors.maptiles.MapTileObject;
import com.bounce.game.gamesys.GameManager;
import com.bounce.game.hud.Hud;
import com.bounce.game.hud.ScoreIndicator;
import com.bounce.game.utils.WorldContactListener;
import com.bounce.game.utils.WorldCreator;

import java.util.LinkedList;

public class PlayScreen implements Screen {
    private WorldCreator worldCreator;

    private Bounce game;

    public World world;

    private float accumulator;

    private OrthographicCamera camera;
    private Viewport viewport;

    private float cameraLeftLimit;
    private float cameraRightLimit;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private float mapWidth;

    private TextureAtlas textureAtlas;

    private Box2DDebugRenderer box2DDebugRenderer;
    private boolean renderB2DDebug;

    private Array<MapTileObject> mapTileObjects;
    private Array<Enemy> enemies;

    private Array<Item> items;
    private LinkedList<SpawningItem> itemSpawnQueue;

    private Array<Effect> effects;
    private LinkedList<SpawningEffect> effectSpawnQueue;

    private Character character;

    private Hud hud;
    private ScoreIndicator scoreIndicator;

    private boolean playingHurryMusic;
    private boolean playMusic;

    private float deathCountdown;

    private Stage levelCompletedStage;
    private boolean levelCompleted = false;
    private boolean flagpoleMusicPlay = false;
    private boolean levelCompletedMusicPlay = false;

    private int map = 1;

    public PlayScreen(Bounce game) {
        this.game = game;
    }

    public PlayScreen(Bounce game, int map) {
        this.game = game;
        this.map = map;
    }

    @Override
    public void show() {

        camera = new OrthographicCamera();

        viewport = new FitViewport(GameManager.V_WIDTH, GameManager.V_HEIGHT);
        viewport.setCamera(camera);

        camera.position.set(GameManager.V_WIDTH / 2, GameManager.V_HEIGHT / 2, 0);

        textureAtlas = new TextureAtlas("imgs/actors.atlas");

        // create Box2D world
        world = new World(GameManager.GRAVITY, true);
        world.setContactListener(new WorldContactListener());

        // load tmx tiled map
        TmxMapLoader tmxMapLoader = new TmxMapLoader();
        tiledMap = tmxMapLoader.load("maps/Level_" + map + ".tmx");

        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / GameManager.PPM);

        mapWidth = ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
//        mapHeight = ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight(); // currently not used

        // create world from TmxTiledMap
        worldCreator = new WorldCreator(this, tiledMap);
        mapTileObjects = worldCreator.getMapTileObject();
        enemies = worldCreator.getEnemies();
        // create character
        if (GameManager.instance.getSavePoint()) {
            character = new Character(this, (worldCreator.getSavePosition().x + 8) / GameManager.PPM, (worldCreator.getSavePosition().y + 8) / GameManager.PPM);
        } else {
            character = new Character(this, (worldCreator.getStartPosition().x + 8) / GameManager.PPM, (worldCreator.getStartPosition().y + 8) / GameManager.PPM);
        }
          // for spawning item
        items = new Array<Item>();
        itemSpawnQueue = new LinkedList<SpawningItem>();

        // for spawning effect
        effects = new Array<Effect>();
        effectSpawnQueue = new LinkedList<SpawningEffect>();

        hud = new Hud(game.batch);
        hud.setLevel("1");

        scoreIndicator = new ScoreIndicator(this, game.batch);

        accumulator = 0;

        cameraLeftLimit = GameManager.V_WIDTH / 2;
        cameraRightLimit =  mapWidth - GameManager.V_WIDTH / 2;

        box2DDebugRenderer = new Box2DDebugRenderer();
        renderB2DDebug = false;

        deathCountdown = 1.0f;

        playingHurryMusic = false;
        playMusic = true;

        levelCompletedStage = new Stage(viewport, game.batch);
        RunnableAction setLevelCompletedScreen = new RunnableAction();
        setLevelCompletedScreen.setRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new GameOverScreen(game));
                dispose();
            }
        });
        levelCompletedStage.addAction(new SequenceAction(new DelayAction(8.0f), setLevelCompletedScreen));

    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public ScoreIndicator getScoreIndicator() {
        return scoreIndicator;
    }

    public void levelCompleted() {
        if (levelCompleted) {
            return;
        }
        levelCompleted = true;
    }

    public void addSpawnItem(float x, float y, Class<? extends Item> type) {
        itemSpawnQueue.add(new SpawningItem(x, y, type));
    }

    private void handleSpawningItem() {
        if (itemSpawnQueue.size() > 0) {
            SpawningItem spawningItem = itemSpawnQueue.poll();

            if (spawningItem.type == Mushroom.class) {
                items.add(new Mushroom(this, spawningItem.x, spawningItem.y));
            }
            else if (spawningItem.type == Star.class) {
                items.add(new Star(this, spawningItem.x, spawningItem.y));
            }

        }
    }

    public void addSpawnEffect(float x, float y, Class<? extends Effect> type) {
        effectSpawnQueue.add(new SpawningEffect(x, y, type));
    }

    public void handleSpawningEffect() {
        if (effectSpawnQueue.size() > 0) {
            SpawningEffect spawningEffect = effectSpawnQueue.poll();

            if (spawningEffect.type == FlippingCoin.class) {
                effects.add(new FlippingCoin(this, spawningEffect.x, spawningEffect.y));
            }
            else if (spawningEffect.type == BrickDebris.class) {
                effects.add(new BrickDebris(this, spawningEffect.x, spawningEffect.y));
            }
        }
    }

    public void handleInput() {

        // press M to pause / play music
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            if (GameManager.instance.isPlayingMusic()) {
                GameManager.instance.pauseMusic();
                playMusic = false;
            }
            else {
                GameManager.instance.resumeMusic();
                playMusic = true;
            }
        }

        // Press B to toggle Box2DDebuggerRenderer
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            renderB2DDebug = !renderB2DDebug;
        }
    }

    public void handleMusic() {
        if (!playMusic) {
            return;
        }

        if (character.isDead()) {
            GameManager.instance.stopMusic();
        }
        else if (levelCompleted) {
            if (!flagpoleMusicPlay) {
                GameManager.instance.playMusic("flagpole.ogg", false);
                flagpoleMusicPlay = true;
            }
            else if (!GameManager.instance.isPlayingMusic("flagpole.ogg")) {
                if (!levelCompletedMusicPlay) {
                    GameManager.instance.playMusic("stage_clear.ogg", false);
                    levelCompletedMusicPlay = true;
                }
            }
        }
        else {
            if (hud.getTimeLeft() < 15) {
                if (!playingHurryMusic) {
                    GameManager.instance.playMusic("out_of_time.ogg", false);
                    playingHurryMusic = true;
                }
                else {
                    if (!GameManager.instance.isPlayingMusic("out_of_time.ogg")) {
                        GameManager.instance.playMusic("music_hurry.ogg");
                    }
                }
            }
            else {
                GameManager.instance.playMusic("music.ogg");
            }
        }
    }

    public void update(float delta) {
        delta *= GameManager.timeScale;
        float step = GameManager.STEP * GameManager.timeScale;

        handleInput();
        handleSpawningItem();
        handleSpawningEffect();
        handleMusic();

        if (hud.getTimeLeft() == 0) {
            character.suddenDeath();
        }

        // Box2D world step
        accumulator += delta;
        if (accumulator > step) {
            world.step(step, 8, 3);
            accumulator -= step;
        }

        // update map tile objects
        for (MapTileObject mapTileObject : mapTileObjects) {
            mapTileObject.update(delta);
        }

        // update enemies
        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }

        // update items
        for (Item item : items) {
            item.update(delta);
        }

        // update effects
        for (Effect effect : effects) {
            effect.update(delta);
        }

        // update Character
        character.update(delta);

        // camera control
        float targetX = camera.position.x;
        if (!character.isDead()) {
            targetX = MathUtils.clamp(character.getPosition().x, cameraLeftLimit, cameraRightLimit);
        }

        camera.position.x = MathUtils.lerp(camera.position.x, targetX, 0.1f);
        if (Math.abs(camera.position.x - targetX) < 0.1f) {
            camera.position.x = targetX;
        }
        camera.update();

        // update map renderer
        mapRenderer.setView(camera);

        // update ScoreIndicator
        scoreIndicator.update(delta);

        // update HUD
        hud.update(delta);

        // update levelCompletedStage
        if (levelCompleted) {
            levelCompletedStage.act(delta);
        }

        cleanUpDestroyedObjects();


        // check if Character is dead
        if (character.isDead()) {
            deathCountdown -= delta;

            if (deathCountdown < 0) {
                GameManager.instance.gameOver();
                game.setScreen(new GameOverScreen(game));
                dispose();
            }
        }
    }

    private void cleanUpDestroyedObjects() {
        /*
        for (int i = 0; i < mapTileObjects.size; i++) {
            if (mapTileObjects.get(i).isDestroyed()) {
                mapTileObjects.removeIndex(i);
            }
        }
        */

        for (int i = 0; i < items.size; i++) {
            if (items.get(i).isDestroyed()) {
                items.removeIndex(i);
            }
        }

        for (int i = 0; i < effects.size; i++) {
            if (effects.get(i).isDestroyed()) {
                effects.removeIndex(i);
            }
        }
    }

    public WorldCreator getWorldCreator() {
        return worldCreator;
    }
    public Vector2 getCharacterPosition() {
        return character.getPosition();
    }
    public Character.State getCharacterCurrentState() {
        return character.getCurrentState();
    }
    public void setCharacterDie(boolean value) {
        character.setDie(value);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw map
        mapRenderer.render(new int[] {0, 1});

        // draw ScoreIndicator
        scoreIndicator.draw();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // draw map tile objects
        for (MapTileObject mapTileObject : mapTileObjects) {
            mapTileObject.draw(game.batch);
        }

        // draw effects
        for (Effect effect : effects) {
            effect.draw(game.batch);
        }

        // draw items
        for (Item item : items) {
            item.draw(game.batch);
        }

        // draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw(game.batch);
        }

        // draw Character
        character.draw(game.batch);

        game.batch.end();

        // draw levelCompletedStage
        levelCompletedStage.draw();

        // draw HUD
        hud.draw();

        if (renderB2DDebug) {
            box2DDebugRenderer.render(world, camera.combined);
        }

        update(delta);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        hud.dispose();
        scoreIndicator.dispose();
        tiledMap.dispose();
        world.dispose();
        textureAtlas.dispose();
        box2DDebugRenderer.dispose();
        levelCompletedStage.dispose();
    }
}
