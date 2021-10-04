package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.janfic.games.computercombat.model.Computer;

/**
 *
 * @author Jan Fic
 */
public class ComputerActor extends Panel {

    Computer computer;
    ProgressBar healthBar, progressBar;

    public ComputerActor(Skin skin) {
        super(skin);

        this.defaults().space(1);

        ProgressBar.ProgressBarStyle green = new ProgressBar.ProgressBarStyle(skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class));
        green.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("9cdb43"));

        ProgressBar.ProgressBarStyle blue = new ProgressBar.ProgressBarStyle(skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class));
        blue.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("249fde"));

        Table table = new Table();

        healthBar = new ProgressBar(0, 20, 1, false, green);
        progressBar = new ProgressBar(0, 20, 1, false, blue);

        Panel panel = new Panel(skin);
        panel.add(new Label("7", skin));

        table.add(healthBar).width(40).row();
        table.add(progressBar).width(40);

        this.add(panel).pad(2).width(20);
        this.add(table);
        setComputer(new Computer());
    }

    public void setComputer(Computer computer) {
        this.computer = computer;
        this.healthBar.setValue(computer.getHealth());
        this.progressBar.setValue(computer.getProgress());
    }

}
