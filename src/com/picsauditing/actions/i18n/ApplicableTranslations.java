package com.picsauditing.actions.i18n;

import java.sql.SQLException;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ApplicableTranslations extends PicsActionSupport {
	private Database database = new Database();
	private String type;

	@Override
	@RequiredPermission(value = OpPerms.DevelopmentEnvironment)
	public String execute() throws Exception {
		return SUCCESS;
	}

	/**
	 * Update all related translations to applicable, then update all expired
	 * objects to force translations to be marked non applicable.
	 * 
	 * @throws SQLException
	 */
	@RequiredPermission(value = OpPerms.DevelopmentEnvironment)
	public String update() throws SQLException {
		if (!Strings.isEmpty(type)) {
			if ("Audit Question".equals(type)) {
				// Only supporting Audit Question right now...
				database.executeUpdate("UPDATE app_translation SET applicable = 1 "
						+ "WHERE msgKey RLIKE 'AuditQuestion\\\\.[0-9]*\\\\.'");

				List<AuditQuestion> expired = dao.findWhere(AuditQuestion.class, "t.expirationDate <= CURDATE()");
				// List<Field> translatableStrings = new ArrayList<Field>();

				// for (Field field : AuditQuestion.class.getDeclaredFields()) {
				// if (field.getType().equals(TranslatableString.class)) {
				// translatableStrings.add(field);
				// }
				// }

				for (AuditQuestion question : expired) {
					String msgKeys = "";

					// for (Field field : translatableStrings) {
					// if (!Strings.isEmpty(msgKeys)) {
					// msgKeys += ", ";
					// }
					//
					// msgKeys += "'" + question.getI18nKey(field.getName()) +
					// "'";
					// }

					database.executeUpdate(String.format(
							"UPDATE app_translation SET applicable = 0 WHERE msgKey IN (%s)", msgKeys));
				}

				addActionMessage(String.format("Successfully set translations not applicable for %d expired %ss",
						expired.size(), type));
			}
		}

		return SUCCESS;
	}

	private String getAllTranslationKeysForQuestion(AuditQuestion question) {
		return Strings.EMPTY_STRING;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getAvailableTypes() {
		return new String[] { "Audit Question" };
	}
}