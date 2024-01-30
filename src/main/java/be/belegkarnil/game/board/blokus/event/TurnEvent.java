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
package be.belegkarnil.game.board.blokus.event;

import be.belegkarnil.game.board.blokus.Game;
import be.belegkarnil.game.board.blokus.Piece;
import be.belegkarnil.game.board.blokus.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.EventObject;

/**
 * This class represents an event that is generated at the beginning or ending of
 * each turn. Each instance contains all the references to linked objects of the event.
 *
 * @author Belegkarnil
 */
public class TurnEvent extends EventObject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567L;
    public static final Piece NO_ACTION = null;
    public final Player current, opponent;
    public final int round, turn;
    public final Piece action;

    public TurnEvent(final Game game, final Player current, final Player opponent, final int round, final int turn){
      this(game, current, opponent, round, turn, NO_ACTION);
    }
    public TurnEvent(final Game game, final Player current, final Player opponent, final int round, final int turn, final Piece action){
        super(game);
        this.current    = current;
        this.opponent   = opponent;
        this.round      = round;
        this.turn       = turn;
        this.action     = action;
    }
}
