package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportExpiredCreditCards extends ReportAccount {

	public String[] sendMail;

	public void buildQuery() {
		super.buildQuery();

		sql.addWhere("c.paymentMethod = 'CreditCard'");
		sql.addWhere("c.ccExpiration < NOW()");

		sql.addOrderBy("c.paymentExpires");
		sql.addOrderBy("c.ccExpiration");

		sql.addField("c.ccExpiration");
		sql.addField("c.paymentExpires");
		sql.addField("c.balance");

		getFilter().setShowTradeInformation(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowConWithPendingAudits(false);
	}

	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}

	@Override
	public String execute() throws Exception {
		if ("SendEmail".equals(button)) {
			if (sendMail.length > 0) {
				StringBuffer sb = new StringBuffer("Contractors who should be emailed");
				for (String conID : sendMail) {
					sb.append(" - ").append(conID);
				}
				addActionMessage(sb.toString());
			}
		}
		return super.execute();
	}

	public String[] getSendMail() {
		return sendMail;
	}

	public void setSendMail(String[] sendMail) {
		this.sendMail = sendMail;
	}

}
