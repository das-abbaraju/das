package com.picsauditing.PICS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.cron.CronMetricsAggregator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagCriteriaContractorDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.log.PicsLogger;

public class ContractorFlagETL {
	private FlagCriteriaDAO flagCriteriaDao;
	private ContractorAccountDAO contractorAccountDao;
	private FlagCriteriaContractorDAO flagCriteriaContractorDao;
	private AuditDataDAO auditDataDao;
	private List<FlagCriteria> flagCriteriaList = null;
	private CronMetricsAggregator cronMetrics;
	private HashSet<Integer> criteriaQuestionSet = new HashSet<Integer>();

	public ContractorFlagETL(FlagCriteriaDAO flagCriteriaDao, ContractorAccountDAO contractorAccountDao,
			FlagCriteriaContractorDAO flagCriteriaContractorDao, AuditDataDAO auditDataDao) {
		this.flagCriteriaDao = flagCriteriaDao;
		this.contractorAccountDao = contractorAccountDao;
		this.flagCriteriaContractorDao = flagCriteriaContractorDao;
		this.auditDataDao = auditDataDao;
	}

	public void execute(List<Integer> contractorList) {
		try {
			// TODO: Check to ensure function returns proper list
			flagCriteriaList = flagCriteriaDao.getDistinctOperatorFlagCriteria();

			// Get AuditQuestion ids that are used
			for (FlagCriteria fc : flagCriteriaList) {
				if (fc.getQuestion() != null)
					criteriaQuestionSet.add(fc.getQuestion().getId());
			}
		} catch (Throwable t) {
			t.printStackTrace();
			StringBuffer body = new StringBuffer();

			body.append("There was an error creating flag criteria data");
			body.append("\n\n");

			body.append(t.getMessage());
			body.append("\n");

			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			body.append(sw.toString());

			sendMail(body.toString());
		}

		int errorCount = 0;

		for (Integer conID : contractorList) {
			try {
				long conStart = System.currentTimeMillis();
				PicsLogger.start("ContractorFlagETL.calculate", "for : " + conID);
				run(conID);

				if (cronMetrics != null) {
					cronMetrics.addContractor(conID, System.currentTimeMillis() - conStart);
				}
			} catch (Throwable t) {
				t.printStackTrace();
				StringBuffer body = new StringBuffer();

				body.append("There was an error calculating flags for contractor ");
				body.append(conID.toString());
				body.append("\n\n");

				body.append(t.getMessage());
				body.append("\n");

				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				body.append(sw.toString());

				sendMail(body.toString());

				if (++errorCount > 3) {
					break;
				}
			}
		}
		PicsLogger.stop();
	}

	@Transactional
	// TODO: ADDDDD DATAAAA CLEAANNNNUUPPPPPP!!!
	// See FlagCalculator2 line 263-302
	private void run(int conID) {
		List<FlagCriteriaContractor> changes = new ArrayList<FlagCriteriaContractor>();
		// List of contractor data to save into DB

		ContractorAccount contractor = contractorAccountDao.find(conID);
		Map<Integer, AuditData> answerMap = auditDataDao.findAnswersByContractor(conID, criteriaQuestionSet);

		for (FlagCriteria flagCriteria : flagCriteriaList) {

			if (flagCriteria.getAuditType() != null) {
				// Checking Audit Type
				if (flagCriteria.getAuditType().getClassType().isPolicy()) {
					// Contractors are evaluated by their CAO,
					// so it's operator specific and we can't calculate exact data here
					// Just put in a place holder row
					changes.add(new FlagCriteriaContractor(contractor, flagCriteria, "true"));
				} else if (flagCriteria.getAuditType().isAnnualAddendum()) {
					int count = 0;

					// Checking for at least 3 active annual updates
					for (ContractorAudit ca : contractor.getAudits()) {
						if (ca.getAuditType().equals(flagCriteria.getAuditType())) {
							if (ca.getAuditStatus().isActiveResubmittedExempt())
								count++;
						}
					}

					changes.add(new FlagCriteriaContractor(contractor, flagCriteria, (count >= 3 ? "true" : "false")));
				} else {
					Boolean hasProperStatus = false;
					for (ContractorAudit ca : contractor.getAudits()) {
						if (ca.getAuditType().equals(flagCriteria.getAuditType())) {
							if (ca.getAuditStatus().isActiveResubmittedExempt())
								hasProperStatus = true;
							else if (!flagCriteria.isValidationRequired() && ca.getAuditStatus().isSubmitted())
								hasProperStatus = true;
						}
					}
					changes.add(new FlagCriteriaContractor(contractor, flagCriteria, hasProperStatus.toString()));
				}
			}

			if (flagCriteria.getQuestion() != null) {
				// find answer in answermap if exists to related question
				// can be null
				final AuditData auditData = answerMap.get(flagCriteria.getQuestion().getId());
				if (auditData != null) {
					final FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor,
							flagCriteria, "");
					if (flagCriteria.getDataType().equals("boolean")) {
						// TODO parse to boolean
						flagCriteriaContractor.setAnswer(auditData.getAnswer());
					} else if (flagCriteria.getDataType().equals("number")) {
						// TODO parse to number
						Float f = Float.parseFloat(auditData.getAnswer());
						flagCriteriaContractor.setAnswer(f.toString());
					} else if (flagCriteria.getDataType().equals("date")) {
						// TODO parse to date
						flagCriteriaContractor.setAnswer(auditData.getAnswer());
					} else if (flagCriteria.getDataType().equals("string")) {
						flagCriteriaContractor.setAnswer(auditData.getAnswer());
					}
					//auditData.isVerified()
					changes.add(flagCriteriaContractor);
				}
			}

			if (flagCriteria.getOshaType() != null) {
				// expecting to contain 4 or less of the most current audits
				final Map<String, OshaAudit> auditsOfThisSHAType = contractor.getOshas()
						.get(flagCriteria.getOshaType());
				// TODO: VERIFY ORDER IS PRESERVED <-----------------------*******
				List<OshaAudit> auditYears = new ArrayList<OshaAudit>(auditsOfThisSHAType.values());
				if (auditYears.size() > 0) {
					if (auditYears.size() > 3) {
						// Check which one to pop off
						auditYears.remove(3);
					}

					Float answer = 0f;
					boolean verified = false;
					int yearIndex = -1;

					switch (flagCriteria.getMultiYearScope()) {
					case ThreeYearAverage:

						break;
					case ThreeYearsAgo:
						if (auditYears.size() >= 3) {

							yearIndex = 2;
						}
					case TwoYearsAgo:
						if (auditYears.size() >= 2) {

							yearIndex = 1;
						}
					case LastYearOnly:
						yearIndex = 0;
						answer = auditYears.get(yearIndex).getRates(flagCriteria.getOshaRateType());
						break;
					}
					// TODO: MAKE SURE TO ADD ENTRY FOR WHETHER OR NOT VALIDATION IS REQUIRED!!!!!!!
					final FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor, flagCriteria, answer.toString());
					// flagCriteriaContractor.set = verified;
					changes.add(flagCriteriaContractor);
				}
			}
		}

		flagCriteriaContractorDao.deleteEntriesForContractor(conID);

	}

	private void sendMail(String message) {
		try {
			EmailQueue email = new EmailQueue();
			email.setToAddresses("errors@picsauditing.com");
			email.setPriority(30);
			email.setSubject("Error in ContractorFlagETL");
			email.setBody(message);
			email.setCreationDate(new Date());
			EmailSender sender = new EmailSender();
			sender.sendNow(email);
		} catch (Exception notMuchWeCanDoButLogIt) {
			System.out.println("Error sending email");
			System.out.println(notMuchWeCanDoButLogIt);
			notMuchWeCanDoButLogIt.printStackTrace();
		}
	}
}
