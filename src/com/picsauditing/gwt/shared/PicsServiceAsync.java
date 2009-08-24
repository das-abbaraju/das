package com.picsauditing.gwt.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.picsauditing.gwt.shared.LoginLogDTO;
import com.picsauditing.gwt.shared.UserDto;

public interface PicsServiceAsync {

	void getUsers(GetUsersRequest request, AsyncCallback<List<UserDto>> callback);
	
	void getUserDetail(int id, AsyncCallback<UserDto> callback);

	void getUserLoginLog(GetLoginLogRequest request, AsyncCallback<List<LoginLogDTO>> callback);
}
