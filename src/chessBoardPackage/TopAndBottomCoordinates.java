package chessBoardPackage;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;

public class TopAndBottomCoordinates extends JComponent
{
	private static final long serialVersionUID=1L;
	private int squareSize=60;
	private int numberOfSquarePerLine=8;
	private String font="Bookman Old Style";
	private int fontSize=12;
	private boolean leftToRight=true;
	
	public TopAndBottomCoordinates(Graphics graphics)
	{
		setPreferredSize(getDimension(graphics));
		setSize(getDimension(graphics));
	}
	
	// we repaint each letter for the line
	@Override
	public void paintComponent(Graphics graphics)
	{
		graphics.setFont(new java.awt.Font(font,Font.PLAIN,fontSize));
		Character[] heightFirstLettersOfTheAlphabet=
		{'a','b','c','d','e','f','g','h'};
		for(int counterLetter=0;counterLetter<numberOfSquarePerLine;counterLetter++)
		{
			FontMetrics fontMetrics=graphics.getFontMetrics();
			int textSize=fontMetrics.getMaxAscent()-1;
			String textToBeDisplayed;
			if(leftToRight==true)
				textToBeDisplayed=heightFirstLettersOfTheAlphabet[counterLetter].toString();
			else
				textToBeDisplayed=heightFirstLettersOfTheAlphabet[numberOfSquarePerLine-counterLetter-1].toString();
			graphics.drawString(textToBeDisplayed,(getWidth()-squareSize*numberOfSquarePerLine)/2+counterLetter*squareSize+squareSize/2-fontMetrics.stringWidth(textToBeDisplayed)/2,textSize);
		}
	}
	
	// the dimension is calculated according to the size of the font
	public Dimension getDimension(Graphics graphics)
	{
		graphics.setFont(new java.awt.Font(font,Font.PLAIN,fontSize));
		FontMetrics fontMetrics=graphics.getFontMetrics();
		return new Dimension(squareSize*numberOfSquarePerLine,fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent());
	}
	
	public void turn180Degrees()
	{
		leftToRight=!leftToRight;
	}
}
