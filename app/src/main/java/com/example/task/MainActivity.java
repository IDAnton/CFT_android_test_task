package com.example.task;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


@SuppressWarnings("SpellCheckingInspection")
public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private TextView valuteNameText, outText, updateText;
    private EditText inputText;
    private ArrayList<Valute> valutes;
    private Spinner valutesSpiner;
    private ArrayAdapter<Valute> valutesAdapter;
    private SharedPreferences.Editor editor;
    private Switch updateSwitch;

    private static SimpleDateFormat dateFormat;

    private static final String KEY_CONNECTIONS = "valutes";
    private static final String url = "https://www.cbr-xml-daily.ru/daily_json.js";
    private static final String digit_format = "#.###";

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (updateSwitch.isChecked()) loadJSONFromURL();
            handler.postDelayed(this, 5000);
        }
    };

    private void initWidgets(){
        updateSwitch = findViewById(R.id.updateSwitch);
        editor = getPreferences(MODE_PRIVATE).edit();
        inputText = findViewById(R.id.inputText);
        updateText = findViewById(R.id.textUpdateTime);
        outText = findViewById(R.id.outText);
        valuteNameText = findViewById(R.id.textView);
    }

    private void initUpdateButton(){
        Button buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadJSONFromURL();
            }
        });
    }

    private void initSpiner(){
        valutesSpiner = findViewById(R.id.spinner);
        valutesAdapter = new ArrayAdapter<Valute>(this, R.layout.spinner_item, valutes);
        valutesSpiner.setAdapter(valutesAdapter);
        valutesAdapter.setDropDownViewResource(R.layout.spinner_item);
        valutesSpiner.setOnItemSelectedListener(this);
    }

    private void initKeyboard(){
        Keyboard keyboard = (Keyboard) findViewById(R.id.keyboard);
        inputText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        inputText.setTextIsSelectable(true);
        inputText.setShowSoftInputOnFocus(false);
        inputText.setFocusable(false);
        InputConnection ic = inputText.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                convert();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        initSpiner();
        initKeyboard();
        initUpdateButton();
        getData();
        handler.post(runnable);
        valutes = new ArrayList<>();
    }

    private void  loadJSONFromURL(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener< String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject jsonValutes = object.getJSONObject("Valute");
                            Iterator<String> keys = jsonValutes.keys();
                            valutes.clear();
                            while(keys.hasNext()) {
                                String key = keys.next();
                                JSONObject jsonValute = (JSONObject) jsonValutes.get(key);
                                valutes.add(new Valute(jsonValute));
                            }
                            saveData();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        valutesAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        valutesSpiner.setSelection(pos);
        Valute valute = (Valute) parent.getItemAtPosition(pos);
        valuteNameText.setText(valute.getName());
        convert();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("spiner","nothing selected");
    }

    public void convert(){
        try {
            Valute valute = (Valute) valutesSpiner.getSelectedItem();
            String value = inputText.getText().toString();
            if (value.equals("")) {
                outText.setText("0");
                return;
            }
            double val = Double.parseDouble(value);
            DecimalFormat format = new DecimalFormat(digit_format);
            outText.setText(String.valueOf(format.format(val / valute.getValue() * valute.getNominal())));
        }catch(NumberFormatException e) {
            Log.d("keyboard", "incorrect number");
        }
    }

    public void getData(){
        String json = getPreferences(MODE_PRIVATE).getString(KEY_CONNECTIONS, null);
        String time = getPreferences(MODE_PRIVATE).getString("time", null);
        if (json == null) {
            loadJSONFromURL();
            return;
        }
        Type type = new TypeToken < List < Valute >> () {}.getType();
        valutes = new Gson().fromJson(json, type);
        updateText.setText(time);
    }

    public void saveData(){
        String s = new Gson().toJson(valutes);
        editor.putString(KEY_CONNECTIONS, s);
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time =  new Gson().toJson(dateFormat.format(Calendar.getInstance().getTime()));
        updateText.setText(time);
        editor.putString("time", time);
        editor.commit();
    }
}

