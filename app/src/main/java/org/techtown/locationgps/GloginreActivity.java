package org.techtown.locationgps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class GloginreActivity extends AppCompatActivity {

//    // ...
//    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
//    private boolean showOneTapUI = true;
//    // ...
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case REQ_ONE_TAP:
//                try {
//                    SignInCredential credential = oneTaper.Client.getSignInCredentialFromIntent(data);
//                    String idToken = credential.getGoogleIdToken();
//                    if (idToken !=  null) {
//                        // Got an ID token from Google. Use it to authenticate
//                        // with Firebase.
//                        Log.d(TAG, "Got ID token.");
//                    }
//                } catch (ApiException e) {
//                    // ...
//                }
//                break;
//        }
//    }
//}


    private TextView tv_nickname;
    private ImageView iv_profile;
    private TextView tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gloginre);

        Intent intent = getIntent();

        tv_nickname = findViewById(R.id.tv_nickname);
        tv_nickname.setText(intent.getStringExtra("nicmName"));
        tv_email = findViewById(R.id.tv_email);
        tv_email.setText(intent.getStringExtra("Email"));
        Button button_sign_out;

        iv_profile = findViewById(R.id.iv_profile);
        Glide.with(this).load(intent.getStringExtra("photoUrl")).into(iv_profile); // 프로필 url을 이미지에 셋팅


        button_sign_out = findViewById(R.id.button_sign_out);
        button_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GloginreActivity.this, "This button ok click.", Toast.LENGTH_SHORT).show();
            }
        });

//        FirebaseAuth mAuth;
//        mAuth = FirebaseAuth.getInstance();

    }

//    //@Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            // ...
//            case R.id.button_sign_out:
//                signOut();
//                break;
//            // ...
//        }
//    }

//    private void signOut() {
//        mGoogleSignInClient.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
//    }


}