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

import java.util.EventListener;
/**
 * This interface lists all the methods that can be called when an event occurs.
 *
 * @author Belegkarnil
 */
public interface GameListener extends EventListener {
    public void onGameStarted(GameEvent ge);
    public void onGameEnded(GameEvent ge);
    public void onRoundStarted(RoundEvent re);
    public void onRoundEnded(RoundEvent re);
    public void onTurnStarted(TurnEvent te);
    public void onTurnEnded(TurnEvent te);
    public void onNoAction(SkipEvent se);
    public void onInvalidPiece(SkipEvent se);
    public void onInvalidPosition(SkipEvent se);
    public void onTimeout(SkipEvent se);
    public void onException(SkipEvent se);
}
