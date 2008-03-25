package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PqfDataKey implements java.io.Serializable {

	private int conId;
	private short questionId;

	public PqfDataKey() {
	}

	public PqfDataKey(int conId, short questionId) {
		this.conId = conId;
		this.questionId = questionId;
	}

	@Column(name = "conID", nullable = false)
	public int getConId() {
		return this.conId;
	}

	public void setConId(int conId) {
		this.conId = conId;
	}

	@Column(name = "questionID", nullable = false)
	public short getQuestionId() {
		return this.questionId;
	}

	public void setQuestionId(short questionId) {
		this.questionId = questionId;
	}

}
