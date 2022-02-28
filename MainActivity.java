package com.AA.textdetection;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.RecognizerResultsIntent;
import android.text.TextPaint;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView textoTraduccion;
    EditText entradaTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textoTraduccion = findViewById(R.id.traduccion);
    }
    ActivityResultLauncher<Intent> lanzadorIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK){
                        Intent info = result.getData();
                        ArrayList<String> data = info.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        //Muestra de texto sin traducir
                        //textoTraduccion.setText(data.get(0));

                        TranslatorOptions options = new TranslatorOptions.Builder()
                                .setSourceLanguage(TranslateLanguage.SPANISH)
                                .setTargetLanguage(TranslateLanguage.ENGLISH)
                                .build();
                        final Translator spanishenglish = Translation.getClient(options);

                        DownloadConditions conditions = new DownloadConditions.Builder()
                                .requireWifi()
                                .build();

                        spanishenglish.downloadModelIfNeeded(conditions)
                                .addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //Se descarga el modelo de traducción
                                                spanishenglish.translate(data.get(0))
                                                        .addOnSuccessListener(
                                                                new OnSuccessListener<String>() {
                                                                    @Override
                                                                    public void onSuccess(String s) {
                                                                        textoTraduccion.setText(s);
                                                                    }
                                                                }
                                                        ).addOnFailureListener(
                                                        new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(),"Error al traducir",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                ).addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Erro al descargar el modelo
                                        Toast.makeText(getApplicationContext(),"Error al descargar modelo",Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );

                    }
                }
            }
    );
    public void botonHablar(View v){
        entradaTexto = findViewById(R.id.texto_write);
        entradaTexto.setText("");
        Intent intentReconocimieinto = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentReconocimieinto.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"SPANISH");
        try {
            lanzadorIntent.launch((intentReconocimieinto));
        }catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),"Error al iniciar el reconocimiento de voz", Toast.LENGTH_SHORT).show();
        }
    }
    public void botonWhite(View v){
        entradaTexto = findViewById(R.id.texto_write);
        String data= String.valueOf(entradaTexto.getText());
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.SPANISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();
        final Translator spanishenglish = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        spanishenglish.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Se descarga el modelo de traducción
                                spanishenglish.translate(data)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        textoTraduccion.setText(s);
                                                    }
                                                }
                                        ).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),"Error al traducir",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                            }
                        }
                ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Erro al descargar el modelo
                        Toast.makeText(getApplicationContext(),"Error al descargar modelo",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        entradaTexto.setText("");
    }
}