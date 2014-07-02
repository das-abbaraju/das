package com.picsauditing.controller;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.forms.AddAnotherForm;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.util.Strings;
import com.picsauditing.util.TranslationUtil;
import org.apache.commons.lang.math.NumberUtils;

import java.util.List;

public class PicsRestActionSupport extends PicsActionSupport {

	private static final long serialVersionUID = -5217323549235443366L;

	private EGI18n egi18n = new EGI18n();

	// Constants for View Names
	public static final String CREATE = "create";
	public static final String LIST = "list";
	public static final String EDIT = "edit";
	public static final String SHOW = "show";

	protected String id;

	public final String getId() {
		return id;
	}

	public final void setId(String id) {
		this.id = id;
	}

	protected boolean isSearch(SearchForm searchForm) {
		return searchForm != null && Strings.isNotEmpty(searchForm.getSearchTerm());
	}

	protected boolean addAnother(AddAnotherForm form) {
		return form != null && form.isAddAnother();
	}

	protected int getIdAsInt() {
		return NumberUtils.toInt(id);
	}

	@Override
	public boolean hasKey(String key) {
		return egi18n.hasKey(key);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getText(String aTextName, String defaultValue, List<?> args, ValueStack stack) {
		useKey(aTextName);
		return egi18n.getText(aTextName, defaultValue, args, stack);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
		useKey(key);
		return egi18n.getText(key, defaultValue, args, stack);

	}

	private void useKey(String key) {
		TranslationUtil.validateTranslationKey(key);
	}

}
