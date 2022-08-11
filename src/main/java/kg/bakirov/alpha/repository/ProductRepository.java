package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.products.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public List<Product> getProducts(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<Product> itemsList = null;

        try (Connection connection = dataSource.getConnection()) {

            /*String sqlQuery = "SELECT ITEMS.CODE as code, ITEMS.NAME as name, ITEMS.STGRPCODE AS groupcode, " +
                    "GNSTITOT.TRANSFERRED + GNSTITOT.PURAMNT + GNSTITOT.ACTPRODIN AS puramount, " +
                    "(GNSTITOT.AVGCURRVAL + GNSTITOT.PURCURR) AS purcurr, " +
                    "GNSTITOT.SALAMNT AS salamount, GNSTITOT.SALCURR AS salcurr, " +
                    "GNSTITOT.ONHAND as onhand, GNSTITOT.LASTTRDATE AS lasttrdate " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS ITEMS LEFT JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_GNTOTST GNSTITOT " +
                    "WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_GNTOTST_I1) " +
                    "ON (ITEMS.LOGICALREF = GNSTITOT.STOCKREF) WHERE (GNSTITOT.INVENNO = -1) ";*/
            
            String sqlQuery = "Set DateFormat DMY SELECT Items.CODE as code, Items.NAME as name, Upper(Items.STGRPCODE) as groupcode, " +

                    "ISNULL ((Select Top 1 PRICE From LG_" + GLOBAL_FIRM_NO + "_PRCLIST Where ((PTYPE=1) and (CARDREF=Items.LOGICALREF))),0) AS purchaseprice, " +
                    "ISNULL ((Select Top 1 PRICE From LG_" + GLOBAL_FIRM_NO + "_PRCLIST Where ((PTYPE=2) and (CARDREF=Items.LOGICALREF))),0) AS saleprice, " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (1,2,3,13,14,25,50))) " +
                    "AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>='" + begdate + "') AND (DATE_<='" + enddate + "')) AND ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (1,2)))),0) AS puramount, " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (6,7,8,11,12,25,51))) " +
                    "AND (STOCKREF=Items.LOGICALREF) AND (CANCELLED=0) " +
                    "AND ((DATE_>='" + begdate + "') AND (DATE_<='" + enddate + "')) and ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (3,4)))),0) AS salamount," +
                    "(ISNULL ((Select SUM(LINENET  / NULLIF(REPORTRATE, 0) ) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (7,8))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) " +"AND ((DATE_>='" + begdate + "') " +
                    "AND (DATE_<='" + enddate + "')) and ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (3,4)))),0) - " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (3))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) " +
                    "AND ((DATE_>='" + begdate + "') AND (DATE_<='" + enddate + "')) and ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (1,2)))),0)) AS salcurr, " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (1,2,3,13,14,25,50))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>='" + begdate + "') AND (DATE_<='" + enddate + "')) and ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (1,2)))),0) - " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (6,7,8,11,12,25,51))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>='" + begdate + "') AND (DATE_<='" + enddate + "')) and ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (3,4)))),0) AS onhand, " +
                    "(ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (1,2,3,13,14,25,50))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>='" + begdate + "') AND (DATE_<='" + enddate + "')) and ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (1,2)))),0) - " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (6,7,8,11,12,25,51))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>='" + begdate + "') AND (DATE_<='" + enddate + "')) and ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (3,4)))),0))* " +
                    "ISNULL ((Select Top 1 PRICE From LG_" + GLOBAL_FIRM_NO + "_PRCLIST Where ((PTYPE=1) and (CARDREF=Items.LOGICALREF))),0) as purchase_sum, " +
                    "(ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (1,2,3,13,14,25,50))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>=" + "'" + begdate + "') AND (DATE_<=" + "'" + enddate + "')) and ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (1,2)))),0) - " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (6,7,8,11,12,25,51))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>=" + "'" + begdate + "') AND (DATE_<=" + "'" + enddate + "')) and ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (3,4)))),0)) * " +
                    "ISNULL ((Select Top 1 PRICE From LG_" + GLOBAL_FIRM_NO + "_PRCLIST Where ((PTYPE=2) and (CARDREF=Items.LOGICALREF))),0) as sale_sum " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS As Items " +
                    "WHERE  (ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (1,2,3,13,14,25,50))) " +
                    "AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>='" + begdate + "') AND (DATE_<='" + enddate + "')) and  ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (1,2)))),0) - " +
                    "ISNULL ((Select SUM(AMOUNT*UINFO2) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE WHERE ((TRCODE in (6,7,8,11,12,25,51))) AND (STOCKREF=Items.LOGICALREF) " +
                    "AND (CANCELLED=0) AND ((DATE_>='" + begdate + "') AND (DATE_<='" + enddate + "')) and  ((SOURCEINDEX = " + sourceindex + ") AND (IOCODE IN (3,4)))),0)<>0) " +
                    "Order BY code";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsList = new ArrayList<>();

            while (resultSet.next()) {
                itemsList.add(
                        new Product(
                                resultSet.getString("code"),
                                resultSet.getString("name"),
                                resultSet.getString("groupcode"),
                                resultSet.getDouble("purchaseprice"),
                                resultSet.getDouble("saleprice"),
                                resultSet.getInt("puramount"),
                                resultSet.getInt("salamount"),
                                resultSet.getDouble("salcurr"),
                                resultSet.getInt("onhand"),
                                resultSet.getDouble("purchase_sum"),
                                resultSet.getDouble("sale_sum")
                        )
                );
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return itemsList;
    }


    /* ---------------------------------------- Инвентарный отчет ------------------------------------------------ */

    public List<ProductInventory> getProductsInventory(int firmno, int periodno) {

        utility.CheckCompany(firmno, periodno);
        List<ProductInventory> itemsInventoryList = null;

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

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsInventoryList = new ArrayList<>();

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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return itemsInventoryList;
    }



    /* ---------------------------------------- Список документов ------------------------------------------------ */

    public List<ProductFiches> getProductFiche(int firmno, int periodno, String begdate, String enddate, int sourceindex ) {

        utility.CheckCompany(firmno, periodno);
        List<ProductFiches> itemsFicheList = null;

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY " +
                    "SELECT LGMAIN.FICHENO AS ficheno, CONVERT(varchar, LGMAIN.DATE_, 23) AS date, LGMAIN.TRCODE AS trcode, " +
                    "LGMAIN.REPORTNET AS net, LGMAIN.NETTOTAL AS nettotal, LGMAIN.REPORTRATE AS reportrate " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE LGMAIN " +
                    "WHERE (LGMAIN.GRPCODE = 3) AND (LGMAIN.DATE_ >= '"+ begdate + "') " +
                    "AND (LGMAIN.DATE_ <= '"+ enddate + "') AND (LGMAIN.SOURCEINDEX = " + sourceindex + ") " +
                    "ORDER BY " +
                    "LGMAIN.GRPCODE, LGMAIN.DATE_, LGMAIN.FTIME, LGMAIN.IOCODE, LGMAIN.TRCODE, LGMAIN.LOGICALREF ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsFicheList = new ArrayList<>();

            while (resultSet.next()) {

                itemsFicheList.add(
                        new ProductFiches(
                                resultSet.getString("ficheno"),
                                resultSet.getString("date"),
                                resultSet.getInt("trcode"),
                                resultSet.getDouble("net"),
                                resultSet.getDouble("nettotal"),
                                resultSet.getDouble("reportrate")
                        )
                );
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return itemsFicheList;
    }


    /* ---------------------------------------- Контент документа ------------------------------------------------ */

    public List<ProductFiche> getFiche(int firmno, int periodno, int fiche ) {

        utility.CheckCompany(firmno, periodno);
        List<ProductFiche> itemsFicheList = null;

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS code, \n" +
                    "(SELECT NAME FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE LOGICALREF = STRNS.STOCKREF) AS name, \n" +
                    "STRNS.AMOUNT AS count, STRNS.PRICE AS price, " +
                    "(STRNS.PRICE / STRNS.REPORTRATE) AS priceusd, " +
                    "STRNS.TOTAL AS total, (STRNS.TOTAL / STRNS.REPORTRATE) AS totalusd, " +
                    "STRNS.LINEEXP AS definition " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE STRNS " +
                    "WHERE (STRNS.STFICHEREF = 1) AND (STRNS.DETLINE = 0) " +
                    "ORDER BY STRNS.STFICHEREF, STRNS.STFICHELNNO ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsFicheList = new ArrayList<>();

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

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return itemsFicheList;
    }


    /* ---------------------------------------- Достаточность товаров ------------------------------------------------ */

    public List<ProductEnough> getProductEnough(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<ProductEnough> itemsPriceList = null;

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

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsPriceList = new ArrayList<>();

            while (resultSet.next()) {


            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return itemsPriceList;
    }


    /* ---------------------------------------- Прайс лист ------------------------------------------------ */

    public List<ProductPrice> getProductPrice(int firmno, int periodno) {

        utility.CheckCompany(firmno, periodno);
        List<ProductPrice> itemsPriceList = null;

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

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsPriceList = new ArrayList<>();

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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return itemsPriceList;
    }


    /* ---------------------------------------- Движения товаров ------------------------------------------------ */

    public List<ProductTransaction> getProductTransactions(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<ProductTransaction> itemsPriceList = null;

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY " +
                    "SELECT CONVERT(varchar, LGMAIN.DATE_, 23) AS date, STFIC.FICHENO AS ficheno, LGMAIN.TRCODE AS trcode, " +
                    "(SELECT TOP 1 CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = STFIC.CLIENTREF) AS clientcode, " +
                    "(SELECT TOP 1 DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = STFIC.CLIENTREF) AS clientname, " +
                    "LGMAIN.AMOUNT AS count, LGMAIN.PRICE AS price, LGMAIN.PRPRICE AS priceusd, " +
                    "LGMAIN.LINENET AS total, (LGMAIN.AMOUNT * LGMAIN.PRPRICE) AS totalusd " +
                    " FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE LGMAIN, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC " +
                    " LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF   =  INVFC.LOGICALREF) " +
                    " WHERE (LGMAIN.LINETYPE IN (0, 1, 5, 6, 8, 9, 11)) AND (LGMAIN.STFICHEREF <> 0) " +
                    "AND (LGMAIN.STFICHEREF = STFIC.LOGICALREF) " +
                    "AND (((STFIC.GRPCODE = 1)) OR ((STFIC.GRPCODE = 2)) OR ((STFIC.GRPCODE = 3)) OR (STFIC.GRPCODE = 0)) " +
                    "AND (LGMAIN.DATE_ >= '"+ begdate + "') AND (LGMAIN.DATE_ <= '" + enddate + "') " +
                    "AND (LGMAIN.SOURCEINDEX = " + sourceindex + ") " +
                    "ORDER BY " +
                    "LGMAIN.DATE_, LGMAIN.FTIME, LGMAIN.IOCODE, LGMAIN.SOURCEINDEX";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsPriceList = new ArrayList<>();

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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return itemsPriceList;
    }


    /* ---------------------------------------- Движения одного товара ------------------------------------------------ */
    public List<ProductTransaction> getProductTransaction(int firmno, int periodno, String begdate, String enddate, int sourceindex, String code) {

        utility.CheckCompany(firmno, periodno);
        List<ProductTransaction> itemsPriceList = null;

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY " +
                    "SELECT CONVERT(varchar, LGMAIN.DATE_, 23) AS date, STFIC.FICHENO AS ficheno, LGMAIN.TRCODE AS trcode, " +
                    "(SELECT TOP 1 CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = STFIC.CLIENTREF) AS clientcode, " +
                    "(SELECT TOP 1 DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = STFIC.CLIENTREF) AS clientname, " +
                    "LGMAIN.AMOUNT AS count, LGMAIN.PRICE AS price, LGMAIN.PRPRICE AS priceusd, " +
                    "LGMAIN.LINENET AS total, (LGMAIN.AMOUNT * LGMAIN.PRPRICE) AS totalusd " +
                    " FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STLINE LGMAIN, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC " +
                    " LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF   =  INVFC.LOGICALREF) " +
                    " WHERE (LGMAIN.LINETYPE IN (0, 1, 5, 6, 8, 9, 11)) AND (LGMAIN.STFICHEREF <> 0) " +
                    "AND (LGMAIN.STFICHEREF = STFIC.LOGICALREF) " +
                    "AND (((STFIC.GRPCODE = 1)) OR ((STFIC.GRPCODE = 2)) OR ((STFIC.GRPCODE = 3)) OR (STFIC.GRPCODE = 0)) " +
                    "AND (LGMAIN.DATE_ >= '"+ begdate + "') AND (LGMAIN.DATE_ <= '" + enddate + "') " +
                    "AND (LGMAIN.SOURCEINDEX = " + sourceindex + ") " +
                    "AND (LGMAIN.STOCKREF = (SELECT TOP 1 LOGICALREF FROM LG_" + GLOBAL_FIRM_NO + "_ITEMS WHERE CODE = '" + code + "')) " +
                    "ORDER BY " +
                    "LGMAIN.DATE_, LGMAIN.FTIME, LGMAIN.IOCODE, LGMAIN.SOURCEINDEX";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsPriceList = new ArrayList<>();

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


    /* ---------------------------------------- Цены всех товаров ------------------------------------------------ */
    public List<ProductPrices> getProductsPrices(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<ProductPrices> itemsPrices = null;

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY " +
                    " SELECT ITMSC.CODE AS code, ITMSC.NAME AS name, LGMAIN.DEFINITION_ AS definition, " +
                    "LGMAIN.PTYPE AS ptype, LGMAIN.PRICE AS price, " +
                    "(SELECT CURCODE FROM L_CURRENCYLIST WHERE LOGICALREF = LGMAIN.CURRENCY) AS currency, " +
                    "CONVERT(varchar, LGMAIN.BEGDATE, 23) AS begdate, " +
                    "CONVERT(varchar, LGMAIN.ENDDATE, 23) AS enddate, LGMAIN.ACTIVE AS active " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_PRCLIST LGMAIN " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC ON (LGMAIN.CARDREF  =  ITMSC.LOGICALREF) " +
                    "WHERE (LGMAIN.ACTIVE = 0) AND (LGMAIN.MTRLTYPE = 0) " +
                    "AND (LGMAIN.BEGDATE <= '"+ begdate + "') AND (LGMAIN.ENDDATE >= '" + enddate + "') " +
                    "ORDER BY " +
                    "LGMAIN.PTYPE, LGMAIN.CARDREF, LGMAIN.MTRLTYPE, LGMAIN.CLIENTCODE, LGMAIN.LOGICALREF";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsPrices = new ArrayList<>();

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
    public List<ProductPrices> getProductPrices(int firmno, int periodno, String begdate, String enddate, int sourceindex, String code) {

        utility.CheckCompany(firmno, periodno);
        List<ProductPrices> itemsPrices = null;

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "Set DateFormat DMY " +
                    "SELECT ITMSC.CODE AS code, ITMSC.NAME AS name, LGMAIN.DEFINITION_ AS definition, " +
                    "LGMAIN.PTYPE AS ptype, LGMAIN.PRICE AS price, " +
                    "(SELECT CURCODE FROM L_CURRENCYLIST WHERE LOGICALREF = LGMAIN.CURRENCY) AS currency, " +
                    "CONVERT(varchar, LGMAIN.BEGDATE, 23) AS begdate, " +
                    "CONVERT(varchar, LGMAIN.ENDDATE, 23) AS enddate, LGMAIN.ACTIVE AS active " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_PRCLIST LGMAIN " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_ITEMS ITMSC ON (LGMAIN.CARDREF  =  ITMSC.LOGICALREF) " +
                    "WHERE (LGMAIN.ACTIVE = 0) AND ( ITMSC.CODE = '" + code + "') AND (LGMAIN.MTRLTYPE = 0) " +
                    "AND (LGMAIN.BEGDATE <= '"+ begdate + "') AND (LGMAIN.ENDDATE >= '" + enddate + "') " +
                    "ORDER BY " +
                    "LGMAIN.PTYPE, LGMAIN.CARDREF, LGMAIN.MTRLTYPE, LGMAIN.CLIENTCODE, LGMAIN.LOGICALREF";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsPrices = new ArrayList<>();

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
