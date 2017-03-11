package ChessSmart;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChessSmart extends Application {
	static Pane pane;
	static Scene scene;
	static Rectangle[][] visibleBoard;
	static Text[][] boardText;
	static ChessBoard board;
	static ArrayList<Character> playerPieceCodes, CPU_PieceCodes;
	static String playerName;
	static int i, j, v, w, x, y;
	static char selectionCode, destinationCode;
	static short compilationStep;
	static boolean playerTurn;
	static boolean playerCheckMode, CPU_CheckMode;
	static boolean gameActive;

	public static void main(String[] args) {
		playerPieceCodes = new ArrayList<Character>();
		playerPieceCodes.add('R');
		playerPieceCodes.add('N');
		playerPieceCodes.add('B');
		playerPieceCodes.add('Q');
		playerPieceCodes.add('K');
		playerPieceCodes.add('P');
		CPU_PieceCodes = new ArrayList<Character>();
		CPU_PieceCodes.add('r');
		CPU_PieceCodes.add('n');
		CPU_PieceCodes.add('b');
		CPU_PieceCodes.add('q');
		CPU_PieceCodes.add('k');
		CPU_PieceCodes.add('p');
		char[][] boardCode = new char[8][8];
		// Assign the original chess piece positions.
		boardCode[0][0] = 'R';
		boardCode[0][7] = 'R';
		boardCode[0][1] = 'N';
		boardCode[0][6] = 'N';
		boardCode[0][2] = 'B';
		boardCode[0][5] = 'B';
		boardCode[0][3] = 'Q';
		boardCode[0][4] = 'K';
		for (j = 0; j < boardCode[0].length; ++j) {
			boardCode[1][j] = 'P';
			boardCode[6][j] = 'p';
		}
		boardCode[7][0] = 'r';
		boardCode[7][7] = 'r';
		boardCode[7][1] = 'n';
		boardCode[7][6] = 'n';
		boardCode[7][2] = 'b';
		boardCode[7][5] = 'b';
		boardCode[7][3] = 'q';
		boardCode[7][4] = 'k';
		for (i = 2; i < 6; ++i)
			for (j = 0; j < boardCode[i].length; ++j)
				boardCode[i][j] = ' ';
		board = new ChessBoard(boardCode, playerPieceCodes, CPU_PieceCodes);
		playerTurn = true;
		playerCheckMode = false;
		CPU_CheckMode = false;
		gameActive = true;
		boardText = new Text[8][8];
		launch(args);
	}

	public void start(Stage primaryStage) {
		pane = new Pane();
		scene = new Scene(pane);
		visibleBoard = new Rectangle[8][8];
		boardText = new Text[8][8];
		for (x = 0; x < visibleBoard.length; ++x)
			for (y = 0; y < visibleBoard[x].length; ++y) {
				visibleBoard[x][y] = new Rectangle(70.0, 70.0);
				visibleBoard[x][y].setX(50.0 + 70.0 * x);
				visibleBoard[x][y].setY(50.0 + 70.0 * y);
				if ((x + y) % 2 == 0)
					visibleBoard[x][y].setFill(Color.GREEN);
				else
					visibleBoard[x][y].setFill(Color.CYAN);
				pane.getChildren().add(visibleBoard[x][y]);
			}
		primaryStage.setScene(scene);
		playRound(primaryStage);
	}

	private void playRound(Stage primaryStage) {
		for (x = 0; x < 8; ++x)
			for (y = 0; y < 8; ++y) {
				pane.getChildren().remove(boardText[x][y]);
				boardText[x][y] = new Text("" + board.getBoardCode()[x][y]);
				boardText[x][y].setX(85.0 + 70.0 * x);
				boardText[x][y].setY(85.0 + 70.0 * y);
				pane.getChildren().add(boardText[x][y]);
			}
		compilationStep %= 2;
		if (playerCheckMode) {
			if (board.playerOneInCheckMate()) {
				JOptionPane.showMessageDialog(null, "That's check mate, " + playerName + "!");
				JOptionPane.showMessageDialog(null, "Better luck next time");
				endGame();
				gameActive = false;
			} else
				playerCheckMode = false;
		}
		if (CPU_CheckMode) {
			if (board.playerTwoInCheckMate()) {
				switch ((int) (Math.random() * 4)) {
				case 0:
					JOptionPane.showMessageDialog(null, "I underestimated your skill! I am out of moves.");
					break;
				case 1:
					JOptionPane.showMessageDialog(null, "Whoa! didn't see that coming! Good game!");
					break;
				case 2:
					JOptionPane.showMessageDialog(null, "Why didn't you tell me you were a pro?");
					break;
				case 3:
					JOptionPane.showMessageDialog(null, "Go easy on the beginners, would ya?");
				}
				endGame();
				gameActive = false;
				primaryStage.show();
			} else {
				CPU_CheckMode = false;
			}
		}
		if (gameActive) {
			primaryStage.show();
			scene.setOnMouseClicked(e -> {
				int X = 0, Y = 0;
				if (playerTurn) {
					for (x = 0; x < visibleBoard.length; ++x)
						for (y = 0; y < visibleBoard[x].length; ++y) {
							if (visibleBoard[x][y].contains(new Point2D(e.getSceneX(), e.getSceneY()))) {
								X = x;
								Y = y;
							}
						}
					if (compilationStep == 0) {/*
												 * In this compilation step, the
												 * player is selecting a piece
												 * from the board.
												 */
						primaryStage.close();
						i = X;
						j = Y;
						selectionCode = board.getBoardCode()[i][j];
						board.getBoardCode()[i][j] = ' ';
						board.setBoardCode(board.getBoardCode());
						++compilationStep;
						playRound(primaryStage);
					} else {/*
							 * In this compilation step, the player is selecting
							 * where on the board they wish to relocated their
							 * piece.
							 */
						primaryStage.close();
						v = X;
						w = Y;
						destinationCode = board.getBoardCode()[v][w];
						try {
							board.getBoardCode()[i][j] = selectionCode;
							ChessMove move = new ChessMove(i, j, v, w, board.getBoardCode(), playerTurn,
									playerPieceCodes, CPU_PieceCodes);
							board.getBoardCode()[v][w] = selectionCode;
							board.getBoardCode()[i][j] = ' ';
							/*
							 * If a move is successful, it becomes the next
							 * player's turn.
							 */
							playerTurn = false;
							if (move.validCastlingMove)
								switch (selectionCode) {
								case 'r':
									board.getBoardCode()[7][4] = ' ';
									board.getBoardCode()[7][5] = selectionCode;
									board.getBoardCode()[7][6] = 'k';
									break;
								case 'k':
									board.getBoardCode()[7][7] = ' ';
									board.getBoardCode()[7][6] = selectionCode;
									board.getBoardCode()[7][5] = 'r';
									break;
								case 'R':
									board.getBoardCode()[0][5] = selectionCode;
									board.getBoardCode()[0][6] = 'K';
									board.getBoardCode()[0][4] = ' ';
									break;
								case 'K':
									board.getBoardCode()[0][7] = ' ';
									board.getBoardCode()[0][6] = selectionCode;
									board.getBoardCode()[0][5] = 'R';
									break;
								}
							move = null;
							if (CPU_PieceCodes.contains(destinationCode))
								switch ((int) (Math.random() * 2)) {
								case 0:
									JOptionPane.showMessageDialog(null, "Ouch!!");
									break;
								case 1:
									JOptionPane.showMessageDialog(null, "Why must you do that to me?");
								}
						} catch (InvalidChessMoveException invalid) {
							switch ((int) (Math.random() * 5)) {
							case 0:
								JOptionPane.showMessageDialog(null, "That move is invalid.");
								break;
							case 1:
								JOptionPane.showMessageDialog(null, "That move is not allowed.");
								break;
							case 2:
								JOptionPane.showMessageDialog(null, "Please make a different move.");
								break;
							case 3:
								JOptionPane.showMessageDialog(null, "You need to make a valid move.");
								break;
							case 4:
								JOptionPane.showMessageDialog(null, "The move you are trying to make is invalid.");
								break;
							case 5:
								JOptionPane.showMessageDialog(null, "No, you can't do that.");
							}
							board.getBoardCode()[i][j] = selectionCode;
						}
						board = new ChessBoard(board.getBoardCode(), playerPieceCodes, CPU_PieceCodes);
						if (board.playerOneInCheck()) {
							playerCheckMode = true;
							if (!playerTurn) {
								/*
								 * A player cannot put themselves in check.
								 */
								playerTurn = true;
								board.getBoardCode()[i][j] = selectionCode;
								board.getBoardCode()[v][w] = destinationCode;
							}
						} else
							playerCheckMode = false;
						if (board.playerTwoInCheck()) {
							CPU_CheckMode = true;
							if (playerTurn) {
								playerTurn = false;
								board.getBoardCode()[i][j] = selectionCode;
								board.getBoardCode()[v][w] = destinationCode;
							}
						} else
							CPU_CheckMode = false;
						++compilationStep;
						playRound(primaryStage);
					}
				} else {
					switch ((int) (Math.random() * 3)) {
					case 0:
						JOptionPane.showMessageDialog(null, "What shall I do next?");
						break;
					case 1:
						JOptionPane.showMessageDialog(null, "Hmmm, what move do I make? I may take a while.");
						break;
					case 2:
						JOptionPane.showMessageDialog(null, "Give me a minute..");
					}
					ArrayList<PossibleMove> possibleMoves = new ArrayList<PossibleMove>();
					AI_ChessOffenseStrategy offense = new AI_ChessOffenseStrategy(board, playerPieceCodes,
							CPU_PieceCodes);
					for (PossibleMove move : offense.possibleMoves) {
						if (CPU_PieceCodes.contains(board.getBoardCode()[move.getI()][move.getJ()]))
							try {
								new ChessMove(move.getI(), move.getJ(), move.getV(), move.getW(), board.getBoardCode(),
										false, playerPieceCodes, CPU_PieceCodes);
								possibleMoves.add(move);
							} catch (InvalidChessMoveException ivcme) {

							}
					}
					AI_ChessDefenseStrategy defense = new AI_ChessDefenseStrategy(board, playerPieceCodes,
							CPU_PieceCodes);
					for (PossibleMove move : defense.possibleMoves) {
						if (CPU_PieceCodes.contains(board.getBoardCode()[move.getI()][move.getJ()]))
							try {
								new ChessMove(move.getI(), move.getJ(), move.getV(), move.getW(), board.getBoardCode(),
										false, playerPieceCodes, CPU_PieceCodes);
								possibleMoves.add(move);
							} catch (InvalidChessMoveException ivcme) {

							} finally {
								
							}
					}
					AI_ChessOffenseCheckStrategy check = new AI_ChessOffenseCheckStrategy(board, playerPieceCodes,
							CPU_PieceCodes);
					for (PossibleMove move : check.possibleMoves) {
						if (CPU_PieceCodes.contains(board.getBoardCode()[move.getI()][move.getJ()]))
							try {
								new ChessMove(move.getI(), move.getJ(), move.getV(), move.getW(), board.getBoardCode(),
										false, playerPieceCodes, CPU_PieceCodes);
								possibleMoves.add(move);
							} catch (InvalidChessMoveException ivcme) {

							} finally {
								
							}
					}
					primaryStage.close();
					playerTurn = true;
					if (possibleMoves.isEmpty()) {
						AI_ChessRandomMoves randomMoves = new AI_ChessRandomMoves(board, playerPieceCodes,
								CPU_PieceCodes);
						for (PossibleMove move : randomMoves.possibleMoves) {
							if (CPU_PieceCodes.contains(board.getBoardCode()[move.getI()][move.getJ()]))
								try {
									new ChessMove(move.getI(), move.getJ(), move.getV(), move.getW(),
											board.getBoardCode(), false, playerPieceCodes, CPU_PieceCodes);
									possibleMoves.add(move);
								} catch (InvalidChessMoveException ivcme) {

								} finally {
									
								}
						}
						randomMoves = null;
						if (possibleMoves.isEmpty()) {
							gameActive = false;
							endGame();
						}
					}
					int highestPriority = 13;
					for (PossibleMove move : possibleMoves)
						if (move.getPriority() < highestPriority)
							highestPriority = move.getPriority();
					ArrayList<PossibleMove> smartSelections = new ArrayList<PossibleMove>();
					for (PossibleMove move : possibleMoves)
						if (move.getPriority() == highestPriority)
							smartSelections.add(move);
					int index = (int) (Math.random() * smartSelections.size());
					i = smartSelections.get(index).getI();
					j = smartSelections.get(index).getJ();
					v = smartSelections.get(index).getV();
					w = smartSelections.get(index).getW();
					if (playerPieceCodes.contains(board.getBoardCode()[v][w])) {
						switch ((int) (Math.random() * 3)) {
						case 0:
							JOptionPane.showMessageDialog(null, "Take that!");
							break;
						case 1:
							JOptionPane.showMessageDialog(null, "That's what you get!");
							break;
						case 2:
							JOptionPane.showMessageDialog(null, "It had to be done!");
						}
					} else
						switch ((int) (Math.random() * 2)) {
						case 0:
							JOptionPane.showMessageDialog(null, "Make your next move!");
							break;
						case 1:
							JOptionPane.showMessageDialog(null, "Show me your potential!");
						}
					board.getBoardCode()[v][w] = board.getBoardCode()[i][j];
					board.getBoardCode()[i][j] = ' ';
					possibleMoves = null;
					start(primaryStage);
				}
			});
		}
	}

	private void endGame() {
		JOptionPane.showMessageDialog(null, "Thanks for playing!");
	}
}

class AI_ChessStrategy {
	protected ChessBoard board;
	private ArrayList<Character> userPieceCodes;
	private ArrayList<Character> cpuPieceCodes;
	int x0, y0, x1, y1, x2, y2, x3, y3;
	boolean[][] inOneMoveDanger, inTwoMoveDanger;
	ChessBoard virtualBoard;

	public AI_ChessStrategy(ChessBoard board, ArrayList<Character> userPieceCodes, ArrayList<Character> cpuPieceCodes) {
		setBoard(board);
		setUserPieceCodes(userPieceCodes);
		setCPUPieceCodes(cpuPieceCodes);
		senseOneMoveDangers();
		senseTwoMoveDangers();
		/**
		 * Move priorities(if possible) :
		 */
		// 1. Put you in check mate
		// 2. Put you in check
		// 3. Defend king from two move danger
		// 4. Defend queen from two move danger
		// 5. Attack your queen
		// 6. Defend rook from two move danger
		// 7. Defend bishop from two move danger
		// 8. Attack your rook
		// 9. Attack your bishop
		// 10. Defend knight from two move danger
		// 11. Attack your knight
		// 12. Attack your pawns
		// 13. Defend pawns
		/**
		 * Attacking and defending policies :
		 */
		// Rank A : Queen
		// Rank B : Rook, Bishop, Knight Rank
		// Rank C : Pawn
		/**
		 * A piece may not attack one of your pieces of equal or lower rank if
		 * it puts itself in immediate danger. For example, a rook may not
		 * attack your knight if it puts your rook one move away from being
		 * defeated. However, a pawn may attack your queen even if the pawn
		 * endangers itself.
		 * 
		 *
		 * A defensive move may not be made that causes a CPU piece of greater
		 * rank to become endangered.
		 */
	}

	protected AI_ChessStrategy(ChessBoard board, ArrayList<Character> userPieceCodes,
			ArrayList<Character> cpuPieceCodes, int dummy) {
		setBoard(board);
		setUserPieceCodes(userPieceCodes);
		setCPUPieceCodes(cpuPieceCodes);
		senseOneMoveDangers();
	}

	void senseTwoMoveDangers() {
		inTwoMoveDanger = new boolean[8][8];
		char[][] boardCode = new char[8][8];
		for (x0 = 0; x0 < boardCode.length; ++x0)
			for (y0 = 0; y0 < boardCode[x0].length; ++y0) {
				boardCode[x0][y0] = board.getBoardCode()[x0][y0];
				inTwoMoveDanger[x0][y0] = false;
			}
		virtualBoard = new ChessBoard(boardCode, userPieceCodes, cpuPieceCodes);
		for (x0 = 0; x0 < 8; ++x0)
			for (y0 = 0; y0 < 8; ++y0)
				if (userPieceCodes.contains(boardCode[x0][y0]))
					for (x1 = 0; x1 < 8; ++x1)
						for (y1 = 0; y1 < 8; ++y1) {
							if (!isValid(x0, y0, x1, y1, true))
								continue;
							virtualBoard.getBoardCode()[x1][y1] = board.getBoardCode()[x0][y0];
							virtualBoard.getBoardCode()[x0][y0] = ' ';
							AI_ChessStrategy temp = new AI_ChessStrategy(virtualBoard, userPieceCodes, cpuPieceCodes,
									1);
							for (x2 = 0; x2 < 8; ++x2)
								for (y2 = 0; y2 < 8; ++y2)
									if (!inTwoMoveDanger[x2][y2])
										inTwoMoveDanger[x2][y2] = temp.inOneMoveDanger[x2][y2];
							virtualBoard.getBoardCode()[x0][y0] = board.getBoardCode()[x0][y0];
							virtualBoard.getBoardCode()[x1][y1] = board.getBoardCode()[x1][y1];
						}
	}

	void senseOneMoveDangers() {
		inOneMoveDanger = new boolean[8][8];
		char[][] boardCode = new char[8][8];
		for (x0 = 0; x0 < 8; ++x0)
			for (y0 = 0; y0 < 8; ++y0) {
				boardCode[x0][y0] = board.getBoardCode()[x0][y0];
				inOneMoveDanger[x0][y0] = false;
			}
		virtualBoard = new ChessBoard(boardCode, userPieceCodes, cpuPieceCodes);
		for (x0 = 0; x0 < 8; ++x0)
			for (y0 = 0; y0 < 8; ++y0)
				if (userPieceCodes.contains(board.getBoardCode()[x0][y0]))
					for (x1 = 0; x1 < 8; ++x1)
						for (y1 = 0; y1 < 8; ++y1)
							if (cpuPieceCodes.contains(board.getBoardCode()[x1][y1])) {
								if (isValid(x0, y0, x1, y1, true))
									inOneMoveDanger[x1][y1] = true;
								else if (!inOneMoveDanger[x1][y1])
									inOneMoveDanger[x1][y1] = false;
							}
	}

	boolean isValid(int i, int j, int v, int w, boolean isPlayerOneTurn) {
		char[][] boardCode = virtualBoard.getBoardCode();
		if (i == v && j == w)
			return false;
		if (isPlayerOneTurn && userPieceCodes.contains(boardCode[v][w]))
			return false;
		if (!isPlayerOneTurn && cpuPieceCodes.contains(boardCode[v][w]))
			return false;
		boolean searchPath = searchPath(i, j, v, w, boardCode);
		boolean[][] tileEmpty = tileEmpty(boardCode);
		switch (boardCode[i][j]) {
		case 'R':
		case 'r':
			return (i == v || j == w) && searchPath;
		case 'N':
		case 'n':
			return Math.abs((i - v) * (j - w)) == 2;
		case 'B':
		case 'b':
			return Math.abs(i - v) == Math.abs(j - w) && searchPath;
		case 'Q':
		case 'q':
			return (i == v || j == w || Math.abs(i - v) == Math.abs(j - w)) && searchPath;
		case 'K':
		case 'k':
			return Math.abs(i - v) < 2 && Math.abs(j - w) < 2;
		case 'P':
		case 'p':
			if (!searchPath)
				return false;
			if (!isPlayerOneTurn) {
				if (userPieceCodes.contains(boardCode[v][w]))
					return v == i - 1 && Math.abs(j - w) == 1;
				if (i == 6) {
					return j == w && v > 3 && v < i && tileEmpty[v][w];
				} else
					return j == w && v == i - 1 && tileEmpty[v][w];
			} else {
				if (cpuPieceCodes.contains(boardCode[v][w]))
					return v == i + 1 && Math.abs(j - w) == 1;
				if (i == 1) {
					return j == w && v < 4 && v > i && tileEmpty[v][w];
				} else
					return j == w && v == i + 1 && tileEmpty[v][w];
			}
		default:
			return false;
		}
	}

	boolean[][] tileEmpty(char[][] boardCode) {
		boolean[][] tileEmpty = new boolean[boardCode.length][boardCode[0].length];
		for (int a = 0; a < tileEmpty.length; ++a)
			for (int b = 0; b < tileEmpty[a].length; ++b)
				if (!(userPieceCodes.contains(boardCode[a][b]) || cpuPieceCodes.contains(boardCode[a][b])))
					tileEmpty[a][b] = true;
		return tileEmpty;
	}

	boolean searchPath(int i, int j, int v, int w, char[][] boardCode) {
		int distance = Math.abs(i - v), dX = 0, dY = 0;
		if (i > v)
			dX = -1;
		else if (i < v)
			dX = 1;
		else
			distance = Math.abs(j - w);
		if (j > w)
			dY = -1;
		else if (j < w)
			dY = 1;
		int iteration = 1;
		boolean[][] tileEmpty = tileEmpty(boardCode);
		try {
			while (iteration < distance) {
				if (!tileEmpty[i + (iteration * dX)][j + (iteration * dY)])
					return false;
				++iteration;
			}
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			return false;
		}
		return true;
	}

	public ChessBoard getBoard() {
		return board;
	}

	public ArrayList<Character> getUserPieceCodes() {
		return userPieceCodes;
	}

	public ArrayList<Character> getCpuPieceCodes() {
		return cpuPieceCodes;
	}

	public void setBoard(ChessBoard board) {
		this.board = board;
	}

	public void setUserPieceCodes(ArrayList<Character> userPieceCodes) {
		this.userPieceCodes = userPieceCodes;
	}

	public void setCPUPieceCodes(ArrayList<Character> cpuPieceCodes) {
		this.cpuPieceCodes = cpuPieceCodes;
	}
}

class PossibleMove {
	private short priority;
	private int i, j, v, w;

	public PossibleMove(short priority, int i, int j, int v, int w) {
		setPriority(priority);
		setI(i);
		setJ(j);
		setV(v);
		setW(w);
	}

	public short getPriority() {
		return priority;
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}

	public int getV() {
		return v;
	}

	public int getW() {
		return w;
	}

	public void setPriority(short priority) {
		this.priority = priority;
	}

	public void setI(int i) {
		this.i = i;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public void setV(int v) {
		this.v = v;
	}

	public void setW(int w) {
		this.w = w;
	}
}

class ChessBoard {
	private char[][] boardCode;
	private ArrayList<Character> playerOnePieceCodes, playerTwoPieceCodes;
	int i, j, v, w;
	int playerOneKingCoordinates, playerTwoKingCoordinates;

	public ChessBoard(char[][] boardCode, ArrayList<Character> playerOnePieceCodes,
			ArrayList<Character> playerTwoPieceCodes) {
		setBoardCode(boardCode);
		setPlayerOnePieceCodes(playerOnePieceCodes);
		setPlayerTwoPieceCodes(playerTwoPieceCodes);
		for (int x = 0; x < boardCode.length; ++x)
			for (int y = 0; y < boardCode[x].length; ++y) {
				if (boardCode[x][y] == 'K')
					playerOneKingCoordinates = (10 * (x + 1)) + y + 1;
				if (boardCode[x][y] == 'k')
					playerTwoKingCoordinates = (10 * (x + 1)) + y + 1;
			}
	}

	int playerOneKingCoordinates() {
		for (int x = 0; x < boardCode.length; ++x)
			for (int y = 0; y < boardCode[x].length; ++y)
				if (boardCode[x][y] == 'K') {
					playerOneKingCoordinates = (10 * (x + 1)) + y + 1;
					return (10 * (x + 1)) + y + 1;
				}
		return 0;
	}

	int playerTwoKingCoordinates() {
		for (int x = 0; x < boardCode.length; ++x)
			for (int y = 0; y < boardCode[x].length; ++y)
				if (boardCode[x][y] == 'k') {
					playerTwoKingCoordinates = (10 * (x + 1)) + y + 1;
					return (10 * (x + 1)) + y + 1;
				}
		return 0;
	}

	boolean playerOneInCheck() {
		w = playerOneKingCoordinates() % 10;
		v = playerOneKingCoordinates() - w;
		v /= 10;
		v -= 1;
		w -= 1;
		for (i = 0; i < 8; ++i)
			for (j = 0; j < 8; ++j) {
				if (playerTwoPieceCodes.contains(boardCode[i][j])) {
					try {
						new ChessMove(i, j, v, w, boardCode, false, playerOnePieceCodes, playerTwoPieceCodes);
						return true; /*
										 * A successful virtual move means that
										 * player one's king is one move away
										 * from being defeated by player two.
										 */
					} catch (InvalidChessMoveException invalid) {

					} catch (ArrayIndexOutOfBoundsException outOfBounds) {

					}
				}
			}
		return false;
	}

	boolean playerTwoInCheck() {
		w = playerTwoKingCoordinates() % 10;
		v = playerTwoKingCoordinates() - w;
		v /= 10;
		v -= 1;
		w -= 1;
		for (i = 0; i < 8; ++i)
			for (j = 0; j < 8; ++j) {
				if (playerOnePieceCodes.contains(boardCode[i][j])) {
					try {
						new ChessMove(i, j, v, w, boardCode, true, playerOnePieceCodes, playerTwoPieceCodes);
						return true;/*
									 * A successful virtual move means that
									 * player two's king is one move away from
									 * being defeated by player one.
									 */
					} catch (InvalidChessMoveException invalid) {

					} catch (ArrayIndexOutOfBoundsException outOfBounds) {

					}
				}
			}
		return false;
	}

	boolean playerOneInCheckMate() {
		for (i = 0; i < 8; ++i)
			for (j = 0; j < 8; ++j) {
				if (playerOnePieceCodes.contains(boardCode[i][j])) {
					VirtualMoves vm = new VirtualMoves(i, j, boardCode, boardCode[i][j], true, playerOnePieceCodes,
							playerTwoPieceCodes);
					if (!vm.stillInCheck) /*
											 * If any virtual move occurs that
											 * changes the boolean stillInCheck
											 * to false, then this player is not
											 * in check mate.
											 */
						return false;
				}
			}
		return true;
	}

	boolean playerTwoInCheckMate() {
		for (i = 0; i < 8; ++i)
			for (j = 0; j < 8; ++j) {
				if (playerTwoPieceCodes.contains(boardCode[i][j])) {
					VirtualMoves vm = new VirtualMoves(i, j, boardCode, boardCode[i][j], false, playerOnePieceCodes,
							playerTwoPieceCodes);
					if (!vm.stillInCheck) { /*
											 * If any virtual move occurs that
											 * changes the boolean stillInCheck
											 * to false, then this player is not
											 * in check mate.
											 */
						return false;
					}
				}
			}
		return true;
	}

	void renderMove(ChessMove move) {
		char[][] newBoardCode = new char[boardCode.length][boardCode[0].length];
		for (i = 0; i < newBoardCode.length; ++i)
			for (j = 0; j < newBoardCode[i].length; ++j)
				if (i == move.getI() && j == move.getJ())
					newBoardCode[i][j] = ' ';
				else if (i == move.getV() && j == move.getW())
					newBoardCode[i][j] = move.getBoardCode()[move.getV()][move.getW()];
				else
					newBoardCode[i][j] = boardCode[i][j];
		setBoardCode(newBoardCode);
	}

	void undoMove(ChessMove move) {
		char[][] newBoardCode = new char[boardCode.length][boardCode[0].length];
		for (i = 0; i < newBoardCode.length; ++i)
			for (j = 0; j < newBoardCode[i].length; ++j)
				if (i == move.getV() && j == move.getW())
					newBoardCode[i][j] = ' ';
				else if (i == move.getI() && j == move.getJ())
					newBoardCode[i][j] = move.getBoardCode()[move.getV()][move.getW()];
				else
					newBoardCode[i][j] = boardCode[i][j];
		setBoardCode(newBoardCode);
	}

	public void setBoardCode(char[][] boardCode) {
		this.boardCode = new char[boardCode.length][boardCode[0].length];
		this.boardCode = boardCode.clone();
	}

	public void setPlayerOnePieceCodes(ArrayList<Character> playerOnePieceCodes) {
		this.playerOnePieceCodes = new ArrayList<Character>();
		for (Character pieceCode : playerOnePieceCodes)
			this.playerOnePieceCodes.add(pieceCode);
	}

	public void setPlayerTwoPieceCodes(ArrayList<Character> playerTwoPieceCodes) {
		this.playerTwoPieceCodes = new ArrayList<Character>();
		for (Character pieceCode : playerTwoPieceCodes)
			this.playerTwoPieceCodes.add(pieceCode);
	}

	public char[][] getBoardCode() {
		return boardCode;
	}

	public ArrayList<Character> getPlayerOnePieceCodes() {
		return playerOnePieceCodes;
	}

	public ArrayList<Character> getPlayerTwoPieceCodes() {
		return playerTwoPieceCodes;
	}
}

class ChessMove {
	private int i, j, v, w;
	private char[][] boardCode;
	private boolean isPlayerOneTurn;
	private ArrayList<Character> playerOnePieceCodes, playerTwoPieceCodes;
	boolean validCastlingMove;
	int dX, dY;
	int distance;
	int iteration;

	public ChessMove(int i, int j, int v, int w, char[][] boardCode, boolean isPlayerOneTurn,
			ArrayList<Character> playerOnePieceCodes, ArrayList<Character> playerTwoPieceCodes)
			throws InvalidChessMoveException {
		setI(i);
		setJ(j);
		setV(v);
		setW(w);
		setBoardCode(boardCode);
		setIsPlayerOneTurn(isPlayerOneTurn);
		setPlayerOnePieceCodes(playerOnePieceCodes);
		setPlayerTwoPieceCodes(playerTwoPieceCodes);
		// Ensure a player cannot attack their own pieces.
		if (!isPlayerOneTurn) {
			if (playerTwoPieceCodes.contains(boardCode[v][w]))
				throw new InvalidChessMoveException();
			if (boardCode[i][j] == 'k') {
				boardCode[i][j] = ' ';
				char origVW = boardCode[v][w];
				boardCode[v][w] = 'k';
				int origI = i, origJ = j;
				for (int x = 0; x < boardCode.length; ++x)
					for (int y = 0; y < boardCode[x].length; ++y)
						if (playerOnePieceCodes.contains(boardCode[x][y])) {
							this.i = x;
							this.j = y;
							this.isPlayerOneTurn = true;
							if (isValid()) {
								this.i = origI;
								this.j = origJ;
								boardCode[i][j] = 'k';
								boardCode[v][w] = origVW;
								this.isPlayerOneTurn = false;
								throw new InvalidChessMoveException();
							}
							this.i = origI;
							this.j = origJ;
							this.isPlayerOneTurn = false;
						}
				// Reset
				boardCode[i][j] = 'k';
				boardCode[v][w] = origVW;
			} else {
				char origIJ = boardCode[i][j], origVW = boardCode[v][w];
				boardCode[v][w] = boardCode[i][j];
				boardCode[i][j] = ' ';
				int origI = i, origJ = j, origV = v, origW = w;
				for (int x0 = 0; x0 < 8; ++x0)
					for (int y0 = 0; y0 < 8; ++y0)
						if (boardCode[x0][y0] == 'k')
							for (int x1 = 0; x1 < 8; ++x1)
								for (int y1 = 0; y1 < 8; ++y1)
									if (playerOnePieceCodes.contains(boardCode[x1][y1])) {
										this.i = x1;
										this.j = y1;
										this.v = x0;
										this.w = y0;
										this.isPlayerOneTurn = true;
										if (isValid()) {
											this.i = origI;
											this.j = origJ;
											this.v = origV;
											this.w = origW;
											boardCode[i][j] = origIJ;
											boardCode[v][w] = origVW;
											this.isPlayerOneTurn = false;
											throw new InvalidChessMoveException();
										}
										this.i = origI;
										this.j = origJ;
										this.v = origV;
										this.w = origW;
										this.isPlayerOneTurn = false;
									}
				// Reset
				boardCode[i][j] = origIJ;
				boardCode[v][w] = origVW;
			}
		} else {
			if (playerOnePieceCodes.contains(boardCode[v][w]))
				throw new InvalidChessMoveException();
			if (boardCode[i][j] == 'K') {
				boardCode[i][j] = ' ';
				char origVW = boardCode[v][w];
				boardCode[v][w] = 'K';
				int origI = i, origJ = j;
				for (int x = 0; x < boardCode.length; ++x)
					for (int y = 0; y < boardCode[x].length; ++y)
						if (playerTwoPieceCodes.contains(boardCode[x][y])) {
							this.i = x;
							this.j = y;
							this.isPlayerOneTurn = false;
							if (isValid()) {
								this.i = origI;
								this.j = origJ;
								boardCode[i][j] = 'K';
								boardCode[v][w] = origVW;
								this.isPlayerOneTurn = true;
								throw new InvalidChessMoveException();
							}
							this.i = origI;
							this.j = origJ;
							this.isPlayerOneTurn = true;
						}
				// Reset
				boardCode[i][j] = 'K';
				boardCode[v][w] = origVW;
			} else {
				char origIJ = boardCode[i][j], origVW = boardCode[v][w];
				boardCode[v][w] = boardCode[i][j];
				boardCode[i][j] = ' ';
				int origI = i, origJ = j, origV = v, origW = w;
				for (int x0 = 0; x0 < 8; ++x0)
					for (int y0 = 0; y0 < 8; ++y0)
						if (boardCode[x0][y0] == 'K')
							for (int x1 = 0; x1 < 8; ++x1)
								for (int y1 = 0; y1 < 8; ++y1)
									if (playerTwoPieceCodes.contains(boardCode[x1][y1])) {
										this.i = x1;
										this.j = y1;
										this.v = x0;
										this.w = y0;
										this.isPlayerOneTurn = false;
										if (isValid()) {
											this.i = origI;
											this.j = origJ;
											this.v = origV;
											this.w = origW;
											boardCode[i][j] = origIJ;
											boardCode[v][w] = origVW;
											this.isPlayerOneTurn = true;
											throw new InvalidChessMoveException();
										}
										this.i = origI;
										this.j = origJ;
										this.v = origV;
										this.w = origW;
										this.isPlayerOneTurn = true;
									}
				// Reset
				boardCode[i][j] = origIJ;
				boardCode[v][w] = origVW;
			}
		}
		switch (boardCode[i][j]) {
		case 'k':
			if (i == 7 && j == 4)
				validCastlingMove = v == 7 && w == 6 && boardCode[7][7] == 'r';
			break;
		case 'r':
			if (i == 7 && j == 7)
				validCastlingMove = v == 7 && w == 5 && boardCode[7][4] == 'k';
			break;
		case 'K':
			if (i == 0 && j == 4)
				validCastlingMove = v == 0 && w == 6 && boardCode[0][7] == 'R';
			break;
		case 'R':
			if (i == 0 && j == 7)
				validCastlingMove = v == 0 && w == 5 && boardCode[0][4] == 'K';
		}
		if (!isValid() || (i == v && j == w))
			throw new InvalidChessMoveException();
	}

	boolean isValid() {
		if (validCastlingMove) {
			try {
				searchPath();
				return true;
			} catch (InvalidChessMoveException invalid) {
				return false;
			}
		}
		/*
		 * Different boolean finding algorithms are designed here for the
		 * different rules of moving different pieces.
		 */
		switch (boardCode[i][j]) {
		case 'R':
		case 'r':
			try {
				searchPath();
				return i == v || j == w;
			} catch (InvalidChessMoveException invalid) {
				return false;
			}
		case 'N':
		case 'n':
			return Math.abs((i - v) * (j - w)) == 2;
		case 'B':
		case 'b':
			try {
				searchPath();
				return Math.abs(i - v) == Math.abs(j - w);
			} catch (InvalidChessMoveException invalid) {
				return false;
			}
		case 'Q':
		case 'q':
			try {
				searchPath();
				return i == v || j == w || Math.abs(i - v) == Math.abs(j - w);
			} catch (InvalidChessMoveException invalid) {
				return false;
			}
		case 'K':
		case 'k':
			return Math.abs(i - v) < 2 && Math.abs(j - w) < 2;
		case 'P':
		case 'p':
			try {
				searchPath();
				if (!isPlayerOneTurn) {
					if (playerOnePieceCodes.contains(boardCode[v][w]))
						return v == i - 1 && Math.abs(j - w) == 1;
					if (i == 6) {
						return j == w && v > 3 && v < i && tileEmpty()[v][w];
					} else
						return j == w && v == i - 1 && tileEmpty()[v][w];
				} else {
					if (playerTwoPieceCodes.contains(boardCode[v][w]))
						return v == i + 1 && Math.abs(j - w) == 1;
					if (i == 1) {
						return j == w && v < 4 && v > i && tileEmpty()[v][w];
					} else
						return j == w && v == i + 1 && tileEmpty()[v][w];
				}
			} catch (InvalidChessMoveException invalid) {
				return false;
			}
		default:
			return false;
		}
	}

	boolean[][] tileEmpty() { /*
								 * A boolean array that shows which tiles are
								 * unoccupied
								 */
		boolean[][] tileEmpty = new boolean[boardCode.length][boardCode[0].length];
		for (int i = 0; i < tileEmpty.length; ++i)
			for (int j = 0; j < tileEmpty[i].length; ++j)
				if (!(playerOnePieceCodes.contains(boardCode[i][j]) || playerTwoPieceCodes.contains(boardCode[i][j])))
					tileEmpty[i][j] = true;
		return tileEmpty;
	}

	/*
	 * Most move attempts require a path to be open to proceed.
	 */
	void searchPath() throws InvalidChessMoveException {
		dX = 0;
		dY = 0;
		distance = Math.abs(i - v);
		if (i > v)
			dX = -1;
		else if (i < v)
			dX = 1;
		else
			distance = Math.abs(j - w);
		if (j > w)
			dY = -1;
		else if (j < w)
			dY = 1;
		iteration = 1;
		try {
			while (iteration < distance) {
				if (!tileEmpty()[i + (iteration * dX)][j + (iteration * dY)])
					throw new InvalidChessMoveException();
				++iteration;
			}
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			throw new InvalidChessMoveException();
		}
	}

	// Create mutators.

	public void setI(int i) {
		this.i = i;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public void setV(int v) {
		this.v = v;
	}

	public void setW(int w) {
		this.w = w;
	}

	public void setBoardCode(char[][] boardCode) {
		this.boardCode = new char[boardCode.length][boardCode[0].length];
		this.boardCode = boardCode.clone();
	}

	public void setIsPlayerOneTurn(boolean isPlayerOneTurn) {
		this.isPlayerOneTurn = isPlayerOneTurn;
	}

	public void setPlayerOnePieceCodes(ArrayList<Character> playerOnePieceCodes) {
		this.playerOnePieceCodes = new ArrayList<Character>();
		this.playerOnePieceCodes.addAll(playerOnePieceCodes);
	}

	public void setPlayerTwoPieceCodes(ArrayList<Character> playerTwoPieceCodes) {
		this.playerTwoPieceCodes = new ArrayList<Character>();
		this.playerTwoPieceCodes.addAll(playerTwoPieceCodes);
	}

	// Create accessors.

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}

	public int getV() {
		return v;
	}

	public int getW() {
		return w;
	}

	public char[][] getBoardCode() {
		return boardCode;
	}

	public boolean getIsPlayerOneTurn() {
		return isPlayerOneTurn;
	}

	public ArrayList<Character> getPlayerOnePieceCodes() {
		return playerOnePieceCodes;
	}

	public ArrayList<Character> getPlayerTwoPieceCodes() {
		return playerTwoPieceCodes;
	}
}

class InvalidChessMoveException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidChessMoveException() {

	}
}

class VirtualMoves {
	private int i, j;
	private char[][] boardCode;
	private char selectionCode;
	private boolean isPlayerOneTurn;
	private char[][] virtualBoardCode;
	private ArrayList<Character> playerOnePieceCodes, playerTwoPieceCodes;
	int v, w;
	char destinationCode;
	boolean stillInCheck;
	ChessBoard virtualChessBoard;

	public VirtualMoves(int i, int j, char[][] boardCode, char selectionCode, boolean isPlayerOneTurn,
			ArrayList<Character> playerOnePieceCodes, ArrayList<Character> playerTwoPieceCodes) {
		setI(i);
		setJ(j);
		setBoardCode(boardCode);
		setSelectionCode(selectionCode);
		setIsPlayerOneTurn(isPlayerOneTurn);
		setPlayerOnePieceCodes(playerOnePieceCodes);
		setPlayerTwoPieceCodes(playerTwoPieceCodes);
		virtualBoardCode = new char[boardCode.length][boardCode[0].length];
		virtualBoardCode = boardCode.clone();
		stillInCheck = true;
		virtualChessBoard = new ChessBoard(boardCode, playerOnePieceCodes, playerTwoPieceCodes);
		switch (selectionCode) {
		case 'R':
		case 'r':
			runAllVirtualRookMoves();
			break;
		case 'N':
		case 'n':
			runAllVirtualKnightMoves();
			break;
		case 'B':
		case 'b':
			runAllVirtualBishopMoves();
			break;
		case 'Q':
		case 'q':
			runAllVirtualQueenMoves();
			break;
		case 'K':
		case 'k':
			runAllVirtualKingMoves();
			break;
		case 'P':
		case 'p':
			runAllVirtualPawnMoves();
		}
	}

	void runAllVirtualRookMoves() {
		w = j;
		for (v = i + 1; v <= i + 8; ++v) {// Moves in the vertical direction
			try {
				new ChessMove(i, j, v % 8, w, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
				} else {
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
		v = i;
		for (w = j + 1; w <= j + 8; ++w) {// Moves in the horizontal direction
			try {
				new ChessMove(i, j, v, w % 8, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
				} else {
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
	}

	void runAllVirtualKnightMoves() { /*
										 * There are 8 different possible moves
										 * of the knight.
										 */
		int possibleMoveIndex = 0;
		while (possibleMoveIndex < 8) {
			switch (possibleMoveIndex) {
			case 0:
				v = i + 1;
				w = j + 2;
				break;
			case 1:
				v = i + 1;
				w = j - 2;
				break;
			case 2:
				v = i - 1;
				w = j + 2;
				break;
			case 3:
				v = i - 1;
				w = j - 2;
				break;
			case 4:
				v = i + 2;
				w = j + 1;
				break;
			case 5:
				v = i + 2;
				w = j - 1;
				break;
			case 6:
				v = i - 2;
				w = j + 1;
				break;
			case 7:
				v = i - 2;
				w = j - 1;
			}
			++possibleMoveIndex;
			try {
				new ChessMove(i, j, v, w, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
				} else {
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
	}

	void runAllVirtualBishopMoves() {
		int trial;
		trial = 1;
		while (trial <= 8) {
			v = i + trial;
			w = j + trial;
			++trial;
			try {
				new ChessMove(i, j, v % 8, w % 8, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
				} else {
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
		trial = 1;
		while (trial <= 8) {
			v = i + trial;
			w = j - trial;
			++trial;
			try {
				new ChessMove(i, j, v % 8, w % 8, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
				} else {
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
	}

	void runAllVirtualQueenMoves() { /*
										 * The virtual queen moves must follow
										 * the previous algorithms used in the
										 * virtual rook moves and virtual bishop
										 * moves.
										 */
		int trial;
		trial = 1;
		while (trial <= 8) {
			v = i + trial;
			w = j + trial;
			++trial;
			try {
				new ChessMove(i, j, v % 8, w % 8, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
				} else {
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
		trial = 1;
		while (trial <= 8) {
			v = i + trial;
			w = j - trial;
			++trial;
			try {
				new ChessMove(i, j, v % 8, w % 8, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
				} else {
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
		w = j;
		for (v = i + 1; v <= i + 8; ++v) {// Moves in the vertical direction
			try {
				new ChessMove(i, j, v % 8, w, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
				} else {
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
		v = i;
		for (w = j + 1; w <= j + 8; ++w) {// Moves in the horizontal direction
			try {
				new ChessMove(i, j, v, w % 8, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
				} else {
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
	}

	void runAllVirtualKingMoves() {
		int possibleMoveIndex = 0;
		while (possibleMoveIndex < 8) {
			switch (possibleMoveIndex) {
			case 0:
				v = i + 1;
				w = j + 1;
				break;
			case 1:
				v = i + 1;
				w = j;
				break;
			case 2:
				v = i + 1;
				w = j - 1;
				break;
			case 3:
				v = i;
				w = j - 1;
				break;
			case 4:
				v = i - 1;
				w = j - 1;
				break;
			case 5:
				v = i - 1;
				w = j;
				break;
			case 6:
				v = i - 1;
				w = j - 1;
				break;
			case 7:
				v = i;
				w = j + 1;
			}
			++possibleMoveIndex;
			try {
				new ChessMove(i, j, v, w, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
				virtualBoardCode[i][j] = ' ';
				destinationCode = virtualBoardCode[v % 8][w];
				virtualBoardCode[v % 8][w] = selectionCode;
				virtualChessBoard.setBoardCode(virtualBoardCode);
				if (!isPlayerOneTurn) {
					virtualChessBoard.playerTwoKingCoordinates += w;
					virtualChessBoard.playerTwoKingCoordinates += 10 * v;
					if (!virtualChessBoard.playerTwoInCheck())
						stillInCheck = false;
					virtualChessBoard.playerTwoKingCoordinates -= w;
					virtualChessBoard.playerTwoKingCoordinates -= 10 * v;
				} else {
					virtualChessBoard.playerOneKingCoordinates += w;
					virtualChessBoard.playerOneKingCoordinates += 10 * v;
					if (!virtualChessBoard.playerOneInCheck())
						stillInCheck = false;
					virtualChessBoard.playerOneKingCoordinates -= w;
					virtualChessBoard.playerOneKingCoordinates -= 10 * v;
				}
				virtualBoardCode[v % 8][w] = destinationCode;
				virtualBoardCode[i][j] = selectionCode;
			} catch (InvalidChessMoveException invalid) {

			} catch (ArrayIndexOutOfBoundsException outOfBounds) {

			}
			virtualBoardCode = boardCode.clone();
		}
	}

	void runAllVirtualPawnMoves() {
		int possibleMoveIndex = 0;
		if (!isPlayerOneTurn) {
			if (i == 6) {
				while (possibleMoveIndex < 4) {
					switch (possibleMoveIndex) {
					case 0:
						v = 4;
						w = j;
						break;
					case 1:
						v = 5;
						w = j;
						break;
					case 2:
						v = 5;
						w = j - 1;
						break;
					case 3:
						v = 5;
						w = j + 1;
					}
					++possibleMoveIndex;
					try {
						new ChessMove(i, j, v, w, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
						virtualBoardCode[i][j] = ' ';
						destinationCode = virtualBoardCode[v % 8][w];
						virtualBoardCode[v % 8][w] = selectionCode;
						virtualChessBoard.setBoardCode(virtualBoardCode);
						if (!virtualChessBoard.playerTwoInCheck())
							stillInCheck = false;
						virtualBoardCode[v % 8][w] = destinationCode;
						virtualBoardCode[i][j] = selectionCode;
					} catch (InvalidChessMoveException invalid) {

					} catch (ArrayIndexOutOfBoundsException outOfBounds) {

					}
					virtualBoardCode = boardCode.clone();
				}
			} else {
				v = i - 1;
				while (possibleMoveIndex < 3) {
					switch (possibleMoveIndex) {
					case 0:
						w = j - 1;
						break;
					case 1:
						w = j;
						break;
					case 2:
						w = j + 1;
					}
					++possibleMoveIndex;
					try {
						new ChessMove(i, j, v, w, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
						virtualBoardCode[i][j] = ' ';
						destinationCode = virtualBoardCode[v % 8][w];
						virtualBoardCode[v % 8][w] = selectionCode;
						virtualChessBoard.setBoardCode(virtualBoardCode);
						if (!virtualChessBoard.playerTwoInCheck())
							stillInCheck = false;
						virtualBoardCode[v % 8][w] = destinationCode;
						virtualBoardCode[i][j] = selectionCode;
					} catch (InvalidChessMoveException invalid) {

					} catch (ArrayIndexOutOfBoundsException outOfBounds) {

					}
					virtualBoardCode = boardCode.clone();
				}
			}
		} else {
			if (i == 1) {
				while (possibleMoveIndex < 4) {
					switch (possibleMoveIndex) {
					case 0:
						v = 3;
						w = j;
						break;
					case 1:
						v = 2;
						w = j;
						break;
					case 2:
						v = 2;
						w = j - 1;
						break;
					case 3:
						v = 2;
						w = j + 1;
					}
					++possibleMoveIndex;
					try {
						new ChessMove(i, j, v, w, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
						virtualBoardCode[i][j] = ' ';
						destinationCode = virtualBoardCode[v % 8][w];
						virtualBoardCode[v % 8][w] = selectionCode;
						virtualChessBoard.setBoardCode(virtualBoardCode);
						if (!virtualChessBoard.playerTwoInCheck())
							stillInCheck = false;
						virtualBoardCode[v % 8][w] = destinationCode;
						virtualBoardCode[i][j] = selectionCode;
					} catch (InvalidChessMoveException invalid) {

					} catch (ArrayIndexOutOfBoundsException outOfBounds) {

					}
					virtualBoardCode = boardCode.clone();
				}
			} else {
				v = i + 1;
				while (possibleMoveIndex < 3) {
					switch (possibleMoveIndex) {
					case 0:
						w = j - 1;
						break;
					case 1:
						w = j;
						break;
					case 2:
						w = j + 1;
					}
					++possibleMoveIndex;
					try {
						new ChessMove(i, j, v, w, boardCode, isPlayerOneTurn, playerOnePieceCodes, playerTwoPieceCodes);
						virtualBoardCode[i][j] = ' ';
						destinationCode = virtualBoardCode[v % 8][w];
						virtualBoardCode[v % 8][w] = selectionCode;
						virtualChessBoard.setBoardCode(virtualBoardCode);
						if (!virtualChessBoard.playerTwoInCheck())
							stillInCheck = false;
						virtualBoardCode[v % 8][w] = destinationCode;
						virtualBoardCode[i][j] = selectionCode;
					} catch (InvalidChessMoveException invalid) {

					} catch (ArrayIndexOutOfBoundsException outOfBounds) {

					}
					virtualBoardCode = boardCode.clone();
				}
			}
		}
	}

	public void setI(int i) {
		this.i = i;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public void setBoardCode(char[][] boardCode) {
		this.boardCode = new char[boardCode.length][boardCode[0].length];
		this.boardCode = boardCode.clone();
	}

	public void setSelectionCode(char selectionCode) {
		this.selectionCode = selectionCode;
	}

	public void setIsPlayerOneTurn(boolean isPlayerOneTurn) {
		this.isPlayerOneTurn = isPlayerOneTurn;
	}

	public void setPlayerOnePieceCodes(ArrayList<Character> playerOnePieceCodes) {
		this.playerOnePieceCodes = new ArrayList<Character>();
		this.playerOnePieceCodes.addAll(playerOnePieceCodes);
	}

	public void setPlayerTwoPieceCodes(ArrayList<Character> playerTwoPieceCodes) {
		this.playerTwoPieceCodes = new ArrayList<Character>();
		this.playerTwoPieceCodes.addAll(playerTwoPieceCodes);
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}

	public char[][] getBoardCode() {
		return boardCode;
	}

	public char getSelectionCode() {
		return selectionCode;
	}

	public boolean getIsPlayerOneTurn() {
		return isPlayerOneTurn;
	}

	public ArrayList<Character> getPlayerOnePieceCodes() {
		return playerOnePieceCodes;
	}

	public ArrayList<Character> getPlayerTwoPieceCodes() {
		return playerTwoPieceCodes;
	}
}

class AI_ChessDefenseStrategy extends AI_ChessStrategy {
	ArrayList<PossibleMove> possibleMoves;

	public AI_ChessDefenseStrategy(ChessBoard board, ArrayList<Character> userPieceCodes,
			ArrayList<Character> cpuPieceCodes) {
		super(board, userPieceCodes, cpuPieceCodes);
		possibleMoves = new ArrayList<PossibleMove>();
		for (x0 = 0; x0 < 8; ++x0) {
			for (y0 = 0; y0 < 8; ++y0) {
				if (inTwoMoveDanger[x0][y0] || inOneMoveDanger[x0][y0])
					for (x1 = 0; x1 < 8; ++x1) {
						for (y1 = 0; y1 < 8; ++y1) {
							if (cpuPieceCodes.contains(board.getBoardCode()[x1][y1]))
								for (x2 = 0; x2 < 8; ++x2)
									for (y2 = 0; y2 < 8; ++y2) {
										if (!isValid(x1, y1, x2, y2, false))
											continue;
										virtualBoard.getBoardCode()[x1][y1] = ' ';
										virtualBoard.getBoardCode()[x2][y2] = board.getBoardCode()[x1][y1];
										AI_ChessStrategy possibleStrategy = new AI_ChessStrategy(virtualBoard,
												userPieceCodes, cpuPieceCodes);
										int a = x0, b = y0;
										if (x1 == x0 && y1 == y0) {
											a = x2;
											b = y2;
										}
										if (!(possibleStrategy.inOneMoveDanger[a][b]
												|| possibleStrategy.inTwoMoveDanger[a][b])) {
											boolean shouldAdd = false;
											PossibleMove pm = new PossibleMove((short) 13, x1, y1, x2, y2);
											switch (virtualBoard.getBoardCode()[a][b]) {
											case 'p':
												shouldAdd = true;
												pm.setPriority((short) 12);
												for (x3 = 0; x3 < 8; ++x3)
													for (y3 = 0; y3 < 8; ++y3)
														if ((possibleStrategy.inOneMoveDanger[x3][y3]
																|| possibleStrategy.inTwoMoveDanger[x3][y3])
																&& !(inOneMoveDanger[x3][y3]
																		|| inTwoMoveDanger[x3][y3]))
															switch (virtualBoard.getBoardCode()[x3][y3]) {
															case 'p':
															case 'n':
															case 'b':
															case 'r':
															case 'q':
																shouldAdd = false;
															}
												break;
											case 'n':
												shouldAdd = true;
												pm.setPriority((short) 9);
												for (x3 = 0; x3 < 8; ++x3)
													for (y3 = 0; y3 < 8; ++y3)
														if ((possibleStrategy.inOneMoveDanger[x3][y3]
																|| possibleStrategy.inTwoMoveDanger[x3][y3])
																&& !(inOneMoveDanger[x3][y3]
																		|| inTwoMoveDanger[x3][y3]))
															switch (virtualBoard.getBoardCode()[x3][y3]) {
															case 'n':
															case 'b':
															case 'r':
															case 'q':
																shouldAdd = false;
															}
												break;
											case 'b':
												shouldAdd = true;
												pm.setPriority((short) 6);
												for (x3 = 0; x3 < 8; ++x3)
													for (y3 = 0; y3 < 8; ++y3)
														if ((possibleStrategy.inOneMoveDanger[x3][y3]
																|| possibleStrategy.inTwoMoveDanger[x3][y3])
																&& !(inOneMoveDanger[x3][y3]
																		|| inTwoMoveDanger[x3][y3]))
															switch (virtualBoard.getBoardCode()[x3][y3]) {
															case 'n':
															case 'b':
															case 'r':
															case 'q':
																shouldAdd = false;
															}
												break;
											case 'r':
												shouldAdd = true;
												pm.setPriority((short) 5);
												for (x3 = 0; x3 < 8; ++x3)
													for (y3 = 0; y3 < 8; ++y3)
														if ((possibleStrategy.inOneMoveDanger[x3][y3]
																|| possibleStrategy.inTwoMoveDanger[x3][y3])
																&& !(inOneMoveDanger[x3][y3]
																		|| inTwoMoveDanger[x3][y3]))
															switch (virtualBoard.getBoardCode()[x3][y3]) {
															case 'n':
															case 'b':
															case 'r':
															case 'q':
																shouldAdd = false;
															}
												break;
											case 'q':
												shouldAdd = true;
												pm.setPriority((short) 3);
												break;
											case 'k':
												shouldAdd = true;
												pm.setPriority((short) 2);
												for (x3 = 0; x3 < 8; ++x3)
													for (y3 = 0; y3 < 8; ++y3)
														if ((possibleStrategy.inOneMoveDanger[x3][y3]
																|| possibleStrategy.inTwoMoveDanger[x3][y3])
																&& !(inOneMoveDanger[x3][y3]
																		|| inTwoMoveDanger[x3][y3]))
															switch (virtualBoard.getBoardCode()[x3][y3]) {
															case 'n':
															case 'b':
															case 'r':
															case 'q':
																shouldAdd = false;
															}
											}
											if (possibleStrategy.getBoard().playerTwoInCheck())
												shouldAdd = false;
											if (shouldAdd)
												possibleMoves.add(pm);
											possibleStrategy = null;
										}
										virtualBoard.getBoardCode()[x1][y1] = board.getBoardCode()[x1][y1];
										virtualBoard.getBoardCode()[x2][y2] = board.getBoardCode()[x2][y2];
									}
						}
					}
			}
		}
	}
}

class AI_ChessOffenseCheckStrategy extends AI_ChessStrategy {
	ArrayList<PossibleMove> possibleMoves;

	public AI_ChessOffenseCheckStrategy(ChessBoard board, ArrayList<Character> userPieceCodes,
			ArrayList<Character> cpuPieceCodes) {
		super(board, userPieceCodes, cpuPieceCodes);
		possibleMoves = new ArrayList<PossibleMove>();
		for (x0 = 0; x0 < 8; ++x0)
			for (y0 = 0; y0 < 8; ++y0)
				if (cpuPieceCodes.contains(board.getBoardCode()[x0][y0]))
					for (x1 = 0; x1 < 8; ++x1)
						for (y1 = 0; y1 < 8; ++y1) {
							if (!isValid(x0, y0, x1, y1, false))
								continue;
							virtualBoard.getBoardCode()[x0][y0] = ' ';
							virtualBoard.getBoardCode()[x1][y1] = board.getBoardCode()[x0][y0];
							AI_ChessStrategy possibleStrategy = new AI_ChessStrategy(virtualBoard, userPieceCodes,
									cpuPieceCodes);
							if (virtualBoard.playerOneInCheckMate()) {
								possibleMoves.add(new PossibleMove((short) 0, x0, y0, x1, y1));
								break;
							} else if (virtualBoard.playerOneInCheck()) {
								boolean shouldAdd = true;
								for (x2 = 0; x2 < 8; ++x2)
									for (y2 = 0; y2 < 8; ++y2)
										if (possibleStrategy.inOneMoveDanger[x2][y2])
											switch (virtualBoard.getBoardCode()[x2][y2]) {
											case 'q':
											case 'r':
											case 'b':
											case 'n':
												shouldAdd = false;
												break;
											case 'p':
												if ((int) (Math.random() * 6) < 5)
													shouldAdd = false;
											}
								if (possibleStrategy.getBoard().playerTwoInCheck())
									shouldAdd = false;
								if (shouldAdd)
									possibleMoves.add(new PossibleMove((short) 1, x0, y0, x1, y1));
							}
							possibleStrategy = null;
							virtualBoard.getBoardCode()[x0][y0] = board.getBoardCode()[x0][y0];
							virtualBoard.getBoardCode()[x1][y1] = board.getBoardCode()[x1][y1];
						}
	}
}

class AI_ChessOffenseStrategy extends AI_ChessStrategy {
	ArrayList<PossibleMove> possibleMoves;

	public AI_ChessOffenseStrategy(ChessBoard board, ArrayList<Character> userPieceCodes,
			ArrayList<Character> cpuPieceCodes) {
		super(board, userPieceCodes, cpuPieceCodes);
		possibleMoves = new ArrayList<PossibleMove>();
		for (x0 = 0; x0 < 8; ++x0)
			for (y0 = 0; y0 < 8; ++y0)
				if (cpuPieceCodes.contains(board.getBoardCode()[x0][y0]))
					for (x1 = 0; x1 < 8; ++x1)
						for (y1 = 0; y1 < 8; ++y1) {
							if (userPieceCodes.contains(board.getBoardCode()[x1][y1])) {
								if (!isValid(x0, y0, x1, y1, false))
									continue;
								virtualBoard.getBoardCode()[x0][y0] = ' ';
								virtualBoard.getBoardCode()[x1][y1] = board.getBoardCode()[x0][y0];
								AI_ChessStrategy possibleStrategy = new AI_ChessStrategy(virtualBoard, userPieceCodes,
										cpuPieceCodes);
								boolean shouldAdd = false;
								PossibleMove pm = new PossibleMove((short) 13, x0, y0, x1, y1);
								switch (board.getBoardCode()[x1][y1]) {
								case 'P':
									shouldAdd = true;
									pm.setPriority((short) 11);
									for (x2 = 0; x2 < 8; ++x2)
										for (y2 = 0; y2 < 8; ++y2)
											if (possibleStrategy.inOneMoveDanger[x2][y2])
												switch (virtualBoard.getBoardCode()[x2][y2]) {
												case 'n':
												case 'b':
												case 'r':
												case 'q':
												case 'p':
													shouldAdd = false;
												}
									break;
								case 'N':
									shouldAdd = true;
									pm.setPriority((short) 10);
									for (x2 = 0; x2 < 8; ++x2)
										for (y2 = 0; y2 < 8; ++y2)
											if (possibleStrategy.inOneMoveDanger[x2][y2])
												switch (virtualBoard.getBoardCode()[x2][y2]) {
												case 'n':
												case 'b':
												case 'r':
												case 'q':
													shouldAdd = false;
												}
									break;
								case 'B':
									shouldAdd = true;
									pm.setPriority((short) 8);
									for (x2 = 0; x2 < 8; ++x2)
										for (y2 = 0; y2 < 8; ++y2)
											if (possibleStrategy.inOneMoveDanger[x2][y2])
												switch (virtualBoard.getBoardCode()[x2][y2]) {
												case 'n':
												case 'b':
												case 'r':
												case 'q':
													shouldAdd = false;
												}
									break;
								case 'R':
									shouldAdd = true;
									pm.setPriority((short) 7);
									for (x2 = 0; x2 < 8; ++x2)
										for (y2 = 0; y2 < 8; ++y2)
											if (possibleStrategy.inOneMoveDanger[x2][y2])
												switch (virtualBoard.getBoardCode()[x2][y2]) {
												case 'n':
												case 'b':
												case 'r':
												case 'q':
													shouldAdd = false;
												}
									break;
								case 'Q':
									shouldAdd = true;
									pm.setPriority((short) 4);
									for (x2 = 0; x2 < 8; ++x2)
										for (y2 = 0; y2 < 8; ++y2)
											if (possibleStrategy.inOneMoveDanger[x2][y2])
												if (virtualBoard.getBoardCode()[x2][y2] == 'q')
													shouldAdd = false;
								}
								if (possibleStrategy.getBoard().playerTwoInCheck())
									shouldAdd = false;
								if (shouldAdd)
									possibleMoves.add(pm);
								virtualBoard.getBoardCode()[x0][y0] = board.getBoardCode()[x0][y0];
								virtualBoard.getBoardCode()[x1][y1] = board.getBoardCode()[x1][y1];
							}
						}
	}
}

class AI_ChessRandomMoves extends AI_ChessStrategy {
	ArrayList<PossibleMove> possibleMoves;

	public AI_ChessRandomMoves(ChessBoard board, ArrayList<Character> userPieceCodes,
			ArrayList<Character> cpuPieceCodes) {
		super(board, userPieceCodes, cpuPieceCodes);
		possibleMoves = new ArrayList<PossibleMove>();
		short priority = 13;
		for (x0 = 0; x0 < 8; ++x0)
			for (y0 = 0; y0 < 8; ++y0)
				if (cpuPieceCodes.contains(board.getBoardCode()[x0][y0]))
					for (x1 = 0; x1 < 8; ++x1)
						for (y1 = 0; y1 < 8; ++y1) {
							if (!isValid(x0, y0, x1, y1, false))
								continue;
							virtualBoard.getBoardCode()[x1][y1] = board.getBoardCode()[x0][y0];
							virtualBoard.getBoardCode()[x0][y0] = ' ';
							AI_ChessStrategy possibleStrategy = new AI_ChessStrategy(virtualBoard, userPieceCodes,
									cpuPieceCodes);
							if (!inTwoMoveDanger[x0][y0] && (possibleStrategy.inTwoMoveDanger[x1][y1])) {
								switch (board.getBoardCode()[x0][y0]) {
								case 'p':
									priority += 1;
									break;
								case 'n':
								case 'b':
								case 'r':
									priority += 2;
									break;
								case 'q':
									priority += 3;
								}
							}
							if (!inOneMoveDanger[x0][y0] && (possibleStrategy.inOneMoveDanger[x1][y1])) {
								switch (board.getBoardCode()[x0][y0]) {
								case 'p':
									priority += 4;
									break;
								case 'n':
								case 'b':
								case 'r':
									priority += 5;
									break;
								case 'q':
									priority += 6;
								}
							}
							possibleStrategy = null;
							possibleMoves.add(new PossibleMove(priority, x0, y0, x1, y1));
							priority = 13;
							virtualBoard.getBoardCode()[x0][y0] = board.getBoardCode()[x0][y0];
							virtualBoard.getBoardCode()[x1][y1] = board.getBoardCode()[x1][y1];
						}
	}
}
