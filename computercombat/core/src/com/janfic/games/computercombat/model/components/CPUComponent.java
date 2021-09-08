package com.janfic.games.computercombat.model.components;

import com.janfic.games.computercombat.model.Component;

/**
 *
 * @author Jan Fic
 */
public class CPUComponent extends Component {

    public CPUComponent(int x, int y) {
        super("CPU Component", "", x, y, "cpu");
    }

    public CPUComponent() {
        super(null, null, 0, 0, "cpu");
    }

}
