package com.palria.passwordsaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        if(!GlobalConfig.isUserLoggedIn()){
            Intent intent = new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }
        setContentView(R.layout.activity_main);
    }

    @Override
   public void onDestroy(){
        super.onDestroy();
        if(!GlobalConfig.isKeepMeLoggedIn(this)){
            Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
        }
    }

    void initDatrababase(){

    }
}