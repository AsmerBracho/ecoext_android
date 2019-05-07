package ecoext.com.ecoext.records;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ecoext.com.ecoext.AddNameAndPurse;
import ecoext.com.ecoext.R;
import ecoext.com.ecoext.general.MainActivity;

/**
 * Class CreateNewRecord
 * This is an activity that load a custom keypad to input the amount
 * and category the transaction as income or outcome
 */
public class CreateNewRecord extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateNewRecord";

    private EditText amountField = null;
    private Context context = this;

    // Views
    private TextView income; // Income Tab
    private TextView outcome; // Outcome Tab
    private android.support.constraint.ConstraintLayout catLayout;
    private ImageView nextDone; // Next Button Tab
    private TextView minusPluss; // Symbol - or +

    private boolean minus = true;

    // Array of Purses
    private ArrayList<String> purses;
    private ArrayList<Integer> purseId;

    /**
     * Method onCreate  is call every time this activity is called
     * inside we define and initialize our main variables and instances
     * this is similar as what a constructor does
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_record);
        initViews();

        // get the purse Names from previous activity
        ArrayList<String> p = getIntent().getStringArrayListExtra("purseNames");
        ArrayList<Integer> pI = getIntent().getIntegerArrayListExtra("purseId");

        // set the purses
        this.purses = new ArrayList<>(p);
        this.purseId = new ArrayList<>(pI);

        Log.d(TAG, "giveMethePurseCreateNewRecord" + purses.size());

        // get the field Cancel
        TextView cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Cancel")
                        .setMessage("Do you want to delete the changes?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(main);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        // get the views
        minusPluss = findViewById(R.id.minus_pluss);
        income = findViewById(R.id.income);
        outcome = findViewById(R.id.outcome);
        catLayout = findViewById(R.id.categotiesLayout);
        nextDone = findViewById(R.id.next_button_done);

        // Set listeners
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outcome.setBackground(context.getResources().getDrawable(R.drawable.background_gray_with_top_stroke));
                income.setBackground(context.getResources().getDrawable(R.drawable.background_graypluss_with_top_stroke));
                minusPluss.setText("+");
                minusPluss.setTextSize(100);
                income.setElevation(2);
                outcome.setElevation(4);
                minus = false;
            }
        });

        outcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                income.setBackground(context.getResources().getDrawable(R.drawable.background_gray_with_top_stroke));
                outcome.setBackground(context.getResources().getDrawable(R.drawable.background_graypluss_with_top_stroke));
                minusPluss.setText("-");
                minusPluss.setTextSize(200);
                income.setElevation(4);
                outcome.setElevation(2);
                minus = true;
            }
        });

        nextDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNameAndPurse = new Intent(getApplicationContext(), AddNameAndPurse.class);
                // Add Extras
                addNameAndPurse.putStringArrayListExtra("purseNames", purses);
                addNameAndPurse.putIntegerArrayListExtra("purseId", purseId);
                String amountToPass;
                if (minus) {
                    amountToPass = "-" + amountField.getText().toString().trim();
                } else {
                    amountToPass = amountField.getText().toString().trim();
                }
                addNameAndPurse.putExtra("amount", amountToPass);
                startActivityForResult(addNameAndPurse, 0);

            }
        });
    }

    /**
     * Method that Handle the click of the Back bottom in your android device.
     * It does not takes parameters and it is void. but it is called automatically
     * when back bottom is pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
    }

    /**
     * Init the Views for the Custom keyboard
     * provided by AvatarQing
     * (https://stackoverflow.com/questions/21872150/android-custom-numeric-keyboard)
     */
    private void initViews() {
        amountField = $(R.id.amount_field);
        $(R.id.t9_key_0).setOnClickListener(this);
        $(R.id.t9_key_1).setOnClickListener(this);
        $(R.id.t9_key_2).setOnClickListener(this);
        $(R.id.t9_key_3).setOnClickListener(this);
        $(R.id.t9_key_4).setOnClickListener(this);
        $(R.id.t9_key_5).setOnClickListener(this);
        $(R.id.t9_key_6).setOnClickListener(this);
        $(R.id.t9_key_7).setOnClickListener(this);
        $(R.id.t9_key_8).setOnClickListener(this);
        $(R.id.t9_key_9).setOnClickListener(this);
        $(R.id.t9_key_clear).setOnClickListener(this);
        $(R.id.t9_key_dot).setOnClickListener(this);
        $(R.id.t9_key_backspace).setOnClickListener(this);
    }


    /**
     * Since this Activity implements the OnClickListener interface we need to implements
     * the on Click method that handles all the different clicks
     *
     * @param v it takes a view
     */
    @Override
    public void onClick(View v) {
        // handle number button click
        if (v.getTag() != null && "number_button".equals(v.getTag())) {
            amountField.append(((TextView) v).getText());
            return;
        }
        if (v.getId() == R.id.t9_key_dot) {
            if (amountField.getText().toString().contains(".")) {
                // Do Nothing
            } else if (amountField.getText().toString() == null) {
                amountField.append("0.");
            } else {
                amountField.append(".");
            }

        }
        switch (v.getId()) {
            case R.id.t9_key_clear: { // handle clear button
                amountField.setText(null);
            }
            break;
            case R.id.t9_key_backspace: { // handle backspace button
                // delete one character
                Editable editable = amountField.getText();
                int charCount = editable.length();
                if (charCount > 0) {
                    editable.delete(charCount - 1, charCount);
                }
            }
            break;
        }
    }

    /**
     * Get the Input from the textField
     *
     * @return the amount as String
     */
    public String getInputText() {
        return amountField.getText().toString();
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }
}
