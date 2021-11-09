package com.janfic.games.computercombat.server;

import com.amazonaws.regions.Regions;
import com.janfic.games.computercombat.network.Type;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.computercombat.network.Message;
import com.janfic.games.computercombat.model.Profile;
import com.janfic.games.computercombat.network.client.SQLAPI;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
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
        cognito = CognitoIdentityProviderClient.builder().credentialsProvider(AnonymousCredentialsProvider.create()).region(software.amazon.awssdk.regions.Region.US_EAST_1).build();
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
        System.out.println("HERE");

        AttributeType emailAttribute = AttributeType.builder().name("email").value(email).build();

        SignUpRequest request = SignUpRequest.builder()
                .username(username)
                .userAttributes(emailAttribute)
                .password(password)
                .clientId("7ivqqa7m71v3ob6qnof7djh90t")
                .build();
        SignUpResponse response = cognito.signUp(request);
        String userSub = response.userSub();

        Profile p = new Profile(userSub);
        p.setEmail(email);
        p.setName(username);
        SQLAPI.getSingleton().saveProfile(p);

        FileHandle f = Gdx.files.internal("starterCollection.csv");
        Scanner scanner = new Scanner(f.readString());

        while (scanner.hasNextLine()) {
            int cardID = Integer.parseInt(scanner.nextLine());
            System.out.println(cardID);
            System.out.println(p.getUID());
            SQLAPI.getSingleton().addCardToProfile(cardID, p);
        }

        return userSub;
    }

    @Deprecated
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
                    data = sub;
                }
            }
            Message m = new Message(Type.PROFILE_INFO, data);
            return m;

        } catch (NotAuthorizedException e) {
            return new Message(Type.NO_AUTH, "NO AUTH");
        } catch (UserNotConfirmedException e) {
            return new Message(Type.VERIFY_WITH_CODE, "Please use verification code");
        } catch (AwsServiceException | SdkClientException e) {
            e.printStackTrace();
        }
        return null;
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
            return false;
        }
    }
}
