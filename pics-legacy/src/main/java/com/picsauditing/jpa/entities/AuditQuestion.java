package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.picsauditing.jpa.entities.builders.AuditQuestionBuilder;
import com.picsauditing.model.i18n.TranslatableString;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditQuestion extends BaseHistoryRequiringLanguages implements Comparable<AuditQuestion> {

	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	static public final int EMR = 2034;
	static public final int MANUAL_PQF = 1331;
	static public final int MANUAL_PQF_SIGNATURE = 10217;
	static public final int OQ_EMPLOYEES = 894;
	static public final int COR = 2954;
	static public final int IEC = 10330;
	static public final int CITATIONS = 3546;
	static public final int EXCESS_EACH = 2161;
	static public final int EXCESS_AGGREGATE = 2165;
	static public final int RISK_LEVEL_ASSESSMENT = 2444;
	static public final int PRODUCT_CRITICAL_ASSESSMENT = 7678;
	static public final int PRODUCT_SAFETY_CRITICAL_ASSESSMENT = 7679;
	static public final int IMPORT_PQF = 7727;
	static public final int OSHA_KEPT_ID = 2064;
	static public final int COHS_KEPT_ID = 2066;
	static public final int UK_HSE_KEPT_ID = 9106;
	static public final int EMR_KEPT_ID = 2033;
	static public final int MEXICO_KEPT_ID = 15337;
	static public final int AUSTRALIA_KEPT_ID = 15214;
	static public final int IRELAND_KEPT_ID = 15660;
	static public final int SOUTH_AFRICA_KEPT_ID = 16282;
	static public final int SINGAPORE_MOM_KEPT_ID = 16590;
	static public final int TURKEY_KEPT_ID = 17168;
	static public final int SWITZERLAND_KEPT_ID = 16894;
	static public final int SPAIN_KEPT_ID = 17097;
	static public final int POLAND_KEPT_ID = 17141;
	static public final int AUSTRIA_KEPT_ID = 17126;
	static public final int ITALY_KEPT_ID = 17111;
	static public final int PORTUGAL_KEPT_ID = 17129;
	static public final int DENMARK_KEPT_ID = 17172;
	static public final int CZECH_KEPT_ID = 17183;
	static public final int HUNGARY_KEPT_ID = 17170;
	static public final int GREECE_KEPT_ID = 17203;

	public static final String TYPE_NUMBER = "Number";
	public static final String TYPE_DATE = "Date";
	public static final String TYPE_DECIMAL = "Decimal Number";
	public static final String TYPE_FILE_CERTIFICATE = "FileCertificate";
	static public final String[] TYPE_ARRAY = { "MultipleChoice", "Text", "Text Area", "Check Box",
			"Additional Insured", "AMBest", "Calculation", TYPE_DATE, TYPE_DECIMAL, "File", TYPE_FILE_CERTIFICATE,
			"License", "Money", TYPE_NUMBER, "ESignature", "Tagit", "MultiSelect", "Percent" };

	private int number;
	private int scoreWeight;
	private boolean hasRequirement;
	private boolean required;
	private boolean groupedWithPrevious;
	private boolean showComment;
	private String requiredAnswer;
	private String visibleAnswer;

	private String name;
    private String slug;

	private String questionType;
	private AuditOptionGroup option;
	private String okAnswer;

	private String columnHeader;

	private String uniqueCode;

	private String title;
	private String requirement;

	private String helpPage;

	// private TranslatableString helpText;
	private String helpText;

	private String criteria;
	private String criteriaAnswer;
	private AuditQuestion requiredQuestion;
	private AuditQuestion visibleQuestion;
	private AuditCategory category;
	private LowMedHigh riskLevel = null;
	private boolean hasTitleText;
	private boolean hasHelpText;
	private boolean hasRequirementText;

	private List<AuditQuestion> dependentRequired;
	private List<AuditQuestion> dependentVisible;
	private Set<AuditQuestion> dependentQuestions;
	protected List<AuditOptionValue> options;
	private List<AuditCategoryRule> auditCategoryRules;
	private List<AuditTypeRule> auditTypeRules;
	private List<AuditQuestionFunction> functions = new ArrayList<AuditQuestionFunction>();
	private List<AuditQuestionFunctionWatcher> functionWatchers = new ArrayList<AuditQuestionFunctionWatcher>();

	private List<AuditTransformOption> transformOptions;

	public AuditQuestion() {

	}

	public AuditQuestion(AuditQuestion a, AuditCategory ac) {
		this.number = a.number;
		this.name = a.getName();
        this.slug = a.getSlug();
		this.questionType = a.questionType;
		this.option = a.option;
		this.hasRequirement = a.hasRequirement;
		this.okAnswer = a.okAnswer;
		this.required = a.required;
		this.requiredQuestion = a.requiredQuestion;
		this.requiredAnswer = a.requiredAnswer;
		this.visibleQuestion = a.visibleQuestion;
		this.visibleAnswer = a.visibleAnswer;
		this.columnHeader = a.getColumnHeader();
		this.uniqueCode = a.uniqueCode;
		this.title = a.getTitle();
		this.groupedWithPrevious = a.groupedWithPrevious;
		this.showComment = a.showComment;
		this.riskLevel = a.riskLevel;
		this.helpPage = a.helpPage;
		this.helpText = a.getHelpText();
		this.requirement = a.getRequirement();
		this.category = ac;
		this.effectiveDate = a.effectiveDate;
		this.expirationDate = a.expirationDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryID", nullable = false)
	public AuditCategory getCategory() {
		return this.category;
	}

	public void setCategory(AuditCategory auditCategory) {
		this.category = auditCategory;
	}

	@Column(nullable = false)
	@ReportField(type = FieldType.Integer, importance = FieldImportance.Average)
	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Transient
	public String getName() {
		if (name != null) {
			return name;
		}

		return new TranslatableString(getI18nKey("name")).toTranslatedString();
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Transient
	@Override
	public String getI18nKey() {
		if (Strings.isEmpty(uniqueCode)) {
			return super.getI18nKey();
		} else {
			return "AuditQuestion." + uniqueCode;
		}
	}

	@Column(nullable = false)
	@ReportField(type = FieldType.String, importance = FieldImportance.Average)
	public String getQuestionType() {
		return this.questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	@ManyToOne
	@JoinColumn(name = "optionID")
	public AuditOptionGroup getOption() {
		return option;
	}

	public void setOption(AuditOptionGroup option) {
		this.option = option;
	}

	@Column(nullable = false)
	@ReportField(type = FieldType.Boolean, importance = FieldImportance.Average)
	public boolean isHasRequirement() {
		return hasRequirement;
	}

	public void setHasRequirement(boolean hasRequirement) {
		this.hasRequirement = hasRequirement;
	}

	public String getOkAnswer() {
		return this.okAnswer;
	}

	public void setOkAnswer(String okAnswer) {
		this.okAnswer = okAnswer;
	}

	@Column(nullable = false)
	@ReportField(type = FieldType.Boolean)
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@ManyToOne
	@JoinColumn(name = "requiredQuestion")
	public AuditQuestion getRequiredQuestion() {
		return requiredQuestion;
	}

	public void setRequiredQuestion(AuditQuestion requiredQuestion) {
		this.requiredQuestion = requiredQuestion;
	}

	public String getRequiredAnswer() {
		return requiredAnswer;
	}

	public void setRequiredAnswer(String requiredAnswer) {
		this.requiredAnswer = requiredAnswer;
	}

	@ManyToOne
	@JoinColumn(name = "visibleQuestion")
	public AuditQuestion getVisibleQuestion() {
		return visibleQuestion;
	}

	public void setVisibleQuestion(AuditQuestion visibleQuestion) {
		this.visibleQuestion = visibleQuestion;
	}

	public String getVisibleAnswer() {
		return visibleAnswer;
	}

	public void setVisibleAnswer(String visibleAnswer) {
		this.visibleAnswer = visibleAnswer;
	}

	@Transient
	public String getColumnHeader() {
		if (columnHeader != null) {
			return columnHeader;
		}

		return new TranslatableString(getI18nKey("columnHeader")).toTranslatedString();
	}

	public void setColumnHeader(String columnHeader) {
		this.columnHeader = columnHeader;
	}

	@Column(length = 50)
	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@Transient
	public String getTitle() {
		if (title != null) {
			return title;
		}

		return new TranslatableString(getI18nKey("title")).toTranslatedString();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(nullable = false)
	public boolean isGroupedWithPrevious() {
		return groupedWithPrevious;
	}

	public void setGroupedWithPrevious(boolean groupedWithPrevious) {
		this.groupedWithPrevious = groupedWithPrevious;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	public boolean isShowComment() {
		return showComment;
	}

	public void setShowComment(boolean showComment) {
		this.showComment = showComment;
	}

	@Enumerated(EnumType.ORDINAL)
	public LowMedHigh getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(LowMedHigh risk) {
		this.riskLevel = risk;
	}

	@Column(length = 100)
	public String getHelpPage() {
		return helpPage;
	}

	public void setHelpPage(String helpPage) {
		this.helpPage = helpPage;
	}

	@Transient
	public String getHelpText() {
		if (helpText != null) {
			return helpText;
		}

		return new TranslatableString(getI18nKey("helpText")).toTranslatedString();
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	@Transient
	public String getRequirement() {
		if (requirement != null) {
			return requirement;
		}

		return new TranslatableString(getI18nKey("requirement")).toTranslatedString();
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	@Transient
	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	@Transient
	public String getCriteriaAnswer() {
		return criteriaAnswer;
	}

	public void setCriteriaAnswer(String criteriaAnswer) {
		this.criteriaAnswer = criteriaAnswer;
	}

	public boolean isHasTitleText() {
		return hasTitleText;
	}

	public void setHasTitleText(boolean hasTitleText) {
		this.hasTitleText = hasTitleText;
	}

	public boolean isHasHelpText() {
		return hasHelpText;
	}

	public void setHasHelpText(boolean hasHelpText) {
		this.hasHelpText = hasHelpText;
	}

	public boolean isHasRequirementText() {
		return hasRequirementText;
	}

	public void setHasRequirementText(boolean hasRequirementText) {
		this.hasRequirementText = hasRequirementText;
	}

	@OneToMany(mappedBy = "requiredQuestion")
	public List<AuditQuestion> getDependentRequired() {
		return dependentRequired;
	}

	public void setDependentRequired(List<AuditQuestion> requiredQuestions) {
		this.dependentRequired = requiredQuestions;
	}

	@OneToMany(mappedBy = "visibleQuestion")
	public List<AuditQuestion> getDependentVisible() {
		return dependentVisible;
	}

	public void setDependentVisible(List<AuditQuestion> visibleQuestions) {
		this.dependentVisible = visibleQuestions;
	}

	@Transient
	public List<AuditQuestion> getDependentVisible(String answer) {
		List<AuditQuestion> dependentVisibleBasedOnAnswer = new ArrayList<AuditQuestion>();
		for (AuditQuestion visQ : dependentVisible) {
			if (testVisibility(answer, visQ.getVisibleAnswer())) {
				dependentVisibleBasedOnAnswer.add(visQ);
			}
		}

		return dependentVisibleBasedOnAnswer;
	}

	@Transient
	public List<AuditQuestion> getDependentVisibleHide(String answer) {
		List<AuditQuestion> dependentVisibleBasedOnAnswer = new ArrayList<AuditQuestion>();
		for (AuditQuestion visQ : dependentVisible) {
			if (!testVisibility(answer, visQ.getVisibleAnswer())) {
				dependentVisibleBasedOnAnswer.add(visQ);
			}
		}

		return dependentVisibleBasedOnAnswer;
	}

	@Transient
	public Set<AuditQuestion> getDependentQuestions() {
		if (dependentQuestions == null) {
			dependentQuestions = new HashSet<AuditQuestion>();
			for (AuditQuestion dependentQuestion : dependentRequired) {
				dependentQuestions.add(dependentQuestion);
				dependentQuestions.addAll(dependentQuestion.getDependentQuestions());
			}
			for (AuditQuestion visibleQuestion : dependentVisible) {
				dependentQuestions.add(visibleQuestion);
				dependentQuestions.addAll(visibleQuestion.getDependentQuestions());
			}
			for (AuditQuestionFunctionWatcher watcher : functionWatchers) {
				dependentQuestions.add(watcher.getFunction().getQuestion());
			}
		}

		return dependentQuestions;
	}

	@OneToMany(mappedBy = "question")
	public List<AuditCategoryRule> getAuditCategoryRules() {
		return auditCategoryRules;
	}

	public void setAuditCategoryRules(List<AuditCategoryRule> auditCategoryRules) {
		this.auditCategoryRules = auditCategoryRules;
	}

	@OneToMany(mappedBy = "question")
	public List<AuditTypeRule> getAuditTypeRules() {
		return auditTypeRules;
	}

	public void setAuditTypeRules(List<AuditTypeRule> auditTypeRules) {
		this.auditTypeRules = auditTypeRules;
	}

	/**
	 * The list of {@link AuditQuestionFunction}s that apply to this
	 * {@link AuditQuestion}.
	 * 
	 * Note: Only the first {@link AuditQuestionFunction} with a
	 * {@link QuestionFunctionType} of "Calculation" will take effect.
	 * 
	 */
	@OneToMany(mappedBy = "question")
	public List<AuditQuestionFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<AuditQuestionFunction> functions) {
		this.functions = functions;
	}

	/**
	 * This is a reference to the {@link AuditQuestionFunction}s that this
	 * {@link AuditQuestion} is required for calcualtion.
	 * 
	 */
	@OneToMany(mappedBy = "question")
	public List<AuditQuestionFunctionWatcher> getFunctionWatchers() {
		return functionWatchers;
	}

	public void setFunctionWatchers(List<AuditQuestionFunctionWatcher> functionWatchers) {
		this.functionWatchers = functionWatchers;
	}

	/**
	 * This method runs all {@link AuditQuestionFunction} of a specific
	 * {@link QuestionFunctionType} "runType".
	 * 
	 * @param runType
	 * @param answerMap
	 * @return Multimap of the {@link AuditQuestion} => a collection of Function
	 *         Results.
	 */
	@Transient
	public Multimap<AuditQuestion, Object> runWatcherFunctions(QuestionFunctionType runType, AnswerMap answerMap) {
		Multimap<AuditQuestion, Object> results = ArrayListMultimap.create();
		for (AuditQuestionFunctionWatcher watcher : functionWatchers) {
			if (watcher.getFunction().getType() == runType) {
				results.put(watcher.getFunction().getQuestion(), watcher.getFunction().calculate(answerMap));
			}
		}
		return results;
	}

	/**
	 * This method runs all {@link AuditQuestionFunction} of a specific
	 * {@link QuestionFunctionType} "runType".
	 * 
	 * @param runType
	 * @param answerMap
	 * @return result
	 */
	@Transient
	public String runFunctions(QuestionFunctionType runType, AnswerMap answerMap) {
		String results = "";
		for (AuditQuestionFunction function : functions) {
			if (function.getType() == runType) {
				results = function.calculate(answerMap).toString();
				break;
			}
		}
		return results;
	}

	/**
	 * This method is used to find a Set of {@link AuditQuestion}.id's that are
	 * needed by the {@link AuditQuestionFunction}s of this
	 * {@link AuditQuestion}.
	 * 
	 * This is to help create an {@link AnswerMap} of the answers that are
	 * required for calculation.
	 */
	@Transient
	public Collection<Integer> getSiblingQuestionWatchers() {
		Set<Integer> siblings = new HashSet<Integer>();
		for (AuditQuestionFunctionWatcher myWatcher : functionWatchers) {
			for (AuditQuestionFunctionWatcher sibling : myWatcher.getFunction().getWatchers()) {
				siblings.add(sibling.getQuestion().getId());
			}
		}
		return siblings;
	}

	@Transient
	public AuditType getAuditType() {
		return category.getParentAuditType();
	}

	@Transient
	public String getExpandedNumber() {
		return category.getFullNumber() + "." + number;
	}

	@Transient
	public String getColumnHeaderOrQuestion() {
		String columnHeader = getColumnHeader();
		if (columnHeader != null && !Strings.isEmpty(columnHeader) && !columnHeader.equals(getI18nKey("columnHeader"))) {
			return columnHeader;
		}

		if (getName() == null) {
			return Strings.EMPTY_STRING;
		}

		return getName().toString();
	}

	@OneToMany(mappedBy = "sourceQuestion", cascade = { CascadeType.REMOVE })
	public List<AuditTransformOption> getTransformOptions() {
		return transformOptions;
	}

	public void setTransformOptions(List<AuditTransformOption> transformOptions) {
		this.transformOptions = transformOptions;
	}

	@Override
	public int compareTo(AuditQuestion other) {
		if (other == null) {
			return 1;
		}

		int cmp = getCategory().compareTo(other.getCategory());

		if (cmp != 0) {
			return cmp;
		}

		int numberCmp = new Integer(getNumber()).compareTo(new Integer(other.getNumber()));

		if (numberCmp != 0) {
			return numberCmp;
		}

		return new Integer(id).compareTo(new Integer(other.getId()));
	}

	/**
	 * Comparator for comparing AuditQuestions not based on natural ordering.
	 * Use compareTo if you want to order based on the AuditQuestion's number
	 * and direct Category (it's natural order)
	 * 
	 * @return The comparator for full ordering of AuditQuestions
	 */
	@Transient
	public static Comparator<AuditQuestion> getComparator() {
		return new Comparator<AuditQuestion>() {
			public int compare(AuditQuestion o1, AuditQuestion o2) {
                String expandedNumber1 = o1.getExpandedNumber();
                String expandedNumber2 = o2.getExpandedNumber();
                String[] o1a = expandedNumber1.split("\\.");
                String[] o2a = expandedNumber2.split("\\.");

				for (int i = 0; i < o1a.length; i++) {
					if (i >= o2a.length) {
						return -1;
					}

					if (o1a[i].equals(o2a[i])) {
						continue;
					} else {
						return Integer.valueOf(o1a[i]).compareTo(Integer.valueOf(o2a[i]));
					}
				}

                if (expandedNumber1.equals(expandedNumber2))
				    return 0;
                else
                    return 1;
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject j = super.toJSON(full);
		j.put("category", category.toJSON());
		j.put("name", getName());

		return j;
	}

	/**
	 * Return true if there are category rules that require immediate
	 * recalculation when the answer to this question changes
	 * 
	 * @return
	 */
	@Transient
	public boolean isRecalculateCategories() {
		if (getAuditCategoryRules().size() > 0) {
			return true;
		}
		if (getAuditTypeRules().size() > 0) {
			return true;
		}
		return false;
	}

	@Transient
	public String getShortQuestion() {
		String columnText = getName().toString();

		if (columnText.length() > 45) {
			columnText = columnText.substring(0, 45);
			columnText += " [...]";
		}

		return getExpandedNumber() + ": " + columnText;
	}

	/**
	 * @return int showing the weight to adjust the score by for this question
	 *         when scoring an audit
	 */
	public int getScoreWeight() {
		return scoreWeight;
	}

	public void setScoreWeight(int scoreWeight) {
		this.scoreWeight = scoreWeight;
	}

	@Transient
	public boolean isValidQuestion(Date validDate) {
		if (validDate.after(effectiveDate) && validDate.before(expirationDate)) {
			return true;
		} else {
			return false;
		}
	}

	@Transient
	public boolean isVisible(AnswerMap answerMap) {
		if (visibleQuestion != null) {
			boolean questionIsVisible = isVisible(answerMap.get(visibleQuestion.getId()));
			AuditQuestion q = visibleQuestion;

			while (q != null && questionIsVisible) {
				if (q.getVisibleQuestion() != null) {
					questionIsVisible = q.isVisible(answerMap.get(q.getVisibleQuestion().getId()));
				}

				q = q.getVisibleQuestion();
			}
			return questionIsVisible;
		}
		return true;
	}

	@Transient
	public boolean isVisible(AuditData data) {
		if (visibleQuestion != null && visibleAnswer != null) {
			String answer = null;
			if (data != null) {
				answer = data.getAnswer();
			}
			return testVisibility(answer, visibleAnswer);
		}
		return true;
	}

	private boolean testVisibility(String answer, String comparisonAnswer) {
		if (comparisonAnswer.equals("NULL") && Strings.isEmpty(answer)) {
			return true;
		}
		if (comparisonAnswer.equals("NOTNULL") && !Strings.isEmpty(answer)) {
			return true;
		}
		if (comparisonAnswer.equals(answer)) {
			return true;
		}
		return false;
	}

	@Transient
	public boolean isVisibleInAudit(ContractorAudit audit) {
		for (AuditCatData category : audit.getCategories()) {
			if (category.getCategory().getId() == this.getCategory().getId()) {
				return category.isApplies();
			}
		}

		return false;
	}

	@Transient
	public boolean isAffectsAudit() {
		return required || requiredQuestion != null || dependentRequired.size() > 0;
	}

	@Transient
	@Override
	public String getAutocompleteItem() {
		return "[" + id + "] " + getName();
	}

	@Transient
	@Override
	public String getAutocompleteValue() {
		return getName();
	}

	/**
	 * Is inline radio group
	 * 
	 * Boolean to check to see if a radio group's options should be displayed in
	 * one line or multiple lines. There is a css class 'inline' that is
	 * togglable by this flag.
	 * 
	 * @return
	 */
	@Transient
	public boolean isInlineRadioGroup() {
		int optionSize = this.option.getValues().size();

		if (this.option.isRadio() && (optionSize == 2 || optionSize == 3)) {
			int optionTextLength = 0;

			for (AuditOptionValue optionValue : this.option.getValues()) {
				optionTextLength += optionValue.getName().toString().length();
			}

			return optionTextLength <= 30;
		}

		return false;
	}

	public boolean hasMissingChildRequiredLanguages() {
		return getLanguages().isEmpty();
	}

	public static AuditQuestionBuilder builder() {
		return new AuditQuestionBuilder();
	}

    @Override
    public int hashCode() {
        int result = 31 * getId();
        result = 31 * result + (getSlug() != null ? getSlug().hashCode() : 0);
        return result;
    }

}