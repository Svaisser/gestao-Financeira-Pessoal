package com.joao.svaisser.gestao_financeira_pessoal.services;

import com.joao.svaisser.gestao_financeira_pessoal.dtos.LoginRequest;
import com.joao.svaisser.gestao_financeira_pessoal.dtos.LoginResponse;
import com.joao.svaisser.gestao_financeira_pessoal.dtos.RegistroRequest;
import com.joao.svaisser.gestao_financeira_pessoal.models.Usuario;
import com.joao.svaisser.gestao_financeira_pessoal.repositories.UsuarioRepository;
import com.joao.svaisser.gestao_financeira_pessoal.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser um número positivo");
        }
        return usuarioRepository.findById(id);
    }

    public Usuario create(Usuario usuario) {
        // Validações
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do usuário não pode ser vazio");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email do usuário não pode ser vazio");
        }

        // Validar se email já existe
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }

        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }

        if (usuario.getSenha().length() < 8) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 8 caracteres");
        }

        // Criptografar senha se PasswordEncoder estiver disponível
        if (passwordEncoder != null) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public Usuario update(Long id, Usuario usuarioAtualizado) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser um número positivo");
        }

        if (usuarioAtualizado == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com ID " + id + " não encontrado"));

        // Validar nome
        if (usuarioAtualizado.getNome() != null && !usuarioAtualizado.getNome().trim().isEmpty()) {
            usuarioExistente.setNome(usuarioAtualizado.getNome());
        } else if (usuarioAtualizado.getNome() != null) {
            throw new IllegalArgumentException("Nome do usuário não pode ser vazio");
        }

        // Validar email (se for diferente)
        if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().trim().isEmpty()) {
            if (!usuarioAtualizado.getEmail().equals(usuarioExistente.getEmail())) {
                if (usuarioRepository.findByEmail(usuarioAtualizado.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Email já cadastrado no sistema");
                }
            }
            usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        } else if (usuarioAtualizado.getEmail() != null) {
            throw new IllegalArgumentException("Email do usuário não pode ser vazio");
        }

        // Validar e atualizar senha
        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().trim().isEmpty()) {
            if (usuarioAtualizado.getSenha().length() < 8) {
                throw new IllegalArgumentException("Senha deve ter no mínimo 8 caracteres");
            }
            if (passwordEncoder != null) {
                usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
            } else {
                usuarioExistente.setSenha(usuarioAtualizado.getSenha());
            }
        }

        // Atualizar status ativo
        if (usuarioAtualizado.getAtivo() != null) {
            usuarioExistente.setAtivo(usuarioAtualizado.getAtivo());
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser um número positivo");
        }

        if (!usuarioRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Usuário com ID " + id + " não encontrado");
        }

        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        return usuarioRepository.findByEmail(email);
    }

    public void desativarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com ID " + id + " não encontrado"));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public void ativarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com ID " + id + " não encontrado"));
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getSenha() == null) {
            throw new IllegalArgumentException("Email e senha são obrigatórios");
        }
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha inválidos"));
        if (!usuario.getAtivo()) {
            throw new IllegalArgumentException("Usuário inativo");
        }
        if (!passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }
        String token = jwtService.generateToken(usuario.getId(), usuario.getEmail());
        return new LoginResponse(token, usuario.getId(), usuario.getEmail(), usuario.getNome());
    }

    public LoginResponse registro(RegistroRequest registroRequest) {
        if (registroRequest == null) {
            throw new IllegalArgumentException("Dados de registro são obrigatórios");
        }
        if (registroRequest.getEmail() == null || registroRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (registroRequest.getNome() == null || registroRequest.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (registroRequest.getSenha() == null || registroRequest.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        if (registroRequest.getSenha().length() < 8) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres");
        }
        if (!registroRequest.getSenha().equals(registroRequest.getConfirmarSenha())) {
            throw new IllegalArgumentException("Senhas não conferem");
        }
        if (usuarioRepository.findByEmail(registroRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setEmail(registroRequest.getEmail());
        usuario.setNome(registroRequest.getNome());
        usuario.setSenha(passwordEncoder.encode(registroRequest.getSenha()));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAtivo(true);
        usuario = usuarioRepository.save(usuario);
        String token = jwtService.generateToken(usuario.getId(), usuario.getEmail());
        return new LoginResponse(token, usuario.getId(), usuario.getEmail(), usuario.getNome());
    }

    public Optional<Usuario> buscarUsuarioPorToken(String token) {
        try {
            if (!jwtService.isTokenValid(token)) {
                return Optional.empty();
            }
            Long usuarioId = jwtService.extractUsuarioId(token);
            return usuarioRepository.findById(usuarioId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities("USER")
                .build();
    }
}
