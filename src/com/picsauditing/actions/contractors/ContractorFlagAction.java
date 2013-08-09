package com.picsauditing.actions.contractors;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ArrayListMultimap;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.dao.FlagDataOverrideDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.models.contractors.ContractorFlagAnswerDisplay;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.Strings;
import com.picsauditing.util.YearList;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorFlagAction extends ContractorActionSupport {
	@Autowired
	protected ContractorAuditOperatorDAO caoDAO;
	@Autowired
	protected ContractorOperatorDAO contractorOperatorDao;
	@Autowired
	protected FacilitiesDAO facDAO;
	@Autowired
	protected FlagDataDAO flagDataDAO;
	@Autowired
	protected FlagDataOverrideDAO flagDataOverrideDAO;
	@Autowired
	protected NaicsDAO naicsDAO;
	@Autowired
	protected OperatorAccountDAO opDAO;
	@Autowired
	protected FlagCriteriaOperatorDAO flagCriteriaOperatorDao;
	@Autowired
	protected ContractorFlagAnswerDisplay contractorFlagAnswerDisplay;

	protected int opID;
	protected ContractorOperator co;
	protected String action = "";

	protected Date forceEnd;
	protected FlagColor forceFlag;
	protected String forceNote;
	protected boolean overrideAll = false;
	protected int dataID;
	protected Map<FlagCriteriaCategory, List<FlagData>> flagDataMap;
	protected Map<FlagCriteria, List<FlagDataOverride>> flagDataOverride;
	protected ArrayListMultimap<AuditType, ContractorAuditOperator> missingAudits = null;
	private Map<FlagColor, Integer> flagCounts;
	private int[] operatorIds = null;
	private boolean displayCorporate = false;
	private boolean permittedToForceFlags = false;

	private File file;
	private String fileContentType;
	private String fileFileName;
	private InputStream inputStream;

	public String execute() throws Exception {
		subHeading = getText("ContractorFlag.FlagStatus");
		limitedView = true;
		findContractor();

		try {
			PicsLogger.start("ContractorFlagAction");
			contractor.incrementRecalculation();
			contractorAccountDao.save(contractor);
			noteCategory = NoteCategory.Flags;

			if (!canFindOperator())
				return BLANK;

		} catch (Exception ignore) {
		} finally {
			PicsLogger.stop();
		}
		
		contractorFlagAnswerDisplay.setContractor(contractor);
		contractorFlagAnswerDisplay.setContractorOperator(co);

		return SUCCESS;
	}

	public String approveFlag() throws Exception {
		findContractor();
		if (!canFindOperator())
			return BLANK;

		co.resetBaseline(permissions);

		contractorOperatorDao.save(co);
		try {
			permissions.tryPermission(OpPerms.EditForcedFlags);
			return this.setUrlForRedirect("ContractorFlag.action?id=" + id + "&opID=" + opID);
		} catch (Exception x) {
		}

		return completeAction("");
	}

	public String recalculate() throws Exception {
		findContractor();
		if (!canFindOperator())
			return BLANK;

		contractorOperatorDao.save(co);

		try {
			String redirectUrl = URLEncoder.encode("ContractorFlag.action?id=" + id + "&opID=" + opID, "UTF-8");
			return setUrlForRedirect("ContractorCronAjax.action?conID=" + id + "&opID=0&steps=All&redirectUrl=" + redirectUrl);
		} catch (Exception x) {
		}
		return SUCCESS;
	}

	public String cancelOverride() throws Exception {
		findContractor();
		if (!canFindOperator())
			return BLANK;

		try {
			permissions.tryPermission(OpPerms.EditForcedFlags);
		} catch (Exception x) {
			return SUCCESS;
		}

		String noteText = "";

		if (overrideAll == true) {
			ContractorOperator co2 = contractorOperatorDao.find(co.getContractorAccount().getId(),
					permissions.getAccountId());
			// save history
			FlagOverrideHistory foh = new FlagOverrideHistory();
			foh.setOverride(co2);
			foh.setAuditColumns(permissions);
			foh.setDeleted(true);
			foh.setDeleteReason(noteText);
			dao.save(foh);

			co2.removeForceFlag();
			co2.setAuditColumns(permissions);
			contractorOperatorDao.save(co2);
			contractor.incrementRecalculation();
			noteText = "Removed the Forced flag for all the sites";
		} else {
			// save history
			FlagOverrideHistory foh = new FlagOverrideHistory();
			foh.setOverride(co);
			foh.setAuditColumns(permissions);
			foh.setDeleted(true);
			foh.setDeleteReason(noteText);
			dao.save(foh);

			co.removeForceFlag();
			noteText = "Removed the Forced flag for " + co.getOperatorAccount().getName();
		}

		return completeAction(noteText);
	}

	public String forceOverallFlag() throws Exception {
		findContractor();
		if (!canFindOperator())
			return BLANK;

		String noteText = "";

		if (forceFlag == null || forceFlag.equals(co.getForceFlag()))
			addActionMessage(getText("ContractorFlag.error.FlagNotChange"));
		if (forceEnd == null)
			addActionError(getText("ContractorFlag.error.NoDate"));
		if (Strings.isEmpty(forceNote))
			addActionError(getText("ContractorFlag.error.NoteRequire"));

		if (getActionErrors().size() > 0) {
			return SUCCESS;
		}

		SimpleDateFormat format = new SimpleDateFormat(PicsDateFormat.Iso);
		if (overrideAll == true) {
			ContractorOperator co2 = contractorOperatorDao.find(co.getContractorAccount().getId(),
					permissions.getAccountId());
			FlagColor currentFlag = co2.getFlagColor();
			co2.setForceEnd(forceEnd);
			co2.setForceFlag(forceFlag);
			co2.setForceBegin(new Date());
			co2.setForcedBy(getUser());
			contractorOperatorDao.save(co2);
			noteText = "Forced the flag from " + currentFlag + " to " + forceFlag + " for all the sites until "
					+ format.format(forceEnd);

		} else {
			FlagColor currentFlag = co.getFlagColor();
			co.setForceEnd(forceEnd);
			co.setForceFlag(forceFlag);
			co.setForceBegin(new Date());
			co.setForcedBy(getUser());
			noteText = "Forced the flag from " + currentFlag + " to " + forceFlag + " for "
					+ co.getOperatorAccount().getName() + "  until " + format.format(forceEnd);
		}

		return completeAction(noteText);
	}

	public String forceIndividualFlag() throws Exception {
		findContractor();
		if (!canFindOperator())
			return BLANK;

		if (forceFlag == null) {
			addActionError(getText("ContractorFlag.error.NoFlagColorChosen"));
			return SUCCESS;
		}
		FlagData flagData = flagDataDAO.find(dataID);

		if (forceEnd == null)
			addActionError(getText("ContractorFlag.error.NoEndDate"));
		if (getActionErrors().size() > 0) {
			return SUCCESS;
		}

		FlagDataOverride flagOverride = flagDataOverrideDAO.findByConAndOpAndCrit(contractor.getId(), opID, flagData
				.getCriteria().getId());
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
		if (flagData.getCriteria().getMultiYearScope() != null
				&& flagData.getCriteria().getMultiYearScope().isIndividualYearScope()) {
			flagOverride.setYear(getAppropriateAnnualAuditYear(flagData.getCriteria()));
		}
		flagOverride.setAuditColumns(new User(permissions.getUserId()));
		flagDataOverrideDAO.save(flagOverride);

		SimpleDateFormat format = new SimpleDateFormat(PicsDateFormat.Iso);
		String noteText = "Forced the flag from " + flagData.getFlag() + " to " + forceFlag + " for criteria "
				+ flagData.getCriteria().getLabel() + " for " + co.getOperatorAccount().getName() + " until "
				+ format.format(forceEnd);
		return completeAction(noteText);
	}

	public String cancelDataOverride() throws Exception {
		findContractor();
		if (!canFindOperator())
			return BLANK;

		FlagData flagData = flagDataDAO.find(dataID);
		String noteText = "Removed the Forced flag for criteria " + flagData.getCriteria().getLabel() + " for "
				+ co.getOperatorAccount().getName();

		FlagDataOverride flagDataOverride = isFlagDataOverride(flagData, null);
		if (flagDataOverride != null) {
			// save history
			FlagOverrideHistory foh = new FlagOverrideHistory();
			foh.setOverride(flagDataOverride);
			foh.setAuditColumns(permissions);
			foh.setDeleted(true);
			foh.setDeleteReason(noteText);
			dao.save(foh);

			for (Iterator<FlagCriteria> it = getFlagDataOverrides(null).keySet().iterator(); it.hasNext();) {
				if (it.next().equals(flagDataOverride.getCriteria()))
					it.remove();
			}
			if (overrideAll) {
				if (flagDataOverride.getOperator().getId() == permissions.getAccountId()) {
					flagDataOverrideDAO.remove(flagDataOverride);
				}
			} else if (flagDataOverride.getOperator().equals(co.getOperatorAccount()))
				flagDataOverrideDAO.remove(flagDataOverride);
		}
		return completeAction(noteText);
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

	public List<ContractorOperator> getCorporateOverrides() {
		ArrayList<ContractorOperator> corporateOverrides = new ArrayList<ContractorOperator>();
		HashMap<String, ContractorOperator> uniqueCorporateOverrides = new HashMap<String, ContractorOperator>();

		for (ContractorOperator co : getActiveOperators()) {
			ContractorOperator forceOverallConOp = co.getForceOverallFlag();
			if (forceOverallConOp != null && forceOverallConOp.getForceFlag() != null) {
				String key = forceOverallConOp.getForceFlag().toString() + "|"
						+ forceOverallConOp.getForceEnd().toString() + "|" + forceOverallConOp.getForcedBy().getId()
						+ "|";
				if (!uniqueCorporateOverrides.containsKey(key))
					uniqueCorporateOverrides.put(key, forceOverallConOp);
			}
		}

		corporateOverrides.addAll(uniqueCorporateOverrides.values());

		return corporateOverrides;
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

	// Note file attachment?
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setFlagCounts(Map<FlagColor, Integer> flagCounts) {
		this.flagCounts = flagCounts;
	}

	public boolean isDisplayCorporate() {
		return displayCorporate;
	}

	public void setDisplayCorporate(boolean displayCorporate) {
		this.displayCorporate = displayCorporate;
	}

	public boolean isPermittedToForceFlags() {
		return permittedToForceFlags;
	}

	public void setPermittedToForceFlags(boolean permittedToForceFlags) {
		this.permittedToForceFlags = permittedToForceFlags;
	}

	public boolean isCanSeeAudit(ContractorAuditOperator cao) {
		if (permissions.isContractor() && cao.getAudit().getAuditType().isCanContractorView())
			return true;
		if (permissions.hasPermission(OpPerms.ContractorDetails))
			return true;
		if (cao.isVisibleTo(permissions))
			return true;
		return false;
	}

	public Map<FlagCriteriaCategory, List<FlagData>> getFlagDataMap() {
		if (flagDataMap == null) {
			flagDataMap = new TreeMap<>();
			Set<FlagData> flagData = co.getFlagDatas();

			List<FlagData> flagDataList = new ArrayList<FlagData>(flagData);

			if (displayCorporate) {
				for (ContractorOperator co2 : getActiveOperators()) {
					flagDataList.addAll(co2.getFlagDatas());
				}
			}

			Collections.sort(flagDataList, new ByOrderCategoryLabel());

			for (FlagData flagData2 : flagDataList) {
				if (flagData2.getCriteria().getCategory() != FlagCriteriaCategory.InsuranceCriteria) {
					if (flagDataMap.get(flagData2.getCriteria().getCategory()) == null)
						flagDataMap.put(flagData2.getCriteria().getCategory(), new ArrayList<FlagData>());
					flagDataMap.get(flagData2.getCriteria().getCategory()).add(flagData2);
				}
			}
		}
		return flagDataMap;
	}

	public Map<FlagCriteria, List<FlagDataOverride>> getFlagDataOverrides(OperatorAccount op) {
		if (op == null)
			op = co.getOperatorAccount();
		flagDataOverride = flagDataOverrideDAO.findByContractorAndOperator(contractor, op);
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

	public FlagDataOverride isFlagDataOverride(FlagData flagData, OperatorAccount op) {
		if (op == null)
			op = co.getOperatorAccount();
		Map<FlagCriteria, List<FlagDataOverride>> flagDataOverrides = getFlagDataOverrides(op);

		if (flagDataOverrides != null && flagDataOverrides.size() > 0) {
			List<FlagDataOverride> flOverride = flagDataOverrides.get(flagData.getCriteria());
			if (flOverride != null && flOverride.size() > 0) {
				for (FlagDataOverride flagDataOverride : flOverride) {
					if (op.isApplicableFlagOperator(flagDataOverride.getOperator()) && flagDataOverride.isInForce()) {
						return flagDataOverride;
					}
				}
			}
		}
		return null;
	}

	public String getContractorAnswer(FlagData f, boolean addLabel) {
		return contractorFlagAnswerDisplay.getContractorAnswer(f, addLabel);
	}

	public String getContractorAnswer(FlagCriteriaContractor fcc, FlagData f, boolean addLabel) {
		return contractorFlagAnswerDisplay.getContractorAnswer(fcc, f, addLabel);
	}

	public boolean isDisplayTable() {
		if (getFlagDataMap() != null) {
			for (FlagCriteriaCategory category : flagDataMap.keySet()) {
				for (FlagData flagData : flagDataMap.get(category)) {
					if (flagData.getFlag().equals(FlagColor.Red) || flagData.getFlag().equals(FlagColor.Amber)
							|| isFlagDataOverride(flagData, null) != null) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean canForceDataFlag(FlagDataOverride flagOverride) {
		if (flagOverride == null) {
			if (permissions.getAccountId() == opID || permissions.isCorporate())
				return true;
		}
		if (permissions.getUserId() == flagOverride.getCreatedBy().getId())
			return false;
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
		if ((conOperator.getForceOverallFlag().getOperatorAccount().getId() == permissions.getAccountId())
				|| conOperator.getForcedBy().getId() == permissions.getUserId())
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	public ArrayListMultimap<AuditType, ContractorAuditOperator> getMissingAudits() {
		if (missingAudits == null) {
			for (FlagData flagData : co.getFlagDatas()) {
				if (flagData.getFlag().isRedAmber()) {
					// TODO restrict the audits we query below to only those
					// that have problems
				}
			}
			
			List<ContractorAudit> currentAnnualUpdates = co.getContractorAccount().getCurrentAnnualUpdates();

			String where = "t.audit.contractorAccount.id = " + id + " AND t.visible = 1 "
					+ "AND t IN (SELECT cao FROM ContractorAuditOperatorPermission WHERE operator.id IN ("
					+ Strings.implode(operatorIds, ",") + "))";
			List<ContractorAuditOperator> list = (List<ContractorAuditOperator>) caoDAO.findWhere(
					ContractorAuditOperator.class, where, 0);
			missingAudits = ArrayListMultimap.create();

			Map<AuditType, FlagCriteria> auditTypeToFlagCriteria = getAuditTypeToFlagCriteria();
			for (ContractorAuditOperator cao : list) {
				if (!cao.getAudit().isExpired())
					if (cao.getAudit().getAuditType().isAnnualAddendum()) {
						if (currentAnnualUpdates.contains(cao.getAudit())) {
							addCaoToMissingAudits(cao, auditTypeToFlagCriteria);
						}
					} else {
						addCaoToMissingAudits(cao, auditTypeToFlagCriteria);
					}
			}
		}
		return missingAudits;
	}

	public Map<AuditType, FlagCriteria> getAuditTypeToFlagCriteria() {
		Map<AuditType, FlagCriteria> result = new LinkedHashMap<AuditType, FlagCriteria>();
		if (displayCorporate) {
			for (ContractorOperator co1 : getActiveOperators()) {
				for (FlagData fd : co1.getFlagDatas()) {
					if (fd.getCriteria().getAuditType() != null)
						result.put(fd.getCriteria().getAuditType(), fd.getCriteria());
				}
			}
		} else {
			for (FlagData fd : co.getFlagDatas()) {
				if (fd.getCriteria().getAuditType() != null)
					result.put(fd.getCriteria().getAuditType(), fd.getCriteria());
			}
		}

		return result;
	}

	public FlagCriteriaOperator getApplicableOperatorCriteria(FlagData fd) {
		List<FlagCriteriaOperator> fcos = new ArrayList<FlagCriteriaOperator>();

		OperatorAccount operator = (displayCorporate) ? fd.getOperator() : co.getOperatorAccount();

		for (FlagCriteriaOperator fco : operator.getFlagCriteriaInherited()) {
			if (fco.getCriteria().equals(fd.getCriteria())
					&& (fco.getFlag().equals(fd.getFlag()) || (isFlagDataOverride(fd, operator) != null))) {
				fcos.add(fco);
			}
		}

		if (fcos.size() == 1)
			return fcos.get(0);
		else if (fcos.size() > 1) {
			// Checking to see which fco triggers the flagging
			for (FlagCriteriaContractor fcc : contractor.getFlagCriteria()) {
				for (FlagCriteriaOperator fco : fcos) {
					FlagDataCalculator calc = new FlagDataCalculator(fcc, fco);
					for (FlagData d : calc.calculate()) {
						if (!d.getFlag().isGreen())
							return fco;
					}
				}
			}
		}

		return null;
	}

	public Map<FlagColor, Integer> getFlagCounts() {
		if (flagCounts == null) {
			flagCounts = new LinkedHashMap<FlagColor, Integer>();
			flagCounts.put(FlagColor.Red, 0);
			flagCounts.put(FlagColor.Amber, 0);
			flagCounts.put(FlagColor.Green, 0);

			for (ContractorOperator contractorOperator : getActiveOperators()) {
				flagCounts
						.put(contractorOperator.getFlagColor(), flagCounts.get(contractorOperator.getFlagColor()) + 1);
			}

			Iterator<Map.Entry<FlagColor, Integer>> iter = flagCounts.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<FlagColor, Integer> entry = iter.next();
				if (entry.getValue() <= 0)
					iter.remove();
			}
		}
		return flagCounts;
	}

	private boolean canFindOperator() {
		if (opID == 0 && (permissions.isOperator() || permissions.isCorporate()))
			opID = permissions.getAccountId();

		if (permissions.isCorporate() && permissions.getAccountId() == opID) {
			displayCorporate = true;
		}

		// can't view unrelated co links
		if (permissions.isCorporate()) {
			// check to see if corporate id pulls anything with this op id,
			// if not then give error
			if (opID != permissions.getAccountId()) {
				Facility f = facDAO.findByCorpOp(permissions.getAccountId(), opID);
				if (f == null) {
					addActionError(getText("ContractorFlag.error.NoPermissionViewFacility"));
					return false;
				}
			}
		} else if (permissions.isContractor()) {
			if (id != permissions.getAccountId()) {
				// check to see if con id and id match
				addActionError(getText("ContractorFlag.error.NoPermissionViewFacility"));
				return false;
			}
		}

		co = contractorOperatorDao.find(id, opID);
		if (co == null) {
			addActionError(getText("ContractorFlag.error.ContractorNotAtSite"));
			return false;
		}

		int i = 0;
		if (displayCorporate) {
			operatorIds = new int[getActiveOperators().size() + 1];
			for (ContractorOperator co1 : getActiveOperators()) {
				operatorIds[i++] = co1.getOperatorAccount().getId();
			}
		} else {
			operatorIds = new int[1];
		}
		operatorIds[i] = opID;

		if (permissions.hasPermission(OpPerms.EditForcedFlags)) {
			if (permissions.isCorporate() || permissions.isAdmin()) {
				permittedToForceFlags = true;
			} else if (permissions.isOperator() && permissions.getAccountId() == opID) {
				permittedToForceFlags = true;
			}
		}

		return true;
	}

	private String getAppropriateAnnualAuditYear(FlagCriteria criteria) {
		YearList yearList = new YearList();
		for (ContractorAudit conAudit : contractor.getSortedAnnualUpdates()) {
			if (conAudit.hasCaoStatus(AuditStatus.Complete)) {
				yearList.add(conAudit.getAuditFor());
			}
		}

		Integer year = yearList.getYearForScope(criteria.getMultiYearScope());

		return ((year == null) ? null : year.toString());
	}

	private String completeAction(String noteText) {
		try {
			if (!Strings.isEmpty(noteText)) {
				Note note = new Note();
				note.setAccount(co.getContractorAccount());
				note.setAuditColumns(permissions);
				note.setNoteCategory(NoteCategory.Flags);
				note.setViewableByOperator(permissions);
				note.setCanContractorView(true);
				note.setPriority(LowMedHigh.High);

				note.setSummary(noteText);

				if (!Strings.isEmpty(forceNote))
					note.setBody(forceNote);

				// Check if there's an attachment
				note = getNoteDao().save(note);
				if (!isFileOkay(note))
					return SUCCESS;

				// If everything's okay, now save
				getNoteDao().save(note);
			}
			co.setFlagLastUpdated(new Date());
			co.setAuditColumns(permissions);
			contractorOperatorDao.save(co);

			String redirectUrl = "ContractorFlag.action?id=" + id + "%26opID=" + opID;
			return setUrlForRedirect("ContractorCronAjax.action?conID=" + id + "&opID=" + opID + "&steps=Flag&steps=WaitingOn"
					+ "&redirectUrl=" + redirectUrl);
		} catch (Exception x) {
		}

		return SUCCESS;
	}

	private boolean isFileOkay(Note note) {
		if (file != null) {
			String extension = "";
			if (fileFileName.indexOf(".") != -1) {
				extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
			}

			// will fail for "" too
			if (!FileUtils.checkFileExtension(extension)) {
				addActionError(getText("ContractorFlag.error.FileTypeNotSuppoprted"));
				return false;
			}
			// delete old files
			File[] files = getFiles(note.getId());
			for (File file : files)
				FileUtils.deleteFile(file);

			try {
				FileUtils.moveFile(file, getFtpDir(), "files/" + FileUtils.thousandize(note.getId()),
						PICSFileType.note_attachment.filename(note.getId()), extension, true);
			} catch (Exception e) {
				return false;
			}

			note.setAttachment(fileFileName);
		}

		return true;
	}

	private File[] getFiles(int noteID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(noteID));
		return FileUtils.getSimilarFiles(dir, PICSFileType.note_attachment.filename(noteID));
	}

	private void addCaoToMissingAudits(ContractorAuditOperator cao, Map<AuditType, FlagCriteria> auditTypeToFlagCriteria) {
		AuditType auditType = cao.getAudit().getAuditType();

		if (auditTypeToFlagCriteria.get(auditType) == null
				|| auditTypeToFlagCriteria.get(auditType).getRequiredStatus() == cao.getStatus()) {
			return;
		}

		for (ContractorAuditOperator aCao : missingAudits.get(auditType)) {
			if (aCao.getAudit().getId() == cao.getAudit().getId()) {
				return;
			}
		}

		missingAudits.put(auditType, cao);
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
