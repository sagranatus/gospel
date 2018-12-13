package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.server.Server_UserData;

public class RegisterActivity extends AppCompatActivity{
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputChristName;
    private EditText inputCathedral;
    private EditText inputAge;
    private EditText inputRegion;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //actionbar setting
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_register);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
        actionbar.setElevation(0);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputChristName= (EditText) findViewById(R.id.christ_name);
        inputCathedral = (EditText) findViewById(R.id.cathedral);
        inputRegion = (EditText) findViewById(R.id.region);
        inputAge= (EditText) findViewById(R.id.age);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        inputFullName.setBackgroundResource(R.drawable.edit_bg);
        inputEmail.setBackgroundResource(R.drawable.edit_bg);
        inputPassword.setBackgroundResource(R.drawable.edit_bg);
        btnRegister.setBackgroundResource(R.drawable.button_bg);
        inputChristName.setBackgroundResource(R.drawable.edit_bg);
        inputCathedral.setBackgroundResource(R.drawable.edit_bg);
        inputRegion.setBackgroundResource(R.drawable.edit_bg);
        inputAge.setBackgroundResource(R.drawable.edit_bg);
        EditText password_confirm = (EditText) findViewById(R.id.password_confirm);
        password_confirm.setBackgroundResource(R.drawable.edit_bg);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());


        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                String name = inputFullName.getText().toString().trim();
                String christ_name = inputChristName.getText().toString().trim();
                String cathedral = inputCathedral.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String age = inputAge.getText().toString().trim();
                String region = inputRegion.getText().toString().trim();
                int place = 0;
                String id = "";
                try{
                    place = email.indexOf('@');
                    id = email.substring(0, place);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),
                            "이메일 주소가 잘못되었습니다", Toast.LENGTH_LONG)
                            .show();
                }
                EditText password_confirm = (EditText) findViewById(R.id.password_confirm);
                String password = inputPassword.getText().toString().trim();
                if(password.equals(password_confirm.getText().toString().trim())){
                    if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()  && !christ_name.isEmpty() && !cathedral.isEmpty()  && !age.isEmpty() && !region.isEmpty()) {
                        Server_UserData.registerUser(RegisterActivity.this, session, pDialog, id, email, password,  name, christ_name, age, region, cathedral);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "모든 정보를 입력해주세요", Toast.LENGTH_LONG)
                                .show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            "비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        // 화면 클릭시 soft keyboard hide
        findViewById(R.id.ll).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });

    }


    // 커스텀 다이얼로그 선택시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                finish();
                return true;
        }
    }

}