package bhmedia.dungdq.kiemhiepaudio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import bhmedia.dungdq.kiemhiepaudio.model.FavouriteList;
import bhmedia.dungdq.kiemhiepaudio.model.GlobalVariable;
import bhmedia.dungdq.kiemhiepaudio.model.MyPlayer;
import bhmedia.dungdq.kiemhiepaudio.model.MyTimePickerDialog;
import bhmedia.dungdq.kiemhiepaudio.process.Utilities;

import bhmedia.dungdq.kiemhiepaudio.customadapter.ListTruyenAudioAdapter;
import bhmedia.dungdq.kiemhiepaudio.database.FavouriteDB;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.SimpleAdapter;
import android.app.TimePickerDialog;

public class PlayListAudio {
	
	Context context;
	View rootView ;
	
	/*public int currentSongIndex = 0;
	public ArrayList<HashMap<String, String>> audioList = new ArrayList<HashMap<String, String>>();*/
	ArrayList<HashMap<String, String>> audioListData = new ArrayList<HashMap<String, String>>();
	//private  MediaPlayer mp;
	static ImageButton btnPlay;
	ImageButton btnPrevious;
	ImageButton btnNext;
	ImageButton btnPlayList;
	ImageButton btnVolume;
	ImageButton btnFavourite;
	ImageButton btnTimer;
	ImageButton btnShare;
	TextView tv_Content;
	TextView tv_TapTruyenAudio;
	static SeekBar audioProgressBar;
	SeekBar volumeBar;
	static TextView audioCurrentDurationLabel;
	static TextView audioTotalDurationLabel; 
	ViewSwitcher mViewSwitcher;
	ListView lv_playlist;
	public static Handler mHandler = new Handler();
	public static Handler mHandler_timer = new Handler();
	private static Utilities utils = new Utilities();
	
	Boolean playlist_display = false;
	Boolean mute = false;
	
	String TENTRUYEN_TAP = "";
	
	static AudioManager am;
	private AudioFocusHelper mAudioFocusHelper;
	
	// Volume
	int Volume = 0;
    int curVolume, maxVolume, save_curVolume;
    
    // Set timer
    int hour, minute, second; // Code 11/04/2014
	static int total_time;
	static Boolean timer_set = false; // Cờ set timer
	static Runnable setTimer;
	
	public PlayListAudio(final Activity activity) {
		this.context = activity;
		LayoutInflater inflater = LayoutInflater.from(activity);
		rootView = inflater.inflate(R.layout.fragment_player, null);
		
		btnPlay= (ImageButton)activity.findViewById(R.id.btnPlay);
		
		//btnPlay = (ImageButton)rootView.findViewById(R.id.btnPlay);
		btnPrevious = (ImageButton)activity.findViewById(R.id.btnPrevious);
		btnNext = (ImageButton)activity.findViewById(R.id.btnNext);
		
		btnPlayList = (ImageButton)activity.findViewById(R.id.btnPlaylist);
		
		audioCurrentDurationLabel = (TextView)activity.findViewById(R.id.audioCurrentDurationLabel);
		audioTotalDurationLabel = (TextView)activity.findViewById(R.id.audioTotalDurationLabel);
		
		audioProgressBar = (SeekBar)activity.findViewById(R.id.audioProgressBar);
		volumeBar = (SeekBar)activity.findViewById(R.id.volumeBar);
		btnVolume = (ImageButton)activity.findViewById(R.id.btnVolume);
		
		btnTimer = (ImageButton)activity.findViewById(R.id.btnTimer);
		
		tv_Content = (TextView)activity.findViewById(R.id.tv_Content);
		
		mViewSwitcher = (ViewSwitcher)activity.findViewById(R.id.profileSwitcher);
		
		lv_playlist = (ListView)activity.findViewById(R.id.lv_playlist);
		tv_TapTruyenAudio = (TextView)activity.findViewById(R.id.tv_TapTruyenAudio);
		
		btnFavourite = (ImageButton)activity.findViewById(R.id.btnFavourite);
		btnShare = (ImageButton)activity.findViewById(R.id.btnShare);
		
		GlobalVariable.mp = MyPlayer.getInstance();
		
		GlobalVariable.currentSongIndex = 0;
		
		GlobalVariable.audioList = GetJSONData.truyenaudiodetail_List;
		
		if (playlist_display == false) {
			mViewSwitcher.setDisplayedChild(0);
		}
		
		// Load playlist
		new getPlaylist().execute();
		
		// Check bookmark favouritelist
		if (GetJSONData.favouritedb.getContactsCount() != 0) {
        	if (GetJSONData.favouritedb.getFavouriteItem(GetJSONData.truyenID_chitiet) != null) {
            	btnFavourite.setBackgroundResource(R.drawable.star_2_button);
            }
            else btnFavourite.setBackgroundResource(R.drawable.star_1_button);
        }
        else btnFavourite.setBackgroundResource(R.drawable.star_1_button);
        
        btnFavourite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//if (MainActivity.favouritedb.getContactsCount() != 0) {
		        	if (GetJSONData.favouritedb.getFavouriteItem(GetJSONData.truyenID_chitiet) == null) {
		            	btnFavourite.setBackgroundResource(R.drawable.star_2_button);
		            	
		            	Log.d("Insert: ", "Inserting .."); 
				        GetJSONData.favouritedb.addFavourite(new FavouriteList(GetJSONData.truyenID_chitiet, GetJSONData.tentruyen_chitiet, GetJSONData.tacgia_chitiet, GetJSONData.sotap_chitiet, GetJSONData.anhdaidien_chitiet));
				        
				        Toast.makeText(context, "Đã thêm vào Danh sách yêu thích", Toast.LENGTH_SHORT).show();
		            }
		            else {
		            	btnFavourite.setBackgroundResource(R.drawable.star_1_button);
		            	
		            	Log.d("Delete: ", "Deleting ..");
		            	GetJSONData.favouritedb.deleteFavourite(GetJSONData.truyenID_chitiet);
		            	
		            	Toast.makeText(context, "Đã xóa khỏi Danh sách yêu thích", Toast.LENGTH_SHORT).show();
		            }
		        //}
		        //else btn_bookmark.setBackgroundResource(R.drawable.star_1);
				//btn_bookmark.setBackgroundResource(R.drawable.star_2);
			}
		});
		
		// Set volume
		//am = (AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusHelper = new AudioFocusHelper(activity);
        mAudioFocusHelper.requestFocus();
        
        GlobalVariable.IsPlaying = false;
        
		maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		curVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		volumeBar.setMax(maxVolume);
		volumeBar.setProgress(curVolume);
		
		btnVolume.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mute == false) {
					mute = true;
					if (Volume != 0) save_curVolume = Volume;
					else save_curVolume = curVolume;
					am.setStreamMute(AudioManager.STREAM_MUSIC, true);
					volumeBar.setProgress(0);
					btnVolume.setBackgroundResource(R.drawable.sound_off);
				}
				else {
					mute = false;
					am.setStreamMute(AudioManager.STREAM_MUSIC, false);
					volumeBar.setProgress(save_curVolume);
					btnVolume.setBackgroundResource(R.drawable.sound_on);
				}
			}
		});
		
		volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Volume: " + Integer.toString(Volume), Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
	            Volume = progress;
	            
	            if (Volume == 0) btnVolume.setBackgroundResource(R.drawable.sound_off);
	            else btnVolume.setBackgroundResource(R.drawable.sound_on);
			}
		});
		
		// Listeners
		audioProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				mHandler.removeCallbacks(mUpdateTimeTask);
				int totalDuration = GlobalVariable.mp.getDuration();
				int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
				
				// forward or backward to certain seconds
				GlobalVariable.mp.seekTo(currentPosition);
				
				// update timer progress again
				updateProgressBar();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				mHandler.removeCallbacks(mUpdateTimeTask);
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		}); // Important
		GlobalVariable.mp.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if(GlobalVariable.currentSongIndex < (GlobalVariable.audioList.size() - 1)){
					playSong(GlobalVariable.currentSongIndex + 1);
					GlobalVariable.currentSongIndex = GlobalVariable.currentSongIndex + 1;
					
				}else{
					// play first song
					playSong(0);
					GlobalVariable.currentSongIndex = 0;
				}
			}
		}); // Important
		
		// By default play first song
		playSong(0);
		tv_Content.setText(GlobalVariable.audioList.get(0).get(GetJSONData.TAG_CONTENTAUDIO_DETAIL));
		
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if(GlobalVariable.mp.isPlaying()){
					if(GlobalVariable.mp!=null){
						GlobalVariable.mp.pause();
						// Changing button image to play button
						//btnPlay.setImageResource(R.drawable.play_button);
						btnPlay.setBackgroundResource(android.R.drawable.ic_media_play);
					}
				}else{
					// Resume song
					if(GlobalVariable.mp!=null){
						GlobalVariable.mp.start();
						// Changing button image to pause button
						//btnPlay.setImageResource(R.drawable.next_button);
						btnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
					}
				}
				//Toast.makeText(activity.getApplicationContext(), "Đã thêm vào Danh sách yêu thích", Toast.LENGTH_SHORT).show();
			}
		});
		
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				if(GlobalVariable.currentSongIndex < (GlobalVariable.audioList.size() - 1)){
					playSong(GlobalVariable.currentSongIndex + 1);
					GlobalVariable.currentSongIndex = GlobalVariable.currentSongIndex + 1;
					tv_Content.setText(GlobalVariable.audioList.get(GlobalVariable.currentSongIndex).get(GetJSONData.TAG_CONTENTAUDIO_DETAIL));
				}else{
					// play first song
					playSong(0);
					GlobalVariable.currentSongIndex = 0;
					tv_Content.setText(GlobalVariable.audioList.get(0).get(GetJSONData.TAG_CONTENTAUDIO_DETAIL));
				}	
			}
		});
		
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(GlobalVariable.currentSongIndex > 0){
					playSong(GlobalVariable.currentSongIndex - 1);
					GlobalVariable.currentSongIndex = GlobalVariable.currentSongIndex - 1;
					tv_Content.setText(GlobalVariable.audioList.get(GlobalVariable.currentSongIndex).get(GetJSONData.TAG_CONTENTAUDIO_DETAIL));
				}else{
					// play last song
					playSong(GlobalVariable.audioList.size() - 1);
					GlobalVariable.currentSongIndex = GlobalVariable.audioList.size() - 1;
					tv_Content.setText(GlobalVariable.audioList.get(GlobalVariable.audioList.size() - 1).get(GetJSONData.TAG_CONTENTAUDIO_DETAIL));
				}
			}
		});
		
		btnPlayList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (playlist_display == false) {
					// Hiện playlist
					mViewSwitcher.setDisplayedChild(1);
					playlist_display = true;
				}
				else if (playlist_display == true) {
					// Hiện content
					mViewSwitcher.setDisplayedChild(0);
					playlist_display = false;
				}
			}
		});
		
		// Set timer stop audio and close app
		btnTimer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (timer_set == false) {
										
					MyTimePickerDialog mTimePicker = new MyTimePickerDialog(activity, new MyTimePickerDialog.OnTimeSetListener() {
						
						@Override
						public void onTimeSet(bhmedia.dungdq.kiemhiepaudio.model.TimePicker view, int selectedHour, int selectedMinute, int selectedSecond) {
							// TODO Auto-generated method stub
							hour = selectedHour;
							minute = selectedMinute;
							second = selectedSecond;
							
							total_time = (hour * 3600) + (minute * 60) + second;
							
							if (total_time != 0) {
								timer_set = true;
								btnTimer.setBackgroundResource(R.drawable.alarm_2);
								Toast.makeText(context, "Sẽ dừng đọc truyện trong " + String.valueOf(hour) + " giờ " + String.valueOf(minute) + " phút " + String.valueOf(second) + " giây.", Toast.LENGTH_SHORT).show();
							}
							
							setTimer = new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									GlobalVariable.mp.stop();
									
									total_time = 0;
									timer_set = false;
									btnTimer.setBackgroundResource(R.drawable.alarm_1);
									
									// Close app
									activity.finish();
									System.exit(0);
								}
							};
							
							mHandler_timer.postDelayed(setTimer, total_time * 1000);
							
							/*mHandler_timer.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									//Log.d("Tong thoi gian: ", String.valueOf(total_time));
									GlobalVariable.mp.stop();
								}
							}, total_time * 1000);*/
						}
					}, hour, minute, second, true);
					
					mTimePicker.show();
				}
				
				else {
					mHandler_timer.removeCallbacks(setTimer);
					timer_set = false;
					btnTimer.setBackgroundResource(R.drawable.alarm_1);
					Toast.makeText(context, "Đã hủy hẹn giờ", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		// Share Facebook
		btnShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				publishFeedDialog();
			}
		});
	}
	
	public static void  playSong(int songIndex){
		// Play song
		try {
        	GlobalVariable.mp.reset();
			//mp.setDataSource(songsList.get(songIndex).get("songPath"));
        	//mp.setDataSource("http://appdata.bhmedia.vn/audio/kiemhiep/kiephiepvietnam/namthiennhattuyetkiem/namthiennhattuyetkiem01.mp3");
        	GlobalVariable.mp.setDataSource(GlobalVariable.audioList.get(songIndex).get(GetJSONData.TAG_LINKAUDIO_DETAIL));
			GlobalVariable.mp.prepare();
			GlobalVariable.mp.start();
			// Displaying Song title
			//String songTitle = songsList.get(songIndex).get("songTitle");
        	//songTitleLabel.setText(songTitle);
			
        	// Changing Button Image to pause image
			//btnPlay.setImageResource(R.drawable.next_button);
			btnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
			
			// set Progress bar values
			audioProgressBar.setProgress(0);
			audioProgressBar.setMax(100);
			
			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Update timer on seekbar
	public static void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }
	
	private static Runnable mUpdateTimeTask = new Runnable() {
	   public void run() {
		   long totalDuration = GlobalVariable.mp.getDuration();
		   long currentDuration = GlobalVariable.mp.getCurrentPosition();
		  
		   // Displaying Total Duration time
		   audioTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
		   // Displaying time completed playing
		   audioCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
		   
		   // Updating progress bar
		   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
		   audioProgressBar.setProgress(progress);
		   
		   // Running this thread after 100 milliseconds
	       mHandler.postDelayed(this, 100);
	   }
	};
	
	/*private static Runnable setTimer = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d("Tong thoi gian: ", String.valueOf(total_time));
			GlobalVariable.mp.stop();
			mHandler.postDelayed(this, total_time * 1000);
		}
	};*/
	
	public class getPlaylist extends AsyncTask<Void, Integer, Void> {
		
		//Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
        	super.onPreExecute();
        }

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// Load playlist
			for (int i = 0; i < GlobalVariable.audioList.size(); i++) {
				String tentruyen_tap = "Chương " + (i+1);
				//Log.d("Tap truyen: ", tentruyen_tap);
				// creating new HashMap
				HashMap<String, String> audio = new HashMap<String, String>();
				
				audio.put(TENTRUYEN_TAP, tentruyen_tap);

				// adding HashList to ArrayList
				audioListData.add(audio);
			}
			return null;
		}
		
		//after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {
        	super.onPostExecute(result);
        	ListAdapter adapter = new SimpleAdapter(context, audioListData,
    				R.layout.playlist_item, new String[] { TENTRUYEN_TAP }, new int[] {R.id.tv_TapTruyenAudio });
    		
    		lv_playlist.setAdapter(adapter);
    		
    		lv_playlist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					playSong(position);
					GlobalVariable.currentSongIndex = position;
					tv_Content.setText(GlobalVariable.audioList.get(position).get(GetJSONData.TAG_CONTENTAUDIO_DETAIL));
				}
			});
        	//lv.setFastScrollEnabled(true);
        }
	}
	
	public class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
		
		public AudioFocusHelper(Context ctx) {
			context = ctx;
	        am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
	    }
		
		public boolean requestFocus() {
	        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
	            am.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
	            AudioManager.AUDIOFOCUS_GAIN);
	    }
		
		public boolean abandonFocus() {
	        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
	            am.abandonAudioFocus(this);
	    }

		@Override
		public void onAudioFocusChange(int focusChange) {
			// TODO Auto-generated method stub
			switch (focusChange) {
		        case AudioManager.AUDIOFOCUS_GAIN:
		            // resume playback
		            //if (GlobalVariable.mp == null) Log.d("Chua co gi", "chua co gi");
		            //else 
		            	if (!GlobalVariable.mp.isPlaying()) GlobalVariable.mp.start();
		            	Toast.makeText(context, "Da start", Toast.LENGTH_SHORT).show();
		            //GlobalVariable.mp.setVolume(1.0f, 1.0f);
		            break;
	
		        case AudioManager.AUDIOFOCUS_LOSS:
		            // Lost focus for an unbounded amount of time: stop playback and release media player
		            if (GlobalVariable.mp.isPlaying()) {
		            	if (GlobalVariable.IsPlaying == false) {
		            		GlobalVariable.mp.stop();
		            		Toast.makeText(context, "Da stop", Toast.LENGTH_SHORT).show();
		            	}
		            	else GlobalVariable.IsPlaying = false;
		            }
		            
		            //GlobalVariable.mp.release();
		            //GlobalVariable.mp = null;
		            break;
	
		        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
		            // Lost focus for a short time, but we have to stop
		            // playback. We don't release the media player because playback
		            // is likely to resume
		            if (GlobalVariable.mp.isPlaying()) GlobalVariable.mp.pause();
		            Toast.makeText(context, "Da pause", Toast.LENGTH_SHORT).show();
		            break;
	
		        /*case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
		            // Lost focus for a short time, but it's ok to keep playing
		            // at an attenuated level
		            if (GlobalVariable.mp.isPlaying()) GlobalVariable.mp.setVolume(0.1f, 0.1f);
		            break;*/
		    }
		}
	}
	
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", "Facebook SDK for Android");
	    params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	    params.putString("link", "https://developers.facebook.com/android");
	    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(context,
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {
				
				@Override
				public void onComplete(Bundle values, FacebookException error) {
					// TODO Auto-generated method stub
					if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(context,
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(context.getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(context.getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(context.getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
				}
	        })
	        .build();
	    feedDialog.show();
	}
}
