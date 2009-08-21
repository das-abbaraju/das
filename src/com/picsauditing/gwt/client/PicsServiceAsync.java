package com.picsauditing.gwt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.picsauditing.gwt.shared.UserDto;

public interface PicsServiceAsync {

	void getUsers(GetUsersRequest request, AsyncCallback<List<UserDto>> callback);

	void getUserDetail(int id, AsyncCallback<UserDto> callback);

}
