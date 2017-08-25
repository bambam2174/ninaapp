package de.sedatkilinc.ninaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Console;

public class MainActivity extends AppCompatActivity {

    public static String EXTRA_MESSAGE = "de.sedatkilinc.ninaapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void editTextBeginEditing(View view) {
        EditText editText = (EditText)view;
        editText.setText("");
    }

    public void sendMessage(View view) {
        Log.d("Button clicked " , ((Button)view).getText().toString());
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String text = editText.getText().toString();
        Log.d("Edit Text " , text);
        intent.putExtra(EXTRA_MESSAGE, text);
        startActivity(intent);
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
