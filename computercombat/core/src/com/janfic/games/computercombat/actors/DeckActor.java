package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.model.Deck;

/**
 *
 * @author Jan Fic
 */
public class DeckActor extends Table {
    
    Deck deck;
    Skin skin;
    LEDActor selectSignal;
    
    boolean selected = false;
    
    public DeckActor(Deck deck, Skin skin) {
        super(skin);
        this.deck = deck;
        this.skin = skin;
        this.setBackground("decked_panel");
        pad(4, 4, 12, 4);
        Label name = new Label(deck.getName(), skin);
        name.setWrap(true);
        name.setAlignment(Align.center);
        selectSignal = new LEDActor(skin, "NETWORK");
        this.add(selectSignal).expandX().right().row();
        this.add(name).grow().center();
    }
    
    public Deck getDeck() {
        return deck;
    }
    
    public void select() {
        selectSignal.setLightOn(true);
        setColor(Color.WHITE);
    }
    
    public void unselect() {
        selectSignal.setLightOn(false);
        setColor(Color.LIGHT_GRAY);
    }
    
}
