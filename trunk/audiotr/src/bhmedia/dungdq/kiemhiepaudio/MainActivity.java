package bhmedia.dungdq.kiemhiepaudio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bhmedia.dungdq.kiemhiepaudio.database.FavouriteDB;
import bhmedia.dungdq.kiemhiepaudio.model.FavouriteList;
import bhmedia.dungdq.kiemhiepaudio.model.GlobalVariable;
import bhmedia.dungdq.kiemhiepaudio.process.ImageLoader;
import bhmedia.dungdq.kiemhiepaudio.process.StringUtils;
import bhmedia.dungdq.kiemhiepaudio.process.VietComparator;
import bhmedia.dungdq.kiemhiepaudio.services.ServiceHandler;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private static CharSequence mTitle;
	
	private static TextView textView;
	
	// URL lấy data JSON
    public static String url = "";
    
    // Key lấy danh sách audio theo category
    public static final String TAG_RESULT = "result";
    public static final String TAG_TRUYENID = "TruyenID";
    public static final String TAG_TENTRUYEN = "TruyenName";
    public static final String TAG_TACGIA = "Tacgia";
    public static final String TAG_SOTAP = "SoPhan";
    public static final String TAG_ANHDAIDIEN = "Truyen_CoverImage";
    
    // Key lấy chi tiết 1 audio
    public static final String TAG_RESULT_DETAIL = "result";
    public static final String TAG_TENTRUYEN_DETAIL = "TruyenName";
    
    JSONArray result;
    
    static ListView lv;
    //static IndexableListView lv;
    //ListTruyenAudioAdapter_Back adapter;
    
    ArrayList<HashMap<String, String>> truyenaudio_List = new ArrayList<HashMap<String, String>>();
    
    public static ArrayList<HashMap<String, String>> mDatabaseOfNames;
    
    public static FavouriteDB favouritedb;
    
    public ProgressDialog progressDialog;
    
    static SearchView searchView;
    
    static TextView tvTenTruyenChiTiet;
    
    static LinearLayout ll_player_control;
    
    static ImageButton btnPlay_control;
    
    static MenuItem search_button;
    
    @Override
    public void onBackPressed(){
    	clickBackButton();
    }

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		
		// Danh sách yêu thích
		//favouritedb = new FavouriteDB(this);

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		GlobalVariable.fmgr = getSupportFragmentManager();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		fragmentManager
				.beginTransaction()
				.replace(R.id.container, PlaceholderFragment.newInstance(position)).commit();
		
		// Gán giá trị position fragmet hiện tại phục vụ back
		GlobalVariable.last_position = position;
		
		// Trường hợp từ Player bấm Navigation
		if (GlobalVariable.player == true) GlobalVariable.player = false;
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 0:
			//mTitle = getString(R.string.title_section1);
			mTitle = "Kiếm hiệp Việt Nam";
			GlobalVariable.url = "http://clip.bhmedia.vn/api/audio?catid=17";
			//truyenaudio_List.clear();
			//new getListTruyenAudio().execute();
			new GetJSONData(MainActivity.this).new getListTruyenAudio().execute();
			break;
		case 1:
			//mTitle = getString(R.string.title_section2);
			mTitle = "Các tác giả khác";
			GlobalVariable.url = "http://clip.bhmedia.vn/api/audio?catid=14";
			//truyenaudio_List.clear();
			//new getListTruyenAudio().execute();
			new GetJSONData(MainActivity.this).new getListTruyenAudio().execute();
			break;
		case 2:
			//mTitle = getString(R.string.title_section3);
			mTitle = "Truyện Cổ Long";
			GlobalVariable.url = "http://clip.bhmedia.vn/api/audio?catid=11";
			//truyenaudio_List.clear();
			//new getListTruyenAudio().execute();
			new GetJSONData(MainActivity.this).new getListTruyenAudio().execute();
			break;
		case 3:
			//mTitle = getString(R.string.title_section4);
			mTitle = "Truyện Kim Dung";
			GlobalVariable.url = "http://clip.bhmedia.vn/api/audio?catid=8";
			//truyenaudio_List.clear();
			//new getListTruyenAudio().execute();
			new GetJSONData(MainActivity.this).new getListTruyenAudio().execute();
			break;
		case 4:
			//mTitle = getString(R.string.title_section4);
			mTitle = "Danh sách yêu thích";
			//truyenaudio_List.clear();
			//new getListTruyenAudioFavourite().execute();
			new GetJSONData(MainActivity.this).new getListTruyenAudioFavourite().execute();
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(false);
		//actionBar.setTitle(mTitle);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			
			search_button = menu.findItem(R.id.search);
			
			if (GlobalVariable.player == true) search_button.setVisible(false);
			
			// Associate searchable configuration with the SearchView
		    SearchManager searchManager =
		           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		    if (Integer.valueOf(android.os.Build.VERSION.SDK) < 11) {
		    	searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
		    	
		    	MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
		    		
		    		ActionBar actionBar = getSupportActionBar();
			        @Override
			        public boolean onMenuItemActionCollapse(MenuItem item) {
			            // Do something when collapsed
			        	actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_bg));
			            return true;  // Return true to collapse action view
			        }

			        @Override
			        public boolean onMenuItemActionExpand(MenuItem item) {
			            // Do something when expanded
			        	actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.actionbar_background));
			            return true;  // Return true to expand action view
			        }
			    });
		    }
		    else {
		    	searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		    	
		    	menu.findItem(R.id.search).setOnActionExpandListener(new OnActionExpandListener() {
					
		    		ActionBar actionBar = getSupportActionBar();
					@Override
					public boolean onMenuItemActionExpand(MenuItem item) {
						// TODO Auto-generated method stub
						actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.actionbar_background));
						return true;
					}
					
					@Override
					public boolean onMenuItemActionCollapse(MenuItem item) {
						// TODO Auto-generated method stub
						actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_bg));
						return true;
					}
				});
		    }
		    
		    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		    
		    SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
		    theTextArea.setTextColor(Color.WHITE);
		    
		    searchView.setOnQueryTextListener(new OnQueryTextListener() {
				
				@Override
				public boolean onQueryTextSubmit(String arg0) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean onQueryTextChange(String newText) {
					// TODO Auto-generated method stub
					//filtercheck = true;
					if (TextUtils.isEmpty(newText)) {
				        GetJSONData.adapter.getFilter().filter("");
				        Log.i("Nomad", "onQueryTextChange Empty String");
				        lv.clearTextFilter();
				    } else {
				        Log.i("Nomad", "onQueryTextChange " + newText.toString());
				        GetJSONData.adapter.getFilter().filter(newText.toString());
				    }
					return true;
				}
			});
			
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			lv = (ListView) rootView.findViewById(R.id.lv_truyenaudio);
			
			/**
			 * Code 07/04/2014
			 * Add player control on MainActivity
			 */
			
			ll_player_control = (LinearLayout) rootView.findViewById(R.id.ll_player_control);
			//if (GlobalVariable.player == true) ll_player_control.setVisibility(View.VISIBLE);
			if (GlobalVariable.mp != null) ll_player_control.setVisibility(View.VISIBLE);
			else ll_player_control.setVisibility(View.GONE);
			
			GlobalVariable.player = false;
			
			ImageButton btnPrevious_control = (ImageButton) rootView.findViewById(R.id.btnPrevious_control);
			btnPlay_control = (ImageButton) rootView.findViewById(R.id.btnPlay_control);
			
			if (GlobalVariable.mp != null) {
				if (GlobalVariable.mp.isPlaying()) btnPlay_control.setBackgroundResource(android.R.drawable.ic_media_pause);
				else btnPlay_control.setBackgroundResource(android.R.drawable.ic_media_play);
			} else btnPlay_control.setBackgroundResource(android.R.drawable.ic_media_pause);
			
			ImageButton btnNext_control = (ImageButton) rootView.findViewById(R.id.btnNext_control);
			
			btnPrevious_control.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					btnPlay_control.setBackgroundResource(android.R.drawable.ic_media_pause);
					
					if(GlobalVariable.currentSongIndex > 0){
						PlayListAudio.playSong(GlobalVariable.currentSongIndex - 1);
						GlobalVariable.currentSongIndex = GlobalVariable.currentSongIndex - 1;
					}else{
						// play last song
						PlayListAudio.playSong(GlobalVariable.audioList.size() - 1);
						GlobalVariable.currentSongIndex = GlobalVariable.audioList.size() - 1;
					}
				}
			});
			
			btnPlay_control.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// check for already playing
					if(GlobalVariable.mp.isPlaying()){
						if(GlobalVariable.mp!=null){
							GlobalVariable.mp.pause();
							// Changing button image to play button
							btnPlay_control.setBackgroundResource(android.R.drawable.ic_media_play);
						}
					}else{
						// Resume song
						if(GlobalVariable.mp!=null){
							GlobalVariable.mp.start();
							// Changing button image to pause button
							btnPlay_control.setBackgroundResource(android.R.drawable.ic_media_pause);
						}
					}
				}
			});
			
			btnNext_control.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					btnPlay_control.setBackgroundResource(android.R.drawable.ic_media_pause);
					
					// check if next song is there or not
					if(GlobalVariable.currentSongIndex < (GlobalVariable.audioList.size() - 1)){
						PlayListAudio.playSong(GlobalVariable.currentSongIndex + 1);
						GlobalVariable.currentSongIndex = GlobalVariable.currentSongIndex + 1;
					}else{
						// play first song
						PlayListAudio.playSong(0);
						GlobalVariable.currentSongIndex = 0;
					}	
				}
			});
			
			/**
			 * End code 07/04/2014
			 */
			
			/**
			 * Code 10/04/2014
			 */
			
			// Show search button
			if (search_button != null) search_button.setVisible(true);
			
			/**
			 * End code 10/04/2014
			 */
			
			/*View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);*/
			/*textView = (TextView) rootView
					.findViewById(R.id.section_label);*/
			/*textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));*/
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}
	
	public static class PlaceholderFragmentPlayer extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		
		public static PlaceholderFragmentPlayer newFragment() {
			PlaceholderFragmentPlayer fragment = new PlaceholderFragmentPlayer();
			return fragment;
		}

		public PlaceholderFragmentPlayer() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_player, container, false);
			
			/**
			 * Code 10/04/2014
			 */
			
			// Hide search button
			search_button.setVisible(false);
			
			/**
			 * End code 10/04/2014
			 */
			
			tvTenTruyenChiTiet = (TextView)rootView.findViewById(R.id.tvTenTruyenChiTiet);
			
			ImageButton btnBack = (ImageButton)rootView.findViewById(R.id.btnBack);
			
			btnBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					GlobalVariable.fmgr.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(GlobalVariable.last_position)).commit();
		    		//GlobalVariable.player = false;
				}
			});

			return rootView;
		}
	}
	
	/**
	 * Code 07/04/2014
	 * Add Back Event Click
	 */
	
	public void clickBackButton() {
		if (GlobalVariable.player == true) {
    		GlobalVariable.fmgr.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(GlobalVariable.last_position)).commit();
    		//GlobalVariable.player = false;
    	}
    	else {
    		/*new AlertDialog.Builder(MainActivity.this)
    	    .setTitle("Thoát ứng dụng")
    	    .setMessage("Bạn có muốn thoát khỏi ứng dụng?")
    	    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) { 
    	        	MainActivity.this.finish();
    	    		System.exit(0);
    	        }
    	     })
    	    .setNegativeButton("Không", null)
    	    .show();*/
    		//new PlayListAudio(this).mHandler.removeCallbacksAndMessages(null);
    		//GlobalVariable.mp.stop();
    		//GlobalVariable.mp.release();
    		super.finish();
    	}
	}
	
	public class NetworkChangeReceiver extends BroadcastReceiver {

		  @Override
		  public void onReceive(final Context context, final Intent intent) {
		    final ConnectivityManager connMgr = (ConnectivityManager) 
		    context.getSystemService(Context.CONNECTIVITY_SERVICE);

		    final android.net.NetworkInfo wifi = 
		    connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		    final android.net.NetworkInfo mobile = 
		    connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		    if (wifi.isAvailable()) {
		    	//Do something
		    	Toast.makeText(context, "Ket noi wifi", Toast.LENGTH_SHORT).show();
		    }
		      
		    if (mobile.isAvailable()) {
		    	//Do something else
		    	Toast.makeText(context, "Ket noi 3G", Toast.LENGTH_SHORT).show();
		    }
	    }
	}
}
