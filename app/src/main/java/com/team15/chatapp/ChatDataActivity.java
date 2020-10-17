package com.team15.chatapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.team15.chatapp.Adapter.ChatDataAdapter;
import com.team15.chatapp.Data.DBHelper;

import java.util.ArrayList;

public class ChatDataActivity extends AppCompatActivity {
    DBHelper mydb;
    private ImageButton imgAdd,imgBack;
    private ListView dataList;
    ChatDataAdapter customAdapter;
    ArrayList array_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_data);
        mydb = new DBHelper(this);
        imgAdd = findViewById(R.id.imgAdd);
        dataList = findViewById(R.id.dataList);
        imgBack = findViewById(R.id.imgBack);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopupDialog();
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setupList();
        dataList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList list = (ArrayList) array_list.get(position);
                menuClick(list,view);
                return false;
            }
        });
    }

    private void menuClick(final ArrayList list, View v) {
        final PopupMenu popup = new PopupMenu(this,v);
        popup.inflate(R.menu.list_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        mydb.deleteItem(Integer.parseInt((String) list.get(0)));
                        setupList();
                        break;
                    case R.id.update:
                        updateDialog(list);
                        break;
                    default:
                        break;

                }
                popup.dismiss();
                return false;
            }
        });

        popup.show();
    }
    private void updateDialog(final ArrayList list) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        final EditText editTextTitle = (EditText) dialogView.findViewById(R.id.editTextTitle);
        Button buttonSubmit = (Button) dialogView.findViewById(R.id.buttonSubmit);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancel);

        editTextTitle.setText(list.get(1).toString());


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                mydb.updateData(Integer.parseInt((String) list.get(0)),title);
                setupList();
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void ShowPopupDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        final EditText editTextTitle = (EditText) dialogView.findViewById(R.id.editTextTitle);
        Button buttonSubmit = (Button) dialogView.findViewById(R.id.buttonSubmit);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancel);


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                mydb.insert(title);
                setupList();
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void setupList() {
        array_list = mydb.getAllCart();
        customAdapter = new ChatDataAdapter(getApplicationContext(), array_list);
        dataList.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }
}