package com.picsauditing.employeeguard.viewmodel;

import com.google.gson.annotations.SerializedName;

public class SkillReviewModel {

	String name;
	String description;
	String expiration;
	@SerializedName("skill_type")
	String skillType;
	@SerializedName("file_url")
	String fileUrl;
	@SerializedName("image_url")
	String imageUrl;
	String status;
	@SerializedName("verification_date")
	String verificationDate;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getSkillType() {
		return skillType;
	}

	public void setSkillType(String skillType) {
		this.skillType = skillType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVerificationDate() {
		return verificationDate;
	}

	public void setVerificationDate(String verificationDate) {
		this.verificationDate = verificationDate;
	}
}
