package com.janfic.games.computercombat.model.components;

import com.janfic.games.computercombat.model.Component;

/**
 *
 * @author Jan Fic
 */
public class GPUComponent extends Component {

    public GPUComponent(int x, int y) {
        super("GPU Component", "", x, y, "gpu");
    }

    public GPUComponent() {
        super(null, null, 0, 0, "gpu");
    }

}
