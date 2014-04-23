package com.example.homework311chcortes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ArticleDetailsFragment extends Fragment {
	
	public ArticleDetailsFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void  onCreate(Bundle savedInstanceState){
		//Hide this fragment at first creation
		this.hideDetailsFragment();
		
		super.onCreate(savedInstanceState);
	}
	

	 @Override
	 public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    
	    savedInstanceState.putString("title", getTitleFromTextView());
	    savedInstanceState.putString("content", getContentFromTextView());
	    
	 }
	 
	// Restore UI state. This bundle has also been passed to onCreate of activity.
	 @Override
	 public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	    if(savedInstanceState == null){
	    	return;
	    }
	    
	    //Put the text back in the text views
	    String title = savedInstanceState.getString("title");
	    String content = savedInstanceState.getString("content");   
	    
	    if(title != null && content != null){
	    	this.setTitleTextView(title);
		    this.setContentTextView(content);
	    }
	 }


	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        // Inflate the layout for this fragment
		 
	        return inflater.inflate(R.layout.fragment_article_details, container, false);
	    }
	
	public void setUIByArticleId(long id){
		
		setArticleTitleById(id);
		
		setArticleContentById(id);
		
	}

	private void setArticleContentById(long id) {
		//Get reference to a new database helper
	    ArticlesDbHelper dbHelper = new ArticlesDbHelper(getActivity().getBaseContext());
		
	    String content = dbHelper.getArticleContentById(id);
	    
	    setContentTextView(content);
	}

	private void setArticleTitleById(long id) {
		//Get reference to a new database helper
	    ArticlesDbHelper dbHelper = new ArticlesDbHelper(getActivity().getBaseContext());
		
	    String title = dbHelper.getArticleTitleById(id);
	    
	    setTitleTextView(title);
	}
	
	private void setContentTextView(String content){
		//Put the string into the textview
		TextView tv = (TextView) getView().findViewById(R.id.article_details_content);
		tv.setText(content);
	}
	
	private void setTitleTextView(String title){
		//put the string into the textview
		TextView tv = (TextView) getView().findViewById(R.id.article_details_title);
		tv.setText(title);
	}
	
	 private String getContentFromTextView() {
		 TextView tv = (TextView) getView().findViewById(R.id.article_details_content);
		return tv.getText().toString();
	}

	private String getTitleFromTextView() {
		TextView tv = (TextView) getView().findViewById(R.id.article_details_title);
		return tv.getText().toString();
	}
	


	private void hideDetailsFragment() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		
		Fragment f = fm.findFragmentByTag(MainActivity.DETAILS_TAG);
		
		FragmentTransaction ft = fm.beginTransaction();
		
		if(f != null){
			ft.hide(f);
			ft.commit();
		}
	}


}
