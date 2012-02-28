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

	public String getScriptlet(int badgeSize) throws Exception {
		String scriptlet = "<script type=\"text/javascript\">\n"
				+ "    var _pbq = _pbq || {};\n"
				+ "\n"
				+ "    _pbq.size = %d;\n"
				+ "    \n"
				+ "    (function () {\n"
				+ "        var pb = document.createElement('script');\n"
				+ "        pb.id = 'pics_badge';\n"
				+ "        pb.type = 'text/javascript';\n"
				+ "        pb.async = true;\n"
				+ "        pb.src = '//www.picsorganizer.com/badge/badge.js#pb-id=%s';\n"
				+ "        \n"
				+ "        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(pb);\n"
				+ "    })();\n" + "</script>";

		return String.format(scriptlet, badgeSize, getEncodedIdAndName());
	}

	private String getEncodedIdAndName() {
		byte[] base64Encoded = Base64.encodeBase64(getContractorIdNameHash().getBytes());
		return new String(base64Encoded);
	}

	private String getContractorIdNameHash() {
		return String.format("%d:%s", contractor.getId(), contractor.getName());
	}
}