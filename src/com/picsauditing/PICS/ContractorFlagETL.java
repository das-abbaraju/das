package com.picsauditing.PICS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.cron.CronMetricsAggregator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagCriteriaContractorDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.log.PicsLogger;

public class ContractorFlagETL {
	private FlagCriteriaDAO flagCriteriaDao;
	private ContractorAccountDAO contractorAccountDao;
	private FlagCriteriaContractorDAO flagCriteriaContractorDao;
	private AuditDataDAO auditDataDao;
	private List<FlagCriteria> distinctFlagCriteriaList;
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
		// try catch send email on Exception
		try {
			// TODO: Check to ensure function returns proper list
			distinctFlagCriteriaList = flagCriteriaDao.getDistinctOperatorFlagCriteria();

			// Iterating over list to get questions
			for (FlagCriteria fc : distinctFlagCriteriaList) {
				if (fc.getQuestion() != null)
					criteriaQuestionSet.add(fc.getQuestion().getId());
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

					try {
						EmailQueue email = new EmailQueue();
						email.setToAddresses("errors@picsauditing.com");
						email.setPriority(30);
						email.setSubject("Flag calculation error for conID = " + conID);
						email.setBody(body.toString());
						email.setContractorAccount(new ContractorAccount(conID));
						email.setCreationDate(new Date());
						EmailSender.send(email);
					} catch (Exception notMuchWeCanDoButLogIt) {
						System.out.println("**********************************");
						System.out.println("Error calculating flags AND unable to send email");
						System.out.println("**********************************");

						System.out.println(notMuchWeCanDoButLogIt);
						notMuchWeCanDoButLogIt.printStackTrace();
					}

					if (++errorCount == 10) {
						break;
					}
				} finally {
					PicsLogger.stop();
				}
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

			try {
				EmailQueue email = new EmailQueue();
				email.setToAddresses("errors@picsauditing.com");
				email.setPriority(30);
				email.setSubject("Flag Criteria data creation error");
				email.setBody(body.toString());
				email.setCreationDate(new Date());
				EmailSender.send(email);
			} catch (Exception notMuchWeCanDoButLogIt) {
				System.out.println("**********************************");
				System.out.println("Error creating flag critera data AND unable to send email");
				System.out.println("**********************************");

				System.out.println(notMuchWeCanDoButLogIt);
				notMuchWeCanDoButLogIt.printStackTrace();
			}
		}
	}

	@Transactional
	private void run(int conID) {
		// TODO: ADDDDD DATAAAA CLEAANNNNUUPPPPPP!!!
		// <----------------------------------------------------------------*********

		// get select FlagCriteria where id in (select id from
		// FlagCriteriaOperator)
		ContractorAccount contractor = contractorAccountDao.find(conID);
		ArrayList<FlagCriteriaContractor> changes = new ArrayList<FlagCriteriaContractor>();

		// Get question answers
		Map<Integer, AuditData> answerMap = auditDataDao.findAnswersByContractor(conID, criteriaQuestionSet);
		
		for (FlagCriteria flagCriteria : distinctFlagCriteriaList) {
		
		HashMap<AuditType, ArrayList<ContractorAudit>> contractorAuditTypeMap = new HashMap<AuditType, ArrayList<ContractorAudit>>();
		
		// Creating contractor audit type buckets
		for (ContractorAudit ca : contractor.getAudits()) {
			// Mapping AuditTypes to lists of FlagCriteria, so that extra
			// audits which do not apply to the criteria will not have to
			// be iterated over.
			ArrayList<ContractorAudit> auditTypeList = contractorAuditTypeMap.get(ca.getAuditType());
			if (auditTypeList != null)
				auditTypeList.add(ca);
			else {
				auditTypeList = new ArrayList<ContractorAudit>();
				auditTypeList.add(ca);
				contractorAuditTypeMap.put(ca.getAuditType(), auditTypeList);
			}
		}
				
			// Checking Audit Type
			if (flagCriteria.getAuditType().getClassType().isPolicy()) { // Is policy audit?
				Boolean hasCurrentPolicy = false;

				if (contractorAuditTypeMap.get(flagCriteria.getAuditType()).size() > 0)
					hasCurrentPolicy = true;

				changes.add(new FlagCriteriaContractor(contractor,flagCriteria,hasCurrentPolicy.toString()));
			} else if (flagCriteria.getAuditType().isAnnualAddendum()) { // Is annual update audit?
				Boolean hasThreeAnnualAddendums = false;
				int numberOfCurrentAddendums = 0;

				// Checking for at least 3 active annual updates
				for (ContractorAudit ca : contractorAuditTypeMap.get(flagCriteria.getAuditType()))
					if (ca.getAuditStatus() == AuditStatus.Active)
						numberOfCurrentAddendums++;

				hasThreeAnnualAddendums = numberOfCurrentAddendums >= 3;

				changes.add(new FlagCriteriaContractor(contractor,flagCriteria,hasThreeAnnualAddendums.toString()));
			} else if (flagCriteria.getAuditType() != null) {
				// Return best audit type
				ContractorAudit bestContractorAudit = getBestContractorAuditFromList(contractorAuditTypeMap.get(flagCriteria.getAuditType()));

				// iterate over all entries in flagCriteriaMap for this
				// auditType
				Boolean hasProperStatus = false;

					if (!flagCriteria.isValidationRequired()
							&& (bestContractorAudit.getAuditStatus() == AuditStatus.Active
									|| bestContractorAudit.getAuditStatus() == AuditStatus.Resubmitted || bestContractorAudit
									.getAuditStatus() == AuditStatus.Submitted)) {
						hasProperStatus = true;
					} else if (flagCriteria.isValidationRequired()
							&& (bestContractorAudit.getAuditStatus() == AuditStatus.Active || bestContractorAudit
									.getAuditStatus() == AuditStatus.Resubmitted)) {
						hasProperStatus = true;
					}

					changes.add(new FlagCriteriaContractor(contractor,flagCriteria,hasProperStatus.toString()));
			} else if(flagCriteria.getQuestion() != null) { // Is a question
				// find answer in answermap if exists to related question
				String answer = answerMap.get(flagCriteria.getQuestion().getId()).getAnswer(); // can be null
				changes.add(new FlagCriteriaContractor(contractor,flagCriteria,answer));
			} else if(flagCriteria.getOshaType() != null){
				// expecting to contain 4 or less of the most current audits
				// TODO: VERIFY ORDER IS PRESERVED <-----------------------*******
				ArrayList<OshaAudit> auditYears = new ArrayList<OshaAudit>(contractor.getOshas().get(flagCriteria.getOshaType()).values());
				
				String answer = null;
				
				switch(flagCriteria.getMultiYearScope()){
					case ThreeYearAverage:
						if(auditYears.size() >= 3){
						
						}
						break;
					case ThreeYearsAgo:
						if(auditYears.size() >= 3){
							
						}						
						break;
					case TwoYearsAgo:
						if(auditYears.size() >= 2){
						
						}
						break;
					case LastYearOnly: 
						if(auditYears.size() >= 1){
						
						}
						break;
				}
				// TODO: MAKE SURE TO ADD ENTRY FOR WHETHER OR NOT VALIDATION IS REQUIRED!!!!!!!
				
			} else {
				//
			}
			
			// do EMR check

			// populate data by evaluating each operator criteria
			// query pqf data (check)
			// query osha data
			// query audit status (check)
			// query caos (check)

			// clean up data

			// add it to database
		}
		// See FlagCalculator2 line 263-302

		// List of contractor data to save into DB
		List<FlagCriteriaContractor> data;

		// populate data by evaluating each operator criteria
		// query pqf data
		// query osha data
		// query audit status
		// query caos

		// handle 3 year averages
		// Maybe we should use the ContractorAccount.getOsha and
		// ContractorAccount.getEmr

		// delete from flag_criteria_con where conID = ?
		// Deleting all questions in table having to do with current contractor
		flagCriteriaContractorDao.deleteEntriesForContractor(conID);

		// insert "data"
	}

	// TODO: Trevor Check <--------------------
	private ContractorAudit getBestContractorAuditFromList(ArrayList<ContractorAudit> auditList) {
		ContractorAudit bestAudit = null;
		int bestScore = 0;

		for (ContractorAudit ca : auditList) {
			int currentScore = scoreAudit(ca);
			if (currentScore >= bestScore) {
				bestScore = currentScore;
				bestAudit = ca;
			}
		}

		return bestAudit;
	}

	private int scoreAudit(ContractorAudit audit) {
		if (audit.getAuditStatus().isExpired())
			return 0;

		int score = 0;

		if (audit.getAuditStatus().isSubmitted())
			score = 100;
		else if (audit.getAuditStatus() == AuditStatus.Active)
			score = 90;
		else if (audit.getAuditStatus() == AuditStatus.Resubmitted)
			score = 80;

		return score;
	}
}
