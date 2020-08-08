package com.example.danhba;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ContactAdapter adapter;
    SQLHelper sqlHelper;

    private List<Contact> contactList;
    private EditText edtName;
    private EditText edtNumber;
    private RadioButton rbtnNam;
    private RadioButton rbtnNu;
    private Button btnAdd;
    private ListView lvContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermission();
        sqlHelper = new SQLHelper(this);
        contactList = new ArrayList<>();

        edtName = findViewById(R.id.edt_name);
        edtNumber = findViewById(R.id.edt_number);
        rbtnNam = findViewById(R.id.rbtn_nam);
        rbtnNu = findViewById(R.id.rbtn_nu);
        btnAdd = findViewById(R.id.btn_add);
        lvContact = findViewById(R.id.lv_contact);

btnAdd.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        boolean isMale = true;
        if (rbtnNam.isChecked()){
            isMale = true;
        }else {
            isMale = false;
        }
        if(view.getId()==R.id.btn_add){
            String name = edtName.getText().toString().trim();
            String number = edtNumber.getText().toString().trim();
            if (TextUtils.isEmpty(name)||
                    TextUtils.isEmpty(number)){
                Toast.makeText(MainActivity.this,"Vui long nhap ten hoac so dien thoai",Toast.LENGTH_SHORT).show();
            }else {
                Contact contact = new Contact(isMale,name,number);
               sqlHelper.insertContact(contact);
                contactList.add(contact);
            }
            adapter.notifyDataSetChanged();
        }

    }

});
        contactList = sqlHelper.getAllContact();
        adapter = new ContactAdapter(this,R.layout.item_contact,contactList);
        lvContact.setAdapter(adapter);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showDialogConfirm(position);
            }
        });
    }



    public void showDialogConfirm(final int position){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        Button btnCall= dialog.findViewById(R.id.btn_call);
        Button btnMessage = dialog.findViewById(R.id.btn_message);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intenCall(position);
            }

            private void intenCall(int position) {
                Intent intent = new Intent();
                intent.setAction(intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+contactList.get(position).getmNumber()));
                startActivity(intent);
            }
        });
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intenSendMessage(position);
            }

            private void intenSendMessage(int position) {
                Intent intent = new Intent();
                intent.setAction(intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:"+contactList.get(position).getmNumber()));
                startActivity(intent);
            }
        });

        dialog.show();

    }
    private void checkAndRequestPermission(){
        String[] permissions = new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(permission);
            }
            if (!listPermissionsNeeded.isEmpty()){
                ActivityCompat.requestPermissions(
                        this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            }
        }
    }


    }
