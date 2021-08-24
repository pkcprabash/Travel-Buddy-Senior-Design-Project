package com.example.travelbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WebsiteLoader extends AppCompatActivity {
    WebView webView;
    ImageView imageViewBackArrow;
    String placeID;

    LinearLayout linearLayoutProgress;
    TextView textViewPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.website_loader);
        initView();
        getIntentData();
    }

    private void getIntentData() {

        Intent intent=getIntent();
        placeID=intent.getStringExtra("placeID");
        textViewPlaceName.setText(intent.getStringExtra("placeName"));
        getPlaceWebSite();

    }
    private void getPlaceWebSite() {
        linearLayoutProgress.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);

        String url=getString(R.string.google_map_place_details_api)
                +"place_id="+placeID
                +"&fields=website,url"
                +"&key="+getString(R.string.google_map_api_key);
        Log.d("TAG", url);

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                , response -> {
            linearLayoutProgress.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            Log.d("Response",response);
            try {

                JSONObject jsonObject=new JSONObject(response);
                if(jsonObject.getString("status").equals("OK")){
                    JSONObject jsonObject1Result=jsonObject.getJSONObject("result");
                    if(jsonObject1Result.has("website")){
                        initWebLoader(jsonObject1Result.getString("website"));
                    }else {
                        showToast("could't found website for this hotel application Loading google map");
                        initWebLoader(jsonObject1Result.getString("url"));
                    }


                }else {
                    showToast("Invalid response from server");
                }


            } catch (JSONException e) {
                e.printStackTrace();
                showToast("Google map api parsing error"+e.getMessage());
            }


        }, error -> {
            showToast("Error : " + error.getMessage());
            Log.d("Error ",error.getMessage());

        });
        stringRequest.setShouldCache(false);
        requestQueue2.add(stringRequest);
    }

    private void initView() {
        webView=findViewById(R.id.webView);// get web view from UI
        linearLayoutProgress=findViewById(R.id.layoutProgress);// get progress layout from UI
        imageViewBackArrow=findViewById(R.id.backArrow); // get back arrow from UI
      textViewPlaceName=findViewById(R.id.textViewPlaceName);

        imageViewBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebLoader(String url) {


        webView.getSettings().setJavaScriptEnabled(true);// enable java script for web view
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);



        linearLayoutProgress.setVisibility(View.VISIBLE);

        webView.setVisibility(View.GONE);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }


            @Override
            public void onPageFinished(WebView view, final String url) {

                if (view.getTitle().equals(""))// when nothing load like empty page
                    view.reload();
                else{


                    linearLayoutProgress.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);

                }
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showToast("Your Internet Connection May not be active Or " + error.getDescription());
                }
            }
        });
        webView.loadUrl(url);
    }
    private void showToast(String s) {
        Toast.makeText(getApplicationContext(),
                s, Toast.LENGTH_SHORT).show();
    }
}