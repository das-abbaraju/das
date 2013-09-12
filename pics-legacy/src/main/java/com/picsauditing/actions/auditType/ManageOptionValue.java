package com.picsauditing.actions.auditType;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.model.i18n.EntityTranslationHelper;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageOptionValue extends ManageOptionComponent {
	@Autowired
	protected AuditDataDAO auditDataDAO;

	private AuditOptionValue value;

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits)
	public String execute() throws Exception {
		if (group == null) {
			addActionError("Missing Option Group");
		}

		return SUCCESS;
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String save() throws Exception {
		if (value.getName() == null) {
			addActionError("Missing answer");
		}

		if (getActionErrors().size() == 0) {
			if (!Strings.isEmpty(value.getUniqueCode()) && value.getUniqueCode().contains(" ")) {
				value.setUniqueCode(value.getUniqueCode().replaceAll(" ", ""));
				addActionMessage("Spaces were removed from the unique code");
			}

			value.setGroup(group);
			value.setAuditColumns(permissions);
			auditOptionValueDAO.save(value);
			EntityTranslationHelper.saveRequiredTranslationsForAuditOptionValue(value, permissions);
		}

		return SUCCESS;
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Delete)
	public String delete() throws Exception {
		List<AuditData> data = auditDataDAO.findByOptionGroupAndValue(group.getId(), value.getIdentifier());

		if (data.size() > 0) {
			addActionError("Option value '" + value.getName() + "' is being used in Audit Data");
		}

		if (getActionErrors().size() == 0) {
			String valueName = value.getName().toString();
			group.getValues().remove(value);
			auditOptionValueDAO.remove(value);
			addActionMessage("Option value " + valueName + " successfully deleted.");

			// Renumber the remaining
			int count = 1;
			for (AuditOptionValue v : group.getValues()) {
				v.setNumber(count);
				count++;
			}
			auditOptionValueDAO.save(group);
		}

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageAudits)
	public String listAjax() throws Exception {
		return "list";
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String editAjax() throws Exception {
		if (value == null) {
			value = new AuditOptionValue();
		}

		return "edit";
	}

	public AuditOptionValue getValue() {
		return value;
	}

	public void setValue(AuditOptionValue value) {
		this.value = value;
	}

	public int getNextNumber() {
		int number = 0;
		for (AuditOptionValue value : group.getValues()) {
			if (number < value.getNumber()) {
				number = value.getNumber();
			}
		}

		return number + 1;
	}

	@Override
	protected void fillSelectedLocales() {
		// TODO Check if we need to update audit option values to have required
		// languages as well
	}
}
