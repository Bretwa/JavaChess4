/*
 * Information panel is used to display information about the current game
 */

package informationPanelPackage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class InformationPanel extends JPanel
{
	private String checkDescription="check";
	private static final long serialVersionUID=1L;
	private Box verticalBox;
	private JScrollPane scrollPane;
	JList<String> movesList;
	JList<Boolean> movesListIsSpecial;
	private static final int width=309;
	private static final int spaceBetweenMovesPanelAndcurrentTurnInformationPanel=10;
	private int height;
	private ArrayList<Integer> listMovesDescriptionEnPassant;
	private ArrayList<String> listMovesDescriptionExplicit;
	private ArrayList<String> listMovesDescriptionStandardAtomic;
	private ArrayList<Boolean> listMovesDescriptionIsSpecial;
	private DefaultListModel<String> modelList;
	private CurrentTurnPanel currentTurnPanel;
	private MovesListCellRenderer movesListCellRenderer;
	boolean isItExplicit=true;
	
	public Boolean IsThisMoveSpecial(int index)
	{
		return listMovesDescriptionIsSpecial.get(index);
	}
	
	public int GetEnPassantIndex(int index)
	{
		if(index>=0)
			return listMovesDescriptionEnPassant.get(index);
		return -1;
	}
	
	// we add a new move description, we fill both explicit and standard description, simplest way actually
	public void addNewMoveDescription(String explicitMoveDescription,String standardMoveDescription,Boolean isSpecial,int enPassantIndex)
	{
		// we add in the two lists
		listMovesDescriptionEnPassant.add(enPassantIndex);
		listMovesDescriptionStandardAtomic.add(standardMoveDescription);
		listMovesDescriptionExplicit.add(explicitMoveDescription);
		listMovesDescriptionIsSpecial.add(isSpecial);
		
		// now we refresh the list according to the display style
		if(isItExplicit==true)
			modelList.add(movesList.getModel().getSize(),listMovesDescriptionExplicit.get(listMovesDescriptionExplicit.size()-1));
		else
		{
			modelList.clear();
			String concatenationStandardMovesDescription="";
			int counterStandardDescriptionsAtomic=0;
			int counterStandardDescriptionsFullLine=0;
			for(;counterStandardDescriptionsAtomic<listMovesDescriptionStandardAtomic.size();counterStandardDescriptionsAtomic++)
				if(counterStandardDescriptionsAtomic%2!=0)
				{
					concatenationStandardMovesDescription+=" "+listMovesDescriptionStandardAtomic.get(counterStandardDescriptionsAtomic);
					modelList.add(movesList.getModel().getSize(),concatenationStandardMovesDescription);
					concatenationStandardMovesDescription="";
				}
				else
				{
					counterStandardDescriptionsFullLine++;
					concatenationStandardMovesDescription=counterStandardDescriptionsFullLine+". "+listMovesDescriptionStandardAtomic.get(counterStandardDescriptionsAtomic);
					if(counterStandardDescriptionsAtomic==listMovesDescriptionStandardAtomic.size()-1)
						modelList.add(movesList.getModel().getSize(),concatenationStandardMovesDescription);
				}
		}
		movesList.scrollRectToVisible(new Rectangle(0,1000000,1,1)); // this is useful for autoscroll
		paintComponents(getGraphics());
	}
	
	// constructor, we set the right dimensions to the component
	public InformationPanel(int heightParameter,Graphics graphics)
	{
		movesListCellRenderer=new MovesListCellRenderer();
		currentTurnPanel=new CurrentTurnPanel(graphics,width);
		height=heightParameter;
		setLayout(null);
		setLayout(new BorderLayout());
		listMovesDescriptionEnPassant=new ArrayList<Integer>();
		listMovesDescriptionExplicit=new ArrayList<String>();
		listMovesDescriptionStandardAtomic=new ArrayList<String>();
		listMovesDescriptionIsSpecial=new ArrayList<Boolean>();
		modelList=new DefaultListModel<String>();
		movesList=new JList<String>(modelList);
		movesList.setCellRenderer(movesListCellRenderer);
		scrollPane=new JScrollPane(movesList);
		verticalBox=Box.createVerticalBox();
		verticalBox.add(currentTurnPanel);
		verticalBox.add(Box.createRigidArea(new Dimension(width,spaceBetweenMovesPanelAndcurrentTurnInformationPanel)));
		verticalBox.add(scrollPane);
		verticalBox.setBounds(0,0,width,height);
		add(verticalBox);
		setPreferredSize(GetDimension());
		setMaximumSize(GetDimension());
	}
	
	public int GetNumberOfMoves()
	{
		return listMovesDescriptionExplicit.size();
	}
	
	// we change the text and repaint the component
	public void SetPlayerTurn(String playerHasToPlay)
	{
		currentTurnPanel.setText(playerHasToPlay);
		currentTurnPanel.repaint();
	}
	
	// clear the entire list
	public void ClearList()
	{
		listMovesDescriptionEnPassant.clear();
		listMovesDescriptionIsSpecial.clear();
		listMovesDescriptionStandardAtomic.clear();
		listMovesDescriptionExplicit.clear();
		modelList.clear();
	}
	
	public Dimension GetDimension()
	{
		return new Dimension(width,height);
	}
	
	public void UndrawLines()
	{
		movesListCellRenderer.setIndex(-1);
		movesListCellRenderer.repaint();
		scrollPane.repaint();
	}
	
	public void DrawLine(int indexMoves)
	{
		movesListCellRenderer.setIndex(indexMoves);
		movesListCellRenderer.repaint();
		scrollPane.repaint();
	}
	
	public String GetStringAt(int indexMoves)
	{
		return listMovesDescriptionExplicit.get(indexMoves);
	}
	
	public String GetSourceSquare(int indexMoves)
	{
		int indexHyphen=listMovesDescriptionExplicit.get(indexMoves).indexOf("-");
		if(indexHyphen==-1)
			return null;
		return listMovesDescriptionExplicit.get(indexMoves).substring(indexHyphen-2,indexHyphen);
	}
	
	public String GetDestinationSquare(int indexMoves)
	{
		int indexHyphen=listMovesDescriptionExplicit.get(indexMoves).indexOf("-");
		return listMovesDescriptionExplicit.get(indexMoves).substring(indexHyphen+1,indexHyphen+3);
	}
	
	// we get the type of piece deleted in the move list,
	public String GetPieceTypeEventuallyDeleted(int indexMoves)
	{
		String eatString="captures";
		int indexEat=listMovesDescriptionExplicit.get(indexMoves).indexOf(eatString);
		if(indexEat!=-1)
		{
			if(listMovesDescriptionExplicit.get(indexMoves).indexOf(checkDescription)!=-1)
				return listMovesDescriptionExplicit.get(indexMoves).substring(indexEat+eatString.length()+1,listMovesDescriptionExplicit.get(indexMoves).substring(indexEat+eatString.length()+1).indexOf(" ")+indexEat+eatString.length()+1);
			else
				return listMovesDescriptionExplicit.get(indexMoves).substring(indexEat+eatString.length()+1,listMovesDescriptionExplicit.get(indexMoves).length());
		}
		else
			return "";
	}
	
	// delete historic is useful for the unmake/remake move and delete moves, all is based on the explicit array moves
	public void DeleteHistoricUntil(int indexMoves)
	{
		if(indexMoves!=-1) // we check this deletion has a sense
		{
			int initialListSize=listMovesDescriptionExplicit.size();
			for(int counterMoves=indexMoves;counterMoves<initialListSize;counterMoves++)
			{
				listMovesDescriptionExplicit.remove(indexMoves); // we remove the explicit description which
				listMovesDescriptionStandardAtomic.remove(indexMoves);
				listMovesDescriptionIsSpecial.remove(indexMoves);
				listMovesDescriptionEnPassant.remove(indexMoves);
			}
			
			// we clean the model in order to rebuild it from the explicit array moves or atomic
			modelList.clear();
			if(isItExplicit==true)
				for(int counterMoves=0;counterMoves<listMovesDescriptionExplicit.size();counterMoves++)
					modelList.add(movesList.getModel().getSize(),listMovesDescriptionExplicit.get(counterMoves));
			else
			{
				String concatenationStandardMovesDescription="";
				int counterStandardDescriptionsAtomic=0;
				int counterStandardDescriptionsFullLine=0;
				for(;counterStandardDescriptionsAtomic<listMovesDescriptionStandardAtomic.size();counterStandardDescriptionsAtomic++)
					if(counterStandardDescriptionsAtomic%2!=0)
					{
						concatenationStandardMovesDescription+=" "+listMovesDescriptionStandardAtomic.get(counterStandardDescriptionsAtomic);
						modelList.add(movesList.getModel().getSize(),concatenationStandardMovesDescription);
						concatenationStandardMovesDescription="";
					}
					else
					{
						counterStandardDescriptionsFullLine++;
						concatenationStandardMovesDescription=counterStandardDescriptionsFullLine+". "+listMovesDescriptionStandardAtomic.get(counterStandardDescriptionsAtomic);
						if(counterStandardDescriptionsAtomic==listMovesDescriptionStandardAtomic.size()-1)
							modelList.add(movesList.getModel().getSize(),concatenationStandardMovesDescription);
					}
			}
		}
	}
	
	public boolean IsPairNumberOfMoves()
	{
		if(listMovesDescriptionExplicit.size()%2==0)
			return true;
		return false;
	}
	
	// set the notation description to standard
	public void SetToStandardNotation()
	{
		modelList.clear();
		isItExplicit=false;
		String concatenationStandardMovesDescription="";
		int counterStandardDescriptionsAtomic=0;
		int counterStandardDescriptionsFullLine=0;
		for(;counterStandardDescriptionsAtomic<listMovesDescriptionStandardAtomic.size();counterStandardDescriptionsAtomic++)
			if(counterStandardDescriptionsAtomic%2!=0)
			{
				concatenationStandardMovesDescription+=" "+listMovesDescriptionStandardAtomic.get(counterStandardDescriptionsAtomic);
				modelList.add(movesList.getModel().getSize(),concatenationStandardMovesDescription);
				concatenationStandardMovesDescription="";
			}
			else
			{
				counterStandardDescriptionsFullLine++;
				concatenationStandardMovesDescription=counterStandardDescriptionsFullLine+". "+listMovesDescriptionStandardAtomic.get(counterStandardDescriptionsAtomic);
				if(counterStandardDescriptionsAtomic==listMovesDescriptionStandardAtomic.size()-1)
					modelList.add(movesList.getModel().getSize(),concatenationStandardMovesDescription);
			}
		movesListCellRenderer.setWithoutColors();
	}
	
	// set the notation description to explicit
	public void SetToExplicitNotation(int indexMoves)
	{
		movesListCellRenderer.selectedIndex=indexMoves;
		isItExplicit=true;
		movesListCellRenderer.setWithColors();
		modelList.clear();
		for(int counterExplicitDescriptions=0;counterExplicitDescriptions<listMovesDescriptionExplicit.size();counterExplicitDescriptions++)
			modelList.add(movesList.getModel().getSize(),listMovesDescriptionExplicit.get(counterExplicitDescriptions));
	}
	
	// get all the moves descriptions according to the standard notation
	public ArrayList<String> GetStandardArrayMovesDescription()
	{
		ArrayList<String> listMovesDescriptionStandard=new ArrayList<String>();
		String concatenationStandardMovesDescription="";
		int counterStandardDescriptionsAtomic=0;
		int counterStandardDescriptionsFullLine=0;
		for(;counterStandardDescriptionsAtomic<listMovesDescriptionStandardAtomic.size();counterStandardDescriptionsAtomic++)
			if(counterStandardDescriptionsAtomic%2!=0)
			{
				concatenationStandardMovesDescription+=" "+listMovesDescriptionStandardAtomic.get(counterStandardDescriptionsAtomic);
				listMovesDescriptionStandard.add(concatenationStandardMovesDescription);
				concatenationStandardMovesDescription="";
			}
			else
			{
				counterStandardDescriptionsFullLine++;
				concatenationStandardMovesDescription=counterStandardDescriptionsFullLine+". "+listMovesDescriptionStandardAtomic.get(counterStandardDescriptionsAtomic);
				if(counterStandardDescriptionsAtomic==listMovesDescriptionStandardAtomic.size()-1)
					listMovesDescriptionStandard.add(concatenationStandardMovesDescription);
			}
		
		return listMovesDescriptionStandard;
	}
}
