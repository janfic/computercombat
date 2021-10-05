package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

/**
 *
 * @author Jan Fic
 */
public class Computer implements Serializable {

    int health, progress;

    public Computer() {
        this.health = 20;
        this.progress = 0;
    }

    @Override
    public void write(Json json) {
        json.writeValue("health", health);
        json.writeValue("progress", progress);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.health = json.readValue("health", Integer.class, jsonData);
        this.progress = json.readValue("progress", Integer.class, jsonData);
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

    public int getHealth() {
        return health;
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
}
