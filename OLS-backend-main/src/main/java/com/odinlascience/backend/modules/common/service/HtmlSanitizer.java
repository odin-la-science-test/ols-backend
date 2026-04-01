package com.odinlascience.backend.modules.common.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * Utilitaire de sanitization HTML/Markdown.
 * Supprime les balises et attributs dangereux (scripts, event handlers, iframes, etc.)
 * tout en conservant le formatage basique (gras, italique, liens, listes, etc.).
 */
public final class HtmlSanitizer {

    private HtmlSanitizer() {
        // Classe utilitaire non instanciable
    }

    /**
     * Nettoie le contenu HTML/Markdown en supprimant les elements dangereux.
     * Utilise {@link Safelist#relaxed()} qui autorise le formatage basique
     * (b, i, em, strong, a, ul, ol, li, blockquote, pre, code, img, table, etc.)
     * mais bloque les scripts, iframes et event handlers.
     *
     * @param content le contenu brut a nettoyer (peut etre null)
     * @return le contenu sanitise, ou null si l'entree est null
     */
    public static String sanitize(String content) {
        if (content == null) {
            return null;
        }
        return Jsoup.clean(content, Safelist.relaxed());
    }
}
