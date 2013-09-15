package com.picsauditing;

public class EntityIdGenerator {

	static private int nextID = 0;

	static public int next() {
		nextID++;
		return nextID;
	}
}
