package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String idUser;
    private FirebaseFirestore db;

    private ListView listViewTareas;
    ArrayAdapter<String> adapterTareas;

    List<String> listaTareas = new ArrayList<>();
    List<String> listaIdTareas = new ArrayList<>();
    boolean sonido = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        listViewTareas = findViewById(R.id.listaTareas);

        actualizarUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.mas:
                //activar el cuadro de dialogo para añadir tarea:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Nueva Tarea")
                        .setMessage("¿Qué quieres hacer a continuación?")
                        .setView(taskEditText)
                        .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Anadir tarea a la base de datos y al listView
                                String miTarea = taskEditText.getText().toString();
                                //Add a new document with a generated id
                                Map<String, Object> data = new HashMap<>();
                                data.put("nombreTarea",miTarea);
                                data.put("usuario",idUser);
                                db.collection("Tareas")
                                        .add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                if(sonido) {
                                                    MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.pencil_check);
                                                    mediaPlayer.start();
                                                }
                                                Toast.makeText(MainActivity.this,"Tarea creada",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if(sonido) {
                                                    MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.audio_error);
                                                    mediaPlayer.start();
                                                }
                                                Toast.makeText(MainActivity.this,"No se pudo crear la tarea",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("Cancelar",null)
                        .create();
                dialog.show();
                return true;
            case R.id.logout:
                //Cierre de sesión en FireBase
                mAuth.signOut();
                Intent logout = new Intent(this, Login.class);
                startActivity(logout);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void actualizarUI(){
        db.collection("Tareas")
                .whereEqualTo("usuario", idUser)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        listaTareas.clear();
                        listaIdTareas.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            listaTareas.add(doc.getString("nombreTarea"));
                            listaIdTareas.add(doc.getId());
                        }
                        //rellenar el listView con el adapter
                        if(listaTareas.size() == 0){
                            listViewTareas.setAdapter(null);
                        }else{
                            adapterTareas = new ArrayAdapter<>(MainActivity.this, R.layout.item_tarea, R.id.textViewTarea,listaTareas);
                            listViewTareas.setAdapter(adapterTareas);
                        }
                    }
                });
    }
    public void borrarTarea(View view){
        View parent = (View) view.getParent();
        TextView tareaTextView = parent.findViewById(R.id.textViewTarea);
        String tarea = tareaTextView.getText().toString();
        int posicion = listaTareas.indexOf(tarea);
        if(sonido) {
            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.check);
            mediaPlayer.start();
        }
        db.collection("Tareas").document(listaIdTareas.get(posicion)).delete();
    }
    public void modificarTarea(View view){
        View parent = (View) view.getParent();
        TextView tareaTextView = parent.findViewById(R.id.textViewTarea);
        String tarea = tareaTextView.getText().toString();
        int posicion = listaTareas.indexOf(tarea);

        // Obtén la tarea actual
        String tareaActual = listaTareas.get(posicion);

        // Crea el EditText y establece el texto con la tarea actual
        final EditText taskEditText = new EditText(this);
        taskEditText.setText(tareaActual);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Modificar Tarea")
                .setMessage("¿Como quieres modificar la tarea?")
                .setView(taskEditText)
                .setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Anadir tarea a la base de datos y al listView
                        String miTarea = taskEditText.getText().toString();
                        //Add a new document with a generated id
                        Map<String, Object> data = new HashMap<>();
                        data.put("nombreTarea", miTarea);
                        data.put("usuario", idUser);

                        // Actualiza el documento existente en lugar de añadir uno nuevo
                        db.collection("Tareas").document(listaIdTareas.get(posicion))
                                .update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (sonido) {
                                            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.pencil_check);
                                            mediaPlayer.start();
                                        }
                                        Toast.makeText(MainActivity.this, "Tarea modificada", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (sonido) {
                                            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.audio_error);
                                            mediaPlayer.start();
                                        }
                                        Toast.makeText(MainActivity.this, "Fallo al modificar la tarea", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .setNegativeButton("Cancelar",null)
                .create();
        dialog.show();
    }
    public void cambiarSonido(MenuItem item) {
        if(sonido){
            sonido = false;
            item.setIcon(R.drawable.sin_sonido);
        }else{
            sonido = true;
            item.setIcon(R.drawable.con_sonido);
        }
    }
}