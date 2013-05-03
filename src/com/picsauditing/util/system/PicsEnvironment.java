package com.picsauditing.util.system;

import com.picsauditing.util.AppVersion;
import com.picsauditing.util.Strings;
import org.apache.struts2.ServletActionContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PicsEnvironment {
	private static final String LOCALHOST = "localhost";
	private static final String STABLE = "stable";
	private static final String BETA = "beta";
	private static final String ALPHA = "alpha";
	private static final String CONFIG = "config";
	private static final String QA = "qa-";

	private String environment;
	private String environmentMajorVersion;
	private String environmentMinorVersion;

	public PicsEnvironment(String environmentMajorVersion, String environmentMinorVersion) {
		determineEnvironment(environmentMajorVersion, environmentMinorVersion);
	}

	private void determineEnvironment(String environmentMajorVersion, String environmentMinorVersion) {
		this.environmentMajorVersion = environmentMajorVersion;
		this.environmentMinorVersion = environmentMinorVersion;

		setEnvironmentIfEmptyFromSystemVariable();
		setEnvironmentIfEmptyFromSubdomain();
		setEnvironmentIfEmptyToBetaIfHigherThanAppVersion();
		setEnvironmentIfEmptyToStable();

		environment = environment.toLowerCase();
	}

	public String getEnvironment() {
		return environment;
	}

	public boolean isAlpha() {
		return ALPHA.equals(environment);
	}

	public boolean isBeta() {
		return BETA.equals(environment);
	}

	public boolean isQa() {
		return environment.startsWith(QA);
	}

	public boolean isConfiguration() {
		return CONFIG.equals(environment);
	}

	public boolean isStable() {
		return STABLE.equals(environment);
	}

	public boolean isLocalhost() {
		return LOCALHOST.equals(environment);
	}

	public boolean isShowAlphaLanguages() {
		return isConfiguration() || isAlpha() || isLocalhost();
	}

	private void setEnvironmentIfEmptyFromSystemVariable() {
		if (Strings.isEmpty(environment)) {
			// The (new) official way to determine the enviroment is using -Dpics.env=something
			String env = System.getProperty("pics.env");
			if (Strings.isNotEmpty(env)) {
				environment = env.trim().toLowerCase();
			}
		}
	}

	private void setEnvironmentIfEmptyFromSubdomain() {
		if (Strings.isEmpty(environment)) {
			// In the absense of -Dpics.env, see if there is an explicit subdomain mentioned in the URL that can tell us
			Pattern p = Pattern.compile("(demo[0-9]+|alpha|config|beta|stable|old|qa-beta|qa-stable)\\..*");
			Matcher m;
			m = p.matcher(getServerName());
			if (m.matches()) {
				environment = m.group(1);
			}

			// "localhost" can be "localhost", "localhost:123456", "foo.bar.baz.local", or "foo.bar.baz.local:123456"
			p = Pattern.compile("(localhost|.*\\.local)(:[0-9]+)?");
			m = p.matcher(getServerName());
			if (m.matches()) {
				environment = LOCALHOST;
			}
		}
	}

	private void setEnvironmentIfEmptyToBetaIfHigherThanAppVersion() {
		if (Strings.isEmpty(environment) && providedVersionDetailsHigherThanCurrentAppVersion()) {
			environment = BETA;
		}
	}

	private void setEnvironmentIfEmptyToStable() {
		if (Strings.isEmpty(environment)) {
			environment = STABLE;
		}
	}

	private String getServerName() {
		return ServletActionContext.getRequest().getServerName();
	}

	/**
	 * Compares the hard-coded version number in the PicsOrganizerVersion class
	 * with app_properties in the database. If the Java code is a higher number,
	 * then it's more advanced, i.e. a Beta version.
	 */
	public boolean providedVersionDetailsHigherThanCurrentAppVersion() {
		if (Strings.isNotEmpty(environmentMajorVersion) && Strings.isNotEmpty(environmentMinorVersion)) {
			AppVersion dbVersion = new AppVersion(environmentMajorVersion, environmentMinorVersion);
			return AppVersion.current.greaterThan(dbVersion);
		}

		return false;
	}
}
