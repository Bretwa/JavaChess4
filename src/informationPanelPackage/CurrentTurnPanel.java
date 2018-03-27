/*
 * CurrentTurnPanel
 */

package informationPanelPackage;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;

public class CurrentTurnPanel extends JComponent
{
	private static final long serialVersionUID=1L;
	private String font="Arial";
	private static final int fontSize=16;
	private String textToBeDisplayed;
	private int width;
	
	// constructor : we set the right dimension of the component
	public CurrentTurnPanel(Graphics graphics,int widthParameter)
	{
		width=widthParameter;
		setPreferredSize(getDimension(graphics));
		setMaximumSize(getDimension(graphics));
		textToBeDisplayed="";
	}
	
	// we paint the text
	@Override
	public void paintComponent(Graphics graphicsParameter)
	{
		Graphics graphics=getGraphics();
		graphics.clearRect(0,0,getDimension(graphics).width,getDimension(graphics).height);
		graphicsParameter.setFont(new java.awt.Font(font,Font.BOLD,fontSize));
		graphicsParameter.drawString(textToBeDisplayed,0,getDimension(graphics).height-1);
	}
	
	// we set the text that has to be displayed
	public void setText(String playerHasToPlay)
	{
		textToBeDisplayed=playerHasToPlay;
		paintComponent(getGraphics());
	}
	
	// the dimension is calculated according to the size of the font
	public Dimension getDimension(Graphics graphics)
	{
		graphics.setFont(new java.awt.Font(font,Font.BOLD,fontSize));
		FontMetrics fontMetrics=graphics.getFontMetrics();
		return new Dimension(width,fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent());
	}
}
