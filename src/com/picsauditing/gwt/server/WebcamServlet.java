package com.picsauditing.gwt.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.picsauditing.dao.WebcamDAO;
import com.picsauditing.gwt.shared.GetWebcamsRequest;
import com.picsauditing.gwt.shared.WebcamDTO;
import com.picsauditing.gwt.shared.WebcamService;
import com.picsauditing.jpa.entities.Webcam;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class WebcamServlet extends RemoteServiceServlet implements WebcamService {

	private WebcamDAO webcamDAO;

	public WebcamServlet() {
		webcamDAO = (WebcamDAO) SpringUtils.getBean("WebcamDAO");
	}

	public List<WebcamDTO> getWebcams(GetWebcamsRequest request) {
		List<Webcam> webcams = webcamDAO.findWhere(null);
		List<WebcamDTO> webcamDTOs = new ArrayList<WebcamDTO>();
		for (Webcam webcam : webcams) {
			webcamDTOs.add(webcam.toDTO());
		}
		return webcamDTOs;
	}

}
