package com.picsauditing.gwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GetUsersRequest implements IsSerializable{

	public int accountId;
	public String name;
	public String email;
	
}
