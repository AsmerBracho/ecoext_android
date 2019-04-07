package ecoext.com.ecoext;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static ecoext.com.ecoext.R.id.categotiesLayout;


public class CreateNewRecord extends AppCompatActivity implements View.OnClickListener {

    private EditText amountField = null;
    Context context = this;

    TextView income;
    TextView outcome;
    TextView selectCateg;
    android.support.constraint.ConstraintLayout catLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_record);
        initViews();

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

        income = findViewById(R.id.income);
        outcome = findViewById(R.id.outcome);
        //selectCateg = findViewById(R.id.selectCateg);
        catLayout = findViewById(R.id.categotiesLayout);

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outcome.setBackground(context.getResources().getDrawable(R.drawable.background_gray_with_top_stroke));
                income.setBackground(context.getResources().getDrawable(R.drawable.background_graypluss_with_top_stroke));

            }
        });

        outcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                income.setBackground(context.getResources().getDrawable(R.drawable.background_gray_with_top_stroke));
                outcome.setBackground(context.getResources().getDrawable(R.drawable.background_graypluss_with_top_stroke));
            }
        });

//        selectCateg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent categories = new Intent(getApplicationContext(), Categories.class);
//                categories.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(categories);
//            }
//
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
    }

    /**
     * Init the Views for the Custom keyboard
     * provided by AvatarQing (https://stackoverflow.com/questions/21872150/android-custom-numeric-keyboard)
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
