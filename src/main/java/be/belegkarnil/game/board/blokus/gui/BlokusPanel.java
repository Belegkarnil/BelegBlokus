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
package be.belegkarnil.game.board.blokus.gui;

import be.belegkarnil.game.board.blokus.Game;
import be.belegkarnil.game.board.blokus.Piece;
import be.belegkarnil.game.board.blokus.strategy.HMIStrategy;

import javax.swing.JPanel;
import java.util.LinkedList;
import java.util.List;
/**
 * This class is a GUI component of the Game. It is an abstract panel ({@link JPanel}) which is able to listen
 * a {@link Game} and be notified when a human player interact to select a {@link Piece} within the {@link HMIStrategy},
 *
 * @author Belegkarnil
 */
public abstract class BlokusPanel extends JPanel {
	private static final List<BlokusPanel> panels = new LinkedList<BlokusPanel>();
	public BlokusPanel(){
		super();
		panels.add(this);
	}
	static void initGame(Game game){
		for(BlokusPanel panel:panels)
			panel.register(game);
	}
	static void firePieceSelected(String name){
		for(BlokusPanel panel:panels)
			panel.onPieceSelected(name);
	}
	abstract void register(Game game);
	abstract void onPieceSelected(String name);
	
}
