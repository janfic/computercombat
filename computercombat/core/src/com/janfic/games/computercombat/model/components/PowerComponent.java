package com.janfic.games.computercombat.model.components;

import com.janfic.games.computercombat.model.Component;

/**
 *
 * @author Jan Fic
 */
public class PowerComponent extends Component {

    public PowerComponent(int x, int y) {
        super("Power Component", "", x, y, "power");
    }

    public PowerComponent() {
        super(null, null, 0, 0, "power");
    }

}
