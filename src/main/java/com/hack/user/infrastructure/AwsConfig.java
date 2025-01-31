package com.hack.user.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;


@Configuration
public class AwsConfig {

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.appClientId}")
    private String appClientId;

    @Value("${aws.cognito.appClientSecret}")
    private String appClientSecret;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.sessionToken:}")
    private String sessionToken;

    public String getUserPoolId() {
        return userPoolId;
    }

    public String getAppClientId() {
        return appClientId;
    }

    public String getAppClientSecret() {
        return appClientSecret;
    }

    public String getRegion() {
        return region;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getSessionToken() {
        return sessionToken;
    }


    // Bean para CognitoIdentityProviderClient
    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretKey);

        if (sessionToken != null && !sessionToken.isEmpty()) {
            return CognitoIdentityProviderClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsSessionCredentials.create(accessKeyId, secretKey, sessionToken)
                    ))
                    .build();
        } else {
            return CognitoIdentityProviderClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();
        }
    }

    // Bean para SnsClient
    @Bean
    public SnsClient snsClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretKey);

        if (sessionToken != null && !sessionToken.isEmpty()) {
            return SnsClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsSessionCredentials.create(accessKeyId, secretKey, sessionToken)
                    ))
                    .build();
        } else {
            return SnsClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();
        }
    }
}