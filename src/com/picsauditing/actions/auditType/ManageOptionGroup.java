package com.picsauditing.actions.auditType;

import java.util.List;
import java.util.Locale;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.jpa.entities.AuditOptionGroup;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageOptionGroup extends ManageOptionComponent {
	private boolean editOnly = false;

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String save() throws Exception {
		if (group.getName() == null || group.getName().toString().isEmpty())
			addActionError("Missing name");

		if (!Strings.isEmpty(group.getUniqueCode()) && group.getUniqueCode().contains(" ")) {
			group.setUniqueCode(group.getUniqueCode().replaceAll(" ", ""));
			addActionMessage("Spaces were removed in the unique code");
		}
		if (group.hasMissingChildRequiredLanguages())
			addActionError("Changes to required languages must always have at least one language left. "
					+ "Make sure your option group has at least one language.");

		if (getActionErrors().size() == 0) {
			group.setAuditColumns(permissions);
			group = (AuditOptionGroup) auditOptionValueDAO.save(group);

			if (question != null) {
				return setUrlForRedirect("ManageOptionValue.action?group=" + group.getId() + "&question=" + question.getId());
			}
		}

		return SUCCESS;
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Delete)
	public String delete() throws Exception {
		if (group.getQuestions().size() > 0)
			addActionError("Option group '" + group.getName() + "' is still linked to audit questions.");

		String groupName = group.getName();

		if (getActionErrors().size() == 0)
			auditOptionValueDAO.remove(group);

		addActionMessage(String.format("Option Type %s successfully deleted.", groupName));

		return SUCCESS;
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String editAjax() throws Exception {
		if (group == null) {
			group = new AuditOptionGroup();
			addUserPreferredLanguage(group);
		}

		return "edit";
	}

	public List<AuditOptionGroup> getAll() {
		return auditOptionValueDAO.getAllOptionTypes();
	}

	public boolean isEditOnly() {
		return editOnly;
	}

	public void setEditOnly(boolean editOnly) {
		this.editOnly = editOnly;
	}

	@Override
	protected void fillSelectedLocales() {
		if (group != null && !group.getLanguages().isEmpty()) {
			for (String language : group.getLanguages()) {
				selectedLocales.add(new Locale(language));
			}
		}
	}
}
