package client;

import chess.*;
import ui.EscapeSequences;


public class ChessBoardDrawer {
    public static void drawBoard(ChessGame game, ChessGame.TeamColor perspective) {
        ChessBoard board = game.getBoard();
        System.out.print("   ");
        if (perspective == ChessGame.TeamColor.WHITE) {
            for (char col = 'a'; col <= 'h'; col++) {
                System.out.print(" " + col + " ");
            }
        } else {
            for (char col = 'h'; col >= 'a'; col--) {
                System.out.print(" " + col + " ");
            }
        }
        System.out.println();


    }

    private static String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
