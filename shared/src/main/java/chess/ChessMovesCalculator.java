package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessMovesCalculator {

    public Collection<ChessMove> determineMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (board.getPiece(myPosition).getPieceType()) {
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMove(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }


}
