-- ==============================================================================
-- V2 : Donnees de base pour la production
-- ==============================================================================

-- ============================================================================
-- COMPTES ADMIN
-- Mots de passe temporaires — CHANGER IMMEDIATEMENT apres le premier login
-- ============================================================================

-- contact@odinlascience.fr / OlsAdmin2026!
INSERT INTO app_users (email, password, first_name, last_name, role, email_verified, auth_provider)
VALUES ('contact@odinlascience.fr',
        '{bcrypt}$2b$12$doDCRMXsIMygs0NXPnyi7.mE0ZBjXbEtOVkMnBRcXJskWPshyBD4K',
        'Admin', 'OLS', 'ADMIN', true, 'LOCAL')
ON CONFLICT (email) DO NOTHING;

-- gabriel.nicolas.mille@gmail.com / OlsGabriel2026!
INSERT INTO app_users (email, password, first_name, last_name, role, email_verified, auth_provider)
VALUES ('gabriel.nicolas.mille@gmail.com',
        '{bcrypt}$2b$12$ZfnG.W6NGGOH6IlvY5VonuvATB7nDHt7Pc2crfkrF3yE366L8waka',
        'Gabriel', 'Mille', 'ADMIN', true, 'LOCAL')
ON CONFLICT (email) DO NOTHING;

-- bastien.astolfi2b@gmail.com / OlsBastien2026!
INSERT INTO app_users (email, password, first_name, last_name, role, email_verified, auth_provider)
VALUES ('bastien.astolfi2b@gmail.com',
        '{bcrypt}$2b$12$dwl1hWxJAIag4VxcgMEJn.cjGYpzavKFtc.dqREZMOKAyCF0Mkm/a',
        'Bastien', 'Astolfi', 'ADMIN', true, 'LOCAL')
ON CONFLICT (email) DO NOTHING;

-- ethan.difraja@gmail.com / OlsEthan2026!
INSERT INTO app_users (email, password, first_name, last_name, role, email_verified, auth_provider)
VALUES ('ethan.difraja@gmail.com',
        '{bcrypt}$2b$12$HptxS/lxhQzKjmIACrht7ur0p5Ga78jS7nSKv.bUs0QHodHPX4yBu',
        'Ethan', 'Di Fraja', 'ADMIN', true, 'LOCAL')
ON CONFLICT (email) DO NOTHING;

-- issamouali21@gmail.com / OlsIssam2026!
INSERT INTO app_users (email, password, first_name, last_name, role, email_verified, auth_provider)
VALUES ('issamouali21@gmail.com',
        '{bcrypt}$2b$12$nHGSrunLevX8YhTuVFJgk.JymiR/KUmzUfmsnvG7eyN5yoZ1566Q2',
        'Issam', 'Ouali', 'ADMIN', true, 'LOCAL')
ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- CATALOGUE MODULES
-- ============================================================================

-- Atlas (Apprentissage)
INSERT INTO modules (module_key, type, price, active) VALUES ('MUNIN_BACTERIO', 'MUNIN_ATLAS', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('MUNIN_MYCO', 'MUNIN_ATLAS', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('MUNIN_VIRO', 'MUNIN_ATLAS', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('MUNIN_IMMUNO', 'MUNIN_ATLAS', 0.0, true) ON CONFLICT (module_key) DO NOTHING;

-- Lab (Outils professionnels)
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_LIMS', 'HUGIN_LAB', 29.99, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_INVENTORY', 'HUGIN_LAB', 14.99, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_CHAT', 'HUGIN_LAB', 9.99, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_MEETING', 'HUGIN_LAB', 19.99, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_MAIL', 'HUGIN_LAB', 12.99, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_CALENDAR', 'HUGIN_LAB', 7.99, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_BOOKING', 'HUGIN_LAB', 14.99, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_NOTES', 'HUGIN_LAB', 7.99, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_QUICKSHARE', 'HUGIN_LAB', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_SUPPORT', 'HUGIN_LAB', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_NOTIFICATIONS', 'HUGIN_LAB', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_CONTACTS', 'HUGIN_LAB', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_ORGANIZATIONS', 'HUGIN_LAB', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_ANNOTATIONS', 'HUGIN_LAB', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
INSERT INTO modules (module_key, type, price, active) VALUES ('HUGIN_STUDY_COLLECTIONS', 'HUGIN_LAB', 0.0, true) ON CONFLICT (module_key) DO NOTHING;
