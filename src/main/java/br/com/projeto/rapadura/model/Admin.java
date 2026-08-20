package br.com.projeto.rapadura.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Admin {
    private String usuario;

    @JsonProperty("senhaHash")
    private String senhaHash;
}
