
package com.example.lyzweather;

import com.example.guanoweather.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TodayCanActivity extends Activity implements OnClickListener{

	private TextView bottom_todaycan;//
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todaycan_activity);
		
		initView();
		setdata();
	}

	private void initView() {
		bottom_todaycan = (TextView) findViewById(R.id.bottom_todaycan);
	}
	
	@Override
	protected void onResume() {
		bottom_todaycan.setBackgroundColor(Color.parseColor(getString(R.string.color_bottombg)));
		super.onResume();
	}
	
	private void setdata() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bottom_citymanager:
			intent = new Intent(this, CityManagerActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.bottom_weathertext:
			finish();
			break;
		}
	}
}
