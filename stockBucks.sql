drop database stockbucks;

create database stockbucks;

use stockbucks;

### 테이블 생성 ###

# 유저 테이블
CREATE TABLE IF NOT EXISTS users(
    users_no int Primary Key auto_increment, -- 사용자 번호
    users_id varchar(100) not null unique, -- 유저 아이디
    users_pw varchar(100) not null, -- 유저 비밀번호
    users_role int not null, -- 유저역할 (0: 본사, 1: 매니저, 2: 사원)
    users_name varchar(100) not null, -- 유저명
    is_active tinyint not null Default 1, -- 활성화 여부
    users_birth varchar(100) not null -- 생년월일
);

# 재고물품 테이블
CREATE TABLE IF NOT EXISTS stock (
	st_no INT PRIMARY KEY AUTO_INCREMENT, -- 재고물품 고유번호
    st_name VARCHAR(100) NOT NULL unique, -- 재고물품명
    st_price INT NOT NULL, -- 가격 (단위 : 원)
    st_quantity INT NOT NULL Default 0, -- 재고수량
    st_owner INT NOT NULL, -- 구분자 (0: 본사, 1: 지점)
    st_category INT NOT NULL, -- 카테고리(디저트 : 0 /  MD : 1 / 일회용품: 2  /원자재: 3 / 병음료 : 4 / 원두 : 5)
    st_unit VARCHAR(100) NOT NULL Default 'EA', -- 단위(재고 수량 단위(kg, g, l, ml, EA 등))
    st_state TINYINT NOT NULL Default 1 -- 발주가능여부(발주 가능 여부(true/false))
);

# 발주 테이블
CREATE TABLE IF NOT EXISTS place_orders (
	po_no INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    po_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    po_total INT NOT NULL,
    users_no INT,
    FOREIGN KEY (users_no) REFERENCES users (users_no) ON UPDATE CASCADE -- users 테이블 user_no와 외래키 설정
);

# 발주 - 재고물품의 중간 테이블
CREATE TABLE IF NOT EXISTS place_orders_stock (
	po_no INT,
    st_no INT,
    post_quantity INT NOT NULL,
    PRIMARY KEY (po_no, st_no),
    FOREIGN KEY (po_no) REFERENCES place_orders (po_no) ON UPDATE CASCADE,
    FOREIGN KEY (st_no) REFERENCES stock (st_no) ON UPDATE CASCADE
);

# 회원 - 재고물품의 중간 테이블
CREATE TABLE IF NOT EXISTS place_orders_basket (
	st_no INT,
    users_no INT,
    PRIMARY KEY (st_no, users_no),
    FOREIGN KEY (st_no) REFERENCES stock (st_no) ON UPDATE CASCADE,
    FOREIGN KEY (users_no) REFERENCES users (users_no) ON UPDATE CASCADE
);

# 제조상품 카테고리
CREATE TABLE IF NOT EXISTS prdcg (
	prdcg_no int Primary Key auto_increment, -- 제조상품카테고리번호
    prdcg_name varchar(100) not null unique, -- 카테고리명
    prdcg_state tinyint not null Default 1 -- 카테고리 활성화 여부(true/false)
);

# 제조상품
CREATE TABLE IF NOT EXISTS production (
	p_no int Primary Key auto_increment, -- 제조상품ID
    p_name varchar(100) not null unique, -- 제조상품이름
    p_price int not null, -- 제조상품가격
    p_state tinyint not null Default 1, -- 제조상품판매 가능 여부
    prdcg_no int not null, -- 제조상품 카테고리 번호
    p_releasesDate timestamp default current_timestamp, -- 제조상품 출시일
    
    FOREIGN KEY (prdcg_no) references prdcg(prdcg_no) ON UPDATE CASCADE
);

# 제조상품_원자재 중개 테이블
CREATE TABLE IF NOT EXISTS production_stock (
	p_no int not null, -- 제조상품
    st_no int not null, -- 잡화 고유번호
    pst_consume int not null, -- 제조 시 해당 원자재 소모량
    
    primary key (p_no, st_no),
	FOREIGN KEY (p_no) REFERENCES production(p_no) ON UPDATE CASCADE,
    FOREIGN KEY (st_no) REFERENCES stock(st_no) ON UPDATE CASCADE
);

# 옵션 카테고리
CREATE TABLE IF NOT EXISTS opt_category ( 
	category_no int Primary Key auto_increment, -- 카테고리 번호
    st_no int, -- 재고물품 고유번호
    category_name varchar(100) unique not null, -- 카테고리명
    
    FOREIGN KEY (st_no) REFERENCES stock(st_no) ON UPDATE CASCADE
);

# 제조상품옵션
CREATE TABLE IF NOT EXISTS opt ( 
	opt_no int primary Key auto_increment, -- 옵션번호
    opt_name varchar(100) not null unique, -- 옵션명
    opt_price int not null Default 0, -- 옵션가격
    opt_consume int, -- 옵션 추가 시 원자재 소모량
    category_no int not null, -- 카테고리 번호
    is_active tinyint not null Default 1, -- 활성화 여부(true/false)
    
    foreign key(category_no) references opt_category(category_no) ON UPDATE CASCADE
);

# 제조상품_옵션 카테고리 중개 테이블
CREATE TABLE IF NOT EXISTS prd_optcg (
	category_no int not null, -- 카테고리 번호
    p_no int not null, -- 제조상품
    optcg_name varchar(100) not null, -- 옵션카테고리명
    
	primary key(category_no, p_no)
);

# 주문_제조상품 중개 테이블
CREATE TABLE IF NOT EXISTS orders_production (
	orders_no int not null, -- 주문번호
    p_no int not null, -- 제조상품ID
    opd_quantity int not null Default 1, -- 주문수량
    
    primary key(orders_no, p_no)
);

# 주문 테이블
CREATE TABLE IF NOT EXISTS orders (
	orders_no int Primary key auto_increment, -- 주문번호
    orders_date timestamp default current_timestamp, -- 주문일시
    orders_quantity int not null, -- 주문수량
    orders_total decimal not null, -- 주문총액
    users_no int not null, -- 주문자 번호
    
    foreign key (users_no) references users(users_no)
);

# 주문_옵션 중개테이블
CREATE TABLE IF NOT EXISTS orders_opt (
	orders_no int not null, -- 주문번호
    p_no int not null, -- 제조상품
    opt_no int not null, -- 옵션번호
    oropt_name varchar(100) not null, -- 옵션명
    oropt_price int not null default 0, -- 옵션가격
    oropt_quantity int,
    
    primary key(orders_no, p_no, opt_no)
);

# 주문_재고물품 중개 테이블
CREATE TABLE IF NOT EXISTS orders_stock (
	orders_no int not null, -- 주문번호
    st_no int not null, -- 잡화 고유번호
    ost_quantity int not null Default 1, -- 주문수량
    
    primary key (orders_no, st_no)
);

### INSERT ###
-- 회원 
insert into users (users_id, users_pw, users_role, users_name, users_birth) values ('test1', '1234', 0, '김창훈', 980101);
insert into users (users_id, users_pw, users_role, users_name, users_birth) values ('test2', '1234', 1, '민소원', 020101);
insert into users (users_id, users_pw, users_role, users_name, users_birth) values ('test3', '1234', 2, '김동현', 960101);

-- 재고 물품
insert into stock (st_name, st_price, st_quantity, st_owner, st_category, st_unit, st_state) values ('원두', 30000, 100, 1, 3, 'g',1);
insert into stock (st_name, st_price, st_quantity, st_owner, st_category, st_unit, st_state) values ('크림', 30000, 100, 1, 3, 'g',1);
insert into stock (st_name, st_price, st_quantity, st_owner, st_category, st_unit, st_state) values ('시럽', 30000, 100, 1, 3, 'ml',1);
insert into stock (st_name, st_price, st_quantity, st_owner, st_category, st_unit, st_state) values ('우유', 30000, 100, 1, 3, 'ml',1);

-- 제조 상품 카테고리
insert into prdcg (prdcg_name) values ('에스프레소');
insert into prdcg (prdcg_name) values ('블론드');
insert into prdcg (prdcg_name) values ('티바나');

-- 제조 상품
insert into production (p_name, p_price, prdcg_no) values ('아메리카노', 4500, 1);
insert into production (p_name, p_price, prdcg_no) values ('블론드 플랫 화이트', 5600, 2);
insert into production (p_name, p_price, prdcg_no) values ('골든 캐모마일 릴렉서', 6300, 3);

-- 제조 상품_원자재 중개
insert into production_stock (p_no, st_no, pst_consume) values (1, 1, 36);

insert into production_stock (p_no, st_no, pst_consume) values (2, 1, 36);
insert into production_stock (p_no, st_no, pst_consume) values (2, 2, 25);
insert into production_stock (p_no, st_no, pst_consume) values (2, 3, 10);
insert into production_stock (p_no, st_no, pst_consume) values (2, 4, 300);

-- 제조상품옵션 카테고리
insert into opt_category (st_no, category_name) values (NULL, '사이즈');
insert into opt_category (st_no, category_name) values (1, '샷');
insert into opt_category (st_no, category_name) values (2, '휘핑크림');
insert into opt_category (st_no, category_name) values (3, '시럽');
insert into opt_category (st_no, category_name) values (4, '우유');

-- 제조상품옵션
insert into opt (opt_name, opt_price, opt_consume, category_no) values
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
insert into prd_optcg(category_no, p_no, optcg_name) values
(1, 1, '사이즈'), 
(2, 1, '샷'), 
(3, 1, '휘핑크림'), 
(4, 1, '시럽'), 
(5, 1, '우유');

insert into prd_optcg(category_no, p_no, optcg_name) values
(1, 2, '사이즈'), 
(2, 2, '샷'), 
(3, 2, '휘핑크림'), 
(4, 2, '시럽'), 
(5, 2, '우유');

-- 주문 테이블
insert into orders (orders_quantity, orders_total, users_no) values
(1, 4500, 3),
(3, 12900, 3),
(8, 41000, 3);

-- 주문_제조상품 테이블
insert into orders_production (orders_no, p_no, opd_quantity) values
(2, 1, 1),
(2, 2, 3);

-- 주문_옵션 중개테이블
insert into orders_opt (orders_no, p_no, opt_no, oropt_name, oropt_price, oropt_quantity) values
(2, 2, 4, '샷', 500, 1),
(2, 2, 6, '일반', 0, 1),
(2, 2, 13, '휘핑 많이', 500, 1);

-- 주문_재고물품 중개 테이블
insert into orders_stock (orders_no, st_no, ost_quantity) values
(1, 1, 1),
(2, 2, 3),
(3, 3, 8);

### select ###
-- 제조상품 및 관련 옵션 주문 후 원자재 별 총 소모량 산출하는 쿼리문

SELECT 
    st_no,
    st_name,
    SUM(production_consume) AS "주문 상품 기본 소모량 총합",
    SUM(option_consume) AS "주문 상품 옵션 소모량 총합",
    SUM(production_consume + option_consume) AS "총 소모량"
FROM (
    -- 제조 상품 소모량
    SELECT 
        s.st_no,
        s.st_name,
        SUM(pst.pst_consume) AS production_consume,
        0 AS option_consume -- 옵션 소모량은 0으로 채움
    FROM orders o
    JOIN orders_production op ON op.orders_no = o.orders_no
    JOIN production p ON p.p_no = op.p_no
    LEFT JOIN production_stock pst ON pst.p_no = p.p_no
    LEFT JOIN stock s ON s.st_no = pst.st_no
    WHERE o.orders_no = 2
    GROUP BY s.st_no, s.st_name
    
    UNION ALL
    
    -- 제조 상품 옵션 소모량
    SELECT 
        s.st_no,
        s.st_name,
        0 AS production_consume, -- 제조 상품 소모량은 0으로 채움
        SUM(opt.opt_consume) AS option_consume
    FROM orders o
    JOIN orders_production op ON op.orders_no = o.orders_no
    JOIN orders_opt oopt ON oopt.p_no = op.p_no
    LEFT JOIN opt opt ON opt.opt_no = oopt.opt_no
    LEFT JOIN opt_category oc ON oc.category_no = opt.category_no
    JOIN stock s ON oc.st_no = s.st_no
    WHERE o.orders_no = 2
    GROUP BY s.st_no, s.st_name
) AS combined
GROUP BY st_no, st_name
ORDER BY st_no;






