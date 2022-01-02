package com.bounce.game.utils;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bounce.game.actors.enemies.*;
import com.bounce.game.actors.maptiles.*;
import com.bounce.game.actors.maptiles.map.MysteryBox;
import com.bounce.game.actors.maptiles.trap.HideBox;
import com.bounce.game.actors.maptiles.trap.MoveBox;
import com.bounce.game.actors.maptiles.map.Brick;
import com.bounce.game.actors.maptiles.trap.FallingBrick;
import com.bounce.game.actors.maptiles.map.*;
import com.bounce.game.actors.maptiles.trap.Landslide;
import com.bounce.game.gamesys.GameManager;
import com.bounce.game.screens.PlayScreen;

public class WorldCreator {

    private Array<MapTileObject> mapTileObjects;
    private Array<Enemy> enemies;

    private Vector2 startPosition;
    private Vector2 savePosition;
    private Vector2 fallingBrickPosition;
    private Vector2 landslidePosition;

    public WorldCreator(PlayScreen playScreen, TiledMap tiledMap) {

        MapLayer mapLayer;
        mapTileObjects = new Array<MapTileObject>();
        enemies = new Array<Enemy>();

        //region Map

        mapLayer = tiledMap.getLayers().get("Ground");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new Ground(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        mapLayer = tiledMap.getLayers().get("Rock");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new Rock(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        mapLayer = tiledMap.getLayers().get("Pipe");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new Pipe(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        mapLayer = tiledMap.getLayers().get("Finish");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new Finish(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        mapLayer = tiledMap.getLayers().get("Save");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new Save(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
                savePosition = new Vector2(x,y);
            }
        }

        //endregion

        //region Floating

        mapLayer = tiledMap.getLayers().get("Brick");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new Brick(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        mapLayer = tiledMap.getLayers().get("MysteryBox");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new MysteryBox(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        //endregion

        //region Trap

        mapLayer = tiledMap.getLayers().get("Landslide");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new Landslide(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        mapLayer = tiledMap.getLayers().get("FallingBrick");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new FallingBrick(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        mapLayer = tiledMap.getLayers().get("HideBox");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new HideBox(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        mapLayer = tiledMap.getLayers().get("MoveBox");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new MoveBox(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        //endregion

        //region Enemy

        mapLayer = tiledMap.getLayers().get("BaseEnemy");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                enemies.add(new BaseEnemy(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM));
            }
        }

        mapLayer = tiledMap.getLayers().get("GuardShieldEnemy");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                enemies.add(new GuardShieldEnemy(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM));
            }
        }

        mapLayer = tiledMap.getLayers().get("FallingEnemy");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                enemies.add(new FallingEnemy(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM));
            }
        }

        mapLayer = tiledMap.getLayers().get("FlyEnemy");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                enemies.add(new FlyEnemy(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM));
            }
        }

        //endregion

        //region Point

        startPosition = new Vector2(64.0f, 64.0f);
        mapLayer = tiledMap.getLayers().get("Start");
        if (mapLayer != null) {
            if (mapLayer.getObjects().getCount() > 0) {
                float x = ((TiledMapTileMapObject) mapLayer.getObjects().get(0)).getX();
                float y = ((TiledMapTileMapObject) mapLayer.getObjects().get(0)).getY();

                startPosition = new Vector2(x, y);
            }
        }

        landslidePosition = new Vector2();
        mapLayer = tiledMap.getLayers().get("LandslidePoint");
        if (mapLayer != null) {
            if (mapLayer.getObjects().getCount() > 0) {
                float x = ((TiledMapTileMapObject) mapLayer.getObjects().get(0)).getX();
                float y = ((TiledMapTileMapObject) mapLayer.getObjects().get(0)).getY();

                landslidePosition = new Vector2(x, y);
            }
        }

        fallingBrickPosition = new Vector2();
        mapLayer = tiledMap.getLayers().get("FallingBrickPoint");
        if (mapLayer != null) {
            if (mapLayer.getObjects().getCount() > 0) {
                float x = ((TiledMapTileMapObject) mapLayer.getObjects().get(0)).getX();
                float y = ((TiledMapTileMapObject) mapLayer.getObjects().get(0)).getY();

                fallingBrickPosition = new Vector2(x, y);
            }
        }

        //endregion

        //region Background

        mapLayer = tiledMap.getLayers().get("Wave");
        if (mapLayer != null) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                float x = ((TiledMapTileMapObject) mapObject).getX();
                float y = ((TiledMapTileMapObject) mapObject).getY();

                mapTileObjects.add(new Wave(playScreen, (x + 8) / GameManager.PPM, (y + 8) / GameManager.PPM, (TiledMapTileMapObject) mapObject));
            }
        }

        //endregion

    }

    public Vector2 getStartPosition() {
        return startPosition;
    }

    public Vector2 getSavePosition() {
        return savePosition;
    }

    public Vector2 getLandslidePosition() {
        return landslidePosition;
    }

    public Vector2 getFallingBrickPosition() {
        return fallingBrickPosition;
    }

    public Array<MapTileObject> getMapTileObject() {
        return mapTileObjects;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }
}
