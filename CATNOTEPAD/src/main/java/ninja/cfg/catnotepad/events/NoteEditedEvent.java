package ninja.cfg.catnotepad.events;

import ninja.cfg.catnotepad.database.NotesDAO;
import ninja.cfg.catnotepad.models.Note;

/**
 * Created by MohMah on 8/21/2016. modified 6/15/2020
 */
public class NoteEditedEvent{
	int noteId;

	public NoteEditedEvent(int noteId){
		this.noteId = noteId;
	}

	public int getNoteId(){
		return noteId;
	}
	
	public Note getNote(){
		return NotesDAO.getNote(noteId);
	}
}
