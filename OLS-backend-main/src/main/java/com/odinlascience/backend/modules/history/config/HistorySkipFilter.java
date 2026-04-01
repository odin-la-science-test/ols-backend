package com.odinlascience.backend.modules.history.config;

import com.odinlascience.backend.modules.history.context.HistoryContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre HTTP qui detecte le header X-History-Skip et positionne le ThreadLocal.
 * Les requetes undo/redo du frontend envoient ce header pour eviter
 * que l'action ne soit enregistree une seconde fois dans l'historique.
 */
@Component
public class HistorySkipFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-History-Skip";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String headerValue = request.getHeader(HEADER_NAME);
            if ("true".equalsIgnoreCase(headerValue)) {
                HistoryContext.setSkip(true);
            }
            filterChain.doFilter(request, response);
        } finally {
            HistoryContext.clear();
        }
    }
}
