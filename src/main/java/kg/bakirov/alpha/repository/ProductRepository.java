package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.products.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    private final MainRepository mainRepository;

    @Autowired
    public ProductRepository(Utility utility, MainRepository mainRepository) {
        this.utility = utility;
        this.mainRepository = mainRepository;
    }

    /* ------------------------------------------ Остаток товаров ---------------------------------------------------- */

    public List<Product> getProducts(int firmno, int periodno, String begdate, String enddate, int sourceindex) {

        utility.CheckCompany(firmno, periodno);
        List<Product> itemsList = null;

        try (Connection connection = mainRepository.getConnection()) {

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

        try (Connection connection = mainRepository.getConnection()) {

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

    public List<ProductFiche> getProductFiche(int firmno, int periodno, String begdate, String enddate, int sourceindex ) {

        utility.CheckCompany(firmno, periodno);
        List<ProductFiche> itemsFicheList = null;

        try (Connection connection = mainRepository.getConnection()) {

            String sqlQuery = "Set DateFormat DMY SELECT STFIC.TRCODE as item_trcode, " +
                    "STFIC.FICHENO AS item_ficheno, CONVERT(varchar, STFIC.DATE_, 23) AS item_date, CLNTC.CODE as item_clientcode, " +
                    "CLNTC.DEFINITION_ as item_clientname, ROUND(CASE STFIC.IOCODE WHEN 1 THEN STFIC.GROSSTOTAL ELSE -STFIC.GROSSTOTAL END, 0) AS item_gross, " +
                    "ROUND(STFIC.TOTALDISCOUNTS, 0) AS item_discounts, ROUND(STFIC.TOTALEXPENSES, 0) AS item_expenses, " +
                    "ROUND(CASE STFIC.IOCODE WHEN 1 THEN STFIC.NETTOTAL ELSE -STFIC.NETTOTAL END, 0) as item_net, STFIC.IOCODE item_type " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_STFICHE STFIC WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) ON (STFIC.INVOICEREF = INVFC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (STFIC.CLIENTREF = CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) ON (STFIC.SALESMANREF = SLSMC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) ON (STFIC.PAYDEFREF = PAYPL.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT sWSp WITH(NOLOCK) ON (STFIC.SOURCEWSREF = sWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_WORKSTAT dWSp WITH(NOLOCK) ON (STFIC.DESTWSREF = dWSp.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_DISTORD DISTORD WITH(NOLOCK) ON (STFIC.DISTORDERREF = DISTORD.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PROJECT PROJECT WITH(NOLOCK) ON (STFIC.PROJECTREF  =  PROJECT.LOGICALREF) " +
                    "WHERE (STFIC.CANCELLED = 0) AND ((STFIC.STATUS IN (0,1)) OR (STFIC.TRCODE IN (11,12,13,14,25,26,50,51))) AND (STFIC.SOURCEINDEX = " + sourceindex + ") " +
                    "AND ((STFIC.DATE_>=" + "'" + begdate + "') AND (STFIC.DATE_<=" + "'" + enddate + "')) " +
                    "ORDER BY STFIC.DATE_, STFIC.FTIME, STFIC.TRCODE, STFIC.FICHENO ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            itemsFicheList = new ArrayList<>();

            while (resultSet.next()) {

                itemsFicheList.add(
                        new ProductFiche(
                                resultSet.getLong("item_trcode"),
                                resultSet.getString("item_ficheno"),
                                resultSet.getString("item_date"),
                                resultSet.getString("item_clientcode"),
                                resultSet.getString("item_clientname"),
                                resultSet.getDouble("item_gross"),
                                resultSet.getDouble("item_discounts"),
                                resultSet.getDouble("item_expenses"),
                                resultSet.getDouble("item_net"),
                                resultSet.getString("item_type")
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

        try (Connection connection = mainRepository.getConnection()) {

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

        try (Connection connection = mainRepository.getConnection()) {

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

}
