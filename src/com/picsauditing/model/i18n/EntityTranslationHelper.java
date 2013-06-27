package com.picsauditing.model.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.jpa.entities.WorkflowState;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;

public class EntityTranslationHelper {

	private static final Logger logger = LoggerFactory.getLogger(EntityTranslationHelper.class);

	public static void saveRequiredTranslationsForWorkflowStep(WorkflowStep workflowStep, Permissions permissions) {
		try {
			processTranslationsForField(workflowStep, workflowStep.getName(), "name", permissions);
		} catch (Exception e) {
			logger.error("Error saving translations for WorkflowStep id {}", workflowStep.getId(), e);
		}
	}

	public static void saveRequiredTranslationsForWorkflowState(WorkflowState workflowState, Permissions permissions) {
		try {
			processTranslationsForField(workflowState, workflowState.getName(), "name", permissions);
		} catch (Exception e) {
			logger.error("Error saving translations for WorkflowState id {}", workflowState.getId(), e);
		}
	}

	public static void saveRequiredTranslationsForTrade(Trade trade, Permissions permissions) {
		try {
			processTranslationsForField(trade, trade.getName(), "name", permissions);
			processTranslationsForField(trade, trade.getName2(), "name2", permissions);
			processTranslationsForField(trade, trade.getHelp(), "help", permissions);
		} catch (Exception e) {
			logger.error("Error saving translations for trade id {}", trade.getId(), e);
		}
	}

	public static void saveRequiredTranslationsForAuditOptionValue(AuditOptionValue auditOptionValue,
			Permissions permissions) {
		try {
			processTranslationsForField(auditOptionValue, auditOptionValue.getName(), "name", permissions);
		} catch (Exception e) {
			logger.error("Error saving translations for audit option value id {}", auditOptionValue.getId(), e);
		}
	}

	public static void saveRequiredTranslationsForFlagCriteria(FlagCriteria flagCriteria, Permissions permissions) {
		try {
			processTranslationsForField(flagCriteria, flagCriteria.getLabel(), "label", permissions);
			processTranslationsForField(flagCriteria, flagCriteria.getDescription(), "description", permissions);
		} catch (Exception e) {
			logger.error("Error saving translations for flag criteria id {}", flagCriteria.getId(), e);
		}
	}

	// we can use the same logic for new/update - save the language the user is
	// currently using and any required languages for which there is no extant
	// key
	public static void saveRequiredTranslationsForAuditTypeName(AuditType auditType, Permissions permissions) {
		try {
			List<String> localesToSave = findAdditonalLanguagesForTranslations(auditType, "name", permissions,
					auditType.getLanguages());
			saveTranslations(auditType, "name", auditType.getName(), localesToSave);
		} catch (Exception e) {
			logger.error("Error saving translations for audit type id = {}", auditType.getId(), e);
		}
	}

	public static void saveRequiredTranslationsForAuditCategory(AuditCategory auditCategory, Permissions permissions) {
		try {
			processTranslationsForField(auditCategory, auditCategory.getName(), "name", permissions);
			processTranslationsForField(auditCategory, auditCategory.getHelpText(), "helpText", permissions);
		} catch (Exception e) {
			logger.error("Error saving translations for audit category id = {}", auditCategory.getId(), e);
		}
	}

	public static void saveRequiredTranslationsForAuditQuestion(AuditQuestion question, Permissions permissions) {
		try {
			processTranslationsForField(question, question.getName(), "name", permissions);
			processTranslationsForField(question, question.getTitle(), "title", permissions);
			processTranslationsForField(question, question.getColumnHeader(), "columnHeader", permissions);
			processTranslationsForField(question, question.getHelpText(), "helpText", permissions);
			processTranslationsForField(question, question.getRequirement(), "requirement", permissions);
		} catch (Exception e) {
			logger.error("Error saving translations for audit question id = {}", question.getId(), e);
		}
	}

	private static void processTranslationsForField(Translatable translatable, String translation, String property,
			Permissions permissions) throws Exception {
		List<String> localesToSave = findAdditonalLanguagesForTranslations(translatable, property, permissions, null);
		saveTranslations(translatable, property, translation, localesToSave);
	}

	private static void saveTranslations(Translatable translatable, String property, String translation,
			List<String> requiredLanguages) throws Exception {
		TranslationServiceFactory.getTranslationService().saveTranslation(translatable.getI18nKey(property),
				translation, requiredLanguages);
	}

	private static List<String> findAdditonalLanguagesForTranslations(Translatable translatable, String property,
			Permissions permissions, List<String> languages) {
		if (translatable == null || permissions == null || Strings.isEmpty(property) || permissions.getLocale() == null) {
			return Collections.emptyList();
		}

		if (CollectionUtils.isEmpty(languages)) {
			return Arrays.asList(permissions.getLocale().getLanguage());
		}

		List<String> localesToSave = new ArrayList<>();
		localesToSave.add(permissions.getLocale().getLanguage());

		TranslationService translationService = TranslationServiceFactory.getTranslationService();
		for (String language : languages) {
			if (!translationService.hasKeyInLocale(translatable.getI18nKey(property), language)) {
				localesToSave.add(language);
			}
		}

		return localesToSave;
	}

}
