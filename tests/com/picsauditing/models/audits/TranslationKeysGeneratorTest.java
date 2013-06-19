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
	// private static final String DUMMY_TRANSLATION = "Dummy Translation";
	private static final String NAME = "name";

	@Test
	public void testGenerateAuditTypeKeys() {
		AuditType auditType = EntityFactory.makeAuditType();
		auditType.setName(NAME);
		List<AuditCategory> auditCategories = new ArrayList<AuditCategory>();

		for (int numberOfCategories = 0; numberOfCategories < 10; numberOfCategories++) {
			AuditCategory category = EntityFactory.makeAuditCategory();
			category.setName(NAME);
			auditCategories.add(category);

			buildAuditQuestionsInCategory(category, 10);
		}

		auditType.setCategories(auditCategories);
		assertEquals(511, translationKeysGenerator.generateAuditTypeKeys(auditType).size());
	}

	@Test
	public void testGenerateCategoryKeys_OneLevelDeep() {
		AuditCategory category = EntityFactory.makeAuditCategory();
		category.setName(NAME);
		buildAuditQuestionsInCategory(category, 10);

		List<AuditCategory> subCategories = new ArrayList<AuditCategory>();

		for (int numberOfCategories = 0; numberOfCategories < 10; numberOfCategories++) {
			AuditCategory subCategory = EntityFactory.makeAuditCategory();
			subCategory.setName(NAME);
			subCategories.add(subCategory);
			subCategory.setSubCategories(new ArrayList<AuditCategory>());

			buildAuditQuestionsInCategory(subCategory, 10);
		}

		category.setSubCategories(subCategories);
		assertEquals(561, translationKeysGenerator.generateCategoryKeys(category).size());
	}

	@Test
	public void testGenerateCategoryKeys_TenLevelsDeep() {
		AuditCategory category = EntityFactory.makeAuditCategory();
		category.setName(NAME);

		buildAuditQuestionsInCategory(category, 10);
		buildNestedCategories(category, 10);

		assertEquals(561, translationKeysGenerator.generateCategoryKeys(category).size());
	}

	@Test
	public void testGenerateAuditQuestionTranslationKeys_RequiredFields() {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setName(NAME);

		assertEquals(5, translationKeysGenerator.generateAuditQuestionTranslationKeys(question).size());
	}

	@Test
	public void testGenerateAuditQuestionTranslationKeys_AllFields() {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setName(NAME);
		question.setColumnHeader("columnHeader");
		question.setTitle("title");
		question.setHelpText("helpText");
		question.setRequirement("requirement");

		assertEquals(5, translationKeysGenerator.generateAuditQuestionTranslationKeys(question).size());
	}

	private void buildNestedCategories(AuditCategory category, int numberOfNestedCategories) {
		if (numberOfNestedCategories == 0) {
			return;
		}

		List<AuditCategory> subCategories = new ArrayList<AuditCategory>();
		AuditCategory subCategory = EntityFactory.makeAuditCategory();
		subCategory.setName(NAME);
		subCategories.add(subCategory);
		category.setSubCategories(subCategories);

		buildAuditQuestionsInCategory(subCategory, 10);
		buildNestedCategories(subCategory, --numberOfNestedCategories);
	}

	private void buildAuditQuestionsInCategory(AuditCategory category, int numberOfQuestions) {
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>();
		for (; numberOfQuestions > 0; numberOfQuestions--) {
			AuditQuestion question = EntityFactory.makeAuditQuestion();

			question.setName(NAME);

			auditQuestions.add(question);
		}
		category.setQuestions(auditQuestions);
	}

	// private TranslatableString buildTranslatableString(BaseTable
	// translatableObject, String attribute) {
	// TranslatableString translatableString =
	// EntityFactory.makeTranslatableString(DUMMY_TRANSLATION);
	// translatableString.setKey(translatableObject.getClass().getSimpleName() +
	// translatableObject.getId() + "."
	// + attribute);
	// return translatableString;
	// }
}