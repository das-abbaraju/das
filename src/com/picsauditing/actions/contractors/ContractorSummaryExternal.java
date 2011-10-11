package com.picsauditing.actions.contractors;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.ImageUtil;

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
		
			File logo = new File(getFtpDir() + "/logos/" + contractor.getLogoFile());
			String fName = contractor.getLogoFile();
			String ext = fName.substring(fName.lastIndexOf(".") + 1);
			
			BufferedImage img = ImageUtil.createBufferedImage(logo);
			img = ImageUtil.resize(img, 150, 150, true);
	
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ImageIO.write(img, ext, bStream);
			Base64 base64 = new Base64();
			byte[] encodedImage = base64.encode(bStream.toByteArray());
	    
			json.put("name", contractor.getName());
			//json.put("image", getFtpDir() + "/logos/" + contractor.getLogoFile());
			json.put("address", contractor.getAddress() + "<br />" +
								contractor.getCity() + ", " +
								contractor.getState().toString() + " " +
								contractor.getZip() + "<br />" +
								contractor.getCountry().toString()
					);
			json.put("description", contractor.getDescription());
			json.put("phone", contractor.getPhone());
			json.put("website", contractor.getWebUrl());
			json.put("logoFileName", contractor.getLogoFile());
			json.put("image", encodedImage.toString());
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
