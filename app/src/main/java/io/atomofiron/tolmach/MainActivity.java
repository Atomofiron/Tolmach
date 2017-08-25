package io.atomofiron.tolmach;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Locale;

import io.atomofiron.tolmach.fragments.AboutFragment;
import io.atomofiron.tolmach.fragments.MainFragment;
import io.atomofiron.tolmach.utils.Lang;

public class MainActivity extends AppCompatActivity {
	private SharedPreferences sp;
	private FragmentManager fragmentManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		sp = I.sp(this);

		ArrayList<Lang> srcLangs = getSrcLangs();
		String defaultCode = getResources().getConfiguration().locale.getLanguage();
		fragmentManager = getSupportFragmentManager();
		if (fragmentManager.findFragmentById(R.id.container) == null)
			fragmentManager.beginTransaction()
					.replace(R.id.container, MainFragment.newInstance(srcLangs, getSrcLang(srcLangs, defaultCode)))
					.commitAllowingStateLoss();
	}

	private Lang getSrcLang(ArrayList<Lang> srcLangs, String def) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			LocaleList localeList = getResources().getConfiguration().getLocales();
			for (int i = 0; i < localeList.size(); i++)
				for (Lang lang : srcLangs)
					if (lang.code.equals(localeList.get(i).getLanguage()))
						return lang;
		} else
			for (Lang lang : srcLangs)
				if (lang.code.equals(def))
					return lang;

		return srcLangs.get(0);
	}

	private static ArrayList<Lang> getSrcLangs() {
		ArrayList<Lang> langs = new ArrayList<>();

		for (String code : I.SPEECH_CODES)
			langs.add(new Lang(code, code, code));

		for (Locale locale : Locale.getAvailableLocales())
			for (Lang lang : langs)
				if (lang.code.equals(locale.getLanguage())) {
					lang.name = locale.getDisplayLanguage();
					lang.country = locale.getCountry();
				}

		return langs;
	}

	private void addFragment(Fragment fragment) {
		fragmentManager.beginTransaction()
				.addToBackStack(null)
				.replace(R.id.container, fragment)
				.commitAllowingStateLoss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.about:
				addFragment(new AboutFragment());
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@TargetApi(Build.VERSION_CODES.M)
	public void showPermissionDialog() {
		new AlertDialog.Builder(this)
				.setCancelable(false)
				.setMessage(R.string.permission_request)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						requestPermissions(new String[]{ Manifest.permission.RECORD_AUDIO }, I.PERMISSION_REQUEST_CODE);
					}
				}).setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).create().show();
	}


	@TargetApi(Build.VERSION_CODES.M)
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
			if (shouldShowRequestPermissionRationale(permissions[0]))
				showPermissionDialog();
			else
				Snackbar.make(findViewById(R.id.container), R.string.no_mic_permission, Snackbar.LENGTH_SHORT).show();
		}
	}
}
