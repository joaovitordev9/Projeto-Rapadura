package br.com.projeto.rapadura.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProdutoRequest {

    private Integer codigo;
    private String nome;
    private String descricao;
    private MultipartFile imagem;
}
