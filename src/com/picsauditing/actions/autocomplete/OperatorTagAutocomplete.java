package com.picsauditing.actions.autocomplete;

import org.springframework.web.util.HtmlUtils;

import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.OperatorTag;

@SuppressWarnings("serial")
public class OperatorTagAutocomplete extends AutocompleteActionSupport<OperatorTag> {

	private OperatorTagDAO dao;

	public OperatorTagAutocomplete(OperatorTagDAO dao) {
		this.dao = dao;
	}

	@Override
	protected void findItems() {
		items = dao.findByOperator(Integer.parseInt(q), true);
	}

	@Override
	protected void createOutput() {
		for (OperatorTag item : items) {
			outputBuffer.append(item.getId()).append("|");
			outputBuffer.append(HtmlUtils.htmlEscape(item.getTag())).append("\n");
		}
	}
}
