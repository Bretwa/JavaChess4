package chessBoardPackage;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class LeftAndRightCoordinates extends JComponent
{
	private static final long serialVersionUID=1L;
	private int squareSize=60;
	private int numberOfSquarePerLine=8;
	private String font="Bookman Old Style";
	private Character[] heightFirstDigits=
	{'1','2','3','4','5','6','7','8'};
	private int spaceAtLeftAndRightOfEachLetter=2;
	private int fontSize=12;
	private boolean bottomToTop=true;
	
	public LeftAndRightCoordinates(Graphics graphics)
	{
		setPreferredSize(getDimension(graphics));
		setSize(getDimension(graphics));
	}
	
	// we repaint each letter for the line
	@Override
	public void paintComponent(Graphics graphics)
	{
		graphics.setFont(new java.awt.Font(font,Font.PLAIN,fontSize));
		for(int counterLetter=0;counterLetter<numberOfSquarePerLine;counterLetter++)
		{
			Graphics2D g2d=(Graphics2D)(graphics);
			FontMetrics fontMetrics2=g2d.getFontMetrics();
			String textToBeDisplayed="";
			if(bottomToTop==true)
				textToBeDisplayed=heightFirstDigits[counterLetter].toString();
			else
				textToBeDisplayed=heightFirstDigits[numberOfSquarePerLine-counterLetter-1].toString();
			Rectangle2D r=fontMetrics2.getStringBounds(textToBeDisplayed,graphics);
			graphics.drawString(textToBeDisplayed,spaceAtLeftAndRightOfEachLetter,squareSize*numberOfSquarePerLine-counterLetter*squareSize-squareSize/2-(int)r.getCenterY());
		}
	}
	
	// the dimension is calculated according to the size of the font
	public Dimension getDimension(Graphics graphics)
	{
		graphics.setFont(new java.awt.Font(font,Font.PLAIN,fontSize));
		FontMetrics fontMetrics=graphics.getFontMetrics();
		int maximumWidth=0;
		for(int counterDigits=0;counterDigits<numberOfSquarePerLine;counterDigits++)
			if(fontMetrics.stringWidth(heightFirstDigits[counterDigits].toString())>maximumWidth)
				maximumWidth=fontMetrics.stringWidth(heightFirstDigits[counterDigits].toString());
		return new Dimension(maximumWidth+2*spaceAtLeftAndRightOfEachLetter,squareSize*numberOfSquarePerLine);
	}
	
	public void turn180Degrees()
	{
		bottomToTop=!bottomToTop;
	}
}
