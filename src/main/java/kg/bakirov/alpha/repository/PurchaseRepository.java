package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.purchases.*;
import kg.bakirov.alpha.model.sales.SaleClientFiches;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_FIRM_NO;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_PERIOD;

@Repository
public class PurchaseRepository {

    private final Utility utility;
    private final DataSource dataSource;

    @Autowired
    public PurchaseRepository(Utility utility, DataSource dataSource) {
        this.utility = utility;
        this.dataSource = dataSource;
    }

    /* ------------------------------------------ Список документов ---------------------------------------------------- */
    public List<PurchaseFiches> getPurchases(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String operationType, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<PurchaseFiches> purchasesFicheList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT STFIC.LOGICALREF AS id, STFIC.TRCODE trcode, " +
                    "STFIC.FICHENO AS ficheno, CONVERT(varchar, STFIC.DATE_, 23) AS date, " +
                    "ISNULL(CLNTC.CODE, 0) as clientcode, ISNULL(CLNTC.DEFINITION_, 0) as clientname, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 6 THEN -STFIC.GROSSTOTAL ELSE STFIC.GROSSTOTAL END, 2) AS gross, " +
                    "ROUND(STFIC.TOTALDISCOUNTS, 2) AS discounts, " +
                    "ROUND(STFIC.TOTALEXPENSES, 2) AS expenses, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 6 THEN -STFIC.NETTOTAL ELSE STFIC.NETTOTAL END, 2) AS net, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 6 THEN -STFIC.REPORTNET ELSE STFIC.REPORTNET END, 2) AS net_usd  " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF = INVFC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF = CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) ON (STFIC.SALESMANREF = SLSMC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) ON (STFIC.PAYDEFREF = PAYPL.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT sWSp WITH(NOLOCK) ON (STFIC.SOURCEWSREF = sWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT dWSp WITH(NOLOCK) ON (STFIC.DESTWSREF = dWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_DISTORD DISTORD WITH(NOLOCK) ON (STFIC.DISTORDERREF = DISTORD.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STFIC.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STFIC.CANCELLED = 0) AND (STFIC.TRCODE IN(" + operationType + ")) AND (STFIC.SOURCEINDEX = ?) " +
                    "AND ((STFIC.DATE_>= CONVERT(dateTime, ?, 104)) AND (STFIC.DATE_<= CONVERT(dateTime, ?, 104))) ";
            if (!filterName.isEmpty()) {
                sqlQuery += "AND (CLNTC.CODE LIKE '%" + filterName + "%' OR CLNTC.DEFINITION_ LIKE '%" + filterName + "%') ";
            }
            sqlQuery += "ORDER BY STFIC.DATE_, STFIC.FTIME, STFIC.TRCODE, STFIC.FICHENO ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sourceIndex);
            statement.setString(2, begDate);
            statement.setString(3, endDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                purchasesFicheList.add(
                        new PurchaseFiches(
                                resultSet.getLong("id"),
                                resultSet.getInt("trcode"),
                                resultSet.getString("ficheno"),
                                resultSet.getString("date"),
                                resultSet.getString("clientcode"),
                                resultSet.getString("clientname"),
                                resultSet.getDouble("gross"),
                                resultSet.getDouble("discounts"),
                                resultSet.getDouble("expenses"),
                                resultSet.getDouble("net"),
                                resultSet.getDouble("net_usd")
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return purchasesFicheList;
    }

    /* ------------------------------------------ Контента документа ---------------------------------------------------- */
    public List<PurchaseFiche> getFiche(int firmNo, int periodNo, int fiche) {

        utility.CheckCompany(firmNo, periodNo);
        List<PurchaseFiche> purchasesFicheList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS code, " +
                    "(SELECT NAME FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS name, " +
                    "STRNS.DATE_ AS date, STRNS.AMOUNT AS count, " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_UNITSETL WHERE LOGICALREF = STRNS.UOMREF) AS unit, " +
                    "STRNS.PRICE AS price, STRNS.TOTAL AS total, (STRNS.PRICE / STRNS.REPORTRATE) AS priceusd, " +
                    "(STRNS.AMOUNT * (STRNS.PRICE / STRNS.REPORTRATE)) AS totalusd " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS " +
                    "WHERE (STRNS.TRCODE in (1,2,3,13,14,25,50)) AND STRNS.LINETYPE = 0 AND (STRNS.STFICHEREF = ?) " +
                    "ORDER BY STRNS.INVOICEREF, STRNS.INVOICELNNO ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, fiche);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                purchasesFicheList.add(
                        new PurchaseFiche(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("date"),
                                resultSet.getInt("count"),
                                resultSet.getString("unit"),
                                resultSet.getDouble("price"),
                                resultSet.getDouble("total"),
                                resultSet.getDouble("priceUsd"),
                                resultSet.getDouble("totalUsd")
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return purchasesFicheList;
    }


    /* ------------------------------------------ Итоговые цифры закупок ---------------------------------------------------- */
    public List<PurchaseTotal> getPurchasesTotal(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<PurchaseTotal> purchaseTotals = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT ITEMS.CODE AS code, ITEMS.NAME AS name, ITEMS.STGRPCODE AS groupCode, " +
                    "SUM(STITOTS.PURAMNT) AS purchase_count, ROUND(SUM(STITOTS.PURCASH), 2) AS purchase_total, " +
                    "ROUND(SUM(STITOTS.PURCURR), 2) AS purchase_total_usd, SUM(STITOTS.SALAMNT) AS sale_count, " +
                    "ROUND(SUM(STITOTS.SALCASH), 2) AS sale_total, ROUND(SUM(STITOTS.SALCURR), 2) AS sale_total_usd " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVTOT STITOTS " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS ON STITOTS.STOCKREF = ITEMS.LOGICALREF " +
                    "WHERE (STITOTS.INVENNO IN (?)) AND (STITOTS.PURAMNT <> 0) " +
                    "AND (STITOTS.DATE_ >= CONVERT(dateTime, ?, 104)) AND (STITOTS.DATE_  <= CONVERT(dateTime, ?, 104)) " +
                    "AND (ITEMS.CODE LIKE ? OR ITEMS.NAME LIKE ?) " +
                    "GROUP BY ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE ORDER BY ITEMS.CODE ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sourceIndex);
            statement.setString(2, begDate);
            statement.setString(3, endDate);
            statement.setString(4, "%" + filterName + "%");
            statement.setString(5, "%" + filterName + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                purchaseTotals.add(
                        new PurchaseTotal(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("groupCode"),
                                resultSet.getDouble("purchase_count"),
                                resultSet.getDouble("purchase_total"),
                                resultSet.getDouble("purchase_total_usd"),
                                resultSet.getDouble("sale_count"),
                                resultSet.getDouble("sale_total"),
                                resultSet.getDouble("sale_total_usd")
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return purchaseTotals;
    }


    /* ------------------------------------------ Распределение закупок по месяцам ---------------------------------------------------- */
    public List<PurchaseMonth> getPurchasesMonth(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) {

        utility.CheckCompany(firmNo, periodNo);
        List<PurchaseMonth> purchaseMonths = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 1))), 0) AS JAN_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 2) )), 0) AS FEB_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 3) )), 0) AS MAR_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 4) )), 0) AS APR_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 5) )), 0) AS MAY_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 6) )), 0) AS JUN_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 7) )), 0) AS JUL_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 8) )), 0) AS AUG_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 9) )), 0) AS SEP_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 10) )), 0) AS OCT_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 11) )), 0) AS NOV_COUNT, " +
                    "ISNULL((SELECT ITMSM.PURCHASES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 12) )), 0) AS DEC_COUNT, " +
                    "ISNULL((SELECT SUM(ITMSM.PURCHASES_AMOUNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12))), 0) AS TOTAL_COUNT, " +
                    "ROUND(ISNULL((SELECT SUM(ITMSM.PURCHASES_CASHAMNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12))), 0), 2) AS TOTAL_SUM, " +
                    "ROUND(ISNULL((SELECT SUM(ITMSM.PURCHASES_CURRAMNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12))), 0), 2) AS TOTAL_USD " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS " +
                    "WHERE (ITEMS.CARDTYPE) <> 22 AND (ITEMS.ACTIVE = 0) " +
                    "AND (ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (1,2,3,13,14,25,50))) " +
                    "AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>= CONVERT(dateTime, ?, 104)) AND (DATE_<= CONVERT(dateTime, ?, 104))) and  ((SOURCEINDEX = ?) AND (IOCODE IN (1,2)))),0) - " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (6,7,8,11,12,25,51))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>= CONVERT(dateTime, ?, 104)) AND (DATE_<= CONVERT(dateTime, ?, 104))) and  ((SOURCEINDEX = ?) AND (IOCODE IN (3,4)))),0)<>0) " +
                    "Order BY code";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setInt(3, sourceIndex);
            statement.setString(4, begDate);
            statement.setString(5, endDate);
            statement.setInt(6, sourceIndex);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                purchaseMonths.add(
                        new PurchaseMonth(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("STGRPCODE"),
                                resultSet.getDouble("JAN_COUNT"),
                                resultSet.getDouble("FEB_COUNT"),
                                resultSet.getDouble("MAR_COUNT"),
                                resultSet.getDouble("APR_COUNT"),
                                resultSet.getDouble("MAY_COUNT"),
                                resultSet.getDouble("JUN_COUNT"),
                                resultSet.getDouble("JUL_COUNT"),
                                resultSet.getDouble("AUG_COUNT"),
                                resultSet.getDouble("SEP_COUNT"),
                                resultSet.getDouble("OCT_COUNT"),
                                resultSet.getDouble("NOV_COUNT"),
                                resultSet.getDouble("DEC_COUNT"),
                                resultSet.getDouble("TOTAL_COUNT"),
                                resultSet.getDouble("TOTAL_SUM"),
                                resultSet.getDouble("TOTAL_USD")
                        )
                );
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return purchaseMonths;
    }


    /* ------------------------------------------ Распределение закупок по контрагентам ---------------------------------------------------- */
    public List<PurchaseClient> getPurchasesClient(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<PurchaseClient> purchasesClientList = new ArrayList<>();
        Map<String, PurchaseClient> map = new TreeMap<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT CLNTC.CODE AS client_code, CLNTC.DEFINITION_ AS client_name, " +
                    "ITMSC.CODE AS item_code, ITMSC.NAME AS item_name, ITMSC.STGRPCODE AS item_group, " +
                    "SUM(STRNS.AMOUNT) AS amount, ROUND(SUM(STRNS.LINENET), 2) AS total, " +
                    "ROUND(ISNULL(SUM(STRNS.LINENET / NULLIF(STRNS.REPORTRATE, 0)), 0), 2) AS total_usd, STRNS.TRCODE AS trcode " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) ON (STRNS.STFICHEREF  =  STFIC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC WITH(NOLOCK) ON (STRNS.STOCKREF  =  ITMSC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_SHIPINFO SHPINF WITH(NOLOCK) ON (STFIC.SHIPINFOREF  =  SHPINF.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STRNS.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STFIC.STATUS IN (0,1)) AND (STRNS.CPSTFLAG <> 1) AND (STRNS.DETLINE <> 1) AND (STRNS.LINETYPE NOT IN (2,3)) " +
                    "AND (STRNS.TRCODE IN (1,5,6,10,26,30,31,32,33,34) ) AND (STFIC.CANCELLED = 0) " +
                    "AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND  (STRNS.SOURCEINDEX = ?)" +
                    "AND (CLNTC.CODE LIKE ? OR CLNTC.DEFINITION_ LIKE ?) " +
                    "GROUP BY CLNTC.CODE, CLNTC.DEFINITION_, ITMSC.CODE, ITMSC.NAME, ITMSC.STGRPCODE, STRNS.TRCODE " +
                    "ORDER BY CLNTC.CODE, ITMSC.CODE ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setInt(3, sourceIndex);
            statement.setString(4, "%" + filterName + "%");
            statement.setString(5, "%" + filterName + "%");
            ResultSet resultSet = statement.executeQuery();

            double count, countRet, total, totalUsd, totalRet, totalRetUsd;

            while (resultSet.next()) {
                if (map.containsKey(resultSet.getString("client_code"))) {
                    PurchaseClient client = map.get(resultSet.getString("client_code"));
                    if (resultSet.getDouble("trcode") == 1) {
                        count = client.getItemAmount() + resultSet.getDouble("amount");
                        total = client.getItemTotal() + resultSet.getDouble("total");
                        totalUsd = client.getItemTotalUsd() + resultSet.getDouble("total_usd");
                        client.setItemAmount(count);
                        client.setItemTotal(total);
                        client.setItemTotalUsd(totalUsd);
                    } else if (resultSet.getDouble("trcode") == 6) {
                        countRet = client.getItemAmountRet() + resultSet.getDouble("amount");
                        totalRet = client.getItemTotalRet() + resultSet.getDouble("total");
                        totalRetUsd = client.getItemTotalUsdRet() + resultSet.getDouble("total_usd");
                        client.setItemAmountRet(countRet);
                        client.setItemTotalRet(totalRet);
                        client.setItemTotalUsdRet(totalRetUsd);
                    }
                    map.put(resultSet.getString("client_code"), client);
                } else {
                    PurchaseClient client = new PurchaseClient();
                    client.setClientCode(resultSet.getString("client_code"));
                    client.setClientName(resultSet.getString("client_name"));

                    if (resultSet.getDouble("trcode") == 1) {
                        client.setItemAmount(resultSet.getDouble("amount"));
                        client.setItemTotal(resultSet.getDouble("total"));
                        client.setItemTotalUsd(resultSet.getDouble("total_usd"));
                        client.setItemAmountRet(0.0);
                        client.setItemTotalRet(0.0);
                        client.setItemTotalUsdRet(0.0);
                    } else if (resultSet.getDouble("trcode") == 6) {
                        client.setItemAmount(0.0);
                        client.setItemTotal(0.0);
                        client.setItemTotalUsd(0.0);
                        client.setItemAmountRet(resultSet.getDouble("amount"));
                        client.setItemTotalRet(resultSet.getDouble("total"));
                        client.setItemTotalUsdRet(resultSet.getDouble("total_usd"));
                    }
                    map.put(resultSet.getString("client_code"), client);
                }
            }
            purchasesClientList.addAll(map.values());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return purchasesClientList;
    }


    /* ------------------------------------------ Перечень документов по контрагентам ---------------------------------------------------- */
    public List<PurchaseClientFiches> getPurchasesClientFiches(int firmno, int periodno, String begdate, String enddate, int sourceindex, String code) {

        utility.CheckCompany(firmno, periodno);
        List<PurchaseClientFiches> clients = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT STFIC.LOGICALREF AS id, STFIC.TRCODE  trcode, " +
                    "    STFIC.FICHENO AS ficheno, CONVERT(varchar, STFIC.DATE_, 23) AS date, " +
                    "    ISNULL(CLNTC.CODE, 0) as clientcode, ISNULL(CLNTC.DEFINITION_, 0) as clientname, " +
                    "    STFIC.GROSSTOTAL AS gross, STFIC.TOTALDISCOUNTS AS discounts, STFIC.TOTALEXPENSES AS expenses, " +
                    "    STFIC.NETTOTAL AS net, STFIC.REPORTNET AS net_usd " +
                    "    FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) " +
                    "    LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF = INVFC.LOGICALREF) " +
                    "    LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF = CLNTC.LOGICALREF) " +
                    "    LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) ON (STFIC.SALESMANREF = SLSMC.LOGICALREF) " +
                    "    LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) ON (STFIC.PAYDEFREF = PAYPL.LOGICALREF) " +
                    "    LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT sWSp WITH(NOLOCK) ON (STFIC.SOURCEWSREF = sWSp.LOGICALREF) " +
                    "    LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT dWSp WITH(NOLOCK) ON (STFIC.DESTWSREF = dWSp.LOGICALREF) " +
                    "    LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_DISTORD DISTORD WITH(NOLOCK) ON (STFIC.DISTORDERREF = DISTORD.LOGICALREF) " +
                    "    LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STFIC.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "    WHERE (STFIC.CANCELLED = 0) AND (STFIC.TRCODE IN (1,5,6,10,26,30,31,32,33,34)) " +
                    "    AND ((STFIC.DATE_>= CONVERT(dateTime, ?, 104)) AND (STFIC.DATE_<= CONVERT(dateTime, ?, 104))) AND (STFIC.SOURCEINDEX = ?) " +
                    "    AND (CLNTC.CODE = ?) " +
                    "    ORDER BY STFIC.DATE_, STFIC.FTIME, STFIC.TRCODE, STFIC.FICHENO ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setInt(3, sourceindex);
            statement.setString(4, code);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                clients.add(
                        new PurchaseClientFiches(
                                resultSet.getLong("id"),
                                resultSet.getInt("trcode"),
                                resultSet.getString("ficheno"),
                                resultSet.getString("date"),
                                resultSet.getString("clientcode"),
                                resultSet.getString("clientname"),
                                resultSet.getDouble("gross"),
                                resultSet.getDouble("discounts"),
                                resultSet.getDouble("expenses"),
                                resultSet.getDouble("net"),
                                resultSet.getDouble("net_usd")
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return clients;
    }

}
