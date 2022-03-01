package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.abilities.DrawAbility;
import java.util.Arrays;

/**
 *
 * @author Jan Fic
 */
public class Computer extends Card implements Serializable {

    public Computer() {
        this("owner");
    }

    public Computer(String playerUID) {
        super(0, playerUID, "Computer", new Collection(), "computer", 1, 20, 0, 0, 0, new int[]{1, 2, 3, 4, 6}, 20, new DrawAbility(), 0);
        this.getAbility().setInformation("Draw a card from you deck", "draw_card", "Draw Card", "new DrawAbility()", 0);
        this.health = 20;
        this.runProgress = 20;
    }

    @Override
    public void write(Json json) {
        super.write(json);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
    }

    public void drawCard() {
        this.runProgress = 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Computer c = new Computer(this.ownerUID);
        c.matchID = this.matchID;
        c.health = this.health;
        c.armor = this.armor;
        c.runProgress = this.runProgress;
        c.attack = this.attack;
        return c;
    }
}
