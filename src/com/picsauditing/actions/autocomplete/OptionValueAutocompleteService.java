package com.picsauditing.actions.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.util.Strings;

// TODO: Find out if this is even working properly
public class OptionValueAutocompleteService extends AbstractAutocompleteService<Autocompleteable> {
	
	@Autowired
	protected AuditOptionValueDAO valueDAO;

	
	@Override
	protected Collection<Autocompleteable> getItems(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}
		
		Collection<Autocompleteable> result = new ArrayList<Autocompleteable>();
		// this is where the implementation belongs
		return result;
	}

	@Override
	protected Object getKey(Autocompleteable item) {
		return item.getAutocompleteItem();
	}

	@Override
	protected Object getValue(Autocompleteable item, Permissions permissions) {
		return item.getAutocompleteValue(); 	
	}
}
