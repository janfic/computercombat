package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Ability;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Collection;
import com.janfic.games.computercombat.model.abilities.DrawAbility;

/**
 *
 * @author Jan Fic
 */
public class ComputerActor extends Panel {

    Card computer;
    ProgressBar healthBar, progressBar;
    boolean activatedAbility;
    Label cardsLeft;

    public ComputerActor(Skin skin, ComputerCombatGame game, Card computer) {
        super(skin);

        this.defaults().space(1);

        ProgressBar.ProgressBarStyle green = new ProgressBar.ProgressBarStyle(skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class));
        green.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("9cdb43"));

        ProgressBar.ProgressBarStyle blue = new ProgressBar.ProgressBarStyle(skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class));
        blue.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("249fde"));

        Table table = new Table();

        healthBar = new ProgressBar(0, computer.getMaxHealth(), 1, false, green);
        progressBar = new ProgressBar(0, computer.getRunRequirements(), 1, false, blue);
        setComputer(computer);

        Table panel = new Table(skin);
        panel.setBackground("border_filled");
        cardsLeft = new Label("8", skin);
        panel.add(cardsLeft).height(15);

        table.add(healthBar).width(70).row();
        table.add(progressBar).width(70);

        this.add(panel).height(15).width(20);
        this.add(table).grow();

        this.setTouchable(Touchable.enabled);
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ability a = new DrawAbility();
                a.setInformation("Draw a card from your deck", "draw_card", "Draw", "new DrawAbility()", 0);
                CardInfoWindow w = new CardInfoWindow(
                        game, ComputerActor.this.computer, skin, true);
                ComputerActor.this.getStage().addActor(w);
                w.getUseAbilityButton().addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ComputerActor.this.activatedAbility = true;
                    }
                });
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (computer.getRunProgress() >= computer.getRunRequirements()) {
            this.panel = getSkin().getPatch("glow_panel");
        } else {
            this.panel = getSkin().getPatch("panel");
        }
    }

    public void setComputer(Card computer) {
        this.computer = computer;
        this.healthBar.setValue(computer.getHealth());
        this.progressBar.setValue(computer.getRunProgress());
    }

    public void setCardsLeft(int amount) {
        this.cardsLeft.setText("" + amount);
    }

    public boolean activatedAbility() {
        return activatedAbility;
    }

    public Card getComputer() {
        return computer;
    }

    public void setActivatedAbility(boolean activatedAbility) {
        this.activatedAbility = activatedAbility;
    }

    public void addProgress(int amount) {
        this.progressBar.setValue(this.progressBar.getValue() + amount);
    }

    public void setProgress(float progress) {
        this.progressBar.setValue(progress);
    }

    public void setHealth(float health) {
        this.healthBar.setValue(health);
    }
}
