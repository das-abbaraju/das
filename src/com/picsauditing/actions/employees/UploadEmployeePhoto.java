package com.picsauditing.actions.employees;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.ImageUtil;

@SuppressWarnings("serial")
public class UploadEmployeePhoto extends AccountActionSupport {
	@Autowired
	private EmployeeDAO employeeDAO;

	protected Employee employee;

	private File file;
	private String fileContentType = null;
	private String fileFileName = null;
	private String extension;
	private boolean validPhoto = false;
	// Step ranges from [1,2]
	private int step = 0;
	private int x1, y1, width, height;
	private final int XSIZE = 150, YSIZE = 150;
	private final int XRESIZE = 800, YRESIZE = 800;
    
	private final Logger logger = LoggerFactory.getLogger(UploadEmployeePhoto.class);
	@Before
	public void startup() {
		if (employee == null)
			addActionError(getText("EmployeePhotoUpload.message.InvalidEmployee"));

		if (!permissions.hasPermission(OpPerms.ManageEmployees, OpType.Edit)
				&& !(permissions.isContractor() && permissions.hasPermission(OpPerms.ContractorAdmin))) {
			// can't edit photos
			addActionError(getText("EmployeePhotoUpload.message.MissingPermissions"));
		}
	}

	public String execute() {
		if (employee != null) {
			if (!permissions.isPicsEmployee() && (permissions.getAccountId() != employee.getAccount().getId())) {
				// not same contractor
				addActionError(getText("EmployeePhotoUpload.message.CannotEdit", new Object[] { employee.getAccount()
						.getName() }));
				return BLANK;
			}
		}

		if (hasActionErrors())
			return SUCCESS;

		if (step == 0) {
			if (showSavePhoto()) { // set to step 2, crop
				step = 2;
			} else
				step = 1;
		}

		return SUCCESS;
	}

	public String upload() {
		String[] validImgExt = { "jpg", "gif", "png" };
		if (file == null)
			addActionError(getText("EmployeePhotoUpload.message.NoPhotoSelected"));

		extension = FileUtils.getExtension(fileFileName);
		if (!FileUtils.checkFileExtension(extension, validImgExt)) {
			file = null;
			addActionError(getText("EmployeePhotoUpload.message.BadFileExtension"));
		}

		if (hasActionErrors())
			return SUCCESS;

		if (file != null && file.length() > 0) {
			BufferedImage bImg = null;
			bImg = ImageUtil.createBufferedImage(file);

			if (bImg.getHeight() > XRESIZE || bImg.getWidth() > YRESIZE)
				bImg = ImageUtil.resize(bImg, XRESIZE, YRESIZE, true);

			File imgFile = ImageUtil.writeImageWithQuality(bImg, "jpg", .75f);

			try {
				FileUtils.moveFile(imgFile, getFtpDir(), "files/" + FileUtils.thousandize(employee.getId()),
						getFileName(employee.getId()), "jpg", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(getText("EmployeePhotoUpload.message.ErrorMoving", new Object[] { imgFile }));
			}

			if (bImg.getWidth() <= XSIZE && bImg.getHeight() <= YSIZE) {
				employee.setPhoto(extension);
				// Finished!
				step = 2;
				addActionMessage(getText("EmployeePhotoUpload.message.PhotoSaved",
						new Object[] { employee.getDisplayName() }));
			} else {
				employee.setPhoto(null);
				// Move to crop step
				step = 2;
				// addActionMessage();
				addAlertMessage(getText("EmployeePhotoUpload.message.PhotoUploaded"));
			}
			employeeDAO.save(employee);
		}

		return SUCCESS;
	}

	public String save() {
		if (width < XSIZE || height < YSIZE)
			addActionError(getText("EmployeePhotoUpload.message.InvalidSelection"));

		if (hasActionErrors())
			return SUCCESS;
		// do img manipulation
		File f = new File(getFtpDir() + "/files/" + FileUtils.thousandize(employee.getId())
				+ getFileName(employee.getId()) + ".jpg");
		if (f != null) {
			BufferedImage bImg = null;
			try {
				bImg = ImageUtil.createBufferedImage(f);
				if (!(bImg.getWidth() <= XSIZE && bImg.getHeight() <= YSIZE)) {
					bImg = ImageUtil.cropResize(bImg, x1, y1, width, height, XSIZE, YSIZE);
				}
				ImageIO.write(bImg, "jpg", f);
				employee.setPhoto(FileUtils.getExtension(f.getName()));
				employeeDAO.save(employee);
			} catch (IOException e) {
				Logger logger = LoggerFactory.getLogger(UploadEmployeePhoto.class);
				logger.error("Could not crop image");
				addActionError("Error with cropping image");
			} finally {
				bImg.flush();
			}
		}
		// move to finish stage
		step = 2;
		addActionMessage(getText("EmployeePhotoUpload.message.SuccessfullyCroppedUploaded"));

		return SUCCESS;
	}

	public String delete() {
		if (hasActionErrors())
			return SUCCESS;

		File f = new File(getFtpDir() + "/files/" + FileUtils.thousandize(employee.getId())
				+ getFileName(employee.getId()) + ".jpg");
		if (f != null) {
			if (f.delete()) {
				addActionMessage("Photo deleted successfully");
				employee.setPhoto(null);
				employeeDAO.save(employee);
				step = 1;
			} else {
				addActionError("Error deleting photo");
			}
		}

		return SUCCESS;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileName(int eID) {
		return PICSFileType.emp + "_" + eID;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isValidPhoto() {
		return validPhoto;
	}

	public void setValidPhoto(boolean validPhoto) {
		this.validPhoto = validPhoto;
	}

	public boolean showSavePhoto() {
		int eID = employee.getId();
		File f = new File(getFtpDir() + "/files/" + FileUtils.thousandize(eID) + getFileName(eID) + ".jpg");
		return f.exists();
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}

}
