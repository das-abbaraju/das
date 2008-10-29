package com.picsauditing.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class EmailAttachmentDAOTest extends TestCase {

	@Autowired
	private EmailAttachmentDAO emailAttachmentDAO;

	@Test
	public void testSaveAndRemove() throws IOException {
		EmailAttachment emailAttachment = new EmailAttachment();
		emailAttachment.setEmailQueue(new EmailQueue());
		emailAttachment.getEmailQueue().setId(2787);
		emailAttachment.setFileName("brochure_1946.pdf");
		emailAttachment.setFileSize(10);
		File file = new File("M:/Development/ftp_dir/files/brochures/brochure_1946.pdf"); 
		FileInputStream fis = new FileInputStream(file);
		byte[] buf = new byte[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			emailAttachment.setContent(buf);
		}
		fis.close();
		emailAttachmentDAO.save(emailAttachment);
	}
	
	@Test
	public void testOpenFile() throws IOException {
		EmailAttachment emailAttachment = emailAttachmentDAO.find(3);
		String filePath = "C:/Users/keerthi/Desktop/Old_Files/brochure_1946.pdf";
		FileOutputStream fos = new FileOutputStream(filePath);
		fos.write(emailAttachment.getContent());
		fos.close();
	}
}
