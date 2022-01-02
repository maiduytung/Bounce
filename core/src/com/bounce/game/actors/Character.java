package com.bounce.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.bounce.game.actors.enemies.Enemy;
import com.bounce.game.actors.items.Item;
import com.bounce.game.gamesys.GameManager;
import com.bounce.game.screens.PlayScreen;

public class Character extends RigidBody {
    public enum State {
        STANDING,
        RUNNING,
        JUMPING,
        FALLING,
        BRAKING,
        DYING
    }

    private final float radius = 6.8f / GameManager.PPM;

    private final float force = 20.0f;
    private final float speedMax = 6.0f;

    private State currentState;

    private float stateTime;

    private TextureRegion standing;
    private TextureRegion jumping;
    private Animation running;
    private TextureRegion braking;

    private TextureRegion dying;

    private boolean isFacingRight;

    private boolean isDead;
    private boolean isLevelCompleted;

    private boolean isGround;
    private boolean isJumping;
    private boolean isDie;
    private boolean isBrake;

    private AssetManager assetManager;

    public Character(PlayScreen playScreen, float x, float y) {
        super(playScreen, x, y);
        TextureAtlas textureAtlas = playScreen.getTextureAtlas();

        standing = new TextureRegion(textureAtlas.findRegion("actors"), 16 * 11, 0, 16, 32);

        jumping = new TextureRegion(textureAtlas.findRegion("actors"), 16 * 3, 0, 16, 32);

        braking = new TextureRegion(textureAtlas.findRegion("actors"), 16 * 9, 0, 16, 32);

        // flip braking image for correct displaying
        braking.flip(true, false);

        // running animation
        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        for (int i = 1; i < 7; i++) {
            keyFrames.add(new TextureRegion(textureAtlas.findRegion("actors"), 16 * i, 0, 16, 32));
        }
        running = new Animation(0.1f, keyFrames);

        dying = new TextureRegion(textureAtlas.findRegion("actors"), 16 * 8, 0, 16, 32);

        setRegion(standing);
        setBounds(getX(), getY(), 16 / GameManager.PPM, 32 / GameManager.PPM);

        currentState = State.STANDING;
        stateTime = 0;

        isFacingRight = true;
        isJumping = false;
        isDie = false;

        isLevelCompleted = false;

        assetManager = GameManager.instance.getAssetManager();
    }


    @Override
    protected void defBody() {

        BodyDef bodyDef = new BodyDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX(), getY());

        body = world.createBody(bodyDef);

        // Character's body

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        shape.setPosition(new Vector2(0, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameManager.CHARACTER_BIT;
        fixtureDef.filter.maskBits = GameManager.GROUND_BIT | GameManager.ENEMY_WEAKNESS_BIT | GameManager.ENEMY_INTERACT_BIT | GameManager.ENEMY_LETHAL_BIT | GameManager.ITEM_BIT | GameManager.FLAGPOLE_BIT;

        body.createFixture(fixtureDef).setUserData(this);

        shape.setPosition(new Vector2(0, radius * 2));
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        // Character's feet
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(new Vector2(-radius, -radius), new Vector2(radius, -radius));
        fixtureDef.shape = edgeShape;
        body.createFixture(fixtureDef).setUserData(this);

        // Character's head
        edgeShape.set(new Vector2(-radius / 6, radius * 3), new Vector2(radius / 6, radius * 3));
        fixtureDef.shape = edgeShape;
        fixtureDef.filter.categoryBits = GameManager.CHARACTER_HEAD_BIT;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
        edgeShape.dispose();
    }

    private void handleInput() {

        // Jump
        if ((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) && isGround) {
            body.applyLinearImpulse(new Vector2(0.0f, 20.0f), body.getWorldCenter(), true);
            isJumping = true;
            assetManager.get("audio/sfx/jump.wav", Sound.class).play();
        }

        // Move left
        if ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) && body.getLinearVelocity().x > -speedMax) {
            body.applyForceToCenter(new Vector2(-force, 0.0f), true);
        }

        // Move right
        if ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) && body.getLinearVelocity().x < speedMax) {
            body.applyForceToCenter(new Vector2(force, 0.0f), true);
        }
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public State getCurrentState() {
        return currentState;
    }

    public boolean isDead() {
        return isDead;
    }

    public void suddenDeath() {
        isDie = true;
    }

    public void levelCompleted() {
        if (isLevelCompleted) {
            return;
        }

        isLevelCompleted = true;

        int point = (int) MathUtils.clamp(getY(), 2.0f, 10.0f) * 100;
        GameManager.instance.addScore(point);
        playScreen.getScoreIndicator().addScoreItem(getX(), getY(), point);
    }

    public void handleLevelCompletedActions() {

        if (getX() < 201.0f) body.applyLinearImpulse(new Vector2(body.getMass() * (4.0f - body.getLinearVelocity().x), 0f), body.getWorldCenter(), true);
    }

    private void checkGrounded() {
        isGround = false;

        Vector2 p1;
        Vector2 p2;

        RayCastCallback rayCastCallback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getUserData().getClass() == Character.class) {
                    return 1;
                }

                if (fraction < 1) {
                    isGround = true;
                    return 0;
                }
                return 0;
            }
        };

        for (int i = 0; i < 3; i++) {
            p1 = new Vector2(body.getPosition().x - radius * (1 - i), body.getPosition().y - radius);
            p2 = new Vector2(p1.x, p1.y - 0.05f);
            world.rayCast(rayCastCallback, p1, p2);
        }

    }

    @Override
    public void update(float delta) {
        checkGrounded();

        // die when falling below ground
        if (body.getPosition().y < -2.0f) {
            isDie = true;
        }

        if (!isDead && !isLevelCompleted) {
            handleInput();
        }
        else if (isLevelCompleted) {
            handleLevelCompletedActions();
        }

        State previousState = currentState;

        if (isDie) {
            if (!isDead) {
                assetManager.get("audio/sfx/die.wav", Sound.class).play();
            }
            isDead = true;
            // do not collide with anything anymore
            for (Fixture fixture : body.getFixtureList()) {
                Filter filter = fixture.getFilterData();
                filter.maskBits = GameManager.NOTHING_BIT;
                fixture.setFilterData(filter);
            }

            currentState = State.DYING;
        }
        else if (!isGround) {
            if (isJumping) {
                currentState = State.JUMPING;
            }
            else {
                currentState = State.FALLING;
            }

        }
        else {
            if (currentState == State.JUMPING) {
                isJumping = false;
            }
            if (isBrake) {
                currentState = State.BRAKING;
                isBrake = false;
            }
            else if (body.getLinearVelocity().x != 0) {
                currentState = State.RUNNING;
            }
            else {
                currentState = State.STANDING;
            }
        }

        float v = 1.0f + Math.abs(body.getLinearVelocity().x) / force;
        stateTime = previousState == currentState ? stateTime + delta * v : 0;

        switch (currentState) {
            case DYING:
                setRegion(dying);
                setSize(16 / GameManager.PPM, 16 / GameManager.PPM);
                break;
            case RUNNING:
                setRegion((TextureRegion) running.getKeyFrame(stateTime, true));
                break;

            case BRAKING:
                setRegion(braking);
                break;

            case JUMPING:
                setRegion(jumping);
                break;

            case FALLING:
            case STANDING:
            default:
                setRegion(standing);
                break;
        }


        if ((body.getLinearVelocity().x < -0.01f || !isFacingRight)) {
            flip(true, false);
            isFacingRight = false;
        }

        if (body.getLinearVelocity().x > 0.01f){
            isFacingRight = true;
        }


        // limit Character's moving area
        if (body.getPosition().x < 0.5f) {
            body.setTransform(0.5f, body.getPosition().y, 0);
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
        else if (body.getPosition().x > playScreen.getMapWidth() - 0.5f) {
            body.setTransform(playScreen.getMapWidth() - 0.5f, body.getPosition().y, 0);
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - radius);
    }

    @Override
    public void onCollide(Collider other) {
        if (other.getFilter().categoryBits == GameManager.ENEMY_WEAKNESS_BIT) {

            ((Enemy) other.getUserData()).getDamage(1);
            float force = body.getMass() * (8.0f - body.getLinearVelocity().y);
            body.applyLinearImpulse(new Vector2(0.0f, force), body.getWorldCenter(), true);

        }
        else if (other.getFilter().categoryBits == GameManager.ENEMY_LETHAL_BIT) {
            isDie = true;
        }
        else if (other.getFilter().categoryBits == GameManager.ENEMY_INTERACT_BIT) {
            ((Enemy) other.getUserData()).interact(this);
            float force = body.getMass() * (8.0f - body.getLinearVelocity().y);
            body.applyLinearImpulse(new Vector2(0.0f, force), body.getWorldCenter(), true);
        }
        else if (other.getFilter().categoryBits == GameManager.ITEM_BIT) {
            Item item = (Item) other.getUserData();
            item.use();
            if (item.getName().equals("mushroom")) {
            }
            else if (item.getName().equals("flower")) {
            }
            else if (item.getName().equals("star")) {
            }
        }
    }

    public void setDie(boolean isDie) {
        this.isDie = isDie;
    }
}
