package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.purchases.PurchaseFiche;
import kg.bakirov.alpha.model.sales.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_FIRM_NO;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_PERIOD;

@Repository
public class SaleRepository {

    private final Utility utility;
    private final DataSource dataSource;

    @Autowired
    public SaleRepository(Utility utility, DataSource dataSource) {
        this.utility = utility;
        this.dataSource = dataSource;
    }


    /* ------------------------------------------ Список документов ---------------------------------------------------- */
    public List<SaleFiches> getSales(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<SaleFiches> saleFiches = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY SELECT STFIC.TRCODE  trcode, " +
                    "STFIC.FICHENO AS ficheno, CONVERT(varchar, STFIC.DATE_, 23) AS date, " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS managercode, " +
                    "(SELECT DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS managername, " +
                    "ISNULL(CLNTC.CODE, 0) as clientcode, ISNULL(CLNTC.DEFINITION_, 0) as clientname, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 2 THEN -STFIC.GROSSTOTAL WHEN 3 THEN -STFIC.GROSSTOTAL WHEN 4 THEN -STFIC.GROSSTOTAL ELSE STFIC.GROSSTOTAL END, 2) AS gross, " +
                    "ROUND(STFIC.TOTALDISCOUNTS, 2) AS discounts, " +
                    "ROUND(STFIC.TOTALEXPENSES, 2) AS expenses, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 2 THEN -STFIC.NETTOTAL WHEN 3 THEN -STFIC.NETTOTAL WHEN 4 THEN -STFIC.NETTOTAL ELSE STFIC.NETTOTAL END, 2) AS net, " +
                    "ROUND(CASE STFIC.TRCODE WHEN 2 THEN -STFIC.REPORTNET WHEN 3 THEN -STFIC.REPORTNET WHEN 4 THEN -STFIC.REPORTNET ELSE STFIC.REPORTNET END, 2) AS net_usd  " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF = INVFC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF = CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) ON (STFIC.SALESMANREF = SLSMC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) ON (STFIC.PAYDEFREF = PAYPL.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT sWSp WITH(NOLOCK) ON (STFIC.SOURCEWSREF = sWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT dWSp WITH(NOLOCK) ON (STFIC.DESTWSREF = dWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_DISTORD DISTORD WITH(NOLOCK) ON (STFIC.DISTORDERREF = DISTORD.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STFIC.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STFIC.CANCELLED = 0) AND (STFIC.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39)) AND (STFIC.SOURCEINDEX = ?) " +
                    "AND ((STFIC.DATE_>=?) AND (STFIC.DATE_<=?)) " +
                    "ORDER BY STFIC.DATE_, STFIC.FTIME, STFIC.TRCODE, STFIC.FICHENO ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sourceindex);
            statement.setString(2, begdate);
            statement.setString(3, enddate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                saleFiches.add(
                        new SaleFiches(
                                resultSet.getInt("trcode"),
                                resultSet.getString("ficheno"),
                                resultSet.getString("date"),
                                resultSet.getString("managercode"),
                                resultSet.getString("managername"),
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return saleFiches;
    }


    /* ------------------------------------------ Контента документа ---------------------------------------------------- */
    public List<SaleFiche> getFiche(int firmno, int periodno, int fiche) {

        utility.CheckCompany(firmno, periodno);
        List<SaleFiche> ficheList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS code, " +
                    "(SELECT NAME FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS name, " +
                    "STRNS.DATE_ AS date, STRNS.AMOUNT AS count, " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_UNITSETL WHERE LOGICALREF = STRNS.UOMREF) AS unit, " +
                    "STRNS.PRICE AS price, STRNS.TOTAL AS total, (STRNS.PRICE / STRNS.REPORTRATE) AS priceusd, " +
                    "(STRNS.AMOUNT * (STRNS.PRICE / STRNS.REPORTRATE)) AS totalusd " +
                    " FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS " +
                    " WHERE (STRNS.TRCODE in (6,7,8,11,12,25,51)) AND STRNS.LINETYPE = 0 AND " +
                    "(STRNS.INVOICEREF = (SELECT LOGICALREF FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE WHERE FICHENO = ?)) " +
                    "ORDER BY STRNS.INVOICEREF, STRNS.INVOICELNNO ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, fiche);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                ficheList.add(
                        new SaleFiche(
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
        return ficheList;
    }


    /* ------------------------------------------ Итоговые цифры продаж ---------------------------------------------------- */
    public List<SaleTotal> getSalesTotal(int firmno, int periodno, String begdate, String enddate) {

        utility.CheckCompany(firmno, periodno);
        List<SaleTotal> saleTotals = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY SELECT ITEMS.CODE AS code, ITEMS.NAME AS name, ITEMS.STGRPCODE AS groupCode, " +
                    "SUM(STITOTS.PURAMNT) AS purchase_count, ROUND(SUM(STITOTS.PURCASH), 2) AS purchase_total, " +
                    "ROUND(SUM(STITOTS.PURCURR), 2) AS purchase_total_usd, SUM(STITOTS.SALAMNT) AS sale_count, " +
                    "ROUND(SUM(STITOTS.SALCASH), 2) AS sale_total, ROUND(SUM(STITOTS.SALCURR), 2) AS sale_total_usd " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVTOT STITOTS " +
                    "WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVTOT_I2) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS ON STITOTS.STOCKREF = ITEMS.LOGICALREF " +
                    "WHERE (STITOTS.INVENNO = -1) AND (STITOTS.SALAMNT <> 0) " +
                    "AND (STITOTS.DATE_ >= ? AND STITOTS.DATE_ <=  ?) " +
                    "GROUP BY ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE ORDER BY ITEMS.CODE ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                saleTotals.add(
                        new SaleTotal(
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return saleTotals;
    }


    /* ------------------------------------------ Распределение продаж по месяцам ---------------------------------------------------- */
    public List<SaleMonth> getSalesMonth(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<SaleMonth> saleMonths = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY SELECT ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 1) )), 0) AS jan, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 2) )), 0) AS feb, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 3) )), 0) AS mar, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 4) )), 0) AS apr, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 5) )), 0) AS may, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 6) )), 0) AS jun, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 7) )), 0) AS jul, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 8) )), 0) AS aug, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 9) )), 0) AS sep, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 10))), 0) AS oct, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 11))), 0) AS nov, " +
                    "ISNULL((SELECT ITMSM.SALES_AMOUNT FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ = 12))), 0) AS dec, " +
                    "ISNULL((SELECT SUM(ITMSM.SALES_AMOUNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12))), 0) AS TOTAL_COUNT, " +
                    "ROUND(ISNULL((SELECT SUM(ITMSM.SALES_CASHAMNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12))), 0), 2) AS TOTAL_SUM, " +
                    "ROUND(ISNULL((SELECT SUM(ITMSM.SALES_CURRAMNT) FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVENS ITMSM  " +
                    "WHERE (ITMSM.STOCKREF = ITEMS.LOGICALREF) AND (ITMSM.INVENNO = 0) AND ((ITMSM.MONTH_ BETWEEN 1 AND 12))), 0), 2) AS TOTAL_USD " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS " +
                    "WHERE (ITEMS.CARDTYPE) <> 22 AND (ITEMS.ACTIVE = 0) " +
                    "AND (ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (1,2,3,13,14,25,50))) " +
                    "AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>=?) AND (DATE_<=?)) and  ((SOURCEINDEX = ?) AND (IOCODE IN (1,2)))),0) - " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE " +
                    "WHERE ((TRCODE in (6,7,8,11,12,25,51))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>=?) AND (DATE_<=?)) and  ((SOURCEINDEX = ?) AND (IOCODE IN (3,4)))),0)<>0) " +
                    "Order BY code";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setInt(3, sourceindex);
            statement.setString(4, begdate);
            statement.setString(5, enddate);
            statement.setInt(6, sourceindex);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                saleMonths.add(
                        new SaleMonth(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("STGRPCODE"),
                                resultSet.getDouble("jan"),
                                resultSet.getDouble("feb"),
                                resultSet.getDouble("mar"),
                                resultSet.getDouble("apr"),
                                resultSet.getDouble("may"),
                                resultSet.getDouble("jun"),
                                resultSet.getDouble("jul"),
                                resultSet.getDouble("aug"),
                                resultSet.getDouble("sep"),
                                resultSet.getDouble("oct"),
                                resultSet.getDouble("nov"),
                                resultSet.getDouble("dec"),
                                resultSet.getDouble("TOTAL_COUNT"),
                                resultSet.getDouble("TOTAL_SUM"),
                                resultSet.getDouble("TOTAL_USD")
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return saleMonths;
    }


    /* ------------------------------------------ Распределение продаж по менеджерам ---------------------------------------------------- */
    public List<SaleClientManager> getSalesManager(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<SaleClientManager> saleClientManagers = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY SELECT (SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS client_code, " +
                    "(SELECT DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS client_name, " +
                    "SUM(STRNS.AMOUNT) AS amount, ROUND(SUM(STRNS.LINENET), 2) AS total, " +
                    "ROUND(ISNULL(SUM(STRNS.LINENET / NULLIF(STRNS.REPORTRATE, 0)), 0), 2) AS total_usd, STRNS.TRCODE AS trcode " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) ON (STRNS.STFICHEREF  =  STFIC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC WITH(NOLOCK) ON (STRNS.STOCKREF  =  ITMSC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_SHIPINFO SHPINF WITH(NOLOCK) ON (STFIC.SHIPINFOREF  =  SHPINF.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STRNS.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STRNS.SOURCEINDEX IN (?)) AND ((STRNS.DATE_>=?) AND (STRNS.DATE_<=?)) " +
                    "AND (STFIC.DEPARTMENT IN (0)) AND (STFIC.BRANCH IN (0)) " +
                    "AND (STFIC.FACTORYNR IN (0)) AND (STFIC.STATUS IN (0,1)) " +
                    "AND (STRNS.CPSTFLAG <> 1) AND (STRNS.DETLINE <> 1) AND (STRNS.LINETYPE NOT IN (2,3)) " +
                    "AND (STRNS.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39) ) AND (STFIC.CANCELLED = 0) " +
                    "GROUP BY CLNTC.PARENTCLREF, STRNS.TRCODE " +
                    "ORDER BY client_code ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sourceindex);
            statement.setString(2, begdate);
            statement.setString(3, enddate);
            ResultSet resultSet = statement.executeQuery();

            String currentCode = null;
            double itemAmount = 0, itemTotal = 0, itemTotalUsd = 0, itemAmountRet = 0, itemTotalRet = 0, itemTotalUsdRet = 0;
            HashMap<String, SaleClientManager> map = new HashMap<>();

            while (resultSet.next()) {
                if (resultSet.getString("client_code") == null) {
                    if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                        itemAmount += resultSet.getDouble("amount");
                        itemTotal += resultSet.getDouble("total");
                        itemTotalUsd += resultSet.getDouble("total_usd");
                        itemAmountRet = 0.0;
                        itemTotalRet = 0.0;
                        itemTotalUsdRet = 0.0;
                    } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                        itemAmount = 0.0;
                        itemTotal = 0.0;
                        itemTotalUsd = 0.0;
                        itemAmountRet += -resultSet.getDouble("amount");
                        itemTotalRet += -resultSet.getDouble("total");
                        itemTotalUsdRet += -resultSet.getDouble("total_usd");
                    }
                } else {
                    if (resultSet.getString("client_code").equals(currentCode)) {
                        if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                            itemAmount += resultSet.getDouble("amount");
                            itemTotal += resultSet.getDouble("total");
                            itemTotalUsd += resultSet.getDouble("total_usd");
                        } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                            itemAmountRet += -resultSet.getDouble("amount");
                            itemTotalRet += -resultSet.getDouble("total");
                            itemTotalUsdRet += -resultSet.getDouble("total_usd");
                        }
                    } else {
                        if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                            itemAmount = resultSet.getDouble("amount");
                            itemTotal = resultSet.getDouble("total");
                            itemTotalUsd = resultSet.getDouble("total_usd");
                            itemAmountRet = 0.0;
                            itemTotalRet = 0.0;
                            itemTotalUsdRet = 0.0;
                        } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                            itemAmount = 0.0;
                            itemTotal = 0.0;
                            itemTotalUsd = 0.0;
                            itemAmountRet = -resultSet.getDouble("amount");
                            itemTotalRet = -resultSet.getDouble("total");
                            itemTotalUsdRet = -resultSet.getDouble("total_usd");
                        }
                    }
                }

                SaleClientManager client = new SaleClientManager();
                client.setClientCode(resultSet.getString("client_code") == null ? "others" : resultSet.getString("client_code"));
                client.setClientName(resultSet.getString("client_name") == null ? "others" : resultSet.getString("client_name"));
                client.setItemAmount(itemAmount);
                client.setItemTotal(itemTotal);
                client.setItemTotalUsd(itemTotalUsd);
                client.setItemAmountRet(itemAmountRet);
                client.setItemTotalRet(itemTotalRet);
                client.setItemTotalUsdRet(itemTotalUsdRet);
                currentCode = resultSet.getString("client_code");

                map.put(resultSet.getString("client_code"), client);
            }

            for (Map.Entry<String, SaleClientManager> entry : map.entrySet()) {
                saleClientManagers.add(entry.getValue());
            }

            Collections.sort(saleClientManagers);

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return saleClientManagers;
    }



    /* ------------------------------------------ Распределение продаж по контрагентам ---------------------------------------------------- */
    public List<SaleClient> getSalesClient(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<SaleClient> saleClients = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY SELECT CLNTC.CODE AS client_code, CLNTC.DEFINITION_ AS client_name, " +
                    "ITMSC.CODE AS item_code, ITMSC.NAME AS item_name, ITMSC.STGRPCODE AS item_group, " +
                    "SUM(STRNS.AMOUNT) AS amount, ROUND(SUM(STRNS.LINENET), 2) AS total, " +
                    "ROUND(ISNULL(SUM(STRNS.LINENET / NULLIF(STRNS.REPORTRATE, 0)), 0), 2) AS total_usd, STRNS.TRCODE AS trcode " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) ON (STRNS.STFICHEREF  =  STFIC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC WITH(NOLOCK) ON (STRNS.STOCKREF  =  ITMSC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_SHIPINFO SHPINF WITH(NOLOCK) ON (STFIC.SHIPINFOREF  =  SHPINF.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STRNS.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STRNS.SOURCEINDEX IN (0)) AND (STFIC.DEPARTMENT IN (0)) AND (STFIC.BRANCH IN (0)) " +
                    "AND (STFIC.FACTORYNR IN (0)) AND (STFIC.STATUS IN (0,1)) " +
                    "AND (STRNS.CPSTFLAG <> 1) AND (STRNS.DETLINE <> 1) AND (STRNS.LINETYPE NOT IN (2,3)) " +
                    "AND (STRNS.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39) ) AND (STFIC.CANCELLED = 0) " +
                    "AND ((STRNS.DATE_>=?) AND (STRNS.DATE_<=?)) AND  (STRNS.SOURCEINDEX = ?) " +
                    "GROUP BY CLNTC.CODE, CLNTC.DEFINITION_, ITMSC.CODE, ITMSC.NAME, ITMSC.STGRPCODE, STRNS.TRCODE " +
                    "ORDER BY CLNTC.CODE, ITMSC.CODE ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setInt(3, sourceindex);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                SaleClient client = new SaleClient();

                client.setClientCode(resultSet.getString("client_code"));
                client.setClientName(resultSet.getString("client_name"));
                client.setItemCode(resultSet.getString("item_code"));
                client.setItemName(resultSet.getString("item_name"));
                client.setItemGroup(resultSet.getString("item_group"));

                if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                    client.setItemAmount(resultSet.getDouble("amount"));
                    client.setItemTotal(resultSet.getDouble("total"));
                    client.setItemTotalUsd(resultSet.getDouble("total_usd"));
                    client.setItemAmountRet(0.0);
                    client.setItemTotalRet(0.0);
                    client.setItemTotalUsdRet(0.0);
                } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                    client.setItemAmount(0.0);
                    client.setItemTotal(0.0);
                    client.setItemTotalUsd(0.0);
                    client.setItemAmountRet(-resultSet.getDouble("amount"));
                    client.setItemTotalRet(-resultSet.getDouble("total"));
                    client.setItemTotalUsdRet(-resultSet.getDouble("total_usd"));
                }

                saleClients.add(client);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return saleClients;
    }




    /* ------------------------------------------ Сводная таблица продаж ---------------------------------------------------- */
    public List<SaleTable> getSalesTable(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<SaleTable> saleTables = new ArrayList<>();
        Map<String, SaleTable> map_month = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = " Set DateFormat DMY SELECT INVFC.DATE_ AS date, INVFC.TRCODE, STRNS.LINETYPE, " +
                    "SUM(ROUND(ISNULL(STRNS.TOTAL, 0) , 2)) AS total, " +
                    "SUM(ROUND(ISNULL(STRNS.LINENET, 0) ,2)) AS net, " +
                    "SUM(ROUND(ISNULL(STRNS.TOTAL / NULLIF(STRNS.REPORTRATE, 0), 0), 2)) AS total_usd, " +
                    "SUM(ROUND(ISNULL(STRNS.LINENET / NULLIF(STRNS.REPORTRATE, 0), 0), 2)) AS net_usd " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WITH(NOLOCK) ON (INVFC.LOGICALREF = STRNS.INVOICEREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (INVFC.CLIENTREF = CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN  LG_SLSMAN WITH(NOLOCK) ON (STRNS.SALESMANREF = LG_SLSMAN.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC WITH(NOLOCK) ON (STRNS.STOCKREF = ITMSC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STRNS.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    " WHERE ((STRNS.DATE_>=?) AND (STRNS.DATE_<=?)) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (INVFC.DEPARTMENT IN (0)) AND (INVFC.BRANCH IN (0)) AND (INVFC.FACTORYNR IN (0)) " +
                    "AND (INVFC.STATUS IN (0,1)) AND (NOT(INVFC.TRCODE IN(5, 10)) ) " +
                    "AND (INVFC.CANCELLED =  0 ) AND (INVFC.GRPCODE = 2) " +
                    "GROUP BY INVFC.DATE_, INVFC.TRCODE, STRNS.LINETYPE " +
                    "ORDER BY INVFC.DATE_, INVFC.TRCODE, STRNS.LINETYPE ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setInt(3, sourceindex);
            ResultSet resultSet = statement.executeQuery();

            saleTables = new ArrayList<>();

            double total = 0;
            double net = 0;
            double expenses = 0;
            double discounts = 0;
            double ret = 0;
            double ret_usd = 0;
            double net_usd = 0;
            int currentMonth = 0;

            while (resultSet.next()) {

                String date = resultSet.getString("date");
                Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);

                int month = calendar.get(Calendar.MONTH) + 1;
                SaleTable saleTable = new SaleTable();

                if (month != currentMonth) {
                    total = 0;
                    net = 0;
                    expenses = 0;
                    discounts = 0;
                    ret = 0;
                    ret_usd = 0;
                    net_usd = 0;
                }

                if (resultSet.getInt("LINETYPE") == 0) {
                    if (resultSet.getInt("TRCODE") == 7 || resultSet.getInt("TRCODE") == 8) {
                        total = total + resultSet.getDouble("total");
                        net = net + resultSet.getDouble("net");
                        net_usd = net_usd + resultSet.getDouble("net_usd");
                    } else if (resultSet.getInt("TRCODE") == 2 || resultSet.getInt("TRCODE") == 3) {
                        ret = ret + resultSet.getDouble("total");
                        ret_usd = ret_usd + resultSet.getDouble("total_usd");
                    }
                } else if (resultSet.getInt("LINETYPE") == 3) {
                    expenses = expenses + resultSet.getDouble("total");
                } else if (resultSet.getInt("LINETYPE") == 2) {
                    discounts = discounts + resultSet.getDouble("total");
                }

                saleTable.setDate(String.valueOf(month));
                saleTable.setTotal(total - ret);
                saleTable.setDiscounts(discounts);
                saleTable.setExpenses(expenses);
                saleTable.setNet(net - ret);
                saleTable.setNet_usd(net_usd - ret_usd);
                saleTable.setRet_total(ret);
                saleTable.setRet_total_usd(ret_usd);
                currentMonth = month;
                map_month.put(String.valueOf(saleTable.getDate()), saleTable);
            }

            Map<String, SaleTable> sortTable = new TreeMap<>(map_month);

            for (Map.Entry<String, SaleTable> entry : sortTable.entrySet()) {
                saleTables.add(entry.getValue());
            }

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return saleTables;
    }



    /* ------------------------------------------ Подробный отчет продаж ---------------------------------------------------- */
    public List<SaleDetail> getSalesDetail(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<SaleDetail> saleDetails = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY " +
                    "Select ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE, " +
                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/STRNS.UINFO1)) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) " +
                    "AND ((STRNS.DATE_>=?) AND (STRNS.DATE_<=?)) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (STRNS.TRCODE IN (2,3)) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)), 0),2) as iade, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) " +
                    "* (STRNS.TOTAL+STRNS.DISTEXP-STRNS.DISTDISC+STRNS.DIFFPRICE-STRNS.VATINC*STRNS.VATAMNT)/STRNS.REPORTRATE) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.REPORTRATE > 0.0" + GLOBAL_FIRM_NO + ") " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) " +
                    "AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) " +
                    "AND ((STRNS.TRCODE IN (2,3)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 2)) AND STRNS.TRCODE IN (2,3))) " +
                    "AND ((STRNS.DATE_>=?) AND (STRNS.DATE_<=?)) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)),0),2) as iade_tutari_usd, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND (STRNS.TRCODE IN (2,3)) " +
                    "AND ((STRNS.DATE_>=?) AND (STRNS.DATE_<=?)) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)  ), 0), 2) as iade_maliyeti_usd, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/STRNS.UINFO1)) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND ((STRNS.IOCODE IN (3,4))) " +
                    "AND ((STRNS.TRCODE IN (7,8)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) " +
                    "AND ((STRNS.DATE_>=?) AND (STRNS.DATE_<=?)) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND STRNS.TRCODE IN (7,8)  )) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)), 0), 2) as net_satis, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) \n" +
                    "* (STRNS.TOTAL+STRNS.DISTEXP-STRNS.DISTDISC+STRNS.DIFFPRICE-STRNS.VATINC*STRNS.VATAMNT)/STRNS.REPORTRATE) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) " +
                    "AND (STRNS.REPORTRATE > 0.0" + GLOBAL_FIRM_NO + ") AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) " +
                    "AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND ((STRNS.IOCODE IN (3,4))) " +
                    "AND ((STRNS.TRCODE IN (7,8)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) AND STRNS.TRCODE IN (7,8)  )) " +
                    "AND ((STRNS.DATE_>=?) AND (STRNS.DATE_<=?)) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)), 0), 2) as satis_tutari_usd, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) " +
                    "AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND (STRNS.TRCODE IN (7,8)) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF) " +
                    "AND ((STRNS.DATE_>=?) AND (STRNS.DATE_<=?)) AND (STRNS.SOURCEINDEX = ?)), 0), 2) as  satis_maliyeti_usd " +

                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS As ITEMS WHERE (CARDTYPE <> 22) ORDER BY CODE";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setInt(3, sourceindex);
            statement.setString(4, begdate);
            statement.setString(5, enddate);
            statement.setInt(6, sourceindex);
            statement.setString(7, begdate);
            statement.setString(8, enddate);
            statement.setInt(9, sourceindex);
            statement.setString(10, begdate);
            statement.setString(11, enddate);
            statement.setInt(12, sourceindex);
            statement.setString(13, begdate);
            statement.setString(14, enddate);
            statement.setInt(15, sourceindex);
            statement.setString(16, begdate);
            statement.setString(17, enddate);
            statement.setInt(18, sourceindex);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                SaleDetail detail = new SaleDetail();

                double saleCount = resultSet.getDouble("net_satis") + resultSet.getDouble("iade");
                double saleTotal = resultSet.getDouble("satis_tutari_usd") + resultSet.getDouble("iade_tutari_usd");
                double saleCost = resultSet.getDouble("satis_maliyeti_usd") + resultSet.getDouble("iade_maliyeti_usd");
                double profitPercent = 0;
                if (saleCost != 0) {
                    profitPercent = ((saleTotal * 100) / saleCost) - 100;
                }
                detail.setCode(resultSet.getString("code"));
                detail.setName(resultSet.getString("name"));
                detail.setItemGroup(resultSet.getString("STGRPCODE"));
                detail.setRetCount(resultSet.getDouble("iade"));
                detail.setRetTotal(resultSet.getDouble("iade_tutari_usd"));
                detail.setRetCost(resultSet.getDouble("iade_maliyeti_usd"));
                detail.setSaleCount(saleCount == 0 ? 0 : -saleCount);
                detail.setSaleTotal(-Math.round(saleTotal * 100d) / 100d);
                detail.setSaleCost(-Math.round(saleCost * 100d) / 100d);
                detail.setProfitTotal(-Math.round((saleTotal - saleCost) * 100d) / 100d);
                detail.setProfitPercent(Math.round(profitPercent * 100d) / 100d);
                saleDetails.add(detail);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return saleDetails;
    }


    /* ------------------------------------------ Ежедневные продажи ---------------------------------------------------- */
    public List<SaleDaily> getSalesDaily(int firmno, int periodno, String begDate, String endDate, int sourceIndex) {

        utility.CheckCompany(firmno, periodno);
        List<SaleDaily> saleClients = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY " +
                    "SELECT  CONVERT(varchar, LGMAIN.DATE_, 23) AS date, LGMAIN.TRCODE AS trcode, " +
                    "SUM(LGMAIN.NETTOTAL) AS net_total, " +
                    "SUM(LGMAIN.REPORTNET) AS report_net " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE LGMAIN  " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (LGMAIN.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "WHERE (LGMAIN.GRPCODE = 2) " +
                    "AND ((LGMAIN.DATE_>=?) AND (LGMAIN.DATE_<=?)) AND (LGMAIN.SOURCEINDEX=?) " +
                    "GROUP BY LGMAIN.DATE_, LGMAIN.TRCODE " +
                    "ORDER BY LGMAIN.DATE_ DESC";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setInt(3, sourceIndex);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SaleDaily client = new SaleDaily();
                client.setDate(resultSet.getString("date"));
                client.setTrCode(resultSet.getInt("trcode"));
                if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                    client.setNet(resultSet.getDouble("net_total"));
                    client.setNet_usd(resultSet.getDouble("report_net"));
                } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                    client.setRet_total(resultSet.getDouble("net_total"));
                    client.setRet_total_usd(resultSet.getDouble("report_net"));
                }
                saleClients.add(client);
            }

            Map<String, SaleDaily> map = new TreeMap<>(Collections.reverseOrder());

            for (SaleDaily saleDaily : saleClients) {
                if (map.containsKey(saleDaily.getDate())) {
                    SaleDaily daily = map.get(saleDaily.getDate());
                    double netTotal = (daily.getNet() == null ? 0.0 : daily.getNet()) + (saleDaily.getNet() == null ? 0.0 : saleDaily.getNet());
                    double reportNet = (daily.getNet_usd() == null ? 0.0 : daily.getNet_usd()) + (saleDaily.getNet_usd() == null ? 0.0 : saleDaily.getNet_usd());
                    double netReturn = (daily.getRet_total() == null ? 0.0 : daily.getRet_total()) + (saleDaily.getRet_total() == null ? 0.0 : saleDaily.getRet_total());
                    double reportReturn = (daily.getRet_total_usd() == null ? 0.0 : daily.getRet_total_usd()) + (saleDaily.getRet_total_usd() == null ? 0.0 : saleDaily.getRet_total_usd());
                    daily.setNet(netTotal);
                    daily.setNet_usd(reportNet);
                    daily.setRet_total(netReturn);
                    daily.setRet_total_usd(reportReturn);
                    map.put(saleDaily.getDate(), daily);
                } else {
                    map.put(saleDaily.getDate(), saleDaily);
                }
            }

            saleClients.clear();
            saleClients.addAll(map.values());

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return saleClients;
    }
}
