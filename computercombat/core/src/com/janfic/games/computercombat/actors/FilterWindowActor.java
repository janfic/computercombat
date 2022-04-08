package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Collection;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.network.client.SQLAPI;
import com.janfic.games.computercombat.util.CardFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Fic
 */
public class FilterWindowActor extends Window {

    ComputerCombatGame game;
    TextButton applyButton;
    CardFilter filter;
    CardFilter nameFilter, rarityFilter, collectionFilter;
    int rarity = -1;
    Table rarityTable, collectionTable;

    List<Integer> collections;
    Skin skin;

    public FilterWindowActor(List<Integer> collections, ComputerCombatGame game, Skin skin) {
        this(game, skin);
        this.collections = collections;
    }

    public FilterWindowActor(ComputerCombatGame game, Skin skin) {
        super("Filter", skin);
        this.skin = skin;
        this.game = game;
        defaults().space(5);

        Panel searchPanel = new Panel(skin);
        searchPanel.add(new Image(skin.get("magnifying_class_icon", Drawable.class))).pad(5);
        TextField searchField = new TextField("", skin);
        searchPanel.add(searchField).growX().row();

        rarityTable = new Table(skin);
        collectionTable = new Table(skin);
        collections = new ArrayList<>();
        collectionTable.setBackground("panel");
        collectionTable.defaults().expandX();
        rarityTable.setBackground("panel");
        rarityTable.defaults().expandX();
        buildRarityTable();
        buildCollectionTable();

        nameFilter = new CardFilter() {
            @Override
            public boolean filter(Card card, MatchState state, Move move) {
                if (searchField.getText().isBlank()) {
                    return true;
                } else {
                    return card.getName().toLowerCase().contains(searchField.getText().toLowerCase());
                }
            }
        };
        collectionFilter = new CardFilter() {
            @Override
            public boolean filter(Card card, MatchState state, Move move) {
                if (collections.isEmpty()) {
                    return true;
                } else {
                    return collections.contains(card.getCollection().getID());
                }
            }
        };
        rarityFilter = new CardFilter() {
            @Override
            public boolean filter(Card card, MatchState state, Move move) {
                if (rarity == -1) {
                    return true;
                } else {
                    return card.getRarity() == rarity;
                }
            }
        };

        applyButton = new TextButton("Apply", skin);
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });

        Label searchLabel = new Label("Name:", skin);
        Label rarityLabel = new Label("Rarity: ", skin);
        Label collectionLabel = new Label("Collection: ", skin);
        add(searchLabel).expandX();
        add(searchPanel).growX().row();
        add(rarityLabel).expandX();
        add(rarityTable).growX().row();
        add(collectionLabel).expandX();
        add(collectionTable).growX().row();

        Table buttons = new Table(skin);
        buttons.add(applyButton).growX();

        TextButton resetAll = new TextButton("Reset", skin);
        resetAll.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                searchField.setText("");
                rarity = -1;
                buildRarityTable();
            }
        });
        TextButton cancel = new TextButton("Cancel", skin);
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });

        this.filter = new CardFilter() {
            @Override
            public boolean filter(Card card, MatchState state, Move move) {
                return nameFilter.filter(card, state, move)
                        && collectionFilter.filter(card, state, move)
                        && rarityFilter.filter(card, state, move);
            }
        };

        buttons.add(resetAll).growX().row();
        buttons.add(cancel).growX().colspan(2).row();
        add(buttons).growX().colspan(2);
    }

    public CardFilter getFilter() {
        return filter;
    }

    public void addApplyButtonListener(ClickListener listener) {
        applyButton.addListener(listener);
    }

    @Override
    public void clearListeners() {
        super.clearListeners(); //To change body of generated methods, choose Tools | Templates.
        applyButton.clearListeners();
    }

    private void buildRarityTable() {
        rarityTable.clear();
        for (int i = 5; i >= 1; i--) {
            int j = i;
            Image r = new Image(skin, rarity != -1 ? (rarity <= i ? "rarity_filled" : "rarity_empty") : "rarity_empty");
            r.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    rarity = j;
                    buildRarityTable();
                }
            });
            r.setColor(rarity != -1 ? skin.getColor(CollectionCard.rarityColors[rarity]) : Color.WHITE);
            rarityTable.add(r);
        }
    }

    private void buildCollectionTable() {
        collectionTable.clear();
        List<Collection> collections = SQLAPI.getSingleton().getCollections();
        for (Collection collection : collections) {
            Table table = new Table();
            table.defaults().pad(1);
            TextureAtlas texturePack = game.getAssetManager().get("texture_packs/" + collection.getTextureName() + ".atlas", TextureAtlas.class);
            TextureRegion icon = texturePack.findRegion(collection.getPath() + "_icon");
            if (icon == null) {
                continue;
            }
            table.add(new Image(icon)).row();
            LEDActor led = new LEDActor(skin, "NETWORK") {
                @Override
                public void act(float delta) {
                    this.setLightOn(FilterWindowActor.this.collections.contains((Integer) collection.getID()));
                }
            };
            table.add(led);
            collectionTable.add(table);
            table.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (FilterWindowActor.this.collections.contains(collection.getID())) {
                        FilterWindowActor.this.collections.remove((Integer) collection.getID());
                        led.setLightOn(false);
                    } else {
                        FilterWindowActor.this.collections.add((Integer) collection.getID());
                        led.setLightOn(true);
                    }
                }
            });
        }
    }

    @Override
    protected void setStage(Stage stage) {
        if (stage != null) {
            for (Actor actor : stage.getActors()) {
                if (actor != this) {
                    actor.setTouchable(Touchable.disabled);
                }
            }
        }
        super.setStage(stage); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove() {
        for (Actor actor : getStage().getActors()) {
            actor.setTouchable(Touchable.enabled);
        }
        return super.remove(); //To change body of generated methods, choose Tools | Templates.
    }

}
