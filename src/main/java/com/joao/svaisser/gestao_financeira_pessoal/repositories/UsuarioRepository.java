package com.joao.svaisser.gestao_financeira_pessoal.repositories;

import com.joao.svaisser.gestao_financeira_pessoal.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}

