package test;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.sql.*;
import org.apache.commons.lang.StringEscapeUtils;

import com.ibm.icu.text.Transliterator;

public class ChineseToEnglishTest {
private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSet = null;
	public static String CHINESE_TO_LATIN = "Han-Latin";
	public static String CHINESE_TO_LATIN_NO_ACCENTS = "Han-Latin; nfd; [:nonspacing mark:] remove; nfc";

  public void myMethod() {
		try {
  // This will load the MySQL driver, each DB has its own driver
      Class.forName("com.mysql.jdbc.Driver");
      // Setup the connection with the DB
      connect = DriverManager
          .getConnection("jdbc:mysql://cobalt.picsauditing.com:3306/pics_alpha1?"
              + "user=pics&password=M0ckingj@y");

      // Statements allow to issue SQL queries to the database
      statement = connect.createStatement();
      // Result set get the result of the SQL query
      resultSet = statement
          .executeQuery("select * from testChineseEnglish");
	  while (resultSet.next()) {
		  String chinese = resultSet.getString("chinese");
		  /*
		  String decomposed = Normalizer.normalize(chinese, Form.NFD);
		  String english = decomposed.replaceAll("[^\\p{ASCII}]","");
		  		  System.out.println("Chinese: "+chinese+" / Decomposed: "+decomposed+" / English: "+english);
		  */
		  Transliterator chineseToLatinTrans = Transliterator.getInstance(CHINESE_TO_LATIN_NO_ACCENTS);
		  String latinized = chineseToLatinTrans.transliterate(chinese);
		  System.out.println("Chinese: "+chinese+" / Latinized: "+latinized);

	  }
      resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

  }

	public static void main (String[] args) {
		ChineseToEnglishTest myObj = new ChineseToEnglishTest();

		myObj.myMethod();

	}
}