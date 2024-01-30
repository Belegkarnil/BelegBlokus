/*
 *  Copyright 2024 Belegkarnil
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 *  associated documentation files (the “Software”), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 *  so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 *  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package be.belegkarnil.game.board.blokus.strategy;

import be.belegkarnil.game.board.blokus.Board;
import be.belegkarnil.game.board.blokus.Piece;
import be.belegkarnil.game.board.blokus.Point;
import be.belegkarnil.game.board.blokus.Strategy;
import be.belegkarnil.game.board.blokus.event.GameListener;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class represents a {@link Strategy} that randomly chooses an initial position for
 * all possibles positions. Then it randomly chooses a piece from all possible piece and
 * try to play. If the piece cannot be place, it tries another random piece. If no piece can
 * be placed, it tries another random position. If no piece can be placed, the strategy skip
 * with a null {@link Piece}.
 *
 * @author Belegkarnil
 */
public class RandomStrategy implements Strategy {
	@Override
	public GameListener register() {
		return null;
	}
	
	@Override
	public GameListener unregister() {
		return null;
	}
	
	@Override
	public Piece action(List<Piece> pieces, Board board, Board.Cell type, List<Piece> opponent) {
		try {
			Set<Point> uniqueCanPlace = new HashSet<Point>();
			for (Point p : board.getStartingPositions()) {
				uniqueCanPlace.add(new Point(p.getX()+1,p.getY()));
				uniqueCanPlace.add(new Point(p.getX()-1,p.getY()));
				uniqueCanPlace.add(new Point(p.getX(),p.getY()+1));
				uniqueCanPlace.add(new Point(p.getX(),p.getY()-1));
			}
			for (int i = 0; i < board.getSize(); i++) {
				for (int j = 0; j < board.getSize(); j++) {
					if (board.getCellAt(i, j) == type) {
						Point corner;
						corner = new Point(i - 1, j);
						if (board.isInBounds(corner) && board.inCellEmpty(corner)) uniqueCanPlace.add(corner);
						corner = new Point(i, j - 1);
						if (board.isInBounds(corner) && board.inCellEmpty(corner)) uniqueCanPlace.add(corner);
						corner = new Point(i + 1, j);
						if (board.isInBounds(corner) && board.inCellEmpty(corner)) uniqueCanPlace.add(corner);
						corner = new Point(i, j + 1);
						if (board.isInBounds(corner) && board.inCellEmpty(corner)) uniqueCanPlace.add(corner);
					}
				}
			}
			
			List<Point> initPos = new LinkedList<Point>();
			initPos.addAll(uniqueCanPlace);
			
			List<Piece> copy = new LinkedList<Piece>();
			copy.addAll(pieces);
			// random pieces and initPos
			Collections.shuffle(copy);
			Collections.shuffle(initPos);
			
			// For each piece, bruteforce at each pos
			for (Point pos : initPos) {
				for (Piece p : copy) {
					p.reset();
					for (int shape=0; shape < p.countShapes(); shape++) {
						for (Point corner : p.getCorners()) {
							final int dx = pos.getX() - corner.getX();
							final int dy = pos.getY() - corner.getY();
							p.translate(dx, dy);
							if (board.canPlace(p, type)) {
								return p;
							} else {
								p.translate(-dx, -dy);
							}
						}
						p.next();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return null;
	}
}
