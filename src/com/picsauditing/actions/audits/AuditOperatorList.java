package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.operators.OperatorActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.AuditTypeCache;

public class AuditOperatorList extends OperatorActionSupport {

	private static final long serialVersionUID = -618269908092576272L;

	protected int oID;
	protected int aID = 1;

	protected List<OperatorAccount> operators;
	protected List<AuditType> auditTypes;
	private AuditTypeDAO auditDAO;
	private AuditOperatorDAO dataDAO;
	private List<AuditOperator> data;

	public AuditOperatorList(OperatorAccountDAO operatorDao, AuditTypeDAO auditDAO, AuditOperatorDAO dataDAO) {
		super(operatorDao);
		this.auditDAO = auditDAO;
		this.dataDAO = dataDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.ManageOperators, OpType.Edit);

		operators = operatorDao.findWhere(true, "", permissions);
		if (oID > 0) {
			super.id = oID;
			findOperator();
		}

		auditTypes = new AuditTypeCache(auditDAO).getAuditTypes();

		AuditType selectedAudit = null;

		// You have to use while iterator instead of a for loop
		// because we're removing an entry from the list
		Iterator<AuditType> iter = auditTypes.iterator();
		while (iter.hasNext()) {
			AuditType audit = iter.next();
			if (audit.getId() == AuditType.NCMS)
				iter.remove();
			if (audit.getId() == aID)
				selectedAudit = audit;
		}

		data = new ArrayList<AuditOperator>();
		HashMap<Integer, AuditOperator> rawDataIndexed = new HashMap<Integer, AuditOperator>();
		if (aID > 0) {
			if (selectedAudit == null) {
				this.addActionError("No auditType found with ID " + aID);
				return SUCCESS;
			}
			AuditType auditType = auditDAO.find(aID);
			// Query the db for operators using this audit and index them by
			// opID
			List<AuditOperator> rawData = dataDAO.findByAudit(aID);
			for (AuditOperator row : rawData) {
				rawDataIndexed.put(row.getOperatorAccount().getId(), row);
			}
			rawData = null; // we don't need this anymore
			List<OperatorAccount> criteriaOperators = new ArrayList<OperatorAccount>();
			if (auditType.getClassType().isPolicy()) {
				criteriaOperators.addAll(operatorDao.findInheritOperators("a.inheritInsurance"));
			} else {
				criteriaOperators.addAll(operatorDao.findInheritOperators("a.inheritAudits"));
			}

			for (OperatorAccount operator : criteriaOperators) {
				AuditOperator newRow = rawDataIndexed.get(operator.getId());
				if (newRow == null) {
					newRow = new AuditOperator();
					newRow.setOperatorAccount(operator);
					newRow.setAuditType(selectedAudit);
				}
				data.add(newRow);
			}
		}
		if (oID > 0) {
			// Query the db for audits used by this operator and index them by
			// typeID
			OperatorAccount selectedObject = null;
			for (OperatorAccount operator : operators) {
				if (operator.getId() == oID)
					selectedObject = operator;
			}
			if (selectedObject == null) {
				this.addActionError("No operator found with ID " + oID);
				return SUCCESS;
			}

			List<AuditOperator> rawData = dataDAO.findByOperator(oID);
			for (AuditOperator row : rawData) {
				rawDataIndexed.put(row.getAuditType().getId(), row);
			}
			rawData = null; // we don't need this anymore
			List<AuditType> ownedAuditTypes = new ArrayList<AuditType>();
			for (AuditType auditType : auditTypes) {
				if (auditType.getClassType().isPolicy()) {
					if (operator.getInheritInsurance().equals(operator))
						ownedAuditTypes.add(auditType);
				} else {
					if (operator.getInheritAudits().equals(operator))
						ownedAuditTypes.add(auditType);
				}
			}
			// AuditOperator temp = rawData.get(0);
			for (AuditType aType : ownedAuditTypes) {
				AuditOperator newRow = rawDataIndexed.get(aType.getId());
				if (newRow == null) {
					newRow = new AuditOperator();
					newRow.setOperatorAccount(selectedObject);
					newRow.setAuditType(aType);
				}
				data.add(newRow);
			}
		}

		return SUCCESS;
	}

	public List<OperatorAccount> getOperators() {
		return operators;
	}

	public List<AuditOperator> getData() {
		return data;
	}

	public int getOID() {
		return oID;
	}

	public void setOID(int oid) {
		aID = 0;
		oID = oid;
	}

	public int getAID() {
		return aID;
	}

	public void setAID(int aid) {
		oID = 0;
		aID = aid;
	}

	public String getOName() {
		if (this.oID > 0)
			for (Account row : this.operators)
				if (this.oID == row.getId())
					return row.getName();
		return "";
	}

	public String getAName() {
		if (this.aID > 0)
			for (AuditType row : this.auditTypes)
				if (this.aID == row.getId())
					return row.getAuditName();
		return "";
	}

	public String getClassName() {
		if (this.aID > 0)
			for (AuditType row : this.auditTypes)
				if (this.aID == row.getId())
					return row.getClassType().toString();
		return "";
	}

	public List<AuditType> getAuditTypes() {
		return auditTypes;
	}

	public Map<Integer, LowMedHigh> getRiskLevelList() {
		return LowMedHigh.getMap();
	}

	public FlagColor[] getFlagColorList() {
		return FlagColor.values();
	}
}
