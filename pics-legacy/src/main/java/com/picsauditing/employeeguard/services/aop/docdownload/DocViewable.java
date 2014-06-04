/**
 *
 *
 * User: aphatarphekar
 * Date: 6/2/2014
 * Time: 4:54 PM
 *
 */
package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.employeeguard.exceptions.DocumentViewAccessDeniedException;

public interface DocViewable {

	public DocViewableStatus chkPermissions(int documentId, int skillId) throws DocumentViewAccessDeniedException;
	public DocViewable attach(DocViewable docViewable);

}
