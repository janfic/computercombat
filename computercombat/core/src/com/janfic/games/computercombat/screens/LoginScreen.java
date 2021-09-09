package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.model.players.HumanPlayer;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import java.util.regex.Pattern;

/**
 *
 * @author Jan Fic
 */
public class LoginScreen implements Screen {
    
    ComputerCombatGame game;
    
    Stage stage;
    OrthographicCamera camera;

    //LibGDX
    Skin skin;
    
    TooltipManager toolTipManager;
    TextField passwordField, userNameField;
    TextTooltip toolTip;
    
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    public LoginScreen(ComputerCombatGame game) {
        this.game = game;
        this.skin = game.getAssetManager().get(Assets.SKIN);
    }
    
    @Override
    public void show() {
        this.camera = new OrthographicCamera(1920 / 4, 1080 / 4);
        this.stage = ComputerCombatGame.makeNewStage(camera);
        this.toolTipManager = TooltipManager.getInstance();
        this.toolTipManager.instant();
        Gdx.input.setInputProcessor(stage);
        
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(10);
        
        Label title = new Label("Login", skin, "title");
        title.setAlignment(Align.center);
        
        Table leftGroup = new Table();
        
        BorderedGrid grid = new BorderedGrid(skin);
        Label userNameFieldLabel = new Label(" Username: ", skin, "paneled");
        Label passwordFieldLabel = new Label("Password: ", skin, "paneled");
        userNameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        TextButton loginButton = new TextButton("Login", skin);
        
        toolTip = new TextTooltip("Only alphanumeric characters (A-Z,0-9)", toolTipManager, skin);
        userNameFieldLabel.addListener(toolTip);
        
        TextField.TextFieldFilter alphaNumeric = new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField tf, char c) {
                return tf.getText().length() < 12 && (Character.isAlphabetic(c) || Character.isDigit(c));
            }
        };
        userNameField.setTextFieldFilter(alphaNumeric);
        passwordField.setTextFieldFilter(alphaNumeric);
        
        userNameFieldLabel.setAlignment(Align.right);
        passwordFieldLabel.setAlignment(Align.right);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        
        grid.pad(10);
        grid.defaults().space(6);
        leftGroup.defaults().space(10);
//        leftGroup.debugAll();

        leftGroup.add(userNameFieldLabel).growX().minHeight(25);
        leftGroup.add(userNameField).minHeight(25).row();
        leftGroup.add(passwordFieldLabel).growX().minHeight(25);
        leftGroup.add(passwordField).minHeight(25).row();
        leftGroup.add(loginButton).colspan(2).row();
        
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean validSignUp = true;
                
                if (userNameField.getText().trim().length() <= 0 || userNameField.getText().trim().length() > 12) {
                    validSignUp = false;
                    userNameField.setColor(Color.RED);
                } else {
                    userNameField.setColor(Color.WHITE);
                }
                
                String password = passwordField.getText().trim();
                
                if (password.length() < 7) {
                    validSignUp = false;
                    passwordField.setColor(Color.RED);
                } else {
                    passwordField.setColor(Color.WHITE);
                }
                
                if (validSignUp) {
                    
                    game.getServerAPI().sendMessage(new Message(Type.LOGIN_REQUEST, userNameField.getText().trim() + ","
                            + passwordField.getText().trim()));
                    Gdx.app.postRunnable(loginRunnable);
                }
            }
        });
        
        grid.add(leftGroup).grow();
        
        Table navGrid = new Table();
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });
        
        navGrid.add(backButton).expand().width(150).left();
        
        table.add(title).growX().row();
        table.add(grid).growY().row();
        table.add(navGrid).growX();
        
        stage.addActor(table);
    }
    
    @Override
    public void render(float f) {
        stage.act(f);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        camera.update();
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void dispose() {
    }
    
    Window popup;
    
    Runnable loginRunnable = new Runnable() {
        @Override
        public void run() {
            while (game.getServerAPI().hasMessage() == false) {
            }
            
            Message message = game.getServerAPI().readMessage();
            
            popup = new Window("", skin);
            popup.defaults().space(5);
            Label messageLabel = new Label("", skin);
            messageLabel.setWrap(true);
            messageLabel.setAlignment(Align.center);
            TextButton okayButton = new TextButton("Okay", skin);
            
            if (null != message.type) {
                switch (message.type) {
                    case ERROR:
                        popup.getTitleLabel().setText("ERROR");
                        messageLabel.setText("Something went wrong. Try again.");
                        popup.add(messageLabel).expand().grow().row();
                        System.out.println(message.getMessage());
                        okayButton.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                popup.remove();
                            }
                        });
                        break;
                    case PROFILE_INFO:
                        Json json = new Json();
                        Profile profile = json.fromJson(Profile.class, message.getMessage());
                        game.setCurrentProfile(profile);
                        game.pushScreen(new PlayScreen(game));
                        break;
                    case VERIFY_WITH_CODE:
                        popup.getTitleLabel().setText("Verification");
                        popup.add(messageLabel).growX().row();
                        messageLabel.setText("Enter Verification Code:");
                        TextField codeField = new TextField("", skin);
                        codeField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
                        popup.add(codeField).row();
                        okayButton.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                if (codeField.getText().trim().length() > 0) {
                                    game.getServerAPI().sendMessage(new Message(Type.VERIFICATION_CODE, userNameField.getText().trim() + "," + codeField.getText().trim()));
                                    Gdx.app.postRunnable(verifyRunnable);
                                }
                            }
                        });
                        break;
                    case NO_AUTH:
                        popup.getTitleLabel().setText("Incorrect Login");
                        messageLabel.setText("Username or Password incorrect. Try Again");
                        popup.add(messageLabel).expand().grow().row();
                        okayButton.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                popup.remove();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
            
            popup.add(okayButton).row();
            
            popup.setSize(stage.getWidth() / 2, stage.getHeight() / 2);
            popup.setPosition(stage.getWidth() / 4, stage.getHeight() / 4);
            stage.addActor(popup);
        }
    };
    
    Runnable verifyRunnable = new Runnable() {
        @Override
        public void run() {
            while (game.getServerAPI().hasMessage() == false) {
            }
            
            Message response = game.getServerAPI().readMessage();
            
            Window window = new Window("", skin);
            window.defaults().space(5);
            Label messageLabel = new Label("", skin);
            messageLabel.setWrap(true);
            messageLabel.setAlignment(Align.center);
            TextButton okayButton = new TextButton("Okay", skin);
            
            if (response.type == Type.SUCCESS) {
                window.getTitleLabel().setText("Success!");
                messageLabel.setText("Email Successfully Verified!");
                window.add(messageLabel).expand().grow().row();
                okayButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        popup.remove();
                        window.remove();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                window.remove();
                            }
                        });
                    }
                });
            } else if (response.type == Type.VERIFY_WITH_CODE) {
                window.getTitleLabel().setText("Incorrect Verification Code");
                messageLabel.setText("Incorrect Code. Try again.");
                window.add(messageLabel).expand().grow().row();
                okayButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        window.remove();
                    }
                });
            }
            
            window.add(okayButton).row();
            
            window.setSize(stage.getWidth() / 2, stage.getHeight() / 2);
            window.setPosition(stage.getWidth() / 4, stage.getHeight() / 4);
            
            stage.addActor(window);
        }
    };
}
