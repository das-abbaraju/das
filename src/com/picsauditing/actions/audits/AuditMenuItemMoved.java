package com.picsauditing.actions.audits;

/**
 * Created with IntelliJ IDEA.
 * User: KChase
 * Date: 3/20/13
 * Time: 7:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class AuditMenuItemMoved extends AuditActionSupport {
	public String execute() throws Exception {
		showCaoTable = false;
		this.findConAudit();
		return SUCCESS;
	}
}
