package ninja.cfg.catnotepad.events;

import ninja.cfg.catnotepad.models.Note;

/**
 * Created by MohMah on 8/21/2016. modified 6/15/2020
 */
public class NoteDeletedEvent{
	Note note;

	public NoteDeletedEvent(Note note){
		this.note = note;
	}

	public Note getNote(){
		return note;
	}
}
