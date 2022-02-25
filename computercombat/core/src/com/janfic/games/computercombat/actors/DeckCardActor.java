package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Deck;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Software;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class DeckCardActor extends Panel {

    ComputerCombatGame game;

    Deck deck;
    Software software;

    BorderedArea imageArea;
    Label amountLabel;
    Table leds;

    public static final Map<Integer, String> components;

    static {
        components = new HashMap<>();
        components.put(1, "CPU");
        components.put(5, "BUG");
        components.put(6, "POWER");
        components.put(4, "NETWORK");
        components.put(2, "RAM");
        components.put(5, "STORAGE");
    }

    public DeckCardActor(ComputerCombatGame game, Skin skin, Deck deck, Software software) {
        super(skin);
        this.game = game;
        this.software = software;
        this.deck = deck;

        leds = new Table();
        leds.defaults().space(5);
        for (Integer runComponent : software.getRunComponents()) {
            LEDActor led = new LEDActor(skin, components.get(runComponent));
            led.setLightOn(true);
            leds.add(led).row();
        }

        imageArea = new BorderedArea(skin);
        imageArea.add(new Image(game.getAssetManager().get("texture_packs/" + software.getCollection().getTextureName() + ".atlas", TextureAtlas.class).findRegion(software.getTextureName())));

        amountLabel = new Label("" + deck.getCardCount(software.getID()), skin, "paneled");
        amountLabel.setAlignment(Align.center);

        this.defaults().space(5);
        this.pad(4);
        this.add(leds);
        this.add(imageArea).width(48).height(48);
        this.add(amountLabel).width(20);
    }

    public BorderedArea getImageArea() {
        return imageArea;
    }
}
