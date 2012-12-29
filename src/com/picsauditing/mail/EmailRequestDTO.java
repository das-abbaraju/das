package com.picsauditing.mail;

import java.util.HashSet;
import java.util.Set;

public class EmailRequestDTO {
	public int templateID = 0;
	public String subject;
	public String body;
	public Set<Integer> userIDs = new HashSet<Integer>();
	public Set<Integer> contractorIDs = new HashSet<Integer>();
}
