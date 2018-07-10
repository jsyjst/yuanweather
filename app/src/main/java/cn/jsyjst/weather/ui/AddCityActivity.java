package cn.jsyjst.weather.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
;
import java.util.ArrayList;
import java.util.List;

import cn.jsyjst.weather.R;
import cn.jsyjst.weather.adapter.FindCityAdapter;
import cn.jsyjst.weather.adapter.RecommendCityAdapter;
import cn.jsyjst.weather.utils.JsonPrase;
import cn.jsyjst.weather.db.CityCrud;
import cn.jsyjst.weather.utils.HttpUtil;


/**
 * 添加城市功能
 */
public class AddCityActivity extends AppCompatActivity {

    /**
     * 推荐城市的list
     */
    private List<String> cityList = new ArrayList<>();
    /***
     * 查询城市的list
     */
    private List<String> findCityList = new ArrayList<>();
    /**
     * 查询的关键词
     */
    private EditText editText;
    private Button delBtn;
    private RecyclerView recyclerView;
    private ListView listView;
    /**
     * 查找城市的布局
     */
    private LinearLayout findLinear;
    /**
     * 推荐城市的布局
     */
    private LinearLayout recommendLinear;
    /**
     * 找不到城市的布局
     */
    private LinearLayout unfindLinear;
    private String find;
    /**
     * 显示查找城市的个数
     */
    private TextView findNumTv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        /**
         * 初始化各控件
         */
        Button backBtn = (Button) findViewById(R.id.btn_add_back);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_city);
        listView = (ListView) findViewById(R.id.lv_find_city);
        editText = (EditText) findViewById(R.id.edit_find);
        delBtn = (Button) findViewById(R.id.btn_del);
        findLinear = (LinearLayout) findViewById(R.id.linear_find_city);
        recommendLinear = (LinearLayout) findViewById(R.id.linear_recommend_city);
        unfindLinear = (LinearLayout) findViewById(R.id.linear_unfind);
        findNumTv = (TextView) findViewById(R.id.tv_find_number);


        /**
         * 处理推荐城市的布局
         */
        initRecommendCity();


        /**
         * 对文本框进行监听，实现推荐城市与查询城市的切换
         */
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                find = charSequence.toString();
                if (editText.length() != 0) {
                    recommendLinear.setVisibility(View.GONE);
                    findLinear.setVisibility(View.VISIBLE);
                    delBtn.setVisibility(View.VISIBLE);
                } else {
                    unfindLinear.setVisibility(View.GONE);
                    findLinear.setVisibility(View.GONE);
                    recommendLinear.setVisibility(View.VISIBLE);
                    delBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /**
                 * 处理查找城市的布局
                 */
                if (editText.length() != 0) {
                    initFind(find);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddCityActivity.this, ManageCityActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 文本清空
         */
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.getText().clear();
            }
        });


    }


    /**
     * 推荐城市的网格布局
     */
    private void initRecommendCity() {
        initCity();
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        RecommendCityAdapter adapter = new RecommendCityAdapter(cityList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 对文本框的内容进行服务器查询，从而实现模糊查找
     *
     * @param find 输入的内容
     */
    private void initFind(String find) {
        String address = "https://api.seniverse.com/v3/location/search.json?key=y76boyq776m6qisk&q=" + find + "&limit=100";
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findCityList.clear();
                        findCityList = JsonPrase.handFindCityResponse(response);
                        initFindCity();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * 查找城市的垂直布局，对ListView中的集合进行判断查找是否有结果
     * 如果找不到则页面转化，告诉用户找不到
     */
    private void initFindCity() {
        if (findCityList.size() == 0) {
            listView.setVisibility(View.GONE);
            unfindLinear.setVisibility(View.VISIBLE);
        } else {
            unfindLinear.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        findNumTv.setText("搜索到 " + findCityList.size() + " 个城市");
        FindCityAdapter adapter = new FindCityAdapter(AddCityActivity.this,
                R.layout.find_city_listview_item, findCityList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = findCityList.get(i);


                /**
                 * 截取“，”之前的字符，获得点击的城市
                 */
                String city = name.substring(0, name.indexOf(","));
                /**
                 * 保存到数据库中
                 */
                CityCrud cityCrud = new CityCrud(AddCityActivity.this, "weather.db", null, 2);
                cityCrud.create(city, null, null, null);

                /**
                 * 传值给主活动
                 */
                Intent intent = new Intent(AddCityActivity.this, MainActivity.class);
                intent.putExtra("number", -2);
                startActivity(intent);

            }
        });
    }


    /**
     * 对推荐城市的初始化
     */
    private void initCity() {
        cityList.add("当前位置");
        cityList.add("北京");
        cityList.add("广州");
        cityList.add("深圳");
        cityList.add("潮州");
        cityList.add("揭阳");
        cityList.add("汕头");
        cityList.add("汕尾");
        cityList.add("佛山");
        cityList.add("中山");
        cityList.add("珠海");
        cityList.add("韶关");
        cityList.add("湛江");
        cityList.add("东莞");
        cityList.add("惠州");
        cityList.add("清远");
        cityList.add("河源");
        cityList.add("梅州");
        cityList.add("茂名");
        cityList.add("肇庆");
        cityList.add("云浮");
        cityList.add("江门");
        cityList.add("上海");
        cityList.add("吉林");
        cityList.add("成都");
        cityList.add("海口");
        cityList.add("乌鲁木齐");
        cityList.add("杭州");
        cityList.add("重庆");
        cityList.add("南京");
        cityList.add("呼和浩特");
        cityList.add("拉萨");
        cityList.add("长沙");
        cityList.add("福州");
        cityList.add("香港");
        cityList.add("澳门");
    }
}
