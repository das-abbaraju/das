package com.picsauditing.actions.contractors;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

@SuppressWarnings("serial")
public class ContractorSummaryExternal extends PicsActionSupport {
	@Autowired
	protected ContractorAccountDAO accountDao;

	private int id;
	private ContractorAccount contractor;
	protected JSONObject json = new JSONObject();

	@SuppressWarnings("unchecked")
	@Anonymous
	public String execute() throws Exception {
		contractor = accountDao.find(id);

		if (contractor != null) {
			String requestHost = this.getRequestHost();

			json.put("name", contractor.getName());
			json.put("address", contractor.getAddress() + "<br />" + contractor.getCity() + ", "
					+ contractor.getCountrySubdivision().toString() + " " + contractor.getZip() + "<br />"
					+ contractor.getCountry().toString());
			json.put("description", contractor.getDescription());
			json.put("phone", contractor.getPhone());
			json.put("website", contractor.getWebUrl());

			File logo_file = new File(getFtpDir() + "/logos/" + contractor.getLogoFile());

			try {
				BufferedImage image = ImageIO.read(logo_file);

				int logo_height = image.getHeight();
				int logo_width = image.getWidth();

				json.put("logo", requestHost + "/ContractorLogo.action?id=" + contractor.getId());
				json.put("logo_height", logo_height);
				json.put("logo_width", logo_width);
			} catch (Exception e) {
				json.put("logo", null);
			}

			return JSON;
		}

		return SUCCESS;
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

}
