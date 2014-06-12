package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

public class MEmployeesManager {

	private Map<Integer,MEmployee> lookup = new HashMap<>();

	public static class MEmployee{
		@Expose
		int id;
		@Expose
		String email;
		@Expose
		String rollupStatus;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getRollupStatus() {
			return rollupStatus;
		}

		public void setRollupStatus(String rollupStatus) {
			this.rollupStatus = rollupStatus;
		}
	}
}
