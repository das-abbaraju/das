package com.picsauditing.actions.operators;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagOshaCriteriaDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagOshaCriterion;

public class FlagOshaCriteriaAction extends OperatorActionSupport implements Preparable {

	private FlagOshaCriteriaDAO flagOshaCriteriaDAO;
	private ContractorAccountDAO contractorAccountDAO;
	protected FlagOshaCriteria redOshaCriteria = null;
	protected FlagOshaCriteria amberOshaCriteria = null;
	private int type;
	private boolean lwcr;
	private boolean trir;
	private boolean fatalities;

	public FlagOshaCriteriaAction(OperatorAccountDAO operatorDao, FlagOshaCriteriaDAO flagOshaCriteriaDAO, ContractorAccountDAO contractorAccountDAO) {
		super(operatorDao);
		this.flagOshaCriteriaDAO = flagOshaCriteriaDAO;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	@Override
	public void prepare() throws Exception {
		findOperator();
		type = getParameter("type");
		if (type > 0) {
			if (type == 1)
				lwcr = true;
			else if (type == 2)
				trir = true;
			else
				fatalities = true;
		}
	}

	@Override
	public String execute() throws Exception {
		if (button != null) {
			if ("save".equals(button)) {
				if (redOshaCriteria.getLwcr() != null 
						|| redOshaCriteria.getTrir() != null 
						|| redOshaCriteria.getFatalities() != null) {
					if(redOshaCriteria.getId() == 0)
						redOshaCriteria.setOperatorAccount(operator);
					
					if(redOshaCriteria.getLwcr() == null)
						redOshaCriteria.setLwcr(new FlagOshaCriterion());
					if(redOshaCriteria.getTrir() == null)
						redOshaCriteria.setTrir(new FlagOshaCriterion());
					if(redOshaCriteria.getFatalities() == null)
						redOshaCriteria.setFatalities(new FlagOshaCriterion());
					redOshaCriteria.setFlagColor(FlagColor.Red);
					redOshaCriteria.setAuditColumns(permissions);
					flagOshaCriteriaDAO.save(redOshaCriteria);
				}	
				if (amberOshaCriteria.getLwcr() != null || 
						amberOshaCriteria.getTrir() != null ||
						amberOshaCriteria.getFatalities() != null) {
					if(amberOshaCriteria.getId() == 0)
						amberOshaCriteria.setOperatorAccount(operator);
					
					if(amberOshaCriteria.getLwcr() == null)
						amberOshaCriteria.setLwcr(new FlagOshaCriterion());
					if(amberOshaCriteria.getTrir() == null)
						amberOshaCriteria.setTrir(new FlagOshaCriterion());
					if(amberOshaCriteria.getFatalities() == null)
						amberOshaCriteria.setFatalities(new FlagOshaCriterion());
					amberOshaCriteria.setFlagColor(FlagColor.Amber);
					amberOshaCriteria.setAuditColumns(permissions);
					flagOshaCriteriaDAO.save(amberOshaCriteria);
				}	
				contractorAccountDAO.updateContractorByOperator(operator);
				return BLANK;
			}
		}
		return SUCCESS;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isLwcr() {
		return lwcr;
	}

	public boolean isTrir() {
		return trir;
	}

	public boolean isFatalities() {
		return fatalities;
	}

	public void setLwcr(boolean lwcr) {
		this.lwcr = lwcr;
	}

	public void setTrir(boolean trir) {
		this.trir = trir;
	}

	public void setFatalities(boolean fatalities) {
		this.fatalities = fatalities;
	}

	public FlagOshaCriteria getAmberOshaCriteria() {
		if(amberOshaCriteria == null)
			amberOshaCriteria = flagOshaCriteriaDAO.findByOperatorFlag(operator, "flagColor = 'Amber'");

		return amberOshaCriteria;
	}

	public void setAmberOshaCriteria(FlagOshaCriteria amberOshaCriteria) {
		this.amberOshaCriteria = amberOshaCriteria;
	}

	public FlagOshaCriteriaDAO getFlagOshaCriteriaDAO() {
		return flagOshaCriteriaDAO;
	}

	public void setFlagOshaCriteriaDAO(FlagOshaCriteriaDAO flagOshaCriteriaDAO) {
		this.flagOshaCriteriaDAO = flagOshaCriteriaDAO;
	}

	public FlagOshaCriteria getRedOshaCriteria() {
		if(redOshaCriteria == null)
			redOshaCriteria = flagOshaCriteriaDAO.findByOperatorFlag(operator, "flagColor = 'Red'");

		return redOshaCriteria;
	}

	public void setRedOshaCriteria(FlagOshaCriteria redOshaCriteria) {
		this.redOshaCriteria = redOshaCriteria;
	}
}
