package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMovesCalculator {

    public Collection<ChessMove> determineMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (board.getPiece(myPosition).getPieceType()) {
            case BISHOP -> bishopMoves(board, myPosition);
//            case KING -> kingMoves(board, myPosition);
//            case KNIGHT -> knightMoves(board, myPosition);
//            case PAWN -> pawnMoves(board, myPosition);
//            case QUEEN -> queenMoves(board, myPosition);
//            case ROOK -> rookMoves(board, myPosition);
            default -> null;
        };
    }

    private boolean outsideBounds(ChessPosition position) {
        return (position.getRow() > 8 || position.getRow() < 1 || position.getColumn() > 8 || position.getColumn() < 1);
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> move = new ArrayList<>();
        int[][] BishopDirections = {{1,1},{1,-1},{-1,1},{-1,-1}}; //possible directions bishop can move
        for (int[] direction : BishopDirections) {
            int y = direction[0];
            int x = direction[1];
            for (int range = 1; ; range++) {
                ChessPosition place = new ChessPosition(myPosition.getRow() + y * range, myPosition.getColumn() + x * range);
                if (outsideBounds(place)) break; //make sure bishop doesn't go out of bounds
                ChessPiece spot =  board.getPiece(place);
                if (spot == null){
                    move.add(new ChessMove(myPosition, place, null));
                    continue; //replace null spot with the bishop
                }
                if (spot.getPieceColor() != board.getPiece(myPosition).getPieceColor()) {
                    move.add(new ChessMove(myPosition, place, null));
                }
                break; //go until piece found, if enemy piece, replace that piece
            }

        }
        return move;
    }

//    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
//        int[][] KingDirections = {{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1}};
//        Collection<ChessMove> move = new ArrayList<>();
//    }
//
//    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
//        Collection<ChessMove> move = new ArrayList<>();
//    }
//
//    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
//        Collection<ChessMove> move = new ArrayList<>();
//    }
//
//    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
//        Collection<ChessMove> move = new ArrayList<>();
//    }
//
//    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
//        Collection<ChessMove> move = new ArrayList<>();
//    }


}
