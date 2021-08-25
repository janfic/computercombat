package com.janfic.games.computercombat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.computercombat.network.client.ServerAPI;
import com.janfic.games.computercombat.screens.LoadingScreen;
import com.janfic.games.computercombat.screens.MatchScreen;

public class ComputerCombatGame extends Game {

    SpriteBatch batch;
    Screen matchScreen;
    AssetManager assetManager;
    ServerAPI serverAPI;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        matchScreen = new MatchScreen(this);
        assetManager.load(Assets.SKIN, Skin.class);
        assetManager.load(Assets.COMPONENT_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.PLAY_BACKGROUND, Texture.class);
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(35f / 255f, 103f / 255f, 78f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        //batch.draw(img, 0, 0);
        batch.end();
        screen.render(Gdx.graphics.getDeltaTime());
        if (serverAPI != null) {
            //serverAPI.update();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public static Stage makeNewStage(Camera cam) {
        return new Stage(new FitViewport(1920 / 4, 1080 / 4, cam));
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public ServerAPI getServerAPI() {
        return serverAPI;
    }

    public void setServerAPI(ServerAPI serverAPI) {
        this.serverAPI = serverAPI;
    }

}
