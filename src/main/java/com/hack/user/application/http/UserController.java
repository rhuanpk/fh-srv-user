package com.hack.user.application.http;

import com.hack.user.core.exceptions.UsuarioJaCadastradoException;
import com.hack.user.core.usecases.login.Login;
import com.hack.user.core.usecases.login.LoginInput;
import com.hack.user.core.usecases.login.LoginOutput;
import com.hack.user.core.usecases.usuario.CadastrarUsuario;
import com.hack.user.core.usecases.usuario.CadastrarUsuarioInput;
import com.hack.user.gateways.LoginGateway;
import com.hack.user.gateways.UsuarioGateway;

import com.hack.user.application.JwtValidationService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtValidationService jwtValidationService;

    @Autowired
    private UsuarioGateway usuarioGateway;

    @Autowired
    private LoginGateway loginGateway;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDto userDto) throws Exception {
        CadastrarUsuarioInput input = new CadastrarUsuarioInput(userDto.email(), userDto.password());
        try {
            CadastrarUsuario cadastrarUsuario = new CadastrarUsuario(this.usuarioGateway);
            cadastrarUsuario.executar(input);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuário registrado com sucesso!"));
        } catch (UsuarioJaCadastradoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Capturando erro de validação de email
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // Capturando erros de chamadas ao Cognito ou SNS
            Map<String, String> response = new HashMap<>();
            response.put("message", "Erro interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        LoginInput input = new LoginInput(credentials.get("email"), credentials.get("password"));
        Login login = new Login(this.loginGateway);
        try {
            LoginOutput output = login.executar(input);
            return ResponseEntity.ok(Map.of("token", output.token()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // Endpoint para validar o token e retornar o email
    @GetMapping("/validateToken")
    public ResponseEntity<Map<String, String>> validateToken(@RequestParam String token) {
        Map<String, String> response = jwtValidationService.validateToken(token);
        if (response.containsKey("email")) {
            // Se o email foi extraído com sucesso, retorna a resposta com o email
            return ResponseEntity.ok(response);
        } else {
            // Caso contrário, a resposta com erro será retornada
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }
}
