package com.picsauditing.access;

import com.picsauditing.jpa.entities.User;

public enum BetaPool {
	None, Developer(User.GROUP_DEVELOPER), Stakeholder(User.GROUP_STAKEHOLDER), BetaTester(User.GROUP_BETATESTER), Global;

	private int groupID;

	private BetaPool() {
	}

	private BetaPool(int groupID) {		
		this.groupID = groupID;
	}
	
	public static BetaPool getBetaPoolByBetaLevel(int betaLevel){
		if(betaLevel < 0 || betaLevel > BetaPool.values().length - 1)
			betaLevel = 0;

		return BetaPool.values()[betaLevel];  
	}
	
	public static boolean isUserBetaTester(Permissions permissions, BetaPool incomingGroupPool) {
		for (BetaPool pool : BetaPool.values()) {
			if (isMemberOfPool(permissions, pool))
				return true;
			if (pool.ordinal() >= incomingGroupPool.ordinal())
				return false;
		}

		return false;
	}

	private static boolean isMemberOfPool(Permissions permissions, BetaPool pool) {
		if (pool.equals(Global))
			return true;
		
		if (pool != None) {
			return permissions.hasGroup(pool.groupID);
		}
		
		return false;
	}
}
