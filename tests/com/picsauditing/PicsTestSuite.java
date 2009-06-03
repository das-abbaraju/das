package com.picsauditing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.picsauditing.PICS.AuditBuilderTest;
import com.picsauditing.PICS.AuditCriteriaAnswerBuilderTest;
import com.picsauditing.PICS.AuditCriteriaAnswerTest;
import com.picsauditing.PICS.BillingCalculatorSingleTest;
import com.picsauditing.PICS.DateBeanTest;
import com.picsauditing.PICS.DefaultDatabase;
import com.picsauditing.PICS.FlagCalculatorTest;
import com.picsauditing.dao.AccountDAOTest;
import com.picsauditing.dao.AccountNameDAOTest;
import com.picsauditing.dao.AmBestDAOTest;
import com.picsauditing.dao.AuditCategoryDAOTest;
import com.picsauditing.dao.AuditCategoryDataTestDAO;
import com.picsauditing.dao.AuditDataDAOTest;
import com.picsauditing.dao.AuditOperatorDAOTest;
import com.picsauditing.dao.AuditQuestionDAOTest;
import com.picsauditing.dao.AuditTypeDaoTest;
import com.picsauditing.dao.ContractorAccountDAOTest;
import com.picsauditing.dao.ContractorAuditDAOTest;
import com.picsauditing.dao.ContractorAuditOperatorDAOTest;
import com.picsauditing.dao.ContractorOperatorDAOTest;
import com.picsauditing.dao.EmailAttachmentDAOTest;
import com.picsauditing.dao.EmailQueueDAOTest;
import com.picsauditing.dao.EmailTemplateDAOTest;
import com.picsauditing.dao.FlagQuestionCriteriaDAOTest;
import com.picsauditing.dao.NoteDAOTest;
import com.picsauditing.dao.OperatorAccountDAOTest;
import com.picsauditing.dao.OperatorFormDAOTest;
import com.picsauditing.dao.TokenDAOTest;
import com.picsauditing.dao.UserAccessDAOTest;
import com.picsauditing.dao.UserDaoTest;
import com.picsauditing.dao.UserLoginLogDAOTest;
import com.picsauditing.dao.UserSwitchDAOTest;
import com.picsauditing.dao.WidgetUserDAOTest;
import com.picsauditing.jpa.entities.ContractorAccountTest;
import com.picsauditing.jpa.entities.FlagQuestionCriteriaTest;
import com.picsauditing.mail.EmailSenderTest;
import com.picsauditing.search.SelectFilterTest;
import com.picsauditing.search.SelectSQLTest;
import com.picsauditing.util.AnswerMapTest;
import com.picsauditing.util.BrainTreeTest;
import com.picsauditing.util.FileUtilsTest;
import com.picsauditing.util.ImagesTest;
import com.picsauditing.util.LuhnTest;
import com.picsauditing.util.StringsTest;
import com.picsauditing.util.VelocityAdaptorTest;
import com.picsauditing.util.excel.ExcelSheetTest;
import com.picsauditing.util.log.PicsLoggerTest;

@RunWith(Suite.class)
@SuiteClasses( {
	//dao
	AccountDAOTest.class,
	AccountNameDAOTest.class,
	AmBestDAOTest.class,
	AuditCategoryDAOTest.class,
	AuditCategoryDataTestDAO.class,
	AuditDataDAOTest.class,
	AuditOperatorDAOTest.class,
	AuditQuestionDAOTest.class,
	AuditTypeDaoTest.class,
	ContractorAccountDAOTest.class,
	ContractorAuditDAOTest.class,
	ContractorAuditOperatorDAOTest.class,
	ContractorOperatorDAOTest.class,
	EmailAttachmentDAOTest.class,
	EmailQueueDAOTest.class,
	EmailTemplateDAOTest.class,
	FlagQuestionCriteriaDAOTest.class,
	NoteDAOTest.class,
	OperatorAccountDAOTest.class,
	OperatorFormDAOTest.class,
	TokenDAOTest.class,
	UserAccessDAOTest.class,
	UserDaoTest.class,
	UserLoginLogDAOTest.class,
	UserSwitchDAOTest.class,
	WidgetUserDAOTest.class,
	//entities
	ContractorAccountTest.class,
	FlagQuestionCriteriaTest.class,
	//mail
	EmailSenderTest.class,
	//PICS
	AuditBuilderTest.class,
	AuditCriteriaAnswerBuilderTest.class,
	AuditCriteriaAnswerTest.class,
	BillingCalculatorSingleTest.class,
	DateBeanTest.class,
	DefaultDatabase.class,
	FlagCalculatorTest.class,
	//search
	SelectFilterTest.class,
	SelectSQLTest.class,
	//util
	AnswerMapTest.class,
	BrainTreeTest.class,
	FileUtilsTest.class,
	ImagesTest.class,
	LuhnTest.class,
	StringsTest.class,
	VelocityAdaptorTest.class,
	
	ExcelSheetTest.class,
	
	PicsLoggerTest.class
})
public class PicsTestSuite {

}
