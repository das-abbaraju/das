package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AccountName;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.LoggingRule;
import com.picsauditing.util.log.PicsLogger;

/**
 * 
 CREATE TABLE contractor_policy_cleanup AS SELECT DISTINCT ca.conID FROM pqfdata d JOIN contractor_audit ca ON ca.id =
 * d.auditID WHERE d.questionID IN (2100,2199,2205,2259,2260,2261,2272,2274,2273,2275,2276,2277,2387) ORDER BY ca.conID
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class ContractorPolicyConverter extends PicsActionSupport {

	private int conID = 0;

	private ContractorAccountDAO contractorAccountDAO;
	private CertificateDAO certificateDAO;
	private ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	private AuditDataDAO auditDataDAO;

	public ContractorPolicyConverter(ContractorAccountDAO contractorAccountDAO, CertificateDAO certificateDAO,
			ContractorAuditOperatorDAO contractorAuditOperatorDAO ,AuditDataDAO auditDataDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.certificateDAO = certificateDAO;
		this.auditDataDAO = auditDataDAO;
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
	}

	public String execute() throws Exception {
		LoggingRule rule = new LoggingRule("ContractorPolicyConverter", true);
		PicsLogger.getRules().add(rule);

		PicsLogger.start("ContractorPolicyConverter");

		if (conID > 0) {
			process(conID);
		} else {
			SelectSQL sql = new SelectSQL("contractor_policy_cleanup");
			sql.setSQL_CALC_FOUND_ROWS(true);
			sql.addOrderBy("conID");
			sql.setLimit(10);
			Database db = new Database();
			List<BasicDynaBean> pageData = db.select(sql.toString(), true);
			log("Found " + pageData.size() + " of " + db.getAllRows() + " contractor(s) that require processing");
			int errors = 0;
			for (BasicDynaBean row : pageData) {
				try {
					int conID = Integer.parseInt(row.get("conID").toString());
					PicsLogger.start("ContractorPolicyConverter-" + conID);
					process(conID);
				} catch (Exception e) {
					log("Exception thrown!!" + e.getMessage());
					log(e.getStackTrace().toString());
					errors++;
				} finally {
					PicsLogger.stop();
				}
				if (errors > 0)
					break;
			}
		}

		PicsLogger.stop(); // ContractorPolicyConverter
		return SUCCESS;
	}

	@Transactional
	private void process(int conID) throws Exception {
		addActionMessage("Processing " + conID);

		ContractorAccount contractor = contractorAccountDAO.find(conID);

		List<Certificate> certificates = certificateDAO.findByConId(conID, permissions);
		Map<String, Certificate> certFiles = new HashMap<String, Certificate>();
		for (Certificate c : certificates) {
			File file = getFile(PICSFileType.certs, c.getId());
			if (file != null) {
				String hash = FileUtils.getFileMD5(file);
				certFiles.put(hash, c);
				log("  found " + FileUtils.size(file) + " certificate file hash " + hash);
			}
		}

		for (ContractorAudit conAudit : contractor.getAudits()) {
			if (conAudit.getAuditType().getClassType().isPolicy()) {
				log("  Policy " + conAudit.getId());

				// Get a map of all the tuples
				Map<Integer, Tuple> tuples = new TreeMap<Integer, Tuple>();
				for (AuditData data : conAudit.getData()) {
					if (data.getQuestion().isAllowMultipleAnswers()) {
						tuples.put(data.getId(), new Tuple(data));
					}
				}

				// Now attach all the child questions to the tuple
				for (AuditData data : conAudit.getData()) {
					if (data.getParentAnswer() != null) {
						Tuple tuple = tuples.get(data.getParentAnswer().getId());
						if (tuple == null)
							log("    WARNING!! Failed to find the parent for dataID=" + data.getId());
						else {
							if ("policyFile".equals(data.getQuestion().getUniqueCode())) {
								File file = getFile(PICSFileType.data, data.getId());
								if (file != null) {
									String hash = FileUtils.getFileMD5(file);
									if (certFiles.containsKey(hash)) {
										log("  policyFile " + data.getId() + " is a duplicate of certificate "
												+ certFiles.get(hash).getId());
									} else {
										log("  policyFile doesn't exists, adding new Certificate");
										Certificate certificate = new Certificate();
										certificate.setAuditColumns(new User(User.SYSTEM));
										certificate.setContractor(contractor);
										certificate.setDescription("");
										certificate.setFileType(data.getAnswer());
										certificate.setCreationDate(data.getCreationDate());
										certificate = certificateDAO.save(certificate);
										
										certificates.add(certificate);
										certFiles.put(hash, certificate);
										tuple.setCertificate(certificate);

										FileUtils.moveFile(file, getFtpDir(), "/files/"
												+ FileUtils.thousandize(certificate.getId()), PICSFileType.certs + "_"
												+ certificate.getId(), certificate.getFileType(), true);
									}
								}
								// TODO don't remove them yet
								// auditDataDAO.remove(data.getId());
							} else {
								tuple.setChildData(data);
							}
						}
					}
				}

				// Create a map of caoTuples so we can attach all the tuples to the appropriate caos
				Map<ContractorAuditOperator, List<Tuple>> caoTuples = new HashMap<ContractorAuditOperator, List<Tuple>>();
				for (ContractorAuditOperator cao : conAudit.getOperators()) {
					caoTuples.put(cao, new ArrayList<Tuple>());
				}
				log("    This policy has " + tuples.size() + " tuple(s) and " + caoTuples.size() + " cao(s)");

				// For each tuple, find the appropriate cao and attach it
				for (Tuple tuple : tuples.values()) {
					String tupleName = tuple.getLegalName().trim();
					for (ContractorAuditOperator cao : conAudit.getOperators()) {
						for (AccountName legalName : cao.getOperator().getNames()) {
							if (tupleName.equalsIgnoreCase("All")) {
								if (caoTuples.get(cao).size() == 0)
									caoTuples.get(cao).add(tuple);
								tuple.setCao(cao); // Doesn't matter who it's assigned to
							} else if (tupleName.equalsIgnoreCase(legalName.getName().trim())) {
								if (tuple.getCao() != null)
									log("    WARNING!! This tuple was already assigned: " + tuple.toString());
								caoTuples.get(cao).add(tuple);
								tuple.setCao(cao);
							}
						}
					}
					if (tuple.getCao() == null)
						log("    WARNING!! This tuple was NEVER assigned: " + tuple.toString());
				}

				for (ContractorAuditOperator cao : conAudit.getOperators()) {
					log("    Updating " + cao.getOperator().getName());
					Tuple tuple = getSingleTuple(caoTuples.get(cao));
					if (tuple != null) {
						cao.setAiName(tuple.getOtherName());
						cao.setAiNameValid(tuple.isNameMatches());
						cao.setCertificate(tuple.getCertificate());
						contractorAuditOperatorDAO.save(cao);
						log("      saved cao " + cao.getId());
					}
				}
			}
		}
	}

	private Tuple getSingleTuple(List<Tuple> tuples) {
		int startingSize = tuples.size();
		
		if (startingSize == 0) {
			log("      No matching tuples");
			return null;
		}
		if (startingSize == 1)
			return tuples.get(0);
		
		log("      Found " + startingSize + " tuples, reducing...");
		
		for(int i=0; i< tuples.size(); i++) {
			if (tuples.get(i).getLegalName().equals("All")) {
				tuples.remove(i);
				log("        removed All");
				return getSingleTuple(tuples);
			}
		}
		
		for(int i=0; i< tuples.size(); i++) {
			if (tuples.get(i).getCertificate() == null) {
				tuples.remove(i);
				log("        removed " + tuples.get(i).getLegalName() + " because it's missing a file attachment");
				return getSingleTuple(tuples);
			}
		}
		
		if (startingSize == tuples.size()) {
			// This avoids infinite loops
			log("      Couldn't reduce tuple size, returning first one");
		}
		
		return getSingleTuple(tuples);
	}
	
	private File getFile(PICSFileType fileType, int fileID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(fileID));
		File[] files = FileUtils.getSimilarFiles(dir, fileType + "_" + fileID);
		if (files.length == 0)
			return null;
		if (files.length > 1) {
			System.out.println("WARNING: found two files for " + fileType + "_" + fileID);
			addActionError("found two files for " + fileType + "_" + fileID);
		}
		return files[0];
	}

	private void log(String message) {
		PicsLogger.log(message);
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	private class Tuple {
		private int anchorID;
		private String legalName;
		private String nameMatches = null;
		private String otherName = null;
		private Certificate certificate = null;
		private ContractorAuditOperator cao;

		public Tuple(AuditData anchor) {
			anchorID = anchor.getId();
			legalName = anchor.getAnswer();
		}

		public int getAnchorID() {
			return anchorID;
		}

		public String getLegalName() {
			return legalName;
		}

		public boolean isNameMatches() {
			return "Yes".equals(nameMatches);
		}

		public String getOtherName() {
			return otherName;
		}

		public Certificate getCertificate() {
			return certificate;
		}

		public void setCertificate(Certificate certificate) {
			this.certificate = certificate;
		}

		public void setChildData(AuditData data) {
			String code = data.getQuestion().getUniqueCode();
			if (Strings.isEmpty(code))
				return;
			if (code.equals("aiMatches")) {
				nameMatches = data.getAnswer();
			}
			if (code.equals("aiOther")) {
				this.otherName = data.getAnswer();
			}
		}

		public ContractorAuditOperator getCao() {
			return cao;
		}

		public void setCao(ContractorAuditOperator cao) {
			this.cao = cao;
		}

		public String toString() {
			StringBuffer output = new StringBuffer();
			output.append(anchorID).append(" - ").append(legalName);
			output.append(": matches=").append(nameMatches);
			output.append(", other=").append(otherName);
			if (certificate != null)
				output.append(", certificate=").append(certificate.getId());
			return output.toString();
		}
	}
}
