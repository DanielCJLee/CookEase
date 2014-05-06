package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Fragment {

    public final static String EXTRA_MESSAGE = "edu.berkeley.cs160.DeansOfDesign.MESSAGE";
    private static TextView instructionText = null;
    
    public String water = "Water boiling";
    public String microDone = "Microwave Done";
    public String microExplo = "Microwave Explosion";
    public String other = "Other Kitchen Tasks";
    String greyBg = "#84a689";
    String purpleBg = "#a684a1";
    String green ="#7BF49B";
    String gray = "#F1D66A";
    String white = "#ffffff";
    //private Mail sendMail;
    String sendOkay = "";

    public String friends_title = "Who do you want to alert?";
    public HashMap<String, Boolean> tasksToSelected = new HashMap<String, Boolean>();
    public ListView taskList;
    private StableArrayAdapter adapter = null;
    private TabActivity act;
    //public boolean inHomeScreen;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		act = (TabActivity) this.getActivity();
		act.setContentView(R.layout.activity_main);
		// Make a mail object to send email with
	    //sendMail = new Mail("cookease.app@gmail.com", "deansofdesign");
		
		// Restore preferences
	    SharedPreferences settings = act.getSharedPreferences("settings", 0);
	    tasksToSelected.put(water, settings.getBoolean(microDone, false));
	    tasksToSelected.put(microDone, settings.getBoolean(microDone, false));
	    tasksToSelected.put(microExplo,settings.getBoolean(microExplo, false));
	    
		// FOR TESTING ONLY, REMOVE LATER: click the instructions for alert
		instructionText = (TextView) act.findViewById(R.id.textView6);
		instructionText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                	act.alert(microDone); //hardcoded microDone for testing
                	act.alert(microExplo);
                }
                
            }
        );
		
		//TODO: change if-statement to general case below once
		//everything's working
		//if (tasksSelected()) {
		//	boilingWaterDetector.startDetection();
		//}
		if (tasksToSelected.get(water)) {
			act.boilingWaterDetector.startDetection();
		}
		
		taskList = (ListView) act.findViewById(R.id.listView1);
		String tasks[] ={water, microDone, microExplo};
		final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < tasks.length; ++i) {
	      list.add(tasks[i]); 
	    }


        adapter = new StableArrayAdapter(act,
        		R.layout.custom_row, list);
	    
	    taskList.setAdapter(adapter);
	    taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	    	@SuppressLint("NewApi")
	    	@Override
	    	public void onItemClick(AdapterView<?> parent, final View view,
	    			int position, long id) {
	    		CheckedTextView item = (CheckedTextView) view;
	    		String itemText = (String) parent.getItemAtPosition(position);
	    		if (tasksToSelected.get(itemText)) { //selected already
	    			item.setBackgroundColor(Color.parseColor(gray));
	    			item.setChecked(false);
	    			tasksToSelected.put(itemText, false);
	    			//TODO: change if-statement to general case below once
	    			//everything's working
	    			//if (tasksSelected()) {
	    			//	boilingWaterDetector.stopDetection();
	    			//}
	    			if (itemText == water) {
	    				act.boilingWaterDetector.stopDetection();
	    			}
	    		} else { //not selected yet
	    			item.setBackgroundColor(Color.parseColor(green));
	    			item.setChecked(true);
	    			tasksToSelected.put(itemText, true);
	    			//TODO: change if-statement to general case below once
	    			//everything's working
	    			//if (tasksSelected()) {
	    			//	boilingWaterDetector.startDetection();
	    			//}
	    			if (itemText == water) {
	    				act.waterAlerted = false;
	    				act.boilingWaterDetector.startDetection();
	    			}
	    		}
	    	}
	    });   
	    taskList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    taskList.setItemChecked(0, tasksToSelected.get(water));
	    taskList.setItemChecked(1, tasksToSelected.get(microDone));
	    taskList.setItemChecked(2, tasksToSelected.get(microExplo));
	    
	    //Disable/Enable task list based on whether app is listening
	    final ImageView mic = (ImageView) act.findViewById(R.id.img1);
	    mic.setOnClickListener(new View.OnClickListener() {
	    	   //@Override
	    	   public void onClick(View v) {
	    		   RelativeLayout taskLayout = (RelativeLayout) act.findViewById(R.id.tasktext);
	    	      //if listening, change mic to gray + gray out listview
	    		   if (act.isListening) {
	    			   int color = 0xFFFFFFFF;
	    			   int transparent = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color));
	    			   mic.setColorFilter(transparent);
	    			   //grey out area
	    			   taskLayout.setBackgroundColor(Color.parseColor("#85856F"));
	    			   taskLayout.setAlpha(0.8f);
	    			   //set instructiontextview unclickable
	    			   TextView tv = (TextView) act.findViewById(R.id.textView6);
	    			   tv.setClickable(false);
	    			   tv.setAlpha(0.25f);
	    			   //set listview unclickable
	    			   ListView lv = (ListView) act.findViewById(R.id.listView1);
	    			   lv.setEnabled(false);
	    			   lv.setAlpha(0.25f);
	    			   act.isListening = false;
	    		   } else {//else change mic color to red, ungray out listview
	    			   mic.setColorFilter(Color.parseColor("#E02200"));
	    			   taskLayout.setBackgroundColor(Color.parseColor("#F1D66A"));
	    			   taskLayout.setAlpha(0.7f);
	    			   //set instructiontextview clickable
	    			   TextView tv = (TextView) act.findViewById(R.id.textView6);
	    			   tv.setClickable(true);
	    			   tv.setAlpha(1);
	    			   //set listview clickable
	    			   ListView lv = (ListView) act.findViewById(R.id.listView1);
	    			   lv.setEnabled(true);
	    			   lv.setAlpha(1);
	    			   act.isListening = true;
	    		   }
	    		  
	    	   }        
	    	});
	    
	   return inflater.inflate(R.layout.activity_main, container, false);
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {
	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	    Context c;
	    
	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      this.c = context;
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }
	    
	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	View view = convertView;
	    	 if( view == null ){
	    	        //We must create a View:
	    		 	LayoutInflater inflater=act.getLayoutInflater();
	    	        view = inflater.inflate(R.layout.custom_row, parent, false);
	    		 	
	    	 }
	    	 CheckedTextView temp = (CheckedTextView) view.findViewById(R.id.text1);
	    	 Drawable dr = null;
	    	 Bitmap bitmap = null;
	    	 Drawable d = null;
	    	 if (position == 0) {
	    		 temp.setText(water);
	    		 dr = getResources().getDrawable(R.drawable.potboil);
	    		 bitmap = ((BitmapDrawable) dr).getBitmap();
	    		 // Scale it to 67 x 67
	    		 d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 67, 67, true));
	    	 } else if (position == 1) {
	    		 temp.setText(microDone);
	    		 dr = getResources().getDrawable(R.drawable.microdone);
	    		 bitmap = ((BitmapDrawable) dr).getBitmap();
	    		 // Scale it to 100 x 67
	    		 d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 67, true));
	    	 } else if (position == 2) {
	    		 temp.setText(microExplo);
	    		 dr = getResources().getDrawable(R.drawable.microexplo);
	    		 bitmap = ((BitmapDrawable) dr).getBitmap();
	    		 // Scale it to 100 x 67
	    		 d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,100, 67, true));
	    	 }
    		 temp.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
	    	if ((position == 0 && tasksToSelected.get(water)) ||
	    			(position == 1 && tasksToSelected.get(microDone)) ||
	    			(position == 2 && tasksToSelected.get(microExplo))) {
	    				view.setBackgroundColor(Color.parseColor(green));
	    	} else {
	    		view.setBackgroundColor(Color.parseColor(gray));
	    	}
	    	adapter.notifyDataSetChanged();
	    	return view;
	    }
	    
	    @Override
	    public boolean isEnabled(int position) {
	        return act.isListening;
	    }

	  }


	public boolean tasksSelected() {
		// TODO (dhaas): this logic needs work.
		Set<String> temp = tasksToSelected.keySet();
		Iterator<String> iter = temp.iterator();
		while (iter.hasNext()) {
			if (tasksToSelected.get(iter.next())) {
				act.isListening = true;
				break;
			} else {
				act.isListening = false;
			}
		}
		return act.isListening;
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
		
       // Restore preferences
       SharedPreferences settings = act.getSharedPreferences("settings", 0);
       tasksToSelected = new HashMap<String, Boolean>();
       tasksToSelected.put(water, settings.getBoolean(water, false));
	   tasksToSelected.put(microDone, settings.getBoolean(microDone, false));
	   tasksToSelected.put(microExplo,settings.getBoolean(microExplo, false));

    }

    @Override
	public void onPause(){
       super.onPause();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = act.getSharedPreferences("settings", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean(water, tasksToSelected.get(water));
      editor.putBoolean(microDone, tasksToSelected.get(microDone));
      editor.putBoolean(microExplo, tasksToSelected.get(microExplo));

      // Commit the edits!
      editor.commit();
    }
    

    @Override
	public void onDestroy(){
    	super.onDestroy();
    }
}
