package com.janfic.games.computercombat.network;

/**
 *
 * @author Jan Fic
 */
public class Message {

    public Type type;
    String message;

    public Message() {
    }

    public Message(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MESSAGE: {TYPE: " + type + ", CONTENT: " + message + "}";
    }

}
