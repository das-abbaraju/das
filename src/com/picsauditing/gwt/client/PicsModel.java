package com.picsauditing.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.picsauditing.gwt.shared.UserDto;

public class PicsModel {

	private int currentAccount = 1100;
	private List<UserDto> users = new ArrayList<UserDto>();
	private int currentUserIndex = -1;

	private List<PicsModelChangeListener> listeners = new ArrayList<PicsModelChangeListener>();
	private final PicsServiceAsync service = (PicsServiceAsync) GWT
			.create(PicsService.class);

	public PicsModel() {
		fetchUsers();
	}

	public void addChangeListener(PicsModelChangeListener listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(PicsModelChangeListener listener) {
		listeners.remove(listener);
	}

	public List<UserDto> getUsers() {
		return users;
	}

	public UserDto getCurrentUser() {
		if (currentUserIndex == -1)
			return new UserDto();
		for (UserDto user : users)
			if (user.getId() == currentUserIndex)
				return user;
		return new UserDto();
	}

	public void setCurrentUserIndex(int userID) {
		if (currentUserIndex != userID) {
			this.currentUserIndex = userID;
			fireChangeEvent();
			fetchUserDetail();
		}
	}

	private void fetchUserDetail() {
		System.out.println("Loading...");
		UserDto u = getCurrentUser();
		int id = u.getId();
		service.getUserDetail(id, new AsyncCallback<UserDto>() {

			public void onSuccess(UserDto result) {
				System.out.println("Loaded!");
				UserDto u = PicsModel.this.getCurrentUser();
				u.setUserDetail(result.getUserDetail());
				fireChangeEvent();
			}

			public void onFailure(Throwable caught) {
				Window.alert("service.getUser.onFailure: [" + caught.toString()
						+ "]");
			}

		});
	}

	private void fetchUsers() {
		GetUsersRequest r = new GetUsersRequest();
		r.accountId = this.currentAccount;
		service.getUsers(r, new AsyncCallback<List<UserDto>>() {

			public void onSuccess(List<UserDto> result) {
				PicsModel.this.users = result;
				fireChangeEvent();
			}

			public void onFailure(Throwable caught) {
				Window.alert("service.getUsers.onFailure: ["
						+ caught.toString() + "]");
			}

		});

	}

	private void fireChangeEvent() {
		for (PicsModelChangeListener listener : listeners) {
			listener.onChange(this);
		}
	}

}
