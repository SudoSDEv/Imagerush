package com.example.anonymous.imagerush;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.spark.submitbutton.SubmitButton;

/**
 * Created by anonymous on 9/21/16.
 */
public class start  extends Activity{
    private int flag=0;
    private SharedPreferences high_Scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);
        high_Scores=getSharedPreferences("Score", Context.MODE_PRIVATE);



        final RadioButton easy= (RadioButton) findViewById(R.id.radioButton);
        final RadioButton intr= (RadioButton) findViewById(R.id.radioButton2);
        final RadioButton hard= (RadioButton) findViewById(R.id.radioButton3);

        FloatingActionButton fb= (FloatingActionButton) findViewById(R.id.fab);

        RadioGroup radioGroup= (RadioGroup) findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(easy.isChecked())
                    flag=1;
                if(intr.isChecked())
                    flag=1;
                if(hard.isChecked())
                    flag=1;

            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag!=0) {
                    if (easy.isChecked()) {
                        start.this.finish();
                        startActivity(new Intent(start.this, MainActivity.class).putExtra("Level", 30000).putExtra("HIGH_SCORE",high_Scores.getFloat("noob_score",0)));
                    }
                    if (intr.isChecked()) {
                        start.this.finish();
                        startActivity(new Intent(start.this, MainActivity.class).putExtra("Level", 20000).putExtra("HIGH_SCORE",high_Scores.getFloat("intr_score",0)));
                    }
                    if (hard.isChecked()) {
                        start.this.finish();
                        startActivity(new Intent(start.this, MainActivity.class).putExtra("Level", 9000).putExtra("HIGH_SCORE",high_Scores.getFloat("pro_score",0)));
                    }
                }
                else
                    Toast.makeText(getBaseContext(),"Olease select a option",Toast.LENGTH_SHORT).show();


            }
        });

        SubmitButton sb2= (SubmitButton) findViewById(R.id.how_to_play);
        sb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder=new AlertDialog.Builder(start.this);
                builder.setTitle("Instructions");
                builder.setMessage("You have to match matching image pairs,within a time provided to you according to the level. Your score are of two types: Match score and Time score, Match score is calculated by the matching pairs, and Time Score is calculated by the time within which you have completed all.[Note : Time score remains 0 if time exceeds though you may have completed some matching pairs.]" );
                builder.setIcon(android.R.drawable.btn_star_big_on);
                builder.setNeutralButton("Ok",null);
                builder.create().show();


            }
        });


    }

}
