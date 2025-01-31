package com.hack.user.gateways;

import com.hack.user.core.entities.Email;
import com.hack.user.core.entities.Usuario;
import com.hack.user.core.repositories.UsuarioRepository;
import com.hack.user.infrastructure.AwsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;

import java.util.UUID;

@Service
public class UsuarioGateway implements UsuarioRepository {

    @Autowired
    private CognitoIdentityProviderClient cognitoClient;

    @Autowired
    private SnsClient snsClient;

    @Autowired
    private AwsConfig awsConfig;

    public boolean usuarioExiste(Email email) {
        String userPoolId = awsConfig.getUserPoolId();

        AdminGetUserRequest request = AdminGetUserRequest.builder()
                .userPoolId(userPoolId)
                .username(email.getEmail())
                .build();

        try {
            AdminGetUserResponse response = this.cognitoClient.adminGetUser(request);
            return response != null;
        } catch (CognitoIdentityProviderException e) {
            if (e.awsErrorDetails().errorCode().equals("UserNotFoundException")) {
                return false;
            }
            throw e;
        }
    }

    public void salvar(Usuario usuario) throws Exception {
        try {
            this.createUserInCognito(usuario);
            this.createSnsTopicForUser(usuario.getEmail().getEmail());
        } catch (Exception e) {
            // Tratando erros de chamadas ao Cognito e SNS
            throw new Exception("Erro ao registrar o usuário: " + e.getMessage(), e);
        }
    }

    // Método para criar o usuário no Cognito
    private void createUserInCognito(Usuario usuario) throws Exception {
        try {

            String userPoolId = awsConfig.getUserPoolId();  // Obtendo o valor configurado

            // Configuração para criar o usuário no Cognito
            AdminCreateUserRequest createUserRequest = AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(usuario.getEmail().getEmail())  // Usando o email como username
                    .temporaryPassword(usuario.getSenha())
                    .userAttributes(
                            AttributeType.builder().name("email").value(usuario.getEmail().getEmail()).build(),
                            AttributeType.builder().name("email_verified").value("true").build()
                    )
                    .messageAction(MessageActionType.SUPPRESS) // Inibir email de criação, SNS já irá enviar no subscription
                    .build();

            cognitoClient.adminCreateUser(createUserRequest);  // Chamada para criar o usuário no Cognito

            // Alterar a senha temporária para permanente sem exigir mudança
            AdminSetUserPasswordRequest setPasswordRequest = AdminSetUserPasswordRequest.builder()
                    .userPoolId(userPoolId)
                    .username(usuario.getEmail().getEmail())
                    .password(usuario.getSenha())  // Senha permanente
                    .permanent(true)  // Marca a senha como permanente
                    .build();

            // Define a senha permanentemente para o usuário
            cognitoClient.adminSetUserPassword(setPasswordRequest);

        } catch (Exception e) {
            throw new Exception("Falha ao criar usuário no Cognito: " + e.getMessage(), e);
        }
    }

    // Método para criar um tópico no SNS para o usuário e assinar o e-mail
    private void createSnsTopicForUser(String email) throws Exception {
        try {
            // Substituir caracteres inválidos do nome do tópico por "_"
            String topicName = email.replaceAll("[^a-zA-Z0-9-_]", "_");

            // Limitar o tamanho do nome do tópico para 256 caracteres
            if (topicName.length() > 256) {
                topicName = topicName.substring(0, 256);
            }

            // Criando o tópico no SNS
            CreateTopicRequest createTopicRequest = CreateTopicRequest.builder()
                    .name(topicName)  // Nome do tópico baseado no email, com caracteres válidos
                    .build();

            String topicArn = snsClient.createTopic(createTopicRequest).topicArn();  // Obtendo o ARN do tópico criado

            // Assinar o e-mail do usuário ao tópico
            snsClient.subscribe(s -> s
                    .protocol("email")
                    .endpoint(email)
                    .topicArn(topicArn)
            );

            //System.out.println("Tópico SNS criado com nome: " + topicName + " e e-mail " + email + " assinado ao tópico.");

        } catch (Exception e) {
            throw new Exception("Falha ao criar tópico ou assinar e-mail no SNS: " + e.getMessage(), e);
        }
    }
}
