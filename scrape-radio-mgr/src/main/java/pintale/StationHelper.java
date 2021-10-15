package pintale;

import java.util.Map;

import com.google.gson.JsonObject;

public class StationHelper
{
	private JsonObject config;
	private Map<String, Process> processes;

	public StationHelper(JsonObject config, Map<String, Process> processes)
	{
		this.config=config;
		this.processes=processes;
	}

	public void printStations()
	{
		System.out.println("Recording Station");
		System.out.println("--------- -------");
		
		for(String key : config.keySet())
		{
			if(!"all".equals(key))
			{
				String recording=processes.containsKey(key)?"yes":"   ";
				System.out.println("      "+recording+" "+key);
			}
		}
		
		System.out.print("> ");
	}
	
	public void startStopStation(String stationId) throws Exception
	{
		//unknown station
		if(!config.has(stationId))
		{
			System.out.println("unknown stationId: "+stationId);
		}
		//already recording, so stop
		else if(processes.containsKey(stationId))
		{
			processes.get(stationId).destroy();
			processes.remove(stationId);
		}
		//start recording
		else
		{
			JsonObject station=config.getAsJsonObject(stationId);
			JsonObject all=config.getAsJsonObject("all");
			String url=Util.get("url",station,all);
			String directory=Util.get("directory",station,all);
			String filename=Util.get("filename",station,all).replace("%S",stationId);
			String userAgent=Util.get("userAgent",station,all);
			
			Process process = new ProcessBuilder().inheritIO().command("streamripper", url, "-d", directory, "-D", filename, "--quiet",
					"-u", userAgent).start();
			processes.put(stationId,process);
		}
	}
}
