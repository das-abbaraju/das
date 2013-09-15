/*
 * $Id: CheckboxInterceptor.java 768855 2009-04-27 02:09:35Z wesw $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.picsauditing.strutsutil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.picsauditing.util.ReflectUtil;

/**
 * <!-- START SNIPPET: description --> Looks for a hidden identification field that specifies the original value of the
 * checkbox. If the checkbox isn't submitted, insert it into the parameters as if it was with the value of 'false'. <!--
 * END SNIPPET: description -->
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * <li>setUncheckedValue - The default value of an unchecked box can be overridden by setting the 'uncheckedValue'
 * property.</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <!-- START SNIPPET: extending -->
 * <p/>
 * <!-- END SNIPPET: extending -->
 */
public class CheckboxInterceptor implements Interceptor {

	/** Auto-generated serialization id */
	private static final long serialVersionUID = -586878104807229585L;

	private String uncheckedValue = Boolean.FALSE.toString();

	private static final Logger LOG = LoggerFactory.getLogger(CheckboxInterceptor.class);

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation ai) throws Exception {
		Map parameters = ai.getInvocationContext().getParameters();
		Map<String, String[]> newParams = new HashMap<String, String[]>();
		Set<Map.Entry> entries = parameters.entrySet();
		for (Iterator<Map.Entry> iterator = entries.iterator(); iterator.hasNext();) {
			Map.Entry entry = iterator.next();
			String key = (String) entry.getKey();

			if (key.startsWith("__checkbox_")) {

				String name = key.substring("__checkbox_".length());

				Object values = entry.getValue();
				iterator.remove();
				if (values != null && values instanceof String[] && ((String[]) values).length > 1) {
					LOG.debug("Bypassing automatic checkbox detection due to multiple checkboxes of the same name: #1",
							name);
					continue;
				}

				// is this checkbox checked/submitted?
				if (!parameters.containsKey(name)) {
					// if not, let's be sure to default the value to false

					boolean isList = false;
					try {
						isList = ai.getAction().getClass().getDeclaredField(name).getType().isAssignableFrom(List.class);
					} catch (NoSuchFieldException ignoreMePlz) {
					}
					if (!isList) {
						newParams.put(name, new String[] { uncheckedValue });
					}
				}
			}
		}

		parameters.putAll(newParams);

		return ai.invoke();
	}

	/**
	 * Overrides the default value for an unchecked checkbox
	 *
	 * @param uncheckedValue
	 *            The uncheckedValue to set
	 */
	public void setUncheckedValue(String uncheckedValue) {
		this.uncheckedValue = uncheckedValue;
	}
}