package chessBoardPackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JPanel;

public class ChessBoardWithCoordinates extends JPanel
{
	private final int squareSize=60;
	private final int numberOfSquarePerLine=8;
	private final ChessBoard chessBoard;
	private final Box verticalBox;
	private final Box horizontalBox;
	private static final long serialVersionUID=1L;
	private final TopAndBottomCoordinates topCoordinates;
	private final TopAndBottomCoordinates bottomCoordinates;
	private final LeftAndRightCoordinates leftCoordinates;
	private final LeftAndRightCoordinates rightCoordinates;
	
	public void makeMove(Point pointSource,Point pointDestination) throws InterruptedException
	{
		Thread moveThread=new Thread(chessBoard);
		chessBoard.setParameterForMove(pointSource,pointDestination,moveThread);
		moveThread.setPriority(Thread.MAX_PRIORITY);
		moveThread.start();
		moveThread.join();
	}
	
	public void turnChessBoard()
	{
		topCoordinates.turn180Degrees();
		bottomCoordinates.turn180Degrees();
		leftCoordinates.turn180Degrees();
		rightCoordinates.turn180Degrees();
		chessBoard.turnChessBoard();
	}
	
	public void switchToNoSliding()
	{
		chessBoard.switchToNoSliding();
	}
	
	public void switchToSlowSliding()
	{
		chessBoard.switchToSlowSliding();
	}
	
	public void switchToFastSliding()
	{
		chessBoard.switchToFastSliding();
	}
	
	public void switchSoundOnOff()
	{
		chessBoard.switchSoundOnOff();
	}
	
	public void drawSeveralSquares(ArrayList<Point> possibleMoves)
	{
		chessBoard.drawSeveralSquares(possibleMoves);
	}
	
	public Point GetCorrespondingSquare(Point pointCoordinates)
	{
		return chessBoard.getCorrespondingSquare(new Point(pointCoordinates.x-leftCoordinates.getWidth(),pointCoordinates.y-topCoordinates.getHeight()));
	}
	
	public int GiveMeThePieceColorOnThisSquare(Point pointCoordinates)
	{
		return chessBoard.GiveMeThePieceColorOnThisSquare(pointCoordinates);
	}
	
	// paint a square, used to refresh
	public void DrawASquare(Point PointParameter,Graphics graphics)
	{
		chessBoard.drawASquare(PointParameter,chessBoard.getGraphics());
	}
	
	// constructor, put the four coordinates components
	public ChessBoardWithCoordinates(Graphics graphics,String piecesMatrixParameter[][])
	{
		setLayout(null);
		chessBoard=new ChessBoard(piecesMatrixParameter);
		bottomCoordinates=new TopAndBottomCoordinates(graphics);
		topCoordinates=new TopAndBottomCoordinates(graphics);
		leftCoordinates=new LeftAndRightCoordinates(graphics);
		rightCoordinates=new LeftAndRightCoordinates(graphics);
		horizontalBox=Box.createHorizontalBox();
		verticalBox=Box.createVerticalBox();
		verticalBox.add(topCoordinates);
		horizontalBox.add(leftCoordinates);
		horizontalBox.add(chessBoard);
		horizontalBox.add(rightCoordinates);
		verticalBox.add(horizontalBox);
		verticalBox.add(bottomCoordinates);
		verticalBox.setBounds(0,0,GetDimension().width,GetDimension().height);
		add(verticalBox);
		setPreferredSize(GetDimension());
		setMaximumSize(GetDimension());
	}
	
	// the dimension is calculated according to the coordinates sizes
	public Dimension GetDimension()
	{
		return new Dimension(numberOfSquarePerLine*squareSize+leftCoordinates.getWidth()+rightCoordinates.getWidth(),numberOfSquarePerLine*squareSize+topCoordinates.getHeight()+bottomCoordinates.getHeight());
	}
	
	public void switchTexturedSquares()
	{
		chessBoard.switchTexturedSquares();
	}
	
	public void drawSeveralSquares(ArrayList<Point> possibleMoves,Color colorParameter)
	{
		chessBoard.drawSeveralSquares(possibleMoves,colorParameter);
	}
	
	// draw a square according to a specific color
	public void DrawASquare(Point PointParameter,Color colorParameter,Graphics graphics)
	{
		chessBoard.drawRectableToASquare(PointParameter,colorParameter,chessBoard.getGraphics());
	}
	
}
