package pintale;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Util
{
	private static Map<String,String> defaultValues=new HashMap<String,String>() {{
		put("directory",".");
		put("filename","%S/%D##%A##%T");
		put("userAgent","Apple-iPhone4C1/1001.523");
		put("skipKnown","false");
	}};

	public static String get(String key, JsonObject object, JsonObject defaultObject)
	{
		JsonElement value = object.has(key)?object.get(key):(defaultObject!=null && defaultObject.has(key))?defaultObject.get(key):null;
		return (value==null)?defaultValues.get(key):value.getAsString();
	}
}
