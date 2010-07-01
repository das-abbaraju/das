package com.picsauditing.actions.employees;

import java.awt.Graphics2D;
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
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.FileUtils;

@SuppressWarnings("serial")
public class UploadEmployeePhoto extends AccountActionSupport implements Preparable {
	
	private EmployeeDAO employeeDAO;
	
	protected Employee employee;
	
	private int employeeID;	
	private File file;
	private String fileContentType = null;
	private String fileFileName = null;
	private String extension;
	private boolean validPhoto = false;
	
	private int x1, y1, width, height;
	
	public UploadEmployeePhoto(EmployeeDAO employeeDAO){
		this.employeeDAO = employeeDAO;
	}

	@Override
	public void prepare() throws Exception {
		int eID = getParameter("employeeID");
		if (eID > 0)
			employee = employeeDAO.find(eID);
	}
	
	public String execute(){
		
		if("Upload".equals(button)){
			employee = employeeDAO.find(employeeID);
			if(employee==null){
				addActionError("Invalid Employee");
				return BLANK;
			}
			String[] validImgExt = {"jpg", "gif", "png"};
			if(file==null){
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
				Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
				FileImageOutputStream outStream = null;				
				File f = new File("tmp.jpg");
				ImageWriter writer = null;
				if(iter.hasNext()){
					writer = iter.next();
				}
				try{
					bImg = ImageIO.read(file);
					
					if(bImg.getHeight()>800||bImg.getWidth()>800){
						Graphics2D g = bImg.createGraphics();
						float w = bImg.getWidth();
						float h = bImg.getHeight();
						float bigSide;
						float ratio;
						int newW = 0, newH = 0;
						if(w>h){
							ratio = h/w;
							bigSide = w;
						}else{
							ratio = w/h;
							bigSide = h;
						}
						ratio = (float)((int)(ratio*100))/100;
						float diff = bigSide - 800;
						diff = diff*ratio;
						if(bigSide==w){
							newW = 800;
							newH = (int)(h-diff);
						} else{
							newH = 800;
							newW = (int)(w-diff);
						}
				        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				        g.drawImage(bImg, 0, 0, newW, newH, null);
				        bImg = bImg.getSubimage(0, 0, newW, newH);
				        g.dispose();

					}
					
					ImageWriteParam iwp = writer.getDefaultWriteParam();
					
					iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					iwp.setCompressionQuality(.75f);
					outStream = new FileImageOutputStream(f);
					writer.setOutput(outStream);
					
					IIOImage image = new IIOImage(bImg, null, null);
					writer.write(null, image, iwp);	
					outStream.flush();					
				} catch(IOException e){
					// File could not be created or opened or saved
					System.out.println("Could not handle image");
					addActionError("Error with file");
				} finally{				
					writer.dispose();
					close(outStream);				
				}
				
												
				try {
					FileUtils.moveFile(f, getFtpDir(), "files/" + FileUtils.thousandize(employee.getId()),
							getFileName(employee.getId()), "jpg", true);
				} catch (Exception e) {
					System.out.println("Error moving "+f);
				}
				if(getActionErrors().size()>0){
					return SUCCESS;
				}
				if(bImg.getWidth()<=175 && bImg.getHeight() <=250){
					employee.setPhoto(extension);
				} else{
					employee.setPhoto(null);					
				}
				employeeDAO.save(employee);
				addActionMessage("Successfully uploaded <b>" + getFileName(employee.getId()) + "</b> file" +
						"<br/> Please crop the image in order to use it as a profile picture");
			}
		}
		if("Save".equals(button)){
			if(employeeID==0){
				addActionError("Invalid Employee");
				return BLANK;
			}
			employee = employeeDAO.find(employeeID);
			if(width>175 || height>250){
				// Too Big
				addActionError("Your cropped image is too big, please try again");
				return SUCCESS;				
			}
			//do img manipulation
			File f = new File(getFtpDir()+"/files/"+FileUtils.thousandize(employeeID)+getFileName(employeeID)+".jpg");
			if(f!=null){
				f.setReadable(true);
				BufferedImage bImg = null;
				try {
					bImg = ImageIO.read(f);
					bImg = bImg.getSubimage(x1, y1, width, height);		
					ImageIO.write(bImg, "jpg", f);
					employee.setPhoto(FileUtils.getExtension(f.getName()));
					employeeDAO.save(employee);
				} catch (IOException e) {
					System.out.println("Could not crop image");
					addActionError("Error with cropping image");
				} finally{
					bImg.flush();
				}
			}
		}
		
		return SUCCESS;
		
	}

	public void close(FileImageOutputStream outStream){
		if(outStream == null)
			return;
		try{
			outStream.close();
		} catch(IOException e){
			System.out.println("Error closing "+outStream.getClass());
			addActionError("Error with file");
		}
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
	public boolean showSavePhoto(){
		int eID = employee.getId();
		File f = new File(getFtpDir()+"/files/"+FileUtils.thousandize(eID)+getFileName(eID)+".jpg");
		if(f.exists()){
			return true;
		}
		return false;
	}

}
