package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.abilities.DrawAbility;
import com.janfic.games.computercombat.model.components.CPUComponent;
import com.janfic.games.computercombat.model.components.NetworkComponent;
import com.janfic.games.computercombat.model.components.PowerComponent;
import com.janfic.games.computercombat.model.components.RAMComponent;
import com.janfic.games.computercombat.model.components.StorageComponent;

/**
 *
 * @author Jan Fic
 */
public class Computer extends Card implements Serializable {

    int deckSize;

    public Computer() {
        this("owner");
    }

    public Computer(String playerUID) {
        super(0, playerUID, "Computer", "computer_pack", "computer", 1, 20, 0, 0, 0, new Class[]{
            CPUComponent.class,
            NetworkComponent.class,
            StorageComponent.class,
            RAMComponent.class,
            PowerComponent.class
        }, 20, new DrawAbility());
        this.getAbility().setInformation("Draw a card from you deck", "draw_card", "Draw Card", "new DrawAbility()", 0);
        this.health = 20;
        this.runProgress = 20;
        this.deckSize = 8;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("deckSize", deckSize);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.deckSize = json.readValue("deckSize", Integer.class, jsonData);
    }

    public void drawCard() {
        this.runProgress = 0;
    }
}
