package com.picsauditing.actions.auditType;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.importpqf.ImportComparison;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditTransformOption;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageTransformOption extends ManageQuestion {
	protected int transformId = 0;
	protected String destinationId = "";
	protected AuditTransformOption option = new AuditTransformOption();
	protected AuditTransformOption origOption = null;
	protected boolean formatNumber = false;
	protected boolean hasLevel = false;
	
	protected void load(AuditQuestion o) {
		super.load(o);
//		this.question = o;
//		load(question.getCategory());
		
		String[] ids = (String[])ActionContext.getContext().getParameters().get("transformId");
		if (ids != null && ids.length > 0)
			transformId = Integer.parseInt(ids[0]);
		
		for (AuditTransformOption op:question.getTransformOptions()) {
			if (op.getId() == transformId) {
				origOption = op;
				option.copy(origOption);
				destinationId = "" + option.getDestinationQuestion().getId();
				formatNumber = option.getDecimalPlaces() >= 0;
				hasLevel = option.getLevel() >= 0f;
			}
		}
		
		if (option.getSourceQuestion() == null) {
			option.setSourceQuestion(question);
		}
	}
	
	public boolean save() {
		// look up destination
		destinationId = nullOnEmpty(destinationId);
		if (destinationId == null) {
			addActionError("Unable to find PQF Question.");
			return false;			
		}
		
		AuditQuestion destinationQuestion = auditQuestionDAO.find(Integer.parseInt(destinationId));
		if (destinationQuestion == null || !destinationQuestion.getCategory().getAuditType().isPqf()) {
			addActionError("Unable to find PQF Question.");
			return false;
		}
		option.setDestinationQuestion(destinationQuestion);
		
		// search value
		option.setSearchValue(nullOnEmpty(option.getSearchValue()));
		
		// extraction value
		option.setExtractPattern(nullOnEmpty(option.getExtractPattern()));
		
		if (option.getExtractIndex() < 0) {
			addActionError("The Extraction Number must get greater than or equal to 0.");
			return false;
		}

		// date format
		option.setDateFromPattern(nullOnEmpty(option.getDateFromPattern()));
		option.setDateToPattern(nullOnEmpty(option.getDateToPattern()));
		if (option.getDateFromPattern() == null && option.getDateToPattern() != null) {
			addActionError("Both the Original and New Date Formats must be specified.");
			return false;
		} 
		if (option.getDateFromPattern() != null && option.getDateToPattern() == null) {
			addActionError("Both the Original and New Date Formats must be specified.");
			return false;
		}
		
		// number format
		if (formatNumber) {
			if (option.getDecimalPlaces() < 0) {
				addActionError("Decimal Places must be a number 0 or greater.");
				return false;
			}

			if (!hasLevel) {
				option.setLevel(-1f);
			} else if (option.getLevel() < 0f){
				addActionError("Level must be a number 0 or greater.");
				return false;
			}
			
		} else {
			option.setDecimalPlaces(-1);
			option.setLevel(-1f);
		}

		// reformat
		option.setReformatFromPattern(nullOnEmpty(option.getReformatFromPattern()));
		option.setReformatToPattern(nullOnEmpty(option.getReformatToPattern()));
		if (option.getReformatFromPattern() == null && option.getReformatToPattern() != null) {
			addActionError("Both the Original and New Reformat Patterns must be specified.");
			return false;
		} 
		if (option.getReformatFromPattern() != null && option.getReformatToPattern() == null) {
			addActionError("Both the Original and New Reformat Patterns must be specified.");
			return false;
		}
		
		// mapping
		if (!validateMappingOption()) {
			return false;
		}
		
		// comparison
		if (!validateComparisonOption()) {
			return false;
		}
		
		// save
		if (origOption == null) {
			dao.save(option);
			question.getTransformOptions().add(option);
		} else {
			origOption.copy(option);
		}
		question = auditQuestionDAO.save(question);

		try {
			redirect("ManageQuestion.action?id=" + question.getId());
		} catch (Exception x) {
		}
		return true;
	}

	protected boolean delete() {
		if (origOption != null) {
			dao.remove(origOption);
			question.getTransformOptions().remove(origOption);
			question = auditQuestionDAO.save(question);
			try {
				redirect("ManageQuestion.action?id=" + question.getId());
			} catch (Exception x) {
			}
			return true;
		}
		return false;
	}
	
	private boolean validateMappingOption() {
		option.setAnswerMapOptions(nullOnEmpty(option.getAnswerMapOptions()));
		
		if (Strings.isEmpty(option.getAnswerMapOptions())) return true;
		
		int count = 0;
		String sanitizedMapping = "";
		
		String[] values = option.getAnswerMapOptions().split("[, ]");
		for (String value:values) {
			if (value.length() > 0) {
				count++;
				if (sanitizedMapping.length() > 0) sanitizedMapping +=",";
				sanitizedMapping +=value;
			}
		}
		
		if (count % 2 != 0) {
			addActionError("Mappings must be entered in pairs.");
			return false;
		}
		
		option.setAnswerMapOptions(sanitizedMapping);
		
		return true;
	}
	
	private boolean validateComparisonOption() {
		if (!option.getComparison().isComparison()) {
			option.setComparisonQuestions(null);
			return true;
		}
		
		option.setComparisonQuestions(nullOnEmpty(option.getComparisonQuestions()));
		
		if (option.getComparisonQuestions() == null) {
			addActionError("You need to specify at least one question ID from this audit for comparison.");
			return false;			
		}
		
		String questions = "";
		String[] values = option.getComparisonQuestions().split("[, ]");
		for (String value:values) {
			if (value.length() == 0) continue;
			int compareId = Integer.parseInt(value);
			AuditQuestion compareQuestion = auditQuestionDAO.find(compareId);
			if (compareQuestion == null || 
					compareQuestion.getCategory().getAuditType().getId() != question.getCategory().getAuditType().getId()) {
				addActionError("Invalid Comparison Question ID list.  You must specify a question ID from this audit for comparison.");
				return false;			
			}
			if (questions.length() > 0) questions +=",";
			questions += value;
		}
		
		if (questions.length() == 0) {
			addActionError("You need to specify at least one question ID from this audit for comparison.");
			return false;						
		}
		
		option.setComparisonQuestions(questions);
		
		return true;
	}
	
	private String nullOnEmpty(String value) {
		if (Strings.isEmpty(value)) return null;
		return value.trim();
	}

	public int getTransformId() {
		return transformId;
	}

	public void setTransformId(int transformId) {
		this.transformId = transformId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public AuditTransformOption getOption() {
		return option;
	}

	public void setOption(AuditTransformOption option) {
		this.option = option;
	}

	public ImportComparison[] getComparisonOptions() {
		return ImportComparison.values();
	}
	
	public String getComparison() {
		return option.getComparison().toString();
	}
	
	public void setComparison(String value) {
		option.setComparison(ImportComparison.valueOf(value));
	}
	
	public boolean isFormatNumber() {
		return formatNumber;
	}

	public void setFormatNumber(boolean formatNumber) {
		this.formatNumber = formatNumber;
	}

	public int getDecimalPlaces() {
		if (option.getDecimalPlaces() < 0) return 0;
		
		return option.getDecimalPlaces();
	}
	
	public void setDecimalPlaces(int value) {
		option.setDecimalPlaces(value);
	}

	public boolean isHasLevel() {
		return hasLevel;
	}

	public void setHasLevel(boolean hasLevel) {
		this.hasLevel = hasLevel;
	}
	
	public float getLevel() {
		if (option.getLevel() < 0) return 0f;
		
		return option.getLevel();
	}
	
	public void setLevel(float value) {
		option.setLevel(value);
	}
}
