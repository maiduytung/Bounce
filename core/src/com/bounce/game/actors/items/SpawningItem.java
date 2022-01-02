package com.bounce.game.actors.items;

public class SpawningItem {
    public float x;
    public float y;
    public Class<? extends Item> type;

    public SpawningItem(float x, float y, Class<? extends Item> type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
