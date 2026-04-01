package com.odinlascience.backend.user.service;

import com.odinlascience.backend.exception.PreferencesConflictException;
import com.odinlascience.backend.user.dto.UpdateUserPreferencesRequest;
import com.odinlascience.backend.user.dto.UserPreferencesDTO;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.model.UserPreferences;
import com.odinlascience.backend.user.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferencesService {

    private final UserPreferencesRepository preferencesRepository;
    private final UserContextService userContextService;

    @Transactional(readOnly = true)
    public UserPreferencesDTO getPreferences() {
        User user = userContextService.getAuthenticatedUser();
        return preferencesRepository.findByUserId(user.getId())
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public UserPreferencesDTO updatePreferences(UpdateUserPreferencesRequest request) {
        User user = userContextService.getAuthenticatedUser();

        UserPreferences prefs = preferencesRepository.findByUserId(user.getId())
                .orElseGet(() -> UserPreferences.builder().user(user).build());

        // Detection de conflit : si le serveur a des prefs plus recentes que le client
        if (prefs.getLastModified() != null && request.getLastModified().isBefore(prefs.getLastModified())) {
            log.info("Conflit de preferences pour l'utilisateur {} : client={}, serveur={}",
                    user.getEmail(), request.getLastModified(), prefs.getLastModified());
            throw new PreferencesConflictException(toDTO(prefs));
        }

        prefs.setPreferencesJson(request.getPreferencesJson());
        prefs.setLastModified(request.getLastModified());
        prefs.setVersion(request.getVersion());

        prefs = preferencesRepository.save(prefs);
        log.debug("Preferences mises a jour pour l'utilisateur {}", user.getEmail());
        return toDTO(prefs);
    }

    private UserPreferencesDTO toDTO(UserPreferences prefs) {
        return UserPreferencesDTO.builder()
                .preferencesJson(prefs.getPreferencesJson())
                .lastModified(prefs.getLastModified())
                .version(prefs.getVersion())
                .build();
    }
}
