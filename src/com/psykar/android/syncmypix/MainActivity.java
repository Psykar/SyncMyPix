//
//    MainActivity.java is part of SyncMyPix
//
//    Authors:
//        Neil Loknath <neil.loknath@gmail.com>
//
//    Copyright (c) 2009 Neil Loknath
//
//    SyncMyPix is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    SyncMyPix is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with SyncMyPix.  If not, see <http://www.gnu.org/licenses/>.
//

package com.nloko.android.syncmypix;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import com.android.providers.contacts.PhotoStore;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.nloko.android.Log;
import com.nloko.android.LogCollector;
import com.nloko.android.LogCollectorNotifier;
import com.nloko.android.Utils;
import com.nloko.android.syncmypix.views.ConfirmSyncDialog;
import com.nloko.android.syncmypix.facebook.*;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final static String TAG = "MainActivity";
	private final static String DEV_EMAIL = "syncmypix@andrew67.com";
	private final static int SOCIAL_NETWORK_LOGIN = 0;

	private final int ABOUT_DIALOG = 2;
	private final int CONFIRM_DIALOG = 3;

	private WeakReference<SyncService> mSyncService;
	private boolean mSyncServiceBound = false;
	
	PhotoStore ps = null;
	public byte[] cropPhoto = null;
	
	Facebook fbClient = null;
	static MainActivity sMainActivity;
	public static MainActivity GetInstance()
	{
		return sMainActivity;
	}
	
	public PhotoStore GetPhotoStore() { return ps; }
	
	public static Class<FacebookSyncService> getSyncSource(Context context)
	{		
		return FacebookSyncService.class;
	}
	
	public static <T extends SyncService> boolean isLoggedInFromSyncSource(Context context, Class<T> source)
	{
		try {
			Method m = source.getMethod("isLoggedIn", Context.class);
			return (Boolean) m.invoke(null, context);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	boolean loggedIn = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sMainActivity = this;
        
		fbClient = new Facebook(getResources().getString(R.string.facebook_api_key));
        
        final MainActivity self = this;
        ImageButton sync = (ImageButton) findViewById(R.id.syncButton);
        sync.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					if(!loggedIn || fbClient.getAccessToken() == null)
					{
						loggedIn = true;
						fbClient.authorize(self, new DialogListener() {
				            public void onComplete(Bundle values) {sync();}
				            	
				            public void onFacebookError(FacebookError error) {try
							{
				            	loggedIn = false;
				            	Toast.makeText(self, "Error on facebook sync again!", Toast.LENGTH_SHORT).show();
								fbClient.logout(self);
							}
							catch(MalformedURLException e)
							{
								e.printStackTrace();
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}}
	
				            public void onError(DialogError e) {try
							{
				            	loggedIn = false;
				            	Toast.makeText(self, "Error on facebook sync again!", Toast.LENGTH_SHORT).show();
								fbClient.logout(self);
							}
							catch(MalformedURLException e1)
							{
								e1.printStackTrace();
							}
							catch(IOException e1)
							{
								e1.printStackTrace();
							}}
	
				            public void onCancel() {loggedIn = false;}
				        });
					}
					else
						sync();
			}
        });
        
        ImageButton settings = (ImageButton) findViewById(R.id.settingsButton);
        settings.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
				startActivity(i);
			}
        });
        
        ImageButton results = (ImageButton) findViewById(R.id.resultsButton);
        results.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showResults();
			}
        });
        
        ImageButton about = (ImageButton) findViewById(R.id.aboutButton);
        about.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(ABOUT_DIALOG);
			}
        });
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fbClient.authorizeCallback(requestCode, resultCode, data);
		
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		switch(requestCode) {
			case SOCIAL_NETWORK_LOGIN:
				sync();
				break;
		}
    }
    
    @SuppressWarnings("unused")
	private void sendLog()
    {
    	final LogCollector collector = new LogCollector();
    	collector.setNotifier(new LogCollectorNotifier() {
			public void onComplete() {
				collector.appendMessage(getString(R.string.main_logMsg));
				String log = collector.getLog();
				if (log != null) {
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	            	emailIntent.setType("text/html");
	            	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
	                  new String[]{ DEV_EMAIL } );
	
	            	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Log" );
	            	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, log);
	            	
	            	startActivity(Intent.createChooser(emailIntent, "Send Log via"));
				}
			}

			public void onError() {
				Toast.makeText(getApplicationContext(), 
						R.string.main_error_Logerror, 
						Toast.LENGTH_LONG).show();
			}
    	});
    	collector.collect();
    }
    
    private void sync()
    {
    	if (!Utils.hasInternetConnection(getApplicationContext())) {
    		Toast.makeText(getApplicationContext(), R.string.syncservice_networkerror, Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	SyncService service = null;
    	if (mSyncService != null) {
    		service = mSyncService.get();
    	}
    	
    	if (service == null || !service.isExecuting()) {
    		startService(new Intent(getApplicationContext(), getSyncSource(getApplicationContext())));
    		startActivity(new Intent(getApplicationContext(), SyncProgressActivity.class));
    	}
    }
    
    private void showResults()
    {
    	Intent i = new Intent(getApplicationContext(), SyncResultsActivity.class);
    	i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    	startActivity(i);
    }

	@Override
	protected void onStart() {
		super.onStart();
		
		if (!mSyncServiceBound) {
			Intent i = new Intent(getApplicationContext(), getSyncSource(getApplicationContext()));
			mSyncServiceBound = bindService(i, mSyncServiceConn, Context.BIND_AUTO_CREATE);
		}
	}
    
    @Override
	protected void onStop() {
		super.onStop();
		if (mSyncServiceBound) {
			Log.d(TAG, "unbinding service");
			unbindService(mSyncServiceConn);
			mSyncServiceBound = false;
		}
	}
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		mSyncServiceConn = null;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		Log.d(TAG, "FINALIZED");
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			/*case R.id.sendLogButton:
				sendLog();
				return true;*/
	    }
		
	    return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case ABOUT_DIALOG:
				return createAboutDialog();
				
			case CONFIRM_DIALOG:
				ConfirmSyncDialog dialog = new ConfirmSyncDialog(this);
				dialog.setProceedButtonListener(new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						sync();
					}
				});
				
				dialog.setCancelButtonListener(null);
				return dialog;
		}
		
		return super.onCreateDialog(id);
	}

	private Dialog createAboutDialog()
	{
		// get version information
		PackageManager pm = getPackageManager(); 
		PackageInfo pi;
		String version = null;
		try {
			pi = pm.getPackageInfo(getPackageName(), 0);
			 version = pi.versionName + "AL";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
		 
		Dialog about = new Dialog(this);
		about.requestWindowFeature(Window.FEATURE_NO_TITLE);
		about.setContentView(R.layout.about);
		
		// dynamically add a TextView for version
		LinearLayout layout = (LinearLayout) about.findViewById(R.id.about_layout);
		if (version != null) {
			TextView versionView = new TextView(getBaseContext());
			versionView.setText(" Version " + version);
			versionView.setTextSize(10);
			
			layout.addView(versionView, 1);
		}
				
		return about;
	}
	
    private ServiceConnection mSyncServiceConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	Log.d(TAG, "onServiceConnected");
        	mSyncService = new WeakReference<SyncService>(((SyncService.LocalBinder)service).getService());
    		if (mSyncService != null) {
    			SyncService s = mSyncService.get();
    			if (s != null && s.isExecuting()) {
    				Intent i = new Intent(s.getApplicationContext(), SyncProgressActivity.class);
    				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    				startActivity(i);
    			}
    		}
        }

        public void onServiceDisconnected(ComponentName className) {
        	Log.d(TAG, "onServiceDisconnected");
        	mSyncService = null;
        }
    };
    
	public Facebook GetFacebookClient()
	{
		return fbClient;
	}
}
