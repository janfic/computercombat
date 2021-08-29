package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;

/**
 *
 * @author Jan Fic
 */
public class CollectionScreen implements Screen {

    ComputerCombatGame game;

    Stage stage;
    OrthographicCamera camera;

    Skin skin;

    public CollectionScreen(ComputerCombatGame game) {
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
        table.defaults().space(5);
        table.pad(5);

        Label title = new Label("Collection", skin, "title");
        title.setAlignment(Align.center);

        Table collection = new Table();
        ScrollPane collectionScrollPane = new ScrollPane(collection, skin);

        BorderedGrid filterBar = new BorderedGrid(skin);
        filterBar.pad(10);
        filterBar.defaults().space(5);
        filterBar.top();

        Panel filterTitle = new Panel(skin);
        filterTitle.add(new Label("Filter Collection", skin)).pad(2);
        Panel searchPanel = new Panel(skin);
        searchPanel.add(new Image(skin.get("magnifying_class_icon", Drawable.class))).pad(5);
        TextField searchField = new TextField("", skin);
        searchPanel.add(searchField).growX().row();

        TextButton applyButton = new TextButton("Apply", skin);

        filterBar.add(filterTitle).row();
        filterBar.add(searchPanel).row();
        filterBar.add(applyButton).row();

        TextButton backButton = new TextButton("Back", skin);

        table.add(title).colspan(2).growX().row();
        table.add(collectionScrollPane).grow();
        table.add(filterBar).growY().row();
        table.add(backButton).expandX().width(150).left().row();

        stage.addActor(table);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });
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

    final Runnable requestProfileInfoRunnable = new Runnable() {
        @Override
        public void run() {
            Message request = new Message(Type.PROFILE_INFO_REQUEST, "");
        }
    };

}
