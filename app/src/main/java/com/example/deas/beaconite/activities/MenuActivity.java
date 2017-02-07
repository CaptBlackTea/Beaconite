package com.example.deas.beaconite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.deas.beaconite.R;

public class MenuActivity extends AppCompatActivity {

	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
	}

	// Menu icons are inflated just as they were with actionbar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.action_mainMenu:
				intent = new Intent(this, MainActivity.class);
				this.startActivity(intent);
				return true;
			case R.id.action_relocationMenu:
				intent = new Intent(this, RelocationActivity.class);
				this.startActivity(intent);
				return true;
			case R.id.action_baseGameMenu:
				intent = new Intent(this, BaseGameActivity.class);
				this.startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
