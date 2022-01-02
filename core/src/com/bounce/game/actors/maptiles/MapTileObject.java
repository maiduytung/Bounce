package com.bounce.game.actors.maptiles;

import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.bounce.game.actors.RigidBody;
import com.bounce.game.gamesys.GameManager;
import com.bounce.game.screens.PlayScreen;

public abstract class MapTileObject extends RigidBody {

    protected TiledMapTileMapObject mapObject;

    public MapTileObject(PlayScreen playScreen, float x, float y, TiledMapTileMapObject mapObject) {
        super(playScreen, x, y);

        this.mapObject = mapObject;

        setRegion(mapObject.getTextureRegion());

        float width = 16 / GameManager.PPM;
        float height = 16 / GameManager.PPM;

        setBounds(x - width / 2, y - height / 2, width, height);
    }

    @Override
    public void update(float delta) {

    }
}
