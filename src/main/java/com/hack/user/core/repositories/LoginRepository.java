package com.hack.user.core.repositories;

public interface LoginRepository {
    public String login(String email, String senha) throws Exception;
}
