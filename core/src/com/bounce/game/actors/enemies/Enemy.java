package com.bounce.game.actors.enemies;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bounce.game.actors.Character;
import com.bounce.game.actors.RigidBody;
import com.bounce.game.screens.PlayScreen;

public abstract class Enemy extends RigidBody {

    protected TextureAtlas textureAtlas;

    protected boolean active = false;

    public Enemy(PlayScreen playScreen, float x, float y) {
        super(playScreen, x, y);
        this.textureAtlas = playScreen.getTextureAtlas();
    }

    public abstract void getDamage(int damage);

    public void interact(Character character) {

    }

}
