package com.hack.user.core.usecases;

import com.hack.user.core.entities.Email;
import com.hack.user.core.exceptions.UsuarioJaCadastradoException;
import com.hack.user.core.repositories.UsuarioRepository;
import com.hack.user.core.usecases.usuario.CadastrarUsuario;
import com.hack.user.core.usecases.usuario.CadastrarUsuarioInput;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CadastrarUsuarioTest {

    @Mock
    private UsuarioRepository usuarioRepository;

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
    void deve_criar_um_usuario() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(this.usuarioRepository.usuarioExiste(any(Email.class))).thenReturn(false);

        CadastrarUsuarioInput input = new CadastrarUsuarioInput("email@valido.com.br",  "senha");
        CadastrarUsuario useCase = new CadastrarUsuario(this.usuarioRepository);
        useCase.executar(input);
    }

    @Test
    void nao_deve_permitir_criar_usuario_repetido() {

        when(this.usuarioRepository.usuarioExiste(any(Email.class))).thenReturn(true);

        CadastrarUsuarioInput input = new CadastrarUsuarioInput("email@valido.com.br",  "senha");
        CadastrarUsuario useCase = new CadastrarUsuario(this.usuarioRepository);

        assertThatThrownBy(() -> useCase.executar(input))
                .isInstanceOf(UsuarioJaCadastradoException.class)
                .hasMessage("Usuário já cadastrado");

    }

}
