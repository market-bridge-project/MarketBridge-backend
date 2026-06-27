-- 시장
INSERT INTO market (name, address) VALUES ('잠실새마을시장', '서울특별시 송파구 잠실동 367-1');

-- 점포
INSERT INTO store (market_id, name, category, intro, image_url, map_x, map_y, open_time, phone_number)
VALUES (1, '엄마손분식', '분식', '40년 전통 떡볶이 맛집', 'https://example.com/store1.jpg', 120.5, 80.3, '09:00~20:00', '02-111-1111');

INSERT INTO store (market_id, name, category, intro, image_url, map_x, map_y, open_time, phone_number)
VALUES (1, '한우정육점', '정육', '신선한 1++ 한우 전문점', 'https://example.com/store2.jpg', 200.0, 150.7, '08:00~19:00', '02-222-2222');

INSERT INTO store (market_id, name, category, intro, image_url, map_x, map_y, open_time, phone_number)
VALUES (1, '시장통커피', '카페', '시장 한복판의 아늑한 카페', 'https://example.com/store3.jpg', 60.2, 220.9, '10:00~22:00', '02-333-3333');

-- 점포 키워드 (store_keywords)
INSERT INTO store_keywords (store_id, keyword) VALUES (1, 'ALONE');
INSERT INTO store_keywords (store_id, keyword) VALUES (1, 'SNACK');
INSERT INTO store_keywords (store_id, keyword) VALUES (1, 'SHORT');

INSERT INTO store_keywords (store_id, keyword) VALUES (2, 'FAMILY');
INSERT INTO store_keywords (store_id, keyword) VALUES (2, 'SHOPPING');

INSERT INTO store_keywords (store_id, keyword) VALUES (3, 'COUPLE');
INSERT INTO store_keywords (store_id, keyword) VALUES (3, 'SNACK');
INSERT INTO store_keywords (store_id, keyword) VALUES (3, 'LONG');

-- 점포 메뉴 (store_menu)
INSERT INTO store_menu (store_id, name, price) VALUES (1, '떡볶이', 4000);
INSERT INTO store_menu (store_id, name, price) VALUES (1, '순대', 5000);
INSERT INTO store_menu (store_id, name, price) VALUES (1, '튀김', 3000);

INSERT INTO store_menu (store_id, name, price) VALUES (2, '한우 등심 100g', 18000);
INSERT INTO store_menu (store_id, name, price) VALUES (2, '한우 안심 100g', 22000);

INSERT INTO store_menu (store_id, name, price) VALUES (3, '아메리카노', 3500);
INSERT INTO store_menu (store_id, name, price) VALUES (3, '카페라떼', 4000);
INSERT INTO store_menu (store_id, name, price) VALUES (3, '수제 쿠키', 2500);
