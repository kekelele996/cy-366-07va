-- 电竞馆上机管理系统初始化脚本（MySQL 8.0）
-- 容器首次启动时自动执行（/docker-entrypoint-initdb.d）。

CREATE TABLE IF NOT EXISTS operation_records (
  id INT AUTO_INCREMENT PRIMARY KEY,
  module_name VARCHAR(120) NOT NULL,
  owner_name VARCHAR(80) NOT NULL,
  status VARCHAR(40) NOT NULL,
  metric VARCHAR(40) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO operation_records (module_name, owner_name, status, metric)
SELECT '机位/包厢实时状态看板', '运营组', 'ready', '100%'
WHERE NOT EXISTS (SELECT 1 FROM operation_records);

-- 会员
CREATE TABLE IF NOT EXISTS members (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_no VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(80) NOT NULL,
  phone VARCHAR(20),
  balance DECIMAL(10, 2) NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 时长包（消费时优先扣减）
CREATE TABLE IF NOT EXISTS duration_packages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  name VARCHAR(80) NOT NULL,
  total_minutes INT NOT NULL,
  remaining_minutes INT NOT NULL,
  valid_from TIMESTAMP NULL,
  valid_until TIMESTAMP NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_pkg_member (member_id, status, remaining_minutes)
);

-- 机位
CREATE TABLE IF NOT EXISTS stations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  station_no VARCHAR(32) NOT NULL UNIQUE,
  zone_name VARCHAR(80) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'idle',
  hourly_rate DECIMAL(10, 2) NOT NULL DEFAULT 0,
  active_session_id BIGINT NULL,
  INDEX idx_station_status (status)
);

-- 预约单
CREATE TABLE IF NOT EXISTS reservations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reservation_no VARCHAR(32) NOT NULL UNIQUE,
  member_id BIGINT NOT NULL,
  station_id BIGINT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'reserved',
  checked_in_at DATETIME NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_reservation_status (status, start_time)
);

-- 上机记录
CREATE TABLE IF NOT EXISTS sessions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_no VARCHAR(32) NOT NULL UNIQUE,
  reservation_id BIGINT NULL,
  member_id BIGINT NOT NULL,
  station_id BIGINT NOT NULL,
  started_at DATETIME NOT NULL,
  ended_at DATETIME NULL,
  package_minutes_used INT NOT NULL DEFAULT 0,
  balance_amount_used DECIMAL(10, 2) NOT NULL DEFAULT 0,
  first_hour_charge DECIMAL(10, 2) NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  INDEX idx_session_status (status, started_at)
);

-- 上机扣费明细（时长包抵扣 / 余额扣款）
CREATE TABLE IF NOT EXISTS session_charges (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  charge_type VARCHAR(20) NOT NULL,
  minutes INT NOT NULL DEFAULT 0,
  amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
  detail VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_charge_session (session_id),
  INDEX idx_charge_created (created_at)
);

-- 演示数据
INSERT INTO members (id, member_no, name, phone, balance, status)
SELECT * FROM (SELECT 1, 'M1001', '周野', '13800001001', 30.00, 'active') AS seed
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 1);
INSERT INTO members (id, member_no, name, phone, balance, status)
SELECT * FROM (SELECT 2, 'M1002', '林洲', '13800001002', 5.00, 'active') AS seed
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 2);
INSERT INTO members (id, member_no, name, phone, balance, status)
SELECT * FROM (SELECT 3, 'M1003', '陈墨', '13800001003', 200.00, 'frozen') AS seed
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 3);
INSERT INTO members (id, member_no, name, phone, balance, status)
SELECT * FROM (SELECT 4, 'M1004', '许棠', '13800001004', 20.00, 'active') AS seed
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 4);

INSERT INTO duration_packages (id, member_id, name, total_minutes, remaining_minutes, status)
SELECT 1, 1, '10 小时时长包', 600, 420, 'active'
WHERE NOT EXISTS (SELECT 1 FROM duration_packages WHERE id = 1);
INSERT INTO duration_packages (id, member_id, name, total_minutes, remaining_minutes, status)
SELECT 2, 2, '10 小时时长包', 600, 30, 'active'
WHERE NOT EXISTS (SELECT 1 FROM duration_packages WHERE id = 2);
INSERT INTO duration_packages (id, member_id, name, total_minutes, remaining_minutes, status)
SELECT 3, 4, '10 小时时长包', 600, 40, 'active'
WHERE NOT EXISTS (SELECT 1 FROM duration_packages WHERE id = 3);

INSERT INTO stations (id, station_no, zone_name, status, hourly_rate)
SELECT 1, 'A01', '大厅 A 区', 'reserved', 12.00
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE id = 1);
INSERT INTO stations (id, station_no, zone_name, status, hourly_rate)
SELECT 2, 'A02', '大厅 A 区', 'idle', 12.00
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE id = 2);
INSERT INTO stations (id, station_no, zone_name, status, hourly_rate)
SELECT 3, 'B07', '包厢 B 区', 'reserved', 30.00
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE id = 3);
INSERT INTO stations (id, station_no, zone_name, status, hourly_rate)
SELECT 4, 'A03', '大厅 A 区', 'idle', 12.00
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE id = 4);
INSERT INTO stations (id, station_no, zone_name, status, hourly_rate)
SELECT 5, 'C11', '竞技 C 区', 'reserved', 20.00
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE id = 5);

INSERT INTO reservations (id, reservation_no, member_id, station_id, start_time, end_time, status)
SELECT 1, 'R2026092601', 1, 1, DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 3 HOUR), 'reserved'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE id = 1);
INSERT INTO reservations (id, reservation_no, member_id, station_id, start_time, end_time, status)
SELECT 2, 'R2026092602', 2, 3, DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 2 HOUR), 'reserved'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE id = 2);
INSERT INTO reservations (id, reservation_no, member_id, station_id, start_time, end_time, status)
SELECT 3, 'R2026092603', 3, 2, DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 2 HOUR), 'reserved'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE id = 3);
INSERT INTO reservations (id, reservation_no, member_id, station_id, start_time, end_time, status)
SELECT 4, 'R2026092604', 4, 5, DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 3 HOUR), 'reserved'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE id = 4);
