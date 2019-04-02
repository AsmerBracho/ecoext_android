package ecoext.com.ecoext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class Categories extends AppCompatActivity {

    ListView listOfCategories;
    ArrayList<String> categories = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);

        listOfCategories = findViewById(R.id.listOfCategories);
        String[] names = new String[] {"Food", "Clothes", "Transportation", "Housing", "Life & Entertainment",
                                        "Communication", "Financial Expenses"};

        categories.addAll(Arrays.asList(names));

        CategoriesAdapter cateAdapt = new CategoriesAdapter(this, categories);





    }
}
