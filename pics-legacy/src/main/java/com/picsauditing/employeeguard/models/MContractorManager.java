package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

public class MContractorManager {
	private Map<Integer,MContractor> lookup = new HashMap<>();


	public static class MContractor {
		@Expose
		private int id;
		@Expose
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
