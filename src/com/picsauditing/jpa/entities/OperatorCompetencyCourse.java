package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.*;

@Entity
@Table(name = "operator_competency_course")
public class OperatorCompetencyCourse extends BaseTable {
	private OperatorCompetency competency;
	private OperatorCompetencyCourseType courseType;

	@ManyToOne
	@JoinColumn(name = "competencyID")
	public OperatorCompetency getCompetency() {
		return competency;
	}

	public void setCompetency(OperatorCompetency competency) {
		this.competency = competency;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "courseType", nullable = false)
	public OperatorCompetencyCourseType getCourseType() {
		return courseType;
	}

	public void setCourseType(OperatorCompetencyCourseType courseType) {
		this.courseType = courseType;
	}
}
