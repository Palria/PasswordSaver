package com.palria.passwordsaver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class UserProfileFragment extends Fragment {

    AlertDialog alertDialog;
//    AlertDialog createLibraryDialog;
    LinearLayout containerLinearLayout;
    TabLayout tabLayout;
    //views
    ImageView editProfileButton;
    RoundedImageView profileImageView;
    TextView currentDisplayNameView;
//    TextView currentEmailView;
//    TextView currentPhoneNumberView;
//    TextView currentWebsiteLinkView;
    TextView currentCountryOfResidence;
    TextView joined_dateTextView;

    TextView numOfPasswordTextView;
    TextView numOfNotesTextView;
//    TextView numOfRatingsTextView;

    LinearLayout numOfPasswordNotesLinearLayout;

    //parent swiper layout
    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView parentScrollView;
    BottomAppBar bottomAppBar;

    TextView   failureIndicatorTextView;
    ImageButton profileMoreIconButton;
    Button statsButton;
    String authorId = "";
    TextView logButton;
//    TextView verificationFlagTextView;
//    ImageView bookmarkedIcon, ratedIcon;
    //learn era bottom sheet dialog
    LEBottomSheetDialog leBottomSheetDialog;

//    RecyclerView passwordRcv;
//    ArrayList<PasswordDataModel> passwordArrayList = new ArrayList<>();
//    PasswordAdapter passwordRcvAdapter;
//
////    RecyclerView notesRcv;
//    ArrayList<NotesDataModel> notesDataModelArrayList=new ArrayList<>();
//    NotesAdapter notesAdapter;

//    boolean isUserAuthor = false;
//    boolean isFirstLoad = true;
    boolean isMyPasswordsFragmentOpened = false;
    boolean isMyNotesFragmentOpened = false;
    boolean isRefresh = false;

    FrameLayout myPasswordsFrameLayout;
    FrameLayout myNotesFrameLayout;
    //shimmer layout loading preloading effect container.
    ShimmerFrameLayout shimmerLayout;

    //replace all categories with categories from database
//    String[] categories = {"Software Development", "Ui Design", "Web Development", "Machine Learning",
//    "Database Design", "Furniture", "Internet", "Communication", "Story", "Drama", "Podcasts","Java","Android Dev","Python","Data Learning","OOPs Concept","Artificial Intelligence"};

    LinearLayout noPasswordFoundView ;
    LinearLayout noNotesFoundView ;
//    AlertDialog declineAlertDialog;

    public UserProfileFragment() {
        // Required empty public constructor
    }
    public UserProfileFragment(BottomAppBar b) {
        bottomAppBar = b;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View parentView = inflater.inflate(R.layout.fragment_user_profile, container, false);
            initUI(parentView);
            loadCurrentUserProfile();

//            fetchAllPasswords(new PasswordFetchListener() {
//                @Override
//                public void onFailed(String errorMessage) {
////               toggleProgress(false);
////                    swipeRefreshLayout.setRefreshing(false);
////                    Toast.makeText(getContext(), "failed to fetch library.", Toast.LENGTH_SHORT).show();
//
//                }
//
//                @Override
//                public void onSuccess(PasswordDataModel passwordDataModel) {
////               toggleProgress(false);
//                    swipeRefreshLayout.setRefreshing(false);
//                    // displayLibrary(libraryDataModel);
//                    shimmerLayout.stopShimmer();
//                    shimmerLayout.setVisibility(View.GONE);
//                    parentScrollView.setVisibility(View.VISIBLE);
//                        passwordArrayList.add(passwordDataModel);
//                        passwordRcvAdapter.notifyItemChanged(passwordArrayList.size());
//
//                }
//            });

//            fetchNotes(new NotesFetchListener() {
//                @Override
//                public void onSuccess(NotesDataModel notesDataModel) {
//
//                    swipeRefreshLayout.setRefreshing(false);
//                    shimmerLayout.stopShimmer();
//                    shimmerLayout.setVisibility(View.GONE);
//
//                    parentScrollView.setVisibility(View.VISIBLE);
//                        notesDataModelArrayList.add(notesDataModel);
//                        notesAdapter.notifyItemChanged(notesDataModelArrayList.size());
//                }
//
//                @Override
//                public void onFailed(String errorMessage) {
//                }
//            });
            initTabLayout();
            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getContext(), EditCurrentUserProfileActivity.class);
                    startActivity(i);

                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadCurrentUserProfile();
                    swipeRefreshLayout.setRefreshing(true);
                    isRefresh =true;
                     isMyPasswordsFragmentOpened = false;
                     isMyNotesFragmentOpened = false;
                    tabLayout.selectTab(tabLayout.getTabAt(0),true);
                    isMyPasswordsFragmentOpened =true;
                    setFrameLayoutVisibility(myPasswordsFrameLayout);
                    AllPasswordsFragment allPasswordsFragment = new AllPasswordsFragment();
                    initFragment(allPasswordsFragment, myPasswordsFrameLayout);
                }
            });

            parentScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                float y = 0;

                @Override
                public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (bottomAppBar != null) {
                        if (oldScrollY > scrollY) {
                            bottomAppBar.performShow();

                        } else {
                            bottomAppBar.performHide();

                        }
                    }

                    //
//             if(scrollY > 30){
//                 if(bottomAppBar!=null) {
//                     bottomAppBar.performHide();
//                 }
//             }else{
//                 if(bottomAppBar!=null) {
//                     bottomAppBar.performShow();
//                 }
//             }

                }
            });

            profileMoreIconButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    leBottomSheetDialog.show();

                }
            });


            statsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getContext(), SecurityTipsActivity.class);
                    startActivity(intent);

                }
            });
//
            logButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getContext(), UserActivityLogActivity.class);
                    startActivity(intent);

                }
            });



       return parentView;
    }


    private void loadCurrentUserProfile(){
        getProfile(new OnUserProfileFetchListener() {
            @Override
            public void onSuccess(String userDisplayName, String userCountryOfResidence, String genderType, String userProfilePhotoDownloadUrl, String joined_date,String numOfPasswordCreated,String numOfNotesCreated,boolean isUserProfilePhotoIncluded) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    Glide.with(getContext())
                            .load(userProfilePhotoDownloadUrl)
                            .centerCrop()
                            .error(R.drawable.default_profile)
                            .into(profileImageView);
                }catch(Exception e){}

                currentDisplayNameView.setText(userDisplayName);
                currentCountryOfResidence.setText(Html.fromHtml("From <b>"+userCountryOfResidence+"</b> "));
                joined_dateTextView.setText(Html.fromHtml("Joined <b>"+joined_date+"</b> "));

                numOfPasswordTextView.setText(numOfPasswordCreated);
                numOfNotesTextView.setText(numOfNotesCreated);
//                numOfRatingsTextView.setText(numOfRatings);

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);

                parentScrollView.setVisibility(View.VISIBLE);
//                Toast.makeText(getContext(), "Libraries loaded.", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailed(String errorMessage) {
//                toggleProgress(false);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Failed to retrieve profile data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI(View parentView){
            //use the parentView to find the by Id as in : parentView.findViewById(...);
            parentScrollView = parentView.findViewById(R.id.scrollView);
            failureIndicatorTextView = parentView.findViewById(R.id.failureIndicatorTextViewId);
            //init views
            editProfileButton = parentView.findViewById(R.id.editProfileIcon);
            tabLayout = parentView.findViewById(R.id.tabLayoutId);
            myPasswordsFrameLayout = parentView.findViewById(R.id.myPasswordsFrameLayoutId);
            myNotesFrameLayout = parentView.findViewById(R.id.myNotesFrameLayoutId);
            profileImageView = parentView.findViewById(R.id.imageView1);
            currentDisplayNameView = parentView.findViewById(R.id.current_name);
            currentCountryOfResidence = parentView.findViewById(R.id.current_country);
            joined_dateTextView = parentView.findViewById(R.id.joined_date);
            logButton = parentView.findViewById(R.id.logButton);
            statsButton = parentView.findViewById(R.id.statsButton);

//            noPasswordFoundView = parentView.findViewById(R.id.noPasswordFoundView);
//            noNotesFoundView = parentView.findViewById(R.id.noNotesFoundView);


            numOfPasswordNotesLinearLayout = parentView.findViewById(R.id.numOfPasswordNotesLinearLayoutId);


            numOfPasswordTextView = parentView.findViewById(R.id.numOfPasswordCreatedTextView);
            numOfNotesTextView = parentView.findViewById(R.id.numOfNotesCreatedTextView);
//            numOfRatingsTextView = parentView.findViewById(R.id.numOfRatingsCreatedTextView);

            swipeRefreshLayout = parentView.findViewById(R.id.swiperRefreshLayout);
            profileMoreIconButton = parentView.findViewById(R.id.profileMoreIcon);
            shimmerLayout = parentView.findViewById(R.id.shimmerLayout);

//            passwordRcv = parentView.findViewById(R.id.passwordRcv);
//            notesRcv = parentView.findViewById(R.id.notesRcv);

            alertDialog = new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setView(getLayoutInflater().inflate(R.layout.default_loading_layout, null))
                    .create();

            leBottomSheetDialog = new LEBottomSheetDialog(getContext());


            leBottomSheetDialog
                    .addOptionItem("New password", R.drawable.ic_baseline_lock_24, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            leBottomSheetDialog.hide();
                            Intent intent = new Intent(getContext(), AddNewPasswordActivity.class);
                            intent.putExtra(GlobalConfig.PASSWORD_ID, "");
                            intent.putExtra(GlobalConfig.IS_EDITION, false);
                            startActivity(intent);
                        }
                    }, 0)
                    .addOptionItem("New notes", R.drawable.ic_baseline_edit_24, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            leBottomSheetDialog.hide();
                            Intent intent = new Intent(getContext(), CreateNewNotesActivity.class);
                            intent.putExtra(GlobalConfig.NOTES_ID, "");
                            intent.putExtra(GlobalConfig.IS_EDITION, false);
                            startActivity(intent);
                        }
                    }, 0)
                     .addOptionItem("Sign out", R.drawable.ic_baseline_error_outline_24, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            leBottomSheetDialog.hide();
                          createConfirmSignOutDialog();
                        }
                    }, 0)
                    .render();


            //init recycler list view here
        /*
        libraryArrayList.add(new LibraryDataModel(
                "Deploying the constructor for free.",
                "lasdjf",
                null,
                "https://api.lorem.space/image/album?w=150&h=150&hash=5115",
                "",
                "",
                0l,
                0l,
                1,
                "jvjhgjyuikjkj",
                0l,
                0l,
                0l,
                0l,
                0));

        libraryArrayList.add(new LibraryDataModel(
                "Cracking the Hash with hashcat",
                "lasdjf",
                null,
                "https://api.lorem.space/image/album?w=150&h=150&hash=410115",
                "",
                "",
                0l,
                0l,
                1,
                "jvjhgjyuikjkj",
                0l,
                0l,
                0l,
                0l,
                0));

        libraryArrayList.add(new LibraryDataModel(
                "Design Principle in Short ",
                "lasdjf",
                null,
                "https://api.lorem.space/image/album?w=150&h=150&hash=12500",
                "",
                "",
                0l,
                0l,
                1,
                "jvjhgjyuikjkj",
                0l,
                0l,
                0l,
                0l,
                0));

*/

//            passwordRcvAdapter = new PasswordAdapter(getContext(),passwordArrayList);
//            passwordRcv.setHasFixedSize(false);
//            passwordRcv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//            passwordRcv.setAdapter(passwordRcvAdapter);
/*
              tutorialsArrayList.add(
                new TutorialDataModel("How to connect to mysql database for free. in 2012 for Users to get it.",
                        "category",
                        "description",
                        "__id__02151",
                        "1 days ago",
                        2l,
                        1l,
                        0l,
                        0l,
                        "Kamaensi",
                        "",
                        "https://api.lorem.space/image/movie?w=400&h=220&hash=45110",
                        0l,
                        0l,
                        0l,
                        0l,
                        0l));

        tutorialsArrayList.add(
                new TutorialDataModel("The protest was organised against the Kathmandu Metropolitan City mayor’s recent move to demolish a part of private property in Sankhamul",
                        "Category",
                        "description",
                        "Jeevan",
                        "32 mins ago",
                        2l,
                        1l,
                        0l,
                        0l,
                        "Jeevan",
                        "",
                        "https://api.lorem.space/image/movie?w=400&h=220&hash=123",
                        0l,
                        0l,
                        0l,
                        0l,
                        0l));

        tutorialsArrayList.add(
                new TutorialDataModel("According to him, the metropolis demolished the house compound without any letter or notice.",
                        "Category",
                        "description",
                        "Palria",
                        "32 mins ago",
                        2l,
                        1l,
                        0l,
                        0l,
                        "Palria",
                        "",
                        "https://api.lorem.space/image/movie?w=450&h=200&hash=334",
                        0l,
                        0l,
                        0l,
                        0l,
                        0l));

        tutorialsArrayList.add(
                new TutorialDataModel("According to him, the metropolis demolished the house compound without any letter or notice.",
                        "Category",
                        "description",
                        "Palria",
                        "32 mins ago",
                        2l,
                        1l,
                        0l,
                        0l,
                        "Palria",
                        "",
                        "https://api.lorem.space/image/movie?w=450&h=200&hash=asdfadsew",
                        0l,
                        0l,
                        0l,
                        0l,
                        0l));

        tutorialsArrayList.add(
                new TutorialDataModel("According to him, the metropolis demolished the house compound without any letter or notice.",
                        "Category",
                        "description",
                        "Palria",
                        "32 mins ago",
                        2l,
                        1l,
                        0l,
                        0l,
                        "Palria",
                        "",
                        "https://api.lorem.space/image/movie?w=450&h=200&hash=sdfsad",
                        0l,
                        0l,
                        0l,
                        0l,
                        0l));

        tutorialsArrayList.add(
                new TutorialDataModel("According to him, the metropolis demolished the house compound without any letter or notice.",
                        "Category",
                        "description",
                        "Palria",
                        "32 mins ago",
                        2l,
                        1l,
                        0l,
                        0l,
                        "Palria",
                        "",
                        "https://api.lorem.space/image/movie?w=450&h=200&hash=sdfs33423",
                        0l,
                        0l,
                        0l,
                        0l,
                        0l));
*/

//            notesAdapter = new NotesAdapter(getContext(),notesDataModelArrayList);
//            notesRcv.setHasFixedSize(false);
//            notesRcv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//            notesRcv.setAdapter(notesAdapter);


    }


    private  void getProfile(OnUserProfileFetchListener onUserProfileFetchListener){
        GlobalConfig.getFirebaseFirestoreInstance().collection(GlobalConfig.ALL_USERS).document(GlobalConfig.getCurrentUserId())
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onUserProfileFetchListener.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String userDisplayName =""+ documentSnapshot.get(GlobalConfig.USER_DISPLAY_NAME);
                        String userCountryOfResidence =""+ documentSnapshot.get(GlobalConfig.USER_COUNTRY_OF_RESIDENCE);
                        String genderType =""+ documentSnapshot.get(GlobalConfig.USER_GENDER_TYPE);
                        String userProfilePhotoDownloadUrl =""+ documentSnapshot.get(GlobalConfig.USER_PROFILE_PHOTO_DOWNLOAD_URL);
                        String joined_date = documentSnapshot.get(GlobalConfig.USER_PROFILE_DATE_CREATED_TIME_STAMP)!=null ? documentSnapshot.getTimestamp(GlobalConfig.USER_PROFILE_DATE_CREATED_TIME_STAMP).toDate()+"" :"Moment ago";
                        if(joined_date.length()>10){
                            joined_date = joined_date.substring(0,10);
                        }
                        long numOfPasswordCreated = documentSnapshot.get(GlobalConfig.TOTAL_NUMBER_OF_PASSWORD_CREATED)!=null ? documentSnapshot.getLong(GlobalConfig.TOTAL_NUMBER_OF_PASSWORD_CREATED):0L;
                        long numOfNotesCreated = documentSnapshot.get(GlobalConfig.TOTAL_NUMBER_OF_NOTES_CREATED)!=null ? documentSnapshot.getLong(GlobalConfig.TOTAL_NUMBER_OF_NOTES_CREATED):0L;
//                        long numOfRatings = documentSnapshot.get(GlobalConfig.TOTAL_NUMBER_OF_AUTHOR_REVIEWS_KEY)!=null ? documentSnapshot.getLong(GlobalConfig.TOTAL_NUMBER_OF_AUTHOR_REVIEWS_KEY):0L;


                        boolean isUserProfilePhotoIncluded = false;

                        if(documentSnapshot.get(GlobalConfig.IS_USER_PROFILE_PHOTO_INCLUDED) != null){
                            isUserProfilePhotoIncluded = documentSnapshot.getBoolean(GlobalConfig.IS_USER_PROFILE_PHOTO_INCLUDED);

                        }

                        onUserProfileFetchListener.onSuccess( userDisplayName, userCountryOfResidence, genderType, userProfilePhotoDownloadUrl,joined_date,""+ numOfPasswordCreated,""+ numOfNotesCreated, isUserProfilePhotoIncluded);
                        }
                });
    }


    private void fetchAllPasswords(PasswordFetchListener passwordFetchListener){
        GlobalConfig.getFirebaseFirestoreInstance()
                .collection(GlobalConfig.ALL_PASSWORDS)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        passwordFetchListener.onFailed(e.getMessage());
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
                            String itemTitles = ""+ documentSnapshot.get(GlobalConfig.ITEM_TITLES);
                            String itemValues = ""+ documentSnapshot.get(GlobalConfig.ITEM_VALUES);

                            passwordFetchListener.onSuccess(new PasswordDataModel(passwordId, passwordTitle, passwordDescription, dateCreated, true
                                    , password, pin, token, code, phone, email, itemTitles, itemValues));

                        }

                        if(queryDocumentSnapshots.size()==0){
                            //show not found

                            noPasswordFoundView.setVisibility(View.VISIBLE);

                        }else{
                            noPasswordFoundView.setVisibility(View.GONE);
                        }

                    }
                });

    }

    private void fetchNotes(NotesFetchListener notesFetchListener){
        Query tutorialQuery = null;

            tutorialQuery =   GlobalConfig.getFirebaseFirestoreInstance().collection(GlobalConfig.ALL_NOTES);
        tutorialQuery.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        notesFetchListener.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            String notesId =""+ documentSnapshot.get(GlobalConfig.NOTES_ID);
                            String tutorialName = ""+ documentSnapshot.get(GlobalConfig.NOTES_TITLE);
                            String tutorialDescription = ""+ documentSnapshot.get(GlobalConfig.NOTES_BODY);
                            String dateCreated =  documentSnapshot.get(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP)!=null &&  documentSnapshot.get(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP) instanceof Timestamp ?  documentSnapshot.getTimestamp(GlobalConfig.NOTES_DATE_CREATED_TIME_STAMP).toDate().toString() : "Moment ago" ;
                            if(dateCreated.length()>10){
                                dateCreated = dateCreated.substring(0,10);
                            }


                            notesFetchListener.onSuccess(new NotesDataModel(
                                    notesId,
                                    tutorialName,
                                    tutorialDescription,
                                    dateCreated,
                                    true

                            ));


                        }

                        if(queryDocumentSnapshots.size()==0){
                            //show not found message

                            noNotesFoundView.setVisibility(View.VISIBLE);

                        }else{
                            noNotesFoundView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void createConfirmSignOutDialog(){

        AlertDialog confirmExitDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Sign out");
        builder.setMessage("Click Sign-out button to sign out of your account");
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_baseline_error_outline_24);
        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                getActivity().onBackPressed();
            }
        })
                .setNegativeButton("Stay back", null);
        confirmExitDialog = builder.create();
        confirmExitDialog.show();

    }

    private void  initTabLayout(){
//        Toast.makeText(getContext(), "tabs done", Toast.LENGTH_SHORT).show();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition()==0){
//                    Toast.makeText(getContext(), "passwords ", Toast.LENGTH_SHORT).show();

                    if(isMyPasswordsFragmentOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(myPasswordsFrameLayout);
                    }
                    else if(!isRefresh){
                        isMyPasswordsFragmentOpened =true;
                        setFrameLayoutVisibility(myPasswordsFrameLayout);
                        AllPasswordsFragment allPasswordsFragment = new AllPasswordsFragment();
                        initFragment(allPasswordsFragment, myPasswordsFrameLayout);
                    }
                    isRefresh =false;

                }
                if(tab.getPosition()==1){
//                    Toast.makeText(getContext(), "notes ", Toast.LENGTH_SHORT).show();

                    if(isMyNotesFragmentOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(myNotesFrameLayout);
                    }else {
                        isMyNotesFragmentOpened =true;
                        setFrameLayoutVisibility(myNotesFrameLayout);
                        AllNotesFragment allNotesFragment = new AllNotesFragment();
                        initFragment(allNotesFragment, myNotesFrameLayout);
                    }

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab passwordsItem = tabLayout.newTab();
        passwordsItem.setText("My Passwords");
        tabLayout.addTab(passwordsItem,0);

        TabLayout.Tab notesItem = tabLayout.newTab();
        notesItem.setText("My Notes");
        tabLayout.addTab(notesItem,1);


    }

    private void initFragment(Fragment fragment, ViewGroup frameLayout){
        getChildFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(), fragment)
                .commit();


    }

    private void setFrameLayoutVisibility(ViewGroup frameLayoutToSetVisible){
        myPasswordsFrameLayout.setVisibility(View.GONE);
        myNotesFrameLayout.setVisibility(View.GONE);
        frameLayoutToSetVisible.setVisibility(View.VISIBLE);
    }

    private void toggleProgress(boolean show)
    {
        if(show){
            alertDialog.show();
        }else{
            alertDialog.cancel();
        }
    }


    interface PasswordFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(PasswordDataModel passwordDataModel);
    }
    interface NotesFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(NotesDataModel notesDataModel);
    }

    interface OnUserProfileFetchListener{
        void onSuccess(String userDisplayName,String userCountryOfResidence,String genderType,String userProfilePhotoDownloadUrl,String joined_date,String numOfPasswordCreated,String numOfNotesCreated,boolean isUserProfilePhotoIncluded);
        void onFailed(String errorMessage);
    }
}

