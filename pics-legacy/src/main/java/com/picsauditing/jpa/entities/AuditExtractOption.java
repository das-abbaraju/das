package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.importpqf.ImportStopAt;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_extract_option")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditExtractOption extends BaseTable implements java.io.Serializable {

	// Extraction attributes
	private boolean startAtBeginning = false;
	private ImportStopAt stopAt = ImportStopAt.None;
	private String startingPoint;
	private String stoppingPoint;
	private boolean collectAsLines = false;
	private AuditQuestion question;

	// Transient attributes
	String answer = null;
	boolean questionFound = false;
	boolean processed = false;
	
	public boolean isStartAtBeginning() {
		return startAtBeginning;
	}

	public void setStartAtBeginning(boolean startAtBeginning) {
		this.startAtBeginning = startAtBeginning;
	}
	
	@Enumerated(EnumType.STRING)
	public ImportStopAt getStopAt() {
		return stopAt;
	}

	public void setStopAt(ImportStopAt stopAt) {
		this.stopAt = stopAt;
	}

	public String getStartingPoint() {
		return startingPoint;
	}

	public void setStartingPoint(String startingPoint) {
		this.startingPoint = startingPoint;
	}

	public String getStoppingPoint() {
		return stoppingPoint;
	}

	public void setStoppingPoint(String stoppingPoint) {
		this.stoppingPoint = stoppingPoint;
	}

	public boolean isCollectAsLines() {
		return collectAsLines;
	}

	public void setCollectAsLines(boolean collectAsLine) {
		this.collectAsLines = collectAsLine;
	}

	@OneToOne
	@JoinColumn(name = "questionID")
	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}
	
	@Transient 
	public boolean isAnswerFound() {
		return !Strings.isEmpty(answer);
	}

	@Transient
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
		processed = (answer != null);
	}
	
	@Transient
	public boolean isQuestionFound() {
		return questionFound;
	}

	public void setQuestionFound(boolean questionFound) {
		this.questionFound = questionFound;
	}

	@Transient
	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
