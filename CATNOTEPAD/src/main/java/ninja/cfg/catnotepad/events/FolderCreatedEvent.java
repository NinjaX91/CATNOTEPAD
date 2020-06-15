package ninja.cfg.catnotepad.events;

import ninja.cfg.catnotepad.models.Folder;

/**
 * Created by MohMah on 8/19/2016. modified 6/15/2020
 */
public class FolderCreatedEvent{
	private Folder folder;

	public FolderCreatedEvent(Folder folder){
		this.folder = folder;
	}

	public Folder getFolder(){
		return folder;
	}
}
