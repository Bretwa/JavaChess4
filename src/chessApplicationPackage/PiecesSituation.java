package chessApplicationPackage;

import java.util.BitSet;

public class PiecesSituation
{
	// all the bitsets for each type of piece
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
	private static final int numberOfSquarePerLine=8;
	
	public PiecesSituation(long whiteKnightsParameter,long whiteBishopsParameter,long whiteQueenParameter,long whiteKingParameter,long whitePawnsParameter,long whiteRooksParameter,long blackKnightsParameter,long blackBishopsParameter,long blackQueenParameter,long blackKingParameter,long blackPawnsParameter,long blackRooksParameter)
	{
		whiteKnights=whiteKnightsParameter;
		whiteBishops=whiteBishopsParameter;
		whiteQueens=whiteQueenParameter;
		whiteKing=whiteKingParameter;
		whitePawns=whitePawnsParameter;
		whiteRooks=whiteRooksParameter;
		blackKnights=blackKnightsParameter;
		blackBishops=blackBishopsParameter;
		blackQueens=blackQueenParameter;
		blackKing=blackKingParameter;
		blackPawns=blackPawnsParameter;
		blackRooks=blackRooksParameter;
	}
	
	public void displayBitSet(BitSet bitSet)
	{
		if(bitSet==null)
		{
			System.out.println("Error in DisplayBitSet bitSet is null");
			return;
		}
		System.out.println("------------------------------");
		for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
		{
			for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
			{
				if(bitSet.get(counterVertical*numberOfSquarePerLine+counterHorizontal)==true)
					System.out.print("1 ");
				else
					System.out.print("0 ");
			}
			System.out.print("\n");
		}
	}
	
	public long getAllPieces()
	{
		long allPieces=0;
		allPieces|=whiteRooks;
		allPieces|=whiteKnights;
		allPieces|=whiteBishops;
		allPieces|=whiteQueens;
		allPieces|=whiteKing;
		allPieces|=whitePawns;
		allPieces|=blackRooks;
		allPieces|=blackKnights;
		allPieces|=blackBishops;
		allPieces|=blackQueens;
		allPieces|=blackKing;
		allPieces|=blackPawns;
		return allPieces;
	}
	
	public boolean equal(PiecesSituation piecesSituationParameter)
	{
		if(whiteRooks==piecesSituationParameter.whiteRooks&&whiteKnights==piecesSituationParameter.whiteKnights&&whiteBishops==piecesSituationParameter.whiteBishops&&whiteQueens==piecesSituationParameter.whiteQueens&&whitePawns==piecesSituationParameter.whitePawns&&whiteRooks==piecesSituationParameter.whiteRooks&&whiteKing==piecesSituationParameter.whiteKing&&blackRooks==piecesSituationParameter.blackRooks&&blackKnights==piecesSituationParameter.blackKnights&&blackBishops==piecesSituationParameter.blackBishops&&blackQueens==piecesSituationParameter.blackQueens&&blackPawns==piecesSituationParameter.blackPawns&&blackRooks==piecesSituationParameter.blackRooks&&blackKing==piecesSituationParameter.blackKing)
			return true;
		return false;
	}
}
