package com.bounce.game.actors.maptiles.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.bounce.game.actors.maptiles.MapTileObject;
import com.bounce.game.screens.PlayScreen;

public class Wave extends MapTileObject {

    private Animation flashingAnimation;

    private float stateTimer;

    public Wave(PlayScreen playScreen, float x, float y, TiledMapTileMapObject mapObject) {
        super(playScreen, x, y, mapObject);

        TiledMap tiledMap = playScreen.getTiledMap();

        Array<TextureRegion> keyFrames = new Array<TextureRegion>();

        for (int i = 1682; i < 1688; i++) {
            keyFrames.add(tiledMap.getTileSets().getTileSet(0).getTile(i).getTextureRegion());
        }
        keyFrames.add(tiledMap.getTileSets().getTileSet(0).getTile(1682).getTextureRegion());
        flashingAnimation = new Animation(0.2f, keyFrames);
        stateTimer = 0;
    }

    @Override
    protected void defBody() {

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        body = world.createBody(bodyDef);

    }

    @Override
    public void update(float delta) {
        stateTimer += delta;

        setRegion((TextureRegion) flashingAnimation.getKeyFrame(stateTimer, true));

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }
}
