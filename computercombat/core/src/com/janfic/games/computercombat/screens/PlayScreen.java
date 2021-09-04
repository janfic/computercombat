package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedArea;
import com.janfic.games.computercombat.actors.BorderedGrid;

public class PlayScreen implements Screen {

    /**
     * Play Ranked Live or Raid Normal Live or Raid Practice AI or Self Tutorial
     * Defense
     *
     * Profile Profile
     *
     * Setup Internet - Get More Cards CPU - Upgrade Cards Storage - Collection
     * RAM - Decks Power - Bug - Attack / Play
     */
    ComputerCombatGame game;

    Stage stage;
    OrthographicCamera camera;

    Texture background;
    Skin skin;

    public PlayScreen(ComputerCombatGame game) {
        this.game = game;
        this.background = game.getAssetManager().get(Assets.PLAY_BACKGROUND, Texture.class);
        this.skin = game.getAssetManager().get(Assets.SKIN);
    }

    @Override
    public void show() {

        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        Gdx.input.setInputProcessor(stage);

        Image image = new Image(background);
        image.setSize(stage.getWidth(), stage.getHeight());

        TextButton playButton = new TextButton("Play", skin);
        TextButton upgradeButton = new TextButton("Upgrade", skin);
        TextButton collectionButton = new TextButton("Collection", skin);
        TextButton decksButton = new TextButton("Decks", skin);
        TextButton settingsButton = new TextButton("Settings", skin);
        TextButton downloadButton = new TextButton("Download", skin);

        stage.addActor(image);
        stage.addActor(playButton);
        stage.addActor(upgradeButton);
        stage.addActor(decksButton);
        stage.addActor(collectionButton);
        stage.addActor(settingsButton);
        stage.addActor(downloadButton);

        playButton.setPosition(190, 170);
        playButton.setWidth(collectionButton.getWidth());
        settingsButton.setPosition(290, 230);
        settingsButton.setWidth(collectionButton.getWidth());
        upgradeButton.setPosition(300, 90);
        upgradeButton.setWidth(collectionButton.getWidth());
        downloadButton.setPosition(20, 230);
        downloadButton.setWidth(collectionButton.getWidth());
        decksButton.setPosition(220, 20);
        decksButton.setWidth(collectionButton.getWidth());
        collectionButton.setPosition(20, 100);

        Table table = new Table();
        table.setFillParent(true);

        BorderedGrid grid = new BorderedGrid(skin);
        grid.pad(9);
        grid.defaults().space(5).growX();
        grid.top();

        BorderedArea imageArea = new BorderedArea(skin);
        TextButton profileButton = new TextButton("Profile", skin);
        TextButton defenseButton = new TextButton("Defense", skin);

        grid.add(imageArea).growX().height(80).row();
        grid.add(profileButton).row();
        grid.add(defenseButton).row();

        table.add(grid).expand().growY().right().width(100);

        stage.addActor(table);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.pushScreen(new QueueScreen(game));
            }

        });

        collectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.pushScreen(new CollectionScreen(game));
            }

        });

        decksButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.pushScreen(new DecksScreen(game));
            }

        });
    }

    @Override
    public void render(float f) {
        stage.act(f);
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {
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
    }

}
