package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.*;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.*;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    ProgressBar deckSize;
    OverlayTextLabelArea<DeckActor> deckSizeArea;

    TextButton createButton, deleteButton;

    DragAndDrop deckToCollectionDragAndDrop, collectionToDeckDragAndDrop;

    FilterWindowActor filterWindow;

    DeckActor selectedDeck;
    Map<Card, Integer> software;

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

        Profile profile = game.getCurrentProfile();
        software = SQLAPI.getSingleton().getPlayerOwnedCards(profile.getUID());
        Gdx.input.setInputProcessor(stage);

        filterWindow = new FilterWindowActor(game, skin);

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

        decksTable = new Table(skin);
        decksTable.defaults().space(3);

        Label decksTitle = new Label("Decks", skin, "title");
        decksTitle.setAlignment(Align.center);

        decks = new Table();
        decks.defaults().space(5).height(60).width(70);
        populateDecks();
        ScrollPane decksScroll = new ScrollPane(decks, skin);
        decksScroll.setFadeScrollBars(false);
        decksScroll.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setScrollFocus(decksScroll);
            }
        });
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
                        SQLAPI.getSingleton().savePlayerDeck(deck, game.getCurrentProfile().getUID());
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
                        SQLAPI.getSingleton().deletePlayerDeck(selectedDeck.getDeck(), game.getCurrentProfile().getUID());
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

        deckSize = new ProgressBar(0, 8, 1, false, skin);
        deckSize.setWidth(100);
        Stack deckSizeStack = new Stack();
        deckSizeStack.add(deckSize);
        Table deckSizeOverlay = new Table();
        deckSizeArea = new OverlayTextLabelArea<DeckActor>(skin, selectedDeck) {
            @Override
            public String updateLabel(DeckActor dataObject) {
                if (dataObject != null) {
                    int amount = dataObject.getDeck().getCardCount(-1);
                    deckSize.setValue(dataObject.getDeck().getCardCount(-1));
                    return "" + dataObject.getDeck().getCardCount(-1);
                } else {
                    return "-";
                }
            }
        };
        deckSizeOverlay.add(deckSizeArea).expand().fillX().width(9).height(9);
        deckSizeStack.add(deckSizeOverlay);

        deckCards = new Table();
        deckCards.defaults().space(5);

        ScrollPane deckCardsPane = new ScrollPane(deckCards, skin);
        deckCardsPane.setFadeScrollBars(false);
        deckCardsPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setScrollFocus(deckCardsPane);
            }
        });
        TextButton saveButton = new TextButton("Save", skin);

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedDeck != null) {
                    int amount = selectedDeck.getDeck().getCardCount(-1);
                    if (amount != 8) {
                        Window window = new Window("Invalid Deck", skin);
                        window.setSize(stage.getWidth() / 2f, stage.getHeight() / 2f);
                        window.setPosition(stage.getWidth() / 4f, stage.getHeight() / 4f);
                        Label info = new Label("The selected deck will be saved. But it is invalid for play due because it has " + amount + " cards.\n Decks must have exactly 8 cards.", skin);
                        info.setWrap(true);
                        info.setAlignment(Align.center);
                        TextButton okay = new TextButton("Okay", skin);
                        okay.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                window.remove();
                                SQLAPI.getSingleton().savePlayerDeck(selectedDeck.getDeck(), game.getCurrentProfile().getUID());
                                saveButton.addAction(Actions.sequence(Actions.color(Color.GREEN), Actions.color(Color.WHITE, 2)));
                            }
                        });
                        window.add(info).grow().row();
                        window.add(okay).growX().row();
                        stage.addActor(window);
                    } else {
                        SQLAPI.getSingleton().savePlayerDeck(selectedDeck.getDeck(), game.getCurrentProfile().getUID());
                        saveButton.addAction(Actions.sequence(Actions.color(Color.GREEN), Actions.color(Color.WHITE, 2)));
                    }

                }
            }
        });

        deckTable.add(cardsTitle).growX().row();
        deckTable.add(deckSizeStack).width(100).row();
        deckTable.add(deckCardsPane).grow().row();
        deckTable.add(saveButton).growX().row();

        collectionTable = new Table(skin);
        collectionTable.defaults().space(3);

        Table titleRow = new Table(skin);
        titleRow.setBackground("border");
        Label collectionTitle = new Label("Collection", skin);
        collectionTitle.setAlignment(Align.center);
        TextButton filterButton = new TextButton("Filter", skin);

        filterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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

        titleRow.add(collectionTitle).growX();
        titleRow.add(filterButton);

        collection = new Table(skin);
        collection.defaults().space(5).growY();

        ScrollPane collectionPane = new ScrollPane(collection, skin);
        collectionPane.setFadeScrollBars(false);
        collectionPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setScrollFocus(collectionPane);
            }
        });

        collectionTable.add(titleRow).growX().row();
        collectionTable.add(collectionPane).grow().row();

        table.add(titleTable).growX().colspan(3).row();
        table.add(decksTable).width(Value.percentWidth(0.20f, table)).growY();
        table.add(deckTable).width(Value.percentWidth(0.25f, table)).growY();
        table.add(collectionTable).grow().row();

        stage.addActor(table);

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
                Card card = (Card) payload.getObject();
                selectedDeck.getDeck().removeCard(card, 1);

                updateDeckCards();
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
                Card card = (Card) payload.getObject();
                if (card.getID() == 0) { // prevent adding computer to deck
                    return;
                }
                if (selectedDeck.getDeck().getCardCount(card.getID()) < software.get(card)) {
                    selectedDeck.getDeck().addCard(card, 1);
                    updateDeckCards();
                }
            }
        });
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
        stageCamera.update();
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

    private void populateDecks() {
        decks.clearChildren();
        List<Deck> playerDecks = SQLAPI.getSingleton().getPlayerDecks(game.getCurrentProfile().getUID());

        for (Deck deck : playerDecks) {
            DeckActor d = new DeckActor(deck, skin);
            d.setColor(Color.LIGHT_GRAY);
            d.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedDeck != null) {
                        selectedDeck.unselect();
                    }
                    deleteButton.setColor(Color.WHITE);
                    deleteButton.setDisabled(false);
                    selectedDeck = d;
                    selectedDeck.select();
                    updateDeckProgress();
                    updateDeckCards();
                }
            });
            decks.add(d).row();
        }
    }

    private void updateDeckProgress() {
        deckSizeArea.setDataObject(selectedDeck);
        int amount = selectedDeck.getDeck().getCardCount(-1);
        if (amount < 8) {
            ProgressBarStyle grey = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
            grey.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("dae0ea"));
            deckSize.setStyle(grey);
        } else if (amount > 8) {
            ProgressBarStyle red = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
            red.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("df3e23"));
            deckSize.setStyle(red);
        } else {
            ProgressBarStyle green = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
            green.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("9cdb43"));
            deckSize.setStyle(green);
        }
        deckSize.setValue(amount);
    }

    private void updateDeckCards() {
        deckCards.clearChildren();

        List<Card> cards = SQLAPI.getSingleton().getCardsInfo(selectedDeck.getDeck().getStack(), game.getCurrentProfile().getUID());
        for (Card card : cards) {
            DeckCardActor dca = new DeckCardActor(game, skin, selectedDeck.getDeck(), card);

            Source source = new Source(dca.getImageArea()) {

                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    Payload payload = new Payload();
                    payload.setObject(card);
                    BorderedArea dragActor = new BorderedArea(skin);
                    dragActor.setWidth(48);
                    dragActor.setHeight(48);
                    dragActor.add(new Image(game.getAssetManager().get("texture_packs/" + card.getCollection().getTextureName() + ".atlas", TextureAtlas.class).findRegion(card.getTextureName()))).width(46).height(46);
                    payload.setDragActor(dragActor);
                    return payload;
                }
            };
            deckCards.add(dca).row();
            deckToCollectionDragAndDrop.addSource(source);
        }
        updateDeckProgress();
    }

    public void buildCollection() {
        Profile profile = game.getCurrentProfile();
        software = SQLAPI.getSingleton().getPlayerOwnedCards(profile.getUID());
        collection.clearChildren();
        int row = 0;

        for (Card card : software.keySet()) {
            Table cardTable = new Table();
            CollectionCard cc = new CollectionCard(game, skin, card, software.get(card));
            cardTable.add(cc).colspan(3).grow().row();
            TextButton addButton = new TextButton("+", skin) {
                @Override
                public void act(float delta) {
                    super.act(delta); //To change body of generated methods, choose Tools | Templates.
                    if (selectedDeck != null && selectedDeck.getDeck().getCardCount(card.getID()) >= software.get(card)) {
                        this.setColor(Color.RED);
                    } else {
                        this.setColor(Color.WHITE);
                    }
                }
            };
            addButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedDeck != null) {
                        if (selectedDeck.getDeck().getCardCount(card.getID()) < software.get(card)) {
                            selectedDeck.getDeck().addCard(card, 1);
                            updateDeckCards();
                        }
                    }
                }
            });
            TextButton removeButton = new TextButton("-", skin);
            removeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedDeck != null) {
                        selectedDeck.getDeck().removeCard(card, 1);
                        updateDeckCards();
                    }
                }
            });
            Label amountInDeck = new Label("-", skin) {
                @Override
                public void act(float delta) {
                    super.act(delta);
                    if (selectedDeck != null) {
                        this.setText("" + selectedDeck.getDeck().getCardCount(card.getID()));
                    } else {
                        this.setText("-");
                    }
                }
            };
            cardTable.add(removeButton).expandX().width(30);
            cardTable.add(amountInDeck);
            cardTable.add(addButton).expandX().width(30);
            if (filterWindow.getFilter().filter(card, null, null)) {
                collection.add(cardTable);
                collectionToDeckDragAndDrop.addSource(new Source(cc) {
                    @Override
                    public Payload dragStart(InputEvent ie, float f, float f1, int i) {
                        Payload payload = new Payload();
                        payload.setObject(card);
                        BorderedArea dragActor = new BorderedArea(skin);
                        dragActor.setWidth(48);
                        dragActor.setHeight(48);
                        dragActor.add(new Image(game.getAssetManager().get("texture_packs/" + card.getCollection().getTextureName() + ".atlas", TextureAtlas.class).findRegion(card.getTextureName()))).width(46).height(46);
                        payload.setDragActor(dragActor);
                        return payload;
                    }
                });
                row++;
                if (row % 2 == 0) {
                    collection.row();
                    row = 0;
                }
            }
        }
    }
}
