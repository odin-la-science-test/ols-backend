package com.odinlascience.backend.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Nonnull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.odinlascience.backend.auth.service.SessionService;
import com.odinlascience.backend.auth.util.CookieUtils;
import com.odinlascience.backend.security.service.JwtService;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final SessionService sessionService;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {

        final String jwt = CookieUtils.extractAccessToken(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String subject;
        try {
            subject = jwtService.extractSubject(jwt);
        } catch (Exception e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtService.isGuestToken(jwt)) {
                UserDetails guestDetails = new User(
                        subject,
                        "",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"))
                );

                if (!jwtService.isTokenExpired(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            guestDetails,
                            null,
                            guestDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated guest: {}", subject);
                }
            } else {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(subject);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UUID sessionId = jwtService.extractSessionId(jwt);

                    if (sessionId != null && !sessionService.isSessionValid(sessionId)) {
                        log.debug("Session revoquee pour user: {}", subject);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    if (sessionId != null) {
                        request.setAttribute("sessionId", sessionId);
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated user: {}", subject);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
