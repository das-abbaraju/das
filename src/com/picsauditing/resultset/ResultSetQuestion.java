package com.picsauditing.resultset;

public class ResultSetQuestion {
	private ResultSetOperator operator = ResultSetOperator.Any;
	private Object value;
	public ResultSetOperator getOperator() {
		return operator;
	}
	public void setOperator(ResultSetOperator operator) {
		this.operator = operator;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	public ResultSetQuestion() {
		this.operator = ResultSetOperator.Any;
		this.value = null;
	}
	
	public ResultSetQuestion(ResultSetOperator operator, Object value) {
		this.operator = operator;
		this.value = value;
	}
	
	public boolean equals(Object parameter) {
		try {
			if (operator.equals(ResultSetOperator.Any))
				return true;
			
			if (operator.equals(ResultSetOperator.Equals)) {
				return this.value.equals(parameter);
			}
			
			if (operator.equals(ResultSetOperator.NotEquals)) {
				return !this.value.equals(parameter);
			}
			
			if (operator.equals(ResultSetOperator.GreaterThan)) {
				int parameterInt = Integer.parseInt(parameter.toString());
				int valueInt = Integer.parseInt(this.value.toString());
				return parameterInt > valueInt;
			}

			if (operator.equals(ResultSetOperator.LessThan)) {
				int parameterInt = Integer.parseInt(parameter.toString());
				int valueInt = Integer.parseInt(this.value.toString());
				return parameterInt < valueInt;
			}
			
			return false;
		} catch (Exception e) {
			System.out.println("Failed to evaluate ResultSetQuestion "+this.toString()+" "+e.getMessage());
			return false;
		}
	}
}
