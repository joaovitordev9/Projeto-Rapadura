package br.com.projeto.rapadura.controller;

import br.com.projeto.rapadura.model.Produto;
import br.com.projeto.rapadura.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() throws IOException {
        return ResponseEntity.ok(produtoService.carregarProdutos());
    }

    @PostMapping
    public ResponseEntity<String> cadastrarProduto(@RequestBody Produto produto, HttpSession session) {
        if (!adminLogado(session)) {
            return ResponseEntity.status(403).body("Acesso negado");
        }

        boolean cadastrado = produtoService.adicionarProduto(produto);

        if (cadastrado) {
            return ResponseEntity.ok("Produto cadastrado com sucesso");
        }

        return ResponseEntity.badRequest().body("Nao foi possivel cadastrar o produto");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarProduto(@PathVariable int id, HttpSession session) {
        if (!adminLogado(session)) {
            return ResponseEntity.status(403).body("Acesso negado");
        }

        boolean removido = produtoService.deletarProduto(id);

        if (removido) {
            return ResponseEntity.ok("Produto removido com sucesso");
        }

        return ResponseEntity.status(404).body("Produto nao encontrado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterarProduto(@PathVariable int id, @RequestBody Produto produto, HttpSession session) {
        if (!adminLogado(session)) {
            return ResponseEntity.status(403).body("Acesso negado");
        }

        boolean alterado = produtoService.alterarProduto(id, produto);

        if (alterado) {
            return ResponseEntity.ok("Produto alterado com sucesso");
        }

        return ResponseEntity.status(404).body("Produto nao encontrado");
    }

    private boolean adminLogado(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("admin"));
    }
}
