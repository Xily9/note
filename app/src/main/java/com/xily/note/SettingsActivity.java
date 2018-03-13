package com.xily.note;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.xily.note.util.Theme;

import java.util.Arrays;


public class SettingsActivity extends AppCompatActivity {
    private SettingsData data;
    private AlertDialog view;
    private EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data=new SettingsData(this);
        Theme.setTheme(this);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ((Switch)findViewById(R.id.st_1)).setChecked(data.getValue("auto_save",true));
        ((Switch)findViewById(R.id.st_2)).setChecked(data.getValue("small_font",false));
        ((Switch)findViewById(R.id.st_3)).setChecked(data.getValue("green",false));
        OnClickListener onClickListener=new OnClickListener();
        findViewById(R.id.settings_4).setOnClickListener(onClickListener);
        findViewById(R.id.settings_5).setOnClickListener(onClickListener);
        findViewById(R.id.settings_6).setOnClickListener(onClickListener);
        onChangeListener onChangeListener=new onChangeListener();
        ((Switch)findViewById(R.id.st_1)).setOnCheckedChangeListener(onChangeListener);
        ((Switch)findViewById(R.id.st_2)).setOnCheckedChangeListener(onChangeListener);
        ((Switch)findViewById(R.id.st_3)).setOnCheckedChangeListener(onChangeListener);
        //((Switch)findViewById(R.id.st_4)).setOnCheckedChangeListener(onChangeListener);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class onChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.st_1:
                    data.setValue("auto_save",isChecked);
                    break;
                case R.id.st_2:
                    data.setValue("small_font",isChecked);
                    break;
                case R.id.st_3:
                    data.setValue("green",isChecked);
                    break;
                //case R.id.st_4:
                //    data.setValue(isChecked?1:2);
                //    data.update(5);
                //   break;
            }
        }
    }
    private class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.settings_4:
                    view=new AlertDialog.Builder(SettingsActivity.this).setTitle("设置主题").setView(R.layout.layout_theme_dialog).show();
                    //批量注册监听方法
                    for(int Rid:Theme.getThemeList())
                        view.findViewById(Rid).setOnClickListener(this);
                    break;
                case R.id.settings_5:
                    final String[] choices = new String[] {"列表视图", "瀑布流(2列)", "瀑布流(3列)","网格视图(2列)","网格视图(3列)"};
                    view=new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("便签列表样式")
                            .setSingleChoiceItems(choices, data.getValue("list", 0), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    data.setValue("list",which);
                                    setResult(RESULT_OK);
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
                case R.id.settings_6:
                    view=new AlertDialog.Builder(SettingsActivity.this).setTitle("最大行数设置").setView(R.layout.layout_maxline_dialog)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    data.setValue("maxline",Integer.valueOf(text.getText().toString()));
                                }
                            }).setNegativeButton("取消",null).show();
                    view.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    text=view.findViewById(R.id.et_maxline);
                    String maxline=String.valueOf(data.getValue("maxline",3));
                    text.setText(maxline);
                    text.setSelection(maxline.length());
                    setResult(RESULT_OK);
                    break;
                default:
                    data.setValue("theme", Arrays.binarySearch(Theme.getThemeList(),v.getId()));
                    setResult(RESULT_OK);
                    view.dismiss();
                    finish();
                    break;
            }
        }
    }
}
