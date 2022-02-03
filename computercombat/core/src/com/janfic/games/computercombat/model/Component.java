package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.computercombat.model.components.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Jan Fic
 */
public abstract class Component implements Serializable {

    public static final Map<Integer, Class<? extends Component>> numberToComponent;
    public static final Map<Class<? extends Component>, Integer> componentToNumber;

    static {
        numberToComponent = new HashMap<>();
        numberToComponent.put(1, CPUComponent.class);
        numberToComponent.put(2, RAMComponent.class);
        numberToComponent.put(3, StorageComponent.class);
        numberToComponent.put(4, NetworkComponent.class);
        numberToComponent.put(5, BugComponent.class);
        numberToComponent.put(6, PowerComponent.class);
        componentToNumber = new HashMap<>();
        componentToNumber.put(CPUComponent.class, 1);
        componentToNumber.put(RAMComponent.class, 2);
        componentToNumber.put(StorageComponent.class, 3);
        componentToNumber.put(NetworkComponent.class, 4);
        componentToNumber.put(BugComponent.class, 5);
        componentToNumber.put(PowerComponent.class, 6);
    }

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
