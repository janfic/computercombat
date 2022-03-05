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
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import com.janfic.games.computercombat.network.client.ServerAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
        this.skin = new Skin(Gdx.files.internal(Assets.SKIN));
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);

        Pixmap cursor = new Pixmap(Gdx.files.internal(Assets.CURSOR));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursor, 0, 0));

        this.progressBar = new ProgressBar(0, 1, 0.01f, false, skin.get("default-horizontal", ProgressBarStyle.class));
        this.statusLabel = new Label("Loading Assets...", skin);

        Table table = new Table(skin);
        table.setFillParent(true);
        table.add(statusLabel).row();
        table.add(progressBar);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                int tries = 0;
                JsonReader json = new JsonReader();
                JsonValue parsed = json.parse(Gdx.files.internal("connections.json"));

                List<String> connections = new ArrayList<>();

                for (JsonValue jsonValue : parsed.child) {
                    connections.add(jsonValue.getString("ip") + " " + jsonValue.getString("port"));
                }

                System.out.println(connections);
                boolean connected = false;
                while (connected == false) {
                    try {
                        String[] connection = connections.get(index).split(" ");
                        game.setServerAPI(new ServerAPI(
                                Gdx.net.newClientSocket(
                                        Net.Protocol.TCP,
                                        connection[0],
                                        Integer.parseInt(connection[1]),
                                        new SocketHints()))
                        );
                        connected = true;
                        Thread.sleep(5);
                    } catch (Exception e) {
                        tries++;
                        statusLabel.setText("Failed to Connect to server. Trying again. (" + tries + ")");
                        if (tries > 4) {
                            statusLabel.setText("Trying a different server...");
                            tries = 0;
                            index++;
                            index = index % connections.size();
                        }
                    }
                }
                game.getServerAPI().sendMessage(new Message(Type.CONNECTION_REQUEST, "CONNECTION_REQUEST"));
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
            if (game.getServerAPI() != null && game.getServerAPI().hasMessage()) {
                Message m = game.getServerAPI().readMessage();
                if (m.type == Type.CONNECTION_ACCEPT) {
                    game.popScreen();
                    game.pushScreen(new MainMenuScreen(game));
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height);
        camera.update();
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
