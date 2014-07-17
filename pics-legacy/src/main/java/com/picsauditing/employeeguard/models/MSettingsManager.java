package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.operations.*;
import com.picsauditing.model.i18n.KeyValue;

import java.util.*;

public class MSettingsManager extends MModelManager{
	private Map<Integer,MSettings> lookup = new HashMap<>();

	public static Set<MSettings> newCollection(){
		return new HashSet<>();
	}

	private Map<Integer, Profile> entityMap = new HashMap<>();

	private final SupportedOperations operations;
	public SupportedOperations operations() {
		return operations;
	}

	public MSettingsManager() {
		operations = new SupportedOperations();
	}

	public MSettings fetchModel(int id){
		return lookup.get(id);
	}

	public MSettings attachWithModel(Profile profile){
		int id = profile.getId();

		if(lookup.get(id)==null){
			lookup.put(id, new MSettings(profile));
		}

		return lookup.get(id);
	}

	private void addEntityToMap(Profile profile){
		entityMap.put(profile.getId(), profile);
	}


	public MSettings copyProfile(Profile profile) throws ReqdInfoMissingException {
		MSettings mSettings = this.fetchModel(profile.getId());
		if(mSettings !=null){
			return mSettings;
		}

		addEntityToMap(profile);
		MSettings model = this.attachWithModel(profile);

		for(MOperations mOperation: mOperations){

			if(mOperation.equals(MOperations.COPY_LOCALE)){
				model.copyLocale();
			}
			else if(mOperation.equals(MOperations.COPY_APP_USER_ID)){
				model.copyAppUserId();
			}

		}

		return model;
	}

	public class SupportedOperations implements MCopyLocale, MCopyAppUserId  {

		@Override
		public SupportedOperations copyLocale() {
			mOperations.add(MOperations.COPY_LOCALE);
			return this;
		}

		@Override
		public SupportedOperations copyAppUserId() {
			mOperations.add(MOperations.COPY_APP_USER_ID);
			return this;
		}
	}

	public static class MSettings extends MBaseModel implements MCopyLocale, MCopyAppUserId  {

		private Profile profile;

		@Expose
		private Integer appUserId;
		@Expose
		private MIdAndName language;
		@Expose
		private MIdAndName dialect;

		public MSettings(Profile profile) {
			this.profile = profile;
		}

		public Locale prepareLocale(){
			if(language!=null && dialect!=null) {
				return new Locale.Builder().setLanguage(language.getId()).setRegion(dialect.getId()).build();
			}
			else if(language!=null ){
				return new Locale.Builder().setLanguage(language.getId()).build();
			}

			return null;
		}

		@Override
		public MSettings copyLocale() {
			Locale locale = profile.getSettings().getLocale();
			this.language= extractLanguage(locale);
			this.dialect = extractDialect(locale);
			return this;
		}

		@Override
		public MSettings copyAppUserId() {
			this.appUserId = profile.getUserId();
			return this;
		}

		private MIdAndName extractLanguage(Locale locale){
			return new MIdAndName.Builder().id(locale.getLanguage()).name(locale.getDisplayLanguage()).build();
		}

		private MIdAndName extractDialect(Locale locale){
			return new MIdAndName.Builder().id(locale.getCountry()).name(locale.getDisplayCountry()).build();
		}

		public MIdAndName getLanguage() {
			return language;
		}

		public void setLanguage(MIdAndName language) {
			this.language = language;
		}

		public MIdAndName getDialect() {
			return dialect;
		}

		public void setDialect(MIdAndName dialect) {
			this.dialect = dialect;
		}

		public Integer getAppUserId() {
			return appUserId;
		}

		public void setAppUserId(Integer appUserId) {
			this.appUserId = appUserId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MSettings mSettings = (MSettings) o;

			if (!profile.equals(mSettings.profile)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return profile.hashCode();
		}


	}






}
