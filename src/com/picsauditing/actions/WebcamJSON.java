package com.picsauditing.actions;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import com.picsauditing.dao.WebcamDAO;
import com.picsauditing.jpa.entities.Webcam;
import com.picsauditing.util.JSONCallback;

@SuppressWarnings("serial")
public class WebcamJSON extends PicsActionSupport {
	private String json;
	private JSONCallback callback;

	@SuppressWarnings("unchecked")
	public WebcamJSON(WebcamDAO webcamDAO) {
		List<Webcam> list = webcamDAO.findWhere("");
		// JSONUtilities.convertFromList(list);

		JSONArray jsonArray = new JSONArray();
		for (Webcam obj : list)
			jsonArray.add(obj.toJSON(false));

		json = jsonArray.toJSONString();
		callback = new JSONCallback(jsonArray);

	}

	public String toJSON() {
		return callback.toString();
	}

	public void setJson(String json) {
		this.json = json;
	}

}
