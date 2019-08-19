package nigeriandailies.com.ng.noteapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    //    create tag for the fragment
    private  static final String TAG = "MainActivity";
    ArrayAdapter adapter ;

//    create instance of ListView
    private ListView listViewNote;

//    call the section pageAdapter for the fragment
//    then create sectionPageAdapter class and extends fragmentPageAdapter
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("NoteApp");
        setSupportActionBar(toolbar);


//        log for the fragment
        Log.d(TAG,"onCreate: Starting.");

//        declare sectionPageAdapter
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPageAdapter);


//        set viewPager
        setUpViewPager(mViewPager);
        TabLayout tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(mViewPager);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
//    create method setUpViewPager, what this method does is to create sectionViewPagerAdapter
    private void setUpViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Home(),"Home");
        adapter.addFragment(new Calender(), "Calender");

//        set the adapter to viewPager
        viewPager.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_main_new_note:

//       Start NoteActivity in newNote mode
                Intent newNoteActivity = new Intent(this, NoteActivity.class);
                startActivity(newNoteActivity);

                startActivity(new Intent(MainActivity.this, NoteActivity.class));
                break;
            // //TO show settings activity
            case  R.id.action_settings:
                break;


        }
        return super.onOptionsItemSelected(item);
    }

//    populate the listView


    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<Note> notes = Utilities.getAllSavedNote(this);



        //sort notes from new to old
        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note lhs, Note rhs) {
                if(lhs.getmDateTime() > rhs.getmDateTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

            }
        }


