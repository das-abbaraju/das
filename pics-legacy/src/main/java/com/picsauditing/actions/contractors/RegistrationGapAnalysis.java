package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.picsauditing.model.account.AccountStatusChanges;
import org.apache.commons.beanutils.BasicDynaBean;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectAccount.Type;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RegistrationGapAnalysis extends PicsActionSupport {
	// Up to two character changes in a string are allowed (e.g. "Jo Glyn" and
	// "Joe Glen")
	private static final int LEVENSHTEIN_DISTANCE_THRESHOLD = 2;

	private Database database = new Database();

	private int daysAgo = 30;
	private ContractorAccount duplicate;
	private ContractorAccount original;
	private Map<ContractorAccount, Set<Match>> matches = new TreeMap<ContractorAccount, Set<Match>>();

	@Override
	public String execute() throws Exception {
		if (!permissions.isPicsEmployee()) {
			throw new NoRightsException("PICS Employee");
		}

		buildPossibleMatches();

		return SUCCESS;
	}

	public String deactivateDuplicate() throws Exception {
		if (duplicate != null) {
			duplicate.setName(String.format("%s (DUPLICATE OF #%d)", original.getName(), original.getId()));
			duplicate.setReason(AccountStatusChanges.DUPLICATE_MERGED_ACCOUNT_REASON);
			duplicate.setStatus(AccountStatus.Deleted);
			duplicate.setAuditColumns(permissions);
			dao.save(duplicate);

			addActionMessage(getTextParameterized("RegistrationGapAnalysis.Deactivated", duplicate.getName(),
					original.getId(), original.getName()));
		}

		return setUrlForRedirect("RegistrationGapAnalysis.action");
	}

	public int getDaysAgo() {
		return daysAgo;
	}

	public void setDaysAgo(int daysAgo) {
		this.daysAgo = daysAgo;
	}

	public void setDuplicate(ContractorAccount duplicate) {
		this.duplicate = duplicate;
	}

	public void setOriginal(ContractorAccount original) {
		this.original = original;
	}

	public Map<ContractorAccount, Set<Match>> getMatches() {
		return matches;
	}

	private void buildPossibleMatches() throws Exception {
		List<ContractorAccount> recentlyRegistered = getRecentlyRegistered();
		List<ContractorAccount> requestedContractors = getRequestedContractors();

		Logger logger = LoggerFactory.getLogger("org.perf4j.DebugTimingLogger");
		StopWatch stopWatch = new Slf4JStopWatch(logger);
		stopWatch.start("Registration Gap Analysis Matching");

		for (ContractorAccount registered : recentlyRegistered) {
			for (ContractorAccount requested : requestedContractors) {
				Map<MatchType, String> matchesOn = compareRegisteredWithRequested(registered, requested);
				if (!matchesOn.isEmpty()) {
					Match match = new Match(requested, matchesOn);

					if (matches.get(registered) == null) {
						matches.put(registered, new TreeSet<Match>());
					}

					matches.get(registered).add(match);
				}
			}
		}

		stopWatch.stop();
	}

	private List<ContractorAccount> getRecentlyRegistered() throws Exception {
		SelectAccount sql = createQuery();

		sql.addWhere(String.format("a.creationDate > DATE_SUB(NOW(), INTERVAL %d DAY)", daysAgo));
		sql.addWhere("a.status IN ('Active', 'Pending')");

		List<BasicDynaBean> results = database.select(sql.toString(), false);
		List<ContractorAccount> recentlyRegistered = new ArrayList<ContractorAccount>();

		for (BasicDynaBean row : results) {
			recentlyRegistered.add(createContractorAndUser(row));
		}

		return recentlyRegistered;
	}

	private List<ContractorAccount> getRequestedContractors() throws Exception {
		SelectAccount sql = createQuery();

		sql.addWhere("a.status IN ('Requested')");

		List<BasicDynaBean> results = database.select(sql.toString(), false);
		List<ContractorAccount> requestedContractors = new ArrayList<ContractorAccount>();

		for (BasicDynaBean row : results) {
			requestedContractors.add(createContractorAndUser(row));
		}

		return requestedContractors;
	}

	private SelectAccount createQuery() {
		SelectAccount sql = new SelectAccount();
		sql.setType(Type.Contractor);

		sql.addJoin("LEFT JOIN users u ON u.id = a.contactID");

		sql.addField("a.id");
		sql.addField("a.name");
		sql.addField("a.address");
		sql.addField("a.city");
		sql.addField("a.zip");
		sql.addField("c.taxID");
		sql.addField("u.id contactID");
		sql.addField("u.name contact");
		sql.addField("u.email");
		sql.addField("u.phone");

		return sql;
	}

	private ContractorAccount createContractorAndUser(BasicDynaBean row) {
		ContractorAccount contractor = new ContractorAccount();
		contractor.setId(Integer.parseInt(getPropertyNullSafe(row, "id")));
		contractor.setName(getPropertyNullSafe(row, "name"));
		contractor.setAddress(getPropertyNullSafe(row, "address"));
		contractor.setCity(getPropertyNullSafe(row, "city"));
		contractor.setZip(getPropertyNullSafe(row, "zip"));
		contractor.setTaxId(getPropertyNullSafe(row, "taxID"));

		if (!Strings.isEmpty(getPropertyNullSafe(row, "contactID"))) {
			User user = new User();
			user.setId(Integer.parseInt(getPropertyNullSafe(row, "contactID")));
			user.setName(getPropertyNullSafe(row, "contact"));
			user.setEmail(getPropertyNullSafe(row, "email"));
			user.setPhone(getPropertyNullSafe(row, "phone"));

			contractor.setPrimaryContact(user);
		}

		return contractor;
	}

	private Map<MatchType, String> compareRegisteredWithRequested(ContractorAccount registered,
			ContractorAccount requested) {
		Map<MatchType, String> matchesOn = new TreeMap<MatchType, String>();

		if (stringsAreSimilar(registered.getName(), requested.getName())) {
			matchesOn.put(MatchType.Name, registered.getName() + ", " + requested.getName());
		}

		if (stringsAreSimilar(registered.getAddress(), requested.getAddress())) {
			String registeredFullAddress = String.format("%s, %s %s", registered.getAddress(), registered.getCity(),
					registered.getZip());
			String requestedFullAddress = String.format("%s, %s %s", requested.getAddress(), requested.getCity(),
					requested.getZip());

			if (stringsAreSimilar(registered.getCity(), requested.getCity())
					|| strippedStartsWithEither(registered.getZip(), requested.getZip())) {
				matchesOn.put(MatchType.Address,
						String.format("[%s], [%s]", registeredFullAddress, requestedFullAddress));
			}
		}

		if (strippedStartsWithEither(registered.getTaxId(), requested.getTaxId())) {
			matchesOn.put(MatchType.TaxID, registered.getTaxId() + ", " + requested.getTaxId());
		}

		User registeredUser = registered.getPrimaryContact();
		User requestedUser = requested.getPrimaryContact();

		if (registeredUser != null && requestedUser != null) {
			if (stringsAreSimilar(registeredUser.getName(), requestedUser.getName())) {
				matchesOn.put(MatchType.Contact, registeredUser.getName() + ", " + requestedUser.getName());
			}

			if (stringsAreSimilar(registeredUser.getEmail(), requestedUser.getEmail())) {
				matchesOn.put(MatchType.Email, registeredUser.getEmail() + ", " + requestedUser.getEmail());
			}

			if (strippedStartsWithEither(registeredUser.getPhoneIndex(), requestedUser.getPhoneIndex())) {
				matchesOn.put(MatchType.Phone, registeredUser.getPhoneIndex() + ", " + requestedUser.getPhoneIndex());
			}
		}

		return matchesOn;
	}

	private String getPropertyNullSafe(BasicDynaBean row, String property) {
		if (row.get(property) != null) {
			return row.get(property).toString();
		}

		return Strings.EMPTY_STRING;
	}

	private boolean stringsAreSimilar(String first, String second) {
		return Strings.isSimilarTo(first, second, LEVENSHTEIN_DISTANCE_THRESHOLD);
	}

	private boolean strippedStartsWithEither(String first, String second) {
		String firstStripped = Strings.stripNonAlphaNumericCharacters(first);
		String secondStripped = Strings.stripNonAlphaNumericCharacters(second);

		return Strings.bothNonBlanksAndOneBeginsWithTheOther(firstStripped, secondStripped);
	}

	public enum MatchType {
		Name("ContractorAccount.name"), Address("ContractorAccount.address"), TaxID("ContractorAccount.taxId"), Contact(
				"User.name"), Email("User.email"), Phone("User.phone");

		private String key;

		private MatchType(String key) {
			this.key = key;
		}
	}

	public class Match implements Comparable<Match> {
		private ContractorAccount requested;
		private Map<MatchType, String> matches;

		public Match(ContractorAccount requested, Map<MatchType, String> matches) {
			this.requested = requested;
			this.matches = matches;
		}

		public ContractorAccount getRequested() {
			return requested;
		}

		public Map<MatchType, String> getMatches() {
			return matches;
		}

		public String getMatchedOn() {
			String matchedOn = "";
			boolean first = true;

			for (MatchType type : matches.keySet()) {
				matchedOn += String.format("%s%s (%s)", (first ? "" : ",<br />"), getText(type.key), matches.get(type));
				first = false;
			}

			return matchedOn;
		}

		@Override
		public int compareTo(Match o) {
			return requested.compareTo(o.getRequested());
		}
	}
}
