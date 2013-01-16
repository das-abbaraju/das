package com.picsauditing.mail;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.access.OpPerms;

public class EmailRequestDTO {
	public int templateID = 0;
	public String subject;
	public String body;
	public Set<Integer> userIDs = new HashSet<Integer>();
	public Set<Integer> contractorIDs = new HashSet<Integer>();
	public Set<OpPerms> userTypes = new HashSet<OpPerms>();
    public String overrideEmail;
}
