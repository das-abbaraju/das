package com.picsauditing.util;

public class ColorAlternater {
	private int counter = 0;
	private String color1 = "#FFFFFF";
	private String color2 = "";

	public ColorAlternater() {

	}

	public ColorAlternater(int counter) {
		this.counter = counter;
	}

	public String nextColor() {
		this.counter++;
		if ((this.counter % 2) == 1)
			return color1;
		else
			return color2;
	}

	@Deprecated
	public String nextBgColor() {

		return getNextBgColor();
	}

	public String getNextBgColor() {
		String nextColor = this.nextColor();
		return (nextColor.length() > 0) ? "bgcolor=\"" + nextColor + "\"" : "";
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

}
