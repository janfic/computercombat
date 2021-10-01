package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.CollectionCard;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class CollectionScreen implements Screen {

    ComputerCombatGame game;

    Stage stage;

    OrthographicCamera camera;

    Skin skin;
    Table collection;
    BorderedGrid filterBar;
    ScrollPane collectionScrollPane;

    ShapeRenderer sr;

    public CollectionScreen(ComputerCombatGame game) {
        this.game = game;
        this.skin = game.getAssetManager().get(Assets.SKIN);
        this.sr = new ShapeRenderer();
    }

    @Override
    public void show() {
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.defaults().space(5);
        table.pad(4);

        Table titleTable = new Table(skin);
        titleTable.setBackground("border");

        Label title = new Label("Collection", skin);
        title.setAlignment(Align.center);
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });
        TextButton filterButton = new TextButton("Filter", skin);

        titleTable.add(backButton);
        titleTable.add(title).growX();
        titleTable.add(filterButton).row();

        filterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Window w = new Window("Filter", skin);
                w.defaults().space(5);

                Panel searchPanel = new Panel(skin);
                searchPanel.add(new Image(skin.get("magnifying_class_icon", Drawable.class))).pad(5);
                TextField searchField = new TextField("", skin);
                searchPanel.add(searchField).growX().row();

                TextButton applyButton = new TextButton("Apply", skin);

                applyButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        w.remove();
                    }
                });

                w.add(searchPanel).expand().top().row();
                w.add(applyButton).growX().bottom().row();

                TextButton cancel = new TextButton("Cancel", skin);
                cancel.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        w.remove();
                    }
                });

                w.add(cancel).growX().row();

                w.setSize(4 * stage.getWidth() / 5, 4 * stage.getHeight() / 5);
                w.setPosition(stage.getWidth() / 10, stage.getHeight() / 10);
                filterButton.getStage().addActor(w);
            }
        });

        collection = new Table();
        collection.defaults().space(1).growY();

        collectionScrollPane = new ScrollPane(collection, skin);
        collectionScrollPane.setFadeScrollBars(false);

        table.add(titleTable).growX().row();
        table.add(collectionScrollPane).grow().row();

        stage.addActor(table);
        Gdx.app.postRunnable(requestProfileInfoRunnable);
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
            Profile profile = game.getCurrentProfile();

            Map<Card, Integer> software = SQLAPI.getSingleton().getPlayerOwnedCards(profile.getUID());

            collection.clearChildren();
            int row = 0;

            for (Card card : software.keySet()) {
                CollectionCard cc = new CollectionCard(game, skin, (Software) card, software.get(card));
                collection.add(cc);
                row++;
                if (row % 4 == 0) {
                    collection.row();
                    row = 0;
                }
            }
        }
    };

}
