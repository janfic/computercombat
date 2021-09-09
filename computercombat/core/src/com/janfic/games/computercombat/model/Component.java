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
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Component) {
            return this.hashCode() == obj.hashCode();
        }
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + this.x;
        hash = 43 * hash + this.y;
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
