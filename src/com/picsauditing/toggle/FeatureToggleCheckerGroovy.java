package com.picsauditing.toggle;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.util.Strings;

public class FeatureToggleCheckerGroovy implements FeatureToggle {
	private final Logger logger = LoggerFactory.getLogger(FeatureToggleCheckerGroovy.class);

	private String cacheName = "feature_toggle";
	private String scriptBaseClass = "com.picsauditing.toggle.FeatureToggleExpressions";
	private CacheManager cacheManager;
	private Permissions permissions;
	private Map<String, Object> dynamicToggleVariables = new HashMap<String, Object>();

	@Autowired
	private FeatureToggleProvider featureToggleProvider;
	@Autowired
	private AppPropertyDAO appPropertyDAO;

	public FeatureToggleCheckerGroovy() {
	}

	public FeatureToggleCheckerGroovy(Permissions permissions) {
		this.permissions = permissions;
	}

	public boolean isFeatureEnabled(String toggleName) {
		Script script = script(toggleName);
		if (script == null) {
			return false;
		}
		return runScript(toggleName, script);
	}

	public void addToggleVariable(String name, Object value) {
		if (Strings.isEmpty(name)) {
			logger.error("A dynamic toggle variable must have a name");
			return;
		}
		if (value == null) {
			dynamicToggleVariables.remove(name);
		} else {
			dynamicToggleVariables.put(name, value);
		}
	}

	private Script script(String toggleName) {
		Script script = scriptFromCache(toggleName);
		if (script == null) {
			try {
				script = createScript(scriptBodyFromProperty(toggleName));
				cacheScript(toggleName, script);
			} catch (FeatureToggleException e) {
				logger.error("Error executing toggle {}: {}", toggleName, e.getMessage());
				return null;
			}
		}
		return script;
	}

	private boolean runScript(String toggleName, Script script) {
		try {
			Object scriptResult = script.run();
			if (scriptResult instanceof Boolean) {
				logger.debug("FeatureToggle \"{}\" is {}",toggleName,scriptResult.toString());
				return (Boolean) scriptResult;
			} else {
				logger.debug("FeatureToggle \"{}\" script returned a non-boolean result; result will be false",
						toggleName);
			}
		} catch (Exception e) {
			// any exception should result in false script
			logger.error("FeatureToggle \"{}\"  script threw an exception; result will be false: {}",toggleName, e.getMessage());
			if (permissions == null) {
				unCacheScript(toggleName);
			}
		}
		return false;
	}

	private String scriptBodyFromProperty(String toggleName) {
		String featureToggleScript = featureToggleProvider.findFeatureToggle(toggleName);
		if (featureToggleScript == null) {
			logger.error("Feature Toggle {} not found", toggleName);
		}
		return featureToggleScript;
	}

	private Script createScript(String scriptBody) throws FeatureToggleException {
		CompilerConfiguration conf = new CompilerConfiguration();
		conf.setScriptBaseClass("com.picsauditing.toggle.FeatureToggleExpressions");
		Binding binding = binding();
		ClassLoader classLoader = this.getClass().getClassLoader();
		GroovyShell groovy = new GroovyShell(classLoader, binding, conf);
		try {
			return groovy.parse(scriptBody);
		} catch (Exception e) {
			throw new FeatureToggleException(e.getMessage());
		}
	}

	private Binding binding() {
		Binding binding = new Binding();
		binding.setVariable("appPropertyDAO", appPropertyDAO);
		binding.setVariable("permissions", getPermissions());
		for (String key : dynamicToggleVariables.keySet()) {
			binding.setVariable(key, dynamicToggleVariables.get(key));
		}
		return binding;
	}

	private Script scriptFromCache(String toggleName) {
		CacheManager cacheManager = cacheManager();
		Cache cache = cacheManager.getCache("feature_toggle");
		if (cache != null) {
			Element element = cache.get(toggleName);
			if (element != null) {
				Object object = element.getObjectValue();
				if (object instanceof Script) {
					((Script) object).setBinding(binding());
					return (Script) object;
				}
			}
		}
		return null;
	}

	private void unCacheScript(String toggleName) {
		Cache cache = cache();
		if (cache != null) {
			cache.remove(toggleName);
		}
	}

	private Cache cache() {
		CacheManager cacheManager = cacheManager();
		Cache cache = cacheManager.getCache(cacheName);
		return cache;
	}

	private void cacheScript(String toggleName, Script script) throws FeatureToggleException {
		Cache cache = cache();
		if (cache != null) {
			cache.put(new Element(toggleName, script));
		} else {
			throw new FeatureToggleException("cache configuration issue - no cache named " + cacheName);
		}
	}

	// this is for injecting a cachemanager during testing
	private CacheManager cacheManager() {
		if (cacheManager == null) {
			return CacheManager.getInstance();
		} else {
			return cacheManager;
		}
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public String getScriptBaseClass() {
		return scriptBaseClass;
	}

	public void setScriptBaseClass(String scriptBaseClass) {
		this.scriptBaseClass = scriptBaseClass;
	}

	public Permissions getPermissions() {
		if (permissions == null) {
			try {
				permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
			} catch (Exception e) {
				logger.warn("permissions cannot be loaded - if the script depends on it, it'll throw an NPE and the feature toggle will be false");
			}
		}
		return permissions;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

}
