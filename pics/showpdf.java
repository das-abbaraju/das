import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.picsauditing.access.PermissionsBean;

public class showpdf extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	enum eExt {pdf, doc, txt, xls, jpg} 
		
	
	private ServletConfig config;
	/**
	* Init the servlet
	*/
	final public void init(ServletConfig config) throws ServletException {
		this.config = config;
	}//init
	
	final public ServletConfig getServletConfig() {
		return config;
	}//ServletConfig
	/**
	* Handles GET requests
	*/
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}//doGet
	
	/**
	* Handles POST requests
	*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		com.picsauditing.access.PermissionsBean pBean = (com.picsauditing.access.PermissionsBean)session.getAttribute("pBean");
		if (pBean == null) {
		  pBean = new com.picsauditing.access.PermissionsBean();
		  session.setAttribute("pBean", pBean);
		}//if
		try{
			pBean.thisPageID = request.getParameter("id"); 
			if (!pBean.checkAccess(PermissionsBean.OP_VIEW,response))
				return;
		}catch(Exception ex){
			response.sendRedirect("/login.jsp");
			return;			
		}//catch
		String req_uid = request.getParameter("id"); 
		String OID= request.getParameter("OID"); 
		String file_type = request.getParameter("file");

		HashSet canSeeSet = null;
		if (pBean.isAuditor())
			canSeeSet = pBean.auditorCanSeeSet;
		else
			canSeeSet = pBean.canSeeSet;

		ServletOutputStream o = response.getOutputStream(); 
		
		ServletContext context = getServletContext(); 
		String sFileName = "";
		String ext = "";
		if ("osha".equals(file_type.substring(0,4))){
			sFileName = context.getInitParameter("FTP_DIR") + "/files/oshas/"+file_type+"_"+OID+".";
		    ext = findFileExt(sFileName);		    
		}else if ("pqf".equals(file_type.substring(0,3))) {
			String qID = file_type.substring(6);
			ext = file_type.substring(3,6);
			sFileName = context.getInitParameter("FTP_DIR") + "/files/pqf/qID_"+qID+"/"+qID+"_"+req_uid+".";
		}//else if
		else {
			sFileName = context.getInitParameter("FTP_DIR") + "/files/"+file_type+"s/"+file_type+"_"+req_uid+".";
			ext = findFileExt(sFileName);
		}
		
		if(ext == ""){
			o.print("The file you requested does not exist. Please contact PICS");
		    o.flush();
		    return;
		}
				
		String ct = setContentType(ext);
		response.setContentType(ct);
		
		//Check for both upper and lower case extension
		String filePath = sFileName + ext;
		File file = new File(filePath);
		if(!file.isFile()){
			filePath = sFileName+ext.toLowerCase();
			if(!file.isFile()){
				o.print("The file you requested does not exist. Please contact PICS");
			    o.flush();
			    return;
			}
		}
		//end check
		
		byte[] byteContents = new byte[(int)file.length()]; 		
		response.setContentLength((int)file.length()); 
		FileInputStream in = new FileInputStream(filePath); 
		in.read(byteContents); 		
		in.close();
		
		o.write(byteContents); 
		o.flush(); 
		return;				
    }//doPost
	/**
	* Destroy the servlet
	*/
	public void destroy () {
	}//destroy
	
	public String setContentType(String ext){
		String ct = "application/pdf";
		if("doc".equalsIgnoreCase(ext))
			ct = getContentType(eExt.doc);
		else if("pdf".equalsIgnoreCase(ext))
			ct = getContentType(eExt.pdf);
		else if("txt".equalsIgnoreCase(ext))
			ct = getContentType(eExt.txt);
		else if("xls".equalsIgnoreCase(ext))
				ct = getContentType(eExt.xls);
		else if("jpg".equalsIgnoreCase(ext))
			ct = getContentType(eExt.jpg);
		
		return ct;
		
	}
	private String getContentType(eExt ext){
		String ret = "";
		
		switch(ext){
		case pdf:
			ret ="application/pdf";
			break;
		case doc:
			ret = "application/msword";
			break;
		case txt:
			ret = "text/plain";
			break;
		case xls:
			ret = "application/vnd.ms-excel";
			//ret = "application/xls";
			break;
		case jpg:
			ret = "image/jpeg";		
								
		}	
		
		return ret;
	}
	
	private String findFileExt(String fn){
		String[] exts = new String[] {"pdf", "doc", "txt", "jpg", "xls"};
		for(int i = 0; i < exts.length; i++){
			File f = new File(fn + exts[i]);
			if(f.exists())
				return exts[i];
		}
		
		return "";
	}
	

}//showpdf
