package com.picsauditing.beans;


import java.util.Date;

import javax.faces.component.UIInput;
import javax.faces.event.ActionEvent;

public class TextManagerBean {
	
	private UIInput textArea;
	private boolean addingText = false;
	private String oldText;
	
	
	
	public UIInput getTextArea() {
		return textArea;
	}

	public void setTextArea(UIInput textArea) {
		this.textArea = textArea;
	}

	public boolean isAddingText() {
		return addingText;
	}

	public void setAddingText(boolean addingText) {
		this.addingText = addingText;
	}

	public String getOldText() {
		return oldText;
	}

	public void setOldText(String oldText) {
		this.oldText = oldText;
	}
	
	public void addText(ActionEvent event){
		addingText = true;
		oldText = (String)getTextArea().getValue();
		getTextArea().setValue(oldText + "\n\n --- " + new Date() + "\n");
	}
	
	public void cancelAddText(ActionEvent event){
		addingText = false;
		getTextArea().setValue(oldText);
	}
	
	public void saveAddText(ActionEvent event){
		addingText = false;
		oldText = (String)getTextArea().getValue();
	}
	
	
	
	

}
