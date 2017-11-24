package com.learning.app.commontests.utils;

import com.learning.app.common.json.JsonReader;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.junit.Ignore;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.InputStream;
import java.util.Scanner;

/*
 * Class providing helper methods to read json files in tests
 */

@Ignore
public class JsonTestUtils {
	public static final String BASE_JSON_DIR = "json/";

	private JsonTestUtils()
	{
	}

	//read json file on relativePath and return as String
	public static String readJsonFile(String relativePath) {
        InputStream inputStream = JsonTestUtils.class.getClassLoader().getResourceAsStream(BASE_JSON_DIR +
				relativePath);
        try (Scanner s = new Scanner(inputStream)) {
			return s.useDelimiter("\\A").hasNext() ? s.next() : "";
		}
	}

	//Check if string is identical to content of json file
	public static void assertJsonMatchesFileContent(String actualJson, String fileNameWithExpectedJson) {
		assertJsonMatchesExpectedJson(actualJson, readJsonFile(fileNameWithExpectedJson));
	}

	//Compare two json strings
	public static void assertJsonMatchesExpectedJson(String actualJson, String expectedJson) {
		try {
			JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}
}