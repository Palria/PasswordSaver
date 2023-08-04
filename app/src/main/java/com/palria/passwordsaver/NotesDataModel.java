package com.palria.passwordsaver;

import java.io.Serializable;

public class NotesDataModel implements Serializable {
    private String notesId;
    private String notesTitle;
    private String notesDescription;
    private String dateAdded;
    private boolean isUploadedOnline;

    public NotesDataModel(String notesId, String notesTitle, String notesDescription, String dateAdded,boolean isUploadedOnline){
        this.notesId = notesId;
        this.notesTitle = notesTitle;
        this.notesDescription = notesDescription;
        this.dateAdded = dateAdded;
        this.isUploadedOnline = isUploadedOnline;
    }

    public String getNotesId() {
        return notesId;
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public String getNotesDescription() {
        return notesDescription;
    }
    public String getDateAdded() {
        return dateAdded;
    }
    public boolean isUploadedOnline() {
        return isUploadedOnline;
    }
}
