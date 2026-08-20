package br.com.projeto.rapadura.service;

import br.com.projeto.rapadura.model.Admin;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AdminService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Admin carregarAdmin() {
        try {
            ClassPathResource resource = new ClassPathResource("data/admin.json");
            return mapper.readValue(resource.getInputStream(), Admin.class);
        } catch (IOException e) {
            System.out.println("Nao foi possivel ler o admin.json");
            return null;
        }
    }

    public boolean login(String usuario, String senha) {
        Admin admin = carregarAdmin();

        if (admin == null) {
            return false;
        }

        return admin.getUsuario().equals(usuario) && encoder.matches(senha, admin.getSenhaHash());
    }
}
