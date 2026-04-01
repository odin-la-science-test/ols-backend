package com.odinlascience.backend.auth.service;

import com.odinlascience.backend.auth.dto.AuthResponse;
import com.odinlascience.backend.auth.dto.CasUserInfo;
import com.odinlascience.backend.auth.dto.GoogleUserInfo;
import com.odinlascience.backend.auth.model.UserSession;
import com.odinlascience.backend.exception.AuthProviderConflictException;
import com.odinlascience.backend.security.service.JwtService;
import com.odinlascience.backend.user.enums.AuthProvider;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.mapper.UserMapper;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthAuthService {

    private final GoogleOAuthClient googleOAuthClient;
    private final CasClient casClient;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;

    @Transactional
    public AuthService.LoginResult authenticateWithGoogle(String code, String deviceInfo, String ipAddress) {
        log.info("Authentification Google en cours");

        String googleAccessToken = googleOAuthClient.exchangeCodeForAccessToken(code);
        GoogleUserInfo googleUser = googleOAuthClient.getUserInfo(googleAccessToken);

        User user = findOrCreateOAuthUser(
                googleUser.getEmail(),
                googleUser.getSub(),
                googleUser.getGivenName(),
                googleUser.getFamilyName(),
                AuthProvider.GOOGLE
        );

        return buildLoginResult(user, deviceInfo, ipAddress);
    }

    @Transactional
    public AuthService.LoginResult authenticateWithCas(String ticket, String serviceUrl, String deviceInfo, String ipAddress) {
        log.info("Authentification CAS en cours");

        CasUserInfo casUser = casClient.validateTicket(ticket, serviceUrl);

        User user = findOrCreateOAuthUser(
                casUser.getEmail(),
                casUser.getUid(),
                casUser.getFirstName(),
                casUser.getLastName(),
                AuthProvider.CAS_UNIV_LILLE
        );

        return buildLoginResult(user, deviceInfo, ipAddress);
    }

    private User findOrCreateOAuthUser(String email, String externalId, String firstName, String lastName,
                                       AuthProvider provider) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getAuthProvider() != provider) {
                throw new AuthProviderConflictException(user.getAuthProvider());
            }
            if (user.getExternalId() == null) {
                user.setExternalId(externalId);
                userRepository.save(user);
            }
            log.info("Utilisateur OAuth existant connecte: {}", email);
            return user;
        }

        User newUser = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .authProvider(provider)
                .externalId(externalId)
                .role(RoleType.STUDENT)
                .build();

        User saved = userRepository.save(newUser);
        log.info("Nouvel utilisateur OAuth cree: {} via {}", email, provider);
        return saved;
    }

    private AuthService.LoginResult buildLoginResult(User user, String deviceInfo, String ipAddress) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        UserSession session = sessionService.createSession(
                user, refreshToken, deviceInfo, ipAddress, jwtService.getRefreshTokenExpirationMs());
        String accessToken = jwtService.generateAccessToken(userDetails, session.getId());

        AuthResponse response = AuthResponse.builder()
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .user(userMapper.toDTO(user))
                .build();

        return new AuthService.LoginResult(accessToken, refreshToken, response);
    }
}
