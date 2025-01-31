package com.hack.user.core.usecases;

import com.hack.user.core.exceptions.RegraDeNegocioException;
import com.hack.user.core.repositories.LoginRepository;
import com.hack.user.core.usecases.login.Login;
import com.hack.user.core.usecases.login.LoginInput;
import com.hack.user.core.usecases.login.LoginOutput;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginTest {

    @Mock
    private LoginRepository loginRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = org.mockito.MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void deve_logar_usuario() throws Exception {
        String token = "token válido";
        when(this.loginRepository.login(any(String.class), any(String.class))).thenReturn(token);
        LoginInput input = new LoginInput("email@gmail.com", "senha");
        Login useCase = new Login(this.loginRepository);
        LoginOutput output = useCase.executar(input);
        assertThat(output.token()).isEqualTo(token);
    }

    @Test
    void nao_deve_logar_usuario_inexistente() throws Exception {
        when(this.loginRepository.login(any(String.class), any(String.class))).thenReturn(null);
        LoginInput input = new LoginInput("email@gmail.com", "senha");
        Login useCase = new Login(this.loginRepository);

        assertThatThrownBy(() -> useCase.executar(input))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessage("Usuário ou senha incorretos");
    }

}
