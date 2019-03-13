package com.my.linechart;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CustomLineChartView linechart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linechart = findViewById(R.id.linechart);

        initData();
    }
    private void initData(){
        List<MyBean> mList = new ArrayList<>();
        for(int i = 0; i < 12; i++){
            MyBean bean = new MyBean();
            if(i == 0){
                bean.setNum("1100");
                bean.setDate("1546790400");
            }else if(i == 1){
                bean.setNum("1000");
                bean.setDate("1546876800");
            }else if(i == 2){
                bean.setNum("1800");
                bean.setDate("1546963200");
            }else if(i == 3){
                bean.setNum("400");
                bean.setDate("1547049600");
            }else if(i == 4){
                bean.setNum("1100");
                bean.setDate("1547136000");
            }else if(i == 5){
                bean.setNum("2000");
                bean.setDate("1547222400");
            }else if(i == 6){
                bean.setNum("700");
                bean.setDate("1550419200");
            }else if(i == 7){
                bean.setNum("900");
                bean.setDate("1551283200");
            }else if(i == 8){
                bean.setNum("300");
                bean.setDate("1551369600");
            }else if(i == 9){
                bean.setNum("500");
                bean.setDate("1551456000");
            }else if(i == 10){
                bean.setNum("1100");
                bean.setDate("1551542400");
            }else if(i == 11){
                bean.setNum("1700");
                bean.setDate("1551628800");
            }
            bean.setPoint(new Point());
            mList.add(bean);
        }

        linechart.setData(mList);
    }
}
