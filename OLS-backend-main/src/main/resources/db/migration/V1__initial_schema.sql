-- ==============================================================================
-- V1 : Schema initial OLS
-- Genere a partir des entites JPA (Hibernate)
-- ==============================================================================

-- ============================================================================
-- USERS & AUTH
-- ============================================================================

CREATE TABLE app_users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255),
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    role            VARCHAR(255) DEFAULT 'GUEST',
    avatar_id       VARCHAR(255),
    auth_provider   VARCHAR(255) DEFAULT 'LOCAL',
    external_id     VARCHAR(255),
    email_verified  BOOLEAN DEFAULT FALSE
);

CREATE TABLE user_preferences (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL UNIQUE REFERENCES app_users(id),
    preferences_json  TEXT,
    last_modified     TIMESTAMP WITH TIME ZONE,
    version           INTEGER DEFAULT 0
);

CREATE TABLE user_sessions (
    id                 UUID PRIMARY KEY,
    user_id            BIGINT NOT NULL REFERENCES app_users(id),
    refresh_token_hash VARCHAR(255) NOT NULL,
    device_info        VARCHAR(255),
    ip_address         VARCHAR(255),
    last_active_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE,
    expires_at         TIMESTAMP WITH TIME ZONE
);

CREATE TABLE email_verification_tokens (
    id         BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    user_id    BIGINT NOT NULL REFERENCES app_users(id),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used       BOOLEAN DEFAULT FALSE
);

CREATE TABLE password_reset_tokens (
    id         BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    user_id    BIGINT NOT NULL REFERENCES app_users(id),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used       BOOLEAN DEFAULT FALSE
);

-- ============================================================================
-- ORGANIZATIONS
-- ============================================================================

CREATE TABLE organizations (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    type          VARCHAR(255) NOT NULL,
    website       VARCHAR(500),
    created_by_id BIGINT NOT NULL REFERENCES app_users(id),
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE organization_memberships (
    id              BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL REFERENCES organizations(id),
    user_id         BIGINT NOT NULL REFERENCES app_users(id),
    role            VARCHAR(255) NOT NULL,
    status          VARCHAR(255) NOT NULL DEFAULT 'INVITED',
    joined_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (organization_id, user_id)
);

CREATE TABLE supervision_relationships (
    id              BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL REFERENCES organizations(id),
    supervisor_id   BIGINT NOT NULL REFERENCES app_users(id),
    supervisee_id   BIGINT NOT NULL REFERENCES app_users(id),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (organization_id, supervisor_id, supervisee_id)
);

-- ============================================================================
-- CATALOG (Modules)
-- ============================================================================

CREATE TABLE modules (
    id         BIGSERIAL PRIMARY KEY,
    module_key VARCHAR(255) NOT NULL UNIQUE,
    type       VARCHAR(255),
    price      DOUBLE PRECISION DEFAULT 0.0,
    active     BOOLEAN DEFAULT TRUE,
    admin_only BOOLEAN DEFAULT FALSE
);

CREATE TABLE user_module_access (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES app_users(id),
    module_id    BIGINT NOT NULL REFERENCES modules(id),
    has_access   BOOLEAN DEFAULT TRUE,
    purchased_at TIMESTAMP WITHOUT TIME ZONE,
    expires_at   TIMESTAMP WITHOUT TIME ZONE,
    UNIQUE (user_id, module_id)
);

-- ============================================================================
-- BACTERIOLOGY
-- ============================================================================

CREATE TABLE bacteria (
    id             BIGSERIAL PRIMARY KEY,
    species        VARCHAR(255) NOT NULL,
    strain         VARCHAR(255),
    gram           VARCHAR(255),
    morpho         VARCHAR(255),
    genome_size    DOUBLE PRECISION,
    mlst           VARCHAR(255),
    pathogenicity  VARCHAR(255),
    habitat        VARCHAR(255),
    maldi_profile  VARCHAR(255),
    catalase       BOOLEAN,
    oxydase        BOOLEAN,
    coagulase      BOOLEAN,
    lactose        BOOLEAN,
    indole         BOOLEAN,
    mannitol       BOOLEAN,
    mobilite       BOOLEAN,
    hemolyse       VARCHAR(255)
);

CREATE TABLE bacterium_plasmids (
    bacterium_id BIGINT NOT NULL REFERENCES bacteria(id),
    plasmid_name VARCHAR(255)
);

CREATE TABLE bacterium_snp_signatures (
    bacterium_id BIGINT NOT NULL REFERENCES bacteria(id),
    snp_marker   INTEGER
);

CREATE TABLE bacterium_resistance_genes (
    bacterium_id BIGINT NOT NULL REFERENCES bacteria(id),
    gene_code    VARCHAR(255)
);

CREATE TABLE bacterium_virulence_factors (
    bacterium_id     BIGINT NOT NULL REFERENCES bacteria(id),
    virulence_factor VARCHAR(255)
);

CREATE TABLE bacterium_api_codes (
    bacterium_id BIGINT NOT NULL REFERENCES bacteria(id),
    gallery      VARCHAR(255),
    code         VARCHAR(255)
);

-- ============================================================================
-- MYCOLOGY
-- ============================================================================

CREATE TABLE fungi (
    id                  BIGSERIAL PRIMARY KEY,
    species             VARCHAR(255) NOT NULL,
    type                VARCHAR(255),
    category            VARCHAR(255),
    description         VARCHAR(255),
    habitat             VARCHAR(255),
    morphology          VARCHAR(255),
    optimal_temperature DOUBLE PRECISION,
    maximal_temperature DOUBLE PRECISION,
    applications        VARCHAR(255),
    metabolism          VARCHAR(255),
    pathogenicity       VARCHAR(255),
    culture_medium      VARCHAR(255),
    aerobic             BOOLEAN,
    dimorphic           BOOLEAN,
    encapsulated        BOOLEAN,
    melanin_producer    BOOLEAN,
    reproduction        VARCHAR(255),
    toxins              VARCHAR(255),
    allergens           VARCHAR(255)
);

CREATE TABLE fungus_api_codes (
    fungus_id BIGINT NOT NULL REFERENCES fungi(id),
    gallery   VARCHAR(255),
    code      VARCHAR(255)
);

CREATE TABLE fungus_metabolites (
    fungus_id  BIGINT NOT NULL REFERENCES fungi(id),
    metabolite VARCHAR(255)
);

CREATE TABLE fungus_enzymes (
    fungus_id BIGINT NOT NULL REFERENCES fungi(id),
    enzyme    VARCHAR(255)
);

CREATE TABLE fungus_degradable_substrates (
    fungus_id BIGINT NOT NULL REFERENCES fungi(id),
    substrate VARCHAR(255)
);

CREATE TABLE fungus_hosts (
    fungus_id BIGINT NOT NULL REFERENCES fungi(id),
    host      VARCHAR(255)
);

-- ============================================================================
-- STUDY COLLECTIONS
-- ============================================================================

CREATE TABLE study_collections (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    owner_id    BIGINT NOT NULL REFERENCES app_users(id),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

CREATE TABLE study_collection_items (
    id            BIGSERIAL PRIMARY KEY,
    collection_id BIGINT NOT NULL REFERENCES study_collections(id),
    module_id     VARCHAR(100) NOT NULL,
    entity_id     BIGINT NOT NULL,
    notes         TEXT,
    added_at      TIMESTAMP WITH TIME ZONE NOT NULL
);

-- ============================================================================
-- NOTES
-- ============================================================================

CREATE TABLE notes (
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    content    TEXT,
    color      VARCHAR(30),
    pinned     BOOLEAN NOT NULL DEFAULT FALSE,
    tags       VARCHAR(500),
    deleted_at TIMESTAMP WITH TIME ZONE,
    owner_id   BIGINT NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- ============================================================================
-- ANNOTATIONS
-- ============================================================================

CREATE TABLE annotations (
    id          BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(255) NOT NULL,
    entity_id   BIGINT NOT NULL,
    content     VARCHAR(2000) NOT NULL,
    color       VARCHAR(255) NOT NULL DEFAULT 'YELLOW',
    deleted_at  TIMESTAMP WITH TIME ZONE,
    owner_id    BIGINT NOT NULL REFERENCES app_users(id),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

-- ============================================================================
-- CONTACTS
-- ============================================================================

CREATE TABLE contacts (
    id           BIGSERIAL PRIMARY KEY,
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    email        VARCHAR(255),
    phone        VARCHAR(30),
    organization VARCHAR(255),
    job_title    VARCHAR(255),
    notes        TEXT,
    favorite     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at   TIMESTAMP WITH TIME ZONE,
    owner_id     BIGINT NOT NULL REFERENCES app_users(id),
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255),
    UNIQUE (owner_id, email)
);

-- ============================================================================
-- QUICKSHARE
-- ============================================================================

CREATE TABLE shared_items (
    id              BIGSERIAL PRIMARY KEY,
    share_code      VARCHAR(12) NOT NULL UNIQUE,
    title           VARCHAR(255),
    type            VARCHAR(255) NOT NULL,
    text_content    TEXT,
    download_count  INTEGER DEFAULT 0,
    max_downloads   INTEGER,
    expires_at      TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at      TIMESTAMP WITH TIME ZONE,
    owner_id        BIGINT NOT NULL REFERENCES app_users(id),
    recipient_email VARCHAR(255)
);

CREATE TABLE shared_files (
    id                BIGSERIAL PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename   VARCHAR(255) NOT NULL,
    content_type      VARCHAR(255),
    file_size         BIGINT,
    shared_item_id    BIGINT NOT NULL REFERENCES shared_items(id)
);

-- ============================================================================
-- SUPPORT
-- ============================================================================

CREATE TABLE support_tickets (
    id          BIGSERIAL PRIMARY KEY,
    subject     VARCHAR(255) NOT NULL,
    description TEXT,
    category    VARCHAR(255) NOT NULL,
    priority    VARCHAR(255) NOT NULL DEFAULT 'MEDIUM',
    status      VARCHAR(255) NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at  TIMESTAMP WITH TIME ZONE,
    owner_id    BIGINT NOT NULL REFERENCES app_users(id)
);

CREATE TABLE ticket_messages (
    id         BIGSERIAL PRIMARY KEY,
    content    TEXT NOT NULL,
    is_admin   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    author_id  BIGINT NOT NULL REFERENCES app_users(id),
    ticket_id  BIGINT NOT NULL REFERENCES support_tickets(id)
);

-- ============================================================================
-- NOTIFICATIONS
-- ============================================================================

CREATE TABLE notifications (
    id           BIGSERIAL PRIMARY KEY,
    type         VARCHAR(50) NOT NULL,
    title        VARCHAR(255) NOT NULL,
    message      TEXT,
    is_read      BOOLEAN NOT NULL DEFAULT FALSE,
    action_url   VARCHAR(255),
    metadata     TEXT,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    recipient_id BIGINT NOT NULL REFERENCES app_users(id)
);

-- ============================================================================
-- HISTORY
-- ============================================================================

CREATE TABLE history_entries (
    id            BIGSERIAL PRIMARY KEY,
    module_slug   VARCHAR(50) NOT NULL,
    action_type   VARCHAR(10) NOT NULL,
    entity_id     BIGINT NOT NULL,
    label_key     VARCHAR(255) NOT NULL,
    icon          VARCHAR(50),
    previous_data TEXT,
    new_data      TEXT,
    owner_id      BIGINT NOT NULL REFERENCES app_users(id),
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255)
);

CREATE INDEX idx_history_owner_module ON history_entries (owner_id, module_slug, created_at);
