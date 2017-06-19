
package com.example.lyzweather;

import java.io.IOException;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.guanoweather.R;
import com.example.lyz.bean.MHttpEntity;
import com.example.lyz.bean.ResponseWrapper;
import com.example.lyz.bean.SendDataBean;
import com.google.gson.GsonBuilder;

public class LaunchActivity extends Activity {

	public static ResponseWrapper response;// 数据结构的对象
	public static final int succeed = 1;
	public static final int fail = 2;
	public static final int nonet = 3;
	public String normalDistrict;
	public String normalCity = "北京";
	public LocationClient mLocationClient = null;
	public BDLocationListener mListener;
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch_activity);

		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		pDialog.setMessage("定位中...");
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.show();
		mLocationClient = new LocationClient(this.getApplicationContext());
		mListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mListener);// 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);// 注：异步线程中不能设置UI

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sendRequest();
			}
		}).start();
	}

	private void sendRequest() {
		Log.i("TAG", System.currentTimeMillis()
				+ "System.currentTimeMillis()222");
		String getData = null;
		MHttpEntity mhe = null;
		try {
			SendDataBean.setCity(normalDistrict);
			Log.e("TAG", normalDistrict + "==>>normalDistrict");
			mhe = MHttpEntity.sendhttpclient(SendDataBean.getData());
			if (mhe.getHentity() != null) {
				getData = EntityUtils.toString(mhe.getHentity());
				GsonBuilder gson = new GsonBuilder();//
				response = gson.create().fromJson(getData,
						ResponseWrapper.class);
				Log.i("TAG", response.getError() + "-->response.getError()");
				if (response.getError() == -3) {
					Log.e("TAG", normalDistrict + "==>>normalDistrict222");
					SendDataBean.setCity(normalDistrict);
					mhe = MHttpEntity.sendhttpclient(SendDataBean.getData());
					if (mhe.getHentity() != null) {
						getData = EntityUtils.toString(mhe.getHentity());
						Log.i("TAG", getData + "-->getData");
					}
					if (response.getError() == -3) {
						SendDataBean.setCity(normalCity);
						Log.e("TAG", normalCity + "==>>normalCity");
						mhe = MHttpEntity
								.sendhttpclient(SendDataBean.getData());
						if (mhe.getHentity() != null) {
							Log.e("TAG", mhe.getHentity() + "==>>mhe.getHentity()");
							getData = EntityUtils.toString(mhe.getHentity());
							Log.i("TAG", getData + "-->getData");
						}
					}
				}
				mhe.getMessage().obj = getData;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		handler.sendMessage(mhe.getMessage());// 使用Handler对网络状态做处理
	}

	/**
	 * 对网络连接状态做处理
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(pDialog != null){
				pDialog.dismiss();
			}
			if (msg != null)
				switch (msg.arg1) {
				case succeed:// 与服务器连接成功，则传递数据并跳转
					Intent intent = new Intent(LaunchActivity.this,
							HomePagerActivity.class);
					if (msg.obj != null)
						intent.putExtra("weather_data", (String) msg.obj);
					intent.putExtra("normal_city", normalCity);
					startActivity(intent);
					finish();
					break;
				case fail:// 与服务器连接失败，弹出错误提示Toast
					Toast.makeText(LaunchActivity.this,
							getString(R.string.net_fail), Toast.LENGTH_SHORT)
							.show();
					Message Mesg = Message.obtain();
					Mesg.arg1 = nonet;// Handler机制，同抽奖类APP
					handler.sendMessageDelayed(Mesg, 2000);// 延迟发送
					break;
				case nonet:
					finish();// 2秒后关闭页面
					break;
				}
		}
	};

	/**
	 * 拦截返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				normalDistrict = location.getDistrict();
				normalCity = location.getCity();
				Log.i("TAG", normalCity+"---->>normalCity");
				if(normalCity == null){
					Toast.makeText(LaunchActivity.this, "定位失败，请检查网络", Toast.LENGTH_SHORT).show();
				}else{
					String[] str = normalCity.split("市");
					normalCity = str[0];
					if("北京".equals(normalCity)){
						Toast.makeText(LaunchActivity.this, "定位失败，默认为北京", Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	}
}
