package bhmedia.dungdq.kiemhiepaudio.model;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentManager;

public class GlobalVariable {
	public static FragmentManager fmgr;
	
	public static Boolean player = false;
	public static Boolean landau = false;
	
	// Lưu vị trí fragment trước đó
	public static int last_position;
	
	public static ProgressDialog progressDialog;
	
	// Link get JSON Data
	public static String url = "";
	
	// Player variable
	public static int currentSongIndex = 0;
	public static ArrayList<HashMap<String, String>> audioList = new ArrayList<HashMap<String, String>>();
	public static MediaPlayer mp;
	
	// Check player status to audio focus
	public static Boolean IsPlaying = false;
}
