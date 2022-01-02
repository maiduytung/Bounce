package com.bounce.game.actors.maptiles.trap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.bounce.game.actors.Collider;
import com.bounce.game.actors.maptiles.MapTileObject;
import com.bounce.game.gamesys.GameManager;
import com.bounce.game.screens.PlayScreen;

public class MoveBox extends MapTileObject {

    private Animation flashingAnimation;

    private float stateTimer;

    public MoveBox(PlayScreen playScreen, float x, float y, TiledMapTileMapObject mapObject) {
        super(playScreen, x, y, mapObject);

        TiledMap tiledMap = playScreen.getTiledMap();

        Array<TextureRegion> keyFrames = new Array<TextureRegion>();

        for (int i = 50; i < 64; i++) {
            keyFrames.add(tiledMap.getTileSets().getTileSet(0).getTile(i).getTextureRegion());
        }
        keyFrames.add(tiledMap.getTileSets().getTileSet(0).getTile(50).getTextureRegion());
        flashingAnimation = new Animation(0.2f, keyFrames);

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

        setRegion((TextureRegion) flashingAnimation.getKeyFrame(stateTimer, true));

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        if (body.getPosition().y > 10f) body.setLinearVelocity(new Vector2(0,0));

    }

    @Override
    public void onTrigger(Collider other) {
        if (other.getFilter().categoryBits == GameManager.CHARACTER_HEAD_BIT) {
            body.setLinearVelocity(new Vector2(0.0f, 10f));
        }
    }
}
