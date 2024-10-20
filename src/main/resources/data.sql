insert into member(phone_number, email, name, password)
values ('010-1234-1234', '1@naver.com', '1', '1');

insert into stay(check_in_time, check_out_time, fee_per_night, guest_count, host_id, title, point)
values ('15:00:00', '11:00:00', 20000.00, 3, 1, '제목2', ST_GeomFromText('POINT(37.9780 128.5665)', 4326));

insert into reservation(guest_count, total_fee, check_in, check_out, guest_id, stay_id, status)
VALUES (1, 50000.00, '2024-5-30 15:00:00', '2024-6-1 11:00:00', 1, 1, 'RESERVED')