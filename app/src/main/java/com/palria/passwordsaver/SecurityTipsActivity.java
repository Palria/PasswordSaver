package com.palria.passwordsaver;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorStateListDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

public class SecurityTipsActivity extends AppCompatActivity {
LinearLayout tipsContainerLinearLayout;
ImageButton backButton;
ImageButton cancelActionButton;
ScrollView bottomSheetScrollView;
BottomSheetBehavior bottomSheetBehavior;
TextView titleTextView;
TextView descriptionTextView;
boolean isBottomSheetOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_tips);
        initUI();
        setupBottomSheet();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecurityTipsActivity.super.onBackPressed();
            }
        });

        String title1 = "Create strong password";
        String description1 = "Strong password consists of combination of uppercase and lowercase letters, symbols, and numbers. Creating strong password will make your Passwords harder to guess by anyone.";
        addTip(title1,description1, getResources().getColor(R.color.light_blue_600,getTheme()));

        String title2 = "  Find My Device";
        String description2 = "Our android phone  offers a Find My Device tool that allows you to track, lock, and erase your device when it’s lost or stolen.\n" +
                " To activate Find My Device, sign in to your Google account, Your location must be  turned on.\n" +
                " To do this follow these steps:" +
                " Go to Settings > Security > turn on Find My Device";
        addTip(title2,description2,getResources().getColor(R.color.error_red,getTheme()));

        String title3 = "Finger Print Authentication";
        String description3 =
                " Newer devices have Finger print sensors built in, you can use them as a secure Authentication to protect your device. Finger print Authentication are more secure than Password Authentication.\n" +
                "                \n" +
                " How to turn Finger Print Authentication on:\n" +
                " Go to Settings > Lock screen and security > Screen lock type > add your fingerprint .";
        addTip(title3,description3, getResources().getColor(R.color.purple_500,getTheme()));

        String title4 = "Face unlock are inaccurate sometimes";
        String description4 =" Face unlock Technology in our phones may inaccurately detect a face similar to ours thereby will permit the face of the third party to unlock our phones.\n"
                +
                " Sometimes ago this happened to me because the third person was as dark and circular in face just as I'm. That person was able to unlock my phone using his face."+
                "    \n" +
                " Consider this Security your last resort." +
                "    \n" +
                " Maybe the future updates of our Operating Systems may correct this.";
        addTip(title4,description4,  getResources().getColor(R.color.color_warning,getTheme()));

        String title5 = "On-Body Detection";
        String description5 = "On-Body Detection feature determines if you have your device on your person or in your hand. If it's in your hand it will keep unlocked.\n" +
                " Once you put your phone down, it will automatically lock. It may not be able to detect if your phone is passed to another person. Your phone can also be unlocked in another person's hand.";
        addTip(title5,description5,  getResources().getColor(R.color.design_default_color_error,getTheme()));


        String title6 = "Trusted Places";
        String description6 ="The Trusted Places feature allows you to configure your phone settings so it remains unlocked while you’re at home or in other locations you chose.";
        addTip(title6,description6,  getResources().getColor(R.color.design_default_color_secondary,getTheme()));


        String title7 = "Trusted Face Recognition";
        String description7 = "This biometric feature is part of Android’s Smart Lock suite. It allows you to unlock your device with facial recognition. But for 2D front camera be careful because it may be unlocked by another person's face. Always consider using 3D depth sensing Camera to detect accurately.";
        addTip(title7,description7,  getResources().getColor(R.color.design_default_color_secondary_variant,getTheme()));

        String title8 = "Trusted Voice";
        String description8 = " If your device has voice detection set up, you can use this feature to unlock your phone when it hears a trusted voice.";
        addTip(title8,description8,  getResources().getColor(R.color.light_blue_900,getTheme()));


        String title9 = "Safe Browsing in Google Chrome";
        String description9 = "The Safe Browsing feature in Google Chrome and other web browsers helps protect against websites that contain malware or phishing content. Make sure it's turned on in your browsers to protect you from phishing and malicious softwares";
        addTip(title9,description9,  getResources().getColor(R.color.light_blue_600,getTheme()));


        String title10 = "Disable Smart Lock and Auto Sign-In";
        String description10 = "While the Smart Lock for passwords and auto sign-in features can be handy, Consider disabling them if you want a strong security setup in your phone. If you have these features on and someone steals your phone, they may be able to unlock your phone and do as they intend.";
        addTip(title10,description10,  getResources().getColor(R.color.teal_700,getTheme()));


        String title11 = "Disable Bluetooth Connectivity";
        String description11 = "Consider disabling Bluetooth when in public. Bluetooth lets your phone connect wirelessly with other devices,it may let others connect to your device without your permission.";
        addTip(title11,description11,  getResources().getColor(R.color.success_green,getTheme()));


        String title12 = "Two-step Authentication";
        String description12 = "Enable this verification in all your accounts to add another layer of security to it." +
                " Once enabled, this form of verification gives you an extra layer of security by requiring two different codes from two different sources. After you submit a password, a code will be sent to your default phone via text or a call. Only entering this code will give you access to an account.\n" +
                " This will help for instance when hackers try signing in to your account, they will be asked to input a code received from your device, in this situation they will not be able to input the correct code because they have no access to your phone.\n" +
                " This is also known as 2FA.";
        addTip(title12,description12,  getResources().getColor(R.color.light_blue_A400,getTheme()));


        String title13 = "Some Android security Threats";
        String description13 = "Malwares that sneak into our phones intend to cause damages. It's important to know them to enable you secure your device against these attacks.\n" +
                " They're: \n" +
                "         Viruses, Computer worms, Trojans, ransomeware, spyware, KeyLoggers. \n"
                +
                " When these viruses penetrate your phone, they damage our files and other important data. When ransomeware is installed in your phone it hijacks it and ask for payment. Spyware will always monitor your Internet activities. KeyLoggers record your key events and alphabets while you type making it easy for them to hack your accounts. Be very careful to avoid letting them into your device.\n"+
                " Steps to enhance your device security:\n" +
                " 1 Consider reading app's requirements before Installation.\n" +
                " 2 Read app's Privacy Policy to know how they use your information.\n" +
                " 3 Consider checking if the app is worthy of Installation if not do not install it.\n" +
                " 5 Update your device to the latest security patch\n" +
                " 6 Avoid opening unsolicited emails or files.\n" +
                " 7 Remove and uninstall unuseful and unneeded apps\n" +
                " 8 Install trusted anti-malware(antivirus)";
        addTip(title13,description13,  getResources().getColor(R.color.success_green,getTheme()));

    }

    @Override
   public void onBackPressed(){
        if(isBottomSheetOpen){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            super.onBackPressed();
        }
    }

    void initUI(){
        tipsContainerLinearLayout = findViewById(R.id.tipsContainerLinearLayoutId);
        backButton = findViewById(R.id.backButtonId);
        cancelActionButton = findViewById(R.id.cancelActionButtonId);
        bottomSheetScrollView = findViewById(R.id.bottomSheetScrollViewId);
        titleTextView = findViewById(R.id.titleTextViewId);
        descriptionTextView = findViewById(R.id.descriptionTextViewId);
    }
    void setupBottomSheet(){
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetScrollView);
        isBottomSheetOpen = false;
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    isBottomSheetOpen = true;
                }
                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    isBottomSheetOpen = false;
                }
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    isBottomSheetOpen = false;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        cancelActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    void addTip(String title, String description,@ColorInt int backgroundColorInt){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView  =  layoutInflater.inflate(R.layout.security_tips_item_layout,tipsContainerLinearLayout,false);
        TextView tipsTitleTextView = itemView.findViewById(R.id.tipsTitleTextViewId);
        tipsTitleTextView.setText(title);
        ConstraintLayout main_constraint_layout = itemView.findViewById(R.id.main_constraint_layout);
//        TextView tipsColorerTextView = itemView.findViewById(R.id.tipsColorerTextViewId);
        main_constraint_layout.setBackgroundColor(backgroundColorInt);
        TextView tipsDescriptionTextView = itemView.findViewById(R.id.tipsDescriptionTextViewId);
        tipsDescriptionTextView.setText(description);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFullTip(title,description);
            }
        });
        int[][] states = {{android.R.attr.state_pressed},{android.R.attr.state_enabled},{android.R.attr.state_enabled}};
        int[] colors = {R.color.secondary_app_color,R.color.error_red,R.color.color_warning};
        ColorStateList colorStateList = new ColorStateList(states,colors);
//        main_constraint_layout.setBackgroundTintList(colorStateList);

        tipsContainerLinearLayout.addView(itemView);
    }

    void viewFullTip(String title, String description){
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
