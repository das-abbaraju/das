package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.util.PicsDateFormat;

import java.util.*;

public class MFileManager {

	private Map<Integer, MFileManager.MFile> lookup = new HashMap<>();

	public static Set<MFile> newCollection() {
		return new HashSet<>();
	}

	public MFile attachWithModel(ProfileDocument profileDocument) {
		int id = profileDocument.getId();

		if (lookup.get(id) == null) {
			lookup.put(id, new MFile(profileDocument));
		}

		return lookup.get(id);
	}

	public Set<MFile> copyBasicInfo(List<ProfileDocument> profileDocuments) {
		Set<MFile> mFiles = MFileManager.newCollection();
		for (ProfileDocument profileDocument : profileDocuments) {
			MFile mFile = this.attachWithModel(profileDocument);
			mFile.copyId().copyName().copyCreatedDate().copyExpirationDate();
			mFiles.add(mFile);
		}

		return mFiles;
	}

	public static class MFile {

		@Expose
		private Integer id;
		@Expose
		private String name;
		@Expose
		private String createdDate;
		@Expose
		private String expirationDate;

		private ProfileDocument profileDocument;

		public MFile(ProfileDocument profileDocument) {
			this.profileDocument = profileDocument;
		}

		public MFile copyId() {
			id = profileDocument.getId();
			return this;
		}

		public MFile copyName() {
			name = profileDocument.getName();
			return this;
		}

		public MFile copyCreatedDate() {
			this.createdDate = PicsDateFormat.formatDateIsoOrBlank(profileDocument.getCreatedDate());
			return this;
		}

		public MFile copyExpirationDate() {
			this.expirationDate = DateBean.getEndOfTime().equals(profileDocument.getEndDate()) ? "Never" :
					PicsDateFormat.formatDateIsoOrBlank(profileDocument.getEndDate());
			return this;
		}

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

		public String getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(String createdDate) {
			this.createdDate = createdDate;
		}

		public String getExpirationDate() {
			return expirationDate;
		}

		public void setExpirationDate(String expirationDate) {
			this.expirationDate = expirationDate;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MFile mFile = (MFile) o;

			if (profileDocument != null ? !profileDocument.equals(mFile.profileDocument) : mFile.profileDocument != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return profileDocument != null ? profileDocument.hashCode() : 0;
		}
	}
}
