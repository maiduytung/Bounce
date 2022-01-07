package com.bounce.game.gamesys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class GameManager implements Disposable {

    public static GameManager instance;

    public static final float PPM = 16;

    public static final int WINDOW_WIDTH = Gdx.graphics.getWidth();
    public static final int WINDOW_HEIGHT = Gdx.graphics.getHeight();

    public static final float V_WIDTH = 32.5f;
    public static final float V_HEIGHT = 15.0f;

    public static final float SCALE= WINDOW_HEIGHT/V_HEIGHT/PPM;

    public static final Vector2 GRAVITY = new Vector2(0.0f, -9.8f * 4);

    public static final float STEP = 1 / 60.0f;

    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short CHARACTER_BIT = 1 << 1;
    public static final short CHARACTER_HEAD_BIT = 1 << 2;
    public static final short ENEMY_LETHAL_BIT = 1 << 3;
    public static final short ENEMY_WEAKNESS_BIT = 1 << 4;
    public static final short ENEMY_INTERACT_BIT = 1 << 5;
    public static final short ITEM_BIT = 1 << 6;
    public static final short TRAP_BIT = 1 << 7;
    public static final short FLAGPOLE_BIT = 1 << 8;

    public static final String musicPath = "audio/music/";
    public static final String sfxPath = "audio/sfx/";

    private AssetManager assetManager;

    private int character;
    private int coins;

    public static float timeScale = 1.5f;

    static Preferences preferences;

    private boolean isMute;

    public GameManager() {
        if (instance == null) {
            instance = this;
        }

        if (assetManager == null) {
            assetManager = new AssetManager();
        }

        loadAudio();

        character = 3;
        coins = 0;

        isMute = false;

        getPrefs();
        initData();
    }

    protected Preferences getPrefs() {
        if (preferences == null)
            preferences = Gdx.app.getPreferences("My Preferences");
        return preferences;
    }

    private void initData() {
        unlock(1);
    }

    private void loadAudio() {
        assetManager.load("audio/music/homemusic.ogg", Music.class);
        assetManager.load("audio/music/music.ogg", Music.class);
        assetManager.load("audio/music/music_hurry.ogg", Music.class);
        assetManager.load("audio/music/out_of_time.ogg", Music.class);
        assetManager.load("audio/music/game_over.ogg", Music.class);
        assetManager.load("audio/music/stage_clear.ogg", Music.class);
        assetManager.load("audio/music/flagpole.ogg", Music.class);
        assetManager.load("audio/sfx/breakblock.wav", Sound.class);
        assetManager.load("audio/sfx/bump.wav", Sound.class);
        assetManager.load("audio/sfx/coin.wav", Sound.class);
        assetManager.load("audio/sfx/jump.wav", Sound.class);
        assetManager.load("audio/sfx/die.wav", Sound.class);
        assetManager.load("audio/sfx/stomp.wav", Sound.class);
        assetManager.load("audio/sfx/kick.wav", Sound.class);
        assetManager.load("audio/sfx/spawn.wav", Sound.class);
        assetManager.finishLoading();
    }

    public void setSavePoint(int level) {
        preferences.putBoolean("sPoint_lv" +level,true);
    }

    public boolean getSavePoint(int level) {
        return preferences.getBoolean("sPoint_lv" +level, false);
    }

    public int getCharacter() {
        return character;
    }

    public void clearCharacter() {
        character = 3;
    }

    public void addCharacter() {
        character --;
    }

    public void addCoin() {
        addCoin(1);
    }

    public void addCoin(int value) {
        coins += value;
    }

    public int getCoins() {
        return coins;
    }

    public static void setTimeScale(float value) {
        timeScale = MathUtils.clamp(value, 0.0f, 2.0f);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    //region Music
    private String currentMusic = "";

    public void playMusic(String filename) {
        playMusic(filename, true);
    }

    public void playMusic(String filename, boolean loop) {
        if (!isMute) {
            if (!currentMusic.equals(filename)) {
                stopMusic();
                currentMusic = filename;
            }

            if (isPlayingMusic(currentMusic)) {
                return;
            }
            assetManager.get(musicPath + filename, Music.class).setLooping(loop);
            assetManager.get(musicPath + filename, Music.class).play();
        }
    }

    public boolean isPlayingMusic() {
        return isPlayingMusic(currentMusic);
    }

    public void pauseMusic() {
        if (currentMusic.length() > 0) {
            assetManager.get(musicPath + currentMusic, Music.class).pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic.length() > 0) {
            if (!isPlayingMusic(currentMusic)) {
                playMusic(currentMusic);
            }
        }
    }

    public void stopMusic() {
        if (currentMusic.length() > 0) {
            assetManager.get(musicPath + currentMusic, Music.class).stop();
        }
    }

    public boolean isPlayingMusic(String filename) {
        return assetManager.get(musicPath + filename, Music.class).isPlaying();
    }
    //endregion

    //region SFX
    public void playSFX(String filename) {
        if (!isMute) {
            assetManager.get(sfxPath + filename, Sound.class).play();
        }
    }
    public void playSFX(String filename, float volume, float pan) {
        if (!isMute) {
            assetManager.get(sfxPath + filename, Sound.class).play(volume, 1.0f, pan);;
        }
    }

    //endregion

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
        if (mute) pauseMusic();
        else resumeMusic();
    }

    public void addHighScore(int level, int hs) {
        preferences.putInteger("hscore_lv"+level,hs);
    }

    public void unlock(int level) {
        preferences.putBoolean("ulock_lv" + level,true);
    }

    public int getHighScore(int level) {
        return preferences.getInteger("hscore_lv"+level,0);
    }

    public boolean checkUnlock(int level) {
        return preferences.getBoolean("ulock_lv" + level,false);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
