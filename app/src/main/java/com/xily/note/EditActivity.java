package com.xily.note;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.xily.note.util.Theme;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    String title,content;
    int id,num=0;
    boolean change=false;
    boolean isshare=false;
    EditText text;
    ArrayList<String> str=new ArrayList<String>();
    SettingsData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setTheme(this);
        setContentView(R.layout.activity_edit);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        text=findViewById(R.id.edit);
        String action = intent.getAction();
        String type = intent.getType();
        if(bundle.getInt("state")==1) {
            id=bundle.getInt("id");
            Data data= DataSupport.where("id=?",Integer.toString(id)).findFirst(Data.class);
            title=data.getTitle();
            content=data.getContent();
        }else{
            if(Intent.ACTION_SEND.equals(action)&&type!=null&&"text/plain".equals(type)) {
                content = bundle.getString(Intent.EXTRA_TEXT);
                title = bundle.getString(Intent.EXTRA_TITLE);
                if(TextUtils.isEmpty(title))title="未命名";
                isshare=true;
            }else {
                title = "未命名";
                content = "";
            }getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        data=new SettingsData(this);
        if(data.getValue("small_font",false)){
            text.setTextSize(15);
        }if(data.getValue("green",false)){
            findViewById(R.id.et_bg).setBackgroundColor(Color.parseColor("#C7EDCC"));
        }
        text.setText(content);
        str.add(content);
        text.setSelection(content.length());
        text.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!change) {
                    num++;
                    if (str.size() == num) {
                        str.add(s.toString());
                    } else {
                        str.set(num, s.toString());
                    }
                }else{
                    text.setSelection(str.get(num).length());
                    change=false;
                }
            }
        });
        setTitle(title);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Save();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Save();
                finish();
                onBackPressed(); // back button
                return true;
            case R.id.edit_sum:
                new AlertDialog.Builder(this).setTitle("统计信息")
                        .setMessage("\n总共有 "+text.getText().toString().length()+" 个字（包括标点符号）")
                .setPositiveButton("确定",null).show();
                break;
            case R.id.edit_share:
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, text.getText().toString());
                startActivity(Intent.createChooser(textIntent, "分享"));
                break;
            case R.id.action_chexiao:
                if(num>0) {
                    num--;
                    change=true;
                    text.setText(str.get(num));
                }
                break;
            case R.id.action_huifu:
                if(str.size()>num+1){
                    num++;
                    change=true;
                    text.setText(str.get(num));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(data.getValue("auto_save",true)){
            Save();
        }
    }
    private boolean Save(){
        String content2=text.getText().toString();
        if((!content2.equals(content)||isshare)&&!content2.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            Data data = new Data();
            data.setContent(content2);
            data.setTime(str);
            if (id > 0 ) {
                data.update(id);
            }else {
                data.setTitle(title);
                data.save();
                id = data.getId();
            }content=content2;
            Toast.makeText(EditActivity.this, "笔记已保存", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            isshare=false;
        }else{
            setResult(RESULT_CANCELED);
        }
        return true;
    }
}
