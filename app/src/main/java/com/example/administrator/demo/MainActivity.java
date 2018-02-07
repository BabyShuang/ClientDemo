package com.example.administrator.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.administrator.servicesdemo.ICallback;
import com.example.administrator.servicesdemo.INotifyService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private INotifyService mINotifyService;
    private Button mSendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定服务端的服务
        Intent intent=new Intent();
        intent.setAction("com.notify.action");
        intent.setPackage("com.example.administrator.servicesdemo");
        startService(intent);
        bindService(intent,mServiceConnection, 0);
        mSendButton=findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用服务端的方法发送信息
                Log.d(TAG, "onClick: 发送hello");
                Log.d(TAG, "onClick: mINotifyService="+mINotifyService);
                if(mINotifyService!=null){
                    try {
                        mINotifyService.sendMsg("hello");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    //当服务端处理完消息后就会回调该接口
    ICallback.Stub mCallback=new ICallback.Stub() {
        @Override
        public void notifyUpdate() throws RemoteException {
            Log.d(TAG, "notifyUpdate: 消息已经处理");

        }
    };

    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mINotifyService=INotifyService.Stub.asInterface(iBinder);
            Log.d(TAG, "onServiceConnected: mINotifyService="+mINotifyService);
            try {
                mINotifyService.registerCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
            mINotifyService=null;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mINotifyService!=null) {
            try {
                mINotifyService.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
