package com.picsauditing.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

public class NoteFilter {
	public final static String PREFIX = "notes|";
	
	private Map<String, Filter> noteFilters = new HashMap<String, Filter>();
	private Map<String, Filter> taskFilters = new HashMap<String, Filter>();
	
	public NoteFilter(Cookie[] cookies) {
		for(Cookie cookie : cookies) {
			if (cookie.getName().startsWith(PREFIX)) {
				Filter f = new Filter(cookie);
				if (cookie.getName().startsWith(PREFIX + "note|"))
					noteFilters.put(f.getName(), f);
				if (cookie.getName().startsWith(PREFIX + "task|"))
					taskFilters.put(f.getName(), f);
			}
		}
	}

	public Map<String, Filter> getTaskFilters() {
		return taskFilters;
	}

	public void setTaskFilters(Map<String, Filter> taskFilters) {
		this.taskFilters = taskFilters;
	}

	public Map<String, Filter> getNoteFilters() {
		return noteFilters;
	}

	public void setNoteFilters(Map<String, Filter> noteFilters) {
		this.noteFilters = noteFilters;
	}

	public class Filter {
		private String name;
		private String value;
		private String label;

		public Filter(Cookie cookie) {
			name = cookie.getName().substring((PREFIX + "note|").length());
			value = cookie.getValue();
			int pos = value.indexOf("=>");
			if (pos > 0) {
				value = value.substring(0, pos);
				label = value.substring(pos + 2);
			}
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}

}
