package com.picsauditing.mail;

public class EmailSender extends GMailSender {
	private static int currentDefaultSender = 1;
	private static final int NUMBER_OF_GMAIL_ACOUNTS = 6;
	private static String defaultPassword = "e3r4t5";
	
	public EmailSender() {
		super(getSender(), defaultPassword);
	}
	public EmailSender(String user, String password) {
		super(user, password);
	}
	
	/**
	 * Try sending with the first email address info@picsauditing.com
	 * If it errors try info2, info3, ...info6.
	 * If it still fails, then exit
	 * @param email
	 * @throws Exception
	 */
	private void sendMail(Email email, int attempts) throws Exception {
		attempts++;
		if (attempts > NUMBER_OF_GMAIL_ACOUNTS)
			throw new Exception("Failed to send email, used all possible senders. See log file for more info.");
		try {
			this.sendMail(email.getSubject(), email.getBody(), getSender(), email.getToAddress());
		} catch (Exception e) {
			System.out.println("Send Mail Exception with account "+currentDefaultSender+": "+e.getMessage());
			changeSender();
			this.sendMail(email, attempts);
		}
	}
	public void sendMail(Email email) throws Exception {
		int attempts = 0;
		this.sendMail(email, attempts);
	}
	
	public static String getSender() {
		if (EmailSender.currentDefaultSender > 2)
			return "PICS Info <info"+currentDefaultSender+"@picsauditing.com>";
		else
			return "PICS Info <info@picsauditing.com>";
	}
	public static void changeSender() {
		currentDefaultSender = (currentDefaultSender % NUMBER_OF_GMAIL_ACOUNTS)+1;
	}
}
