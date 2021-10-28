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

    int health, progress;
    int deckSize;

    public Computer() {
        super(0, "Computer", "computer_pack", "computer", 1, 20, 0, 0, 0, new Class[]{
            CPUComponent.class,
            NetworkComponent.class,
            StorageComponent.class,
            RAMComponent.class,
            PowerComponent.class
        }, 20, new DrawAbility());
        this.getAbility().setInformation("Draw a card from you deck", "draw_card", "Draw Card", "new DrawAbility()", 0);
        this.health = 20;
        this.progress = 20;
        this.deckSize = 8;
    }

    @Override
    public void write(Json json) {
        json.writeValue("health", health);
        json.writeValue("progress", progress);
        json.writeValue("deckSize", deckSize);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.health = json.readValue("health", Integer.class, jsonData);
        this.progress = json.readValue("progress", Integer.class, jsonData);
        this.deckSize = json.readValue("deckSize", Integer.class, jsonData);
    }

    public void addProgress(int progress) {
        this.progress += progress;
        if (progress > 20) {
            progress = 20;
        }
    }

    public void drawCard() {
        progress = 0;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isProgressFull() {
        return progress >= 20;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
