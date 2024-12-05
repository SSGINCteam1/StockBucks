package com.ssginc.placeonorders.model.dao;

import com.ssginc.placeonorders.model.dto.*;
import com.ssginc.placeonorders.model.vo.PlaceOrdersStockVO;
import com.ssginc.placeonorders.model.vo.PlaceOrdersVO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public interface PlaceOnOrdersDAOInterface {
    // ========================== 1. 재고 조회 ==========================
    // -------------------------- 1.1 지점 재고 전체 조회 --------------------------
    // 지점 재고 전체 조회
    List<SelectStockListDTO> selectAllStockList();

    // -------------------------- 1.2 지점 재고 카테고리별 조회 --------------------------
    // 지점 재고 카테고리별 조회
    List<SelectStockListDTO> selectStockListByCategory(int category);

    // -------------------------- 1.3 지점 재고 키워드 검색 --------------------------
    // 지점 재고 키워드 검색
    List<SelectStockListDTO> selectStockListByKeyword(String searchKeyword);

    // ========================== 2. 발주 신청 ==========================
    // -------------------------- 2.1 장바구니 품목 목록 조회 --------------------------
    // 장바구니에 담은 품목 조회
    List<SelectBasketListDTO> selectBasketListByUsersNo(int usersNo);

    // 유저 번호와 품목 번호로 장바구니에 담은 품목 조회
    SelectBasketListDTO selectBasketStockByUsersNoAndStockNo(int usersNo, int stockNo);

    // -------------------------- 2.1.1 장바구니 품목 수정 --------------------------
    // 장바구니 품목 수정
    int updateBasketStock(Connection con, int usersNo, int selectedBasketStockNo, int inputQuantity);

    // -------------------------- 2.1.2 장바구니 품목 삭제 --------------------------
    // 장바구니 품목 삭제
    int deleteBasketStockByStockNo(int usersNo, int selectedBaksetStockNo);

    // -------------------------- 2.2 장바구니 품목 발주 신청 --------------------------
    // 발주 테이블에 추가
    InsertPlaceOrdersDTO insertPlaceOrders(Connection con, int totalPrice, int usersNo);

    // 발주_재고물품 테이블에 추가
    int insertPlaceOrdersStock(Connection con, int poNo, int stNo, int placeOrdersQuantity);

    // 발주 장바구니 테이블에서 삭제
    int deletePlaceOrdersBasketByUsersNo(Connection con, int usersNo);

    // ========================== 3. 발주 내역 조회 ==========================
    // -------------------------- 3.1 발주 내역 전체 조회 --------------------------
    // 발주 내역 전체 조회
    ArrayList<PlaceonOrdersCheckDTO> selectAllOrderableStockChecks(Connection con);

    // -------------------------- 3.2 발주 내역 카테고리별 조회 --------------------------
    // 발주 내역 카테고리별 조회
    ArrayList<PlaceOnOrdersHistoryDTO> HistoryplaceOrdersStockByCategory(Connection con, int category);

    // -------------------------- 3.3 발주 내역 기간별 조회 --------------------------
    // 년도별 주문 내역 목록 조회
    ArrayList<PlaceonOrdersCheckDTO> PlaceOnOrdersHistoryByYear(Connection conn, int year);

    // 월별 발주 내역 목록 조회
    ArrayList<PlaceonOrdersCheckDTO> PlaceOnOrdersHistoryByMonth(Connection conn, int month, int year);

    // 일자별 발주 내역 목록 조회
    ArrayList<PlaceonOrdersCheckDTO> PlaceOnOrdersHistoryByDay(Connection conn, int day, int month, int year);

    // -------------------------- 3.4 발주 수정 --------------------------
    // 발주 수정
    int UpdateOrderHistory(Connection con, PlaceOrdersStockVO vo);

    // -------------------------- 3.5 발주 취소 --------------------------
    // 발주 재고물품 테이블에서 삭제
    void DeleteOrderHistory2(Connection con, PlaceOrdersStockVO vo);

    // 발주 테이블에서 삭제
    void DeleteOrderHistory(Connection con, PlaceOrdersVO vo);

    // ========================== 4. 발주 가능 품목 조회 ==========================
    // -------------------------- 4.1 발주 가능 품목 전체 조회 --------------------------
    // 전체 데이터 조회
    ArrayList<PlaceonOrdersDTO> selectAllOrderableStocks(Connection con);

    // -------------------------- 4.2 발주 가능 품목 카테고리별 조회 --------------------------
    // 특정 카테고리 데이터 조회
    ArrayList<PlaceonOrdersDTO> selectAllOrderableStocksByCategory(Connection con, int st_category);

    // -------------------------- 4.3 발주 가능 품목 키워드 검색 --------------------------
    // 검색 데이터 조회
    ArrayList<PlaceonOrdersDTO> selectAllOrderableStocksByKeyword(Connection con, String keyword);

    // -------------------------- 4. 장바구니에 품목 담기 --------------------------
    // 장바구니에 품목 담기
    void placeOnOrdersInsertBag(Connection con, PlaceOnOrdersInsertBagDTO dto);
}
