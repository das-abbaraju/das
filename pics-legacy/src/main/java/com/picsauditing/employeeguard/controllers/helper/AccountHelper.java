package com.picsauditing.employeeguard.controllers.helper;


import com.picsauditing.employeeguard.models.AccountModel;
import org.apache.commons.collections.MapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AccountHelper {

	public static <V> Map<AccountModel, V> convertMap(final Map<Integer, AccountModel> accountModels,
													  final Map<Integer, V> accountToValueMap) {
		if (MapUtils.isEmpty(accountModels) || MapUtils.isEmpty(accountToValueMap)) {
			return Collections.emptyMap();
		}

		Map<AccountModel, V> convertedMap = new HashMap<>();
		for (Integer id : accountToValueMap.keySet()) {
			convertedMap.put(accountModels.get(id), accountToValueMap.get(id));
		}

		return convertedMap;
	}

}
