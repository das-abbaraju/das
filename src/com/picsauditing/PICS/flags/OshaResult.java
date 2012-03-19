package com.picsauditing.PICS.flags;

public class OshaResult {

	private boolean verified;
	private String answer;
	private String years;
	
	public boolean isVerified() {
		return verified;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public String getYear() {
		return years;
	}
	
	public static class Builder {
		
		private boolean verified;
		private String answer;
		private String years;
		
		public Builder verified(boolean verified) {
			this.verified = verified;
			return this;
		}
		
		public Builder answer(String answer) {
			this.answer = answer;
			return this;
		}
		
		public Builder years(String years) {
			this.years = years;
			return this;
		}
		
		public OshaResult build() {
			OshaResult oshaResult = new OshaResult();
			
			oshaResult.verified = this.verified;
			oshaResult.answer = this.answer;
			oshaResult.years = this.years;
			
			return oshaResult;
		}
		
	}
	
}
