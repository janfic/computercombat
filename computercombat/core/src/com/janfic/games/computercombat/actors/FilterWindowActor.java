package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
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
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.match.MatchState;
import com.janfic.games.computercombat.model.moves.Move;
import com.janfic.games.computercombat.util.CardFilter;

/**
 *
 * @author Jan Fic
 */
public class FilterWindowActor extends Window {

    TextButton applyButton;
    CardFilter filter;
    int rarity = -1;
    Table rarityTable;

    Skin skin;

    public FilterWindowActor(Skin skin) {
        super("Filter", skin);
        this.skin = skin;
        defaults().space(5);

        Panel searchPanel = new Panel(skin);
        searchPanel.add(new Image(skin.get("magnifying_class_icon", Drawable.class))).pad(5);
        TextField searchField = new TextField("", skin);
        searchPanel.add(searchField).growX().row();

        rarityTable = new Table(skin);
        rarityTable.setBackground("panel");
        rarityTable.defaults().expandX();
        buildRarityTable();

        applyButton = new TextButton("Apply", skin);
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                filter = new CardFilter() {
                    @Override
                    public boolean filter(Card card, MatchState state, Move move) {
                        if (searchField.getText().isBlank() && rarity == -1) {
                            return true;
                        } else if (rarity == -1) {
                            return card.getName().toLowerCase().contains(searchField.getText().toLowerCase());
                        } else {
                            return card.getName().toLowerCase().contains(searchField.getText().toLowerCase()) && rarity == card.getRarity();
                        }
                    }
                };
                remove();
            }
        });

        Label searchLabel = new Label("Name:", skin);
        Label rarityLabel = new Label("Rarity: ", skin);
        add(searchLabel).expandX().row();
        add(searchPanel).expandX().row();
        add(rarityLabel).expandX().row();
        add(rarityTable).width(150).row();
        add(applyButton).expand().growX().bottom().row();

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
                return true;
            }
        };

        add(resetAll).growX().row();
        add(cancel).growX().row();
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
