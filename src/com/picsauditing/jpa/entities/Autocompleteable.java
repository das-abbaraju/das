package com.picsauditing.jpa.entities;

/**
 * 
 * @author kpartridge
 * 
 */
public interface Autocompleteable {

	/**
	 * The value of an autocomplete that gets placed in the hidden input of the form.
	 */
	String getAutocompleteResult();

	/**
	 * The value of an autocomplete that is displayed in the drop list.
	 */
	String getAutocompleteItem();

	/**
	 * The value of an autocomplete that gets stored in the text field when selected.
	 */
	String getAutocompleteValue();

}
