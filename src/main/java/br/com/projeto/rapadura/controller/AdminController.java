package br.com.projeto.rapadura.controller;

import br.com.projeto.rapadura.model.LoginRequest;
import br.com.projeto.rapadura.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpSession session) {
        boolean autenticado = adminService.login(request.getUsuario(), request.getSenha());

        if (autenticado) {
            session.setAttribute("admin", true);
            return ResponseEntity.ok("Login realizado");
        }

        return ResponseEntity.status(401).body("Credenciais invalidas");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout realizado");
    }

    @GetMapping("/admin/status")
    public Map<String, Boolean> status(HttpSession session) {
        return Map.of("admin", Boolean.TRUE.equals(session.getAttribute("admin")));
    }
}
