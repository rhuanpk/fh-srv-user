package com.hack.user.core.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class EmailTest {

    @Test
    void deve_permitir_um_email_valido() {
        String emailValido = "email_valido@gmail.com";
        Email email = new Email(emailValido);
        assertThat(email.getEmail()).isEqualTo(emailValido);
    }

    @Test
    void nao_deve_permitir_email_invalido() {
        assertThatThrownBy(() -> new Email("email_invalido"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email inválido");
    }

}
