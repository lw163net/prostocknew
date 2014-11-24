package com.coolcom.prostocknew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

public class FragmentStockindex extends Fragment {

	GetStock thread=null;
	String Stockindexid="s_sh000001,s_sz399001";
	ArrayList<stockindexdata> stocklist=null;
	ListView stocklistview=null;
	private int getattr=1;
	String url="http://hq.sinajs.cn/list=";
	EditText etstock=null;
	String[] Allindexlist=null;
	String tempa[]=null;
	private ListView lv=null;
	//ProgressBar bar1=null;
	public static FragmentStockindex newInstance() {
		FragmentStockindex f = new FragmentStockindex();
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getattr=1;
		
	}
	@Override public void onStart() {
		super.onStart();
		SharedPreferences sp=getActivity().getSharedPreferences("prostockold",Context.MODE_PRIVATE);
		String Stockid=sp.getString("strindex", "");
		Stockindexid=Stockid;
		//Stockid=Stockindexid;
		if(!Stockid.equals("")){
			Responseindex(url+Stockindexid,getattr);
		}
		else
		{
			new AlertDialog.Builder(getActivity()).setTitle("没有自选指数,是否添加?")
			.setPositiveButton("是的", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Addindex();
				}
			}).setNegativeButton("不要", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).show();
			
			
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view=null;
		view= inflater.inflate(R.layout.stockindexmain,null);
		return view;
	}
	private void Addindex() {
		// TODO Auto-generated method stub
		try{
				Allindexlist=this.getResources().getStringArray(R.array.stringstockindex);
				int indexlength=Allindexlist.length;
				tempa=new String[indexlength];
				String tempb[]=new String[indexlength];
				boolean tempd[]=new boolean[indexlength];
				for(int i=0;i<Allindexlist.length;i++){
					String tempstr=Allindexlist[i];
					String[] tempc=tempstr.split(",");
					tempa[i]=tempc[0];
					tempb[i]=tempc[1];	
					if(Stockindexid.indexOf(tempc[0])==-1)
						tempd[i]=false;
					else
						tempd[i]=true;
				}
				
				AlertDialog ad=new AlertDialog.Builder(getActivity()).setTitle("选择指数")
								.setMultiChoiceItems(tempb, tempd,
								new DialogInterface.OnMultiChoiceClickListener() {
									public void onClick(DialogInterface dialog, int which, boolean isChecked) {
										
									}
								}).setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface arg0, int arg1) {
												// TODO Auto-generated method stub
											
													Stockindexid="";
													for(int j=0;j<tempa.length;j++){
														if(lv.getCheckedItemPositions().get(j)){
															Stockindexid=Stockindexid+tempa[j]+",";
														}
													}
													if(!Stockindexid.equals(""))
														Stockindexid=Stockindexid.substring(0, Stockindexid.length()-1);
													SharedPreferences sp=getActivity().getSharedPreferences("prostockold",Context.MODE_PRIVATE);
									    	    	Editor editor=sp.edit();
									    	    	editor.putString("strindex",Stockindexid);
									    	    	editor.commit();
									    	    	if(!Stockindexid.equals(""))
									    	    		Responseindex(url+Stockindexid,getattr);
				
											}
										}).setNegativeButton("取消", null).create();
				lv=ad.getListView();
		
				ad.show();
		}
		catch(Exception ex){
			new AlertDialog.Builder(getActivity()).setMessage(ex.getMessage()).show();
		}
	}

	public List<Map<String, Object>> fillmap(){
		List<Map<String, Object>> items=new ArrayList<Map<String,Object>>();
		for(int i=0;i<stocklist.size();i++){
			Map<String, Object> tempmap=new HashMap<String, Object>();
			tempmap.put("zsmc", stocklist.get(i).getZsmc());
			tempmap.put("zsds", stocklist.get(i).getZsds());
			tempmap.put("zszdjs",stocklist.get(i).getZsjg());
			tempmap.put("zszdf", stocklist.get(i).getZszdl()+"%");
			tempmap.put("zssj", stocklist.get(i).getZssj());
			items.add(tempmap);
		}
		return items;
	}
	public  void  Responseindex(String tempurl,int attr){
		
        thread=new GetStock(handlerindex);
       
        thread.doStart(tempurl,attr,getActivity());   
	}
	Handler handlerindex=new Handler(){
		public void handleMessage(Message m){
			
			switch(m.what){
			case 1:
				try{
					String mylist[]=(String[])m.getData().getStringArray("stock");
					stocklist=new ArrayList<stockindexdata>();
					for(int i=0;i<mylist.length;i++){
						String tempstr=mylist[i];
						//tempstr=tempstr.substring(7);
						String a[]=tempstr.split(",");
						stockindexdata tempstock=new stockindexdata();									
						if(a.length<7){		
							tempstock.setZsmc(a[1]);
							tempstock.setZsds(a[2]);
							tempstock.setZsjg(a[3]);
							tempstock.setZszdl(a[4]);
							tempstock.setZssj(a[5]);
						}else{	
							if(a[0].equals("hkHSI")){
								tempstock.setZsmc(a[2]);
								tempstock.setZsds(a[7]);
								tempstock.setZsjg(a[8]);
								tempstock.setZszdl(a[9]);
								int templength=a.length-1;
								tempstock.setZssj(a[templength-1]+" "+a[templength]);
							}
							if(a[1].equals("道琼斯")||a[1].equals("纳斯达克")||a[1].equals("标普指数")){
								tempstock.setZsmc(a[1]);
								tempstock.setZsds(a[2]);
								tempstock.setZsjg(a[5]);
								tempstock.setZszdl(a[3]);
								int templength=a.length-1;
								tempstock.setZssj(a[templength-1]);
							}
							if(a[1].equals("上证指数")||a[1].equals("深证成指")||a[1].equals("沪深300")){
								tempstock.setZsmc(a[1]);
								tempstock.setZsds(a[2]);
								tempstock.setZsjg(a[3]);
								tempstock.setZszdl(a[4]);
								int tempcje=Integer.parseInt(a[6])/10000;
								tempstock.setZssj(String.valueOf(tempcje)+"亿");
							}
						}
						stocklist.add(tempstock);
					}
					List<Map<String, Object>> items=fillmap();
					IndexSimpleAdapter myAdapter= new IndexSimpleAdapter(getActivity(),items,R.layout.stockindex
												 ,new String[]{"zsmc","zsds","zszdjs","zszdf","zssj"}
												 ,new int[]{R.id.txtzsmc,R.id.txtzsds,R.id.txtzszdjs,R.id.txtzszdf,R.id.txtzssj});
					stocklistview=(ListView)getActivity().findViewById(R.id.ListView01);
					stocklistview.setAdapter(myAdapter);
					break;
				}
				catch(Exception e){
					new AlertDialog.Builder(getActivity())
	                .setTitle("读取数据出错,错误如下:") 
	                .setMessage(m.getData().getString("error")) 
	                .setNeutralButton("Close", new DialogInterface.OnClickListener() { 
	                  public void onClick(DialogInterface dlg, int sumthin) { 
	                  } 
	                }) 
	                .show();
					break;
				}
				
				
			case 2:
			
				break;
			
			}
		}
	};//end handler
}
