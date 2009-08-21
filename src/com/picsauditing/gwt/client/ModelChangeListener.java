package com.picsauditing.gwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface ModelChangeListener<T extends IsSerializable> extends IsSerializable{
	
	void onChange(T eventSource);

}
