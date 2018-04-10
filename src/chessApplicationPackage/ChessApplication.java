package chessApplicationPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;

import chessBoardPackage.ChessBoardMouseListener;
import chessBoardPackage.ChessBoardWithCoordinates;
import informationPanelPackage.InformationPanel;
import musicianPackage.Musician;

public class ChessApplication extends JFrame implements ActionListener
{
	// for the sound
	static URL currentUrl;
	Musician musician;
	public String moveSound="move.wav";
	public String victorySound="victory.wav";
	
	private final int maximumGameDescriptionLength=80;
	private int indexMoves=-1;
	private static final int white=1;
	private static final int black=-white;
	private static final int whiteIsPat=2*white;
	private static final int blackIsPat=2*black;
	private static final int noCurrentGame=0;
	
	private final String applicationName="JavaChess 4";
	public static Point oldSelectedSquare;
	private static ChessBoardMouseListener chessBoardMouseListener;
	
	// the spaces between component
	private static final int spaceAtRightOfTheInformationPanel=10;
	private static final int spaceAtBottomOfTheChessboard=10;
	private static final int spaceAtTopOfTheChessboard=10;
	private static final int spaceAtLeftOfTheChessboard=10;
	private static final int spaceAtRightOfTheChessboard=10;
	
	// for the menu bar
	JMenuBar menuBar;
	public static int maximumDepth=8;
	public static int defaultDepth=3;
	private static final String game="Game";
	private static final String quit="Quit";
	private static final String newGame="New game";
	private static final String saveGame="Save game";
	private static final String loadGame="Load game";
	public static String computerLevel="Computer level";
	public static String computerPlaysBlack="Computer plays black";
	public static String computerPlaysWhite="Computer plays white";
	private static final String moves="Moves";
	private static final String cancelMove="Cancel move";
	private static final String replayMove="Replay move";
	private static final String options="Options";
	private static final String turnChessBoard="Turn chessboard";
	private static final String slowSliding="Slow Sliding";
	private static final String fastSliding="Fast Sliding";
	private static final String noSliding="No Sliding";
	private static final String texturedSquares="Textured squares";
	private static final String standardNotation="Standard notation";
	private static final String explicitNotation="Explicit notation";
	private static final String playSound="Play sound";
	private static final String randomMoves="Random moves";
	private final JMenuItem itemComputerPlaysBlack;
	private final JMenuItem itemComputerPlaysWhite;
	private final JMenuItem itemRandomMoves;
	private final JMenuItem itemPlaySound;
	public static ArrayList<JRadioButtonMenuItem> arrayListBlackLevel;
	public static ArrayList<JRadioButtonMenuItem> arrayListWhiteLevel;
	public static String blackPlayerLevel="Black player level";
	public static String whitePlayerLevel="White player level";
	public static String help="Help";
	public static String howToPlay="How to play";
	
	// tips window
	private static final int tipsFrameWidth=600;
	private static final int tipsFrameHeight=230;
	public static String tips="Tips";
	public static final int tipsBorderSize=5;
	
	private final InformationPanel informationPanel; // the information panel, used to options information
	private final Box mainHorizontalBox;
	private final Box mainVerticalBox;
	private static final long serialVersionUID=1L;
	private final ChessRuler chessRuler;
	private static final int SQUARES_NUMBER_PER_LINE=8;
	private final String piecesMatrix[][]; // used for piece representation
	ChessBoardWithCoordinates chessBoardWithCoordinates=null; // the chessboard, the main component of the game
	int isLastMoveEnableEnPassant; // for en passant memory
	
	public static void main(String[] args)
	{
		new ChessApplication();
	}
	
	public ChessApplication()
	{
		musician=new Musician();
		chessRuler=new ChessRuler();
		piecesMatrix=new String[SQUARES_NUMBER_PER_LINE][SQUARES_NUMBER_PER_LINE];
		for(int CounterVertical=0;CounterVertical<SQUARES_NUMBER_PER_LINE;CounterVertical++)
			for(int CounterHorizontal=0;CounterHorizontal<SQUARES_NUMBER_PER_LINE;CounterHorizontal++)
				piecesMatrix[CounterVertical][CounterHorizontal]=new String("");
		oldSelectedSquare=new Point(-1,-1);
		isLastMoveEnableEnPassant=-1;
		
		// first of all we create the menu bar and add items on it
		menuBar=new JMenuBar();
		JMenu menuGame=new JMenu(game);
		JMenuItem itemNewGame=new JMenuItem(newGame);
		menuGame.add(itemNewGame);
		itemNewGame.addActionListener(this);
		JMenuItem itemSaveGame=new JMenuItem(saveGame);
		menuGame.add(itemSaveGame);
		itemSaveGame.addActionListener(this);
		JMenuItem itemLoadGame=new JMenuItem(loadGame);
		menuGame.add(itemLoadGame);
		itemLoadGame.addActionListener(this);
		menuGame.addSeparator();
		itemComputerPlaysBlack=new JCheckBoxMenuItem(computerPlaysBlack);
		itemComputerPlaysBlack.addActionListener(this);
		itemComputerPlaysBlack.setSelected(true); // computer play blacks by default
		
		menuGame.add(itemComputerPlaysBlack);
		itemComputerPlaysWhite=new JCheckBoxMenuItem(computerPlaysWhite);
		itemComputerPlaysWhite.addActionListener(this);
		menuGame.add(itemComputerPlaysWhite);
		menuGame.addSeparator();
		JMenuItem itemQuit=new JMenuItem(quit);
		menuGame.add(itemQuit);
		itemQuit.addActionListener(this);
		menuBar.add(menuGame);
		JMenu menuMoves=new JMenu(moves);
		JMenuItem itemCancelMove=new JMenuItem(cancelMove);
		itemCancelMove.setAccelerator(KeyStroke.getKeyStroke('c'));
		menuMoves.add(itemCancelMove);
		itemCancelMove.addActionListener(this);
		JMenuItem itemReplayMove=new JMenuItem(replayMove);
		itemReplayMove.setAccelerator(KeyStroke.getKeyStroke('r'));
		menuMoves.add(itemReplayMove);
		itemReplayMove.addActionListener(this);
		menuBar.add(menuMoves);
		JMenu menuOptions=new JMenu(options);
		ButtonGroup group=new ButtonGroup();
		JRadioButtonMenuItem itemStandardNotation=new JRadioButtonMenuItem(standardNotation);
		menuOptions.add(itemStandardNotation);
		group.add(itemStandardNotation);
		itemStandardNotation.addActionListener(this);
		JRadioButtonMenuItem itemExplicitNotation=new JRadioButtonMenuItem(explicitNotation);
		itemExplicitNotation.setSelected(true);
		menuOptions.add(itemExplicitNotation);
		group.add(itemExplicitNotation);
		itemExplicitNotation.addActionListener(this);
		menuOptions.addSeparator();
		ButtonGroup slidingGroup=new ButtonGroup();
		JRadioButtonMenuItem itemSlowSliding=new JRadioButtonMenuItem(slowSliding);
		menuOptions.add(itemSlowSliding);
		slidingGroup.add(itemSlowSliding);
		itemSlowSliding.addActionListener(this);
		JRadioButtonMenuItem itemFastSliding=new JRadioButtonMenuItem(fastSliding);
		menuOptions.add(itemFastSliding);
		slidingGroup.add(itemFastSliding);
		itemFastSliding.addActionListener(this);
		itemFastSliding.setSelected(true);
		JRadioButtonMenuItem itemNoSliding=new JRadioButtonMenuItem(noSliding);
		menuOptions.add(itemNoSliding);
		slidingGroup.add(itemNoSliding);
		itemNoSliding.addActionListener(this);
		menuOptions.addSeparator();
		itemRandomMoves=new JCheckBoxMenuItem(randomMoves);
		itemRandomMoves.setAccelerator(KeyStroke.getKeyStroke('m'));
		menuOptions.add(itemRandomMoves);
		itemRandomMoves.setSelected(true);
		itemRandomMoves.addActionListener(this);
		menuOptions.addSeparator();
		JCheckBoxMenuItem itemTexturedSquare=new JCheckBoxMenuItem(texturedSquares);
		itemTexturedSquare.setAccelerator(KeyStroke.getKeyStroke('s'));
		menuOptions.add(itemTexturedSquare);
		itemTexturedSquare.addActionListener(this);
		itemTexturedSquare.setSelected(true); // by default we have textured squares
		
		menuOptions.addSeparator();
		itemPlaySound=new JCheckBoxMenuItem(playSound);
		itemPlaySound.setAccelerator(KeyStroke.getKeyStroke('p'));
		itemPlaySound.setSelected(true); // yes sound
		itemPlaySound.setSelected(false); // yes sound
		menuOptions.add(itemPlaySound);
		itemPlaySound.addActionListener(this);
		menuBar.add(menuOptions);
		menuOptions.addSeparator();
		JMenuItem itemSwitchSides=new JMenuItem(turnChessBoard);
		itemSwitchSides.setAccelerator(KeyStroke.getKeyStroke('t'));
		menuOptions.add(itemSwitchSides);
		itemSwitchSides.addActionListener(this);
		
		// now a create the menu for the computer level
		JMenu menuComputerConfiguration=new JMenu(computerLevel);
		
		// add black computer levels
		ButtonGroup groupBlack=new ButtonGroup();
		arrayListBlackLevel=new ArrayList<JRadioButtonMenuItem>();
		for(int counterLevel=1;counterLevel<=maximumDepth;counterLevel++)
		{
			String blackPlayerLevelCounter=blackPlayerLevel+" "+counterLevel;
			JRadioButtonMenuItem menuItemBlackPlayerLevel=new JRadioButtonMenuItem(blackPlayerLevelCounter);
			if(counterLevel==defaultDepth)
				menuItemBlackPlayerLevel.setSelected(true);
			arrayListBlackLevel.add(menuItemBlackPlayerLevel);
			menuItemBlackPlayerLevel.addActionListener(this);
			groupBlack.add(menuItemBlackPlayerLevel);
			menuComputerConfiguration.add(menuItemBlackPlayerLevel);
		}
		
		// put a separator between black level and white level
		menuComputerConfiguration.addSeparator();
		
		// add white computer levels
		ButtonGroup groupWhite=new ButtonGroup();
		arrayListWhiteLevel=new ArrayList<JRadioButtonMenuItem>();
		for(int counterLevel=1;counterLevel<=maximumDepth;counterLevel++)
		{
			String whitePlayerLevelCounter=whitePlayerLevel+" "+counterLevel;
			JRadioButtonMenuItem menuItemWhitePlayerLevel=new JRadioButtonMenuItem(whitePlayerLevelCounter);
			if(counterLevel==defaultDepth)
				menuItemWhitePlayerLevel.setSelected(true);
			arrayListWhiteLevel.add(menuItemWhitePlayerLevel);
			menuItemWhitePlayerLevel.addActionListener(this);
			groupWhite.add(menuItemWhitePlayerLevel);
			menuComputerConfiguration.add(menuItemWhitePlayerLevel);
		}
		menuComputerConfiguration.addActionListener(this);
		menuBar.add(menuComputerConfiguration);
		JMenu menuHelp=new JMenu(help);
		JMenuItem itemHowToPlay=new JMenuItem(howToPlay);
		itemHowToPlay.addActionListener(this);
		menuHelp.add(itemHowToPlay);
		JMenuItem itemTips=new JMenuItem(tips);
		itemTips.addActionListener(this);
		menuHelp.add(itemTips);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);
		
		// create and add boxes for graphics
		mainHorizontalBox=Box.createHorizontalBox();
		mainVerticalBox=Box.createVerticalBox();
		setVisible(true);
		chessBoardWithCoordinates=new ChessBoardWithCoordinates(getGraphics(),piecesMatrix);
		Dimension chessBoardDimension=chessBoardWithCoordinates.GetDimension();
		mainHorizontalBox.add(Box.createRigidArea(new Dimension(spaceAtLeftOfTheChessboard,chessBoardDimension.height)));
		mainHorizontalBox.add(chessBoardWithCoordinates);
		mainHorizontalBox.add(Box.createRigidArea(new Dimension(spaceAtRightOfTheChessboard,chessBoardDimension.height)));
		mainVerticalBox.add(Box.createRigidArea(new Dimension(chessBoardDimension.width,spaceAtTopOfTheChessboard)));
		mainVerticalBox.add(mainHorizontalBox);
		mainVerticalBox.add(Box.createRigidArea(new Dimension(chessBoardDimension.width,spaceAtBottomOfTheChessboard)));
		getContentPane().add(mainVerticalBox);
		setResizable(false);
		informationPanel=new InformationPanel(chessBoardDimension.height,getGraphics());
		mainHorizontalBox.add(informationPanel); // we add the information panel
		mainHorizontalBox.add(Box.createRigidArea(new Dimension(spaceAtRightOfTheInformationPanel,chessBoardDimension.height)));
		
		// set right dimensions
		pack();
		Dimension dimensionThatFit=new Dimension();
		dimensionThatFit.height=chessBoardDimension.height+menuBar.getHeight()+getInsets().top+getInsets().bottom+spaceAtTopOfTheChessboard+spaceAtBottomOfTheChessboard;
		dimensionThatFit.width=chessBoardDimension.width+getInsets().left+getInsets().right+spaceAtLeftOfTheChessboard+spaceAtRightOfTheChessboard+spaceAtRightOfTheInformationPanel+informationPanel.getWidth();
		setSize(dimensionThatFit);
		setLocationRelativeTo(getParent());
		chessBoardMouseListener=new ChessBoardMouseListener(this);
		addMouseListener(chessBoardMouseListener);
		informationPanel.SetPlayerTurn("White turn");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(applicationName);
		setVisible(true);
	}
	
	// a click has been done on the board, it has to be analyzed to know what happened
	public void onMousePressedOnTheChessBoard(MouseEvent mouseEvent) throws InterruptedException
	{
		// we get the right point for the chessboard with coordinates
		Point pointThatFitWithTheChessBoardWithCoordinates=new Point(mouseEvent.getPoint().x-spaceAtLeftOfTheChessboard-getInsets().left,mouseEvent.getPoint().y-spaceAtTopOfTheChessboard-menuBar.getHeight()-getInsets().top);
		Point newSeletectedSquare=chessBoardWithCoordinates.GetCorrespondingSquare(pointThatFitWithTheChessBoardWithCoordinates);
		if(newSeletectedSquare==null)
			return;
		
		// we check if the current square is a piece the play can move
		if(chessBoardWithCoordinates.GiveMeThePieceColorOnThisSquare(newSeletectedSquare)==chessRuler.GetCurrentTurn())
		{
			// we paint the old square, if it has been selected, it is repaint
			if(oldSelectedSquare.x>=0)
			{
				ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*SQUARES_NUMBER_PER_LINE,isLastMoveEnableEnPassant);
				chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves); // repaint target if a new piece has been selected
				chessBoardWithCoordinates.DrawASquare(oldSelectedSquare,mainVerticalBox.getGraphics());
			}
			
			// the old square is the same that the new, we have to unselect it and possible moves
			if(oldSelectedSquare.x==newSeletectedSquare.x&&oldSelectedSquare.y==newSeletectedSquare.y)
			{
				chessBoardWithCoordinates.DrawASquare(newSeletectedSquare,mainVerticalBox.getGraphics());
				oldSelectedSquare=new Point(-1,-1);
				return;
			}
			
			chessBoardWithCoordinates.DrawASquare(newSeletectedSquare,Color.green,mainVerticalBox.getGraphics());
			oldSelectedSquare=new Point(newSeletectedSquare.x,newSeletectedSquare.y);
			
			ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*SQUARES_NUMBER_PER_LINE,isLastMoveEnableEnPassant);
			chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves,Color.blue);
			
		}
		else
		{
			// maybe a square target has been selected
			if(oldSelectedSquare.x>=0)
			{
				// we invert the old and new square in order to make the good move
				if(chessRuler.isThisMovePossible(oldSelectedSquare,newSeletectedSquare,isLastMoveEnableEnPassant)==true)
				{
					ArrayList<String> arrayMoveDescription=new ArrayList<String>();
					// we erase the old possible moves
					ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*SQUARES_NUMBER_PER_LINE,isLastMoveEnableEnPassant);
					chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves);
					
					// we make the move and update move description
					if(indexMoves!=-1)
						chessRuler.counterMoveFinished=indexMoves;
					informationPanel.DeleteHistoricUntil(indexMoves);
					indexMoves=-1;
					informationPanel.UndrawLines();
					
					boolean[] arrayIsSpecial=new boolean[1];
					chessBoardWithCoordinates.makeMove(oldSelectedSquare,newSeletectedSquare);
					if(itemPlaySound.isSelected()==true)
						musician.playSoundMove();
					int eventuallyCastlingOrEnPassant=chessRuler.makeThisMoveAndGetDescription(oldSelectedSquare,newSeletectedSquare,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
					
					Point sourceCastling=null;
					Point destinationCastling=null;
					switch(eventuallyCastlingOrEnPassant)
					{
					case ChessRuler.whiteRookKingCastlingDestination:
						sourceCastling=new Point(ChessRuler.rightWhiteRookInitialPosition);
						destinationCastling=new Point(ChessRuler.whiteRookKingCastlingDestination%SQUARES_NUMBER_PER_LINE,ChessRuler.whiteRookKingCastlingDestination/SQUARES_NUMBER_PER_LINE);
						break;
					case ChessRuler.whiteRookQueenCastlingDestination:
						sourceCastling=new Point(ChessRuler.leftWhiteRookInitialPosition);
						destinationCastling=new Point(ChessRuler.whiteRookQueenCastlingDestination%SQUARES_NUMBER_PER_LINE,ChessRuler.whiteRookQueenCastlingDestination/SQUARES_NUMBER_PER_LINE);
						break;
					case ChessRuler.blackRookKingCastlingDestination:
						sourceCastling=new Point(ChessRuler.rightBlackRookInitialPosition);
						destinationCastling=new Point(ChessRuler.blackRookKingCastlingDestination%SQUARES_NUMBER_PER_LINE,ChessRuler.blackRookKingCastlingDestination/SQUARES_NUMBER_PER_LINE);
						break;
					case ChessRuler.blackRookQueenCastlingDestination:
						sourceCastling=new Point(ChessRuler.leftBlackRookInitialPosition);
						destinationCastling=new Point(ChessRuler.blackRookQueenCastlingDestination%SQUARES_NUMBER_PER_LINE,ChessRuler.blackRookQueenCastlingDestination/SQUARES_NUMBER_PER_LINE);
						break;
					default:
						// it means that is a en passant move refresh the right square
						if(eventuallyCastlingOrEnPassant!=0)
						{
							transformBitSetsIntoReadableMatrix();
							Point pointForEnPassant=new Point(newSeletectedSquare.x,oldSelectedSquare.y);
							chessBoardWithCoordinates.DrawASquare(pointForEnPassant,mainVerticalBox.getGraphics());
						}
					}
					isLastMoveEnableEnPassant=chessRuler.isItDoublePawnMoveForEnPassant(oldSelectedSquare,newSeletectedSquare);
					if(sourceCastling!=null)
					{
						chessBoardWithCoordinates.makeMove(sourceCastling,destinationCastling);
						if(itemPlaySound.isSelected()==true)
							musician.playSoundMove();
					}
					
					// paint the target for pawn promotion
					transformBitSetsIntoReadableMatrix();
					chessBoardWithCoordinates.DrawASquare(newSeletectedSquare,mainVerticalBox.getGraphics());
					
					oldSelectedSquare=new Point(-1,-1);
					goToNextTurn(arrayMoveDescription,arrayIsSpecial[0],isLastMoveEnableEnPassant);
					
					// check if the game is over
					if(finishTheGameIfItsTheCase()==true)
						return;
					
					// play computer if necessary
					if((chessRuler.GetCurrentTurn()==black&&itemComputerPlaysBlack.isSelected()==true)||(chessRuler.GetCurrentTurn()==white&&itemComputerPlaysWhite.isSelected()==true))
					{
						playComputer();
						// check if the game is over
						if(finishTheGameIfItsTheCase()==true)
							return;
					}
				}
			}
		}
	}
	
	// get the white computer level into the menu
	public int getWhiteLevel()
	{
		Iterator<JRadioButtonMenuItem> iteratorWhiteLevel=arrayListWhiteLevel.iterator();
		while(iteratorWhiteLevel.hasNext())
		{
			JRadioButtonMenuItem currentRadioButtonMenuItem=iteratorWhiteLevel.next();
			if(currentRadioButtonMenuItem.isSelected()==true)
			{
				String stringLevel=currentRadioButtonMenuItem.getText().substring(currentRadioButtonMenuItem.getText().length()-1,currentRadioButtonMenuItem.getText().length());
				return Integer.parseInt(stringLevel);
			}
		}
		System.out.println("Error while getting white level, no level selected");
		return -1;
	}
	
	// get the black computer level into the menu
	public int getBlackLevel()
	{
		Iterator<JRadioButtonMenuItem> iteratorBlackLevel=arrayListBlackLevel.iterator();
		while(iteratorBlackLevel.hasNext())
		{
			JRadioButtonMenuItem currentRadioButtonMenuItem=iteratorBlackLevel.next();
			if(currentRadioButtonMenuItem.isSelected()==true)
			{
				String stringLevel=currentRadioButtonMenuItem.getText().substring(currentRadioButtonMenuItem.getText().length()-1,currentRadioButtonMenuItem.getText().length());
				return Integer.parseInt(stringLevel);
			}
		}
		System.out.println("Error while getting black level, no level selected");
		return -1;
	}
	
	public void playComputer() throws InterruptedException
	{
		ArrayList<Point> listPointSource=new ArrayList<Point>();
		ArrayList<Point> listPointDestination=new ArrayList<Point>();
		ArrayList<String> listMoveDescription=new ArrayList<String>();
		boolean[] arrayIsSpecial=new boolean[1];
		int indexOfComputerMove=-1;
		if(chessRuler.GetCurrentTurn()==black&&itemComputerPlaysBlack.isSelected()==true)
			indexOfComputerMove=chessRuler.playComputer(getBlackLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial,isLastMoveEnableEnPassant);
		if(chessRuler.GetCurrentTurn()==white&&itemComputerPlaysWhite.isSelected()==true)
			indexOfComputerMove=chessRuler.playComputer(getWhiteLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial,isLastMoveEnableEnPassant);
		
		isLastMoveEnableEnPassant=chessRuler.isItDoublePawnMoveForEnPassant(listPointSource.get(indexOfComputerMove),listPointDestination.get(indexOfComputerMove));
		chessBoardWithCoordinates.makeMove(listPointSource.get(indexOfComputerMove),listPointDestination.get(indexOfComputerMove));
		if(itemPlaySound.isSelected()==true)
			musician.playSoundMove();
		transformBitSetsIntoReadableMatrix();
		goToNextTurn(listMoveDescription,arrayIsSpecial[0],isLastMoveEnableEnPassant);
		chessBoardWithCoordinates.DrawASquare(listPointSource.get(indexOfComputerMove),mainVerticalBox.getGraphics());
		chessBoardWithCoordinates.DrawASquare(listPointDestination.get(indexOfComputerMove),mainVerticalBox.getGraphics());
	}
	
	public void displayMessage(final String message)
	{
		JOptionPane optionPane=new JOptionPane(message);
		JDialog dialog=optionPane.createDialog(this,applicationName);
		dialog.setModal(false);
		dialog.setVisible(true);
	}
	
	private boolean finishTheGameIfItsTheCase()
	{
		int winner=chessRuler.IfGameHasEndedGiveMeTheWinner(isLastMoveEnableEnPassant);
		if(winner!=0)
		{
			if(itemPlaySound.isSelected()==true)
				musician.playSoundVictory();
			switch(winner)
			{
			case blackIsPat:
				informationPanel.SetPlayerTurn("Game is drawn");
				displayMessage("Black player is pat! Game is drawn.");
				break;
			case whiteIsPat:
				informationPanel.SetPlayerTurn("Game is drawn");
				displayMessage("White player is pat! Game is drawn.");
				break;
			case white:
				informationPanel.SetPlayerTurn("White is the winner");
				displayMessage("White player wins!");
				break;
			case black:
				informationPanel.SetPlayerTurn("Black is the winner");
				displayMessage("Black player wins!");
				break;
			default:
				;
			}
			chessRuler.EndTheGame();
			return true;
		}
		return false;
	}
	
	public void goToNextTurn(ArrayList<String> arrayListMovesDescriptions,Boolean isSpecialMove,int enPassantIndex)
	{
		chessRuler.changePlayerTurn();
		if(chessRuler.GetCurrentTurn()==white)
			informationPanel.SetPlayerTurn("White turn");
		if(chessRuler.GetCurrentTurn()==black)
			informationPanel.SetPlayerTurn("Black turn");
		informationPanel.addNewMoveDescription(arrayListMovesDescriptions.get(0),arrayListMovesDescriptions.get(1),isSpecialMove,enPassantIndex);
	}
	
	// useful when an action has been done and selection doesn't have anymore sense
	public void unselectSquareIfSelected()
	{
		if(oldSelectedSquare.x>=0)
		{
			ArrayList<Point> arrayListPossibleMoves=chessRuler.getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(oldSelectedSquare.x+oldSelectedSquare.y*SQUARES_NUMBER_PER_LINE,isLastMoveEnableEnPassant);
			chessBoardWithCoordinates.DrawASquare(oldSelectedSquare,mainVerticalBox.getGraphics());
			chessBoardWithCoordinates.drawSeveralSquares(arrayListPossibleMoves);
			oldSelectedSquare=new Point(-1,1);
		}
	}
	
	private void playComputerVsComputerGame() throws InterruptedException
	{
		paint(getGraphics());
		/*
		 * long beginingTimeForComputerVsComputerGame=System.currentTimeMillis(); long totalMovesForTheEntireGame=0; long incrementTime=0;
		 */
		for(int counterMoves=0;counterMoves<100000;counterMoves++)
		{
			ArrayList<Point> listPointSource=new ArrayList<Point>();
			ArrayList<Point> listPointDestination=new ArrayList<Point>();
			ArrayList<String> listMoveDescription=new ArrayList<String>();
			boolean[] arrayIsSpecial=new boolean[1];
			// long
			// beginingTimeForComputerVsComputerGameOnePly=System.currentTimeMillis();
			int indexChosenMove=-1;
			if(chessRuler.GetCurrentTurn()==black&&itemComputerPlaysBlack.isSelected()==true)
			{
				indexChosenMove=chessRuler.playComputer(getBlackLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial,isLastMoveEnableEnPassant);
				chessBoardWithCoordinates.makeMove(listPointSource.get(indexChosenMove),listPointDestination.get(indexChosenMove));
				if(itemPlaySound.isSelected()==true)
					musician.playSoundMove();
			}
			if(chessRuler.GetCurrentTurn()==white&&itemComputerPlaysWhite.isSelected()==true)
			{
				indexChosenMove=chessRuler.playComputer(getWhiteLevel(),listMoveDescription,listPointSource,listPointDestination,arrayIsSpecial,isLastMoveEnableEnPassant);
				chessBoardWithCoordinates.makeMove(listPointSource.get(indexChosenMove),listPointDestination.get(indexChosenMove));
				if(itemPlaySound.isSelected()==true)
					musician.playSoundMove();
			}
			/*
			 * long endTimeForComputerVsComputerGameOnePly=System.currentTimeMillis (); long timeForOneComputerPly=endTimeForComputerVsComputerGameOnePly -beginingTimeForComputerVsComputerGameOnePly;
			 * 
			 * incrementTime+=timeForOneComputerPly; totalMovesForTheEntireGame+=chessRuler.totalNodesCounter; // if(chessRuler.counterMoveFinished%5==0) System.out.println(chessRuler .counterMoveFinished+" Total time : " +incrementTime/(double)1000+" noeuds : "+ totalMovesForTheEntireGame +" vitesse : "+(long)((double)totalMovesForTheEntireGame /(incrementTime/(double)1000))+" noeuds/sec "+ (double)(((double)incrementTime /(double)1000)/(double)chessRuler.counterMoveFinished )+" seconds/move");
			 */
			
			transformBitSetsIntoReadableMatrix();
			chessBoardWithCoordinates.DrawASquare(listPointSource.get(indexChosenMove),mainVerticalBox.getGraphics());
			chessBoardWithCoordinates.DrawASquare(listPointDestination.get(indexChosenMove),mainVerticalBox.getGraphics());
			goToNextTurn(listMoveDescription,arrayIsSpecial[0],isLastMoveEnableEnPassant);
			if(chessRuler.IfGameHasEndedGiveMeTheWinner(isLastMoveEnableEnPassant)!=0)
			{
				/*
				 * long endTimeForComputerVsComputerGame=System.currentTimeMillis(); long totalTimeForComputerVsComputerGame= endTimeForComputerVsComputerGame -beginingTimeForComputerVsComputerGame; System.out.println("Total time : " +totalTimeForComputerVsComputerGame+" total evaluations : "+ totalMovesForTheEntireGame +" ratio : "+(long)((double)totalMovesForTheEntireGame /(totalTimeForComputerVsComputerGame /(double)1000))+" moves by second "+ ((double)totalTimeForComputerVsComputerGame /(double)1000)/(double )chessRuler.counterMoveFinished+" seconds by moves " +chessRuler.counterMoveFinished+" moves");
				 */
				
				finishTheGameIfItsTheCase();
				break;
			}
		}
	}
	
	public String getNextWord(String stringParameter,int wordCounterParameter)
	{
		int indexBeginingWord=0;
		int wordCounter=0;
		for(int charCounter=1;charCounter<stringParameter.length();charCounter++)
		{
			if((stringParameter.charAt(charCounter)!=' '&&stringParameter.charAt(charCounter)!='\n')&&stringParameter.charAt(charCounter-1)==' '||stringParameter.charAt(charCounter-1)=='\n')
				indexBeginingWord=charCounter;
			
			if((stringParameter.charAt(charCounter)==' '||stringParameter.charAt(charCounter)=='\n')&&stringParameter.charAt(charCounter-1)!=' '&&stringParameter.charAt(charCounter-1)!='\n')
			{
				if(wordCounter==wordCounterParameter)
					return stringParameter.substring(indexBeginingWord,charCounter);
				wordCounter++;
			}
		}
		if(wordCounter==wordCounterParameter)
			return stringParameter.substring(indexBeginingWord,stringParameter.length());
		return "";
	}
	
	@Override
	public void actionPerformed(ActionEvent actionEvent)
	{
		if(actionEvent.getActionCommand().equals(randomMoves))
		{
			if(itemRandomMoves.isSelected()==true)
				chessRuler.setRandomMoves();
			if(itemRandomMoves.isSelected()==false)
				chessRuler.setDeterministicMoves();
		}
		
		// we initialize a new game
		if(actionEvent.getActionCommand().equals(newGame))
		{
			indexMoves=-1;
			itemComputerPlaysBlack.setSelected(false);
			itemComputerPlaysWhite.setSelected(false);
			chessRuler.initializeNewGame();
			transformBitSetsIntoReadableMatrix();
			informationPanel.ClearList();
			informationPanel.SetPlayerTurn("White turn");
			paint(getGraphics());
			isLastMoveEnableEnPassant=-1;
		}
		
		if(actionEvent.getActionCommand().equals(tips))
		{
			JFrame tipsFrame=new JFrame();
			tipsFrame.setTitle(tips);
			Container tipsContainer=tipsFrame.getContentPane();
			tipsFrame.setLayout(new BorderLayout());
			JLabel compte=new JLabel("<html><br/> - If you are playing against the computer and if you cancel a move, the computer will stop playing. You have to manually reset the computer player.<br/><br/>"+
			" - Random moves : sometimes the computer has to choice between severals moves, use this option to randomly choose among these choices.<br/><br/>"+
					" - Shortcuts : use 'c' to cancel a move, 'r' to replay a move, 'p' to play sound each move, 't' to turn the chessboard, 's' to set or unset textures on squares and 'r' to switch on/off random moves.<br/><br/>"
					+ "- To play computer versus computer you need to check the two checkboxes in the game menu."
					+ "</html>");
			tipsContainer.add(compte,BorderLayout.NORTH);
			tipsFrame.setSize(tipsFrameWidth,tipsFrameHeight);
			tipsFrame.setLocationRelativeTo(null);
			tipsFrame.setVisible(true);
			Border raisedBorder=BorderFactory.createEmptyBorder(tipsBorderSize,tipsBorderSize,tipsBorderSize,tipsBorderSize);
			((JComponent)(tipsFrame.getContentPane())).setBorder(raisedBorder);
		}
		
		if(actionEvent.getActionCommand().equals(howToPlay))
		{
			String url="http://en.wikibooks.org/wiki/Chess/Playing_The_Game";
			String os=System.getProperty("os.name").toLowerCase();
			Runtime rt=Runtime.getRuntime();
			try
			{
				if(os.indexOf("win")>=0)
					rt.exec("rundll32 url.dll,FileProtocolHandler "+url);
				else if(os.indexOf("mac")>=0)
					rt.exec("open "+url);
				else if(os.indexOf("nix")>=0||os.indexOf("nux")>=0)
				{
					String[] browsers=
					{"epiphany","firefox","mozilla","konqueror","netscape","opera","links","lynx"};
					
					StringBuffer cmd=new StringBuffer();
					for(int i=0;i<browsers.length;i++)
						cmd.append((i==0?"":" || ")+browsers[i]+" \""+url+"\" ");
					rt.exec(new String[]
					{"sh","-c",cmd.toString()});
				}
			}
			catch(Exception exception)
			{
				return;
			}
		}
		
		if(actionEvent.getActionCommand().equals(computerPlaysBlack))
		{
			if(itemComputerPlaysBlack.isSelected()==true)
			{
				if(indexMoves!=-1)
					chessRuler.SetCounterOfMoves(indexMoves);
				informationPanel.DeleteHistoricUntil(indexMoves);
				indexMoves=-1;
				informationPanel.UndrawLines();
				if(chessRuler.GetCurrentTurn()==noCurrentGame)
				{
					itemComputerPlaysBlack.setSelected(false);
					return;
				}
				if(itemComputerPlaysWhite.isSelected()==true)
					try
					{
						playComputerVsComputerGame();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
				else if(chessRuler.GetCurrentTurn()==black)
				{
					try
					{
						playComputer();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
					finishTheGameIfItsTheCase();
				}
			}
		}
		
		if(actionEvent.getActionCommand().equals(computerPlaysWhite))
		{
			if(itemComputerPlaysWhite.isSelected()==true)
			{
				if(indexMoves!=-1)
					chessRuler.SetCounterOfMoves(indexMoves);
				informationPanel.DeleteHistoricUntil(indexMoves);
				indexMoves=-1;
				informationPanel.UndrawLines();
				if(chessRuler.GetCurrentTurn()==noCurrentGame)
				{
					itemComputerPlaysWhite.setSelected(false);
					return;
				}
				if(itemComputerPlaysBlack.isSelected()==true)
					try
					{
						playComputerVsComputerGame();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
				else if(chessRuler.GetCurrentTurn()==white)
				{
					try
					{
						playComputer();
					}
					catch(InterruptedException exception)
					{
						exception.printStackTrace();
					}
					finishTheGameIfItsTheCase();
				}
			}
		}
		
		// we replay the move
		if(actionEvent.getActionCommand().equals(replayMove))
		{
			if(indexMoves==informationPanel.GetNumberOfMoves()||indexMoves==-1)
				return; // there is no remake to do so we leave
			unselectSquareIfSelected();
			indexMoves++;
			informationPanel.DrawLine(indexMoves);
			String sourceStringSquare=informationPanel.GetSourceSquare(indexMoves-1);
			if(sourceStringSquare==null) // castling management
			{
				String castling2=getNextWord(informationPanel.GetStringAt(indexMoves-1),1);
				String castling3=getNextWord(informationPanel.GetStringAt(indexMoves-1),2);
				ArrayList<Point> arrayConcernedSquares=chessRuler.makeCastling(castling2+" "+castling3);
				transformBitSetsIntoReadableMatrix();
				for(int counterSquares=0;counterSquares<arrayConcernedSquares.size();counterSquares++)
					chessBoardWithCoordinates.DrawASquare(arrayConcernedSquares.get(counterSquares),mainVerticalBox.getGraphics());
			}
			else
			{
				Point sourceSquare=chessRuler.GetCorrespondingSquare(sourceStringSquare);
				String destinationStringSquare=informationPanel.GetDestinationSquare(indexMoves-1);
				Point destinationSquare=chessRuler.GetCorrespondingSquare(destinationStringSquare);
				ArrayList<String> arrayMoveDescription=new ArrayList<String>();
				boolean[] arrayIsSpecial=new boolean[1];
				chessRuler.makeThisMoveAndGetDescription(sourceSquare,destinationSquare,arrayMoveDescription,arrayIsSpecial,informationPanel.GetEnPassantIndex(indexMoves-2));
				isLastMoveEnableEnPassant=informationPanel.GetEnPassantIndex(indexMoves-1);
				chessRuler.SetCounterOfMoves(indexMoves);
				
				// we paint the transformation
				transformBitSetsIntoReadableMatrix();
				chessBoardWithCoordinates.DrawASquare(sourceSquare,mainVerticalBox.getGraphics());
				chessBoardWithCoordinates.DrawASquare(destinationSquare,mainVerticalBox.getGraphics());
				
				if(informationPanel.GetEnPassantIndex(indexMoves-2)!=-1)
				{
					Point enPassantPoint=null;
					if(chessRuler.GetCurrentTurn()==white)
						enPassantPoint=new Point(informationPanel.GetEnPassantIndex(indexMoves-2)%SQUARES_NUMBER_PER_LINE,informationPanel.GetEnPassantIndex(indexMoves-2)/SQUARES_NUMBER_PER_LINE);
					else
						enPassantPoint=new Point(informationPanel.GetEnPassantIndex(indexMoves-2)%SQUARES_NUMBER_PER_LINE,informationPanel.GetEnPassantIndex(indexMoves-2)/SQUARES_NUMBER_PER_LINE);
					chessBoardWithCoordinates.DrawASquare(enPassantPoint,mainVerticalBox.getGraphics());
				}
			}
			unselectSquareIfSelected();
			chessRuler.changePlayerTurn();
			
			if(chessRuler.GetCurrentTurn()==white)
				informationPanel.SetPlayerTurn("White turn");
			if(chessRuler.GetCurrentTurn()==black)
				informationPanel.SetPlayerTurn("Black turn");
			
			finishTheGameIfItsTheCase(); // check if the game is over
		}
		
		// we save the game into a local file
		if(actionEvent.getActionCommand().equals(saveGame))
		{
			// we have to build the file name, before all we retrieve the current date
			Calendar currentCalendar=Calendar.getInstance();
			long beginingTime=currentCalendar.getTimeInMillis();
			Date currentDate=new Date(beginingTime);
			
			// now we retrieve the beginning date
			DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd kk-mm-ss");
			Date beginningDate=chessRuler.GetBeginningDate();
			String stringBeginningDate=dateFormat.format(beginningDate);
			GregorianCalendar difference=new GregorianCalendar();
			
			// we calculate the difference
			difference.setTimeInMillis(currentDate.getTime()-beginningDate.getTime());
			int year=difference.get(Calendar.YEAR)-1970;
			int month=difference.get(Calendar.MONTH);
			int day=difference.get(Calendar.DAY_OF_MONTH)-1;
			int hour=difference.get(Calendar.HOUR_OF_DAY)-1;
			int minute=difference.get(Calendar.MINUTE);
			int seconds=difference.get(Calendar.SECOND);
			
			// now we have all the items to create the entire file name
			String fileName="c:\\"+stringBeginningDate+" - ";
			if(year>0)
				fileName+=year+"-";
			if(month>0||year>0)
			{
				if(month<10)
					fileName+="0";
				fileName+=month+"-";
			}
			if(day>0||month>0||year>0)
			{
				if(day<10)
					fileName+="0";
				fileName+=day+" ";
			}
			if(hour>0||day>0||month>0||year>0)
			{
				if(hour<10)
					fileName+="0";
				fileName+=hour+"-";
			}
			if(minute<10)
				fileName+="0";
			fileName+=""+minute+"-";
			if(seconds<10)
				fileName+="0";
			fileName+=seconds+" - "+chessRuler.getCounterOfMoves()+".pgn"; // here we have the full file name
			
			// we put the good look and feel
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception exception)
			{
				exception.printStackTrace();
			}
			Locale.setDefault(java.util.Locale.ENGLISH);
			
			// we create the file chooser
			final JFileChooser fileChooser=new JFileChooser();
			fileChooser.setLocale(Locale.ENGLISH);
			fileChooser.updateUI();
			fileChooser.setSelectedFile(new File(fileName));
			fileChooser.setDialogTitle("Select directory to save current game");
			int returnOpenDialog=fileChooser.showOpenDialog(this);
			if(returnOpenDialog!=0)
				return;
			File file=fileChooser.getSelectedFile();
			String fileNameSelected=file.getPath();
			if(fileNameSelected==null)
				return;
			
			// we have to get all the standard moves descriptions and concatenate all of it
			ArrayList<String> listMovesDescription=informationPanel.GetStandardArrayMovesDescription();
			String concatenationMovesDescription="";
			int currentLineLength=0;
			for(int counterMoves=0;counterMoves<listMovesDescription.size();counterMoves++)
			{
				currentLineLength+=listMovesDescription.get(counterMoves).length();
				if(currentLineLength>maximumGameDescriptionLength)
				{
					concatenationMovesDescription+="\n";
					currentLineLength=listMovesDescription.get(counterMoves).length();
				}
				concatenationMovesDescription+=listMovesDescription.get(counterMoves);
				if(counterMoves<listMovesDescription.size()-1)
				{
					concatenationMovesDescription+=" ";
					currentLineLength++;
				}
			}
			
			// now we can write everything on the file
			try
			{
				FileWriter fileWriter=new FileWriter(fileNameSelected,true);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
				bufferedWriter.write("[Event \"Local game\"]\n");
				bufferedWriter.write("[Site \"?\"]\n");
				bufferedWriter.write("[Date \""+new SimpleDateFormat("yyyy").format(beginningDate)+"."+new SimpleDateFormat("MM").format(beginningDate)+"."+new SimpleDateFormat("dd").format(beginningDate)+"\"]\n");
				bufferedWriter.write("[Round \""+chessRuler.getCounterOfMoves()+"\"]\n");
				bufferedWriter.write("[White \"?\"]\n");
				bufferedWriter.write("[Black \"?\"]\n");
				bufferedWriter.write("[Result \"*\"]\n\n");
				
				// the game itself
				bufferedWriter.write(concatenationMovesDescription);
				bufferedWriter.flush();
				bufferedWriter.close();
			}
			catch(IOException exception)
			{
				exception.printStackTrace();
			}
		}
		
		// we load a game
		if(actionEvent.getActionCommand().equals(loadGame))
		{
			// we open the file dialog box to select the right file
			String fileName="";
			FileDialog fileDialog=new FileDialog(this);
			fileDialog.setTitle("Select directory to save current game");
			fileDialog.setMode(FileDialog.LOAD);
			fileDialog.setVisible(true);
			fileName=fileDialog.getDirectory()+fileDialog.getFile();
			if(fileName==""||fileName==null||fileDialog.getFile()==null)
				return;
			
			// we open the file chosen and read its entire content
			File file=new File(fileName);
			StringBuilder stringBuilder=null;
			Charset charset=Charset.defaultCharset();
			Reader reader=null;
			try
			{
				reader=new InputStreamReader(new FileInputStream(file),charset);
			}
			catch(FileNotFoundException fileNotFoundException)
			{
				fileNotFoundException.printStackTrace();
			}
			stringBuilder=new StringBuilder((int)file.length());
			char[] arrayChar=new char[(int)file.length()];
			int sizeRead=0;
			try
			{
				sizeRead=reader.read(arrayChar);
			}
			catch(IOException inputOutputException)
			{
				inputOutputException.printStackTrace();
			}
			stringBuilder.append(arrayChar,0,sizeRead);
			try
			{
				reader.close();
			}
			catch(IOException ioException)
			{
				ioException.printStackTrace();
			}
			
			// we reset the entire current game
			unselectSquareIfSelected();
			chessRuler.initializeNewGame();
			informationPanel.UndrawLines();
			indexMoves=-1;
			informationPanel.ClearList();
			informationPanel.SetPlayerTurn("White turn");
			informationPanel.UndrawLines();
			
			// now we have to analyze the content of the file
			String movesDescription=new String(arrayChar); // we get the entire content of the file into a string
			movesDescription=movesDescription.substring(movesDescription.indexOf("\n1."));
			movesDescription=movesDescription.replaceAll(ChessRuler.promotionStandard,"");
			movesDescription=movesDescription.replaceAll("\\+","");
			movesDescription=movesDescription.replaceAll(" "+chessRuler.enPassantStandard,chessRuler.enPassantReducedForAnalysis);
			movesDescription=movesDescription.replaceAll("x",""); // the fact piece are eaten doesn't bring any useful information
			movesDescription=movesDescription.replaceAll("\n"," "); // carriage return are more problem than other thing, we replace by space to because it has same meaning
			informationPanel.SetToExplicitNotation(indexMoves);
			
			// we look at all the words in the string
			boolean[] arrayIsSpecial=new boolean[1];
			for(int wordCounter=0;;wordCounter++)
			{
				String currentWord=getNextWord(movesDescription,wordCounter);
				if(currentWord.equals(""))
					break;
				if(currentWord.indexOf(".")==-1)
				{
					ArrayList<String> arrayMoveDescription=new ArrayList<String>();
					isLastMoveEnableEnPassant=chessRuler.makeThisMoveAndGetDescriptionFromAWord(currentWord,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
					goToNextTurn(arrayMoveDescription,arrayIsSpecial[0],isLastMoveEnableEnPassant);
				}
			}
			
			// loaded of the file is done, we can now update graphics
			paint(getGraphics());
		}
		
		// we leave the application
		if(actionEvent.getActionCommand().equals(quit))
		{
			System.exit(0);
		}
		
		if(actionEvent.getActionCommand().equals(standardNotation))
		{
			informationPanel.SetToStandardNotation();
			unselectSquareIfSelected();
		}
		
		if(actionEvent.getActionCommand().equals(explicitNotation))
		{
			informationPanel.SetToExplicitNotation(indexMoves);
			unselectSquareIfSelected();
		}
		
		// we cancel the move
		if(actionEvent.getActionCommand().equals(cancelMove))
		{
			itemComputerPlaysBlack.setSelected(false);
			itemComputerPlaysWhite.setSelected(false);
			if(chessRuler.GetCurrentTurn()==noCurrentGame)
				chessRuler.SetToLastTurnBeforeCheckAndMate(informationPanel.IsPairNumberOfMoves());
			unselectSquareIfSelected();
			if(indexMoves==0)
				return; // we have unmake everything so we leave
			if(indexMoves==-1)
				indexMoves=informationPanel.GetNumberOfMoves(); // this is the beginning of a new cycle
			indexMoves--;
			informationPanel.DrawLine(indexMoves);
			chessRuler.changePlayerTurn();
			if(chessRuler.GetCurrentTurn()==white)
				informationPanel.SetPlayerTurn("White turn");
			if(chessRuler.GetCurrentTurn()==black)
				informationPanel.SetPlayerTurn("Black turn");
			String sourceStringSquare=informationPanel.GetSourceSquare(indexMoves);
			if(sourceStringSquare==null)
			{
				String castling2=getNextWord(informationPanel.GetStringAt(indexMoves),1);
				String castling3=getNextWord(informationPanel.GetStringAt(indexMoves),2);
				ArrayList<Point> arrayConcernedSquares=chessRuler.UnmakeCastling(castling2+" "+castling3); // castling management, we get the right castling, queen or king side
				transformBitSetsIntoReadableMatrix();
				for(int counterSquares=0;counterSquares<arrayConcernedSquares.size();counterSquares++)
					chessBoardWithCoordinates.DrawASquare(arrayConcernedSquares.get(counterSquares),mainVerticalBox.getGraphics());
			}
			else
			{
				Point sourceSquare=chessRuler.GetCorrespondingSquare(sourceStringSquare);
				String destinationStringSquare=informationPanel.GetDestinationSquare(indexMoves);
				String pieceTypeEventuallyDeletedString=informationPanel.GetPieceTypeEventuallyDeleted(indexMoves);
				pieceTypeEventuallyDeletedString=getNextWord(pieceTypeEventuallyDeletedString,0); // for promotion
				int pieceTypeEventuallyDeleted=chessRuler.getPieceIdWithString(pieceTypeEventuallyDeletedString);
				Point destinationSquare=chessRuler.GetCorrespondingSquare(destinationStringSquare);
				chessRuler.UnmakeMove(sourceSquare,destinationSquare,pieceTypeEventuallyDeleted,informationPanel.IsThisMoveSpecial(indexMoves));
				isLastMoveEnableEnPassant=informationPanel.GetEnPassantIndex(indexMoves-1);
				
				// we paint the transformation
				transformBitSetsIntoReadableMatrix();
				chessBoardWithCoordinates.DrawASquare(sourceSquare,mainVerticalBox.getGraphics());
				chessBoardWithCoordinates.DrawASquare(destinationSquare,mainVerticalBox.getGraphics());
				if(informationPanel.GetStringAt(indexMoves).indexOf(chessRuler.enPassantExplicit)!=-1)
				{
					transformBitSetsIntoReadableMatrix();
					Point enPassantPoint=null;
					if(chessRuler.GetCurrentTurn()==white)
						enPassantPoint=new Point(destinationSquare.x,destinationSquare.y+1);
					else
						enPassantPoint=new Point(destinationSquare.x,destinationSquare.y-1);
					chessBoardWithCoordinates.DrawASquare(enPassantPoint,mainVerticalBox.getGraphics());
				}
			}
		}
		
		if(actionEvent.getActionCommand().equals(turnChessBoard))
		{
			chessBoardWithCoordinates.turnChessBoard();
			chessBoardWithCoordinates.paint(chessBoardWithCoordinates.getGraphics());
		}
		
		if(actionEvent.getActionCommand().equals(playSound))
		{
			chessBoardWithCoordinates.switchSoundOnOff();
		}
		
		// we turn the chessboard of 180 degrees
		if(actionEvent.getActionCommand().equals(texturedSquares))
		{
			chessBoardWithCoordinates.switchTexturedSquares();
			paint(getGraphics());
		}
		
		if(actionEvent.getActionCommand().equals(noSliding))
		{
			chessBoardWithCoordinates.switchToNoSliding();
		}
		if(actionEvent.getActionCommand().equals(fastSliding))
		{
			chessBoardWithCoordinates.switchToFastSliding();
			
		}
		if(actionEvent.getActionCommand().equals(slowSliding))
		{
			chessBoardWithCoordinates.switchToSlowSliding();
		}
	}
	
	public void invertSquare(Point squareToReverse)
	{
		squareToReverse.x=Math.abs(squareToReverse.x-SQUARES_NUMBER_PER_LINE+1);
		squareToReverse.y=Math.abs(squareToReverse.y-SQUARES_NUMBER_PER_LINE+1);
	}
	
	public void invertMultipleSquares(ArrayList<Point> arrayListSquaresToReverse)
	{
		for(int counterIndex=0;counterIndex<arrayListSquaresToReverse.size();counterIndex++)
			invertSquare(arrayListSquaresToReverse.get(counterIndex));
	}
	
	public void transformBitSetsIntoReadableMatrix()
	{
		// first of all we erase all the matrix in order to recreate a clean one
		for(int counterVertical=0;counterVertical<SQUARES_NUMBER_PER_LINE;counterVertical++)
			for(int counterHorizontal=0;counterHorizontal<SQUARES_NUMBER_PER_LINE;counterHorizontal++)
				piecesMatrix[counterVertical][counterHorizontal]=new String("");
			
		// now we have to fill the string matrix according to the chessboard orientation
		for(int counterBit=0;counterBit<SQUARES_NUMBER_PER_LINE*SQUARES_NUMBER_PER_LINE;counterBit++)
		{
			if((chessRuler.whiteRooks&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("wr");
			if((chessRuler.whiteBishops&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("wb");
			if((chessRuler.whiteQueens&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("wq");
			if((chessRuler.whiteKnights&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("wk");
			if((chessRuler.whitePawns&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("wp");
			if((chessRuler.whiteKing&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("wK");
			if((chessRuler.blackRooks&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("br");
			if((chessRuler.blackBishops&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("bb");
			if((chessRuler.blackQueens&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("bq");
			if((chessRuler.blackKnights&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("bk");
			if((chessRuler.blackPawns&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("bp");
			if((chessRuler.blackKing&(1L<<counterBit))!=0)
				piecesMatrix[counterBit/SQUARES_NUMBER_PER_LINE][counterBit%SQUARES_NUMBER_PER_LINE]=new String("bK");
		}
	}
	
	@Override
	public void paint(Graphics graphics)
	{
		menuBar.paint(menuBar.getGraphics());
		transformBitSetsIntoReadableMatrix();
		if(mainVerticalBox.getGraphics()!=null)
		{
			getContentPane().paint(mainVerticalBox.getGraphics());
			mainVerticalBox.paint(mainVerticalBox.getGraphics());
		}
		// this is when the user move the window outside the current screen, and keep the selection squares displayed
		if(oldSelectedSquare.x>=0)
			chessBoardWithCoordinates.DrawASquare(oldSelectedSquare,Color.green,mainVerticalBox.getGraphics());
	}
}
