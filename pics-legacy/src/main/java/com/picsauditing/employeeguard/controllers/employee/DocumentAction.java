package com.picsauditing.employeeguard.controllers.employee;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.controllers.helper.RefererHelper;
import com.picsauditing.employeeguard.entities.DocumentType;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.forms.employee.ProfileDocumentInfo;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.validators.document.ProfileDocumentFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.strutsutil.FileDownloadContainer;
import com.picsauditing.util.FileUtils;
import com.picsauditing.validator.Validator;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DocumentAction extends PicsRestActionSupport implements AjaxValidator {

	private static final long serialVersionUID = -6816560877429952204L;

	public static final String EMPLOYEE_SKILL_ID = "employee_skill_id";
	public static final String EMPLOYEE_SKILL_REFERER_URL = "employee/skill/";

	@Autowired
	private FormBuilderFactory formBuilderFactory;
	@Autowired
	private ProfileDocumentService profileDocumentService;
	@Autowired
	private ProfileDocumentFormValidator profileDocumentFormValidator;
	@Autowired
	private ProfileEntityService profileEntityService;

	@FormBinding({"employee_file_create", "employee_file_edit"})
	private DocumentForm documentForm;
	@FormBinding("employee_file_search")
	private SearchForm searchForm;

	private ProfileDocument document;
	private List<ProfileDocumentInfo> documents;

	public String index() throws Exception {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());

		if (isSearch(searchForm)) {
			String searchTerm = searchForm.getSearchTerm();
			List<ProfileDocument> profileDocuments = profileDocumentService.search(searchTerm, profile.getId());
			documents = formBuilderFactory.getProfileDocumentInfoBuilder().buildList(profileDocuments);
		} else {
			List<ProfileDocument> profileDocuments = profileDocumentService.getDocumentsForProfile(profile.getId());
			documents = formBuilderFactory.getProfileDocumentInfoBuilder().buildList(profileDocuments);
		}

		Collections.sort(documents);

		return LIST;
	}

	public String show() {
		document = profileDocumentService.getDocument(getIdAsInt());

		return SHOW;
	}

	public String create() {
		RefererHelper.saveSkillRefererIdToSession();

		return CREATE;
	}

	@SkipValidation
	public String editFileSection() {
		if (documentForm == null) {
			document = profileDocumentService.getDocument(getIdAsInt());
			documentForm = new DocumentForm.Builder().profileDocument(document).build();
		} else {
			document = documentForm.buildProfileDocument();
		}

		return "edit-form";
	}

	// TODO: Use different strategies within a FileService to retrieve the file based on DocumentType
	@SkipValidation
	public String download() {
		ProfileDocument document = profileDocumentService.getDocument(getIdAsInt());

		byte[] output = null;
		try {
			output = FileUtils.getBytesFromFile(profileDocumentService.getDocumentFile(document, getFtpDir()));
		} catch (Exception exception) {
			addActionError("Could not prepare download");
		}

		if (!hasActionErrors()) {
			fileContainer = new FileDownloadContainer.Builder()
					.contentType("text/csv")
					.contentDisposition("attachment; filename=" + document.getFileName())
					.fileInputStream(new ByteArrayInputStream(output)).build();
		}

		return FILE_DOWNLOAD;
	}

	/* other methods */

	public String insert() throws Exception {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());

		if (RefererHelper.sessionHasSkillId()) {
			int skillId = RefererHelper.getSkillIdFromSession();
			profileDocumentService.create(profile, documentForm, getFtpDir(), permissions.getAppUserID(),
					skillId);

			return setUrlForRedirect("/employee-guard/employee/skill/" + skillId);
		}

		ProfileDocument profileDocument = profileDocumentService.create(profile, documentForm, getFtpDir(),
				permissions.getAppUserID());

		return setUrlForRedirect("/employee-guard/employee/file/" + profileDocument.getId());
	}

	private Map<String, Object> getSession() {
		return ActionContext.getContext().getSession();
	}

	public String update() throws Exception {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
		document = documentForm.buildProfileDocument();
		document.setDocumentType(DocumentType.Certificate);

		ProfileDocument profileDocument = profileDocumentService.update(getIdAsInt(), profile, document,
				permissions.getAppUserID(), documentForm.getFile(), documentForm.getFileFileName(), getFtpDir());

		return setUrlForRedirect("/employee-guard/employee/file/" + profileDocument.getId());
	}

	public String delete() throws IOException {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
		profileDocumentService.delete(getIdAsInt(), profile.getId());

		return setUrlForRedirect("/employee-guard/employee/file");
	}

    /* validators */

	@Override
	public Validator getCustomValidator() {
		return profileDocumentFormValidator;
	}

	@Override
	public void validate() {
		ValueStack valueStack = ActionContext.getContext().getValueStack();
		DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

		profileDocumentFormValidator.validate(valueStack, validatorContext);
	}

	/* getters + setters */

	public ProfileDocument getDocument() {
		return document;
	}

	public List<ProfileDocumentInfo> getDocuments() {
		return documents;
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

	public DocumentForm getDocumentForm() {
		return documentForm;
	}

	public void setDocumentForm(DocumentForm documentForm) {
		this.documentForm = documentForm;
	}
}
