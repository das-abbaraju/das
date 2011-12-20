package com.picsauditing.actions.notes;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.ReportFilterNote;

@SuppressWarnings("serial")
public class NoteEditor extends AccountActionSupport {
	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private EmployeeDAO employeeDAO;

	private String mode = "edit";
	private Note note;
	private int viewableBy;
	private int viewableByOther;
	private boolean embedded = true;
	private ReportFilterNote filter = new ReportFilterNote();
	private File file;
	private String fileContentType;
	private String fileFileName;
	private InputStream inputStream;

	private int employeeID;
	public static int RESTRICTED_TO = 3;

	@Override
	public String execute() throws Exception {
		if (note != null && note.getViewableBy() != null) {
			viewableBy = note.getViewableBy().getId();
		}

		if (viewableBy > Account.PRIVATE) {
			viewableByOther = viewableBy;
			viewableBy = RESTRICTED_TO;
		} else if (viewableBy == 0) {
			viewableBy = Account.EVERYONE;
		}

		return mode;
	}

	@RequiredPermission(value = OpPerms.EditNotes, type = OpType.Edit)
	public String save() throws Exception {
		if (note.getId() == 0) {
			// This is a new note
			note.setAccount(account);
		}

		if (viewableBy == RESTRICTED_TO) {
			if (viewableByOther <= RESTRICTED_TO) {
				addActionError("Please select an account to restrict the note to.");
				return mode;
			}
			
			viewableBy = viewableByOther;
		}

		note.setViewableBy(new Account());
		note.getViewableBy().setId(viewableBy);

		if (employeeID > 0) {
			note.setEmployee(new Employee());
			note.getEmployee().setId(employeeID);
		} else
			note.setEmployee(null);

		note.setAuditColumns(permissions);
		noteDAO.save(note);

		if (viewableBy > Account.PRIVATE) {
			viewableByOther = viewableBy;
			viewableBy = RESTRICTED_TO;
		} else {
			viewableByOther = Account.NONE;
		}

		if (file != null) {
			String extension = "";
			if (fileFileName.indexOf(".") != -1) {
				extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
			}

			// will fail for "" too
			if (!FileUtils.checkFileExtension(extension)) {
				addActionError("File type not supported.");
				return mode;
			}
			// delete old files
			File[] files = getFiles(note.getId());
			for (File file : files)
				FileUtils.deleteFile(file);

			FileUtils.moveFile(file, getFtpDir(), "files/" + FileUtils.thousandize(note.getId()),
					PICSFileType.note_attachment.filename(note.getId()), extension, true);

			note.setAttachment(fileFileName);
			noteDAO.save(note);
		}

		addActionMessage("Successfully saved Note");
		return mode;
	}

	@RequiredPermission(value = OpPerms.EditNotes, type = OpType.Delete)
	public String hide() throws Exception {
		note.setStatus(NoteStatus.Hidden);
		return save();
	}

	@RequiredPermission(value = OpPerms.EditNotes, type = OpType.Edit)
	public String remove() throws Exception {
		File[] files = getFiles(note.getId());
		for (File file : files)
			FileUtils.deleteFile(file);
		note.setAttachment(null);
		return save();
	}

	public String attachment() throws Exception {
		Downloader downloader = new Downloader(ServletActionContext.getResponse(),
				ServletActionContext.getServletContext());
		File[] files = getFiles(note.getId());
		if (files[0] != null) {
			downloader.download(files[0], note.getAttachment());
			return null;
		} else {
			addActionError("File not found");
		}

		return mode;
	}

	private File[] getFiles(int noteID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(noteID));
		return FileUtils.getSimilarFiles(dir, PICSFileType.note_attachment.filename(noteID));
	}

	// ///////////////////////////

	public List<Account> getFacilities() {
		List<Account> facilities = accountDAO.findViewableOperators(permissions);
		return facilities;
	}

	public ReportFilterNote getFilter() {
		return filter;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public Map<Integer, String> getViewableByList() {
		Map<Integer, String> list = new HashMap<Integer, String>();
		if (permissions.seesAllContractors())
			list.put(Account.EVERYONE, "Everyone");
		list.put(Account.PRIVATE, "Only Me");
		list.put(3, "Restricted to:");
		return list;
	}

	public int getViewableBy() {
		return viewableBy;
	}

	public void setViewableBy(int viewableBy) {
		this.viewableBy = viewableBy;
	}

	public int getViewableByOther() {
		return viewableByOther;
	}

	public void setViewableByOther(int viewableByOther) {
		this.viewableByOther = viewableByOther;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isEmbedded() {
		return embedded;
	}

	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
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

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public List<Employee> getEmployeeList() {
		return employeeDAO.findByAccount(account);
	}
}
