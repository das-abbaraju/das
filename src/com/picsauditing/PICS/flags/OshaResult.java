package com.picsauditing.PICS.flags;

/**
 * Used as a Container for information related to Osha or International Osha-Like
 * data because there are cases where multiple data points are needed when 
 * processing business logic.
 */
public class OshaResult {

	private boolean verified;
	private String answer;
	private String year;
	
	public boolean isVerified() {
		return verified;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public String getYear() {
		return year;
	}
	
	/**
	 * Used to build a new instance of OshaResult
	 */
	public static class Builder {
		
		private boolean verified;
		private String answer;
		private String year;
		
		public Builder verified(boolean verified) {
			this.verified = verified;
			return this;
		}
		
		public Builder answer(String answer) {
			this.answer = answer;
			return this;
		}
		
		public Builder year(String year) {
			this.year = year;
			return this;
		}
		
		public OshaResult build() {
			OshaResult oshaResult = new OshaResult();
			
			oshaResult.verified = this.verified;
			oshaResult.answer = this.answer;
			oshaResult.year = this.year;
			
			return oshaResult;
		}
		
	}
	
}
