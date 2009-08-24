package com.picsauditing.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GetLoginLogRequest implements IsSerializable{

	public String username;
	public int startIndex;
}
