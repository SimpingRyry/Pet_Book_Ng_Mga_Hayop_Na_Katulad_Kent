package com.example.petbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DonationsAdapter.OnButtonClickListener{
    String SECRET_KEY = "sk_test_51PFSUlAj3m1FbFF3DFbkUifQl1vQSVGMg2AVRrY3Yd14JAGaE5pFSQFKm36Po02vtENJyYvddsT0ctBlKIn8CMwe00pUU1m63p";
    String PUBLISH_KEY = "pk_test_51PFSUlAj3m1FbFF3kXjFCtmENiu9tQTngjmVPrvq5rNGoN3FwUIQAUIru8vLgf84qeVWgh5vSu9SFkyH04uYDXUP00sVb4JUiI"; PaymentSheet paymentsheet;
    String customerID;
    String EphericalKey;
    String ClientSecret;
    private SharedPreferences preferences;
    final  private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

    private void get_token(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::update_token);
    }

    private void update_token(String token){
        databaseReference.child(preferences.getString("account_type","")).child(preferences.getString("loggedInUser", "")).child("fcmtoken").setValue(token);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    get_token();
        FrameLayout framelayout = findViewById(R.id.mainlayout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        PaymentConfiguration.init(this,PUBLISH_KEY);
        fetchapi();
        paymentsheet = new PaymentSheet(this, paymentSheetResult -> {
                OnPaymentResult(paymentSheetResult);
        });

        if (NetworkUtils.isInternetConnected(getApplicationContext())) {

            // Device is connected to the internet
        } else {
            Toast.makeText(MainActivity.this,"Please ensure network connectivity",Toast.LENGTH_SHORT).show();
            // Device is not connected to the internet
        }






        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int itemID = menuItem.getItemId();

                if (itemID == R.id.button_home) {
                    loadFragment(new HomeFragment(), false);
                } else if (itemID == R.id.button_donation) {
                    loadFragment(new DonationFragment(), false);
                } else if (itemID == R.id.button_lnf) {
                    loadFragment(new LostAndFoundFragment(), false);
                } else {
                    loadFragment(new MessagesFragment(), false);
                }

                return true;
            }
        });
        loadFragment(new HomeFragment(), true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.mainlayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.mainlayout, fragment);
        }
        fragmentTransaction.commit();
    }

    private void fetchapi(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    customerID = object.getString("id");



                    getEphiricalkey(customerID);


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);


                return header;
            }


        };

        RequestQueue requestQueue =  Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }



    private void getEphiricalkey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    EphericalKey = object.getString("id");
                    getClientSecret(customerID,EphericalKey);




                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                header.put("Stripe-Version","2024-04-10");

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerID);

                return params;
            }
        };

        RequestQueue requestQueue =  Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephericalKey) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    ClientSecret = object.getString("client_secret");


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerID);
                params.put("amount","1"+"00");
                params.put("currency","usd");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };

        RequestQueue requestQueue =  Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public  void paymentFlow(){
        paymentsheet.presentWithPaymentIntent(ClientSecret,new PaymentSheet.Configuration("ABC Company",new PaymentSheet.CustomerConfiguration(
                customerID,
                EphericalKey
        )));
    }
    private void OnPaymentResult(PaymentSheetResult paymentSheetResult){
        if (paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            fetchapi();
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing

    }

    @Override
    public void onButtonClick(int position) {
        paymentFlow();
    }
}