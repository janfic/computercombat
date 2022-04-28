package com.janfic.games.computercombat.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Jan Fic
 */
public class Component implements Serializable {

    public static Map<Integer, String> colorToTextureName;

    static {
        colorToTextureName = new HashMap<>();
        colorToTextureName.put(0, "empty");
        colorToTextureName.put(1, "cpu");
        colorToTextureName.put(2, "ram");
        colorToTextureName.put(3, "storage");
        colorToTextureName.put(4, "network");
        colorToTextureName.put(5, "bug");
        colorToTextureName.put(6, "power");
    }

    /**
     * Index 0 - 3 represent nearest neighbors ( Cartesian distance 1 ). Index 4
     * - 7 represent second nearest neighbors ( up left, down left, up right,
     * down right ) ( diagonals ). Index 8 - 11 represent third nearest
     * neighbors ( upup, leftleft, rightright, downdown )
     */
    public Component[] neighbors;

    public final static int[] coordsToNeighbors = new int[]{
        0, 1, 0, 1, 0, 1, 0, -1, 2, -1, 0, 3,
        1, 1, 4, 1, -1, 5, -1, -1, 6, -1, 1, 7,
        0, 2, 8, 2, 0, 9, 0, -2, 10, -2, 0, 11
    };

    private String textureName;
    private int color;
    private int x, y;
    private boolean isInvalid; // should this component be updated ( recalculate )

    // A List of indicies in which neighbors at these index form a match with this component;
    private Set<Integer> matchNeighbors;

    public Component() {
        this.textureName = null;
        this.neighbors = new Component[12];
        this.matchNeighbors = new HashSet<>();
        isInvalid = true;
    }

    public Component(int color, int x, int y) {
        this();
        this.color = color;
        this.x = x;
        this.y = y;
        this.textureName = colorToTextureName.get(color);
    }

    public Component(Component component) {
        this(component.color, component.x, component.y);
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

    public int getColor() {
        return color;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setNeighbor(int x, int y, Component c) {
        for (int i = 0; i < coordsToNeighbors.length; i += 3) {
            if (x - this.getX() == coordsToNeighbors[i] && y - this.getY() == coordsToNeighbors[i + 1]) {
                neighbors[coordsToNeighbors[i + 2]] = c;
            }
        }
    }

    public void setNeighbor(int index, Component c) {
        neighbors[index] = c;
    }

    public Component getNeighbor(int x, int y) {
        for (int i = 0; i < coordsToNeighbors.length; i += 3) {
            if (x - this.getX() == coordsToNeighbors[i] && y - this.getY() == coordsToNeighbors[i + 1]) {
                return neighbors[coordsToNeighbors[i + 2]];
            }
        }
        return null;
    }

    public int getNeighborIndex(int x, int y) {
        for (int i = 0; i < coordsToNeighbors.length; i += 3) {
            if (x - this.getX() == coordsToNeighbors[i] && y - this.getY() == coordsToNeighbors[i + 1]) {
                return coordsToNeighbors[i + 2];
            }
        }
        return -1;
    }

    public void changeColor(int newColor) {
        this.color = newColor;
        this.textureName = colorToTextureName.get(this.color);
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public void invalidate() {
        isInvalid = true;
    }

    public void invalidateNeighbors() {
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != null) {
                neighbors[i].invalidate();
            }
        }
    }

    public void update() {
        // Reset known information
        matchNeighbors.clear();
        // Check for cascade

        // Check for possible moves
        findMove();
        // Check if part of match
        findMatch();
        // Validate this component
        this.isInvalid = false;
        if (color == 0) {
            this.isInvalid = true;
        }
    }

    // Looks at up/down, left/right neighbors to see if they have same color.
    // This does does not see matches greater than 3 as the same match.
    private void findMatch() {
        // Check if component is in the middle of two same colored components.
        if (neighbors[0] != null && neighbors[2] != null
                && getColor() == neighbors[0].getColor() && getColor() == neighbors[2].getColor()) {
            matchNeighbors.add(0);
            matchNeighbors.add(2);
        }
        if (neighbors[1] != null && neighbors[3] != null
                && getColor() == neighbors[1].getColor() && getColor() == neighbors[3].getColor()) {
            matchNeighbors.add(1);
            matchNeighbors.add(3);
        }
    }

    public void updateMatchedNeighbors() {
        // Tell neighbors that formed a match with me that they are in a match with this component.
        // PROBLEM: Must be done after the neighbor has been validated, or this information will be erased. 
        for (Integer matchNeighbor : matchNeighbors) {
            int thisComponentAsNeighborIndex = neighbors[matchNeighbor].getNeighborIndex(getX(), getY());
            if (thisComponentAsNeighborIndex != -1) {
                neighbors[matchNeighbor].getMatchNeighbors().add(thisComponentAsNeighborIndex);
            }
        }
    }

    private void findMove() {

    }

    public boolean isMatched() {
        return !matchNeighbors.isEmpty();
    }

    public Set<Integer> getMatchNeighbors() {
        return matchNeighbors;
    }

    public List<Component> getNeighbors(Set<Integer> neighborList) {
        List<Component> components = new ArrayList<>();
        for (Integer neighborIndex : neighborList) {
            if (neighborIndex >= 0) {
                components.add(neighbors[neighborIndex]);
            }
        }
        return components;
    }

    @Override
    public String toString() {
        return "" + hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Component) {
            Component c = (Component) obj;
            return this.x == c.x && this.y == c.y && this.color == c.color;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + this.x;
        hash = 13 * hash + this.y;
        hash = 13 * hash + this.color;
        return hash;
    }

    @Override
    public void write(Json json) {
        json.writeValue("color", color);
        json.writeValue("x", x);
        json.writeValue("y", y);
    }

    @Override
    public void read(Json json, JsonValue jv) {
        this.color = json.readValue("color", Integer.class, jv);
        this.x = json.readValue("x", Integer.class, jv);
        this.y = json.readValue("y", Integer.class, jv);
        this.textureName = colorToTextureName.get(this.color);
    }

}
