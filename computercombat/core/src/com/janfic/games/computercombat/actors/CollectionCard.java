package com.janfic.games.computercombat.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.model.Component;
import com.janfic.games.computercombat.model.Card;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jan Fic
 */
public class CollectionCard extends BorderedGrid {
    
    public static final Map<Integer, String> components;
    
    static {
        components = new HashMap<>();
        components.put(1, "CPU");
        components.put(5, "BUG");
        components.put(6, "POWER");
        components.put(4, "NETWORK");
        components.put(2, "RAM");
        components.put(3, "STORAGE");
    }
    
    public static final String[] rarityColors = new String[]{
        "BLACK",
        "BUG",
        "CPU",
        "POWER",
        "NETWORK",
        "STORAGE"
    };
    
    Card software;
    int amount;
    EventListener newWindowOnClick;
    
    public CollectionCard(ComputerCombatGame game, Skin skin, Card software, int amount) {
        super(skin);
        this.software = software;
        this.amount = amount;
        this.defaults().space(3);
        this.pad(5);
        this.top();
        this.align(Align.top);
        int rarity = software.getRarity();
        Table rarityBorder = new Table(skin);
        rarityBorder.setBackground("rarity_border");
        rarityBorder.setColor(skin.getColor(rarityColors[rarity]));
        rarityBorder.defaults().space(3);
        rarityBorder.pad(5);
        rarityBorder.top();
        rarityBorder.align(Align.top);
        
        this.add(rarityBorder).grow();
        
        Table rarityTable = new Table(skin);
        rarityTable.setBackground("panel");
        rarityTable.defaults().expandX();
        
        for (int i = 5; i >= 1; i--) {
            Image r = new Image(skin, rarity <= i ? "rarity_filled" : "rarity_empty");
            r.setColor(skin.getColor(rarityColors[rarity]));
            rarityTable.add(r);
        }
        
        rarityBorder.add(rarityTable).growX().space(0).row();
        
        TextureAtlas texturePack = game.getAssetManager().get("texture_packs/" + software.getCollection().getTextureName() + ".atlas", TextureAtlas.class);
        BorderedArea area = new BorderedArea(skin);
        area.add(new Image(texturePack.findRegion(software.getTextureName())));
        Label l = new Label(software.getName(), skin, "paneled");
        l.setWrap(true);
        l.setAlignment(Align.center);
        rarityBorder.add(l).width(90).space(0).row();
        
        rarityBorder.add(area).width(48).height(48).row();
        
        ProgressBar.ProgressBarStyle red = new ProgressBar.ProgressBarStyle(skin.get("default-vertical", ProgressBar.ProgressBarStyle.class));
        red.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("df3e23"));
        ProgressBar.ProgressBarStyle green = new ProgressBar.ProgressBarStyle(skin.get("default-vertical", ProgressBar.ProgressBarStyle.class));
        green.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("9cdb43"));
        ProgressBar.ProgressBarStyle grey = new ProgressBar.ProgressBarStyle(skin.get("default-vertical", ProgressBar.ProgressBarStyle.class));
        grey.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("dae0ea"));
        ProgressBar.ProgressBarStyle blue = new ProgressBar.ProgressBarStyle(skin.get("default-vertical", ProgressBar.ProgressBarStyle.class));
        blue.knobBefore = skin.newDrawable("progress_bar_before_vertical", Color.valueOf("249fde"));
        
        ProgressBar runBar = new ProgressBar(0, 10, 1, false, blue);
        ProgressBar healthBar = new ProgressBar(0, 10, 1, false, green);
        ProgressBar defenseBar = new ProgressBar(0, 10, 1, false, grey);
        ProgressBar attackBar = new ProgressBar(0, 10, 1, false, red);
        runBar.setValue(10);
        healthBar.setValue(10);
        defenseBar.setValue(10);
        attackBar.setValue(10);
        
        Stack defenseStack = new Stack();
        defenseStack.add(defenseBar);
        Table defenseOverlay = new Table();
        OverlayTextLabelArea<Card> defenseOverlayTextLabelArea = new OverlayTextLabelArea<Card>(skin, software) {
            @Override
            public String updateLabel(Card dataObject) {
                return "" + dataObject.getMaxArmor();
            }
        };
        defenseOverlay.add(defenseOverlayTextLabelArea).width(12).height(9);
        defenseStack.add(defenseOverlay);
        Stack runRequirementsStack = new Stack();
        runRequirementsStack.add(runBar);
        Table runRequirementsOverlay = new Table();
        OverlayTextLabelArea<Card> runOverlayTextLabelArea = new OverlayTextLabelArea<Card>(skin, software) {
            @Override
            public String updateLabel(Card dataObject) {
                return "" + dataObject.getRunRequirements();
            }
        };
        runRequirementsOverlay.add(runOverlayTextLabelArea).width(12).height(9);
        runRequirementsStack.add(runRequirementsOverlay);
        Stack healthStack = new Stack();
        healthStack.add(healthBar);
        Table healthOverlay = new Table();
        OverlayTextLabelArea<Card> healthOverlayTextLabelArea = new OverlayTextLabelArea<Card>(skin, software) {
            @Override
            public String updateLabel(Card dataObject) {
                return "" + dataObject.getMaxHealth();
            }
        };
        healthOverlay.add(healthOverlayTextLabelArea).width(12).height(9);
        healthStack.add(healthOverlay);
        Stack attackStack = new Stack();
        attackStack.add(attackBar);
        Table attackOverlay = new Table();
        OverlayTextLabelArea<Card> attackOverlayTextLabelArea = new OverlayTextLabelArea<Card>(skin, software) {
            @Override
            public String updateLabel(Card dataObject) {
                return "" + dataObject.getMaxAttack();
            }
        };
        attackOverlay.add(attackOverlayTextLabelArea).width(12).height(9);
        attackStack.add(attackOverlay);
        
        rarityBorder.add(attackStack).width(80).height(9).row();
        rarityBorder.add(defenseStack).width(80).height(9).row();
        rarityBorder.add(healthStack).width(80).height(9).row();
        rarityBorder.add(runRequirementsStack).width(80).height(9).row();
        
        Table footer = new Table(skin);
        footer.defaults().space(0);
        Panel leds = new Panel(skin);
        leds.defaults().space(9);
        for (Integer runComponent : software.getRunComponents()) {
            LEDActor led = new LEDActor(skin, components.get(runComponent));
            led.setLightOn(true);
            leds.add(led).pad(1);
        }
        
        Label amountLabel = new Label("" + amount, skin, "paneled");
        amountLabel.setAlignment(Align.center);
        amountLabel.setFontScale(0.5f);
        
        footer.add(amountLabel).growY().width(16);
        footer.add(leds).height(16).growX().bottom().expand();
        TextureRegion icon = texturePack.findRegion(software.getCollection().getPath() + "_icon");
        if (icon != null) {
            Panel iconPanel = new Panel(skin);
            Image collectionIcon = new Image(icon);
            iconPanel.add(collectionIcon).size(8, 8).row();
            footer.add(iconPanel).height(16);
        }
        rarityBorder.add(footer).expand().growX().bottom();
        newWindowOnClick = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Window w = new CardInfoWindow(game, software, skin, false);
                
                CollectionCard.this.getStage().addActor(w);
            }
        };
        this.addListener(newWindowOnClick);
    }
    
    @Override
    
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
    
    public EventListener getNewWindowOnClick() {
        return newWindowOnClick;
    }
}
