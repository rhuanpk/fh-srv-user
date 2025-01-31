package com.hack.user.gateways;

import com.hack.user.core.repositories.LoginRepository;
import com.hack.user.infrastructure.AwsConfig;
import com.hack.user.infrastructure.SecretHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import java.util.Map;

@Service
public class LoginGateway implements LoginRepository {

    @Autowired
    private CognitoIdentityProviderClient cognitoClient;

    @Autowired
    private AwsConfig awsConfig;

    public String login(String email, String senha) throws Exception {
        try {
            String userPoolId = awsConfig.getUserPoolId();
            String clientId = awsConfig.getAppClientId();
            String clientSecret = awsConfig.getAppClientSecret();

            // Gerando o SECRET_HASH
            String secretHash = SecretHashUtil.generateSecretHash(email, clientId, clientSecret);

            // Autenticação do usuário com Cognito
            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .userPoolId(userPoolId)
                    .clientId(clientId)
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .authParameters(Map.of(
                            "USERNAME", email,
                            "PASSWORD", senha,
                            "SECRET_HASH", secretHash
                    ))
                    .build();

            AdminInitiateAuthResponse authResponse = cognitoClient.adminInitiateAuth(authRequest);
            return authResponse.authenticationResult().idToken();
        } catch (Exception e) {
            return null;
        }
    }

}
