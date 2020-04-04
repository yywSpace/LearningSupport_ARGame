package com.example.learningsupport_argame;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.Client.ClientLab;
import com.example.learningsupport_argame.Client.OnReceiveMessageList;
import com.example.learningsupport_argame.Client.UDPClient;
import com.example.learningsupport_argame.Navi.Activity.SendLocationExample;
import com.unity3d.player.UnityPlayerOperationActivity;

import java.io.IOException;


public class TestActivity extends AppCompatActivity {
    private String TAG = "TestActivity";
    Button mChatButton;
    Button mSquareButton;
    Button mClubButton;
    SendLocationExample mSendLocationExample1;
    SendLocationExample mSendLocationExample2;
    SendLocationExample mSendLocationExample3;
    UDPClient mUDPClient;
    private TextView mTextView1, mTextView2, mTextView3, mTextViewMessageList;
    private EditText mEditText1, mEditText2, mEditText3;
    private Button mButton1, mButton2, mButton3, mButtonMessageLst, mButtonSendToOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUnity();

        // 模拟多个用户
        try {
            mSendLocationExample1 = new SendLocationExample("MapWayExample/way1.txt", this, "Spider");
            mSendLocationExample2 = new SendLocationExample("MapWayExample/way2.txt", this, "郭小磊");
            mSendLocationExample3 = new SendLocationExample("MapWayExample/way3.txt", this, "刘根");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTextView1 = findViewById(R.id.name1);
        mTextView1.setText("王烁");
        mTextView2 = findViewById(R.id.name2);
        mTextView2.setText("郭小磊");
        mTextView3 = findViewById(R.id.name3);
        mTextView3.setText("刘根");

        mTextViewMessageList = findViewById(R.id.message_list);
        mEditText1 = findViewById(R.id.edit_text_1);
        mEditText2 = findViewById(R.id.edit_text_2);
        mEditText3 = findViewById(R.id.edit_text_3);
        mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);
        mButton3 = findViewById(R.id.button_3);
        mButtonMessageLst = findViewById(R.id.button_message_list);
        mButtonSendToOther = findViewById(R.id.button_send_to_other);
        mButton1.setOnClickListener(v -> {
            UDPClient udpClient = mSendLocationExample1.getUDPClient();
            new Thread(() -> {
                udpClient.ChatToUser("yywSpace", mEditText1.getText().toString());
            }).start();
        });
        mButton2.setOnClickListener(v -> {
            UDPClient udpClient = mSendLocationExample2.getUDPClient();
            new Thread(() -> {
                udpClient.ChatToUser("yywSpace", mEditText2.getText().toString());
            }).start();
        });
        mButton3.setOnClickListener(v -> {
            UDPClient udpClient = mSendLocationExample3.getUDPClient();
            new Thread(() -> {
                udpClient.ChatToUser("yywSpace", mEditText3.getText().toString());
            }).start();
        });

        mUDPClient = ClientLab.getInstance(this, ClientLab.sPort, ClientLab.sIp, ClientLab.sUserName);

        mUDPClient.setOnReceiveMessageList(messageList -> {
            runOnUiThread(() -> {
                mTextViewMessageList.setText(messageList);
            });
        });
        mButtonMessageLst.setOnClickListener(v -> {
            new Thread(() -> {
                mUDPClient.MessageList("王烁");
            }).start();
        });
        mButtonSendToOther.setOnClickListener(v -> {
            new Thread(() -> {
                mUDPClient.ChatToUser("王烁", "hello");
            }).start();
        });


        Button button = findViewById(R.id.search_test);
        button.setOnClickListener(v -> onSearchRequested());


    }

    void initUnity() {
        mChatButton = findViewById(R.id.call_chatroom_button);
        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, MainActivity.class);
                intent.putExtra("scene", "chat_room");
                startActivity(intent);
            }
        });

        mSquareButton = findViewById(R.id.call_square_button);
        mSquareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, MainActivity.class);
                intent.putExtra("scene", "square");
                startActivity(intent);
            }
        });

        mClubButton = findViewById(R.id.call_club_button);
        mClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, MainActivity.class);
                intent.putExtra("scene", "club");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时注销用户, 此处有可能bug，mMapView.onDestroy()调用后其后语句无法执行
        // 所以就不掉用了
//        mSendLocationExample1.sendLocationThread.interrupt();
//        mSendLocationExample2.sendLocationThread.interrupt();
//        mSendLocationExample3.sendLocationThread.interrupt();
//        new Thread(() -> mUDPClient.Logout()).start();
    }
}
