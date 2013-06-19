package com.picsauditing.models.audits;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Translatable;

public class TranslationKeysGenerator {

	private Date today = new Date();

	private static final String DOT_NAME = ".name";

	public Set<String> generateCategoryKeys(AuditCategory category) {
		TranslationKeySet translationKeys = new TranslationKeySet();

		generateCategoryKeys(category, translationKeys);

		return translationKeys.getTranslationKeySet();
	}

	private void generateCategoryKeys(AuditCategory category, TranslationKeySet translationKeys) {
		translationKeys.addTranslatableString(category, "name");

		for (AuditQuestion question : category.getQuestions()) {
			generateAuditQuestionTranslationKeys(question, translationKeys);
		}

		for (AuditCategory subCategory : category.getSubCategories()) {
			generateCategoryKeys(subCategory, translationKeys);
		}
	}

	public Set<String> generateAuditTypeKeys(AuditType auditType) {
		TranslationKeySet translationKeys = new TranslationKeySet();
		translationKeys.addTranslatableString(auditType, "name");

		for (AuditCategory category : auditType.getCategories()) {
			translationKeys.addTranslatableString(category, "name");
			for (AuditQuestion question : category.getQuestions()) {
				generateAuditQuestionTranslationKeys(question, translationKeys);
			}
		}

		return translationKeys.getTranslationKeySet();
	}

	public Set<String> generateAuditQuestionTranslationKeys(AuditQuestion question) {
		TranslationKeySet translationKeySet = new TranslationKeySet();
		generateAuditQuestionTranslationKeys(question, translationKeySet);
		return translationKeySet.getTranslationKeySet();
	}

	private void generateAuditQuestionTranslationKeys(AuditQuestion question, TranslationKeySet translationKeys) {
		if (question.isValidQuestion(today)) {
			translationKeys.addTranslatableString(question, "name");
			translationKeys.addTranslatableString(question, "title");
			translationKeys.addTranslatableString(question, "requirement");
			translationKeys.addTranslatableString(question, "columnHeader");
			translationKeys.addTranslatableString(question, "helpText");

			// translationKeys.addTranslatableString(question.getName());
			// translationKeys.addTranslatableString(question.getTitle());
			// translationKeys.addTranslatableString(question.getRequirement());
			// translationKeys.addTranslatableString(question.getColumnHeader());
			// translationKeys.addTranslatableString(question.getHelpText());
		}
	}

	private class TranslationKeySet {
		private Set<String> translationKeySet = new HashSet<String>();

		public TranslationKeySet() {
			translationKeySet = new HashSet<String>();
		}

		public void addKey(String translationKey) {
			translationKeySet.add(translationKey);
		}

		public void addTranslatableString(Translatable translatable, String... property) {
			if (property != null && property.length > 1) {
				throw new IllegalArgumentException("Only provide one or zero properties.");
			}

			if (property == null) {
				translationKeySet.add(translatable.getI18nKey());
			} else {
				translationKeySet.add(translatable.getI18nKey(property[0]));
			}

			// String translationKey = translatableString != null ?
			// translatableString.getKey() : null;
			// if (translationKey != null) {
			// translationKeySet.add(translationKey);
			// }
		}

		public Set<String> getTranslationKeySet() {
			return translationKeySet;
		}
	}
}
