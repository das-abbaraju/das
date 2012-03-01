package com.picsauditing.actions.contractors;

import org.apache.commons.codec.binary.Base64;

@SuppressWarnings("serial")
public class ContractorBadge extends ContractorActionSupport {
	@Override
	public String execute() throws Exception {
		if (contractor == null) {
			findContractor();
		}
		
		id = contractor.getId();
		account = contractor;
		
		return SUCCESS;
	}

	public String getHash() {
	    byte[] base64Encoded = Base64.encodeBase64(getContractorIdNameHash().getBytes());
        
        return new String(base64Encoded);
	}

	private String getContractorIdNameHash() {
		return String.format("%d:%s", contractor.getId(), contractor.getName());
	}
}