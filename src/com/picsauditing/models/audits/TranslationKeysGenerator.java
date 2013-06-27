package com.picsauditing.models.audits;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;

public class TranslationKeysGenerator {

	private Date today = new Date();

	public Set<String> generateCategoryKeys(AuditCategory category) {
		if (category == null) {
			return Collections.emptySet();
		}

		Set<String> translationKeys = new HashSet<>();
		generateCategoryKeys(category, translationKeys);
		return translationKeys;
	}

	private void generateCategoryKeys(AuditCategory category, Set<String> translationKeys) {
		translationKeys.add(category.getI18nKey("name"));

		for (AuditQuestion question : category.getQuestions()) {
			translationKeys.addAll(generateAuditQuestionTranslationKeys(question));
		}

		for (AuditCategory subCategory : category.getSubCategories()) {
			generateCategoryKeys(subCategory, translationKeys);
		}
	}

	public Set<String> generateAuditTypeKeys(AuditType auditType) {
		if (auditType == null) {
			return Collections.emptySet();
		}

		Set<String> translationKeys = new HashSet<>();
		translationKeys.add(auditType.getI18nKey("name"));

		for (AuditCategory category : auditType.getCategories()) {
			translationKeys.add(category.getI18nKey("name"));
			for (AuditQuestion question : category.getQuestions()) {
				translationKeys.addAll(generateAuditQuestionTranslationKeys(question));
			}
		}

		return translationKeys;
	}

	public Set<String> generateAuditQuestionTranslationKeys(AuditQuestion question) {
		if (question == null || !question.isValidQuestion(today)) {
			return Collections.emptySet();
		}

		Set<String> translationKeys = new HashSet<>();
		translationKeys.add(question.getI18nKey("name"));
		translationKeys.add(question.getI18nKey("title"));
		translationKeys.add(question.getI18nKey("requirement"));
		translationKeys.add(question.getI18nKey("columnHeader"));
		translationKeys.add(question.getI18nKey("helpText"));

		return translationKeys;
	}

}
