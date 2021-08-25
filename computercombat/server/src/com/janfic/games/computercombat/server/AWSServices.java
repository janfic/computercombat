package com.janfic.games.computercombat.server;

import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.network.Type;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotConfirmedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

/**
 *
 * @author Jan Fic
 */
public class AWSServices {

    private final String userPoolID;
    CognitoIdentityProviderClient cognito;

    public AWSServices(String userPoolID) {
        this.userPoolID = userPoolID;
        cognito = CognitoIdentityProviderClient.create();
    }

    public boolean isUsernameAvailable(String userName) {
        try {
            cognito.adminGetUser(AdminGetUserRequest.builder().username(userName).userPoolId(userPoolID).build());
        } catch (UserNotFoundException e) {
            return true;
        }
        return false;
    }

    public boolean isEmailUsed(String email) {
        ListUsersRequest request = ListUsersRequest.builder()
                .userPoolId(userPoolID)
                .filter("email = \"" + email + "\"")
                .build();
        ListUsersResponse response = cognito.listUsers(request);
        System.out.println(response.toString());
        return response.users().size() > 0;
    }

    public void createUser(String username, String email, String password) {

        AttributeType emailAttribute = AttributeType.builder().name("email").value(email).build();
        //AttributeType passwordAttribute = AttributeType.builder().name("password").value(password).build();

        SignUpRequest request = SignUpRequest.builder()
                .username(username)
                .userAttributes(emailAttribute)
                .password(password)
                .clientId("7ivqqa7m71v3ob6qnof7djh90t")
                .build();
        SignUpResponse response = cognito.signUp(request);
        System.out.println(response.toString());
    }

    public Message getUser(String username, String password) {

        Map<String, String> params = new HashMap<>();
        params.put("USERNAME", username);
        params.put("PASSWORD", password);

        InitiateAuthRequest request = InitiateAuthRequest.builder()
                .clientId("7ivqqa7m71v3ob6qnof7djh90t")
                .authParameters(params)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .build();
        try {
            InitiateAuthResponse response = cognito.initiateAuth(request);
            System.out.println(response.challengeName());
            Message m = new Message(Type.PROFILE_INFO, response.authenticationResult().toString());
            return m;
        } catch (NotAuthorizedException e) {
            return new Message(Type.NO_AUTH, "NO AUTH");
        } catch (UserNotConfirmedException e) {
            return new Message(Type.VERIFY_WITH_CODE, "Please use verification code");
        } catch (AwsServiceException | SdkClientException e) {
            return new Message(Type.ERROR, "UNKOWN ERROR : \n" + e.getMessage());
        }
    }

    public boolean verifyUser(String username, String code) {
        ConfirmSignUpRequest request = ConfirmSignUpRequest.builder()
                .username(username)
                .clientId("7ivqqa7m71v3ob6qnof7djh90t")
                .confirmationCode(code)
                .build();

        try {
            ConfirmSignUpResponse response = cognito.confirmSignUp(request);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
