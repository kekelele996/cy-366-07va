-- 演示数据：会员（余额/时长包覆盖三种扣费场景）、机位、预约、一条上机中的会话

INSERT INTO members (id, member_no, name, balance)
SELECT 1, 'M001', '王一局', 50.00
WHERE NOT EXISTS (SELECT 1 FROM members WHERE member_no = 'M001');
INSERT INTO members (id, member_no, name, balance)
SELECT 2, 'M002', '赵开黑', 3.00
WHERE NOT EXISTS (SELECT 1 FROM members WHERE member_no = 'M002');
INSERT INTO members (id, member_no, name, balance)
SELECT 3, 'M003', '孙团战', 40.00
WHERE NOT EXISTS (SELECT 1 FROM members WHERE member_no = 'M003');
INSERT INTO members (id, member_no, name, balance)
SELECT 4, 'M004', '李补时', 20.00
WHERE NOT EXISTS (SELECT 1 FROM members WHERE member_no = 'M004');

-- M001：充足时长包；M002：时长包+余额都不够首小时（单价 10 元，缺口 2 元）；
-- M003：无时长包全靠余额；M004：30 分钟时长包 + 余额兜底
INSERT INTO time_packages (member_id, package_name, total_minutes, remaining_minutes, expire_at)
SELECT 1, '30 小时包', 1800, 1200, TIMESTAMPADD(MONTH, 1, CURRENT_TIMESTAMP)
WHERE NOT EXISTS (SELECT 1 FROM time_packages tp WHERE tp.member_id = 1);
INSERT INTO time_packages (member_id, package_name, total_minutes, remaining_minutes, expire_at)
SELECT 2, '10 小时包', 600, 30, TIMESTAMPADD(MONTH, 1, CURRENT_TIMESTAMP)
WHERE NOT EXISTS (SELECT 1 FROM time_packages tp WHERE tp.member_id = 2);
INSERT INTO time_packages (member_id, package_name, total_minutes, remaining_minutes, expire_at)
SELECT 4, '10 小时包', 600, 30, TIMESTAMPADD(MONTH, 1, CURRENT_TIMESTAMP)
WHERE NOT EXISTS (SELECT 1 FROM time_packages tp WHERE tp.member_id = 4 AND tp.remaining_minutes = 30);

INSERT INTO seats (id, seat_no, area, zone, seat_type, status, hourly_rate)
SELECT 1, 'A01', '大厅 A 区', 'A', '单人机位', 'RESERVED', 8.00
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'A01');
INSERT INTO seats (id, seat_no, area, zone, seat_type, status, hourly_rate)
SELECT 2, 'A02', '大厅 A 区', 'A', '单人机位', 'RESERVED', 10.00
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'A02');
INSERT INTO seats (id, seat_no, area, zone, seat_type, status, hourly_rate)
SELECT 3, 'A03', '大厅 A 区', 'A', '单人机位', 'RESERVED', 12.00
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'A03');
INSERT INTO seats (id, seat_no, area, zone, seat_type, status, hourly_rate)
SELECT 4, 'A04', '大厅 A 区', 'A', '单人机位', 'IDLE', 8.00
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'A04');
INSERT INTO seats (id, seat_no, area, zone, seat_type, status, hourly_rate)
SELECT 5, 'B01', '包厢 B 区', 'B', '双人包厢', 'RESERVED', 20.00
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'B01');
INSERT INTO seats (id, seat_no, area, zone, seat_type, status, hourly_rate)
SELECT 6, 'B02', '包厢 B 区', 'B', '五人开黑房', 'IN_USE', 30.00
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'B02');
INSERT INTO seats (id, seat_no, area, zone, seat_type, status, hourly_rate)
SELECT 7, 'C01', '高配电竞区', 'C', '单人机位', 'FAULT', 15.00
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'C01');
INSERT INTO seats (id, seat_no, area, zone, seat_type, status, hourly_rate)
SELECT 8, 'A05', '大厅 A 区', 'A', '单人机位', 'RESERVED', 10.00
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'A05');

-- 可到店开机的预约用相对当前时间的窗口，保证任意时刻演示都有效；
-- YY-1004 超出提前开机窗口，YY-1005 预约在故障机位上（用于核验拦截演示）
INSERT INTO reservations (id, reservation_no, member_id, seat_id, start_time, end_time, status)
SELECT 1, 'YY-1001', 1, 1,
       TIMESTAMPADD(HOUR, -1, CURRENT_TIMESTAMP),
       TIMESTAMPADD(HOUR, 3, CURRENT_TIMESTAMP),
       'RESERVED'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE reservation_no = 'YY-1001');
INSERT INTO reservations (id, reservation_no, member_id, seat_id, start_time, end_time, status)
SELECT 2, 'YY-1002', 2, 2,
       TIMESTAMPADD(MINUTE, -30, CURRENT_TIMESTAMP),
       TIMESTAMPADD(HOUR, 3, CURRENT_TIMESTAMP),
       'RESERVED'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE reservation_no = 'YY-1002');
INSERT INTO reservations (id, reservation_no, member_id, seat_id, start_time, end_time, status)
SELECT 3, 'YY-1003', 3, 3,
       TIMESTAMPADD(HOUR, -2, CURRENT_TIMESTAMP),
       TIMESTAMPADD(HOUR, 2, CURRENT_TIMESTAMP),
       'RESERVED'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE reservation_no = 'YY-1003');
INSERT INTO reservations (id, reservation_no, member_id, seat_id, start_time, end_time, status)
SELECT 4, 'YY-1004', 1, 5,
       TIMESTAMPADD(HOUR, 6, CURRENT_TIMESTAMP),
       TIMESTAMPADD(HOUR, 9, CURRENT_TIMESTAMP),
       'RESERVED'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE reservation_no = 'YY-1004');
INSERT INTO reservations (id, reservation_no, member_id, seat_id, start_time, end_time, status)
SELECT 5, 'YY-1005', 2, 7,
       TIMESTAMPADD(HOUR, -1, CURRENT_TIMESTAMP),
       TIMESTAMPADD(HOUR, 2, CURRENT_TIMESTAMP),
       'RESERVED'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE reservation_no = 'YY-1005');
-- M004：时长包 30 分钟 + 余额兜底 30 分钟（5 元）的混合扣费成功场景
INSERT INTO reservations (id, reservation_no, member_id, seat_id, start_time, end_time, status)
SELECT 6, 'YY-1006', 4, 8,
       TIMESTAMPADD(MINUTE, -15, CURRENT_TIMESTAMP),
       TIMESTAMPADD(HOUR, 3, CURRENT_TIMESTAMP),
       'RESERVED'
WHERE NOT EXISTS (SELECT 1 FROM reservations WHERE reservation_no = 'YY-1006');

-- 一条已在进行中的上机会话（运营台演示用）
INSERT INTO usage_sessions (member_id, seat_id, reservation_id, start_time, plan_end_time, status, charged_minutes, charge_summary)
SELECT 3, 6, NULL,
       TIMESTAMPADD(HOUR, -2, CURRENT_TIMESTAMP),
       TIMESTAMPADD(HOUR, 1, CURRENT_TIMESTAMP),
       'IN_USE', 180, '余额扣费 90.00 元'
WHERE NOT EXISTS (
  SELECT 1 FROM usage_sessions WHERE member_id = 3 AND seat_id = 6 AND status = 'IN_USE'
);

INSERT INTO charge_records (session_id, member_id, charge_type, minutes, amount)
SELECT s.id, 3, 'BALANCE', 180, 90.00
FROM usage_sessions s
WHERE s.member_id = 3 AND s.seat_id = 6 AND s.status = 'IN_USE'
  AND NOT EXISTS (SELECT 1 FROM charge_records c WHERE c.session_id = s.id);

INSERT INTO operation_records (module_name, owner_name, status, metric)
SELECT '机位/包厢实时状态看板', '运营组', 'ready', '100%'
WHERE NOT EXISTS (SELECT 1 FROM operation_records WHERE module_name = '机位/包厢实时状态看板');
