package com.picsauditing.dao;

public abstract class DAOFactory {
	/**
	* Factory method for instantiation of concrete factories.
	*/
	
	@SuppressWarnings("unchecked")
	public static Class JPA = JPADAOFactory.class;
	
	private String persistenceUnit;
	
	@SuppressWarnings("unchecked")
	public static DAOFactory instance(Class factory, String persistenceUnit) {
		try {
			 DAOFactory daoFactory = (DAOFactory)factory.newInstance();
			 daoFactory.setPersistenceUnit(persistenceUnit);
			 return daoFactory;
			} catch (Exception ex) {
				throw new RuntimeException("Couldn't create DAOFactory: " + factory);
			}
		}
		
		public String getPersistenceUnit() {
			return persistenceUnit;
		}



		public void setPersistenceUnit(String persistenceUnit) {
			this.persistenceUnit = persistenceUnit;
		}



		// Add your DAO interfaces here
		public abstract AccountDAO getAccountDAO();
		public abstract ContractorInfoDAO getContractorInfoDAO();
		public abstract OshaLogDAO getOshaLogDAO();
		public abstract PqfLogDAO getPqfLogDAO();
		public abstract AccountReportDAO getAccountReportDAO();
		public abstract ContractorInfoReportDAO getContractorInfoReportDAO();
		public abstract PqfLogReportDAO getPqfLogReportDAO();
}