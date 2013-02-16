package com.picsauditing.actions.notes;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import com.picsauditing.dao.*;
import com.picsauditing.toggle.FeatureToggle;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Grepper;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.ReportFilterNote;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class NoteEditor extends AccountActionSupport {
	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
    @Autowired
    private UserSwitchDAO userSwitchDAO;
    @Autowired
    private FeatureToggle featureToggleChecker;

    private String mode = "edit";
	private Note note;
	private int viewableBy;
	private int viewableByOther;
	private boolean embedded = true;
	private final ReportFilterNote filter = new ReportFilterNote();
	private File file;
	private String fileContentType;
	private String fileFileName;
	private InputStream inputStream;
    private int defaultRestrictedViewableAccountID;

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
		} else if (viewableBy == Account.NONE) {
			viewableBy = Account.EVERYONE;
		}

		if (permissions.hasPermission(OpPerms.Billing) || permissions.isOperatorCorporate()) {
			viewableBy = RESTRICTED_TO;
			viewableByOther = permissions.getTopAccountID();
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
		note.getViewableBy().setId((viewableBy == Account.NONE) ? Account.EVERYONE : viewableBy);

		if (employeeID > 0) {
			note.setEmployee(new Employee());
			note.getEmployee().setId(employeeID);
		} else
			note.setEmployee(null);

		updateInternalSalesInfo(permissions, account);

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

	protected void updateInternalSalesInfo(Permissions permissions, Account account) {
		if (permissions.hasGroup(User.GROUP_ISR) && account.isContractor()) {
			ContractorAccountDAO contractorDAO = SpringUtils
					.getBean("ContractorAccountDAO", ContractorAccountDAO.class);
			ContractorAccount ca = contractorDAO.find(account.getId());
			ca.setLastContactedByInsideSales(permissions.getUserId());
			contractorDAO.save(ca);
		}
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

	public List<Account> getFacilities() {
        final List<Account> facilities = new ArrayList<Account>();
        if (permissions.isOperatorCorporate()) {
            if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_DO_NOT_USE_SWITCHTO_ACCOUNTS_IN_NOTE_RESTRICTION)) {
                populateFacilitiesWithPrimaryCorpOnTopAndSwitchTosUnderneath(facilities);
            } else if (permissions.isCorporate()) {
                facilities.add(accountDAO.find(permissions.getPrimaryCorporateAccountID()));
            } else if (permissions.isOperator()) {
                facilities.add(accountDAO.find(permissions.getAccountId()));
            }
        } else {
            facilities.addAll(accountDAO.findNoteRestrictionOperators(permissions));
        }

        return facilities;
	}

    private void populateFacilitiesWithPrimaryCorpOnTopAndSwitchTosUnderneath(List<Account> facilities) {
        Account primaryCorporate = accountDAO.find(permissions.getPrimaryCorporateAccountID());
        addSwitchToAccountsSkipPimaryCorporate(facilities, primaryCorporate);
        Collections.sort(facilities, new Comparator<Account>() {
            @Override
            public int compare(Account o1, Account o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        facilities.add(0, primaryCorporate);
    }

    private void addSwitchToAccountsSkipPimaryCorporate(final List<Account> facilities, Account primaryCorporate) {
        facilities.addAll(new Grepper<Account>() {
            @Override
            public boolean check(Account t) {
                return !facilities.contains(t);
            }
        }.grep(userSwitchDAO.findAccountsByUserId(permissions.getUserId())));
        facilities.remove(primaryCorporate);
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
			list.put(Account.EVERYONE, getText("NoteViewable.Everyone"));
		list.put(Account.PRIVATE, getText("NoteViewable.Private"));
		list.put(3, getText("NoteViewable.Restricted"));
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

	public String getAccountType() {
		return getText("AccountType." + accountDAO.find(id).getType());
	}

    public int getDefaultRestrictedViewableAccountID() {
        if (permissions.isCorporate()) {
            return permissions.getPrimaryCorporateAccountID();
        } else if (permissions.isOperator()) {
            if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_DO_NOT_USE_SWITCHTO_ACCOUNTS_IN_NOTE_RESTRICTION)) {
                return permissions.getPrimaryCorporateAccountID();
            } else {
                return permissions.getAccountId();
            }
        } else {
            return permissions.getTopAccountID();
        }
    }
}
