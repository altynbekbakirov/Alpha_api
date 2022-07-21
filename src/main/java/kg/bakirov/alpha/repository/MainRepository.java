package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.DbConnection;
import kg.bakirov.alpha.model.company.Firm;
import kg.bakirov.alpha.model.company.Period;
import kg.bakirov.alpha.model.company.WareHouse;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MainRepository {

    public static String GLOBAL_FIRM_NO;
    public static String GLOBAL_PERIOD;


    /* ---------------------------------------- Подключение к базе данных ------------------------------------------------ */

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=TIGERDB", "sa", "123456789");
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return connection;
    }


    /* ---------------------------------------- Список компаний ------------------------------------------------ */
    public List<Firm> getFirmList() {

        List<Firm> firmList = new ArrayList<>();

        try(Connection connection = getConnection()) {
            String sqlQuery = "SELECT NR, NAME, TITLE FROM L_CAPIFIRM WITH(NOLOCK) ORDER BY NR";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                firmList.add(
                        new Firm(resultSet.getInt("nr"),
                                resultSet.getString("name"),
                                resultSet.getString("title")
                        )
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return firmList;
    }


    /* ---------------------------------------- Финансовые периоды ------------------------------------------------ */
    public List<Period> getPeriodList(int firmNo) {
        List<Period> periodList = new ArrayList<>();

        try(Connection connection = getConnection()) {
            String sqlQuery = "SELECT NR, FIRMNR, CONVERT(VARCHAR, BEGDATE, 104) as BEGDATE, CONVERT(VARCHAR, ENDDATE, 104) AS ENDDATE, ACTIVE " +
                    "FROM L_CAPIPERIOD WITH(NOLOCK) WHERE (FIRMNR = " + firmNo + ") ORDER BY NR";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                periodList.add(
                        new Period(
                                resultSet.getInt("nr"),
                                resultSet.getInt("FIRMNR"),
                                resultSet.getString("BEGDATE"),
                                resultSet.getString("ENDDATE"),
                                resultSet.getInt("ACTIVE")
                        )
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return periodList;
    }


    /* ---------------------------------------- Склады ------------------------------------------------ */
    public List<WareHouse> getWareHouseList(int firmNo) {
        List<WareHouse> wareHouseListList = new ArrayList<>();

        try(Connection connection = getConnection()) {
            String sqlQuery = "SELECT LOGICALREF, NR ,FIRMNR, NAME FROM L_CAPIWHOUSE  WHERE (FIRMNR = " + firmNo + ") ORDER BY NR";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                wareHouseListList.add(
                        new WareHouse(
                                resultSet.getInt("LOGICALREF"),
                                resultSet.getInt("NR"),
                                resultSet.getInt("FIRMNR"),
                                resultSet.getString("NAME")
                        )
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return wareHouseListList;
    }


    /* ---------------------------------------- Очистка сетевого файла ------------------------------------------------ */
    public void deleteNetwork(int firmno) {

        String firm = String.format("%03d", firmno);

        try(Connection connection = DbConnection.getInstance()) {

            String sqlQuery = "DELETE FROM L_NET DELETE FROM L_GOUSERS DELETE FROM LG_NET_" + firm;
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
