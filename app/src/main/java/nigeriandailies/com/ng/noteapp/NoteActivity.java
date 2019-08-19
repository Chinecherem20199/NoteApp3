package nigeriandailies.com.ng.noteapp;

import android.app.Notification;
import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.zip.Inflater;

public class NoteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    //state of the activity
    Calendar calendar1 = Calendar.getInstance();

    TextClock test;
    TextView btn;
    TimePickerDialog timeDialog;
    DatePickerDialog dateDialog;
    private boolean mIsViewingOrUpdating;
    private long mNoteCreationTime;
    private EditText mEtTitle;
    private  EditText mEtContent;

    private String mNoteFileName;
    private  Note mLoadedNote;
    private String originalNote;
    //
//    private  Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mEtTitle = findViewById(R.id.note_title_edit);
        mEtContent = findViewById(R.id.note_content);
        test = findViewById(R.id.alarm);
        btn = findViewById(R.id.notifycancel);

        originalNote();


        dateDialog =DatePickerDialog.newInstance(
                NoteActivity.this,
                calendar1.get(Calendar.YEAR),
                calendar1.get(Calendar.MONTH),
                calendar1.get(Calendar.DAY_OF_MONTH)
        );

        //        initialize the timepickerdialog with the current time
        timeDialog = TimePickerDialog.newInstance(
                NoteActivity.this,
                calendar1.get(Calendar.HOUR_OF_DAY),
                calendar1.get(Calendar.MINUTE),
                calendar1.get(Calendar.SECOND),
                false
        );


        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog.show(getFragmentManager(),"datePicker");


            }
        });

        mNoteFileName = getIntent().getStringExtra("NOTE FILE");

//       && mNoteFileName.endsWith(Utilities.EXTRAS_NOTE_FILENAME
        //check if view/edit note bundle is set, otherwise user wants to create new note
        if (mNoteFileName != null && !mNoteFileName.isEmpty()) {
            mLoadedNote = Utilities.getNoteByName(this, mNoteFileName);

            if (mLoadedNote != null){

                //update the widgets from the loaded note
                mEtTitle.setText(mLoadedNote.getmTitle());
                mEtContent.setText(mLoadedNote.getmContent());

            }

        }
    }
//    Can be updated

    private void originalNote() {
        if (mEtTitle.getText().toString().isEmpty() || mEtContent.getText().toString().isEmpty())
            return;
        originalNote = mLoadedNote.getmDateTime().toString();
        originalNote = mEtTitle.getText().toString();
        originalNote = mEtContent.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//         instantiate menu XML files into Menu objects
        //load menu based on the state we are in (new, view/update/delete)
        if(mIsViewingOrUpdating) { //user is viewing or updating a note
            getMenuInflater().inflate(R.menu.menu_note_new, menu);
        } else { //user wants to create a new note
            getMenuInflater().inflate(R.menu.menu_note_add, menu);
        }

     return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case  R.id.action_note_save:
            case R.id.action_update_note:
//                call the function save note
                saveNote();
                break;

            case R.id.action_note_delete:
                deleteNote();
                break;
            case R.id.action_cancel: //cancel the note
                actionCancel();
                break;
            case R.id.action_send_email:
                sendEmail();
                break;
        }

        return true;
    }

    private void sendEmail() {
        String subject = mEtTitle.getText().toString();
        String content = mEtContent.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(intent);
    }

    /**
     * Back button press is same as cancel action...so should be handled in the same manner!
     */
    @Override
    public void onBackPressed() {
        actionCancel();
    }

    private void actionCancel() {
        if(checkNoteAltred()) { //if note is not altered by user (user only viewed the note/or did not write anything)
            finish(); //just exit the activity and go back to MainActivity

        } else { //we want to remind user to decide about saving the changes or not, by showing a dialog
            AlertDialog.Builder dialogCancel = new AlertDialog.Builder(this)
                    .setTitle("discard changes...")
                    .setMessage("are you sure you  want to discard changes made to this note?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish(); //just go back to main activity
                        }
                    })
                    .setNegativeButton("NO", null); //null = stay in the activity!
            dialogCancel.show();
        }
    }

    private boolean checkNoteAltred() {
        //if in view/update mode
        if(mIsViewingOrUpdating) {
            checkPreviousNoteValue();
            return mLoadedNote != null && (!mEtTitle.getText().toString().equalsIgnoreCase(mLoadedNote.getmTitle())
                    || !mEtContent.getText().toString().equalsIgnoreCase(mLoadedNote.getmContent()) );

        }
        //if in new note mode
        else {
            return !mEtTitle.getText().toString().isEmpty() || !mEtContent.getText().toString().isEmpty();
        }
    }

    private void checkPreviousNoteValue() {
        mLoadedNote = Utilities.getNoteByName(this, mNoteFileName);
        if (mNoteFileName ==originalNote){
            finish();
        }

    }


    //     create a function to save note
    private void saveNote(){

        //get the content of widgets to make a note object
        String title = mEtTitle.getText().toString();
        String content = mEtContent.getText().toString();
        Note note;

        //see if user has entered anything :D lol
        if(title.isEmpty()) { //title
            Toast.makeText(NoteActivity.this, "please enter title for the note!"
                    , Toast.LENGTH_LONG).show();
            return;
        }

        if(content.isEmpty()) { //content
            Toast.makeText(NoteActivity.this, "please add your note!"
                    , Toast.LENGTH_LONG).show();
            return;
        }

        //set the creation time, if new note, now, otherwise the loaded note's creation time
        if(mLoadedNote == null) {
            note = new Note(System.currentTimeMillis(), mEtTitle.getText().toString(), mEtContent.getText().toString());
        } else {
             note = new Note(mLoadedNote.getmDateTime(), mEtTitle.getText().toString(), mEtContent.getText().toString());
        }

        //finally save the note!
        if(Utilities.saveNote(this, note)) { //success!
            //tell user the note was saved!
//            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        } else { //failed to save the note! but this should not really happen :P :D :|
            Toast.makeText(this, "can not save the note. make sure you have enough space " +
                    "on your device", Toast.LENGTH_LONG).show();
        }



        finish(); //exit the activity, should return us to MainActivity
    }



    private void deleteNote(){
        //ask user if he really wants to delete the note!
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this)
                .setTitle("delete note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mLoadedNote != null && Utilities.deleteNote(getApplicationContext(), mNoteFileName)) {
                            Toast.makeText(NoteActivity.this, mLoadedNote.getmTitle() + " is deleted"
                                    , Toast.LENGTH_LONG).show();
                        } else {
                           Toast.makeText(NoteActivity.this, "can not delete this note '" + mLoadedNote.getmTitle() +
                                            mLoadedNote.getmContent() + mLoadedNote.getmDateTime()
                                    , Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                })
                .setNegativeButton("NO", null); //do nothing on clicking NO button :P

        dialogDelete.show();


    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        calendar1.set(Calendar.YEAR,year);
        calendar1.set(Calendar.MONTH,monthOfYear);
        calendar1.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        mEtContent.getText();
        mEtTitle.getText();
        
//        tpd.getOnTimeSetListener();
        timeDialog.show(getFragmentManager(), "Timepickerdialog");


    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        calendar1.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar1.set(Calendar.MINUTE,minute);
        calendar1.set(Calendar.SECOND,second);
        mEtTitle.getText();
        mEtContent.getText();
//        ringtone.play();



        NotifyMe notifyMe = new NotifyMe.Builder(getApplicationContext())
                .small_icon(R.drawable.ic_notifications_active_black_24dp)
                .title(mEtTitle.getText().toString())
                .content(mEtContent.getText().toString())

                .color(255,0,0,255)
                .led_color(255,255,255,255)
                .time(calendar1)
                .addAction(new Intent(),"Snooze",false)
                .key("test")
                .addAction(new Intent(),"Dismiss",true,false)
                .addAction(new Intent(),"Done")
                .large_icon(R.mipmap.ic_launcher_round)
                .build();
                notifyMe.getBuilder();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_SOUND);
    }


}

