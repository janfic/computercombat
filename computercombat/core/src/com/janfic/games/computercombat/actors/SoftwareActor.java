package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Card;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Software;
import com.janfic.games.computercombat.model.components.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class SoftwareActor extends Panel {

    ProgressBar progressBar, healthBar, defenseBar, attackBar;
    BorderedArea imageArea;
    boolean activatedAbility;
    VerticalGroup leds;

    public static final Map<Class<? extends Component>, String> components;

    static {
        components = new HashMap<>();
        components.put(CPUComponent.class, "CPU");
        components.put(BugComponent.class, "BUG");
        components.put(PowerComponent.class, "POWER");
        components.put(NetworkComponent.class, "NETWORK");
        components.put(RAMComponent.class, "RAM");
        components.put(StorageComponent.class, "STORAGE");
    }

    Software software;
    List<OverlayTextLabelArea<Software>> areas;

    public SoftwareActor(Skin skin, boolean flipped, Software software, ComputerCombatGame game) {
        super(skin);

        this.software = software;
        this.areas = new ArrayList<>();

        this.defaults().height(48).space(1);

        ProgressBarStyle red = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        red.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("df3e23"));
        ProgressBarStyle green = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        green.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("9cdb43"));
        ProgressBarStyle grey = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        grey.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("dae0ea"));
        ProgressBarStyle blue = new ProgressBarStyle(skin.get("default-vertical", ProgressBarStyle.class));
        blue.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("249fde"));

        progressBar = new ProgressBar(0, software.getRunRequirements(), 1, true, blue);
        healthBar = new ProgressBar(0, software.getMaxHealth(), 1, true, green);
        defenseBar = new ProgressBar(0, software.getMaxArmor(), 1, true, grey);
        attackBar = new ProgressBar(0, software.getMaxAttack(), 1, true, red);
        imageArea = new BorderedArea(skin);
        imageArea.add(new Image(game.getAssetManager().get("texture_packs/" + software.getPack() + ".atlas", TextureAtlas.class).findRegion(software.getTextureName())));

        this.setTouchable(Touchable.enabled);
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CardInfoWindow w = new CardInfoWindow(game, software, skin, true);
                w.setSize(2 * getStage().getWidth() / 3f, getStage().getHeight());
                w.setPosition(getStage().getWidth() / 6f, getStage().getHeight());
                SoftwareActor.this.getStage().addActor(w);
                w.getUseAbilityButton().addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        SoftwareActor.this.activatedAbility = true;
                    }
                });
            }
        });

        progressBar.setValue(software.getRunProgress());
        healthBar.setValue(software.getHealth());
        defenseBar.setValue(software.getArmor());
        attackBar.setValue(software.getAttack());

        Stack progressStack = new Stack();
        progressStack.add(progressBar);
        Table progressOverlay = new Table();
        OverlayTextLabelArea<Software> progressLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                progressBar.setValue(dataObject.getRunProgress());
                return "" + dataObject.getRunProgress();
            }
        };
        this.areas.add(progressLabelArea);
        progressOverlay.add(progressLabelArea).expand().fillX().height(9);
        progressStack.add(progressOverlay);

        Stack healthStack = new Stack();
        healthStack.add(healthBar);
        Table healthOverlay = new Table();
        OverlayTextLabelArea<Software> healthLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                healthBar.setValue(dataObject.getHealth());
                return "" + dataObject.getHealth();
            }
        };
        this.areas.add(healthLabelArea);
        healthOverlay.add(healthLabelArea).expand().fillX().height(9);
        healthStack.add(healthOverlay);

        Stack defenseStack = new Stack();
        defenseStack.add(defenseBar);
        Table defenseOverlay = new Table();
        OverlayTextLabelArea<Software> defenseLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                defenseBar.setValue(dataObject.getArmor());
                return "" + dataObject.getArmor();
            }
        };
        this.areas.add(defenseLabelArea);
        defenseOverlay.add(defenseLabelArea).expand().fillX().height(9);
        defenseStack.add(defenseOverlay);

        Stack attackStack = new Stack();
        attackStack.add(attackBar);
        Table attackOverlay = new Table();
        OverlayTextLabelArea<Software> attackLabelArea = new OverlayTextLabelArea<Software>(skin, software) {
            @Override
            public String updateLabel(Software dataObject) {
                attackBar.setValue(dataObject.getAttack());
                return "" + dataObject.getAttack();
            }
        };
        this.areas.add(attackLabelArea);
        attackOverlay.add(attackLabelArea).expand().fillX().height(9);
        attackStack.add(attackOverlay);

        leds = new VerticalGroup();
        for (Class<? extends Component> runComponent : software.getRunComponents()) {
            leds.addActor(new LEDActor(skin, components.get(runComponent)));
        }

        leds.space(3).center();

        if (flipped) {
            this.add(leds).padRight(2);
            this.add(progressStack).width(9).prefSize(8, 8);
            this.add(attackStack).width(9);
            this.add(imageArea).width(48);
            this.add(defenseStack).width(9);
            this.add(healthStack).width(9);
        } else {
            this.add(healthStack).width(9);
            this.add(defenseStack).width(9);
            this.add(imageArea).width(48);
            this.add(attackStack).width(9);
            this.add(progressStack).width(9).prefSize(8, 8);
            this.add(leds).padLeft(2);
        }
    }

    public List<OverlayTextLabelArea<Software>> getOverlayTextLabelAreas() {
        return areas;
    }

    public VerticalGroup getLEDs() {
        return leds;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public boolean activatedAbility() {
        return activatedAbility;
    }

    public void setActivatedAbility(boolean activatedAbility) {
        this.activatedAbility = activatedAbility;
    }

    public List<Component> getSelectedComponents() {
        return new ArrayList<>();
    }

    public List<Card> getSelectedSoftwares() {
        return new ArrayList<>();
    }

    public void addProgress(float amount) {
        this.progressBar.setValue(this.progressBar.getValue() + amount);
    }

    public void setProgress(float progress) {
        this.progressBar.setValue(progress);
    }

    public void setHealth(float health) {
        this.healthBar.setValue(health);
    }

    public void setArmor(float armor) {
        this.defenseBar.setValue(armor);
    }

    public void setAttack(float attack) {
        this.attackBar.setValue(attack);
    }
}
