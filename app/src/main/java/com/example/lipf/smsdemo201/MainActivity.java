package com.example.lipf.smsdemo201;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class MainActivity extends AppCompatActivity {
    private Button btn1,btn2;
    private EditText edt1,edt2;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button)findViewById(R.id.button);
        btn2 = (Button)findViewById(R.id.button2);
        edt1 = (EditText)findViewById(R.id.editText);
        edt2 = (EditText)findViewById(R.id.editText2);



        SMSSDK.initSDK(this,"12812e4aa7490","db3a7efa80e02b6b9db01451233c5ed0", true);
        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "123", Toast.LENGTH_SHORT).show();
                SMSSDK.getVerificationCode("86", edt1.getText().toString());
                phone = edt1.getText().toString();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSSDK.submitVerificationCode("86",phone,edt2.getText().toString());
            }
        });
    }


    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result"+event);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //已经验证
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();


                }

            } else {
//				((Throwable) data).printStackTrace();
//				Toast.makeText(MainActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
//					Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;

                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(MainActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }


        };
    };

    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    };


}
