package com.picsauditing.models.audits;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;


public class TranslationKeysGeneratorTest {

	private TranslationKeysGenerator translationKeysGenerator = new TranslationKeysGenerator();
	
	@Test
	public void testGenerateAuditTypeKeys() {
		AuditType auditType = EntityFactory.makeAuditType();
		List<AuditCategory> auditCategories = new ArrayList<AuditCategory>();
		
		for (int numberOfCategories = 0; numberOfCategories < 10; numberOfCategories++) {
			AuditCategory category = EntityFactory.makeAuditCategory();
			auditCategories.add(category);
			
			buildAuditQuestionsInCategory(category, 10);
		}
		
		auditType.setCategories(auditCategories);
		assertEquals(111, translationKeysGenerator.generateAuditTypeKeys(auditType).size());
	}
	
	@Test
	public void testGenerateCategoryKeys_OneLevelDeep() {
		AuditCategory category = EntityFactory.makeAuditCategory();
		buildAuditQuestionsInCategory(category, 10);
		
		
		List<AuditCategory> subCategories = new ArrayList<AuditCategory>();
		
		for (int numberOfCategories = 0; numberOfCategories < 10; numberOfCategories++) {
			AuditCategory subCategory = EntityFactory.makeAuditCategory();
			subCategories.add(subCategory);
			subCategory.setSubCategories(new ArrayList<AuditCategory>());
			
			buildAuditQuestionsInCategory(subCategory, 10);
		}
		category.setSubCategories(subCategories);
		assertEquals(121, translationKeysGenerator.generateCategoryKeys(category).size());
	}

	@Test
	public void testGenerateCategoryKeys_TenLevelsDeep() {
		AuditCategory category = EntityFactory.makeAuditCategory();
	
		buildAuditQuestionsInCategory(category, 10);
		buildNestedCategories(category, 10);
		
		assertEquals(121, translationKeysGenerator.generateCategoryKeys(category).size());
	}
	
	private void buildNestedCategories(AuditCategory category, int numberOfNestedCategories) {
		if (numberOfNestedCategories == 0)
			return;
		
		List<AuditCategory> subCategories = new ArrayList<AuditCategory>();
		AuditCategory subCategory = EntityFactory.makeAuditCategory();
		subCategories.add(subCategory);
		category.setSubCategories(subCategories);
		
		buildAuditQuestionsInCategory(subCategory, 10);
		buildNestedCategories(subCategory, --numberOfNestedCategories);
	}
	
	private void buildAuditQuestionsInCategory(AuditCategory category, int numberOfQuestions) {
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>();
		for (; numberOfQuestions > 0; numberOfQuestions--) {
			auditQuestions.add(EntityFactory.makeAuditQuestion());
		}
		category.setQuestions(auditQuestions);
	}
}