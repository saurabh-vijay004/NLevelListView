package com.example.user.nlevellistview;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

	List<NLevelItem> list;
	ListView listView;
	JSONArray json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView1);
		list = new ArrayList<NLevelItem>();
		try {
			prepareListData();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//here we create 5 grandparent (top level) NLevelItems
		//then foreach grandparent create a random number of parent (second level) NLevelItems
		//then foreach parent create a random number of children (third level) NLevelItems
		
		//we pass in an anonymous instance of NLevelView to the NLevelItem, this NLevelView is
		//what supplies the NLevelAdapter with a View for this NLevelItem
		Random rng = new Random();
	/*	final LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < 5; i++) {
			
			final NLevelItem grandParent = new NLevelItem(new SomeObject("GrandParent "+i),null, new NLevelView() {
				
				@Override
				public View getView(NLevelItem item) {
					View view = inflater.inflate(R.layout.list_item, null);
					TextView tv = (TextView) view.findViewById(R.id.textView);
					tv.setBackgroundColor(Color.GREEN);
					String name = (String) ((SomeObject) item.getWrappedObject()).getName();
					tv.setText(name);
					return view;
				}
			});
			list.add(grandParent);
			int numChildren = rng.nextInt(4) + 1;
			for (int j = 0; j < numChildren; j++) {
				NLevelItem parent = new NLevelItem(new SomeObject("Parent "+j),grandParent, new NLevelView() {
					
					@Override
					public View getView(NLevelItem item) {
						View view = inflater.inflate(R.layout.list_item, null);
						TextView tv = (TextView) view.findViewById(R.id.textView);
						tv.setBackgroundColor(Color.YELLOW);
						String name = (String) ((SomeObject) item.getWrappedObject()).getName();
						tv.setText(name);
						return view;
					}
				});
		
				list.add(parent);
				int grandChildren = rng.nextInt(5)+1;
				for( int k = 0; k < grandChildren; k++) {
					NLevelItem child = new NLevelItem(new SomeObject("child "+k),parent, new NLevelView() {
						
						@Override
						public View getView(NLevelItem item) {
							View view = inflater.inflate(R.layout.list_item, null);
							TextView tv = (TextView) view.findViewById(R.id.textView);
							tv.setBackgroundColor(Color.GRAY);
							String name = (String) ((SomeObject) item.getWrappedObject()).getName();
							tv.setText(name);
							return view;
						}
					});
				
					list.add(child);
				}
			}
		} */
		
		NLevelAdapter adapter = new NLevelAdapter(list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				((NLevelAdapter)listView.getAdapter()).toggle(arg2);
				((NLevelAdapter)listView.getAdapter()).getFilter().filter();
				
			}
		});
	}
	
	class SomeObject {
		public String name;

		public SomeObject(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}


	private String getJSONString(Context context)
	{
		String str = "";
		try
		{
			AssetManager assetManager = context.getAssets();
			InputStream in = assetManager.open("sample_json");
			InputStreamReader isr = new InputStreamReader(in);
			char [] inputBuffer = new char[100];

			int charRead;
			while((charRead = isr.read(inputBuffer))>0)
			{
				String readString = String.copyValueOf(inputBuffer,0,charRead);
				str += readString;
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}

		return str;
	}
	/*
     * Preparing the list data
     */
	private void prepareListData() throws JSONException {
		try {
	//		listDataHeader = new ArrayList<String>();
	//		listDataChild = new HashMap<String, List<String>>();
			json = new JSONArray(getJSONString(getApplicationContext()));
			//  System.out.println("json:::" + json.length());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (json.length() > 0) {
			final LayoutInflater inflater = LayoutInflater.from(this);
			for (int i = 0; i < json.length(); i++)
				try {
					JSONObject jo = json.getJSONObject(i);

					System.out.println(jo);
					if (jo.has("Name") && jo.get("Name") != null) {
						String strHeader = (String) jo.get("Name");
						// Adding child data
						final NLevelItem grandParent = new NLevelItem(new SomeObject(strHeader),null, new NLevelView() {

							@Override
							public View getView(NLevelItem item) {
								View view = inflater.inflate(R.layout.list_item, null);
								TextView tv = (TextView) view.findViewById(R.id.textView);
								//tv.setBackgroundColor(Color.GREEN);
								String name = (String) ((SomeObject) item.getWrappedObject()).getName();
								tv.setText(name);
								return view;
							}
						});
						list.add(grandParent);
					//	listDataHeader.add(strHeader);
						//System.out.println(jo.get("Childrens"));
						if (jo.has("Childrens") && jo.get("Childrens") != null){
							JSONArray childrenArray = jo.getJSONArray("Childrens");
							if (childrenArray.length() > 0) {
								List<String> innerChild = new ArrayList<String>();
								for (int j=0; j< childrenArray.length(); j++) {
									JSONObject childObject = childrenArray.getJSONObject(j);
									if (childObject.has("Name") && childObject.get("Name") != null) {
										String childName = (String) childObject.get("Name");
										//innerChild.add(childName);
										NLevelItem parent = new NLevelItem(new SomeObject(childName), grandParent, new NLevelView() {

											@Override
											public View getView(NLevelItem item) {
												View view = inflater.inflate(R.layout.second_level_item, null);
												TextView tv = (TextView) view.findViewById(R.id.textView);
												//tv.setBackgroundColor(Color.YELLOW);
												String name = (String) ((SomeObject) item.getWrappedObject()).getName();
												tv.setText(name);
												return view;
											}
										});
										list.add(parent);
										if (childObject.has("Childrens") && childObject.get("Childrens") != null) {
											JSONArray innerChildrenArray = childObject.getJSONArray("Childrens");
											System.out.println("innerChildrenArray Length:: "+ innerChildrenArray.length() + "innerChildrenArray::" + innerChildrenArray);

											if (innerChildrenArray.length() > 0) {
												for (int k = 0; k < innerChildrenArray.length(); k++) {
													JSONObject innerChildObject = innerChildrenArray.getJSONObject(k);
													System.out.println("innerChildObject::  " + innerChildObject);


													if (innerChildObject.has("Name") && innerChildObject.get("Name") != null) {

														String innerChildName = (String) innerChildObject.get("Name");
														System.out.println("innerChildName::" + innerChildName);
														NLevelItem child = new NLevelItem(new SomeObject(innerChildName), parent, new NLevelView() {

															@Override
															public View getView(NLevelItem item) {
																View view = inflater.inflate(R.layout.third_level_item, null);
																TextView tv = (TextView) view.findViewById(R.id.textView);
																//	tv.setBackgroundColor(Color.GRAY);
																String name = (String) ((SomeObject) item.getWrappedObject()).getName();
																tv.setText(name);
																return view;
															}
														});

														list.add(child);
													}
												}
											}
										}
									}
								}
							//	listDataChild.put(listDataHeader.get(i),innerChild);
								System.out.println("json:::" + innerChild);
							}
						}
						else {
							continue;
						}
					}
//                    listDataHeader.add("Now Showing");
//                    listDataHeader.add("Coming Soon..");
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
	}


}
