package com.picsauditing.model.i18n;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.picsauditing.i18n.model.database.TranslationUsage;
import com.picsauditing.i18n.service.TranslateRestClient;
import com.picsauditing.toggle.FeatureToggle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import scala.Some;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TranslationUsageLogSynchronizer {
    private Logger logger = LoggerFactory.getLogger(TranslationUsageLogSynchronizer.class);
    private static final int LIMIT = 50;
    private static final String selectTranslationsToSynchronize = "SELECT * FROM translation_usage WHERE synchronizedBatch = ?";
    private static final String claimTranslationsToSynchronize =
            "UPDATE translation_usage t " +
                    "SET t.synchronizedBatch = ?, t.synchronizedDate = ? " +
                    "WHERE t.synchronizedBatch IS NULL " + // " OR t.synchronizedDate < DATE_SUB(@now, INTERVAL 1 DAY)) " +
                    "ORDER BY t.lastUsed ASC " +
                    "LIMIT " + LIMIT;

    @Autowired
    private TranslateRestClient translateRestClient;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private FeatureToggle featureToggleChecker;

    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelay = 60000)
    public void executeCron() throws Exception {
        if (featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)) {
            List<TranslationUsage> usageToSynchronize = findAndLockKeyUsageToProcess();
            if (usageToSynchronize.size() > 0) {
                if (!translateRestClient.updateTranslationLog(usageToSynchronize)) {
                    logger.error("Unable to synchronize translation key usage");
                };
            }
        }
    }

    public List<TranslationUsage> findAndLockKeyUsageToProcess() {
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        String guid = guid();
        jdbcTemplate.update(claimTranslationsToSynchronize, guid, new Date());
        return jdbcTemplate.query(selectTranslationsToSynchronize, new Object[]{guid}, new TranslationUsageRowMapper());
    }

    private String guid() {
        EthernetAddress ethernetAddress = EthernetAddress.fromInterface();
        TimeBasedGenerator uuid_gen = Generators.timeBasedGenerator(ethernetAddress);
        UUID uuid = uuid_gen.generate();
        String guid = uuid.toString();
        return guid;
    }

    private JdbcTemplate jdbcTemplate() {
        if (jdbcTemplate == null)
            jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

    public class TranslationUsageRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TranslationUsage (
                new Some(rs.getInt("id")),
                rs.getString("msgKey"),
                rs.getString("msgLocale"),
                rs.getString("pageName"),
                new Some(rs.getString("pageOrder")),
                rs.getString("environment"),
                new Some(rs.getDate("firstUsed")),
                new Some(rs.getDate("lastUsed")),
                new Some(rs.getString("synchronizedBatch")),
                new Some(rs.getDate("synchronizedDate"))
            );
        }
    }

}