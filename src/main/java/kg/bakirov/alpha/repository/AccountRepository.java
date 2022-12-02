package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.accounts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_FIRM_NO;
import static kg.bakirov.alpha.repository.MainRepository.GLOBAL_PERIOD;

@Repository
public class AccountRepository {

    private final Utility utility;
    private final DataSource dataSource;

    @Autowired
    public AccountRepository(Utility utility, DataSource dataSource) {
        this.utility = utility;
        this.dataSource = dataSource;
    }


    /* ------------------------------------------ Акт сверки взаиморасчетов ---------------------------------------------------- */
    public List<Account> getAccounts(int firmNo, int periodNo, String begDate, String endDate, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<Account> accountList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT CLCARD.LOGICALREF AS id, CLCARD.CODE AS kod, CLCARD.DEFINITION_ AS aciklama, CLCARD.ADDR1 AS adres, CLCARD.TELNRS1 AS telno, " +

                    "ROUND(ISNULL((SELECT SUM((1-CTRNS.SIGN)*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (CLNTC.CODE LIKE CLCARD.CODE) AND (CTRNS.DATE_ >= CONVERT(dateTime, ?, 104) AND CTRNS.DATE_ <= CONVERT(dateTime, ?, 104)) " +
                    "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) " +
                    "AND (CTRNS.CANCELLED = 0) AND (CTRNS.MODULENR <> 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0)+ " +
                    "ISNULL((SELECT SUM(((1-CTRNS.SIGN)+(CTRNS.SIGN*INVFC.FROMKASA))*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, " +
                    "LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC " +
                    "WHERE CTRNS.DEPARTMENT IN (0) and CTRNS.BRANCH IN (0) " +
                    "and (CLNTC.CODE LIKE CLCARD.CODE) AND (CTRNS.DATE_ >= CONVERT(dateTime, ?, 104) AND CTRNS.DATE_ <= CONVERT(dateTime, ?, 104)) " +
                    "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) AND (CTRNS.SOURCEFREF = INVFC.LOGICALREF) " +
                    "AND (INVFC.CANCELLED = 0) AND (CTRNS.MODULENR = 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0), 2) borc, " +

                    "ROUND(ISNULL((SELECT SUM(CTRNS.SIGN*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE (CLNTC.CODE LIKE CLCARD.CODE) AND (CTRNS.DATE_ >= CONVERT(dateTime, ?, 104) AND CTRNS.DATE_ <= CONVERT(dateTime, ?, 104)) " +
                    "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) AND (CTRNS.CANCELLED = 0) " +
                    "AND (CTRNS.MODULENR <> 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0) + " +
                    "ISNULL((SELECT SUM((CTRNS.SIGN+((1-CTRNS.SIGN)*INVFC.FROMKASA))*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, " +
                    "LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC " +
                    "WHERE (CLNTC.CODE LIKE CLCARD.CODE) AND (CTRNS.DATE_ >= CONVERT(dateTime, ?, 104) AND CTRNS.DATE_ <= CONVERT(dateTime, ?, 104)) " +
                    "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) " +
                    "AND (CTRNS.SOURCEFREF = INVFC.LOGICALREF) AND (INVFC.CANCELLED = 0) " +
                    "AND (CTRNS.MODULENR = 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0), 2) as alacak  " +

                    "FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD As CLCARD " +
                    "WHERE (CLCARD.CARDTYPE <> 22 AND CLCARD.CARDTYPE <> 4) " +
                    "AND (CLCARD.CODE LIKE ? OR CLCARD.DEFINITION_ LIKE ?) " +
                    "ORDER BY CODE ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setString(3, begDate);
            statement.setString(4, endDate);
            statement.setString(5, begDate);
            statement.setString(6, endDate);
            statement.setString(7, begDate);
            statement.setString(8, endDate);
            statement.setString(9, "%" + filterName + "%");
            statement.setString(10, "%" + filterName + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                accountList.add(
                        new Account(
                                resultSet.getLong("id"),
                                resultSet.getString("kod"),
                                resultSet.getString("aciklama"),
                                resultSet.getString("adres"),
                                resultSet.getString("telno"),
                                resultSet.getDouble("borc"),
                                resultSet.getDouble("alacak"),
                                Math.round(((resultSet.getDouble("borc") -
                                        resultSet.getDouble("alacak")) * 100d) / 100d)
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return accountList;
    }


    /* ------------------------------------------ Отчет по задолжностям ---------------------------------------------------- */
    public List<AccountDebit> getAccountDebit(int firmNo, int periodNo, String begDate, String endDate, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<AccountDebit> customerDebitList = new ArrayList<>();
        List<AccountDebit> accountDebitList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT CLNTC.LOGICALREF AS id,  CTRNS.SIGN, CTRNS.REPORTNET, CTRNS.AMOUNT, " +
                    "CLNTC.CODE, CLNTC.DEFINITION_, CLNTC.TELNRS1, CLNTC.ADDR1, CLNUM.RISKTOTAL, CLNUM.REPRISKTOTAL " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (CTRNS.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLRNUMS CLNUM WITH(NOLOCK) ON (CLNTC.LOGICALREF  =  CLNUM.CLCARDREF) " +
                    "WHERE (CLNTC.LOGICALREF <> 0) AND (CTRNS.CANCELLED = 0) AND (CTRNS.STATUS = 0) AND (((CTRNS.MODULENR=5) " +
                    "AND (CTRNS.TRCODE<>12)) OR (CTRNS.MODULENR<>5)) AND (CLNTC.CARDTYPE <> 22) AND (CLNUM.RISKTOTAL > 0) " +
                    "AND (CTRNS.DATE_ >= CONVERT(dateTime, ?, 104) AND CTRNS.DATE_ <= CONVERT(dateTime, ?, 104))" +
                    "AND (CLNTC.CODE LIKE ? OR CLNTC.DEFINITION_ LIKE ?) " +
                    " ORDER BY CLNTC.CODE";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begDate);
            statement.setString(2, endDate);
            statement.setString(3, "%" + filterName + "%");
            statement.setString(4, "%" + filterName + "%");
            ResultSet resultSet = statement.executeQuery();

            double debit = 0;
            double credit = 0;
            double debitUsd = 0;
            double creditUsd = 0;
            String currentCode = null;

            Map<String, AccountDebit> map = new TreeMap<>();

            while (resultSet.next()) {

                int sign = resultSet.getInt("sign");
                AccountDebit accountDebit = new AccountDebit();

                accountDebit.setCode(resultSet.getString("code"));
                accountDebit.setName(resultSet.getString("DEFINITION_"));
                accountDebit.setAddress(resultSet.getString("ADDR1"));
                accountDebit.setPhone(resultSet.getString("TELNRS1"));
                accountDebit.setBalance(resultSet.getDouble("RISKTOTAL"));
                accountDebit.setBalanceUsd(resultSet.getDouble("REPRISKTOTAL"));

                if (currentCode == null || !currentCode.equals(resultSet.getString("code"))) {
                    if (sign == 0) {
                        debit = resultSet.getDouble("AMOUNT");
                        debitUsd = resultSet.getDouble("REPORTNET");
                        credit = 0;
                        creditUsd = 0;
                    } else {
                        credit = resultSet.getDouble("AMOUNT");
                        creditUsd = resultSet.getDouble("REPORTNET");
                        debit = 0;
                        debitUsd = 0;
                    }
                } else {
                    if (sign == 0) {
                        debit = debit + resultSet.getDouble("AMOUNT");
                        debitUsd = debitUsd + resultSet.getDouble("REPORTNET");
                    } else {
                        credit = credit + resultSet.getDouble("AMOUNT");
                        creditUsd = creditUsd + resultSet.getDouble("REPORTNET");
                    }
                }

                accountDebit.setCredit(credit);
                accountDebit.setCreditUsd(creditUsd);
                accountDebit.setDebit(debit);
                accountDebit.setDebitUsd(debitUsd);
                customerDebitList.add(accountDebit);

                map.put(resultSet.getString("code"), accountDebit);
                currentCode = resultSet.getString("code");
            }
            for (Map.Entry<String, AccountDebit> entry : map.entrySet()) {
                customerDebitList.add(entry.getValue());
            }

            for (Map.Entry<String, AccountDebit> entry : map.entrySet()) {
                accountDebitList.add(entry.getValue());
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return accountDebitList;
    }


    /* ------------------------------------------ Выписка контрагентов ---------------------------------------------------- */
    public List<AccountExtract> getAccountExtract(int firmno, int periodno, String begdate, String enddate) {

        utility.CheckCompany(firmno, periodno);
        List<AccountExtract> accountExtractList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT TOP 300 CLNTC.CODE AS code, CLNTC.DEFINITION_ AS name, " +
                    "CONVERT(varchar, CTRNS.DATE_, 23) AS date, CTRNS.TRCODE, CTRNS.SIGN, " +
                    "ROUND(CTRNS.REPORTNET, 2) AS reportnet, CTRNS.TRANNO, CTRNS.LINEEXP, INVFC.FROMKASA, " +
                    "INVFC.FICHENO, CASE WHEN CTRNS.TRCODE=14 AND CTRNS.MODULENR=5 THEN 0 ELSE 1 END AS TRTEMP, " +
                    "CTRNS.TRCODE as trcode_def FROM " +
                    "LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS WITH(NOLOCK) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) " +
                    "ON (CTRNS.PAYDEFREF = PAYPL.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFICHE CLFIC WITH(NOLOCK) " +
                    "ON (CTRNS.SOURCEFREF = CLFIC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) " +
                    "ON (CTRNS.SOURCEFREF = INVFC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CSROLL RLFIC WITH(NOLOCK) " +
                    "ON (CTRNS.SOURCEFREF = RLFIC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_EMFICHE GLFIC WITH(NOLOCK) " +
                    "ON (CTRNS.ACCFICHEREF = GLFIC.LOGICALREF) LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) " +
                    "ON (INVFC.SALESMANREF = SLSMC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) " +
                    "ON (CTRNS.CLIENTREF = CLNTC.LOGICALREF) " +
                    "WHERE (CTRNS.BRANCH IN (0)) AND (CTRNS.DEPARTMENT IN (0)) " +
                    "AND (CTRNS.DATE_ >= CONVERT(dateTime, ?, 104) AND CTRNS.DATE_ <= CONVERT(dateTime, ?, 104))  " +
                    "ORDER BY CLNTC.CODE, CTRNS.DATE_, TRTEMP ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            ResultSet resultSet = statement.executeQuery();

            double totalSum = 0;
            String currentCode = null;

            while (resultSet.next()) {

                int sign = resultSet.getInt("SIGN");
                AccountExtract extract = new AccountExtract();

                extract.setCode(resultSet.getString("code"));
                extract.setName(resultSet.getString("name"));
                extract.setTrcode(resultSet.getInt("trcode_def"));
                extract.setDescription(resultSet.getString("LINEEXP"));
                extract.setDate(resultSet.getString("date"));

                if (sign == 0) {
                    extract.setDebit(resultSet.getDouble("reportnet"));
                } else {
                    extract.setCredit(resultSet.getDouble("reportnet"));
                }

                if (resultSet.getInt("TRTEMP") != 0) {
                    extract.setFicheno(resultSet.getString("FICHENO"));
                } else {
                    extract.setFicheno(resultSet.getString("TRANNO"));
                }

                if ((currentCode == null) || !currentCode.equals(resultSet.getString("code"))) {
                    extract.setBalanceBefore(0.0);
                    totalSum = resultSet.getDouble("reportnet");
                } else {
                    extract.setBalanceBefore(totalSum);
                    if (sign == 0) {
                        totalSum = totalSum + resultSet.getDouble("reportnet");
                    } else {
                        totalSum = totalSum - resultSet.getDouble("reportnet");
                    }
                }

                if (extract.getDebit() == null) extract.setDebit(0.0);
                if (extract.getCredit() == null) extract.setCredit(0.0);

                extract.setBalance(totalSum);
                accountExtractList.add(extract);


                if (resultSet.getInt("FROMKASA") == 1 && (resultSet.getString("TRCODE").equals("31")
                        || resultSet.getString("TRCODE").equals("32") || resultSet.getString("TRCODE").equals("33")
                        || resultSet.getString("TRCODE").equals("34") || resultSet.getString("TRCODE").equals("36")
                        || resultSet.getString("TRCODE").equals("37") || resultSet.getString("TRCODE").equals("38")
                        || resultSet.getString("TRCODE").equals("39") || resultSet.getString("TRCODE").equals("43")
                        || resultSet.getString("TRCODE").equals("44") || resultSet.getString("TRCODE").equals("56"))) {
                    AccountExtract extractKasa = new AccountExtract();

                    extractKasa.setCode(resultSet.getString("code"));
                    extractKasa.setName(resultSet.getString("name"));
                    extractKasa.setDescription(resultSet.getString("LINEEXP"));
                    extractKasa.setDate(resultSet.getString("date"));
                    extractKasa.setCredit(resultSet.getDouble("reportnet"));
                    extractKasa.setBalanceBefore(totalSum);

                    if (resultSet.getInt("TRTEMP") != 0) {
                        extractKasa.setFicheno(resultSet.getString("FICHENO"));
                    } else {
                        extractKasa.setFicheno(resultSet.getString("TRANNO"));
                    }

                    if ((resultSet.getString("TRCODE").equals("32")
                            || resultSet.getString("TRCODE").equals("33"))) {
                        extractKasa.setTrcode(resultSet.getInt("trcode_def"));
                        totalSum = totalSum + resultSet.getDouble("reportnet");
                    } else {
                        totalSum = totalSum - resultSet.getDouble("reportnet");
                        extractKasa.setTrcode(1);
                    }

                    if (extractKasa.getDebit() == null) extractKasa.setDebit(0.0);
                    if (extractKasa.getCredit() == null) extractKasa.setCredit(0.0);

                    extractKasa.setBalance(totalSum);
                    accountExtractList.add(extractKasa);
                }
                currentCode = resultSet.getString("code");
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return accountExtractList;
    }


    /* ------------------------------------------ Выписка одного контрагента ---------------------------------------------------- */
    public List<AccountExtract> getAccountExtract(int firmno, int periodno, String begdate, String enddate, String code) {

        utility.CheckCompany(firmno, periodno);
        List<AccountExtract> accountExtractList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT CLNTC.CODE AS code, CLNTC.DEFINITION_ AS name, " +
                    "CONVERT(varchar, CTRNS.DATE_, 23) AS date, CTRNS.TRCODE, CTRNS.SIGN, " +
                    "ROUND(CTRNS.REPORTNET, 2) AS reportnet, CTRNS.TRANNO, CTRNS.LINEEXP, INVFC.FROMKASA, " +
                    "INVFC.FICHENO, CASE WHEN CTRNS.TRCODE=14 AND CTRNS.MODULENR=5 THEN 0 ELSE 1 END AS TRTEMP, " +
                    "CTRNS.TRCODE as trcode_def FROM " +
                    "LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS WITH(NOLOCK) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_PAYPLANS PAYPL WITH(NOLOCK) " +
                    "ON (CTRNS.PAYDEFREF = PAYPL.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFICHE CLFIC WITH(NOLOCK) " +
                    "ON (CTRNS.SOURCEFREF = CLFIC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC WITH(NOLOCK) " +
                    "ON (CTRNS.SOURCEFREF = INVFC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CSROLL RLFIC WITH(NOLOCK) " +
                    "ON (CTRNS.SOURCEFREF = RLFIC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_EMFICHE GLFIC WITH(NOLOCK) " +
                    "ON (CTRNS.ACCFICHEREF = GLFIC.LOGICALREF) LEFT OUTER JOIN LG_SLSMAN SLSMC WITH(NOLOCK) " +
                    "ON (INVFC.SALESMANREF = SLSMC.LOGICALREF) LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) " +
                    "ON (CTRNS.CLIENTREF = CLNTC.LOGICALREF) " +
                    "WHERE (CTRNS.BRANCH IN (0)) AND (CTRNS.DEPARTMENT IN (0)) " +
                    "AND (CTRNS.DATE_ >= CONVERT(dateTime, ?, 104) AND CTRNS.DATE_ <= CONVERT(dateTime, ?, 104))  AND (CLNTC.CODE = ?) " +
                    "ORDER BY CLNTC.CODE, CTRNS.DATE_, TRTEMP ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setString(3, code);
            ResultSet resultSet = statement.executeQuery();

            double totalSum = 0;
            String currentCode = null;

            while (resultSet.next()) {

                int sign = resultSet.getInt("SIGN");
                AccountExtract extract = new AccountExtract();

                extract.setCode(resultSet.getString("code"));
                extract.setName(resultSet.getString("name"));
                extract.setTrcode(resultSet.getInt("trcode_def"));
                extract.setDescription(resultSet.getString("LINEEXP"));
                extract.setDate(resultSet.getString("date"));

                if (sign == 0) {
                    extract.setDebit(resultSet.getDouble("reportnet"));
                } else {
                    extract.setCredit(resultSet.getDouble("reportnet"));
                }

                if (resultSet.getInt("TRTEMP") != 0) {
                    extract.setFicheno(resultSet.getString("FICHENO"));
                } else {
                    extract.setFicheno(resultSet.getString("TRANNO"));
                }

                if ((currentCode == null) || !currentCode.equals(resultSet.getString("code"))) {
                    extract.setBalanceBefore(0.0);
                    totalSum = resultSet.getDouble("reportnet");
                } else {
                    extract.setBalanceBefore(totalSum);
                    if (sign == 0) {
                        totalSum = totalSum + resultSet.getDouble("reportnet");
                    } else {
                        totalSum = totalSum - resultSet.getDouble("reportnet");
                    }
                }

                if (extract.getDebit() == null) extract.setDebit(0.0);
                if (extract.getCredit() == null) extract.setCredit(0.0);

                extract.setBalance(totalSum);
                accountExtractList.add(extract);


                if (resultSet.getInt("FROMKASA") == 1 && (resultSet.getString("TRCODE").equals("31")
                        || resultSet.getString("TRCODE").equals("32") || resultSet.getString("TRCODE").equals("33")
                        || resultSet.getString("TRCODE").equals("34") || resultSet.getString("TRCODE").equals("36")
                        || resultSet.getString("TRCODE").equals("37") || resultSet.getString("TRCODE").equals("38")
                        || resultSet.getString("TRCODE").equals("39") || resultSet.getString("TRCODE").equals("43")
                        || resultSet.getString("TRCODE").equals("44") || resultSet.getString("TRCODE").equals("56"))) {
                    AccountExtract extractKasa = new AccountExtract();

                    extractKasa.setCode(resultSet.getString("code"));
                    extractKasa.setName(resultSet.getString("name"));
                    extractKasa.setDescription(resultSet.getString("LINEEXP"));
                    extractKasa.setDate(resultSet.getString("date"));
                    extractKasa.setCredit(resultSet.getDouble("reportnet"));
                    extractKasa.setBalanceBefore(totalSum);

                    if (resultSet.getInt("TRTEMP") != 0) {
                        extractKasa.setFicheno(resultSet.getString("FICHENO"));
                    } else {
                        extractKasa.setFicheno(resultSet.getString("TRANNO"));
                    }

                    if ((resultSet.getString("TRCODE").equals("32")
                            || resultSet.getString("TRCODE").equals("33"))) {
                        extractKasa.setTrcode(resultSet.getInt("trcode_def"));
                        totalSum = totalSum + resultSet.getDouble("reportnet");
                    } else {
                        totalSum = totalSum - resultSet.getDouble("reportnet");
                        extractKasa.setTrcode(1);
                    }

                    if (extractKasa.getDebit() == null) extractKasa.setDebit(0.0);
                    if (extractKasa.getCredit() == null) extractKasa.setCredit(0.0);

                    extractKasa.setBalance(totalSum);
                    accountExtractList.add(extractKasa);
                }
                currentCode = resultSet.getString("code");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return accountExtractList;
    }


    /* ------------------------------------------ Квитанции текущего счета----------------------------------------------- */
    public List<AccountFiches> getAccountFiches(int firmno, int periodno, String begdate, String enddate) {

        utility.CheckCompany(firmno, periodno);
        List<AccountFiches> accountFiches = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "CONVERT(varchar, LGMAIN.DATE_, 23) AS date, LGMAIN.FICHENO AS ficheno, " +
                    "LGMAIN.TRCODE AS trcode, LGMAIN.DEBIT AS debit, LGMAIN.CREDIT AS credit, " +
                    "LGMAIN.REPDEBIT AS repdebit, LGMAIN.REPCREDIT AS repcredit, LGMAIN.GENEXP1 AS definition " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFICHE LGMAIN " +
                    "WHERE (LGMAIN.DATE_ >= CONVERT(dateTime, ?, 104) AND LGMAIN.DATE_ <= CONVERT(dateTime, ?, 104)) " +
                    "ORDER BY LGMAIN.TRCODE, LGMAIN.FICHENO, LGMAIN.LOGICALREF ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                accountFiches.add(
                        new AccountFiches(
                                resultSet.getString("date"),
                                resultSet.getString("ficheno"),
                                resultSet.getInt("trcode"),
                                resultSet.getDouble("debit"),
                                resultSet.getDouble("credit"),
                                resultSet.getDouble("repdebit"),
                                resultSet.getDouble("repcredit"),
                                resultSet.getString("definition")
                        )
                );
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return accountFiches;
    }


    /* ------------------------------------------ Квитанция по номеру ----------------------------------------------- */
    public List<AccountFiche> getAccountFiche(int firmno, int periodno, String begdate, String enddate, int code) {

        utility.CheckCompany(firmno, periodno);
        List<AccountFiche> accountFiches = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT " +
                    "(SELECT CODE FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CTRNS.CLIENTREF) AS code, " +
                    "(SELECT DEFINITION_ FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD WHERE LOGICALREF = CTRNS.CLIENTREF) AS name, " +
                    "CTRNS.LINEEXP AS definition, CTRNS.SIGN AS sign, CTRNS.AMOUNT AS total, CTRNS.REPORTNET AS totalUsd " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS " +
                    "WHERE (CTRNS.MODULENR = 5) AND (CTRNS.DATE_ >= CONVERT(dateTime, ?, 104)) " +
                    "AND (CTRNS.DATE_ <= CONVERT(dateTime, ?, 104)) AND (CTRNS.TRANNO = ?) " +
                    "ORDER BY CTRNS.LINENR ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, begdate);
            statement.setString(2, enddate);
            statement.setInt(3, code);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                AccountFiche fiche = new AccountFiche();
                fiche.setCode(resultSet.getString("code"));
                fiche.setName(resultSet.getString("name"));
                fiche.setDefinition(resultSet.getString("definition"));
                if (resultSet.getInt("sign") == 0) {
                    fiche.setDebit(resultSet.getDouble("total"));
                    fiche.setDebitUsd(resultSet.getDouble("totalUsd"));
                } else {
                    fiche.setCredit(resultSet.getDouble("total"));
                    fiche.setCreditUsd(resultSet.getDouble("totalUsd"));
                }
                accountFiches.add(fiche);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return accountFiches;
    }

    /* ------------------------------------------ Отчет просроченных долгов ----------------------------------------------- */
    public List<AccountAging> getAccountsAging(int firmNo, int periodNo, String begDate, String endDate, String date1, String date2, String date3, String date4, String date5, String filterName) {

        utility.CheckCompany(firmNo, periodNo);
        List<AccountAging> accountAgings = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SELECT LOGICALREF as id,  CODE  as code, DEFINITION_  as name, TELCODES1 + '' + TELNRS1  as phone, " +
                    
                    "Isnull((Select Sum(AMOUNT) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) and (YEAR(DATE_) = CONVERT(dateTime, ?, 104)) and (DATE_<= CONVERT(dateTime, ?, 104)) and (SIGN=0))),0) -" +
                    "Isnull((Select Sum(AMOUNT) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) and (YEAR(DATE_) = CONVERT(dateTime, ?, 104)) and (DATE_ <= CONVERT(dateTime, ?, 104)) and (SIGN=1))),0) as balance, " +

                    "Isnull((Select Sum(AMOUNT) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE as CLF  Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END <= CONVERT(dateTime, ?, 104)) and (SIGN=1))),0) as payment1, " +
                    "Isnull((Select Sum(AMOUNT) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE as CLF  Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END > CONVERT(dateTime, ?, 104)) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END <= CONVERT(dateTime, ?, 104)) and (SIGN=1))),0) as payment2, " +
                    "Isnull((Select Sum(AMOUNT) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE as CLF  Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END > CONVERT(dateTime, ?, 104)) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END <= CONVERT(dateTime, ?, 104)) and (SIGN=1))),0) as payment3, " +
                    "Isnull((Select Sum(AMOUNT) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE as CLF  Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END > CONVERT(dateTime, ?, 104)) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END <= CONVERT(dateTime, ?, 104)) and (SIGN=1))),0) as payment4, " +
                    "Isnull((Select Sum(AMOUNT) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE as CLF  Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END > CONVERT(dateTime, ?, 104)) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END <= CONVERT(dateTime, ?, 104)) and (SIGN=1))),0) as payment5, " +
                    "Isnull((Select Sum(AMOUNT) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE as CLF  Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) and (CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END <= CONVERT(dateTime, ?, 104)) AND (SIGN=1))),0) payment, " +
                    
                    "Isnull((Select CONVERT(varchar, Max(CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END ), 104) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE as CLF  Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) AND (TRCODE IN (1,20,2,21)))), '') as lastFinTrans, " +
                    "Isnull((Select CONVERT(varchar, Max(CASE WHEN CLF.DATE_ >= CLF.DATE_ THEN CLF.DATE_ ELSE CLF.DATE_ END ), 104) From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE as CLF  Where ((1 = 1)  and (CANCELLED=0) AND (STATUS = 0) AND (CLIENTREF = CarKart.LOGICALREF) AND (TRCODE IN (37,38,32,33)))), '') as lastMatTrans " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD AS CarKart " +
                    "Where (CarKart.CardType in (1,2,3)) AND (CarKart.ACTIVE = 0) " +
                    "AND (CarKart.LOGICALREF IN (Select CLIENTREF From LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE " +
                    "WHERE (DATE_ >= CONVERT(dateTime, ?, 104)) AND (DATE_ <= CONVERT(dateTime, ?, 104)))) " +
                    "AND (CODE LIKE ? OR DEFINITION_ LIKE ?) " +
                    "Order by CODE ";

            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, Integer.parseInt(date5.split("\\.")[2]));
            statement.setString(2, date5);
            statement.setInt(3, Integer.parseInt(date5.split("\\.")[2]));
            statement.setString(4, date5);
            statement.setString(5, date1);
            statement.setString(6, date1);
            statement.setString(7, date2);
            statement.setString(8, date2);
            statement.setString(9, date3);
            statement.setString(10, date3);
            statement.setString(11, date4);
            statement.setString(12, date4);
            statement.setString(13, date5);
            statement.setString(14, date5);
            statement.setString(15, begDate);
            statement.setString(16, endDate);
            statement.setString(17, "%" + filterName + "%");
            statement.setString(18, "%" + filterName + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                AccountAging fiche = new AccountAging();
                fiche.setCode(resultSet.getString("code"));
                fiche.setName(resultSet.getString("name"));
                fiche.setPhone(resultSet.getString("phone"));
                fiche.setBalance(resultSet.getDouble("balance"));
                fiche.setPayment1(resultSet.getDouble("payment1"));
                fiche.setPayment2(resultSet.getDouble("payment2"));
                fiche.setPayment3(resultSet.getDouble("payment3"));
                fiche.setPayment4(resultSet.getDouble("payment4"));
                fiche.setPayment5(resultSet.getDouble("payment5"));
                fiche.setPayment(resultSet.getDouble("payment"));
                fiche.setLastFinTrans(resultSet.getString("lastFinTrans"));
                fiche.setLastMatTrans(resultSet.getString("lastMatTrans"));
                accountAgings.add(fiche);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return accountAgings;
    }

}