package chessApplicationPackage;

import java.awt.Point;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

public class ChessRuler extends Thread
{
	boolean randomMovements;
	int alphaTemp,betaTemp; // for optimization
	
	public int totalNodesCounter;
	private static final int maximumPossibleMoves=200;
	static ArrayList<Integer> listSourceForMultithreading=new ArrayList<Integer>();
	static ArrayList<Integer> listDestinationForMultithreading=new ArrayList<Integer>();
	static int listValuesForMultithreading[];
	private int counterNodes;
	private int currentTurnMultithreading;
	private int beginIndexMultithreading;
	private int endIndexMultithreading;
	private int depthForThreadComputing;
	
	// all the longs for each type of piece
	public long whiteRooks;
	public long whiteKnights;
	public long whiteBishops;
	public long whiteQueens;
	public long whiteKing;
	public long whitePawns;
	public long blackRooks;
	public long blackKnights;
	public long blackBishops;
	public long blackQueens;
	public long blackKing;
	public long blackPawns;
	private static final int NUMBER_OF_SQUARES_PER_LINE=8;
	int counterMoveFinished;
	private Date gameBeginningDate;
	private int currentTurn;
	private static final int white=1;
	private static final int black=-white;
	private static final int whiteIsPat=2*white;
	private static final int blackIsPat=2*black;
	private static final int noCurrentGame=0;
	private static final int noPieceId=0;
	private static final int pawnId=1000;
	private static final int knightId=2400;
	private static final int bishopId=4000;
	private static final int rookId=6400;
	private static final int queenId=104000;
	private static final int kingId=10000000;
	private static final int infinite=1000000000;
	private ArrayList<PiecesSituation> listPiecesSituation;
	private ArrayList<Integer> listPiecesSituationOccurrences;
	private static final int maximumOccurrenceForASituation=3;
	
	// array for moves storage
	private static int arrayTopLeft[];
	private static int arrayBottomLeft[];
	private static long arrayKingMoves[];
	private static long arrayKnightMoves[];
	private static long arrayMagicNumberForVerticalLines[];
	private static long arrayDiagonalBottomLeftTopRightMask[];
	private static long arrayDiagonalTopLeftBottomRightMask[];
	private static long matrixDiagonalBottomLeftTopRightMoveResult[][];
	private static long matrixDiagonalTopLeftBottomRightMoveResult[][];
	private static long arrayHorizontalLineMask[];
	private static long arrayVerticalLineMask[];
	private static long matrixLineHorizontalMoveResult[][];
	private static long matrixLineVerticalMoveResult[][];
	private static long magicForDiagonals;
	private static long magicDestinationMask;
	
	// pawn promotion
	public static final String promotionExplicit="promotion";
	public static final String promotionStandard="=Q";
	private static final String checkDescription="check";
	
	// en passant
	public String enPassantExplicit="en passant";
	public String enPassantStandard="e.p";
	public String enPassantReducedForAnalysis="ep";
	
	// for castlings
	public long whiteKingCastlingMask;
	public long whiteQueenCastlingMask;
	public long blackKingCastlingMask;
	public long blackQueenCastlingMask;
	public static final int beginBlackQueenCastling=1;
	public static final int endBlackQueenCastling=3;
	public static final int beginBlackKingCastling=5;
	public static final int endBlackKingCastling=6;
	public static final int beginWhiteQueenCastling=57;
	public static final int endWhiteQueenCastling=59;
	public static final int beginWhiteKingCastling=61;
	public static final int endWhiteKingCastling=62;
	public static final int blackKingQueenCastlingDestination=2;
	public static final int blackRookQueenCastlingDestination=3;
	public static final int blackKingKingCastlingDestination=6;
	public static final int blackRookKingCastlingDestination=5;
	public static final int whiteKingQueenCastlingDestination=58;
	public static final int whiteRookQueenCastlingDestination=59;
	public static final int whiteKingKingCastlingDestination=62;
	public static final int whiteRookKingCastlingDestination=61;
	public static final String kingSideCastlingStandard="0-0";
	public static final String kingSideCastlingExplicit="kingside castling";
	public static final String queenSideCastlingStandard="0-0-0";
	public static final String queenSideCastlingExplicit="queenside castling";
	public boolean isWhiteKingHasMoved;
	public boolean isBlackKingHasMoved;
	public boolean isBlackLeftRookHasMoved;
	public boolean isBlackRightRookHasMoved;
	public boolean isWhiteLeftRookHasMoved;
	public boolean isWhiteRightRookHasMoved;
	
	// there are all the coordinates for a standard game with the white at bottom
	public static final Point leftWhiteBishopInitialPosition=new Point(2,7);
	public static final Point rightWhiteBishopInitialPosition=new Point(5,7);
	public static final Point leftWhiteKnightInitialPosition=new Point(1,7);
	public static final Point rightWhiteKnightInitialPosition=new Point(6,7);
	public static final Point leftWhiteRookInitialPosition=new Point(0,7);
	public static final Point rightWhiteRookInitialPosition=new Point(7,7);
	public static final Point whiteQueenInitialPosition=new Point(3,7);
	public static final Point whiteKingInitialPosition=new Point(4,7);
	public static final Point firstLeftWhitePawnsInitialPosition=new Point(0,6);
	public static final Point leftBlackRookInitialPosition=new Point(0,0);
	public static final Point rightBlackRookInitialPosition=new Point(7,0);
	public static final Point leftBlackKnightInitialPosition=new Point(1,0);
	public static final Point rightBlackKnightInitialPosition=new Point(6,0);
	public static final Point leftBlackBishopInitialPosition=new Point(2,0);
	public static final Point rightBlackBishopInitialPosition=new Point(5,0);
	public static final Point blackQueenInitialPosition=new Point(3,0);
	public static final Point blackKingInitialPosition=new Point(4,0);
	public static final Point firstLeftBlackPawnsInitialPosition=new Point(0,1);
	
	public void displayLong(long longParameter)
	{
		System.out.println("-------------- longParameter : "+longParameter+" --------------");
		for(int counterVertical=0;counterVertical<NUMBER_OF_SQUARES_PER_LINE;counterVertical++)
		{
			for(int counterHorizontal=0;counterHorizontal<NUMBER_OF_SQUARES_PER_LINE;counterHorizontal++)
			{
				if((longParameter&(1L<<(counterVertical*NUMBER_OF_SQUARES_PER_LINE+counterHorizontal)))!=0)
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.println("");
		}
	}
	
	public void displayLong(long longParameter,String description)
	{
		System.out.println("-------------- longParameter : "+longParameter+" --------------  description : "+description+" --------------");
		for(int counterVertical=0;counterVertical<NUMBER_OF_SQUARES_PER_LINE;counterVertical++)
		{
			for(int counterHorizontal=0;counterHorizontal<NUMBER_OF_SQUARES_PER_LINE;counterHorizontal++)
			{
				if((longParameter&(1L<<(counterVertical*NUMBER_OF_SQUARES_PER_LINE+counterHorizontal)))!=0)
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.println("");
		}
	}
	
	public long getCurrentPieces()
	{
		if(currentTurn==white)
			return getWhitePieces();
		if(currentTurn==black)
			return getBlackPieces();
		return -1;
	}
	
	public long getWhitePieces()
	{
		long whitePieces=whitePawns;
		whitePieces|=whiteKnights;
		whitePieces|=whiteBishops;
		whitePieces|=whiteRooks;
		whitePieces|=whiteQueens;
		whitePieces|=whiteKing;
		return whitePieces;
	}
	
	public long getBlackPieces()
	{
		
		long blackPieces=blackPawns;
		blackPieces|=blackKnights;
		blackPieces|=blackBishops;
		blackPieces|=blackRooks;
		blackPieces|=blackQueens;
		blackPieces|=blackKing;
		return blackPieces;
	}
	
	public long getAllPieces()
	{
		return getBlackPieces()|getWhitePieces();
	}
	
	public int makeMoveWithTwoIndexForCurrentTurnWithPieceId(int pieceId,int sourceIndex,int destinationIndex,boolean[] isSpecial)
	{
		isSpecial[0]=false;
		switch(currentTurn)
		{
		case white:
			switch(pieceId)
			{
			case kingId:
				whiteKing&=~(1L<<sourceIndex);
				whiteKing|=1L<<destinationIndex;
				if(isWhiteKingHasMoved==false&&sourceIndex==whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isWhiteKingHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case pawnId:
				whitePawns&=~(1L<<sourceIndex);
				if(destinationIndex<NUMBER_OF_SQUARES_PER_LINE)
				{
					whiteQueens|=1L<<destinationIndex;
					isSpecial[0]=true;
				}
				else
					whitePawns|=1L<<destinationIndex;
				break;
			case knightId:
				whiteKnights&=~(1L<<sourceIndex);
				whiteKnights|=1L<<destinationIndex;
				break;
			case rookId:
				whiteRooks&=~(1L<<sourceIndex);
				whiteRooks|=1L<<destinationIndex;
				if(isWhiteRightRookHasMoved==false&&sourceIndex==rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isWhiteRightRookHasMoved=true;
					isSpecial[0]=true;
				}
				if(isWhiteLeftRookHasMoved==false&&sourceIndex==leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isWhiteLeftRookHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case bishopId:
				whiteBishops&=~(1L<<sourceIndex);
				whiteBishops|=1L<<destinationIndex;
				break;
			case queenId:
				whiteQueens&=~(1L<<sourceIndex);
				whiteQueens|=1L<<destinationIndex;
				break;
			default:
				;
			}
			if((blackKing&(1L<<destinationIndex))!=0)
			{
				blackKing&=~(1L<<destinationIndex);
				return kingId;
			}
			if((blackPawns&(1L<<destinationIndex))!=0)
			{
				blackPawns&=~(1L<<destinationIndex);
				return pawnId;
			}
			if((blackKnights&(1L<<destinationIndex))!=0)
			{
				blackKnights&=~(1L<<destinationIndex);
				return knightId;
			}
			if((blackBishops&(1L<<destinationIndex))!=0)
			{
				blackBishops&=~(1L<<destinationIndex);
				return bishopId;
			}
			if((blackRooks&(1L<<destinationIndex))!=0)
			{
				blackRooks&=~(1L<<destinationIndex);
				return rookId;
			}
			if((blackQueens&(1L<<destinationIndex))!=0)
			{
				blackQueens&=~(1L<<destinationIndex);
				return queenId;
			}
			break;
		case black:
			switch(pieceId)
			{
			case kingId:
				blackKing&=~(1L<<sourceIndex);
				blackKing|=1L<<destinationIndex;
				if(isBlackKingHasMoved==false&&sourceIndex==blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isBlackKingHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case pawnId:
				blackPawns&=~(1L<<sourceIndex);
				if(destinationIndex>=NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))
				{
					blackQueens|=1L<<destinationIndex;
					isSpecial[0]=true;
				}
				else
					blackPawns|=1L<<destinationIndex;
				break;
			case knightId:
				blackKnights&=~(1L<<sourceIndex);
				blackKnights|=1L<<destinationIndex;
				break;
			case rookId:
				blackRooks&=~(1L<<sourceIndex);
				blackRooks|=1L<<destinationIndex;
				if(isBlackRightRookHasMoved==false&&sourceIndex==rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isBlackRightRookHasMoved=true;
					isSpecial[0]=true;
				}
				if(isBlackLeftRookHasMoved==false&&sourceIndex==leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isBlackLeftRookHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case bishopId:
				blackBishops&=~(1L<<sourceIndex);
				blackBishops|=1L<<destinationIndex;
				break;
			case queenId:
				blackQueens&=~(1L<<sourceIndex);
				blackQueens|=1L<<destinationIndex;
				break;
			default:
				;
			}
			if((whiteKing&(1L<<destinationIndex))!=0)
			{
				whiteKing&=~(1L<<destinationIndex);
				return kingId;
			}
			if((whitePawns&(1L<<destinationIndex))!=0)
			{
				whitePawns&=~(1L<<destinationIndex);
				return pawnId;
			}
			if((whiteKnights&(1L<<destinationIndex))!=0)
			{
				whiteKnights&=~(1L<<destinationIndex);
				return knightId;
			}
			if((whiteBishops&(1L<<destinationIndex))!=0)
			{
				whiteBishops&=~(1L<<destinationIndex);
				return bishopId;
			}
			if((whiteRooks&(1L<<destinationIndex))!=0)
			{
				whiteRooks&=~(1L<<destinationIndex);
				return rookId;
			}
			if((whiteQueens&(1L<<destinationIndex))!=0)
			{
				whiteQueens&=~(1L<<destinationIndex);
				return queenId;
			}
			break;
		default:
			;
		}
		return noPieceId;
	}
	
	public int MakeLegalMoveWithTwoIndexForCurrentTurnWithPieceId(int pieceId,int sourceIndex,int destinationIndex,boolean[] isSpecial)
	{
		isSpecial[0]=false;
		switch(currentTurn)
		{
		case white:
			switch(pieceId)
			{
			case kingId:
				whiteKing&=~(1L<<sourceIndex);
				whiteKing|=1L<<destinationIndex;
				if(isWhiteKingHasMoved==false&&sourceIndex==whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isWhiteKingHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case pawnId:
				whitePawns&=~(1L<<sourceIndex);
				if(destinationIndex<NUMBER_OF_SQUARES_PER_LINE)
				{
					whiteQueens|=1L<<destinationIndex;
					isSpecial[0]=true;
				}
				else
					whitePawns|=1L<<destinationIndex;
				break;
			case knightId:
				whiteKnights&=~(1L<<sourceIndex);
				whiteKnights|=1L<<destinationIndex;
				break;
			case rookId:
				whiteRooks&=~(1L<<sourceIndex);
				whiteRooks|=1L<<destinationIndex;
				if(isWhiteRightRookHasMoved==false&&sourceIndex==rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isWhiteRightRookHasMoved=true;
					isSpecial[0]=true;
				}
				if(isWhiteLeftRookHasMoved==false&&sourceIndex==leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isWhiteLeftRookHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case bishopId:
				whiteBishops&=~(1L<<sourceIndex);
				whiteBishops|=1L<<destinationIndex;
				break;
			case queenId:
				whiteQueens&=~(1L<<sourceIndex);
				whiteQueens|=1L<<destinationIndex;
				break;
			default:
				;
			}
			if((blackKing&(1L<<destinationIndex))!=0)
			{
				blackKing&=~(1L<<destinationIndex);
				return kingId;
			}
			if((blackPawns&(1L<<destinationIndex))!=0)
			{
				blackPawns&=~(1L<<destinationIndex);
				return pawnId;
			}
			if((blackKnights&(1L<<destinationIndex))!=0)
			{
				blackKnights&=~(1L<<destinationIndex);
				return knightId;
			}
			if((blackBishops&(1L<<destinationIndex))!=0)
			{
				blackBishops&=~(1L<<destinationIndex);
				return bishopId;
			}
			if((blackRooks&(1L<<destinationIndex))!=0)
			{
				blackRooks&=~(1L<<destinationIndex);
				return rookId;
			}
			if((blackQueens&(1L<<destinationIndex))!=0)
			{
				blackQueens&=~(1L<<destinationIndex);
				return queenId;
			}
			break;
		case black:
			switch(pieceId)
			{
			case kingId:
				blackKing&=~(1L<<sourceIndex);
				blackKing|=1L<<destinationIndex;
				if(isBlackKingHasMoved==false&&sourceIndex==blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isBlackKingHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case pawnId:
				blackPawns&=~(1L<<sourceIndex);
				if(destinationIndex>=NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))
				{
					blackQueens|=1L<<destinationIndex;
					isSpecial[0]=true;
				}
				else
					blackPawns|=1L<<destinationIndex;
				break;
			case knightId:
				blackKnights&=~(1L<<sourceIndex);
				blackKnights|=1L<<destinationIndex;
				break;
			case rookId:
				blackRooks&=~(1L<<sourceIndex);
				blackRooks|=1L<<destinationIndex;
				if(isBlackRightRookHasMoved==false&&sourceIndex==rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isBlackRightRookHasMoved=true;
					isSpecial[0]=true;
				}
				if(isBlackLeftRookHasMoved==false&&sourceIndex==leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE)
				{
					isBlackLeftRookHasMoved=true;
					isSpecial[0]=true;
				}
				break;
			case bishopId:
				blackBishops&=~(1L<<sourceIndex);
				blackBishops|=1L<<destinationIndex;
				break;
			case queenId:
				blackQueens&=~(1L<<sourceIndex);
				blackQueens|=1L<<destinationIndex;
				break;
			default:
				;
			}
			if((whiteKing&(1L<<destinationIndex))!=0)
			{
				whiteKing&=~(1L<<destinationIndex);
				return kingId;
			}
			if((whitePawns&(1L<<destinationIndex))!=0)
			{
				whitePawns&=~(1L<<destinationIndex);
				return pawnId;
			}
			if((whiteKnights&(1L<<destinationIndex))!=0)
			{
				whiteKnights&=~(1L<<destinationIndex);
				return knightId;
			}
			if((whiteBishops&(1L<<destinationIndex))!=0)
			{
				whiteBishops&=~(1L<<destinationIndex);
				return bishopId;
			}
			if((whiteRooks&(1L<<destinationIndex))!=0)
			{
				whiteRooks&=~(1L<<destinationIndex);
				return rookId;
			}
			if((whiteQueens&(1L<<destinationIndex))!=0)
			{
				whiteQueens&=~(1L<<destinationIndex);
				return queenId;
			}
			break;
		default:
			;
		}
		return noPieceId;
	}
	
	public int getBlackPieceType(int indexPiece)
	{
		if((blackQueens&1L<<indexPiece)!=0)
			return queenId;
		else if((blackPawns&1L<<indexPiece)!=0)
			return pawnId;
		else if((blackRooks&1L<<indexPiece)!=0)
			return rookId;
		else if((blackBishops&1L<<indexPiece)!=0)
			return bishopId;
		else if((blackKnights&1L<<indexPiece)!=0)
			return knightId;
		else if((blackKing&1L<<indexPiece)!=0)
			return kingId;
		return noPieceId;
	}
	
	public int getWhitePieceType(int indexPiece)
	{
		if((whiteQueens&1L<<indexPiece)!=0)
			return queenId;
		else if((whitePawns&1L<<indexPiece)!=0)
			return pawnId;
		else if((whiteRooks&1L<<indexPiece)!=0)
			return rookId;
		else if((whiteBishops&1L<<indexPiece)!=0)
			return bishopId;
		else if((whiteKnights&1L<<indexPiece)!=0)
			return knightId;
		else if((whiteKing&1L<<indexPiece)!=0)
			return kingId;
		return noPieceId;
	}
	
	public int getPieceTypeAtThisIndexForCurrentTurn(Point pieceCoordinate)
	{
		int pieceIndex=pieceCoordinate.x+pieceCoordinate.y*NUMBER_OF_SQUARES_PER_LINE;
		switch(currentTurn)
		{
		case white:
			return getWhitePieceType(pieceIndex);
		case black:
			return getBlackPieceType(pieceIndex);
		default:
			;
		}
		return noPieceId;
	}
	
	public int makeThisMoveAndGetDescription(Point oldSelectedSquare,Point newSelectedSquare,ArrayList<String> arrayMoveDescription,boolean[] isSpecial,int isLastMoveEnableEnPassant)
	{
		counterMoveFinished++;
		int returnValue=makeThisMoveAndGetDescriptionWithoutIncrement(oldSelectedSquare,newSelectedSquare,arrayMoveDescription,isSpecial,isLastMoveEnableEnPassant);
		
		// we save position into the array and set the good number of occurrence
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueens,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueens,blackKing,blackPawns,blackRooks);
		for(int counterSituations=0;counterSituations<listPiecesSituation.size();counterSituations++)
			if(listPiecesSituation.get(counterSituations).equal(piecesSituation)==true)
			{
				listPiecesSituationOccurrences.set(counterSituations,listPiecesSituationOccurrences.get(counterSituations)+1);
				return returnValue;
			}
		listPiecesSituation.add(piecesSituation);
		listPiecesSituationOccurrences.add(1);
		return returnValue;
	}
	
	public int GiveMeThePieceColorOnThisSquare(Point pointCoordinates)
	{
		if((getWhitePieces()&1L<<(pointCoordinates.x+pointCoordinates.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
			return white;
		if((getBlackPieces()&1L<<(pointCoordinates.x+pointCoordinates.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
			return black;
		return 0;
	}
	
	public int getPieceTypeAtThisIndexAndWithThisColor(int pieceColor,int pieceIndex)
	{
		switch(pieceColor)
		{
		case white:
			return getWhitePieceType(pieceIndex);
		case black:
			return getBlackPieceType(pieceIndex);
		default:
			;
		}
		return noPieceId;
	}
	
	public int GetThePieceColorAtThisIndex(int pieceIndex)
	{
		if((getWhitePieces()&1L<<pieceIndex)!=0)
			return white;
		if((getBlackPieces()&1L<<pieceIndex)!=0)
			return black;
		return noPieceId;
	}
	
	public int getPieceTypeAtThisIndexWithCurrentColor(int indexPiece)
	{
		switch(currentTurn)
		{
		case black:
			return getPieceTypeAtThisIndexWithBlackColor(indexPiece);
		case white:
			return getPieceTypeAtThisIndexWithWhiteColor(indexPiece);
		default:
			return noPieceId;
		}
	}
	
	public int getPieceTypeAtThisIndexWithWhiteColor(int indexPiece)
	{
		if((whitePawns&(1L<<indexPiece))!=0)
			return pawnId;
		else if((whiteKnights&(1L<<indexPiece))!=0)
			return knightId;
		else if((whiteBishops&(1L<<indexPiece))!=0)
			return bishopId;
		else if((whiteRooks&(1L<<indexPiece))!=0)
			return rookId;
		else if((whiteQueens&(1L<<indexPiece))!=0)
			return queenId;
		else if((whiteKing&(1L<<indexPiece))!=0)
			return kingId;
		return noPieceId;
	}
	
	public int getPieceTypeAtThisIndexWithBlackColor(int indexPiece)
	{
		if((blackPawns&(1L<<indexPiece))!=0)
			return pawnId;
		else if((blackKnights&(1L<<indexPiece))!=0)
			return knightId;
		else if((blackBishops&(1L<<indexPiece))!=0)
			return bishopId;
		else if((blackRooks&(1L<<indexPiece))!=0)
			return rookId;
		else if((blackQueens&(1L<<indexPiece))!=0)
			return queenId;
		else if((blackKing&(1L<<indexPiece))!=0)
			return kingId;
		return noPieceId;
	}
	
	// used for king's moves, to check if other king doesn't attack this square, the method without check checking is used
	public boolean isThisSquareAttacked(int squareIndex,long whitePieces,long blackPieces,long allPieces)
	{
		switch(currentTurn)
		{
		case white:
			if((arrayKingMoves[squareIndex]&blackKing)!=0)
				return true;
			if((arrayKnightMoves[squareIndex]&blackKnights)!=0)
				return true;
			if((getLinesMoves(squareIndex)&blackRooks)!=0)
				return true;
			if((getDiagonalsMoves(squareIndex)&blackBishops)!=0)
				return true;
			if((getQueensMoves(squareIndex)&blackQueens)!=0)
				return true;
			if((getDiagonalsMoves(squareIndex)&blackQueens)!=0)
				return true;
			if((getWhitePawnMoves(squareIndex,blackPieces,allPieces)&blackPawns)!=0)
				return true;
			break;
		case black:
			
			if((arrayKingMoves[squareIndex]&whiteKing)!=0)
				return true;
			if((arrayKnightMoves[squareIndex]&whiteKnights)!=0)
				return true;
			if((getLinesMoves(squareIndex)&whiteRooks)!=0)
				return true;
			if((getDiagonalsMoves(squareIndex)&whiteBishops)!=0)
				return true;
			if((getDiagonalsMoves(squareIndex)&whiteQueens)!=0)
				return true;
			if((getLinesMoves(squareIndex)&whiteQueens)!=0)
				return true;
			if((getBlackPawnMoves(squareIndex,whitePieces,allPieces)&whitePawns)!=0)
				return true;
			break;
		default:
			;
		}
		return false;
	}
	
	public int unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(int pieceId,int sourceIndex,int destinationIndex,int pieceEventuallyDeleted,boolean isSpecial)
	{
		boolean[] isSpecialUnmake=new boolean[1];
		makeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceId,sourceIndex,destinationIndex,isSpecialUnmake);
		switch(currentTurn)
		{
		case white:
			switch(pieceEventuallyDeleted)
			{
			case kingId:
				blackKing|=1L<<sourceIndex;
				break;
			case pawnId:
				blackPawns|=1L<<sourceIndex;
				break;
			case knightId:
				blackKnights|=1L<<sourceIndex;
				break;
			case bishopId:
				blackBishops|=1L<<sourceIndex;
				break;
			case rookId:
				blackRooks|=1L<<sourceIndex;
				break;
			case queenId:
				blackQueens|=1L<<sourceIndex;
				break;
			default:
				;
			}
			if(isSpecial==true&&pieceId!=rookId&&pieceId!=kingId)
				whiteQueens&=~(1L<<sourceIndex);
			if(isSpecial==true&&destinationIndex==whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE&&pieceId==kingId)
				isWhiteKingHasMoved=false;
			if(isSpecial==true&&destinationIndex==rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE&&pieceId==rookId)
				isWhiteRightRookHasMoved=false;
			if(isSpecial==true&&destinationIndex==leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE&&pieceId==rookId)
				isWhiteLeftRookHasMoved=false;
			break;
		case black:
			switch(pieceEventuallyDeleted)
			{
			case kingId:
				whiteKing|=1L<<sourceIndex;
				break;
			case pawnId:
				whitePawns|=1L<<sourceIndex;
				break;
			case knightId:
				whiteKnights|=1L<<sourceIndex;
				break;
			case bishopId:
				whiteBishops|=1L<<sourceIndex;
				break;
			case rookId:
				whiteRooks|=1L<<sourceIndex;
				break;
			case queenId:
				whiteQueens|=1L<<sourceIndex;
				break;
			default:
				;
			}
			if(isSpecial==true&&pieceId!=rookId&&pieceId!=kingId)
				blackQueens&=~(1L<<sourceIndex);
			if(isSpecial==true&&destinationIndex==blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE&&pieceId==kingId)
				isBlackKingHasMoved=false;
			if(isSpecial==true&&destinationIndex==rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE&&pieceId==rookId)
				isBlackRightRookHasMoved=false;
			if(isSpecial==true&&destinationIndex==leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE&&pieceId==rookId)
				isBlackLeftRookHasMoved=false;
			break;
		default:
			;
		}
		return noPieceId;
	}
	
	public ArrayList<Point> UnmakeCastling(String castlingDescription)
	{
		boolean[] isSpecial=new boolean[1];
		ArrayList<Point> arrayConcernedSquares=new ArrayList<Point>();
		if(currentTurn==white)
		{
			if(castlingDescription.equals(kingSideCastlingExplicit)==true||castlingDescription.equals(kingSideCastlingStandard)==true) // king
			// side
			{
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,whiteRookKingCastlingDestination,rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,isSpecial);
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,whiteKingKingCastlingDestination,whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,isSpecial);
				arrayConcernedSquares.add(new Point(whiteKingKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,whiteKingKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add(new Point(whiteRookKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,whiteRookKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add((Point)rightWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteRightRookHasMoved=false;
				isWhiteKingHasMoved=false;
			}
			else
			// queen side
			{
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,whiteRookQueenCastlingDestination,leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,isSpecial);
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,whiteKingQueenCastlingDestination,whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,isSpecial);
				arrayConcernedSquares.add(new Point(whiteKingQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,whiteKingQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add(new Point(whiteRookQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,whiteRookQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add((Point)leftWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteLeftRookHasMoved=false;
				isWhiteKingHasMoved=false;
			}
		}
		else
		{
			if(castlingDescription.equals(kingSideCastlingExplicit)==true||castlingDescription.equals(kingSideCastlingStandard)==true)
			{
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,blackRookKingCastlingDestination,rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,isSpecial);
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,blackKingKingCastlingDestination,blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,isSpecial);
				arrayConcernedSquares.add(new Point(blackKingKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,blackKingKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add(new Point(blackRookKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,blackRookKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add((Point)rightBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackRightRookHasMoved=false;
				isBlackKingHasMoved=false;
			}
			else
			{
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,blackRookQueenCastlingDestination,leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,isSpecial);
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,blackKingQueenCastlingDestination,blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,isSpecial);
				arrayConcernedSquares.add(new Point(blackKingQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,blackKingQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add(new Point(blackRookQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,blackRookQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add((Point)leftBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackLeftRookHasMoved=false;
				isBlackKingHasMoved=false;
			}
		}
		return arrayConcernedSquares;
	}
	
	private long getMovesForKingWithCheckChecking(int pieceIndex,long whitePieces,long blackPieces,long allPieces)
	{
		long movePossibilities=arrayKingMoves[pieceIndex];
		
		// we delete moves that put king in check
		long currentPossibleMoves=movePossibilities;
		int leadingZeros=Long.numberOfTrailingZeros(currentPossibleMoves);
		long saveKing;
		if(currentTurn==white)
		{
			saveKing=whiteKing;
			whiteKing=0;
		}
		else
		{
			saveKing=blackKing;
			blackKing=0;
		}
		while(true)
		{
			if(leadingZeros==NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE)
				break;
			if(isThisSquareAttacked(leadingZeros,whitePieces,blackPieces,allPieces)==true)
				movePossibilities&=~(1L<<leadingZeros);
			currentPossibleMoves&=~(1L<<leadingZeros);
			leadingZeros=Long.numberOfTrailingZeros(currentPossibleMoves);
		}
		if(currentTurn==white)
			whiteKing=saveKing;
		else
			blackKing=saveKing;
		
		// castling management
		if(currentTurn==white)
		{
			if(((whiteKing&1L<<(whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE))!=0)&&((whiteRooks&1L<<(rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE))!=0)&&isWhiteKingHasMoved==false&&isWhiteRightRookHasMoved==false)
			{
				long whiteKingCastlingMaskTemp=whiteKingCastlingMask;
				long allPiecesForCastling=(allPieces&whiteKingCastlingMaskTemp);
				if(allPiecesForCastling==0)
				{
					int indexSquare=Long.numberOfTrailingZeros(whiteKingCastlingMaskTemp);
					while(indexSquare!=NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE)
					{
						if(isThisSquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
						whiteKingCastlingMaskTemp&=~(1L<<indexSquare);
						indexSquare=Long.numberOfTrailingZeros(whiteKingCastlingMaskTemp);
					}
					if(indexSquare==NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE&&isThisSquareAttacked(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						movePossibilities|=1L<<(whiteKingKingCastlingDestination);
				}
			}
			if(((whiteKing&1L<<(whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE))!=0)&&((whiteRooks&1L<<(leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE))!=0)&&isWhiteKingHasMoved==false&&isWhiteLeftRookHasMoved==false)
			{
				long whiteQueenCastlingMaskTemp=whiteQueenCastlingMask;
				long allPiecesForCastling=(allPieces&whiteQueenCastlingMaskTemp);
				if(allPiecesForCastling==0)
				{
					int indexSquare=Long.numberOfTrailingZeros(whiteQueenCastlingMaskTemp);
					while(indexSquare!=NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE)
					{
						if(isThisSquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
						whiteQueenCastlingMaskTemp&=~(1L<<indexSquare);
						indexSquare=Long.numberOfTrailingZeros(whiteQueenCastlingMaskTemp);
					}
					if(indexSquare==NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE&&isThisSquareAttacked(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						movePossibilities|=1L<<(whiteKingQueenCastlingDestination);
				}
			}
		}
		else
		{
			if(((blackKing&1L<<(blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE))!=0)&&((blackRooks&1L<<(rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE))!=0)&&isBlackKingHasMoved==false&&isBlackRightRookHasMoved==false)
			{
				long blackKingCastlingMaskTemp=blackKingCastlingMask;
				long allPiecesForCastling=(allPieces&blackKingCastlingMaskTemp);
				if(allPiecesForCastling==0)
				{
					int indexSquare=Long.numberOfTrailingZeros(blackKingCastlingMaskTemp);
					while(indexSquare!=NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE)
					{
						if(isThisSquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
						blackKingCastlingMaskTemp&=~(1L<<indexSquare);
						indexSquare=Long.numberOfTrailingZeros(blackKingCastlingMaskTemp);
					}
					if(indexSquare==NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE&&isThisSquareAttacked(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						movePossibilities|=1L<<(blackKingKingCastlingDestination);
				}
			}
			if(((blackKing&1L<<(blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE))!=0)&&((blackRooks&1L<<(leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE))!=0)&&isBlackKingHasMoved==false&&isBlackLeftRookHasMoved==false)
			{
				long blackQueenCastlingMaskTemp=blackQueenCastlingMask;
				long allPiecesForCastling=(allPieces&blackQueenCastlingMaskTemp);
				if(allPiecesForCastling==0)
				{
					int indexSquare=Long.numberOfTrailingZeros(blackQueenCastlingMaskTemp);
					while(indexSquare!=NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE)
					{
						if(isThisSquareAttacked(indexSquare,blackPieces,whitePieces,allPieces)==true)
							break;
						blackQueenCastlingMaskTemp&=~(1L<<indexSquare);
						indexSquare=Long.numberOfTrailingZeros(blackQueenCastlingMaskTemp);
					}
					if(indexSquare==NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE&&isThisSquareAttacked(pieceIndex,blackPieces,whitePieces,allPieces)==false)
						movePossibilities|=1L<<(blackKingQueenCastlingDestination);
				}
			}
		}
		movePossibilities&=~getCurrentPieces();
		return movePossibilities;
	}
	
	private long getMovesForKingWithCheckCheckingWithoutCastling(int pieceIndex,long whitePieces,long blackPieces,long allPieces)
	{
		long movePossibilities=arrayKingMoves[pieceIndex];
		movePossibilities&=~getCurrentPieces();
		
		// we delete moves that put king in check
		long currentPossibleMoves=movePossibilities;
		int leadingZeros=Long.numberOfTrailingZeros(currentPossibleMoves);
		long saveKing;
		if(currentTurn==white)
		{
			saveKing=whiteKing;
			whiteKing=0;
		}
		else
		{
			saveKing=blackKing;
			blackKing=0;
		}
		while(true)
		{
			if(leadingZeros==NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE)
				break;
			if(isThisSquareAttacked(leadingZeros,whitePieces,blackPieces,allPieces)==true)
				movePossibilities&=~(1L<<leadingZeros);
			currentPossibleMoves&=~(1L<<leadingZeros);
			leadingZeros=Long.numberOfTrailingZeros(currentPossibleMoves);
		}
		if(currentTurn==white)
			whiteKing=saveKing;
		else
			blackKing=saveKing;
		return movePossibilities;
	}
	
	// we investigate to know is three moves repetition occurs
	public boolean isThisMoveHasToBeRemovedDueToThreeRepetitionsLaw(int color,int pieceType,int indexSource,int indexDestination)
	{
		boolean[] isSpecial=new boolean[1];
		int pieceEventuallyDeleted=makeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,indexSource,indexDestination,isSpecial);
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueens,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueens,blackKing,blackPawns,blackRooks);
		for(int counterSituations=0;counterSituations<listPiecesSituation.size();counterSituations++)
			if(listPiecesSituation.get(counterSituations).equal(piecesSituation)==true&&listPiecesSituationOccurrences.get(counterSituations)+1>=maximumOccurrenceForASituation)
			{
				unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,indexDestination,indexSource,pieceEventuallyDeleted,isSpecial[0]);
				return true;
			}
		unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,indexDestination,indexSource,pieceEventuallyDeleted,isSpecial[0]);
		return false;
	}
	
	// check is multiple piece with the same color and same type can go to the same square
	public Boolean isItAmbiguous(Point oldSelectedSquare,Point newSelectedSquare,long currentPieces,int isLastMoveEnableEnPassant)
	{
		if((currentPieces&1L<<(oldSelectedSquare.x+oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
			for(int counterPieces=0;counterPieces<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;counterPieces++)
				if(((currentPieces&1L<<counterPieces)!=0)&&counterPieces!=(oldSelectedSquare.x+oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE))
				{
					ArrayList<Integer> arrayListPossibleMoves=getListOfPossibleMovesForAPieceWithCheckChecking(counterPieces,isLastMoveEnableEnPassant,true);
					for(int counterMoves=0;counterMoves<arrayListPossibleMoves.size();counterMoves++)
						if(newSelectedSquare.x==arrayListPossibleMoves.get(counterMoves)%NUMBER_OF_SQUARES_PER_LINE&&newSelectedSquare.y==arrayListPossibleMoves.get(counterMoves)/NUMBER_OF_SQUARES_PER_LINE)
							return true;
				}
		return false;
	}
	
	// transform a coordinate square into a string with algebraic notation
	private String getSquareInString(Point coordinates)
	{
		Character[] heightFirstLettersOfTheAlphabet=
		{'a','b','c','d','e','f','g','h'};
		String result="";
		result+=heightFirstLettersOfTheAlphabet[coordinates.x];
		result+=NUMBER_OF_SQUARES_PER_LINE-coordinates.y;
		return result;
	}
	
	// get the name of a piece, useful to understand what happens
	private String getNamePieceAtThisSquare(Point squareCoordinates)
	{
		if((whiteKing&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE)|blackKing&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
			return "king";
		else if((whiteKnights&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE)|blackKnights&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
			return "knight";
		else if((whitePawns&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE)|blackPawns&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
			return "pawn";
		else if((whiteRooks&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE)|blackRooks&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
			return "rook";
		else if((whiteBishops&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE)|blackBishops&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
			return "bishop";
		else if((whiteQueens&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE)|blackQueens&1L<<(squareCoordinates.x+squareCoordinates.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
			return "queen";
		return "";
	}
	
	// determine if the move is ambiguous or if a piece has been eaten and give the standard description of the move
	private String TransformExplicitMoveDescriptionIntoStandardMoveDescription(String explicitMoveDescription,boolean isItAmbiguousMove)
	{
		String standardMoveDescription=explicitMoveDescription;
		int indexDot=standardMoveDescription.indexOf(".");
		int indexPawn=explicitMoveDescription.indexOf("pawn");
		if(indexPawn>indexDot+3) // this is useful to know if its a pawn who moved or if it's a pawn eaten
			indexPawn=-1;
		standardMoveDescription=standardMoveDescription.replaceAll("pawn ","");
		standardMoveDescription=standardMoveDescription.replaceAll("knight ","N");
		standardMoveDescription=standardMoveDescription.replaceAll("queen ","Q");
		standardMoveDescription=standardMoveDescription.replaceAll("king ","K");
		standardMoveDescription=standardMoveDescription.replaceAll("bishop ","B");
		standardMoveDescription=standardMoveDescription.replaceAll("rook ","R");
		standardMoveDescription=standardMoveDescription.replaceAll(enPassantExplicit," "+enPassantStandard);
		int indexHyphen=standardMoveDescription.indexOf("-");
		if(indexPawn==-1)
		{
			if(isItAmbiguousMove==false)
			{
				if(standardMoveDescription.indexOf(kingSideCastlingExplicit)!=-1) // castling management
					standardMoveDescription=kingSideCastlingStandard;
				else if(standardMoveDescription.indexOf(queenSideCastlingExplicit)!=-1)
					standardMoveDescription=queenSideCastlingStandard;
				else
				{
					standardMoveDescription=standardMoveDescription.substring(indexDot+2,indexDot+3)+standardMoveDescription.substring(indexHyphen+1,indexHyphen+3);
				}
			}
			else
			{
				standardMoveDescription=standardMoveDescription.substring(indexDot+2,indexDot+5)+standardMoveDescription.substring(indexHyphen+1,indexHyphen+3);
			}
		}
		else
		{
			if(isItAmbiguousMove==false)
			{
				standardMoveDescription=standardMoveDescription.substring(indexHyphen+1,indexHyphen+3); // we do not set any letter for the pawn case
				if(explicitMoveDescription.indexOf(promotionExplicit)!=-1)
					standardMoveDescription+=promotionStandard;
			}
			else
			{
				standardMoveDescription=standardMoveDescription.substring(indexHyphen-2,indexHyphen-0)+standardMoveDescription.substring(indexHyphen+1,indexHyphen+3);
				if(explicitMoveDescription.indexOf(promotionExplicit)!=-1)
					standardMoveDescription+=promotionStandard;
			}
		}
		int indexEat=explicitMoveDescription.indexOf("captures ");
		if(indexEat!=-1)
		{
			if(indexPawn==-1)
				standardMoveDescription=standardMoveDescription.substring(0,1)+"x"+standardMoveDescription.substring(1,standardMoveDescription.length());
			else
				standardMoveDescription="x"+standardMoveDescription; // we are in pawn case
		}
		int indexCheck=explicitMoveDescription.indexOf(checkDescription);
		if(indexCheck!=-1)
			standardMoveDescription+="+";
		if(explicitMoveDescription.indexOf(enPassantExplicit)!=-1)
			standardMoveDescription+=" "+enPassantStandard;
		return standardMoveDescription;
	}
	
	// we make the move, with a description, this is useful for human play
	public int makeThisMoveAndGetDescriptionWithoutIncrement(Point oldSelectedSquare,Point newSelectedSquare,ArrayList<String> arrayMoveDescription,boolean[] isSpecial,int isLastMoveEnableEnPassant)
	{
		isSpecial[0]=false;
		int returnValue=0;
		
		// before all we have to know if several pieces can move to destination or only one
		boolean isItAmbiguousMove=false;
		if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==white)
		{
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,whitePawns,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteKnights,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteQueens,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteBishops,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,whiteRooks,isLastMoveEnableEnPassant);
		}
		if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==black)
		{
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,blackPawns,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,blackKnights,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,blackQueens,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,blackBishops,isLastMoveEnableEnPassant);
			isItAmbiguousMove=isItAmbiguousMove||isItAmbiguous(oldSelectedSquare,newSelectedSquare,blackRooks,isLastMoveEnableEnPassant);
		}
		
		// we create explicit description of the move
		String moveDescription="";
		moveDescription+=counterMoveFinished+". ";
		
		// we check if it's a castling
		if(getPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==whiteKingInitialPosition.y&&oldSelectedSquare.x==whiteKingInitialPosition.x&&newSelectedSquare.y==whiteKingKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE&&newSelectedSquare.x==whiteKingKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE)
		{
			moveDescription+=kingSideCastlingExplicit;
			makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,whiteRookKingCastlingDestination,isSpecial);
			returnValue=whiteRookKingCastlingDestination;
		}
		else if(getPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==whiteKingInitialPosition.y&&oldSelectedSquare.x==whiteKingInitialPosition.x&&newSelectedSquare.y==whiteKingQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE&&newSelectedSquare.x==whiteKingQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE)
		{
			moveDescription+=queenSideCastlingExplicit;
			makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,whiteRookQueenCastlingDestination,isSpecial);
			returnValue=whiteRookQueenCastlingDestination;
		}
		
		else if(getPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==blackKingInitialPosition.y&&oldSelectedSquare.x==blackKingInitialPosition.x&&newSelectedSquare.y==blackKingKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE&&newSelectedSquare.x==blackKingKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE)
		{
			moveDescription+=kingSideCastlingExplicit;
			makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,blackRookKingCastlingDestination,isSpecial);
			returnValue=blackRookKingCastlingDestination;
		}
		else if(getPieceTypeAtThisIndexAndWithThisColor(currentTurn,oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE+oldSelectedSquare.x)==kingId&&oldSelectedSquare.y==blackKingInitialPosition.y&&oldSelectedSquare.x==blackKingInitialPosition.x&&newSelectedSquare.y==blackKingQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE&&newSelectedSquare.x==blackKingQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE)
		{
			moveDescription+=queenSideCastlingExplicit;
			makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,blackRookQueenCastlingDestination,isSpecial);
			returnValue=blackRookQueenCastlingDestination;
		}
		else
		{
			moveDescription+=getNamePieceAtThisSquare(oldSelectedSquare)+" "+getSquareInString(oldSelectedSquare)+"-"+getSquareInString(newSelectedSquare);
			if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==white)
				if((getBlackPieces()&1L<<(newSelectedSquare.x+newSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
					moveDescription+=" captures "+getNamePieceAtThisSquare(newSelectedSquare);
			if(GiveMeThePieceColorOnThisSquare(oldSelectedSquare)==black)
				if((getWhitePieces()&1L<<(newSelectedSquare.x+newSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE))!=0)
					moveDescription+=" captures "+getNamePieceAtThisSquare(newSelectedSquare);
		}
		
		// promotion
		if((newSelectedSquare.y==0&&(whitePawns&1L<<(oldSelectedSquare.x+oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE))!=0)||(newSelectedSquare.y==NUMBER_OF_SQUARES_PER_LINE-1&&((blackPawns&1L<<(oldSelectedSquare.x+oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE))!=0)))
			moveDescription+=" - "+promotionExplicit;
		
		// make the move itself
		int pieceType=getPieceTypeAtThisIndexWithCurrentColor(oldSelectedSquare.x+oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE);
		makeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,oldSelectedSquare.x+oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE,newSelectedSquare.x+newSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE,isSpecial);
		
		// we have to know if it's an en passant move, if this is case, we write it
		if(currentTurn==white&&isLastMoveEnableEnPassant!=-1&&firstLeftBlackPawnsInitialPosition.y+2==oldSelectedSquare.y&&newSelectedSquare.x==isLastMoveEnableEnPassant%NUMBER_OF_SQUARES_PER_LINE&&getPieceTypeAtThisIndexWithCurrentColor(newSelectedSquare.x+newSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE)==pawnId)
		{
			returnValue=oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE+newSelectedSquare.x;
			blackPawns&=~(1L<<returnValue);
			moveDescription+=" captures pawn "+enPassantExplicit;
			isSpecial[0]=true;
		}
		if(currentTurn==black&&isLastMoveEnableEnPassant!=-1&&firstLeftWhitePawnsInitialPosition.y-2==oldSelectedSquare.y&&newSelectedSquare.x==isLastMoveEnableEnPassant%NUMBER_OF_SQUARES_PER_LINE&&getPieceTypeAtThisIndexWithCurrentColor(newSelectedSquare.x+newSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE)==pawnId)
		{
			returnValue=oldSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE+newSelectedSquare.x;
			whitePawns&=~(1L<<returnValue);
			moveDescription+=" captures pawn "+enPassantExplicit;
			isSpecial[0]=true;
		}
		
		// we look if the opponent's king is under check
		changePlayerTurn();
		if(currentTurn==black)
		{
			if(isThisSquareAttacked(Long.numberOfTrailingZeros(blackKing),getWhitePieces(),getBlackPieces(),getAllPieces())==true)
				moveDescription+=" - "+checkDescription;
		}
		else
		{
			if(isThisSquareAttacked(Long.numberOfTrailingZeros(whiteKing),getWhitePieces(),getBlackPieces(),getAllPieces())==true)
				moveDescription+=" - "+checkDescription;
		}
		changePlayerTurn();
		arrayMoveDescription.add(moveDescription);
		arrayMoveDescription.add(TransformExplicitMoveDescriptionIntoStandardMoveDescription(moveDescription,isItAmbiguousMove));
		return returnValue;
	}
	
	public boolean isThisMovePossible(Point sourceCoordinates,Point destinationCoordinates,int isLastMoveEnableEnPassant)
	{
		ArrayList<Integer> arrayListPossibleMoves=getListOfPossibleMovesForAPieceWithCheckChecking(sourceCoordinates.x+sourceCoordinates.y*NUMBER_OF_SQUARES_PER_LINE,isLastMoveEnableEnPassant,true);
		for(int indexCounter=0;indexCounter<arrayListPossibleMoves.size();indexCounter++)
			if(destinationCoordinates.x==arrayListPossibleMoves.get(indexCounter)%NUMBER_OF_SQUARES_PER_LINE&&destinationCoordinates.y==arrayListPossibleMoves.get(indexCounter)/NUMBER_OF_SQUARES_PER_LINE)
				return true;
		return false;
	}
	
	public void generateRooksMoves()
	{
		// lines for rooks and queens
		for(int currentPieceIndex=0;currentPieceIndex<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;currentPieceIndex++)
		{
			for(int counterVertical=1;counterVertical<NUMBER_OF_SQUARES_PER_LINE-1;counterVertical++)
				arrayVerticalLineMask[currentPieceIndex]|=1L<<(counterVertical*NUMBER_OF_SQUARES_PER_LINE)+currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE;
			
			// now we compute vertical possibilities
			for(long counterVerticalPossibilities=0;counterVerticalPossibilities<Math.pow(2,NUMBER_OF_SQUARES_PER_LINE-2);counterVerticalPossibilities++)
			{
				long magicDestinationWithMostZero=0L;
				long currentVerticalPossibilities=0L;
				// we set top left bottom right diagonal possibilities
				for(int counterBitsIntoTheVerticalMask=0;counterBitsIntoTheVerticalMask<NUMBER_OF_SQUARES_PER_LINE-2;counterBitsIntoTheVerticalMask++)
				{
					// we get the current bit for the current possibilities
					long currentBitOnFirstPosition=(counterVerticalPossibilities&(1L<<counterBitsIntoTheVerticalMask))>>(counterBitsIntoTheVerticalMask);
					currentVerticalPossibilities|=currentBitOnFirstPosition<<counterBitsIntoTheVerticalMask*(NUMBER_OF_SQUARES_PER_LINE)+currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+NUMBER_OF_SQUARES_PER_LINE;
					
					// here we compute magic destination
					long currentBitOnFirstPositionForMag=(counterVerticalPossibilities&(1L<<counterBitsIntoTheVerticalMask))>>(counterBitsIntoTheVerticalMask);
					magicDestinationWithMostZero|=currentBitOnFirstPositionForMag<<counterBitsIntoTheVerticalMask;
				}
				
				// for each possibility we have to compute the possibles moves
				// to top
				long currentMoveResult=0L;
				for(int counterToTop=currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE-1;counterToTop>=0;counterToTop--)
				{
					currentMoveResult|=1L<<currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+counterToTop*NUMBER_OF_SQUARES_PER_LINE;
					if((currentVerticalPossibilities&1L<<(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+counterToTop*NUMBER_OF_SQUARES_PER_LINE))!=0)
						break;
					
				}
				
				// to bottom
				for(int counterToBottom=currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE+1;counterToBottom<NUMBER_OF_SQUARES_PER_LINE;counterToBottom++)
				{
					currentMoveResult|=1L<<currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+counterToBottom*NUMBER_OF_SQUARES_PER_LINE;
					if((currentVerticalPossibilities&1L<<(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+counterToBottom*NUMBER_OF_SQUARES_PER_LINE))!=0)
						break;
					
				}
				matrixLineVerticalMoveResult[currentPieceIndex][(int)magicDestinationWithMostZero]=currentMoveResult;
			}
			
			for(int counterVertical=1;counterVertical<NUMBER_OF_SQUARES_PER_LINE-1;counterVertical++)
				arrayHorizontalLineMask[currentPieceIndex]|=1L<<(currentPieceIndex-currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE)+counterVertical;
			for(long counterHorizontalPossibilities=0;counterHorizontalPossibilities<Math.pow(2,NUMBER_OF_SQUARES_PER_LINE-2);counterHorizontalPossibilities++)
			{
				long magicDestinationWithMostZero=0L;
				long currentHorizontalPossibilities=0L;
				for(int counterBitsIntoTheHorizontalMask=0;counterBitsIntoTheHorizontalMask<6;counterBitsIntoTheHorizontalMask++)
				{
					// we get the current bit for the current possibilities
					long currentBitOnFirstPosition=(counterHorizontalPossibilities&(1L<<counterBitsIntoTheHorizontalMask))>>(counterBitsIntoTheHorizontalMask);
					currentHorizontalPossibilities|=currentBitOnFirstPosition<<counterBitsIntoTheHorizontalMask+(currentPieceIndex-(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE)+1);
					
					// here we compute magic destination
					long currentBitOnFirstPositionForMag=(counterHorizontalPossibilities&(1L<<counterBitsIntoTheHorizontalMask))>>(counterBitsIntoTheHorizontalMask);
					magicDestinationWithMostZero|=currentBitOnFirstPositionForMag<<counterBitsIntoTheHorizontalMask;
				}
				
				// to left
				long currentMoveResult=0L;
				for(int counterToLeft=currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE-1;counterToLeft>=0;counterToLeft--)
				{
					currentMoveResult|=1L<<currentPieceIndex-currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+counterToLeft;
					if((currentHorizontalPossibilities&1L<<(currentPieceIndex-currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+counterToLeft))!=0)
						break;
					
				}
				
				// to right
				for(int counterToRight=currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+1;counterToRight<NUMBER_OF_SQUARES_PER_LINE;counterToRight++)
				{
					currentMoveResult|=1L<<currentPieceIndex-currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+counterToRight;
					if((currentHorizontalPossibilities&1L<<(currentPieceIndex-currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE+counterToRight))!=0)
						break;
					
				}
				matrixLineHorizontalMoveResult[currentPieceIndex][(int)magicDestinationWithMostZero]=currentMoveResult;
			}
			
			// we have to set magic numbers for vertical lines
			arrayMagicNumberForVerticalLines[currentPieceIndex]=0L;
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE==0) // a
			{
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<7;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<14;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<21;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<28;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<35;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<42;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<49;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<56;
			}
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE==1) // b
			{
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<6;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<13;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<20;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<27;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<34;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<41;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<48;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<55;
			}
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE==2) // c
			{
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<5;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<12;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<19;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<26;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<33;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<40;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<47;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<54;
			}
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE==3) // d
			{
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<4;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<11;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<18;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<25;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<32;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<39;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<46;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<53;
			}
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE==4) // e
			{
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<3;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<10;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<17;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<24;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<31;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<38;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<45;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<52;
			}
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE==5) // f
			{
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<2;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<9;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<16;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<23;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<30;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<37;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<44;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<51;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<58;
				
			}
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE==6) // g
			{
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<1;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<8;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<15;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<22;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<29;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<36;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<43;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<50;
			}
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE==7) // h
			{
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<0;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<7;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<14;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<21;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<28;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<35;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<42;
				arrayMagicNumberForVerticalLines[currentPieceIndex]|=1L<<49;
			}
		}
	}
	
	public void generateKnightsMoves()
	{
		for(int currentPieceIndex=0;currentPieceIndex<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;currentPieceIndex++)
		{
			long boardWithFilter=0;
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE>1) // first left-top and second bottom-right
			{
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE>0)
					boardWithFilter|=1L<<currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE-2;
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1)
					boardWithFilter|=1L<<currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE-2;
			}
			if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE>1) // second left-bottom and first top-right
			{
				if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE>0)
					boardWithFilter|=1L<<currentPieceIndex-2*NUMBER_OF_SQUARES_PER_LINE-1;
				if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1)
					boardWithFilter|=1L<<currentPieceIndex-2*NUMBER_OF_SQUARES_PER_LINE+1;
			}
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-2) // second right-top and first bottom-right
			{
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE>0)
					boardWithFilter|=1L<<currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE+2;
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1)
					boardWithFilter|=1L<<currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE+2;
			}
			if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-2) // first left-bottom and second bottom-right
			{
				if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE>0)
					boardWithFilter|=1L<<currentPieceIndex+2*NUMBER_OF_SQUARES_PER_LINE-1;
				if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1)
					boardWithFilter|=1L<<currentPieceIndex+2*NUMBER_OF_SQUARES_PER_LINE+1;
			}
			arrayKnightMoves[currentPieceIndex]=boardWithFilter;
		}
	}
	
	public void generateKingsMoves()
	{
		for(int currentPieceIndex=0;currentPieceIndex<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;currentPieceIndex++)
		{
			
			// we have to compute king moves now
			long boardWithFilter=0;
			
			// top-middle
			if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE>0)
				boardWithFilter|=1L<<currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE;
			
			// bottom-middle
			if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1)
				boardWithFilter|=1L<<currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE;
			
			// left : top, middle and bottom
			if(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE>0)
			{
				boardWithFilter|=1L<<currentPieceIndex-1;
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE>0)
					boardWithFilter|=1L<<currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE-1;
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1)
					boardWithFilter|=1L<<currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE-1;
			}
			
			// right : top, middle and bottom
			if((currentPieceIndex+1)%NUMBER_OF_SQUARES_PER_LINE!=0)
			{
				boardWithFilter|=1L<<currentPieceIndex+1;
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE>0)
					boardWithFilter|=1L<<currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE+1;
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1)
					boardWithFilter|=1L<<currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE+1;
			}
			arrayKingMoves[currentPieceIndex]=boardWithFilter;
		}
	}
	
	public void generateBishopsMoves()
	{
		// the magic for diagonal
		magicForDiagonals=0L;
		magicForDiagonals|=1L<<0;
		magicForDiagonals|=1L<<8;
		magicForDiagonals|=1L<<16;
		magicForDiagonals|=1L<<24;
		magicForDiagonals|=1L<<32;
		magicForDiagonals|=1L<<40;
		magicForDiagonals|=1L<<48;
		magicForDiagonals|=1L<<56;
		
		for(int currentPieceIndex=0;currentPieceIndex<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;currentPieceIndex++)
		{
			// we determine top left insertion point
			int horizontalTopLeftIndex=currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE-currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE;
			if(horizontalTopLeftIndex<0)
				horizontalTopLeftIndex=0;
			arrayTopLeft[currentPieceIndex]=horizontalTopLeftIndex;
			int verticalTopLeftIndex=currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE-currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE;
			if(verticalTopLeftIndex<0)
				verticalTopLeftIndex=0;
			long currentDiagonalTopLeftBottomRightMask=0;
			
			// now we have to know how many bits there will be into the mask for top left to bottom right
			int numberOfBitsIntoTheMaskTopLeftBottomRight=NUMBER_OF_SQUARES_PER_LINE-Math.abs(horizontalTopLeftIndex-verticalTopLeftIndex)-2; // -2 to delete extremums
			
			// we set top left bottom right diagonal mask
			for(int counterBitsIntoTheMaskTopLeftBottomRight=1;counterBitsIntoTheMaskTopLeftBottomRight<=numberOfBitsIntoTheMaskTopLeftBottomRight;counterBitsIntoTheMaskTopLeftBottomRight++)
				currentDiagonalTopLeftBottomRightMask|=1L<<horizontalTopLeftIndex+verticalTopLeftIndex*NUMBER_OF_SQUARES_PER_LINE+(counterBitsIntoTheMaskTopLeftBottomRight*(NUMBER_OF_SQUARES_PER_LINE+1));
			
			// we determine bottom left insertion point
			int horizontalBottomLeftIndex=currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE-(NUMBER_OF_SQUARES_PER_LINE-currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE-1);
			if(horizontalBottomLeftIndex<0)
				horizontalBottomLeftIndex=0;
			arrayBottomLeft[currentPieceIndex]=horizontalBottomLeftIndex;
			int verticalBottomLeftIndex=currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE+currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE;
			if(verticalBottomLeftIndex>=NUMBER_OF_SQUARES_PER_LINE)
				verticalBottomLeftIndex=NUMBER_OF_SQUARES_PER_LINE-1;
			
			// now we have to know how many bits there will be into the mask for bottom left to top right
			int numberOfBitsIntoTheMaskBottomLeftTopRight=verticalBottomLeftIndex-horizontalBottomLeftIndex-1; // -1 to delete extremum
			long currentDiagonalBottomLeftTopRightMask=0;
			for(int counterBitsIntoTheMaskBottomLeftTopRight=1;counterBitsIntoTheMaskBottomLeftTopRight<=numberOfBitsIntoTheMaskBottomLeftTopRight;counterBitsIntoTheMaskBottomLeftTopRight++)
				currentDiagonalBottomLeftTopRightMask|=1L<<horizontalBottomLeftIndex+verticalBottomLeftIndex*NUMBER_OF_SQUARES_PER_LINE-(counterBitsIntoTheMaskBottomLeftTopRight*(NUMBER_OF_SQUARES_PER_LINE-1));
			
			// save the mask
			arrayDiagonalTopLeftBottomRightMask[currentPieceIndex]=currentDiagonalTopLeftBottomRightMask;
			arrayDiagonalBottomLeftTopRightMask[currentPieceIndex]=currentDiagonalBottomLeftTopRightMask;
			
			// now we compute possibilities
			for(long counterTopLeftBottomRightPossibilities=0;counterTopLeftBottomRightPossibilities<Math.pow(2,numberOfBitsIntoTheMaskTopLeftBottomRight);counterTopLeftBottomRightPossibilities++)
			{
				long currentPossibilitiesMaskForTopLeftBottomRightPossibilities=0;
				long magicDestinationWithMostZero=0;
				
				// we set top left bottom right diagonal possibilities
				for(int counterBitsIntoTheMaskTopLeftBottomRight=0;counterBitsIntoTheMaskTopLeftBottomRight<numberOfBitsIntoTheMaskTopLeftBottomRight;counterBitsIntoTheMaskTopLeftBottomRight++)
				{
					// we get the current bit for the current possibilities
					long currentBitOnFirstPosition=(counterTopLeftBottomRightPossibilities&(1L<<counterBitsIntoTheMaskTopLeftBottomRight))>>(counterBitsIntoTheMaskTopLeftBottomRight);
					currentPossibilitiesMaskForTopLeftBottomRightPossibilities|=currentBitOnFirstPosition<<horizontalTopLeftIndex+verticalTopLeftIndex*NUMBER_OF_SQUARES_PER_LINE+((counterBitsIntoTheMaskTopLeftBottomRight+1)*(NUMBER_OF_SQUARES_PER_LINE+1));
					
					// here we compute magic destination
					long currentBitOnFirstPositionForMag=(counterTopLeftBottomRightPossibilities&(1L<<counterBitsIntoTheMaskTopLeftBottomRight))>>(counterBitsIntoTheMaskTopLeftBottomRight);
					magicDestinationWithMostZero|=currentBitOnFirstPositionForMag<<counterBitsIntoTheMaskTopLeftBottomRight;
					
				}
				
				// now we have to compute moves possible for the top left bottom right diagonal to top left
				long movesResult=0;
				int counterVertical=(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE)-1;
				int counterHorizontal=(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE)-1;
				for(;counterVertical>=0&&counterHorizontal>=0;counterHorizontal--,counterVertical--)
				{
					movesResult|=1L<<(counterHorizontal+counterVertical*NUMBER_OF_SQUARES_PER_LINE);
					if((currentPossibilitiesMaskForTopLeftBottomRightPossibilities&1L<<(counterHorizontal+counterVertical*NUMBER_OF_SQUARES_PER_LINE))!=0)
						break;
				}
				
				// to bottom right
				counterVertical=(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE)+1;
				counterHorizontal=(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE)+1;
				for(;counterVertical<NUMBER_OF_SQUARES_PER_LINE&&counterHorizontal<NUMBER_OF_SQUARES_PER_LINE;counterHorizontal++,counterVertical++)
				{
					movesResult|=1L<<(counterHorizontal+counterVertical*NUMBER_OF_SQUARES_PER_LINE);
					if((currentPossibilitiesMaskForTopLeftBottomRightPossibilities&1L<<(counterHorizontal+counterVertical*NUMBER_OF_SQUARES_PER_LINE))!=0)
						break;
				}
				
				// we set the moves
				matrixDiagonalTopLeftBottomRightMoveResult[currentPieceIndex][(int)magicDestinationWithMostZero]=movesResult;
			}
			
			// now we do the bottom left top right diagonal possibilities
			for(long counterBottomLeftTopRightPossibilities=0;counterBottomLeftTopRightPossibilities<Math.pow(2,numberOfBitsIntoTheMaskBottomLeftTopRight);counterBottomLeftTopRightPossibilities++)
			{
				long currentPossibilitiesMaskForBottomLeftTopRightPossibilities=0;
				long magicDestinationWithMostZero=0;
				
				// we set top left bottom right diagonal possibilities
				for(int counterBitsIntoTheMaskBottomLeftTopRight=0;counterBitsIntoTheMaskBottomLeftTopRight<numberOfBitsIntoTheMaskBottomLeftTopRight;counterBitsIntoTheMaskBottomLeftTopRight++)
				{
					// we get the current bit for the current possibilities
					long currentBitOnFirstPosition=(counterBottomLeftTopRightPossibilities&(1L<<counterBitsIntoTheMaskBottomLeftTopRight))>>(counterBitsIntoTheMaskBottomLeftTopRight);
					currentPossibilitiesMaskForBottomLeftTopRightPossibilities|=currentBitOnFirstPosition<<horizontalBottomLeftIndex+verticalBottomLeftIndex*NUMBER_OF_SQUARES_PER_LINE-((counterBitsIntoTheMaskBottomLeftTopRight+1)*(NUMBER_OF_SQUARES_PER_LINE-1));
					
					// here we compute magic destination
					long currentBitOnFirstPositionForMag=(counterBottomLeftTopRightPossibilities&(1L<<counterBitsIntoTheMaskBottomLeftTopRight))>>(counterBitsIntoTheMaskBottomLeftTopRight);
					magicDestinationWithMostZero|=currentBitOnFirstPositionForMag<<counterBitsIntoTheMaskBottomLeftTopRight;
				}
				
				// now we have to compute moves possible for the bottom left top right diagonal to bottom left
				long movesResult=0;
				int counterVertical=(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE)+1;
				int counterHorizontal=(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE)-1;
				for(;counterVertical<NUMBER_OF_SQUARES_PER_LINE&&counterHorizontal>=0;counterHorizontal--,counterVertical++)
				{
					movesResult|=1L<<(counterHorizontal+counterVertical*NUMBER_OF_SQUARES_PER_LINE);
					if((currentPossibilitiesMaskForBottomLeftTopRightPossibilities&1L<<(counterHorizontal+counterVertical*NUMBER_OF_SQUARES_PER_LINE))!=0)
						break;
					
				}
				
				// to top right
				counterVertical=(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE)-1;
				counterHorizontal=(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE)+1;
				for(;counterVertical>=0&&counterHorizontal<NUMBER_OF_SQUARES_PER_LINE;counterHorizontal++,counterVertical--)
				{
					movesResult|=1L<<(counterHorizontal+counterVertical*NUMBER_OF_SQUARES_PER_LINE);
					if((currentPossibilitiesMaskForBottomLeftTopRightPossibilities&1L<<(counterHorizontal+counterVertical*NUMBER_OF_SQUARES_PER_LINE))!=0)
						break;
				}
				
				// we set the moves
				matrixDiagonalBottomLeftTopRightMoveResult[currentPieceIndex][(int)magicDestinationWithMostZero]=movesResult;
			}
		}
	}
	
	// we get all the moves for a piece at a specific square, useful for human player
	public ArrayList<Point> getListOfPossibleMovesForAPieceWithCheckCheckingWithPoint(int indexSource,int isLastMoveEnableEnPassant)
	{
		ArrayList<Integer> listIndex=getListOfPossibleMovesForAPieceWithCheckChecking(indexSource,isLastMoveEnableEnPassant,true);
		ArrayList<Point> listPoint=new ArrayList<Point>();
		for(int counterIndex=0;counterIndex<listIndex.size();counterIndex++)
			listPoint.add(new Point(listIndex.get(counterIndex)%NUMBER_OF_SQUARES_PER_LINE,listIndex.get(counterIndex)/NUMBER_OF_SQUARES_PER_LINE));
		return listPoint;
	}
	
	public long getDiagonalsMoves(int currentPieceIndex)
	{
		long magicNumber=magicForDiagonals;
		// we determine top left insertion point
		int horizontalTopLeftIndex=currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE-currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE;
		if(horizontalTopLeftIndex<0)
			horizontalTopLeftIndex=0;
		
		long moveResult=0L;
		long topLeftBottomRightPossibilities=arrayDiagonalTopLeftBottomRightMask[currentPieceIndex]&getAllPieces();
		long bigKeyValueWithoutMaskForTopLeftBottomRight=magicNumber*topLeftBottomRightPossibilities;
		long bigKeyValueWithtMaskForTopLeftBottomRight=magicDestinationMask&bigKeyValueWithoutMaskForTopLeftBottomRight;
		long keyForTopLeftBottomRight=(bigKeyValueWithtMaskForTopLeftBottomRight)>>(NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))+horizontalTopLeftIndex+1;
		moveResult=matrixDiagonalTopLeftBottomRightMoveResult[currentPieceIndex][(int)keyForTopLeftBottomRight];
		
		// we determine bottom left insertion point
		int horizontalBottomLeftIndex=currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE-(NUMBER_OF_SQUARES_PER_LINE-currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE-1);
		if(horizontalBottomLeftIndex<0)
			horizontalBottomLeftIndex=0;
		
		long possibilitiesForBottomLeftTopRightPossibilities=arrayDiagonalBottomLeftTopRightMask[currentPieceIndex]&getAllPieces();
		long bigKeyValueWithoutMaskForBottomLeftTopRight=magicNumber*possibilitiesForBottomLeftTopRightPossibilities;
		long bigKeyValueWithtMaskForBottomLeftTopRight=magicDestinationMask&bigKeyValueWithoutMaskForBottomLeftTopRight;
		long keyForBottomLeftTopRight=(bigKeyValueWithtMaskForBottomLeftTopRight)>>(NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))+horizontalBottomLeftIndex+1;
		
		moveResult|=matrixDiagonalBottomLeftTopRightMoveResult[currentPieceIndex][(int)keyForBottomLeftTopRight];
		return moveResult&~getCurrentPieces();
	}
	
	public long getDiagonalsMovesWithoutColor(int currentPieceIndex,long allPieces)
	{
		return matrixDiagonalTopLeftBottomRightMoveResult[currentPieceIndex][(int)((magicDestinationMask&(magicForDiagonals*(arrayDiagonalTopLeftBottomRightMask[currentPieceIndex]&allPieces)))>>(NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))+arrayTopLeft[currentPieceIndex]+1)]|matrixDiagonalBottomLeftTopRightMoveResult[currentPieceIndex][(int)((magicDestinationMask&(magicForDiagonals*(arrayDiagonalBottomLeftTopRightMask[currentPieceIndex]&allPieces)))>>(NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))+arrayBottomLeft[currentPieceIndex]+1)];
	}
	
	public long getLinesMovesWithoutColor(int currentPieceIndex,long allPieces)
	{
		return matrixLineVerticalMoveResult[currentPieceIndex][(int)((magicDestinationMask&(arrayMagicNumberForVerticalLines[currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE]*(arrayVerticalLineMask[currentPieceIndex]&allPieces)))>>(NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))+1)]|matrixLineHorizontalMoveResult[currentPieceIndex][(int)((arrayHorizontalLineMask[currentPieceIndex]&allPieces)>>(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE)*NUMBER_OF_SQUARES_PER_LINE+1)];
	}
	
	public long getLinesMoves(int currentPieceIndex)
	{
		long magicNumber=arrayMagicNumberForVerticalLines[currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE];
		
		// first of all we get the mask for the vertical line
		long moveResult=0L;
		long verticalPossibilities=arrayVerticalLineMask[currentPieceIndex]&getAllPieces();
		long bigKeyValueWithoutMaskForVertical=magicNumber*verticalPossibilities;
		long bigKeyValueWithMaskForVertical=magicDestinationMask&bigKeyValueWithoutMaskForVertical;
		long keyForVerticalLines=(bigKeyValueWithMaskForVertical)>>(NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))+1;
		moveResult=matrixLineVerticalMoveResult[currentPieceIndex][(int)keyForVerticalLines];
		
		long horizontalPossibilities=arrayHorizontalLineMask[currentPieceIndex]&getAllPieces();
		long keyForHorizontalLines=horizontalPossibilities>>(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE)*NUMBER_OF_SQUARES_PER_LINE+1;
		moveResult|=matrixLineHorizontalMoveResult[currentPieceIndex][(int)keyForHorizontalLines];
		return moveResult&~getCurrentPieces();
	}
	
	public long getWhitePawnMoves(int indexSource,long blackPieces,long allPieces)
	{
		long movesPossibilites=0;
		if((allPieces&1L<<indexSource-NUMBER_OF_SQUARES_PER_LINE)==0)
		{
			movesPossibilites|=1L<<indexSource-NUMBER_OF_SQUARES_PER_LINE;
			if(indexSource/NUMBER_OF_SQUARES_PER_LINE==firstLeftWhitePawnsInitialPosition.y&&(allPieces&1L<<indexSource-2*NUMBER_OF_SQUARES_PER_LINE)==0)
				movesPossibilites|=1L<<indexSource-2*NUMBER_OF_SQUARES_PER_LINE;
		}
		if((indexSource%NUMBER_OF_SQUARES_PER_LINE>0)&&(blackPieces&(1L<<(indexSource-NUMBER_OF_SQUARES_PER_LINE-1)))!=0)
			movesPossibilites|=1L<<indexSource-NUMBER_OF_SQUARES_PER_LINE-1;
		if((indexSource%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1)&&(blackPieces&(1L<<(indexSource-NUMBER_OF_SQUARES_PER_LINE+1)))!=0)
			movesPossibilites|=1L<<indexSource-NUMBER_OF_SQUARES_PER_LINE+1;
		return movesPossibilites;
	}
	
	public long getBlackPawnMoves(int indexSource,long whitePieces,long allPieces)
	{
		long movesPossibilites=0;
		if((allPieces&1L<<indexSource+NUMBER_OF_SQUARES_PER_LINE)==0)
		{
			movesPossibilites|=1L<<indexSource+NUMBER_OF_SQUARES_PER_LINE;
			if(indexSource/NUMBER_OF_SQUARES_PER_LINE==firstLeftBlackPawnsInitialPosition.y&&(allPieces&1L<<indexSource+2*NUMBER_OF_SQUARES_PER_LINE)==0)
				movesPossibilites|=1L<<indexSource+2*NUMBER_OF_SQUARES_PER_LINE;
		}
		if((indexSource%NUMBER_OF_SQUARES_PER_LINE>0)&&(whitePieces&(1L<<(indexSource+NUMBER_OF_SQUARES_PER_LINE-1)))!=0)
			movesPossibilites|=1L<<indexSource+NUMBER_OF_SQUARES_PER_LINE-1;
		if((indexSource%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1)&&(whitePieces&(1L<<(indexSource+NUMBER_OF_SQUARES_PER_LINE+1)))!=0)
			movesPossibilites|=1L<<indexSource+NUMBER_OF_SQUARES_PER_LINE+1;
		return movesPossibilites;
	}
	
	public ArrayList<Integer> getListOfPossibleMovesForAPieceWithCheckChecking(int indexSource,int isLastMoveEnableEnPassant,boolean withCastling)
	{
		boolean[] isSpecial=new boolean[1];
		long allPieces=getAllPieces();
		long whitePieces=getWhitePieces();
		long blackPieces=getBlackPieces();
		
		// we put each move possible into an arrayList and return it
		long movePossibilities=0;
		long currentKing=blackKing;
		if(currentTurn==white)
			currentKing=whiteKing;
		int currentPieceType=getPieceTypeAtThisIndexWithCurrentColor(indexSource);
		switch(currentPieceType)
		// get moves and delete moves that put or let king in check
		{
		case pawnId:
			if(currentTurn==white)
			{
				movePossibilities=getWhitePawnMoves(indexSource,blackPieces,allPieces);
				if(isLastMoveEnableEnPassant!=-1&&indexSource/NUMBER_OF_SQUARES_PER_LINE==firstLeftBlackPawnsInitialPosition.y+2&&(isLastMoveEnableEnPassant==indexSource+1||isLastMoveEnableEnPassant==indexSource-1))
					movePossibilities|=1L<<isLastMoveEnableEnPassant-NUMBER_OF_SQUARES_PER_LINE;
			}
			if(currentTurn==black)
			{
				movePossibilities=getBlackPawnMoves(indexSource,whitePieces,allPieces);
				if(isLastMoveEnableEnPassant!=-1&&indexSource/NUMBER_OF_SQUARES_PER_LINE==firstLeftWhitePawnsInitialPosition.y-2&&(isLastMoveEnableEnPassant==indexSource+1||isLastMoveEnableEnPassant==indexSource-1))
					movePossibilities|=1L<<isLastMoveEnableEnPassant+NUMBER_OF_SQUARES_PER_LINE;
			}
			for(int counterBits=0;counterBits<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallyDeleted=makeMoveWithTwoIndexForCurrentTurnWithPieceId(pawnId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(pawnId,counterBits,indexSource,pieceEventuallyDeleted,isSpecial[0]);
				}
			break;
		case kingId:
			if(withCastling==true)
				movePossibilities=getMovesForKingWithCheckChecking(indexSource,whitePieces,blackPieces,allPieces);
			else
				movePossibilities=getMovesForKingWithCheckCheckingWithoutCastling(indexSource,whitePieces,blackPieces,allPieces);
			break;
		case rookId:
			movePossibilities=getLinesMoves(indexSource);
			for(int counterBits=0;counterBits<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallyDeleted=makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,counterBits,indexSource,pieceEventuallyDeleted,isSpecial[0]);
				}
			break;
		case bishopId:
			movePossibilities=getDiagonalsMoves(indexSource);
			for(int counterBits=0;counterBits<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallyDeleted=makeMoveWithTwoIndexForCurrentTurnWithPieceId(bishopId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(bishopId,counterBits,indexSource,pieceEventuallyDeleted,isSpecial[0]);
				}
			break;
		case queenId:
			movePossibilities=getDiagonalsMoves(indexSource);
			movePossibilities|=getLinesMoves(indexSource);
			for(int counterBits=0;counterBits<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallyDeleted=makeMoveWithTwoIndexForCurrentTurnWithPieceId(queenId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(queenId,counterBits,indexSource,pieceEventuallyDeleted,isSpecial[0]);
				}
			break;
		case knightId:
			movePossibilities=arrayKnightMoves[indexSource];
			movePossibilities&=~getCurrentPieces();
			for(int counterBits=0;counterBits<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;counterBits++)
				if((movePossibilities&1L<<counterBits)!=0)
				{
					int pieceEventuallyDeleted=makeMoveWithTwoIndexForCurrentTurnWithPieceId(knightId,indexSource,counterBits,isSpecial);
					if(isThisSquareAttacked(Long.numberOfTrailingZeros(currentKing),whitePieces,blackPieces,allPieces)==true)
						movePossibilities&=~(1L<<counterBits);
					unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(knightId,counterBits,indexSource,pieceEventuallyDeleted,isSpecial[0]);
				}
			break;
		default:
		}
		ArrayList<Integer> arrayListPoint=new ArrayList<Integer>();
		for(int counterBits=0;counterBits<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;counterBits++)
			if((movePossibilities&1L<<counterBits)!=0)
				if(isThisMoveHasToBeRemovedDueToThreeRepetitionsLaw(currentTurn,currentPieceType,indexSource,counterBits)==false)
					arrayListPoint.add(counterBits);
		return arrayListPoint;
	}
	
	public int isItDoublePawnMoveForEnPassant(Point oldSelectedSquare,Point newSelectedSquare)
	{
		switch(currentTurn)
		{
		case white:
			if(oldSelectedSquare.y==firstLeftWhitePawnsInitialPosition.y&&newSelectedSquare.y==firstLeftWhitePawnsInitialPosition.y-2&&getPieceTypeAtThisIndexWithCurrentColor(newSelectedSquare.x+newSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE)==pawnId)
				return newSelectedSquare.x+newSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE;
			break;
		case black:
			if(oldSelectedSquare.y==firstLeftBlackPawnsInitialPosition.y&&newSelectedSquare.y==firstLeftBlackPawnsInitialPosition.y+2&&getPieceTypeAtThisIndexWithCurrentColor(newSelectedSquare.x+newSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE)==pawnId) // ici
				return newSelectedSquare.x+newSelectedSquare.y*NUMBER_OF_SQUARES_PER_LINE;
			break;
		default:
			;
		}
		return -1;
	}
	
	public ArrayList<Point> makeCastling(String castlingDescription)
	{
		boolean[] isSpecial=new boolean[1];
		ArrayList<Point> arrayConcernedSquares=new ArrayList<Point>();
		if(currentTurn==white)
		{
			if(castlingDescription.equals(kingSideCastlingExplicit)==true||castlingDescription.equals(kingSideCastlingStandard)==true)
			{
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,whiteRookKingCastlingDestination,isSpecial);
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,whiteKingKingCastlingDestination,isSpecial);
				arrayConcernedSquares.add(new Point(whiteKingKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,whiteKingKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add(new Point(whiteRookKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,whiteRookKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add((Point)rightWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteRightRookHasMoved=true;
				isWhiteKingHasMoved=true;
			}
			else
			{
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,whiteRookQueenCastlingDestination,isSpecial);
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,whiteKingQueenCastlingDestination,isSpecial);
				arrayConcernedSquares.add(new Point(whiteKingQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,whiteKingQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add(new Point(whiteRookQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,whiteRookQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add((Point)leftWhiteRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)whiteKingInitialPosition.clone());
				isWhiteLeftRookHasMoved=true;
				isWhiteKingHasMoved=true;
			}
		}
		else
		{
			if(castlingDescription.equals(kingSideCastlingExplicit)==true||castlingDescription.equals(kingSideCastlingStandard)==true)
			{
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,blackRookKingCastlingDestination,isSpecial);
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,blackKingKingCastlingDestination,isSpecial);
				arrayConcernedSquares.add(new Point(blackKingKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,blackKingKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add(new Point(blackRookKingCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,blackRookKingCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add((Point)rightBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackRightRookHasMoved=true;
				isBlackKingHasMoved=true;
			}
			else
			{
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(rookId,leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,blackRookQueenCastlingDestination,isSpecial);
				makeMoveWithTwoIndexForCurrentTurnWithPieceId(kingId,blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE,blackKingQueenCastlingDestination,isSpecial);
				arrayConcernedSquares.add(new Point(blackKingQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,blackKingQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add(new Point(blackRookQueenCastlingDestination%NUMBER_OF_SQUARES_PER_LINE,blackRookQueenCastlingDestination/NUMBER_OF_SQUARES_PER_LINE));
				arrayConcernedSquares.add((Point)leftBlackRookInitialPosition.clone());
				arrayConcernedSquares.add((Point)blackKingInitialPosition.clone());
				isBlackLeftRookHasMoved=true;
				isBlackKingHasMoved=true;
			}
		}
		return arrayConcernedSquares;
	}
	
	public void SetToLastTurnBeforeCheckAndMate(boolean isItPairMovement)
	{
		if(isItPairMovement==true)
			currentTurn=white;
		else
			currentTurn=black;
	}
	
	public void SetCounterOfMoves(int counterOfMovesParameter)
	{
		counterMoveFinished=counterOfMovesParameter;
	}
	
	// we unmake a move and restore the piece which has eventually been deleted
	public void unmakeMoveForWithoutRefreshRehearsalHistoric(Point sourceSquare,Point destinationSquare,int pieceDeleted,boolean isSpecial)
	{
		int currentColor=GetThePieceColorAtThisIndex(destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE);
		int pieceId=getPieceTypeAtThisIndexAndWithThisColor(currentColor,destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE);
		boolean[] arrayIsSpecial=new boolean[1];
		makeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceId,destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE,sourceSquare.x+sourceSquare.y*NUMBER_OF_SQUARES_PER_LINE,arrayIsSpecial);
		if(isSpecial==true)
		{
			switch(pieceId)
			{
			case pawnId:
				if(currentColor==white)
				{
					blackPawns|=1L<<(destinationSquare.x+(destinationSquare.y+1)*NUMBER_OF_SQUARES_PER_LINE);
					blackPawns&=~(1L<<(destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE));
					pieceDeleted=noPieceId;
				}
				if(currentColor==black)
				{
					whitePawns|=1L<<(destinationSquare.x+(destinationSquare.y-1)*NUMBER_OF_SQUARES_PER_LINE);
					whitePawns&=~(1L<<(destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE));
					pieceDeleted=noPieceId;
				}
				break;
			case queenId:
				if(currentColor==white)
				{
					whiteQueens&=~(1L<<sourceSquare.x+sourceSquare.y*NUMBER_OF_SQUARES_PER_LINE);
					whitePawns|=1L<<sourceSquare.x+sourceSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				}
				else if(currentColor==black)
				{
					blackQueens&=~(1L<<sourceSquare.x+sourceSquare.y*NUMBER_OF_SQUARES_PER_LINE);
					blackPawns|=1L<<sourceSquare.x+sourceSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				}
				break;
			case rookId:
				if(currentColor==white)
				{
					if(sourceSquare.x==rightWhiteRookInitialPosition.x&&sourceSquare.y==rightWhiteRookInitialPosition.y)
						isWhiteRightRookHasMoved=false;
					else if(sourceSquare.x==leftWhiteRookInitialPosition.x&&sourceSquare.y==leftWhiteRookInitialPosition.y)
						isWhiteLeftRookHasMoved=false;
				}
				else if(currentColor==black)
				{
					if(sourceSquare.x==rightBlackRookInitialPosition.x&&sourceSquare.y==rightBlackRookInitialPosition.y)
						isBlackRightRookHasMoved=false;
					else if(sourceSquare.x==leftBlackRookInitialPosition.x&&sourceSquare.y==leftBlackRookInitialPosition.y)
						isBlackLeftRookHasMoved=false;
				}
				break;
			case kingId:
				if(currentColor==white&&sourceSquare.x==whiteKingInitialPosition.x&&sourceSquare.y==whiteKingInitialPosition.y)
					isWhiteKingHasMoved=false;
				else if(currentColor==black&&sourceSquare.x==blackKingInitialPosition.x&&sourceSquare.y==blackKingInitialPosition.y)
					isBlackKingHasMoved=false;
			default:
				;
			}
		}
		if(pieceDeleted!=noPieceId)
		{
			if(GiveMeThePieceColorOnThisSquare(sourceSquare)==black)
			{
				
				if(pieceDeleted==pawnId)
					whitePawns|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==knightId)
					whiteKnights|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==kingId)
					whiteKing|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==rookId)
					whiteRooks|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==bishopId)
					whiteBishops|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==queenId)
					whiteQueens|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
			}
			if(GiveMeThePieceColorOnThisSquare(sourceSquare)==white)
			{
				if(pieceDeleted==pawnId)
					blackPawns|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==knightId)
					blackKnights|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==kingId)
					blackKing|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==rookId)
					blackRooks|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==bishopId)
					blackBishops|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
				else if(pieceDeleted==queenId)
					blackQueens|=1L<<destinationSquare.x+destinationSquare.y*NUMBER_OF_SQUARES_PER_LINE;
			}
		}
	}
	
	public void UnmakeMove(Point sourceSquare,Point destinationSquare,int pieceDeleted,boolean isSpecial)
	{
		PiecesSituation piecesSituation=new PiecesSituation(whiteKnights,whiteBishops,whiteQueens,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueens,blackKing,blackPawns,blackRooks);
		for(int counterSituations=0;counterSituations<listPiecesSituation.size();counterSituations++)
			if(listPiecesSituation.get(counterSituations).equal(piecesSituation)==true)
			{
				listPiecesSituationOccurrences.set(counterSituations,listPiecesSituationOccurrences.get(counterSituations)-1);
				if(listPiecesSituationOccurrences.get(counterSituations)==0)
				{
					listPiecesSituationOccurrences.remove(counterSituations);
					listPiecesSituation.remove(counterSituations);
				}
				break;
			}
		unmakeMoveForWithoutRefreshRehearsalHistoric(sourceSquare,destinationSquare,pieceDeleted,isSpecial);
	}
	
	int getPieceIdWithString(String pieceTypeEventuallyDeletedString)
	{
		if(pieceTypeEventuallyDeletedString.equals(new String("pawn"))==true)
			return pawnId;
		else if(pieceTypeEventuallyDeletedString.equals(new String("knight"))==true)
			return knightId;
		else if(pieceTypeEventuallyDeletedString.equals(new String("king"))==true)
			return kingId;
		else if(pieceTypeEventuallyDeletedString.equals(new String("rook"))==true)
			return rookId;
		else if(pieceTypeEventuallyDeletedString.equals(new String("bishop"))==true)
			return bishopId;
		else if(pieceTypeEventuallyDeletedString.equals(new String("queen"))==true)
			return queenId;
		return noPieceId;
	}
	
	// calculate a point coordinate with the string coordinates given in parameter
	Point GetCorrespondingSquare(String squareCoordinates)
	{
		Point pointCoordinate=new Point(-1,-1);
		Character[] heightFirstLettersOfTheAlphabet=
		{'a','b','c','d','e','f','g','h'};
		for(int counterLetter=0;counterLetter<heightFirstLettersOfTheAlphabet.length;counterLetter++)
			if(squareCoordinates.charAt(0)==heightFirstLettersOfTheAlphabet[counterLetter])
				pointCoordinate.x=counterLetter;
		pointCoordinate.y=NUMBER_OF_SQUARES_PER_LINE-Integer.decode(squareCoordinates.substring(1,2));
		return pointCoordinate;
	}
	
	// check if at least one move of the current player is possible
	public int IfGameHasEndedGiveMeTheWinner(int isLastMoveEnableEnPassant)
	{
		long blackAllPieces=getBlackPieces();
		long whiteAllPieces=getWhitePieces();
		long allPieces=blackAllPieces|whiteAllPieces;
		switch(currentTurn)
		{
		case white:
			int indexWhitePiece;
			int totalMovesForWhitePlayer=0;
			for(;;)
			{
				indexWhitePiece=Long.numberOfTrailingZeros(whiteAllPieces);
				if(indexWhitePiece!=Long.SIZE)
				{
					whiteAllPieces&=~(1L<<indexWhitePiece);
					ArrayList<Integer> listPoint=getListOfPossibleMovesForAPieceWithCheckChecking(indexWhitePiece,isLastMoveEnableEnPassant,true);
					totalMovesForWhitePlayer+=listPoint.size();
				}
				else
					break;
			}
			if(totalMovesForWhitePlayer==0)
			{
				if(isThisSquareAttacked(Long.numberOfTrailingZeros(whiteKing),whiteAllPieces,blackAllPieces,allPieces)==false)
					return whiteIsPat;
				return black;
			}
			break;
		case black:
			int indexBlackPiece;
			int totalMovesForBlackPlayer=0;
			for(;;)
			{
				indexBlackPiece=Long.numberOfTrailingZeros(blackAllPieces);
				if(indexBlackPiece!=Long.SIZE)
				{
					blackAllPieces&=~(1L<<indexBlackPiece);
					ArrayList<Integer> listPoint=getListOfPossibleMovesForAPieceWithCheckChecking(indexBlackPiece,isLastMoveEnableEnPassant,true);
					totalMovesForBlackPlayer+=listPoint.size();
				}
				else
					break;
			}
			if(totalMovesForBlackPlayer==0)
			{
				if(isThisSquareAttacked(Long.numberOfTrailingZeros(blackKing),whiteAllPieces,blackAllPieces,allPieces)==false)
					return blackIsPat;
				return white;
			}
			break;
		}
		return 0;
	}
	
	public void EndTheGame()
	{
		currentTurn=noCurrentGame;
	}
	
	public Date GetBeginningDate()
	{
		return gameBeginningDate;
	}
	
	public int getCounterOfMoves()
	{
		return counterMoveFinished;
	}
	
	public ChessRuler(int currentTurnParameter,int beginIndexParameter,int endIndexParameter,int depthParameter,long whiteKnightsParameter,long whiteBishopsParameter,long whiteQueensParameter,long whiteKingParameter,long whitePawnsParameter,long whiteRooksParameter,long blackKnightsParameter,long blackBishopsParameter,long blackQueensParameter,long blackKingParameter,long blackPawnsParameter,long blackRooksParameter,int isLastMoveEnableEnPassantParameter)
	{
		currentTurnMultithreading=currentTurnParameter;
		depthForThreadComputing=depthParameter;
		beginIndexMultithreading=beginIndexParameter;
		endIndexMultithreading=endIndexParameter;
		whiteKnights=whiteKnightsParameter;
		whiteBishops=whiteBishopsParameter;
		whiteQueens=whiteQueensParameter;
		whiteKing=whiteKingParameter;
		whitePawns=whitePawnsParameter;
		whiteRooks=whiteRooksParameter;
		blackKnights=blackKnightsParameter;
		blackBishops=blackBishopsParameter;
		blackQueens=blackQueensParameter;
		blackKing=blackKingParameter;
		blackPawns=blackPawnsParameter;
		blackRooks=blackRooksParameter;
		isWhiteKingHasMoved=true;
		isBlackKingHasMoved=true;
		isBlackLeftRookHasMoved=true;
		isBlackRightRookHasMoved=true;
		isWhiteLeftRookHasMoved=true;
		isWhiteRightRookHasMoved=true;
	}
	
	@Override
	public void run()
	{
		currentTurn=currentTurnMultithreading;
		counterNodes=0;
		for(int counterMoves=beginIndexMultithreading;counterMoves<endIndexMultithreading;counterMoves++)
		{
			boolean isSpecial[]=new boolean[1];
			int pieceIdSource=getPieceTypeAtThisIndexAndWithThisColor(currentTurnMultithreading,listSourceForMultithreading.get(counterMoves));
			int pieceEventualyDeleted=makeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceIdSource,listSourceForMultithreading.get(counterMoves),listDestinationForMultithreading.get(counterMoves),isSpecial);
			int alpha=-infinite;
			int beta=-alpha;
			currentTurn=-currentTurn;
			int returnValue;
			if(depthForThreadComputing!=0)
				returnValue=minMax(-currentTurn,depthForThreadComputing,EvaluateCurrentSituation(),alpha,beta);
			else
				returnValue=EvaluateCurrentSituation();
			currentTurn=-currentTurn;
			listValuesForMultithreading[counterMoves]=returnValue;
			unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceIdSource,listDestinationForMultithreading.get(counterMoves),listSourceForMultithreading.get(counterMoves),pieceEventualyDeleted,isSpecial[0]);
		}
	}
	
	private int playComputerAtOnlyASpecificLevel(int maximumDepth,ArrayList<Integer> listPointSource,ArrayList<Integer> listPointDestination,int isLastMoveEnableEnPassant) throws InterruptedException
	{
		listPointSource.clear();
		listPointDestination.clear();
		listValuesForMultithreading=new int[200];
		int numberOfCores=Runtime.getRuntime().availableProcessors();
		if(listSourceForMultithreading.size()<numberOfCores)
			numberOfCores=listSourceForMultithreading.size();
		int remainPossibleMoves=listSourceForMultithreading.size();
		ArrayList<ChessRuler> listChessRuler=new ArrayList<ChessRuler>();
		ArrayList<Thread> listThread=new ArrayList<Thread>();
		for(int counterCore=numberOfCores;counterCore>0;counterCore--)
		{
			float currentMovesFloat=remainPossibleMoves/counterCore;
			int currentMoves=(int)currentMovesFloat;
			if(currentMovesFloat>0)
			{
				if(currentMoves==0)
					currentMoves=remainPossibleMoves;
			}
			else
				break;
			remainPossibleMoves=remainPossibleMoves-currentMoves;
			ChessRuler instanceChessRulesMan=new ChessRuler(currentTurn,remainPossibleMoves,remainPossibleMoves+currentMoves,maximumDepth-1,whiteKnights,whiteBishops,whiteQueens,whiteKing,whitePawns,whiteRooks,blackKnights,blackBishops,blackQueens,blackKing,blackPawns,blackRooks,isLastMoveEnableEnPassant);
			listChessRuler.add(instanceChessRulesMan);
			Thread thread=new Thread(instanceChessRulesMan);
			listThread.add(thread);
			thread.start(); // real computing is starting
		}
		
		// we wait all threads done their work and count the number of evaluations
		int currentTotalNodesCounter=0;
		for(int counterThread=0;counterThread<numberOfCores;counterThread++)
		{
			listThread.get(counterThread).join();
			currentTotalNodesCounter+=listChessRuler.get(counterThread).counterNodes;
		}
		
		// we find the best value
		int bestEvaluation;
		if(currentTurn==white)
		{
			bestEvaluation=-infinite;
			for(int counterSource=0;counterSource<listSourceForMultithreading.size();counterSource++)
				if(bestEvaluation<listValuesForMultithreading[counterSource])
					bestEvaluation=listValuesForMultithreading[counterSource];
		}
		else
		{
			bestEvaluation=infinite;
			for(int counterSource=0;counterSource<listSourceForMultithreading.size();counterSource++)
				if(bestEvaluation>listValuesForMultithreading[counterSource])
					bestEvaluation=listValuesForMultithreading[counterSource];
		}
		for(int counterSource=0;counterSource<listSourceForMultithreading.size();counterSource++)
			if(listValuesForMultithreading[counterSource]==bestEvaluation)
			{
				listPointSource.add(listSourceForMultithreading.get(counterSource));
				listPointDestination.add(listDestinationForMultithreading.get(counterSource));
			}
		return currentTotalNodesCounter;
	}
	
	public int makeThisMoveAndGetDescriptionWithMoveDescription(long currentPieces,String moveDescription,ArrayList<String> arrayMoveDescription,boolean[] arrayIsSpecial,int isLastMoveEnableEnPassant)
	{
		// first of all we get destination
		String moveDescriptionWithoutPieceIdentifier=moveDescription.replaceAll("N","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll(promotionStandard,"");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("R","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("B","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("Q","");
		moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll("K","");
		String stringDestination=moveDescriptionWithoutPieceIdentifier.substring(moveDescriptionWithoutPieceIdentifier.length()-2,moveDescriptionWithoutPieceIdentifier.length());
		Point pointDestination=null;
		if(stringDestination.equals(enPassantReducedForAnalysis)==true)
		{
			moveDescriptionWithoutPieceIdentifier=moveDescriptionWithoutPieceIdentifier.replaceAll(enPassantReducedForAnalysis,"");
			stringDestination=moveDescriptionWithoutPieceIdentifier.substring(moveDescriptionWithoutPieceIdentifier.length()-2,moveDescriptionWithoutPieceIdentifier.length());
			pointDestination=GetCorrespondingSquare(stringDestination);
			if(currentTurn==white)
				isLastMoveEnableEnPassant=pointDestination.x+(pointDestination.y+1)*NUMBER_OF_SQUARES_PER_LINE;
			if(currentTurn==black)
				isLastMoveEnableEnPassant=pointDestination.x+(pointDestination.y-1)*NUMBER_OF_SQUARES_PER_LINE;
		}
		else
			pointDestination=GetCorrespondingSquare(stringDestination);
		ArrayList<Integer> arrayListPossibleMoves=new ArrayList<Integer>();
		int numberOfPossiblePieces=0;
		ArrayList<Integer> arrayListPossibleSourcePieces=new ArrayList<Integer>();
		for(int counterBits=0;counterBits<NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE;counterBits++)
		{
			arrayListPossibleMoves.clear();
			if((currentPieces&1L<<counterBits)!=0)
			{
				arrayListPossibleMoves=getListOfPossibleMovesForAPieceWithCheckChecking(counterBits,isLastMoveEnableEnPassant,true);
				int destinationOfCurrentMove=0;
				for(int counterMovesFirstLevel=0;counterMovesFirstLevel<arrayListPossibleMoves.size();counterMovesFirstLevel++)
				{
					destinationOfCurrentMove=arrayListPossibleMoves.get(counterMovesFirstLevel);
					if(destinationOfCurrentMove%NUMBER_OF_SQUARES_PER_LINE==pointDestination.x&&destinationOfCurrentMove/NUMBER_OF_SQUARES_PER_LINE==pointDestination.y)
					{
						numberOfPossiblePieces++;
						arrayListPossibleSourcePieces.add(counterBits);
					}
				}
			}
		}
		
		// here multiple piece can go do the destination, we have to delete the wrong pieces
		if(numberOfPossiblePieces>1)
		{
			// we have to know what piece is concerned
			ArrayList<Point> ArrayListPossibleSourceWithColumnFilter=new ArrayList<Point>();
			for(int counterPossiblePieceForColumnFilter=0;counterPossiblePieceForColumnFilter<arrayListPossibleSourcePieces.size();counterPossiblePieceForColumnFilter++)
			{
				Point currentPieceSourceForColumnFilter=new Point(arrayListPossibleSourcePieces.get(counterPossiblePieceForColumnFilter)%NUMBER_OF_SQUARES_PER_LINE,arrayListPossibleSourcePieces.get(counterPossiblePieceForColumnFilter)/NUMBER_OF_SQUARES_PER_LINE);
				if(moveDescriptionWithoutPieceIdentifier.charAt(0)<'a'||moveDescriptionWithoutPieceIdentifier.charAt(0)>'h') // we
				// do coherence check on the file content
				{
					javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"A bad character has been found : ["+moveDescriptionWithoutPieceIdentifier.charAt(0)+"].\n"+"It should be between a and h.\n"+"Move description : "+moveDescription);
					return -1;
				}
				if(currentPieceSourceForColumnFilter.x==moveDescriptionWithoutPieceIdentifier.charAt(0)-'a')
					ArrayListPossibleSourceWithColumnFilter.add((Point)currentPieceSourceForColumnFilter.clone());
			}
			if(ArrayListPossibleSourceWithColumnFilter.size()>1) // column filter is not enough, we use line filter
			{
				ArrayList<Point> ArrayListPossibleSourceWithColumnAndLineFilter=new ArrayList<Point>();
				for(int counterPossiblePieceForLineFilter=0;counterPossiblePieceForLineFilter<ArrayListPossibleSourceWithColumnFilter.size();counterPossiblePieceForLineFilter++)
				{
					Point currentPieceSourceForColumnAndLineFilter=ArrayListPossibleSourceWithColumnFilter.get(counterPossiblePieceForLineFilter);
					if(moveDescriptionWithoutPieceIdentifier.charAt(1)<'1'||moveDescriptionWithoutPieceIdentifier.charAt(1)>'8') // we do coherence check on the file content for line filter
					{
						javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"A bad character has been found : "+moveDescriptionWithoutPieceIdentifier.charAt(1)+".\n"+"It should be between 1 and 8.\n"+"Move description : "+moveDescription);
						return -1;
					}
					if(NUMBER_OF_SQUARES_PER_LINE-currentPieceSourceForColumnAndLineFilter.y-1==moveDescriptionWithoutPieceIdentifier.charAt(1)-'1')
						ArrayListPossibleSourceWithColumnAndLineFilter.add((Point)currentPieceSourceForColumnAndLineFilter.clone()); // we have found the piece according to line filter
				}
				if(ArrayListPossibleSourceWithColumnAndLineFilter.size()==0) // error
				// case we should have one and only one piece
				{
					javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify line of a begining move in an ambiguous case, no piece found.\n"+"Move description : "+moveDescription);
					return -1;
				}
				else if(ArrayListPossibleSourceWithColumnAndLineFilter.size()>1) // error case we should have one and only one piece
				{
					javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify line of a begining move in an ambiguous case, too many pieces found.\n"+"Move description : "+moveDescription);
					return -1;
				}
				else if(ArrayListPossibleSourceWithColumnAndLineFilter.size()==1)
				{
					makeThisMoveAndGetDescription(ArrayListPossibleSourceWithColumnAndLineFilter.get(0),pointDestination,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant); // we have the right piece according to column and line filter
					return isItDoublePawnMoveForEnPassant(ArrayListPossibleSourceWithColumnAndLineFilter.get(0),pointDestination);
				}
			}
			else if(ArrayListPossibleSourceWithColumnFilter.size()==0)
			{
				javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify column of a begining move in an ambiguous case, no piece found.\n"+"Move description : "+moveDescription);
				return -1;
			}
			else if(ArrayListPossibleSourceWithColumnFilter.size()==1)
			{
				makeThisMoveAndGetDescription(ArrayListPossibleSourceWithColumnFilter.get(0),pointDestination,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
				return isItDoublePawnMoveForEnPassant(ArrayListPossibleSourceWithColumnFilter.get(0),pointDestination);
			}
		}
		else if(numberOfPossiblePieces==0) // case which non piece can go to the destination
		{
			javax.swing.JOptionPane.showMessageDialog(null,"Error while reading a PNG file.\n"+"Impossible to identify piece at the begining move.\n"+"Move description : "+moveDescription);
			return -1;
		}
		else
		{
			makeThisMoveAndGetDescription(new Point(arrayListPossibleSourcePieces.get(0)%NUMBER_OF_SQUARES_PER_LINE,arrayListPossibleSourcePieces.get(0)/NUMBER_OF_SQUARES_PER_LINE),pointDestination,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant); // we have directly the good piece, it's too easy
			return isItDoublePawnMoveForEnPassant(new Point(arrayListPossibleSourcePieces.get(0)%NUMBER_OF_SQUARES_PER_LINE,arrayListPossibleSourcePieces.get(0)/NUMBER_OF_SQUARES_PER_LINE),pointDestination);
		}
		return -1;
	}
	
	public int getBlackPieceTypeWithTarget(long target)
	{
		if((blackPawns&target)!=0)
			return pawnId;
		else if((blackRooks&target)!=0)
			return rookId;
		else if((blackBishops&target)!=0)
			return bishopId;
		else if((blackKnights&target)!=0)
			return knightId;
		else if((blackQueens&target)!=0)
			return queenId;
		return kingId;
	}
	
	private int minMaxForZeroDepth(int currentTurnParameter,int currentEvaluation,int alpha,int beta)
	{
		long piecesTemp;
		long target;
		long movePossibilities;
		if(-currentTurnParameter==white)
		{
			long opponentPieces=getBlackPieces();
			long ownPieces=getWhitePieces();
			long allPieces=ownPieces|opponentPieces;
			piecesTemp=whiteQueens;
			while(piecesTemp!=0)
			{
				movePossibilities=getQueensMovesWithoutColor(Long.numberOfTrailingZeros(piecesTemp),allPieces)&opponentPieces;
				while(movePossibilities!=0)
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
					if(beta<=alpha)
						return alpha;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteRooks;
			while(piecesTemp!=0)
			{
				movePossibilities=getLinesMovesWithoutColor(Long.numberOfTrailingZeros(piecesTemp),allPieces)&opponentPieces;
				while(movePossibilities!=0)
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
					if(beta<=alpha)
						return alpha;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteBishops;
			while(piecesTemp!=0)
			{
				movePossibilities=getDiagonalsMovesWithoutColor(Long.numberOfTrailingZeros(piecesTemp),allPieces)&opponentPieces;
				while(movePossibilities!=0)
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
					if(beta<=alpha)
						return alpha;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteKnights;
			while(piecesTemp!=0)
			{
				movePossibilities=arrayKnightMoves[Long.numberOfTrailingZeros(piecesTemp)]&opponentPieces;
				while(movePossibilities!=0)
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
					if(beta<=alpha)
						return alpha;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			movePossibilities=arrayKingMoves[Long.numberOfTrailingZeros(whiteKing)]&opponentPieces;
			while(movePossibilities!=0)
			{
				alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
				if(beta<=alpha)
					return alpha;
				movePossibilities&=movePossibilities-1;
			}
			
			piecesTemp=whitePawns;
			while(piecesTemp!=0)
			{
				int currentPieceIndex=Long.numberOfTrailingZeros(piecesTemp);
				// captures left
				target=1L<<(currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE-1);
				if((opponentPieces&target)!=0&&(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE>0))
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(target));
					if(beta<=alpha)
						return alpha;
				}
				
				// captures right
				target=1L<<(currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE+1);
				if((opponentPieces&target)!=0&&(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1))
				{
					alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(target));
					if(beta<=alpha)
						return alpha;
				}
				
				// pawn promotion
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE==1)
				{
					if((allPieces&1L<<currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE)==0)
					{
						// move one square forward
						alpha=Math.max(alpha,currentEvaluation-pawnId+queenId);
						if(beta<=alpha)
							return alpha;
					}
					
					// captures left
					target=1L<<(currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE-1);
					if((opponentPieces&target)!=0&&(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE>0))
					{
						alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(target)-pawnId+queenId);
						if(beta<=alpha)
							return alpha;
					}
					
					// captures right
					target=1L<<(currentPieceIndex-NUMBER_OF_SQUARES_PER_LINE+1);
					if((opponentPieces&target)!=0&&(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1))
					{
						alpha=Math.max(alpha,currentEvaluation+getBlackPieceTypeWithTarget(target)-pawnId+queenId);
						if(beta<=alpha)
							return alpha;
					}
				}
				piecesTemp&=piecesTemp-1;
			}
			return Math.max(alpha,currentEvaluation);
		}
		else
		{
			long opponentPieces=getWhitePieces();
			long ownPieces=getBlackPieces();
			long allPieces=ownPieces|opponentPieces;
			piecesTemp=blackQueens;
			while(piecesTemp!=0)
			{
				movePossibilities=getQueensMovesWithoutColor(Long.numberOfTrailingZeros(piecesTemp),allPieces)&opponentPieces;
				while(movePossibilities!=0)
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
					if(beta<=alpha)
						return beta;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=blackRooks;
			while(piecesTemp!=0)
			{
				movePossibilities=getLinesMovesWithoutColor(Long.numberOfTrailingZeros(piecesTemp),allPieces)&opponentPieces;
				while(movePossibilities!=0)
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
					if(beta<=alpha)
						return beta;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=blackBishops;
			while(piecesTemp!=0)
			{
				movePossibilities=getDiagonalsMovesWithoutColor(Long.numberOfTrailingZeros(piecesTemp),allPieces)&opponentPieces;
				while(movePossibilities!=0)
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
					if(beta<=alpha)
						return beta;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=blackKnights;
			while(piecesTemp!=0)
			{
				movePossibilities=arrayKnightMoves[Long.numberOfTrailingZeros(piecesTemp)]&opponentPieces;
				while(movePossibilities!=0)
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
					if(beta<=alpha)
						return beta;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
			}
			
			movePossibilities=arrayKingMoves[Long.numberOfTrailingZeros(blackKing)]&opponentPieces;
			while(movePossibilities!=0)
			{
				beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(1L<<Long.numberOfTrailingZeros(movePossibilities)));
				if(beta<=alpha)
					return beta;
				movePossibilities&=movePossibilities-1;
			}
			
			piecesTemp=blackPawns;
			while(piecesTemp!=0)
			{
				int currentPieceIndex=Long.numberOfTrailingZeros(piecesTemp);
				// captures left
				target=1L<<(currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE-1);
				if((opponentPieces&target)!=0&&(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE>0))
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(target));
					if(beta<=alpha)
						return beta;
				}
				
				// captures right
				target=1L<<(currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE+1);
				if((opponentPieces&target)!=0&&(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1))
				{
					beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(target));
					if(beta<=alpha)
						return beta;
				}
				
				// queen promotion
				if(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE==NUMBER_OF_SQUARES_PER_LINE-2)
				{
					// move on square forward
					if((allPieces&1L<<currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE)==0)
					{
						beta=Math.min(beta,currentEvaluation+pawnId-queenId);
						if(beta<=alpha)
							return beta;
					}
					
					// captures left
					target=1L<<(currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE-1);
					if((opponentPieces&target)!=0&&currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE>0)
					{
						beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(target)+pawnId-queenId);
						if(beta<=alpha)
							return beta;
					}
					
					// captures right
					target=1L<<(currentPieceIndex+NUMBER_OF_SQUARES_PER_LINE+1);
					if((opponentPieces&target)!=0&&(currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1))
					{
						beta=Math.min(beta,currentEvaluation-getWhitePieceTypeWithTarget(target)+pawnId-queenId);
						if(beta<=alpha)
							return beta;
					}
				}
				piecesTemp&=piecesTemp-1;
			}
			return Math.min(beta,currentEvaluation);
		}
	}
	
	public int getWhitePieceTypeWithTarget(long target)
	{
		if((whitePawns&target)!=0)
			return pawnId;
		else if((whiteRooks&target)!=0)
			return rookId;
		else if((whiteBishops&target)!=0)
			return bishopId;
		else if((whiteKnights&target)!=0)
			return knightId;
		else if((whiteQueens&target)!=0)
			return queenId;
		return kingId;
	}
	
	private int minMax(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta)
	{
		if(currentDepth==1)
			counterNodes++;
		if(--currentDepth==0)
			return minMaxForZeroDepth(currentTurnParameter,currentEvaluation,alpha,beta);
		currentTurnParameter=-currentTurnParameter;
		long saveBeforeWithoutPiece;
		long piecesTemp;
		long saveBeforeMove;
		long target;
		long captureMoves;
		long movePossibilities;
		if(currentTurnParameter==white)
		{
			long opponentPieces=getBlackPieces();
			long ownPieces=getWhitePieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=whiteQueens;
			saveBeforeMove=whiteQueens;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				movePossibilities=getQueensMovesWithoutColor(indexPiece,allPieces);
				whiteQueens&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteQueens;
				captureMoves=movePossibilities&opponentPieces;
				while(captureMoves!=0)
				{
					target=1L<<Long.numberOfTrailingZeros(captureMoves);
					whiteQueens|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteQueens=saveBeforeMove;
						return alpha;
					}
					whiteQueens=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				while(movePossibilities!=0)
				{
					whiteQueens|=1L<<Long.numberOfTrailingZeros(movePossibilities);
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteQueens=saveBeforeMove;
						return alpha;
					}
					whiteQueens=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteQueens=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteRooks;
			saveBeforeMove=whiteRooks;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				movePossibilities=getLinesMovesWithoutColor(indexPiece,allPieces);
				whiteRooks&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteRooks;
				captureMoves=movePossibilities&opponentPieces;
				while(captureMoves!=0)
				{
					target=1L<<Long.numberOfTrailingZeros(captureMoves);
					whiteRooks|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteRooks=saveBeforeMove;
						return alpha;
					}
					whiteRooks=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				while(movePossibilities!=0)
				{
					whiteRooks|=1L<<Long.numberOfTrailingZeros(movePossibilities);
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteRooks=saveBeforeMove;
						return alpha;
					}
					whiteRooks=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteRooks=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteBishops;
			saveBeforeMove=whiteBishops;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				movePossibilities=getDiagonalsMovesWithoutColor(indexPiece,allPieces);
				whiteBishops&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteBishops;
				captureMoves=movePossibilities&opponentPieces;
				while(captureMoves!=0)
				{
					target=1L<<Long.numberOfTrailingZeros(captureMoves);
					whiteBishops|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteBishops=saveBeforeMove;
						return alpha;
					}
					whiteBishops=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				while(movePossibilities!=0)
				{
					whiteBishops|=1L<<Long.numberOfTrailingZeros(movePossibilities);
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteBishops=saveBeforeMove;
						return alpha;
					}
					whiteBishops=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteBishops=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			piecesTemp=whiteKnights;
			saveBeforeMove=whiteKnights;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				whiteKnights&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whiteKnights;
				movePossibilities=arrayKnightMoves[indexPiece];
				captureMoves=movePossibilities&opponentPieces;
				while(captureMoves!=0)
				{
					target=1L<<Long.numberOfTrailingZeros(captureMoves);
					whiteKnights|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whiteKnights=saveBeforeMove;
						return alpha;
					}
					whiteKnights=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				while(movePossibilities!=0)
				{
					whiteKnights|=1L<<Long.numberOfTrailingZeros(movePossibilities);
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whiteKnights=saveBeforeMove;
						return alpha;
					}
					whiteKnights=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				whiteKnights=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			
			saveBeforeMove=whiteKing;
			movePossibilities=arrayKingMoves[Long.numberOfTrailingZeros(whiteKing)];
			captureMoves=movePossibilities&opponentPieces;
			while(captureMoves!=0)
			{
				whiteKing=1L<<Long.numberOfTrailingZeros(captureMoves);
				alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,whiteKing);
				if(beta<=alpha)
				{
					whiteKing=saveBeforeMove;
					return alpha;
				}
				captureMoves&=captureMoves-1;
			}
			movePossibilities&=reverseAllPieces;
			while(movePossibilities!=0)
			{
				whiteKing=1L<<Long.numberOfTrailingZeros(movePossibilities);
				alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
				if(beta<=alpha)
				{
					whiteKing=saveBeforeMove;
					return alpha;
				}
				movePossibilities&=movePossibilities-1;
			}
			whiteKing=saveBeforeMove;
			
			piecesTemp=whitePawns;
			saveBeforeMove=whitePawns;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				whitePawns&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=whitePawns;
				
				// captures left
				target=1L<<(indexPiece-NUMBER_OF_SQUARES_PER_LINE-1);
				if((opponentPieces&target)!=0&&(indexPiece%NUMBER_OF_SQUARES_PER_LINE>0))
				{
					whitePawns|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
				}
				
				// captures right
				target=1L<<(indexPiece-NUMBER_OF_SQUARES_PER_LINE+1);
				if((opponentPieces&target)!=0&&(indexPiece%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1))
				{
					whitePawns|=target;
					alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
				}
				
				// move forward
				if((allPieces&1L<<indexPiece-NUMBER_OF_SQUARES_PER_LINE)==0)
				{
					// move one square forward
					whitePawns|=1L<<indexPiece-NUMBER_OF_SQUARES_PER_LINE;
					alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						whitePawns=saveBeforeMove;
						return alpha;
					}
					whitePawns=saveBeforeWithoutPiece;
					
					// move two square forward
					if(indexPiece/NUMBER_OF_SQUARES_PER_LINE==firstLeftWhitePawnsInitialPosition.y&&(allPieces&1L<<indexPiece-2*NUMBER_OF_SQUARES_PER_LINE)==0)
					{
						whitePawns|=1L<<indexPiece-2*NUMBER_OF_SQUARES_PER_LINE;
						alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
						whitePawns=saveBeforeWithoutPiece;
					}
				}
				
				// pawn promotion
				if(indexPiece/NUMBER_OF_SQUARES_PER_LINE==1)
				{
					long saveQueens=whiteQueens;
					if((allPieces&1L<<indexPiece-NUMBER_OF_SQUARES_PER_LINE)==0)
					{
						// move one square forward
						whiteQueens|=1L<<indexPiece-NUMBER_OF_SQUARES_PER_LINE;
						alpha=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta));
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
					
					// captures left
					target=1L<<(indexPiece-NUMBER_OF_SQUARES_PER_LINE-1);
					if((opponentPieces&target)!=0&&(indexPiece%NUMBER_OF_SQUARES_PER_LINE>0))
					{
						whiteQueens|=target;
						alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta,target);
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
					
					// captures right
					target=1L<<(indexPiece-NUMBER_OF_SQUARES_PER_LINE+1);
					if((opponentPieces&target)!=0&&(indexPiece%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1))
					{
						whiteQueens|=target;
						alpha=deleteEventualBlackPieceAndGetAlpha(currentTurnParameter,currentDepth,currentEvaluation-pawnId+queenId,alpha,beta,target);
						whiteQueens=saveQueens;
						if(beta<=alpha)
						{
							whitePawns=saveBeforeMove;
							return alpha;
						}
					}
				}
				whitePawns=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			return alpha;
		}
		else
		{
			long opponentPieces=getWhitePieces();
			long ownPieces=getBlackPieces();
			long allPieces=ownPieces|opponentPieces;
			long reverseAllPieces=~allPieces;
			piecesTemp=blackQueens;
			saveBeforeMove=blackQueens;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				movePossibilities=getQueensMovesWithoutColor(indexPiece,allPieces);
				blackQueens&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackQueens;
				captureMoves=movePossibilities&opponentPieces;
				while(captureMoves!=0)
				{
					target=1L<<Long.numberOfTrailingZeros(captureMoves);
					blackQueens|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackQueens=saveBeforeMove;
						return beta;
					}
					blackQueens=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				while(movePossibilities!=0)
				{
					blackQueens|=1L<<Long.numberOfTrailingZeros(movePossibilities);
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackQueens=saveBeforeMove;
						return beta;
					}
					blackQueens=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackQueens=saveBeforeMove;
			}
			
			saveBeforeMove=blackRooks;
			piecesTemp=blackRooks;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				movePossibilities=getLinesMovesWithoutColor(indexPiece,allPieces);
				blackRooks&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackRooks;
				captureMoves=movePossibilities&opponentPieces;
				while(captureMoves!=0)
				{
					target=1L<<Long.numberOfTrailingZeros(captureMoves);
					blackRooks|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackRooks=saveBeforeMove;
						return beta;
					}
					blackRooks=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				while(movePossibilities!=0)
				{
					blackRooks|=1L<<Long.numberOfTrailingZeros(movePossibilities);
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackRooks=saveBeforeMove;
						return beta;
					}
					blackRooks=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackRooks=saveBeforeMove;
			}
			
			piecesTemp=blackBishops;
			saveBeforeMove=blackBishops;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				movePossibilities=getDiagonalsMovesWithoutColor(indexPiece,allPieces);
				blackBishops&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackBishops;
				captureMoves=movePossibilities&opponentPieces;
				while(captureMoves!=0)
				{
					target=1L<<Long.numberOfTrailingZeros(captureMoves);
					blackBishops|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackBishops=saveBeforeMove;
						return beta;
					}
					blackBishops=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				while(movePossibilities!=0)
				{
					blackBishops|=1L<<Long.numberOfTrailingZeros(movePossibilities);
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackBishops=saveBeforeMove;
						return beta;
					}
					blackBishops=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackBishops=saveBeforeMove;
			}
			
			piecesTemp=blackKnights;
			saveBeforeMove=blackKnights;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				blackKnights&=~(1L<<indexPiece);
				saveBeforeWithoutPiece=blackKnights;
				movePossibilities=arrayKnightMoves[indexPiece];
				captureMoves=movePossibilities&opponentPieces;
				while(captureMoves!=0)
				{
					target=1L<<Long.numberOfTrailingZeros(captureMoves);
					blackKnights|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackKnights=saveBeforeMove;
						return beta;
					}
					blackKnights=saveBeforeWithoutPiece;
					captureMoves&=captureMoves-1;
				}
				movePossibilities&=reverseAllPieces;
				while(movePossibilities!=0)
				{
					blackKnights|=1L<<Long.numberOfTrailingZeros(movePossibilities);
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackKnights=saveBeforeMove;
						return beta;
					}
					blackKnights=saveBeforeWithoutPiece;
					movePossibilities&=movePossibilities-1;
				}
				piecesTemp&=piecesTemp-1;
				blackKnights=saveBeforeMove;
			}
			
			saveBeforeMove=blackKing;
			movePossibilities=arrayKingMoves[Long.numberOfTrailingZeros(blackKing)];
			captureMoves=movePossibilities&opponentPieces;
			while(captureMoves!=0)
			{
				blackKing=1L<<Long.numberOfTrailingZeros(captureMoves);
				beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,blackKing);
				if(beta<=alpha)
				{
					blackKing=saveBeforeMove;
					return beta;
				}
				captureMoves&=captureMoves-1;
			}
			movePossibilities&=reverseAllPieces;
			while(movePossibilities!=0)
			{
				blackKing=1L<<Long.numberOfTrailingZeros(movePossibilities);
				beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
				if(beta<=alpha)
				{
					blackKing=saveBeforeMove;
					return beta;
				}
				movePossibilities&=movePossibilities-1;
			}
			blackKing=saveBeforeMove;
			
			piecesTemp=blackPawns;
			saveBeforeMove=blackPawns;
			while(piecesTemp!=0)
			{
				int indexPiece=Long.numberOfTrailingZeros(piecesTemp);
				blackPawns&=~(1L<<indexPiece); // in all the case the pawn
				// source is no more
				saveBeforeWithoutPiece=blackPawns;
				
				// captures left
				target=1L<<(indexPiece+NUMBER_OF_SQUARES_PER_LINE-1);
				if((opponentPieces&target)!=0&&(indexPiece%NUMBER_OF_SQUARES_PER_LINE>0))
				{
					blackPawns|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
				}
				
				// captures right
				target=1L<<(indexPiece+NUMBER_OF_SQUARES_PER_LINE+1);
				if((opponentPieces&target)!=0&&(indexPiece%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1))
				{
					blackPawns|=target;
					beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta,target);
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
				}
				
				if((allPieces&1L<<indexPiece+NUMBER_OF_SQUARES_PER_LINE)==0)
				{
					// move on square forward
					blackPawns|=1L<<indexPiece+NUMBER_OF_SQUARES_PER_LINE;
					beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
					if(beta<=alpha)
					{
						blackPawns=saveBeforeMove;
						return beta;
					}
					blackPawns=saveBeforeWithoutPiece;
					
					// move two square forward
					if(indexPiece/NUMBER_OF_SQUARES_PER_LINE==firstLeftBlackPawnsInitialPosition.y&&(allPieces&1L<<indexPiece+2*NUMBER_OF_SQUARES_PER_LINE)==0)
					{
						blackPawns|=1L<<indexPiece+2*NUMBER_OF_SQUARES_PER_LINE;
						beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation,alpha,beta));
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
						blackPawns=saveBeforeWithoutPiece;
					}
				}
				
				// queen promotion
				if(indexPiece/NUMBER_OF_SQUARES_PER_LINE==NUMBER_OF_SQUARES_PER_LINE-2)
				{
					long saveQueens=blackQueens;
					// move on square forward
					if((allPieces&1L<<indexPiece+NUMBER_OF_SQUARES_PER_LINE)==0)
					{
						blackQueens|=1L<<indexPiece-NUMBER_OF_SQUARES_PER_LINE;
						beta=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta));
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
					
					// captures left
					target=1L<<(indexPiece+NUMBER_OF_SQUARES_PER_LINE-1);
					if((opponentPieces&target)!=0&&indexPiece%NUMBER_OF_SQUARES_PER_LINE>0)
					{
						blackQueens|=target;
						beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta,target);
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
					
					// captures right
					target=1L<<(indexPiece+NUMBER_OF_SQUARES_PER_LINE+1);
					if((opponentPieces&target)!=0&&(indexPiece%NUMBER_OF_SQUARES_PER_LINE<NUMBER_OF_SQUARES_PER_LINE-1))
					{
						blackQueens|=target;
						beta=deleteEventualWhitePieceAndGetBeta(currentTurnParameter,currentDepth,currentEvaluation+pawnId-queenId,alpha,beta,target);
						blackQueens=saveQueens;
						if(beta<=alpha)
						{
							blackPawns=saveBeforeMove;
							return beta;
						}
					}
				}
				blackPawns=saveBeforeMove;
				piecesTemp&=piecesTemp-1;
			}
			return beta;
		}
	}
	
	public int deleteEventualBlackPieceAndGetAlpha(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta,long target)
	{
		if((blackPawns&target)!=0)
		{
			blackPawns&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+pawnId,alpha,beta));
			blackPawns|=target;
			return alphaTemp;
		}
		if((blackRooks&target)!=0)
		{
			blackRooks&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+rookId,alpha,beta));
			blackRooks|=target;
			return alphaTemp;
		}
		if((blackBishops&target)!=0)
		{
			blackBishops&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+bishopId,alpha,beta));
			blackBishops|=target;
			return alphaTemp;
		}
		if((blackKnights&target)!=0)
		{
			blackKnights&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+knightId,alpha,beta));
			blackKnights|=target;
			return alphaTemp;
		}
		if((blackQueens&target)!=0)
		{
			blackQueens&=~target;
			alphaTemp=Math.max(alpha,minMax(currentTurnParameter,currentDepth,currentEvaluation+queenId,alpha,beta));
			blackQueens|=target;
			return alphaTemp;
		}
		return kingId; // no need to do more
	}
	
	public int deleteEventualWhitePieceAndGetBeta(int currentTurnParameter,int currentDepth,int currentEvaluation,int alpha,int beta,long target)
	{
		if((whitePawns&target)!=0)
		{
			whitePawns&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-pawnId,alpha,beta));
			whitePawns|=target;
			return betaTemp;
		}
		if((whiteRooks&target)!=0)
		{
			whiteRooks&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-rookId,alpha,beta));
			whiteRooks|=target;
			return betaTemp;
		}
		if((whiteBishops&target)!=0)
		{
			whiteBishops&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-bishopId,alpha,beta));
			whiteBishops|=target;
			return betaTemp;
		}
		if((whiteKnights&target)!=0)
		{
			whiteKnights&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-knightId,alpha,beta));
			whiteKnights|=target;
			return betaTemp;
		}
		if((whiteQueens&target)!=0)
		{
			whiteQueens&=~target;
			betaTemp=Math.min(beta,minMax(currentTurnParameter,currentDepth,currentEvaluation-queenId,alpha,beta));
			whiteQueens|=target;
			return betaTemp;
		}
		return -kingId;
	}
	
	// use for redo feature
	public int makeThisMoveAndGetDescriptionFromAWord(String moveDescription,ArrayList<String> arrayMoveDescription,boolean[] arrayIsSpecial,int isLastMoveEnableEnPassant)
	{
		if(GetCurrentTurn()==white)
		{
			// now we have to find the source of the moves
			long currentPieces=0;
			int indexPiece=moveDescription.indexOf("N");
			if(indexPiece!=-1)
				currentPieces=whiteKnights;
			indexPiece=moveDescription.indexOf("R");
			if(indexPiece!=-1)
				currentPieces=whiteRooks;
			indexPiece=moveDescription.indexOf("B");
			if(indexPiece!=-1)
				currentPieces=whiteBishops;
			indexPiece=moveDescription.indexOf("Q");
			if(indexPiece!=-1)
				currentPieces=whiteQueens;
			indexPiece=moveDescription.indexOf("K");
			if(indexPiece!=-1)
				currentPieces=whiteKing;
			if(currentPieces==0)
				currentPieces=whitePawns;
			if(moveDescription.equals(kingSideCastlingStandard)==true) // castling management
			{
				makeCastling(moveDescription);
				counterMoveFinished++;
				arrayMoveDescription.add(counterMoveFinished+". "+kingSideCastlingExplicit);
				arrayMoveDescription.add(counterMoveFinished+". "+kingSideCastlingStandard);
				arrayIsSpecial[0]=true;
			}
			else if(moveDescription.equals(queenSideCastlingStandard)==true) // castling management
			{
				makeCastling(moveDescription);
				counterMoveFinished++;
				arrayMoveDescription.add(counterMoveFinished+". "+queenSideCastlingExplicit);
				arrayMoveDescription.add(counterMoveFinished+". "+queenSideCastlingStandard);
				arrayIsSpecial[0]=true;
			}
			else
				return makeThisMoveAndGetDescriptionWithMoveDescription(currentPieces,moveDescription,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
		}
		else if(GetCurrentTurn()==black)
		{
			// now we have to find the source of the moves
			long currentPieces=0;
			int indexPiece=moveDescription.indexOf("N");
			if(indexPiece!=-1)
				currentPieces=blackKnights;
			indexPiece=moveDescription.indexOf("R");
			if(indexPiece!=-1)
				currentPieces=blackRooks;
			indexPiece=moveDescription.indexOf("B");
			if(indexPiece!=-1)
				currentPieces=blackBishops;
			indexPiece=moveDescription.indexOf("Q");
			if(indexPiece!=-1)
				currentPieces=blackQueens;
			indexPiece=moveDescription.indexOf("K");
			if(indexPiece!=-1)
				currentPieces=blackKing;
			if(currentPieces==0)
				currentPieces=blackPawns;
			if(moveDescription.equals(kingSideCastlingStandard)==true) // castling management
			{
				makeCastling(moveDescription);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+kingSideCastlingExplicit);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+kingSideCastlingStandard);
				arrayIsSpecial[0]=true;
			}
			else if(moveDescription.equals(queenSideCastlingStandard)==true) // castling management
			{
				makeCastling(moveDescription);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+queenSideCastlingExplicit);
				arrayMoveDescription.add((counterMoveFinished+1)+". "+queenSideCastlingStandard);
				arrayIsSpecial[0]=true;
			}
			else
				return makeThisMoveAndGetDescriptionWithMoveDescription(currentPieces,moveDescription,arrayMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
		}
		return -1;
	}
	
	void getMovesAtFirstLevelWithCheckChecking(int isLastMoveEnableEnPassant)
	{
		listSourceForMultithreading.clear();
		listDestinationForMultithreading.clear();
		if(currentTurn==black)
		{
			long blackPieces=getBlackPieces();
			for(int indexPiece=Long.numberOfTrailingZeros(blackPieces);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(blackPieces))
			{
				ArrayList<Integer> listDestination=getListOfPossibleMovesForAPieceWithCheckChecking(indexPiece,isLastMoveEnableEnPassant,false);
				listDestinationForMultithreading.addAll(listDestination);
				for(int counterDestination=0;counterDestination<listDestination.size();counterDestination++)
					listSourceForMultithreading.add(indexPiece);
				blackPieces&=blackPieces-1;
			}
		}
		else
		{
			long whitePieces=getWhitePieces();
			for(int indexPiece=Long.numberOfTrailingZeros(whitePieces);indexPiece!=Long.SIZE;indexPiece=Long.numberOfTrailingZeros(whitePieces))
			{
				ArrayList<Integer> listDestination=getListOfPossibleMovesForAPieceWithCheckChecking(indexPiece,isLastMoveEnableEnPassant,false);
				listDestinationForMultithreading.addAll(listDestination);
				for(int counterDestination=0;counterDestination<listDestination.size();counterDestination++)
					listSourceForMultithreading.add(indexPiece);
				whitePieces&=whitePieces-1;
			}
		}
	}
	
	public long getQueensMovesWithoutColor(int currentPieceIndex,long allPieces)
	{
		return matrixLineVerticalMoveResult[currentPieceIndex][(int)((magicDestinationMask&(arrayMagicNumberForVerticalLines[currentPieceIndex%NUMBER_OF_SQUARES_PER_LINE]*(arrayVerticalLineMask[currentPieceIndex]&allPieces)))>>(NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))+1)]|matrixLineHorizontalMoveResult[currentPieceIndex][(int)((arrayHorizontalLineMask[currentPieceIndex]&allPieces)>>(currentPieceIndex/NUMBER_OF_SQUARES_PER_LINE)*NUMBER_OF_SQUARES_PER_LINE+1)]|matrixDiagonalTopLeftBottomRightMoveResult[currentPieceIndex][(int)((magicDestinationMask&(magicForDiagonals*(arrayDiagonalTopLeftBottomRightMask[currentPieceIndex]&allPieces)))>>(NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))+arrayTopLeft[currentPieceIndex]+1)]|matrixDiagonalBottomLeftTopRightMoveResult[currentPieceIndex][(int)((magicDestinationMask&(magicForDiagonals*(arrayDiagonalBottomLeftTopRightMask[currentPieceIndex]&allPieces)))>>(NUMBER_OF_SQUARES_PER_LINE*(NUMBER_OF_SQUARES_PER_LINE-1))+arrayBottomLeft[currentPieceIndex]+1)];
	}
	
	public long getQueensMoves(int indexSource)
	{
		long movePossibilities=getLinesMoves(indexSource);
		movePossibilities|=getDiagonalsMoves(indexSource);
		return movePossibilities;
	}
	
	public long getKingMoves(int indexSource,long ownPiece)
	{
		long movePossibilities=arrayKingMoves[indexSource];
		movePossibilities&=~ownPiece;
		return movePossibilities;
	}
	
	public long getKingMoves(int indexSource)
	{
		long movePossibilities=arrayKingMoves[indexSource];
		movePossibilities&=~getCurrentPieces();
		return movePossibilities;
	}
	
	public long getKnightMoves(int indexSource,long ownPieces)
	{
		long movesPossibilites=arrayKnightMoves[indexSource];
		movesPossibilites&=~ownPieces;
		return movesPossibilites;
	}
	
	int EvaluatePositioningSituation()
	{
		int saveTurn=currentTurn;
		long whitePieces=getWhitePieces();
		long blackPieces=getBlackPieces();
		long allPieces=getAllPieces();
		currentTurn=white;
		int whiteQueenCount=0;
		long piecesTemp=whiteQueens;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteQueenCount+=Long.bitCount(getQueensMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int whiteRookCount=0;
		piecesTemp=whiteRooks;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteRookCount+=Long.bitCount(getLinesMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int whiteBishopCount=0;
		piecesTemp=whiteBishops;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteBishopCount+=Long.bitCount(getDiagonalsMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int whiteKnightCount=0;
		piecesTemp=whiteKnights;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteKnightCount+=Long.bitCount(getKnightMoves(indexPiece,whitePieces));
			piecesTemp&=piecesTemp-1;
		}
		int whitePawnCount=0;
		piecesTemp=whitePawns;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whitePawnCount+=Long.bitCount(getWhitePawnMoves(indexPiece,blackPieces,allPieces));
			piecesTemp&=piecesTemp-1;
		}
		int whiteKingCount=0;
		piecesTemp=whiteKing;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			whiteKingCount+=Long.bitCount(getKingMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		currentTurn=black;
		int blackQueenCount=0;
		piecesTemp=blackQueens;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackQueenCount+=Long.bitCount(getQueensMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int blackRookCount=0;
		piecesTemp=blackRooks;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackRookCount+=Long.bitCount(getLinesMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int blackBishopCount=0;
		piecesTemp=blackBishops;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackBishopCount+=Long.bitCount(getDiagonalsMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		int blackKnightCount=0;
		piecesTemp=blackKnights;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackKnightCount+=Long.bitCount(getKnightMoves(indexPiece,blackPieces));
			piecesTemp&=piecesTemp-1;
		}
		int blackPawnCount=0;
		piecesTemp=blackPawns;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackPawnCount+=Long.bitCount(getBlackPawnMoves(indexPiece,whitePieces,allPieces));
			piecesTemp&=piecesTemp-1;
		}
		int blackKingCount=0;
		piecesTemp=blackKing;
		for(int indexPiece=Long.numberOfTrailingZeros(piecesTemp);piecesTemp!=0;indexPiece=Long.numberOfTrailingZeros(piecesTemp))
		{
			blackKingCount+=Long.bitCount(getKingMoves(indexPiece));
			piecesTemp&=piecesTemp-1;
		}
		currentTurn=saveTurn;
		/*
		 * return (int) ((whiteQueenCount-blackQueenCount)*queenId)+ ((whiteRookCount-blackRookCount)*rookId)+ ((whiteBishopCount-blackBishopCount)*bishopId)+ ((whiteKnightCount-blackKnightCount)*knightId)+ ((whitePawnCount-blackPawnCount)*pawnId)+ ((whiteKingCount-blackKingCount)*kingIdValue);
		 */
		// actually most efficient with this there are more moves
		return ((whiteQueenCount-blackQueenCount))+((whiteRookCount-blackRookCount))+((whiteBishopCount-blackBishopCount))+((whiteKnightCount-blackKnightCount))+((whitePawnCount-blackPawnCount))+((whiteKingCount-blackKingCount));
	}
	
	int EvaluateCurrentSituation()
	{
		return (Long.bitCount(whitePawns)-Long.bitCount(blackPawns))*pawnId+(Long.bitCount(whiteQueens)-Long.bitCount(blackQueens))*queenId+(Long.bitCount(whiteKing)-Long.bitCount(blackKing))*kingId+(Long.bitCount(whiteBishops)-Long.bitCount(blackBishops))*bishopId+(Long.bitCount(whiteKnights)-Long.bitCount(blackKnights))*knightId+(Long.bitCount(whiteRooks)-Long.bitCount(blackRooks))*rookId;
	}
	
	public int playComputer(int maximumDepth,ArrayList<String> listMoveDescription,ArrayList<Point> listPointSourceFinal,ArrayList<Point> listPointDestinationFinal,boolean[] arrayIsSpecial,int isLastMoveEnableEnPassant) throws InterruptedException
	{
		isLastMoveEnableEnPassant=-1;
		getMovesAtFirstLevelWithCheckChecking(isLastMoveEnableEnPassant);
		ArrayList<Integer> listPointSource=new ArrayList<Integer>();
		ArrayList<Integer> listPointDestination=new ArrayList<Integer>();
		totalNodesCounter=playComputerAtOnlyASpecificLevel(maximumDepth,listPointSource,listPointDestination,isLastMoveEnableEnPassant);
		
		for(int counterDepth=1;counterDepth<maximumDepth;counterDepth++) // we filter lowest depths for blind problematic
		{
			if(listPointSource.size()==1)
				break;
			ArrayList<Integer> listPointSourceFilter=new ArrayList<Integer>();
			ArrayList<Integer> listPointDestinationFilter=new ArrayList<Integer>();
			playComputerAtOnlyASpecificLevel(counterDepth,listPointSourceFilter,listPointDestinationFilter,isLastMoveEnableEnPassant);
			ArrayList<Integer> listPointSourceTemp=new ArrayList<Integer>();
			ArrayList<Integer> listPointDestinationTemp=new ArrayList<Integer>();
			for(int counterMovesFilter=0;counterMovesFilter<listPointSourceFilter.size();counterMovesFilter++)
				for(int counterMovesOriginal=0;counterMovesOriginal<listPointSource.size();counterMovesOriginal++)
					if(listPointSource.get(counterMovesOriginal)==listPointSourceFilter.get(counterMovesFilter)&&listPointDestination.get(counterMovesOriginal)==listPointDestinationFilter.get(counterMovesFilter)&&listPointSource.size()>1)
					{
						listPointSourceTemp.add(listPointSource.get(counterMovesOriginal));
						listPointDestinationTemp.add(listPointDestination.get(counterMovesOriginal));
					}
			if(listPointSourceTemp.size()>0)
			{
				listPointSource.clear();
				listPointDestination.clear();
				for(int counterLowerFilter=0;counterLowerFilter<listPointSourceTemp.size();counterLowerFilter++)
				{
					listPointSource.add(listPointSourceTemp.get(counterLowerFilter));
					listPointDestination.add(listPointDestinationTemp.get(counterLowerFilter));
				}
			}
		}
		
		// now we have to delete move that will put in pat
		int counterBestMoves=0;
		boolean arrayIndexToBeDeleted[]=new boolean[maximumPossibleMoves];
		for(int counter=0;counter<maximumPossibleMoves;counter++)
			arrayIndexToBeDeleted[counter]=false;
		int counterMovesDeleted=0;
		boolean isSpecial[]=new boolean[1];
		ArrayList<String> listeMoveDescriptionTemp=new ArrayList<String>();
		for(;counterBestMoves<listPointSource.size();counterBestMoves++)
		{
			int typeOfEventualyDeletedPiece=getPieceTypeAtThisIndexAndWithThisColor(-currentTurn,listPointDestination.get(counterBestMoves));
			makeThisMoveAndGetDescriptionWithoutIncrement(new Point(listPointSource.get(counterBestMoves)%NUMBER_OF_SQUARES_PER_LINE,listPointSource.get(counterBestMoves)/NUMBER_OF_SQUARES_PER_LINE),new Point(listPointDestination.get(counterBestMoves)%NUMBER_OF_SQUARES_PER_LINE,listPointDestination.get(counterBestMoves)/NUMBER_OF_SQUARES_PER_LINE),listeMoveDescriptionTemp,isSpecial,isLastMoveEnableEnPassant);
			changePlayerTurn();
			int winner=IfGameHasEndedGiveMeTheWinner(isItDoublePawnMoveForEnPassant(new Point(listPointSource.get(counterBestMoves)%NUMBER_OF_SQUARES_PER_LINE,listPointSource.get(counterBestMoves)/NUMBER_OF_SQUARES_PER_LINE),new Point(listPointDestination.get(counterBestMoves)%NUMBER_OF_SQUARES_PER_LINE,listPointDestination.get(counterBestMoves)/NUMBER_OF_SQUARES_PER_LINE)));
			changePlayerTurn();
			if(winner!=0)
			{
				switch(winner)
				{
				case whiteIsPat:
				case blackIsPat:
					if(EvaluateCurrentSituation()*currentTurn>0)
					{
						counterMovesDeleted++;
						arrayIndexToBeDeleted[counterBestMoves]=true;
					}
				default:
					;
				}
			}
			unmakeMoveForWithoutRefreshRehearsalHistoric(new Point(listPointSource.get(counterBestMoves)%NUMBER_OF_SQUARES_PER_LINE,listPointSource.get(counterBestMoves)/NUMBER_OF_SQUARES_PER_LINE),new Point(listPointDestination.get(counterBestMoves)%NUMBER_OF_SQUARES_PER_LINE,listPointDestination.get(counterBestMoves)/NUMBER_OF_SQUARES_PER_LINE),typeOfEventualyDeletedPiece,isSpecial[0]);
		}
		if(listSourceForMultithreading.size()>1&&counterMovesDeleted>0) // we delete only if there a least one issue, if not it's a desperate situation
		{
			ArrayList<Integer> listSourceTemp=new ArrayList<Integer>();
			ArrayList<Integer> listDestinationTemp=new ArrayList<Integer>();
			for(int counterMoves=0;counterMoves<listSourceForMultithreading.size();counterMoves++)
				if(arrayIndexToBeDeleted[counterMoves]==false)
				{
					listSourceTemp.add(listSourceForMultithreading.get(counterMoves));
					listDestinationTemp.add(listDestinationForMultithreading.get(counterMoves));
				}
			listPointSource=listSourceTemp;
			listPointDestination=listDestinationTemp;
		}
		
		// we filter to get pawns move if there are
		ArrayList<Integer> listSourceTemp=new ArrayList<Integer>();
		ArrayList<Integer> listDestinationTemp=new ArrayList<Integer>();
		if(currentTurn==black)
		{
			int lowPawnVertical=-infinite;
			for(int counter=0;counter<listPointSource.size();counter++)
				if(getPieceTypeAtThisIndexWithCurrentColor(listPointSource.get(counter))==pawnId&&(listPointDestination.get(counter)>=lowPawnVertical))
				{
					if(listPointDestination.get(counter)>lowPawnVertical)
					{
						listSourceTemp.clear();
						listDestinationTemp.clear();
					}
					listSourceTemp.add(listPointSource.get(counter));
					listDestinationTemp.add(listPointDestination.get(counter));
					lowPawnVertical=listPointDestination.get(counter);
				}
		}
		else
		{
			int lowPawnVertical=infinite;
			for(int counter=0;counter<listPointSource.size();counter++)
				if(getPieceTypeAtThisIndexWithCurrentColor(listPointSource.get(counter))==pawnId&&(listPointDestination.get(counter)<=lowPawnVertical))
				{
					if(listPointDestination.get(counter)<lowPawnVertical)
					{
						listSourceTemp.clear();
						listDestinationTemp.clear();
					}
					listSourceTemp.add(listPointSource.get(counter));
					listDestinationTemp.add(listPointDestination.get(counter));
					lowPawnVertical=listPointDestination.get(counter);
				}
		}
		if(listSourceTemp.size()>0&&listPointSource.size()>1)
		{
			listPointSource=listSourceTemp;
			listPointDestination=listDestinationTemp;
		}
		
		// we get the best values according to moves possibilities
		isSpecial=new boolean[1];
		int bestPossibilities=infinite;
		if(currentTurn==white)
			bestPossibilities=-infinite;
		
		for(int counter=0;counter<listPointSource.size();counter++)
		{
			int pieceType=getPieceTypeAtThisIndexAndWithThisColor(currentTurn,listPointSource.get(counter));
			int pieceEventuallyDeleted=makeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,listPointSource.get(counter),listPointDestination.get(counter),isSpecial);
			int currentEvaluation=EvaluatePositioningSituation();
			if(currentTurn==white)
			{
				if(currentEvaluation>=bestPossibilities)
				{
					if(currentEvaluation>bestPossibilities)
					{
						listPointSourceFinal.clear();
						listPointDestinationFinal.clear();
					}
					listPointSourceFinal.add(new Point(listPointSource.get(counter)%NUMBER_OF_SQUARES_PER_LINE,listPointSource.get(counter)/NUMBER_OF_SQUARES_PER_LINE));
					listPointDestinationFinal.add(new Point(listPointDestination.get(counter)%NUMBER_OF_SQUARES_PER_LINE,listPointDestination.get(counter)/NUMBER_OF_SQUARES_PER_LINE));
					bestPossibilities=currentEvaluation;
				}
			}
			if(currentTurn==black)
			{
				if(currentEvaluation<=bestPossibilities)
				{
					if(currentEvaluation<bestPossibilities)
					{
						listPointSourceFinal.clear();
						listPointDestinationFinal.clear();
					}
					listPointSourceFinal.add(new Point(listPointSource.get(counter)%NUMBER_OF_SQUARES_PER_LINE,listPointSource.get(counter)/NUMBER_OF_SQUARES_PER_LINE));
					listPointDestinationFinal.add(new Point(listPointDestination.get(counter)%NUMBER_OF_SQUARES_PER_LINE,listPointDestination.get(counter)/NUMBER_OF_SQUARES_PER_LINE));
					bestPossibilities=currentEvaluation;
				}
			}
			unmakeMoveWithTwoIndexForCurrentTurnWithPieceId(pieceType,listPointDestination.get(counter),listPointSource.get(counter),pieceEventuallyDeleted,isSpecial[0]);
		}
		
		int choosenMovement=0;
		if(randomMovements==true&&listPointSourceFinal.size()!=1)
			// we determine the next move with random possibilities
			choosenMovement=(int)(Math.random()*listPointSourceFinal.size());
		
		// otherwise make the first move
		listPointSourceFinal.get(choosenMovement).x=listPointSourceFinal.get(choosenMovement).x;
		listPointSourceFinal.get(choosenMovement).y=listPointSourceFinal.get(choosenMovement).y;
		listPointDestinationFinal.get(choosenMovement).x=listPointDestinationFinal.get(choosenMovement).x;
		listPointDestinationFinal.get(choosenMovement).y=listPointDestinationFinal.get(choosenMovement).y;
		makeThisMoveAndGetDescription(listPointSourceFinal.get(choosenMovement),listPointDestinationFinal.get(choosenMovement),listMoveDescription,arrayIsSpecial,isLastMoveEnableEnPassant);
		return choosenMovement;
	}
	
	// we set all the pieces with their right position
	public void initializeNewGame()
	{
		
		blackRooks=0;
		blackRooks=blackRooks|(1L<<leftBlackRookInitialPosition.x+leftBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		blackRooks=blackRooks|(1L<<rightBlackRookInitialPosition.x+rightBlackRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		blackBishops=0;
		blackBishops=blackBishops|(1L<<leftBlackBishopInitialPosition.x+leftBlackBishopInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		blackBishops=blackBishops|(1L<<rightBlackBishopInitialPosition.x+rightBlackBishopInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		blackKnights=0;
		blackKnights=blackKnights|(1L<<leftBlackKnightInitialPosition.x+leftBlackKnightInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		blackKnights=blackKnights|(1L<<rightBlackKnightInitialPosition.x+rightBlackKnightInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		blackQueens=0;
		blackQueens=blackQueens|(1L<<blackQueenInitialPosition.x+blackQueenInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		blackKing=0;
		blackKing=blackKing|(1L<<blackKingInitialPosition.x+blackKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		blackPawns=0;
		for(int counterHorizontal=firstLeftBlackPawnsInitialPosition.x;counterHorizontal<NUMBER_OF_SQUARES_PER_LINE;counterHorizontal++)
			blackPawns|=1L<<(counterHorizontal+firstLeftBlackPawnsInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		whiteRooks=0;
		whiteRooks=whiteRooks|(1L<<leftWhiteRookInitialPosition.x+leftWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		whiteRooks=whiteRooks|(1L<<rightWhiteRookInitialPosition.x+rightWhiteRookInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		whiteBishops=0;
		whiteBishops=whiteBishops|(1L<<leftWhiteBishopInitialPosition.x+leftWhiteBishopInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		whiteBishops=whiteBishops|(1L<<rightWhiteBishopInitialPosition.x+rightWhiteBishopInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		whiteKnights=0;
		whiteKnights=whiteKnights|(1L<<leftWhiteKnightInitialPosition.x+leftWhiteKnightInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		whiteKnights=whiteKnights|(1L<<rightWhiteKnightInitialPosition.x+rightWhiteKnightInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		whiteQueens=0;
		whiteQueens=whiteQueens|(1L<<whiteQueenInitialPosition.x+whiteQueenInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		whiteKing=0;
		whiteKing=whiteKing|(1L<<whiteKingInitialPosition.x+whiteKingInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		whitePawns=0;
		for(int counterHorizontal=firstLeftWhitePawnsInitialPosition.x;counterHorizontal<NUMBER_OF_SQUARES_PER_LINE;counterHorizontal++)
			whitePawns|=1L<<(counterHorizontal+firstLeftWhitePawnsInitialPosition.y*NUMBER_OF_SQUARES_PER_LINE);
		
		  // to simulate blind effect :	blackRooks=blackRooks|(1L<<16); blackRooks=blackRooks|(1L<<23); blackRooks=blackRooks|(1L<<32); blackRooks=blackRooks|(1L<<39); blackKing=0; blackKing=blackKing|(1L<< blackKingInitialPosition.x+blackKingInitialPosition .y*NUMBER_OF_SQUARES_PER_LINE); whiteKing=0; whiteKing=whiteKing|(1L<<28); blackKnights=0; blackKnights|=1L<<49;
		 
		
		// for castling
		isWhiteKingHasMoved=false;
		isBlackKingHasMoved=false;
		isBlackLeftRookHasMoved=false;
		isBlackRightRookHasMoved=false;
		isWhiteLeftRookHasMoved=false;
		isWhiteRightRookHasMoved=false;
		
		currentTurn=white;
		counterMoveFinished=0;
		listPiecesSituation=new ArrayList<PiecesSituation>();
		listPiecesSituationOccurrences=new ArrayList<Integer>();
		
		// because it's a new game, we have to initialize begining date
		Calendar calendar=Calendar.getInstance();
		long beginningTime=calendar.getTimeInMillis();
		gameBeginningDate=new Date(beginningTime);
	}
	
	public ChessRuler()
	{
		blackQueenCastlingMask=0;
		for(int counterIndexCastling=beginBlackQueenCastling;counterIndexCastling<=endBlackQueenCastling;counterIndexCastling++)
			blackQueenCastlingMask|=1L<<counterIndexCastling;
		blackKingCastlingMask=0;
		for(int counterIndexCastling=beginBlackKingCastling;counterIndexCastling<=endBlackKingCastling;counterIndexCastling++)
			blackKingCastlingMask|=1L<<counterIndexCastling;
		whiteQueenCastlingMask=0;
		for(int counterIndexCastling=beginWhiteQueenCastling;counterIndexCastling<=endWhiteQueenCastling;counterIndexCastling++)
			whiteQueenCastlingMask|=1L<<counterIndexCastling;
		whiteKingCastlingMask=0;
		for(int counterIndexCastling=beginWhiteKingCastling;counterIndexCastling<=endWhiteKingCastling;counterIndexCastling++)
			whiteKingCastlingMask|=1L<<counterIndexCastling;
		
		arrayBottomLeft=new int[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE];
		arrayTopLeft=new int[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE];
		arrayKingMoves=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE];
		arrayKnightMoves=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE];
		arrayMagicNumberForVerticalLines=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE];
		arrayHorizontalLineMask=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE];
		arrayVerticalLineMask=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE];
		arrayDiagonalTopLeftBottomRightMask=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE];
		arrayDiagonalBottomLeftTopRightMask=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE];
		matrixDiagonalBottomLeftTopRightMoveResult=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE][(int)Math.pow(2,NUMBER_OF_SQUARES_PER_LINE-2)];
		matrixDiagonalTopLeftBottomRightMoveResult=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE][(int)Math.pow(2,NUMBER_OF_SQUARES_PER_LINE-2)];
		matrixLineHorizontalMoveResult=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE][(int)Math.pow(2,NUMBER_OF_SQUARES_PER_LINE-2)];
		matrixLineVerticalMoveResult=new long[NUMBER_OF_SQUARES_PER_LINE*NUMBER_OF_SQUARES_PER_LINE][(int)Math.pow(2,NUMBER_OF_SQUARES_PER_LINE-2)];
		magicDestinationMask=0;
		magicDestinationMask|=1L<<57;
		magicDestinationMask|=1L<<58;
		magicDestinationMask|=1L<<59;
		magicDestinationMask|=1L<<60;
		magicDestinationMask|=1L<<61;
		magicDestinationMask|=1L<<62;
		generateBishopsMoves();
		generateRooksMoves();
		generateKnightsMoves();
		generateKingsMoves();
		initializeNewGame();
		setRandomMoves();
	}
	
	public void setRandomMoves()
	{
		randomMovements=true;
	}
	
	public void setDeterministicMoves()
	{
		randomMovements=false;
	}
	
	// change the player turn, because we use opposed values, we don't have to know what is the current turn, and the turn we have to switch on
	public void changePlayerTurn()
	{
		currentTurn=-currentTurn;
	}
	
	public int GetCurrentTurn()
	{
		return currentTurn;
	}
}
