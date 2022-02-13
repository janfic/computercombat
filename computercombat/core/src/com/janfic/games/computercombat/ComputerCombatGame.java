package com.janfic.games.computercombat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.network.client.SQLAPI;
import com.janfic.games.computercombat.network.client.ServerAPI;
import com.janfic.games.computercombat.screens.LoadingScreen;
import java.util.Stack;

public class ComputerCombatGame extends Game {

    static ComputerCombatGame game;

    SpriteBatch batch;
    AssetManager assetManager;
    ServerAPI serverAPI;
    Stack<Screen> screenStack;
    Profile currentProfile;

    float timer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        screenStack = new Stack<>();
        Skin skin = new Skin(Gdx.files.internal(Assets.SKIN));
        assetManager.load(Assets.SKIN, Skin.class);
        for (FileHandle file : Gdx.files.internal("texture_packs").list(".atlas")) {
            assetManager.load(file.path(), TextureAtlas.class);
        }
        assetManager.load(Assets.PLAY_BACKGROUND, Texture.class);
        assetManager.load(Assets.TITLE, Texture.class);
        assetManager.load(Assets.MAIN_MENU_BACKGROUND, Texture.class);
        pushScreen(new LoadingScreen(this));
    }

    public Screen popScreen() {
        Screen s = screenStack.pop();
        if (screenStack.isEmpty() == false) {
            screenStack.peek().show();
        }
        return s;
    }

    public void pushScreen(Screen screen) {
        screenStack.push(screen);
        screen.show();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(36f / 255f, 82f / 255f, 59f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        //batch.draw(img, 0, 0);
        batch.end();
        if (screenStack.isEmpty() == false) {
            screenStack.peek().render(Gdx.graphics.getDeltaTime());
        }
        timer += Gdx.graphics.getDeltaTime();
        if (timer >= 10) {
            SQLAPI.getSingleton().pingDatabase();
            timer = 0;
        }
    }

    @Override
    public void dispose() {
        System.out.println("here");
        batch.dispose();
        serverAPI.dispose();
        serverAPI.getSocket().dispose();
        SQLAPI.getSingleton().dispose();
    }

    @Override
    public void resize(int width, int height) {
        screenStack.get(0).resize(width, height);
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

    public void setCurrentProfile(Profile profile) {
        this.currentProfile = profile;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }
}
