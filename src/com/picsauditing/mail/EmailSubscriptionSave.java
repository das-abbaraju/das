package com.picsauditing.mail;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.*;

@SuppressWarnings("serial")
public class EmailSubscriptionSave extends PicsActionSupport {
	protected EmailSubscription eu = null;
	protected EmailSubscriptionDAO emailSubscriptionDAO;
	protected boolean addsubscription = false;
	protected SubscriptionTimePeriod sPeriod = null;

	public EmailSubscriptionSave(EmailSubscriptionDAO emailSubscriptionDAO) {
		this.emailSubscriptionDAO = emailSubscriptionDAO;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;
		if (eu.getId() == 0)
			eu.setUser(new User(permissions.getUserId()));

		eu.setAuditColumns(permissions);
		if (addsubscription) {
			if (sPeriod == null) {
				SubscriptionTimePeriod[] sPeriods = eu.getSubscription().getSupportedTimePeriods();
				if (sPeriods.length == 2)
					eu.setTimePeriod(sPeriods[1]);
				else
					eu.setTimePeriod(SubscriptionTimePeriod.Weekly);
			} else
				eu.setTimePeriod(sPeriod);
		}
		eu = emailSubscriptionDAO.save(eu);

		User user = dao.find(User.class, permissions.getUserId());

		String note = user.getName()
				+ " changed his " + eu.getSubscription().toString() + " email subscription to " + eu.getTimePeriod();

		addNoteToAccount(user.getAccount(),
				note,
				NoteCategory.General,
				LowMedHigh.Med,
				true,
				1);

		return SUCCESS;
	}

	public void addNoteToAccount(Account account, String newNote, NoteCategory category, LowMedHigh priority,
		boolean canContractorView, int viewableBy) {
		Note note = new Note();
		note.setAccount(account);
		note.setAuditColumns(permissions);
		note.setSummary(newNote);
		note.setPriority(priority);
		note.setNoteCategory(category);
		note.setViewableById(viewableBy);
		note.setCanContractorView(canContractorView);
		note.setStatus(NoteStatus.Closed);
		noteDao.save(note);
		return;
	}


	public SubscriptionTimePeriod getSPeriod() {
		return sPeriod;
	}

	public void setSPeriod(SubscriptionTimePeriod period) {
		sPeriod = period;
	}

	public EmailSubscription getEu() {
		return eu;
	}

	public void setEu(EmailSubscription eu) {
		this.eu = eu;
	}

	public void prepare() throws Exception {
		int id = this.getParameter("eu.id");
		eu = emailSubscriptionDAO.find(id);
	}

	public boolean isAddsubscription() {
		return addsubscription;
	}

	public void setAddsubscription(boolean addsubscription) {
		this.addsubscription = addsubscription;
	}
}
