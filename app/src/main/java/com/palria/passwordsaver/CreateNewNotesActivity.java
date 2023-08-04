package com.palria.passwordsaver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateNewNotesActivity extends AppCompatActivity {
    String notesId;
    ImageButton backButton;
    ImageButton setEditingActionButton;
    FloatingActionButton uploadNotesActionButton;
    EditText noteBodyEditText;
    EditText noteTitleEditText;
    AlertDialog alertDialog;
    boolean isEdition = false;
    boolean isViewOnly = false;
    boolean isFromLocal = false;
    AlertDialog confirmationDialog;
     boolean isEditing = false;
    NotesSQLiteDatabaseHelper notesSQLiteDatabaseHelper;
    LEBottomSheetDialog leBottomSheetDialog;
    String IS_SAVED_ONLINE =GlobalConfig.FALSE;
    NotesDataModel notesDataModel;
    AlertDialog successDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_notes);
        fetchIntentData();
        initUI();
        notesSQLiteDatabaseHelper = new NotesSQLiteDatabaseHelper(this);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewNotesActivity.super.onBackPressed();
            }
        });

        uploadNotesActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SECURITY TIPS",noteBodyEditText.getText()+"");
                leBottomSheetDialog.show();
            }
        });

        setEditingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
if(isEditing){
    setNotEditing();

}else{
    setIsEditing();
}

            }
        });
//if(isEdition && !isFromLocal){
//    fetchNotesDetails(new NotesFetchListener() {
//        @Override
//        public void onFailed(String errorMessage) {
//            toggleProgress(false);
//
//        }
//
//        @Override
//        public void onSuccess(NotesDataModel notesDataModel) {
//            noteTitleEditText.setText(notesDataModel.getNotesTitle());
//            noteBodyEditText.setText(notesDataModel.getNotesDescription());
//            toggleProgress(false);
//        }
//    });
//
//}
 if(isEdition || isViewOnly || isFromLocal){
    noteTitleEditText.setText(notesDataModel.getNotesTitle()+"");
    noteBodyEditText.setText(notesDataModel.getNotesDescription()+"");
}
else{
    notesId = GlobalConfig.getRandomString(70);
}

if(isViewOnly){
    setNotEditing();
}else{
    setIsEditing();
}
    }

    @Override
    public void onBackPressed(){
        createConfirmExitDialog();
    }
    private void fetchIntentData(){
        Intent intent = getIntent();
        isEdition = intent.getBooleanExtra(GlobalConfig.IS_EDITION,false);
        isViewOnly = intent.getBooleanExtra(GlobalConfig.IS_VIEW_ONLY,false);
        isFromLocal = intent.getBooleanExtra(GlobalConfig.IS_FROM_LOCAL,false);
        IS_SAVED_ONLINE = intent.getStringExtra(GlobalConfig.IS_SAVED_ONLINE);
        notesDataModel = (NotesDataModel) intent.getSerializableExtra(GlobalConfig.NOTES_DATA_MODEL);
        if(isEdition || isViewOnly || isFromLocal) {
            notesId = intent.getStringExtra(GlobalConfig.NOTES_ID);
        }
    }
    private void initUI(){
        backButton = findViewById(R.id.backButtonId);
        setEditingActionButton = findViewById(R.id.setEditingActionButtonId);
        uploadNotesActionButton = findViewById(R.id.uploadNotesActionButtonId);
        noteBodyEditText = findViewById(R.id.noteBodyEditTextId);
        noteTitleEditText = findViewById(R.id.noteTitleEditTextId);

        leBottomSheetDialog = new LEBottomSheetDialog(this);

        leBottomSheetDialog
                .addOptionItem("Save online", R.drawable.ic_baseline_lock_24, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        leBottomSheetDialog.hide();
                        showNoteUploadConfirmationDialog(true);

                    }
                }, 0)
                .addOptionItem("Save only local", R.drawable.ic_baseline_edit_24, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        leBottomSheetDialog.hide();
                        showNoteUploadConfirmationDialog(false);

                    }
                }, 0)
                .render();


        alertDialog = new AlertDialog.Builder(CreateNewNotesActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout, null))
                .create();
    }


    private void toggleProgress(boolean show)
    {
        if(show){
            alertDialog.show();
        }else{
            alertDialog.cancel();
        }
    }

    public void createNoteOnline(){
        String noteTitle = noteTitleEditText.getText()+"";
        String noteBody = noteBodyEditText.getText()+"";
if(!(noteBody.isEmpty() || noteTitle.isEmpty())) {
    toggleProgress(true);
WriteBatch writeBatch = GlobalConfig.getFirebaseFirestoreInstance().batch();

    HashMap<String,Object> notesDetails = new HashMap<>();
    notesDetails.put(GlobalConfig.NOTES_ID,notesId);
    notesDetails.put(GlobalConfig.NOTES_TITLE,noteTitle);
    notesDetails.put(GlobalConfig.NOTES_BODY,noteBody);
    notesDetails.put(GlobalConfig.NOTES_DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());
    if((!isEdition && !isViewOnly)|| (isFromLocal && !notesDataModel.isUploadedOnline())){
        notesDetails.put(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP, FieldValue.serverTimestamp());

        HashMap<String,Object> notesDetails1 = new HashMap<>();
        notesDetails1.put(GlobalConfig.TOTAL_NUMBER_OF_NOTES_CREATED,FieldValue.increment(1L));
        DocumentReference documentReference1 = GlobalConfig.getFirebaseFirestoreInstance()
                .collection(GlobalConfig.ALL_USERS)
                .document(GlobalConfig.getCurrentUserId());
        writeBatch.set(documentReference1,notesDetails1, SetOptions.merge());

    }
//    if(isFromLocal){
//        if(!notesDataModel.isUploadedOnline()){
//            notesDetails.put(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP, FieldValue.serverTimestamp());
//
//            HashMap<String,Object> notesDetails1 = new HashMap<>();
//            notesDetails1.put(GlobalConfig.TOTAL_NUMBER_OF_NOTES_CREATED,FieldValue.increment(1L));
//            DocumentReference documentReference1 = GlobalConfig.getFirebaseFirestoreInstance()
//                    .collection(GlobalConfig.ALL_USERS)
//                    .document(GlobalConfig.getCurrentUserId());
//            writeBatch.set(documentReference1,notesDetails1, SetOptions.merge());
//
//        }
//    }
   DocumentReference documentReference = GlobalConfig.getFirebaseFirestoreInstance()
            .collection(GlobalConfig.ALL_NOTES)
            .document(notesId);
    writeBatch.set(documentReference,notesDetails, SetOptions.merge());


    writeBatch.commit()
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toggleProgress(false);
                    GlobalConfig.createSnackBar(CreateNewNotesActivity.this, noteBodyEditText, "Your note failed to upload please try again", Snackbar.LENGTH_INDEFINITE);

                }
            })
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    updateNoteLocal(GlobalConfig.TRUE, new GlobalConfig.ActionCallback() {
                        @Override
                        public void onSuccess() {

                            toggleProgress(false);
                            createSuccessDialog();
                        }

                        @Override
                        public void onFailed(String errorMessage) {
                            toggleProgress(false);
                            GlobalConfig.createSnackBar(CreateNewNotesActivity.this, noteBodyEditText, "Your note failed to upload please try again", Snackbar.LENGTH_INDEFINITE);

                        }
                    });

                    toggleProgress(false);
                    createSuccessDialog();
                }
            });

}else{
    Toast.makeText(this, "Please enter title or body of the note to continue", Toast.LENGTH_SHORT).show();
}
    }
    void saveNoteLocal(String isSavedOnline ,GlobalConfig.ActionCallback actionCallback) {
        String noteId = notesId;
        String noteTitle = noteTitleEditText.getText() + "";
        String noteBody = noteBodyEditText.getText() + "";
        String dateCreated = GlobalConfig.getDate();
//        String isSavedOnline = GlobalConfig.FALSE;

        notesSQLiteDatabaseHelper.insertData(noteId, noteTitle, noteBody, dateCreated, isSavedOnline, new GlobalConfig.ActionCallback() {
            @Override
            public void onSuccess() {
                actionCallback.onSuccess();
            }

            @Override
            public void onFailed(String errorMessage) {
                actionCallback.onFailed(errorMessage);
            }
        });
    }
  void updateNoteLocal(String isSavedOnline,GlobalConfig.ActionCallback actionCallback){
        String noteId = notesId;
        String noteTitle = noteTitleEditText.getText()+"";
        String noteBody = noteBodyEditText.getText()+"";
//        String dateCreated = GlobalConfig.getDate();
//        String isSavedOnline = GlobalConfig.FALSE;

        notesSQLiteDatabaseHelper.updateData(noteId, noteTitle, noteBody, isSavedOnline, new GlobalConfig.ActionCallback() {
            @Override
            public void onSuccess() {
                actionCallback.onSuccess();
            }

            @Override
            public void onFailed(String errorMessage) {
                actionCallback.onFailed(errorMessage);

            }
        });

    }
    private void fetchNotesDetails(NotesFetchListener notesFetchListener){
        toggleProgress(true);
        GlobalConfig.getFirebaseFirestoreInstance()
                .collection(GlobalConfig.ALL_NOTES)
                .document(notesId)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        notesFetchListener.onFailed(e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String notesId =""+ documentSnapshot.get(GlobalConfig.NOTES_ID);
                        String notesTitle = ""+ documentSnapshot.get(GlobalConfig.NOTES_TITLE);
                        String notesBody = ""+ documentSnapshot.get(GlobalConfig.NOTES_BODY);
                        String dateCreated =  documentSnapshot.get(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP)!=null &&  documentSnapshot.get(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP) instanceof Timestamp ?  documentSnapshot.getTimestamp(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP).toDate().toString() : "Moment ago" ;
                        if(dateCreated.length()>10){
                            dateCreated = dateCreated.substring(0,10);
                        }

                        notesFetchListener.onSuccess( new NotesDataModel(
                                notesId,
                                notesTitle,
                                notesBody,
                                dateCreated,
                                true

                        ));
                    }
                });
    }


    private void createConfirmExitDialog(){

        AlertDialog confirmExitDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Exit");
        builder.setMessage("Click exit button to exit the screen");
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_baseline_error_outline_24);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CreateNewNotesActivity.super.onBackPressed();
            }
        })
                .setNegativeButton("Stay back", null);
        confirmExitDialog = builder.create();
        confirmExitDialog.show();

    }
    private void createSuccessDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Success");
        builder.setMessage("Your note was uploaded successfully");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_error_outline_24);
        builder.setPositiveButton("New note", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CreateNewNotesActivity.this, CreateNewNotesActivity.class);
                intent.putExtra(GlobalConfig.NOTES_ID, "");
                intent.putExtra(GlobalConfig.IS_EDITION, false);
                startActivity(intent);
                CreateNewNotesActivity.super.onBackPressed();

            }
        })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CreateNewNotesActivity.super.onBackPressed();

                    }
                });
        successDialog = builder.create();
        successDialog.show();

    }

    private void showNoteUploadConfirmationDialog(boolean isOnline){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm your action");
        builder.setMessage("You are about to upload your note, please confirm if you are done editing it");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_error_outline_24);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isOnline) {
                    createNoteOnline();
                    if(!notesSQLiteDatabaseHelper.exists(notesId)) {
                        saveNoteLocal(IS_SAVED_ONLINE,new GlobalConfig.ActionCallback() {
                            @Override
                            public void onSuccess() {

                                toggleProgress(false);
                                createSuccessDialog();
                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                toggleProgress(false);
                                GlobalConfig.createSnackBar(CreateNewNotesActivity.this, noteBodyEditText, "Your note failed to save please try again", Snackbar.LENGTH_INDEFINITE);

                            }
                        });
                    }else{
                    if (!isFromLocal && !isEdition && !isViewOnly) {
                        saveNoteLocal(IS_SAVED_ONLINE,new GlobalConfig.ActionCallback() {
                            @Override
                            public void onSuccess() {

                                toggleProgress(false);
                                createSuccessDialog();
                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                toggleProgress(false);
                                GlobalConfig.createSnackBar(CreateNewNotesActivity.this, noteBodyEditText, "Your note failed to save please try again", Snackbar.LENGTH_INDEFINITE);

                            }
                        });
                    }
                }
                }else {
                    if(!notesSQLiteDatabaseHelper.exists(notesId)) {
                        saveNoteLocal(IS_SAVED_ONLINE,new GlobalConfig.ActionCallback() {
                            @Override
                            public void onSuccess() {

                                toggleProgress(false);
                                createSuccessDialog();
                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                toggleProgress(false);
                                GlobalConfig.createSnackBar(CreateNewNotesActivity.this, noteBodyEditText, "Your note failed to save please try again", Snackbar.LENGTH_INDEFINITE);

                            }
                        });
                    }else{
                    if (isEdition || isViewOnly || isFromLocal) {
                        updateNoteLocal(IS_SAVED_ONLINE, new GlobalConfig.ActionCallback() {
                            @Override
                            public void onSuccess() {

                                toggleProgress(false);
                                createSuccessDialog();
                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                toggleProgress(false);
                                GlobalConfig.createSnackBar(CreateNewNotesActivity.this, noteBodyEditText, "Your note failed to save please try again", Snackbar.LENGTH_INDEFINITE);

                            }
                        });
                    } else {
                        saveNoteLocal(IS_SAVED_ONLINE,new GlobalConfig.ActionCallback() {
                            @Override
                            public void onSuccess() {

                                toggleProgress(false);
                                createSuccessDialog();
                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                toggleProgress(false);
                                GlobalConfig.createSnackBar(CreateNewNotesActivity.this, noteBodyEditText, "Your note failed to save please try again", Snackbar.LENGTH_INDEFINITE);

                            }
                        });
                    }
                }
                }
            }
        })
                .setNegativeButton("Edit more", null);
        confirmationDialog = builder.create();
        confirmationDialog.show();

        // successDialog.show();

    }

void setNotEditing(){
    uploadNotesActionButton.setVisibility(View.GONE);
    noteBodyEditText.setEnabled(false);
    noteTitleEditText.setEnabled(false);
    noteTitleEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    setEditingActionButton.setImageResource(R.drawable.ic_baseline_edit_24);

    isEditing = false;
}
void setIsEditing(){
    uploadNotesActionButton.setVisibility(View.VISIBLE);
    noteBodyEditText.setEnabled(true);
    noteBodyEditText.requestFocus();
    noteTitleEditText.setEnabled(true);
    noteTitleEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    setEditingActionButton.setImageResource(R.drawable.ic_baseline_lock_24);

    isEditing = true;

}

    interface NotesFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(NotesDataModel notesDataModel);
    }

}