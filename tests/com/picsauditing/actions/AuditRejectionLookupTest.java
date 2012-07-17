package com.picsauditing.actions;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.Action;
import com.picsauditing.PICS.DBBean;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.util.Strings;

/*
 * populateJsonArray is tested with execute
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({AuditRejectionLookup.class, DBBean.class, I18nCache.class})
@PowerMockIgnore({"javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*"})
public class AuditRejectionLookupTest {
	private static final String TRANSLATION_KEY_PREFIX = "Insurance.Rejection.Reason.Code.";
	private AuditRejectionLookup auditRejectionLookup;
	
	private Map<String, String> keyValueTestData =
			Collections.unmodifiableMap(new HashMap<String, String>() {
				private static final long serialVersionUID = 1L;
				{
					put("BP.Carson.NA.WOS", "Policy must include; BP, its directors, officers, employees and agents as additional insured and a Waiver of subrogation is required for ALL policies (except workers comp).");
					put("Performance.Pipe.All.NAI", 
							"Additional language must appear as follows \"The certificate holder is included as an additional insured under the referenced General Liability, Umbrella\\/Excess Liability and Automobile Liability policies as required by written contract or agreement. All of the referenced insurance waives subrogation in favor of certificate holder as required by written contract or agreement.\"");
					put("Thomas.Steel.All.ICH.CHI","Certificate holder can be the following below:\nApollo Metals:\n1001 Fourteenth Avenue\nBethlehem, PA\n18018\nOr\nApollo Metals\nc\\/o PICS\nP.O. Box 51387\nIrvine, CA. 92619-1387\nThomas Steel Strip Corp.\nDelaware Avenue, NW\nWarren, OH\n44485\nOr\nThomas Steel Strip Corp.\nc\\/o PICS\nP.O. Box 51387\nIrvine, CA. 92619-1387");
				}
			});
	
	@Mock private ResultSet results; 
	@Mock private Connection connection;
	@Mock private Statement statement;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(I18nCache.class);
		
		auditRejectionLookup = PowerMockito.spy(new AuditRejectionLookup());
		PowerMockito.mockStatic(DBBean.class);

		when(DBBean.getDBConnection()).thenReturn(connection);
		when(connection.createStatement()).thenReturn(statement);
		when(statement.executeQuery(anyString())).thenReturn(results);
	}

	@Test
	public void testExecute_OneResult() throws Exception {
		when(results.next()).thenReturn(Boolean.TRUE).thenReturn(Boolean.FALSE);
		when(results.getString(1)).thenReturn(TRANSLATION_KEY_PREFIX+"BP.Carson.NA.WOS");
		when(results.getString(2)).thenReturn(keyValueTestData.get("BP.Carson.NA.WOS"));
		
		String strutsResponse = auditRejectionLookup.execute();
		String json = auditRejectionLookup.getJsonArray().toString();
		
		assertEquals(Action.SUCCESS, strutsResponse);
		assertEquals("[{\"id\":\"BP.Carson.NA.WOS\",\"value\":\"Policy must include; BP, its directors, officers, employees and agents as additional insured and a Waiver of subrogation is required for ALL policies (except workers comp).\"}]", json);
	}
	
	@Test
	public void testExecute_ManyResults() throws Exception {
		stubResultsToReturnTrueOnNextEqualToTestDataSize();
		stubResultsToReturnAllTestData();
		
		String strutsResponse = auditRejectionLookup.execute();
		
		assertEquals(Action.SUCCESS, strutsResponse);
		assertEquals(jsonArrayFromTestData(), auditRejectionLookup.getJsonArray());
	}

	private void stubResultsToReturnTrueOnNextEqualToTestDataSize() throws SQLException {
		when(results.next()).thenAnswer(new Answer() {
			private int countCalls = 0; 
			public Object answer(InvocationOnMock invocation) {
				if (countCalls++ < keyValueTestData.size()) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			}
		});
	}

	private void stubResultsToReturnAllTestData() throws SQLException {
		when(results.getString(anyInt())).thenAnswer(new Answer() {
			private Iterator<String> keys = keyValueTestData.keySet().iterator();
			private String msgKey;
			public Object answer(InvocationOnMock invocation) {
				Object toReturn = null;
				Object[] args = invocation.getArguments();
				int i = (Integer) args[0];
				if (i == 1) {
					msgKey = keys.next();
					toReturn = TRANSLATION_KEY_PREFIX+msgKey;
				} else if (i == 2) {
					toReturn = keyValueTestData.get(msgKey);
				}
				return toReturn;
			}
		});
	}

	@SuppressWarnings("unchecked")
	private JSONArray jsonArrayFromTestData() {
		JSONArray jsonArray = new JSONArray();
		for (String msgKey: keyValueTestData.keySet()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", msgKey);
			jsonObject.put("value", keyValueTestData.get(msgKey));
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	@Test
	public void testParseCode_keysWithPrefixForAllTestData() throws Exception {
		for (String msgKey: keyValueTestData.keySet()) {
			String testKey = TRANSLATION_KEY_PREFIX + msgKey; 
			String newMsgKey = Whitebox.invokeMethod(auditRejectionLookup, "parseCode", testKey);
			assertEquals(msgKey, newMsgKey);
		}
	}

	@Test
	public void testParseCode_nullKey() throws Exception {
		String newMsgKey = Whitebox.invokeMethod(auditRejectionLookup, "parseCode", (Object[])null);
		assertTrue(Strings.isEmpty(newMsgKey));
	}

	@Test
	public void testParseCode_emptyKey() throws Exception {
		String newMsgKey = Whitebox.invokeMethod(auditRejectionLookup, "parseCode", "");
		assertTrue(Strings.isEmpty(newMsgKey));
	}

}
