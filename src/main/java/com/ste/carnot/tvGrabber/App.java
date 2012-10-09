package com.ste.carnot.tvGrabber;

import java.awt.Cursor;
import java.io.File;
import java.net.UnknownHostException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

/**
 * App Class
 *
 */
public class App 
{
	private static Logger logger = LoggerFactory.getLogger(App.class);

	protected static JSONObject channelsList;
	private static JSONObject programList;
	private static Mongo mongo;
	private static DB database;
	protected static DBCollection channelsCollection;

	private static File file = new File("/Users/antoine/Documents/tvGrab/tv-2.xml");
	//private static File file = new File("/home/europ/tvGrab/tv.xml");
	private static String mongoAddress = "127.0.0.1";
	private static int mongoPort = 27017;
	private static String dbName = "tvGrabber";
	private static String collectionName;
	private static JSONArray array;



	public static void main( String[] args )
	{
		try {
			// Master instantiation
			Master master = new Master();

			// Find the channels list in the XML file
			channelsList = master.findChannels(file);

			// Get the channels
			array = (JSONArray) channelsList.get("channels");

			// Initialize the DB
			collectionName = "channels";
			initialiseDb();

			for(int i=0; i<array.size(); i++) {

				DBObject object = (DBObject) JSON.parse(array.get(i).toString());

				// Find the existence of the object
				DBCursor cursor = channelsCollection.find(object);
				logger.debug("{}", cursor.toString());

				// Save the channel if it does not exist in the DB
				if(cursor.size() == 0) {
					channelsCollection.insert(object);
				}
			}

			/*
			 * Programs
			 */

			//Find the program list in the XML File
			programList = master.findPrograms(file);
			logger.debug("programList: {}", programList.toJSONString());

			// Get the channels
			array = (JSONArray) programList.get("programs");
			logger.debug("array: {}", array.toJSONString());
			
			// Initialize the DB
			collectionName = "programs";
			channelsCollection = database.getCollection(collectionName);
			
			for(int i=0; i<array.size(); i++) {

				DBObject object = (DBObject) JSON.parse(array.get(i).toString());

				// Find the existence of the object
				DBCursor cursor = channelsCollection.find(object);
				logger.debug("{}", cursor.toString());

				// Save the channel if it does not exist in the DB
				if(cursor.size() == 0) {
					logger.debug("Try to update db");
					channelsCollection.insert(object);
				}
				logger.debug("Update finished");
			}
			
			mongo.close();
		}
		catch(Exception e) {
			logger.error("Bug: {}", e);
		}

	}

	private static void initialiseDb() {
		try {
			mongo = new Mongo(mongoAddress, mongoPort);
			database = mongo.getDB(dbName);
			channelsCollection = database.getCollection(collectionName);
		} catch (Exception e) {
			logger.error("Bug: {}", e);
		}  	
	}
}
