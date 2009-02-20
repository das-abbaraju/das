package com.picsauditing.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.dao.ContractorNoteDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorNote;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.log.PicsLogger;



@SuppressWarnings( "serial" )
public class MigrateNotes extends PicsActionSupport {

	protected int remainder = 0;
	protected int modBy = 1;
	
	protected ContractorNoteDAO contractorNoteDao = null;
	protected NoteDAO noteDao = null;

	public MigrateNotes( ContractorNoteDAO conDao, NoteDAO noteDao ) {
		this.contractorNoteDao = conDao;
		this.noteDao = noteDao;
	}
	
	@Override
	public String execute() throws Exception {

		PicsLogger.start("MigrateNotes");
		
		List<ContractorNote> notes = contractorNoteDao.findWhere("a.badNotes != -1 and a.badAdminNotes != -1");
		
		int count = 0;
		int total = 0;
		
		for( ContractorNote note : notes ) {
			if( note.getId() % modBy == remainder ) {
				total++;
			}
		}
		
		
		
		for( ContractorNote note : notes ) {
			count++;
			if( note.getId() % modBy == remainder ) {
				PicsLogger.log("" + Thread.currentThread().getId() + " migrating " + count + " of " + total + ". Contractor id: " + note.getId() );
				migrateContractor(note);
			}
		}

		PicsLogger.stop();
		return SUCCESS;
	}
	
	private void migrateContractor( ContractorNote contractor ) {

		
		int badCount = 0;
		int badAdminCount = 0;
		try {
			String noteStrings = contractor.getNote();
			List<Note> notes = splitNotes( noteStrings );
			List<List<Note>> badNotes = new Vector<List<Note>>();
			List<Note> thisSet = new Vector<Note>();
			
			
			for( Note note : notes ) {

				try {
					if( note.convertNote() ) {
						note.setCreatedBy(new User());
						note.getCreatedBy().setId(1798);
	
						note.setAccount(new Account());
						note.getAccount().setId(contractor.getId());
						note.setViewableById(Account.EVERYONE);
						
						noteDao.save(note);

						if( thisSet.size() > 1 ) {  //should only happen if there has been an exception
							badNotes.add(new Vector<Note>(thisSet));
						}
						
						thisSet.clear();
						thisSet.add(note);

					}
					else {

						if( thisSet.size() > 1 ) {  //should only happen if there has been an exception
							badNotes.add(new Vector<Note>(thisSet));
						}

						List<Note> objs = new Vector<Note>();
						objs.add(note);
						badNotes.add(objs);
					}
					
					
				}
				catch( Exception e ) {
					if( thisSet.size() > 0 ) {
						Note multiLineNote = thisSet.get(thisSet.size()-1); 
						
						if( multiLineNote.getBody() != null )
							multiLineNote.setBody(multiLineNote.getBody() + "\n" + note.getOriginalText() );
						else
							multiLineNote.setBody(note.getOriginalText() );
						
					}
				}
			}
			
			StringBuilder newNotes = new StringBuilder();
			for( List<Note> badNoteSet : badNotes) {
				for( Note badNote : badNoteSet ) {
					noteDao.remove(badNote.getId());
					newNotes.append(badNote.getOriginalText());
					newNotes.append("\n");
					badCount++;
				}
			}

			contractor.setNote(newNotes.toString());
			contractor.setBadNotes(badCount);
			contractorNoteDao.save(contractor);
		}
		catch( Exception e ) {
			e.printStackTrace();
		}


		
		try {
			String noteStrings = contractor.getAdminNote();
			List<Note> notes = splitNotes( noteStrings );
			List<List<Note>> badNotes = new Vector<List<Note>>();
			List<Note> thisSet = new Vector<Note>();
			
			for( Note note : notes ) {

				try {
					if( note.convertNote() ) {
						note.setCreatedBy(new User());
						note.getCreatedBy().setId(1798);
	
						note.setAccount(new Account());
						note.getAccount().setId(contractor.getId());
						note.setViewableById(Account.PicsID);
						noteDao.save(note);
						
						if( thisSet.size() > 1 ) {  //should only happen if there has been an exception
							badNotes.add(new Vector<Note>(thisSet));
						}

						thisSet.clear();
						thisSet.add(note);

					}
					else {
						if( thisSet.size() > 1 ) {  //should only happen if there has been an exception
							badNotes.add(new Vector<Note>(thisSet));
						}
						
						
						List<Note> objs = new Vector<Note>();
						objs.add(note);
						badNotes.add(objs);
					}

					
				}
				catch( Exception e ) {
					if( thisSet.size() > 0 ) {
						Note multiLineNote = thisSet.get(thisSet.size()-1); 
						if( multiLineNote.getBody() != null )
							multiLineNote.setBody(multiLineNote.getBody() + "\n" + note.getOriginalText() );
						else
							multiLineNote.setBody(note.getOriginalText() );
					}
				}
			}
			
			StringBuilder newNotes = new StringBuilder();
			for( List<Note> badNoteSet : badNotes) {
				for( Note badNote : badNoteSet ) {
					noteDao.remove(badNote.getId());
					newNotes.append(badNote.getOriginalText());
					newNotes.append("\n");
					badAdminCount++;
				}
			}

			
			contractor.setAdminNote(newNotes.toString());
			contractor.setBadAdminNotes(badAdminCount);
			contractorNoteDao.save(contractor);
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		
	}
	
	public List<Note> splitNotes( String notesField ) throws IOException {
		List<Note> response = new Vector<Note>();
		
		BufferedReader reader = new BufferedReader( new StringReader(notesField) );
	
		String line;
		
		while( ( line = reader.readLine() ) != null ) {
			Note note = new Note();
			note.setOriginalText(line);
			response.add(note);
		}
		
		return response;
	}

	public int getRemainder() {
		return remainder;
	}

	public void setRemainder(int remainder) {
		this.remainder = remainder;
	}

	public int getModBy() {
		return modBy;
	}

	public void setModBy(int modBy) {
		this.modBy = modBy;
	}
	
	
//	protected Date getDateFromBeginning( String line ) {
//		Date response = null;
//
//		Map<String, List<Integer>> dateFormats = HashMap<String, List<Integer>();
//		
//		dateFormats.add("MM/dd/yyyy a HH:mm:ss z");
//		
//		for( String format : dateFormats ) {
//			try {
//				SimpleDateFormat sdf = new SimpleDateFormat( format );
//				response = sdf.parse(source)
//				
//				
//			}
//			catch( Exception e ) {
//				
//			}
//		}
//
//		return response;
//	}
	
	
	
}
