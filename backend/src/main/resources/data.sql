-- 到店开机演示数据（H2 本地内存库）
-- 仅当表为空时插入，方便本地直接验证「选择预约单 -> 核验 -> 扣费开机」流程。

INSERT INTO members (id, member_no, name, phone, balance, status)
  SELECT * FROM (SELECT 1, 'M1001', '周野', '13800001001', 30.00, 'active')
  WHERE NOT EXISTS (SELECT 1 FROM members);
INSERT INTO members (id, member_no, name, phone, balance, status)
  SELECT * FROM (SELECT 2, 'M1002', '林洲', '13800001002', 5.00, 'active')
  WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 2);
INSERT INTO members (id, member_no, name, phone, balance, status)
  SELECT * FROM (SELECT 3, 'M1003', '陈墨', '13800001003', 200.00, 'frozen')
  WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 3);
INSERT INTO members (id, member_no, name, phone, balance, status)
  SELECT * FROM (SELECT 4, 'M1004', '许棠', '13800001004', 20.00, 'active')
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
  SELECT 1, 'R2026092601', 1, 1, DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), DATEADD('HOUR', 3, CURRENT_TIMESTAMP), 'reserved'
  WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE id = 1);
INSERT INTO reservations (id, reservation_no, member_id, station_id, start_time, end_time, status)
  SELECT 2, 'R2026092602', 2, 3, DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), DATEADD('HOUR', 2, CURRENT_TIMESTAMP), 'reserved'
  WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE id = 2);
INSERT INTO reservations (id, reservation_no, member_id, station_id, start_time, end_time, status)
  SELECT 3, 'R2026092603', 3, 2, DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), DATEADD('HOUR', 2, CURRENT_TIMESTAMP), 'reserved'
  WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE id = 3);
INSERT INTO reservations (id, reservation_no, member_id, station_id, start_time, end_time, status)
  SELECT 4, 'R2026092604', 4, 5, DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), DATEADD('HOUR', 3, CURRENT_TIMESTAMP), 'reserved'
  WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE id = 4);
