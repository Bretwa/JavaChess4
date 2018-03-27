package chessBoardPackage;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import chessApplicationPackage.ChessApplication;

public class ChessBoardMouseListener extends MouseAdapter
{
	public ChessApplication chessApplicationInstance;
	
	public ChessBoardMouseListener(ChessApplication ChessApplicationParameter)
	{
		chessApplicationInstance=ChessApplicationParameter;
	}
	
	@Override
	public void mousePressed(MouseEvent MouseEventParameter)
	{
		try
		{
			chessApplicationInstance.onMousePressedOnTheChessBoard(MouseEventParameter);
		}
		catch(InterruptedException exception)
		{
			exception.printStackTrace();
		}
	}
}
