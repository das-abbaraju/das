package com.picsauditing.actions;

import java.util.List;

import org.json.simple.JSONArray;

import com.picsauditing.dao.WebcamDAO;
import com.picsauditing.jpa.entities.Webcam;

@SuppressWarnings("serial")
public class WebcamJSON extends PicsActionSupport {
	private String json;

	@SuppressWarnings("unchecked")
	public WebcamJSON(WebcamDAO webcamDAO) {
		List<Webcam> list = webcamDAO.findWhere("");
		// JSONUtilities.convertFromList(list);

		JSONArray jsonArray = new JSONArray();
		for (Webcam obj : list)
			jsonArray.add(obj.toJSON(false));

		json = jsonArray.toJSONString();

	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
