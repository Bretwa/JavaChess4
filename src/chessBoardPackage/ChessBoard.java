package chessBoardPackage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class ChessBoard extends JComponent implements Runnable
{
	private static final long serialVersionUID=1L;
	private final int white=1;
	private final int black=-white;
	private final int rectangleSelectionWidth=3;
	boolean isTexturedSquares;
	boolean areWhiteAtBottom;
	boolean isSoundOn;
	String piecesMatrix[][];
	private final int squareSize=60;
	private final int numberOfSquarePerLine=8;
	
	// the images for each kind of piece
	public BufferedImage whiteRookImage;
	public BufferedImage whiteKnightImage;
	public BufferedImage whiteBishopImage;
	public BufferedImage whiteQueenImage;
	public BufferedImage whiteKingImage;
	public BufferedImage whitePawnImage;
	public BufferedImage whiteSquareImage;
	public BufferedImage blackRookImage;
	public BufferedImage blackKnightImage;
	public BufferedImage blackBishopImage;
	public BufferedImage blackQueenImage;
	public BufferedImage blackKingImage;
	public BufferedImage blackPawnImage;
	public BufferedImage blackSquareImage;
	
	// file image for each piece type
	public String whiteKnightImageFile="white_knight.png";
	public String whiteBishopImageFile="white_bishop.png";
	public String whiteRookImageFile="white_rook.png";
	public String whiteQueenImageFile="white_queen.png";
	public String whitePawnImageFile="white_pawn.png";
	public String whiteKingImageFile="white_king.png";
	public String whiteSquareImageFile="white_square.png";
	public String blackQueenImageFile="black_queen.png";
	public String blackKingImageFile="black_king.png";
	public String blackPawnImageFile="black_pawn.png";
	public String blackRookImageFile="black_rook.png";
	public String blackKnightImageFile="black_knight.png";
	public String blackSquareImageFile="black_square.png";
	public String blackBishopImageFile="black_bishop.png";
	
	private static int pixelsSpaceForNoSliding=0;
	private static int pixelsBySecondNoSliding=0;
	private static int pixelsSpaceForSlowSliding=1;
	private static int pixelsBySecondSlowSliding=300;
	private static int pixelsSpaceForFastSliding=1;
	private static int pixelsBySecondFastSliding=1500;
	private int pixelsBySecondForMove;
	private int pixelsSpaceForMove;
	Point sourcePointForMove=new Point();
	Point destinationPointForMove=new Point();
	Thread threadForMove;
	
	public void switchToNoSliding()
	{
		pixelsBySecondForMove=pixelsBySecondNoSliding;
		pixelsSpaceForMove=pixelsSpaceForNoSliding;
	}
	
	public void switchToSlowSliding()
	{
		pixelsBySecondForMove=pixelsBySecondSlowSliding;
		pixelsSpaceForMove=pixelsSpaceForSlowSliding;
	}
	
	public void switchToFastSliding()
	{
		pixelsBySecondForMove=pixelsBySecondFastSliding;
		pixelsSpaceForMove=pixelsSpaceForFastSliding;
	}
	
	public void switchTexturedSquares()
	{
		isTexturedSquares=!isTexturedSquares;
	}
	
	public void switchSoundOnOff()
	{
		isSoundOn=!isSoundOn;
	}
	
	public BufferedImage getImageAccordingToString(String pieceId)
	{
		switch(pieceId)
		{
		case "wr":
			return whiteRookImage;
		case "wk":
			return whiteKnightImage;
		case "wb":
			return whiteBishopImage;
		case "wq":
			return whiteQueenImage;
		case "wK":
			return whiteKingImage;
		case "wp":
			return whitePawnImage;
		case "br":
			return blackRookImage;
		case "bk":
			return blackKnightImage;
		case "bb":
			return blackBishopImage;
		case "bq":
			return blackQueenImage;
		case "bK":
			return blackKingImage;
		case "bp":
			return blackPawnImage;
		default:
			;
		}
		return null;
	}
	
	public int GiveMeThePieceColorOnThisSquare(Point pointCoordinates)
	{
		if(piecesMatrix[pointCoordinates.y][pointCoordinates.x].contains("w"))
			return white;
		if(piecesMatrix[pointCoordinates.y][pointCoordinates.x].contains("b"))
			return black;
		return 0;
	}
	
	// retrieve a point in ChessBoard coordinates with pixels coordinates as
	// input parameter
	public Point getCorrespondingSquare(Point pointCoordinates)
	{
		int horizontalSquareSelected=pointCoordinates.x/squareSize;
		int verticalSquareSelected=pointCoordinates.y/squareSize;
		if(horizontalSquareSelected<0||horizontalSquareSelected>=numberOfSquarePerLine||verticalSquareSelected<0||verticalSquareSelected>=numberOfSquarePerLine)
			return null;
		Point correspondingSquare=new Point(horizontalSquareSelected,verticalSquareSelected);
		if(areWhiteAtBottom==false)
			invertSquare(correspondingSquare);
		return correspondingSquare;
	}
	
	// allow to put an image under another one considering a transparent color
	public BufferedImage makeColorTransparent(BufferedImage imageParameter,Color color)
	{
		BufferedImage resultImage=new BufferedImage(imageParameter.getWidth(),imageParameter.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics=resultImage.createGraphics();
		graphics.setComposite(AlphaComposite.Src);
		graphics.drawImage(imageParameter,null,0,0);
		graphics.dispose();
		for(int counterVertical=0;counterVertical<resultImage.getHeight();counterVertical++)
			for(int counterHorizontal=0;counterHorizontal<resultImage.getWidth();counterHorizontal++)
				if(resultImage.getRGB(counterHorizontal,counterVertical)==color.getRGB())
					resultImage.setRGB(counterHorizontal,counterVertical,0x8F1C1C);
		return resultImage;
	}
	
	public void invertSquare(Point squareToReverse)
	{
		squareToReverse.x=Math.abs(squareToReverse.x-numberOfSquarePerLine+1);
		squareToReverse.y=Math.abs(squareToReverse.y-numberOfSquarePerLine+1);
	}
	
	public void drawASquare(Point insertionPointParameter,Graphics graphics)
	{
		Point insertionPoint=new Point();
		insertionPoint.x=insertionPointParameter.x;
		insertionPoint.y=insertionPointParameter.y;
		String imageString=piecesMatrix[insertionPoint.y][insertionPoint.x];
		if(areWhiteAtBottom==false)
			invertSquare(insertionPoint);
		
		// first of all we repaint the square itself
		if(isTexturedSquares==true)
		{
			if(insertionPoint.y%2==0)
			{
				if(insertionPoint.x%2==0)
					graphics.drawImage(whiteSquareImage,insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
				else
					graphics.drawImage(blackSquareImage,insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
			}
			else
			{
				if(insertionPoint.x%2==0)
					graphics.drawImage(blackSquareImage,insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
				else
					graphics.drawImage(whiteSquareImage,insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
			}
		}
		else
		{
			if(insertionPoint.y%2==0)
			{
				if(insertionPoint.x%2==0)
					graphics.setColor(Color.white);
				else
					graphics.setColor(Color.black);
			}
			else
			{
				if(insertionPoint.x%2==0)
					graphics.setColor(Color.black);
				else
					graphics.setColor(Color.white);
			}
			graphics.fillRect(insertionPoint.x*squareSize,insertionPoint.y*squareSize,squareSize-1,squareSize-1);
			graphics.drawRect(insertionPoint.x*squareSize,insertionPoint.y*squareSize,squareSize-1,squareSize-1);
		}
		if(getImageAccordingToString(imageString)!=null)
			graphics.drawImage(makeColorTransparent(getImageAccordingToString(imageString),Color.green),insertionPoint.x*squareSize,insertionPoint.y*squareSize,null);
	}
	
	public void setParameterForMove(Point pointSourceParameter,Point pointDestinationParameter,Thread threadParameter)
	{
		sourcePointForMove.x=pointSourceParameter.x;
		sourcePointForMove.y=pointSourceParameter.y;
		destinationPointForMove.x=pointDestinationParameter.x;
		destinationPointForMove.y=pointDestinationParameter.y;
		threadForMove=threadParameter;
	}
	
	// make move with pixel by seconds
	public void makeMove(Point sourcePoint,Point destinationPoint) throws InterruptedException
	{
		
	}
	
	// draw several squares in blue given in a list
	public void drawSeveralSquares(ArrayList<Point> possibleMoves)
	{
		for(int counterMoves=0;counterMoves<possibleMoves.size();counterMoves++)
			drawASquare(possibleMoves.get(counterMoves),getGraphics());
	}
	
	// draw several squares in blue given in a list
	public void drawSeveralSquares(ArrayList<Point> possibleMoves,Color colorParameter)
	{
		for(int counterMoves=0;counterMoves<possibleMoves.size();counterMoves++)
			drawRectableToASquare(possibleMoves.get(counterMoves),colorParameter,getGraphics());
	}
	
	// draw a square according to a specific color
	public void drawRectableToASquare(Point pointParameter2,Color colorParameter,Graphics graphics)
	{
		Point pointParameter=new Point(pointParameter2);
		if(areWhiteAtBottom==false)
			invertSquare(pointParameter);
		
		int HorizontalSquareSelected=pointParameter.x;
		int VerticalSquareSelected=pointParameter.y;
		graphics.setColor(colorParameter);
		for(int RectangleWidth=0;RectangleWidth<rectangleSelectionWidth;RectangleWidth++)
		{
			// left
			graphics.drawLine(HorizontalSquareSelected*squareSize+RectangleWidth,VerticalSquareSelected*squareSize,HorizontalSquareSelected*squareSize+RectangleWidth,VerticalSquareSelected*squareSize+squareSize-1);
			
			// top
			graphics.drawLine(HorizontalSquareSelected*squareSize,VerticalSquareSelected*squareSize+RectangleWidth,HorizontalSquareSelected*squareSize+squareSize-1,VerticalSquareSelected*squareSize+RectangleWidth);
			
			// right
			graphics.drawLine(HorizontalSquareSelected*squareSize+squareSize-RectangleWidth-1,VerticalSquareSelected*squareSize,HorizontalSquareSelected*squareSize+squareSize-RectangleWidth-1,VerticalSquareSelected*squareSize+squareSize-1);
			
			// bottom
			graphics.drawLine(HorizontalSquareSelected*squareSize,VerticalSquareSelected*squareSize+squareSize-RectangleWidth-1,HorizontalSquareSelected*squareSize+squareSize-1,VerticalSquareSelected*squareSize+squareSize-RectangleWidth-1);
		}
	}
	
	// repaint the whole chess board, each square actually
	@Override
	public void paintComponent(Graphics graphics)
	{
		for(int CounterVertical=0;CounterVertical<numberOfSquarePerLine;CounterVertical++)
			for(int CounterHorizontal=0;CounterHorizontal<numberOfSquarePerLine;CounterHorizontal++)
				drawASquare(new Point(CounterHorizontal,CounterVertical),graphics);
	}
	
	public ChessBoard(String piecesMatrixParameter[][])
	{
		pixelsBySecondForMove=pixelsBySecondFastSliding;
		pixelsSpaceForMove=pixelsSpaceForFastSliding;
		isTexturedSquares=true;
		areWhiteAtBottom=true;
		piecesMatrix=piecesMatrixParameter;
		setPreferredSize(getDimension());
		try
		{
			whiteRookImage=ImageIO.read(getClass().getResourceAsStream(whiteRookImageFile));
			whiteRookImage=makeColorTransparent(whiteRookImage,Color.green);
			whiteKnightImage=ImageIO.read(getClass().getResourceAsStream(whiteKnightImageFile));
			whiteBishopImage=ImageIO.read(getClass().getResourceAsStream(whiteBishopImageFile));
			whiteQueenImage=ImageIO.read(getClass().getResourceAsStream(whiteQueenImageFile));
			whiteKingImage=ImageIO.read(getClass().getResourceAsStream(whiteKingImageFile));
			whitePawnImage=ImageIO.read(getClass().getResourceAsStream(whitePawnImageFile));
			whiteSquareImage=ImageIO.read(getClass().getResourceAsStream(whiteSquareImageFile));
			blackRookImage=ImageIO.read(getClass().getResourceAsStream(blackRookImageFile));
			blackKnightImage=ImageIO.read(getClass().getResourceAsStream(blackKnightImageFile));
			blackBishopImage=ImageIO.read(getClass().getResourceAsStream(blackBishopImageFile));
			blackQueenImage=ImageIO.read(getClass().getResourceAsStream(blackQueenImageFile));
			blackKingImage=ImageIO.read(getClass().getResourceAsStream(blackKingImageFile));
			blackPawnImage=ImageIO.read(getClass().getResourceAsStream(blackPawnImageFile));
			blackSquareImage=ImageIO.read(getClass().getResourceAsStream(blackSquareImageFile));
		}
		catch(IOException imageException)
		{
			imageException.printStackTrace();
		}
	}
	
	public Dimension getDimension()
	{
		return new Dimension(numberOfSquarePerLine*squareSize,numberOfSquarePerLine*squareSize);
	}
	
	public void turnChessBoard()
	{
		areWhiteAtBottom=!areWhiteAtBottom;
	}
	
	@Override
	public void run()
	{
		/*
		 * we have two different ways to compute the time remaining for each pixel move - we can compute according to always recompute average : it's a problem when we have many big wait value, at the beginning we will have slow moves and after very fast - we can compute at the beginning the average time and at each slice and compute the delta according to the last moves
		 */
		pixelsSpaceForMove=Math.abs(pixelsSpaceForMove);
		BufferedImage imageSource=getImageAccordingToString(piecesMatrix[sourcePointForMove.y][sourcePointForMove.x]);
		String pieceType=piecesMatrix[sourcePointForMove.y][sourcePointForMove.x];
		piecesMatrix[sourcePointForMove.y][sourcePointForMove.x]="";
		Graphics graphics=getGraphics();
		
		// this is not sliding move
		if(pixelsSpaceForMove==0)
		{
			piecesMatrix[destinationPointForMove.y][destinationPointForMove.x]=pieceType;
			drawASquare(sourcePointForMove,graphics);
			drawASquare(destinationPointForMove,graphics);
			return;
		}
		
		Point sourcePointForMoveInPixels=new Point(sourcePointForMove.x*squareSize,sourcePointForMove.y*squareSize);
		Point destinationPointForMoveInPixels=new Point(destinationPointForMove.x*squareSize,destinationPointForMove.y*squareSize);
		double beginingTime=System.currentTimeMillis();
		double timeForThisMovement=Math.sqrt(Math.pow(destinationPointForMoveInPixels.y-sourcePointForMoveInPixels.y,2)+Math.pow(destinationPointForMoveInPixels.x-sourcePointForMoveInPixels.x,2))/pixelsBySecondForMove*1000;
		double verticalDelta=Math.abs(destinationPointForMoveInPixels.y-sourcePointForMoveInPixels.y);
		double horizontalDelta=Math.abs(destinationPointForMoveInPixels.x-sourcePointForMoveInPixels.x);
		double maxDelta=Math.max(verticalDelta,horizontalDelta);
		double minDelta=Math.min(verticalDelta,horizontalDelta);
		double littleRatio=minDelta/maxDelta*pixelsSpaceForMove;
		
		if(verticalDelta>=horizontalDelta)
		{
			if(sourcePointForMove.y>destinationPointForMove.y)
				pixelsSpaceForMove=-pixelsSpaceForMove;
			if(sourcePointForMove.x>destinationPointForMove.x)
				littleRatio=-littleRatio;
		}
		else
		{
			if(sourcePointForMove.x>destinationPointForMove.x)
				pixelsSpaceForMove=-pixelsSpaceForMove;
			if(sourcePointForMove.y>destinationPointForMove.y)
				littleRatio=-littleRatio;
		}
		int horizontalCounter=0;
		int verticalCounter=0;
		int counterForAvoidTooMuchRefresh=0;
		
		// we buckle with the main pixel counter to do the sliding move
		for(double mainPixelCounter=0,secondaryPixelCounter=0;Math.abs(mainPixelCounter)<=maxDelta;mainPixelCounter+=pixelsSpaceForMove,secondaryPixelCounter+=littleRatio)
		{
			if(verticalDelta>=horizontalDelta)
			{
				horizontalCounter=(int)Math.round(secondaryPixelCounter+sourcePointForMoveInPixels.x);
				verticalCounter=(int)Math.round(mainPixelCounter+sourcePointForMoveInPixels.y);
			}
			else
			{
				horizontalCounter=(int)Math.round(mainPixelCounter+sourcePointForMoveInPixels.x);
				verticalCounter=(int)Math.round(secondaryPixelCounter+sourcePointForMoveInPixels.y);
			}
			Point topLeft=new Point(horizontalCounter/squareSize,verticalCounter/squareSize);
			Point topRight=new Point((horizontalCounter+squareSize-1)/squareSize,verticalCounter/squareSize);
			Point bottomLeft=new Point(horizontalCounter/squareSize,(verticalCounter+squareSize-1)/squareSize);
			Point bottomRight=new Point((horizontalCounter+squareSize-1)/squareSize,(verticalCounter+squareSize-1)/squareSize);
			if(counterForAvoidTooMuchRefresh%3==0) // this is not useful to make a lot of refresh and it's avoid clipping
			{
				drawASquare(topLeft,graphics);
				if(topRight.x!=topLeft.x||topRight.y!=topLeft.y)
					drawASquare(topRight,graphics);
				drawASquare(bottomRight,graphics);
				if(bottomRight.x!=bottomLeft.x||bottomRight.y!=bottomLeft.y)
					drawASquare(bottomLeft,graphics);
			}
			counterForAvoidTooMuchRefresh++;
			if(areWhiteAtBottom==false)
			{
				horizontalCounter=Math.abs(horizontalCounter-squareSize*(numberOfSquarePerLine-1));
				verticalCounter=Math.abs(verticalCounter-squareSize*(numberOfSquarePerLine-1));
			}
			
			graphics.drawImage(makeColorTransparent(imageSource,Color.green),horizontalCounter,verticalCounter,null);
			int deltaTime=(int)(Math.round((Math.abs(mainPixelCounter)/maxDelta)*timeForThisMovement-(System.currentTimeMillis()-beginingTime)));
			if(deltaTime<0)
				deltaTime=0;
			try
			{
				Thread.sleep(deltaTime);
			}
			catch(InterruptedException sleepException)
			{
				sleepException.printStackTrace();
			}
		}
		piecesMatrix[destinationPointForMove.y][destinationPointForMove.x]=pieceType;
	}
}