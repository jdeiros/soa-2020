package com.unlam.dimequiensoy.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.unlam.dimequiensoy.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EndGameActivity extends AppCompatActivity {

    Button btnGoToOptionGame;
    private TextView textFinalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        btnGoToOptionGame = findViewById(R.id.btnGoToOptionGame);
        textFinalResult = findViewById(R.id.textFinalResult);
        textFinalResult.setText(getResult(savedInstanceState,getIntent()));
        btnGoToOptionGame.setOnClickListener(view -> goToOptionGame());
    }

    private String getResult(Bundle savedInstanceState, Intent intent) {
        int score = 0;
        String jsonData;
        if (savedInstanceState == null) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                jsonData = null;
            } else {
                jsonData = extras.getString("jsonData");
            }
        } else {
            jsonData = (String) savedInstanceState.getSerializable("jsonData");
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has("okay")) {
                    String valor = jsonObject.getString("okay");
                    if (valor == "true")
                        score++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  "Cantidad de personajes correctos: " + score;
    }

    private void goToOptionGame() {
        Intent intent = new Intent(EndGameActivity.this, SelectOptionGameActivity.class);
        startActivity(intent);
    }
}
