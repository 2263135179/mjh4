package com.example.note;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class Research extends AppCompatActivity implements OnClickListener{
    Button ButtonDelete,ButtonSave,ButtonCancel;
    EditText EditTextContent,EditTextTitle,EditTextREAuthor;
    String tranTitle;//存储查询的标题
    String Author;//存储作者信息
    ImageView showImage;
    MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,1);//操作数据库

    private void InitNote() {
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,1);//操作数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //同上，获得可写文件
        Cursor cursor  = db.query("Note",new String[]{"id","title","author","content","picture"},"title=?",new String[]{tranTitle+""},null,null,null,null);

        if(cursor.moveToNext()) {       //逐行查找，得到匹配信息
            do {
                String Title = cursor.getString(cursor.getColumnIndex("title"));//获取每一行记录的值，并分别赋值给对应的变量。
                String Author = cursor.getString(cursor.getColumnIndex("author"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String picture = cursor.getString(cursor.getColumnIndex("picture"));
                Uri u = Uri.parse(picture);
                EditTextREAuthor.setText(Author);
                EditTextContent.setText(content);
                EditTextTitle.setText(Title);
                showImage.setImageURI(u);//将Uri对象u设置到showImage控件中，显示对应的图片。
            } while (cursor.moveToNext());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//调用父类的onCreate()方法，以确保正确地初始化Activity
        setContentView(R.layout.activity_research);//使用setContentView()方法设置当前Activity的布局
        EditTextContent = (EditText)findViewById(R.id.EditTextREEditContent);
        EditTextTitle = (EditText)findViewById(R.id.EditTextREEditTitle) ;
        ButtonCancel = (Button)findViewById(R.id.ButtonRECancel);
        ButtonSave = (Button)findViewById(R.id.ButtonRESave);
        ButtonDelete = (Button)findViewById(R.id.ButtonREDelete);
        EditTextREAuthor = findViewById(R.id.EditTextREAuthor);

        showImage = findViewById(R.id.showImage);

        ButtonCancel.setOnClickListener(this);//设置点击事件监听器
        ButtonSave.setOnClickListener(this);
        ButtonDelete.setOnClickListener(this);

        Intent intent = getIntent();//获取启动Research Activity时传递的Intent对象
        Bundle extras = intent.getExtras();//获取传递的额外数据，即Bundle对象extras。
        tranTitle = extras.getString("tranTitletoRE");      //接受主界面传来的title值

        InitNote();


    }
    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.ButtonREDelete:       //删除该title的日志
                Log.d("title is ",tranTitle);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Note","title=?",new String[]{tranTitle+""});     //进行字符串匹配
                Research.this.setResult(RESULT_OK,getIntent());
                Research.this.finish();
                break;
            case R.id.ButtonRESave:         //将界面内容保存
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();        //获取可写文件
                Date date = new Date();
                ContentValues values = new ContentValues();         //获取信息
                String Title = String.valueOf(EditTextTitle.getText());
                String Content = String.valueOf(EditTextContent.getText());
                if(Title.length()==0){
                    Toast.makeText(this, "请输入标题", Toast.LENGTH_LONG).show();
                }else {
                    values.put("title", Title);         //填装信息
                    values.put("content", Content);
                    db1.update("Note", values, "title=?", new String[]{tranTitle + ""});        //字符串匹配
                    Research.this.setResult(RESULT_OK, getIntent());        //返回主界面
                    Research.this.finish();
                }

                Author = String.valueOf(EditTextREAuthor.getText());
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("author",Author);
                editor.apply();     //写入作者信息

                break;


            case R.id.ButtonRECancel:
                Research.this.setResult(RESULT_OK,getIntent());
                Research.this.finish();
                break;

        }

    }
}