package com.picsauditing.actions;

import com.picsauditing.util.PicsCaptCha;

@SuppressWarnings("serial")
public class PicsCaptChaCheck extends PicsActionSupport {
	private PicsCaptCha picsCaptCha;
	private int sumValue;

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public PicsCaptCha getPicsCaptCha() {
		return picsCaptCha;
	}

	public void setPicsCaptCha(PicsCaptCha picsCaptCha) {
		this.picsCaptCha = picsCaptCha;
	}
	//TODO break it out. need refactor
	public Boolean isPicsCaptchaResponseValid(int uresponse, int sumValue) {		
		for (int failures = 0; failures <= 4; failures++) {
			try {
				if (uresponse == sumValue)
					return true;
			} catch (Exception e) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					// Should never get interrupted
					e1.printStackTrace();
				}
			}
		}

		return null;
	}

	public String getPicscaptchaHtml() {
		picsCaptCha = new PicsCaptCha();
		setSumValue(picsCaptCha.getSumNumber());
		return picsCaptCha.getFirstNumber() + " + " + picsCaptCha.getSecondNumber();
	}

	public int getSumValue() {
		return picsCaptCha.getSumNumber();
	}

	public void setSumValue(int sumValue) {
		this.sumValue = sumValue;
	}
}
