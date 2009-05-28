package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.PICS.Utilities;
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
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.LoggingRule;
import com.picsauditing.util.log.PicsLogger;

/**
 * CREATE TABLE contractor_policy_cleanup AS SELECT DISTINCT ca.conID, 0 as done, '' as output <br>
 * FROM pqfdata d JOIN contractor_audit ca ON ca.id = d.auditID <br>
 * WHERE d.questionID IN (2100,2199,2205,2259,2260,2261,2272,2274,2273,2275,2276,2277,2387) <br>
 * ORDER BY ca.conID
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class ContractorPolicyConverter extends PicsActionSupport {

	private int batch = 1;
	static final private int MAX_ERRORS = 5;
	private int conID = 0;

	private ContractorAccountDAO contractorAccountDAO;
	private CertificateDAO certificateDAO;
	private ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	private AuditDataDAO auditDataDAO;

	public ContractorPolicyConverter(ContractorAccountDAO contractorAccountDAO, CertificateDAO certificateDAO,
			ContractorAuditOperatorDAO contractorAuditOperatorDAO, AuditDataDAO auditDataDAO) {
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
			sql.addWhere("done=0");
			sql.setLimit(batch);
			Database db = new Database();
			List<BasicDynaBean> pageData = db.select(sql.toString(), true);
			log("Found " + pageData.size() + " of " + db.getAllRows() + " contractor(s) that require processing");
			PicsLogger.setOutputOn(true);

			int errors = 0;
			for (BasicDynaBean row : pageData) {
				try {
					conID = Integer.parseInt(row.get("conID").toString());
					PicsLogger.start("ContractorPolicyConverter-" + conID);
					process(conID);
					String delete = "UPDATE contractor_policy_cleanup SET done = 1 WHERE conID = " + conID;
					db.executeUpdate(delete);
				} catch (Exception e) {
					log("Exception thrown!!" + e.getMessage());
					log(e.getStackTrace().toString());
					errors++;
				} finally {
					PicsLogger.stop();
					String output = PicsLogger.getOutput();
					String sqlUpdate = "UPDATE contractor_policy_cleanup SET output = '"
							+ Utilities.escapeQuotes(output) + "' WHERE conID = " + conID;
					db.executeUpdate(sqlUpdate);
				}
				if (errors >= MAX_ERRORS)
					break;
			}
		}

		PicsLogger.stop(); // ContractorPolicyConverter
		return SUCCESS;
	}

	/**
	 * For the given contractor, do the following:<br>
	 * 1) Get a list of existing certificates (should be empty)<br>
	 * 2) For each audit<br>
	 * 2a) Organize all the AuditData into Map of Tuples (first parents, then children) 2b) For each Tuple, find any
	 * possible matching CAO 2c) For each CAO, find the best Tuple (reduce to only one) 2d) For each CAO, if one Tuple
	 * exists, then update CAO and map Certificate 3) Remove converted AuditData records
	 * 
	 * @param conID
	 * @throws Exception
	 */
	@Transactional
	private void process(int conID) throws Exception {
		addActionMessage("Processing " + conID);

		ContractorAccount contractor = contractorAccountDAO.find(conID);
		Set<Integer> oldTupleData = new HashSet<Integer>();

		List<Certificate> certificates = certificateDAO.findByConId(conID);
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
				log("  Policy " + conAudit.getId() + " " + conAudit.getAuditType().getAuditName());

				Set<Integer> tuplePolicyTypes = new HashSet<Integer>();
				tuplePolicyTypes.add(13); // GL
				tuplePolicyTypes.add(14); // WC 
				tuplePolicyTypes.add(15); // Auto
				tuplePolicyTypes.add(16); // Excess
				
				if (tuplePolicyTypes.contains(conAudit.getAuditType().getId())) {
					// This policy has tuples, we need to map them over
					// Get a map of all the tuples
					Map<Integer, Tuple> tuples = new TreeMap<Integer, Tuple>();
					for (AuditData data : conAudit.getData()) {
						if (data.getQuestion().isAllowMultipleAnswers()) {
							oldTupleData.add(data.getId());
							tuples.put(data.getId(), new Tuple(data));
						}
					}

					// Now attach all the child questions to the tuple
					for (AuditData data : conAudit.getData()) {
						if (data.getParentAnswer() != null) {
							oldTupleData.add(data.getId());

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
											certificate.setDescription(data.getParentAnswer().getAnswer());
											certificate.setFileType(data.getAnswer());
											certificate.setCreationDate(data.getCreationDate());
											if (data.getAudit().getCreationDate().before(data.getCreationDate()))
												certificate.setCreationDate(data.getAudit().getCreationDate());
											certificate = certificateDAO.save(certificate);

											certificates.add(certificate);
											certFiles.put(hash, certificate);
											tuple.setCertificate(certificate);

											FileUtils.moveFile(file, getFtpDir(), "/files/"
													+ FileUtils.thousandize(certificate.getId()), PICSFileType.certs
													+ "_" + certificate.getId(), certificate.getFileType(), true);
										}
									}
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
							if (!cao.getValid().isTrue()) {
								YesNo tupleValid = YesNo.No;
								if (tuple.isNameMatches() && tuple.isWaiver())
									tupleValid = YesNo.Yes;
								cao.setValid(tupleValid);
							}
							if (cao.getCertificate() == null || tuple.getCertificate() != null)
								cao.setCertificate(tuple.getCertificate());
							contractorAuditOperatorDAO.save(cao);
							log("      saved cao " + cao.getId());
						}
					}
				} else {
					// This is a flat policy type, all we need to do is to iterate over the caos
					for (ContractorAuditOperator cao : conAudit.getOperators()) {
						log("    Updating " + cao.getOperator().getName());

						for (AuditData data : conAudit.getData()) {
							String code = data.getQuestion().getUniqueCode();
							if ("policyFile".equals(code)) {
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
										certificate.setDescription(data.getParentAnswer().getAnswer());
										certificate.setFileType(data.getAnswer());
										certificate.setCreationDate(data.getCreationDate());
										if (data.getAudit().getCreationDate().before(data.getCreationDate()))
											certificate.setCreationDate(data.getAudit().getCreationDate());
										certificate = certificateDAO.save(certificate);

										certificates.add(certificate);
										certFiles.put(hash, certificate);
										cao.setCertificate(certificate);

										FileUtils.moveFile(file, getFtpDir(), "/files/"
												+ FileUtils.thousandize(certificate.getId()), PICSFileType.certs + "_"
												+ certificate.getId(), certificate.getFileType(), true);
									}
								}
							}
						}

						contractorAuditOperatorDAO.save(cao);
						log("      saved cao " + cao.getId());
					}
				}
			}
		} // End: for (ContractorAudit conAudit : contractor.getAudits())

		// Done processing contractor, now remove all of the auditData records
		auditDataDAO.remove(oldTupleData);
	}

	private Tuple getSingleTuple(List<Tuple> tuples) {
		if (tuples == null)
			return null;
		
		int startingSize = tuples.size();

		if (startingSize == 0) {
			log("      No matching tuples");
			return null;
		}
		if (startingSize == 1)
			return tuples.get(0);

		log("      Found " + startingSize + " tuples, reducing...");

		for (int i = 0; i < tuples.size(); i++) {
			if (tuples.get(i).getLegalName().equals("All")) {
				log("        removed All");
				tuples.remove(i);
				return getSingleTuple(tuples);
			}
		}

		for (int i = 0; i < tuples.size(); i++) {
			if (tuples.get(i).getCertificate() == null) {
				log("        removed " + tuples.get(i).getLegalName() + " because it's missing a file attachment");
				tuples.remove(i);
				return getSingleTuple(tuples);
			}
		}

		for (int i = 0; i < tuples.size(); i++) {
			if (tuples.get(i).getLegalName().length() < 5) {
				log("        removed " + tuples.get(i).getLegalName()
						+ " because it has a small name (maybe a corporate account)");
				tuples.remove(i);
				return getSingleTuple(tuples);
			}
		}

		log("        removed " + tuples.get(0).getLegalName()
				+ " because ... well it was entered first so something had to be uploaded again");
		tuples.remove(0);
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

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}

	private class Tuple {
		private int anchorID;
		private String legalName;
		private String nameMatches = null;
		private String otherName = null;
		private boolean waiver = false;
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

		public boolean isWaiver() {
			return waiver;
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
			if (code.equals("aiWaiverSub")) {
				if ("Yes".equals(data.getAnswer()))
					waiver = true;
				if ("NA".equals(data.getAnswer()))
					waiver = true;
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
