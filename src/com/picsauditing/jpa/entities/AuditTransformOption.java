package com.picsauditing.jpa.entities;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ibm.icu.text.NumberFormat;
import com.picsauditing.importpqf.ImportComparison;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_transform_option")
@PrimaryKeyJoinColumn(name = "id")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditTransformOption  extends BaseTable implements java.io.Serializable {
	private String extractPattern;
	private int extractIndex = -1;

	private String dateFromPattern;
	private String dateToPattern;

	private int decimalPlaces = -1;
	private float multiplier = 1f;
	private float level = -1f;

	private String reformatFromPattern;
	private String reformatToPattern;

	private String answerMapOptions;

	private boolean alternateAnswerOkay = false;
	private boolean somethingOrNothing = false;
	
	private String searchValue;
	private boolean commentResponse = false;
	ImportComparison comparison = ImportComparison.None;
	AuditQuestion comparisonQuestion;
	
	// Load attributes
	AuditQuestion sourceQuestion;
	AuditQuestion destinationQuestion;
	
	public String getExtractPattern() {
		return extractPattern;
	}

	public void setExtractPattern(String extractPattern) {
		this.extractPattern = extractPattern;
	}

	public int getExtractIndex() {
		return extractIndex;
	}

	public void setExtractIndex(int extractIndex) {
		this.extractIndex = extractIndex;
	}

	public String getDateFromPattern() {
		return dateFromPattern;
	}

	public void setDateFromPattern(String dateFromPattern) {
		this.dateFromPattern = dateFromPattern;
	}

	public String getDateToPattern() {
		return dateToPattern;
	}

	public void setDateToPattern(String dateToPattern) {
		this.dateToPattern = dateToPattern;
	}

	public int getDecimalPlaces() {
		return decimalPlaces;
	}

	public void setDecimalPlaces(int decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}

	public String getReformatFromPattern() {
		return reformatFromPattern;
	}

	public void setReformatFromPattern(String reformatFromPattern) {
		this.reformatFromPattern = reformatFromPattern;
	}

	public String getReformatToPattern() {
		return reformatToPattern;
	}

	public void setReformatToPattern(String reformatToPattern) {
		this.reformatToPattern = reformatToPattern;
	}

	public String getAnswerMapOptions() {
		return answerMapOptions;
	}

	public void setAnswerMapOptions(String answerMapOptions) {
		this.answerMapOptions = answerMapOptions;
	}

	public boolean isAlternateAnswerOkay() {
		return alternateAnswerOkay;
	}

	public void setAlternateAnswerOkay(boolean alternateAnswerOkay) {
		this.alternateAnswerOkay = alternateAnswerOkay;
	}

	public boolean isSomethingOrNothing() {
		return somethingOrNothing;
	}

	public void setSomethingOrNothing(boolean somethingOrNothing) {
		this.somethingOrNothing = somethingOrNothing;
	}

	@ManyToOne
	@JoinColumn(name = "sourceQuestionID")
	public AuditQuestion getSourceQuestion() {
		return sourceQuestion;
	}

	public void setSourceQuestion(AuditQuestion question) {
		this.sourceQuestion = question;
	}

	@ManyToOne
	@JoinColumn(name = "destinationQuestionID")
	public AuditQuestion getDestinationQuestion() {
		return destinationQuestion;
	}

	public void setDestinationQuestion(AuditQuestion question) {
		this.destinationQuestion = question;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public boolean isCommentResponse() {
		return commentResponse;
	}

	public void setCommentResponse(boolean commentResponse) {
		this.commentResponse = commentResponse;
	}
	
	public float getLevel() {
		return level;
	}

	public void setLevel(float level) {
		this.level = level;
	}

	@ManyToOne
	@JoinColumn(name = "comparisonQuestionID")
	public AuditQuestion getComparisonQuestion() {
		return comparisonQuestion;
	}

	public void setComparisonQuestion(AuditQuestion comparisonQuestion) {
		this.comparisonQuestion = comparisonQuestion;
	}

	@Enumerated(EnumType.STRING)
	public ImportComparison getComparison() {
		return comparison;
	}

	public void setComparison(ImportComparison comparison) {
		this.comparison = comparison;
	}
	
	@Transient
	public List<Integer> getComparisonIds() {
		List<Integer> list = new ArrayList<Integer>();
		
		if (comparisonQuestion != null) {
			list.add(comparisonQuestion.getId());
		}
		return list;
	}

	public String transformResponse(String response) {
		String workingAnswer = response;

		if (somethingOrNothing) {
			if (!Strings.isEmpty(workingAnswer))
				workingAnswer = "Yes";
			else
				workingAnswer = "No";
		}
		
		if (workingAnswer == null)
			return null;
		
		if (searchValue != null) {
			if (Strings.isEmpty(workingAnswer)) {
				workingAnswer = "No";
			} else if (workingAnswer.indexOf(searchValue) > 0) {
				workingAnswer = "Yes";
			} else {
				workingAnswer = "No";
			}
		}
		
		try {
			if (extractPattern != null && extractPattern.length() > 0) {
				String pattern = extractPattern;
				int index = extractPattern.indexOf("\\n");
				while (index > 0) {
					pattern = pattern.substring(0, index) + "\n" + pattern.substring(index + 2, pattern.length());
					index = pattern.indexOf("\\n");
				}
				MessageFormat format = new MessageFormat(pattern);
				Object[] values = null;
				try {
					values = format.parse(workingAnswer);
					if (values.length > extractIndex) {
						workingAnswer = values[extractIndex].toString();
					}
				} catch (ParseException x) {
					if (extractIndex != 0) throw x;
				}
			}
			
			if (dateFromPattern != null && dateToPattern != null) {
				SimpleDateFormat parser = new SimpleDateFormat(dateFromPattern);
				SimpleDateFormat formatter = new SimpleDateFormat(dateToPattern);
				
				workingAnswer = formatter.format(parser.parse(workingAnswer));
			}

			if (decimalPlaces >= 0) {
				NumberFormat parser = NumberFormat.getInstance();
				NumberFormat format = NumberFormat.getNumberInstance();
				format.setMaximumFractionDigits(decimalPlaces);
				format.setMinimumFractionDigits(decimalPlaces);
				format.setGroupingUsed(false);
				workingAnswer = format.format(parser.parse(workingAnswer).floatValue() * multiplier);
				
				if (level >= 0f) {
					if (Float.parseFloat(workingAnswer) >=  level) {
						workingAnswer = "Yes";
					} else {
						workingAnswer = "No";
					}
				}
			}

			if (reformatFromPattern != null && reformatToPattern != null) {
				MessageFormat parser = new MessageFormat(reformatFromPattern);
				MessageFormat formatter = new MessageFormat(reformatToPattern);
				Object[] values = parser.parse(workingAnswer);
				workingAnswer = formatter.format(values);
			}
			
			if (answerMapOptions != null) {
				HashMap<String, String> map = new HashMap<String, String>();
			    String[] values = answerMapOptions.split(",");
				for (int i=0; i<values.length - 1; i+=2) {
					map.put(values[i].trim(), values[i+1].trim());
				}
				
				if (map.containsKey(workingAnswer)) {
					workingAnswer = map.get(workingAnswer);
				} else {
					workingAnswer = null;
				}
			}

		} catch (Exception x) {
			if (!alternateAnswerOkay)
				workingAnswer = null;
		}
		
		return workingAnswer;
	}
}
