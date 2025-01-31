package com.hack.user.core.usecases.login;

import com.hack.user.core.exceptions.RegraDeNegocioException;
import com.hack.user.core.repositories.LoginRepository;

public class Login {

    private final LoginRepository loginRepository;

    public Login(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LoginOutput executar(LoginInput input) throws Exception {
        String token = this.loginRepository.login(input.email(), input.senha());
        if (token == null) {
            throw new RegraDeNegocioException("Usuário ou senha incorretos");
        }
        return new LoginOutput(token);
    }

}
