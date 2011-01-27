package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

@SuppressWarnings("serial")
@Entity
@Table(name = "user_assignment_matrix")
public class UserAssignmentMatrix extends BaseTable {

	private User user;
	private UserAssignmentMatrixType assignmentType;
	private State state;
	private Country country;
	private String postalStart;
	private String postalEnd;

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public UserAssignmentMatrixType getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(UserAssignmentMatrixType assignmentType) {
		this.assignmentType = assignmentType;
	}

	@ManyToOne
	@JoinColumn(name = "state")
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@ManyToOne
	@JoinColumn(name = "country")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(name = "postal_start")
	public String getPostalStart() {
		return postalStart;
	}

	public void setPostalStart(String postalStart) {
		this.postalStart = postalStart;
	}

	@Column(name = "postal_end")
	public String getPostalEnd() {
		return postalEnd;
	}

	public void setPostalEnd(String postalEnd) {
		this.postalEnd = postalEnd;
	}

	@Override
	public String toString() {
		return String.format("%s state:%s, country:%s, zip:%s-%s", user.getName(), state, country, postalStart,
				postalEnd);
	}
}
