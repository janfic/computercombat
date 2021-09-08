package com.janfic.games.computercombat.model;

import java.util.Objects;

/**
 *
 * @author Jan Fic
 */
public abstract class Component {

    private final String name;
    private final String colorHex;
    private final String textureName;
    private int x, y;

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
}
