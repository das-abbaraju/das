package com.picsauditing.employeeguard.controllers;

import com.google.gson.Gson;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.UserAgentParser;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.daos.AccountSkillProfileDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.status.ExpirationCalculator;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.employeeguard.util.DateUtil;
import com.picsauditing.employeeguard.viewmodel.EmployeeModel;
import com.picsauditing.employeeguard.viewmodel.SkillReviewModel;
import com.picsauditing.search.Database;
import com.picsauditing.strutsutil.FileDownloadContainer;
import com.picsauditing.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Date;
import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.DOCUMENT_DOWNLOAD_LINK;
import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.DOCUMENT_THUMBNAIL_LINK;
import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.buildUrl;

public class SkillReviewAction extends PicsRestActionSupport {
	private static Logger log = LoggerFactory.getLogger(SkillReviewAction.class);

	@Autowired
	private ProfileDocumentService profileDocumentService;

	@Autowired
	private EmployeeEntityService employeeEntityService;

	@Autowired
	private AccountService accountService;

	private int employeeId;
	private int skillId;

	private InputStream inputStream;

	public String fetchSkillInfo() {
		SkillReviewModel skillReviewModel = new SkillReviewModel();

		try {
			AccountSkillProfile accountSkillProfile = profileDocumentService.getAccountSkillProfileForEmployeeAndSkill(employeeId, skillId);

			if(accountSkillProfile!=null) {

				AccountSkill skill = accountSkillProfile.getSkill();

				skillReviewModel.setName(skill.getName());
				skillReviewModel.setDescription(skill.getDescription());
				Date endDate = ExpirationCalculator.calculateExpirationDate(accountSkillProfile);
				skillReviewModel.setExpiration(DateBean.format(endDate, "YYYY-MM-DD"));
				ProfileDocument profileDocument = accountSkillProfile.getProfileDocument();
				if(profileDocument!=null) {
					if(profileDocument.getFileType().contains("image/")){
						skillReviewModel.setImageUrl(buildUrl(DOCUMENT_THUMBNAIL_LINK, employeeId, skillId));
					}
					skillReviewModel.setFileUrl(buildUrl(DOCUMENT_DOWNLOAD_LINK, employeeId, skillId));
				}
			}
		} catch (DocumentViewAccessDeniedException e) {
			log.warn(String.format("Illegal Access detected AccountId=[%d], employeeId=[%d], skillId=[%d]",permissions.getAccountId(), employeeId, skillId));
		}

		jsonString = new Gson().toJson(skillReviewModel);
    return JSON_STRING;
  }


	public String fetchEmployeeInfo() {
		EmployeeModel employeeModel = new EmployeeModel();

		Employee employee = employeeEntityService.find(employeeId);
		if(employee !=null) {
			AccountModel accountModel = accountService.getAccountById(employee.getAccountId());
			employeeModel.setCompanyName(accountModel.getName());
			employeeModel.setId(employeeId);
			employeeModel.setFirstName(employee.getFirstName());
			employeeModel.setLastName(employee.getLastName());
			employeeModel.setTitle(employee.getPositionName());
		}

		jsonString = new Gson().toJson(employeeModel);
		return JSON_STRING;
	}


	public String downloadThumbnail() throws Exception {

		try {
			AccountSkillProfile accountSkillProfile = profileDocumentService.getAccountSkillProfileForEmployeeAndSkill(employeeId, skillId);
			if(accountSkillProfile!=null) {
				ProfileDocument profileDocument = accountSkillProfile.getProfileDocument();
				if(profileDocument != null) {
						String fileType = profileDocument.getFileType();
						if(fileType !=null && fileType.contains("image/")){
							File documentFile = profileDocumentService.getDocumentFile(profileDocument, getFtpDir());
							inputStream =  new FileInputStream(documentFile);
//-- TODO - For next story
/*
							BufferedImage bufferedImage = ImageIO.read(documentFile);
							int maxOfBoth = bufferedImage.getHeight()>bufferedImage.getWidth()?bufferedImage.getHeight():bufferedImage.getWidth();

							Image img = ImageIO.read(documentFile).getScaledInstance(100, 100, BufferedImage.SCALE_FAST);
*/
						}
				}
			}
		} catch (DocumentViewAccessDeniedException e) {
			log.warn(String.format("Illegal Access detected AccountId=[%d], employeeId=[%d], skillId=[%d]", permissions.getAccountId(), employeeId, skillId));
		}

		return "photo";

	}

	public String downloadDocument() {

		try {
			AccountSkillProfile accountSkillProfile = profileDocumentService.getAccountSkillProfileForEmployeeAndSkill(employeeId, skillId);
			if(accountSkillProfile!=null) {
				ProfileDocument profileDocument = accountSkillProfile.getProfileDocument();
				if(profileDocument != null) {

					byte[] output = null;
					try {
						String fileType = profileDocument.getFileType();
						if(fileType !=null && fileType.contains("image/")){
							File documentFile = profileDocumentService.getDocumentFile(profileDocument, getFtpDir());
							output = FileUtils.getBytesFromFile(documentFile);
						}
					} catch (Exception exception) {
						addActionError("Could not prepare download");
					}

					if (!hasActionErrors()) {
						fileContainer = new FileDownloadContainer.Builder()
										.contentType("image/jpeg")
										.contentDisposition("attachment; filename=" + profileDocument.getFileName())
										.fileInputStream(new ByteArrayInputStream(output)).build();
					}

				}
			}
		} catch (DocumentViewAccessDeniedException e) {
			addActionError("Could not prepare download");
			log.warn(String.format("Illegal Access detected AccountId=[%d], employeeId=[%d], skillId=[%d]",permissions.getAccountId(), employeeId, skillId));
		}

		return FILE_DOWNLOAD;
	}


	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
}
