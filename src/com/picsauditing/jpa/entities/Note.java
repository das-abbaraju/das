package com.picsauditing.jpa.entities;

import java.io.StringReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.Permissions;

@SuppressWarnings("serial")
@Entity
@Table(name = "note")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class Note extends BaseTable implements java.io.Serializable {
	private Account account;
	private String summary = "";
	private NoteCategory noteCategory = NoteCategory.General;
	private LowMedHigh priority = LowMedHigh.Med;
	private Account viewableBy;
	private boolean canContractorView = false;
	private NoteStatus status = NoteStatus.Closed;
	private Date followupDate;
	private String body = null;
	private String attachment;
	private String originalText = null;

	public Note() {
	}

	public Note(Account account, User user, String summary) {
		super(user);
		this.account = account;
		this.summary = summary;
		setViewableById(Account.EVERYONE);
	}

	@ManyToOne
	@JoinColumn(name = "accountID", nullable = false, updatable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Column(name = "summary", length = 150, nullable = false)
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "noteCategory", nullable = false)
	public NoteCategory getNoteCategory() {
		return noteCategory;
	}

	public void setNoteCategory(NoteCategory noteCategory) {
		this.noteCategory = noteCategory;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "priority")
	public LowMedHigh getPriority() {
		return priority;
	}

	public void setPriority(LowMedHigh priority) {
		this.priority = priority;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "viewableBy")
	public Account getViewableBy() {
		return viewableBy;
	}

	public void setViewableByOperator(Permissions permissions) {
		setViewableById(permissions.getTopAccountID());
	}

	public void setViewableBy(Account viewableBy) {
		this.viewableBy = viewableBy;
	}

	public void setViewableById(int viewableBy) {
		if (viewableBy == 0)
			this.viewableBy = null;
		this.viewableBy = new Account();
		this.viewableBy.setId(viewableBy);
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	public boolean isCanContractorView() {
		return canContractorView;
	}

	public void setCanContractorView(boolean canContractorView) {
		this.canContractorView = canContractorView;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "status")
	public NoteStatus getStatus() {
		return status;
	}

	public void setStatus(NoteStatus status) {
		this.status = status;
	}

	@Temporal(TemporalType.DATE)
	public Date getFollowupDate() {
		return followupDate;
	}

	public void setFollowupDate(Date followupDate) {
		this.followupDate = followupDate;
	}

	public String getBody() {
		return body;
	}

	@Transient
	public String getBodyHtml() {
		return Utilities.escapeHTML(body);
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Note other = (Note) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Transient
	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public boolean matchesFormat() {
		String expression = "^([0-9]{1,4}/[0-9]{1,2}/[0-9]{1,4})( [0-9]{1,2}:[0-9]{2} [AP]M .{3}?)? [\\(]*(.*?)[\\)]*: (.*)";
		Pattern pattern = Pattern.compile(expression, Pattern.CANON_EQ);
		Matcher matcher = pattern.matcher(getOriginalText());
		return matcher.find();
	}

	public boolean convertNote() {
		// ([0-9]{1,4}/[0-9]{1,2}/[0-9]{1,4})( [0-9]{1,2}:[0-9]{2} [AP]M .{3}?)?
		// [\(]*(.*?)[\)]*: (.*)
		// System.out.println(oldNote);

		String expression = "^([0-9]{1,4}/[0-9]{1,2}/[0-9]{1,4})( [0-9]{1,2}:[0-9]{2} [AP]M .{3}?)? [\\(]*(.*?)[\\)]*: (.*)";
		Pattern pattern = Pattern.compile(expression, Pattern.CANON_EQ);
		Matcher matcher = pattern.matcher(getOriginalText());
		if (matcher.find()) {

			String date = matcher.group(1);
			String time = matcher.group(2);
			String who = matcher.group(3);
			String what = matcher.group(4);

			Date noteDate = null;

			String tempDate = "";

			if (date != null && date.trim().length() > 0) {
				tempDate += date.trim();
			}

			try {

				if (time != null && time.trim().length() > 0) {
					tempDate += " " + time.trim();
					noteDate = DateBean.parseDateTime(tempDate);
				} else {

					if (who.indexOf(":") != -1) {
						return false;
					}
					noteDate = DateBean.parseDate(tempDate);
				}

				if (noteDate == null) {
					return false;
				}

				setCreationDate(noteDate);
			} catch (Exception e) {
				return false;
			}

//			if (who != null && who.trim().length() > 0) {
//				setUserName(who);
//			}

			if (what == null || what.trim().length() == 0) {
				return false;
			}

			StringBuilder summaryBuilder = new StringBuilder();
			StringBuilder bodyBuilder = new StringBuilder(); // couldn't resist
			StringReader originalReader = new StringReader(what);

			try {

				char c;
				int charInt;
				int position = 0;
				boolean flippedToBody = false;
				while ((charInt = originalReader.read()) != -1) {
					position++;
					c = (char) charInt;

					if (!flippedToBody) {
						if (position == 99) {
							flippedToBody = true;
						}
						if (c == '\n') {
							flippedToBody = true;
							continue;
						}
						summaryBuilder.append(c);
					} else {
						bodyBuilder.append(c);
					}
				}
			} catch (Exception e) {
				return false;
			}

			setSummary(summaryBuilder.toString());
			setBody(bodyBuilder.toString());
		} else {
			throw new RuntimeException("could not parse note");
		}
		return true;
	}

	public static void main(String[] args) {

		String expression = "^([0-9]{1,4}/[0-9]{1,2}/[0-9]{1,4})( [0-9]{1,2}:[0-9]{2} [AP]M .{3}?)? [\\(]*(.*?)[\\)]*: (.*)";
		Pattern pattern = Pattern.compile(expression, Pattern.CANON_EQ);

		Matcher matcher = pattern.matcher("5/19/04 3:00 PM PDT: sent welcome email");
		if (matcher.find()) {

			String date = matcher.group(1);
			String time = matcher.group(2);
			String who = matcher.group(3);
			String what = matcher.group(4);
			System.out.println("who: " + who);
			System.out.println("what: " + what);
			System.out.println("date: " + date);
			System.out.println("time: " + time);

		}

	}

}
