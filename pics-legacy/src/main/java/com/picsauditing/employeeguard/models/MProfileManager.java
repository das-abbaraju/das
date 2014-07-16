package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.operations.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class MProfileManager extends MModelManager{
	private Map<Integer,MProfile> lookup = new HashMap<>();

	public static Set<MProfile> newCollection(){
		return new HashSet<>();
	}

	private Map<Integer, Profile> entityMap = new HashMap<>();

	private final SupportedOperations operations;
	public SupportedOperations operations() {
		return operations;
	}

	public MProfileManager() {
		operations = new SupportedOperations();
	}

	public MProfile fetchModel(int id){
		return lookup.get(id);
	}

	public MProfile attachWithModel(Profile profile){
		int id = profile.getId();

		if(lookup.get(id)==null){
			lookup.put(id, new MProfile(profile));
		}

		return lookup.get(id);
	}

	private void addEntityToMap(Profile profile){
		entityMap.put(profile.getId(), profile);
	}


	public MProfile copyProfile(Profile profile) throws ReqdInfoMissingException {
		MProfile mProfile = this.fetchModel(profile.getId());
		if(mProfile !=null){
			return mProfile;
		}

		addEntityToMap(profile);
		MProfile model = this.attachWithModel(profile);

		for(MOperations mOperation: mOperations){

			if(mOperation.equals(MOperations.COPY_ID)){
				model.copyId();
			}
			else if(mOperation.equals(MOperations.COPY_APP_USER_ID)){
				model.copyAppUserId();
			}
			else if(mOperation.equals(MOperations.COPY_EMAIL)){
				model.copyEmail();
			}
			else if(mOperation.equals(MOperations.COPY_FIRST_NAME)){
				model.copyFirstName();
			}
			else if(mOperation.equals(MOperations.COPY_LAST_NAME)){
				model.copyLastName();
			}
			else if(mOperation.equals(MOperations.COPY_PHONE)){
				model.copyPhone();
			}

		}

		return model;
	}

	public class SupportedOperations implements MCopyId, MCopyAppUserId,MCopyFirstName,MCopyLastName,MCopyEmail,MCopyPhone  {

		@Override
		public SupportedOperations copyId() {
			mOperations.add(MOperations.COPY_ID);
			return this;
		}

		@Override
		public SupportedOperations copyAppUserId() {
			mOperations.add(MOperations.COPY_APP_USER_ID);
			return this;
		}

		@Override
		public SupportedOperations copyEmail() {
			mOperations.add(MOperations.COPY_EMAIL);
			return this;
		}

		@Override
		public SupportedOperations copyFirstName() {
			mOperations.add(MOperations.COPY_FIRST_NAME);
			return this;
		}

		@Override
		public SupportedOperations copyLastName() {
			mOperations.add(MOperations.COPY_LAST_NAME);
			return this;
		}

		@Override
		public SupportedOperations copyPhone() {
			mOperations.add(MOperations.COPY_PHONE);
			return this;
		}
	}

	public static class MProfile extends MBaseModel implements MCopyId, MCopyAppUserId,MCopyFirstName,MCopyLastName,MCopyEmail,MCopyPhone  {

		private Profile profile;

		private Integer appUserId;

		@Expose
		private String firstName;
		@Expose
		private String lastName;
		@Expose
		private String email;
		@Expose
		private String phone;

		public MProfile(Profile profile) {
			this.profile = profile;
		}

		@Override
		public MProfile copyId(){
			id=profile.getId();
			return this;
		}

		@Override
		public MProfile copyAppUserId() {
			appUserId=profile.getUserId();
			return this;
		}

		@Override
		public MProfile copyEmail() {
			email=profile.getEmail();
			return this;
		}

		@Override
		public MProfile copyFirstName() {
			firstName=profile.getFirstName();
			return this;
		}

		@Override
		public MProfile copyLastName() {
			lastName=profile.getLastName();
			return this;
		}

		@Override
		public MProfile copyPhone() {
			phone=profile.getPhone();
			return this;
		}

		public Integer getAppUserId() {
			return appUserId;
		}

		public void setAppUserId(Integer appUserId) {
			this.appUserId = appUserId;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MProfile mProfile = (MProfile) o;

			if (!profile.equals(mProfile.profile)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return profile.hashCode();
		}

	}



}
