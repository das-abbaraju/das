package com.picsauditing.dao;

public class JPADAOFactory extends DAOFactory {
	
	@SuppressWarnings("unchecked")
	private GenericJPADAO instantiateDAO(Class daoClass) {
		try {
			GenericJPADAO dao = (GenericJPADAO)	daoClass.newInstance();
			dao.setPersistenceUnit(getPersistenceUnit());
			return dao;
		} catch (Exception ex) {
			throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
		}
	}
	
	@Override
	public AccountDAO getAccountDAO() {
		return (AccountDAO) instantiateDAO(AccountDAOJPA.class);
	}

	@Override
	public ContractorInfoDAO getContractorInfoDAO() {
		return (ContractorInfoDAO) instantiateDAO(ContractorInfoDAOJPA.class);
	}

	@Override
	public OshaLogDAO getOshaLogDAO() {
		return (OshaLogDAO) instantiateDAO(OshaLogDAOJPA.class);
	}
	
	@Override
	public PqfLogDAO getPqfLogDAO() {
		return (PqfLogDAO) instantiateDAO(PqfLogDAOJPA.class);
	}
	
	@Override
	public AccountReportDAO getAccountReportDAO() {
		return (AccountReportDAO) instantiateDAO(AccountReportDAOJPA.class);
	}
	
	@Override
	public ContractorInfoReportDAO getContractorInfoReportDAO() {
		return (ContractorInfoReportDAO) instantiateDAO(ContractorInfoReportDAOJPA.class);
	}
	
	@Override
	public PqfLogReportDAO getPqfLogReportDAO() {
		return (PqfLogReportDAO) instantiateDAO(PqfLogReportDAOJPA.class);
	}
	
	@Override
	public GeneralContractorDAO getGeneralContractorDAO() {
		return (GeneralContractorDAO) instantiateDAO(GeneralContractorDAOJPA.class);
	}




}
