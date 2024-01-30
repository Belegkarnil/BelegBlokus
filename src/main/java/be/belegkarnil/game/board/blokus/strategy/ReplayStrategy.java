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
import be.belegkarnil.game.board.blokus.Strategy;
import be.belegkarnil.game.board.blokus.event.GameListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
/**
 * This class represents a {@link Strategy} that replays the same pieces of a player
 * by parsing a log file.
 *
 * @author Belegkarnil
 */
public class ReplayStrategy implements Strategy {
	List<String> actions;
	public ReplayStrategy(File file, boolean firstPlayer) throws FileNotFoundException {
		Scanner scan = new Scanner(new FileInputStream(file));
		actions = new LinkedList<String>();
		int pos = 1;
		int current = 0;
		while(scan.hasNextLine()) {
			final String line = scan.nextLine();
			if (line.startsWith("Round ")){
				pos		= (pos + 1) % 2;
				current	= 0;
			} else if (line.startsWith("Action=")) {
				if(current == pos){
					actions.addLast(line.substring("Action=".length()));
				}
				current++;
			}
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
		if(!actions.isEmpty()){
			String current = actions.removeFirst();
			if(current.equals("none")) return null;
			String[] params = current.split(",");
			int x = -1, y = -1, shape = -1;
			
			for(String param:params){
				param = param.strip();
				if(param.startsWith("x=")) x = Integer.parseInt(param.substring("x=".length()));
				else if(param.startsWith("y=")) y = Integer.parseInt(param.substring("y=".length()));
				else if(param.startsWith("shape=")) shape = Integer.parseInt(param.substring("shape=".length()));
				else current = param;
			}
			
			Piece action = Piece.valueOf(current);
			action.translate(x,y);
			while(action.getShape() != shape) action.next();
			
			return action;
		}
		return null;
	}
}
