package com.picsauditing.actions.contractors;

import java.net.URLEncoder;
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
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.dao.FlagDataOverrideDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorFlagAction extends ContractorActionSupport {
	protected ContractorOperatorDAO contractorOperatorDao;
	protected FlagDataDAO flagDataDAO;
	protected FlagDataOverrideDAO flagDataOverrideDAO;
	protected OperatorAccountDAO opDAO;
	protected FacilitiesDAO facDAO;
	protected NaicsDAO naicsDAO;

	protected int opID;
	protected ContractorOperator co;
	protected String action = "";

	protected Date forceEnd;
	protected FlagColor forceFlag;
	protected String forceNote;
	protected boolean overrideAll = false;
	protected int dataID;
	protected Map<String, List<FlagData>> flagDataMap;
	protected Map<FlagCriteria, List<FlagDataOverride>> flagDataOverride;

	public ContractorFlagAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDao, FlagDataDAO flagDataDAO,
			FlagDataOverrideDAO flagDataOverrideDAO, FacilitiesDAO facDAO, NaicsDAO naicsDAO) {
		super(accountDao, auditDao);
		this.contractorOperatorDao = contractorOperatorDao;
		this.flagDataDAO = flagDataDAO;
		this.flagDataOverrideDAO = flagDataOverrideDAO;
		this.facDAO = facDAO;
		this.naicsDAO = naicsDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		PicsLogger.start("ContractorFlagAction");
		contractor.incrementRecalculation();
		accountDao.save(contractor);
		noteCategory = NoteCategory.Flags;

		if (permissions.isOperator() || (opID == 0 && permissions.isCorporate()))
			opID = permissions.getAccountId();

		// can't view unrelated co links
		if (permissions.isCorporate()) {
			// check to see if corporate id pulls anything with this op id,
			// if not then give error
			if (opID != permissions.getAccountId()) {
				Facility f = facDAO.findByCorpOp(permissions.getAccountId(), opID);
				if (f == null) {
					addActionError("You do not have permission to view this Facility");
					return BLANK;
				}
			}
		} else if (permissions.isContractor()) {
			if (id != permissions.getAccountId()) {
				// check to see if con id and id match
				addActionError("You do not have permission to view this Facility");
				return BLANK;
			}
		}

		co = contractorOperatorDao.find(id, opID);
		if (co == null) {
			addActionError("This contractor doesn't work at the given site");
			return BLANK;
		}

		if (button != null) {
			if (button.equalsIgnoreCase("Recalculate Now")) {
				contractorOperatorDao.save(co);
				String redirectUrl = URLEncoder.encode("ContractorFlag.action?id=" + id + "&opID=" + opID, "UTF-8");
				return redirect("ContractorCronAjax.action?conID=" + id + "&opID=" + opID + "&steps=All&redirectUrl="
						+ redirectUrl);
			}

			permissions.tryPermission(OpPerms.EditForcedFlags);

			Note note = new Note();
			note.setAccount(co.getContractorAccount());
			note.setAuditColumns(permissions);
			note.setNoteCategory(noteCategory);
			note.setViewableByOperator(permissions);
			note.setCanContractorView(true);

			String noteText = "";
			if (button.equalsIgnoreCase("Force Overall Flag")) {
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

				if (overrideAll == true) {
					ContractorOperator co2 = contractorOperatorDao.find(co.getContractorAccount().getId(), permissions
							.getAccountId());
					co2.setForceEnd(forceEnd);
					co2.setForceFlag(forceFlag);
					co2.setForceBegin(new Date());
					co2.setForcedBy(getUser());
					contractorOperatorDao.save(co2);
					noteText = "Forced the flag to " + forceFlag + " for all the sites";
				} else {
					co.setForceEnd(forceEnd);
					co.setForceFlag(forceFlag);
					co.setForceBegin(new Date());
					co.setForcedBy(getUser());
					noteText = "Forced the flag to " + forceFlag + " for " + co.getOperatorAccount().getName();
				}
			} else if (button.equalsIgnoreCase("Cancel Override")) {
				if (overrideAll == true) {
					ContractorOperator co2 = contractorOperatorDao.find(co.getContractorAccount().getId(), permissions
							.getAccountId());
					co2.removeForceFlag();
					co2.setAuditColumns(permissions);
					contractorOperatorDao.save(co2);
					contractor.incrementRecalculation();
				} else {
					co.removeForceFlag();
					noteText = "Removed the forced flag for " + co.getOperatorAccount().getName();
				}
			} else if ("Force Individual Flag".equals(button)) {
				if (forceFlag == null) {
					addActionError("You did not choose a flag color");
					return SUCCESS;
				}
				FlagData flagData = flagDataDAO.find(dataID);

				if (forceFlag.equals(co.getForceFlag()))
					addActionError("You didn't change the flag color");
				if (forceEnd == null)
					addActionError("You didn't specify an end date");
				if (getActionErrors().size() > 0) {
					PicsLogger.stop();
					return SUCCESS;
				}

				FlagDataOverride flagOverride = flagDataOverrideDAO.findByConAndOpAndCrit(contractor.getId(), opID,
						flagData.getCriteria().getId());
				if (flagOverride == null) {
					flagOverride = new FlagDataOverride();
					flagOverride.setContractor(contractor);
					flagOverride.setOperator(co.getOperatorAccount());
				}
				if (overrideAll) {
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
				noteText = "Removed the Force flag for criteria " + flagData.getCriteria().getLabel() + " for "
						+ co.getOperatorAccount().getName();

				FlagDataOverride flagDataOverride = isFlagDataOverride(flagData);
				if (flagDataOverride != null) {
					for (FlagCriteria flagCriteria : getFlagDataOverrides().keySet()) {
						if (flagCriteria.equals(flagDataOverride.getCriteria())) {
							getFlagDataOverrides().remove(flagCriteria);
						}
					}
					if (overrideAll) {
						if (flagDataOverride.getOperator().getId() == permissions.getAccountId()) {
							flagDataOverrideDAO.remove(flagDataOverride);
						}
					} else if (flagDataOverride.getOperator().equals(co.getOperatorAccount()))
						flagDataOverrideDAO.remove(flagDataOverride);
				}
			}

			co.setFlagLastUpdated(new Date());
			co.setAuditColumns(permissions);
			contractorOperatorDao.save(co);
			note.setSummary(noteText);

			if (!Strings.isEmpty(forceNote))
				note.setBody(forceNote);

			getNoteDao().save(note);
			String redirectUrl = "ContractorFlag.action?id=" + id + "%26opID=" + opID;
			return redirect("ContractorCronAjax.action?conID=" + id + "&opID=" + opID + "&steps=Flag&steps=WaitingOn"
					+ "&redirectUrl=" + redirectUrl);
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

	public ArrayList<String> getUnusedFlagColors(int id) {
		FlagData flagData = flagDataDAO.find(id);
		ArrayList<String> fColor = FlagColor.getValuesWithDefault();
		if (flagData != null)
			fColor.remove(flagData.getFlag().name());
		fColor.remove(FlagColor.Clear.name());
		return fColor;
	}

	public ArrayList<String> getUnusedCoFlag() {
		FlagColor fc = co.getFlagColor();
		ArrayList<String> fColor = FlagColor.getValuesWithDefault();
		if (fc != null)
			fColor.remove(fc.name());
		fColor.remove(FlagColor.Clear.name());
		return fColor;
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

			for (FlagData flagData2 : flagDataList) {
				if (!flagData2.getCriteria().isInsurance()) {
					if (flagDataMap.get(flagData2.getCriteria().getCategory()) == null) {
						flagDataMap.put(flagData2.getCriteria().getCategory(), new ArrayList<FlagData>());
					}
					flagDataMap.get(flagData2.getCriteria().getCategory()).add(flagData2);
				}
			}
		}
		return flagDataMap;
	}

	public Map<FlagCriteria, List<FlagDataOverride>> getFlagDataOverrides() {
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
		if (getFlagDataOverrides() != null && getFlagDataOverrides().size() > 0) {
			List<FlagDataOverride> flOverride = getFlagDataOverrides().get(flagData.getCriteria());
			if (flOverride != null && flOverride.size() > 0) {
				for (FlagDataOverride flagDataOverride : flOverride) {
					if (flagDataOverride.getOperator().equals(co.getOperatorAccount()) && flagDataOverride.isInForce())
						return flagDataOverride;
				}
				if (flOverride.get(0).isInForce())
					return flOverride.get(0);
			}
		}
		return null;
	}

	public String getContractorAnswer(FlagCriteriaContractor fcc, FlagData f, boolean addLabel) {
		FlagCriteria fc = f.getCriteria();
		String answer = fcc.getAnswer();

		if (fc.getDescription().contains("AMB Class"))
			answer = getAmBestClass(answer);
		else if (fc.getDescription().contains("AMB Rating"))
			answer = getAmBestRating(answer);
		else if (fc.getQuestion() != null && fc.getQuestion().getId() == AuditQuestion.EMR) {
			addLabel = false;
			answer = "EMR for " + fcc.getAnswer2().split("<br/>")[0] + " is "
					+ format(Float.parseFloat(answer), "#,##0.000");
		} else if (fc.getOshaRateType() != null) {
			addLabel = false;
			answer = fc.getOshaType().name() + " " + fc.getOshaRateType().getDescription() + " for "
					+ fcc.getAnswer2().split("<br/>")[0] + " is " + Strings.formatDecimalComma(answer);

			if (fc.getOshaRateType().equals(OshaRateType.LwcrNaics)
					|| fc.getOshaRateType().equals(OshaRateType.TrirNaics)) {
				for (FlagCriteriaOperator fco : co.getOperatorAccount().getFlagCriteriaInherited()) {
					if (fco.getCriteria().equals(fc) && fco.getCriteria().equals(f.getCriteria())) {
						answer += " and must be less than ";
						if (fc.getOshaRateType().equals(OshaRateType.LwcrNaics))
							answer += (getIndustryAverage(true, f.getContractor().getNaics()) * Float.parseFloat(fco
									.criteriaValue())) / 100;
						if (fc.getOshaRateType().equals(OshaRateType.TrirNaics)) {
							answer += (getIndustryAverage(false, f.getContractor().getNaics()) * Float.parseFloat(fco
									.criteriaValue())) / 100;
						}
					}
				}
				answer += " for industry code " + f.getContractor().getNaics().getCode();
			}
		} else if (fc.getDataType().equals(FlagCriteria.NUMBER))
			answer = Strings.formatDecimalComma(answer);
		if (addLabel)
			answer = fc.getLabel() + " - " + answer;

		if (!Strings.isEmpty(fcc.getAnswer2())) {
			String[] exploded = fcc.getAnswer2().split("<br/>");
			String year = null;
			String conAnswer = null;
			String verified = null;

			for (String token : exploded) {
				if (token.contains("Year"))
					year = token;
				if (token.contains("Contractor"))
					conAnswer = token;
				if (token.contains("Verified"))
					verified = token;
			}

			if (verified != null)
				answer += "&nbsp;" + verified;
			if (year != null) {
				String front = "<span title=\"" + year;

				if (conAnswer != null)
					answer += " " + conAnswer;

				answer = front + "\">" + answer + "</span>";
			}
		}

		return answer;
	}

	public boolean isDisplayTable() {
		if (getflagDataMap() != null) {
			for (String category : flagDataMap.keySet()) {
				for (FlagData flagData : flagDataMap.get(category)) {
					if (flagData.getFlag().equals(FlagColor.Red) || flagData.getFlag().equals(FlagColor.Amber)
							|| isFlagDataOverride(flagData) != null) {
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

	public boolean canForceDataFlag(FlagDataOverride flagOverride) {
		if (flagOverride == null) {
			if (permissions.getAccountId() == opID || permissions.isCorporate())
				return true;
		}
		if (flagOverride.getOperator().getId() != permissions.getAccountId())
			return true;
		return false;
	}

	public boolean canForceOverallFlag(ContractorOperator conOperator) {
		// if they can override the flag we return true
		if (conOperator == null) {
			if (permissions.getAccountId() == opID || permissions.isCorporate())
				return false;
		}
		if (conOperator.getForceOverallFlag().getOperatorAccount().getId() == permissions.getAccountId())
			return true;

		return false;
	}

	private float getIndustryAverage(boolean lwcr, Naics naics) {
		naics = getBroaderNaics(lwcr, naics);

		if (naics == null)
			return 0;
		if (lwcr)
			return naics.getLwcr();
		else
			return naics.getTrir();
	}

	private Naics getBroaderNaics(boolean lwcr, Naics naics) {
		String code = naics.getCode();
		if (Strings.isEmpty(code))
			return null;

		if ((lwcr && naics.getLwcr() > 0) || (!lwcr && naics.getTrir() > 0))
			return naics;
		else {
			Naics naics2 = naicsDAO.find(code.substring(0, code.length() - 1));
			if (naics2 == null)
				return null;

			if ((lwcr && naics2.getLwcr() > 0) || (!lwcr && naics2.getTrir() > 0))
				return naics2;
			else
				return getBroaderNaics(lwcr, naics2);
		}
	}
}
