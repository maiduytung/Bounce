package com.bounce.game.actors.effects;

public class SpawningEffect {

    public float x;
    public float y;
    public Class<? extends Effect> type;

    public SpawningEffect(float x, float y, Class<? extends Effect> type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
