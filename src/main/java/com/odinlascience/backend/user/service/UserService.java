package com.odinlascience.backend.user.service;

import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.mapper.UserMapper;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

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
}