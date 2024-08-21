insert into member(phone_number, email, name, password)
    values ('010-1234-1234', '1@naver.com', '1', '1');

# 위도(-90~90, y) 경도(-180 ~ 180, x) 순
 insert into stay(check_in_time, check_out_time, fee_per_night, guest_count, member_id, title, point)
     values ('19:00:00', '12:00:00', 20000.00, 3, 1, '제목2', ST_GeomFromText('POINT(37.9780 128.5665)', 4326));