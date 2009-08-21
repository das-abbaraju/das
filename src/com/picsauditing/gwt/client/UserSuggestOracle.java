package com.picsauditing.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.picsauditing.gwt.shared.UserDto;

public class UserSuggestOracle extends SuggestOracle {

	private static Timer timer = null;

	private final PicsServiceAsync service = (PicsServiceAsync) GWT.create(PicsService.class);

	@Override
	public void requestSuggestions(final Request request, final Callback callback) {
		if (request.getQuery().length() < 3)
			return;

		if (timer != null)
			timer.cancel();

		timer = new Timer() {
			@Override
			public void run() {
				GetUsersRequest getUserRequest = new GetUsersRequest();
				getUserRequest.name = request.getQuery();
				getUserRequest.limit = request.getLimit();
				service.getUsers(getUserRequest, new AsyncCallback<List<UserDto>>() {
					public void onSuccess(List<UserDto> result) {
						List<Suggestion> suggestList = new ArrayList<Suggestion>();
						for (UserDto userDto : result)
							suggestList.add(new PicsSuggestion<UserDto>(userDto));

						callback.onSuggestionsReady(request, new Response(suggestList));
					}

					public void onFailure(Throwable caught) {
						Window.alert("An error occurred while fetching users.");
					}
				});
			}
		};

		timer.schedule(200);
	}
}
