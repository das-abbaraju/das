package com.picsauditing.dao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;

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
		File file = new File("tests/brochure_1946.pdf");
		String mimeType = new MimetypesFileTypeMap().getContentType(file);
		System.out.println(mimeType);
		//emailAttachment.setMimeType(mimeType);
		
		FileInputStream fis = new FileInputStream(file);
		System.out.println("Found file size = "+file.length());
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		while (fis.read(buf) != -1) {
			byteStream.write(buf);
			System.out.println("Stream size = "+byteStream.size());
		}
		
		emailAttachment.setContent(byteStream.toByteArray());
		fis.close();
		emailAttachmentDAO.save(emailAttachment);
	}
	
	@Test
	public void testOpenFile() throws IOException {
		EmailAttachment emailAttachment = emailAttachmentDAO.find(7);
		byte[] fileData = emailAttachment.getContent();
		System.out.println(fileData.length);
	}
}
