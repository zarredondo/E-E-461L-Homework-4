package vn.pugs.gocoder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText addressInput;
    EditText testText;
    Button addressInputButton;
    String addressString;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Network network = new Network();

        addressInput = (EditText) findViewById(R.id.address_input);
        addressInputButton = (Button) findViewById(R.id.address_input_btn);
        testText = (EditText) findViewById(R.id.testText);

        result = (String) network.connectNetwork("4600 W Guadalupe St, Austin TX");

        testText.setText(result);

        addressInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressString = addressInput.getText().toString();
                testText.setText(result);
            }
        });
    }


}
