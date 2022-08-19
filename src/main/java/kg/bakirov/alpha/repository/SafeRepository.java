package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.safes.Safe;
import kg.bakirov.alpha.model.safes.SafeExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_FIRM_NO;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_PERIOD;

@Repository
public class SafeRepository {

    private final Utility utility;
    private final DataSource dataSource;

    @Autowired
    public SafeRepository(Utility utility, DataSource dataSource) {
        this.utility = utility;
        this.dataSource = dataSource;
    }


    /* ------------------------------------------ Список касс ---------------------------------------------------- */
    public List<Safe> getSafes(int firmno, int periodno) {

        utility.CheckCompany(firmno, periodno);
        List<Safe> list = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "LGMAIN.CODE AS code, LGMAIN.NAME AS name, LGMAIN.EXPLAIN AS definition, " +
                    "(SELECT SUM(CASHTOT.DEBIT)- SUM(CASHTOT.CREDIT) " +
                    " FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CSHTOTS CASHTOT " +
                    " WHERE (CASHTOT.CARDREF = LGMAIN.LOGICALREF) AND (CASHTOT.TOTTYPE = 2) AND (CASHTOT.DAY_ > -1)) AS balanceUsd, " +
                    "(SELECT SUM(CASHTOT.DEBIT) - SUM(CASHTOT.CREDIT) " +
                    " FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CSHTOTS CASHTOT " +
                    " WHERE (CASHTOT.CARDREF = LGMAIN.LOGICALREF) AND (CASHTOT.TOTTYPE = 1) AND (CASHTOT.DAY_ > -1)) AS balance " +
                    " FROM LG_" + GLOBAL_FIRM_NO + "_KSCARD LGMAIN WITH(NOLOCK, INDEX = I" + GLOBAL_FIRM_NO + "_KSCARD_I4) " +
                    " WHERE (LGMAIN.ACTIVE = 0) " +
                    "ORDER BY LGMAIN.ACTIVE, LGMAIN.CODE";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Safe safe = new Safe();
                safe.setName(resultSet.getString("name"));
                safe.setCode(resultSet.getString("code"));
                safe.setDefinition(resultSet.getString("definition"));
                safe.setBalance(resultSet.getDouble("balance"));
                safe.setBalanceUsd(resultSet.getDouble("balanceUsd"));
                list.add(safe);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return list;
    }

    /* ------------------------------------------ Выписка всех касс ---------------------------------------------------- */
    public List<SafeExtract> getSafesExtract(int firmno, int periodno, String begdate, String enddate) {

        utility.CheckCompany(firmno, periodno);
        List<SafeExtract> list = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SET DATEFORMAT DMY " +
                    "SELECT " +
                    "CONVERT(varchar, LGMAIN.DATE_, 23) AS date, LGMAIN.FICHENO AS ficheno, LGMAIN.CUSTTITLE AS title, " +
                    "LGMAIN.LINEEXP AS definition, LGMAIN.TRCODE AS trcode, LGMAIN.SIGN AS sign, LGMAIN.TRNET AS net, " +
                    "LGMAIN.REPORTNET AS netusd, LGMAIN.HOUR_ AS hour, LGMAIN.MINUTE_ AS minute " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_KSLINES LGMAIN " +
                    "WHERE (LGMAIN.DATE_ >= ? AND LGMAIN.DATE_ <= ?) " +
                    "ORDER BY LGMAIN.CARDREF, LGMAIN.DATE_, LGMAIN.HOUR_, LGMAIN.MINUTE_, LGMAIN.FICHENO";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SafeExtract safe = new SafeExtract();
                safe.setDate(resultSet.getString("date"));
                safe.setFicheNo(resultSet.getString("ficheno"));
                safe.setTitle(resultSet.getString("title"));
                safe.setDefinition(resultSet.getString("definition"));
                safe.setTrCode(resultSet.getByte("trcode"));
                safe.setSign(resultSet.getByte("sign"));
                safe.setNet(resultSet.getDouble("net"));
                safe.setNetUsd(resultSet.getDouble("netusd"));
                safe.setHour(resultSet.getByte("hour"));
                safe.setMinute(resultSet.getByte("minute"));
                list.add(safe);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return list;
    }

    /* ------------------------------------------ Выписка всех касс ---------------------------------------------------- */
    public List<SafeExtract> getSafeExtract(int firmno, int periodno, String begdate, String enddate, String code) {

        utility.CheckCompany(firmno, periodno);
        List<SafeExtract> list = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SET DATEFORMAT DMY " +
                    "SELECT " +
                    "CONVERT(varchar, LGMAIN.DATE_, 23) AS date, LGMAIN.FICHENO AS ficheno, LGMAIN.CUSTTITLE AS title, " +
                    "LGMAIN.LINEEXP AS definition, LGMAIN.TRCODE AS trcode, LGMAIN.SIGN AS sign, LGMAIN.TRNET AS net, " +
                    "LGMAIN.REPORTNET AS netusd, LGMAIN.HOUR_ AS hour, LGMAIN.MINUTE_ AS minute " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_KSLINES LGMAIN " +
                    "WHERE (LGMAIN.DATE_ >= ? AND LGMAIN.DATE_ <= ?) " +
                    "AND (LGMAIN.CARDREF = (SELECT LOGICALREF FROM LG_001_KSCARD WHERE CODE = ?))" +
                    "ORDER BY LGMAIN.CARDREF, LGMAIN.DATE_, LGMAIN.HOUR_, LGMAIN.MINUTE_, LGMAIN.FICHENO";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setString(3, code);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SafeExtract safe = new SafeExtract();
                safe.setDate(resultSet.getString("date"));
                safe.setFicheNo(resultSet.getString("ficheno"));
                safe.setTitle(resultSet.getString("title"));
                safe.setDefinition(resultSet.getString("definition"));
                safe.setTrCode(resultSet.getByte("trcode"));
                safe.setSign(resultSet.getByte("sign"));
                safe.setNet(resultSet.getDouble("net"));
                safe.setNetUsd(resultSet.getDouble("netusd"));
                safe.setHour(resultSet.getByte("hour"));
                safe.setMinute(resultSet.getByte("minute"));
                list.add(safe);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return list;
    }

}
