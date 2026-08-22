package br.com.projeto.rapadura.service;

import br.com.projeto.rapadura.DTO.ProdutoRequest;
import br.com.projeto.rapadura.model.Produto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    private static final TypeReference<List<Produto>> PRODUTO_LIST_TYPE = new TypeReference<>() {};

    private final ObjectMapper mapper = new ObjectMapper();
    private final Path produtosPath = Paths.get("data", "produtos.json");

    public List<Produto> carregarProdutos() throws IOException {
        garantirArquivoProdutos();

        if (Files.size(produtosPath) == 0) {
            return new ArrayList<>();
        }

        List<Produto> produtos = mapper.readValue(produtosPath.toFile(), PRODUTO_LIST_TYPE);
        return produtos != null ? produtos : new ArrayList<>();
    }

    public void salvarProdutos(List<Produto> produtos) throws IOException {
        garantirArquivoProdutos();
        mapper.writerWithDefaultPrettyPrinter().writeValue(produtosPath.toFile(), produtos);
    }

    public boolean adicionarProduto(ProdutoRequest request) {
        try {
            List<Produto> produtos = carregarProdutos();

            Produto produto = new Produto();

            produto.setNome(request.getNome());
            produto.setDescricao(request.getDescricao());

            if (request.getImagem() != null && !request.getImagem().isEmpty()) {
                String caminhoImagem = salvarImagem(request.getImagem());
                produto.setImagem(caminhoImagem);
            }

            produto.setCodigo(proximoCodigo(produtos));

            produtos.add(produto);
            salvarProdutos(produtos);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean deletarProduto(int id) {
        try {
            List<Produto> produtos = carregarProdutos();
            boolean removido = produtos.removeIf(produto -> produto.getCodigo() != null && produto.getCodigo() == id);

            if (!removido) {
                return false;
            }

            salvarProdutos(produtos);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean alterarProduto(int id, Produto produto) {
        try {
            List<Produto> produtos = carregarProdutos();

            for (Produto p : produtos) {
                if (p.getCodigo() != null && p.getCodigo() == id) {
                    p.setNome(produto.getNome());
                    p.setDescricao(produto.getDescricao());
                    p.setImagem(produto.getImagem());
                    salvarProdutos(produtos);
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void garantirArquivoProdutos() throws IOException {
        if (Files.exists(produtosPath)) {
            return;
        }

        Files.createDirectories(produtosPath.getParent());

        ClassPathResource resource = new ClassPathResource("data/produtos.json");
        if (resource.exists()) {
            try (InputStream input = resource.getInputStream()) {
                byte[] conteudo = input.readAllBytes();
                Files.write(produtosPath, conteudo.length == 0 ? "[]".getBytes() : conteudo);
                return;
            }
        }

        Files.writeString(produtosPath, "[]");
    }

    private int proximoCodigo(List<Produto> produtos) {
        return produtos.stream()
                .map(Produto::getCodigo)
                .filter(codigo -> codigo != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private final Path pastaUploads = Paths.get("uploads");

    private String salvarImagem(MultipartFile imagem) throws IOException {

        if (imagem == null || imagem.isEmpty()) {
            return null;
        }

        Files.createDirectories(pastaUploads);

        String extensao = "";

        String nomeOriginal = imagem.getOriginalFilename();

        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }

        String nomeArquivo = UUID.randomUUID() + extensao;

        Path caminho = pastaUploads.resolve(nomeArquivo);

        imagem.transferTo(caminho);

        return "/uploads/" + nomeArquivo;
    }
}
