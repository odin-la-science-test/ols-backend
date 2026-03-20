-- ============================================================================
-- 0. UTILISATEURS DE TEST
-- ============================================================================
-- Mot de passe: 1234 (hashé avec BCrypt, préfixé {bcrypt} pour DelegatingPasswordEncoder)

INSERT INTO app_users (email, password, first_name, last_name, role)
VALUES ('admin@example.com', '{bcrypt}$2a$10$dYT5CMj6KyNNK9TwEC3ebeLtl8EgyQEEwleWKShyPhUHPqgIWzWEq', 'Admin', 'User',
        'ADMIN');

INSERT INTO app_users (email, password, first_name, last_name, role)
VALUES ('student@example.com', '{bcrypt}$2a$10$dYT5CMj6KyNNK9TwEC3ebeLtl8EgyQEEwleWKShyPhUHPqgIWzWEq', 'Student',
        'User', 'STUDENT');

INSERT INTO app_users (email, password, first_name, last_name, role)
VALUES ('pro@example.com', '{bcrypt}$2a$10$dYT5CMj6KyNNK9TwEC3ebeLtl8EgyQEEwleWKShyPhUHPqgIWzWEq', 'Pro', 'User',
        'PROFESSIONAL');

INSERT INTO app_users (email, password, first_name, last_name, role)
VALUES ('guest@example.com', '{bcrypt}$2a$10$dYT5CMj6KyNNK9TwEC3ebeLtl8EgyQEEwleWKShyPhUHPqgIWzWEq', 'Guest', 'User',
        'GUEST');


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

-- Module d'administration du support (admin uniquement)
INSERT INTO modules (title, module_key, slug, icon, description, route_path, type, price, active, admin_only)
VALUES ('Admin Support', 'HUGIN_ADMIN_SUPPORT', 'admin-support', 'shield-check',
        'Gestion et traitement des tickets de support utilisateurs', '/lab/admin/support',
        'HUGIN_LAB', 0.0, true, true);

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
        false, true, true, true, true, 'gamma');

-- Bactérie 2 : S. Aureus (Gram+, Cocci)
INSERT INTO bacteria (id, species, strain, gram, morpho, genome_size, mlst, pathogenicity, habitat, maldi_profile,
                      catalase, oxydase, coagulase, mannitol, mobilite, hemolyse)
VALUES (2, 'Staphylococcus aureus', 'MRSA USA300', 'POSITIVE', 'COCCI', 2.8, 'ST8', 'très élevée', 'cutané/nasal',
        'caractéristique', true, false, true, true, false, 'beta');

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