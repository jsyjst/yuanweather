package cn.jsyjst.weather.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.jsyjst.weather.R;
import cn.jsyjst.weather.services.AutoUpdateService;


/**
 * 设置功能，主要是自动更新的开关
 */
public class SetActivity extends AppCompatActivity {
    private boolean isTouch = false;

    private ImageView switchIv;

    private SharedPreferences prefs;

    private SharedPreferences.Editor editor;

    private RelativeLayout updateSpaceRv;

    private TextView updateTimeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        RelativeLayout switchRv = (RelativeLayout) findViewById(R.id.rv_set_switch);
        Button backBtn = (Button) findViewById(R.id.btn_set_back);
        updateSpaceRv = (RelativeLayout) findViewById(R.id.rv_set_updateSpace);
        switchIv = (ImageView) findViewById(R.id.btn_set_switch);
        updateSpaceRv = (RelativeLayout) findViewById(R.id.rv_set_updateSpace);
        updateTimeTv = (TextView) findViewById(R.id.tv_update_time);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 利用sharePreference来记住开关的开和关,可以在下一次打开时显示其状态
         */
        isTouch = prefs.getBoolean("updateSwitch", false);
        if (isTouch) {
            switchIv.setImageResource(R.drawable.switch_open);
        } else {
            switchIv.setImageResource(R.drawable.switch_close);
        }

        /**
         * 自动更新开关
         */
        switchRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTouch = !isTouch;
                if (isTouch) {
                    editor.putBoolean("updateSwitch", true);
                    editor.apply();
                    switchIv.setImageResource(R.drawable.switch_open);
                    updateSpaceRv.setEnabled(true);
                    /**
                     * 启动后台更新,从本地看看是否有设置时间间隔，有的话传值给自动更新的服务
                     */
                    Intent startIntent = new Intent(SetActivity.this, AutoUpdateService.class);
                    String updateTime = prefs.getString("updateSpace", "1");
                    startIntent.putExtra("time", updateTime);
                    startService(startIntent);

                } else {
                    updateSpaceRv.setEnabled(false);
                    editor.putBoolean("updateSwitch", false);
                    editor.apply();
                    switchIv.setImageResource(R.drawable.switch_close);

                    Intent stopIntent = new Intent(SetActivity.this, AutoUpdateService.class);
                    stopIntent.putExtra("stop",0);
                    stopService(stopIntent);
                }
            }
        });

        String updateTime = prefs.getString("updateSpace", "1");
        /**
         * 显示更新小时数
         */
        updateTimeTv.setText(updateTime + "小时");
        /**
         * 判断有没打开自动更新的按钮，如果没有则不可设置更新间隔
         */
        if (updateTime != null && isTouch) {
            updateSpaceRv.setEnabled(true);
        } else {
            updateSpaceRv.setEnabled(false);
        }
        /**
         * 设置更新间隔，利用带有editText的提示框
         */
        updateSpaceRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(SetActivity.this);
                final View v = factory.inflate(R.layout.edit_alertdialog, null);
                /**
                 * 获得输入框对象
                 */
                final EditText edit = v.findViewById(R.id.edit_updateSpace);
                String text = prefs.getString("updateSpace", "1");
                edit.setText(text);
                /**
                 * 光标移动
                 */
                edit.setSelection(edit.getText().length());
                new AlertDialog.Builder(SetActivity.this)
                        .setView(v)
                        .setPositiveButton("确定",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        String updateSpace = "";
                                        int time = 0;
                                        /**
                                         * 捕获异常，防止因为输入的时间间隔格式错误而导致程序崩溃
                                         */
                                        try {
                                            updateSpace = edit.getText().toString();
                                            time = Integer.valueOf(updateSpace);
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }
                                        if (0 < time && time < 25) {
                                            /**
                                             * 启动后台更新，输入时间间隔的格式只能是1到24小时
                                             */
                                            Intent startIntent = new Intent(SetActivity.this, AutoUpdateService.class);
                                            startIntent.putExtra("time", updateSpace);
                                            startService(startIntent);

                                            updateTimeTv.setText(updateSpace + "小时");
                                            editor.putString("updateSpace", updateSpace);
                                            editor.apply();
                                        } else {
                                            Toast.makeText(SetActivity.this, "输入的时间间隔不符合，请重新输入", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }
}
