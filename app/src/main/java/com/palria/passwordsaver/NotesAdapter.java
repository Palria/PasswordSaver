package com.palria.passwordsaver;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    ArrayList<NotesDataModel> notesDataModelArrayList;
    Context context;
    boolean isFromLocalContext;


    public NotesAdapter(Context context, ArrayList<NotesDataModel> notesDataModelArrayList, boolean isFromLocalContext) {
        this.notesDataModelArrayList = notesDataModelArrayList;
        this.context = context;
        this.isFromLocalContext = isFromLocalContext;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.notes_item_layout, parent, false);
       ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        NotesDataModel notesDataModel = notesDataModelArrayList.get(position);

            holder.notesTitleTextView.setText(notesDataModel.getNotesTitle());
            holder.dateAddedTextView.setText(notesDataModel.getDateAdded());
            holder.notesDescriptionTextView.setText(notesDataModel.getNotesDescription());

            if(notesDataModel.isUploadedOnline()){
                holder.uploadNoteOnlineActionButton.setVisibility(View.GONE);
            }

        holder.menuActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalConfig.createPopUpMenu(context, R.menu.password_action_menu, holder.menuActionButton, new GlobalConfig.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {
                        if(item.getItemId() == R.id.deleteId){
//                            holder.itemView.setEnabled(false);
//                            holder.deleteProgressIndicator.setVisibility(View.VISIBLE);


                            if(notesDataModel.isUploadedOnline() && !isFromLocalContext){
                                WriteBatch writeBatch = GlobalConfig.getFirebaseFirestoreInstance().batch();

                                DocumentReference documentReference = GlobalConfig.getFirebaseFirestoreInstance().collection(GlobalConfig.ALL_NOTES).document(notesDataModel.getNotesId());
                                writeBatch.delete(documentReference);

                                HashMap<String,Object> passwordDetails1 = new HashMap<>();
                                passwordDetails1.put(GlobalConfig.TOTAL_NUMBER_OF_NOTES_CREATED, FieldValue.increment(-1L));
                                DocumentReference documentReference1 = GlobalConfig.getFirebaseFirestoreInstance()
                                        .collection(GlobalConfig.ALL_USERS)
                                        .document(GlobalConfig.getCurrentUserId());
                                writeBatch.set(documentReference1,passwordDetails1, SetOptions.merge());

                                writeBatch.commit().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        holder.itemView.setEnabled(true);
//                                        holder.deleteProgressIndicator.setVisibility(View.GONE);
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
//                                        int positionDeleted = notesDataModelArrayList.indexOf(notesDataModel);
//                                        notesDataModelArrayList.remove(notesDataModel);
//                                        notifyItemChanged(positionDeleted);

                                  }
                                });
                            }


                            else{
                                new NotesSQLiteDatabaseHelper(context).deleteData(notesDataModel.getNotesId(), new GlobalConfig.ActionCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailed(String errorMessage) {

                                    }
                                });

                            }
                            int positionDeleted = notesDataModelArrayList.indexOf(notesDataModel);
                            notesDataModelArrayList.remove(notesDataModel);
                            notifyItemChanged(positionDeleted);


                        }
                        else if(item.getItemId() == R.id.editId){
                                Intent intent = new Intent(context, CreateNewNotesActivity.class);
                                intent.putExtra(GlobalConfig.NOTES_ID, notesDataModel.getNotesId());
                                intent.putExtra(GlobalConfig.NOTES_DATA_MODEL, notesDataModel);
                                intent.putExtra(GlobalConfig.IS_EDITION, true);
                                intent.putExtra(GlobalConfig.IS_SAVED_ONLINE, notesDataModel.isUploadedOnline()?GlobalConfig.TRUE:GlobalConfig.FALSE);
                                intent.putExtra(GlobalConfig.IS_FROM_LOCAL, !notesDataModel.isUploadedOnline());
                                intent.putExtra(GlobalConfig.IS_VIEW_ONLY, false);
                                context.startActivity(intent);
                        }


                        return true;
                    }
                });
            }
        });

        holder.uploadNoteOnlineActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(context, CreateNewNotesActivity.class);
                intent.putExtra(GlobalConfig.NOTES_ID, notesDataModel.getNotesId());
                intent.putExtra(GlobalConfig.NOTES_DATA_MODEL, notesDataModel);
                intent.putExtra(GlobalConfig.IS_FROM_LOCAL, !notesDataModel.isUploadedOnline());
                intent.putExtra(GlobalConfig.IS_SAVED_ONLINE, notesDataModel.isUploadedOnline()?GlobalConfig.TRUE:GlobalConfig.FALSE);
                intent.putExtra(GlobalConfig.IS_EDITION, true);
                intent.putExtra(GlobalConfig.IS_FROM_LOCAL, true);
                intent.putExtra(GlobalConfig.IS_VIEW_ONLY, false);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                        Intent intent = new Intent(context, CreateNewNotesActivity.class);
                        intent.putExtra(GlobalConfig.NOTES_ID, notesDataModel.getNotesId());
                        intent.putExtra(GlobalConfig.NOTES_DATA_MODEL, notesDataModel);
                        intent.putExtra(GlobalConfig.IS_SAVED_ONLINE, notesDataModel.isUploadedOnline()?GlobalConfig.TRUE:GlobalConfig.FALSE);
                        intent.putExtra(GlobalConfig.IS_FROM_LOCAL, !notesDataModel.isUploadedOnline());
                        intent.putExtra(GlobalConfig.IS_EDITION, false);
                        intent.putExtra(GlobalConfig.IS_VIEW_ONLY, true);
                        context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return notesDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageButton menuActionButton;
        public TextView notesTitleTextView;
        public TextView dateAddedTextView;
        public TextView notesDescriptionTextView;
        public Button uploadNoteOnlineActionButton;
        public ProgressBar deleteProgressIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            this.menuActionButton =  itemView.findViewById(R.id.menuActionButtonId);
            this.notesTitleTextView =  itemView.findViewById(R.id.notesTitleTextViewId);
            this.dateAddedTextView =  itemView.findViewById(R.id.dateAddedTextViewId);
            this.notesDescriptionTextView =  itemView.findViewById(R.id.notesDescriptionTextViewId);
            this.uploadNoteOnlineActionButton =  itemView.findViewById(R.id.uploadNoteOnlineActionButtonId);
            this.deleteProgressIndicator =  itemView.findViewById(R.id.deleteProgressIndicatorId);

        }
    }

}

