package com.example.collegeregistapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

// **회원가입 요청을 보내는 class정의**
public class RegisterRequest extends StringRequest {

    // 작성해놓은 UserRegister.php 경로 정의
    final static private String URL = "http://happyhunte.cafe24.com/UserRegister.php";
    //자료형 배열에 적합한 Map String 배열의 변수 정의
    private Map<String, String> parameters;

    // MySQL DB_data를 parameter값으로 class 생성자 정의
    public RegisterRequest(String userID, String userPassword, String userGender
            , String userMajor, String userEmail, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userGender", userGender);
        parameters.put("userMajor", userMajor);
        parameters.put("userEmail", userEmail);

    }

    public Map<String, String> getParams() {
        return parameters;
    }
}
