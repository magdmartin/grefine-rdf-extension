package org.deri.grefine.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.testng.Assert;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;

import static org.testng.Assert.assertEquals;

public class TestUtils {
	public static void isSerializedTo(Object obj, String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jacksonJson = mapper.writeValueAsString(obj);
            testJsonEquivalence(jacksonJson, json);
    	} catch (JsonProcessingException e) {
    	    e.printStackTrace();
    	    Assert.fail("jackson serialization failed");
    	}
	}
	
	public static void testJsonEquivalence(String actual, String expected) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(actual);
			
			JsonNode  expectedObj = mapper.readTree(expected);
			if (! actualObj.equals(expectedObj)) {
				jsonDiff(expected, actual);
			}
			assertEquals((Object) actualObj, (Object) expectedObj);
		} catch(IOException e) {
			Assert.fail("Invalid JSON payload");
		}
	}
	
    public static void jsonDiff(String a, String b) throws JsonParseException, JsonMappingException {
        ObjectMapper myMapper = new ObjectMapper().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                .configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            JsonNode nodeA = myMapper.readValue(a, JsonNode.class);
            JsonNode nodeB = myMapper.readValue(b, JsonNode.class);
            String prettyA = myMapper.writeValueAsString(myMapper.treeToValue(nodeA, Object.class));
            String prettyB = myMapper.writeValueAsString(myMapper.treeToValue(nodeB, Object.class));
            
            // Compute the max line length of A
            LineNumberReader readerA = new LineNumberReader(new StringReader(prettyA));
            int maxLength = 0;
            String line = readerA.readLine();
            while (line != null) {
                if(line.length() > maxLength) {
                    maxLength = line.length();
                }
                line = readerA.readLine();
            }
            
            // Pad all lines
            readerA = new LineNumberReader(new StringReader(prettyA));
            LineNumberReader readerB = new LineNumberReader(new StringReader(prettyB));
            StringWriter writer = new StringWriter();
            String lineA = readerA.readLine();
            String lineB = readerB.readLine();
            while(lineA != null || lineB != null) {
                if (lineA == null) {
                    lineA = "";
                }
                if (lineB == null) {
                    lineB = "";
                }
                String paddedLineA = lineA +  new String(new char[maxLength + 2 - lineA.length()]).replace("\0", " ");
                writer.write(paddedLineA);
                writer.write(lineB + "\n");
                lineA = readerA.readLine();
                lineB = readerB.readLine();
            }
            System.out.print(writer.toString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
