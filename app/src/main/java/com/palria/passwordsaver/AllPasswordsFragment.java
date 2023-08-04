package com.palria.passwordsaver;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllPasswordsFragment extends Fragment {
    RecyclerView localPasswordsRecyclerView;
    RecyclerView onlinePasswordsRecyclerView;

    PasswordAdapter localPasswordsAdapter;
    ArrayList<PasswordDataModel> localPasswordsDataModelArrayList = new ArrayList<>();

    PasswordAdapter onlinePasswordsAdapter;
    ArrayList<PasswordDataModel> onlinePasswordsDataModelArrayList = new ArrayList<>();
    LinearLayout noOnlinePasswordsFoundView;
    PasswordsSQLiteDatabaseHelper passwordsSQLiteDatabaseHelper;

    TabLayout tabLayout;

public AllPasswordsFragment(){

}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordsSQLiteDatabaseHelper = new PasswordsSQLiteDatabaseHelper(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_all_passwords, container, false);

        initUI(parentView);
//        Toast.makeText(getContext(), "Passwords welcome.", Toast.LENGTH_SHORT).show();
        initTabLayout();
        initOnlineRecyclerView();
        initLocalRecyclerView();
        getOnlinePasswords();
        getLocalPasswords();
        return parentView;
    }

    void initUI(View parentView){
        localPasswordsRecyclerView = parentView.findViewById(R.id.localPasswordsRecyclerViewId);
        onlinePasswordsRecyclerView = parentView.findViewById(R.id.onlinePasswordsRecyclerViewId);
        noOnlinePasswordsFoundView = parentView.findViewById(R.id.noPasswordFoundView);
        tabLayout = parentView.findViewById(R.id.tabLayoutId);


    }

    void initLocalRecyclerView(){
        localPasswordsAdapter = new PasswordAdapter(getContext(),localPasswordsDataModelArrayList,true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        localPasswordsRecyclerView.setLayoutManager(linearLayoutManager);
        localPasswordsRecyclerView.setAdapter(localPasswordsAdapter);
    }

    void initOnlineRecyclerView(){
        onlinePasswordsAdapter = new PasswordAdapter(getContext(),onlinePasswordsDataModelArrayList,false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        onlinePasswordsRecyclerView.setLayoutManager(linearLayoutManager);
        onlinePasswordsRecyclerView.setAdapter(onlinePasswordsAdapter);
    }

    private void getOnlinePasswords(){
        GlobalConfig.getFirebaseFirestoreInstance()
                .collection(GlobalConfig.ALL_PASSWORDS)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        passwordFetchListener.onFailed(e.getMessage());
                        Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
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
                            String itemTitles = documentSnapshot.get(GlobalConfig.ITEM_TITLES)!=null?""+ documentSnapshot.get(GlobalConfig.ITEM_TITLES):"";
                            String itemValues = documentSnapshot.get(GlobalConfig.ITEM_VALUES)!=null?""+ documentSnapshot.get(GlobalConfig.ITEM_VALUES):"";

                            onlinePasswordsDataModelArrayList.add(new PasswordDataModel(passwordId, passwordTitle, passwordDescription, dateCreated, true
                                    , password, pin, token, code, phone, email, itemTitles, itemValues));
                            onlinePasswordsAdapter.notifyItemChanged(onlinePasswordsDataModelArrayList.size());

                        }

                        if(queryDocumentSnapshots.size()==0){
                            //show not found

                            noOnlinePasswordsFoundView.setVisibility(View.VISIBLE);

                        }else{
                            noOnlinePasswordsFoundView.setVisibility(View.GONE);
                        }

                    }
                });

    }

    void getLocalPasswords(){
        Cursor cursor = passwordsSQLiteDatabaseHelper.getData();
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String passwordId = cursor.getString(0);
                String passwordTitle = ""+ cursor.getString(1);
                String passwordDescription = ""+cursor.getString(2);
                String dateCreated =  cursor.getString(3);
                boolean isUploadedOnline =  (cursor.getString(4)+"").equals(GlobalConfig.TRUE);
                String password = ""+cursor.getString(6);
                String pin = ""+ cursor.getString(7);
                String token = ""+ cursor.getString(8);
                String code = ""+ cursor.getString(9);
                String phone = ""+ cursor.getString(10);
                String email = ""+ cursor.getString(11);
                String itemTitles = ""+ cursor.getString(12);
                String itemValues = ""+ cursor.getString(13);

                localPasswordsDataModelArrayList.add(new PasswordDataModel(passwordId, passwordTitle, passwordDescription, dateCreated, isUploadedOnline
                        , password, pin, token, code, phone, email, itemTitles, itemValues));
                localPasswordsAdapter.notifyItemChanged(onlinePasswordsDataModelArrayList.size());

            }
            if(cursor.getCount()<=0){
                noOnlinePasswordsFoundView.setVisibility(View.VISIBLE);
//                    Toast.makeText(getContext(), "Passwords empty.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void  initTabLayout(){
//        Toast.makeText(getContext(), "tabs done", Toast.LENGTH_SHORT).show();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition()==0){
//                    Toast.makeText(getContext(), "passwords ", Toast.LENGTH_SHORT).show();

                    //Just set the frame layout visibility
                    setLayoutVisibility(localPasswordsRecyclerView);
                    noOnlinePasswordsFoundView.setVisibility(View.INVISIBLE);

                }
                if(tab.getPosition()==1){
//                    Toast.makeText(getContext(), "Passwords ", Toast.LENGTH_SHORT).show();
                    //Just set the frame layout visibility
                    setLayoutVisibility(onlinePasswordsRecyclerView);
                    noOnlinePasswordsFoundView.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab onlineNoteItem = tabLayout.newTab();
        onlineNoteItem.setText("Local");
        tabLayout.addTab(onlineNoteItem,0);

        TabLayout.Tab localPasswordsItem = tabLayout.newTab();
        localPasswordsItem.setText("Online");
        tabLayout.addTab(localPasswordsItem,1);


    }
    private void setLayoutVisibility(View layoutToSetVisible){
        onlinePasswordsRecyclerView.setVisibility(View.GONE);
        localPasswordsRecyclerView.setVisibility(View.GONE);
        layoutToSetVisible.setVisibility(View.VISIBLE);
    }

}