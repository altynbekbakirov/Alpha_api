package kg.bakirov.alpha.repository;

import kg.bakirov.alpha.model.company.Firm;
import kg.bakirov.alpha.model.company.Period;
import kg.bakirov.alpha.model.company.WareHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MainRepository {

    public static String GLOBAL_FIRM_NO;
    public static String GLOBAL_PERIOD;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MainRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /* ---------------------------------------- Список компаний ------------------------------------------------ */
    public List<Firm> getFirmList() {
        return jdbcTemplate.query("SELECT NR, NAME, TITLE FROM L_CAPIFIRM WITH(NOLOCK) ORDER BY NR", new BeanPropertyRowMapper<>(Firm.class));
    }


    /* ---------------------------------------- Финансовые периоды ------------------------------------------------ */
    public List<Period> getPeriodList(int firmNo) {
        return jdbcTemplate.query("SELECT NR, FIRMNR, CONVERT(VARCHAR, BEGDATE, 104) as BEGDATE, CONVERT(VARCHAR, ENDDATE, 104) AS ENDDATE, ACTIVE " +
                                    "FROM L_CAPIPERIOD WITH(NOLOCK) WHERE (FIRMNR = " + firmNo + ") ORDER BY NR",
                new BeanPropertyRowMapper<>(Period.class));
    }


    /* ---------------------------------------- Склады ------------------------------------------------ */
    public List<WareHouse> getWareHouseList(int firmNo) {
        return jdbcTemplate.query("SELECT LOGICALREF, NR ,FIRMNR, NAME FROM L_CAPIWHOUSE  WHERE (FIRMNR = " + firmNo + ") ORDER BY NR",
                new BeanPropertyRowMapper<>(WareHouse.class));
    }


    /* ---------------------------------------- Очистка сетевого файла ------------------------------------------------ */
    public void deleteNetwork(int firmno) {
        String firm = String.format("%03d", firmno);
        jdbcTemplate.update("DELETE FROM L_NET DELETE FROM L_GOUSERS DELETE FROM LG_NET_" + firm);
    }


}
