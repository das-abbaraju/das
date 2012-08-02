package com.picsauditing.util.business;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

/**
 * This class utilizes static factory methods to build notes anywhere in the application
 * where notes are generated from entity/model objects.
 * 
 * Please move this to a more appropriate package as needed.
 */
public class NoteFactory {
	
	private static final String ADD_TAG_TO_CONTRACTOR_TRANSLATION_KEY = "WhoIs.TaggedBy";
	private static final String REMOVE_TAG_FROM_CONTRACTOR_TRANSLATION_KEY = "WhoIs.UntaggedBy";
	
	private static final Logger logger = LoggerFactory.getLogger(NoteFactory.class);
	
	/**
	 * Private constructor to enforce singleton nature of this class 
	 */
	private NoteFactory() { }
	
	public static Note generateNoteForInvoiceUpdate(Invoice toUpdate, Invoice updateWith, User user) {
//		BigDecimal oldTotal = toUpdate.getTotalAmount();
//		Currency oldCurrency = toUpdate.getCurrency();
//		addNote(toUpdate.getAccount(), "Updated invoice " + toUpdate.getId() + " from " + oldTotal + oldCurrency
//				+ " to " + updateWith.getTotalAmount() + updateWith.getCurrency(), NoteCategory.Billing,
//				LowMedHigh.Med, false, Account.PicsID, user);
		
		StringBuilder noteSummary = new StringBuilder();
		noteSummary.append("Updated invoice ").append(toUpdate.getId()).append(" from ")
				.append(toUpdate.getTotalAmount()).append(toUpdate.getCurrency()).append(" to ")
				.append(updateWith.getTotalAmount()).append(updateWith.getCurrency());
		
		Note note = new Note();
		note.setAuditColumns(user);
		note.setAccount(toUpdate.getAccount());
		note.setSummary(noteSummary.toString());
		note.setPriority(LowMedHigh.Med);
		note.setNoteCategory(NoteCategory.Audits);
		note.setViewableById(Account.EVERYONE);
		note.setCanContractorView(true);
		note.setStatus(NoteStatus.Closed);
		return note;
	}
	
	public static Note generateNoteForImportPQF(ContractorAccount contractor, Permissions permissions) {
		Note note = new Note();		
		note.setAuditColumns(permissions);
		note.setAccount(contractor);
		note.setSummary("Import PQF option selected.");
		note.setPriority(LowMedHigh.Med);
		note.setNoteCategory(NoteCategory.Audits);
		note.setViewableById(Account.EVERYONE);
		note.setCanContractorView(true);
		note.setStatus(NoteStatus.Closed);		
		
		return note;
	}
	
	public static Note generateNoteForBillingDetail(Invoice invoice, String subject, User user) {
//		Note note = new Note(invoice.getAccount(), user, subject);
//		note.setNoteCategory(NoteCategory.Billing);
//		note.setCanContractorView(true);
//		note.setViewableById(Account.PicsID);
//		return note;
		return generateNoteForBillingDetail(invoice, subject, null, user);
	}
	
	public static Note generateNoteForBillingDetail(Invoice invoice, String subject, String body, User user) {
		Note note = new Note(invoice.getAccount(), user, subject);
		note.setBody(body);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		return note;
//		noteDAO.save(note);
	}
	
	public static Note generateNoteForTaggingContractor(ContractorTag contractorTag, Permissions permissions) {
		return buildNoteForContractorTagging(contractorTag, permissions, ADD_TAG_TO_CONTRACTOR_TRANSLATION_KEY);
	}		
	
	public static Note generateNoteForRemovingTagFromContractor(int tagId, Permissions permissions) {
		ContractorTag contractorTag = lookupContractorTag(tagId);
		return buildNoteForContractorTagging(contractorTag, permissions, REMOVE_TAG_FROM_CONTRACTOR_TRANSLATION_KEY);
	}
	
	private static Note buildNoteForContractorTagging(ContractorTag contractorTag, Permissions permissions, String translationKey) {
		if (contractorTag == null) {
			throw new IllegalArgumentException("You must pass in a valid contractor tag.");
		}
		
		Note note = new Note();		
		note.setAuditColumns(permissions);
		note.setAccount(contractorTag.getContractor());
		note.setNoteCategory(NoteCategory.OperatorChanges);
		note.setSummary(buildNoteMessageForContractorTagging(contractorTag, permissions, translationKey).trim());
		note.setViewableBy(lookupOperatorAccount(contractorTag.getTag().getId()));
		
		return note;
	}
	
	private static String buildNoteMessageForContractorTagging(ContractorTag contractorTag, Permissions permissions, String translationKey) {
		OperatorTag operatorTag = lookupOperatorTag(contractorTag.getTag().getId());		
		return messageTagPrefix(operatorTag) + I18nCache.getInstance().getText(translationKey, Locale.US, permissions.getName(), 
				permissions.getAccountName(), contractorTag.getUpdateDate());
	}
	
	private static String messageTagPrefix(OperatorTag operatorTag) {
		String prefix = "";
		if (operatorTag != null) {
			prefix = operatorTag.getTag();
		}
		
		if (!Strings.isEmpty(prefix)) {
			prefix = "(Tag: " + prefix + ") ";
		}
		
		return prefix;
	}
	
	private static OperatorAccount lookupOperatorAccount(int operatorTagId) {
		OperatorTag operatorTag = lookupOperatorTag(operatorTagId);
		if (operatorTag == null) {
			return null;
		}
		
		return operatorTag.getOperator();
	}
	
	private static ContractorTag lookupContractorTag(int contractorTagId) {
		try {
			ContractorTagDAO contractorTagDAO = SpringUtils.getBean("ContractorTagDAO");
			return contractorTagDAO.find(contractorTagId);
		} catch (Exception e) {
			logger.error("Error occurred while looking up ContractorTag with id = {}", contractorTagId, e);
		}
		
		return null;		
	}
	
	private static OperatorTag lookupOperatorTag(int operatorTagId) {
		try {
			OperatorTagDAO operatorTagDAO = SpringUtils.getBean("OperatorTagDAO");
			return operatorTagDAO.find(operatorTagId);
		} catch (Exception e) {
			logger.error("Error occurred while looking up OperatorTag with id = {}", operatorTagId, e);
		}
		
		return null;
	}
	
	/**
	 * This may or may not be better suited to be here.
	 */
	private static class NoteBuilder {
		
		private Account account;
		private String summary;
		private LowMedHigh priority;
		private NoteCategory noteCategory;
		private int viewableById;
		private boolean canContractorView;
		private NoteStatus noteStatus;
		private Employee employee;
		private User auditColumns;
		
		public NoteBuilder setAccount(Account account) {
			this.account = account;
			return this;
		}

		public NoteBuilder setSummary(String summary) {
			this.summary = summary;
			return this;
		}

		public NoteBuilder setPriority(LowMedHigh priority) {
			this.priority = priority;
			return this;
		}

		public NoteBuilder setNoteCategory(NoteCategory noteCategory) {
			this.noteCategory = noteCategory;
			return this;
		}

		public NoteBuilder setViewableById(int viewableById) {
			this.viewableById = viewableById;
			return this;
		}

		public NoteBuilder setCanContractorView(boolean canContractorView) {
			this.canContractorView = canContractorView;
			return this;
		}

		public NoteBuilder setNoteStatus(NoteStatus noteStatus) {
			this.noteStatus = noteStatus;
			return this;
		}
		
		public NoteBuilder setEmployee(Employee employee) {
			this.employee = employee;
			return this;
		}

		public NoteBuilder setAuditColumns(User auditColumns) {
			this.auditColumns = auditColumns;
			return this;
		}

		public Note build() {
			Note note = new Note();
			note.setAuditColumns(auditColumns);
			note.setAccount(account);
			note.setSummary(summary);
			note.setPriority(priority);
			note.setNoteCategory(noteCategory);
			note.setViewableById(viewableById);
			note.setCanContractorView(canContractorView);
			note.setStatus(noteStatus);
			note.setEmployee(employee);
			
			return note;
		}
		
	}

}
