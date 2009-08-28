package com.picsauditing.actions;

import java.util.List;

import org.json.simple.JSONArray;

import com.picsauditing.dao.WebcamDAO;
import com.picsauditing.jpa.entities.Webcam;
import com.picsauditing.util.JSONCallback;

@SuppressWarnings("serial")
public class WebcamJSON extends PicsActionSupport {
	private JSONCallback cb;

	@SuppressWarnings("unchecked")
	public WebcamJSON(WebcamDAO webcamDAO) {
		List<Webcam> list = webcamDAO.findWhere("");
		// JSONUtilities.convertFromList(list);

		JSONArray jsonArray = new JSONArray();
		for (Webcam obj : list)
			jsonArray.add(obj.toJSON(false));

		cb = new JSONCallback(jsonArray);
	}

	public String getJson() {
		return cb.toString();
	}

	public void setCallback(String callback) {
		cb.setCallbackFunction(callback);
	}

	public void set_dc(long dc) {
	}

}
