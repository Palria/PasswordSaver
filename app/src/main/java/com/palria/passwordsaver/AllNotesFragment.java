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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllNotesFragment extends Fragment {
RecyclerView localNotesRecyclerView;
RecyclerView onlineNotesRecyclerView;

NotesAdapter localNotesAdapter;
ArrayList<NotesDataModel> localNotesDataModelArrayList = new ArrayList<>();

NotesAdapter onlineNotesAdapter;
ArrayList<NotesDataModel> onlineNotesDataModelArrayList = new ArrayList<>();
LinearLayout noOnlineNotesFoundView;
    NotesSQLiteDatabaseHelper notesSQLiteDatabaseHelper;

    TabLayout tabLayout;

    public AllNotesFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notesSQLiteDatabaseHelper = new NotesSQLiteDatabaseHelper(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_all_notes, container, false);

        initUI(parentView);
//        Toast.makeText(getContext(), "notes welcome.", Toast.LENGTH_SHORT).show();
            initTabLayout();
        initOnlineRecyclerView();
        initLocalRecyclerView();
        getOnlineNotes();
        getLocalNotes();
        return parentView;
    }

void initUI(View parentView){
    localNotesRecyclerView = parentView.findViewById(R.id.localNotesRecyclerViewId);
    onlineNotesRecyclerView = parentView.findViewById(R.id.onlineNotesRecyclerViewId);
    noOnlineNotesFoundView = parentView.findViewById(R.id.noNotesFoundView);
    tabLayout = parentView.findViewById(R.id.tabLayoutId);


}

void initLocalRecyclerView(){
    localNotesAdapter = new NotesAdapter(getContext(),localNotesDataModelArrayList,true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
    localNotesRecyclerView.setLayoutManager(linearLayoutManager);
    localNotesRecyclerView.setAdapter(localNotesAdapter);
}

void initOnlineRecyclerView(){
    onlineNotesAdapter = new NotesAdapter(getContext(),onlineNotesDataModelArrayList,false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
    onlineNotesRecyclerView.setLayoutManager(linearLayoutManager);
    onlineNotesRecyclerView.setAdapter(onlineNotesAdapter);
}

    void getOnlineNotes() {
        GlobalConfig.getFirebaseFirestoreInstance()
                .collection(GlobalConfig.ALL_NOTES)
                .orderBy(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP, Query.Direction.DESCENDING)
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                Toast.makeText(getContext(), "notes done.", Toast.LENGTH_SHORT).show();

                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//                                    Toast.makeText(getContext(), "notes loaded.", Toast.LENGTH_SHORT).show();

                    String notesId = documentSnapshot.getId();
                    String notesTitle = ""+ documentSnapshot.get(GlobalConfig.NOTES_TITLE);
                    String notesBody = ""+ documentSnapshot.get(GlobalConfig.NOTES_BODY);
                    String dateAdded =  documentSnapshot.get(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP)!=null? documentSnapshot.getTimestamp(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP).toDate()+"": "Moment ago";
                    if(dateAdded.length()>10){
                        dateAdded = dateAdded.substring(0,10);
                    }
                    onlineNotesDataModelArrayList.add(new NotesDataModel(notesId, notesTitle, notesBody,dateAdded,true));
                    onlineNotesAdapter.notifyItemChanged(onlineNotesDataModelArrayList.size());

                }
                if(queryDocumentSnapshots.isEmpty()){
                    noOnlineNotesFoundView.setVisibility(View.VISIBLE);
//                    Toast.makeText(getContext(), "notes empty.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    void getLocalNotes(){
        Cursor cursor = notesSQLiteDatabaseHelper.getNotes();
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String notesId = cursor.getString(0);
                String notesTitle = ""+ cursor.getString(1);
                String notesBody = ""+cursor.getString(2);
                String dateAdded =  cursor.getString(3);
                boolean isUploadedOnline =  (cursor.getString(4)+"").equals(GlobalConfig.TRUE);
                localNotesDataModelArrayList.add(new NotesDataModel(notesId, notesTitle, notesBody,dateAdded,isUploadedOnline));
               localNotesAdapter.notifyItemChanged(onlineNotesDataModelArrayList.size());

            }
        if(cursor.getCount()<=0){
            noOnlineNotesFoundView.setVisibility(View.VISIBLE);
//                    Toast.makeText(getContext(), "notes empty.", Toast.LENGTH_SHORT).show();

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
                        setLayoutVisibility(localNotesRecyclerView);
                    noOnlineNotesFoundView.setVisibility(View.INVISIBLE);

                }
                if(tab.getPosition()==1){
//                    Toast.makeText(getContext(), "notes ", Toast.LENGTH_SHORT).show();
                        //Just set the frame layout visibility
                        setLayoutVisibility(onlineNotesRecyclerView);
                    noOnlineNotesFoundView.setVisibility(View.INVISIBLE);

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

        TabLayout.Tab localNotesItem = tabLayout.newTab();
        localNotesItem.setText("Online");
        tabLayout.addTab(localNotesItem,1);


    }
    private void setLayoutVisibility(View layoutToSetVisible){
        onlineNotesRecyclerView.setVisibility(View.GONE);
        localNotesRecyclerView.setVisibility(View.GONE);
        layoutToSetVisible.setVisibility(View.VISIBLE);
    }

}