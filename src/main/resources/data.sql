-- ============================================================================
-- 0. UTILISATEURS DE TEST
-- ============================================================================
-- Mot de passe: 1234 (hashé avec BCrypt, préfixé {bcrypt} pour DelegatingPasswordEncoder)

INSERT INTO app_users (email, password, first_name, last_name, role, email_verified, auth_provider)
VALUES ('admin@example.com', '{bcrypt}$2a$10$dYT5CMj6KyNNK9TwEC3ebeLtl8EgyQEEwleWKShyPhUHPqgIWzWEq', 'Admin', 'User',
        'ADMIN', true, 'LOCAL');

INSERT INTO app_users (email, password, first_name, last_name, role, email_verified, auth_provider)
VALUES ('student@example.com', '{bcrypt}$2a$10$dYT5CMj6KyNNK9TwEC3ebeLtl8EgyQEEwleWKShyPhUHPqgIWzWEq', 'Student',
        'User', 'STUDENT', true, 'LOCAL');

INSERT INTO app_users (email, password, first_name, last_name, role, email_verified, auth_provider)
VALUES ('pro@example.com', '{bcrypt}$2a$10$dYT5CMj6KyNNK9TwEC3ebeLtl8EgyQEEwleWKShyPhUHPqgIWzWEq', 'Pro', 'User',
        'PROFESSIONAL', true, 'LOCAL');

INSERT INTO app_users (email, password, first_name, last_name, role, email_verified, auth_provider)
VALUES ('guest@example.com', '{bcrypt}$2a$10$dYT5CMj6KyNNK9TwEC3ebeLtl8EgyQEEwleWKShyPhUHPqgIWzWEq', 'Guest', 'User',
        'GUEST', true, 'LOCAL');


-- ============================================================================
-- 1. CATALOGUE : Modules (Munin Atlas & Hugin Lab)
-- ============================================================================

-- Atlas (Apprentissage)
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Bactériologie', 'MUNIN_BACTERIO', 'bacteriologie', 'bug', 'Étude des bactéries et identification',
        '/atlas/bacteriology', 'MUNIN_ATLAS', 0.0, true);
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Mycologie', 'MUNIN_MYCO', 'mycologie', 'leaf', 'Science des champignons et levures', '/atlas/mycology',
        'MUNIN_ATLAS', 0.0, true);
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Virologie', 'MUNIN_VIRO', 'virologie', 'dna', 'Étude des virus', '/atlas/virology', 'MUNIN_ATLAS', 0.0, true);
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Immunologie', 'MUNIN_IMMUNO', 'immunologie', 'shield', 'Système immunitaire et défenses', '/atlas/immunology',
        'MUNIN_ATLAS', 0.0, true);

-- Labo (Outils Pro)
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('LIMS Pro', 'HUGIN_LIMS', 'lims', 'flask-conical', 'Système de gestion d''information de laboratoire',
        '/lab/lims', 'HUGIN_LAB', 29.99, true);
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Inventaire', 'HUGIN_INVENTORY', 'inventaire', 'package', 'Gestion des stocks et réactifs', '/lab/inventory',
        'HUGIN_LAB', 14.99, true);

-- Modules de messagerie intégrer
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Messagerie', 'HUGIN_CHAT', 'messagerie', 'message-circle',
        'Serveurs de chat et d''envoie de fichiers', '/lab/chat',
        'HUGIN_LAB', (9.99), true);

-- Module de visioconférence
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Visioconférence', 'HUGIN_MEETING', 'visio', 'headset',
        'Appels audio, vidéo et partage d''écran en temps réel', '/lab/meeting',
        'HUGIN_LAB', 19.99, true);

-- Module de mailing (admin)
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('E-mail', 'HUGIN_MAIL', 'mail', 'mail',
        'Client de messagerie professionnel et gestion de boites mail', '/lab/mail',
        'HUGIN_LAB', 12.99, true);

-- Module d'emploi du temps
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Calendrier', 'HUGIN_CALENDAR', 'agenda', 'calendar',
        'Emploi du temps partagé et planification d''événements', '/lab/calendar',
        'HUGIN_LAB', 7.99, true);

-- Module de réservation de salles
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Réservations', 'HUGIN_BOOKING', 'reservations', 'calendar-check',
        'Gestion des salles, incubateurs et équipements partagés', '/lab/booking',
        'HUGIN_LAB', 14.99, true);

-- Module de notes collaboratives
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Notes', 'HUGIN_NOTES', 'notes', 'notebook-pen',
        'Prise de notes, cahier de labo et documents collaboratifs', '/lab/notes',
        'HUGIN_LAB', 7.99, true);

-- Module de partage rapide de fichiers
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('QuickShare', 'HUGIN_QUICKSHARE', 'quickshare', 'share-2',
        'Partage instantané de fichiers et données entre collaborateurs', '/lab/quickshare',
        'HUGIN_LAB', 0.0, true);

-- Module de feedback et signalement
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Support', 'HUGIN_SUPPORT', 'support', 'life-buoy',
        'Signalement de bugs, suggestions et contact avec l''équipe OLS', '/lab/support',
        'HUGIN_LAB', 0.0, true);

-- Module de gestion des notifications
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Notifications', 'HUGIN_NOTIFICATIONS', 'notifications', 'bell',
        'Historique et préférences de notifications par module', '/lab/notifications',
        'HUGIN_LAB', 0.0, true);

-- Module de carnet de contacts
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Contacts', 'HUGIN_CONTACTS', 'contacts', 'contact-round',
        'Carnet de contacts professionnels et collaborateurs de laboratoire', '/lab/contacts',
        'HUGIN_LAB', 0.0, true);

-- Module d'organisations
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Organisations', 'HUGIN_ORGANIZATIONS', 'organization', 'building-2',
        'Gestion des organisations, membres et relations de supervision', '/lab/organization',
        'HUGIN_LAB', 0.0, true);

-- Module d'annotations
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Annotations', 'HUGIN_ANNOTATIONS', 'annotations', 'sticky-note',
        'Annotations personnelles sur les entites (bacteries, champignons, contacts, etc.)', '/lab/annotations',
        'HUGIN_LAB', 0.0, true);

-- Module de collections d''etude
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active)
VALUES ('Collections d''etude', 'HUGIN_STUDY_COLLECTIONS', 'study-collections', 'library-big',
        'Playlists d''etude cross-modules pour organiser vos revisions', '/lab/study-collections',
        'HUGIN_LAB', 0.0, true);


-- ============================================================================
-- 1.5 ACCÈS AUX MODULES (User <-> Module)
-- ============================================================================
-- Note: Les modules gratuits (price=0) sont accessibles à tous
-- Note: ADMIN a accès à tout par défaut (logique dans ModuleAccessService)

-- Le user "pro" (id=3) a acheté le module LIMS (id=5)
INSERT INTO user_module_access (user_id, module_id, has_access, purchased_at)
VALUES (3, 5, true, CURRENT_TIMESTAMP);

-- Le user "student" (id=2) a acheté le module Inventaire (id=6)
INSERT INTO user_module_access (user_id, module_id, has_access, purchased_at)
VALUES (2, 6, true, CURRENT_TIMESTAMP);


-- ============================================================================
-- 2. BACTÉRIOLOGIE : Données Scientifiques
-- ============================================================================

-- Bactérie 1 : E. Coli (Gram-, Bacille)
INSERT INTO bacteria (id, species, strain, gram, morpho, genome_size, mlst, pathogenicity, habitat, maldi_profile,
                      catalase, oxydase, lactose, indole, mannitol, mobilite, hemolyse)
VALUES (1, 'Escherichia coli', 'O157:H7', 'NEGATIVE', 'BACILLI', 5.5, 'ST11', 'élevée', 'intestinal', 'distinct', true,
        false, true, true, true, true, 'GAMMA');

-- Bactérie 2 : S. Aureus (Gram+, Cocci)
INSERT INTO bacteria (id, species, strain, gram, morpho, genome_size, mlst, pathogenicity, habitat, maldi_profile,
                      catalase, oxydase, coagulase, mannitol, mobilite, hemolyse)
VALUES (2, 'Staphylococcus aureus', 'MRSA USA300', 'POSITIVE', 'COCCI', 2.8, 'ST8', 'très élevée', 'cutané/nasal',
        'caractéristique', true, false, true, true, false, 'BETA');

-- Bactérie 3 : K. Pneumoniae (Gram-, Bacille, BMR)
INSERT INTO bacteria (id, species, strain, gram, morpho, genome_size, mlst, pathogenicity, habitat, maldi_profile,
                      catalase, oxydase, lactose, indole, mannitol, mobilite)
VALUES (3, 'Klebsiella pneumoniae', 'KPC-2', 'NEGATIVE', 'BACILLI', 5.3, 'ST258', 'très élevée', 'hospitalier',
        'distinct', true, false, true, false, false, false);


-- ============================================================================
-- 3. LISTES (Plasmides, Gènes, Facteurs, Codes API)
-- Tables générées automatiquement par @ElementCollection
-- ============================================================================

-- Codes API Bactéries (nouvelle table @ElementCollection avec galerie)
-- E. Coli : différentes galeries selon profil biochimique testé
INSERT INTO bacterium_api_codes (bacterium_id, gallery, code)
VALUES (1, 'API 20 E', '5144572');
INSERT INTO bacterium_api_codes (bacterium_id, gallery, code)
VALUES (1, 'API 20 E', '5044572');
-- S. Aureus : galerie spécifique Staphylocoques
INSERT INTO bacterium_api_codes (bacterium_id, gallery, code)
VALUES (2, 'API Staph', '6706113');
INSERT INTO bacterium_api_codes (bacterium_id, gallery, code)
VALUES (2, 'API ID 32 Staph', '0006311');
-- K. Pneumoniae : entérobactéries, deux galeries possibles
INSERT INTO bacterium_api_codes (bacterium_id, gallery, code)
VALUES (3, 'API 20 E', '5215773');
INSERT INTO bacterium_api_codes (bacterium_id, gallery, code)
VALUES (3, 'API 20 NE', '5205773');

-- E. Coli (ID 1)
INSERT INTO bacterium_plasmids (bacterium_id, plasmid_name)
VALUES (1, 'pO157');
INSERT INTO bacterium_plasmids (bacterium_id, plasmid_name)
VALUES (1, 'pO111');
INSERT INTO bacterium_resistance_genes (bacterium_id, gene_code)
VALUES (1, 'blaCTX-M-15');
INSERT INTO bacterium_resistance_genes (bacterium_id, gene_code)
VALUES (1, 'aac(6'')-Ib-cr');
INSERT INTO bacterium_virulence_factors (bacterium_id, virulence_factor)
VALUES (1, 'stx1');
INSERT INTO bacterium_virulence_factors (bacterium_id, virulence_factor)
VALUES (1, 'stx2');
INSERT INTO bacterium_virulence_factors (bacterium_id, virulence_factor)
VALUES (1, 'eae');
INSERT INTO bacterium_snp_signatures (bacterium_id, snp_marker)
VALUES (1, 245);
INSERT INTO bacterium_snp_signatures (bacterium_id, snp_marker)
VALUES (1, 892);
INSERT INTO bacterium_snp_signatures (bacterium_id, snp_marker)
VALUES (1, 1234);

-- S. Aureus (ID 2)
INSERT INTO bacterium_plasmids (bacterium_id, plasmid_name)
VALUES (2, 'pUSA03');
INSERT INTO bacterium_resistance_genes (bacterium_id, gene_code)
VALUES (2, 'mecA');
INSERT INTO bacterium_resistance_genes (bacterium_id, gene_code)
VALUES (2, 'tetK');
INSERT INTO bacterium_resistance_genes (bacterium_id, gene_code)
VALUES (2, 'ermC');
INSERT INTO bacterium_virulence_factors (bacterium_id, virulence_factor)
VALUES (2, 'pvl');
INSERT INTO bacterium_virulence_factors (bacterium_id, virulence_factor)
VALUES (2, 'sea');
INSERT INTO bacterium_virulence_factors (bacterium_id, virulence_factor)
VALUES (2, 'tst');
INSERT INTO bacterium_snp_signatures (bacterium_id, snp_marker)
VALUES (2, 89);
INSERT INTO bacterium_snp_signatures (bacterium_id, snp_marker)
VALUES (2, 456);
INSERT INTO bacterium_snp_signatures (bacterium_id, snp_marker)
VALUES (2, 1890);

-- K. Pneumoniae (ID 3)
INSERT INTO bacterium_plasmids (bacterium_id, plasmid_name)
VALUES (3, 'pKPC-2');
INSERT INTO bacterium_plasmids (bacterium_id, plasmid_name)
VALUES (3, 'pNDM');
INSERT INTO bacterium_resistance_genes (bacterium_id, gene_code)
VALUES (3, 'blaKPC-2');
INSERT INTO bacterium_resistance_genes (bacterium_id, gene_code)
VALUES (3, 'blaOXA-48');
INSERT INTO bacterium_resistance_genes (bacterium_id, gene_code)
VALUES (3, 'blaNDM-1');
INSERT INTO bacterium_virulence_factors (bacterium_id, virulence_factor)
VALUES (3, 'fimH');
INSERT INTO bacterium_virulence_factors (bacterium_id, virulence_factor)
VALUES (3, 'ycfM');


-- ============================================================================
-- 4. MYCOLOGIE : Données Scientifiques
-- ============================================================================

-- Champignon 1 : Saccharomyces cerevisiae (Levure, Fermentation)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (1, 'Saccharomyces cerevisiae', 'LEVURES', 'FERMENTATION',
        'Levure de boulanger et de bière, organisme modèle en biologie cellulaire',
        'Surfaces de fruits, moûts de fermentation', 'Cellules ovoïdes, reproduction par bourgeonnement', 32.5, 40.0,
        'Boulangerie, brasserie, vinification, production d''éthanol, recherche scientifique',
        'Fermentation alcoolique (anaérobie), respiration aérobie',
        'Non pathogène, généralement reconnu comme sûr (GRAS)', 'Malt, glucose, milieu synthétique minimal', true,
        false, false, false, 'Bourgeonnement', NULL, 'Très faible');

-- Champignon 2 : Candida albicans (Levure, Pathogènes)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (2, 'Candida albicans', 'LEVURES', 'PATHOGENES', 'Levure commensale pouvant devenir pathogène opportuniste',
        'Muqueuses (bouche, tractus digestif, vagin), peau', 'Dimorphique: forme levure et forme filamenteuse (hyphes)',
        37.0, 42.0, 'Études de pathogénicité, modèle de dimorphisme fongique',
        'Fermentation et respiration, switch morphologique selon conditions',
        'Candidoses (muguet, vaginite), infections systémiques chez immunodéprimés', 'Sabouraud dextrose, YPD', true,
        true, false, false, 'Bourgeonnement et pseudohyphes', NULL, 'Modérée');

-- Champignon 3 : Aspergillus niger (Moisissure, Culture)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (3, 'Aspergillus niger', 'MOISISSURES', 'CULTURE',
        'Moisissure noire commune, importante en biotechnologie industrielle',
        'Sol, matière organique en décomposition, aliments',
        'Mycélium filamenteux, conidiophores noirs caractéristiques', 35.0, 47.0,
        'Production d''acide citrique, enzymes (amylases, pectinases), fermentation industrielle',
        'Aérobie strict, dégrade nombreux substrats organiques',
        'Rarement pathogène, peut causer aspergilloses chez immunodéprimés', 'PDA, milieu Czapek', true, false, false,
        true, 'Conidiation asexuée', 'Ochratoxine (faible)', 'Modérée');

-- Champignon 4 : Penicillium chrysogenum (Moisissure, Médicinaux)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (4, 'Penicillium chrysogenum', 'MOISISSURES', 'MEDICINAUX',
        'Moisissure productrice de pénicilline, révolution de la médecine moderne',
        'Sol, aliments (fromages), matière organique', 'Mycélium vert-bleu, conidiophores en pinceau (penicillus)',
        25.0, 30.0, 'Production de pénicilline et autres antibiotiques beta-lactames, affinage des fromages',
        'Aérobie, production de métabolites secondaires dont antibiotiques',
        'Non pathogène généralement, allergies possibles', 'Milieu Czapek, PDA', true, false, false, false,
        'Conidiation asexuée', NULL, 'Élevée (allergie à la pénicilline)');

-- Champignon 5 : Agaricus bisporus (Champignon filamenteux, Comestible)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (5, 'Agaricus bisporus', 'CHAMPIGNONS_FILAMENTEUX', 'COMESTIBLES',
        'Champignon de Paris, espèce la plus cultivée au monde', 'Sol riche en matière organique, fumier de cheval',
        'Carpophore avec chapeau blanc-beige, lamelles roses puis brunes', 15.0, 18.0,
        'Alimentation (champignon blanc, champignon brun, portobello), culture commerciale',
        'Saprophyte, décompose matière organique', 'Non pathogène, comestible et nutritif', 'Compost fumier stérilisé',
        true, false, false, false, 'Reproduction sexuée (basidial)', NULL, 'Très faible');

-- Champignon 6 : Amanita phalloides (Champignon filamenteux, Toxique)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (6, 'Amanita phalloides', 'CHAMPIGNONS_FILAMENTEUX', 'TOXIQUES',
        'Amanite phalloïde, champignon mortel responsable de 90% des intoxications fatales',
        'Forêts de feuillus (chênes, châtaigniers), mycorhizien',
        'Chapeau verdâtre, anneau blanc, volve à la base du pied', 15.0, 25.0,
        'Aucune (toxique), études toxicologiques', 'Mycorhizien (symbiose avec arbres)',
        'Hautement toxique: amatoxines causant nécrose hépatique fatale',
        'Milieu de culture spécialisé en lab seulement', true, false, false, false, 'Reproduction sexuée',
        'Amatoxines (très toxiques)', 'Extrême');

-- Champignon 7 : Ganoderma lucidum (Champignon filamenteux, Médicinal)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (7, 'Ganoderma lucidum', 'CHAMPIGNONS_FILAMENTEUX', 'MEDICINAUX',
        'Reishi, champignon médicinal utilisé depuis 2000 ans en médecine traditionnelle',
        'Bois mort de feuillus, souches', 'Carpophore en forme de rein, surface vernie rouge-brun', 25.0, 30.0,
        'Médecine traditionnelle, compléments alimentaires, immunomodulation, antioxydants',
        'Saprophyte lignicole (décompose le bois)', 'Non pathogène, propriétés médicinales', 'Bois dur, PDA', true,
        false, false, true, 'Reproduction sexuée', NULL, 'Très faible');

-- Champignon 8 : Cryptococcus neoformans (Levure, Pathogènes)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (8, 'Cryptococcus neoformans', 'LEVURES', 'PATHOGENES',
        'Levure encapsulée causant la cryptococcose, infection opportuniste grave',
        'Fientes d''oiseaux (pigeons), sol, bois en décomposition',
        'Levure encapsulée (capsule polysaccharidique épaisse), bourgeonnement', 35.0, 40.0,
        'Recherche sur infections fongiques, modèle de pathogénicité', 'Aérobie, production de mélanine',
        'Méningite cryptococcique chez immunodéprimés (VIH/SIDA), infections pulmonaires',
        'Sabouraud dextrose, milieu Niger seed', true, true, true, true, 'Bourgeonnement', NULL, 'Modérée');

-- Champignon 9 : Rhizopus stolonifer (Moisissure, Culture)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (9, 'Rhizopus stolonifer', 'MOISISSURES', 'CULTURE',
        'Moisissure du pain, champignon zygomycète à croissance rapide',
        'Aliments (pain, fruits), sol, matière organique',
        'Mycélium blanc à grisâtre, sporanges noirs sur sporangiophores', 25.0, 30.0,
        'Production de tempeh (fermentation soja), études de croissance fongique',
        'Saprophyte, fermentation et respiration', 'Rarement pathogène, mucormycose chez immunodéprimés sévères',
        'PDA, milieu Czapek', true, false, false, false, 'Sporulation asexuée et sexuée', NULL, 'Faible');

-- Champignon 10 : Claviceps purpurea (Champignon filamenteux, Toxique)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (10, 'Claviceps purpurea', 'CHAMPIGNONS_FILAMENTEUX', 'TOXIQUES',
        'Ergot du seigle, champignon parasite produisant des alcaloïdes toxiques',
        'Parasite des graminées (seigle, blé, orge)', 'Sclérotes noirs (ergots) remplaçant les grains de céréales',
        15.0, 25.0, 'Production d''alcaloïdes médicinaux (ergotamine, ergométrine), recherche historique',
        'Parasite obligatoire des graminées', 'Ergotisme (feu de Saint-Antoine): convulsions, gangrène, hallucinations',
        'Culture spécialisée en laboratoire', true, false, false, false, 'Reproduction asexuée et sexuée',
        'Ergotamine, ergométrine (alcaloïdes)', 'Modérée');

-- Champignon 11 : Trichoderma reesei (Moisissure, Culture)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (11, 'Trichoderma reesei', 'MOISISSURES', 'CULTURE',
        'Moisissure industrielle, champion de la production de cellulases', 'Sol tropical, bois en décomposition',
        'Mycélium vert, conidies abondantes', 28.0, 35.0,
        'Production industrielle d''enzymes (cellulases, hémicellulases), biocarburants, textile',
        'Saprophyte, sécrète enzymes hydrolytiques puissantes', 'Non pathogène, utilisé en biocontrôle',
        'Milieu Czapek, PDA', true, false, false, false, 'Conidiation asexuée', NULL, 'Très faible');

-- Champignon 12 : Pleurotus ostreatus (Champignon filamenteux, Comestible)
INSERT INTO fungi (id, species, type, category, description, habitat, morphology, optimal_temperature,
                   maximal_temperature, applications, metabolism, pathogenicity, culture_medium, aerobic, dimorphic,
                   encapsulated, melanin_producer, reproduction, toxins, allergens)
VALUES (12, 'Pleurotus ostreatus', 'CHAMPIGNONS_FILAMENTEUX', 'COMESTIBLES',
        'Pleurote en huître, champignon comestible facile à cultiver', 'Bois mort de feuillus (hêtres, peupliers)',
        'Chapeau en forme d''huître, gris-beige, lamelles décurrentes', 13.0, 18.0,
        'Culture commerciale, mycoremédiation (dégradation polluants), alimentation',
        'Saprophyte lignicole, dégrade lignine et cellulose', 'Non pathogène, comestible et médicinal',
        'Sciure de bois stérilisée', true, false, false, false, 'Reproduction sexuée', NULL, 'Très faible');


-- ============================================================================
-- 5. LISTES MYCOLOGIE (Métabolites, Enzymes, Substrats, Hôtes, Codes API)
-- ============================================================================

-- Codes API Champignons (nouvelle table @ElementCollection avec galerie)
-- Levures : galeries API 20 C AUX et API ID 32 C
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (1, 'API 20 C AUX', 'SAC001');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (1, 'API 20 C AUX', '2100044');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (2, 'API 20 C AUX', '6152130');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (2, 'API ID 32 C', '2576174');
-- Moisissures (pas de galerie API standard, codes internes de référence)
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (3, 'Référence interne', 'ASP-NIG-001');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (4, 'Référence interne', 'PEN-CHR-001');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (5, 'Référence interne', 'AGA-BIS-001');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (6, 'Référence interne', 'AMA-PHA-001');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (7, 'Référence interne', 'GAN-LUC-001');
-- Cryptococcus : levure pathogène avec galeries standards
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (8, 'API 20 C AUX', '2000040');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (8, 'API ID 32 C', '6776111');
-- Autres moisissures
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (9, 'Référence interne', 'RHI-STO-001');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (10, 'Référence interne', 'CLA-PUR-001');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (11, 'Référence interne', 'TRI-REE-001');
INSERT INTO fungus_api_codes (fungus_id, gallery, code)
VALUES (12, 'Référence interne', 'PLE-OST-001');

-- Saccharomyces cerevisiae (ID 1)
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (1, 'Invertase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (1, 'Alcool déshydrogénase');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (1, 'Éthanol');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (1, 'CO2');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (1, 'Glucose');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (1, 'Sucrose');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (1, 'Maltose');

-- Candida albicans (ID 2)
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (2, 'Protéase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (2, 'Lipase');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (2, 'Toxines');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (2, 'Antioxydants');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (2, 'Humains');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (2, 'Animaux');

-- Aspergillus niger (ID 3)
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (3, 'Amylase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (3, 'Pectinase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (3, 'Cellulase');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (3, 'Acide citrique');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (3, 'Amidon');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (3, 'Cellulose');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (3, 'Pectine');

-- Penicillium chrysogenum (ID 4)
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (4, 'Pénicilline acylase');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (4, 'Pénicilline G');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (4, 'Pénicilline V');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (4, 'Glucose');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (4, 'Phénylacétate');

-- Agaricus bisporus (ID 5)
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (5, 'Cellulase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (5, 'Ligninase');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (5, 'Lignine');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (5, 'Cellulose');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (5, 'Matière organique');

-- Amanita phalloides (ID 6)
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (6, 'Amatoxines');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (6, 'Phalloidine');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (6, 'Chênes');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (6, 'Châtaigniers');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (6, 'Hêtres');

-- Ganoderma lucidum (ID 7)
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (7, 'Laccase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (7, 'Manganèse peroxydase');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (7, 'Polysaccharides');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (7, 'Triterpènes');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (7, 'Antioxydants');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (7, 'Bois dur');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (7, 'Feuillus');

-- Cryptococcus neoformans (ID 8)
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (8, 'Mélanine');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (8, 'Capsule polysaccharidique');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (8, 'Laccase');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (8, 'Pigeons');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (8, 'Humains (immunodéprimés)');

-- Rhizopus stolonifer (ID 9)
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (9, 'Amylase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (9, 'Lipase');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (9, 'Amidon');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (9, 'Lipides');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (9, 'Protéines');

-- Claviceps purpurea (ID 10)
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (10, 'Ergotamine');
INSERT INTO fungus_metabolites (fungus_id, metabolite)
VALUES (10, 'Ergométrine');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (10, 'Seigle');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (10, 'Blé');
INSERT INTO fungus_hosts (fungus_id, host)
VALUES (10, 'Orge');

-- Trichoderma reesei (ID 11)
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (11, 'Cellulase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (11, 'Hémicellulose');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (11, 'Xylanase');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (11, 'Cellulose');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (11, 'Hémicellulose');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (11, 'Lignine');

-- Pleurotus ostreatus (ID 12)
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (12, 'Laccase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (12, 'Manganèse peroxydase');
INSERT INTO fungus_enzymes (fungus_id, enzyme)
VALUES (12, 'Cellulase');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (12, 'Bois');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (12, 'Cellulose');
INSERT INTO fungus_degradable_substrates (fungus_id, substrate)
VALUES (12, 'Lignine');


-- ============================================================================
-- 6. CONTACTS : Données de test
-- ============================================================================
-- Rappel users: admin=1, student=2, pro=3, guest=4

-- Contacts du compte admin (id=1)
INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Marie', 'Dupont', 'marie.dupont@pasteur.fr', '+33 1 45 68 80 00', 'Institut Pasteur', 'Chercheuse en microbiologie',
        'Spécialiste des résistances aux antibiotiques. Collabore sur le projet MRSA.', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Jean-Pierre', 'Martin', 'jp.martin@inserm.fr', '+33 1 44 23 60 00', 'INSERM U1135', 'Directeur de recherche',
        'Responsable unité immunologie. Contact pour les collaborations inter-labos.', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Sophie', 'Bernard', 'sophie.bernard@chu-lyon.fr', '+33 4 72 11 02 00', 'CHU Lyon', 'Biologiste médicale',
        'Référente antibiogrammes. Envoie les souches pour séquençage.', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Thomas', 'Lefèvre', 'thomas.lefevre@cnrs.fr', '+33 1 44 96 40 00', 'CNRS UMR 7245', 'Post-doctorant',
        'Travaille sur les biofilms bactériens. Thèse soutenue en 2024.', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Claire', 'Moreau', 'claire.moreau@univ-paris.fr', '+33 1 57 27 80 00', 'Université Paris Cité', 'Maître de conférences',
        'Enseigne la mycologie médicale. Co-autrice article Candida albicans.', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Ahmed', 'Benali', 'ahmed.benali@ap-hm.fr', '+33 4 91 38 00 00', 'AP-HM Marseille', 'Interne en biologie',
        'Stagiaire de 6ème année. Suit le protocole PCR multiplex.', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Émilie', 'Rousseau', 'emilie.rousseau@biomerieux.com', '+33 4 78 87 20 00', 'bioMérieux', 'Ingénieure R&D',
        'Contact technique pour les galeries API. Réf. commerciale: BM-2024-0456.', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Lucas', 'Petit', 'student@example.com', '+33 6 12 34 56 78', 'OLS', 'Étudiant',
        'Utilisateur étudiant de la plateforme OLS.', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Contacts du compte pro (id=3)
INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Admin', 'User', 'admin@example.com', NULL, 'OLS', 'Administrateur',
        'Administrateur de la plateforme.', true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Nathalie', 'Garcia', 'nathalie.garcia@ihu-lyon.fr', '+33 4 72 07 10 00', 'IHU Lyon', 'Technicienne de laboratoire',
        'Gère les cultures sur milieux chromogènes.', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Contact du compte student (id=2)
INSERT INTO contacts (first_name, last_name, email, phone, organization, job_title, notes, favorite, owner_id, created_at, updated_at)
VALUES ('Pro', 'User', 'pro@example.com', NULL, 'OLS', 'Professionnel',
        'Utilisateur professionnel de la plateforme.', true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ============================================================================
-- 7. NOTES : Cahier de laboratoire
-- ============================================================================

-- Notes du compte admin (id=1)
INSERT INTO notes (title, content, color, pinned, tags, owner_id, created_at, updated_at)
VALUES ('Protocole PCR multiplex - S. aureus',
        'Protocole optimisé pour la détection simultanée des gènes mecA, pvl et tst chez S. aureus.

## Réactifs
- Master Mix 2X (Qiagen)
- Amorces mecA-F/R (10 µM)
- Amorces pvl-F/R (10 µM)
- Amorces tst-F/R (10 µM)
- Eau nucléase-free

## Programme thermocycleur
1. Dénaturation initiale : 95°C - 5 min
2. 35 cycles : 95°C/30s -> 58°C/30s -> 72°C/45s
3. Extension finale : 72°C - 7 min

## Notes
- Témoins positifs : ATCC 43300 (mecA+), ATCC BAA-1556 (pvl+)
- Taille attendue : mecA=310pb, pvl=433pb, tst=180pb',
        'BLUE', true, 'pcr,protocole,s-aureus,mrsa', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (title, content, color, pinned, tags, owner_id, created_at, updated_at)
VALUES ('Résultats antibiogramme - Lot 2024-03',
        'Antibiogrammes réalisés le 15/03/2024 sur 12 souches de K. pneumoniae.

## Résumé
- 8/12 souches sensibles aux carbapénèmes
- 4/12 souches productrices de KPC-2 (confirmé par PCR)
- 2/12 souches co-productrices OXA-48

## CMI (µg/mL)
| Souche | Imipénème | Méropénème | Ertapénème |
|--------|-----------|------------|------------|
| KP-001 | 0.5       | 0.25       | 0.5        |
| KP-002 | >16       | >16        | >16        |
| KP-003 | 8         | 4          | >16        |

## Action
- Signaler les 4 BMR au CLIN
- Envoyer au CNR pour typage moléculaire',
        'RED', true, 'antibiogramme,klebsiella,bmr,résultats', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (title, content, color, pinned, tags, owner_id, created_at, updated_at)
VALUES ('Culture fongique - C. albicans isolat #42',
        'Isolat prélevé sur patient immunodéprimé (service hématologie).

## Observation macroscopique (Sabouraud 48h, 37°C)
- Colonies crémeuses, blanches, lisses
- Odeur de levure caractéristique
- Croissance rapide (visible à 24h)

## Observation microscopique
- Levures ovoïdes bourgeonnantes
- Pseudofilaments présents (test de filamentation positif à 37°C dans sérum)
- Chlamydospores sur milieu RAT

## Identification
- API 20 C AUX : profil 6152130 -> C. albicans (99.8%)
- MALDI-TOF : score 2.31 -> C. albicans (haute confiance)',
        'GREEN', false, 'mycologie,candida,culture,identification', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (title, content, color, pinned, tags, owner_id, created_at, updated_at)
VALUES ('Réunion d''équipe - Points à aborder',
        '- Bilan des cultures de la semaine
- Mise à jour du stock de milieux de culture
- Planning des gardes du mois prochain
- Formation nouveau technicien sur MALDI-TOF
- Présentation article Lancet Microbe (Marie Dupont)',
        'YELLOW', false, 'réunion,organisation,planning', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (title, content, color, pinned, tags, owner_id, created_at, updated_at)
VALUES ('Maintenance MALDI-TOF Bruker',
        'Dernière maintenance préventive : 01/03/2024
Prochaine maintenance prévue : 01/06/2024

## Checklist quotidienne
- [ ] Calibration avec BTS (Bacterial Test Standard)
- [ ] Vérifier le niveau de matrice (HCCA)
- [ ] Nettoyer la cible après chaque série
- [ ] Sauvegarder les spectres du jour

## Contact SAV
- bioMérieux SAV : 0 800 30 20 10
- Réf. contrat : MALDI-2024-FR-0891
- Technicien assigné : Marc Delattre',
        'PURPLE', false, 'maldi-tof,maintenance,équipement', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (title, content, color, pinned, tags, owner_id, created_at, updated_at)
VALUES ('Bibliographie - Résistances émergentes 2024',
        '## Articles à lire
1. "Emergence of NDM-1 in K. pneumoniae" - Lancet Infect Dis 2024
2. "MRSA community-acquired: new epidemiology" - Clin Microbiol Rev 2024
3. "Candida auris: global threat" - Nature Microbiology 2024

## Notes de lecture
- NDM-1 se répand via plasmides conjugatifs (IncL/M)
- Les SARM communautaires produisent plus souvent la PVL
- C. auris : résistance intrinsèque au fluconazole, mortalité 30-60%',
        'ORANGE', false, 'bibliographie,résistance,veille', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Notes du compte pro (id=3)
INSERT INTO notes (title, content, color, pinned, tags, owner_id, created_at, updated_at)
VALUES ('Inventaire réactifs - Mars 2024',
        '## Stock critique
- Milieu Sabouraud : 15 boîtes (seuil: 20) -> COMMANDER
- Gélose au sang : 45 boîtes (OK)
- Disques antibiotiques : lot 2024-06 (péremption: 09/2024)
- API 20 E : 8 galeries restantes -> COMMANDER
- Colorant de Gram : 2 flacons (OK)',
        'RED', true, 'inventaire,stock,commande', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (title, content, color, pinned, tags, owner_id, created_at, updated_at)
VALUES ('Procédure - Hémocultures positives',
        '1. Examen direct (Gram) dans les 15 min
2. Repiquage sur gélose au sang + chocolat + chromogène
3. Identification rapide par MALDI-TOF si possible
4. Antibiogramme en urgence (méthode disques)
5. Signaler au clinicien dans l''heure',
        'BLUE', false, 'procédure,hémoculture,urgence', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Note du compte student (id=2)
INSERT INTO notes (title, content, color, pinned, tags, owner_id, created_at, updated_at)
VALUES ('Cours - Coloration de Gram',
        '## Principe
Coloration différentielle basée sur la structure de la paroi bactérienne.

## Étapes
1. Violet de gentiane (1 min) -> colore toutes les bactéries
2. Lugol (1 min) -> fixe le colorant
3. Décoloration alcool (30 sec) -> décolore les Gram-
4. Fuchsine (30 sec) -> contre-coloration des Gram-

## Résultat
- Gram+ -> violet (paroi épaisse, peptidoglycane)
- Gram- -> rose (paroi fine, membrane externe)

## Exemples
- Gram+ : Staphylococcus, Streptococcus, Bacillus
- Gram- : E. coli, Klebsiella, Pseudomonas',
        'GREEN', true, 'cours,gram,bactériologie,bases', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ============================================================================
-- 8. NOTIFICATIONS : Historique
-- ============================================================================

-- Notifications pour admin (id=1)
INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('SYSTEM', 'Bienvenue sur OLS !',
        'Bienvenue sur Odin La Science. Explorez les modules Munin Atlas pour apprendre et Hugin Lab pour vos outils de laboratoire.',
        true, '/', NULL, 1, CURRENT_TIMESTAMP - INTERVAL '7 days');

INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('NEW_LOGIN', 'Nouvelle connexion détectée',
        'Connexion depuis un nouvel appareil : Chrome sur Windows 11 (Paris, FR).',
        true, '/settings', '{"deviceInfo": "Chrome 124 / Windows 11", "ipAddress": "82.65.12.34"}', 1, CURRENT_TIMESTAMP - INTERVAL '5 days');

INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('QUICKSHARE_RECEIVED', 'Nouveau partage reçu',
        'Pro User vous a partagé "Résultats séquençage lot B".',
        false, '/lab/quickshare', '{"shareCode": "xK9mQ2pL", "senderName": "Pro User"}', 1, CURRENT_TIMESTAMP - INTERVAL '2 days');

INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('SUPPORT_REPLY', 'Réponse à votre ticket',
        'Un administrateur a répondu à votre ticket "Erreur d''affichage module Mycologie".',
        false, '/lab/support', '{"ticketId": 2, "ticketSubject": "Erreur d''affichage module Mycologie"}', 1, CURRENT_TIMESTAMP - INTERVAL '1 day');

INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('CONTACT_ADDED', 'Nouveau contact ajouté',
        'Émilie Rousseau (bioMérieux) a été ajoutée à vos contacts.',
        true, '/lab/contacts', '{"contactName": "Émilie Rousseau", "contactEmail": "emilie.rousseau@biomerieux.com"}', 1, CURRENT_TIMESTAMP - INTERVAL '3 days');

INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('MODULE_ACCESS_GRANTED', 'Accès module activé',
        'Vous avez maintenant accès au module "LIMS Pro".',
        true, '/lab/lims', '{"moduleKey": "HUGIN_LIMS", "moduleName": "LIMS Pro"}', 1, CURRENT_TIMESTAMP - INTERVAL '6 days');

INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('ORGANIZATION_INVITED', 'Invitation à une organisation',
        'Vous avez été invité à rejoindre "Laboratoire de Microbiologie - CHU Paris".',
        false, '/lab/organization', '{"organizationName": "Laboratoire de Microbiologie - CHU Paris", "role": "MANAGER"}', 1, CURRENT_TIMESTAMP - INTERVAL '12 hours');

-- Notifications pour student (id=2)
INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('SYSTEM', 'Bienvenue sur OLS !',
        'Bienvenue ! Commencez par explorer le module Bactériologie dans Munin Atlas.',
        true, '/atlas/bacteriology', NULL, 2, CURRENT_TIMESTAMP - INTERVAL '10 days');

INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('MODULE_ACCESS_GRANTED', 'Accès module activé',
        'Vous avez maintenant accès au module "Inventaire".',
        true, '/lab/inventory', '{"moduleKey": "HUGIN_INVENTORY", "moduleName": "Inventaire"}', 2, CURRENT_TIMESTAMP - INTERVAL '8 days');

INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('ORGANIZATION_INVITED', 'Invitation à une organisation',
        'Vous avez été invité à rejoindre "Université Paris Cité - L3 Microbiologie" en tant qu''étudiant.',
        false, '/lab/organization', '{"organizationName": "Université Paris Cité - L3 Microbiologie", "role": "INTERN"}', 2, CURRENT_TIMESTAMP - INTERVAL '4 days');

-- Notifications pour pro (id=3)
INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('SYSTEM', 'Bienvenue sur OLS !',
        'Bienvenue ! Découvrez les outils professionnels dans Hugin Lab.',
        true, '/lab', NULL, 3, CURRENT_TIMESTAMP - INTERVAL '15 days');

INSERT INTO notifications (type, title, message, is_read, action_url, metadata, recipient_id, created_at)
VALUES ('SUPPORT_STATUS_CHANGED', 'Ticket résolu',
        'Votre ticket "Import CSV ne fonctionne pas" a été marqué comme résolu.',
        true, '/lab/support', '{"ticketId": 3, "newStatus": "RESOLVED"}', 3, CURRENT_TIMESTAMP - INTERVAL '2 days');


-- ============================================================================
-- 9. QUICKSHARE : Partages rapides
-- ============================================================================

-- Partages du compte admin (id=1)
INSERT INTO shared_items (share_code, title, type, text_content, download_count, max_downloads, expires_at, owner_id, recipient_email, created_at)
VALUES ('aB3xK9mQ', 'Protocole extraction ADN', 'TEXT',
        'Protocole d''extraction d''ADN bactérien (kit Qiagen DNeasy Blood & Tissue)

1. Centrifuger 1 mL de culture overnight à 5000g / 10 min
2. Resuspendre le culot dans 180 µL de tampon ATL
3. Ajouter 20 µL de protéinase K, vortexer
4. Incuber à 56°C pendant 1h (agitation 300 rpm)
5. Ajouter 200 µL de tampon AL, vortexer 15 sec
6. Ajouter 200 µL d''éthanol (96-100%), vortexer
7. Transférer sur colonne DNeasy, centrifuger 6000g / 1 min
8. Laver avec 500 µL AW1, centrifuger
9. Laver avec 500 µL AW2, centrifuger 20000g / 3 min
10. Éluer avec 100 µL de tampon AE (préchauffé à 56°C)

Rendement attendu : 5-30 µg (NanoDrop A260/A280 > 1.8)',
        3, 10, CURRENT_TIMESTAMP + INTERVAL '30 days', 1, NULL, CURRENT_TIMESTAMP - INTERVAL '5 days');

INSERT INTO shared_items (share_code, title, type, text_content, download_count, max_downloads, expires_at, owner_id, recipient_email, created_at)
VALUES ('pQ7wR2nX', 'Liste antibiotiques testés', 'TEXT',
        'Panel antibiotiques - Entérobactéries (selon CA-SFM/EUCAST 2024)

Béta-lactamines : Amoxicilline, Amox/Ac.clav, Pipéracilline/Tazo, Céfoxitine, Céfotaxime, Ceftazidime, Ertapénème, Imipénème, Méropénème
Aminosides : Gentamicine, Tobramycine, Amikacine
Fluoroquinolones : Ciprofloxacine, Lévofloxacine
Autres : Cotrimoxazole, Fosfomycine, Nitrofurantoïne, Colistine (CMI uniquement)',
        7, NULL, NULL, 1, 'sophie.bernard@chu-lyon.fr', CURRENT_TIMESTAMP - INTERVAL '10 days');

INSERT INTO shared_items (share_code, title, type, text_content, download_count, max_downloads, expires_at, owner_id, recipient_email, created_at)
VALUES ('mN4vJ8kL', 'Résultats MALDI-TOF batch #47', 'TEXT',
        'Résultats identification MALDI-TOF - Batch #47 (20/03/2024)

| Échantillon | Espèce identifiée          | Score | Confiance |
|-------------|---------------------------|-------|-----------|
| B47-001     | Escherichia coli          | 2.41  | Haute     |
| B47-002     | Klebsiella pneumoniae     | 2.28  | Haute     |
| B47-003     | Pseudomonas aeruginosa    | 2.35  | Haute     |
| B47-004     | Staphylococcus aureus     | 2.52  | Haute     |
| B47-005     | Enterococcus faecalis     | 1.98  | Moyenne   |
| B47-006     | Non identifié             | 1.42  | Faible    |

B47-006 : repasser avec extraction protéique préalable.',
        1, 5, CURRENT_TIMESTAMP + INTERVAL '7 days', 1, NULL, CURRENT_TIMESTAMP - INTERVAL '2 days');

-- Partages du compte pro (id=3)
INSERT INTO shared_items (share_code, title, type, text_content, download_count, max_downloads, expires_at, owner_id, recipient_email, created_at)
VALUES ('xK9mQ2pL', 'Résultats séquençage lot B', 'TEXT',
        'Séquençage Sanger - Lot B (gène 16S rRNA)

Souche B-01 : Enterobacter cloacae complex (99.7% identité)
Souche B-02 : Citrobacter freundii (99.2% identité)
Souche B-03 : Serratia marcescens (99.9% identité)

Séquences déposées sur GenBank : en attente de numéros d''accession.
Fichiers FASTA disponibles sur le serveur : /data/sequencing/lot-B/',
        0, NULL, NULL, 3, 'admin@example.com', CURRENT_TIMESTAMP - INTERVAL '2 days');

INSERT INTO shared_items (share_code, title, type, text_content, download_count, max_downloads, expires_at, owner_id, recipient_email, created_at)
VALUES ('hT5bY1cF', 'Commande fournisseur Sigma-Aldrich', 'TEXT',
        'Commande réactifs - Réf. interne CMD-2024-0089

- Agar bactériologique (réf. A1296) x5
- Bouillon Luria-Bertani (réf. L3522) x3
- IPTG (réf. I6758) x1
- X-Gal (réf. B4252) x1
- Ampicilline sodium (réf. A0166) x2

Total estimé : 342.50 EUR HT
Livraison estimée : 3-5 jours ouvrés',
        2, NULL, CURRENT_TIMESTAMP + INTERVAL '14 days', 3, NULL, CURRENT_TIMESTAMP - INTERVAL '4 days');


-- ============================================================================
-- 10. SUPPORT : Tickets et messages
-- ============================================================================

-- Ticket 1 : Bug reporté par student (id=2) - RESOLVED avec conversation
INSERT INTO support_tickets (subject, description, category, priority, status, owner_id, created_at, updated_at)
VALUES ('Impossible de charger les galeries API en bactériologie',
        'Quand je clique sur "Identifier par code API" dans le module bactériologie, la page reste bloquée sur le spinner de chargement. J''ai essayé avec Chrome et Firefox, même résultat. Le reste du module fonctionne normalement.',
        'BUG', 'HIGH', 'RESOLVED', 2, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '10 days');

INSERT INTO ticket_messages (content, is_admin, author_id, ticket_id, created_at)
VALUES ('Bonjour, merci pour le signalement. Pouvez-vous me donner le code API que vous essayez d''entrer ? Et quel navigateur utilisez-vous exactement (version) ?',
        true, 1, 1, CURRENT_TIMESTAMP - INTERVAL '13 days');

INSERT INTO ticket_messages (content, is_admin, author_id, ticket_id, created_at)
VALUES ('J''utilise Chrome 124.0.6367.91 sur Windows 11. J''ai essayé avec le code 5144572 (API 20 E pour E. coli). La console affiche une erreur 500.',
        false, 2, 1, CURRENT_TIMESTAMP - INTERVAL '13 days' + INTERVAL '2 hours');

INSERT INTO ticket_messages (content, is_admin, author_id, ticket_id, created_at)
VALUES ('Merci pour les détails. Le problème venait d''une erreur de parsing côté serveur. Le correctif a été déployé. Pouvez-vous réessayer ?',
        true, 1, 1, CURRENT_TIMESTAMP - INTERVAL '11 days');

INSERT INTO ticket_messages (content, is_admin, author_id, ticket_id, created_at)
VALUES ('Ça fonctionne parfaitement maintenant ! Merci pour la réactivité.',
        false, 2, 1, CURRENT_TIMESTAMP - INTERVAL '10 days');

-- Ticket 2 : Feature request par admin (id=1) - OPEN
INSERT INTO support_tickets (subject, description, category, priority, status, owner_id, created_at, updated_at)
VALUES ('Erreur d''affichage module Mycologie',
        'Les caractères spéciaux (accents) ne s''affichent pas correctement dans les descriptions des champignons. Par exemple "Amanite phalloïde" apparaît comme "Amanite phallo?de". Problème visible uniquement sur la page de détail, pas dans la liste.',
        'BUG', 'MEDIUM', 'IN_PROGRESS', 1, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day');

INSERT INTO ticket_messages (content, is_admin, author_id, ticket_id, created_at)
VALUES ('Nous avons identifié le problème : l''encodage UTF-8 n''est pas correctement appliqué sur certains champs TEXT. Un fix est en cours de développement.',
        true, 1, 2, CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Ticket 3 : Feature request par pro (id=3) - RESOLVED
INSERT INTO support_tickets (subject, description, category, priority, status, owner_id, created_at, updated_at)
VALUES ('Import CSV ne fonctionne pas',
        'Quand j''essaie d''importer un fichier CSV dans le module Inventaire, j''obtiens une erreur "Format non supporté" alors que le fichier est bien au format CSV avec séparateur point-virgule. Le fichier fait 2.3 Mo et contient 450 lignes.',
        'BUG', 'HIGH', 'RESOLVED', 3, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '2 days');

INSERT INTO ticket_messages (content, is_admin, author_id, ticket_id, created_at)
VALUES ('Bonjour, le module Inventaire attend un séparateur virgule (,) et non point-virgule (;). Nous allons ajouter le support des deux formats dans la prochaine mise à jour.',
        true, 1, 3, CURRENT_TIMESTAMP - INTERVAL '18 days');

INSERT INTO ticket_messages (content, is_admin, author_id, ticket_id, created_at)
VALUES ('D''accord, en attendant j''ai converti mon fichier. Mais ce serait bien de supporter le point-virgule car c''est le format par défaut d''Excel en France.',
        false, 3, 3, CURRENT_TIMESTAMP - INTERVAL '17 days');

INSERT INTO ticket_messages (content, is_admin, author_id, ticket_id, created_at)
VALUES ('La mise à jour a été déployée, les deux séparateurs sont maintenant supportés. Merci pour le retour !',
        true, 1, 3, CURRENT_TIMESTAMP - INTERVAL '2 days');

-- Ticket 4 : Question par student (id=2) - OPEN
INSERT INTO support_tickets (subject, description, category, priority, status, owner_id, created_at, updated_at)
VALUES ('Comment accéder au module LIMS ?',
        'Bonjour, je suis étudiant en L3 microbiologie et j''aimerais accéder au module LIMS Pro pour mon stage. Est-ce qu''il existe un tarif étudiant ou une licence académique ?',
        'QUESTION', 'LOW', 'OPEN', 2, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Ticket 5 : Feature request par pro (id=3) - OPEN
INSERT INTO support_tickets (subject, description, category, priority, status, owner_id, created_at, updated_at)
VALUES ('Suggestion : export PDF des fiches bactéries',
        'Ce serait très utile de pouvoir exporter les fiches détaillées des bactéries et champignons en PDF pour les intégrer dans nos rapports de laboratoire. Idéalement avec le logo OLS et la date d''export.',
        'FEATURE_REQUEST', 'MEDIUM', 'OPEN', 3, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '6 days');

INSERT INTO ticket_messages (content, is_admin, author_id, ticket_id, created_at)
VALUES ('Excellente suggestion ! Nous avons ajouté cette fonctionnalité à notre roadmap. Elle est prévue pour la version 2.1. Merci !',
        true, 1, 5, CURRENT_TIMESTAMP - INTERVAL '5 days');


-- ============================================================================
-- 11. ORGANISATIONS : Structures et membres
-- ============================================================================

-- Organisation 1 : Laboratoire créé par admin
INSERT INTO organizations (name, description, type, website, created_by_id, created_at, updated_at)
VALUES ('Laboratoire de Microbiologie - CHU Paris',
        'Unité de microbiologie clinique du CHU Paris. Diagnostic bactériologique, mycologique et virologique. Accrédité COFRAC ISO 15189.',
        'LABORATORY', 'https://chu-paris.fr/microbiologie', 1, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP);

-- Organisation 2 : Université créée par admin
INSERT INTO organizations (name, description, type, website, created_by_id, created_at, updated_at)
VALUES ('Université Paris Cité - L3 Microbiologie',
        'Promotion 2024 de la licence 3 Microbiologie. Cours, TP et stages de bactériologie et mycologie médicale.',
        'UNIVERSITY', 'https://u-paris.fr/microbiologie', 1, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP);

-- Organisation 3 : Centre de recherche créé par pro
INSERT INTO organizations (name, description, type, website, created_by_id, created_at, updated_at)
VALUES ('INSERM U1135 - Immunologie et Infections',
        'Unité de recherche INSERM dédiée à l''étude des interactions hôte-pathogène et à la résistance aux antimicrobiens.',
        'RESEARCH_CENTER', 'https://inserm.fr/u1135', 3, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP);

-- Memberships pour Organisation 1 (Labo CHU Paris)
-- Admin est OWNER
INSERT INTO organization_memberships (organization_id, user_id, role, status, joined_at, updated_at)
VALUES (1, 1, 'OWNER', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP);

-- Pro est MEMBER actif
INSERT INTO organization_memberships (organization_id, user_id, role, status, joined_at, updated_at)
VALUES (1, 3, 'MEMBER', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP);

-- Student est INTERN actif
INSERT INTO organization_memberships (organization_id, user_id, role, status, joined_at, updated_at)
VALUES (1, 2, 'INTERN', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP);

-- Memberships pour Organisation 2 (Université)
-- Admin est OWNER
INSERT INTO organization_memberships (organization_id, user_id, role, status, joined_at, updated_at)
VALUES (2, 1, 'OWNER', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP);

-- Student est INTERN
INSERT INTO organization_memberships (organization_id, user_id, role, status, joined_at, updated_at)
VALUES (2, 2, 'INTERN', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP);

-- Guest est invité (pas encore accepté)
INSERT INTO organization_memberships (organization_id, user_id, role, status, joined_at, updated_at)
VALUES (2, 4, 'INTERN', 'INVITED', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP);

-- Memberships pour Organisation 3 (INSERM)
-- Pro est OWNER
INSERT INTO organization_memberships (organization_id, user_id, role, status, joined_at, updated_at)
VALUES (3, 3, 'OWNER', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP);

-- Admin est MANAGER
INSERT INTO organization_memberships (organization_id, user_id, role, status, joined_at, updated_at)
VALUES (3, 1, 'MANAGER', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP);


-- ============================================================================
-- 12. SUPERVISIONS : Relations de supervision
-- ============================================================================

-- Dans le labo CHU Paris : admin supervise student
INSERT INTO supervision_relationships (organization_id, supervisor_id, supervisee_id, created_at)
VALUES (1, 1, 2, CURRENT_TIMESTAMP - INTERVAL '15 days');

-- Dans le labo CHU Paris : pro supervise student aussi (co-encadrement)
INSERT INTO supervision_relationships (organization_id, supervisor_id, supervisee_id, created_at)
VALUES (1, 3, 2, CURRENT_TIMESTAMP - INTERVAL '15 days');

-- Dans l'université : admin supervise student
INSERT INTO supervision_relationships (organization_id, supervisor_id, supervisee_id, created_at)
VALUES (2, 1, 2, CURRENT_TIMESTAMP - INTERVAL '20 days');

-- Dans INSERM : pro supervise admin (dans le cadre recherche)
INSERT INTO supervision_relationships (organization_id, supervisor_id, supervisee_id, created_at)
VALUES (3, 3, 1, CURRENT_TIMESTAMP - INTERVAL '18 days');


-- ============================================================================
-- 13. ACCÈS MODULES SUPPLÉMENTAIRES
-- ============================================================================
-- Rappel: admin a accès à tout par logique métier (ModuleAccessService)
-- Rappel: modules gratuits (price=0) accessibles à tous
-- Existant: pro(3) -> LIMS(5), student(2) -> Inventaire(6)

-- Student achète aussi le module Notes (id=12)
INSERT INTO user_module_access (user_id, module_id, has_access, purchased_at)
VALUES (2, 12, true, CURRENT_TIMESTAMP - INTERVAL '5 days');

-- Pro achète aussi Messagerie (id=7), Calendrier (id=10), Notes (id=12)
INSERT INTO user_module_access (user_id, module_id, has_access, purchased_at)
VALUES (3, 7, true, CURRENT_TIMESTAMP - INTERVAL '10 days');
INSERT INTO user_module_access (user_id, module_id, has_access, purchased_at)
VALUES (3, 10, true, CURRENT_TIMESTAMP - INTERVAL '8 days');
INSERT INTO user_module_access (user_id, module_id, has_access, purchased_at)
VALUES (3, 12, true, CURRENT_TIMESTAMP - INTERVAL '12 days');