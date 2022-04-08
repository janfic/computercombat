package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.CollectionCard;
import com.janfic.games.computercombat.actors.FilterWindowActor;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Collection;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class CollectionScreen implements Screen {

    ComputerCombatGame game;

    Collection pack;

    Stage stage;

    OrthographicCamera camera;

    Skin skin;
    Table collection;
    BorderedGrid filterBar;
    ScrollPane collectionScrollPane;

    FilterWindowActor filterWindow;
    List<CollectionCard> cards;

    public CollectionScreen(ComputerCombatGame game) {
        this.game = game;
        this.skin = game.getAssetManager().get(Assets.SKIN);
        this.cards = new ArrayList<CollectionCard>();
    }

    public CollectionScreen(ComputerCombatGame game, Collection collection) {
        this(game);
        this.pack = collection;

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

        Label title = new Label(pack == null ? "Collection" : pack.getName() + " Collection", skin);
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
        List<Integer> collections = new ArrayList<>();
        if (pack != null) {
            collections.add(pack.getID());
        }
        filterWindow = new FilterWindowActor(collections, game, skin);
        filterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                List<Integer> collections = new ArrayList<>();
                if (pack != null) {
                    collections.add(pack.getID());
                }
                filterWindow = new FilterWindowActor(collections, game, skin);
                filterWindow.setSize(4 * stage.getWidth() / 5, 4 * stage.getHeight() / 5);
                filterWindow.setPosition(stage.getWidth() / 10, stage.getHeight() / 10);
                filterWindow.addApplyButtonListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        buildCollection();
                    }
                });
                filterButton.getStage().addActor(filterWindow);
            }
        });

        collection = new Table();
        collection.defaults().space(1).growY();

        collectionScrollPane = new ScrollPane(collection, skin);
        collectionScrollPane.setFadeScrollBars(false);

        table.add(titleTable).growX().row();
        table.add(collectionScrollPane).grow().row();

        stage.addActor(table);
        buildCollection();
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

    public void buildCollection() {
        Profile profile = game.getCurrentProfile();
        Map<Card, Integer> playerCards = new HashMap<>();
        Map<Card, Integer> allCards = new HashMap<>();
        List<Card> ac = SQLAPI.getSingleton().getCardsInfo(new ArrayList<>(), profile.getUID());
        playerCards = SQLAPI.getSingleton().getPlayerOwnedCards(profile.getUID());
        for (Card card : ac) {
            allCards.put(card, 0);
        }
        for (Card card : playerCards.keySet()) {
            allCards.put(card, playerCards.get(card));
        }
        collection.clearChildren();
        int row = 0;

        for (Card card : allCards.keySet()) {
            CollectionCard cc = new CollectionCard(game, skin, card, allCards.get(card));
            if (allCards.get(card) == 0) {
                cc.setColor(1, 1, 1, 0.5f);
            }
            if (filterWindow.getFilter().filter(card, null, null)) {
                cards.add(cc);
                collection.add(cc);
                row++;
                if (row % 4 == 0) {
                    collection.row();
                    row = 0;
                }
            }
        }
    }
}
