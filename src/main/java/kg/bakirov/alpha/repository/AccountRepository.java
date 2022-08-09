package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.helper.Utility;
import kg.bakirov.alpha.model.accounts.Account;
import kg.bakirov.alpha.model.accounts.AccountDebit;
import kg.bakirov.alpha.model.accounts.AccountExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public List<Account> getAccounts(int firmno, int periodno, String begdate, String enddate) {

        utility.CheckCompany(firmno, periodno);
        List<Account> accountList = null;

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SET DATEFORMAT DMY " +
                    "SELECT CLCARD.LOGICALREF AS id, CLCARD.CODE AS kod, CLCARD.DEFINITION_ AS aciklama, CLCARD.ADDR1 AS adres, CLCARD.TELNRS1 AS telno, " +

                    "ROUND(ISNULL((SELECT SUM((1-CTRNS.SIGN)*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE CTRNS.DEPARTMENT IN (0) and CTRNS.BRANCH IN (0) " +
                    "AND (CLNTC.CODE LIKE CLCARD.CODE) AND (CTRNS.DATE_ >= '" + begdate + "' AND CTRNS.DATE_ <= '" + enddate + "') " +
                    "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) " +
                    "AND (CTRNS.CANCELLED = 0) AND (CTRNS.MODULENR <> 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0)+ " +
                    "ISNULL((SELECT SUM(((1-CTRNS.SIGN)+(CTRNS.SIGN*INVFC.FROMKASA))*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, " +
                    "LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC " +
                    "WHERE CTRNS.DEPARTMENT IN (0) and CTRNS.BRANCH IN (0) " +
                    "and (CLNTC.CODE LIKE CLCARD.CODE) AND (CTRNS.DATE_ >= '" + begdate + "' AND CTRNS.DATE_ <= '" + enddate + "') " +
                    "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) AND (CTRNS.SOURCEFREF = INVFC.LOGICALREF) " +
                    "AND (INVFC.CANCELLED = 0) AND (CTRNS.MODULENR = 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0), 2) borc, " +

                    "ROUND(ISNULL((SELECT SUM(CTRNS.SIGN*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC " +
                    "WHERE CTRNS.DEPARTMENT IN (0) and CTRNS.BRANCH IN (0) " +
                    "and (CLNTC.CODE LIKE CLCARD.CODE) AND (CTRNS.DATE_ >= '" + begdate + "' AND CTRNS.DATE_ <= '" + enddate + "') " +
                    "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) AND (CTRNS.CANCELLED = 0) " +
                    "AND (CTRNS.MODULENR <> 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0) + " +
                    "ISNULL((SELECT SUM((CTRNS.SIGN+((1-CTRNS.SIGN)*INVFC.FROMKASA))*CTRNS.REPORTNET) " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS, " +
                    "LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC, LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_INVOICE INVFC " +
                    "WHERE CTRNS.DEPARTMENT IN (0) and CTRNS.BRANCH IN (0) " +
                    "and (CLNTC.CODE LIKE CLCARD.CODE) AND (CTRNS.DATE_ >= '" + begdate + "' AND CTRNS.DATE_ <= '" + enddate + "') " +
                    "AND (CTRNS.CLIENTREF = CLCARD.LOGICALREF) " +
                    "AND (CTRNS.SOURCEFREF = INVFC.LOGICALREF) AND (INVFC.CANCELLED = 0) " +
                    "AND (CTRNS.MODULENR = 4) AND (NOT (CTRNS.TRCODE IN (12,35,40)))), 0), 2) as alacak  " +

                    "FROM LG_" + GLOBAL_FIRM_NO + "_CLCARD As CLCARD " +
                    "WHERE (CLCARD.CARDTYPE <> 22 AND CLCARD.CARDTYPE <> 4) ORDER BY CODE ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            accountList = new ArrayList<>();

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

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return accountList;
    }


    /* ------------------------------------------ Отчет по задолжностям ---------------------------------------------------- */

    public List<AccountDebit> getAccountDebit(int firmno, int periodno, String begdate, String enddate) {

        utility.CheckCompany(firmno, periodno);
        List<AccountDebit> customerDebitList = new ArrayList<>();
        List<AccountDebit> accountDebitList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SET DATEFORMAT DMY SELECT CLNTC.LOGICALREF AS id,  CTRNS.SIGN, CTRNS.REPORTNET, CTRNS.AMOUNT, " +
                    "CLNTC.CODE, CLNTC.DEFINITION_, CLNTC.TELNRS1, CLNTC.ADDR1, CLNUM.RISKTOTAL, CLNUM.REPRISKTOTAL " +
                    "FROM LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLFLINE CTRNS WITH(NOLOCK) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_CLCARD CLNTC WITH(NOLOCK) ON (CTRNS.CLIENTREF  =  CLNTC.LOGICALREF) " +
                    "LEFT OUTER JOIN LG_" + GLOBAL_FIRM_NO + "_" + GLOBAL_PERIOD + "_CLRNUMS CLNUM WITH(NOLOCK) ON (CLNTC.LOGICALREF  =  CLNUM.CLCARDREF) " +
                    "WHERE (CLNTC.LOGICALREF <> 0) AND (CTRNS.CANCELLED = 0) AND (CTRNS.STATUS = 0) AND (((CTRNS.MODULENR=5) " +
                    "AND (CTRNS.TRCODE<>12)) OR (CTRNS.MODULENR<>5)) AND (CLNTC.CARDTYPE <> 22) AND (CLNUM.RISKTOTAL > 0) " +
                    "AND (CTRNS.DATE_ >= '" + begdate + "' AND CTRNS.DATE_ <= '" + enddate + "') ORDER BY CLNTC.CODE";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            double debit = 0;
            double credit = 0;
            double debitUsd = 0;
            double creditUsd = 0;
            String currentCode = null;

            Map<String, AccountDebit> map = new TreeMap<>();

            while (resultSet.next()) {

                int sign = resultSet.getInt("sign");
                AccountDebit accountDebit = new AccountDebit();

                accountDebit.setId(resultSet.getLong("id"));
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

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return accountDebitList;
    }


    /* ------------------------------------------ Выписка контрагентов ---------------------------------------------------- */

    public List<AccountExtract> getAccountExtract(int firmno, int periodno, String begdate, String enddate) {

        utility.CheckCompany(firmno, periodno);
        List<AccountExtract> accountExtractList = null;

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SET DATEFORMAT DMY SELECT CLNTC.CODE AS code, CLNTC.DEFINITION_ AS name, " +
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
                    "AND (CTRNS.DATE_ >= '" + begdate + "' AND CTRNS.DATE_ <= '" + enddate + "')  " +
                    "ORDER BY CLNTC.CODE, CTRNS.DATE_, TRTEMP ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            accountExtractList = new ArrayList<>();
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



    /* ------------------------------------------ Выписка одного контрагента ---------------------------------------------------- */

    public List<AccountExtract> getAccountExtract(int firmno, int periodno, String begdate, String enddate, int account) {

        utility.CheckCompany(firmno, periodno);
        List<AccountExtract> accountExtractList = null;

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = "SET DATEFORMAT DMY SELECT CLNTC.CODE AS code, CLNTC.DEFINITION_ AS name, " +
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
                    "AND (CTRNS.DATE_ >= '" + begdate + "' AND CTRNS.DATE_ <= '" + enddate + "')  " +
                    "AND (CLNTC.LOGICALREF = " + account + ") " +
                    "ORDER BY CLNTC.CODE, CTRNS.DATE_, TRTEMP ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            accountExtractList = new ArrayList<>();
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
}