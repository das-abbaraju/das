package com.picsauditing.actions.report;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.util.EmailAddressUtils;

@SuppressWarnings("serial")
public class ReportExpiredCreditCards extends ReportAccount {

	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private EmailBuilder emailBuilder;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private EmailSender emailSender;

	private String[] sendMail;

	public ReportExpiredCreditCards(ContractorAccountDAO contractorAccountDAO, EmailBuilder emailBuilder,
			NoteDAO noteDAO) {
		sql = new SelectContractorAudit();
		this.contractorAccountDAO = contractorAccountDAO;
		this.emailBuilder = emailBuilder;
		this.noteDAO = noteDAO;

		orderByDefault = "c.balance DESC, c.paymentExpires DESC, c.ccExpiration";
	}

	public void buildQuery() {
		super.buildQuery();

		sql.addJoin("LEFT JOIN email_queue eq ON c.id = eq.conID AND eq.templateID = 59");

		sql.addWhere("c.paymentMethod = 'CreditCard'");
		sql.addWhere("c.ccExpiration < NOW()");
		sql.addWhere("c.ccOnFile = 1");
		sql.addWhere("eq.creationDate IS NULL");

		sql.addGroupBy("c.id");
		sql.addGroupBy("eq.templateID");

		sql.addField("c.ccExpiration");
		sql.addField("c.paymentExpires");
		sql.addField("c.balance");
		sql.addField("MAX(eq.creationDate) lastSent");

		getFilter().setShowTradeInformation(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowCcOnFile(false);
	}

	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}

	@Override
	public String execute() throws Exception {
		if ("Send Email".equals(button)) {
			if (sendMail == null) {
				addActionMessage("Unable to send email - no contractors were selected.");
			} else if (sendMail.length > 0) {
				for (String conIDString : sendMail) {
					try {
						int conID = Integer.parseInt(conIDString);
						ContractorAccount con = contractorAccountDAO.find(conID);
						emailBuilder.setTemplate(59);
						emailBuilder.setPermissions(permissions);
						emailBuilder.setContractor(con, OpPerms.ContractorBilling);
						emailBuilder.setFromAddress(EmailAddressUtils.getBillingEmail(con.getCurrency()));
						EmailQueue email = emailBuilder.build();
						email.setSubjectViewableById(Account.PicsID);
						email.setBodyViewableById(Account.PicsID);
						emailSender.send(email);

						Note note = new Note();
						note.setAccount(con);
						note.setAuditColumns(permissions);
						note.setSummary("Expired Credit Card email sent to " + email.getToAddresses());
						note.setNoteCategory(NoteCategory.Billing);
						note.setViewableById(Account.PicsID);
						noteDAO.save(note);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
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
