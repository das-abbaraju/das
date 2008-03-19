package com.picsauditing.actions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TestBean 
{
	@Id
	@GeneratedValue( strategy=GenerationType.IDENTITY )
	protected int id = 0;
	protected String greeting = null;


	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGreeting() {
		return greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}
}
