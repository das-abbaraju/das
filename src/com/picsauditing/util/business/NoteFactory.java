package com.picsauditing.util.business;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.SpringUtils;

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
		
		String noteMessage = I18nCache.getInstance().getText(translationKey, Locale.US, permissions.getName(), 
				permissions.getAccountName(), contractorTag.getUpdateDate());
		
		Note note = new Note();		
		note.setAuditColumns(permissions);
		note.setAccount(contractorTag.getContractor());
		note.setNoteCategory(NoteCategory.OperatorChanges);
		note.setSummary(noteMessage);
		note.setViewableBy(lookupOperatorAccount(contractorTag.getTag().getId()));
		
		return note;
	}
	
	private static OperatorAccount lookupOperatorAccount(int operatorTagId) {
		try {
			OperatorTagDAO operatorTagDAO = SpringUtils.getBean("OperatorTagDAO");
			OperatorTag operatorTag = operatorTagDAO.find(operatorTagId);
			return operatorTag.getOperator();
		} catch (Exception e) {
			logger.error("Error occurred while looking up OperatorAccount with OperatorTagId = {}", operatorTagId, e);
		}
		
		return null;
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

}
