-- 到店开机流程相关表结构（H2 本地内存库，MySQL 兼容模式）
-- 生产 MySQL 脚本见 database/init.sql，两处结构保持一致。

CREATE TABLE IF NOT EXISTS members (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_no VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(80) NOT NULL,
  phone VARCHAR(20),
  balance DECIMAL(10, 2) NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS duration_packages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  name VARCHAR(80) NOT NULL,
  total_minutes INT NOT NULL,
  remaining_minutes INT NOT NULL,
  valid_from TIMESTAMP,
  valid_until TIMESTAMP,
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  station_no VARCHAR(32) NOT NULL UNIQUE,
  zone_name VARCHAR(80) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'idle',
  hourly_rate DECIMAL(10, 2) NOT NULL DEFAULT 0,
  active_session_id BIGINT
);

CREATE TABLE IF NOT EXISTS reservations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reservation_no VARCHAR(32) NOT NULL UNIQUE,
  member_id BIGINT NOT NULL,
  station_id BIGINT NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'reserved',
  checked_in_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sessions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_no VARCHAR(32) NOT NULL UNIQUE,
  reservation_id BIGINT,
  member_id BIGINT NOT NULL,
  station_id BIGINT NOT NULL,
  started_at TIMESTAMP NOT NULL,
  ended_at TIMESTAMP,
  package_minutes_used INT NOT NULL DEFAULT 0,
  balance_amount_used DECIMAL(10, 2) NOT NULL DEFAULT 0,
  first_hour_charge DECIMAL(10, 2) NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'active'
);

CREATE TABLE IF NOT EXISTS session_charges (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  charge_type VARCHAR(20) NOT NULL,
  minutes INT NOT NULL DEFAULT 0,
  amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
  detail VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
