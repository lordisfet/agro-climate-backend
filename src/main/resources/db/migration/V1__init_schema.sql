-- V1__init_schema.sql

-- ==========================================
-- БЛОК 1: СПРАВОЧНИКИ (Dictionaries / Lookups)
-- ==========================================

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL -- 'ADMIN', 'OWNER', 'WORKER'
);

CREATE TABLE node_statuses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL -- 'ACTIVE', 'INACTIVE', 'ERROR'
);

CREATE TABLE parameters (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL, -- 'TEMPERATURE', 'HUMIDITY', 'CO2'
    unit VARCHAR(20) NOT NULL         -- 'Celsius', '%', 'PPM'
);

CREATE TABLE alert_levels (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL -- 'WARNING', 'CRITICAL'
);

CREATE TABLE alert_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL -- 'HIGH_TEMP', 'SENSOR_FAILURE' etc.
);

-- Инсертим базовые данные в справочники (Seed data)
INSERT INTO roles (name) VALUES ('ADMIN'), ('OWNER'), ('WORKER');
INSERT INTO node_statuses (name) VALUES ('ACTIVE'), ('INACTIVE'), ('ERROR');
INSERT INTO parameters (name, unit) VALUES ('TEMPERATURE', 'Celsius'), ('HUMIDITY', '%'), ('CO2', 'PPM');
INSERT INTO alert_levels (name) VALUES ('WARNING'), ('CRITICAL');

-- ==========================================
-- БЛОК 2: ОСНОВНЫЕ СУЩНОСТИ
-- ==========================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL REFERENCES roles(id) ON DELETE RESTRICT
);

CREATE TABLE nodes (
    id VARCHAR(64) PRIMARY KEY, -- DevEUI шлюза/датчика
    location VARCHAR(255) NOT NULL, 
    status_id INTEGER NOT NULL REFERENCES node_statuses(id) ON DELETE RESTRICT,
    last_update TIMESTAMP WITH TIME ZONE
);

-- ==========================================
-- БЛОК 3: ТРАНЗАКЦИОННЫЕ ДАННЫЕ (Time-Series & Events)
-- ==========================================

-- Вариант нормализованной таблицы измерений (EAV паттерн)
CREATE TABLE measurements (
    id BIGSERIAL PRIMARY KEY,
    node_id VARCHAR(64) NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    parameter_id INTEGER NOT NULL REFERENCES parameters(id) ON DELETE RESTRICT,
    value NUMERIC(8, 2) NOT NULL -- Хранит и градусы, и ppm
);

-- Индексы для EAV под IoT (жизненно необходимы при таком дизайне!)
CREATE INDEX idx_measurements_node_param_time ON measurements (node_id, parameter_id, timestamp DESC);
CREATE INDEX idx_measurements_timestamp ON measurements (timestamp DESC);

CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    node_id VARCHAR(64) NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    type_id INTEGER NOT NULL REFERENCES alert_types(id) ON DELETE RESTRICT,
    level_id INTEGER NOT NULL REFERENCES alert_levels(id) ON DELETE RESTRICT,
    message TEXT NOT NULL,
    resolved BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_alerts_unresolved ON alerts (resolved) WHERE resolved = FALSE;

CREATE TABLE logs (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action TEXT NOT NULL
);