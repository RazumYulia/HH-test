package org.test.hh;

import org.testng.annotations.Test;

import org.testng.annotations.BeforeClass;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
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
	public void Test2 () throws IOException {

		
	}
	
	@Test
	public void Test3 () throws IOException {
		

	}
	
}
