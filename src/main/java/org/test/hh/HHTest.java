package org.test.hh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HHTest extends Steps {

    public static final String DATA_FILE = "Resources\\TestCases.txt";
    public static final String COUNTRIES_SEARCH = "http://api.hh.ru/areas/countries";
    public static final String EMPLOYER_SEARCH = "http://api.hh.ru/employers";
    public static final String AREAS_SEARCH = "http://api.hh.ru/areas";
    public static final String VACANCIES_SEARCH = "http://api.hh.ru/vacancies";

    private List<String> expectedvalue;
    private JSONParser parser;

    @BeforeClass
    public void beforeClass() {
        this.parser = new JSONParser();
        try {
            expectedvalue = Files.readAllLines(Paths.get(DATA_FILE));
        } catch (IOException e) {
            Assert.fail("File with TC doesn't exist");
        }
    }

    @Test
    public void testGetContriesCount() {

        HttpResponse response = this.sendHttpReq(COUNTRIES_SEARCH);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());

        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            Assert.assertTrue(parser.parse(responseString) instanceof JSONArray);
            JSONArray CountryPayload = (JSONArray) parser.parse(responseString);
            Assert.assertEquals(Integer.parseInt(expectedvalue.get(0)), CountryPayload.size());
        } catch (IOException | ParseException e) {
            Assert.fail("test1 failed! Couldn't get countries count");
        }
    }

    @Test
    public void testEmployerSearch() {

        int RussiaId = -1;

        HttpResponse response = sendHttpReq(COUNTRIES_SEARCH);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());

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
        } catch (IOException | ParseException e) {
            Assert.fail("test2 failed! Couldn't get country code");
        }
        Assert.assertNotEquals(RussiaId, -1);

        String url = EMPLOYER_SEARCH + "?text=новые%20облачные&area=" + RussiaId;
        response = sendHttpReq(url);
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
        } catch (IOException | ParseException e) {
            Assert.fail("test2 failed! Couldn't get employer");
        }
    }

    @Test
    public void testVacancySearch() {

        int SpbId = -1;

        HttpResponse response = sendHttpReq(AREAS_SEARCH);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
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
        } catch (IOException | ParseException e) {
            Assert.fail("test3 failed! Couldn't get area id");
        }

        Assert.assertNotEquals(SpbId, -1);

        String url = VACANCIES_SEARCH + "?text=QA%20Automation%20Engineer%20(Server)&area=" + SpbId;
        response = sendHttpReq(url);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());

        String responseString;
        try {
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            JSONObject EmployerFound = (JSONObject) parser.parse(responseString);
            Assert.assertEquals(Boolean.parseBoolean(expectedvalue.get(2)), (Long) EmployerFound.get("found")>0);
        } catch (IOException | ParseException e) {
            Assert.fail("test3 failed! Couldn't get vacancy");
        }
    }
}
