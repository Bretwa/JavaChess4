/*
 * MovesListCellRenderer is used to give good colors to the moves list
 */

package informationPanelPackage;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class MovesListCellRenderer extends JLabel implements ListCellRenderer<Object>
{
	private static final long serialVersionUID=1L;
	public int selectedIndex=-1;
	private boolean differentiateLines=true;
	
	// we create the good rendering for each cell
	@Override
	public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus)
	{
		setText(value.toString());
		if(differentiateLines==true)
		{
			Color background;
			Color foreground;
			if(index%2==0)
			{
				background=new Color(241,226,207); // white color
				foreground=Color.black;
			}
			else
			{
				background=new Color(0,0,0); // black color
				foreground=Color.white;
			}
			setBackground(background);
			setForeground(foreground);
		}
		else
		{
			setBackground(Color.white);
			setForeground(Color.black);
		}
		if(selectedIndex==index&&differentiateLines==true)
			setBorder(BorderFactory.createMatteBorder(3,0,0,0,Color.blue));
		else
			setBorder(null);
		return this;
	}
	
	public void setWithoutColors()
	{
		differentiateLines=false;
	}
	
	public void setWithColors()
	{
		differentiateLines=true;
	}
	
	public void setIndex(int selectedIndexParameter)
	{
		selectedIndex=selectedIndexParameter;
	}
	
	// constructor, set the items opaque
	public MovesListCellRenderer()
	{
		setOpaque(true);
	}
}