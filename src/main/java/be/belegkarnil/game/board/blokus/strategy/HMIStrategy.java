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

import java.util.List;

/**
 * This class is a special a {@link Strategy} class that is recognized by the GUI. This class allow
 * a human player to select a {@link Piece} and a position {@link Point}.
 *
 * @author Belegkarnil
 */
public class HMIStrategy implements Strategy {
	private final Object lock;
	private Piece action;
	private boolean undefined;
	public HMIStrategy(){
		lock			= new Object();
		undefined	= true;
		action		= null;
	}
	
	public void setAction(Piece action){
		synchronized (lock) {
			this.action = action;
			undefined	= false;
		}
	}
	
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
		Piece piece = null;
		boolean undefined = true;
		while(undefined) {
			synchronized (lock) {
				undefined = this.undefined;
				if(!undefined){
					piece				= action;
					action			= null;
					this.undefined	= true;
				}
			}
			if(undefined) Thread.yield();
		}
		return piece;
	}
}
