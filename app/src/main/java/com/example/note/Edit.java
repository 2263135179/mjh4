package com.example.note;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import androidx.appcompat.app.AppCompatActivity;



public class Edit extends AppCompatActivity implements OnClickListener{
    Button ButtonDelete,ButtonSave,ButtonCancel;
    EditText EditTextContent,EditTextTitle,EditTextEditAuthor;
    ImageView showImage;
    int tran = 0;
    String Author="";
    MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,1);//创建了一个MyDataBaseHelper对象dbHelper，用于操作数据库
    private Bitmap getPhotoBitmap(Uri uri)  {
        try {
            FileInputStream fs = new FileInputStream(uri.getPath());//读取文件内容

            Bitmap bitmap  = BitmapFactory.decodeStream(fs);
            return bitmap;
        }catch (IOException e){
            Log.e("IO",e.getMessage());//记录错误信息，并返回null
        }
        return null;
    }
    private void InitNote() {       //进行数据填装
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取一个可写的数据库对象db。
        Cursor cursor  = db.query("Note",new String[]{"id","title","author","content","picture"},"id=?",new String[]{tran+""},null,null,null,null);//查询数据库表"Note"中指定id的记录
        if(cursor.moveToNext()) {       //根据mainactivity传来的id值选择数据库中对应的行，将值返回
            do {
                String Title = cursor.getString(cursor.getColumnIndex("title"));
                String Author = cursor.getString(cursor.getColumnIndex("author"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String picture = cursor.getString(cursor.getColumnIndex("picture"));
                EditTextEditAuthor.setText(Author);
                EditTextContent.setText(content);
                EditTextTitle.setText(Title);
                if(!picture.equals("")){
                    Uri u=Uri.parse(picture);//解析图片路径为Uri对象u
                    Log.d("ImagesUrI",u.getPath());
                    showImage.setImageBitmap(getPhotoBitmap(u));
                }
            } while (cursor.moveToNext());
        }
//        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
//        String name = pref.getString("author","");      //通过sharedpreferences传递作者信息
//        //Log.d("MainActivity","name is " + name);
//        EditTextEditAuthor.setText(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);//设置当前Activity的布局文件为"activity_edit.xml"
        EditTextContent = (EditText)findViewById(R.id.EditTextEditContent);//获取布局文件中的控件，赋值给对应的成员变量。
        EditTextTitle = (EditText)findViewById(R.id.EditTextEditTitle) ;
        ButtonCancel = (Button)findViewById(R.id.ButtonCancel);
        ButtonSave = (Button)findViewById(R.id.ButtonSave);
        ButtonDelete = (Button)findViewById(R.id.ButtonDelete);
        EditTextEditAuthor = findViewById(R.id.EditTextEditAuthor);

        showImage = findViewById(R.id.showImage);


        ButtonCancel.setOnClickListener(this);//设置点击事件监听器,将当前Edit类实例作为OnClickListener传递给这些按钮的setOnClickListener()方法。
        ButtonSave.setOnClickListener(this);
        ButtonDelete.setOnClickListener(this);

        Intent intent = getIntent();//获取启动Edit Activity时传递的Intent对象。
        tran = intent.getIntExtra("tran",-1);       //取出mainactivity传来的id值

        InitNote();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.ButtonDelete:     //将对应的id行删除

                AlertDialog.Builder builder=new AlertDialog.Builder( this );
                builder.setTitle( "确定要删除吗？" );//标题
                builder.setNegativeButton( "取消",null );//null代表不作任何操作，只是消失对话框
                builder.setPositiveButton( "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("Note","id=?",new String[]{tran+""});//使用db.delete()方法删除数据库表"Note"中指定id的记录，删除条件为"id=?"，参数值为tran的字符串形式。
                        Edit.this.setResult(RESULT_OK,getIntent());//设置当前Activity的结果为RESULT_OK，表示操作成功。
                        Edit.this.finish();
                    }
                } );
                builder.create().show();


                break;
            case R.id.ButtonSave:       //保存该界面的数据
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                Date date = new Date();//获取当前日期和时间。
                ContentValues values = new ContentValues();//存储需要更新到数据库的值。
                String Title = String.valueOf(EditTextTitle.getText());
                String Author = String.valueOf(EditTextEditAuthor.getText());
                String Content = String.valueOf(EditTextContent.getText());
                if(Title.length()==0){
                    Toast.makeText(this, "请输入标题", Toast.LENGTH_LONG).show();
                }else {
                    values.put("title", Title);
                    values.put("author", Author);
                    values.put("content", Content);
                    db1.update("Note", values, "id=?", new String[]{tran + ""});        //对数据进行更新
                    Edit.this.setResult(RESULT_OK, getIntent());
                    Edit.this.finish();
                }


                Author = String.valueOf(EditTextEditAuthor.getText());
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();//编辑SharedPreferences数据
                editor.putString("author",Author);      //写入作者信息
                editor.apply();//提交编辑的数据，将Author值写入SharedPreferences中。

                break;


            case R.id.ButtonCancel:
                Edit.this.setResult(RESULT_OK,getIntent());
                Edit.this.finish();
                break;

        }

    }
}