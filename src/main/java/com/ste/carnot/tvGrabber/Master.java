package com.ste.carnot.tvGrabber;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Master {

	private Logger logger = LoggerFactory.getLogger(Master.class);
	private Document document;
	private Element root;

	public JSONObject findChannels(File file) {

		logger.info("in method findChannels");

		try {
			// Instantiate a new JSONObject
			JSONObject dataToSend = new JSONObject();
			JSONArray array = new JSONArray();

			// Create a SAXBuilder Instantiation
			SAXBuilder sxb = new SAXBuilder();

			// Load the XML file
			this.document = sxb.build(file);

			// New Element root 
			this.root = this.document.getRootElement();

			// Channels List
			List channelsList = this.root.getChildren("channel");

			// List iterator
			Iterator i = channelsList.iterator();

			while(i.hasNext()) {
				// Define the active Element
				Element current = (Element) i.next();

				// Create a JSONObject where we can add different keys/values
				JSONObject obj = new JSONObject();
				obj.put("name", current.getChild("display-name").getText());
				obj.put("id", current.getAttributeValue("id"));
				array.add(obj);
			}

			dataToSend.put("channels", array);

			logger.debug("{}", dataToSend.toJSONString());
			return dataToSend;
		}
		catch(Exception e) {
			logger.error("Bug: {}", e);
			return null;
		}
	}

	public JSONObject findPrograms(File file) {

		try {
			// Instantiate a new JSONObject
			JSONObject dataToSend = new JSONObject();
			JSONArray array = new JSONArray();

			// Create a SAXBuilder Instantiation
			SAXBuilder sxb = new SAXBuilder();

			// Load the XML file
			this.document = sxb.build(file);

			// New Element root 
			this.root = this.document.getRootElement();

			// Channels List
			List programList = this.root.getChildren("programme");

			// List iterator
			Iterator iterator = programList.iterator();

			while(iterator.hasNext()) {
				// Define the active Element
				Element current = (Element) iterator.next();

				// Create a JSONObject where we can add different keys/values
				JSONObject obj = new JSONObject();

				//Find firt informations in the current.Attributes
				List<Attribute> listAttributes = current.getAttributes();

				for (int i=0; i<listAttributes.size(); i++) {
					Date date = null;
					
					switch(listAttributes.get(i).getName()) {
					case "channel":
						obj.put("channel", listAttributes.get(i).getValue());
						break;

					case "start":
						date = parseDate(listAttributes.get(i).getValue());
						logger.debug("start: {}", date.toString());
						obj.put("start", date);
						break;

					case "stop":
						date = parseDate(listAttributes.get(i).getValue());
						logger.debug("stop: {}", date.toString());
						obj.put("stop", date);
						break;
						
					case "showview":
						obj.put("showview", listAttributes.get(i).getValue());
						break;
					}	
				}

				List<Element> element = current.getChildren();
				logger.debug("Element List: {}", element.toString());

				Element testElement = null;

				for(int i=0; i<element.size(); i++) {
					logger.debug("element: {}", element.get(i));
					switch(element.get(i).getName()) {				
					case "title":
						obj.put("title", current.getChild("title").getText());
						break;
					case "sub-title":
						obj.put("subtitle", current.getChild("sub-title").getText());
						break;
					case "desc":
						obj.put("desc", current.getChild("desc").getText());
						break;
					case "date":
						obj.put("date", current.getChild("date").getText());
						break;
					case "category":
						obj.put("category", current.getChild("category").getText());
						break;
					case "episode":
						obj.put("episode", current.getChild("episode-num").getText());
						break;
					case "video":
						List<Element> list = current.getChild("video").getChildren();
						for(int j=0; j<list.size(); j++) {
							switch(list.get(j).getName()) {
							case "aspect":
								obj.put("aspect", current.getChild("video").getChild("aspect").getText());
								break;
							case "quality":
								obj.put("quality", current.getChild("video").getChild("quality").getText());
								break;
							default:
							}
						}
						break;
					case "rating":
						if(current.getChild("rating").getChild("value").getName() == "value") {
							obj.put("rating", current.getChild("rating").getChild("value").getText());
						}
						break;
					default:
						logger.debug("default !!!");
						break;
					}
				}

				array.add(obj);
			}

			dataToSend.put("programs", array);
			return dataToSend;
		}
		catch(Exception e) {
			logger.error("Bug: {}", e);
			return null;
		}


	}
	
	protected Date parseDate(String date) {
		try {
			Date convertedDate = new SimpleDateFormat("YYYYMMddhhmmss").parse(date);
			return convertedDate;
		}
		catch(Exception e) {
			logger.error("Bug: {}", e.toString());
			return null;
		}
	}
}
