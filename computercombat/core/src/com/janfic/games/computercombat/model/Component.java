package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Objects;

/**
 *
 * @author Jan Fic
 */
public abstract class Component implements Serializable {

    private String name;
    private String colorHex;
    private String textureName;
    private int x, y;

    public Component() {
        this.name = null;
        this.colorHex = null;
        this.textureName = null;
    }

    public Component(String name, String hex, int x, int y, String textureName) {
        this.name = name;
        this.colorHex = hex;
        this.x = x;
        this.y = y;
        this.textureName = textureName;
    }

    public Component(Component component) {
        this.name = component.name;
        this.colorHex = component.colorHex;
        this.textureName = component.textureName;
        this.x = component.x;
        this.y = component.y;
    }

    public String getName() {
        return this.name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getTextureName() {
        return textureName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "" + hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Component) {
            Component c = (Component) obj;
            return this.x == c.x && this.y == c.y && this.name.equals(c.name);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.name);
        hash = 13 * hash + this.x;
        hash = 13 * hash + this.y;
        return hash;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("name", name);
        json.writeValue("x", x);
        json.writeValue("y", y);
        json.writeValue("textureName", textureName);
        json.writeValue("colorHex", colorHex);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.name = json.readValue("name", String.class, jv);
        this.x = json.readValue("x", Integer.class, jv);
        this.y = json.readValue("y", Integer.class, jv);
        this.textureName = json.readValue("textureName", String.class, jv);
        this.colorHex = json.readValue("colorHex", String.class, jv);
    }
}
