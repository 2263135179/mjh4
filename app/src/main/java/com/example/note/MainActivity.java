package com.example.note;


import android.view.View.OnClickListener;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnClickListener{
    private List<Integer> IDList = new ArrayList<>();//定义了一个名为IDList的整型列表，用于存储一组整数数据
    private List<String> TADList = new ArrayList<>();//定义了一个名为TADList的字符串列表，用于存储一组字符串数据。
    ArrayAdapter simpleAdapter;//适配数据到列表视图。
    Button ButtonSeek;
    EditText EditTextSeek;
    String EditTextSeekString ;
    private void InitNote() {       //进行数据填装
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,1);//创建一个MyDataBaseHelper对象dbHelper，用于操作数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //通过dbhelper获得可写文件
        Cursor cursor  = db.rawQuery("select * from Note",null);//获取数据库表"Note"的所有记录的Cursor对象cursor
        IDList.clear();
        TADList.clear();        //清空两个list
        while(cursor.moveToNext()){
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            IDList.add(id);
            TADList.add(title+"\n"+ date);      //对两个list填充数据
        }
    }

    public void RefreshTADList(){       //返回该界面时刷新的方法
        int size = TADList.size();//获取TADList列表的大小
        //if(size>0){
        TADList.removeAll(TADList);
        IDList.removeAll(IDList); //清空两个list中的值
        simpleAdapter.notifyDataSetChanged();   //通知适配器数据发生变化
        //}
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();         //实例化SQLitedatabase
        Cursor cursor  = db.rawQuery("select * from Note",null);
        while(cursor.moveToNext()){         //对两个list重新赋予值
            int id=cursor.getInt(cursor.getColumnIndex("id"));//获取当前行记录的"id"字段的整型值，并赋值给变量id

            String title = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            IDList.add(id);//将id添加到IDList列表中
            TADList.add(title+"\n"+ date);      //将title和时间分开显示
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        RefreshTADList();       //调用刷新方法
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        InitNote();

        //为悬浮按钮添加监听事件

        //添加日记
        findViewById(R.id.action_add).setOnClickListener(new View.OnClickListener(){//使用findViewById()方法获取悬浮按钮的视图，并调用setOnClickListener()方法为其设置点击事件监听器。
            public void onClick(View v){
                Toast.makeText(MainActivity.this,"添加",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Add.class);
                startActivity(intent);
            }
        });


        //查询日记
        findViewById(R.id.action_chaxun).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(MainActivity.this,"查询",Toast.LENGTH_SHORT).show();
                SearchDialog();
            }
        });

        simpleAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,TADList);       //配置适配器
        ListView ListView = (ListView)findViewById(R.id.ListView);
        ListView.setAdapter(simpleAdapter);                 //将两个list中的值通过ArrayList显示出来



        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){      //配置ArrayList点击按钮.ListView的列表项配置了一个点击事件监听器
            @Override
            public void  onItemClick(AdapterView<?> parent, View view , int position , long id){
                int tran = IDList.get(position);        //点击不同的行，返回不同的id
                Intent intent = new Intent(MainActivity.this, Edit.class);//跳转到Edit Activity。
                intent.putExtra("tran",tran);//将变量tran作为参数传递给Edit Activity。
                startActivity(intent);      //通过intent传输,启动Edit Activity
            }
        });
    }
    //查询
    private void SearchDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.search, null);
        //创建一个弹出框
        new AlertDialog.Builder(this)
                .setTitle("日记")//标题
                .setView(tableLayout)//设置视图
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String EditTextSeekString=((EditText)tableLayout.findViewById(R.id.Search)).getText().toString();
                        if(EditTextSeekString.length()==0){             //查询为空，给出提示信息
                            //RefreshTADList();
                            Toast.makeText(MainActivity.this,"查询值不能为空",Toast.LENGTH_LONG).show();
                        }
                        else{           //否则通过intent给查询界面传入查询的title
                            Intent intent = new Intent(MainActivity.this, Research.class);
                            //intent.putExtra("tranTitle",EditTextSeekString);
                            intent.putExtra("tranTitletoRE",EditTextSeekString);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()//创建对话框
                .show();//显示对话框
    }


    @Override
    public void onClick(View v){
        switch (v.getId()){

        }

    }


}