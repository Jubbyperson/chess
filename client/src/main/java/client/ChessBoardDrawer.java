package client;

import chess.*;
import ui.EscapeSequences;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class ChessBoardDrawer {
    public static void drawBoard(ChessGame game, ChessGame.TeamColor perspective) {
        drawBoardInternal(game, perspective, null, null);
    }

    public static void drawBoardWithHighlights(ChessGame game, ChessGame.TeamColor perspective,
                                               ChessPosition highlightStart, Collection<ChessMove> validMoves) {
        drawBoardInternal(game, perspective, highlightStart, validMoves);
    }

    private static void drawBoardInternal(ChessGame game, ChessGame.TeamColor perspective,
                                          ChessPosition highlightStart, Collection<ChessMove> validMoves) {
        ChessBoard board = game.getBoard();
        Set<ChessPosition> highlightedSquares = new HashSet<>();

        // Populate highlighted squares if highlights are provided
        if (highlightStart != null) {
            highlightedSquares.add(highlightStart);
        }
        if (validMoves != null) {
            for (ChessMove move : validMoves) {
                highlightedSquares.add(move.getEndPosition());
            }
        }

        // Draw top column labels
        drawColumnLabels(perspective);

        // Draw board rows
        for (int row = 1; row <= 8; row++) {
            int displayRow = (perspective == ChessGame.TeamColor.WHITE) ? (9 - row) : row;
            System.out.print(" " + displayRow + " ");

            for (int col = 1; col <= 8; col++) {
                int displayCol = (perspective == ChessGame.TeamColor.WHITE) ? col : (9 - col);
                ChessPosition position = new ChessPosition(displayRow, displayCol);
                ChessPiece piece = board.getPiece(position);

                // Determine background color
                String bgColor;
                if (highlightedSquares.contains(position)) {
                    bgColor = EscapeSequences.SET_BG_COLOR_YELLOW;
                } else {
                    boolean isLightSquare = ((row + col) % 2 == 0);
                    bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                }

                // Draw piece or empty square
                if (piece != null) {
                    String pieceSymbol = getPieceSymbol(piece);
                    String textColor = getTextColor(piece, perspective);
                    System.out.print(bgColor + textColor + pieceSymbol + EscapeSequences.RESET_BG_COLOR);
                } else {
                    System.out.print(bgColor + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR);
                }
            }

            System.out.println(EscapeSequences.RESET_TEXT_COLOR + " " + displayRow);
        }

        // Draw bottom column labels
        drawColumnLabels(perspective);
        System.out.println();
    }

    private static void drawColumnLabels(ChessGame.TeamColor perspective) {
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

    private static String getTextColor(ChessPiece piece, ChessGame.TeamColor perspective) {
        if (perspective == ChessGame.TeamColor.WHITE) {
            return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.SET_TEXT_COLOR_RED : EscapeSequences.SET_TEXT_COLOR_BLUE;
        } else {
            // When viewing from BLACK perspective, reverse the colors
            return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_RED;
        }
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
