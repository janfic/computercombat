package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.DeckActor;
import com.janfic.games.computercombat.data.Deck;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class QueueScreen implements Screen {

    ComputerCombatGame game;

    Stage stage;
    OrthographicCamera camera;

    Skin skin;

    Table decksTable;
    Table decks;

    DeckActor selectedDeck;

    Label queueStatus;
    Table queueStatusTable;
    TextButton cancelQueue;

    boolean queued, canceled;
    boolean isRanked, isLive;

    public QueueScreen(ComputerCombatGame game) {
        this.game = game;
        this.skin = game.getAssetManager().get(Assets.SKIN, Skin.class);
    }

    @Override
    public void show() {
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        isRanked = false;
        isLive = true;

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.defaults().space(3);
        table.pad(3);
        table.setFillParent(true);

        Table titleTable = new Table(skin);
        titleTable.setBackground("border");

        Label title = new Label("Play", skin);
        title.setAlignment(Align.center);
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getServerAPI().sendMessage(new Message(Type.CANCEL_QUEUE, game.getCurrentProfile().getUID()));
                game.popScreen();
            }
        });

        titleTable.add(backButton);
        titleTable.add(title).growX().row();

        table.add(titleTable).growX().row();

        BorderedGrid mainTable = new BorderedGrid(skin);

        decksTable = new Table(skin);
        decksTable.defaults().space(3);

        Label decksTitle = new Label("Decks", skin, "title");
        decksTitle.setAlignment(Align.center);

        decks = new Table();
        decks.defaults().space(5).height(60).width(70);
        populateDecks();
        ScrollPane decksScroll = new ScrollPane(decks, skin);
        decksScroll.setFadeScrollBars(false);

        TextButton updateButton = new TextButton("Edit Decks", skin);
        updateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getServerAPI().sendMessage(new Message(Type.CANCEL_QUEUE, game.getCurrentProfile().getUID()));
                while (game.getServerAPI().hasMessage() == false) {
                }

                game.getServerAPI().readMessage();
                queued = false;
                canceled = false;

                game.pushScreen(new DecksScreen(game));
            }
        });

        decksTable.add(decksTitle).growX().row();
        decksTable.add(decksScroll).grow().row();
        decksTable.add(updateButton).growX();

        mainTable.add(decksTable).left().width(120).growY().pad(5);

        Table playTable = new Table();
        playTable.defaults().space(3);
        playTable.pad(5);
        playTable.top();

        Table toggleRankedButton = new Table(skin);
        toggleRankedButton.setBackground("border");
        toggleRankedButton.defaults().space(3);
        toggleRankedButton.pad(10);

        TextButton rankedButton = new TextButton("Ranked", skin);
        TextButton casualButton = new TextButton("Casual", skin);

        rankedButton.setColor(Color.LIGHT_GRAY);
        casualButton.setColor(Color.WHITE);

        rankedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                rankedButton.setColor(Color.WHITE);
                casualButton.setColor(Color.LIGHT_GRAY);
                isRanked = true;
            }
        });

        casualButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                rankedButton.setColor(Color.LIGHT_GRAY);
                casualButton.setColor(Color.WHITE);
                isRanked = false;
            }
        });

        toggleRankedButton.add(rankedButton, casualButton);

        Table modeToggleButton = new Table(skin);
        modeToggleButton.pad(10);
        modeToggleButton.defaults().space(3);
        modeToggleButton.setBackground("border");

        TextButton liveMatchButton = new TextButton("Live\n-----\nPlay against an opponent in real time", skin);
        TextButton raidMatchButton = new TextButton("Raid\n-----\nPlay against an offline player's defenses", skin);
        liveMatchButton.getLabel().setWrap(true);
        raidMatchButton.getLabel().setWrap(true);
        raidMatchButton.setColor(Color.LIGHT_GRAY);

        liveMatchButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                raidMatchButton.setColor(Color.LIGHT_GRAY);
                liveMatchButton.setColor(Color.WHITE);
                isLive = true;
            }
        });

        raidMatchButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                liveMatchButton.setColor(Color.LIGHT_GRAY);
                raidMatchButton.setColor(Color.WHITE);
                isLive = false;
            }
        });

        modeToggleButton.add(liveMatchButton).grow();
        modeToggleButton.add(raidMatchButton).grow();

        TextButton playButton = new TextButton("Play", skin);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!queued && selectedDeck != null) {
                    Gdx.app.postRunnable(queue);
                }
            }
        });

        queueStatusTable = new Table(skin);
        queueStatusTable.setBackground("border");

        cancelQueue = new TextButton("Cancel", skin);

        cancelQueue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getServerAPI().sendMessage(new Message(Type.CANCEL_QUEUE, game.getCurrentProfile().getUID()));
                canceled = true;
            }
        });

        queueStatus = new Label("Press Play to Queue", skin);
        queueStatus.setAlignment(Align.center);

        queueStatusTable.add(queueStatus).growX().row();
        //queueStatusTable.add(cancelQueue);

        playTable.add(toggleRankedButton).row();
        playTable.add(modeToggleButton).grow().row();
        playTable.add(playButton).growX().row();
        playTable.add(queueStatusTable).growX().row();

        mainTable.add(playTable).grow().row();

        table.add(mainTable).grow().row();

        stage.addActor(table);
    }

    @Override
    public void render(float f) {
        stage.act(f);
        stage.draw();
        if (game.getServerAPI().hasMessage()) {
            Message m = game.getServerAPI().readMessage();
            if (m.type == Type.QUEUE_POSITION) {
                queueStatus.setText("Queued: Position " + m.getMessage() + " in queue");
            }
            if (m.type == Type.SUCCESS && canceled) {
                queueStatusTable.clearChildren();
                queueStatus.setText("Press Play to Queue");
                queueStatusTable.add(queueStatus).growX().row();
                canceled = false;
                queued = false;
            }
        }
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

    public void populateDecks() {
        decks.clearChildren();
        for (Deck deck : game.getCurrentProfile().getDecks()) {
            DeckActor d = new DeckActor(deck, skin);
            d.setColor(Color.LIGHT_GRAY);
            d.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedDeck != null) {
                        selectedDeck.setColor(Color.LIGHT_GRAY);
                    }
                    selectedDeck = d;
                    selectedDeck.setColor(Color.WHITE);
                }
            });
            decks.add(d).row();
        }
    }

    private Runnable queue = new Runnable() {
        @Override
        public void run() {
            Json json = new Json();
            List<String> data = new ArrayList<>();
            data.add(json.toJson(game.getCurrentProfile()));
            data.add(json.toJson(selectedDeck.getDeck()));
            data.add(json.toJson(new boolean[]{isRanked, isLive}));
            Message requestQueue = new Message(Type.JOIN_QUEUE_REQUEST, json.toJson(data));

            game.getServerAPI().sendMessage(requestQueue);

            while (game.getServerAPI().hasMessage() == false) {
            }

            Message response = game.getServerAPI().readMessage();

            if (response.type == Type.QUEUE_POSITION) {
                queued = true;
                queueStatus.setText("Queued: Position " + response.getMessage() + " in queue");
            }

            queueStatusTable.clearChildren();
            queueStatusTable.add(cancelQueue);
            queueStatusTable.add(queueStatus).growX().row();
        }
    };

}
