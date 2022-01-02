package com.bounce.game.actors;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Collider {

    Fixture fixture;

    public Collider(Fixture fixture) {
        this.fixture = fixture;
    }

    public RigidBody getUserData() {
        return (RigidBody) fixture.getUserData();
    }

    public Body getBody() {
        return fixture.getBody();
    }

    public Filter getFilter() {
        return fixture.getFilterData();
    }

    public Fixture getFixture() {
        return fixture;
    }


}
