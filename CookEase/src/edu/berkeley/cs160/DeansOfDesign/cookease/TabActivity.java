package edu.berkeley.cs160.DeansOfDesign.cookease;

import edu.berkeley.cs160.DeansOfDesign.cookease.BoilingWaterDetector.OnBoilingEventListener;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.view.Window;

public class TabActivity extends Activity implements OnBoilingEventListener {
	
	// For handling tabs
	ActionBar.Tab tab1, tab2, tab3;
	Fragment fragmentTab1 = new MainActivity();
	Fragment fragmentTab2 = new NotificationsActivity();
	Fragment fragmentTab3 = new AnalyticsActivity();
    
    // For audio processing
    protected BoilingWaterDetector boilingWaterDetector;
    protected boolean waterAlerted = false;
    protected boolean isListening;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_tab);
  
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        tab1 = actionBar.newTab().setText("Home");
        tab2 = actionBar.newTab().setText("Notifications");
        tab3 = actionBar.newTab().setText("Analytics");
        final class MyTabListener implements ActionBar.TabListener {
        	Fragment fragment;
        	
        	public MyTabListener(Fragment fragment) {
        		this.fragment = fragment;
        	}


			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				ft.replace(R.id.fragment_container, fragment);
				setContentView(R.layout.activity_tab);
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				ft.remove(fragment);
				setContentView(R.layout.activity_tab);
			}
        }
        tab1.setTabListener(new MyTabListener(fragmentTab1));
        tab2.setTabListener(new MyTabListener(fragmentTab2));
        tab3.setTabListener(new MyTabListener(fragmentTab3));
        
        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);
        
        // Set up audio processing.
		boilingWaterDetector = new BoilingWaterDetector(this, 0.1);
		boilingWaterDetector.setOnBoilingEventListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tab, menu);
		return true;
	}

	// Stop listening for things!
    @Override
	public void onDestroy(){
    	super.onDestroy();
    	boilingWaterDetector.stopDetection();
    }
    
	@Override
	public void processBoilingEvent() {
		if (!waterAlerted) {
			waterAlerted = true;
			runOnUiThread(new Runnable() {
				public void run() {
					// TODO(dhaas): What if main tab isn't selected? How do we handle alerts?
					MainActivity mainTab = (MainActivity) fragmentTab1;
					mainTab.alert(mainTab.water); 
				}
			});
		}
	}
	
}
