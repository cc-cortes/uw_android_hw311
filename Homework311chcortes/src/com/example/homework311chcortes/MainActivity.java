package com.example.homework311chcortes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity; 
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
	
	//A holder class to pass information for a single article
	private class ArticleInfo{
		ArticleInfo(){
			title = "";
			content = "";
			icon = "";
			date = null;
		}
		
		public String title;
		public String content;
		public String icon;
		public Date date;
	}

	//*********Members************************//
	
	//Reference to the Database
	ArticlesDbHelper dbHelper;
	
	//State of the UI, determines which fragment, list or details, is visible
	int viewState = 0;
	
	public static final int LIST_MODE = 1;
	public static final int DETAILS_MODE = 2;
	
	public static final String LIST_TAG = "list fragment";
	public static final String DETAILS_TAG = "details fragment";
	
	private ShakeEventManager mShaker;
	
	//*********Methods************************//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Set reference to a new database helper
	    dbHelper = new ArticlesDbHelper(getBaseContext());
		
	    //Clear the current articles in the db
	    dbHelper.clearArticlesInDatabase();
	    
	    //Add the articles back into the db from the xml
		readXmlIntoDb();
		
		//Add the list and details fragments into the UI, if they haven't already been added
		addFragmentsToUI();
		
		//Set the Shake Event Manager
		setShakeEventManager();
		
		//Set which fragments are visible
		setFragmentVisibleState();
	}

	private void setShakeEventManager() {
		 mShaker = new ShakeEventManager(this);
		    mShaker.setOnShakeListener(new ShakeEventManager.OnShakeListener () {
		      public void onShake()
		      {
		    	showOnShakeMessage();
		        readXmlIntoDb();
		      }
		    });
	}
	
	/*
	@Override
	  public void onResume()
	  {
	    mShaker.resume();
	    super.onResume();
	  }
	  @Override
	  public void onPause()
	  {
	    mShaker.pause();
	    super.onPause();
	  }
	  */
	
	public void showOnShakeMessage(){
		new AlertDialog.Builder(this)
        .setPositiveButton(android.R.string.ok, null)
        .setMessage("Data Refreshed!")
        .show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Save the Instance state of the UI
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  
	  //Save the view mode state
	  savedInstanceState.putInt("view_state", this.viewState);
	}
			
	// Restore UI state. This bundle has also been passed to onCreate.
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);	  
		
		//Restore the view mode state
		this.viewState = savedInstanceState.getInt("view_state");
		
		//Set the visibility of the fragments
		this.setFragmentVisibleState();
	}
	
	
	//Read the articles XML from the assets folder into the db
	private void readXmlIntoDb(){
		Document dom = readXmlIntoDom();
		writeDomIntoDb(dom);
	}
	
	//reads the articles XML from assets into a DOM structure
	//Create the Dom structure that temporarily holds the articles
	private Document readXmlIntoDom(){
		//Get the asset file as an input stream
		 AssetManager assetManager = getAssets();
         InputStream inputStream = null;
         Document articlesDoc;
         
         try {
             inputStream = assetManager.open("HRD314_data.xml");
         } catch (IOException e) {
             Log.e("tag", e.getMessage());
         }
         
       //Build the DOM from the input stream
         try {
             DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
             dbf.setNamespaceAware(false);
             dbf.setValidating(false);
             DocumentBuilder db = dbf.newDocumentBuilder();
             articlesDoc = db.parse(inputStream);
             int i = 1;
             i++; //debug break to view the doc
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }

         return articlesDoc;
	}
	
	//Write the articles from the inputed dom document into the database
	private void writeDomIntoDb(Document doc){
		NodeList articles = doc.getElementsByTagName("item");
		ArticleInfo ai;
		
		for(int i = 0; i < articles.getLength(); i++){
			ai = getArticleInfoFromNode(articles.item(i));
			writeArticleInfoIntoDb(ai);
		}
	}
	
	//Get the article info from a node and put it into an object
	private ArticleInfo getArticleInfoFromNode(Node node){
		ArticleInfo ai = new ArticleInfo();
		
		//Get title
		ai.title = getArticleTitleFromNode(node);
		
		//Get content
		ai.content = getArticleContentFromNode(node);
		
		//get icon
		ai.icon = getArticleIconFromNode(node);
		
		//get date
		ai.date = getArticleDateFromNode(node);
		
		return ai;
	}

	//get string from a sub-node of an article item node
	private String getStringFromArticleNode(Node node, String localName){
		NodeList nl = node.getChildNodes();
		ArrayList<Node> subnodes = new ArrayList<Node>();
				
		Node nodePointer;
		String localNamePointer = "";
		String xmlString = "";
		
		short nodeType;
		String nodeName;
				
		//Run through nodelist, add to nodes array if localname is "date"
		for(int i = 0; i < nl.getLength(); i++){
			nodePointer = nl.item(i);
			localNamePointer = nodePointer.getLocalName(); //Produces null for ElementImpl
			nodeType = nodePointer.getNodeType();		
			nodeName = nodePointer.getNodeName(); //Produces the local name for ElementImpl
			
			if(localName.contains(nodeName)){
				subnodes.add(nodePointer);
			}
		}
				
		//Malformed XML cases	
		if(subnodes.size() == 0){
			//If zero nodes, give empty string
			xmlString = "";
			return xmlString;
		}
		else if(subnodes.size() > 1){
			//If greater than one node, throw a flag but still give the first one
		}
		
		//If one node as expected, get the string
		nodePointer = subnodes.get(0);
		
		xmlString = nodePointer.getTextContent();
						
		//get the string child of the one node
		return xmlString;
	}

	//get the string in the date element from the inputted node and convert it to a date object
	private Date getArticleDateFromNode(Node node) {
		String localName = "date";
		String dateString = this.getStringFromArticleNode(node, localName);
		Date date;
		
		if(dateString == ""){
			date = new Date(Date.UTC(0, 0, 0, 0, 0, 0));
		}
		else{
			date = new Date(Date.parse(dateString));
		}
		
		return date;
	}

	//get the string in the icon element from the inputted node
	private String getArticleIconFromNode(Node node) {
		String localName = "icon";
		String iconString = this.getStringFromArticleNode(node, localName);
		
		return iconString;
	}

	//get the string in the content element from the inputted node
	private String getArticleContentFromNode(Node node) {
		String localName = "content";
		String contentString = this.getStringFromArticleNode(node, localName);
		
		return contentString;
	}

	//get the string in the title element from the inputted node
	private String getArticleTitleFromNode(Node node) {
		String localName = "title";
		String titleString = this.getStringFromArticleNode(node, localName);
		
		return titleString;
	}

	//Read the information from an ArticleInfo object and write it into the Db
	private void writeArticleInfoIntoDb(ArticleInfo ai){
		//wrapping this as there may be some required conversion/cleaning for nulls
		
		dbHelper.addArticleToDatabase(ai.title, ai.content, ai.icon, ai.date);
	}
	

	private void addFragmentsToUI() {
		addListFragmentToUI();
		addDetailsFragmentToUI();
	}
	
	//Adds the Article List Fragment to the UI, if it hasn't already been added
	private void addListFragmentToUI(){
		
		ArticleListFragment alf = new ArticleListFragment();
		
		FragmentManager fm = this.getSupportFragmentManager();
		
		Fragment f = fm.findFragmentByTag(LIST_TAG);
		
		if(f == null){
			//There is no fragment with that tag, so add it
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.listfragmentholder, alf, LIST_TAG);
			ft.commit();
		}
	}
	
	//Adds the Article Details Fragment to the UI, if it hasn't already been added
	private void addDetailsFragmentToUI(){

		ArticleDetailsFragment adf = new ArticleDetailsFragment();
			
		FragmentManager fm = this.getSupportFragmentManager();
			
		Fragment f = fm.findFragmentByTag(DETAILS_TAG);
			
		if(f == null){
			//There is no fragment with that tag, so add it
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.detailsfragmentholder, adf, DETAILS_TAG);
			ft.commit();
		}
	}
	
	//Set which fragments are visible
	private void setFragmentVisibleState(){
		if(this.viewState == 0){
			showListFragment();
			hideDetailsFragment();
		}
		else if(this.viewState == MainActivity.LIST_MODE){
			showListFragment();
			hideDetailsFragment();
		}
		else if(this.viewState == MainActivity.DETAILS_MODE){
			hideListFragment();
			showDetailsFragment();
		}
	}

	private void showDetailsFragment() {
		FragmentManager fm = this.getSupportFragmentManager();
		
		Fragment f = fm.findFragmentByTag(DETAILS_TAG);
		
		FragmentTransaction ft = fm.beginTransaction();
		
		if(f != null){
			ft.show(f);
			ft.commit();
			this.viewState = MainActivity.DETAILS_MODE;
		}
	}

	private void hideListFragment() {
		FragmentManager fm = this.getSupportFragmentManager();
		
		Fragment f = fm.findFragmentByTag(LIST_TAG);
		
		FragmentTransaction ft = fm.beginTransaction();
		
		if(f != null){
			ft.hide(f);
			ft.commit();
		}
	}

	private void hideDetailsFragment() {
		FragmentManager fm = this.getSupportFragmentManager();
		
		Fragment f = fm.findFragmentByTag(DETAILS_TAG);
		
		FragmentTransaction ft = fm.beginTransaction();
		
		if(f != null){
			ft.hide(f);
			ft.commit();
		}
	}

	private void showListFragment() {
		
		FragmentManager fm = this.getSupportFragmentManager();
		
		Fragment f = fm.findFragmentByTag(LIST_TAG);
		
		FragmentTransaction ft = fm.beginTransaction();
		
		if(f != null){
			ft.show(f);
			ft.commit();
			this.viewState = MainActivity.LIST_MODE;
		}
	}
	
	//If the details fragment is visible, make the list fragment visible
	@Override
	public void  onBackPressed (){
		FragmentManager fm = this.getSupportFragmentManager();
		
		Fragment f = fm.findFragmentByTag(DETAILS_TAG);
		
		if(f != null && !f.isHidden()){
			showListFragment();
			hideDetailsFragment();
		}
		else{
			super.onBackPressed();
		}
	}


	
}
