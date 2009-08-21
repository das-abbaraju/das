package com.picsauditing.gwt.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.gwt.client.GetUsersRequest;
import com.picsauditing.gwt.client.PicsService;
import com.picsauditing.gwt.shared.UserDetailDto;
import com.picsauditing.gwt.shared.UserDto;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.log.LoggingRule;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class GwtServlet extends RemoteServiceServlet implements PicsService {
	
	private UserDAO userDAO;
	
	public GwtServlet() {
		userDAO = (UserDAO)SpringUtils.getBean("UserDAO");
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

		List<User> usersJPA = userDAO.findByAccountID(request.accountId, "", "");
		for (User user : usersJPA) {
			users.add(user.toDTO());
		}
		
		PicsLogger.stop();
		return users;
	}

}
