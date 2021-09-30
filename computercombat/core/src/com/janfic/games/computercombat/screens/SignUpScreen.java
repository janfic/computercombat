package com.janfic.games.computercombat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.computercombat.Assets;
import com.janfic.games.computercombat.ComputerCombatGame;
import com.janfic.games.computercombat.actors.BorderedGrid;
import com.janfic.games.computercombat.actors.Panel;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jan Fic
 */
public class SignUpScreen implements Screen {

    ComputerCombatGame game;

    Stage stage;
    OrthographicCamera camera;

    //LibGDX
    Skin skin;

    TooltipManager toolTipManager;
    TextField emailField, passwordField, confirmField, userNameField;
    TextTooltip toolTip;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public SignUpScreen(ComputerCombatGame game) {
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
        table.defaults().pad(5);

        Label title = new Label("New Account", skin, "title");
        title.setAlignment(Align.center);

        Table leftGroup = new Table();
        Table rightGroup = new Table();

        BorderedGrid grid = new BorderedGrid(skin);
        Table usernameTable = new Table(skin);
        usernameTable.background("border_filled");
        Label userNameFieldLabel = new Label(" Username: ", skin);
        Image usernameInfo = new Image(skin, "info_icon");
        usernameTable.add(usernameInfo).left();
        usernameTable.add(userNameFieldLabel).expand().right();
        Label emailFieldLabel = new Label("Email: ", skin, "filled");
        Table passwordTable = new Table(skin);
        passwordTable.background("border_filled");
        Label passwordFieldLabel = new Label(" Create Password: ", skin);
        Image passwordInfo = new Image(skin, "info_icon");
        passwordTable.add(passwordInfo).left();
        passwordTable.add(passwordFieldLabel).expand().right();
        Label confirmFieldLabel = new Label(" Confirm Password: ", skin, "filled");
        userNameField = new TextField("", skin);
        emailField = new TextField("", skin);
        passwordField = new TextField("", skin);
        confirmField = new TextField("", skin);
        TextButton createAccountButton = new TextButton("Create Account", skin);

        TextTooltip usernameToolTip = new TextTooltip("Only alphanumeric characters (A-Z,0-9)", toolTipManager, skin);
        usernameInfo.addListener(usernameToolTip);
        TextTooltip passwordToolTip = new TextTooltip("Minimum 8 alphanumeric characters (A-Z,0-9)", toolTipManager, skin);
        passwordInfo.addListener(passwordToolTip);

        TextFieldFilter alphaNumeric = new TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField tf, char c) {
                return tf.getText().length() < 12 && (Character.isAlphabetic(c) || Character.isDigit(c));
            }
        };
        userNameField.setTextFieldFilter(alphaNumeric);
        passwordField.setTextFieldFilter(alphaNumeric);

        userNameFieldLabel.setAlignment(Align.right);
        emailFieldLabel.setAlignment(Align.right);
        passwordFieldLabel.setAlignment(Align.right);
        confirmFieldLabel.setAlignment(Align.right);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        confirmField.setPasswordMode(true);
        confirmField.setPasswordCharacter('*');

        grid.pad(10);
        grid.defaults().space(6);
        leftGroup.defaults().space(5);
//        leftGroup.debugAll();

        leftGroup.add(usernameTable).growX().minHeight(25);
        leftGroup.add(userNameField).minHeight(25).row();
        leftGroup.add(emailFieldLabel).growX().minHeight(25);
        leftGroup.add(emailField).minHeight(25).row();
        leftGroup.add(passwordTable).growX().minHeight(25);
        leftGroup.add(passwordField).minHeight(25).row();
        leftGroup.add(confirmFieldLabel).growX().minHeight(25);
        leftGroup.add(confirmField).minHeight(25).row();
        leftGroup.add(createAccountButton).colspan(2).row();

        createAccountButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailField.getText().trim());

                boolean validSignUp = true;

                if (!matcher.find()) {
                    validSignUp = false;
                    emailField.setColor(Color.RED);
                } else {
                    emailField.setColor(Color.WHITE);
                }

                if (userNameField.getText().trim().length() <= 0 || userNameField.getText().trim().length() > 12) {
                    validSignUp = false;
                    userNameField.setColor(Color.RED);
                    //stage.addActor(toolTip.getContainer());
                    //stage.addActor(toolTip.getContainer().getActor());
                    // toolTip.getContainer().setPosition(0, 0);
                    //toolTip.getContainer().setSize(toolTip.getActor().getWidth(), toolTip.getActor().getHeight());
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

                if (!passwordField.getText().trim().equals(confirmField.getText().trim())) {
                    validSignUp = false;
                    confirmField.setColor(Color.RED);
                } else {
                    confirmField.setColor(Color.WHITE);
                }

                if (validSignUp) {
                    game.getServerAPI().sendMessage(new Message(Type.NEW_PROFILE_REQUEST,
                            userNameField.getText().trim() + ","
                            + emailField.getText().trim() + ","
                            + passwordField.getText().trim()));

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (game.getServerAPI().hasMessage() == false) {
                            }

                            Message message = game.getServerAPI().readMessage();

                            Window window = new Window("", skin);
                            Label messageLabel = new Label("", skin);
                            messageLabel.setWrap(true);
                            TextButton okayButton = new TextButton("Okay", skin);
                            TextButton xButton = new TextButton("X", skin);
                            xButton.setColor(Color.RED);
                            xButton.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    window.remove();
                                }
                            });
                            window.getTitleTable().add(xButton);

                            if (message.type == Type.ERROR) {
                                window.getTitleLabel().setText("ERROR");
                                messageLabel.setText(message.getMessage());
                                okayButton.addListener(new ClickListener() {
                                    @Override
                                    public void clicked(InputEvent event, float x, float y) {
                                        window.remove();
                                    }
                                });
                            } else if (message.type == Type.PROFILE_INFO) {
                                window.getTitleLabel().setText("Success!");
                                messageLabel.setText("Welcome " + userNameField.getText().trim() + "! Your profile has been created! Login to start playing!");
                                messageLabel.setWrap(true);
                                okayButton.addListener(new ClickListener() {
                                    @Override
                                    public void clicked(InputEvent event, float x, float y) {
                                        game.popScreen();
                                    }
                                });
                            }
                            window.add(messageLabel).expand().grow().row();
                            window.add(okayButton).row();

                            window.setSize(stage.getWidth() / 2, stage.getHeight() / 2);
                            window.setPosition(stage.getWidth() / 4, stage.getHeight() / 4);
                            stage.addActor(window);
                        }
                    });

                    thread.start();
                }
            }
        });

        Panel divider = new Panel(skin);

        rightGroup.defaults().space(6);

        TextButton signUpWithGoogle = new TextButton("Sign Up With Google", skin);
        signUpWithGoogle.getLabel().setWrap(true);
        TextButton signUpWithFacebook = new TextButton("Sign Up With Facebook", skin);
        signUpWithFacebook.getLabel().setWrap(true);
        TextButton signUpWithApple = new TextButton("Sign Up With Apple", skin);
        signUpWithApple.getLabel().setWrap(true);

        rightGroup.add(signUpWithGoogle).width(100).row();
        rightGroup.add(signUpWithFacebook).width(100).row();
        rightGroup.add(signUpWithApple).width(100);

        grid.add(leftGroup).grow();
        grid.add(divider).growY();
        grid.add(rightGroup).grow();

        Table navGrid = new Table();
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.popScreen();
            }
        });

        navGrid.add(backButton).expand().width(150).left();

        table.add(title).grow().row();
        table.add(grid).expand().grow().row();
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

}
