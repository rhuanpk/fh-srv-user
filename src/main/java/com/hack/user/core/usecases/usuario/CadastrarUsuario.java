package com.hack.user.core.usecases.usuario;

import com.hack.user.core.entities.Email;
import com.hack.user.core.entities.Usuario;
import com.hack.user.core.exceptions.UsuarioJaCadastradoException;
import com.hack.user.core.repositories.UsuarioRepository;

public class CadastrarUsuario {

    private final UsuarioRepository usuarioRepository;

    public CadastrarUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void executar(CadastrarUsuarioInput input) throws Exception {
        Email email = new Email(input.email());
        if (this.usuarioRepository.usuarioExiste(email)) {
            throw new UsuarioJaCadastradoException("Usuário já cadastrado");
        }
        Usuario usuario = new Usuario(email, input.senha());
        this.usuarioRepository.salvar(usuario);
    }

}
