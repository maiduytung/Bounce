package com.bounce.game.actors.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.bounce.game.gamesys.GameManager;
import com.bounce.game.screens.PlayScreen;

public class FlyEnemy extends Enemy {

    public enum State {
        HIDDEN,
        STOMPED,
        DYING,
    }

    private float stateTime;

    private State currentState;

    private boolean stomped;
    private boolean die;

    public FlyEnemy(PlayScreen playScreen, float x, float y) {
        super(playScreen, x, y);

        setRegion(new TextureRegion(textureAtlas.findRegion("actors"), 128, 64, 16, 16));
        setBounds(getX() - 8.0f / GameManager.PPM, getY() - 8.0f / GameManager.PPM, 16.0f / GameManager.PPM, 16.0f / GameManager.PPM);

        stateTime = 0;

        stomped = false;
        die = false;

        currentState = State.HIDDEN;
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
            body.setLinearVelocity(new Vector2(0,25f));
        }

        if (!active) {
            return;
        }

        State previousState = currentState;

        if (stomped) {
            stomped = false;
            currentState = State.STOMPED;
            becomeStomped();

            GameManager.instance.playSFX("stomp.wav");

            playScreen.getScoreIndicator().addScoreItem(getX(), getY(), 100);
        }
        else if (die) {
            die = false;
            currentState = State.DYING;

            body.applyLinearImpulse(new Vector2(0.0f, 7.2f), body.getWorldCenter(), true);
            becomeDead();

            float cameraX = playScreen.getCamera().position.x;
            float distanceRatio = (body.getPosition().x - cameraX) / GameManager.V_WIDTH * 2;
            float pan = MathUtils.clamp(distanceRatio, -1, 1);
            float volume = MathUtils.clamp(2.0f - (float)Math.sqrt(Math.abs(distanceRatio)), 0, 1);
            GameManager.instance.playSFX("stomp.wav",volume, pan);

            playScreen.getScoreIndicator().addScoreItem(getX(), getY(), 100);
        }

        if (previousState != currentState) {
            stateTime = 0;
        }

        switch (currentState) {
            case STOMPED:
                setRegion(new TextureRegion(textureAtlas.findRegion("Goomba"), 16 * 2, 0, 16, 16));
                if (stateTime > 1.0f) {
                    queueDestroy();
                }
                break;

            case DYING:
                setFlip(false, true);
                if (stateTime > 2.0f) {
                    queueDestroy();
                }
                break;

            default:
                break;
        }

        stateTime += delta;

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }


    private void becomeStomped() {
        Filter filter = new Filter();
        filter.maskBits = GameManager.GROUND_BIT;
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
    }

    private void becomeDead() {
        Filter filter;
        for (Fixture fixture : body.getFixtureList()) {
            filter = fixture.getFilterData();
            filter.categoryBits = GameManager.NOTHING_BIT;
            filter.maskBits = GameManager.NOTHING_BIT;
            fixture.setFilterData(filter);
        }
    }

    @Override
    public void getDamage(int damage) {
        if (toBeDestroyed || currentState == State.STOMPED || currentState == State.DYING || !active) {
            return;
        }

        // hit by Character on head
        if (damage == 1) {
            stomped = true;
        }
        else {
            die = true;
        }

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
