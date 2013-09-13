package com.picsauditing.PICS;

import java.util.List;
import java.util.Vector;

public abstract class Grepper<T> {

	public abstract boolean check(T t);

	public List<T> grep(List<T> in) {

		List<T> output = new Vector<T>();

		if (in != null) {

			for (T t : in) {
				if (check(t)) {
					output.add(t);
				}
			}
		}

		return output;
	}

}
