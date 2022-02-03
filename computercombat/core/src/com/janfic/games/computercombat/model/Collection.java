package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

/**
 *
 * @author Jan Fic
 */
public class Collection implements Serializable {

    private int id;
    private String name, description;
    private String textureName;
    private String path;

    public Collection() {
        this(1, "Computer", "computer", "computer_pack", "computer_pack");
    }

    public Collection(int id, String name, String description, String textureName, String path) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.textureName = textureName;
        this.path = path;
    }

    public int getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getTextureName() {
        return textureName;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("id", id);
        json.writeValue("description", description);
        json.writeValue("path", path);
        json.writeValue("textureName", textureName);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.name = json.readValue("name", String.class, jsonData);
        this.id = json.readValue("id", Integer.class, jsonData);
        this.description = json.readValue("description", String.class, jsonData);
        this.path = json.readValue("path", String.class, jsonData);
        this.textureName = json.readValue("textureName", String.class, jsonData);
    }
}
