package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.dao.FlagDataOverrideDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorFlagAction extends ContractorActionSupport {
	protected ContractorOperatorDAO contractorOperatorDao;
	protected FlagDataDAO flagDataDAO;
	protected FlagDataOverrideDAO flagDataOverrideDAO;

	protected int opID;
	protected ContractorOperator co;
	protected String action = "";

	protected Date forceEnd;
	protected FlagColor forceFlag;
	protected String forceNote;
	protected boolean overrideAll = false;
	protected int dataID;
	protected Map<String, List<FlagData>> flagDataMap;
	protected Map<FlagCriteria, FlagDataOverride> flagDataOverride;

	public ContractorFlagAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDao, FlagDataDAO flagDataDAO,
			FlagDataOverrideDAO flagDataOverrideDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDao = contractorOperatorDao;
		this.flagDataDAO = flagDataDAO;
		this.flagDataOverrideDAO = flagDataOverrideDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		PicsLogger.start("ContractorFlagAction");
		contractor.setNeedsRecalculation(true);
		contractor.setLastRecalculation(null);
		accountDao.save(contractor);

		if (opID == 0)
			opID = permissions.getAccountId();

		co = contractorOperatorDao.find(id, opID);
		if (co == null) {
			addActionError("This contractor doesn't work at the given site");
			return BLANK;
		}

		if (button != null) {
			if (button.equalsIgnoreCase("Recalculate Now")) {
				co.setFlagLastUpdated(new Date());
				contractorOperatorDao.save(co);
				return redirect("ContractorCronAjax.action?conID=" + id + "&opID=" + opID
					+ "&button=ConFlag&steps=Flag&steps=WaitingOn");
			}
			
			permissions.tryPermission(OpPerms.EditForcedFlags);

			Note note = new Note();
			note.setAccount(co.getContractorAccount());
			note.setAuditColumns(permissions);
			note.setNoteCategory(noteCategory);
			note.setViewableByOperator(permissions);
			note.setCanContractorView(true);

			String noteText = "";
			if (button.equalsIgnoreCase("Force Flag")) {
				if (forceFlag.equals(co.getForceFlag()))
					addActionError("You didn't change the flag color");
				if (forceEnd == null)
					addActionError("You didn't specify an end date");
				if (Strings.isEmpty(forceNote))
					addActionError("You must enter a note when forcing a flag ");

				if (getActionErrors().size() > 0) {
					PicsLogger.stop();
					return SUCCESS;
				}

				co.setForceEnd(forceEnd);
				co.setForceFlag(forceFlag);
				noteText = "Forced the flag to " + forceFlag + " for " + co.getOperatorAccount().getName();

				if (overrideAll == true) {
					for (ContractorOperator co2 : getOperators()) {
						if (!co.equals(co2) && !forceFlag.equals(co2.getForceFlag())) {
							co2.setForceEnd(forceEnd);
							co2.setForceFlag(forceFlag);
							co2.setAuditColumns(permissions);
							contractorOperatorDao.save(co2);

							noteText += ", " + co.getOperatorAccount().getName();
						}
					}
				}
			} else if (button.equalsIgnoreCase("Cancel Override")) {
				co.setForceEnd(null);
				co.setForceFlag(null);
				noteText = "Removed the forced flag for " + co.getOperatorAccount().getName();

				if (overrideAll == true) {
					for (ContractorOperator co2 : getOperators()) {
						if (!co.equals(co2) && co2.getForceFlag() != null) {
							// cancel the flag for all my other operators for
							// this contractor
							contractor.setNeedsRecalculation(true);
							co2.setForceEnd(null);
							co2.setForceFlag(null);
							co2.setAuditColumns(permissions);
							contractorOperatorDao.save(co2);

							noteText += ", " + co.getOperatorAccount().getName();
						}
					}
				}
			} else if ("Force Data Override".equals(button)) {
				FlagData flagData = flagDataDAO.find(dataID);
				if (forceFlag.equals(flagData.getFlag()))
					addActionError("You didn't change the flag color");
				if (forceEnd == null)
					addActionError("You didn't specify an end date");
				if (getActionErrors().size() > 0) {
					PicsLogger.stop();
					return SUCCESS;
				}

				FlagDataOverride flagOverride = new FlagDataOverride();
				flagOverride.setContractor(contractor);
				flagOverride.setOperator(co.getOperatorAccount());
				if(overrideAll) {
					flagOverride.setOperator(new OperatorAccount());
					flagOverride.getOperator().setId(permissions.getAccountId());
				}
				flagOverride.setForceflag(forceFlag);
				flagOverride.setForceEnd(forceEnd);
				flagOverride.setCriteria(flagData.getCriteria());
				flagOverride.setAuditColumns(permissions);
				flagDataOverrideDAO.save(flagOverride);

				noteText = "Forced the flag to " + forceFlag + " for criteria " + flagData.getCriteria().getLabel()
						+ " for " + co.getOperatorAccount().getName();
			} else if ("Cancel Data Override".equals(button)) {
				FlagData flagData = flagDataDAO.find(dataID); 
				noteText = "Removed the Force flag for criteria " + flagData.getCriteria().getLabel()
				+ " for " + co.getOperatorAccount().getName();
				
				FlagDataOverride flagDataOverride = isFlagDataOverride(flagData);
				if(flagDataOverride != null) {
					for (FlagCriteria flagCriteria : getFlagDataOverrides().keySet()) {
						if (flagCriteria.equals(flagDataOverride.getCriteria())) {
							getFlagDataOverrides().remove(flagCriteria);
						}
					}
					if(flagDataOverride.getOperator().equals(co.getOperatorAccount()))
						flagDataOverrideDAO.remove(flagDataOverride);
				}
				if (overrideAll == true) {
					if(flagDataOverride.getOperator().getId() == permissions.getAccountId()) {
						flagDataOverrideDAO.remove(flagDataOverride);
					}
				}
			}
			
			co.setFlagLastUpdated(new Date());
			contractorOperatorDao.save(co);
			note.setSummary(noteText);
			
			if (forceNote != null && !forceNote.equals(""))
				note.setBody(forceNote);
			
			getNoteDao().save(note);
			return redirect("ContractorCronAjax.action?conID=" + id + "&opID=" + opID + "&button=ConFlag&steps=Flag&steps=WaitingOn");
		}

		PicsLogger.stop();
		return SUCCESS;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public ContractorOperator getCo() {
		return co;
	}

	public void setCo(ContractorOperator co) {
		this.co = co;
	}

	public FlagColor[] getFlagList() {
		return FlagColor.values();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(Date forceEnd) {
		this.forceEnd = forceEnd;
	}

	public FlagColor getForceFlag() {
		return forceFlag;
	}

	public void setForceFlag(FlagColor forceFlag) {
		this.forceFlag = forceFlag;
	}

	public String getForceNote() {
		return forceNote;
	}

	public void setForceNote(String forceNote) {
		this.forceNote = forceNote;
	}

	public boolean isOverrideAll() {
		return overrideAll;
	}

	public void setOverrideAll(boolean overrideAll) {
		this.overrideAll = overrideAll;
	}

	public int getDataID() {
		return dataID;
	}

	public void setDataID(int dataID) {
		this.dataID = dataID;
	}

	public boolean isCanSeeAudit(AuditType auditType) {
		if (permissions.isContractor() && auditType.isCanContractorView())
			return true;
		if (permissions.hasPermission(OpPerms.ContractorDetails))
			return true;
		return false;
	}

	public Map<String, List<FlagData>> getflagDataMap() {
		if (flagDataMap == null) {
			flagDataMap = new TreeMap<String, List<FlagData>>();
			Set<FlagData> flagData = co.getFlagDatas();
			
			List<FlagData> flagDataList = new ArrayList<FlagData>(flagData);
			Collections.sort(flagDataList, new ByOrderCategoryLabel());
			
			for(FlagData flagData2 : flagDataList) {
				if(!flagData2.getCriteria().isInsurance()) {
					if(flagDataMap.get(flagData2.getCriteria().getCategory()) == null) {
						flagDataMap.put(flagData2.getCriteria().getCategory(), new ArrayList<FlagData>());
					}
					flagDataMap.get(flagData2.getCriteria().getCategory()).add(flagData2);
				}
			}
		}
		return flagDataMap;
	}

	public Map<FlagCriteria, FlagDataOverride> getFlagDataOverrides() {
		if (flagDataOverride == null)
			flagDataOverride = flagDataOverrideDAO.findByContractorAndOperator(contractor, co.getOperatorAccount());
		return flagDataOverride;
	}
	
	public String getAmBestRating(String value) {
		int rating = (int) Float.parseFloat(value);
		return AmBest.ratingMap.get(rating);
	}
	
	public String getAmBestClass(String value) {
		int classValue = (int) Float.parseFloat(value);
		return AmBest.financialMap.get(classValue);
	}

	public FlagDataOverride isFlagDataOverride(FlagData flagData) {
		if (getFlagDataOverrides() != null) {
			FlagDataOverride flOverride = getFlagDataOverrides().get(flagData.getCriteria());
			if (flOverride != null && flOverride.isInForce()) {
				return flOverride;
			}
		}
		return null;
	}
	
	public boolean isDisplayTable() {
		if (getflagDataMap() != null) {
			for (String category : flagDataMap.keySet()) {
				for (FlagData flagData : flagDataMap.get(category)) {
					if (flagData.getFlag().equals(FlagColor.Red) || flagData.getFlag().equals(FlagColor.Amber) ||
							isFlagDataOverride(flagData) != null) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private class ByOrderCategoryLabel implements Comparator<FlagData> {
		public int compare(FlagData o1, FlagData o2) {
			FlagCriteria f1 = o1.getCriteria();
			FlagCriteria f2 = o2.getCriteria();
			
			if (f1.getDisplayOrder() == f2.getDisplayOrder()) {
				if (f1.getCategory().equals(f2.getCategory())) {
					return f1.getLabel().compareTo(f2.getLabel());
				} else
					return f1.getCategory().compareTo(f2.getCategory());
			} else
				return f1.getDisplayOrder() - f2.getDisplayOrder();
		}
	}
}
