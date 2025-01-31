package com.hack.user.core.entities;

public class Usuario {

    private Email email;
    private String senha;

    public Usuario(Email email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public Email getEmail() {
        return this.email;
    }

    public String getSenha() {
        return this.senha;
    }
}
