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


public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {

    ArrayList<PasswordDataModel> passwordDataModelArrayList;
    Context context;
    boolean isFromLocalContext;


    public PasswordAdapter(Context context, ArrayList<PasswordDataModel> passwordDataModelArrayList, boolean isFromLocalContext) {
        this.passwordDataModelArrayList = passwordDataModelArrayList;
        this.context = context;
        this.isFromLocalContext = isFromLocalContext;
    }

    @NonNull
    @Override
    public PasswordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.password_item_layout, parent, false);
       ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordAdapter.ViewHolder holder, int position) {
        PasswordDataModel passwordDataModel = passwordDataModelArrayList.get(position);

            holder.passwordTitleTextView.setText(passwordDataModel.getPasswordTitle());
            holder.dateAddedTextView.setText(passwordDataModel.getDateAdded());
            holder.passwordDescriptionTextView.setText(passwordDataModel.getPasswordDescription());

        if(passwordDataModel.isUploadedOnline()){
            holder.uploadPasswordOnlineActionButton.setVisibility(View.GONE);
        }else{
            holder.uploadPasswordOnlineActionButton.setVisibility(View.VISIBLE);
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

                            if(passwordDataModel.isUploadedOnline() && !isFromLocalContext){
                                WriteBatch writeBatch = GlobalConfig.getFirebaseFirestoreInstance().batch();

                                DocumentReference documentReference = GlobalConfig.getFirebaseFirestoreInstance().collection(GlobalConfig.ALL_PASSWORDS).document(passwordDataModel.getPasswordId());
                                writeBatch.delete(documentReference);

                                HashMap<String,Object> passwordDetails1 = new HashMap<>();
                                passwordDetails1.put(GlobalConfig.TOTAL_NUMBER_OF_PASSWORD_CREATED,FieldValue.increment(-1L));
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

                                    }
                                });
                            }
                            else{
                                new PasswordsSQLiteDatabaseHelper(context).deleteData(passwordDataModel.getPasswordId(), new GlobalConfig.ActionCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailed(String errorMessage) {

                                    }
                                });


                            }
                            int positionDeleted = passwordDataModelArrayList.indexOf(passwordDataModel);
                            passwordDataModelArrayList.remove(passwordDataModel);
                            notifyItemChanged(positionDeleted);

                        }
                        else if(item.getItemId() == R.id.editId){
                            Intent intent = new Intent(context, AddNewPasswordActivity.class);
                            intent.putExtra(GlobalConfig.PASSWORD_ID, passwordDataModel.getPasswordId());
                            intent.putExtra(GlobalConfig.PASSWORD_DATA_MODEL, passwordDataModel);
                            intent.putExtra(GlobalConfig.IS_EDITION, true);
                            intent.putExtra(GlobalConfig.IS_SAVED_ONLINE, passwordDataModel.isUploadedOnline()?GlobalConfig.TRUE:GlobalConfig.FALSE);
                            intent.putExtra(GlobalConfig.IS_FROM_LOCAL, !passwordDataModel.isUploadedOnline());
                            intent.putExtra(GlobalConfig.IS_VIEW_ONLY, false);
                            context.startActivity(intent);
                        }


                        return true;
                    }
                });
            }
        });

        holder.uploadPasswordOnlineActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(context, AddNewPasswordActivity.class);
                intent.putExtra(GlobalConfig.PASSWORD_ID, passwordDataModel.getPasswordId());
                intent.putExtra(GlobalConfig.PASSWORD_DATA_MODEL, passwordDataModel);
                intent.putExtra(GlobalConfig.IS_FROM_LOCAL, !passwordDataModel.isUploadedOnline());
                intent.putExtra(GlobalConfig.IS_SAVED_ONLINE, passwordDataModel.isUploadedOnline()?GlobalConfig.TRUE:GlobalConfig.FALSE);
                intent.putExtra(GlobalConfig.IS_EDITION, true);
                intent.putExtra(GlobalConfig.IS_FROM_LOCAL, true);
                intent.putExtra(GlobalConfig.IS_VIEW_ONLY, false);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(context, AddNewPasswordActivity.class);
                intent.putExtra(GlobalConfig.PASSWORD_ID, passwordDataModel.getPasswordId());
                intent.putExtra(GlobalConfig.PASSWORD_DATA_MODEL, passwordDataModel);
                intent.putExtra(GlobalConfig.IS_SAVED_ONLINE, passwordDataModel.isUploadedOnline()?GlobalConfig.TRUE:GlobalConfig.FALSE);
                intent.putExtra(GlobalConfig.IS_FROM_LOCAL, !passwordDataModel.isUploadedOnline());
                intent.putExtra(GlobalConfig.IS_EDITION, false);
                intent.putExtra(GlobalConfig.IS_VIEW_ONLY, true);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return passwordDataModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageButton menuActionButton;
        public TextView passwordTitleTextView;
        public TextView dateAddedTextView;
        public TextView passwordDescriptionTextView;
        public Button uploadPasswordOnlineActionButton;
        public ProgressBar deleteProgressIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            this.menuActionButton =  itemView.findViewById(R.id.menuActionButtonId);
            this.passwordTitleTextView =  itemView.findViewById(R.id.passwordTitleTextViewId);
            this.dateAddedTextView =  itemView.findViewById(R.id.dateAddedTextViewId);
            this.passwordDescriptionTextView =  itemView.findViewById(R.id.passwordDescriptionTextViewId);
            this.uploadPasswordOnlineActionButton =  itemView.findViewById(R.id.uploadPasswordOnlineActionButtonId);
            this.deleteProgressIndicator =  itemView.findViewById(R.id.deleteProgressIndicatorId);

        }
    }

}

