package com.xily.note;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.xily.note.adapter.LinearAdapter;
import com.xily.note.util.Theme;

import org.litepal.crud.DataSupport;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRvMain;
    private List<Data> notedata;
    private LinearAdapter adapter;
    private EditText text;
    private SwipeRefreshLayout mRefreshLayout;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setTheme(this);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mRefreshLayout = findViewById(R.id.layout_swipe_refresh);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        mRefreshLayout.setColorSchemeColors(typedValue.data);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            public void onRefresh() {
                notedata= DataSupport.order("time desc").find(Data.class);
                adapter.refreshAll(notedata);
                adapter.notifyDataSetChanged();
                adapter.notifyItemRangeChanged(0,notedata.size());
                mRefreshLayout.setRefreshing(false);
            }
        });
        mRvMain=findViewById(R.id.rv_main);
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("state",0);
                startActivityForResult(intent,1);
            }
        });
        notedata= DataSupport.order("time desc").find(Data.class);
        final SettingsData data=new SettingsData(this);
        int type,type2,listnum;
        //mRvMain.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        switch(data.getValue("list",0)){
            case 1:
                type=2;
                type2=1;
                listnum=2;
                break;
            case 2:
                type=2;
                type2=1;
                listnum=3;
                break;
            case 3:
                type=2;
                type2=2;
                listnum=2;
                break;
            case 4:
                type=2;
                type2=2;
                listnum=3;
                break;
            default:
                type=1;
                type2=1;
                listnum=1;
                break;
        }if(type2==1)
            mRvMain.setLayoutManager(new StaggeredGridLayoutManager(listnum,StaggeredGridLayoutManager.VERTICAL));
        else
            mRvMain.setLayoutManager(new GridLayoutManager(this,listnum));
        adapter=new LinearAdapter(MainActivity.this,notedata,type,new LinearAdapter.OnItemClickListener(){
            @Override
            public void onClick(int pos, LinearAdapter.LinearViewHolder holder) {
                final Data note=notedata.get(pos);
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("state",1);
                intent.putExtra("id",note.getId());
                startActivityForResult(intent,2);
            }
        },new LinearAdapter.OnItemLongClickListener() {
            @Override
            public void onClick(final int pos, final LinearAdapter.LinearViewHolder holder) {
                final Data note=notedata.get(pos);
                final String array[]=new String[]{"重命名","删除","分享"};
                new AlertDialog.Builder(MainActivity.this).setTitle(note.getTitle()).setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 2:
                                Intent textIntent = new Intent(Intent.ACTION_SEND);
                                textIntent.setType("text/plain");
                                textIntent.putExtra(Intent.EXTRA_TEXT, note.getContent());
                                startActivity(Intent.createChooser(textIntent, "分享"));
                                break;
                            case 1:
                                new AlertDialog.Builder(MainActivity.this).setTitle("删除").setMessage("\n你确定要删除吗？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DataSupport.delete(Data.class,note.getId());
                                                notedata.remove(pos);
                                                adapter.notifyItemRemoved(pos);
                                                if(pos!=notedata.size())adapter.notifyItemRangeChanged(pos,notedata.size()-pos);
                                                Snackbar.make(getWindow().getDecorView().findViewById(R.id.layout_frame),"笔记被删除",Snackbar.LENGTH_SHORT).
                                                        setAction("撤销", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Data newdata=new Data();
                                                                newdata.setTitle(note.getTitle());
                                                                newdata.setTime(note.getTime());
                                                                newdata.setContent(note.getContent());
                                                                newdata.save();
                                                                notedata.add(pos,newdata);
                                                                adapter.notifyItemInserted(pos);
                                                                adapter.notifyItemRangeChanged(pos,notedata.size());
                                                            }
                                                        }).show();
                                            }
                                        }).setNegativeButton("取消", null).show();
                                break;
                            case 0:
                                final AlertDialog dialog1=new AlertDialog.Builder(MainActivity.this).setTitle("重命名").setView(R.layout.layout_rename_dialog)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Data data=new Data();
                                                data.setTitle(text.getText().toString());
                                                data.update(note.getId());
                                                notedata.get(pos).setTitle(text.getText().toString());
                                                adapter.notifyItemChanged(pos);
                                                Snackbar.make(getWindow().getDecorView().findViewById(R.id.layout_frame),"成功重命名",Snackbar.LENGTH_SHORT).show();
                                            }
                                        }).setNegativeButton("取消",null).show();
                                dialog1.getWindow().setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                text=dialog1.findViewById(R.id.et_rename);
                                text.setText(note.getTitle());
                                break;
                        }
                    }
                }).show();
            }
        });
        mRvMain.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        MenuItem menuItem=menu.findItem(R.id.action_serach);
        searchView= (SearchView) menuItem.getActionView();//加载searchview
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty((newText))){
                    notedata= DataSupport.where("content like ?","%"+newText+"%").order("time desc").find(Data.class);
                }else {
                    notedata= DataSupport.order("time desc").find(Data.class);
                }adapter.refreshAll(notedata);
                adapter.notifyDataSetChanged();
                adapter.notifyItemRangeChanged(0,notedata.size());
                return false;
            }
        });
        searchView.setQueryHint("查找");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=null;
        switch (item.getItemId()) {
            case R.id.action_serach:
                break;
            case R.id.action_about:
                intent=new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                intent=new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent,0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK) {
            Data newnote = DataSupport.order("time desc").findFirst(Data.class);
            notedata.add(0,newnote);
            adapter.notifyItemInserted(0);
            adapter.notifyItemRangeChanged(0,notedata.size());
            mRvMain.smoothScrollToPosition(0);
        }else if(requestCode==2){
            notedata= DataSupport.order("time desc").find(Data.class);
            adapter.refreshAll(notedata);
            adapter.notifyDataSetChanged();
            adapter.notifyItemRangeChanged(0,notedata.size());
        }else if(requestCode==0&&resultCode==RESULT_OK){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}

