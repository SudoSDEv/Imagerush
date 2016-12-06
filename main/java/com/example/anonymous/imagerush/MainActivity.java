package com.example.anonymous.imagerush;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pierry.simpletoast.SimpleToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TypedArray typedArray;// holds the drawables for the images.
    private Button im=null;
    static String[] store=new String[2];// holds 2 successive string representation of clicked images
    private float points=0; // holds score for each matchinf pair.

    private int remainingCards; // counts the number of cards yet to be discovered.
    HashMap<String, Integer> tags=new HashMap<>();
    /*
    hashMap "tags" used to keep a record of images corresponding to each button
    */
    HashMap<String,Button> buttons=new HashMap<>();//
    /*
    hashMap "buttons" used to hold
    views of all  buttons ( image buttons ).
    */

    ArrayList<Integer> placements=new ArrayList<>();
    /*
    ArrayList "placements" used to shuffle
    images for buttons for each game launch or restart.
    */

    final ArrayList<Integer> successors=new ArrayList<>();
    /*
        Used for holding two successive clicked view resource ids.
    */
    Thread thread=null; //temporary placeholder for using threads within application.

    Thread flip=null;
    /*
     thread used for showing the images for each button for 0.2 seconds to give a flipping effect
     */

    private int pos_1st_clicked; //
    static int time;//used for setting the time for each of 3 levels : Noob, Intermediate , Hard
    private float finalscore;// holds the final score with respect to time.


    Animation animation=null;// animates each buttons
    private Chronometer chronometer;//counts time
    View.OnClickListener onClickListenerStart; // reference to a OnClickListener during reset or game launch
    View.OnClickListener onClickDo;

    SharedPreferences get_score; // for storing and retrieving high scores
    private float high_Score; // stores the high score of current_level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chronometer= (Chronometer) findViewById(R.id.chronometer2);
        animation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake);


        Bundle b=getIntent().getExtras();
        time=b.getInt("Level"); /////////// sets Level of game

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        typedArray=getResources().obtainTypedArray(R.array.Image_ids);

        initializePlacements(placements,typedArray);



        buttons.put("ImageView0",(Button) findViewById(R.id.button0));
        buttons.put("ImageView1",(Button) findViewById(R.id.button1));
        buttons.put("ImageView2",(Button) findViewById(R.id.button2));
        buttons.put("ImageView3",(Button) findViewById(R.id.button3));
        buttons.put("ImageView4",(Button) findViewById(R.id.button4));
        buttons.put("ImageView5",(Button) findViewById(R.id.button5));
        buttons.put("ImageView6",(Button) findViewById(R.id.button6));
        buttons.put("ImageView7",(Button) findViewById(R.id.button7));
        buttons.put("ImageView8",(Button) findViewById(R.id.button8));
        buttons.put("ImageView9",(Button) findViewById(R.id.button9));
        buttons.put("ImageView10",(Button) findViewById(R.id.button10));
        buttons.put("ImageView11",(Button) findViewById(R.id.button11));

        thread=getThread();

        onClickListenerStart=new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TextView tv= (TextView) findViewById(R.id.textView2);
                tv.setText(" ");
                Collections.shuffle(placements);

                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());

                tags.clear();

                points=0;

                Button im;

                if(thread!=null && thread.isAlive()) {
                    thread.interrupt();
                }

                if(flip!=null && flip.isAlive())
                    flip.interrupt();

                remainingCards=buttons.size();

                 for(int i=0;i<buttons.size();i++)
                 {
                     im=buttons.get("ImageView"+i);
                     im.setBackgroundResource(placements.get(i));
                     im.setTag(placements.get(i));
                 }

                thread=getThread();
                thread.start();

            }


        };

        onClickDo=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.button0:
                            doWork(view,0);
                            break;
                        case  R.id.button1:
                            doWork(view,1);
                            break;
                        case  R.id.button2:
                            doWork(view,2);
                            break;
                        case  R.id.button3:
                            doWork(view,3);
                            break;
                        case  R.id.button4:
                            doWork(view,4);
                            break;
                        case  R.id.button5:
                            doWork(view,5);
                            break;
                        case  R.id.button6:
                            doWork(view,6);
                            break;
                        case  R.id.button7:
                            doWork(view,7);
                            break;
                        case  R.id.button8:
                            doWork(view,8);
                            break;
                        case  R.id.button9:
                            doWork(view,9);
                            break;
                        case  R.id.button10:
                            doWork(view,10);
                            break;
                        case  R.id.button11:
                            doWork(view,11);
                            break;
                    }
            }
        };


        Button b1=(Button)findViewById(R.id.button); // invisible button to start the game ( considered for ease for maintenance )
        b1.setOnClickListener(onClickListenerStart);
        b1.performClick();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void doWork(View view,int pos)// caller function for the entire process for a button click.
    {
        Button b= (Button) findViewById(view.getId());
        b.startAnimation(animation);


        if (successors.size() == 0) {
            successors.add(view.getId());

        }
        else
        {
            if(successors.get(0)==view.getId()) {
                Toast.makeText(getBaseContext(), "You pressed same object.", Toast.LENGTH_SHORT).show();
                successors.clear();
                store=new String[2];
                return;
            }
            else successors.clear();
        }

        if(tags.get(view.getTag().toString())!=null && tags.get(view.getTag().toString())==2) {
            Toast.makeText(getBaseContext(), "Already discovered!", Toast.LENGTH_SHORT).show();
            store=new String[2];

        }
        else {

            updateGame(view.getTag().toString(), pos);
        }


    }

    private Thread show(int pos) { // used to display images during gameplay for 0.2 sec.

        final Button b= buttons.get("ImageView"+pos);
            b.setBackgroundResource(placements.get(pos));

            Thread r = new Thread(new Runnable() {
                @Override
                public void run() {
                    Handler h = new Handler(Looper.getMainLooper());
                    try {

                        Thread.sleep(200);
                        h.post(new Runnable() {
                            @Override
                            public void run() {

                                b.setBackgroundResource(R.drawable.grey);

                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }

            });


            return r;
    }


    private void initializePlacements(ArrayList<Integer> placements, TypedArray typedArray) {// method for initializing placements ArrayList
            for(int i=0;i<typedArray.length();i++)
            {
                placements.add(typedArray.getResourceId(i,-1));
                placements.add(typedArray.getResourceId(i,-1));

            }

    }

    private void updateGame(String s,int pos) {// helper method for doWork() to update game for a click.


        TextView textView= (TextView) findViewById(R.id.textView2);
        if(remainingCards!=0) {
            if (store[0]==null) {

                store[0] = s;
                pos_1st_clicked=pos;

                flip=show(pos);
                flip.start();


            } else {
                store[1] = s;
                if (store[0].equals(store[1])) {

                    points += 100.0/6;
                    if(points>=99.99) {
                        points = Math.round(points);
                        disable_execution();
                    }

                    if(flip!=null && flip.isAlive())
                        flip.interrupt();

                    Button b=buttons.get("ImageView"+pos_1st_clicked);
                    b.setBackgroundResource(placements.get(pos_1st_clicked));
                    b=buttons.get("ImageView"+pos);
                    b.setBackgroundResource(placements.get(pos));

                    pos_1st_clicked=-1;

                    remainingCards-=2;

                    textView.setText("Match score : "+(int)points);
                    tags.put(s,2); // sets the value of a image id for the corresponding view to 2 i.e no more to be discovered of same type.

                } else {
                    flip=show(pos);
                    flip.start();


                }
                store = new String[2];


            }

        }
        else
            Toast.makeText(getBaseContext(),"Game Over!",Toast.LENGTH_LONG).show();


    }

    private void disable_execution() {

        for(HashMap.Entry<String,Button> e:buttons.entrySet())
        {
            e.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleToast.ok(MainActivity.this, "Restart if you want to play again", "{fa-check-square}");
                }
            });
        }
    }

    private void resetImageViews() { // helper method for getThread() to hide all images.

        for(int i=0;i<buttons.size();i++)
        {
            im=buttons.get("ImageView"+i);
            im.setClickable(true);
            im.setOnClickListener(onClickDo);
            Button button=buttons.get("ImageView"+i);
            button.setBackgroundResource(R.drawable.grey);
        }

    }

    private Thread getThread()// return the thread to display all the images during game launch
    {
        final Handler h=new Handler();
        final TextView tv= (TextView) findViewById(R.id.textView2);

        tv.setText("Startup time : ");
        final Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                int c = 12;
                try {
                    for (Map.Entry<String, Button> e : buttons.entrySet()) {
                        e.getValue().setClickable(false);
                    }

                    while (c >= 0) {

                            Thread.sleep(1000);


                        final int finalC = c;

                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                if (finalC <= 5)
                                    tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                tv.setText("Startup time: " + finalC + "");
                            }
                        });
                        c--;
                    }

                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            resetImageViews();// used to hide the images for each button.
                            tv.setText("Match Score : 0");
                            tv.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                            startTimer();

                        }
                    });
                }
                catch (InterruptedException e){}
            }
        });

    return thread;
    }

    private void startTimer() { // sets the timer for gameplay.

                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    float remTime;

                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        remTime=SystemClock.elapsedRealtime()-chronometer.getBase();
                        if(remTime>=time) {
                            SimpleToast.info(getApplicationContext(),"Time Up!");
                            chronometer.stop();

                            final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                            builder.setIcon(android.R.drawable.ic_menu_send);
                            finalscore = 0;
                            builder.setTitle("Time Score : " + finalscore );
                            builder.setMessage("Sorry you have not completed within time. Do you want to restart? ");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MainActivity.this.finish();
                                    Log.d("hs",high_Score+"");

                                    startActivity(new Intent(MainActivity.this,MainActivity.class).putExtra("Level",time).putExtra("HIGH_SCORE",high_Score));
                                }
                            });
                            builder.setNegativeButton("No", null);
                            disable_execution();

                            builder.create().show();


                        }
                        else if(points==100.0)
                        {
                            points=0;

                            chronometer.stop();

                            final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                            builder.setIcon(android.R.drawable.ic_menu_send);
                            finalscore= (time-remTime)/1000;
                            if(finalscore>high_Score)
                            {
                                high_Score=finalscore;
                                write_data(high_Score);
                                SimpleToast.info(getBaseContext(),"Congrats! High Score : "+high_Score,"{fa-check-square}");
                            }
                            builder.setTitle("Time Score : " + finalscore );
                            builder.setMessage("Well done!");
                            builder.setMessage("Do you want to restart?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MainActivity.this.finish();
                                    startActivity(new Intent(MainActivity.this,MainActivity.class).putExtra("Level",time).putExtra("HIGH_SCORE",high_Score));
                                }
                            });
                            builder.setNegativeButton("No", null);
                            disable_execution();

                            builder.create().show();
                        }
                    }
                });
                chronometer.start();

    }

    private void write_data(float high_score) {
        SharedPreferences.Editor editor = get_score.edit();

        if(time==30000) {
            editor.putFloat("noob_score", high_score);
        }
        else if(time==20000)
        {
            editor.putFloat("intr_score", high_score);
        }
        else editor.putFloat("pro_score",high_score);
        editor.apply();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            MainActivity.this.finish();
            startActivity(new Intent(MainActivity.this,start.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.how_to_play) {

            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Instructions");
            builder.setNeutralButton("Ok",null);
            builder.setMessage("You have to match matching image pairs,within a time provided to you according to the level. Your score are of two types: Match score and Time score, Match score is calculated by the matching pairs, and Time Score is calculated by the time within which you have completed all.[Note : Time score remains 0 if time exceeds though you may have completed some matching pairs.]" );
            builder.setIcon(android.R.drawable.btn_star_big_on);
            builder.create().show();

        }

        if(id==R.id.about)
        {
            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setTitle("About");
            builder.setMessage("This app is developed by Sayan Dutta.I love to innovate \"Simple\"\n                 ~Contact~\nsayan.dutta4848@gmail.com");
            builder.setIcon(android.R.drawable.ic_menu_crop);
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Mainmenu) {

            MainActivity.this.finish();
            startActivity(new Intent(MainActivity.this,start.class));

        } else if (id == R.id.Reset) {

            Button b= (Button) findViewById(R.id.button);
            b.performClick();

        } else if (id == R.id.Challenge) {

                        try {
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_EMAIL, new String[]{});
                            email.putExtra(Intent.EXTRA_SUBJECT, "~ Challenge for ImageRush ~");
                            email.putExtra(Intent.EXTRA_TEXT, "Hi,can you beat my score of " + high_Score + " in ImageRush? Download link: www.google.com");

                            email.setType("message/rfc822");

                            startActivity(Intent.createChooser(email, "Choose an Email client :"));
                        }
                        catch (Exception e){
                            Log.d("Error :",e.toString() );
                        }


        } else if (id == R.id.Exit) {

            this.finish();
        }
        else if(id==R.id.HighScore){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.star_big_on);
            builder.setTitle("All time best:");
            if(time==30000)
            builder.setMessage("Hi beginner! Your all time best is : "+high_Score);
            else if(time==20000)
            builder.setMessage("Hi inter! Your all time best is : "+high_Score);
            else
                builder.setMessage("Hi pro! Your all time best is : "+ high_Score);
            builder.setNeutralButton("Ok", null);
            builder.create().show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        get_score=getSharedPreferences("Score", Context.MODE_PRIVATE);
        high_Score=getIntent().getExtras().getFloat("HIGH_SCORE");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        typedArray.recycle();
    }

}
