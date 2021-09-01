package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.client.ServerAPI;

/**
 *
 * @author Jan Fic
 */
public class LoadingScreen implements Screen {
    
    private final ComputerCombatGame game;
    private ServerAPI serverAPI;
    
    AssetManager assetManager;
    
    Stage stage;
    OrthographicCamera camera;
    
    Skin skin;
    ProgressBar progressBar;
    Label statusLabel;
    
    public LoadingScreen(ComputerCombatGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
    }
    
    @Override
    public void show() {
        this.skin = new Skin(Gdx.files.local(Assets.SKIN));
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        
        Pixmap cursor = new Pixmap(Gdx.files.local(Assets.CURSOR));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursor, 0, 0));
        
        this.progressBar = new ProgressBar(0, 1, 0.01f, false, skin.get("default-horizontal", ProgressBarStyle.class));
        this.statusLabel = new Label("Loading...", skin);
        
        Table table = new Table(skin);
        table.setFillParent(true);
        table.add(statusLabel).row();
        table.add(progressBar);
        
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                game.setServerAPI(new ServerAPI(Gdx.net.newClientSocket(Net.Protocol.TCP, "44.196.13.213", 7272, new SocketHints())));
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
                game.getServerAPI().sendMessage(new Message(Type.CONNECTION_REQUEST, "CONNECTION_REQUEST"));
                while (game.getServerAPI().hasMessage() == false) {
                }
                Message message = game.getServerAPI().readMessage();
            }
        });
        thread.start();
        
        stage.addActor(table);
    }
    
    @Override
    public void render(float f) {
        stage.act(f);
        stage.draw();
        assetManager.update();
        progressBar.setValue(assetManager.getProgress());
        if (progressBar.getValue() >= 1) {
            statusLabel.setText("Connecting to Server...");
            if (game.getServerAPI() != null && game.getServerAPI().isConnected()) {
                game.popScreen();
                game.pushScreen(new MainMenuScreen(game));
            }
        }
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
    
}
