package com.picsauditing.actions.auditType;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;

public class ManageAuditType extends PicsActionSupport implements Preparable {

	protected AuditTypeDAO auditTypeDao = null;
	protected int id = 0;
	protected AuditType auditType = null;
	
	protected Map<Integer, Integer> orders = null;
	

	public ManageAuditType(AuditTypeDAO auditTypeDAO) {
		this.auditTypeDao = auditTypeDAO;
	}

	public String execute() {
		return SUCCESS;
	}


	protected void load(int id) {
		if (id != 0) {
			load(auditTypeDao.find(id));
		}
	}
	
	protected void load( AuditType newType ) {
		this.auditType = newType;
	}

	
	@Override
	public void prepare() throws Exception {

		String[] ids = (String[]) ActionContext.getContext().getParameters()
				.get("id");

		if (ids != null && ids.length != 0) {
			
			int thisId = Integer.parseInt( ids[0] ); 
			load( thisId );
		}
	}

	public Map<Integer, Integer> getOrders() {
		return orders;
	}

	public void setOrders(Map<Integer, Integer> orders) {
		this.orders = orders;
	}


	public String save() {

		if( auditType != null ) {
			auditTypeDao.save(auditType);
		}
		return SUCCESS;
	}

	
	

}
