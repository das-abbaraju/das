package com.picsauditing.actions.operators;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagOshaCriteriaDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagOshaCriterion;
import com.picsauditing.jpa.entities.NoteCategory;

public class FlagOshaCriteriaAction extends OperatorActionSupport implements Preparable {

	private FlagOshaCriteriaDAO flagOshaCriteriaDAO;
	private ContractorAccountDAO contractorAccountDAO;
	protected FlagOshaCriteria redOshaCriteria = null;
	protected FlagOshaCriteria amberOshaCriteria = null;
	private int type = 0;
	private boolean lwcr;
	private boolean trir;
	private boolean fatalities;
	private boolean cad7;
	private boolean neer;
	private boolean dart;
	
	public FlagOshaCriteriaAction(OperatorAccountDAO operatorDao, FlagOshaCriteriaDAO flagOshaCriteriaDAO, ContractorAccountDAO contractorAccountDAO) {
		super(operatorDao);
		this.flagOshaCriteriaDAO = flagOshaCriteriaDAO;
		this.contractorAccountDAO = contractorAccountDAO;
		this.noteCategory = NoteCategory.Flags;
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
			else if(type == 3)
				fatalities = true;
			else if(type == 4)
				cad7 = true;
			else if(type == 5)
				neer = true;
			else if (type == 6)
				dart = true;
		}
	}
	@Override
	public String execute() throws Exception {
		if (button != null) {
			if ("save".equals(button)) {
				if (redOshaCriteria.getLwcr() != null 
						|| redOshaCriteria.getTrir() != null 
						|| redOshaCriteria.getFatalities() != null
						|| redOshaCriteria.getCad7() != null
						|| redOshaCriteria.getNeer() != null
						|| redOshaCriteria.getDart() != null) {
					if(redOshaCriteria.getId() == 0)
						redOshaCriteria.setOperatorAccount(operator);
					
					if(redOshaCriteria.getLwcr() == null)
						redOshaCriteria.setLwcr(new FlagOshaCriterion());
					if(redOshaCriteria.getTrir() == null)
						redOshaCriteria.setTrir(new FlagOshaCriterion());
					if(redOshaCriteria.getFatalities() == null)
						redOshaCriteria.setFatalities(new FlagOshaCriterion());
					if(redOshaCriteria.getCad7() == null)
						redOshaCriteria.setCad7(new FlagOshaCriterion());
					if(redOshaCriteria.getNeer() == null)
						redOshaCriteria.setNeer(new FlagOshaCriterion());
					if(redOshaCriteria.getDart() == null) {
						redOshaCriteria.setDart(new FlagOshaCriterion());
					}
					redOshaCriteria.setFlagColor(FlagColor.Red);
					redOshaCriteria.setAuditColumns(permissions);
					flagOshaCriteriaDAO.save(redOshaCriteria);
				}	
				if (amberOshaCriteria.getLwcr() != null || 
						amberOshaCriteria.getTrir() != null ||
						amberOshaCriteria.getFatalities() != null ||
						amberOshaCriteria.getCad7() != null ||
						amberOshaCriteria.getNeer() != null
						|| amberOshaCriteria.getDart() != null) {
					if(amberOshaCriteria.getId() == 0)
						amberOshaCriteria.setOperatorAccount(operator);
					
					if(amberOshaCriteria.getLwcr() == null)
						amberOshaCriteria.setLwcr(new FlagOshaCriterion());
					if(amberOshaCriteria.getTrir() == null)
						amberOshaCriteria.setTrir(new FlagOshaCriterion());
					if(amberOshaCriteria.getFatalities() == null)
						amberOshaCriteria.setFatalities(new FlagOshaCriterion());
					if(amberOshaCriteria.getCad7() == null)
						amberOshaCriteria.setCad7(new FlagOshaCriterion());
					if(amberOshaCriteria.getNeer() == null)
						amberOshaCriteria.setNeer(new FlagOshaCriterion());
					if(amberOshaCriteria.getDart() == null)
						amberOshaCriteria.setDart(new FlagOshaCriterion());
					
					amberOshaCriteria.setFlagColor(FlagColor.Amber);
					amberOshaCriteria.setAuditColumns(permissions);
					flagOshaCriteriaDAO.save(amberOshaCriteria);
				}
				String note = "Flag Criteria has been edited for " + operator.getOshaType();
				if(isLwcr())
					note += " LWCR";
				else if(isTrir())
					note += " TRIR";
				else if (isFatalities())
					note += " Fatalities";
				else if(isCad7())
					note += " Cad7";
				else if(isNeer())
					note += " Neer";
				else if (isDart())
					note += " DART";
				this.addNote(operator, note);
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
	
	public boolean isCad7() {
		return cad7;
	}

	public void setCad7(boolean cad7) {
		this.cad7 = cad7;
	}

	public boolean isNeer() {
		return neer;
	}

	public void setNeer(boolean neer) {
		this.neer = neer;
	}
	
	public boolean isDart() {
		return dart;
	}

	public void setDart(boolean dart) {
		this.dart = dart;
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
