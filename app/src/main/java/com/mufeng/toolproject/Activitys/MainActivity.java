package com.mufeng.toolproject.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mufeng.toolproject.R;
import com.mufeng.toolproject.utils.DirectoryUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initWidget() {
        findViewById(R.id.install_btn).setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        DirectoryUtils.getEnvironmentDirectories();
        DirectoryUtils.getApplicationDirectories(this);

        String path = Environment.getExternalStorageDirectory().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.install_btn:
                Intent intent = new Intent(MainActivity.this, FileStorageActivity.class);
                startActivity(intent);
                break;
        }

    }
}
