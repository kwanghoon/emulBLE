package com.h3.hrm3200;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
//import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;


public class UserSettingActivity extends Activity {
    public final static String prefName = "UserSettingPref";

    public final static String keyStored = "stored";
    private final static String keyName = "name";
//    public final static String keyBirth = "birth";
    public final static String keyBirthYear = "birth year";
    public final static String keyAge = "age";
    public final static String keyWeight = "weight";
    public final static String keyLbBoolean = "kg or lb";
    public final static String keyStride = "stride";
    public final static String keyFtinBoolean = "ftin or cm";
    public final static String keyPeriod = "period";
    public final static String keyOverwrite = "overwrite";

    public final static int BIRTH_YEAR_START = 1900; // 생년선택시 최초 연도


    private EditText nameEditText;

//    private EditText birthEditText;
    private String dataSpinner[];
    private Spinner yearSpinner;
    private String birthYear;

    private EditText weightEditText;
    private RadioGroup weightRadioGroup;
    private boolean weightUnitLb = true;
    private int weightKg;
    private int weightLb;

//    private EditText strideEditText;
    private EditText strideFtEditText;
    private EditText strideInEditText;
    private TextView ftTextView;
    private TextView inTextView;
    private RadioGroup strideRadioGroup;
    private boolean strideUnitFtin = true;
    private int strideCm;
    private int strideFtin;

    private RadioGroup periodRadioGroup;
    private int period = 10;
    private RadioGroup overwriteRadioGroup;
    private boolean overWrite;

    private ImageButton cancelButton;
    private ImageButton okButton;


    private final static String TAG = "UserSettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        nameEditText = (EditText)findViewById(R.id.editText);
//        birthEditText = (EditText)findViewById(R.id.editText2);

        weightEditText = (EditText)findViewById(R.id.editText3);
        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (weightUnitLb) {
                    weightLb = Integer.parseInt("0" + weightEditText.getText().toString());
                    weightKg = (int) Math.round(weightLb * 0.45359237);
                }
                else {
                    weightKg = Integer.parseInt("0" + weightEditText.getText().toString());
                    weightLb = (int)(Math.round(weightKg * 2.20462262185));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        weightRadioGroup = (RadioGroup)findViewById(R.id.radioGroupWeight);
        weightRadioGroup.setOnCheckedChangeListener(radioCheckListener);

//        strideEditText = (EditText)findViewById(R.id.editText4);
        strideFtEditText = (EditText)findViewById(R.id.editTextFit);
        strideFtEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        strideInEditText = (EditText)findViewById(R.id.editTextInch);
        strideInEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ftTextView = (TextView)findViewById(R.id.textViewFt);
        inTextView = (TextView)findViewById(R.id.textViewIn);
        strideRadioGroup = (RadioGroup)findViewById(R.id.radioGroupStride);
        strideRadioGroup.setOnCheckedChangeListener(radioCheckListener);

        periodRadioGroup = (RadioGroup)findViewById(R.id.radiogroupPeriod);
        periodRadioGroup.setOnCheckedChangeListener(radioCheckListener);

        overwriteRadioGroup = (RadioGroup)findViewById(R.id.radioGroupOverwrite);
        overwriteRadioGroup.setOnCheckedChangeListener(radioCheckListener);

        cancelButton = (ImageButton)findViewById(R.id.imageButton5);
        okButton = (ImageButton)findViewById(R.id.imageButton6);

        yearSpinner = (Spinner)findViewById(R.id.spinnerBirth);

        int birth_year_end = Calendar.getInstance().get(Calendar.YEAR);
        int spinner_count = birth_year_end - BIRTH_YEAR_START;
        dataSpinner = new String[spinner_count];
        for (int i=0; i<spinner_count; i++)
            dataSpinner[i] = (BIRTH_YEAR_START+i) + "";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
        yearSpinner.setSelection(2003-BIRTH_YEAR_START); //2003 초기화

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthYear = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    RadioGroup.OnCheckedChangeListener radioCheckListener
            = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if (group.getId() == R.id.radiogroupPeriod) {
                switch (checkedId) {
                    case R.id.radioButton1sec:
                        period = 1;
                        break;
                    case R.id.radioButton10sec:
                        period = 10;
                        break;
                }
            }
            else if (group.getId() == R.id.radioGroupOverwrite) {

                switch (checkedId) {
                    case R.id.radioButtonOverwrite:
                        overWrite = true;
                        break;
                    case R.id.radioButtonNonOverwrite:
                        overWrite = false;
                        break;
                }
            }
            else if (group.getId() == R.id.radioGroupWeight) {

                switch (checkedId) {
                    case R.id.radioButtonKg:
                        weightUnitLb = false;

                        weightEditText.setText(weightKg + "");
                        break;
                    case R.id.radioButtonLb:
                        weightUnitLb = true;
                        weightEditText.setText(weightLb + "");
                        break;
                }
            }
            else if (group.getId() == R.id.radioGroupStride) {

                switch (checkedId) {
                    case R.id.radioButtonFtin:
                        strideUnitFtin = true;
//                        Log.i(TAG, "checkedId:radioButtonFtin strideCm : " + strideCm);
                        int tmp = Integer.parseInt("0" + strideFtEditText.getText().toString()) * 12
                                + Integer.parseInt("0" + strideInEditText.getText().toString());
                        if (strideFtin != tmp && tmp != 0) {
                            strideCm = Integer.parseInt("0" + strideInEditText.getText().toString());
                            strideFtin = (int) (Math.round(strideCm * 0.3937008));
                        }
                        ftTextView.setText("\'");
                        inTextView.setText("\"");
                        strideFtEditText.setEnabled(true);
                        strideFtEditText.setText((strideFtin / 12) + "");
                        strideInEditText.setText((strideFtin % 12) + "");
                        break;
                    case R.id.radioButtonCm:
//                        Log.i(TAG, "checkedId:radioButtonCm strideCm : " + strideCm);
                        strideUnitFtin = false;
                        tmp = Integer.parseInt("0" + strideInEditText.getText().toString());
//                        Log.i(TAG, "checkedId:radioButtonCm tmp : " + tmp);
                        if (strideCm != tmp && tmp != 0) {
                            strideFtin = Integer.parseInt("0" + strideFtEditText.getText().toString()) * 12
                                    + Integer.parseInt("0" + strideInEditText.getText().toString());
                            strideCm = (int) Math.round(strideFtin * 2.54);
                        }
                        ftTextView.setText(" ");
                        inTextView.setText("cm");
                        strideFtEditText.setEnabled(false);
                        strideFtEditText.setText("");
                        strideInEditText.setText(strideCm + "");
                        break;
                }
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = getSharedPreferences(prefName, 0);

        nameEditText.setText(pref.getString(keyName, "H3, System"));
//        birthEditText.setText(pref.getString(keyBirth,"2003.06.20"));

        yearSpinner.setSelection(Integer.parseInt(pref.getString(keyBirthYear,"2003"))-BIRTH_YEAR_START);

//        int birth = pref.getInt(keyBirth, 0);
//        birthEditText.setText(birth==0 ? "" : (birth + ""));

        weightUnitLb = pref.getBoolean(keyLbBoolean, true);
        weightKg = pref.getInt(keyWeight, 0);
        if ( weightKg == 0 ) weightKg = 70;
        weightLb = (int)(Math.round(weightKg * 2.20462262185));

        if (weightUnitLb) {
            weightRadioGroup.check(R.id.radioButtonLb);
            weightEditText.setText(weightLb + "");
        } else {
            weightRadioGroup.check(R.id.radioButtonKg);
            weightEditText.setText(weightKg + "");
        }

        strideUnitFtin = pref.getBoolean(keyFtinBoolean, true);
        strideCm = pref.getInt(keyStride, 0);
        if ( strideCm == 0 ) strideCm = 70;
        strideFtin = (int)(Math.round(strideCm * 0.3937008));

        if (strideUnitFtin) {
            strideRadioGroup.check(R.id.radioButtonFtin);
            ftTextView.setText("\'");
            inTextView.setText("\"");
            strideFtEditText.setEnabled(true);
            strideFtEditText.setText((strideFtin / 12) + "");
            strideInEditText.setText((strideFtin % 12) + "");
        } else {
            strideRadioGroup.check(R.id.radioButtonCm);
            ftTextView.setText(" ");
            inTextView.setText("cm");
            strideFtEditText.setText("");
            strideFtEditText.setEnabled(false);
            strideInEditText.setText(strideCm + "");
        }
//        strideEditText.setText(stride==0 ? "70" : (stride + ""));

        period = pref.getInt(keyPeriod, 10);
        if (period == 1) {
            periodRadioGroup.check(R.id.radioButton1sec);
        } else if (period == 10) {
            periodRadioGroup.check(R.id.radioButton10sec);
        }
        overWrite = pref.getBoolean(keyOverwrite, true);
        if (overWrite)
            overwriteRadioGroup.check(R.id.radioButtonOverwrite);
        else
            overwriteRadioGroup.check(R.id.radioButtonNonOverwrite);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(new View(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onClick(View v) {
        if (v==okButton) {
            SharedPreferences pref = getSharedPreferences(prefName, 0);
            SharedPreferences.Editor editor = pref.edit();

            editor.putBoolean(keyStored, true);
            editor.putString(keyName, nameEditText.getText().toString());
            editor.putString(keyBirthYear, birthYear);

            int age = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(birthYear);
            editor.putInt(keyAge, age);
//            Log.i(TAG, "age : " + age);

            if (weightUnitLb) {
                weightLb = Integer.parseInt("0" + weightEditText.getText().toString());
                weightKg = (int) Math.round(weightLb * 0.45359237);
            }
            else {
                weightKg = Integer.parseInt("0" + weightEditText.getText().toString());
                weightLb = (int)(Math.round(weightKg * 2.20462262185));
            }
            editor.putInt(keyWeight, weightKg);
            editor.putBoolean(keyLbBoolean, weightUnitLb);

//            editor.putInt(keyStride, Integer.parseInt("0" + strideEditText.getText().toString()));

            if (strideUnitFtin) {
                strideFtin = Integer.parseInt("0" + strideFtEditText.getText().toString()) * 12
                        + Integer.parseInt("0" + strideInEditText.getText().toString());
                strideCm = (int) Math.round(strideFtin * 2.54);
            }
            else {
                strideCm = Integer.parseInt("0" + strideInEditText.getText().toString());
                strideFtin = (int)(Math.round(strideCm * 0.3937008));
            }
            editor.putInt(keyStride, strideCm);
            editor.putBoolean(keyFtinBoolean, strideUnitFtin);

            editor.putInt(keyPeriod, period);
            editor.putBoolean(keyOverwrite, overWrite);

            editor.commit();
        }
        else if (v==cancelButton) {

        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == android.R.id.home)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                                                 .setTitle("Quit")
                                                 .setMessage("Do you want to quit?")
                                                 .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         Intent intent = new Intent();
                                                         intent.setAction(MainActivity.ACTION_FINISH_ACTIVITY);
                                                         setResult(RESULT_OK, intent);
                                                         finish();
//                                                         finishAffinity();
                                                     }
                                                 }).setNegativeButton( "No", null ).show();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
