package com.picsauditing.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import junit.framework.TestCase;

import org.junit.Test;

import com.picsauditing.jpa.entities.Note;
import com.picsauditing.search.SelectUserUnion;

public class StringsTest extends TestCase {

	public StringsTest(String name) {
		super(name);
	}
	
	public void testEmail() {
		try {
			Address[] addresses = InternetAddress.parse("tallred@picsauditing.com");
			System.out.println(addresses.toString());
			addresses = InternetAddress.parse("tallred@picsauditing.com, kn@pics.com");
			System.out.println(addresses.toString());
			addresses = InternetAddress.parse("Trevor <tallred@picsauditing.com>");
			System.out.println(addresses.toString());
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			fail();
		}
	}

	public void testInsertSpaceNull() {
		assertEquals(null, Strings.insertSpaces(null));
	}

	public void testInsertSpace1() {
		assertEquals("a", Strings.insertSpaces("a"));
	}

	public void testInsertSpace3() {
		assertEquals("a b c", Strings.insertSpaces("abc"));
	}

	public void testArray() {
		List<String> list = new ArrayList<String>();
		list.add("Hello");
		addString(list);
		addString(list);
		assertEquals(3, list.size());
	}

	private void addString(List<String> list) {
		list.add("World" + list.size());
	}

	public void testString() {
		String color = "Green";
		color = changeColor(color);
		assertEquals("Red", color);
	}

	private String changeColor(String color) {
		color = "Red";
		return color;
	}

	@Test
	public void testHash() {
		System.out.println(Strings.hashUrlSafe("testinsfgsf"));
	}

	@Test
	public void testSQL() {
		SelectUserUnion sql = new SelectUserUnion();
		// System.out.println(sql.toString());
		sql.addField("u.id");
		sql.addField("u.username");
		sql.addField("u.name");
		sql.addField("u.accountID");
		sql.addField("a.name");
		sql.addJoin("JOIN accounts a ON a.id = u.accountID");

		sql.addWhere("u.isActive = 'Y'");
		sql.addOrderBy("u.name");
		sql.setLimit(100);
		// System.out.println(sql.toString());
		sql.toString();
	}

	@Test
	public void testNotes() throws Exception {
		String noteText = FileUtils.readFile("tests/test_notes.txt");
		
		BufferedReader reader = new BufferedReader( new StringReader(noteText) );
		
		String line;
		List<Note> notes = new Vector<Note>();
		List<Note> thisSet = new Vector<Note>();
		List<List<Note>> badNotes = new Vector<List<Note>>();
		
		while( ( line = reader.readLine() ) != null ) {
			Note note = new Note();
			note.setOriginalText(line);
			notes.add(note);
		}
		
		for( Note note : notes ) {
			try {
				
				note.convertNote();
				
				if( thisSet.size() > 1 ) {
					badNotes.add(new Vector<Note>(thisSet));
				}
				thisSet.clear();
				thisSet.add(note);
			}
			catch( Exception e ) {
				thisSet.add(note);
			}
		}
		
		for( List<Note> badSet : badNotes ) {
			System.out.println("================================");
			for( Note note : badSet ) {
				System.out.println(note.getOriginalText());
			}
			System.out.println("================================");
		}
		
	}
	
	@Test
	public void testParseFloat() {
		NumberFormat format = new DecimalFormat("#,##0"); 
		
		String answer = "1234567890";
		
		BigDecimal value = new BigDecimal(answer);
		String valueString = format.format(value);
		
		assertEquals("1,234,567,890", valueString);
	}
	
	@Test
	public void testPhoneStripper() {
		assertEquals("9112223333", Strings.stripPhoneNumber("(911)222-3333"));
		assertEquals("9112223333", Strings.stripPhoneNumber("911-222-3333"));
		assertEquals("8002223333", Strings.stripPhoneNumber("1(800) 222-3333"));
		assertEquals("9112223333", Strings.stripPhoneNumber("911.222.3333 x4"));
	}
	
	@Test
	public void testExtractAccountID() {
		assertEquals(123456, Strings.extractAccountID("123456"));
		assertEquals(123456, Strings.extractAccountID("123456.7"));
		assertEquals(0, Strings.extractAccountID("Bob's Cranes"));
		assertEquals(0, Strings.extractAccountID("1 Micro"));
	}

	public void testMapParams() {
		String url = "response=3&responsetext=Invalid Customer Vault Id REFID:101460773&authcode=&transactionid=0&avsresponse=&cvvresponse=&orderid=123&type=sale&response_code=300&customer_vault_id=0";
		Map<String, String> map = Strings.mapParams(url);
		assertEquals("3", map.get("response"));
		assertEquals("Invalid Customer Vault Id REFID:101460773", map.get("responsetext"));
		assertEquals("", map.get("authcode"));
		assertEquals("0", map.get("customer_vault_id"));
	}

	public void testIndexName() {
		assertEquals(null, Strings.indexName(null));
		assertEquals("HBOBQJOHNS5STARCRANEINCBJCRANE", 
				Strings.indexName(" H. Bob & Q. John's 5 Star Crane Inc./BJ Crane"));
		assertEquals("QWERTYASDFZXCV", Strings.indexName("QWERTYASDFZXCV"));
	}
	
	public void testMd5() {
		assertEquals("593b069af7c100f8ee335184c763fad1", Strings.md5("e4d909c290d0fb1ca068ffaddf22cbd0|20080516190549"));
	}
}
