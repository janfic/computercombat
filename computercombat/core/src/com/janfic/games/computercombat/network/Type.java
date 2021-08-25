/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.janfic.games.computercombat.network;

/**
 *
 * @author Jan Fic
 */
public enum Type {
    CONNECTION_REQUEST,
    CONNECTION_ACCEPT,
    PING,
    NEW_PROFILE_REQUEST,
    LOGIN_REQUEST,
    LOGIN_FAILED,
    VERIFY_WITH_CODE,
    VERIFICATION_CODE,
    PROFILE_INFO_REQUEST,
    PROFILE_INFO,
    PROFILE_NOT_FOUND,
    ERROR,
    MATCH_STATE_REQUEST,
    MATCH_STATE_DATA,
    MOVE_REQUEST,
    MOVE_ACCEPT,
    NO_AUTH,
    JOIN_QUEUE_REQUEST,
    QUEUE_POSITION,
    FOUND_MATCH,
    SUCCESS,
    READY
}
