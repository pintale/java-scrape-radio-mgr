package pintale;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

public class FileHelper
{
	private static final String KNOWN_FILES_PATH = "knownFiles.txt";
	private JsonObject config;

	public FileHelper(JsonObject config)
	{
		this.config=config;
	}

	public void removeDuplicates() throws IOException
	{
		Set<String> knownFiles=getKnownsFromFile(KNOWN_FILES_PATH);
		Set<String> knownSongs=knownFiles.stream().map(f -> getSong(f)).collect(Collectors.toSet());
		JsonObject all=config.getAsJsonObject("all");
		
		for(String key : config.keySet())
		{
			if(!"all".equals(key))
			{
				JsonObject station=config.getAsJsonObject(key);
				String skipKnown=Util.get("skipKnown",station,all);
				
				if("true".equals(skipKnown))
				{
					String directory=Util.get("directory",station,all);
					skipKnown(directory,key,knownFiles,knownSongs);
				}
			}
		}
		
		String txt=knownFiles.stream().collect(Collectors.joining(System.lineSeparator()));
		Files.writeString(Paths.get(KNOWN_FILES_PATH), txt, StandardOpenOption.CREATE);
	}
	
	private String getSong(String filename)
	{
		//2021_09_24_12_12_53##NMB48##Don't Look Back!.mp3
		// to NMB48##Don't Look Back!.mp3
		return filename.substring(2+filename.indexOf("##"));
	}

	private Set<String> getKnownsFromFile(String path) throws IOException
	{
		if(!new File(path).exists())
		{
			return new HashSet<>();
		}
		
		return Files.lines(Paths.get(path)).collect(Collectors.toSet());
	}

	private void skipKnown(String parentDir, String stationId, Set<String> knownFiles, Set<String> knownSongs)
	{
		File dir=new File(parentDir,stationId);
		
		if(dir.isDirectory())
		{
			for(File file : dir.listFiles())
			{
				if(file.isFile())
				{
					String song=getSong(file.getName());
					
					if(knownSongs.contains(song) && !knownFiles.contains(file.getName()))
					{
						file.delete();
					}
					else if(!knownSongs.contains(song))
					{
						knownSongs.add(song);
						knownFiles.add(file.getName());
					}
				}
			}
		}
	}
}
