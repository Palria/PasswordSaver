package com.palria.passwordsaver;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class GlobalConfig {

    private static String CURRENT_USER_ID;
    private static String CURRENT_USER_TOKEN_ID = "EMPTY";
    private static String IS_NIGHT_MODE = "IS_NIGHT_MODE";
    private static String IS_KEEP_ME_LOGGED_IN = "IS_KEEP_ME_LOGGED_IN";


    public static String TRUE = "TRUE";
    public static String FALSE = "FALSE";



    public static String ALL_USERS = "ALL_USERS";
    public static String USER_ID = "USER_ID";
    public static String USER_TOKEN_ID = "USER_TOKEN_ID";
    public static String USER_PROFILE_DATE_EDITED_TIME_STAMP = "USER_PROFILE_DATE_EDITED_TIME_STAMP";
    public static String USER_PROFILE_PHOTO = "USER_PROFILE_PHOTO";
    public static String USER_IMAGES = "USER_IMAGES";
    public static String USER_DISPLAY_NAME = "USER_DISPLAY_NAME";
    public static String USER_COUNTRY_OF_RESIDENCE = "USER_COUNTRY_OF_RESIDENCE";
    public static String USER_GENDER_TYPE = "USER_GENDER_TYPE";
    public static String USER_PROFILE_PHOTO_DOWNLOAD_URL = "USER_PROFILE_PHOTO_DOWNLOAD_URL";
    public static String USER_PROFILE_DATE_CREATED_TIME_STAMP = "USER_PROFILE_DATE_CREATED_TIME_STAMP";
    public static String TOTAL_NUMBER_OF_PASSWORD_CREATED = "TOTAL_NUMBER_OF_PASSWORD_CREATED";
    public static String TOTAL_NUMBER_OF_NOTES_CREATED = "TOTAL_NUMBER_OF_NOTES_CREATED";
    public static String IS_USER_PROFILE_PHOTO_INCLUDED = "IS_USER_PROFILE_PHOTO_INCLUDED";
//

    public static String PASSWORD_ID = "PASSWORD_ID";
    public static String PASSWORD_TITLE = "PASSWORD_TITLE";
    public static String PASSWORD_DESCRIPTION = "PASSWORD_DESCRIPTION";
    public static String PASSWORD_DATE_CREATED_TIME_STAMP = "PASSWORD_DATE_CREATED_TIME_STAMP";
    public static String PASSWORD_DATE_EDITED_TIME_STAMP = "PASSWORD_DATE_EDITED_TIME_STAMP";
    public static String ALL_PASSWORDS = "ALL_PASSWORDS";
    public static String PASSWORD_DATA_MODEL = "PASSWORD_DATA_MODEL";

    public static String PASSWORD_CREDENTIAL = "PASSWORD_CREDENTIAL";
    public static String PIN_CREDENTIAL = "PIN_CREDENTIAL";
    public static String TOKEN_CREDENTIAL = "TOKEN_CREDENTIAL";
    public static String CODE_CREDENTIAL = "CODE_CREDENTIAL";
    public static String PHONE_CREDENTIAL = "PHONE_CREDENTIAL";
    public static String EMAIL_CREDENTIAL = "EMAIL_CREDENTIAL";
    public static String ITEM_TITLES = "ITEM_TITLES";
    public static String ITEM_VALUES = "ITEM_VALUES";


    public static String IS_SAVED_ONLINE = "IS_SAVED_ONLINE";
    public static String IS_VIEW_ONLY = "IS_VIEW_ONLY";
    public static String IS_EDITION = "IS_EDITION";
    public static String IS_FROM_LOCAL = "IS_FROM_LOCAL";

    public static String NOTES_ID = "NOTES_ID";
    public static String ALL_NOTES = "ALL_NOTES";
    public static String NOTES_TITLE = "NOTES_TITLE";
    public static String NOTES_BODY = "NOTES_BODY";
    public static String NOTES_DATA_MODEL = "NOTES_DATA_MODEL";
    public static String NOTES_DATE_CREATED_TIME_STAMP = "NOTES_DATE_CREATED_TIME_STAMP";
    public static String NOTES_DATE_EDITED_TIME_STAMP = "NOTES_DATE_EDITED_TIME_STAMP";


    public static final String LOG_TIME_STAMP = "LOG_TIME_STAMP";
    public static  final String ACTIVITY_LOG_TYPE = "ACTIVITY_LOG_TYPE";
    public static  final String USER_ACTIVITY_LOG = "USER_ACTIVITY_LOG";
    public static  final String ACTIVITY_LOG_USER_SIGN_UP_TYPE = "ACTIVITY_LOG_USER_SIGN_UP_TYPE";
    public static  final String ACTIVITY_LOG_USER_SIGN_IN_TYPE = "ACTIVITY_LOG_USER_SIGN_IN_TYPE";
    public static final  String ACTIVITY_LOG_USER_EDIT_ACCOUNT_TYPE = "ACTIVITY_LOG_USER_EDIT_ACCOUNT_TYPE";
    public static final  String ACTIVITY_LOG_USER_VIEWED_PASSWORD_TYPE = "ACTIVITY_LOG_USER_VIEWED_PASSWORD_TYPE";
    public static  final String ACTIVITY_LOG_USER_ADD_NEW_PASSWORD_TYPE = "ACTIVITY_LOG_USER_ADD_NEW_PASSWORD_TYPE";
    public static  final String ACTIVITY_LOG_USER_DELETE_PASSWORD_TYPE = "ACTIVITY_LOG_USER_DELETE_PASSWORD_TYPE";
    public static  final String ACTIVITY_LOG_USER_EDIT_PASSWORD_TYPE = "ACTIVITY_LOG_USER_EDIT_PASSWORD_TYPE";
    public static final  String ACTIVITY_LOG_USER_CREATE_NEW_NOTES_TYPE = "ACTIVITY_LOG_USER_CREATE_NEW_NOTES_TYPE";
    public static  final String ACTIVITY_LOG_USER_VIEWED_NOTES_TYPE = "ACTIVITY_LOG_USER_VIEWED_NOTES_TYPE";
    public static final  String ACTIVITY_LOG_USER_DELETE_NOTES_TYPE = "ACTIVITY_LOG_USER_DELETE_NOTES_TYPE";
    public static final  String ACTIVITY_LOG_USER_EDIT_NOTES_TYPE = "ACTIVITY_LOG_USER_EDIT_NOTES_TYPE";



    private static FirebaseFirestore firebaseFirestoreInstance;
    private static FirebaseStorage firebaseStorageInstance;
    private static boolean isNightMode = false;
    /**
     * <p>This method performs a check to see whether a user is logged in or not</p>
     * @return {@link boolean} denoting if a user is logged in or not
     * */
    static boolean isUserLoggedIn(){

        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * A callback method that tells when a user has either succeeded or failed in signing in to the platform
     * <p>{@link SignInListener#onSuccess(String, String)} - this method is triggered when a user signs in successfully  </p>
     * <p>{@link SignInListener#onFailed(String)} - this method is triggered when a user signs in process fails  </p>
     * <p>{@link SignInListener#onEmptyInput(boolean, boolean)} - this method is triggered when a user has an empty input  </p>
     * */
    interface SignInListener{
        void onSuccess(String email, String password);
        void onFailed(String errorMessage);
        void onEmptyInput(boolean isEmailEmpty, boolean isPasswordEmpty);
    }

    /**
     * A callback method that tells when a user has either succeeded or failed in signing in to the platform
     * <p>{@link SignUpListener#onSuccess(String, String)} - this method is triggered when a user signs in successfully  </p>
     * <p>{@link SignUpListener#onFailed(String)} - this method is triggered when a user signs in process fails  </p>
     * <p>{@link SignUpListener#onEmptyInput(boolean, boolean)} - this method is triggered when a user has an empty input  </p>
     * */
    interface SignUpListener{
        void onSuccess(String email, String password);
        void onFailed(String errorMessage);
        void onEmptyInput(boolean isEmailEmpty, boolean isPasswordEmpty);
    }

    /**
     * Signs a user in to the platform
     * @param email the email of the user
     * @param password the password of the user
     * @param signInListener a callback that tells when the sign in fails or succeeds
     * */
    static void signInUserWithEmailAndPassword(Context context,@NonNull String email, @NonNull String password,SignInListener signInListener){
        if(email != null && password != null){
            if(!email.isEmpty() && !password.isEmpty()){
                String  trimmedEmail = email.trim();
                String trimmedPassword = password.trim();
                FirebaseAuth firebaseAuth =    FirebaseAuth.getInstance();

                firebaseAuth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                signInListener.onFailed(e.getMessage());
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                signInListener.onSuccess(trimmedEmail, trimmedPassword);
                            }
                        });
            }else{
                signInListener.onEmptyInput(email.isEmpty(),password.isEmpty());
            }
        }else{
            signInListener.onEmptyInput(email == null,password == null);

        }
    }

    /**
     * Creates a user account
     * @param email the email of the user
     * @param password the password of the user
     * @param signUpListener a callback that tells when the sign un fails or succeeds
     * */
    static void signUpUserWithEmailAndPassword(Context context,@NonNull String email,@NonNull  String password,SignUpListener signUpListener){
        if(email != null && password != null){
            if(!email.isEmpty() && !password.isEmpty()){
                String  trimmedEmail = email.trim();
                String trimmedPassword = password.trim();
                FirebaseAuth firebaseAuth =    FirebaseAuth.getInstance();

                firebaseAuth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                signUpListener.onFailed(e.getMessage());
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                signUpListener.onSuccess(trimmedEmail, trimmedPassword);
                            }
                        });
            }else{
                signUpListener.onEmptyInput(email.isEmpty(),password.isEmpty());
            }
        }else{
            signUpListener.onEmptyInput(email == null,password == null);

        }
    }


    /**
     * Checks if a user's device us connected to the internet or not
     * @return  {@link boolean}
     * */
    @SuppressLint("MissingPermission")
    static boolean isConnectedOnline(Context context) {



        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if( cm.getActiveNetworkInfo() != null){
            return cm.getActiveNetworkInfo().isConnected();

        }
        return false;
    }

    static boolean isKeepMeLoggedIn(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        return sharedPreferences.getBoolean(GlobalConfig.IS_KEEP_ME_LOGGED_IN,true);
    }
    static void keepMeLoggedIn(Context context,boolean isKeepMeLoggedIn){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(GlobalConfig.IS_KEEP_ME_LOGGED_IN,isKeepMeLoggedIn);
        editor.apply();

    }


    /**This checks if the user is Learn Era Platform or not*/
    public static boolean isLearnEraAccount(){
        if(getCurrentUserId().equals("vnC7yVCJw1X6rp7bik7BSJHk6xC3")) {
            return true;
        }
        return false;
    }

    /**
     * This method sets the current user's ID
     * @param currentUserId  This parameter will be initialized from the {@link FirebaseAuth#getUid()}
     *
     *<p>It has to be first invoked from the {@link SignInActivity} context</p>
     * */
    static void setCurrentUserId(@NonNull String currentUserId){
        GlobalConfig.CURRENT_USER_ID = currentUserId;
    }
    /**
     * This returns the unique ID of the current user
     *
     * @return {@link String} The unique ID of the current user
     * */
    public static String getCurrentUserId(){
        if(GlobalConfig.CURRENT_USER_ID == null && FirebaseAuth.getInstance().getCurrentUser() != null){
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return GlobalConfig.CURRENT_USER_ID+"";
    }

    /**
     * sets the app to night mode */
    static void setIsNightMode(Context context, boolean isNightMode){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(),MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(GlobalConfig.IS_NIGHT_MODE,isNightMode);
        editor.apply();
        GlobalConfig.isNightMode = isNightMode;
    }
    /**
     *checks if app is in night mode*/
    public static boolean isNightMode(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(),MODE_PRIVATE);

        return sharedPreferences.getBoolean(GlobalConfig.IS_NIGHT_MODE,false);
    }

    /**
     * This method sets the token of the current user
     * <p>This token is initialized from {@link com.google.firebase.auth.FirebaseUser#getIdToken(boolean)}</p>
     * This token is used for notification purpose
     * */
    static void setCurrentUserTokenId(@NonNull String currentUserTokenId){
        GlobalConfig.CURRENT_USER_TOKEN_ID = currentUserTokenId;
    }

    /**
     * This returns the token of the current user
     * @return {@link String} which will be used for some operations
     * */
    static String getCurrentUserTokenId(){

        return GlobalConfig.CURRENT_USER_TOKEN_ID;
    }

    /**
     * Sets the {@link FirebaseFirestore } instance
     * <p>This instance is initialized from {@link FirebaseFirestore#getInstance()}</p>
     * */
    static void setFirebaseFirestoreInstance(){
        GlobalConfig.firebaseFirestoreInstance = FirebaseFirestore.getInstance();
    }
    /**
     * Returns the global instance of the {@link FirebaseFirestore} which will be
     * used to perform actions in {@link FirebaseFirestore} database
     * @return {@link FirebaseFirestore}
     * */
    public static FirebaseFirestore getFirebaseFirestoreInstance(){
        if(GlobalConfig.firebaseFirestoreInstance == null){
            return FirebaseFirestore.getInstance();
        }
        return GlobalConfig.firebaseFirestoreInstance;
    }

    /**
     * Sets the {@link FirebaseStorage } instance
     * <p>This instance is initialized from {@link FirebaseStorage#getInstance()}</p>
     * */
    static void setFirebaseStorageInstance(){
        GlobalConfig.firebaseStorageInstance = FirebaseStorage.getInstance();
    }


    /**
     * Returns the global instance of the {@link FirebaseFirestore} which will be used to perform actions in {@link FirebaseStorage} database
     * @return {@link FirebaseStorage}
     * */
    static FirebaseStorage getFirebaseStorageInstance(){
        if(GlobalConfig.firebaseStorageInstance == null){
            return FirebaseStorage.getInstance();
        }
        return GlobalConfig.firebaseStorageInstance;
    }

    /**
     * Returns a random {@link String} as long as the length specified
     * The purpose of the method is usually for unique ID generation
     * It is recommended that the length be long
     * @param length the length of the {@link String} that will be returned
     * @return {@link String} the string value
     * */
    public static String getRandomString(int length) {
        String[] characterArray = new String[]{
                "a", "A", "1",
                "b", "B", "2",
                "c", "C", "3",
                "d", "D", "4",
                "e", "E", "5",
                "f", "F", "6",
                "g", "G", "7",
                "h", "H", "8",
                "i", "I", "9",
                "j", "J", "0",
                "k", "K", "1",
                "l", "L", "2",
                "m", "M", "3",
                "n", "N", "4",
                "o", "O", "5",
                "p", "P", "6",
                "q", "Q", "7",
                "r", "R", "8",
                "s", "S", "9",
                "t", "T", "0",
                "u", "U", "1",
                "v", "V", "2",
                "w", "W", "3",
                "x", "X", "4",
                "y", "Y", "5",
                "z", "Z", "6"
        };

        StringBuilder randomString = new StringBuilder("AUNS");

        for(int i=0; i<length; i++){

            int randomPosition = new Random().nextInt(characterArray.length -1);

            randomString.append(Array.get(characterArray, randomPosition));
        }
        return randomString + "CJDM";
    }

    public static  String getDate(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd', 'HH:mm:ss", Locale.US);
        Date date = new Date();
        String formattedDate = simpleDateFormat.format(date);


        return formattedDate;
    }

    /**
     * This method returns the name of countries across the globe
     * @return  {@link ArrayList} the list that contains the name of countries
     * @param countryArrayList the arrayList that will hold the list of the countries, this is the same {@link Object} that will be returne
     *
     * */
    public static ArrayList<String> getCountryArrayList(@Nullable ArrayList<String> countryArrayList){
        ArrayList<String> arrayList = null;
        if(countryArrayList == null){
            arrayList = new ArrayList<>();

        }else{
            arrayList = countryArrayList;

        }

        arrayList.add("Albania");
        arrayList.add("Algeria");
        arrayList.add("American Samoa");
        arrayList.add("Andorra");
        arrayList.add("Angola");
        arrayList.add("Anguilla");
        arrayList.add("Antarctica");
        arrayList.add("Antigua and Barbuda");
        arrayList.add("Argentina");
        arrayList.add("Armenia");
        arrayList.add("Aruba");
        arrayList.add("Australia");
        arrayList.add("Austria");
        arrayList.add("Azerbaijan");
        arrayList.add("Bahamas");
        arrayList.add("Bahrain");
        arrayList.add("Bangladesh");
        arrayList.add("Barbados");
        arrayList.add("Belgium");
        arrayList.add("Belize");
        arrayList.add("Benin");
        arrayList.add("Bermuda");
        arrayList.add("Bhutan");
        arrayList.add("Bolivia");
        arrayList.add("Bonaire, Sint Eustatius and Saba");
        arrayList.add("Bosnia and Herzegovina");
        arrayList.add("Botswana");
        arrayList.add("Bouvet Island");
        arrayList.add("Brazil");
        arrayList.add("British Indian Ocean Territory");
        arrayList.add("British Virgin Islands");
        arrayList.add("Brunei Darussalam");
        arrayList.add("Bulgaria");
        arrayList.add("Burkina Faso");
        arrayList.add("Burundi");
        arrayList.add("Cambodia");
        arrayList.add("Cameroon");
        arrayList.add("Canada");
        arrayList.add("Cape Verde");
        arrayList.add("Cayman Islands");
        arrayList.add("Central African Republic");
        arrayList.add("Chad");
        arrayList.add("Chile");
        arrayList.add("China");
        arrayList.add("Christmas Island");
        arrayList.add("Cocos (Keeling) Islands");
        arrayList.add("Colombia");
        arrayList.add("Comoros");
        arrayList.add("Congo");
        arrayList.add("Cook Islands");
        arrayList.add("Costa Rica");
        arrayList.add("Cote d'Ivoire");
        arrayList.add("Croatia");
        arrayList.add("Curacao");
        arrayList.add("Cyprus");
        arrayList.add("Czech Republic");
        arrayList.add("Denmark");
        arrayList.add("Djibouti");
        arrayList.add("Dominica");
        arrayList.add("Dominican Republic");
        arrayList.add("Ecuador");
        arrayList.add("Egypt");
        arrayList.add("El Salvador");
        arrayList.add("Equatorial Guinea");
        arrayList.add("Eritrea");
        arrayList.add("Estonia");
        arrayList.add("Ethiopia");
        arrayList.add("Falkland Islands");
        arrayList.add("Faroe Islands");
        arrayList.add("Fiji");
        arrayList.add("Finland");
        arrayList.add("France");
        arrayList.add("French Guiana");
        arrayList.add("French Polynesia");
        arrayList.add("French Southern and Antarctic Lands");
        arrayList.add("Gabon");
        arrayList.add("Gambia");
        arrayList.add("Georgia");
        arrayList.add("Germany");
        arrayList.add("Ghana");
        arrayList.add("Gibraltar");
        arrayList.add("Greece");
        arrayList.add("Greenland");
        arrayList.add("Grenada");
        arrayList.add("Guadeloupe");
        arrayList.add("Guam");
        arrayList.add("Guatemala");
        arrayList.add("Guernsey");
        arrayList.add("Guinea");
        arrayList.add("Guinea-Bissau");
        arrayList.add("Guyana");
        arrayList.add("Haiti");
        arrayList.add("Heard Island and McDonald Islands");
        arrayList.add("Holy See");
        arrayList.add("Honduras");
        arrayList.add("Hong Kong");
        arrayList.add("Hungary");
        arrayList.add("Iceland");
        arrayList.add("Indonesia");
        arrayList.add("Ireland");
        arrayList.add("Isle of Man");
        arrayList.add("Israel");
        arrayList.add("Italy");
        arrayList.add("Jamaica");
        arrayList.add("Japan");
        arrayList.add("Jordan");
        arrayList.add("Kazakhstan");
        arrayList.add("Kenya");
        arrayList.add("Kiribati");
        arrayList.add("Kuwait");
        arrayList.add("Kyrgyzstan");
        arrayList.add("Laos");
        arrayList.add("Latvia");
        arrayList.add("Lebanon");
        arrayList.add("Lesotho");
        arrayList.add("Liechtenstein");
        arrayList.add("Lithuania");
        arrayList.add("Luxembourg");
        arrayList.add("Macao");
        arrayList.add("Macedonia");
        arrayList.add("Madagascar");
        arrayList.add("Malawi");
        arrayList.add("Malaysia");
        arrayList.add("Maldives");
        arrayList.add("Mali");
        arrayList.add("Malta");
        arrayList.add("Marshall Islands");
        arrayList.add("Martinique");
        arrayList.add("Mauritania");
        arrayList.add("Mauritius");
        arrayList.add("Mayotte");
        arrayList.add("Moldova");
        arrayList.add("Monaco");
        arrayList.add("Mongolia");
        arrayList.add("Montenegro");
        arrayList.add("Montserrat");
        arrayList.add("Morocco");
        arrayList.add("Mozambique");
        arrayList.add("Myanmar");
        arrayList.add("Namibia");
        arrayList.add("Nauru");
        arrayList.add("Nepal");
        arrayList.add("Netherlands");
        arrayList.add("Netherlands Antilles");
        arrayList.add("New Caledonia");
        arrayList.add("New Zealand");
        arrayList.add("Nicaragua");
        arrayList.add("Niger");
        arrayList.add("Nigeria");
        arrayList.add("Niue");
        arrayList.add("Norfolk Island");
        arrayList.add("Northern Mariana Islands");
        arrayList.add("Norway");
        arrayList.add("Oman");
        arrayList.add("Pakistan");
        arrayList.add("Palau");
        arrayList.add("Palestinian Territories");
        arrayList.add("Panama");
        arrayList.add("Papua New Guinea");
        arrayList.add("Paraguay");
        arrayList.add("Peru");
        arrayList.add("Philippines");
        arrayList.add("Pitcairn");
        arrayList.add("Poland");
        arrayList.add("Portugal");
        arrayList.add("Puerto Rico");
        arrayList.add("Qatar");
        arrayList.add("Reunion");
        arrayList.add("Romania");
        arrayList.add("Rwanda");
        arrayList.add("Saint Barthelemy");
        arrayList.add("Saint Helena");
        arrayList.add("Saint Kitts and Nevis");
        arrayList.add("Saint Lucia");
        arrayList.add("Saint Martin (French part)");
        arrayList.add("Saint Pierre and Miquelon");
        arrayList.add("Saint Vincent and the Grenadines");
        arrayList.add("Samoa");
        arrayList.add("San Marino");
        arrayList.add("Sao Tome and Principe");
        arrayList.add("Saudi Arabia");
        arrayList.add("Senegal");
        arrayList.add("Serbia");
        arrayList.add("Seychelles");
        arrayList.add("Sierra Leone");
        arrayList.add("Singapore");
        arrayList.add("Sint Maarten (Dutch part)");
        arrayList.add("Slovakia");
        arrayList.add("Slovenia");
        arrayList.add("Solomon Islands");
        arrayList.add("Somalia");
        arrayList.add("South Africa");
        arrayList.add("South Korea");
        arrayList.add("Spain");
        arrayList.add("Sri Lanka");
        arrayList.add("Suriname");
        arrayList.add("Svalbard and Jan Mayen");
        arrayList.add("Swaziland");
        arrayList.add("Sweden");
        arrayList.add("Switzerland");
        arrayList.add("Taiwan");
        arrayList.add("Tajikistan");
        arrayList.add("Tanzania");
        arrayList.add("Thailand");
        arrayList.add("Timor-Leste");
        arrayList.add("Togo");
        arrayList.add("Tokelau");
        arrayList.add("Tonga");
        arrayList.add("Trinidad and Tobago");
        arrayList.add("Tunisia");
        arrayList.add("Turkey");
        arrayList.add("Turkmenistan");
        arrayList.add("Turks and Caicos Islands");
        arrayList.add("Tuvalu");
        arrayList.add("Uganda");
        arrayList.add("Ukraine");
        arrayList.add("United Arab Emirates");
        arrayList.add("United Kingdom");
        arrayList.add("United States Minor Outlying Islands");
        arrayList.add("United States Virgin Islands");
        arrayList.add("Uruguay");
        arrayList.add("Uzbekistan");
        arrayList.add("Vanuatu");
        arrayList.add("Venezuela");
        arrayList.add("Vietnam");
        arrayList.add("Wallis and Futuna");
        arrayList.add("Western Sahara");
        arrayList.add("Zambia");
        arrayList.add("Zimbabwe");

        return arrayList;
    }
    public static void showAlertMessage(String type, Context c, String title, String message){
        type = type.toLowerCase();
        LayoutInflater layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alert_view = layoutInflater.inflate(R.layout.custom_alert_box,null);
        ImageView iconView = alert_view.findViewById(R.id.icon);

        //default
        iconView.setBackgroundResource(R.drawable.ic_baseline_warning_amber_24);
        ColorStateList colorStateList = null;

        TextView titleView = alert_view.findViewById(R.id.title);
        titleView.setText(title);

        TextView messageView = alert_view.findViewById(R.id.message);
        messageView.setText(message);

        //set icon according to the type
        switch (type){
            case "success":
                iconView.setBackgroundResource(R.drawable.ic_baseline_check_circle_outline_24);
                colorStateList= new ColorStateList(
                        new int[][]{ new int[]{android.R.attr.state_pressed},
                                // pressed
                                new int[]{},
                                // default
                                new int[]{}
                        },
                        new int[]{ c.getColor(R.color.success_green),
                                // pressed
                                c.getColor(R.color.success_green),
                                // default
                                c.getColor(R.color.success_green)
                        });
                iconView.setBackgroundTintList(colorStateList);
                break;

            case "error":
                iconView.setBackgroundResource(R.drawable.ic_baseline_error_outline_24);
                colorStateList= new ColorStateList(
                        new int[][]{ new int[]{android.R.attr.state_pressed},
                                // pressed
                                new int[]{},
                                // default
                                new int[]{}
                        },
                        new int[]{ c.getColor(R.color.error_red),
                                // pressed
                                c.getColor(R.color.error_red),
                                // default
                                c.getColor(R.color.error_red)
                        });
                iconView.setBackgroundTintList(colorStateList);
                break;

            case "warning":
                iconView.setBackgroundResource(R.drawable.ic_baseline_warning_amber_24);
                colorStateList= new ColorStateList(
                        new int[][]{ new int[]{android.R.attr.state_pressed},
                                // pressed
                                new int[]{},
                                // default
                                new int[]{}
                        },
                        new int[]{ c.getColor(R.color.color_warning),
                                // pressed
                                c.getColor(R.color.color_warning),
                                // default
                                c.getColor(R.color.color_warning)
                        });
                iconView.setBackgroundTintList(colorStateList);
                break;
            default:
                break;
        }


        //show mesage
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(c);
        dialog.setView(alert_view);


        dialog.show();
    }

    public static Snackbar createSnackBar(Context context , View view, String text, int lengthPeriod){
        Snackbar snackBar = Snackbar.make(view,text,lengthPeriod);
        snackBar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
        snackBar.show();

        return snackBar;
    }
    public static  Snackbar createSnackBar2(Context context , View view,String text,String buttonLabel,int lengthPeriod,View.OnClickListener onClickListener){
        Snackbar snackBar = Snackbar.make(view,text,lengthPeriod);
        snackBar.setAction(buttonLabel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(view);
                snackBar.dismiss();
            }
        });
        snackBar.show();

        return snackBar;
    }

    public static  void checkIfDocumentExists(DocumentReference documentReference, OnDocumentExistStatusCallback onDocumentExistStatusCallback){
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        onDocumentExistStatusCallback.onExist(documentSnapshot);
                        /**
                         * new RatingDataModel(
                         *                                 documentSnapshot.getString("userId"),
                         *                                 documentSnapshot.getId(),
                         *                                 documentSnapshot.getId(),
                         *                                 "Library",
                         *                                 "",
                         *                                 "Hlo dami",
                         *                                 4,
                         *                                 "DownloadURl"
                         *                         )
                         */
                    }else if (!documentSnapshot.exists()){
                        onDocumentExistStatusCallback.onNotExist();
                    }
                }
                else{
                    onDocumentExistStatusCallback.onFailed(task.getException().getMessage());
                }
            }
        });
    }


    /**
     * Inflates a menu from the resource folder
     * @param context the context of the invocation
     * @param menuRes the int that represents the menu to be inflated from the res folder
     * @param anchorView the view that will be used to indicate the point to inflate the menu
     * @param onMenuItemClickListener the listener that triggers when the menu is clicked
     * */
    public static void createPopUpMenu(Context context, int menuRes, View anchorView, OnMenuItemClickListener onMenuItemClickListener) {


        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuItemClickListener.onMenuItemClicked(item);
                return false;

            }
        });
        popupMenu.show();


    }

    /**
     * This method generates keywords which is used for performing search operation in the database, it trims the given {@link String}
     * into words which enables us query and match each word for similarity in the database.
     * @param itemName the {@link String} to be trimmed
     * @return {@link ArrayList} the list that contains the keywords
     * */
    static ArrayList<String> generateSearchVerbatimKeyWords(@NonNull String itemName){
        ArrayList<String> searchVerbatimKeywordsArrayList = new ArrayList<>();

        if(itemName != null && !itemName.isEmpty()) {
            itemName = itemName.toLowerCase();
            int itemLength = itemName.length();
            searchVerbatimKeywordsArrayList.add(itemName);
            searchVerbatimKeywordsArrayList.addAll(Arrays.asList(itemName.split(" ")));

        }else{
            searchVerbatimKeywordsArrayList = new ArrayList<>();
        }

        return searchVerbatimKeywordsArrayList;
    }

    /**
     * This method generates keywords which is used for performing search operation in the database, it trims the given {@link String}
     * into {@link Character} which enables us query and match each word for similarity in the database.
     * @param itemName the {@link String} to be trimmed
     * @return {@link ArrayList} the list that contains the keywords
     * */

    static ArrayList<String> generateSearchAnyMatchKeyWords(@NonNull String itemName) {
        ArrayList<String> searchAnyMatchKeywordsArrayList = new ArrayList<>();

        if (itemName != null && !itemName.isEmpty()) {
            itemName = itemName.toLowerCase();
            int itemLength = itemName.length();

            if (itemLength != 0) {
                for (int i = 0; i < itemLength; i++) {
                    searchAnyMatchKeywordsArrayList.add(itemName.substring(i));


                    for (int j = itemLength - 1; j > i; j--) {

                        searchAnyMatchKeywordsArrayList.add(itemName.substring(i, j));


                    }


                }

                for (int i = 0; i < itemLength; i++) {
                    for (int k = i + 1; k < itemLength; k++) {
                        if (!searchAnyMatchKeywordsArrayList.contains(itemName.charAt(i) + "" + itemName.charAt(k))) {

                            searchAnyMatchKeywordsArrayList.add(itemName.charAt(i) + "" + itemName.charAt(k));

                        }
                    }

                }
            }
        }
        return searchAnyMatchKeywordsArrayList;
    }

    public static void updateActivityLog(String activityLogType, String passwordId, String notesId, ActionCallback actionCallback){
        String activityLogId = getRandomString(10);
        HashMap<String,Object> activityLogDetails = new HashMap<>();
        activityLogDetails.put(LOG_TIME_STAMP, FieldValue.serverTimestamp());
        activityLogDetails.put(ACTIVITY_LOG_TYPE,activityLogType);
        activityLogDetails.put(PASSWORD_ID,passwordId);
        activityLogDetails.put(NOTES_ID,notesId);

        if(isUserLoggedIn()) {
            getFirebaseFirestoreInstance()
                    .collection(ALL_USERS)
                    .document(getCurrentUserId())
                    .collection(USER_ACTIVITY_LOG)
                    .document(activityLogId)
                    .set(activityLogDetails)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            actionCallback.onFailed(e.getMessage());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            actionCallback.onSuccess();
                        }
                    });
        }else{
            actionCallback.onSuccess();

        }

        switch(activityLogType){
            case GlobalConfig.ACTIVITY_LOG_USER_SIGN_UP_TYPE:


                return;
            case GlobalConfig.ACTIVITY_LOG_USER_SIGN_IN_TYPE:


                return;
            case GlobalConfig.ACTIVITY_LOG_USER_EDIT_ACCOUNT_TYPE:


                return;
            case GlobalConfig.ACTIVITY_LOG_USER_VIEWED_PASSWORD_TYPE:


                return;

            case GlobalConfig.ACTIVITY_LOG_USER_ADD_NEW_PASSWORD_TYPE:


                return;
            case GlobalConfig.ACTIVITY_LOG_USER_DELETE_PASSWORD_TYPE:


                return;
            case GlobalConfig.ACTIVITY_LOG_USER_EDIT_PASSWORD_TYPE:


                return;
            case GlobalConfig.ACTIVITY_LOG_USER_CREATE_NEW_NOTES_TYPE:


                return;
            case GlobalConfig.ACTIVITY_LOG_USER_VIEWED_NOTES_TYPE:


                return;
            case GlobalConfig.ACTIVITY_LOG_USER_DELETE_NOTES_TYPE:


                return;
            case GlobalConfig.ACTIVITY_LOG_USER_EDIT_NOTES_TYPE:


                return;
        }

    }


//    public static Intent getHostActivityIntent(Context context, @Nullable Intent intent, String fragmentOpenType, String userId){
//        if(intent == null){
//            intent = new Intent(context,HostActivity.class);
//        }
//
//        intent.putExtra(GlobalConfig.FRAGMENT_TYPE_KEY,fragmentOpenType);
//        intent.putExtra(GlobalConfig.USER_ID,userId);
//
//        return  intent;
//    }
//
//    public static ShimmerFrameLayout showShimmerLayout(Context context, ViewGroup viewGroup ){
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        ShimmerFrameLayout shimmerFrameLayout = (ShimmerFrameLayout) layoutInflater.inflate(R.layout.progress_indicator_shimmer_layout,viewGroup,false);
////        ShimmerFrameLayout shimmerFrameLayout  = view.findViewById(R.id.progressIndicatorShimmerId);
//        shimmerFrameLayout.startShimmer();
//        viewGroup.addView(shimmerFrameLayout);
//        return shimmerFrameLayout;
//    }
//    public static void removeShimmerLayout(ViewGroup viewGroup, ShimmerFrameLayout shimmerFrameLayout){
//        if(shimmerFrameLayout !=null){
//            shimmerFrameLayout.stopShimmer();
//            viewGroup.removeView(shimmerFrameLayout);
//        }
//        shimmerFrameLayout = null;
//    }
    /**
     * An interface for manipulatiing menu
     * */
    public interface OnMenuItemClickListener {
        /**triggered when menu is clicked*/
        boolean onMenuItemClicked(MenuItem item);

    }

    public interface OnDocumentExistStatusCallback{

        void onExist(DocumentSnapshot documentSnapshot);
        void onNotExist();
        void onFailed(@NonNull String errorMessage);

    }

    public interface ActionCallback{
        void onSuccess();
        void onFailed(String errorMessage);
    }
//    interface OnCurrentUserProfileFetchListener{
//        void onSuccess(CurrentUserProfileDataModel currentUserProfileDataModel);
//        void onFailed(String errorMessage);
//    }

}
