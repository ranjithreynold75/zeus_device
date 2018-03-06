package com.example.ranjithreynold.zeus_device;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.toolbox.Volley.newRequestQueue;

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
    TextView textView;

String car_reg="TN23CA0237";
String name="";
String phone="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=findViewById(R.id.textView5);
      socket.connect();
      socket.on("message",get_id);
      socket.on("otp",view_otp);
      socket.on("user_connect",user_connect);   //After requesting connection from phone
      //otp=findViewById(R.id.textView);
      user=findViewById(R.id.textView);
      data_re();
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


    public void refresh_data(View view) {
        data_re();
    }

    public void data_re()
    {
        final String[] data = {""};
        StringRequest stringRequest=new StringRequest(Request.Method.POST, "http://zeus75.herokuapp.com/trip_plan", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("plan");
                    int count=0;

                    while (count<jsonArray.length())
                    {
                        JSONObject jsonObject1=jsonArray.getJSONObject(count);
                        String user=jsonObject1.getString("user");
                        String date=jsonObject1.getString("date");
                        String time=jsonObject1.getString("time");
                        String description=jsonObject1.getString("description");
                        data[0] =new String(new StringBuilder(user+"\t"+date+"\t"+time+"\t"+description+"\n"));
                        count++;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                textView.setText(data[0]);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError || error instanceof NoConnectionError)
                {
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("id",car_reg);
                return hashMap;
            }
        };
        RequestQueue requestQueue= newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
