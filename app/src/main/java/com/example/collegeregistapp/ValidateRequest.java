package com.example.collegeregistapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

// **회원가입이 가능여부(ID중복 체크등)를 체크하는 Class정의**
public class ValidateRequest extends StringRequest {

    // 작성해놓은 UserRegister.php 경로 정의
    final static private String URL = "http://happyhunte.cafe24.com/UserValidate.php";
    //자료형 PHP데이터를 저장을 위해 배열에 적합한 Map String 배열의 변수 정의
    private Map<String, String> parameters;

    // MySQL ID를 parameter값으로 class 생성자 정의
    // userID를 위 URL주소의 php데이터에 확인요청하는 정의
    public ValidateRequest(String userID, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    public Map<String, String> getParams(){
        return parameters;
    }

}
