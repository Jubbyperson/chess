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

        for (int row = 1; row <= 8; row++) {
            int displayRow = (perspective == ChessGame.TeamColor.WHITE) ? (9 - row) : row;
            System.out.print(" " + displayRow + " ");

            for (int col = 1; col <= 8; col++) {
                int displayCol = (perspective == ChessGame.TeamColor.WHITE) ? col : (9 - col);
                ChessPosition position = new ChessPosition(row, displayCol);
                ChessPiece piece = board.getPiece(position);

                boolean isLightSquare = (perspective == ChessGame.TeamColor.WHITE) ? 
                    ((row + col) % 2 == 0) : ((row + col) % 2 == 1);
                String bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                if (piece != null) {
                    String pieceSymbol = getPieceSymbol(piece);
                    // Reverse colors when perspective is BLACK
                    String textColor;
                    if (perspective == ChessGame.TeamColor.WHITE) {
                        textColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                                EscapeSequences.SET_TEXT_COLOR_RED : EscapeSequences.SET_TEXT_COLOR_BLUE;
                    } else {
                        // When viewing from BLACK perspective, reverse the colors
                        textColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                                EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_RED;
                    }
                    System.out.print(bgColor + textColor + pieceSymbol + EscapeSequences.RESET_BG_COLOR);
                } else {
                    System.out.print(bgColor + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR);
                }
            }

            System.out.println(EscapeSequences.RESET_TEXT_COLOR + " " + displayRow);
        }
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
}
