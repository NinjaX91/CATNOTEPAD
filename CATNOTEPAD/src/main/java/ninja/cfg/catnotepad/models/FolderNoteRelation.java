package ninja.cfg.catnotepad.models;


import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import ninja.cfg.catnotepad.database.AppDatabase;

/**
 * Created by MohMah on 8/17/2016. Modified by NinjaX91 to add App Center SDK and Intune SDK 6/15/2020
 */
@Table(database = AppDatabase.class, allFields = true)
public class FolderNoteRelation extends BaseModel {
	private static final String TAG = "FolderNoteRelation";

	@PrimaryKey
	@ForeignKey(onDelete = ForeignKeyAction.CASCADE)
	private Folder folder;

	@PrimaryKey
	@ForeignKey(onDelete = ForeignKeyAction.CASCADE)
	private Note note;

	public Folder getFolder(){
		return folder;
	}

	public void setFolder(Folder folder){
		this.folder = folder;
	}

	public Note getNote(){
		return note;
	}

	public void setNote(Note note){
		this.note = note;
	}

}
