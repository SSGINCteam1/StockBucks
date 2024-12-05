DROP DATABASE stockBucks;

CREATE DATABASE stockBucks;

USE stockbucks;

########################
######## CREATE ########
########################

# 유저 테이블
CREATE TABLE IF NOT EXISTS users(
    users_no INT PRIMARY KEY AUTO_INCREMENT, -- 사용자 번호
    users_id VARCHAR(100) NOT NULL UNIQUE, -- 유저 아이디
    users_pw VARCHAR(100) NOT NULL, -- 유저 비밀번호
    users_role INT NOT NULL, -- 유저역할 (0: 본사, 1: 매니저, 2: 사원)
    users_name VARCHAR(100) NOT NULL, -- 유저명
    is_active TINYINT NOT NULL DEFAULT 1, -- 활성화 여부
    users_birth VARCHAR(100) NOT NULL -- 생년월일
);

# 재고물품 테이블
CREATE TABLE IF NOT EXISTS stock (
	st_no INT PRIMARY KEY AUTO_INCREMENT, -- 재고물품 고유번호
    st_name VARCHAR(100) NOT NULL, -- 재고물품명
    st_price INT NOT NULL, -- 가격 (단위 : 원)
    st_quantity INT NOT NULL DEFAULT 0, -- 재고수량
    st_owner INT NOT NULL check (st_owner IN (0, 1)), -- 구분자 (0: 본사, 1: 지점)
    st_category INT NOT NULL check (st_category >= 0 AND st_category <= 5), -- 카테고리(디저트 : 0 /  MD : 1 / 일회용품: 2  /원자재: 3 / 병음료 : 4 / 원두 : 5)
    st_unit VARCHAR(100) NOT NULL DEFAULT 'EA', -- 단위(재고 수량 단위(kg, g, l, ml, EA 등))
    st_state TINYINT NOT NULL DEFAULT 1 -- 발주가능여부(발주 가능 여부(true/false))
);

# 발주 테이블
CREATE TABLE IF NOT EXISTS place_orders (
	po_no INT AUTO_INCREMENT PRIMARY KEY, -- 발주 번호
    po_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP(), -- 발주 일시
    po_total INT NOT NULL, -- 발주 총액
    users_no INT, -- 사용자 번호
    
    FOREIGN KEY (users_no) REFERENCES users (users_no) ON UPDATE CASCADE
);

# 발주 - 재고물품의 중간 테이블
CREATE TABLE IF NOT EXISTS place_orders_stock (
	po_no INT, -- 발주 번호
    st_no INT, -- 재고물품 고유번호
    post_quantity INT NOT NULL, -- 물품 발주 수량
    
    PRIMARY KEY (po_no, st_no),
    FOREIGN KEY (po_no) REFERENCES place_orders (po_no) ON UPDATE CASCADE,
    FOREIGN KEY (st_no) REFERENCES stock (st_no) ON UPDATE CASCADE
);

# 회원 - 재고물품의 중간 테이블(장바구니 테이블)
CREATE TABLE IF NOT EXISTS place_orders_basket (
	users_no INT, -- 사용자 번호
    st_no INT, -- 재고물품 고유번호
    pob_quantity INT NOT NULL, -- 물품 발주 수량
    
    PRIMARY KEY (st_no, users_no),
    FOREIGN KEY (users_no) REFERENCES users (users_no) ON UPDATE CASCADE,
    FOREIGN KEY (st_no) REFERENCES stock (st_no) ON UPDATE CASCADE
);

# 제조상품 카테고리
CREATE TABLE IF NOT EXISTS prdcg (
	prdcg_no INT PRIMARY KEY AUTO_INCREMENT, -- 제조상품카테고리번호
    prdcg_name VARCHAR(100) NOT NULL UNIQUE, -- 카테고리명
    prdcg_state TINYINT DEFAULT 1 -- 카테고리 활성화 여부(true/false)
);

# 제조상품
CREATE TABLE IF NOT EXISTS products (
	p_no INT PRIMARY KEY AUTO_INCREMENT, -- 제조상품ID
    p_name VARCHAR(100) NOT NULL UNIQUE, -- 제조상품이름
    p_price INT NOT NULL, -- 제조상품가격
    p_state TINYINT NOT NULL DEFAULT 1, -- 제조상품판매 가능 여부
    prdcg_no INT NOT NULL, -- 제조상품 카테고리 번호
    p_releasesDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP(), -- 제조상품 출시일
    
    FOREIGN KEY (prdcg_no) REFERENCES prdcg(prdcg_no) ON UPDATE CASCADE
);

# 제조상품_원자재 중개 테이블
CREATE TABLE IF NOT EXISTS prd_stock (
	p_no INT NOT NULL, -- 제조상품
    st_no INT NOT NULL, -- 잡화 고유번호
    pst_consume INT NOT NULL, -- 제조 시 해당 원자재 소모량
    
	FOREIGN KEY (p_no) REFERENCES products(p_no) ON UPDATE CASCADE,
    FOREIGN KEY (st_no) REFERENCES stock(st_no) ON UPDATE CASCADE,
    PRIMARY KEY (p_no, st_no)
);

# 옵션 카테고리
CREATE TABLE IF NOT EXISTS opt_category ( 
	category_no INT PRIMARY KEY AUTO_INCREMENT, -- 카테고리 번호
    st_no INT, -- 재고물품 고유번호
    category_name VARCHAR(100) NOT NULL UNIQUE, -- 카테고리명
    
    FOREIGN KEY (st_no) REFERENCES stock(st_no) ON UPDATE CASCADE
);

# 제조상품옵션
CREATE TABLE IF NOT EXISTS opt ( 
	opt_no INT PRIMARY KEY AUTO_INCREMENT, -- 옵션번호
    opt_name VARCHAR(100) NOT NULL UNIQUE, -- 옵션명
    opt_price INT NOT NULL DEFAULT 0, -- 옵션가격
    opt_consume INT, -- 옵션 추가 시 원자재 소모량
    category_no INT NOT NULL, -- 카테고리 번호
    is_active TINYINT NOT NULL DEFAULT 1, -- 활성화 여부(true/false)
    
    FOREIGN KEY(category_no) REFERENCES opt_category(category_no) ON UPDATE CASCADE
);

# 제조상품_옵션 카테고리 중개 테이블
CREATE TABLE IF NOT EXISTS prd_optcg (
	category_no INT NOT NULL, -- 카테고리 번호
    p_no INT NOT NULL, -- 제조상품
    optcg_name VARCHAR(100) NOT NULL, -- 옵션카테고리명
    
    FOREIGN KEY (category_no) REFERENCES opt(opt_no) ON UPDATE CASCADE, 
    FOREIGN KEY (p_no) REFERENCES products(p_no) ON UPDATE CASCADE,
	PRIMARY KEY (category_no, p_no)
);

# 주문 테이블
CREATE TABLE IF NOT EXISTS orders (
	orders_no INT PRIMARY KEY AUTO_INCREMENT, -- 주문번호
    orders_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP(), -- 주문일시
    orders_quantity INT NOT NULL, -- 주문수량
    orders_total DECIMAL NOT NULL, -- 주문총액
    users_no INT NOT NULL, -- 주문자 번호
    
    FOREIGN KEY (users_no) REFERENCES users(users_no) ON UPDATE CASCADE
);

# 주문_제조상품 중개 테이블
CREATE TABLE IF NOT EXISTS orders_prd (
	ord_prd_no INT PRIMARY KEY AUTO_INCREMENT,
	orders_no INT NOT NULL, -- 주문번호
    p_no INT NOT NULL, -- 제조상품ID
    opd_quantity INT NOT NULL DEFAULT 1, -- 주문수량
    
    FOREIGN KEY (orders_no) REFERENCES orders(orders_no) ON UPDATE CASCADE
);

# 주문_옵션 중개테이블
CREATE TABLE IF NOT EXISTS orders_opt (
	ord_prd_no INT NOT NULL, -- 주문_제조상품 번호
    opt_no INT NOT NULL, -- 옵션번호
    oropt_name VARCHAR(100) NOT NULL, -- 옵션명
    oropt_price INT NOT NULL DEFAULT 0, -- 옵션가격
    oropt_quantity INT default 1,
    
    FOREIGN KEY (ord_prd_no) REFERENCES orders_prd(ord_prd_no) ON UPDATE CASCADE,
    FOREIGN KEY (opt_no) REFERENCES opt(opt_no) ON UPDATE CASCADE,
    
    PRIMARY KEY(ord_prd_no, opt_no)
);

# 주문_재고물품 중개 테이블
CREATE TABLE IF NOT EXISTS orders_stock (
	orders_no INT NOT NULL, -- 주문번호
    st_no INT NOT NULL, -- 잡화 고유번호
    ost_quantity INT NOT NULL DEFAULT 1, -- 주문수량
    
    FOREIGN KEY (orders_no) REFERENCES orders(orders_no) ON UPDATE CASCADE,
    PRIMARY KEY (orders_no, st_no)
);

########################
######## INSERT ########
########################

-- 회원 
INSERT INTO users (users_id, users_pw, users_role, users_name, users_birth) VALUES
('test1', '1234', 0, '김창훈', 980101),
('test2', '1234', 1, '민소원', 020101),
('test3', '1234', 2, '김동현', 960101);

INSERT INTO stock (st_name, st_price, st_quantity, st_owner, st_category, st_unit, st_state)
VALUES
('탕종 플레인 베이글', 2300, 5, 1, 0, 'EA', 1),
('탕종 블루베리 베이글', 2500, 8, 1, 0, 'EA', 1),
('탕종 파마산 치즈 베이글', 2500, 3, 1, 0, 'EA', 1),
('산타 베어리스타 케이크', 5800, 7, 1, 0, 'EA', 1),
('트리 레드벨벳 치즈 케이크', 5400, 2, 1, 0, 'EA', 1),
('고구마 카스텔라 생크림 케이크', 5000, 9, 1, 0, 'EA', 1),
('밀레앙 스타벅스 플랑', 5800, 4, 1, 0, 'EA', 1),
('클래식 피낭시에', 2500, 1, 1, 0, 'EA', 1),
('진한 가나슈 9 레이어 케이크', 4800, 6, 1, 0, 'EA', 1),
('블루베리 마블 치즈 케이크', 5100, 10, 1, 0, 'EA', 1),
('부드러운 생크림 카스텔라', 3200, 3, 1, 0, 'EA', 1),
('필리 치즈 브리오슈 샌드위치', 5500, 7, 1, 0, 'EA', 1),
('멜팅 치즈 햄 & 어니언 샌드위치', 4800, 2, 1, 0, 'EA', 1),
('베이컨 체다 & 오믈렛 샌드위치', 4100, 9, 1, 0, 'EA', 1),
('바질 토마토 탕종 베이글 샌드위치', 4100, 6, 1, 0, 'EA', 1),
('단호박 에그 샐러드 샌드위치', 3800, 4, 1, 0, 'EA', 1),
('치킨 베이컨 랩', 4600, 1, 1, 0, 'EA', 1),
('B.E.L.T 샌드위치', 4100, 8, 1, 0, 'EA', 1),
('베이컨 치즈 토스트', 3400, 5, 1, 0, 'EA', 1),
('비아 크리스마스 블렌드 12개입', 10200, 10, 1, 0, 'EA', 1),
('SS 스탠리 크림 캔처 텀블러', 27300, 3, 1, 1, 'EA', 1),
('JNM 하우스 보온병', 32200, 6, 1, 1, 'EA', 1),
('스타벅스 하우스 에코백', 14000, 1, 1, 1, 'EA', 1),
('SS 뉴트럴 밸류 텀블러', 23100, 8, 1, 1, 'EA', 1),
('크림 컴프레소', 47600, 2, 1, 1, 'EA', 1),
('사이렌 레버 드리퍼', 24500, 9, 1, 1, 'EA', 1),
('우드 핸들 글라스 서버', 25200, 5, 1, 1, 'EA', 1),
('사이렌 글라스 컨테이너', 28700, 4, 1, 1, 'EA', 1),
('사이렌 하우스 머그', 14000, 10, 1, 1, 'EA', 1),
('스타벅스 헤이즐넛 시럽', 6300, 7, 1, 1, 'EA', 1),
('스타벅스 바닐라 시럽', 6300, 3, 1, 1, 'EA', 1),
('슬리브 (프로모션 슬리브)', 6300, 142, 1, 2, 'EA', 1),
('슬리브 (기본 슬리브)', 6300, 198, 1, 2, 'EA', 1),
('빨대 (기본 빨대)', 2800, 157, 1, 2, 'EA', 1),
('빨대 (두꺼운 빨대)', 3500, 173, 1, 2, 'EA', 1),
('빨대 (V 빨대)', 4200, 124, 1, 2, 'EA', 1),
('테이크아웃 컵 (T)', 4900, 182, 1, 2, 'EA', 1),
('테이크아웃 컵 (G)', 5600, 135, 1, 2, 'EA', 1),
('테이크아웃 컵 (V)', 6300, 190, 1, 2, 'EA', 1),
('꿀', 12000, 1000, 1, 3, 'ml', 1),
('더블 스트렝스 민트 티', 9700, 20, 1, 3, 'EA', 1),
('두유', 9700, 1000, 1, 3, 'ml', 1),
('딸기 리프레셔 믹스', 12100, 1000, 1, 3, 'ml', 1),
('딸기 슬라이스', 5000, 1000, 1, 3, 'g', 1),
('레모네이드', 11600, 1000, 1, 3, 'ml', 1),
('레몬 슬라이스', 5000, 1000, 1, 3, 'g', 1),
('로즈마리', 12900, 1000, 1, 3, 'g', 1),
('리얼요거트', 10700, 1000, 1, 3, 'ml', 1),
('리치농축액', 9400, 1000, 1, 3, 'ml', 1),
('라이트 핑크 자몽 베이스', 12200, 1000, 1, 3, 'ml', 1),
('라임 슬라이스', 5000, 1000, 1, 3, 'g', 1),
('망고 리프레셔 베이스', 10300, 1000, 1, 3, 'ml', 1),
('망고 용과 슬라이스', 5000, 1000, 1, 3, 'g', 1),
('망고주스', 11200, 1000, 1, 3, 'ml', 1),
('말차파우더', 12700, 1000, 1, 3, 'g', 1),
('민트 블렌드 티백', 6300, 20, 1, 3, 'EA', 1),
('바나나', 9500, 20, 1, 3, 'EA', 1),
('바닐라 크림', 12600, 1000, 1, 3, 'ml', 1),
('바닐라 시럽', 9400, 1000, 1, 3, 'ml', 1),
('블랙티 티백', 6300, 20, 1, 3, 'EA', 1),
('뱅쇼 베이스', 12500, 1000, 1, 3, 'ml', 1),
('시그니처 초콜릿', 11800, 1000, 1, 3, 'g', 1),
('에스프레소 크림', 11400, 1000, 1, 3, 'ml', 1),
('에스프레소 칩', 11000, 1000, 1, 3, 'g', 1),
('얼그레이 티백', 6300, 20, 1, 3, 'EA', 1),
('오렌지 슬라이스', 5000, 1000, 1, 3, 'g', 1),
('오트밀', 4000, 1000, 1, 3, 'ml', 1),
('유기농 말차파우더', 12100, 1000, 1, 3, 'g', 1),
('유자진저 베이스', 12900, 1000, 1, 3, 'ml', 1),
('유스베리 티백', 6300, 20, 1, 3, 'EA', 1),
('연유', 4000, 1000, 1, 3, 'ml', 1),
('잉글리시 브렉퍼스트 티백', 6300, 20, 1, 3, 'EA', 1),
('자몽 슬라이스', 5000, 1000, 1, 3, 'g', 1),
('자바칩', 8500, 1000, 1, 3, 'g', 1),
('제주 유기 녹차 티백', 6300, 20, 1, 3, 'EA', 1),
('초콜릿 시럽', 11800, 1000, 1, 3, 'ml', 1),
('초콜릿파우더', 9400, 1000, 1, 3, 'g', 1),
('캬라멜소스', 10800, 1000, 1, 3, 'ml', 1),
('캬라멜시럽', 9000, 1000, 1, 3, 'ml', 1),
('캐모마일 티백', 6300, 20, 1, 3, 'EA', 1),
('쿨라임 베이스', 10600, 1000, 1, 3, 'ml', 1),
('토피넛 파우더', 12200, 1000, 1, 3, 'g', 1),
('프라푸치노 로스트', 9700, 1000, 1, 3, 'g', 1),
('프라푸치노용 시럽', 11500, 1000, 1, 3, 'ml', 1),
('패션 탱고 티', 11100, 1000, 1, 3, 'ml', 1),
('프레시 딸기 베이스', 12700, 1000, 1, 3, 'ml', 1),
('우유',3000,1000,1,3,'ml',1),
('무지방우유',3000,1000,1,3,'ml',1),
('저지방우유',3000,1000,1,3,'ml',1),
('휘핑크림', 5500, 1000, 1, 3, 'ml', 1),
('흑당시럽', 11200, 1000, 1, 3, 'ml', 1),
('콜드브루 원두', 8000, 1000, 1, 3, 'g', 1),
('일반 원두', 8000, 1000, 1, 3, 'g', 1),
('블론드 원두', 8000, 1000, 1, 3, 'g', 1),
('디카페인 원두', 8000, 1000, 1, 3, 'g', 1),
('수박주스', 3200, 7, 1, 4, 'EA', 1),
('스퀴드즈 오렌지 주스', 2700, 2, 1, 4, 'EA', 1),
('딸기주스', 3200, 8, 1, 4, 'EA', 1),
('햇사과 주스', 3300, 5, 1, 4, 'EA', 1),
('한라봉 주스', 3300, 3, 1, 4, 'EA', 1),
('유기농 스파클링 애플 주스', 3400, 9, 1, 4, 'EA', 1),
('딸기 가득 요거트', 2900, 1, 1, 4, 'EA', 1),
('블루베리 요거트', 3200, 6, 1, 4, 'EA', 1),
('ABC 클렌즈', 3300, 4, 1, 4, 'EA', 1),
('케일클렌즈', 3300, 10, 1, 4, 'EA', 1),
('에비앙', 2300, 2, 1, 4, 'EA', 1),
('페리에 라임', 2300, 8, 1, 4, 'EA', 1),
('유기농 오렌지 100% 주스',4500,20,1,4,'EA',1),
('크리스마스 블렌드', 14000, 3, 1, 5, 'EA', 1),
('크리스마스 블론드 로스트', 14000, 7, 1, 5, 'EA', 1),
('별다방 블렌드', 14000, 1, 1, 5, 'EA', 1),
('베란다 블렌드', 12600, 9, 1, 5, 'EA', 1),
('크리스마스 블렌드 에스프레소 로스트', 14000, 5, 1, 5, 'EA', 1),
('하우스 블렌드', 12600, 4, 1, 5, 'EA', 1),
('파이크 플레이스 로스트', 12600, 6, 1, 5, 'EA', 1),
('콜롬비아', 14000, 2, 1, 5, 'EA', 1),
('케냐', 14000, 8, 1, 5, 'EA', 1),
('에티오피아', 14000, 10, 1, 5, 'EA', 1),
('디카페인 하우스 블렌드', 14000, 3, 1, 5, 'EA', 1),
('탕종 플레인 베이글', 2300, 0, 0, 0, 'EA', 1),
('탕종 블루베리 베이글', 2500, 0, 0, 0, 'EA', 1),
('탕종 파마산 치즈 베이글', 2500, 0, 0, 0, 'EA', 1),
('산타 베어리스타 케이크', 5800, 0, 0, 0, 'EA', 1),
('트리 레드벨벳 치즈 케이크', 5400, 0, 0, 0, 'EA', 1),
('고구마 카스텔라 생크림 케이크', 5000, 0, 0, 0, 'EA', 1),
('밀레앙 스타벅스 플랑', 5800, 0, 0, 0, 'EA', 1),
('클래식 피낭시에', 2500, 0, 0, 0, 'EA', 1),
('진한 가나슈 9 레이어 케이크', 4800, 0, 0, 0, 'EA', 1),
('블루베리 마블 치즈 케이크', 5100, 0, 0, 0, 'EA', 1),
('부드러운 생크림 카스텔라', 3200, 0, 0, 0, 'EA', 1),
('필리 치즈 브리오슈 샌드위치', 5500, 0, 0, 0, 'EA', 1),
('멜팅 치즈 햄 & 어니언 샌드위치', 4800, 0, 0, 0, 'EA', 1),
('베이컨 체다 & 오믈렛 샌드위치', 4100, 0, 0, 0, 'EA', 1),
('바질 토마토 탕종 베이글 샌드위치', 4100, 0, 0, 0, 'EA', 1),
('단호박 에그 샐러드 샌드위치', 3800, 0, 0, 0, 'EA', 1),
('치킨 베이컨 랩', 4600, 0, 0, 0, 'EA', 1),
('B.E.L.T 샌드위치', 4100, 0, 0, 0, 'EA', 1),
('베이컨 치즈 토스트', 3400, 0, 0, 0, 'EA', 1),
('비아 크리스마스 블렌드 12개입', 10200, 0, 0, 0, 'EA', 1),
('SS 스탠리 크림 캔처 텀블러', 27300, 0, 0, 1, 'EA', 1),
('JNM 하우스 보온병', 32200, 0, 0, 1, 'EA', 1),
('스타벅스 하우스 에코백', 14000, 0, 0, 1, 'EA', 1),
('SS 뉴트럴 밸류 텀블러', 23100, 0, 0, 1, 'EA', 1),
('크림 컴프레소', 47600, 0, 0, 1, 'EA', 1),
('사이렌 레버 드리퍼', 24500, 0, 0, 1, 'EA', 1),
('우드 핸들 글라스 서버', 25200, 0, 0, 1, 'EA', 1),
('사이렌 글라스 컨테이너', 28700, 0, 0, 1, 'EA', 1),
('사이렌 하우스 머그', 14000, 0, 0, 1, 'EA', 1),
('스타벅스 헤이즐넛 시럽', 6300, 0, 0, 1, 'EA', 1),
('스타벅스 바닐라 시럽', 6300, 0, 0, 1, 'EA', 1),
('슬리브 (프로모션 슬리브)', 6300, 0, 0, 2, 'EA', 1),
('슬리브 (기본 슬리브)', 6300, 0, 0, 2, 'EA', 1),
('빨대 (기본 빨대)', 2800, 0, 0, 2, 'EA', 1),
('빨대 (두꺼운 빨대)', 3500, 0, 0, 2, 'EA', 1),
('빨대 (V 빨대)', 4200, 0, 0, 2, 'EA', 1),
('테이크아웃 컵 (T)', 4900, 0, 0, 2, 'EA', 1),
('테이크아웃 컵 (G)', 5600, 0, 0, 2, 'EA', 1),
('테이크아웃 컵 (V)', 6300, 0, 0, 2, 'EA', 1),
('꿀', 12000, 0, 0, 3, 'ml', 1),
('더블 스트렝스 민트 티', 9700, 0, 0, 3, 'EA', 1),
('두유', 9700, 0, 0, 3, 'ml', 1),
('딸기 리프레셔 믹스', 12100, 0, 0, 3, 'ml', 1),
('딸기 슬라이스', 5000, 0, 0, 3, 'g', 1),
('레모네이드', 11600, 0, 0, 3, 'ml', 1),
('레몬 슬라이스',5000, 0, 0, 3, 'g', 1),
('로즈마리', 12900, 0, 0, 3, 'g', 1),
('리얼요거트', 10700, 0, 0, 3, 'ml', 1),
('리치농축액', 9400, 0, 0, 3, 'ml', 1),
('라이트 핑크 자몽 베이스', 12200, 0, 0, 3, 'ml', 1),
('라임 슬라이스', 5000, 0, 0, 3, 'g', 1),
('망고 리프레셔 베이스', 10300, 0, 0, 3, 'ml', 1),
('망고 용과 슬라이스', 5000, 0, 0, 3, 'g', 1),
('망고주스', 11200, 0, 0, 3, 'ml', 1),
('말차파우더', 12700, 0, 0, 3, 'g', 1),
('민트 블렌드 티백', 6300, 0, 0, 3, 'EA', 1),
('바나나', 9500, 0, 0, 3, 'EA', 1),
('바닐라 크림', 12600, 0, 0, 3, 'ml', 1),
('바닐라 시럽', 9400, 0, 0, 3, 'ml', 1),
('블랙티 티백', 6300, 0, 0, 3, 'EA', 1),
('뱅쇼 베이스', 12500, 0, 0, 3, 'ml', 1),
('시그니처 초콜릿', 11800, 0, 0, 3, 'g', 1),
('에스프레소 크림', 11400, 0, 0, 3, 'ml', 1),
('에스프레소 칩', 11000, 0, 0, 3, 'g', 1),
('얼그레이 티백', 6300, 0, 0, 3, 'EA', 1),
('오렌지 슬라이스', 5000, 0, 0, 3, 'g', 1),
('오트밀', 4000, 0, 0, 3, 'ml', 1),
('유기농 말차파우더', 12100, 0, 0, 3, 'g', 1),
('유자진저 베이스', 12900, 0, 0, 3, 'ml', 1),
('유스베리 티백', 6300, 0, 0, 3, 'EA', 1),
('자몽 슬라이스', 5000, 0, 0, 3, 'g', 1),
('자스민 티백', 6300, 0, 0, 3, 'EA', 1),
('정통 인도카레 소스', 9900, 0, 0, 3, 'ml', 1),
('초콜릿 시럽', 11500, 0, 0, 3, 'ml', 1),
('초코칩', 9200, 0, 0, 3, 'g', 1),
('페퍼민트', 9400, 0, 0, 3, 'g', 1),
('플레인 요거트', 10700, 0, 0, 3, 'ml', 1),
('홍차 시럽', 10700, 0, 0, 3, 'ml', 1),
('흑설탕', 10100, 0, 0, 3, 'g', 1),
('히비스커스', 8700, 0, 0, 3, 'g', 1),
('우유',3000,0,0,3,'ml',1),
('무지방우유',3000,0,0,3,'ml',1),
('저지방우유',3000,0,0,3,'ml',1),
('레드 커런트', 4800,1000,1, 3, 'g', 1),
('레드 커런트', 4800, 0,0, 3, 'g', 1),
('수박주스', 3200, 7, 1, 4, 'EA', 1),
('스퀴드즈 오렌지 주스', 2700, 2, 1, 4, 'EA', 1),
('딸기주스', 3200, 8, 1, 4, 'EA', 1),
('햇사과 주스', 3300, 5, 1, 4, 'EA', 1),
('한라봉 주스', 3300, 3, 1, 4, 'EA', 1),
('유기농 스파클링 애플 주스', 3400, 9, 1, 4, 'EA', 1),
('딸기 가득 요거트', 2900, 1, 1, 4, 'EA', 1),
('블루베리 요거트', 3200, 6, 1, 4, 'EA', 1),
('ABC 클렌즈', 3300, 4, 1, 4, 'EA', 1),
('케일클렌즈', 3300, 10, 1, 4, 'EA', 1),
('에비앙', 2300, 2, 1, 4, 'EA', 1),
('페리에 라임', 2300, 8, 1, 4, 'EA', 1),
('유기농 오렌지 100% 주스',4500,20,1,4,'EA',1),
('크리스마스 블렌드', 14000, 3, 1, 5, 'EA', 1),
('크리스마스 블론드 로스트', 14000, 7, 1, 5, 'EA', 1),
('별다방 블렌드', 14000, 1, 1, 5, 'EA', 1),
('베란다 블렌드', 12600, 9, 1, 5, 'EA', 1),
('크리스마스 블렌드 에스프레소 로스트', 14000, 5, 1, 5, 'EA', 1),
('하우스 블렌드', 12600, 4, 1, 5, 'EA', 1),
('파이크 플레이스 로스트', 12600, 6, 1, 5, 'EA', 1),
('콜롬비아', 14000, 2, 1, 5, 'EA', 1),
('케냐', 14000, 8, 1, 5, 'EA', 1),
('에티오피아', 14000, 10, 1, 5, 'EA', 1),
('디카페인 하우스 블렌드', 14000, 3, 1, 5, 'EA', 1),
('허니 자몽 소스',10000,1000,1,3,'ml',1),
('허니 자몽 소스',10000,0,0,3,'ml',1),
('히비스커스',10000,20,1,3,'EA',1),
('스퀴즈드 오렌지 주스',3800,10,1,4,'EA',1),
('유기농 오렌지 100% 주스',4900,10,1, 4,'EA',1),
('딸기 가득 요거트',4700,10,1,4,'EA', 1),
('햇사과 주스',4700,10,1,4,'EA',1),
('망고 주스',3800,10,1,4,'EA', 1),
('한라봉 주스',4700,10,1,4,'EA', 1),
('블루베리 요거트',3800,10,1,4,'EA', 1),
('ABC 클렌즈',4700,10,1,4,'EA', 1),
('케일 클렌즈',4700,10,1,4,'EA', 1),
('에비앙',3300,10,1,4,'EA', 1),
('페리에 라임',3300,10,1,4,'EA', 1),
('유기농 스파클링 애플 주스',4800,10,1,4,'EA',1);

-- 제조 상품 카테고리
INSERT INTO prdcg(prdcg_name) VALUES
('NEW'),
('에스프레소'),
('콜드브루'),
('블론드'),
('디카페인'),
('티바나'),
('피지오'),
('리프레셔'),
('프라푸치노'),
('블렌디드'),
('기타');

-- 제조 상품
INSERT INTO products (p_name, p_price, prdcg_no) VALUES
('토피넛 라떼',6500,2),
('더블 에스프레소 크림 라떼',6500,2),
('플랫 화이트',5600,2),
('카페 아메리카노',4500,2),
('카페라떼',5000,2),
('스타벅스 돌체 라떼',5900,2),
('카페 모카',5500,2),
('카푸치노',5000,2),
('캬라멜 마키아또',5900,2),
('화이트 초콜릿 모카',5900,2),
('커피 스타벅스 더블 샷',5100,2),
('바닐라 스타벅스 더블 샷',5100,2),
('에스프레소',3700,2),
('에스프레소 마키아또',3700,2),
('에스프레소 콘 파나',3700,2),
('콜드 브루',4900,3),
('돌체 콜드 브루',6000,3),
('바닐라 크림 콜드 브루',5800,3),
('오트 콜드 브루',5800,3),
('블론드 플랫 화이트',5600,4),
('블론드 카페 아메리카노',4500,4),
('블론드 카페 라떼',5000,4),
('블론드 바닐라 더블 샷 마키아또',5900,4),
('블론드 스타벅스 돌체 라떼',5900,4),
('디카페인 플랫 화이트', 5900,5),
('디카페인 카페 아메리카노', 4800,5),
('디카페인 카페 라떼', 5300,5),
('디카페인 스타벅스 돌체 라떼', 6200,5),
('디카페인 카라멜 마키아또', 6200,5),
('골든 캐모마일 릴렉서', 6200,6),
('논알코올 홀리데이 패션 티 뱅쇼', 6700,6),
('얼 그레이 바닐라 티 라떼', 6100,6),
('스타벅스 클래식 밀크 티', 5900,6),
('차이 티 라떼', 5000,6),
('제주 말차 라떼', 6100,6),
('유자 민트 티', 5900,6),
('자몽 허니 블랙 티', 5700,6),
('제주 유기농 녹차로 만든 티', 5300,6),
('잉글리쉬 브렉퍼스트 티', 4500,6),
('얼그레이 티', 4500,6),
('유스베리 티', 6450,6),
('히비스커스 블렌드 티', 4500,6),
('민트 블렌드 티', 4500,6),
('라이트 핑크 자몽 피지오',6300,7),
('쿨 라임 피지오',6100,7),
('딸기 아사이 레모네이드 스타벅스 리프레셔',6100,8),
('망고 용과 레모네이드 스타벅스 리프레셔',6100,8),
('더블 에스프레소 칩 프라푸치노',6500,9),
('제주 말차 크림 프라푸치노',6500,9),
('자바 칩 프라푸치노',6500,9),
('초콜릿 크림 칩 프라푸치노',6500,9),
('카라멜 프라푸치노',6500,9),
('에스프레소 프라푸치노',6500,9),
('딸기 딜라이트 요거트 블렌디드',6500,10),
('망고 바나나 블렌디드',6600,10),
('망고 패션 티 블렌디드',5600,10),
('스타 벅스 딸기 라떼',6500,11),
('시그니처 핫 초콜릿',5700,11),
('스팀 우유',4100,11),
('우유',4100,11);

-- 제조 상품_원자재 중개
INSERT INTO prd_stock (p_no, st_no, pst_consume) VALUES
(1, 1, 36),
(2, 1, 36),
(2, 2, 25),
(2, 3, 10),
(2, 4, 300);

-- 제조상품옵션 카테고리
INSERT INTO opt_category (st_no, category_name) VALUES
(NULL, '사이즈'),
(1, '샷'),
(2, '휘핑크림'),
(3, '시럽'),
(4, '우유');

-- 제조상품옵션
INSERT INTO opt (opt_name, opt_price, opt_consume, category_no) VALUES
('톨(355ml)', 0, 18, 1),
('그란데(473ml)', 800, 36, 1),
('벤티(591ml)', 1600, 54, 1),
('샷', 500, 18, 2),
('시럽', 0, 15, 4),
('일반', 0, 300, 5),
('저지방', 0, 300, 5),
('무지방', 0, 300, 5),
('두유', 0, 300, 5),
('오트(귀리)', 800, 300, 5),
('휘핑 없이', 0, 0, 3),
('휘핑 적게', 0, 10, 3),
('휘핑 많이', 500, 20, 3);

-- 제조상품_옵션 카테고리 중개 테이블
INSERT INTO prd_optcg(category_no, p_no, optcg_name) VALUES
(1, 1, '사이즈'), 
(2, 1, '샷'), 
(3, 1, '휘핑크림'), 
(4, 1, '시럽'), 
(5, 1, '우유');

INSERT INTO prd_optcg(category_no, p_no, optcg_name) VALUES
(1, 2, '사이즈'), 
(2, 2, '샷'), 
(3, 2, '휘핑크림'), 
(4, 2, '시럽'), 
(5, 2, '우유');

-- 주문 테이블
INSERT INTO orders (orders_quantity, orders_total, users_no) VALUES
(1, 4500, 3),
(3, 12900, 3),
(8, 41000, 3);

-- 주문_제조상품 테이블
INSERT INTO orders_prd (orders_no, p_no, opd_quantity) VALUES
(2, 1, 1),
(2, 2, 3);

-- 주문_옵션 중개테이블
INSERT INTO orders_opt (orders_no, p_no, opt_no, oropt_name, oropt_price, oropt_quantity) VALUES
(2, 2, 4, '샷', 500, 1),
(2, 2, 6, '일반', 0, 1),
(2, 2, 13, '휘핑 많이', 500, 1);

-- 주문_재고물품 중개 테이블
INSERT INTO orders_stock (orders_no, st_no, ost_quantity) VALUES
(1, 1, 1),
(2, 2, 3),
(3, 3, 8);

-- 발주 테이블
INSERT INTO place_orders (po_total, users_no) VALUES
(300000, 2),
(200000, 2),
(250000, 2);

-- 발주 - 재고물품의 중간 테이블
INSERT INTO place_orders_stock (po_no, st_no, post_quantity) VALUES
(1, 3, 200),
(1, 5, 50),
(1, 7, 10),
(2, 1, 50),
(2, 2, 50),
(3, 4, 70),
(3, 6, 40),
(3, 8, 250);

-- 회원 - 재고물품의 중간 테이블(장바구니 테이블)
INSERT INTO place_orders_basket (users_no, st_no, pob_quantity) VALUES
(2, 4, 80),
(2, 1, 30),
(2, 7, 5),
(2, 3, 30),
(2, 2, 50);

INSERT INTO place_orders_basket (users_no, st_no, pob_quantity) VALUES
(2, 7, 5);

########################
######## SELECT ########
########################

-- SELECT * FROM users;
-- SELECT * FROM stock;
-- SELECT * FROM place_orders;
-- SELECT * FROM place_orders_stock;
-- SELECT * FROM place_orders_basket;
-- SELECT * FROM products;
-- SELECT * FROM prd_optcg;


-- -- 재고 전체 조회
-- SELECT st_no, st_name, st_quantity, st_category, st_unit
-- FROM stock
-- WHERE st_owner = 1;

-- -- 지점 재고 카테고리별 조회
-- SELECT st_no, st_name, st_quantity, st_category, st_unit
-- FROM stock
-- WHERE st_owner = 1 and st_category = 4;

-- -- 지점 재고 키워드 검색
-- SELECT st_no, st_name, st_quantity, st_category, st_unit
-- FROM stock
-- WHERE st_owner = 1 and st_name LIKE '%크림%';

-- -- 발주 내역 전체 조회
-- SELECT post.po_no, post.st_no, s.st_name, s.st_price, post.post_quantity, (s.st_price * post.post_quantity) AS sub_total, s.st_category, po.po_date, u.users_name
-- FROM place_orders_stock post
-- INNER JOIN stock s
-- ON post.st_no = s.st_no
-- INNER JOIN place_orders po
-- ON post.po_no = po.po_no
-- INNER JOIN users u
-- ON po.users_no = u.users_no;

-- -- 발주 장바구니 담은 품목 조회
-- SELECT pob.st_no, s.st_name, s.st_price, pob.pob_quantity, (s.st_price * pob.pob_quantity) AS pob_price, s.st_category, s.st_unit
-- FROM place_orders_basket pob
-- INNER JOIN stock s ON pob.st_no = s.st_no
-- INNER JOIN users u ON pob.users_no = u.users_no
-- WHERE u.users_no = 2;

-- -- 품목번호로 품목 조회
-- SELECT pob.st_no, s.st_name, s.st_price, pob.pob_quantity, (s.st_price * pob.pob_quantity) AS pob_price, s.st_category, s.st_unit
-- FROM place_orders_basket pob
-- INNER JOIN stock s ON pob.st_no = s.st_no
-- INNER JOIN users u ON pob.users_no = u.users_no
-- WHERE pob.st_no = 2 AND u.users_no = 2;

-- -- 장바구니 품목 수량 수정
-- UPDATE place_orders_basket
-- SET pob_quantity = 10
-- WHERE st_no = 7;

-- -- 장바구니 품목 삭제
-- DELETE FROM place_orders_basket
-- WHERE users_no = 2 AND st_no = 7;

-- -- 품목 옵션 선택
-- SELECT p.p_no, p.p_name, o.category_no, o.optcg_name, ot.opt_name, ot.opt_price -- 품목번호, 품목명, 카테고리 번호, 카테고리명, 옵션명, 옵션가격 출력
-- FROM products p
-- JOIN prd_optcg o
-- ON o.p_no = p.p_no
-- JOIN opt_category oc
-- ON o.category_no = oc.category_no
-- JOIN opt ot
-- ON ot.category_no = oc.category_no
-- WHERE p.p_no = 1 -- 품목 번호
-- ORDER BY o.category_no, ot.opt_price; -- 1순위 : 카테고리 번호, 2순위 : 옵션 가격 으로 오름차순 정렬

-- -- 제조상품 및 관련 옵션 주문 후 원자재 별 총 소모량 산출하는 쿼리문

-- SELECT 
--     st_no,
--     st_name,
--     SUM(production_consume) AS "주문 상품 기본 소모량 총합",
--     SUM(option_consume) AS "주문 상품 옵션 소모량 총합",
--     SUM(production_consume + option_consume) AS "총 소모량"
-- FROM (
--     -- 제조 상품 소모량
--     SELECT 
--         s.st_no,
--         s.st_name,
--         SUM(pst.pst_consume) AS production_consume,
--         0 AS option_consume -- 옵션 소모량은 0으로 채움
--     FROM orders o
--     JOIN orders_production op ON op.orders_no = o.orders_no
--     JOIN production p ON p.p_no = op.p_no
--     LEFT JOIN production_stock pst ON pst.p_no = p.p_no
--     LEFT JOIN stock s ON s.st_no = pst.st_no
--     WHERE o.orders_no = 2
--     GROUP BY s.st_no, s.st_name
--     
--     UNION ALL
--     
--     -- 제조 상품 옵션 소모량
--     SELECT 
--         s.st_no,
--         s.st_name,
--         0 AS production_consume, -- 제조 상품 소모량은 0으로 채움
--         SUM(opt.opt_consume) AS option_consume
--     FROM orders o
--     JOIN orders_production op ON op.orders_no = o.orders_no
--     JOIN orders_opt oopt ON oopt.p_no = op.p_no
--     LEFT JOIN opt opt ON opt.opt_no = oopt.opt_no
--     LEFT JOIN opt_category oc ON oc.category_no = opt.category_no
--     JOIN stock s ON oc.st_no = s.st_no
--     WHERE o.orders_no = 2
--     GROUP BY s.st_no, s.st_name
-- ) AS combined
-- GROUP BY st_no, st_name
-- ORDER BY st_no;