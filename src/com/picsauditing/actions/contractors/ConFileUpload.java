package com.picsauditing.actions.contractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;

public class ConFileUpload extends ContractorActionSupport {
	private static final long serialVersionUID = 2438788697676816034L;
	private InputStream inputStream;

	public ConFileUpload(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		this.findContractor();

		if (button != null) {
			if (contractor.getId() > 0) {
				if (button.equals("download")) {
					Downloader downloader = new Downloader(ServletActionContext.getResponse(), ServletActionContext
							.getServletContext());
					try {
						File[] files = getFiles(contractor.getId());
						downloader.download(files[0], null);
						return null;
					} catch (Exception e) {
						addActionError("Failed to download file: " + e.getMessage());
						return BLANK;
					}
				}
				if (button.equals("logo")) {
					try {
						File logo = new File(getFtpDir() + "/logos/" + contractor.getLogoFile());
						if (logo.exists()) {
							inputStream = new FileInputStream(logo);
							return "logo";
						} else
							return BLANK;
					} catch (Exception e) {
						addActionError("Failed to download file: " + e.getMessage());
						return BLANK;
					}
				}
			}

		}

		return SUCCESS;
	}

	private String getFileName(int conID) {
		return PICSFileType.brochure + "_" + conID;
	}

	private File[] getFiles(int conID) {
		File dir = new File(getFtpDir() + "/files/brochures/");
		return FileUtils.getSimilarFiles(dir, getFileName(conID));
	}

	public InputStream getInputStream() {
		return inputStream;
	}
}
