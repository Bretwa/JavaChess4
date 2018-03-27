package musicianPackage;

import java.io.BufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Musician
{
	public static int BUFFER_SIZE=128000;
	private static String moveFileName="move.wav";
	private static String victoryFileName="victory.wav";
	
	public void playSoundMove()
	{
		playSound(moveFileName);
	}
	
	public void playSoundVictory()
	{
		playSound(victoryFileName);
	}
	
	void playSound(String filename)
	{
		int nBytesRead=0;
		try
		{
			AudioInputStream audioStream=AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(filename)));
			SourceDataLine sourceLine=(SourceDataLine)AudioSystem.getLine(new DataLine.Info(SourceDataLine.class,audioStream.getFormat()));
			sourceLine.open(audioStream.getFormat());
			sourceLine.start();
			byte[] abData=new byte[BUFFER_SIZE];
			while(nBytesRead!=-1)
			{
				nBytesRead=audioStream.read(abData,0,abData.length);
				if(nBytesRead>=0)
					sourceLine.write(abData,0,nBytesRead);
			}			
			sourceLine.drain();
			sourceLine.close();
			audioStream.close();
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
	}
}