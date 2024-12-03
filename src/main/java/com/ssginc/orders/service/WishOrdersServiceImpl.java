package com.ssginc.orders.service;

import com.ssginc.orders.model.dao.TimOrdersDAO;
import com.ssginc.orders.model.dao.WishOrdersDAO;
import com.ssginc.orders.model.dto.ConsumptionDTO;
import com.ssginc.orders.model.dto.OptionsDTO;
import com.ssginc.orders.model.dto.WishProductsDTO;
import com.ssginc.util.HikariCPDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class WishOrdersServiceImpl implements WishOrdersService {

    private DataSource dataSource;
    public WishOrdersDAO wishOrdersDAO;
    public TimOrdersDAO timOrdersDAO;

    public WishOrdersServiceImpl() {
        this.dataSource = HikariCPDataSource.getInstance().getDataSource();
        this.wishOrdersDAO = new WishOrdersDAO();
        this.timOrdersDAO = new TimOrdersDAO();
    }

    @Override
    public int insertOrders(List<WishProductsDTO> products, int usersNo) {

        int res = 1;

        int totalQuantity = products.size();

        int totalPrice = 0;

        int prdPrice = 0;

        for (WishProductsDTO product : products) {
            log.info("WishProductsDTO = {}", product);
            prdPrice = 0;
            for (OptionsDTO opt : product.getOptions()) {
                int optQuantity = opt.getQuantity() == 0 ? 1 : opt.getQuantity();
                prdPrice += opt.getPrice() * optQuantity;
            }
            prdPrice += product.getPrice() * product.getQuantity();
            totalPrice += prdPrice;
        }

        try(Connection conn = dataSource.getConnection()){

            conn.setAutoCommit(false);

            // 소모량 구하기
            Map<Integer, Integer> consumptionMap = new HashMap<>(); // key : st_no / value : 소모량

            List<ConsumptionDTO> consumptions = null;

            // insert 한 orders 테이블의 기본키 반환
            int ordersKey = wishOrdersDAO.insertOrders(conn, totalQuantity, totalPrice, usersNo);

            for (WishProductsDTO product : products) {

                if (product.getIsBeverage() == 1){
                    int ordPrdKey = wishOrdersDAO.insertOrdersPrd(conn,ordersKey, product.getQuantity(), product.getPno());

                    for (OptionsDTO opt : product.getOptions()) {
                        res *= wishOrdersDAO.insertOrdersOpt(conn, ordPrdKey, opt.getOptNo(), opt.getPrice(), opt.getQuantity(), opt.getOptName());
                    }

                    consumptions = timOrdersDAO.selectProductConsumptionList(conn, product.getPno()); // 음료 제조  소모량
                    for (ConsumptionDTO c : consumptions) {
                        consumptionMap.put(c.getStockNo(), consumptionMap.getOrDefault(consumptionMap.get(c.getStockNo()), 0) + c.getConsumption() * product.getQuantity());
                    }

                    for (OptionsDTO opt : product.getOptions()) {
                        consumptions = timOrdersDAO.selectOptConsumptionList(conn,opt.getOptNo()); // 옵션 제조  소모량
                        for (ConsumptionDTO c : consumptions) {
                            consumptionMap.put(c.getStockNo(), consumptionMap.getOrDefault(consumptionMap.get(c.getStockNo()), 0) + c.getConsumption() * opt.getQuantity());
                        }
                    }
                } else if (product.getIsBeverage() == 2){
                    consumptionMap.put(product.getPno(), consumptionMap.getOrDefault(consumptionMap.get(product.getPno()), 0) + product.getQuantity());
                    res *= wishOrdersDAO.insertOrdersStock(conn,ordersKey,product.getPno(), product.getQuantity());
                }

            }

            int updateRes = 1;

            for (int key : consumptionMap.keySet()) {
                int stockConsumption = consumptionMap.get(key);

                updateRes *= wishOrdersDAO.updateStockForOrders(conn, key, stockConsumption);
            }

            if (res * updateRes > 0) {
                conn.commit();
                log.info("Orders inserted successfully");
            } else {
                conn.rollback();
                log.error("Insert orders failed");
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }
}
