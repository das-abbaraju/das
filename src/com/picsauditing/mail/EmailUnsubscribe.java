package com.picsauditing.mail;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

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
public class EmailUnsubscribe extends PicsActionSupport implements ServletRequestAware {
	protected UserDAO userDAO;
	protected EmailSubscriptionDAO emailSubscriptionDAO;
	protected NoteDAO noteDAO;
	
	protected HttpServletRequest request;

	public EmailUnsubscribe(UserDAO userDAO, EmailSubscriptionDAO emailSubscriptionDAO, NoteDAO noteDAO) {
		this.userDAO = userDAO;
		this.emailSubscriptionDAO = emailSubscriptionDAO;
		this.noteDAO = noteDAO;
	}

	public String execute() {
		String userIDstring = request.getParameter("id");
		String subString = request.getParameter("sub");
		if (Strings.isEmpty(userIDstring) || Strings.isEmpty(subString)) {
			addActionError("Cannot unsubscribe from " + subString +" email. Please log in to your account and edit your email preferences.");
			return SUCCESS;
		}
		
		int userID = Integer.parseInt(userIDstring);
		User user = userDAO.find(userID);
		if (user != null) {
			List<EmailSubscription> emList = emailSubscriptionDAO.findByUserId(userID);
			Iterator<EmailSubscription> eIterator = emList.iterator();
			while(eIterator.hasNext()) {
				EmailSubscription eSubscription = eIterator.next();
				if(eSubscription.getSubscription().toString().equals(subString)) {
					emailSubscriptionDAO.remove(eSubscription);
				}
			}
			
			String newNote = user.getName() + " unsubscribed from " + subString;
			Note note = new Note();
			note.setAccount(user.getAccount());
			note.setAuditColumns(user);
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

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setId(String id) {
	}

	public void setSub(String type) {
	}
}
