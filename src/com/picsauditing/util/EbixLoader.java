package com.picsauditing.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.util.log.PicsLogger;

public class EbixLoader {

	private AppPropertyDAO appPropDao;
	private ContractorAuditDAO contractorAuditDAO;
	private ContractorAccountDAO contractorAccountDAO;

	public EbixLoader(AppPropertyDAO appPropDao, ContractorAuditDAO contractorAuditDAO,
			ContractorAccountDAO contractorAccountDAO) {
		this.appPropDao = appPropDao;
		this.contractorAuditDAO = contractorAuditDAO;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public void load() throws SocketException, IOException {
		String server = appPropDao.find("huntsmansync.ftp.server").getValue();
		String username = appPropDao.find("huntsmansync.ftp.user").getValue();
		String password = appPropDao.find("huntsmansync.ftp.password").getValue();
		String folder = appPropDao.find("huntsmansync.ftp.folder").getValue();

		PicsLogger.log("Server: " + server);
		PicsLogger.log("username: " + username);
		PicsLogger.log("folder: " + folder);

		// there may be other files in that folder. we can use this to filter
		// down to the ones we want.
		// String pattern =
		// appPropDao.find("huntsmansync.ftp.filePattern").getValue();

		FTPClient ftp = new FTPClient();

		PicsLogger.log("logging in to server...");

		ftp.connect(server);
		ftp.enterLocalPassiveMode();

		ftp.login(username, password);

		ftp.changeWorkingDirectory(folder);

		String[] names = ftp.listNames();

		if (names != null) {

			for (String fileName : names) {

				PicsLogger.log("Processing file: " + fileName);

				BufferedReader reader = null;

				InputStream retrieveFileStream = ftp.retrieveFileStream(fileName);

				if (retrieveFileStream != null) {

					reader = new BufferedReader(new InputStreamReader(retrieveFileStream));

					String line = null;

					while ((line = reader.readLine()) != null) {

						if (line.length() > 0) {

							String[] data = line.split(",");
							PicsLogger.log("Processing data: " + data[0] + "/" + data[1]);

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
									ContractorAccount conAccount = contractorAccountDAO.find(contractorId);
									List<ContractorAudit> audits = contractorAuditDAO.findWhere(900, "auditType.id = "
											+ AuditType.HUNTSMAN_EBIX + " and contractorAccount.id = "
											+ conAccount.getId(), "");

									if (audits == null || audits.size() == 0) {
										PicsLogger.log("WARNING: Ebix record found for contractor "
												+ conAccount.getId() + " but no Ebix Compliance audit was found");
										continue;
									}

									for (ContractorAudit audit : audits) {
										PicsLogger.log("Setting Ebix audit " + audit.getId() + " for contractor "
												+ conAccount.getId() + " to " + status.name());
										for (ContractorAuditOperator cao : audit.getOperators()) {
											if (status != cao.getStatus()) {
												cao.setStatus(status);
												contractorAuditDAO.save(audit);

												conAccount.incrementRecalculation();
												contractorAccountDAO.save(conAccount);
											} else {
												PicsLogger.log("No change for Ebix audit " + audit.getId()
														+ " for contractor " + conAccount.getId() + ", "
														+ status.name());
											}
										}
									}

								} catch (Exception e) {
									PicsLogger.log("ERROR: Error Processing Ebix for contractor " + data[0]);
									e.printStackTrace();
								}

							} else {
								PicsLogger.log("Bad Data Found : " + data);
							}
						}
					}
				} else {
					PicsLogger.log("unable to open connection: " + ftp.getReplyCode() + ":" + ftp.getReplyString());
				}
			}
		}

		ftp.logout();
		ftp.disconnect();

	}
}
