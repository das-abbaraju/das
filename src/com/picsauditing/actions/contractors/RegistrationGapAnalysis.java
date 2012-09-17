package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
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
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectAccount.Type;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RegistrationGapAnalysis extends PicsActionSupport {
	private Database database = new Database();

	private int daysAgo = 30;
	private Map<ContractorAccount, List<ContractorAccount>> possibleMatches = new TreeMap<ContractorAccount, List<ContractorAccount>>();
	private Map<ContractorAccount, List<Match>> matches = new TreeMap<ContractorAccount, List<Match>>();

	@Override
	public String execute() throws Exception {
		if (!permissions.isPicsEmployee()) {
			throw new NoRightsException("PICS Employee");
		}

		buildPossibleMatches();

		return SUCCESS;
	}

	public int getDaysAgo() {
		return daysAgo;
	}

	public void setDaysAgo(int daysAgo) {
		this.daysAgo = daysAgo;
	}

	public Map<ContractorAccount, List<ContractorAccount>> getPossibleMatches() {
		return possibleMatches;
	}

	public Map<ContractorAccount, List<Match>> getMatches() {
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
				if (isPartialMatch(registered, requested)) {
					if (possibleMatches.get(registered) == null) {
						possibleMatches.put(registered, new ArrayList<ContractorAccount>());
					}

					possibleMatches.get(registered).add(requested);
				}

				if (possibleMatches.get(registered) != null) {
					Collections.sort(possibleMatches.get(registered));
				}

				Set<MatchType> matchesOn = getMatchTypes(registered, requested);
				if (!matchesOn.isEmpty()) {
					Match match = new Match(registered, requested, matchesOn);

					if (matches.get(registered) == null) {
						matches.put(registered, new ArrayList<Match>());
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

	private boolean isPartialMatch(ContractorAccount registered, ContractorAccount requested) {
		if (StringUtils.getLevenshteinDistance(registered.getName(), requested.getName()) < 2) {
			return true;
		}

		if (StringUtils.getLevenshteinDistance(registered.getAddress(), requested.getAddress()) < 2) {
			if (StringUtils.getLevenshteinDistance(registered.getCity(), requested.getCity()) < 2) {
				return true;
			}

			if (registered.getZip().startsWith(requested.getZip())
					|| requested.getZip().startsWith(registered.getZip())) {
				return true;
			}
		}

		if (registered.getTaxId().startsWith(requested.getTaxId())
				|| requested.getTaxId().startsWith(registered.getTaxId())) {
			return true;
		}

		if (registered.getPrimaryContact() != null) {
			if (StringUtils.getLevenshteinDistance(registered.getPrimaryContact().getName(), requested
					.getPrimaryContact().getName()) < 2) {
				return true;
			}

			if (StringUtils.getLevenshteinDistance(registered.getPrimaryContact().getEmail(), requested
					.getPrimaryContact().getEmail()) < 2) {
				return true;
			}

			if (StringUtils.getLevenshteinDistance(registered.getPrimaryContact().getPhone(), requested
					.getPrimaryContact().getPhone()) < 2) {
				return true;
			}
		}

		return false;
	}

	private Set<MatchType> getMatchTypes(ContractorAccount registered, ContractorAccount requested) {
		Set<MatchType> matchesOn = new TreeSet<MatchType>();

		if (StringUtils.getLevenshteinDistance(registered.getName(), requested.getName()) < 2) {
			matchesOn.add(MatchType.Name);
		}

		if (StringUtils.getLevenshteinDistance(registered.getAddress(), requested.getAddress()) < 2) {
			if (StringUtils.getLevenshteinDistance(registered.getCity(), requested.getCity()) < 2) {
				matchesOn.add(MatchType.Address);
			}

			if (registered.getZip().startsWith(requested.getZip())
					|| requested.getZip().startsWith(registered.getZip())) {
				matchesOn.add(MatchType.Address);
			}
		}

		if (registered.getTaxId().startsWith(requested.getTaxId())
				|| requested.getTaxId().startsWith(registered.getTaxId())) {
			matchesOn.add(MatchType.TaxID);
		}

		if (registered.getPrimaryContact() != null) {
			if (StringUtils.getLevenshteinDistance(registered.getPrimaryContact().getName(), requested
					.getPrimaryContact().getName()) < 2) {
				matchesOn.add(MatchType.Contact);
			}

			if (StringUtils.getLevenshteinDistance(registered.getPrimaryContact().getEmail(), requested
					.getPrimaryContact().getEmail()) < 2) {
				matchesOn.add(MatchType.Email);
			}

			if (StringUtils.getLevenshteinDistance(registered.getPrimaryContact().getPhone(), requested
					.getPrimaryContact().getPhone()) < 2) {
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

	public class Match {
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

		public String getMatchingValues(ContractorAccount contractor) {
			String matchingValues = "";

			for (MatchType type : types) {
				String data = getDataBy(contractor, type);

				if (data != null) {
					matchingValues += data + "\n\n";
				}
			}

			return matchingValues.trim();
		}

		private String getDataBy(ContractorAccount contractor, MatchType type) {
			if (type == MatchType.Name) {
				return contractor.getName();
			}

			if (type == MatchType.Address) {
				return String.format("%s, %s %s", contractor.getAddress(), contractor.getCity(), contractor.getZip());
			}

			if (type == MatchType.TaxID) {
				return contractor.getTaxId();
			}

			if (contractor.getPrimaryContact() != null) {
				if (type == MatchType.Contact) {
					return contractor.getPrimaryContact().getName();
				}

				if (type == MatchType.Email) {
					return contractor.getPrimaryContact().getEmail();
				}

				if (type == MatchType.Phone) {
					return contractor.getPhone();
				}
			}

			return null;
		}
	}
}
