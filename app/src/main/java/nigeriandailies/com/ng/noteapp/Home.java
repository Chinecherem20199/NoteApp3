package nigeriandailies.com.ng.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Home extends Fragment {
    private static final String TAG = "Home";
    private ListView listViewNote;


    @Override
    public void onResume() {
        super.onResume();

        ArrayList<Note> notes = Utilities.getAllSavedNote(getActivity());


        if(notes != null || notes.size() == 0) {

            NoteAdapter na = new NoteAdapter(getActivity(), R.layout.item_note, notes);
            listViewNote.setAdapter(na);

            //set click listener for items in the list, by clicking each item the note should be loaded into NoteActivity
            listViewNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //run the NoteActivity in view/edit mode
                    String fileName = ((Note) listViewNote.getItemAtPosition(position)).getmDateTime()
                            + Utilities.FILE_EXTENSION;
                    Intent viewNoteIntent = new Intent(getActivity(), NoteActivity.class);
                    viewNoteIntent.putExtra("NOTE FILE", fileName);
//                viewNoteIntent.putExtra(Utilities.EXTRAS_NOTE_FILENAME, fileName);
                    startActivity(viewNoteIntent);
                }
            });
        } else {
            //remind user that we have no notes!
            Toast.makeText(getActivity(), "you have no saved notes!\ncreate some new notes :)"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);


        listViewNote = view.findViewById(R.id.lisView);



      return view;
   }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
