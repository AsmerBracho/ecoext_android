package ecoext.com.ecoext;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CreatePurse extends AppCompatActivity {

    private String TAG = "CreatePurse";
    private TextInputLayout name;
    private TextInputLayout description;
    private TextInputLayout initialAmount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purse);

        // Init Views
        name = findViewById(R.id.text_input_layout_name);
        description = findViewById(R.id.text_input_layout_description);
        initialAmount = findViewById(R.id.text_input_layout_amount);


    }

    private boolean validateName() {
        String input = name.getEditText().getText().toString().trim();
        if (input.isEmpty()) {
            name.setError("Name can't be empty");
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

    private boolean validateAmount() {
        String input = initialAmount.getEditText().getText().toString().trim();
        if (input.isEmpty()) {
            initialAmount.setError("amount can't be empty");
            return false;
        } else {
            initialAmount.setError(null);
            return true;
        }
    }

    public void createPurseButton(View view) {
        if (!validateName() | !validateAmount()) {
            return;
        } else {
            // get Info to send to Database
            String purseName = name.getEditText().getText().toString().trim();
            String amount = initialAmount.getEditText().getText().toString().trim();

            double initialAmount = Double.parseDouble(amount);
        }

    }
}
