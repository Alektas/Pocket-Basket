package alektas.pocketbasket.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.R;
import androidx.annotation.Nullable;

public class AddItemActivity extends Activity {
    public static final String ITEM_NAME = "ITEM_NAME";
    public static final String ITEM_CATEGORY_RES = "ITEM_CATEGORY_RES";
    public static final String ITEM_IMG_RES = "ITEM_IMG_RES";

    private Spinner mSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_add_item_layout);
        mSpinner = findViewById(R.id.categories_spinner);
        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.category_view, getTags());
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    private List<Integer> getTags() {
        List<Integer> tags = new ArrayList<>();
        tags.add(R.string.all);
        tags.add(R.string.drink);
        tags.add(R.string.fruit);
        tags.add(R.string.vegetable);
        tags.add(R.string.floury);
        tags.add(R.string.milky);
        tags.add(R.string.groats);
        tags.add(R.string.sweets);
        tags.add(R.string.meat);
        tags.add(R.string.seafood);
        tags.add(R.string.semis);
        tags.add(R.string.sauce_n_oil);
        tags.add(R.string.household);
        tags.add(R.string.other);
        return tags;
    }

    public void onItemAdd(View view) {
        EditText nameField = findViewById(R.id.add_item_field);
        String name = nameField.getText().toString();

        Intent intent = new Intent();
        intent.putExtra(ITEM_NAME, name);
        intent.putExtra(ITEM_CATEGORY_RES, (int) mSpinner.getSelectedItem());
        intent.putExtra(ITEM_IMG_RES, 0);
        setResult(RESULT_OK, intent);
        finish();
    }
}
