package br.com.projeto.rapadura.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Produto{
    private Integer codigo;
    private String nome;
    private String descricao;
    private String imagem;
}
