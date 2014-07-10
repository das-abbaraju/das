package com.picsauditing.auditbuilder.entities;

/**
 * The enum that is used to determine the action this {@link AuditQuestionFunction} performs.
 *
 * @author kpartridge
 *
 */
public enum QuestionFunctionType {

	/**
	 * This Type is used to change the answer on an {@link AuditData} to a specific {@link AuditQuestion}.
	 *
	 * The question should be a "Calculation" type to restrict it from being edited.
	 */
	Calculation,
    /**
     * This is to calculate the sum of a particular question type in an audit class over a certain period.
     */
    Rollup,
	/**
	 * This is a proposed function type for {@link QuestionFunction} that determine the visibility of a question
	 */
	Visible,
	/**
	 * This is a proposed function type for {@link QuestionFunctionType} that determine the required/not-required state
	 * of a {@link AuditQuestion}
	 */
	Required;

}
