package com.palria.passwordsaver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
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

public class AddNewPasswordActivity extends AppCompatActivity {
    String passwordId;
    ImageButton backButton;
    ImageButton setEditingActionButton;
    FloatingActionButton uploadPasswordActionButton;

    TextInputEditText passwordDescriptionEditText;
    TextInputEditText passwordTitleEditText;

    TextInputEditText passwordInput;
    TextInputEditText PINInput;
    TextInputEditText TOKENInput;
    TextInputEditText CODEInput;
    TextInputEditText PHONEInput;
    TextInputEditText EMAILInput;
    LinearLayout newItemContainerLinearLayout;
    TextView addMoreItemsActionTextView;
    AlertDialog successDialog;
    AlertDialog alertDialog;
    boolean isEdition = false;
    boolean isViewOnly = false;
    boolean isFromLocal = false;
    AlertDialog confirmationDialog;
    boolean isEditing = false;
    PasswordsSQLiteDatabaseHelper passwordSQLiteDatabaseHelper;
    LEBottomSheetDialog leBottomSheetDialog;
    String IS_SAVED_ONLINE =GlobalConfig.FALSE;
    PasswordDataModel passwordDataModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_password);
        fetchIntentData();
        initUI();
        passwordSQLiteDatabaseHelper = new PasswordsSQLiteDatabaseHelper(this);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewPasswordActivity.super.onBackPressed();
            }
        });

        uploadPasswordActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leBottomSheetDialog.show();
            }
        });
        addMoreItemsActionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewItem("","");
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
//        if(isEdition && !isFromLocal){
//            fetchPasswordDetails(new PasswordFetchListener() {
//                @Override
//                public void onFailed(String errorMessage) {
//                    toggleProgress(false);
//
//                }
//
//                @Override
//                public void onSuccess(PasswordDataModel passwordDataModel) {
//                    passwordTitleEditText.setText(passwordDataModel.getPasswordTitle());
//                    passwordDescriptionEditText.setText(passwordDataModel.getPasswordDescription());
//                    toggleProgress(false);
//                }
//            });
//
//        }
         if(isEdition || isViewOnly || isFromLocal){
            passwordTitleEditText.setText(passwordDataModel.getPasswordTitle());
            passwordDescriptionEditText.setText(passwordDataModel.getPasswordDescription());
            passwordInput.setText(passwordDataModel.getPassword());
            PINInput.setText(passwordDataModel.getPin());
            TOKENInput.setText(passwordDataModel.getToken());
            CODEInput.setText(passwordDataModel.getCode());
            PHONEInput.setText(passwordDataModel.getPhone());
            EMAILInput.setText(passwordDataModel.getEmail());

            String itemTitles = passwordDataModel.getItemTitles();
            String itemValues = passwordDataModel.getItemValues();

            String[] itemTitleList = itemTitles.split(",");
            String[] itemValueList = itemValues.split(",");

            if(itemTitleList.length!=0 || itemValueList.length!=0) {
                for (int i = 0; i < itemTitleList.length; i++) {
                    addNewItem(itemTitleList[i], itemValueList[i]);
                }
            }
        }
        else{
            passwordId = GlobalConfig.getRandomString(70);
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
        passwordDataModel = (PasswordDataModel) intent.getSerializableExtra(GlobalConfig.PASSWORD_DATA_MODEL);
        if(isEdition || isViewOnly || isFromLocal) {
            passwordId = intent.getStringExtra(GlobalConfig.PASSWORD_ID);
        }
    }
    private void initUI(){
        backButton = findViewById(R.id.backButtonId);
        setEditingActionButton = findViewById(R.id.setEditingActionButtonId);
        uploadPasswordActionButton = findViewById(R.id.uploadPassowordsActionButtonId);
        passwordDescriptionEditText = findViewById(R.id.infoDescriptionTextEditId);
        passwordTitleEditText = findViewById(R.id.passwordTitleEditTextId);

        passwordInput = findViewById(R.id.passwordInputId);
        PINInput = findViewById(R.id.PINInputId);
        TOKENInput = findViewById(R.id.TOKENInputId);
        CODEInput = findViewById(R.id.CODEInputId);
        PHONEInput = findViewById(R.id.PHONEInputId);
        EMAILInput = findViewById(R.id.EMAILInputId);

        newItemContainerLinearLayout = findViewById(R.id.newItemContainerLinearLayoutId);
        addMoreItemsActionTextView = findViewById(R.id.addMoreItemsActionTextViewId);

        leBottomSheetDialog = new LEBottomSheetDialog(this);

        leBottomSheetDialog
                .addOptionItem("Save online", R.drawable.ic_baseline_lock_24, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        leBottomSheetDialog.hide();
                        showPasswordUploadConfirmationDialog(true);

                    }
                }, 0)
                .addOptionItem("Save only local", R.drawable.ic_baseline_edit_24, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        leBottomSheetDialog.hide();
                        showPasswordUploadConfirmationDialog(false);

                    }
                }, 0)
                .render();


        alertDialog = new AlertDialog.Builder(AddNewPasswordActivity.this)
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
        String passwordTitle = passwordTitleEditText.getText()+"";
        String passwordDescription = passwordDescriptionEditText.getText()+"";
        if(!(passwordDescription.isEmpty() || passwordTitle.isEmpty())) {
            toggleProgress(true);
            WriteBatch writeBatch = GlobalConfig.getFirebaseFirestoreInstance().batch();

            HashMap<String,Object> passwordDetails = new HashMap<>();
            passwordDetails.put(GlobalConfig.PASSWORD_ID,passwordId);
            passwordDetails.put(GlobalConfig.PASSWORD_TITLE,passwordTitle);
            passwordDetails.put(GlobalConfig.PASSWORD_DESCRIPTION,passwordDescription);
            passwordDetails.put(GlobalConfig.PASSWORD_DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());
            passwordDetails.put(GlobalConfig.PASSWORD_CREDENTIAL, passwordInput.getText()+"");
            passwordDetails.put(GlobalConfig.PIN_CREDENTIAL, PINInput.getText()+"");
            passwordDetails.put(GlobalConfig.TOKEN_CREDENTIAL, TOKENInput.getText()+"");
            passwordDetails.put(GlobalConfig.CODE_CREDENTIAL, CODEInput.getText()+"");
            passwordDetails.put(GlobalConfig.PHONE_CREDENTIAL, PHONEInput.getText()+"");
            passwordDetails.put(GlobalConfig.EMAIL_CREDENTIAL, EMAILInput.getText()+"");
            passwordDetails.put(GlobalConfig.ITEM_TITLES, getItemTitles());
            passwordDetails.put(GlobalConfig.ITEM_VALUES, getItemValues());

            if((!isEdition && !isViewOnly)|| (isFromLocal && !passwordDataModel.isUploadedOnline())){
                passwordDetails.put(GlobalConfig.PASSWORD_DATE_CREATED_TIME_STAMP, FieldValue.serverTimestamp());

                HashMap<String,Object> passwordDetails1 = new HashMap<>();
                passwordDetails1.put(GlobalConfig.TOTAL_NUMBER_OF_PASSWORD_CREATED,FieldValue.increment(1L));
                DocumentReference documentReference1 = GlobalConfig.getFirebaseFirestoreInstance()
                        .collection(GlobalConfig.ALL_USERS)
                        .document(GlobalConfig.getCurrentUserId());
                writeBatch.set(documentReference1,passwordDetails1, SetOptions.merge());

            }
//            if(isFromLocal){
//                if(!passwordDataModel.isUploadedOnline()){
//                    passwordDetails.put(GlobalConfig.PASSWORD_DATE_CREATED_TIME_STAMP, FieldValue.serverTimestamp());
//
//                    HashMap<String,Object> passwordDetails1 = new HashMap<>();
//                    passwordDetails1.put(GlobalConfig.TOTAL_NUMBER_OF_PASSWORD_CREATED,FieldValue.increment(1L));
//                    DocumentReference documentReference1 = GlobalConfig.getFirebaseFirestoreInstance()
//                            .collection(GlobalConfig.ALL_USERS)
//                            .document(GlobalConfig.getCurrentUserId());
//                    writeBatch.set(documentReference1,passwordDetails1, SetOptions.merge());
//
//                }
            //}
            DocumentReference documentReference = GlobalConfig.getFirebaseFirestoreInstance()
                    .collection(GlobalConfig.ALL_PASSWORDS)
                    .document(passwordId);
            writeBatch.set(documentReference,passwordDetails, SetOptions.merge());


            writeBatch.commit()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toggleProgress(false);
                            GlobalConfig.createSnackBar(AddNewPasswordActivity.this, passwordDescriptionEditText, "Your credential failed to upload please try again", Snackbar.LENGTH_INDEFINITE);

                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updatePasswordLocal(GlobalConfig.TRUE, new GlobalConfig.ActionCallback() {
                                @Override
                                public void onSuccess() {

                                    toggleProgress(false);
                                    createSuccessDialog();
                                }

                                @Override
                                public void onFailed(String errorMessage) {
                                    toggleProgress(false);
                                    GlobalConfig.createSnackBar(AddNewPasswordActivity.this, passwordDescriptionEditText, "Your credential failed to upload please try again", Snackbar.LENGTH_INDEFINITE);

                                }
                            });

                            toggleProgress(false);
                            createSuccessDialog();
                        }
                    });

        }else{
            Toast.makeText(this, "Please enter title or description of the credential to continue", Toast.LENGTH_SHORT).show();
        }
    }
    void savePasswordLocal(String isSavedOnline, GlobalConfig.ActionCallback actionCallback) {
        String passwordId1 = passwordId;
        String passwordTitle = passwordTitleEditText.getText() + "";
        String passwordDescription = passwordDescriptionEditText.getText() + "";
        String dateCreated = GlobalConfig.getDate();
//        String isSavedOnline = GlobalConfig.FALSE;
        String password = passwordInput.getText() + "";
        String pin = PINInput.getText() + "";
        String token = TOKENInput.getText() + "";
        String code = CODEInput.getText() + "";
        String phone = PHONEInput.getText() + "";
        String email = EMAILInput.getText() + "";
        String itemTitles = getItemTitles();
        String itemValues = getItemValues();
        passwordSQLiteDatabaseHelper.insertData(passwordId1, passwordTitle, passwordDescription, dateCreated, isSavedOnline
                , password, pin, token, code, phone, email, itemTitles, itemValues, new GlobalConfig.ActionCallback() {
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
    void updatePasswordLocal(String isSavedOnline,GlobalConfig.ActionCallback actionCallback){
        String passwordId1 = passwordId;
        String passwordTitle = passwordTitleEditText.getText() + "";
        String passwordDescription = passwordDescriptionEditText.getText() + "";
        String password = passwordInput.getText() + "";
        String pin = PINInput.getText() + "";
        String token = TOKENInput.getText() + "";
        String code = CODEInput.getText() + "";
        String phone = PHONEInput.getText() + "";
        String email = EMAILInput.getText() + "";
        String itemTitles = getItemTitles();
        String itemValues = getItemValues();
        passwordSQLiteDatabaseHelper.updateData(passwordId1, passwordTitle, passwordDescription, isSavedOnline
                , password, pin, token, code, phone, email, itemTitles, itemValues, new GlobalConfig.ActionCallback() {
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
    private void fetchPasswordDetails(PasswordFetchListener passwordFetchListener){
        toggleProgress(true);
        GlobalConfig.getFirebaseFirestoreInstance()
                .collection(GlobalConfig.ALL_PASSWORDS)
                .document(passwordId)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        passwordFetchListener.onFailed(e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String passwordId =""+ documentSnapshot.get(GlobalConfig.PASSWORD_ID);
                String passwordTitle = ""+ documentSnapshot.get(GlobalConfig.PASSWORD_TITLE);
                String passwordDescription = ""+ documentSnapshot.get(GlobalConfig.PASSWORD_DESCRIPTION);
                String dateCreated =  documentSnapshot.get(GlobalConfig.PASSWORD_DATE_CREATED_TIME_STAMP)!=null &&  documentSnapshot.get(GlobalConfig.PASSWORD_DATE_CREATED_TIME_STAMP) instanceof Timestamp ?  documentSnapshot.getTimestamp(GlobalConfig.PASSWORD_DATE_CREATED_TIME_STAMP).toDate().toString() : "Moment ago" ;
                if(dateCreated.length()>10){
                    dateCreated = dateCreated.substring(0,10);
                }
                String password = ""+ documentSnapshot.get(GlobalConfig.PASSWORD_CREDENTIAL);
                String pin = ""+ documentSnapshot.get(GlobalConfig.PIN_CREDENTIAL);
                String token = ""+ documentSnapshot.get(GlobalConfig.TOKEN_CREDENTIAL);
                String code = ""+ documentSnapshot.get(GlobalConfig.CODE_CREDENTIAL);
                String phone = ""+ documentSnapshot.get(GlobalConfig.PHONE_CREDENTIAL);
                String email = ""+ documentSnapshot.get(GlobalConfig.EMAIL_CREDENTIAL);
                String itemTitles = ""+ documentSnapshot.get(GlobalConfig.ITEM_TITLES);
                String itemValues = ""+ documentSnapshot.get(GlobalConfig.ITEM_VALUES);

                passwordFetchListener.onSuccess(new PasswordDataModel(passwordId, passwordTitle, passwordDescription, dateCreated, true
                        , password, pin, token, code, phone, email, itemTitles, itemValues));
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
                AddNewPasswordActivity.super.onBackPressed();
            }
        })
                .setNegativeButton("Stay back", null);
        confirmExitDialog = builder.create();
        confirmExitDialog.show();

    }
    private void createSuccessDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Success");
        builder.setMessage("Your credentials was uploaded successfully");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_error_outline_24);
        builder.setPositiveButton("New Credential", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AddNewPasswordActivity.this, AddNewPasswordActivity.class);
                intent.putExtra(GlobalConfig.PASSWORD_ID, "");
                intent.putExtra(GlobalConfig.IS_EDITION, false);
                startActivity(intent);
                AddNewPasswordActivity.super.onBackPressed();

            }
        })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddNewPasswordActivity.super.onBackPressed();

                    }
                });
        successDialog = builder.create();
        successDialog.show();

    }

    private void showPasswordUploadConfirmationDialog(boolean isOnline){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm your action");
        builder.setMessage("You are about to upload your credentials, please confirm if you are done editing it");
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_baseline_error_outline_24);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isOnline) {
                    createNoteOnline();
                    if(!passwordSQLiteDatabaseHelper.exists(passwordId)) {
                        savePasswordLocal(IS_SAVED_ONLINE,new GlobalConfig.ActionCallback() {
                            @Override
                            public void onSuccess() {

                                toggleProgress(false);
                                createSuccessDialog();
                            }

                            @Override
                            public void onFailed(String errorMessage) {

                                toggleProgress(false);
                                GlobalConfig.createSnackBar(AddNewPasswordActivity.this, passwordDescriptionEditText, "Your credential failed to save please try again", Snackbar.LENGTH_INDEFINITE);

                            }
                        });
                    }else{
                        if (!isFromLocal && !isEdition && !isViewOnly) {
                            savePasswordLocal(IS_SAVED_ONLINE,new GlobalConfig.ActionCallback() {
                                @Override
                                public void onSuccess() {

                                    toggleProgress(false);
                                    createSuccessDialog();
                                }

                                @Override
                                public void onFailed(String errorMessage) {

                                    toggleProgress(false);
                                    GlobalConfig.createSnackBar(AddNewPasswordActivity.this, passwordDescriptionEditText, "Your credential failed to save please try again", Snackbar.LENGTH_INDEFINITE);

                                }
                            });

                        }
                    }
                }else {
                    if (!passwordSQLiteDatabaseHelper.exists(passwordId)) {
                        savePasswordLocal(IS_SAVED_ONLINE,new GlobalConfig.ActionCallback() {
                            @Override
                            public void onSuccess() {

                                toggleProgress(false);
                                createSuccessDialog();
                            }

                            @Override
                            public void onFailed(String errorMessage) {

                                toggleProgress(false);
                                GlobalConfig.createSnackBar(AddNewPasswordActivity.this, passwordDescriptionEditText, "Your credential failed to save please try again", Snackbar.LENGTH_INDEFINITE);

                            }
                        });
                    } else {
                        if (isEdition || isViewOnly || isFromLocal) {
                            updatePasswordLocal(IS_SAVED_ONLINE, new GlobalConfig.ActionCallback() {
                                @Override
                                public void onSuccess() {

                                    toggleProgress(false);
                                    createSuccessDialog();
                                }

                                @Override
                                public void onFailed(String errorMessage) {

                                    toggleProgress(false);
                                    GlobalConfig.createSnackBar(AddNewPasswordActivity.this, passwordDescriptionEditText, "Your credential failed to save please try again", Snackbar.LENGTH_INDEFINITE);

                                }
                            });
                        } else {
                            savePasswordLocal(IS_SAVED_ONLINE,new GlobalConfig.ActionCallback() {
                                @Override
                                public void onSuccess() {

                                    toggleProgress(false);
                                    createSuccessDialog();
                                }

                                @Override
                                public void onFailed(String errorMessage) {
                                    toggleProgress(false);
                                    GlobalConfig.createSnackBar(AddNewPasswordActivity.this, passwordDescriptionEditText, "Your credential failed to save please try again", Snackbar.LENGTH_INDEFINITE);

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

    void addNewItem(String itemTitle, String itemValue){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView  =  layoutInflater.inflate(R.layout.new_password_item_layout,newItemContainerLinearLayout,false);
        EditText itemTitleEditText = itemView.findViewById(R.id.itemTitleEditTextId);
        itemTitleEditText.setText(itemTitle);
        TextInputEditText itemValueTextInput = itemView.findViewById(R.id.itemValueTextInputId);
        itemValueTextInput.setText(itemValue);

        ImageView removeItemActionButton = itemView.findViewById(R.id.removeItemActionButtonId);
        removeItemActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newItemContainerLinearLayout.removeView(itemView);
            }
        });

        newItemContainerLinearLayout.addView(itemView);
    }

    String getItemTitles(){
        StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<newItemContainerLinearLayout.getChildCount(); i++){
            View itemView  =  newItemContainerLinearLayout.getChildAt(i);
            EditText itemTitleEditText = itemView.findViewById(R.id.itemTitleEditTextId);
            String title = (itemTitleEditText.getText()+"");
            stringBuilder.append(title);
            if((newItemContainerLinearLayout.getChildCount()-1)!=i){
                stringBuilder.append(",");

            }
        }
        return stringBuilder+"";
    }
    String getItemValues(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<newItemContainerLinearLayout.getChildCount(); i++){
            View itemView  =  newItemContainerLinearLayout.getChildAt(i);
            TextInputEditText itemValueTextInput = itemView.findViewById(R.id.itemValueTextInputId);
            String description = (itemValueTextInput.getText()+"");
            stringBuilder.append(description);
            if((newItemContainerLinearLayout.getChildCount()-1)!=i){
                stringBuilder.append(",");

            }
        }
        return stringBuilder+"";
    }

    void setNotEditing(){
        uploadPasswordActionButton.setVisibility(View.GONE);
        passwordDescriptionEditText.setEnabled(false);
        passwordTitleEditText.setEnabled(false);
        passwordTitleEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        setEditingActionButton.setImageResource(R.drawable.ic_baseline_edit_24);
        for(int i=0; i<newItemContainerLinearLayout.getChildCount(); i++){
            View itemView  =  newItemContainerLinearLayout.getChildAt(i);
            EditText itemTitleEditText = itemView.findViewById(R.id.itemTitleEditTextId);
            TextInputEditText itemValueTextInput = itemView.findViewById(R.id.itemValueTextInputId);
            itemTitleEditText.setEnabled(false);
            itemValueTextInput.setEnabled(false);
        }
        passwordInput.setEnabled(false);
        PINInput.setEnabled(false);
        TOKENInput.setEnabled(false);
        CODEInput.setEnabled(false);
        PHONEInput.setEnabled(false);
        EMAILInput.setEnabled(false);

        isEditing = false;
    }
    void setIsEditing(){
        uploadPasswordActionButton.setVisibility(View.VISIBLE);
        passwordDescriptionEditText.setEnabled(true);
//        passwordDescriptionEditText.requestFocus();
        passwordTitleEditText.setEnabled(true);
        passwordTitleEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        setEditingActionButton.setImageResource(R.drawable.ic_baseline_lock_24);
        for(int i=0; i<newItemContainerLinearLayout.getChildCount(); i++){
            View itemView  =  newItemContainerLinearLayout.getChildAt(i);
            EditText itemTitleEditText = itemView.findViewById(R.id.itemTitleEditTextId);
            TextInputEditText itemValueTextInput = itemView.findViewById(R.id.itemValueTextInputId);
            itemTitleEditText.setEnabled(true);
            itemValueTextInput.setEnabled(true);
        }
        passwordInput.setEnabled(true);
        PINInput.setEnabled(true);
        TOKENInput.setEnabled(true);
        CODEInput.setEnabled(true);
        PHONEInput.setEnabled(true);
        EMAILInput.setEnabled(true);

        isEditing = true;

    }

    interface PasswordFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(PasswordDataModel passwordDataModel);
    }

}