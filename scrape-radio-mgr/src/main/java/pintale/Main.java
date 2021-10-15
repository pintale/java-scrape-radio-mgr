package pintale;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Main implements Closeable
{
	private JsonObject config;
	private Map<String,Process> processes=new HashMap<>();
	private StationHelper stationHelper;
	private FileHelper fileHelper;
	private Timer timer = new Timer();
	
	public Main() throws Exception
	{
		config = new Gson().fromJson(Files.newBufferedReader(Paths.get("config.json")), JsonObject.class);
		stationHelper=new StationHelper(config,processes);
		fileHelper=new FileHelper(config);
		
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				try
				{
					fileHelper.removeDuplicates();
				}
				catch (IOException e)
				{
					System.err.println(e.getMessage());
				}
			}
		}, 1000, 60 * 1000);
	}
	
	public static void main(String[] args) throws Exception
	{
		try(Main main=new Main())
		{
			main.run();
		}
		catch(java.util.NoSuchElementException nsee)
		{
			//ignore
		}
	}

	public void run() throws Exception
	{
		try (Scanner scanner = new Scanner(System.in))
		{
			String input;

			do
			{
				stationHelper.printStations();
				input = scanner.nextLine();
				action(input);
			} while (!"exit".equals(input));
		}
	}

	private void action(String input) throws Exception
	{
		switch (input)
		{
		case "help":
			printHelp();
			break;
		case "exit":
			break;
		default:
			for(String key : config.keySet())
			{
				if(!"all".equals(key) && key.matches(input))
				{
					stationHelper.startStopStation(key);
				}
			}
		}
		System.out.println();
	}

	private void printHelp()
	{
		String url="https://github.com/pintale/java-scrape-radio-mgr";
		System.out.println("Visit "+url);
	}

	@Override
	public void close() throws IOException
	{
		timer.cancel();
		fileHelper.removeDuplicates();
		
		for(Process p : processes.values())
		{
			p.destroy();
		}
	}
}
