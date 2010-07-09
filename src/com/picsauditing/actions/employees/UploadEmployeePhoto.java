package com.picsauditing.actions.employees;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.FileUtils;

@SuppressWarnings("serial")
public class UploadEmployeePhoto extends AccountActionSupport implements
		Preparable {

	private EmployeeDAO employeeDAO;

	protected Employee employee;

	private int employeeID;
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

	public UploadEmployeePhoto(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}

	@Override
	public void prepare() throws Exception {
		int eID = getParameter("employeeID");
		if (eID > 0)
			employee = employeeDAO.find(eID);
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;
		
		if(!permissions.hasPermission(OpPerms.ManageEmployees, OpType.Edit)){
			// can't edit photos
			addActionError("You do not have permissions to Edit Employee Photos");
			return BLANK;
		}
		if(employeeID > 0){
			employee = employeeDAO.find(employeeID);
			if(!permissions.isPicsEmployee() && (permissions.getAccountId()!=employee.getAccount().getId())){
				// not same contractor
				addActionError("You can not edit Photos for "+employee.getAccount().getName());
				return BLANK;
			}
		}
		
		if(step==0){
			if(showSavePhoto()){ // set to step 2, crop
				step = 2;
			} else step = 1;			
		}

		if ("Upload".equals(button)) {
			if (employee == null) {
				addActionError("Invalid Employee");
				return BLANK;
			}
			String[] validImgExt = { "jpg", "gif", "png" };
			if (file == null) {
				addActionError("No Photo Selected");
				return SUCCESS;
			}
			extension = FileUtils.getExtension(fileFileName);
			if (!FileUtils.checkFileExtension(extension, validImgExt)) {
				file = null;
				addActionError("Bad File Extension");
				return SUCCESS;
			}
			if (file != null && file.length() > 0) {
				BufferedImage bImg = null;
				BufferedImage resizedImg = null;
				Iterator<ImageWriter> iter = ImageIO
						.getImageWritersByFormatName("jpeg");
				FileImageOutputStream outStream = null;
				File f = new File("tmp.jpg");
				ImageWriter writer = null;
				if (iter.hasNext()) {
					writer = iter.next();
				}
				try {
					bImg = ImageIO.read(file);

					if (bImg.getHeight() > XRESIZE || bImg.getWidth() > YRESIZE) {
						resizedImg = photoResize(bImg, 800, 800, true);
					} else resizedImg = bImg;

					ImageWriteParam iwp = writer.getDefaultWriteParam();

					iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					iwp.setCompressionQuality(.75f);
					outStream = new FileImageOutputStream(f);
					writer.setOutput(outStream);

					IIOImage image = new IIOImage(resizedImg, null, null);
					writer.write(null, image, iwp);
					outStream.flush();
				} catch (IOException e) {
					// File could not be created or opened or saved
					System.out.println("Could not handle image");
					addActionError("Error with file");
				} finally {
					bImg.flush();
					if(resizedImg!=null){
						resizedImg.flush();						
					}
					writer.dispose();
					close(outStream);
				}

				try {
					FileUtils.moveFile(f, getFtpDir(), "files/"
							+ FileUtils.thousandize(employee.getId()),
							getFileName(employee.getId()), "jpg", true);
				} catch (Exception e) {
					System.out.println("Error moving " + f);
				}
				if (hasActionErrors()) {
					return SUCCESS;
				}
				if (bImg.getWidth() <= XSIZE && bImg.getHeight() <= YSIZE) {
					employee.setPhoto(extension);
					// Finished!
					step = 2;
					addActionMessage("Photo for"+employee.getDisplayName()+" has been saved and is now in use!");
				} else {
					employee.setPhoto(null);
					//Move to crop step
					step = 2;
					//addActionMessage();
					addAlertMessage("Your Photo has been Uploaded!\nPlease click on the photo below and drag to crop your image." +
					"When you are happy with your selection click the 'Crop Photo' Button below to crop and save this photo for the profile page");
				}
				employeeDAO.save(employee);
			}
		}
		
		if ("Save".equals(button)) {
			if (employee == null) {
				addActionError("Invalid Employee");
				return BLANK;
			}
			if(width < XSIZE || height < YSIZE){
				addActionError("Invalid Selection");
				return SUCCESS;
				
			}
			// do img manipulation
			File f = new File(getFtpDir() + "/files/"
					+ FileUtils.thousandize(employeeID)
					+ getFileName(employeeID) + ".jpg");
			if (f != null) {
				BufferedImage bImg = null;
				try {
					bImg = ImageIO.read(f);
					// bImg = bImg.getSubimage(x1, y1, width, height);
					if (!(bImg.getWidth() <= XSIZE && bImg.getHeight() <= YSIZE)) {
						bImg = photoCrop(bImg, x1, y1, width, height, XSIZE,
								YSIZE);
					}
					ImageIO.write(bImg, "jpg", f);
					employee.setPhoto(FileUtils.getExtension(f.getName()));
					employeeDAO.save(employee);
				} catch (IOException e) {
					System.out.println("Could not crop image");
					addActionError("Error with cropping image");
				} finally {
					bImg.flush();
				}
			}
			// move to finish stage
			step = 2;
			addAlertMessage("The profile photo for this employee has been successfully cropped and uploaded! ");
		}
		
		if("Delete".equals(button)){
			if(employee == null){
				addActionError("Invalid Employee");
				return BLANK;
			}
			
			File f = new File(getFtpDir() + "/files/"
					+ FileUtils.thousandize(employeeID)
					+ getFileName(employeeID) + ".jpg");
			if(f!=null){
				if(f.delete()){
					addActionMessage("Photo deleted successfully");
					employee.setPhoto(null);
					employeeDAO.save(employee);
					step = 0;
					return SUCCESS;
				} else{
					addActionError("Error deleting photo");
					return SUCCESS;
				}
			}
			
		}

		return SUCCESS;

	}

	public void close(FileImageOutputStream outStream) {
		if (outStream == null)
			return;
		try {
			outStream.close();
		} catch (IOException e) {
			System.out.println("Error closing " + outStream.getClass());
			addActionError("Error with file");
		}
	}
	
	public File writeImage(Image img, String format){
		
		return null;		
	}

	public BufferedImage photoResize(BufferedImage image, int width,
			int height, boolean maintainRatio) {
		BufferedImage resizedImg = null;

		float w = image.getWidth();
		float h = image.getHeight();
		float bigSide;
		float ratio, diff;
		int newW = 0, newH = 0;
		if (maintainRatio) {
			if (w > h) {
				ratio = h / w;
				bigSide = w;
			} else {
				ratio = w / h;
				bigSide = h;
			}
			ratio = (float) ((int) (ratio * 100)) / 100;
			if (ratio == 1) {
				newW = width;
				newH = height;
			} else {
				diff = bigSide - Math.max(width, height);
				diff = diff * ratio;
				if (bigSide == w) {
					newW = width;
					newH = (int) (h - diff);
				} else {
					newH = height;
					newW = (int) (w - diff);
				}
			}
		} else {
			newW = width;
			newH = height;
		}
		resizedImg = new BufferedImage(newW, newH, image.getType());
		Graphics2D g = resizedImg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, 0, 0, newW, newH, null);
		g.dispose();

		return resizedImg;
	}

	public BufferedImage photoCrop(BufferedImage image, int x, int y,
			int width, int height, int cropX, int cropY) {
		image = image.getSubimage(x, y, width, height);
		image = photoResize(image, cropX, cropY, true);

		return image;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int eID) {
		this.employeeID = eID;
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
		File f = new File(getFtpDir() + "/files/" + FileUtils.thousandize(eID)
				+ getFileName(eID) + ".jpg");
		return f.exists();
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}

}
