package org.test.hh;

import org.testng.annotations.Test;

import org.testng.annotations.BeforeClass;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HHTest extends Steps {
	
	public final static String DataFile = "Resources\\TestCases.txt";
	private List<String> expectedvalue;
	
	@BeforeClass
	public void beforeClass() {
		try {
			expectedvalue = Files.readAllLines(Paths.get(DataFile));
		} catch (IOException e) {
			Assert.fail("File with TC doesn't exist");
			e.printStackTrace();
		}
		System.out.println("@BeforeClass: Done");
	}

	@Test
	public void Test1 ( ) {
		
		String url = "http://api.hh.ru/areas/countries";
		HttpResponse response = SendHttpReq(url);
		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		
		try {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			JSONParser parser = new JSONParser();
			
			Assert.assertTrue(parser.parse(responseString) instanceof JSONArray);
			JSONArray CountryPayload = (JSONArray) parser.parse(responseString);
			Assert.assertEquals(Integer.parseInt(expectedvalue.get(0)), CountryPayload.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void Test2 () {
		int RussiaId = -1;
		
		String url = "http://api.hh.ru/areas/countries";
		HttpResponse response = SendHttpReq(url);
		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		JSONParser parser = new JSONParser();
		
		try {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			Assert.assertTrue(parser.parse(responseString) instanceof JSONArray);
			JSONArray CountryPayload = (JSONArray) parser.parse(responseString);
			for (int i=0; i< CountryPayload.size(); i++){
				JSONObject CountryObject = (JSONObject) CountryPayload.get(i);
				if ("Россия".equals(CountryObject.get("name"))) {
					RussiaId = Integer.parseInt((String) CountryObject.get("id"));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Assert.assertNotEquals(RussiaId, -1);
		
		url = "http://api.hh.ru/employers?text=новые%20облачные&area="+RussiaId;
		response = SendHttpReq(url);
		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		
		String responseString;
		try {
			responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			JSONObject EmployerFound = (JSONObject) parser.parse(responseString);
			Assert.assertNotEquals(0, (Long) EmployerFound.get("found"));
			JSONArray itemsArray = (JSONArray) EmployerFound.get("items");
			boolean found = false;
			for (int i = 0; i < itemsArray.size(); i++) {
				JSONObject employerItem = (JSONObject) itemsArray.get(i);
				if (expectedvalue.get(1).equals(employerItem.get("name"))) {
					found = true;
					break;
				}
			}
			Assert.assertTrue(found);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void Test3 () {
		int SpbId = -1;
		
		String url = "http://api.hh.ru/areas";
		HttpResponse response = SendHttpReq(url);
		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		JSONParser parser = new JSONParser();
		
		try {
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			Assert.assertTrue(parser.parse(responseString) instanceof JSONArray);
			JSONArray CountryPayload = (JSONArray) parser.parse(responseString);
			for (int i=0; i< CountryPayload.size(); i++){
				JSONObject CountryObject = (JSONObject) CountryPayload.get(i);
				if ("Россия".equals(CountryObject.get("name"))) {
					JSONArray AreaPayload = (JSONArray) CountryObject.get("areas");
					for (int j=0; j< AreaPayload.size(); j++){
						JSONObject AreaFound = (JSONObject) AreaPayload.get(j);
						if ("Санкт-Петербург".equals(AreaFound.get("name"))) {
							SpbId = Integer.parseInt((String) AreaFound.get("id"));
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Assert.assertNotEquals(SpbId, -1);
		
		url = "http://api.hh.ru/vacancies?text=QA%20Automation%20Engineer%20(Server)&area="+SpbId;
		response = SendHttpReq(url);
		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		
		String responseString;
		try {
			responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			JSONObject EmployerFound = (JSONObject) parser.parse(responseString);
			Assert.assertEquals(Boolean.parseBoolean(expectedvalue.get(2)), (Long) EmployerFound.get("found")>0);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
}
