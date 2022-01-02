package com.bounce.game.actors.effects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bounce.game.actors.RigidBody;
import com.bounce.game.screens.PlayScreen;

public abstract class Effect extends RigidBody {

    protected TextureAtlas textureAtlas;

    public Effect(PlayScreen playScreen, float x, float y) {
        super(playScreen, x, y);
        this.textureAtlas = playScreen.getTextureAtlas();

    }

}
