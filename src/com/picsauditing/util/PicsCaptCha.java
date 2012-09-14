package com.picsauditing.util;

import java.util.Random;

public class PicsCaptCha {
	private int firstNumber;
	private int secondNumber;
	private int sumNumber;

	public PicsCaptCha() {
		Random randomGenerator = new Random();
		int tempFirst = randomGenerator.nextInt(10);
		int tempSecond = randomGenerator.nextInt(10);

		setFirstNumber(tempFirst);
		setSecondNumber(tempSecond);
		setSumNumber(tempFirst + tempSecond);
	}

	public int getFirstNumber() {
		return firstNumber;
	}

	private void setFirstNumber(int firstNumber) {
		this.firstNumber = firstNumber;
	}

	public int getSecondNumber() {
		return secondNumber;
	}

	private void setSecondNumber(int secondNumber) {
		this.secondNumber = secondNumber;
	}

	public int getSumNumber() {
		return sumNumber;
	}

	public void setSumNumber(int sumNumber) {
		this.sumNumber = sumNumber;
	}
}
