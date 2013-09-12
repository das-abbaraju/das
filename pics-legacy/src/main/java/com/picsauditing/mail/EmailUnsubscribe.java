package com.picsauditing.mail;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmailUnsubscribe extends PicsActionSupport {
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private EmailSubscriptionDAO emailSubscriptionDAO;
	@Autowired
	private NoteDAO noteDAO;

	private int id;
	private String sub;

	@Anonymous
	public String execute() {
		if (id == 0 || Strings.isEmpty(sub)) {
			addActionError("Cannot unsubscribe from " + sub
					+ " email. Please log in to your account and edit your email preferences.");
			return SUCCESS;
		}

		User user = userDAO.find(id);
		if (user != null) {
			List<EmailSubscription> emList = emailSubscriptionDAO.findByUserId(id);
			Iterator<EmailSubscription> eIterator = emList.iterator();
			while (eIterator.hasNext()) {
				EmailSubscription eSubscription = eIterator.next();
				if (eSubscription.getSubscription().toString().equals(sub)) {
					emailSubscriptionDAO.remove(eSubscription);
				}
			}

			String newNote = user.getName() + " unsubscribed from " + sub;
			Note note = new Note();
			note.setAccount(user.getAccount());
			note.setAuditColumns(permissions);
			note.setSummary(newNote);
			note.setNoteCategory(NoteCategory.Other);
			note.setViewableById(Account.EVERYONE);
			note.setPriority(LowMedHigh.Med);
			note.setCanContractorView(false);
			note.setStatus(NoteStatus.Closed);
			noteDAO.save(note);
		}

		return SUCCESS;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getSub() {
		return sub;
	}

}
