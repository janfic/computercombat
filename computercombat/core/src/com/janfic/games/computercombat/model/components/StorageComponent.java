package com.janfic.games.computercombat.model.components;

import com.janfic.games.computercombat.model.Component;

/**
 *
 * @author Jan Fic
 */
public class StorageComponent extends Component {

    public StorageComponent(int x, int y) {
        super("Storage Component", "", x, y, "storage");
    }

    public StorageComponent() {
        super("Storage Component", null, 0, 0, "storage");
    }

}
