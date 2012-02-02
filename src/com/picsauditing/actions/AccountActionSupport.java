package com.picsauditing.actions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class AccountActionSupport extends PicsActionSupport {

	protected int id;
	// protected Account account;
	protected String subHeading = null;
	protected List<Note> notes;
	protected NoteCategory noteCategory = NoteCategory.General;

	@Autowired
	protected AccountDAO accountDAO;
	@Autowired
	private NoteDAO noteDao;
	@Autowired
	private CountryDAO countryDAO;
	@Autowired
	private StateDAO stateDAO;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Delete this method and property when we start i18n
	 * 
	 * @return
	 */
	public String getSubHeading() {
		return subHeading;
	}

	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Account getAccount() {
		if (account == null) {
			loadPermissions();
			account = accountDAO.find(permissions.getAccountId(), permissions.getAccountType());
		}
		return account;
	}

	/************* NOTES ************/
	@Deprecated
	protected NoteDAO getNoteDao() {
		return noteDao;
	}

	public NoteCategory getNoteCategory() {
		return noteCategory;
	}

	public void setNoteCategory(NoteCategory noteCategory) {
		this.noteCategory = noteCategory;
	}

	/**
	 * Get a list of notes up to the limit, using the given where clause
	 * 
	 * @param where
	 *            should be in the format of "AND field=1", can be an empty string
	 * @param limit
	 *            ie 25
	 * @return
	 */
	public List<Note> getNotes(String where, int firstLimit, int limit) {
		if (notes == null)
			notes = noteDao.getNotes(id, permissions, "status IN (1,2)" + where, firstLimit, limit);

		return notes;
	}

	/**
	 * Get a list of 5 embedded notes, based on noteCategory
	 * 
	 * @return
	 */
	public List<Note> getNotes() {
		return getNotes(" AND noteCategory IN ('" + noteCategory.toString() + "','General')", 0, 5);
	}

	protected Note addNote(Account account, String newNote) {
		return addNote(account, newNote, noteCategory);
	}

	protected Note addNote(Account account, String newNote, NoteCategory noteCategory) {
		return addNote(account, newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, null, null);
	}

	protected Note addNote(Account account, String newNote, NoteCategory noteCategory, int viewableBy) {
		return addNote(account, newNote, noteCategory, LowMedHigh.Low, true, viewableBy, null, null);
	}

	protected Note addNote(Account account, String newNote, NoteCategory noteCategory, LowMedHigh priority,
			boolean canContractorView, int viewableBy, User user) {
		return addNote(account, newNote, noteCategory, LowMedHigh.Low, true, viewableBy, user, null);
	}

	protected Note addNote(Account account, String newNote, NoteCategory category, LowMedHigh priority,
			boolean canContractorView, int viewableBy, User user, Employee employee) {
		Note note = new Note();
		note.setAccount(account);
		note.setAuditColumns(permissions);
		note.setSummary(newNote);
		note.setPriority(priority);
		note.setNoteCategory(category);
		note.setViewableById(viewableBy);
		note.setCanContractorView(canContractorView);
		note.setStatus(NoteStatus.Closed);
		note.setEmployee(employee);
		dao.save(note);
		return note;
	}

	public int getViewableByAccount(Account account) {
		if (account != null)
			return account.getId();
		return Account.EVERYONE;
	}

	/***** END of NOTES *****/

	@Deprecated
	public CountryDAO getCountryDAO() {
		return countryDAO;
	}

	@Deprecated
	public StateDAO getStateDAO() {
		return stateDAO;
	}

	public List<Country> getCountryList() {
		List<Country> countryList = countryDAO.findAll();
		Collections.sort(countryList, new Comparator<Country>() {
			@Override
			public int compare(Country o1, Country o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		countryList.add(0, countryDAO.find("GB"));
		countryList.add(0, countryDAO.find("CA"));
		countryList.add(0, countryDAO.find("US"));

		return countryList;
	}

	@SuppressWarnings("unchecked")
	public List<State> getStateList() {
		List<State> results = Collections.emptyList();
		if (account == null) {
			results = stateDAO.findAll();
		} else {
			results = stateDAO.findByCountry(account.getCountry());
		}

		Collections.sort(results, new Comparator<State>() {
			@Override
			public int compare(State o1, State o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		return results;
	}

	public List<State> getStateList(String countries) {
		List<State> result;
		if (countries == null) {
			result = stateDAO.findAll();
		} else {
			boolean negative = false;
			if (countries.startsWith("!")) {
				countries = countries.replace("!", "");
				negative = true;
			}
			result = stateDAO.findByCountries(Arrays.asList(countries.split("[|]")), negative);
		}

		Collections.sort(result, new Comparator<State>() {
			@Override
			public int compare(State o1, State o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		return result;
	}

	public boolean isShowMoreNotes() {
		if (account.getType().equals("Contractor")) {
			return permissions.hasPermission(OpPerms.ContractorDetails);
		}
		return true;
	}

	public AccountStatus[] getStatusList() {
		return AccountStatus.values();
	}
}
