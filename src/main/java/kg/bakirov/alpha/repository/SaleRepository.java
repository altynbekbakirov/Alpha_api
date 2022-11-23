package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.sales.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

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
    public List<SaleFiches> getSales(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String operationType, String filterCode) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleFiches> saleFiches = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT STFIC.LOGICALREF AS id, STFIC.TRCODE AS trcode, " +
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
                    "WHERE (STFIC.CANCELLED = 0) AND (STFIC.TRCODE IN (" + operationType + ")) AND (STFIC.SOURCEINDEX = ?) " +
                    "AND ((STFIC.DATE_>= CONVERT(dateTime, ?, 104)) AND (STFIC.DATE_<= CONVERT(dateTime, ?, 104))) " +
                    "AND (CLNTC.CODE LIKE ? OR CLNTC.DEFINITION_ LIKE ?) " +
                    "ORDER BY STFIC.DATE_, STFIC.FTIME, STFIC.TRCODE, STFIC.FICHENO ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sourceIndex);
            statement.setString(2, begDate);
            statement.setString(3, endDate);
            statement.setString(4, "%" + filterCode + "%");
            statement.setString(5, "%" + filterCode + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                saleFiches.add(
                        new SaleFiches(
                                resultSet.getLong("id"),
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


    /* ------------------------------------------ Содержимое документа ---------------------------------------------------- */
    public List<SaleFiche> getFiche(int firmno, int periodno, int fiche) {

        utility.CheckCompany(firmno, periodno);
        List<SaleFiche> ficheList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS code, " +
                    "(SELECT NAME FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS name, " +
                    "CONVERT(varchar, STRNS.DATE_, 23) AS date, STRNS.AMOUNT AS count, " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_UNITSETL WHERE LOGICALREF = STRNS.UOMREF) AS unit, " +
                    "STRNS.PRICE AS price, STRNS.TOTAL AS total, (STRNS.PRICE / STRNS.REPORTRATE) AS priceusd, " +
                    "(STRNS.AMOUNT * (STRNS.PRICE / STRNS.REPORTRATE)) AS totalusd " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS " +
                    "WHERE (STRNS.TRCODE in (2,3,7,8)) AND STRNS.LINETYPE = 0 AND (STRNS.STFICHEREF = ?) " +
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
    public List<SaleTotal> getSalesTotal(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleTotal> saleTotals = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT ITEMS.CODE AS code, ITEMS.NAME AS name, ITEMS.STGRPCODE AS groupCode, " +
                    "SUM(STITOTS.PURAMNT) AS purchase_count, ROUND(SUM(STITOTS.PURCASH), 2) AS purchase_total, " +
                    "ROUND(SUM(STITOTS.PURCURR), 2) AS purchase_total_usd, SUM(STITOTS.SALAMNT) AS sale_count, " +
                    "ROUND(SUM(STITOTS.SALCASH), 2) AS sale_total, ROUND(SUM(STITOTS.SALCURR), 2) AS sale_total_usd " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVTOT STITOTS " +
                    "WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVTOT_I2) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS ON STITOTS.STOCKREF = ITEMS.LOGICALREF " +
                    "WHERE (STITOTS.INVENNO IN (?)) AND (STITOTS.SALAMNT <> 0) " +
                    "AND (STITOTS.DATE_ >= CONVERT(dateTime, ?, 104) AND STITOTS.DATE_ <=  CONVERT(dateTime, ?, 104)) " +
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


    /* ------------------------------------------ Продажи по месяцам ---------------------------------------------------- */
    public List<SaleMonth> getSalesMonth(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleMonth> saleMonths = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE, " +
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
                    "AND (CANCELLED=0) AND ((DATE_>= CONVERT(dateTime, ?, 104)) AND (DATE_<= CONVERT(dateTime, ?, 104))) and  ((SOURCEINDEX = ?) AND (IOCODE IN (1,2)))),0) - " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE " +
                    "WHERE ((TRCODE in (6,7,8,11,12,25,51))) AND (STOCKREF=Items.LOGICALREF) " +
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


    /* ------------------------------------------ Продажи по менеджерам ---------------------------------------------------- */
    public List<SaleClientManager> getSalesManager(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleClientManager> saleClientManagers = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT (SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS client_code, " +
                    "(SELECT DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS client_name, " +
                    "SUM(STRNS.AMOUNT) AS amount, ROUND(SUM(STRNS.LINENET), 2) AS total, " +
                    "ROUND(ISNULL(SUM(STRNS.LINENET / NULLIF(STRNS.REPORTRATE, 0)), 0), 2) AS total_usd, STRNS.TRCODE AS trcode " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) ON (STRNS.STFICHEREF  =  STFIC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC WITH(NOLOCK) ON (STRNS.STOCKREF  =  ITMSC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_SHIPINFO SHPINF WITH(NOLOCK) ON (STFIC.SHIPINFOREF  =  SHPINF.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STRNS.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STRNS.SOURCEINDEX IN (?)) AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) " +
                    "AND (STRNS.CPSTFLAG <> 1) AND (STRNS.DETLINE <> 1) AND (STRNS.LINETYPE NOT IN (2,3)) " +
                    "AND (STRNS.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39) ) AND (STFIC.CANCELLED = 0) ";
            if (!filterName.isEmpty()) {
                sqlQuery += "AND (CLNTC.PARENTCLREF = (SELECT TOP 1 LOGICALREF FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD " +
                        "WHERE CODE LIKE '%" + filterName + "%' OR DEFINITION_ LIKE '%" + filterName + "%'))";
            }
            sqlQuery += "GROUP BY CLNTC.PARENTCLREF, STRNS.TRCODE " +
                    "ORDER BY client_code ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sourceIndex);
            statement.setString(2, begDate);
            statement.setString(3, endDate);
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

    /* ------------------------------------------ Продажи по одному менеджеру ---------------------------------------------------- */
    public List<SaleClientManager> getSalesManagerOne(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String code) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleClientManager> saleClientManagers = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT (SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS client_code, " +
                    "(SELECT DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CLNTC.PARENTCLREF) AS client_name, " +
                    "SUM(STRNS.AMOUNT) AS amount, ROUND(SUM(STRNS.LINENET), 2) AS total, " +
                    "ROUND(ISNULL(SUM(STRNS.LINENET / NULLIF(STRNS.REPORTRATE, 0)), 0), 2) AS total_usd, STRNS.TRCODE AS trcode " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) ON (STRNS.STFICHEREF  =  STFIC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC WITH(NOLOCK) ON (STRNS.STOCKREF  =  ITMSC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_SHIPINFO SHPINF WITH(NOLOCK) ON (STFIC.SHIPINFOREF  =  SHPINF.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STRNS.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STRNS.SOURCEINDEX IN (?)) AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) " +
                    "AND (CLNTC.PARENTCLREF = (SELECT LOGICALREF FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE CODE = ?)) " +
                    "AND (STFIC.DEPARTMENT IN (0)) AND (STFIC.BRANCH IN (0)) " +
                    "AND (STFIC.FACTORYNR IN (0)) AND (STFIC.STATUS IN (0,1)) " +
                    "AND (STRNS.CPSTFLAG <> 1) AND (STRNS.DETLINE <> 1) AND (STRNS.LINETYPE NOT IN (2,3)) " +
                    "AND (STRNS.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39) ) AND (STFIC.CANCELLED = 0) " +
                    "GROUP BY CLNTC.PARENTCLREF, STRNS.TRCODE " +
                    "ORDER BY client_code ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sourceIndex);
            statement.setString(2, begDate);
            statement.setString(3, endDate);
            statement.setString(4, code);
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


    /* ------------------------------------------ Продажи по контрагентам ---------------------------------------------------- */
    public List<SaleClient> getSalesClient(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleClient> saleClients = new ArrayList<>();
        Map<String, SaleClient> map = new TreeMap<>();

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
                    "WHERE (STRNS.SOURCEINDEX IN (0)) AND (STFIC.DEPARTMENT IN (0)) AND (STFIC.BRANCH IN (0)) " +
                    "AND (STFIC.FACTORYNR IN (0)) AND (STFIC.STATUS IN (0,1)) " +
                    "AND (STRNS.CPSTFLAG <> 1) AND (STRNS.DETLINE <> 1) AND (STRNS.LINETYPE NOT IN (2,3)) " +
                    "AND (STRNS.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39) ) AND (STFIC.CANCELLED = 0) " +
                    "AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND  (STRNS.SOURCEINDEX = ?) " +
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
                    SaleClient client = map.get(resultSet.getString("client_code"));
                    if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                        count = client.getItemAmount() + resultSet.getDouble("amount");
                        total = client.getItemTotal() + resultSet.getDouble("total");
                        totalUsd = client.getItemTotalUsd() + resultSet.getDouble("total_usd");
                        client.setItemAmount(count);
                        client.setItemTotal(total);
                        client.setItemTotalUsd(totalUsd);
                    } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                        countRet = client.getItemAmountRet() + resultSet.getDouble("amount");
                        totalRet = client.getItemTotalRet() + resultSet.getDouble("total");
                        totalRetUsd = client.getItemTotalUsdRet() + resultSet.getDouble("total_usd");
                        client.setItemAmountRet(countRet);
                        client.setItemTotalRet(totalRet);
                        client.setItemTotalUsdRet(totalRetUsd);
                    }
                    map.put(resultSet.getString("client_code"), client);
                } else {
                    SaleClient client = new SaleClient();
                    client.setClientCode(resultSet.getString("client_code"));
                    client.setClientName(resultSet.getString("client_name"));

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
                        client.setItemAmountRet(resultSet.getDouble("amount"));
                        client.setItemTotalRet(resultSet.getDouble("total"));
                        client.setItemTotalUsdRet(resultSet.getDouble("total_usd"));
                    }
                    map.put(resultSet.getString("client_code"), client);
                }
            }
            saleClients.addAll(map.values());

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return saleClients;
    }


    /* ------------------------------------------ Перечень документов по контрагентам ---------------------------------------------------- */
    public List<SaleClientFiches> getSalesClientFiches(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String code) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleClientFiches> saleClients = new ArrayList<>();

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
                    "    WHERE (STFIC.CANCELLED = 0) AND (STFIC.TRCODE IN (2,3,4,7,8,9,35,36,37,38,39)) " +
                    "    AND ((STFIC.DATE_>= CONVERT(dateTime, ?, 104)) AND (STFIC.DATE_<= CONVERT(dateTime, ?, 104))) AND (STFIC.SOURCEINDEX = ?) " +
                    "    AND (CLNTC.CODE = ?) " +
                    "    ORDER BY STFIC.DATE_, STFIC.FTIME, STFIC.TRCODE, STFIC.FICHENO ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setInt(3, sourceIndex);
            statement.setString(4, code);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                saleClients.add(
                        new SaleClientFiches(
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
        return saleClients;
    }


    /* ------------------------------------------ Топ продаж по контрагентам ---------------------------------------------------- */
    public List<SaleClientTop> getSalesClientTop(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleClientTop> saleClientTops = new ArrayList<>();
        Map<String, SaleClientTop> map = new TreeMap<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT CLNTC.CODE AS client_code, CLNTC.DEFINITION_ AS client_name, " +
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
                    "AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND  (STRNS.SOURCEINDEX = ?) " +
                    "GROUP BY CLNTC.CODE, CLNTC.DEFINITION_, STRNS.TRCODE " +
                    "ORDER BY CLNTC.CODE ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setInt(3, sourceIndex);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getDouble("trcode") == 7 || resultSet.getDouble("trcode") == 8) {
                    if (map.containsKey(resultSet.getString("client_code"))) {
                        SaleClientTop saleClientTop = map.get(resultSet.getString("client_code"));
                        double amount = saleClientTop.getItemAmount() + resultSet.getDouble("amount");
                        double total = saleClientTop.getItemTotal() + resultSet.getDouble("total");
                        double totalUsd = saleClientTop.getItemTotalUsd() + resultSet.getDouble("total_usd");
                        saleClientTop.setItemAmount(amount);
                        saleClientTop.setItemTotal(total);
                        saleClientTop.setItemTotalUsd(totalUsd);
                        map.put(resultSet.getString("client_code"), saleClientTop);
                    } else {
                        SaleClientTop client = new SaleClientTop();
                        client.setClientCode(resultSet.getString("client_code"));
                        client.setClientName(resultSet.getString("client_name"));
                        client.setItemAmount(resultSet.getDouble("amount"));
                        client.setItemTotal(resultSet.getDouble("total"));
                        client.setItemTotalUsd(resultSet.getDouble("total_usd"));
                        client.setItemAmountRet(0.0);
                        client.setItemTotalRet(0.0);
                        client.setItemTotalUsdRet(0.0);
                        map.put(resultSet.getString("client_code"), client);
                    }
                } else if (resultSet.getDouble("trcode") == 2 || resultSet.getDouble("trcode") == 3) {
                    if (map.containsKey(resultSet.getString("client_code"))) {
                        SaleClientTop clientTop = map.get(resultSet.getString("client_code"));
                        double amount = clientTop.getItemAmountRet() + resultSet.getDouble("amount");
                        double total = clientTop.getItemTotalRet() + resultSet.getDouble("total");
                        double totalUsd = clientTop.getItemTotalUsdRet() + resultSet.getDouble("total_usd");
                        clientTop.setItemAmountRet(amount);
                        clientTop.setItemTotalRet(total);
                        clientTop.setItemTotalUsdRet(totalUsd);
                        map.put(resultSet.getString("client_code"), clientTop);
                    } else {
                        SaleClientTop client = new SaleClientTop();
                        client.setClientCode(resultSet.getString("client_code"));
                        client.setClientName(resultSet.getString("client_name"));
                        client.setItemAmount(0.0);
                        client.setItemTotal(0.0);
                        client.setItemTotalUsd(0.0);
                        client.setItemAmountRet(resultSet.getDouble("amount"));
                        client.setItemTotalRet(resultSet.getDouble("total"));
                        client.setItemTotalUsdRet(resultSet.getDouble("total_usd"));
                        map.put(resultSet.getString("client_code"), client);
                    }
                }
            }
            saleClientTops.addAll(map.values());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return saleClientTops;
    }


    /* ------------------------------------------ Сводная таблица продаж ---------------------------------------------------- */
    public List<SaleTable> getSalesTable(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleTable> saleTables = new ArrayList<>();
        Map<Integer, SaleTable> map_month = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT MONTH(INVFC.DATE_) AS date, INVFC.TRCODE, STRNS.LINETYPE, " +
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
                    "WHERE ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (INVFC.DEPARTMENT IN (0)) AND (INVFC.BRANCH IN (0)) AND (INVFC.FACTORYNR IN (0)) " +
                    "AND (INVFC.STATUS IN (0,1)) AND (NOT(INVFC.TRCODE IN(5, 10)) ) " +
                    "AND (INVFC.CANCELLED =  0 ) AND (INVFC.GRPCODE = 2) ";
            if (!filterName.isEmpty()) {
                sqlQuery += "AND (CLNTC.CODE LIKE '%" + filterName + "%' OR CLNTC.DEFINITION_ LIKE '%" + filterName + "%') ";
            }
            sqlQuery += "GROUP BY INVFC.DATE_, INVFC.TRCODE, STRNS.LINETYPE " +
                    "ORDER BY INVFC.DATE_, INVFC.TRCODE, STRNS.LINETYPE ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setInt(3, sourceIndex);
            ResultSet resultSet = statement.executeQuery();

            double total = 0;
            double net = 0;
            double expenses = 0;
            double discounts = 0;
            double ret = 0;
            double ret_usd = 0;
            double net_usd = 0;
            int currentMonth = 0;

            while (resultSet.next()) {
                int month = resultSet.getInt("date");
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

                saleTable.setDate(month);
                saleTable.setTotal(total - ret);
                saleTable.setDiscounts(discounts);
                saleTable.setExpenses(expenses);
                saleTable.setNet(net - ret);
                saleTable.setNet_usd(net_usd - ret_usd);
                saleTable.setRet_total(ret);
                saleTable.setRet_total_usd(ret_usd);
                currentMonth = month;
                map_month.put(saleTable.getDate(), saleTable);
            }

            saleTables = new ArrayList<>(map_month.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saleTables;
    }


    /* ------------------------------------------ Подробный отчет продаж ---------------------------------------------------- */
    public List<SaleDetail> getSalesDetail(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleDetail> saleDetails = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Select ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE, " +
                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/STRNS.UINFO1)) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) " +
                    "AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (STRNS.TRCODE IN (2,3)) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)), 0),2) as iade, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) " +
                    "* (STRNS.TOTAL+STRNS.DISTEXP-STRNS.DISTDISC+STRNS.DIFFPRICE-STRNS.VATINC*STRNS.VATAMNT)/STRNS.REPORTRATE) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.REPORTRATE > 0.0" + GLOBAL_FIRM_NO + ") " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) " +
                    "AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) " +
                    "AND ((STRNS.TRCODE IN (2,3)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 2)) AND STRNS.TRCODE IN (2,3))) " +
                    "AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)),0),2) as iade_tutari_usd, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND (STRNS.TRCODE IN (2,3)) " +
                    "AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)  ), 0), 2) as iade_maliyeti_usd, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/STRNS.UINFO1)) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND ((STRNS.IOCODE IN (3,4))) " +
                    "AND ((STRNS.TRCODE IN (7,8)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) " +
                    "AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND STRNS.TRCODE IN (7,8)  )) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)), 0), 2) as net_satis, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) \n" +
                    "* (STRNS.TOTAL+STRNS.DISTEXP-STRNS.DISTDISC+STRNS.DIFFPRICE-STRNS.VATINC*STRNS.VATAMNT)/STRNS.REPORTRATE) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) " +
                    "AND (STRNS.REPORTRATE > 0.0" + GLOBAL_FIRM_NO + ") AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) " +
                    "AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND ((STRNS.IOCODE IN (3,4))) " +
                    "AND ((STRNS.TRCODE IN (7,8)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) AND STRNS.TRCODE IN (7,8)  )) " +
                    "AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND (STRNS.SOURCEINDEX = ?) " +
                    "AND (STRNS.CLIENTREF = CLNTC.LOGICALREF)), 0), 2) as satis_tutari_usd, " +

                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) " +
                    "AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND (STRNS.TRCODE IN (7,8)) AND (STRNS.CLIENTREF = CLNTC.LOGICALREF) " +
                    "AND ((STRNS.DATE_>= CONVERT(dateTime, ?, 104)) AND (STRNS.DATE_<= CONVERT(dateTime, ?, 104))) AND (STRNS.SOURCEINDEX = ?)), 0), 2) as  satis_maliyeti_usd " +

                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS As ITEMS " +
                    "WHERE (CARDTYPE <> 22) AND (ITEMS.CODE LIKE ? OR ITEMS.NAME LIKE ?) AND (LOGICALREF IN (SELECT STOCKREF FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE))" +
                    "ORDER BY CODE";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setInt(3, sourceIndex);
            statement.setString(4, begDate);
            statement.setString(5, endDate);
            statement.setInt(6, sourceIndex);
            statement.setString(7, begDate);
            statement.setString(8, endDate);
            statement.setInt(9, sourceIndex);
            statement.setString(10, begDate);
            statement.setString(11, endDate);
            statement.setInt(12, sourceIndex);
            statement.setString(13, begDate);
            statement.setString(14, endDate);
            statement.setInt(15, sourceIndex);
            statement.setString(16, begDate);
            statement.setString(17, endDate);
            statement.setInt(18, sourceIndex);
            statement.setString(19, "%" + filterName + "%");
            statement.setString(20, "%" + filterName + "%");
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
    public List<SaleDaily> getSalesDaily(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) {

        utility.CheckCompany(firmNo, periodNo);
        List<SaleDaily> saleClients = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT  CONVERT(varchar, LGMAIN.DATE_, 23) AS date, LGMAIN.TRCODE AS trcode, " +
                    "SUM(LGMAIN.NETTOTAL) AS net_total, SUM(LGMAIN.REPORTNET) AS report_net " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE LGMAIN  " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (LGMAIN.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "WHERE (LGMAIN.GRPCODE = 2) AND ((LGMAIN.DATE_>= CONVERT(dateTime, ?, 104)) " +
                    "AND (LGMAIN.DATE_<= CONVERT(dateTime, ?, 104))) AND (LGMAIN.SOURCEINDEX=?) " +
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
