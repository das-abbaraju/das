package com.picsauditing.jpa.entities;

public enum QuestionFunctionType {
	
	// Experiments with Kyle and Trevor with Question Functions

	Visible {
		public boolean isYes(String answer) {
			return answer.equals("Yes");
		}
	},
	Catcher {
		public boolean isRequiredYes(String answer) {
			return answer.equals("Yes");
		}

		public boolean solve(String answer) {
			return false;
		}
	},
	Pitcher {
		public boolean solve(String answer) {
			return solver(answer);
		}
	};

	// abstract public boolean solve(String answer);

	public boolean solver(String answer) {
		return false;
	}

}
