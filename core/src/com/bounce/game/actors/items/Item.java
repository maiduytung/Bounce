package com.bounce.game.actors.items;

import com.bounce.game.actors.RigidBody;
import com.bounce.game.screens.PlayScreen;

public abstract class Item extends RigidBody {

    protected String name = "item";

    public Item(PlayScreen playScreen, float x, float y) {
        super(playScreen, x, y);
    }

    public String getName() {
        return name;
    }

    public abstract void use();

}
