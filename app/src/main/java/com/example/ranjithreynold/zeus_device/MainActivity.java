package com.example.ranjithreynold.zeus_device;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */

public class MainActivity extends Activity {

    String otp1="";
    Socket socket;
    {
        try
        {
            socket= IO.socket("http://zeus75.herokuapp.com");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    TextView otp;
    TextView user;

String car_reg="TN23CA0237";
String name="";
String phone="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      socket.connect();
      socket.on("message",get_id);
      socket.on("otp",view_otp);
      socket.on("user_connect",user_connect);   //After requesting connection from phone
      //otp=findViewById(R.id.textView);
      user=findViewById(R.id.textView);
      }


    Emitter.Listener user_connect=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject= (JSONObject) args[0];
                         phone=jsonObject.getString("phone");
                         name=jsonObject.getString("name");
                        user.setText(String.format("Welcome %s", name));

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    };





    Emitter.Listener get_id=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject= (JSONObject) args[0];
                        String message=jsonObject.getString("id");
                        JSONObject jsonObject1=new JSONObject();
                        jsonObject1.put("id",message);
                        jsonObject1.put("category","car");
                        jsonObject1.put("no",car_reg);
                        socket.emit("register",jsonObject1.toString());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    Emitter.Listener view_otp=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject= (JSONObject) args[0];
                        String message=jsonObject.getString("pin");
                        otp1=message;
                        OTP otp2=new OTP();
                        otp2.show(getFragmentManager(),"OTP");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    };




}
