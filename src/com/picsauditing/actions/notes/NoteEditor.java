package com.picsauditing.actions.notes;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.ReportFilterNote;

@SuppressWarnings("serial")
public class NoteEditor extends AccountActionSupport implements Preparable {
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

	private AccountDAO accountDAO;
	private NoteDAO noteDAO;

	public NoteEditor(AccountDAO accountDAO, NoteDAO noteDAO) {
		this.accountDAO = accountDAO;
		this.noteDAO = noteDAO;
	}

	public void prepare() throws Exception {
		// TODO Auto-generated method stub
		int noteID = this.getParameter("note.id");
		if (noteID > 0) {
			note = noteDAO.find(noteID);
			account = note.getAccount();
			viewableBy = note.getViewableBy().getId();
			if (viewableBy > 2) {
				viewableByOther = viewableBy;
				viewableBy = 3;
			}
		} else {
			note = new Note();
		}
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (account == null)
			account = accountDAO.find(id);
		// Check permissions to view

		if ("hide".equalsIgnoreCase(button)) {
			permissions.tryPermission(OpPerms.EditNotes, OpType.Delete);
			note.setStatus(NoteStatus.Hidden);
			button = "save";
		}

		if ("remove".equals(button)) {
			permissions.tryPermission(OpPerms.EditNotes, OpType.Edit);
			File[] files = getFiles(note.getId());
			for (File file : files)
				FileUtils.deleteFile(file);
			note.setAttachment(null);
			button = "save";
		}

		if ("save".equalsIgnoreCase(button)) {
			permissions.tryPermission(OpPerms.EditNotes, OpType.Edit);
			if (note.getId() == 0) {
				// This is a new note
				note.setAccount(account);
			}
			if (viewableBy > 2)
				note.setViewableById(viewableByOther);
			else
				note.setViewableById(viewableBy);
			note.setAuditColumns(permissions);

			noteDAO.save(note);

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
			}

			addActionMessage("Successfully saved Note");
		}

		if ("attachment".equals(button)) {
			Downloader downloader = new Downloader(ServletActionContext.getResponse(), ServletActionContext
					.getServletContext());
			File[] files = getFiles(note.getId());
			if (files[0] != null) {
				downloader.download(files[0], note.getAttachment());
				return null;
			} else {
				addActionError("File not found");
			}
		}

		if (viewableBy == 0)
			viewableBy = Account.EVERYONE;
		if (viewableByOther == 0 && !permissions.seesAllContractors()) {
			viewableBy = 3;
			viewableByOther = permissions.getAccountId();
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
}
