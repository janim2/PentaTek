package com.uenr.pentatek;

//all importations of libraries are done here
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class About extends AppCompatActivity {

//    initialization of variables that would be used
    private TextView penta_text, version_text, group_name_text, members_text,
    name_one_text, name_two_text, name_three_text, name_four_text, name_five_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

//        setting the actionbar title
        getSupportActionBar().setTitle("PentaTek | About");

//        initializing the font type
        Typeface breezed_cap =Typeface.createFromAsset(getAssets(),  "fonts/BreezedcapsBoldoblique-Epvj.ttf");
        Typeface quicksand_light =Typeface.createFromAsset(getAssets(),  "fonts/Quicksand-Light.ttf");
        Typeface quicksand_regular =Typeface.createFromAsset(getAssets(),  "fonts/Quicksand-Regular.ttf");

//        relating the xml widgets to the java code
        penta_text = findViewById(R.id.penta_text);
        version_text = findViewById(R.id.version_text);
        group_name_text = findViewById(R.id.group_name_text);
        members_text  = findViewById(R.id.members_text);
        name_one_text = findViewById(R.id.name_one_text);
        name_two_text = findViewById(R.id.name_two_text);
        name_three_text = findViewById(R.id.name_three_text);
        name_four_text = findViewById(R.id.name_four_text);
        name_five_text = findViewById(R.id.name_five_text);

        //setting the font style
        penta_text.setTypeface(breezed_cap);
        version_text.setTypeface(quicksand_light);
        group_name_text.setTypeface(quicksand_regular);
        members_text.setTypeface(quicksand_light);
        name_one_text.setTypeface(quicksand_regular);
        name_two_text.setTypeface(quicksand_regular);
        name_three_text.setTypeface(quicksand_regular);
        name_four_text.setTypeface(quicksand_regular);
        name_five_text.setTypeface(quicksand_regular);
    }
}
