package com.picsauditing.PICS;

public class ContractorEvaluationQuestion {
	public enum Types { radio, textarea }
	public enum DefaultRadioAnswers { Excellent, Satisfactory, Unsatisfactory, NA }

	private int id;
	private String question;
	private String answer = DefaultRadioAnswers.Excellent.toString();
	private Types type = Types.radio;
	private boolean required = false;
	private int catID;
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Types getType() {
		return type;
	}

	public void setType(Types type) {
		this.type = type;
	}

	public void setCatID(int id) {
		this.catID = id * 100;
	}

	public ContractorEvaluationQuestion(String question) {
		this.question = question;
	}
	public ContractorEvaluationQuestion(String question, boolean required) {
		this.question = question;
		this.required = required;
	}
	public ContractorEvaluationQuestion(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}
	public ContractorEvaluationQuestion(String question, String answer, boolean required) {
		this.question = question;
		this.required = required;
		this.answer = answer;
	}
	public ContractorEvaluationQuestion(String question, String answer,
			Types type, boolean required) {
		this.question = question;
		this.answer = answer;
		this.type = type;
		this.required = required;
	}
	
	public String printAnswers() {
		StringBuilder html = new StringBuilder();
		if (this.type.equals(Types.radio)) {
			for (DefaultRadioAnswers answerType : DefaultRadioAnswers.values()) {
				html.append("<label><input type=\"radio\" name=\"answer_");
				html.append(this.catID + this.id);
				html.append("\"");
				if (answerType.toString().equals(this.answer))
					html.append(" checked=\"checked\"");
				html.append("/>");
				if (answerType.equals(DefaultRadioAnswers.NA))
					html.append("N/A");
				else
					html.append(answerType);
				html.append("</label>");
				if (!answerType.equals(DefaultRadioAnswers.NA))
					html.append("<br />");
			}
		}
		if (this.type.equals(Types.textarea)) {
			html.append("<textarea rows=\"10\" cols=\"50\" name=\"answer_");
			html.append(this.id);
			html.append("\">");
			html.append(this.answer);
			html.append("</textarea>");
		}
		
		return html.toString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
