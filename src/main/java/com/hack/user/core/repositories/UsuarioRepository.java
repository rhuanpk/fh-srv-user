package com.hack.user.core.repositories;

import com.hack.user.core.entities.Email;
import com.hack.user.core.entities.Usuario;

public interface UsuarioRepository {

    void salvar(Usuario usuario) throws Exception;

    boolean usuarioExiste(Email email);

}
