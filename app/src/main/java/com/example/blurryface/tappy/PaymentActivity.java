package com.example.blurryface.tappy;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.africastalking.AfricasTalking;
import com.africastalking.models.payment.checkout.CheckoutResponse;
import com.africastalking.models.payment.checkout.MobileCheckoutRequest;
import com.africastalking.services.PaymentService;
import com.africastalking.utils.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;


import dmax.dialog.SpotsDialog;


public class PaymentActivity extends Activity {
    PaymentService paymentService;
    TextView shield1,shield2,shield3;
    OkHttpClient client;
    Request request;
    int status;
    boolean onFirstResume;
    SpotsDialog dialog;
    float initialdistance;
    int extraShield=0;
    int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        //get the score and level user had before game over
        String score = getIntent().getStringExtra("distance");
        initialdistance = Float.parseFloat(score);
        String level = getIntent().getStringExtra("currentLevel");
        currentLevel = Integer.parseInt(level);
        //initialise UI
        shield1 = findViewById(R.id.oneLifeTxt);
        shield2 = findViewById(R.id.twolivesText);
        shield3 = findViewById(R.id.threeLives);
        try {
            AfricasTalking.initialize("10.66.23.215",35897, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //initialise the dialog
        dialog = new SpotsDialog(this,"Processing");
        //set our status to 0 to mean first resume
        onFirstResume = true;
        status = 0;
    }
    public void onBuyOne(View view){
        extraShield=1;
        //user buys one life
        final String amount = shield1.getText().toString();
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                payment(amount);

            }
        });
        status = 5;
    }

    public void onBuyTwo(View view){
        extraShield=2;
        //user buys two lives
        final String amount = shield2.getText().toString();
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                payment(amount);

            }
        });
        status = 5;
    }

    public void onBuyThree(View view){
        extraShield=3;
        //user buys three lives
        final String amount = shield3.getText().toString();
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                payment(amount);

            }
        });
        status = 5;
    }
    public void onCancel(View view){
        Intent intent = new Intent(PaymentActivity.this,GameOverActivity.class);
        intent.putExtra("distance",String.valueOf(initialdistance));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void payment(String amount){
        try {
            paymentService = AfricasTalking.getPaymentService();
            MobileCheckoutRequest checkoutRequest = new MobileCheckoutRequest("MusicApp",amount,"0703280748");
            paymentService.checkout(checkoutRequest, new Callback<CheckoutResponse>() {
                @Override
                public void onSuccess(CheckoutResponse data) {
                    Log.e("sss",data.status);
                    Toast.makeText(PaymentActivity.this,data.status,Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    dialog.dismiss();
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void confirmPayment(){
        client = new OkHttpClient();
        request = new Request.Builder().url("http://10.66.23.215:30001/transaction/status").build();
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                dialog.dismiss();
                Log.e("failure",e.getMessage());
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                dialog.dismiss();
                String status = response.body().string();
                //if user either cancels or has insufficient funds we go to game over
                if(status.equals("Failed")){
                    //if it fails to pay sends you to game over page
                    showFailMessage();
                    Intent intent = new Intent(PaymentActivity.this,GameOverActivity.class);
                    intent.putExtra("distance",String.valueOf(initialdistance));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }else if(status.equals("Success")){
                    //if successful resume the game with your previous score and an extra life
                    Intent intent = new Intent(PaymentActivity.this,GameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("extrashield",String.valueOf(1));
                    intent.putExtra("initialDistance",String.valueOf(initialdistance));
                    intent.putExtra("currentLevel",String.valueOf(currentLevel));
                    startActivity(intent);
                }

            }
        });
    }
    public void showFailMessage(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PaymentActivity.this,"Your payment has failed",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //when user first gets to the activity
        if(onFirstResume){
            onFirstResume = false;
            Log.e("resume",String.valueOf(status));
        }else if(!onFirstResume&&status==5) {
            //after mpesa pop up
            status = 3;
            Log.e("resume",String.valueOf(status));

            dialog.show();
            //wait for ten seconds to confirm
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    confirmPayment();
                }
            }, 10000);
        }else{
            Log.e("resume","normal");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(status==5){
            status = 5;
        }
        else {
            status=3;
        }

    }


}
