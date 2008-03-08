package com.picsauditing.rules;

import java.util.Collection;

public class RulesQuestion {
	private RulesOperator operator = RulesOperator.Any;
	private Object value;
	
	public RulesQuestion() {
		this.operator = RulesOperator.Any;
		this.value = null;
	}
	
	public RulesQuestion(RulesOperator operator, Object value) {
		this.operator = operator;
		this.value = value;
	}
	public RulesQuestion(RulesOperator operator) {
		this.operator = operator;
	}
	
	public boolean equals(Object parameter) {
		try {
			if (operator.equals(RulesOperator.Any))
				return true;
			
			if (operator.equals(RulesOperator.Equals)) {
				return this.value.equals(parameter);
			}
			
			if (operator.equals(RulesOperator.NotEquals)) {
				return !this.value.equals(parameter);
			}
			
			if (operator.equals(RulesOperator.GreaterThan)) {
				int parameterInt = Integer.parseInt(parameter.toString());
				int valueInt = Integer.parseInt(this.value.toString());
				return parameterInt > valueInt;
			}

			if (operator.equals(RulesOperator.LessThan)) {
				int parameterInt = Integer.parseInt(parameter.toString());
				int valueInt = Integer.parseInt(this.value.toString());
				return parameterInt < valueInt;
			}
			
			if (operator.equals(RulesOperator.Contains)) {
				if (parameter instanceof Collection) {
					Collection<Object> temp = (Collection<Object>)parameter;
					return temp.contains(value);
				}
				return parameter.toString().contains(value.toString());
			}
			
			if (operator.equals(RulesOperator.StartsWith)) {
				return parameter.toString().startsWith(value.toString());
			}
			
			return false;
		} catch (Exception e) {
			System.out.println("Failed to evaluate RulesQuestion "+this.toString()+" "+e.getMessage());
			return false;
		}
	}
	
	
	public RulesOperator getOperator() {
		return operator;
	}
	public void setOperator(RulesOperator operator) {
		this.operator = operator;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
