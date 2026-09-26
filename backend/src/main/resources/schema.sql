-- 电竞馆上机管理系统：会员 / 机位 / 预约 / 上机 / 扣费
-- 兼容 MySQL 8.0，同时作为后端 H2（MODE=MySQL）的初始化脚本，故不使用 ENGINE 等方言子句。

CREATE TABLE IF NOT EXISTS members (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_no VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(80) NOT NULL,
  balance DECIMAL(10, 2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS time_packages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  package_name VARCHAR(80) NOT NULL,
  total_minutes INT NOT NULL,
  remaining_minutes INT NOT NULL,
  expire_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_packages_member FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE TABLE IF NOT EXISTS seats (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  seat_no VARCHAR(32) NOT NULL UNIQUE,
  area VARCHAR(80) NOT NULL,
  zone VARCHAR(40) NOT NULL,
  seat_type VARCHAR(40) NOT NULL,
  -- IDLE 空闲 / IN_USE 使用中 / RESERVED 已预约 / FAULT 故障
  status VARCHAR(20) NOT NULL DEFAULT 'IDLE',
  hourly_rate DECIMAL(10, 2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reservations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reservation_no VARCHAR(40) NOT NULL UNIQUE,
  member_id BIGINT NOT NULL,
  seat_id BIGINT NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  -- RESERVED 已预约 / CHECKED_IN 已到店开机 / CANCELLED 已取消
  status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reservations_member FOREIGN KEY (member_id) REFERENCES members(id),
  CONSTRAINT fk_reservations_seat FOREIGN KEY (seat_id) REFERENCES seats(id)
);

CREATE TABLE IF NOT EXISTS usage_sessions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  seat_id BIGINT NOT NULL,
  reservation_id BIGINT NULL,
  start_time TIMESTAMP NOT NULL,
  plan_end_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NULL,
  -- IN_USE 使用中 / FINISHED 已下机
  status VARCHAR(20) NOT NULL DEFAULT 'IN_USE',
  charged_minutes INT NOT NULL DEFAULT 0,
  charge_summary VARCHAR(200),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_sessions_member FOREIGN KEY (member_id) REFERENCES members(id),
  CONSTRAINT fk_sessions_seat FOREIGN KEY (seat_id) REFERENCES seats(id),
  CONSTRAINT fk_sessions_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

CREATE TABLE IF NOT EXISTS charge_records (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  -- PACKAGE 时长包扣减 / BALANCE 余额扣减
  charge_type VARCHAR(20) NOT NULL,
  minutes INT NOT NULL DEFAULT 0,
  amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_charges_session FOREIGN KEY (session_id) REFERENCES usage_sessions(id),
  CONSTRAINT fk_charges_member FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE TABLE IF NOT EXISTS operation_records (
  id INT AUTO_INCREMENT PRIMARY KEY,
  module_name VARCHAR(120) NOT NULL,
  owner_name VARCHAR(80) NOT NULL,
  status VARCHAR(40) NOT NULL,
  metric VARCHAR(40) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
