package com.picsauditing.actions;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.struts2.ServletActionContext;

public abstract class FileUploadActionSupport extends PicsActionSupport 
{
	protected File file = null;
	protected String fileContentType = null;
	protected String fileFileName = null;
	
	
	public String execute() throws Exception
	{
		if( file != null )
		{
			System.out.println("---");
			System.out.println(fileFileName);
			
			System.out.println("---");
		}
		else
		{
			System.out.println("File was not uploaded");
		}
			
		return SUCCESS;
	}


	public File getFile() {
		return file;
	}


	public void setFile(File file) {
		this.file = file;
	}


	public String getFileContentType() {
		return fileContentType;
	}


	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}


	public String getFileFileName() {
		return fileFileName;
	}


	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFtpRoot()
	{
		return ServletActionContext.getServletContext().getInitParameter("FTP_DIR");
	}

	public boolean copyFile( File f , String newPath, boolean deleteFilesThatHaveASimilarNameButDifferentExtension )
	{
		//make sure the folder exists

		File parentFolder = ensurePathExists(newPath); 
		if( parentFolder != null )
		{
			//if the file exists, delete it
			final File theNewFile = new File( newPath );
			
			
			if( !deleteFilesThatHaveASimilarNameButDifferentExtension )
			{
				if( theNewFile.exists() && ! theNewFile.delete())
				{
					System.out.println("unable to delete file");
				}
			}
			else
			{
				File[] deleteList = parentFolder.listFiles( new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						
						return ! (new File( dir, name ).isDirectory()) 
								&& name.substring( 0, name.lastIndexOf(".") ).equals(theNewFile.getName().substring( 0, theNewFile.getName().lastIndexOf(".") ) );
					}
					
				});
				
				for( File toDelete : deleteList )
				{
					toDelete.delete();
				}
			}
			
			//do the copy
			return f.renameTo( theNewFile );			
		}
		return false;	
	}

	
	

	public File ensurePathExists(String newPath) {
		String[] folders = newPath.split("/");
		String path = "";
		File thisDir = null;
		
		for( int i = 0; i < folders.length - 1; i++ )  //length - 1 because the last slot contains the actual filename
		{
			String currentLevel = folders[i];
			
			if( currentLevel.length() > 0)
			{
				path = path + currentLevel;
				
				thisDir = new File( path );
				
				if( ! thisDir.exists() )
				{
					if( !thisDir.mkdir() )
					{
						return null;
					}
				}
				path = path + "/";
			}
		}
		return thisDir;
	}
	

	
	
}
