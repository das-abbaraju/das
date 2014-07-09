package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.ProfileDocument;

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
			mFile.copyId().copyName().copyCreatedDate().copyExpirationDate().copyVerificationDate();
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
		private Long createdDate;
		@Expose
		private Long expirationDate;
		@Expose
		private Long verificationDate;
		@Expose
		private Boolean neverExpires;

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
			this.createdDate = profileDocument.getCreatedDate().getTime();
			return this;
		}

		public MFile copyExpirationDate() {
			Date expirationDate = profileDocument.getEndDate();

			this.expirationDate = expirationDate != null ? expirationDate.getTime() : null;
			this.neverExpires = profileDocument.isDoesNotExpire();

			return this;
		}

		public MFile copyVerificationDate() {
			Date startDate = profileDocument.getStartDate();

			this.verificationDate = startDate != null ? startDate.getTime() : null;

			return this;
		}

		public Integer getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Long getCreatedDate() {
			return createdDate;
		}

		public Long getExpirationDate() {
			return expirationDate;
		}

		public Long getVerificationDate() {
			return verificationDate;
		}

		public Boolean getNeverExpires() {
			return neverExpires;
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
