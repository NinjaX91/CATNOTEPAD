package ninja.cfg.catnotepad.events;

import ninja.cfg.catnotepad.database.NotesDAO;
import ninja.cfg.catnotepad.models.Note;

/**
 * Created by MohMah on 8/22/2016.
 */
public class NoteFoldersUpdatedEvent{

	int noteId;

	public NoteFoldersUpdatedEvent(int noteId){
		this.noteId = noteId;
	}

	public int getNoteId(){
		return noteId;
	}

	public Note getNote(){
		return NotesDAO.getNote(noteId);
	}
}
