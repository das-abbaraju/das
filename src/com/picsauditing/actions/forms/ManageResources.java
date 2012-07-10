package com.picsauditing.actions.forms;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorFormDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageResources extends PicsActionSupport {
	@Autowired
	protected OperatorAccountDAO operatorDao;
	@Autowired
	private OperatorFormDAO operatorFormDAO;
	@Autowired
	private AccountDAO accountDAO;

	private String formName;
	private Account account;
	private Locale locale;
	private File file;
	protected String fileFileName;
	private OperatorForm resource;
	private OperatorForm parentResource;
	private int selectedId;
	private int id;
	private int parentId;
	private int childId;
	private OperatorAccount operator;
	private String accountName;

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		findResource();
		
		if (parentResource != null) {
			formName = parentResource.getFormName();
			account = parentResource.getAccount();
		} else {
			
		}

		formName = resource.getFormName();
		account = resource.getAccount();
		locale = resource.getLocale();
			
		if (formName == null)
			formName = (parentResource == null) ? "": parentResource.getFormName();
		if (account == null)
			account = (parentResource == null) ? accountDAO.find(1100) : parentResource.getAccount();
		if (locale == null)
			locale = permissions.getLocale();
		

		return SUCCESS;
	}
	
	protected void findResource() {
		if (parentId != 0) {
			parentResource = operatorFormDAO.find(parentId);
		}
		if (id == 0) {
			resource = new OperatorForm();
		} else {
			resource = operatorFormDAO.find(id);
		}
	}

	protected void findOperator() {
		loadPermissions();

		int id = 0;

		if (operator == null) {
			if (permissions.isOperatorCorporate())
				id = permissions.getAccountId();

			if (id != 0)
				operator = operatorDao.find(id);
		}
	}

	public String save() throws Exception {
		findResource();
		
		if (parentResource != null) {
			account = parentResource.getAccount();
		}
		
		if (id==0 && account==null && Strings.isEmpty(accountName)) {
			addActionError(getText("ManageResources.error.NoAccount"));
			return SUCCESS;			
		}
		
		if (id==0 && account==null) {
			@SuppressWarnings("unchecked")
			List<Account> accounts = accountDAO.findWhere("name='" + Strings.escapeQuotes(accountName) + "'");
			if (accounts.size() != 1) {
				addActionError(getText("ManageResources.error.NoAccount"));
				return SUCCESS;							
			}
			account = accounts.get(0);
		}
		
		if (Strings.isEmpty(formName)) {
			addActionError(getText("ManageResources.error.NoName"));
			return SUCCESS;
		}
		
		boolean fileSpecified = (file != null && file.length() > 0);
		if (resource.getId() == 0 && !fileSpecified) {
			addActionError(getText("ManageResources.error.NoFile"));
			return SUCCESS;
		}

		if (fileSpecified) {
			if (!FileUtils.checkFileExtension(FileUtils.getExtension(fileFileName))) {
				addActionError(getText("ManageResources.error.BadExtension"));
				return SUCCESS;
			}
		}

		resource.setFormName(formName);
		
		if (locale != null)
			resource.setLocale(locale);
		if (account != null)
			resource.setAccount(account);
		if (resource.getAccount() == null) {
			resource.setAccount(parentResource.getAccount());
			resource.setParent(parentResource);
		}
		if (resource.getId() == 0)
			resource.setFile("temporary"); // to be updated later

		if (resource.getId() == 0 && parentResource != null) { // check duplicate
			if (resource.getLocale().toString().equals(parentResource.getLocale().toString())) {
				addActionError(getTextParameterized("ManageResources.error.DuplicateLocale", 
						resource.getLocale().getDisplayName(resource.getLocale())));
				return SUCCESS;
			}
			for (OperatorForm child : parentResource.getChildren()) {
				if (resource.getLocale().toString().equals(child.getLocale().toString())) {
					addActionError(getTextParameterized("ManageResources.error.DuplicateLocale", 
							resource.getLocale().getDisplayName(resource.getLocale())));
					return SUCCESS;
				}
			}
			resource.setParent(parentResource);
		}
		
		resource = operatorFormDAO.save(resource);
		
		if (id == 0) {
			id = resource.getId();
		}
		if (parentResource != null)
			id = parentResource.getId();

		if (fileSpecified) {
			resource.setFile(saveFile(resource.getId()));
			operatorFormDAO.save(resource);
			addActionMessage(this.getTextParameterized("ManageResources.message.UploadSuccess", fileFileName));
		} else {
			addActionMessage(getText("ManageResources.message.Updated"));
		}
		
		return redirect("ManageResources.action?id=" + id);
	}

	public String delete() throws Exception {
		findResource();

		String fileName = null;

		for (OperatorForm oForm : resource.getChildren()) {
			fileName = oForm.getFile();
			oForm.setParent(null);
			operatorFormDAO.remove(oForm);
			if (fileName.startsWith("form")) {
				FileUtils.deleteFile(new File(getFtpDir() + "/forms/" + fileName));
			} else {
				FileUtils.deleteFile(new File(getFtpDir() + "/files/" + FileUtils.thousandize(oForm.getId()) + fileName));
			}
		}

		fileName = resource.getFile();
		resource.getChildren().clear();
		operatorFormDAO.remove(resource);
		if (fileName.startsWith("form")) {
			FileUtils.deleteFile(new File(getFtpDir() + "/forms/" + fileName));
		} else {
			FileUtils.deleteFile(new File(getFtpDir() + "/files/" + FileUtils.thousandize(resource.getId()) + fileName));
		}

		return redirect("Resources.action");
	}

	public String remove() throws Exception {
		if (selectedId == 0) {
			addActionError(getText("ManageResources.error.NoFileSelected"));
			return SUCCESS;
		}

		findResource();
		
		OperatorForm selectedForm = null;
		boolean parentSelected = false;
		if (resource.getId() == selectedId) {
			parentSelected = true;
			selectedForm = resource;
		}

		if (selectedForm == null) {
			for (OperatorForm oForm : resource.getChildren()) {
				if (oForm.getId() == selectedId) {
					selectedForm = oForm;
					break;
				}
			}
		}

		if (selectedForm == null) {
			addActionError(getText("ManageResources.error.NoFileSelected"));
			return SUCCESS;
		}

		if (parentSelected) {
			// promote a new parent and shuffle children
			OperatorForm newParent = resource.getChildren().get(0); // select first child to be parent
			newParent.getChildren().clear();
			for (OperatorForm child : resource.getChildren()) {
				if (child.getId() == newParent.getId()) {
					child.setParent(null);
				} else {
					child.setParent(newParent);
					newParent.getChildren().add(child);
				}
			}

			resource.getChildren().clear();
			resource = newParent;
		} else {
			resource.getChildren().remove(selectedForm);
		}

		// delete form
		operatorFormDAO.remove(selectedForm);

		// delete associated files
		String fileName = selectedForm.getFile();
		if (fileName.startsWith("form")) {
			FileUtils.deleteFile(new File(getFtpDir() + "/forms/" + fileName));
		} else {
			FileUtils.deleteFile(new File(getFtpDir() + "/files/" + FileUtils.thousandize(selectedForm.getId()) + fileName));
		}

		return redirect("ManageResources.action?id=" + id);
	}

	public String makeDefault() throws Exception {
		if (selectedId == 0) {
			addActionError(getText("ManageResources.error.NoFileSelected"));
			return SUCCESS;
		}

		findResource();

		OperatorForm selectedForm = null;
		for (OperatorForm child : resource.getChildren()) {
			if (child.getId() == selectedId) {
				selectedForm = child;
				break;
			}
		}
		if (selectedForm == null) {
			addActionError(getText("ManageResources.error.NoFileSelected"));
			return SUCCESS;
		}

		selectedForm.getChildren().clear();
		selectedForm.getChildren().add(resource);
		selectedForm.setParent(null);
		for (OperatorForm child : resource.getChildren()) {
			if (child.getId() != selectedForm.getId()) {
				selectedForm.getChildren().add(child);
				child.setParent(selectedForm);
				operatorFormDAO.save(child);
			}
		}

		resource.getChildren().clear();
		resource.setParent(selectedForm);
		operatorFormDAO.save(resource);
		operatorFormDAO.save(selectedForm);
		
		return redirect("ManageResources.action?id=" + selectedForm.getId());
	}

	private String saveFile(int id) throws Exception {
		String fileName = "resource_" + id;
		FileUtils.moveFile(file, getFtpDir(), "/files/" + FileUtils.thousandize(id), fileName, FileUtils.getExtension(fileFileName), false);
		return fileName + "." + FileUtils.getExtension(fileFileName);
	}

	public List<OperatorForm> getResources() {
		Set<Integer> ids = new HashSet<Integer>();

		if (operator != null) {
			ids.add(1100); // PICS
			ids.add(operator.getId());
			for (OperatorAccount op : operator.getChildOperators()) {
				ids.add(op.getId());
			}
			for (OperatorAccount op : operator.getParentOperators()) {
				ids.add(op.getId());
			}
		}

		return operatorFormDAO.findByOperators(ids);
	}

	public List<Account> getFacilities() {
		if (operator != null) {
			ArrayList<Integer> ids = new ArrayList<Integer>();
				if (permissions.isAdmin()) {
					ids.add(1100);
				}
			ids.add(operator.getId());
			for (Facility facility :operator.getCorporateFacilities()) {
				ids.add(facility.getId());
			}
			for (OperatorAccount oa :operator.getParentOperators()) {
				if (permissions.isAdmin() || !Account.PICS_CORPORATE.contains(oa.getId()))
					ids.add(oa.getId());
			}
			@SuppressWarnings("unchecked")
			List<Account> list = (List<Account>) dao.findWhere(Account.class,
					"t.id IN (" + Strings.implode(ids) + ") AND t.type IN ('Operator', 'Corporate') ", 0,
					"CASE WHEN t.name LIKE 'PICS' THEN 1 ELSE 2 END, t.name");
			return list;
		} else {
		@SuppressWarnings("unchecked")
		List<Account> list = (List<Account>) dao.findWhere(Account.class,
				"t.type IN ('Operator','Corporate') or t.id=1100", 0,
				"CASE WHEN t.name LIKE 'PICS' THEN 1 ELSE 2 END, t.name");
		return list;
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public OperatorForm getResource() {
		return resource;
	}

	public void setForm(OperatorForm resource) {
		this.resource = resource;
	}

	public int getSelectedId() {
		return selectedId;
	}

	public void setSelectedId(int selectedId) {
		this.selectedId = selectedId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getChildId() {
		return childId;
	}

	public void setChildId(int childId) {
		this.childId = childId;
	}

	public OperatorForm getParentResource() {
		return parentResource;
	}

	public void setParentResource(OperatorForm parentResource) {
		this.parentResource = parentResource;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

}
