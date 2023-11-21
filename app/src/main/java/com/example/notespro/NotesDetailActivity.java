package com.example.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import kotlin.OverloadResolutionByLambdaReturnType;

public class NotesDetailActivity extends AppCompatActivity {
    EditText titleET,contentET;
    ImageButton saveBtn;
    TextView pageTitle,deleteNoteBtn;
    String title,content,docId;
    boolean isEditMode = false;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        titleET = (EditText) findViewById(R.id.titleET);
        contentET = (EditText) findViewById(R.id.contentET);
        saveBtn = (ImageButton) findViewById(R.id.saveBtn);
        pageTitle = (TextView) findViewById(R.id.pageTitle);
        deleteNoteBtn = (TextView) findViewById(R.id.deleteNoteBtn);
        saveBtn.setOnClickListener(view -> saveNote());

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode=true;
        }
        titleET.setText(title);
        contentET.setText(content);
        if(isEditMode){
            pageTitle.setText("Edit your note");
            deleteNoteBtn.setVisibility(View.VISIBLE);
            deleteNoteBtn.setOnClickListener(view -> deleteNoteFromFirebase());
        }
    }



    void saveNote() {
        String title = titleET.getText().toString();
        String content = contentET.getText().toString();
        if(title==null || title.isEmpty()){
            titleET.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTimestamp(Timestamp.now());
        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        if(isEditMode){
            //update note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else{
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(isEditMode){
                        Utility.showToast(NotesDetailActivity.this,"Note edited successfully");
                    }
                    else{
                        Utility.showToast(NotesDetailActivity.this,"Note added successfully");
                    }

                    finish();
                }else{
                    Utility.showToast(NotesDetailActivity.this,"Failed while adding note");

                }
            }
        });

    }
    void deleteNoteFromFirebase() {
        DocumentReference documentReference;
            //delete note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                        Utility.showToast(NotesDetailActivity.this,"Note deleted successfully");
                    finish();
                }else{
                    Utility.showToast(NotesDetailActivity.this,"Failed while deleting note");

                }
            }
        });

    }
}