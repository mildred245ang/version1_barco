package com.example.dibujoprueba;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final int File = 1;
    DatabaseReference myRef;
   // Bitmap bmp;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.uploadImageView)

    ImageView mUploadImageView;
    MediaPlayer agua;
    ImageButton azul;
    ImageButton negro;
    ImageButton rojo;
    ImageButton amarillo;
    ImageButton rosa;
    Area_Dibujo lienzo;
    float ppequenyo;
    float pmediano;
    float pgrande;
    float pdefecto;
    int cont = 0;

    ImageButton trazo;
    ImageButton nuevo;
    ImageButton borrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user1");

        mUploadImageView.setOnClickListener(v -> fileUpload());
        //agua = MediaPlayer.create(this, R.raw.sardegna_1);
        negro = (ImageButton) findViewById(R.id.negro);
        amarillo = (ImageButton) findViewById(R.id.amarillo);
        rojo = (ImageButton) findViewById(R.id.rojo);
        rosa = (ImageButton) findViewById(R.id.rosa);
        azul = (ImageButton) findViewById(R.id.azul);
        trazo = (ImageButton)findViewById(R.id.trazo);
        borrar= (ImageButton)findViewById(R.id.borrar);
        nuevo = (ImageButton) findViewById(R.id.nuevo);
        mUploadImageView = (ImageView) findViewById(R.id.uploadImageView);
        lienzo = (Area_Dibujo) findViewById(R.id.lienzo2);

        negro.setOnClickListener(this::onClick);
        rosa.setOnClickListener(this::onClick);
        rojo.setOnClickListener(this::onClick);
        amarillo.setOnClickListener(this::onClick);
        azul.setOnClickListener(this::onClick);
        trazo.setOnClickListener(this::onClick);
        nuevo.setOnClickListener(this::onClick);
        mUploadImageView.setOnClickListener(this::onClick);

        lienzo = (Area_Dibujo) findViewById(R.id.lienzo2);
        ppequenyo = 10;
        pmediano = 20;
        pgrande = 30;

        pdefecto = pmediano;

    }

    public void onClick(View v) {
        String color = null;


        switch (v.getId()) {
            case R.id.azul:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.negro:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.rojo:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.amarillo:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.rosa:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;

            case R.id.trazo:
                final Dialog tamano_punto= new Dialog(this);
                tamano_punto.setTitle("Tamaño del punto:");
                tamano_punto.setContentView(R.layout.tamano_punto);
                //listen for clicks on tamaños de los botones
                TextView smallBtn = (TextView)tamano_punto.findViewById(R.id.tpequenyo);
                smallBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        lienzo.setBorrado(false);
                        lienzo.setTamanyoPunto(ppequenyo);

                        tamano_punto.dismiss();
                    }
                });
                TextView mediumBtn = (TextView)tamano_punto.findViewById(R.id.tmediano);
                mediumBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        lienzo.setBorrado(false);
                        lienzo.setTamanyoPunto(pmediano);

                        tamano_punto.dismiss();
                    }
                });
                TextView largeBtn = (TextView)tamano_punto.findViewById(R.id.tgrande);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lienzo.setBorrado(false);
                        lienzo.setTamanyoPunto(pgrande);

                        tamano_punto.dismiss();
                    }
                });
                //show and wait for user interaction
                tamano_punto.show();
            case R.id.nuevo:
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("Nuevo Dibujo");
                newDialog.setMessage("¿Comenzar nuevo dibujo (perderás el dibujo actual)?");
                newDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        lienzo.NuevoDibujo();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
                break;

            case R.id.uploadImageView:
                AlertDialog.Builder salvarDibujo = new AlertDialog.Builder(this);
                salvarDibujo.setTitle("Salvar dibujo");
                salvarDibujo.setMessage("¿Salvar Dibujo a la galeria?");
                salvarDibujo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Salvar dibujo
                        lienzo.setDrawingCacheEnabled(true);
                        //attempt to save
                        cont = cont + 1;
                        String imgSaved = MediaStore.Images.Media.insertImage(
                                getContentResolver(), lienzo.getDrawingCache(), "pez" + cont , "drawing");
                        //Mensaje de todo correcto
                        if (imgSaved != null) {
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "¡Pez guardado corectamente!", Toast.LENGTH_SHORT);
                            savedToast.show();
                            fileUpload();
                        } else {
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    "¡Error! La imagen no ha podido ser salvada.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }
                        lienzo.destroyDrawingCache();
                    }
                });
                salvarDibujo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                salvarDibujo.show();

                break;
            default:

                break;
            case R.id.borrar:

                final Dialog borrarpunto = new Dialog(this);
                borrarpunto.setTitle("Tamaño de borrado:");
                borrarpunto.setContentView(R.layout.tamano_punto);
                //listen for clicks on tamaños de los botones
                TextView smallBtnBorrar = (TextView)borrarpunto.findViewById(R.id.tpequenyo);
                smallBtnBorrar.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        lienzo.setBorrado(true);
                        lienzo.setTamanyoPunto(ppequenyo);

                        borrarpunto.dismiss();
                    }
                });
                TextView mediumBtnBorrar = (TextView)borrarpunto.findViewById(R.id.tmediano);
                mediumBtnBorrar.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        lienzo.setBorrado(true);
                        lienzo.setTamanyoPunto(pmediano);

                        borrarpunto.dismiss();
                    }
                });
                TextView largeBtnBorrar = (TextView)borrarpunto.findViewById(R.id.tgrande);
                largeBtnBorrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lienzo.setBorrado(true);
                        lienzo.setTamanyoPunto(pgrande);

                        borrarpunto.dismiss();
                    }
                });
                //show and wait for user interaction
                borrarpunto.show();

        }
    }



    public void fileUpload() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        File file = new File(getFilesDir(), "imgSaved");
        startActivityForResult(intent, File);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == File) {

            if (resultCode == RESULT_OK) {

                Uri FileUri = data.getData();

                StorageReference Folder = FirebaseStorage.getInstance().getReference().child("User1");

                final StorageReference file_name = Folder.child("file" + FileUri.getLastPathSegment());


                file_name.putFile(FileUri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("link", String.valueOf(uri));
                    myRef.setValue(hashMap);

                    Log.d("Mensaje", "Se subió correctamente");

                }));

            }

        }

    }

}