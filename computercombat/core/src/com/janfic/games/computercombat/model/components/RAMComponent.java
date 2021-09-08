package com.janfic.games.computercombat.model.components;

import com.janfic.games.computercombat.model.Component;

/**
 *
 * @author Jan Fic
 */
public class RAMComponent extends Component {

    public RAMComponent(int x, int y) {
        super("RAM Component", "", x, y, "ram");
    }

    public RAMComponent() {
        super(null, null, 0, 0, "ram");
    }
}
