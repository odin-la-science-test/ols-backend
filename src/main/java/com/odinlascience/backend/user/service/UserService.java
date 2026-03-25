package com.odinlascience.backend.user.service;

import com.odinlascience.backend.modules.common.spi.UserQuerySPI;
import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.mapper.UserMapper;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserQuerySPI {

    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserDTO> getAllUsers() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    public UserDTO getUserByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toDTO)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    public List<UserDTO> searchUsers(String query) {
        if (query == null || query.isBlank()) return List.of();
        return repository.search(query.trim()).stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ─── UserQuerySPI ───

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public List<User> search(String query) {
        if (query == null || query.isBlank()) return List.of();
        return repository.search(query.trim());
    }

    @Transactional
    public UserDTO updateAvatar(String avatarId) {
        User user = getCurrentUser()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié"));
        user.setAvatarId(avatarId);
        return mapper.toDTO(repository.save(user));
    }

    @Override
    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return repository.findByEmail(auth.getName());
    }
}