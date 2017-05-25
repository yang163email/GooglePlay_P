package com.yan.actionbar;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.btn);
        button.setOnLongClickListener(this);
        mActionBar = getSupportActionBar();

        mActionBar.setTitle("MainTitle");// 设置主title部分
        mActionBar.setSubtitle("SubTitle");// 设置子title部分

        mActionBar.setIcon(R.mipmap.ic_launcher);// 设置应用图标
        mActionBar.setLogo(R.mipmap.ic_action_call);

        mActionBar.setDisplayShowTitleEnabled(true);// 设置菜单 标题是否可见
//        mActionBar.setDisplayShowHomeEnabled(true);// 设置应用图标是否
        mActionBar.setDisplayUseLogoEnabled(false);// 设置是否显示Logo优先
        mActionBar.setDisplayHomeAsUpEnabled(true);// 设置back按钮是否可见

//        navigationMode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.web_client:
                item.setChecked(!item.isChecked());
                Toast.makeText(this, "前端还行", Toast.LENGTH_SHORT).show();
                break;
            case R.id.c_plus:
                item.setChecked(!item.isChecked());
                Toast.makeText(this, "c++有点难", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void navigationMode() {
        final List<String> objects = new ArrayList<String>();
        objects.add("主页");
        objects.add("新闻");
        objects.add("娱乐");
        /*mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, objects);
        mActionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                Toast.makeText(MainActivity.this, "选择了：" + objects.get(itemPosition),  Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (int i = 0; i < 3; i++) {
            ActionBar.Tab tab = mActionBar.newTab();
            tab.setText(objects.get(i));
            tab.setIcon(R.mipmap.ic_launcher);
            tab.setTabListener(new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    Toast.makeText(MainActivity.this, "选中了：" + tab.getText(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

                }
            });

            mActionBar.addTab(tab);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Toast.makeText(MainActivity.this, "点击了：" + item.getTitle(), Toast.LENGTH_SHORT).show();
                mode.finish();
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        return true;
    }
}
