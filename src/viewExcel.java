/**
* viewExcel.java
*/

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import jxl.*; 
import jxl.write.*;

public class viewExcel extends HttpServlet {

	/* Dir where the pdf are stored. Path should be a fully specified url */
//	String PDFPath = "http://www.picsauditing.com/pdf/";
//	String PDFExt = ".pdf";

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {

			response.setContentType("vnd.ms-excel");
			OutputStream out = response.getOutputStream();
//			outFDF.Save(out);
		WritableWorkbook workbook = Workbook.createWorkbook(out);
		WritableSheet sheet = workbook.createSheet("PICS data", 0); 
		Label label = new Label(0, 2, "A label record"); 
		sheet.addCell(label); 
		workbook.write(); 
		workbook.close(); 

			out.close();

		} catch(JXLException e) {
			/* We handle an error by emitting an html header */
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("Caught Excel exception");
			out.println(e.toString());
			e.printStackTrace(out);
		}//catch
	}//doPost
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request,response);
	}//doPost
}