package com.picsauditing.actions;



@SuppressWarnings( "serial" )
public class MigrateCertificates extends PicsActionSupport {

//	protected CertificateDAO certDao = null;
//	protected ContractorAccountDAO contractorDAO = null;
//	protected ContractorAuditDAO auditDAO = null;
//	protected AuditTypeDAO auditTypeDAO = null;
//	protected ContractorAuditOperatorDAO conAuditOperatorDAO = null;
//	protected AuditDataDAO auditDataDAO = null;
//	protected AuditBuilder auditBuilder = null;
//	protected AuditPercentCalculator auditPercentCalculator = null;
//
//	private AuditCategoryDataDAO auditCategoryDataDAO;
//	private AuditCategoryDAO auditCategoryDAO;
//	
//	protected static Map<String, String> auditTypeMapper = null;
//	protected static Map<String, List<Integer>> liabilityLimitQuestionIds = null;
//	protected static Map<String, List<Integer>> expirationDateQuestionIds = null;
//	protected static Map<String, List<Integer>> fileExtensionQuestionIds = null;
//	protected static Map<String, List<Integer>> nonNamedFileExtensionQuestionIds = null;
//	protected static Map<String, List<Integer>> additionalInsuredsQuestionIds = null;
//	protected static Map<String, List<Integer>> waiverQuestionIds = null;
//
//	static {
//		auditTypeMapper = new HashMap<String, String>() {
//			{
//				put("General Liability", "General Liability");
//				put("Worker's Comp", "Workers Comp");
//				put("Automobile", "Automobile Liability");
//				put("Excess/Umbrella", "Excess/Umbrella Liability");
//				put("Professional Liability", "Professional Liability");
//				put("Pollution Liability", "Pollution Liability");
//				put("Contractor Liability", "Contractor Liability");
//				put("Employer's Liability", "Employer''s Liability");
//				put("E&O", "E&O Liability");
//			}
//		};
//
//		liabilityLimitQuestionIds = new HashMap<String, List<Integer>>() {
//			{
//				put("General Liability", Arrays.asList(2074));
//				put("Workers Comp", Arrays.asList(2149));
//				put("Automobile Liability", Arrays.asList(2155));
//				put("Excess/Umbrella Liability", Arrays.asList(2165));
//				put("Professional Liability", Arrays.asList(2167));
//				put("Pollution Liability", Arrays.asList(2173));
//				put("Contractor Liability", Arrays.asList(2179));
//				put("Employer's Liability", Arrays.asList(2185, 2186, 2187));
//				put("E&O Liability", Arrays.asList(2191));
//			}
//		};
//
//		expirationDateQuestionIds = new HashMap<String, List<Integer>>() {
//			{
//				put("General Liability", Arrays.asList(2082));
//				put("Workers Comp", Arrays.asList(2105));
//				put("Automobile Liability", Arrays.asList(2111));
//				put("Excess/Umbrella Liability", Arrays.asList(2117));
//				put("Professional Liability", Arrays.asList(2123));
//				put("Pollution Liability", Arrays.asList(2129));
//				put("Contractor Liability", Arrays.asList(2135));
//				put("Employer's Liability", Arrays.asList(2141));
//				put("E&O Liability", Arrays.asList(2147));
//			}
//		};
//		
//		fileExtensionQuestionIds = new HashMap<String, List<Integer>>() {
//			{
//				put("General Liability", Arrays.asList(2100));
//				put("Workers Comp", Arrays.asList(2202));
//				put("Automobile Liability", Arrays.asList(2199));
//				put("Excess/Umbrella Liability", Arrays.asList(2205));
//				put("Professional Liability", Arrays.asList(2208));
//				put("Pollution Liability", Arrays.asList(2211));
//				put("Contractor Liability", Arrays.asList(2214));
//				put("Employer's Liability", Arrays.asList(2217));
//				put("E&O Liability", Arrays.asList(2220));
//			}
//		};
//		
//		nonNamedFileExtensionQuestionIds = new HashMap<String, List<Integer>>() {
//			{
//				put("General Liability", Arrays.asList(2073));
//				put("Workers Comp", Arrays.asList(2101));
//				put("Automobile Liability", Arrays.asList(2107));
//				put("Excess/Umbrella Liability", Arrays.asList(2113));
//				put("Professional Liability", Arrays.asList(2119));
//				put("Pollution Liability", Arrays.asList(2125));
//				put("Contractor Liability", Arrays.asList(2131));
//				put("Employer's Liability", Arrays.asList(2137));
//				put("E&O Liability", Arrays.asList(2143));
//			}
//		};
//		
//		additionalInsuredsQuestionIds = new HashMap<String, List<Integer>>() {
//			{
//				put("General Liability", Arrays.asList(2098));
//				put("Workers Comp", Arrays.asList(2200));
//				put("Automobile Liability", Arrays.asList(2197));
//				put("Excess/Umbrella Liability", Arrays.asList(2203));
//				put("Professional Liability", Arrays.asList(2206));
//				put("Pollution Liability", Arrays.asList(2209));
//				put("Contractor Liability", Arrays.asList(2212));
//				put("Employer's Liability", Arrays.asList(2215));
//				put("E&O Liability", Arrays.asList(2218));
//			}
//		};
//		
//		waiverQuestionIds = new HashMap<String, List<Integer>>() {
//			{
//				put("General Liability", Arrays.asList(2099));
//				put("Workers Comp", Arrays.asList(2201));
//				put("Automobile Liability", Arrays.asList(2198));
//				put("Excess/Umbrella Liability", Arrays.asList(2204));
//				put("Professional Liability", Arrays.asList(2207));
//				put("Pollution Liability", Arrays.asList(2210));
//				put("Contractor Liability", Arrays.asList(2213));
//				put("Employer's Liability", Arrays.asList(2216));
//				put("E&O Liability", Arrays.asList(2219));
//			}
//		};
//	}
//
//	public MigrateCertificates(CertificateDAO certDao,
//			ContractorAccountDAO contractorDao, AuditTypeDAO auditTypeDAO,
//			ContractorAuditDAO auditDao, AuditDataDAO auditDataDao,
//			ContractorAuditOperatorDAO conAuditOperatorDao, AuditBuilder auditBuilder, AuditPercentCalculator auditPercentCalculator,
//			AuditCategoryDAO auditCategoryDAO, AuditCategoryDataDAO auditCategoryDataDAO
//		) {
//		this.certDao = certDao;
//		this.contractorDAO = contractorDao;
//		this.auditTypeDAO = auditTypeDAO;
//		this.auditDAO = auditDao;
//		this.conAuditOperatorDAO = conAuditOperatorDao;
//		this.auditDataDAO = auditDataDao;
//		this.auditBuilder = auditBuilder;
//		this.auditPercentCalculator = auditPercentCalculator;
//		this.auditCategoryDAO = auditCategoryDAO;
//		this.auditCategoryDataDAO = auditCategoryDataDAO;
//		
//		
//	}
//
//	public String execute() throws Exception {
//
//		List<Certificate> allCerts = certDao.findAll();
//
//		// conid, certtype, list of certs
//		Map<Integer, Map<String, List<Certificate>>> byContractor = new HashMap<Integer, Map<String, List<Certificate>>>();
//
//		keyListByContractorAndCertType(allCerts, byContractor);
//
//		clearDaos();
//
//		for (Integer thisContractor : byContractor.keySet()) {
//
//			Map<String, List<Certificate>> byType = byContractor
//					.get(thisContractor);
//
//			for (String certType : byType.keySet()) {
//
//				List<Certificate> certs = byType.get(certType);
//
//				migrateCerts(thisContractor, certType, certs);
//			}
//		}
//
//		return SUCCESS;
//	}
//
//	protected void migrateCerts(Integer thisContractor, String certType,
//			List<Certificate> certs) {
//
//		// connect the contractor object
//		ContractorAccount contractor = contractorDAO.find(thisContractor);
//
//		Certificate golden = certs.get(0); // which certificate to base the
//											// conAudit off of
//
//		ContractorAudit audit = createContractorAudit(contractor, certType,
//				golden);
//
//		for (Certificate cert : certs) {
//
//			try {
//				AuditData fileQuestion = null;
//				
//			
//				Certificate connectedCert = certDao.find(cert.getId());
//				
//				createContractorAuditOperator(connectedCert, audit);
//				
//				createLiabilityLimitAnswers(audit, connectedCert);
//				createExpirationDateAnswers(audit, connectedCert);
//				createWaiverAnswers(audit, connectedCert);
//				
//				if( connectedCert.getNamedInsured() == null || connectedCert.getNamedInsured().length() == 0 ) {
//					fileQuestion = createNonNamedFileAnswers(audit, connectedCert);
//				}
//				else {
//					AuditData namedInsured = createAdditionalInsuredAnswers(audit, connectedCert);
//					fileQuestion = createFileAnswers(audit, connectedCert, namedInsured);
//				}
//				
//				auditDAO.save(audit);
//
//				try {
//					if( getFile(audit, cert).exists() ) {
//						moveFile( audit, connectedCert, fileQuestion );
//					}
//				}
//				catch( Exception problemMovingFile ) {
//					problemMovingFile.printStackTrace();
//				}
//				
//				certDao.remove(cert.getId());
//			}
//			catch( Exception e ) {
//				e.printStackTrace();
//			}
//		}
//		
//
//		clearDaos();
//
//	}
//
//	protected void createLiabilityLimitAnswers(ContractorAudit audit,
//			Certificate cert) {
//		List<Integer> questionIds = liabilityLimitQuestionIds.get(audit
//				.getAuditType().getAuditName());
//
//		for (Integer questionId : questionIds) {
//			if( doesNotYetExist(questionId, audit)) {
//				AuditData ad = new AuditData();
//				ad.setAudit(audit);
//				
//				ad.setAnswer(new Integer( cert.getLiabilityLimit()).toString() );
//				
//				ad.setQuestion(new AuditQuestion());
//				ad.getQuestion().setId(questionId);
//				ad.setComment("");
//				ad.setWasChanged(YesNo.No);
//	
//				ad.setCreatedBy(new User());
//				ad.getCreatedBy().setId(941);
//	
//				ad.setUpdatedBy(new User());
//				ad.getUpdatedBy().setId(941);
//	
//				ad.setCreationDate(new Date());
//				ad.setUpdateDate(new Date());
//				
//				auditDataDAO.save(ad);
//			}
//		}
//	}
//
//	
//	
//	protected boolean doesNotYetExist( Integer questionId, ContractorAudit audit ) {
//		AuditData answer = auditDataDAO.findAnswerToQuestion(audit.getId(), questionId);
//		return answer == null;
//	}
//	
//	
//	protected void createExpirationDateAnswers(ContractorAudit audit,
//			Certificate cert) {
//		List<Integer> questionIds = expirationDateQuestionIds.get(audit
//				.getAuditType().getAuditName());
//		
//		for (Integer questionId : questionIds) {
//			if( doesNotYetExist(questionId, audit)) {
//				AuditData ad = new AuditData();
//				ad.setAudit(audit);
//				
//				ad.setAnswer( cert.getExpiration().toString() );
//				
//				ad.setQuestion(new AuditQuestion());
//				ad.getQuestion().setId(questionId);
//				ad.setComment("");
//				ad.setWasChanged(YesNo.No);
//				
//				ad.setCreatedBy(new User());
//				ad.getCreatedBy().setId(941);
//				
//				ad.setUpdatedBy(new User());
//				ad.getUpdatedBy().setId(941);
//				
//				ad.setCreationDate(new Date());
//				ad.setUpdateDate(new Date());
//				
//				auditDataDAO.save(ad);
//			}
//		}
//	}
//	
//	
//	protected AuditData createAdditionalInsuredAnswers(ContractorAudit audit,
//			Certificate cert) {
//		List<Integer> questionIds = additionalInsuredsQuestionIds.get(audit
//				.getAuditType().getAuditName());
//		
//		AuditData ad = null;
//		
//		for (Integer questionId : questionIds) {
//			
//			ad = new AuditData();
//			ad.setAudit(audit);
//			
//			ad.setAnswer( cert.getNamedInsured() );
//			
//			ad.setQuestion(new AuditQuestion());
//			ad.getQuestion().setId(questionId);
//			ad.setComment("");
//			ad.setWasChanged(YesNo.No);
//			
//			ad.setCreatedBy(new User());
//			ad.getCreatedBy().setId(941);
//			
//			ad.setUpdatedBy(new User());
//			ad.getUpdatedBy().setId(941);
//			
//			ad.setCreationDate(new Date());
//			ad.setUpdateDate(new Date());
//			
//			auditDataDAO.save(ad);
//		}
//		
//		return ad;
//	}
//	
//	protected AuditData createWaiverAnswers(ContractorAudit audit,
//			Certificate cert) {
//		List<Integer> questionIds = waiverQuestionIds.get(audit
//				.getAuditType().getAuditName());
//		
//		AuditData ad = null;
//		
//		for (Integer questionId : questionIds) {
//			if( doesNotYetExist(questionId, audit)) {
//				ad = new AuditData();
//				ad.setAudit(audit);
//				
//				//ad.setParentAnswer(parent);
//				
//				ad.setAnswer( cert.getSubrogationWaived().name() );
//				
//				ad.setQuestion(new AuditQuestion());
//				ad.getQuestion().setId(questionId);
//				ad.setComment("");
//				ad.setWasChanged(YesNo.No);
//				
//				ad.setCreatedBy(new User());
//				ad.getCreatedBy().setId(941);
//				
//				ad.setUpdatedBy(new User());
//				ad.getUpdatedBy().setId(941);
//				
//				ad.setCreationDate(new Date());
//				ad.setUpdateDate(new Date());
//				
//				auditDataDAO.save(ad);
//			}
//		}
//		
//		return ad;
//	}
//	
//	protected AuditData createFileAnswers(ContractorAudit audit,
//			Certificate cert, AuditData parent) {
//		List<Integer> questionIds = fileExtensionQuestionIds.get(audit
//				.getAuditType().getAuditName());
//		
//		AuditData ad = null;
//		
//		for (Integer questionId : questionIds) {
//			
//			ad = new AuditData();
//			ad.setAudit(audit);
//			
//			ad.setParentAnswer(parent);
//			
//			ad.setAnswer( cert.getFileExtension() );
//			
//			ad.setQuestion(new AuditQuestion());
//			ad.getQuestion().setId(questionId);
//			ad.setComment("");
//			ad.setWasChanged(YesNo.No);
//			
//			ad.setCreatedBy(new User());
//			ad.getCreatedBy().setId(941);
//			
//			ad.setUpdatedBy(new User());
//			ad.getUpdatedBy().setId(941);
//			
//			ad.setCreationDate(new Date());
//			ad.setUpdateDate(new Date());
//			
//			auditDataDAO.save(ad);
//		}
//		
//		return ad;
//	}
//	
//	protected AuditData createNonNamedFileAnswers(ContractorAudit audit,
//			Certificate cert) {
//		List<Integer> questionIds = nonNamedFileExtensionQuestionIds.get(audit
//				.getAuditType().getAuditName());
//		
//		AuditData ad = null;
//		
//		for (Integer questionId : questionIds) {
//			if( doesNotYetExist(questionId, audit)) {
//				ad = new AuditData();
//				ad.setAudit(audit);
//				
//				ad.setAnswer( cert.getFileExtension() );
//				
//				ad.setQuestion(new AuditQuestion());
//				ad.getQuestion().setId(questionId);
//				ad.setComment("");
//				ad.setWasChanged(YesNo.No);
//				
//				ad.setCreatedBy(new User());
//				ad.getCreatedBy().setId(941);
//				
//				ad.setUpdatedBy(new User());
//				ad.getUpdatedBy().setId(941);
//				
//				ad.setCreationDate(new Date());
//				ad.setUpdateDate(new Date());
//				
//				auditDataDAO.save(ad);
//			}
//		}
//		
//		return ad;
//	}
//	
//	
//	protected void createContractorAuditOperator(Certificate connectedCert,
//			ContractorAudit audit) {
//
//		
//		try {
//			
//			ContractorAuditOperator cao = conAuditOperatorDAO.find(audit.getId(), connectedCert.getOperatorAccount().getId());
//
//			if( cao != null ) return;
//		}
//		catch( Exception noResult ) {}
//		
//		
//		ContractorAuditOperator conAuditOperator = new ContractorAuditOperator();
//		conAuditOperator.setNotes(connectedCert.getReason());
//		conAuditOperator.setOperator(connectedCert.getOperatorAccount());
//		conAuditOperator.setAudit(audit);
//		
//		/*
//		 * OldStatus: Approved, Expired, Pending, Rejected
//		 * CaoStatus: NotApplicable, Awaiting, Approved, Rejected
//		 * Approved => Approved
//		 * Rejected => Rejected
//		 * Expired => Awaiting
//		 * Pending => Awaiting
//		 */
//		CaoStatus newStatus = CaoStatus.Awaiting;
//		try {
//			String oldStatus = connectedCert.getStatus();
//			newStatus = CaoStatus.valueOf(oldStatus);
//		} catch (Exception justUseMissing) {
//		} finally {
//			conAuditOperator.setStatus(newStatus);
//		}
//
//		conAuditOperator.setCreatedBy(new User());
//		conAuditOperator.getCreatedBy().setId(941);
//
//		conAuditOperator.setUpdatedBy(new User());
//		conAuditOperator.getUpdatedBy().setId(941);
//
//		conAuditOperator.setCreationDate(new Date());
//		conAuditOperator.setUpdateDate(new Date());
//
//		try {
//			conAuditOperatorDAO.save(conAuditOperator);
//			
//		}
//		catch(Exception e) { e.printStackTrace(); } //no need to throw it because we only care that exists
//	}
//
//	protected ContractorAudit createContractorAudit(
//			ContractorAccount contractor, String certType, Certificate golden) {
//
//		List<AuditType> auditTypes = auditTypeDAO
//				.findWhere("classType = 'Policy' and auditName = '"
//						+ auditTypeMapper.get(certType) + "'");
//
//		if (auditTypes == null || auditTypes.size() == 0)
//			throw new RuntimeException("bad auditType: " + certType);
//
//		List<ContractorAudit> audits = null;
//		ContractorAudit audit = null;
//		try {
//			audits = auditDAO.findWhere(1, " contractorAccount.id = " + contractor.getIdString() + " AND auditType.auditTypeID = " + auditTypes.get(0).getId(), "");	
//		}
//		catch( Exception weHandleThatNext ) {}
//		
//		if( audits == null || audits.size() == 0) {
//			
//			AuditType auditType = auditTypes.get(0);
//	
//			// create contractoraudit
//			audit = new ContractorAudit();
//			audit.setAuditType(auditType);
//			audit.setContractorAccount(contractor);
//			audit.setCreatedDate(new Date());
//	
//			if (golden.getStatus().equals("Expired")) {
//				audit.setAuditStatus(AuditStatus.Expired);
//			} else if (golden.getVerified() == YesNo.Yes) {
//				audit.setAuditStatus(AuditStatus.Active);
//			} else if (golden.getVerified() == YesNo.No) {
//				audit.setAuditStatus(AuditStatus.Submitted);
//			}
//	
//			audit.setExpiresDate(golden.getExpiration());
//			audit.setPercentComplete(100);
//	
//			if (golden.getVerified() == YesNo.Yes) {
//				audit.setPercentVerified(100);
//			} else {
//				audit.setPercentVerified(0);
//			}
//			audit.setManuallyAdded(true);
//	
//			auditDAO.save(audit);
//		}
//		else {
//			audit = audits.get(0);
//		}
//		return audit;
//	}
//	
//	protected void moveFile( ContractorAudit audit, Certificate cert, AuditData fileQuestion ) throws Exception {
//		
//		File certificate = getFile(audit, cert);
//		
//		if( certificate.exists() ) {
//
//			String fileBase = "files/"
//					+ FileUtils.thousandize(fileQuestion
//							.getId());
//			String newFileName = "data_"
//					+ fileQuestion.getId();
//
//			FileUtils.moveFile(certificate, getFtpDir(),
//					fileBase, newFileName,
//					FilenameUtils.getExtension(certificate
//							.getName()), true);
//		}
//		else {
//			throw new RuntimeException( "file does not exist: " + certificate.getAbsolutePath() );
//		}
//	}
//
//	protected File getFile(ContractorAudit audit, Certificate cert) {
//		File certificateDir = new File(getFtpDir(), "certificates");
//
//		String fileName = "cert_" + audit.getContractorAccount().getIdString() + "_" + new Integer( cert.getId() ).toString() + "." + cert.getFileExtension();
//		
//		File certificate = new File( certificateDir, fileName );
//		return certificate;
//	}
//
//	protected void keyListByContractorAndCertType(List<Certificate> allCerts,
//			Map<Integer, Map<String, List<Certificate>>> byContractor) {
//		for (Certificate cert : allCerts) {
//
//			Integer conId = cert.getContractorAccount().getId();
//			String certType = cert.getType();
//
//			Map<String, List<Certificate>> thisContractor = byContractor
//					.get(conId);
//
//			if (thisContractor == null) {
//				thisContractor = new HashMap<String, List<Certificate>>();
//				byContractor.put(conId, thisContractor);
//
//			}
//
//			List<Certificate> thisType = thisContractor.get(certType);
//
//			if (thisType == null) {
//				thisType = new Vector<Certificate>();
//				thisContractor.put(certType, thisType);
//			}
//
//			thisType.add(cert);
//		}
//	}
//	
//	protected void clearDaos() {
//		certDao.clear();
//		contractorDAO.clear();
//		auditTypeDAO.clear();
//		auditDAO.clear();
//		conAuditOperatorDAO.clear();
//		auditDataDAO.clear();
//		auditCategoryDAO.clear();
//		auditCategoryDataDAO.clear();
//
//	}
}
