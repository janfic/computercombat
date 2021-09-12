package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.*;
import com.janfic.games.computercombat.data.Deck;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class DecksScreen implements Screen {

    ComputerCombatGame game;

    Skin skin;

    OrthographicCamera stageCamera;
    Stage stage;

    Table table, decks, deckCards;
    Table decksTable, deckTable, collectionTable, collection;

    TextButton createButton, deleteButton;

    DragAndDrop deckToCollectionDragAndDrop, collectionToDeckDragAndDrop;

    DeckActor selectedDeck;

    boolean isPopup;

    public DecksScreen(ComputerCombatGame game) {
        this.game = game;
        this.skin = game.getAssetManager().get(Assets.SKIN, Skin.class);
    }

    @Override
    public void show() {
        this.stageCamera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.isPopup = false;

        this.stage = ComputerCombatGame.makeNewStage(stageCamera);

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.defaults().space(3);
        table.pad(2);

        Table titleTable = new Table(skin);
        titleTable.setBackground("border");

        Label title = new Label("Decks", skin);
        title.setAlignment(Align.center);
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });

        titleTable.add(backButton);
        titleTable.add(title).growX().row();

        table.add(titleTable).growX().colspan(3).row();

        decksTable = new Table(skin);
        decksTable.defaults().space(3);

        Label decksTitle = new Label("Decks", skin, "title");
        decksTitle.setAlignment(Align.center);

        decks = new Table();
        decks.defaults().space(5).height(60).width(70);
        populateDecks();
        ScrollPane decksScroll = new ScrollPane(decks, skin);
        decksScroll.setFadeScrollBars(false);
        createButton = new TextButton("Create", skin);

        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Window window = new Window("Create New Deck", skin);
                window.setSize(stage.getWidth() / 2, stage.getHeight() / 2);
                window.setPosition(stage.getWidth() / 4, stage.getHeight() / 4);

                Table table = new Table();
                TextButton doneButton = new TextButton("Done", skin);
                TextButton cancelButton = new TextButton("Cancel", skin);
                TextField deckNameField = new TextField("", skin);

                doneButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Deck deck = new Deck(deckNameField.getText());
                        game.getCurrentProfile().getDecks().add(deck);
                        populateDecks();
                        window.remove();
                        isPopup = false;
                    }
                });
                cancelButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        window.remove();
                        isPopup = false;
                    }
                });

                table.add(new Label("Enter New Deck Name:", skin)).center().expand().row();
                table.add(deckNameField).expand().row();
                table.add(doneButton).growX().row();
                table.add(cancelButton).growX().row();
                window.add(table).grow();

                isPopup = true;
                stage.addActor(window);
            }
        });

        deleteButton = new TextButton("Delete", skin);
        deleteButton.setColor(Color.LIGHT_GRAY);
        deleteButton.setDisabled(true);

        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedDeck == null) {
                    return;
                }
                Window window = new Window("Delete Deck", skin);
                window.setSize(stage.getWidth() / 2, stage.getHeight() / 2);
                window.setPosition(stage.getWidth() / 4, stage.getHeight() / 4);

                Table table = new Table();
                TextButton doneButton = new TextButton("Delete Deck", skin);
                TextButton cancelButton = new TextButton("Cancel", skin);

                doneButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (selectedDeck == null) {
                            return;
                        }
                        game.getCurrentProfile().getDecks().remove(selectedDeck.getDeck());
                        selectedDeck = null;
                        deckCards.clearChildren();
                        populateDecks();
                        window.remove();
                        deleteButton.setColor(Color.LIGHT_GRAY);
                        isPopup = false;
                    }
                });
                cancelButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        window.remove();
                        isPopup = false;
                    }
                });

                Label l = new Label("Are you sure you want to delete deck: \"" + selectedDeck.getDeck().getName() + "\"?\nThis action cannot be undone!", skin);
                l.setAlignment(Align.center);
                l.setWrap(true);
                table.add(l).grow().row();
                table.add(doneButton).growX().row();
                table.add(cancelButton).growX().row();
                window.add(table).grow();

                isPopup = true;
                stage.addActor(window);
            }
        });

        decksTable.add(decksTitle).growX().row();
        decksTable.add(decksScroll).grow().row();
        decksTable.add(createButton).growX().row();
        decksTable.add(deleteButton).growX().row();

        deckTable = new Table(skin);
        deckTable.defaults().space(3);

        Label cardsTitle = new Label("Cards", skin, "title");
        cardsTitle.setAlignment(Align.center);

        deckCards = new Table();
        deckCards.defaults().space(5);

        ScrollPane deckCardsPane = new ScrollPane(deckCards, skin);
        deckCardsPane.setFadeScrollBars(false);

        TextButton saveButton = new TextButton("Save", skin);

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Json json = new Json();
                        String data = json.toJson(game.getCurrentProfile());
                        game.getServerAPI().sendMessage(new Message(Type.UPDATE_PROFILE, data));

                        while (game.getServerAPI().hasMessage() == false) {
                        }

                        Message response = game.getServerAPI().readMessage();

                        if (response.type == Type.SUCCESS) {
                            saveButton.addAction(Actions.sequence(Actions.color(Color.GREEN), Actions.color(Color.WHITE, 2)));
                        }
                    }
                });

            }
        });

        deckTable.add(cardsTitle).growX().row();
        deckTable.add(deckCardsPane).grow().row();
        deckTable.add(saveButton).growX().row();

        collectionTable = new Table(skin);
        collectionTable.defaults().space(3);

        Table titleRow = new Table(skin);
        titleRow.setBackground("border");
        Label collectionTitle = new Label("Collection", skin);
        collectionTitle.setAlignment(Align.center);
        TextButton filterButton = new TextButton("Filter", skin);

        titleRow.add(collectionTitle).growX();
        titleRow.add(filterButton);

        collection = new Table(skin);
        collection.defaults().space(5).growY();

        ScrollPane collectionPane = new ScrollPane(collection, skin);
        collectionPane.setFadeScrollBars(false);

        collectionTable.add(titleRow).growX().row();
        collectionTable.add(collectionPane).grow().row();

        table.add(decksTable).width(Value.percentWidth(0.20f, table)).growY();
        table.add(deckTable).width(Value.percentWidth(0.25f, table)).growY();
        table.add(collectionTable).grow().row();

        stage.addActor(table);

        Gdx.app.postRunnable(requestProfileInfoRunnable);

        this.deckToCollectionDragAndDrop = new DragAndDrop();
        this.collectionToDeckDragAndDrop = new DragAndDrop();

        deckToCollectionDragAndDrop.addTarget(new Target(collectionPane) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                if (selectedDeck == null) {
                    return;
                }
                Software card = (Software) payload.getObject();
                selectedDeck.getDeck().removeCard(card.getPack() + "/" + card.getName(), 1);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        updateDeckCards();
                    }
                });
            }
        });

        collectionToDeckDragAndDrop.addTarget(new Target(deckCardsPane) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                if (selectedDeck == null) {
                    return;
                }
                Software card = (Software) payload.getObject();
                selectedDeck.getDeck().addCard(card.getPack() + "/" + card.getName(), 1);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        updateDeckCards();
                    }
                });
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
                    deleteButton.setColor(Color.WHITE);
                    deleteButton.setDisabled(false);
                    selectedDeck = d;
                    selectedDeck.setColor(Color.WHITE);
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            updateDeckCards();
                        }
                    });
                }
            });
            decks.add(d).row();
        }
    }

    public void updateDeckCards() {
        Json json = new Json();
        deckCards.clearChildren();
        game.getServerAPI().sendMessage(new Message(Type.CARD_INFO_REQUEST, json.toJson(selectedDeck.getDeck().getCards())));

        while (game.getServerAPI().hasMessage() == false) {
        }

        Message response = game.getServerAPI().readMessage();

        List<Software> cards = json.fromJson(List.class, response.getMessage());
        for (Software card : cards) {
            DeckCardActor dca = new DeckCardActor(game, skin, selectedDeck.getDeck(), card);

            Source source = new Source(dca.getImageArea()) {

                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    Payload payload = new Payload();
                    payload.setObject(card);
                    BorderedArea dragActor = new BorderedArea(skin);
                    dragActor.setWidth(48);
                    dragActor.setHeight(48);
                    dragActor.add(new Image(game.getAssetManager().get("texture_packs/" + card.getPack() + ".atlas", TextureAtlas.class).findRegion(card.getTextureName()))).width(46).height(46);
                    payload.setDragActor(dragActor);
                    return payload;
                }
            };
            deckCards.add(dca).row();
            deckToCollectionDragAndDrop.addSource(source);
        }
    }

    final Runnable requestProfileInfoRunnable = new Runnable() {
        @Override
        public void run() {
            Json json = new Json();
            Profile profile = game.getCurrentProfile();
            Deck playerCollection = profile.getCollection();

            Message request = new Message(Type.CARD_INFO_REQUEST, json.toJson(playerCollection.getCards()));

            game.getServerAPI().sendMessage(request);

            while (game.getServerAPI().hasMessage() == false) {
            }

            Message response = game.getServerAPI().readMessage();
            List<Software> cardInfo = json.fromJson(List.class, response.getMessage());
            cardInfo.sort((o1, o2) -> {
                return o1.getName().compareTo(o2.getName()); //To change body of generated lambdas, choose Tools | Templates.
            });
            collection.clearChildren();
            boolean isEven = false;

            for (Software card : cardInfo) {
                CollectionCard cc = new CollectionCard(game, skin, card, profile.getCollection().getCardCount(card.getPack() + "/" + card.getName()));

                collectionToDeckDragAndDrop.addSource(new Source(cc) {
                    @Override
                    public Payload dragStart(InputEvent ie, float f, float f1, int i) {
                        Payload payload = new Payload();
                        payload.setObject(card);
                        BorderedArea dragActor = new BorderedArea(skin);
                        dragActor.setWidth(48);
                        dragActor.setHeight(48);
                        dragActor.add(new Image(game.getAssetManager().get("texture_packs/" + card.getPack() + ".atlas", TextureAtlas.class).findRegion(card.getTextureName()))).width(46).height(46);
                        payload.setDragActor(dragActor);
                        return payload;
                    }
                });
                collection.add(cc);
                if (isEven) {
                    collection.row();
                }
                isEven = !isEven;
            }
        }
    };

}
