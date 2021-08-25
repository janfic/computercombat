package com.janfic.games.computercombat.model;

/**
 *
 * @author Jan Fic
 */
public abstract class Component {
    private final String name;
    private final String colorHex;
    private int x, y;

    public Component(String name, String hex, int x, int y) {
        this.name = name;
        this.colorHex = hex;
        this.x = x;
        this.y = y;
    }
    
    public String getName() {
        return this.name;
    }

    public String getColorHex() {
        return colorHex;
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
   
}