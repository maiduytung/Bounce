package com.bounce.game.actors.maptiles.trap;

import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.bounce.game.actors.Collider;
import com.bounce.game.actors.Character;
import com.bounce.game.actors.maptiles.MapTileObject;
import com.bounce.game.gamesys.GameManager;
import com.bounce.game.screens.PlayScreen;

public class FallingBrick extends MapTileObject {
    public FallingBrick(PlayScreen playScreen, float x, float y, TiledMapTileMapObject mapObject) {
        super(playScreen, x, y, mapObject);
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
        fixtureDef.filter.categoryBits = GameManager.GROUND_BIT | GameManager.TRAP_BIT;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();

    }

    @Override
    public void update(float delta) {
        if (destroyed) {
            return;
        }

        if (toBeDestroyed) {
            setBounds(0, 0, 0, 0);
            world.destroyBody(body);
            destroyed = true;
            return;
        }

        if (body.getPosition().y < -2f) queueDestroy();

        if (playScreen.getCharacterCurrentState() != Character.State.DYING && playScreen.getCharacterPosition().x > playScreen.getWorldCreator().getFallingBrickPosition().x / GameManager.PPM
                && playScreen.getCharacterPosition().x < playScreen.getWorldCreator().getFallingBrickPosition().x / GameManager.PPM + 4f
                && playScreen.getCharacterPosition().y < body.getPosition().y) {
            body.setLinearVelocity(new Vector2(0.0f, -8.5f));
        }

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    @Override
    public void onTrigger(Collider other) {
        if (other.getFilter().categoryBits == GameManager.CHARACTER_HEAD_BIT) {
            playScreen.setCharacterDie(true);
        }
    }
}
