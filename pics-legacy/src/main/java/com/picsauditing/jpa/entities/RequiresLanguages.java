package com.picsauditing.jpa.entities;

import javax.persistence.Transient;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: UAung
 * Date: 4/18/13
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RequiresLanguages {
	String getRequiredLanguages();

	void setRequiredLanguages(String requiredLanguages);

	@Transient
	List<String> getLanguages();

	@Transient
	void setLanguages(List<String> languages);

	boolean hasMissingChildRequiredLanguages();

	void addAndRemoveRequiredLanguages(List<String> add, List<String> remove);
}
