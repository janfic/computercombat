package com.janfic.games.computercombat.model.components;

import com.janfic.games.computercombat.model.Component;

/**
 *
 * @author Jan Fic
 */
public class BugComponent extends Component {

    public BugComponent(int x, int y) {
        super("Bug Component", "", x, y, "bug");
    }

    public BugComponent() {
        super(null, null, 0, 0, "bug");
    }
}
