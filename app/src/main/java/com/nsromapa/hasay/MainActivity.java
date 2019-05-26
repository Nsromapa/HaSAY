package com.nsromapa.hasay;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ImageView activity_message_send;
    private TextView activity_message_text;
    private static final int REQ_CODE = 110;

    private TextToSpeech textToSpeech;
    private int speakNow = 0;

    private static Socket s;
    private static PrintWriter printWriter;

    private String ip = "192.168.43.200";
    private RecyclerView messagesRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_message_text = findViewById(R.id.activity_message_text);
        activity_message_send = findViewById(R.id.activity_message_send);


        //Setup chats into the chat recyclerView
        messagesRecycler = findViewById(R.id.activity_chat_recyclerview);
        messagesRecycler.setHasFixedSize(true);
        messagesRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        messagesRecycler.setLayoutManager(linearLayoutManager);


        activity_message_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = activity_message_text.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    openMic();
                } else {
                    sendMessage(message);
                }
            }
        });


        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsResult = textToSpeech.setLanguage(Locale.getDefault());
                    if (ttsResult == TextToSpeech.LANG_MISSING_DATA ||
                            ttsResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "Language is not Supported.", Toast.LENGTH_SHORT).show();
                    } else {

                        textToSpeech.setPitch(0.96f);
                        textToSpeech.setSpeechRate(1.0f);
                        speakNow = 1;

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Speech is unaccepted in this device...", Toast.LENGTH_SHORT).show();
                }
            }
        });


        activity_message_send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String message = activity_message_text.getText().toString();
                activity_message_text.setText("");
                speakText(message);

                return false;
            }
        });


        activity_message_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    activity_message_send.setImageResource(R.drawable.ic_keyboard_voice_24dp);
                } else {
                    activity_message_send.setImageResource(R.drawable.ic_send_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        new Thread(new MessageReceiver());

    }

    private void openMic() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk to HaSAY");

        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, "Sorry, this device does not support Speech Language", Toast.LENGTH_LONG).show();
        }
    }


    private void sendMessage(String message) {
        new MessageSender().execute(message);
    }


    private void speakText(String message) {
        if (speakNow == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            Toast.makeText(this, "Speach Error....", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    sendMessage(result.get(0));
                }
                break;
        }

    }


    class MessageSender extends AsyncTask<String, String, String> {

        private RecyclerView.Adapter adapter;
        private ArrayList<MessagesObjects> messagesObjects = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            adapter = new MessageAdapter(MainActivity.this, messagesObjects);
            messagesRecycler.setAdapter(adapter);
        }


        @Override
        protected String doInBackground(String... message) {
            try {
                s = new Socket(ip, 5000);
                printWriter = new PrintWriter(s.getOutputStream());
                printWriter.write(message[0]);
                printWriter.flush();
                printWriter.close();
                s.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return message[0];
        }


        @Override
        protected void onProgressUpdate(String... message) {
            messagesObjects.add(new MessagesObjects(String.valueOf(System.currentTimeMillis()), "user", message[0]));
//            new MessagesArrayList().setChatsObjects(chatsObjects);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String message) {
            messagesRecycler.smoothScrollToPosition(Objects.requireNonNull(messagesRecycler.getAdapter()).getItemCount());
        }
    }


    class MessageReceiver implements Runnable{

        Socket socket;
        ServerSocket serverSocket;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        String message;

        Handler handler = new Handler();
        @Override
        public void run() {
            try {

                while(true){
                    serverSocket = new ServerSocket(5000);
                    socket = serverSocket.accept();
                    inputStreamReader = new InputStreamReader(socket.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);

                    message = bufferedReader.readLine();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }


}





