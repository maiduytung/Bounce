package com.bounce.game.actors.maptiles.map;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.bounce.game.actors.Collider;
import com.bounce.game.actors.effects.FlippingCoin;
import com.bounce.game.actors.enemies.Enemy;
import com.bounce.game.actors.items.Mushroom;
import com.bounce.game.actors.maptiles.MapTileObject;
import com.bounce.game.gamesys.GameManager;
import com.bounce.game.screens.PlayScreen;

public class MysteryBox extends MapTileObject {

    private boolean hitable;
    private boolean hit;
    private boolean lethal;

    private Vector2 originalPosition;
    private Vector2 movablePosition;
    private Vector2 targetPosition;

    private TextureRegion unhitableTextureRegion;
    private Animation flashingAnimation;

    private float stateTimer;

    public MysteryBox(PlayScreen playScreen, float x, float y, TiledMapTileMapObject mapObject) {
        super(playScreen, x, y, mapObject);

        TiledMap tiledMap = playScreen.getTiledMap();
        unhitableTextureRegion = tiledMap.getTileSets().getTileSet(0).getTile(80).getTextureRegion();

        Array<TextureRegion> keyFrames = new Array<TextureRegion>();

        for (int i = 50; i < 64; i++) {
            keyFrames.add(tiledMap.getTileSets().getTileSet(0).getTile(i).getTextureRegion());
        }
        keyFrames.add(tiledMap.getTileSets().getTileSet(0).getTile(50).getTextureRegion());
        flashingAnimation = new Animation(0.2f, keyFrames);

        originalPosition = new Vector2(x, y);
        movablePosition = new Vector2(x, y + 0.15f);
        targetPosition = originalPosition;

        hitable = true;
        hit = false;
        lethal = false;

        stateTimer = 0;
    }

    @Override
    protected void defBody() {

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = GameManager.GROUND_BIT;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    @Override
    public void update(float delta) {
        stateTimer += delta;
        if (hitable) {
            setRegion((TextureRegion) flashingAnimation.getKeyFrame(stateTimer, true));
        }
        else {
            setRegion(unhitableTextureRegion);
        }


        float x = body.getPosition().x;
        float y = body.getPosition().y;
        Vector2 dist = new Vector2(x, y).sub(targetPosition);
        if (dist.len2() > 0.0001f) {
            body.setTransform(new Vector2(x, y).lerp(targetPosition, 0.6f), 0);
        }
        else {
            body.setTransform(targetPosition, 0);
            if (hit) {
                hit = false;
                targetPosition = originalPosition;
            }
        }

        if (lethal) {
            lethal = false;

            RayCastCallback raycastCallback = new RayCastCallback() {
                @Override
                public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                    if (fraction <= 1.0f) {
                        if (fixture.getUserData() instanceof Enemy) {
                            ((Enemy) fixture.getUserData()).getDamage(2);
                        } else if (fixture.getUserData() instanceof Mushroom) {
                            ((Mushroom) fixture.getUserData()).bounce();
                        }
                        return 0;
                    }
                    return 0;
                }
            };

            // damage the enemy or push up mushroom above when hit
            for (int i = 0; i < 3; i++) {
                Vector2 p1 = new Vector2(body.getPosition().x - (i - 1) * 0.5f, body.getPosition().y + 0.4f);
                Vector2 p2 = new Vector2(p1).add(0, 0.6f);
                world.rayCast(raycastCallback, p1, p2);
            }
        }

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    @Override
    public void onTrigger(Collider other) {
        if (other.getFilter().categoryBits == GameManager.CHARACTER_HEAD_BIT) {
            if (hitable) {

                GameManager.instance.addScore(200);
                playScreen.getScoreIndicator().addScoreItem(getX(), getY(), 200);
                hitable = false;
                hit = true;
                lethal = true;
                targetPosition = movablePosition;

                if (mapObject.getProperties().containsKey("mushroom")) {
                    playScreen.addSpawnItem(body.getPosition().x, body.getPosition().y + 16 / GameManager.PPM, Mushroom.class);
                    GameManager.instance.getAssetManager().get("audio/sfx/spawn.wav", Sound.class).play();
                }
                else {
                    playScreen.addSpawnEffect(body.getPosition().x, body.getPosition().y + 1.0f, FlippingCoin.class);
                    GameManager.instance.getAssetManager().get("audio/sfx/coin.wav", Sound.class).play();
                    GameManager.instance.addCoin();
                }
            }
            else {
                GameManager.instance.getAssetManager().get("audio/sfx/bump.wav", Sound.class).play();
            }
        }
    }
}
