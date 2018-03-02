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


String car_reg="TN23CA0237";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      socket.connect();
      socket.on("message",get_id);
      socket.on("otp",view_otp);
      otp=findViewById(R.id.otp);


      }


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
                        otp.setText(message);
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