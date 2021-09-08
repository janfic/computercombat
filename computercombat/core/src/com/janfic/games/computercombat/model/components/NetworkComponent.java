package com.janfic.games.computercombat.model.components;

import com.janfic.games.computercombat.model.Component;

/**
 *
 * @author Jan Fic
 */
public class NetworkComponent extends Component {

    public NetworkComponent(int x, int y) {
        super("Network Component", "", x, y, "network");
    }

    public NetworkComponent() {
        super(null, null, 0, 0, "network");
    }

}
