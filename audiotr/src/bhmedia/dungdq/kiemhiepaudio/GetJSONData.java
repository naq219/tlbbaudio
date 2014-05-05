package bhmedia.dungdq.kiemhiepaudio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bhmedia.dungdq.kiemhiepaudio.customadapter.ListTruyenAudioAdapter;
import bhmedia.dungdq.kiemhiepaudio.database.FavouriteDB;
import bhmedia.dungdq.kiemhiepaudio.model.FavouriteList;
import bhmedia.dungdq.kiemhiepaudio.model.GlobalVariable;
import bhmedia.dungdq.kiemhiepaudio.process.VietComparator;
import bhmedia.dungdq.kiemhiepaudio.services.ServiceHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class GetJSONData {
	
	// Key lấy danh sách audio theo category
    public static final String TAG_RESULT = "result";
    public static final String TAG_TRUYENID = "TruyenID";
    public static final String TAG_TENTRUYEN = "TruyenName";
    public static final String TAG_TACGIA = "Tacgia";
    public static final String TAG_SOTAP = "SoPhan";
    public static final String TAG_ANHDAIDIEN = "Truyen_CoverImage";
    
    // Key lấy chi tiết 1 audio
    public static final String TAG_RESULT_DETAIL = "result";
    public static final String TAG_TRUYENID_DETAIL = "TruyenID";
    public static final String TAG_TENTRUYEN_DETAIL = "TruyenName";
    public static final String TAG_TACGIA_DETAIL = "Tacgia";
    public static final String TAG_SOTAP_DETAIL = "SoPhan";
    public static final String TAG_ANHDAIDIEN_DETAIL = "Truyen_CoverImage";
    public static final String TAG_LINKAUDIO_DETAIL = "LinkAudio";
    public static final String TAG_CONTENTAUDIO_DETAIL = "Content";
    
    static ArrayList<HashMap<String, String>> truyenaudio_List = new ArrayList<HashMap<String, String>>();
    
    public static ArrayList<HashMap<String, String>> truyenaudiodetail_List = new ArrayList<HashMap<String, String>>();
	
	Context context;
	
	//View rootView ;
	
	static ListTruyenAudioAdapter adapter;
	
	public static ArrayList<HashMap<String, String>> mDatabaseOfNames;
	
	public static FavouriteDB favouritedb;
	
	public static String truyenID_chitiet, tentruyen_chitiet, tacgia_chitiet, sotap_chitiet, anhdaidien_chitiet; 
	
	public GetJSONData(Activity context) {
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		//rootView = inflater.inflate(R.layout.fragment_player, null);
		
		favouritedb = new FavouriteDB(context);
	}
	
	public class getListTruyenAudio extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
        	GlobalVariable.progressDialog = ProgressDialog.show(context,"",  "Đang tải dữ liệu ...", false, false);
        	truyenaudio_List.clear();
        	super.onPreExecute();
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {
        	// getting JSON string
	    	ServiceHandler sh = new ServiceHandler();
	    	
	    	String jsonStr = sh.makeServiceCall(GlobalVariable.url, ServiceHandler.GET);
	    	
	    	Log.d("Response: ", "> " + jsonStr);
	    	
	    	if (jsonStr != null) {
	    		try {
	    			JSONObject jsonObj = new JSONObject(jsonStr);
	    			
	    			JSONArray result = jsonObj.getJSONArray(TAG_RESULT);
	    			
	    			// vòng lặp cho tất cả truyện audio
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject c = result.getJSONObject(i);
                         
                        String truyenid = c.getString(TAG_TRUYENID);
                        String tentruyen = c.getString(TAG_TENTRUYEN);
                        String tacgia = c.getString(TAG_TACGIA);
                        String sotap = c.getString(TAG_SOTAP);
                        String anhdaidien = c.getString(TAG_ANHDAIDIEN);
 
                        // Phone node is JSON Object
                        /*JSONObject phone = c.getJSONObject(TAG_PHONE);
                        String mobile = phone.getString(TAG_PHONE_MOBILE);
                        String home = phone.getString(TAG_PHONE_HOME);
                        String office = phone.getString(TAG_PHONE_OFFICE);*/
 
                        // tmp hashmap for single contact
                        HashMap<String, String> results = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        results.put(TAG_TRUYENID, truyenid);
                        results.put(TAG_TENTRUYEN, tentruyen);
                        results.put(TAG_TACGIA, tacgia);
                        results.put(TAG_SOTAP, sotap);
                        results.put(TAG_ANHDAIDIEN, anhdaidien);
 
						// add result to truyenaudio_List
                        truyenaudio_List.add(results);
                        
                        // Sắp xếp list theo alphabet
                        Collections.sort(truyenaudio_List, new Comparator<HashMap< String,String >>() {

                            @Override
                            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                                // Do your comparison logic here and return accordingly.
                            	VietComparator vcp = new VietComparator();
                            	return vcp.compare(lhs.get(TAG_TENTRUYEN), rhs.get(TAG_TENTRUYEN));
                                //return lhs.get(TAG_TENTRUYEN).compareTo(rhs.get(TAG_TENTRUYEN));
                            }
                        });
                        
                        mDatabaseOfNames = truyenaudio_List;
                    }
	    		}
	    		catch (JSONException e) {
	    			
	    		}
	    	} else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
	        
	        return null;
        }  
  
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {
        	super.onPostExecute(result);
            // Dismiss the progress dialog
            if (GlobalVariable.progressDialog.isShowing()) GlobalVariable.progressDialog.dismiss();
        	adapter = new ListTruyenAudioAdapter(context, truyenaudio_List);
        	MainActivity.lv.setAdapter(adapter);
        	//lv.setFastScrollEnabled(true);
        	
        	// Reading all contacts
            /*Log.d("Reading: ", "Reading all contacts.."); 
            List<FavouriteList> favourites = MainActivity.favouritedb.getAllFavourites();       
             
            for (FavouriteList fvr : favourites) {
                String log = "Truyen ID: "+fvr.getTruyenID()+" ,Ten truyen: " + fvr.getTenTruyen() + " ,Tac gia: " + fvr.getTacGia() + " ,So tap: " + fvr.getSoTap() + " ,Anh dai dien: " + fvr.getAnhDaiDien();
                // Writing Contacts to log
                Log.d("Item: ", log);
            }*/
        }  
    }
	
	public class getListTruyenAudioFavourite extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
        	//progressDialog = ProgressDialog.show(MainActivity.this,"",  "Đang tải dữ liệu ...", false, false);
        	truyenaudio_List.clear();
        	super.onPreExecute();
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {
        	List<FavouriteList> favourites = favouritedb.getAllFavourites();
        	
        	for (FavouriteList fvr : favourites) {
        		String truyenid = fvr.getTruyenID();
        		String tentruyen = fvr.getTenTruyen();
        		String tacgia = fvr.getTacGia();
        		String sotap = fvr.getSoTap();
        		String anhdaidien = fvr.getAnhDaiDien();
        		
        		// tmp hashmap for single contact
                HashMap<String, String> results = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                results.put(TAG_TRUYENID, truyenid);
                results.put(TAG_TENTRUYEN, tentruyen);
                results.put(TAG_TACGIA, tacgia);
                results.put(TAG_SOTAP, sotap);
                results.put(TAG_ANHDAIDIEN, anhdaidien);

                // add result to truyenaudio_List
                truyenaudio_List.add(results);
                
                // Sắp xếp list theo alphabet
                Collections.sort(truyenaudio_List, new Comparator<HashMap< String,String >>() {

                    @Override
                    public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                        // Do your comparison logic here and return accordingly.
                    	VietComparator vcp = new VietComparator();
                    	return vcp.compare(lhs.get(TAG_TENTRUYEN), rhs.get(TAG_TENTRUYEN));
                        //return lhs.get(TAG_TENTRUYEN).compareTo(rhs.get(TAG_TENTRUYEN));
                    }
                });
                
                mDatabaseOfNames = truyenaudio_List;
        	}
	        
	        return null;
        }  
  
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {
        	super.onPostExecute(result);
            // Dismiss the progress dialog
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
        	adapter = new ListTruyenAudioAdapter(context, truyenaudio_List);
        	MainActivity.lv.setAdapter(adapter);
        	//lv.setFastScrollEnabled(true);
        }  
    }
	
	public class getDetailAudio extends AsyncTask<Void, Integer, Void>  
    {
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {
        	truyenaudiodetail_List.clear();
        	GlobalVariable.progressDialog = ProgressDialog.show(context,"",  "Đang tải dữ liệu ...", false, false);
        	super.onPreExecute();
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {
        	// getting JSON string
	    	ServiceHandler sh = new ServiceHandler();
	    	
	    	String jsonStr = sh.makeServiceCall(GlobalVariable.url, ServiceHandler.GET);
	    	
	    	Log.d("Response: ", "> " + jsonStr);
	    	
	    	if (jsonStr != null) {
	    		try {
	    			JSONObject jsonObj = new JSONObject(jsonStr);
	    			
	    			//result = jsonObj.getJSONArray(TAG_RESULT);
	    			
	    			// vòng lặp cho tất cả truyện audio
                    /*for (int i = 0; i < result.length(); i++) {
                        JSONObject c = result.getJSONObject(i);
                         
                        String truyenid = c.getString(TAG_TRUYENID);
                        String tentruyen = c.getString(TAG_TENTRUYEN);
                        String tacgia = c.getString(TAG_TACGIA);
                        String sotap = c.getString(TAG_SOTAP);
                        String anhdaidien = c.getString(TAG_ANHDAIDIEN);*/
 
                        // Phone node is JSON Object
                        JSONObject result_detail = jsonObj.getJSONObject(TAG_RESULT_DETAIL);
                        truyenID_chitiet = result_detail.getString(TAG_TRUYENID_DETAIL); 
                        tentruyen_chitiet = result_detail.getString(TAG_TENTRUYEN_DETAIL);
                        tacgia_chitiet = result_detail.getString(TAG_TACGIA_DETAIL);
                        sotap_chitiet = result_detail.getString(TAG_SOTAP_DETAIL);
                        anhdaidien_chitiet = result_detail.getString(TAG_ANHDAIDIEN_DETAIL);
                        
                        JSONArray linkaudioarray = result_detail.getJSONArray(TAG_LINKAUDIO_DETAIL);
                        JSONArray contentaudioarray = result_detail.getJSONArray(TAG_CONTENTAUDIO_DETAIL);
                        for (int i = 0; i < linkaudioarray.length(); i++) {
                        	String linkaudio = linkaudioarray.getString(i);
                        	
                        	if (!linkaudio.startsWith("http://")) linkaudio = "http://" + linkaudio;
                        	
                        	String contentaudio = Html.fromHtml(contentaudioarray.getString(i)).toString();
                        	
                        	HashMap<String, String> link_content = new HashMap<String, String>();
                        	
                        	link_content.put(TAG_LINKAUDIO_DETAIL, linkaudio);
                        	link_content.put(TAG_CONTENTAUDIO_DETAIL, contentaudio);
                        	
                        	truyenaudiodetail_List.add(link_content);
                        }
 
                        // tmp hashmap for single contact
                        /*HashMap<String, String> results = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        results.put(TAG_TRUYENID, truyenid);
                        results.put(TAG_TENTRUYEN, tentruyen);
                        results.put(TAG_TACGIA, tacgia);
                        results.put(TAG_SOTAP, sotap);
                        results.put(TAG_ANHDAIDIEN, anhdaidien);
 
                        // add result to truyenaudio_List
                        truyenaudio_List.add(results);*/
                        
                        // Sắp xếp list theo alphabet
                        /*Collections.sort(truyenaudio_List, new Comparator<HashMap< String,String >>() {

                            @Override
                            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                                // Do your comparison logic here and return accordingly.
                            	VietComparator vcp = new VietComparator();
                            	return vcp.compare(lhs.get(TAG_TENTRUYEN), rhs.get(TAG_TENTRUYEN));
                                //return lhs.get(TAG_TENTRUYEN).compareTo(rhs.get(TAG_TENTRUYEN));
                            }
                        });
                        
                        mDatabaseOfNames = truyenaudio_List;*/
                    //}
	    		}
	    		catch (JSONException e) {
	    			
	    		}
	    	} else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
	        
	        return null;
        }  
  
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {
        	super.onPostExecute(result);
            // Dismiss the progress dialog
            if (GlobalVariable.progressDialog.isShowing()) GlobalVariable.progressDialog.dismiss();
        	/*adapter = new ListTruyenAudioAdapter(getApplicationContext(), truyenaudio_List);
        	lv.setAdapter(adapter);*/
            MainActivity.tvTenTruyenChiTiet.setText(tentruyen_chitiet);
            
            new PlayListAudio((Activity)context);
            GlobalVariable.IsPlaying = true;
        }  
    }
}
