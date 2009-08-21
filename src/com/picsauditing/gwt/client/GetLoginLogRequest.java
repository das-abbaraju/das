package com.picsauditing.gwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GetLoginLogRequest implements IsSerializable{

	public String username;
	public int startIndex;
}
