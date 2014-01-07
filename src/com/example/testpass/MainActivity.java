package com.example.testpass;








import redis.clients.jedis.Jedis;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Button startS, stopS, pass;
	private TextView et;
	private MyReceiver receiver;
	private IntentFilter filter;
	Jedis jedis = new Jedis("192.168.103.18");
	String value="";
	@Override
	public void onDestroy() {
		this.unregisterReceiver(receiver);
		// this.stopService(this.intentService);
		super.onDestroy();
	}

	public void refreshText()
	{
		
		value = jedis.lpop("forpass");
		
		if(value!=null && !value.trim().equals(""))
		{
			this.et.setText(value);
		}
		else
		{
			this.et.setText("--");
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		jedis.auth("123456redis");
		value = jedis.lpop("forpass");
		
		System.out.println("after redis connect");
		receiver = new MyReceiver();
		filter = new IntentFilter();
		filter.addAction("android.intent.action.MY_RECEIVER");
		registerReceiver(receiver, filter);
		this.startS = (Button) findViewById(R.id.button2);
		this.stopS = (Button) findViewById(R.id.button3);
		this.pass =  (Button) findViewById(R.id.button1);
		this.et = (TextView) findViewById(R.id.textView1);
		if(value!=null && !value.trim().equals(""))
		{
			this.et.setText(value);
		}
		else
		{
			this.et.setText("--");
		}
		
		
		this.pass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				Jedis jedis = new Jedis("192.168.103.18");
//				jedis.auth("123456redis");
				jedis.rpush("passed_1", value);
				System.out.println("ss "+ value);
				jedis.disconnect();
			}

		});

		
		this.startS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PushService.actionStart(getApplicationContext());
				System.out.println("Start");
			}

		});
		
		
		this.stopS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PushService.actionStop(getApplicationContext());
				System.out.println("Stop");
			}

		});
		
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
					+ intent.getStringExtra("msg"));
			
			refreshText();
		}
	}
}
