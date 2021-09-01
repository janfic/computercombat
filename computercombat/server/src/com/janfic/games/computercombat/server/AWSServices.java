package com.janfic.games.computercombat.server;

import com.amazonaws.regions.Regions;
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
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringInputStream;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.model.Profile;
import java.io.IOException;
import java.util.List;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;

/**
 *
 * @author Jan Fic
 */
public class AWSServices {

    private final String userPoolID;
    CognitoIdentityProviderClient cognito;
    AmazonS3 s3;

    public AWSServices(String userPoolID) {
        this.userPoolID = userPoolID;
        cognito = CognitoIdentityProviderClient.create();
        s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
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

    public String createUser(String username, String email, String password) {

        AttributeType emailAttribute = AttributeType.builder().name("email").value(email).build();

        SignUpRequest request = SignUpRequest.builder()
                .username(username)
                .userAttributes(emailAttribute)
                .password(password)
                .clientId("7ivqqa7m71v3ob6qnof7djh90t")
                .build();
        SignUpResponse response = cognito.signUp(request);
        String userSub = response.userSub();

        return userSub;
    }

    public void saveProfile(Profile profile) {
        Json json = new Json();
        try {
            StringInputStream is = new StringInputStream(json.toJson(profile));
            PutObjectRequest request = new PutObjectRequest("computer-combat-player-data", "player_data/" + profile.getUID() + ".json", is, new ObjectMetadata());
            s3.putObject(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFileAsString(String fileKey) {
        try {
            GetObjectRequest getProfileDataRequest = new GetObjectRequest("computer-combat-player-data", fileKey);
            S3Object object = s3.getObject(getProfileDataRequest);
            return new String(object.getObjectContent().readAllBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Message userLogin(String username, String password) {

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
            GetUserRequest userRequest = GetUserRequest.builder().accessToken(response.authenticationResult().accessToken()).build();
            List<AttributeType> userAttributes = cognito.getUser(userRequest).userAttributes();
            String data = "";
            for (AttributeType userAttribute : userAttributes) {
                if (userAttribute.name().equals("sub")) {
                    String sub = userAttribute.value();
                    GetObjectRequest getProfileDataRequest = new GetObjectRequest("computer-combat-player-data", "player_data/" + sub + ".json");
                    S3Object object = s3.getObject(getProfileDataRequest);
                    data = new String(object.getObjectContent().readAllBytes());
                }
            }
            Message m = new Message(Type.PROFILE_INFO, data);
            return m;

        } catch (NotAuthorizedException e) {
            return new Message(Type.NO_AUTH, "NO AUTH");
        } catch (UserNotConfirmedException e) {
            return new Message(Type.VERIFY_WITH_CODE, "Please use verification code");
        } catch (AwsServiceException | SdkClientException e) {
            return new Message(Type.ERROR, "UNKOWN ERROR : \n" + e.getMessage());
        } catch (IOException e) {
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
