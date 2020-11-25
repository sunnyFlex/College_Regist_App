package com.example.collegeregistapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

// ***** 회원가입 관련 Activity정의 *****
public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private Spinner spinner;

    private String userID;
    private String userPassword;
    private String userGender;
    private String userMajor;
    private String userEmail;

    private AlertDialog dialog;
    private boolean validate = false;

    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = findViewById(R.id.major_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.major, android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        final EditText idText = findViewById(R.id.id_text);
        final EditText passwordText = findViewById(R.id.password_text);
        final EditText emailText = findViewById(R.id.email_text);


        // *** gender 체크 ***
        // * 체크된button 체크 ID를 가져와 변수에 저장
        RadioGroup genderGroup = findViewById(R.id.gender_group);
        int genderGroupID = genderGroup.getCheckedRadioButtonId();
        userGender = ((RadioButton)findViewById(genderGroupID)).getText().toString();

        // * gender 체크버튼변경시 확인
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton genderButton = findViewById(i);
                userGender = genderButton.getText().toString();
            }
        });


        // *** ID중복확인 정의 ***
        // ** id중복확인 버튼 클릭시
        final Button validateButton = findViewById(R.id.validate_Button);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // - user가 입력한 String을 변수에 저장
                String userID = idText.getText().toString();
                // ** 만약 이미 중복확인이 되어 있는 상태또는 ID미입력상태 라면
                // - 이미 중복확인 되어 있다면 return으로 함수 종료
                if (validate) {
                    return;
                }
                // - userId를 미입력한 경우 알림창 출력
                if (userID.equals("")) {
                    // - 경고 알림창 띄운다.
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder
                            .setMessage("아이디를 입력해 주세요")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // ** 정상적으로 ID값이 입력되어 있다면 응답하는 콜백이벤트
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // * JSON을 통해 응답을 받을 수 있게 정의
                        try {
                            // -- 응답 내용JSON파일에 응답을 담아서 boolean 변수에 저장
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            // * 만약 JSON에서 success 응답이 왔다면 즉 사용할수 있는 ID라면
                            if (success) {
                                // - 성공 알림팝업 출력
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 ID입니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();
                                // - idText 비활성화
                                idText.setEnabled(false);
                                // - 중복확인됨을 true로
                                validate = true;
                                // - idText 바탕색을 회색으로 변경
                                idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                // - 확인버튼 회색 변경
                                validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            } else{
                                // * 중복확인에서 성공하지 못했을시 즉 중복ID가 존재할시
                                // - 실패 알림팝업 출력
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                // ** ID중복 확인할 PHP데이터파일을 ValidateRequest Class에서 가져와 확인요청할 정보버튼 클릭시 필수 정의
                // - 추가해 놓은 ValidateRequest.java class에 userID와 responseListener 입력값으로 인스턴스 변수 선언
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                // - volley요청큐를 FiFo방식의 RequestQueue데이터로 저장 해서 요청을 보낸다.
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                // - validataRequest변수를 queue의 맨마지막에 추가
                queue.add(validateRequest);
            }
        });

        // *** 회원가입하기 정의 ***
        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // - 입력된 각각 data 가져와 저장
                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                String userMajor = spinner.getSelectedItem().toString();
                String userEmail = emailText.getText().toString();

                // ** 회원가입 불가상황 정의
                // * ID중복이 확인 되지 않았을 경우
                if (!validate) {
                    // - 실패 알림창 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("ID 중복체크 필요")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }
                // * 각각의 입력창에 하나라도 입력되지 않은 경우
               if (userID.equals("") || userPassword.equals("") || userMajor.equals("") || userEmail.equals("")) {
                   // - 실패 알림창 출력
                   AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("모두 정보를 입력해 주세요")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                   return;
               }

                // ** 회원가입 가능 상황 정의
                // * Response 응답 관련 이벤트 정의
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 가입 성공.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();
                                //회원가입이 성공한 경우 회원가입 activity를 finish()로 닫아준다.
                                finish();
                            } else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 가입 실패")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                // ** Request 요청 관련 정의
                // - RegisterRequest.java class에 필요데이터를 입력값으로 인스턴트 메소드를 정의
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userGender, userMajor, userEmail, responseListener);
                // - 새 queue 생성
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                // - queue 에 변수 추가
                queue.add(registerRequest);
            }
        });
    }

    // *** 회원가입 완료후 정의 ***
    @Override
    protected void onStop() {
        super.onStop();
        // * 만약 다이얼로그가 비어있지 않다면
        if (dialog != null){
            // - 다이얼 로그를 닫고
            dialog.dismiss();
            // - 다이얼 로그를 비운다.
            dialog = null;
        }
    }
}