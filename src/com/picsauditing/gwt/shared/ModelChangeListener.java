package com.picsauditing.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface ModelChangeListener<T extends IsSerializable> extends IsSerializable{
	
	void onChange(T eventSource);

}
