package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMovesCalculator {

    public Collection<ChessMove> determineMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (board.getPiece(myPosition).getPieceType()) {
            case BISHOP -> bishopMoves(board, myPosition);
            case KING -> kingMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
        };
    }

    private boolean outsideBounds(ChessPosition position) {
        return (position.getRow() > 8 || position.getRow() < 1 || position.getColumn() > 8 || position.getColumn() < 1);
    }

    private Collection<ChessMove> slidingPieceMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        for (int[] direction : directions) {
            int y = direction[0];
            int x = direction[1];
            for (int range = 1; ; range++) {
                ChessPosition place = new ChessPosition(myPosition.getRow() + y * range, myPosition.getColumn() + x * range);
                if (outsideBounds(place)) break;
                ChessPiece spot = board.getPiece(place);
                if (spot == null) {
                    moves.add(new ChessMove(myPosition, place, null));
                    continue;
                }
                if (spot.getPieceColor() != myPiece.getPieceColor()) {
                    moves.add(new ChessMove(myPosition, place, null));
                }
                break;
            }
        }
        return moves;
    }

    private Collection<ChessMove> stepPieceMoves(ChessBoard board, ChessPosition myPosition, int[][] directions){
        Collection<ChessMove> move = new ArrayList<>();
        for (int[] direction : directions) {
            int y = direction[0];
            int x = direction[1];
            ChessPosition place = new ChessPosition(myPosition.getRow() + y, myPosition.getColumn() + x);
            if (outsideBounds(place)) continue; //Check next direction if current is blocked by edge
            ChessPiece spot =  board.getPiece(place);
            if ((spot == null) || (spot.getPieceColor() != board.getPiece(myPosition).getPieceColor())) {
                move.add(new ChessMove(myPosition, place, null)); //if spot to move to is unoccupied or taken by enemy piece, move to that spot
            }
        }
        return move;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        int[][] BishopDirections = {{1,1},{1,-1},{-1,1},{-1,-1}}; //possible directions bishop can move
        return slidingPieceMoves(board, myPosition, BishopDirections);
    }

     private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        int[][] QueenDirections = {{1,1},{1,-1},{-1,1},{-1,-1}, {0,1},{0,-1},{1,0},{-1,0}};
        return slidingPieceMoves(board, myPosition, QueenDirections);
    }
    
     private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        int[][] RookDirections = {{0,1},{0,-1},{-1,0},{1,0}};
        return slidingPieceMoves(board, myPosition, RookDirections);
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        int[][] KingDirections = {{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1}}; //king possible moves from current space
        return stepPieceMoves(board, myPosition, KingDirections);
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] KnightDirections = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        return stepPieceMoves(board, myPosition, KnightDirections);
    }


    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        //general movement and promotion logic:
        Collection<ChessMove> move = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        int directionCoord;
        int promotionRow;
        if (piece.getPieceColor() == ChessGame.TeamColor.WHITE) {
            directionCoord = 1;
            promotionRow = 8;
        } else {
            directionCoord = -1;
            promotionRow = 1;
        }
        ChessPosition oneSpaceAhead = new ChessPosition(myPosition.getRow() + directionCoord, myPosition.getColumn());
        int[][] PawnDirections = {{directionCoord, 0}};
        for (int[] direction : PawnDirections) {
            int y = direction[0];
            int x = direction[1];
            ChessPosition place = new ChessPosition(myPosition.getRow() + y, myPosition.getColumn() + x);
            if (outsideBounds(place)) continue;
            if (board.getPiece(place) == null) {
                if (oneSpaceAhead.getRow() == promotionRow && board.getPiece(oneSpaceAhead) == null) {
                    move.add(new ChessMove(myPosition, oneSpaceAhead, ChessPiece.PieceType.QUEEN));
                    move.add(new ChessMove(myPosition, oneSpaceAhead, ChessPiece.PieceType.ROOK));
                    move.add(new ChessMove(myPosition, oneSpaceAhead, ChessPiece.PieceType.BISHOP));
                    move.add(new ChessMove(myPosition, oneSpaceAhead, ChessPiece.PieceType.KNIGHT));
                } else {
                    move.add(new ChessMove(myPosition, place, null));
                }
            }
        }

        //starting position movement logic:
        ChessPosition twoSpaceAhead = new ChessPosition(myPosition.getRow() + 2 * directionCoord, myPosition.getColumn());
        boolean whiteAtStart = ((piece.getPieceColor() == ChessGame.TeamColor.WHITE) && (myPosition.getRow() == 2) && (board.getPiece(twoSpaceAhead) == null) && (board.getPiece(oneSpaceAhead) == null));
        if (whiteAtStart) {
            move.add(new ChessMove(myPosition, twoSpaceAhead, null));
        }
        boolean blackAtStart = ((piece.getPieceColor() == ChessGame.TeamColor.BLACK) && (myPosition.getRow() == 7) && (board.getPiece(twoSpaceAhead) == null) && (board.getPiece(oneSpaceAhead) == null));
        if (blackAtStart) {
            move.add(new ChessMove(myPosition, twoSpaceAhead, null));
        }

        //capture logic
        int captureColumn;
        for (captureColumn = -1; captureColumn <= 1; captureColumn += 2) {
            ChessPosition target = new ChessPosition(myPosition.getRow() + directionCoord, myPosition.getColumn() + captureColumn);
            if (outsideBounds(target)) continue;
            ChessPiece place = board.getPiece(target);
            if (place != null && place.getPieceColor() != piece.getPieceColor()) {
                if (target.getRow() == promotionRow) {
                    move.add(new ChessMove(myPosition, target, ChessPiece.PieceType.QUEEN));
                    move.add(new ChessMove(myPosition, target, ChessPiece.PieceType.ROOK));
                    move.add(new ChessMove(myPosition, target, ChessPiece.PieceType.BISHOP));
                    move.add(new ChessMove(myPosition, target, ChessPiece.PieceType.KNIGHT));
                } else {
                    move.add(new ChessMove(myPosition, target, null));
                }
            }
        }

        return move;
    }
}

    