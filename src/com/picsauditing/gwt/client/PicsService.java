package com.picsauditing.gwt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.picsauditing.gwt.shared.LoginLogDTO;
import com.picsauditing.gwt.shared.UserDto;

@RemoteServiceRelativePath("picsService")
public interface PicsService extends RemoteService{
	
	List<UserDto> getUsers(GetUsersRequest request);
	UserDto getUserDetail(int id);
	List<LoginLogDTO> getUserLoginLog(GetLoginLogRequest request);
}
