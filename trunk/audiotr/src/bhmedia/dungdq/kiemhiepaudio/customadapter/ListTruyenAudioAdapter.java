package bhmedia.dungdq.kiemhiepaudio.customadapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bhmedia.dungdq.kiemhiepaudio.GetJSONData;
import bhmedia.dungdq.kiemhiepaudio.MainActivity.PlaceholderFragment;
import bhmedia.dungdq.kiemhiepaudio.MainActivity.PlaceholderFragmentPlayer;
import bhmedia.dungdq.kiemhiepaudio.model.FavouriteList;
import bhmedia.dungdq.kiemhiepaudio.model.GlobalVariable;
import bhmedia.dungdq.kiemhiepaudio.process.ImageLoader_original;
import bhmedia.dungdq.kiemhiepaudio.process.StringMatcher;
import bhmedia.dungdq.kiemhiepaudio.process.StringUtils;

import bhmedia.dungdq.kiemhiepaudio.MainActivity;
import bhmedia.dungdq.kiemhiepaudio.R;
import bhmedia.dungdq.kiemhiepaudio.process.ImageLoader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class ListTruyenAudioAdapter extends BaseAdapter implements Filterable {
	
	// Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data, datafilter;
    ImageLoader imageLoader;
    
    public ListTruyenAudioAdapter(Context context, ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
        imageLoader = new ImageLoader(context);
    }
	
    @Override
    public int getCount() {
        return data.size();
    }
 
    @Override
    public Object getItem(int position) {
        return null;
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView tvTenTruyen;
        TextView tvTacGiaSoTap;
        //TextView tvTongSoTap;
        ImageView anhdaidien;
        final ImageButton btn_bookmark;
 
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        convertView = inflater.inflate(R.layout.truyenaudio_list_item, parent, false);
        RelativeLayout rlt_truyenaudio_label = (RelativeLayout)convertView.findViewById(R.id.rlt_truyenaudio_label);
        TextView tv_label_truyenaudio = (TextView)convertView.findViewById(R.id.tv_label_truyenaudio);
        
        // Get the position from the results
        HashMap<String, String> resultp = new HashMap<String, String>();
		resultp = data.get(position);
 
        // Locate the TextViews in listview_item.xml
        tvTenTruyen = (TextView) convertView.findViewById(R.id.tvTenTruyen); 
        tvTacGiaSoTap = (TextView) convertView.findViewById(R.id.tvTacGiaSoTap);
        //tvTongSoTap = (TextView) convertView.findViewById(R.id.tvTongSoTap);
        // Locate the ImageView in listview_item.xml
        anhdaidien = (ImageView) convertView.findViewById(R.id.list_bg_truyenaudio); 
        btn_bookmark = (ImageButton) convertView.findViewById(R.id.btn_bookmark);
        
        // Loại bỏ dấu tiếng Việt ở label
        StringUtils bodau = new StringUtils();
 
        // Xử lý hiển thị label session
        if (position == 0) {
        	tv_label_truyenaudio.setText(bodau.unAccent(resultp.get(GetJSONData.TAG_TENTRUYEN).substring(0, 1)));
        	rlt_truyenaudio_label.setVisibility(View.VISIBLE);
        }
        else {
        	if (!resultp.get(GetJSONData.TAG_TENTRUYEN).substring(0, 1).equalsIgnoreCase(data.get(position - 1).get(GetJSONData.TAG_TENTRUYEN).substring(0, 1))) {
            	tv_label_truyenaudio.setText(bodau.unAccent(resultp.get(GetJSONData.TAG_TENTRUYEN).substring(0, 1)));
            	rlt_truyenaudio_label.setVisibility(View.VISIBLE);
            }
            else {
            	rlt_truyenaudio_label.setVisibility(View.GONE);
            }
        }
        
        // Capture position and set results to the TextViews
        tvTenTruyen.setText(resultp.get(GetJSONData.TAG_TENTRUYEN));
        tvTacGiaSoTap.setText(resultp.get(GetJSONData.TAG_TACGIA) + " / " + resultp.get(GetJSONData.TAG_SOTAP) + " tập");
        //tvTongSoTap.setText(resultp.get(MainActivity.TAG_SOTAP) + " tập");
        
        imageLoader.DisplayImage(resultp.get(GetJSONData.TAG_ANHDAIDIEN), anhdaidien);
        
        //Log.d("Mang favourite: ", MainActivity.favouritedb.getFavouriteItem(resultp.get(MainActivity.TAG_TRUYENID)).getTruyenID());
        
        if (GetJSONData.favouritedb.getContactsCount() != 0) {
        	if (GetJSONData.favouritedb.getFavouriteItem(resultp.get(GetJSONData.TAG_TRUYENID)) != null) {
            	btn_bookmark.setBackgroundResource(R.drawable.star_2);
            }
            else btn_bookmark.setBackgroundResource(R.drawable.star_1);
        }
        else btn_bookmark.setBackgroundResource(R.drawable.star_1);
        
        btn_bookmark.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//if (MainActivity.favouritedb.getContactsCount() != 0) {
		        	if (GetJSONData.favouritedb.getFavouriteItem(data.get(position).get(GetJSONData.TAG_TRUYENID)) == null) {
		            	btn_bookmark.setBackgroundResource(R.drawable.star_2);
		            	
		            	Log.d("Insert: ", "Inserting .."); 
				        GetJSONData.favouritedb.addFavourite(new FavouriteList(data.get(position).get(GetJSONData.TAG_TRUYENID), data.get(position).get(GetJSONData.TAG_TENTRUYEN), data.get(position).get(GetJSONData.TAG_TACGIA), data.get(position).get(GetJSONData.TAG_SOTAP), data.get(position).get(GetJSONData.TAG_ANHDAIDIEN)));
				        
				        Toast.makeText(context, "Đã thêm vào Danh sách yêu thích", Toast.LENGTH_SHORT).show();
		            }
		            else {
		            	btn_bookmark.setBackgroundResource(R.drawable.star_1);
		            	
		            	Log.d("Delete: ", "Deleting ..");
		            	GetJSONData.favouritedb.deleteFavourite(data.get(position).get(GetJSONData.TAG_TRUYENID));
		            	
		            	Toast.makeText(context, "Đã xóa khỏi Danh sách yêu thích", Toast.LENGTH_SHORT).show();
		            }
		        //}
		        //else btn_bookmark.setBackgroundResource(R.drawable.star_1);
				//btn_bookmark.setBackgroundResource(R.drawable.star_2);
			}
		});
        
        //MainActivity.last_item = resultp.get(MainActivity.TAG_TENTRUYEN).substring(0, 1);
        
        /*if (resultp.get(TabHanoi_KHOfflineActivity.TAG_TACGIA) != "") {*/
        	// Capture button clicks on ListView items
            convertView.setOnClickListener(new OnClickListener() {
     
                @Override
                public void onClick(View arg0) {
                    // Get the position from the results
                    /*HashMap<String, String> resultp = new HashMap<String, String>();
                    resultp = data.get(position);
                    
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent.putExtra("PhanLoaiID", resultp.get(TabHanoiActivity.TAG_PHANLOAIID));
                    //intent.putExtra("KhoaHocOfflineID", resultp.get(TabHanoi_KHOfflineActivity.TAG_KHOAHOCOFFLINEID));
                    
                    context.startActivity(intent);*/
                	GlobalVariable.player = true;
                	GlobalVariable.fmgr.beginTransaction().replace(R.id.container,
    						PlaceholderFragmentPlayer.newFragment()).commit();
                	GlobalVariable.url = "http://clip.bhmedia.vn/api/audio_detail?id=" + data.get(position).get(GetJSONData.TAG_TRUYENID);
                	new GetJSONData((Activity)context).new getDetailAudio().execute();
                }
            });
        /*}
        else {
        	tvTenTacGia.setText(resultp.get(TabHanoi_KHOfflineActivity.TAG_TACGIA));
        	tvSoNguoiDangKy.setText(resultp.get(TabHanoi_KHOfflineActivity.TAG_SOLUONGDADANGKY));
        	tvGiaban.setText(resultp.get(TabHanoi_KHOfflineActivity.TAG_GIABAN));
        	tvGiadaily.setText(resultp.get(TabHanoi_KHOfflineActivity.TAG_GIADAILY));
        }*/
 
        return convertView;
    }
    
	@Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
            	
            	data = (ArrayList<HashMap<String, String>>) results.values;
            	//MainActivity.filtercheck = true;
                notifyDataSetChanged();
            }

			@Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<HashMap<String, String>> FilteredArrayNames = new ArrayList<HashMap<String, String>>();
                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < GetJSONData.mDatabaseOfNames.size(); i++) {
                	String truyenid = GetJSONData.mDatabaseOfNames.get(i).get(GetJSONData.TAG_TRUYENID);
                	String tentruyen = GetJSONData.mDatabaseOfNames.get(i).get(GetJSONData.TAG_TENTRUYEN);
                	String tacgia = GetJSONData.mDatabaseOfNames.get(i).get(GetJSONData.TAG_TACGIA);
                	String sotap = GetJSONData.mDatabaseOfNames.get(i).get(GetJSONData.TAG_SOTAP);
                    String anhdaidien = GetJSONData.mDatabaseOfNames.get(i).get(GetJSONData.TAG_ANHDAIDIEN);
                    if (tentruyen.toLowerCase().contains(constraint.toString()))  {
                        //FilteredArrayNames.add(dataNames);
                    	Log.d("Xâu: ", tentruyen);
                    	HashMap<String, String> rsfilter = new HashMap<String, String>();
                    	rsfilter.put(GetJSONData.TAG_TRUYENID, truyenid);
                    	rsfilter.put(GetJSONData.TAG_TENTRUYEN, tentruyen);
                    	rsfilter.put(GetJSONData.TAG_TACGIA, tacgia);
                    	rsfilter.put(GetJSONData.TAG_SOTAP, sotap);
                    	rsfilter.put(GetJSONData.TAG_ANHDAIDIEN, anhdaidien);
                    	FilteredArrayNames.add(rsfilter);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
    }
}
