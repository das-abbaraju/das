package com.picsauditing.gwt.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.gwt.shared.GetLoginLogRequest;
import com.picsauditing.gwt.shared.GetUsersRequest;
import com.picsauditing.gwt.shared.PicsService;
import com.picsauditing.gwt.shared.LoginLogDTO;
import com.picsauditing.gwt.shared.UserDto;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class GwtServlet extends RemoteServiceServlet implements PicsService {
	
	private UserDAO userDAO;
	private UserLoginLogDAO userLoginLogDAO;
	
	public GwtServlet() {
		userDAO = (UserDAO)SpringUtils.getBean("UserDAO");
		userLoginLogDAO = (UserLoginLogDAO) SpringUtils.getBean("UserLoginLogDAO");
	}

	public UserDto getUserDetail(int id) {
		PicsLogger.start("GwtServlet");
		
		User user = userDAO.find(id);
		UserDto u = user.toDTO();
		u.setUserDetail(user.toDetail());
		PicsLogger.stop();
		return u;
	}

	public List<UserDto> getUsers(GetUsersRequest request) {
		PicsLogger.start("GwtServlet");

		ArrayList<UserDto> users = new ArrayList<UserDto>();

		if (request.accountId > 0) {
			List<User> usersJPA = userDAO.findByAccountID(request.accountId, "", "");
			for (User user : usersJPA) {
				users.add(user.toDTO());
			}
		} else if (request.name != null) {
			List<User> usersJPA;
			if (request.limit > 0)
				usersJPA = userDAO.findWhere("isGroup = 'No' AND isActive = 'Yes' AND name LIKE '" + request.name + "%'", request.limit);
			else
				usersJPA = userDAO.findWhere("isGroup = 'No' AND isActive = 'Yes' AND name LIKE '" + request.name + "%'");
			for (User user : usersJPA) {
				users.add(user.toDTO());
			}
		}

		PicsLogger.stop();
		return users;
	}
	
	public List<LoginLogDTO> getUserLoginLog(GetLoginLogRequest request) {
		PicsLogger.start("GwtServlet");

		ArrayList<LoginLogDTO> loginList = new ArrayList<LoginLogDTO>();
		List<UserLoginLog> loginLogsJPA = userLoginLogDAO.findRecentLogins(request.username, 0, 10);
		for(UserLoginLog loginLog : loginLogsJPA) {
			loginList.add(loginLog.toDTO());
		}
		

		PicsLogger.stop();
		return loginList;
	}
}
