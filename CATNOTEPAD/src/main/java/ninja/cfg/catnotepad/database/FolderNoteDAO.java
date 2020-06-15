package ninja.cfg.catnotepad.database;


import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ninja.cfg.catnotepad.models.Folder;
import ninja.cfg.catnotepad.models.FolderNoteRelation;
import ninja.cfg.catnotepad.models.FolderNoteRelation_Table;
import ninja.cfg.catnotepad.models.Note;

/**
 * Created by MohMah on 8/20/2016. modified 6/15/2020
 */
public class FolderNoteDAO{
	public static List<Folder> getFolders(int noteId){
		List<FolderNoteRelation> folderNoteRelations = SQLite
				.select()
				.from(FolderNoteRelation.class)
				.where(FolderNoteRelation_Table.note_id.is(noteId))
				.queryList();
		List<Folder> folders = new ArrayList<>(folderNoteRelations.size());
		for (FolderNoteRelation folderNoteRelation : folderNoteRelations){
			folderNoteRelation.load();
			folders.add(folderNoteRelation.getFolder());
		}
		return folders;
	}

	public static List<Note> getLatestNotes(Folder folder){
		List<FolderNoteRelation> folderNoteRelations = SQLite
				.select()
				.from(FolderNoteRelation.class)
				.where(FolderNoteRelation_Table.folder_id.is(folder.getId()))
				.queryList();
		List<Note> notes = new ArrayList<>(folderNoteRelations.size());
		for (FolderNoteRelation folderNoteRelation : folderNoteRelations){
			folderNoteRelation.load();
			notes.add(folderNoteRelation.getNote());
		}
		Collections.sort(notes, new Comparator<Note>(){
			@Override public int compare(Note lhs, Note rhs){
				return lhs.getCreatedAt().compareTo(rhs.getCreatedAt());
			}
		});
		return notes;
	}

	public static void removeFolderNoteRelation(Folder folder, Note note){
		SQLite
				.delete()
				.from(FolderNoteRelation.class)
				.where(FolderNoteRelation_Table.note_id.is(note.getId()), FolderNoteRelation_Table.folder_id.is(folder.getId()))
				.execute();
	}

	public static void createFolderNoteRelation(Folder folder, Note note){
		FolderNoteRelation fnr = new FolderNoteRelation();
		fnr.setFolder(folder);
		fnr.setNote(note);
		fnr.save();
	}
}
