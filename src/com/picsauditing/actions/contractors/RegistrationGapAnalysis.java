package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;
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
	// Up to two character changes in a string are allowed (e.g. "Jo Glyn" and "Joe Glen")
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
			duplicate.setReason("Duplicate/Merged Account");
			duplicate.setStatus(AccountStatus.Deactivated);
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
				Set<MatchType> matchesOn = getMatchTypes(registered, requested);
				if (!matchesOn.isEmpty()) {
					Match match = new Match(registered, requested, matchesOn);

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

	// TODO Rename to compareRegisteredWithRequest
	private Set<MatchType> getMatchTypes(ContractorAccount registered, ContractorAccount requested) {
		Set<MatchType> matchesOn = new TreeSet<MatchType>();

		if (StringUtils.getLevenshteinDistance(registered.getName(), requested.getName()) < LEVENSHTEIN_DISTANCE_THRESHOLD) {
			matchesOn.add(MatchType.Name);
		}

		if (StringUtils.getLevenshteinDistance(registered.getAddress(), requested.getAddress()) < LEVENSHTEIN_DISTANCE_THRESHOLD) {
			if (StringUtils.getLevenshteinDistance(registered.getCity(), requested.getCity()) < LEVENSHTEIN_DISTANCE_THRESHOLD) {
				matchesOn.add(MatchType.Address);
			}

			// TODO utility: StringUtils.bothNonBlanksAndOneBeginsWithTheOther
			if (registered.getZip().startsWith(requested.getZip())
					|| requested.getZip().startsWith(registered.getZip())) {
				matchesOn.add(MatchType.Address);
			}
		}

		// TODO utility: StringUtils.bothNonBlanksAndOneBeginsWithTheOther
		if (!Strings.isEmpty(registered.getTaxId())
				&& !Strings.isEmpty(requested.getTaxId())
				&& (registered.getTaxId().startsWith(requested.getTaxId()) || requested.getTaxId().startsWith(
						registered.getTaxId()))) {
			matchesOn.add(MatchType.TaxID);
		}

		User registeredUser = registered.getPrimaryContact();
		User requestedUser = requested.getPrimaryContact();

		if (registeredUser != null && requestedUser != null) {
			if (StringUtils.getLevenshteinDistance(registeredUser.getName(), requestedUser.getName()) < LEVENSHTEIN_DISTANCE_THRESHOLD) {
				matchesOn.add(MatchType.Contact);
			}

			if (StringUtils.getLevenshteinDistance(registeredUser.getEmail(), requestedUser.getEmail()) < LEVENSHTEIN_DISTANCE_THRESHOLD) {
				matchesOn.add(MatchType.Email);
			}

			// TODO utility: StringUtils.bothNonBlanksAndOneBeginsWithTheOther
			if (registeredUser.getPhoneIndex() != null
					&& requestedUser.getPhoneIndex() != null
					&& (registeredUser.getPhoneIndex().contains(requestedUser.getPhoneIndex()) || requestedUser
							.getPhoneIndex().contains(registeredUser.getPhoneIndex()))) {
				matchesOn.add(MatchType.Phone);
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

	public enum MatchType {
		Name("ContractorAccount.name"), Address("ContractorAccount.address"), TaxID("ContractorAccount.taxId"), Contact(
				"User.name"), Email("User.email"), Phone("User.phone");

		private String key;

		private MatchType(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	public class Match implements Comparable<Match> {
		private ContractorAccount registered;
		private ContractorAccount requested;
		private Set<MatchType> types;

		public Match(ContractorAccount registered, ContractorAccount requested, Set<MatchType> types) {
			this.registered = registered;
			this.requested = requested;
			this.types = types;
		}

		public ContractorAccount getRegistered() {
			return registered;
		}

		public ContractorAccount getRequested() {
			return requested;
		}

		public Set<MatchType> getTypes() {
			return types;
		}

		public String getMatchedOn() {
			boolean first = true;
			String matchedOn = "";

			for (MatchType type : types) {
				matchedOn += (first ? "" : ", ") + getText(type.getKey());

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
