package com.picsauditing.util;

import com.picsauditing.actions.cron.CronTask;
import com.picsauditing.actions.cron.CronTaskException;
import com.picsauditing.actions.cron.CronTaskResult;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.*;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class EbixLoader implements CronTask {
    @Autowired
    AppPropertyDAO appPropDao;
    @Autowired
    ContractorAuditDAO contractorAuditDAO;
    @Autowired
    ContractorAccountDAO contractorAccountDAO;

    private final Logger logger = LoggerFactory.getLogger(EbixLoader.class);

    public String getDescription() {
        return "Update Huntsman FTP";
    }

    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult();
        String server = appPropDao.find("huntsmansync.ftp.server").getValue();
        String username = appPropDao.find("huntsmansync.ftp.user").getValue();
        String password = appPropDao.find("huntsmansync.ftp.password").getValue();
        String folder = appPropDao.find("huntsmansync.ftp.folder").getValue();

        logger.debug("Server: " + server);
        logger.debug("username: " + username);
        logger.debug("folder: " + folder);

        FTPClient ftp = new FTPClient();

        logger.debug("logging in to server...");

        try {
            ftp.connect(server);
            ftp.enterLocalPassiveMode();

            ftp.login(username, password);

            ftp.changeWorkingDirectory(folder);

            String[] names = ftp.listNames();

            if (names != null) {
                for (String fileName : names) {
                    processFile(ftp, fileName);
                }
            }

            ftp.logout();
        } catch (IOException e) {

        } finally {
            try {
                ftp.disconnect();
            } catch (IOException e) {
                logger.error("Failed to disconnect FTP because - " + e.getMessage());
            }
        }
        return results;
    }

    private void processFile(FTPClient ftp, String fileName) throws IOException {
        logger.debug("Processing file: " + fileName);

        BufferedReader reader = null;

        InputStream retrieveFileStream = ftp.retrieveFileStream(fileName);

        if (retrieveFileStream != null) {

            reader = new BufferedReader(new InputStreamReader(retrieveFileStream));

            String line = null;

            while ((line = reader.readLine()) != null) {

                if (line.length() > 0) {

                    String[] data = line.split(",");
                    logger.debug("Processing data: " + data[0] + "/" + data[1]);

                    int contractorId = 0;
                    try {
                        contractorId = Integer.parseInt(data[0]);
                    } catch (Exception ignoreStrings) {
                        // Sometimes we get ids that are strings like
                        // HC00000629
                    }

                    if (data.length == 2 && contractorId > 0) {

                        // the other field. comes in as a Y/N.
                        AuditStatus status = AuditStatus.Pending;
                        if (data[1].equals("Y"))
                            status = AuditStatus.Complete;

                        try {
                            List<ContractorAudit> audits = null;
                            ContractorAccount conAccount = contractorAccountDAO.find(contractorId);
                            if (conAccount != null) {
                                audits = contractorAuditDAO.findWhere(900, "auditType.id = "
                                        + AuditType.HUNTSMAN_EBIX + " and contractorAccount.id = "
                                        + conAccount.getId(), "");
                            }

                            if (CollectionUtils.isEmpty(audits)) {
                                if (conAccount != null) {
                                    logger.warn("WARNING: Ebix record found for contractor "
                                            + conAccount.getId() + " but no Ebix Compliance audit was found");
                                } else {
                                    logger.warn("WARNING: Ebix record found for contractor MISSING CONTRACTOR ID but no Ebix Compliance audit was found");
                                }
                                continue;
                            }

                            for (ContractorAudit audit : audits) {
                                logger.debug("Setting Ebix audit " + audit.getId() + " for contractor "
                                        + conAccount.getId() + " to " + status.name());
                                for (ContractorAuditOperator cao : audit.getOperators()) {
                                    if (status != cao.getStatus()) {
                                        cao.changeStatus(status, null);
                                        contractorAuditDAO.save(audit);

                                        conAccount.incrementRecalculation();
                                        contractorAccountDAO.save(conAccount);
                                    } else {
                                        logger.debug("No change for Ebix audit " + audit.getId()
                                                + " for contractor " + conAccount.getId() + ", "
                                                + status.name());
                                    }
                                }
                            }

                        } catch (Exception e) {
                            logger.error("ERROR: Error Processing Ebix for contractor " + data[0]);
                            e.printStackTrace();
                        }

                    } else {
                        logger.error("Bad Data Found : " + data);
                    }
                }
            }
        } else {
            logger.error("unable to open connection: " + ftp.getReplyCode() + ":" + ftp.getReplyString());
        }
    }
}
