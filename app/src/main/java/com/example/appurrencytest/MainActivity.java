package com.example.appurrencytest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.math.BigDecimal;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    public static ArrayList<String> currency_name;
    public static String reassembled_money_name, reassembled_money_coefficient;
    private EditText money_input;
    private Button button_enter_money, button_update_list;
    private TextView money_name,
            money_coefficient,
            money_info_in_case,
            money_in_case,
            money_coefficient_in_case,
            timer;
    private final String timer_value = "60";
    private ImageButton loading_value_button, convert_button;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://www.cbr-xml-daily.ru/daily_json.js";

        currency_name = new ArrayList<>();

        //Inputs
        money_input = findViewById(R.id.money_input);

        //Buttons
        button_enter_money = findViewById(R.id.button_enter_money);
        convert_button = findViewById(R.id.convert_button);
        loading_value_button = findViewById(R.id.loading_value_button);
        button_update_list = findViewById(R.id.button_update_list);
        timer = findViewById(R.id.timer);

        //Timer set value
        timer.setText(timer_value);

        //Text view
        money_info_in_case = findViewById(R.id.money_info_in_case);
        money_in_case = findViewById(R.id.money_in_case);
        money_name = findViewById(R.id.money_name);
        money_coefficient = findViewById(R.id.money_coefficient);
        money_coefficient_in_case = findViewById(R.id.money_coefficient_in_case);

        //Get currency name and money coefficient
        updateData(url);

        //Adapter
        ListView list_of_value = findViewById(R.id.list_of_value);
        list_of_value.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_single_choice, currency_name);

        // Нажатие на кнопку "Загрузить валюту".
        loading_value_button.setOnClickListener(view -> {
            updateData(url);
            updateListView(list_of_value, itemsAdapter);

        });

        //Timer
        myTimer(url, list_of_value, itemsAdapter);

        // Нажатие на кнопку "Обновить валюту".
        button_update_list.setOnClickListener(view -> {
            updateData(url);
            updateListView(list_of_value, itemsAdapter);
        });

        // Нажатие на список.
        list_of_value.setOnItemClickListener((adapterView, view, position, l) -> {
            Object text = list_of_value.getItemAtPosition(position);
            String[] text_split = text.toString().split(" - ");
            reassembled_money_name = text_split[0].trim();
            reassembled_money_coefficient = text_split[1].trim();
            money_name.setText(reassembled_money_name);
            money_coefficient.setText(reassembled_money_coefficient);
        });


        // Нажатие на кнопку "Конвертировать валюту".
        convert_button.setOnClickListener(view -> {
            boolean param_1 = !money_in_case.getText().toString().trim().equals("");
            boolean param_2 = money_name.getText().toString().equals("Выбранная валюта");

            if (param_1) {
                Toast.makeText(MainActivity.this, R.string.error_empty_case, Toast.LENGTH_LONG).show();
            }
            else if ( param_2){
                Toast.makeText(MainActivity.this, R.string.error_empty_choice, Toast.LENGTH_LONG).show();
            }
            else {
                double coefficient_in_list_double, money_in_case_double,
                        money_coefficient_in_case_double, money_coefficient_in_list_double;

                BigDecimal result;

                money_in_case_double =
                        Double.parseDouble(String.valueOf(money_in_case.getText()));
                coefficient_in_list_double =
                        Double.parseDouble(String.valueOf(money_coefficient.getText()));
                money_coefficient_in_case_double =
                        Double.parseDouble(String.valueOf(money_coefficient_in_case.getText()));
                money_coefficient_in_list_double =
                        Double.parseDouble(String.valueOf(money_coefficient.getText()));



                if (money_coefficient_in_case_double == 0) {
                    result = new BigDecimal(money_in_case_double * coefficient_in_list_double);
                }
                else {
                    result = new BigDecimal(
                            (money_coefficient_in_case_double / money_coefficient_in_list_double) * money_in_case_double);
                }
                result = result.setScale(4, BigDecimal.ROUND_DOWN);
                money_info_in_case.setText(money_name.getText());
                money_in_case.setText(result.toString());
                money_coefficient_in_case.setText(money_coefficient.getText());
            }

        });

        // Нажатие на кнопку "Ввести".
        button_enter_money.setOnClickListener(view -> {
            //подсказка
            if (money_input.getText().toString().trim().equals("")) {
                Toast.makeText(MainActivity.this, R.string.error_enter_value, Toast.LENGTH_LONG).show();
            } else {
                money_in_case.setText(money_input.getText().toString().trim());
                money_input.setText("");
                money_coefficient_in_case.setText(R.string.money_coefficient_in_case);
                // скрыть клавиатуру
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(money_input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });



    }
    public void myTimer(String url, ListView list_of_value, ArrayAdapter<String> itemsAdapter){
        //Timer
        long seconds = Long.parseLong(timer.getText().toString());
        CountDownTimer my_timer = new CountDownTimer(seconds * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(Long.toString(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                timer.setText(timer_value);
                updateData(url);
                updateListView(list_of_value, itemsAdapter);
                myTimer(url, list_of_value, itemsAdapter);
            }
        };
        my_timer.start();
    }

    public void updateData(String url){
        new GetURLData().execute(url);
    }

    public void updateListView(ListView list_of_value, ArrayAdapter<String> itemsAdapter) {
        list_of_value.setAdapter(itemsAdapter);
        money_name.setText(R.string.money_name);
        money_coefficient.setText(R.string.money_coefficient);
    }

}

