package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.products.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_FIRM_NO;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_PERIOD;

@Repository
public class ProductRepository {

    private final Utility utility;
    private final DataSource dataSource;

    @Autowired
    public ProductRepository(Utility utility, DataSource dataSource) {
        this.utility = utility;
        this.dataSource = dataSource;
    }

    /* ------------------------------------------ Остаток товаров ---------------------------------------------------- */
    public List<Product> getProducts(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<Product> itemsList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT ITEMS.CODE as code, ITEMS.NAME as name, ITEMS.STGRPCODE AS groupcode, " +
                    "ISNULL ((Select Top 1 PRICE From LG_" + GLOBAL_FIRM_NO + "_PRCLIST Where ((PTYPE=1) AND (CARDREF=ITEMS.LOGICALREF))),0) AS purchaseprice, " +
                    "ISNULL ((Select Top 1 PRICE From LG_" + GLOBAL_FIRM_NO + "_PRCLIST Where ((PTYPE=2) AND (CARDREF=ITEMS.LOGICALREF))),0) AS saleprice, " +
                    "SUM(STITOTS.PURAMNT) AS puramount, SUM(STITOTS.PURCURR) AS purcurr, SUM(STITOTS.PURCASH) AS purcash, " +
                    "SUM(STITOTS.SALAMNT) AS salamount, SUM(STITOTS.SALCURR) AS salcurr, SUM(STITOTS.SALCASH) AS salcash, " +
                    "SUM(STITOTS.ONHAND) as onhand " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STINVTOT STITOTS INNER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS ON STITOTS.STOCKREF = ITEMS.LOGICALREF " +
                    "WHERE (STITOTS.INVENNO = ?) AND (STITOTS.DATE_ >= CONVERT(dateTime, ?, 104)) AND (STITOTS.DATE_  <= CONVERT(dateTime, ?, 104)) " +
                    "AND (ITEMS.CODE LIKE ? OR ITEMS.NAME LIKE ?)" +
                    "GROUP BY ITEMS.LOGICALREF, ITEMS.CODE, ITEMS.NAME, ITEMS.STGRPCODE";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sourceIndex);
            statement.setString(2, begDate);
            statement.setString(3, endDate);
            statement.setString(4, "%" + filterName + "%");
            statement.setString(5, "%" + filterName + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                itemsList.add(
                        new Product(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("groupcode"),
                                resultSet.getDouble("purchaseprice"),
                                resultSet.getDouble("saleprice"),
                                resultSet.getDouble("puramount"),
                                resultSet.getDouble("purcurr"),
                                resultSet.getDouble("salamount"),
                                resultSet.getDouble("salcurr"),
                                resultSet.getDouble("onhand"),
                                resultSet.getDouble("purchaseprice") * resultSet.getInt("onhand"),
                                resultSet.getDouble("saleprice") * resultSet.getInt("onhand")
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsList;
    }


    /* ---------------------------------------- Инвентарный отчет ------------------------------------------------ */
    public List<ProductInventory> getProductsInventory(int firmNo, int periodNo) {

        utility.CheckCompany(firmNo, periodNo);
        List<ProductInventory> itemsInventoryList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Select ITEMS.CODE code, ITEMS.NAME name, ITEMS.STGRPCODE groupcode, " +
                    "ROUND(ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/(CASE STRNS.UINFO1 WHEN 0 THEN 1 ELSE STRNS.UINFO1 END))) " +
                    "FROM  LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS " +
                    "WHERE (STRNS.STOCKREF = ITEMS.LOGICALREF) " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND " +
                    "((STRNS.TRCODE IN (1, 2, 3, 5, 10, 13, 14, 15, 16, 17, 18, 19, 26, 30, 31, 32, 33, 34, 50)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 2)) AND " +
                    "STRNS.TRCODE IN (1, 2, 3, 5, 10, 13, 14, 15, 16, 17, 18, 19, 25, 26, 30, 31, 32, 33, 34, 50)  ))), 0) + " +
                    "ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.AMOUNT*(STRNS.UINFO2/STRNS.UINFO1)) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS WHERE  " +
                    "(STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) AND " +
                    "(STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND ((STRNS.IOCODE IN (3,4))) AND ((STRNS.TRCODE IN (2, 3, 4, 6, 7, 8, 9, 11, 12, 20, 21, 22, 23, 24, 35, " +
                    "36, 37, 38, 39, 51)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) AND STRNS.TRCODE IN (2, 3, 4, 6, 7, 8, 9, 11, 12, 20, " +
                    " 21, 22, 23, 24, 25, 35, 36, 37, 38, 39, 51)  ))), 0), 0) as onhand,  " +
                    "ROUND(ISNULL((SELECT SUM(CASE STRNS.OUTCOSTCURR WHEN 0 THEN (STRNS.TOTAL-STRNS.DISTCOST+STRNS.DIFFPRICE)/STRNS.REPORTRATE ELSE STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT END) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS  WHERE " +
                    "(STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.REPORTRATE > 0.0 ) " +
                    "AND (STRNS.CANCELLED = 0) AND (STRNS.STFICHEREF <> 0) " +
                    "AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) AND (STRNS.IOCODE < 3) " +
                    "AND (STRNS.TRCODE IN (1, 2, 3, 5, 13, 14, 15, 16, 17, 18, 19, 25, 26, 30, 31, 32, 33, 34, 50))), 0)+ " +
                    "ISNULL((SELECT SUM(((2.5-STRNS.IOCODE)/ABS(2.5-STRNS.IOCODE)) * STRNS.OUTCOSTCURR*STRNS.UINFO2/STRNS.UINFO1*STRNS.AMOUNT) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS  WHERE " +
                    "(STRNS.STOCKREF = ITEMS.LOGICALREF) AND (STRNS.CANCELLED = 0) " +
                    "AND (STRNS.STFICHEREF <> 0) AND (STRNS.LPRODSTAT <> 2) AND (STRNS.LINETYPE IN (0, 1, 5, 6, 8, 9)) " +
                    "AND((STRNS.TRCODE IN (4,6,7,8,9,11,12,20,21, 22, 23, 24, 35, 36, 37, 38, 39, 51)) OR (((STRNS.TRCODE = 25) AND (STRNS.IOCODE = 3)) " +
                    "AND STRNS.TRCODE IN (4, 6, 7, 8, 9, 11, 12, 20, 21, 22, 23, 24, 25, 35, 36, 37, 38, 39, 51)  ))), 0), 0) AS total " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS AS ITEMS WHERE (ITEMS.CARDTYPE <> 22) ORDER BY ITEMS.CODE";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                ProductInventory inventory = new ProductInventory();

                inventory.setItem_code(resultSet.getString("code"));
                inventory.setItem_name(resultSet.getString("name"));
                inventory.setItem_group(resultSet.getString("groupcode"));
                inventory.setItem_onHand(resultSet.getDouble("onhand"));

                if (resultSet.getDouble("onhand") > 0) {
                    inventory.setItem_avgVal(resultSet.getDouble("total") / resultSet.getDouble("onhand"));
                    inventory.setItem_total(resultSet.getDouble("total"));
                } else {
                    inventory.setItem_avgVal(0);
                    inventory.setItem_total(0);
                }
                itemsInventoryList.add(inventory);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsInventoryList;
    }


    /* ---------------------------------------- Список документов ------------------------------------------------ */
    public List<ProductFiches> getProductFiche(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) {

        utility.CheckCompany(firmNo, periodNo);
        List<ProductFiches> itemsFicheList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT LGMAIN.LOGICALREF AS id, LGMAIN.FICHENO AS ficheno, " +
                    "CONVERT(varchar, LGMAIN.DATE_, 23) AS date, LGMAIN.TRCODE AS trcode, " +
                    "LGMAIN.REPORTNET AS net, LGMAIN.NETTOTAL AS nettotal, LGMAIN.REPORTRATE AS reportrate " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE LGMAIN " +
                    "WHERE (LGMAIN.GRPCODE = 3) AND (LGMAIN.DATE_ >= CONVERT(dateTime, ?, 104)) " +
                    "AND (LGMAIN.DATE_ <= CONVERT(dateTime, ?, 104)) AND (LGMAIN.SOURCEINDEX = ?) " +
                    "ORDER BY LGMAIN.GRPCODE, LGMAIN.DATE_, LGMAIN.FTIME, " +
                    "LGMAIN.IOCODE, LGMAIN.TRCODE, LGMAIN.LOGICALREF ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setInt(3, sourceIndex);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                itemsFicheList.add(
                        new ProductFiches(
                                resultSet.getLong("id"),
                                resultSet.getString("ficheno"),
                                resultSet.getString("date"),
                                resultSet.getInt("trcode"),
                                resultSet.getDouble("net"),
                                resultSet.getDouble("nettotal"),
                                resultSet.getDouble("reportrate")
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsFicheList;
    }


    /* ---------------------------------------- Содержимое документа ------------------------------------------------ */
    public List<ProductFiche> getFiche(int firmNo, int periodNo, String begDate, String endDate, int fiche) {

        utility.CheckCompany(firmNo, periodNo);
        List<ProductFiche> itemsFicheList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS code, " +
                    "(SELECT NAME FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS name, " +
                    "STRNS.AMOUNT AS count, STRNS.PRICE AS price, " +
                    "(STRNS.PRICE / STRNS.REPORTRATE) AS priceusd, " +
                    "STRNS.TOTAL AS total, (STRNS.TOTAL / STRNS.REPORTRATE) AS totalusd, " +
                    "STRNS.LINEEXP AS definition " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS " +
                    "WHERE (STRNS.DETLINE = 0) AND (STRNS.DATE_ >= CONVERT(dateTime, ?, 104)) " +
                    "AND (STRNS.DATE_ <= CONVERT(dateTime, ?, 104)) AND (STRNS.STFICHEREF IN (?)) " +
                    "ORDER BY STRNS.STFICHEREF, STRNS.STFICHELNNO ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setInt(3, fiche);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                itemsFicheList.add(
                        new ProductFiche(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getInt("count"),
                                resultSet.getDouble("price"),
                                resultSet.getDouble("priceUsd"),
                                resultSet.getDouble("total"),
                                resultSet.getDouble("totalusd"),
                                resultSet.getString("definition")
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsFicheList;
    }


    /* ---------------------------------------- Достаточность товаров ------------------------------------------------ */
    public List<ProductEnough> getProductEnough(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<ProductEnough> itemsPriceList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT ROW_NUMBER() OVER(ORDER BY code ASC) AS row, ITEMS.CODE AS code, " +
                    "ITEMS.NAME AS name, ITEMS.STGRPCODE AS groupcode, GNSTITOT.ONHAND AS onhand, " +
                    "ISNULL((SELECT USLINE.CODE FROM LG_" + GLOBAL_FIRM_NO + "_UNITSETL USLINE WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_UNITSETL_I4) " +
                    "WHERE (USLINE.UNITSETREF = ITEMS.UNITSETREF) AND (USLINE.MAINUNIT = 1)), 0) AS unit, " +
                    "ISNULL((SELECT TOP 1 PRICE FROM LG_" + GLOBAL_FIRM_NO + "_PRCLIST prclist WHERE ((ITEMS.LOGICALREF = prclist.CARDREF) " +
                    "AND (PTYPE = 2) AND (BEGDATE <= GETDATE()) AND (ENDDATE >= GETDATE()))), 0) AS price " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS LEFT JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD +
                    "_GNTOTST GNSTITOT WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_GNTOTST_I1) " +
                    "ON (GNSTITOT.STOCKREF = ITEMS.LOGICALREF) WHERE ((GNSTITOT.INVENNO = -1) AND (GNSTITOT.ONHAND > 0)) ORDER BY ITEMS.CODE";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = statement.executeQuery();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsPriceList;
    }


    /* ---------------------------------------- Движения товаров ------------------------------------------------ */
    public List<ProductTransaction> getProductTransactions(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<ProductTransaction> itemsPriceList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT CONVERT(varchar, LGMAIN.DATE_, 23) AS date, STFIC.FICHENO AS ficheno, LGMAIN.TRCODE AS trcode, " +
                    "(SELECT TOP 1 CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = STFIC.CLIENTREF) AS clientcode, " +
                    "(SELECT TOP 1 DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = STFIC.CLIENTREF) AS clientname, " +
                    "LGMAIN.AMOUNT AS count, LGMAIN.PRICE AS price, LGMAIN.PRPRICE AS priceusd, " +
                    "LGMAIN.LINENET AS total, (LGMAIN.AMOUNT * LGMAIN.PRPRICE) AS totalusd " +
                    " FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE LGMAIN, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC " +
                    " LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF   =  INVFC.LOGICALREF) " +
                    " WHERE (LGMAIN.LINETYPE IN (0, 1, 5, 6, 8, 9, 11)) AND (LGMAIN.STFICHEREF <> 0) " +
                    "AND (LGMAIN.STFICHEREF = STFIC.LOGICALREF) " +
                    "AND (((STFIC.GRPCODE = 1)) OR ((STFIC.GRPCODE = 2)) OR ((STFIC.GRPCODE = 3)) OR (STFIC.GRPCODE = 0)) " +
                    "AND (LGMAIN.DATE_ >= CONVERT(dateTime, ?, 104)) AND (LGMAIN.DATE_ <= CONVERT(dateTime, ?, 104)) AND (LGMAIN.SOURCEINDEX IN (?)) " +
                    "ORDER BY LGMAIN.DATE_, LGMAIN.FTIME, LGMAIN.IOCODE, LGMAIN.SOURCEINDEX";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setInt(3, sourceindex);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                itemsPriceList.add(
                        new ProductTransaction(
                                resultSet.getString("date"),
                                resultSet.getString("ficheno"),
                                resultSet.getInt("trcode"),
                                resultSet.getString("clientcode"),
                                resultSet.getString("clientname"),
                                resultSet.getInt("count"),
                                resultSet.getDouble("price"),
                                resultSet.getDouble("priceusd"),
                                resultSet.getDouble("total"),
                                resultSet.getDouble("totalusd")
                        )
                );
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsPriceList;
    }


    /* ---------------------------------------- Движения одного товара ------------------------------------------------ */
    public List<ProductTransaction> getProductTransaction(int firmno, int periodno, String begdate, String enddate, int sourceindex, String code) {

        utility.CheckCompany(firmno, periodno);
        List<ProductTransaction> itemsPriceList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT CONVERT(varchar, LGMAIN.DATE_, 23) AS date, STFIC.FICHENO AS ficheno, LGMAIN.TRCODE AS trcode, " +
                    "(SELECT TOP 1 CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = STFIC.CLIENTREF) AS clientcode, " +
                    "(SELECT TOP 1 DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = STFIC.CLIENTREF) AS clientname, " +
                    "LGMAIN.AMOUNT AS count, LGMAIN.PRICE AS price, LGMAIN.PRPRICE AS priceusd, " +
                    "LGMAIN.LINENET AS total, (LGMAIN.AMOUNT * LGMAIN.PRPRICE) AS totalusd " +
                    " FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE LGMAIN, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC " +
                    " LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF   =  INVFC.LOGICALREF) " +
                    " WHERE (LGMAIN.LINETYPE IN (0, 1, 5, 6, 8, 9, 11)) AND (LGMAIN.STFICHEREF <> 0) " +
                    "AND (LGMAIN.STFICHEREF = STFIC.LOGICALREF) " +
                    "AND (((STFIC.GRPCODE = 1)) OR ((STFIC.GRPCODE = 2)) OR ((STFIC.GRPCODE = 3)) OR (STFIC.GRPCODE = 0)) " +
                    "AND (LGMAIN.DATE_ >= CONVERT(dateTime, ?, 104)) AND (LGMAIN.DATE_ <= CONVERT(dateTime, ?, 104)) AND (LGMAIN.SOURCEINDEX IN (?)) " +
                    "AND (LGMAIN.STOCKREF = (SELECT TOP 1 LOGICALREF FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE CODE = '" + code + "')) " +
                    "ORDER BY LGMAIN.DATE_, LGMAIN.FTIME, LGMAIN.IOCODE, LGMAIN.SOURCEINDEX";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setInt(3, sourceindex);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                itemsPriceList.add(
                        new ProductTransaction(
                                resultSet.getString("date"),
                                resultSet.getString("ficheno"),
                                resultSet.getInt("trcode"),
                                resultSet.getString("clientcode"),
                                resultSet.getString("clientname"),
                                resultSet.getInt("count"),
                                resultSet.getDouble("price"),
                                resultSet.getDouble("priceusd"),
                                resultSet.getDouble("total"),
                                resultSet.getDouble("totalusd")
                        )
                );
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsPriceList;
    }


    /* ---------------------------------------- Прайс лист ------------------------------------------------ */
    public List<ProductPrice> getProductPrice(int firmNo, int periodNo, String begDate, String endDate, int sourceIndex) {

        utility.CheckCompany(firmNo, periodNo);
        List<ProductPrice> itemsPriceList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT ROW_NUMBER() OVER(ORDER BY code ASC) AS row, ITEMS.CODE AS code, " +
                    "ITEMS.NAME AS name, ITEMS.STGRPCODE AS groupcode, GNSTITOT.ONHAND AS onhand, " +
                    "ISNULL((SELECT USLINE.CODE FROM LG_" + GLOBAL_FIRM_NO + "_UNITSETL USLINE WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_UNITSETL_I4) " +
                    "WHERE (USLINE.UNITSETREF = ITEMS.UNITSETREF) AND (USLINE.MAINUNIT = 1)), 0) AS unit, " +
                    "ISNULL((SELECT TOP 1 PRICE FROM LG_" + GLOBAL_FIRM_NO + "_PRCLIST prclist WHERE ((ITEMS.LOGICALREF = prclist.CARDREF) " +
                    "AND (PTYPE = 2))), 0) AS price " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS LEFT JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD +
                    "_GNTOTST GNSTITOT " +
                    "ON (GNSTITOT.STOCKREF = ITEMS.LOGICALREF) WHERE ((GNSTITOT.INVENNO = ?) AND (GNSTITOT.ONHAND > 0)) ORDER BY ITEMS.CODE";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sourceIndex);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                itemsPriceList.add(
                        new ProductPrice(
                                resultSet.getLong("row"),
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("groupcode"),
                                resultSet.getDouble("onhand"),
                                resultSet.getString("unit"),
                                resultSet.getDouble("price")
                        )
                );
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsPriceList;
    }


    /* ---------------------------------------- Цены всех товаров ------------------------------------------------ */
    public List<ProductPrices> getProductsPrices(int firmno, int periodno, String begdate, String enddate) {

        utility.CheckCompany(firmno, periodno);
        List<ProductPrices> itemsPrices = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT ITMSC.CODE AS code, ITMSC.NAME AS name, LGMAIN.DEFINITION_ AS definition, " +
                    "LGMAIN.PTYPE AS ptype, LGMAIN.PRICE AS price, " +
                    "(SELECT CURCODE FROM L_CURRENCYLIST WHERE LOGICALREF = LGMAIN.CURRENCY) AS currency, " +
                    "CONVERT(varchar, LGMAIN.BEGDATE, 23) AS begdate, " +
                    "CONVERT(varchar, LGMAIN.ENDDATE, 23) AS enddate, LGMAIN.ACTIVE AS active " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_PRCLIST LGMAIN " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC ON (LGMAIN.CARDREF  =  ITMSC.LOGICALREF) " +
                    "WHERE (LGMAIN.ACTIVE = 0) AND (LGMAIN.MTRLTYPE = 0) " +
//                    "AND (LGMAIN.BEGDATE <= CONVERT(dateTime, ?, 104)) AND (LGMAIN.ENDDATE >= CONVERT(dateTime, ?, 104)) " +
                    "ORDER BY LGMAIN.PTYPE, LGMAIN.CARDREF, LGMAIN.MTRLTYPE, LGMAIN.CLIENTCODE, LGMAIN.LOGICALREF";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                itemsPrices.add(
                        new ProductPrices(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("definition"),
                                resultSet.getInt("ptype"),
                                resultSet.getDouble("price"),
                                resultSet.getString("currency"),
                                resultSet.getString("begdate"),
                                resultSet.getString("enddate"),
                                resultSet.getInt("active")
                        )
                );
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsPrices;
    }


    /* ---------------------------------------- Цены товара ------------------------------------------------ */
    public List<ProductPrices> getProductPrices(int firmno, int periodno, String begdate, String enddate, String code) {

        utility.CheckCompany(firmno, periodno);
        List<ProductPrices> itemsPrices = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT ITMSC.CODE AS code, ITMSC.NAME AS name, LGMAIN.DEFINITION_ AS definition, " +
                    "LGMAIN.PTYPE AS ptype, LGMAIN.PRICE AS price, " +
                    "(SELECT CURCODE FROM L_CURRENCYLIST WHERE LOGICALREF = LGMAIN.CURRENCY) AS currency, " +
                    "CONVERT(varchar, LGMAIN.BEGDATE, 23) AS begdate, " +
                    "CONVERT(varchar, LGMAIN.ENDDATE, 23) AS enddate, LGMAIN.ACTIVE AS active " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_PRCLIST LGMAIN " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC ON (LGMAIN.CARDREF  =  ITMSC.LOGICALREF) " +
                    "WHERE (LGMAIN.ACTIVE = 0) AND ( ITMSC.CODE = ?) AND (LGMAIN.MTRLTYPE = 0) " +
//                    "AND (LGMAIN.BEGDATE <= CONVERT(dateTime, ?, 104)) AND (LGMAIN.ENDDATE >= CONVERT(dateTime, ?, 104)) " +
                    "ORDER BY LGMAIN.PTYPE, LGMAIN.CARDREF, LGMAIN.MTRLTYPE, LGMAIN.CLIENTCODE, LGMAIN.LOGICALREF";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                itemsPrices.add(
                        new ProductPrices(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("definition"),
                                resultSet.getInt("ptype"),
                                resultSet.getDouble("price"),
                                resultSet.getString("currency"),
                                resultSet.getString("begdate"),
                                resultSet.getString("enddate"),
                                resultSet.getInt("active")
                        )
                );
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return itemsPrices;
    }

}
