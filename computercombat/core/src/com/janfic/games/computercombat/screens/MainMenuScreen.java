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
import com.janfic.games.computercombat.actors.BorderedGrid;

/**
 *
 * @author Jan Fic
 */
public class MainMenuScreen implements Screen {

    ComputerCombatGame game;

    Stage stage;
    OrthographicCamera camera;

    Skin skin;

    public MainMenuScreen(ComputerCombatGame game) {
        this.game = game;
        this.skin = game.getAssetManager().get(Assets.SKIN);
    }

    @Override
    public void show() {
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.defaults().space(10);
        table.pad(10);

        Image image = new Image(game.getAssetManager().get(Assets.TITLE, Texture.class));

        BorderedGrid grid = new BorderedGrid(skin);
        TextButton loginButton = new TextButton("Login", skin);
        TextButton accountButton = new TextButton("New Account", skin);
        TextButton exitButton = new TextButton("Exit", skin);
        grid.add(loginButton).pad(10).growX().row();
        grid.add(accountButton).pad(10).growX().row();
        grid.add(exitButton).pad(10).growX();

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
            }
        });

        accountButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SignUpScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(image).width(220).height(70).row();
        table.add(grid).width(150).height(150).expand();

        stage.addActor(table);
    }

    @Override
    public void render(float f) {
        stage.act(f);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
    }

}
