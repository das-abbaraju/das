package com.picsauditing.mail;

public class EmailSender extends GMailSender {
	private static int currentDefaultSender = 1;
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
	public void sendMail(Email email) throws Exception {
		if (currentDefaultSender > 6)
			throw new Exception("Failed to send email, used all possible senders. See log file for more info.");
		try {
			this.sendMail(email.getSubject(), email.getBody(), getSender(), email.getToAddress());
		} catch (Exception e) {
			System.out.println("Send Mail Exception:"+e.getMessage());
			changeSender();
			this.sendMail(email);
		}
	}
	
	public static String getSender() {
		if (EmailSender.currentDefaultSender > 2)
			return "info"+currentDefaultSender+"@picsauditing.com";
		else
			return "info@picsauditing.com";
	}
	public static void changeSender() {
		currentDefaultSender++;
	}
}
