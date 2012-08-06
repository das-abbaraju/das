package com.picsauditing.models.audits;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;

public class TranslationKeysGenerator {

	private Date today = new Date();
	
	private static final String DOT_NAME = ".name";
	
	public Set<String> generateCategoryKeys(AuditCategory category) {
		Set<String> translationKeys = new HashSet<String>();
		
		generateCategoryKeys(category, translationKeys);
		
		return translationKeys;
	}
	
	private void generateCategoryKeys(AuditCategory category, Set<String> usedKeys) {
		usedKeys.add(category.getI18nKey() + DOT_NAME);
		
		for (AuditQuestion question: category.getQuestions()) {
			if (question.isValidQuestion(today)) {
				usedKeys.add(question.getI18nKey() + DOT_NAME);
			}
		}
		
		for (AuditCategory subCategory: category.getSubCategories()) {
			generateCategoryKeys(subCategory, usedKeys);
		}
	}

	public Set<String> generateAuditTypeKeys(AuditType auditType) {
		Set<String> translationKeys = new HashSet<String>();
		translationKeys.add(auditType.getI18nKey() + DOT_NAME);
		
		for (AuditCategory category: auditType.getCategories()) {
			translationKeys.add(category.getI18nKey() + DOT_NAME);
			for (AuditQuestion question: category.getQuestions()) {
				if (question.isValidQuestion(today)) {
					translationKeys.add(question.getI18nKey() + DOT_NAME);
				}
			}
		}
		
		return translationKeys;
	}
}
