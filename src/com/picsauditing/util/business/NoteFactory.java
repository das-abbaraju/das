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

}
