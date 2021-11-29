package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import SeparatePackage.aidlInterface;

public class MainActivity extends AppCompatActivity{
    private EditText val1;
    private EditText val2;
    private TextView result;
    private Button button_add;

    private aidlInterface aidlObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initialize UI
        result = findViewById(R.id.result);
        button_add = findViewById(R.id.button_add);
        val1 = findViewById(R.id.val1);
        val2 = findViewById(R.id.val2);




        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value1 = Integer.parseInt(val1.getText().toString());
                int value2 = Integer.parseInt(val2.getText().toString());
                //int resultofcalc = performCalculation(value1,value2);


                try {
                    int resultOfCalc = aidlObject.calculateData(value1,value2);
                    result.setText(Integer.toString(resultOfCalc));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
            //moving this calculation logic to service app
//            private int performCalculation(int value1, int value2) {
//                return value1+value2;
//            }
        });

        bindToAIDLService();
    }

    private void bindToAIDLService() {
        Intent aidlServiceIntent = new Intent("connect_to_aidl_service");

        bindService(implicitIntentToExplicitIntent(aidlServiceIntent,this),serviceConnectionObject,BIND_AUTO_CREATE);
    }
    ServiceConnection serviceConnectionObject =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            aidlObject = aidlInterface.Stub.asInterface(iBinder);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


//converting implicit intent ot explicit intent
    public Intent implicitIntentToExplicitIntent(Intent implicitIntent, Context context){
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentServices(implicitIntent,0);
        if(resolveInfoList == null || resolveInfoList.size()!=1){
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName,serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }




}