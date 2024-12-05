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


-- 주문 테이블
-- INSERT INTO orders (orders_quantity, orders_total, users_no) VALUES
-- (3, 22300, 3);

-- 주문_제조상품 테이블
-- INSERT INTO orders_prd (orders_no, p_no, opd_quantity) VALUES
-- (1, 1, 1),
-- (1, 2, 2),
-- (1, 2, 1);

-- 주문_옵션 중개테이블
-- INSERT INTO orders_opt (ord_prd_no, opt_no, oropt_name, oropt_price, oropt_quantity) VALUES
-- (3, 4, '샷', 500, 1),
-- (3, 6, '일반', 0, 1),
-- (3, 13, '휘핑 많이', 500, 1);

-- 주문_재고물품 중개 테이블
-- INSERT INTO orders_stock (orders_no, st_no, ost_quantity) VALUES
-- (1, 5, 1);




########################
######## SELECT ########
########################
                
                