package com.edgetag.sampleapp.view.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.edgetag.model.OnComplete;
import com.edgetag.sampleapp.R;
import com.edgetag.EdgeTag;
import com.edgetag.EdgeTagConfiguration;
import com.edgetag.model.CompletionHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class EdgeTagHomeActivity extends AppCompatActivity {

    Button initPress;
    ToggleButton toggleButton;
    TextView initResponse;
    Button consent_button_press;
    EditText consent_text;
    TextView consent_response;
    Button provider_button_press;
    Button user_button_press;
    EditText tag_key;
    EditText provider_key;
    TextView tag_response;
    TextView user_response;
    EditText userkey;
    EditText uservalue;

    EditText post_data;
    Button post_data_button_press;
    TextView post_data_response;

    EditText get_data;
    Button get_data_button_press;
    TextView get_data_response;

    Button get_key_button_press;
    TextView get_key_response;

    Button ad_available_button_press;
    TextView ad_available_response;

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edgetag);

        EdgeTagConfiguration edgetagConfiguration = new EdgeTagConfiguration();
        edgetagConfiguration.setEndPointUrl("https://sdk-demo-t.edgetag.io");

        initPress = findViewById(R.id.init_button_press);
        toggleButton = findViewById(R.id.disableConsentCheckToggleButton);
        initResponse = findViewById(R.id.init_response);
        consent_button_press = findViewById(R.id.consent_button_press);
        consent_text = findViewById(R.id.consent_text);
        consent_response = findViewById(R.id.consent_response);
        provider_button_press = findViewById(R.id.provider_button_press);
        tag_key = findViewById(R.id.tag_key);
        provider_key = findViewById(R.id.provider_key);
        tag_response = findViewById(R.id.tag_response);

        user_button_press = findViewById(R.id.user_button_press);
        user_response = findViewById(R.id.user_response);
        uservalue = findViewById(R.id.uservalue);
        userkey = findViewById(R.id.userkey);
        user_button_press = findViewById(R.id.user_button_press);
        user_response = findViewById(R.id.user_response);

        post_data = findViewById(R.id.post_data);
        post_data_button_press = findViewById(R.id.post_data_button_press);
        post_data_response = findViewById(R.id.post_data_response);

        get_data = findViewById(R.id.get_data);
        get_data_button_press = findViewById(R.id.get_data_button_press);
        get_data_response = findViewById(R.id.get_data_response);

        get_key_button_press = findViewById(R.id.get_key_button_press);
        get_key_response = findViewById(R.id.get_key_response);

        ad_available_button_press=findViewById(R.id.ad_available_button_press);
        ad_available_response=findViewById(R.id.ad_available_response);


        HashMap<String, Boolean> consentHashMap = new HashMap<>();
        consentHashMap.put("facebook", true);
        consentHashMap.put("smart", false);

        HashMap<String, Object> tag = new HashMap<>();
        tag.put("value", "10.00");
        tag.put("currency", "USD");

        HashMap<String, Boolean> provider = new HashMap<>();
        provider.put("facebook", true);
        provider.put("smart", true);

        gson = new Gson();
        consent_text.setText(gson.toJson(consentHashMap));
        tag_key.setText(gson.toJson(tag));
        provider_key.setText(gson.toJson(provider));


        initPress.setOnClickListener(v -> {
            if (toggleButton.isChecked()) {
                edgetagConfiguration.setDisableConsentCheck(true);
            } else {
                edgetagConfiguration.setDisableConsentCheck(false);
            }
            EdgeTag.INSTANCE.init(getApplication(), edgetagConfiguration, new CompletionHandler() {
                @Override
                public void onSuccess() {
                    initResponse.setText("Success");
                }

                @Override
                public void onError(int code, String msg) {
                    initResponse.setText(msg);
                }
            });
        });

        consent_button_press.setOnClickListener(v -> {
            HashMap<String, Boolean> consentData = gson.fromJson(consent_text.getText().toString(), HashMap.class);
            EdgeTag.INSTANCE.consent(consentData, new CompletionHandler() {
                @Override
                public void onSuccess() {
                    consent_response.setText("Success");
                }

                @Override
                public void onError(int code, String msg) {
                    consent_response.setText(msg);
                }
            });
        });

        provider_button_press.setOnClickListener(v -> {
            HashMap<String, Boolean> providerData = gson.fromJson(provider_key.getText().toString(), HashMap.class);
            HashMap<String, Object> tagData = gson.fromJson(tag_key.getText().toString(), HashMap.class);
            EdgeTag.INSTANCE.tag("EventName", tagData, providerData, new CompletionHandler() {
                @Override
                public void onSuccess() {
                    tag_response.setText("Success");
                }

                @Override
                public void onError(int code, String msg) {
                    tag_response.setText(msg);
                }
            });
        });

        uservalue.setText("me@domain.com");
        userkey.setText("email");

        user_button_press.setOnClickListener(v -> {
            EdgeTag.INSTANCE.user(userkey.getText().toString(),uservalue.getText().toString(), new CompletionHandler() {
                @Override
                public void onSuccess() {
                    user_response.setText("Success");
                }

                @Override
                public void onError(int code, String msg) {
                    user_response.setText(msg);
                }
            });
        });

        handlePostData();
        handlegetData();
        handlegetkey();
        handleAdAvailability();

    }

    private void handleAdAvailability() {
        ad_available_button_press.setOnClickListener(v -> {
            EdgeTag.INSTANCE.isAdvertiserIdAvailable(new OnComplete() {

                @Override
                public void onError(int code, String msg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ad_available_response.setText(msg);
                        }
                    });

                }

                @Override
                public void onSuccess(Object msg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ad_available_response.setText(msg.toString());
                        }
                    });
                }
            });
        });
    }

    private void handlePostData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", "me@abckl.ij");
        data.put("cutomInfo", "Random string entry");
        data.put("numberValue", "987");
        data.put("testBool", "false");
        gson = new Gson();
        post_data.setText(gson.toJson(data));
        post_data_button_press.setOnClickListener(v -> {
            EdgeTag.INSTANCE.postData(data, new OnComplete() {
                @Override
                public void onSuccess(@NonNull Object msg) {
                    post_data_response.setText(msg.toString());
                }

                @Override
                public void onError(int code, @NonNull String msg) {
                    post_data_response.setText(msg.toString());
                }
            });
        });
    }

    private void handlegetData() {
        ArrayList<String> key = new ArrayList<>();
        key.add("email");
        key.add("cutomInfo");
        key.add("numberValue");
        key.add("testBool");
        gson = new Gson();
        get_data.setText(gson.toJson(key));
        get_data_button_press.setOnClickListener(v -> {
            EdgeTag.INSTANCE.getData(key, new OnComplete() {
                @Override
                public void onSuccess(@NonNull Object msg) {
                    get_data_response.setText(msg.toString());
                }

                @Override
                public void onError(int code, @NonNull String msg) {
                    get_data_response.setText(msg.toString());
                }
            });
        });
    }

    private void handlegetkey() {
        get_key_button_press.setOnClickListener(v -> {
            EdgeTag.INSTANCE.getKeys(new OnComplete() {
                @Override
                public void onSuccess(@NonNull Object msg) {
                    get_key_response.setText(msg.toString());
                }

                @Override
                public void onError(int code, @NonNull String msg) {
                    get_key_response.setText(msg.toString());
                }
            });
        });
    }

}
