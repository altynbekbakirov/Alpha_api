package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.safes.Safe;
import kg.bakirov.alpha.model.safes.SafeExtract;
import kg.bakirov.alpha.model.safes.SafeResume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Safe> getSafes(int firmNo, int periodNo) {

        utility.CheckCompany(firmNo, periodNo);
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
    public List<SafeExtract> getSafesExtract(int firmNo, int periodNo, String begDate, String endDate, String filterName, String operationType) {

        utility.CheckCompany(firmNo, periodNo);
        List<SafeExtract> list = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "CONVERT(varchar, LGMAIN.DATE_, 23) AS date, KSCARD.NAME AS safe_, LGMAIN.FICHENO AS ficheno, LGMAIN.CUSTTITLE AS title, " +
                    "LGMAIN.LINEEXP AS definition, LGMAIN.TRCODE AS trcode, LGMAIN.SIGN AS sign, LGMAIN.TRNET AS net, " +
                    "LGMAIN.REPORTNET AS netusd, LGMAIN.HOUR_ AS hour, LGMAIN.MINUTE_ AS minute " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_KSLINES LGMAIN " +
                    "INNER JOIN LG_" + GLOBAL_FIRM_NO + "_KSCARD KSCARD ON LGMAIN.CARDREF = KSCARD.LOGICALREF " +
                    "WHERE (LGMAIN.DATE_ >= CONVERT(dateTime, ?, 104) AND LGMAIN.DATE_ <= CONVERT(dateTime, ?, 104)) ";
            if (!filterName.isEmpty()) {
                sqlQuery += "AND ((LGMAIN.CUSTTITLE LIKE '%" + filterName + "%') OR (LGMAIN.LINEEXP LIKE '%" + filterName + "%')) ";
            }

            sqlQuery += "AND (LGMAIN.TRCODE IN (" + operationType + ")) " +
                    "ORDER BY LGMAIN.DATE_, LGMAIN.HOUR_, LGMAIN.MINUTE_, LGMAIN.FICHENO";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SafeExtract safe = new SafeExtract();
                safe.setDate(resultSet.getString("date"));
                safe.setSafe_(resultSet.getString("safe_"));
                safe.setFicheNo(resultSet.getString("ficheno"));
                safe.setTitle(resultSet.getString("title"));
                safe.setDefinition(resultSet.getString("definition"));
                safe.setTrCode(resultSet.getByte("trcode"));
                if (resultSet.getInt("sign") == 0) {
                    safe.setCollection(resultSet.getDouble("net"));
                    safe.setCollectionUsd(resultSet.getDouble("netUsd"));
                } else {
                    safe.setPayment(resultSet.getDouble("net"));
                    safe.setPaymentUsd(resultSet.getDouble("netUsd"));
                }
                safe.setHour(resultSet.getByte("hour"));
                safe.setMinute(resultSet.getByte("minute"));
                list.add(safe);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return list;
    }


    /* ------------------------------------------ Выписка одной кассы ---------------------------------------------------- */
    public List<SafeExtract> getSafeExtract(int firmNo, int periodNo, String begDate, String endDate, String code, String filterName, String operationType) {

        utility.CheckCompany(firmNo, periodNo);
        List<SafeExtract> list = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "CONVERT(varchar, LGMAIN.DATE_, 23) AS date, KSCARD.NAME AS safe_, LGMAIN.FICHENO AS ficheno, LGMAIN.CUSTTITLE AS title, " +
                    "LGMAIN.LINEEXP AS definition, LGMAIN.TRCODE AS trcode, LGMAIN.SIGN AS sign, LGMAIN.TRNET AS net, " +
                    "LGMAIN.REPORTNET AS netusd, LGMAIN.HOUR_ AS hour, LGMAIN.MINUTE_ AS minute " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_KSLINES LGMAIN " +
                    "INNER JOIN LG_" + GLOBAL_FIRM_NO + "_KSCARD KSCARD ON LGMAIN.CARDREF = KSCARD.LOGICALREF " +
                    "WHERE (LGMAIN.DATE_ >= CONVERT(dateTime, ?, 104) AND LGMAIN.DATE_ <= CONVERT(dateTime, ?, 104)) " +
                    "AND KSCARD.CODE = ? AND (LGMAIN.CUSTTITLE LIKE ? OR LGMAIN.LINEEXP LIKE ?) " +
                    "AND (LGMAIN.TRCODE IN (" + operationType + ")) " +
                    "ORDER BY LGMAIN.CARDREF, LGMAIN.DATE_, LGMAIN.HOUR_, LGMAIN.MINUTE_, LGMAIN.FICHENO";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setString(3, code);
            statement.setString(4, "%" + filterName + "%");
            statement.setString(5, "%" + filterName + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SafeExtract safe = new SafeExtract();
                safe.setDate(resultSet.getString("date"));
                safe.setSafe_(resultSet.getString("safe_"));
                safe.setFicheNo(resultSet.getString("ficheno"));
                safe.setTitle(resultSet.getString("title"));
                safe.setDefinition(resultSet.getString("definition"));
                safe.setTrCode(resultSet.getByte("trcode"));
                if (resultSet.getInt("sign") == 0) {
                    safe.setCollection(resultSet.getDouble("net"));
                    safe.setCollectionUsd(resultSet.getDouble("netUsd"));
                } else {
                    safe.setPayment(resultSet.getDouble("net"));
                    safe.setPaymentUsd(resultSet.getDouble("netUsd"));
                }
                safe.setHour(resultSet.getByte("hour"));
                safe.setMinute(resultSet.getByte("minute"));
                list.add(safe);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return list;
    }


    /* ------------------------------------------ Реэюме кассового счета ---------------------------------------------------- */
    public List<SafeResume> getSafeResume(int firmNo, int periodNo, String begDate, String endDate, String code) {

        utility.CheckCompany(firmNo, periodNo);
        List<SafeResume> list = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT CASHTOT.TOTTYPE AS currency, CASHTOT.DAY_  AS day, " +
                    "SUM(CASHTOT.DEBIT) AS debit, SUM(CASHTOT.CREDIT) AS credit, MONTH(CASHTOT.DATE_) AS month " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CSHTOTS CASHTOT " +
                    "WHERE   (CASHTOT.TOTTYPE IN (1, 2)) AND (CASHTOT.DAY_ >= 0) " +
                    "AND (CASHTOT.CARDREF = (SELECT LOGICALREF FROM LG_" + GLOBAL_FIRM_NO + "_KSCARD WHERE CODE = ?)) " +
                    "AND ((CASHTOT.DATE_ >= CONVERT(dateTime, ?, 104)) AND (CASHTOT.DATE_ <= CONVERT(dateTime, ?, 104))) " +
                    "GROUP BY CASHTOT.TOTTYPE, MONTH(CASHTOT.DATE_), CASHTOT.DAY_";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, code);
            statement.setString(2, begDate);
            statement.setString(3, endDate);
            ResultSet resultSet = statement.executeQuery();

            Map<Integer, SafeResume> map = new HashMap<>();
            double total = 0;
            double totalUsd = 0;

            while (resultSet.next()) {
                int currentMonth = resultSet.getInt("day") == 0 ? 0 : resultSet.getInt("month");

                // Com
                if (resultSet.getInt("currency") == 1) {
                    total += resultSet.getDouble("debit") - resultSet.getDouble("credit");
                    if (map.containsKey(currentMonth)) {
                        map.get(currentMonth).setDebit(map.get(currentMonth).getDebit() + resultSet.getDouble("debit"));
                        map.get(currentMonth).setCredit(map.get(currentMonth).getCredit() + resultSet.getDouble("credit"));
                        map.get(currentMonth).setTotal(total);
                    } else {
                        SafeResume resume = new SafeResume();
                        resume.setMonth(currentMonth);
                        resume.setDebit(resultSet.getDouble("debit"));
                        resume.setCredit(resultSet.getDouble("credit"));
                        resume.setTotal(total);
                        map.put(currentMonth, resume);
                    }
                }
                // Usd
                else {
                    totalUsd += resultSet.getDouble("debit") - resultSet.getDouble("credit");
                    if (map.containsKey(currentMonth)) {
                        map.get(currentMonth).setDebitUsd(map.get(currentMonth).getDebitUsd() + resultSet.getDouble("debit"));
                        map.get(currentMonth).setCreditUsd(map.get(currentMonth).getCreditUsd() + resultSet.getDouble("credit"));
                        map.get(currentMonth).setTotalUsd(totalUsd);
                    } else {
                        SafeResume resume = new SafeResume();
                        resume.setMonth(currentMonth);
                        resume.setDebitUsd(resultSet.getDouble("debit"));
                        resume.setCreditUsd(resultSet.getDouble("credit"));
                        resume.setTotalUsd(totalUsd);
                        map.put(currentMonth, resume);
                    }
                }
            }
            list.addAll(map.values());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return list;
    }

}
