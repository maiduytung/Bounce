package com.bounce.game.actors.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.bounce.game.gamesys.GameManager;
import com.bounce.game.screens.PlayScreen;

public class FallingEnemy extends Enemy {

    public FallingEnemy(PlayScreen playScreen, float x, float y) {
        super(playScreen, x, y);

        setRegion(new TextureRegion(textureAtlas.findRegion("actors"), 128, 64, 16, 16));
        setBounds(getX() - 8.0f / GameManager.PPM, getY() - 8.0f / GameManager.PPM, 16.0f / GameManager.PPM, 16.0f / GameManager.PPM);
    }

    @Override
    public void update(float delta) {
        if (destroyed) {
            return;
        }

        if (toBeDestroyed) {
            world.destroyBody(body);
            setBounds(0, 0, 0, 0);
            destroyed = true;
            return;
        }

        if (playScreen.getCharacterPosition().x > body.getPosition().x - 1f && playScreen.getCharacterPosition().x < body.getPosition().x + 1f
                && playScreen.getCharacterPosition().y < 11f) {
            active = true;
            body.setLinearVelocity(new Vector2(0,-30f));
        }

        if (!active) {
            return;
        }

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    @Override
    public void getDamage(int damage) {
    }

    @Override
    protected void defBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        // feet
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(
                new Vector2(-7.0f, -7.0f).scl(1 / GameManager.PPM),
                new Vector2(7.0f, -7.0f).scl(1 / GameManager.PPM)
                );

        fixtureDef.shape = edgeShape;
        fixtureDef.filter.categoryBits = GameManager.ENEMY_LETHAL_BIT;
        fixtureDef.filter.maskBits = GameManager.GROUND_BIT | GameManager.CHARACTER_BIT;
        body.createFixture(fixtureDef).setUserData(this);


        // lethal
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(2.0f / GameManager.PPM);
        circleShape.setPosition(new Vector2(-6, 0).scl(1 / GameManager.PPM));

        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.ENEMY_LETHAL_BIT;
        fixtureDef.filter.maskBits = GameManager.CHARACTER_BIT;
        body.createFixture(fixtureDef).setUserData(this);

        circleShape.setPosition(new Vector2(6, 0).scl(1 / GameManager.PPM));
        body.createFixture(fixtureDef).setUserData(this);

        // weakness
        Vector2[] vertices = {
                new Vector2(-6.8f, 7.0f).scl(1 / GameManager.PPM),
                new Vector2(6.8f, 7.0f).scl(1 / GameManager.PPM),
                new Vector2(-2.0f, -2.0f).scl(1 / GameManager.PPM),
                new Vector2(2.0f, -2.0f).scl(1 / GameManager.PPM),
        };
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);

        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.ENEMY_WEAKNESS_BIT;
        fixtureDef.filter.maskBits = GameManager.CHARACTER_BIT;

        body.createFixture(fixtureDef).setUserData(this);

        circleShape.dispose();
        edgeShape.dispose();
        polygonShape.dispose();

    }

}
