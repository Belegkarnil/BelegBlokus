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
package be.belegkarnil.game.board.blokus;

import be.belegkarnil.game.board.blokus.event.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * This class is a core class of the Game and represents the Game
 * with all its mechanics.
 *
 * @author Belegkarnil
 */
public class Game implements Runnable{
    public static final int DEFAULT_SKIP_LIMIT = 3;
    public static final int DEFAULT_TIMEOUT = 60;
    public static final int DEFAULT_NUMBER_OF_WINNING_ROUNDS = 2;
    private final Player[] players;
    private final int timeout, numWinningRounds, skipLimit;
    private int turn, round;
    private List<GameListener> gameListeners;
    private Board board;
    public Game(Board board, Player firstPlayer, Player secondPlayer){
        this(board,firstPlayer,secondPlayer,DEFAULT_TIMEOUT,DEFAULT_NUMBER_OF_WINNING_ROUNDS,DEFAULT_SKIP_LIMIT);
    }
    public Game(Board board, Player firstPlayer, Player secondPlayer, int timeout, int numWinningRounds, int skipLimit){
        this.board          = board;
        this.players        = new Player[]{ firstPlayer, secondPlayer };
        this.gameListeners  = new LinkedList<GameListener>();
        this.round          = 0;
        this.turn           = 0;
        
        this.timeout            = timeout;
        this.numWinningRounds   = numWinningRounds;
        this.skipLimit          = skipLimit;
    }
    public Player getFirstPlayer(){
        return players[0];
    }
    public Player getSecondPlayer(){
        return players[1];
    }
    public int getTimeout(){
        return timeout;
    }
    public int getSkipLimit(){
        return skipLimit;
    }
    public int countWinningRounds(){
        return numWinningRounds;
    }
    public int getRound(){
        return round;
    }
    public int getTurn(){
        return turn;
    }
    public void addGameListener(GameListener listener){
        if(listener == null)return;
        this.gameListeners.add(listener);
    }
    public void removeGameListener(GameListener listener){
        if(listener == null)return;
        this.gameListeners.remove(listener);
    }

    protected void executeGame(){
        for(Player player:players){
            removeGameListener(player.getStrategy().register());
        }
        //for(Player player:players) player.initialize(Piece.values());
        fireStarted(new GameEvent(this,players[0],this.players[1]));
        while(players[0].countWin() < numWinningRounds && players[1].countWin() < numWinningRounds){
            executeRound();
        }
        fireEnded(new GameEvent(this,players[0],this.players[1],players[0].countWin() >= numWinningRounds ? players[0] : players[1]));
        for(Player player:players){
            removeGameListener(player.getStrategy().unregister());
        }
    }
    protected void executeRound(){
        board.initialize(players[0], players[1]);
        fireStarted(new RoundEvent(this,players[0],this.players[1],round));
        Player winner = null;
        do{
            executeTurn();
            if(players[0].getPieces().isEmpty() || players[1].countSkip() >= skipLimit) winner = players[0];
            else if(players[1].getPieces().isEmpty() || players[0].countSkip() >= skipLimit) winner = players[1];
        }while(winner == null);
        winner.win();
        fireEnded(new RoundEvent(this,players[0],this.players[1],round,winner));
        round++;
        Player swap = players[0];
        players[0] = players[1];
        players[1] = swap;
    }

    protected void executeTurn(){
        final Player current = players[turn & 1];
        final Player opponent = players[turn & 1];
        fireStarted(new TurnEvent(this,current,opponent,round,turn));
        
        Piece action = null;
        boolean readAction = true;
        final Board.Cell type = current == getFirstPlayer() ? Board.Cell.FIRST_PLAYER : Board.Cell.SECOND_PLAYER;
        final StrategyTask task = new StrategyTask(current.getStrategy(), current.getPieces(), opponent.getPieces(), this.board, type);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(task);
        try {
            future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            readAction = false;
            current.skip();
            fireTimeout(new SkipEvent(this,current));
        } catch (Exception e) {
            readAction = false;
            current.skip();
            fireException(new SkipEvent(this,current,e));
        } finally {
            executor.shutdownNow();
            if(readAction){
                action = task.getAction();
            }
        }
        if(!readAction) {
            // skip, action is already skipped (timeout / exception)
        }else if(action == null){
            current.skip();
            fireNoAction(new SkipEvent(this,current));
        }else if(!current.isValid(action)){
            current.skip();
            fireInvalidPiece(new SkipEvent(this,current));
        }else if(! board.canPlace(action,current)) {
            current.skip();
            fireInvalidPosition(new SkipEvent(this,current));
        }else{
            board.place(action,current);
            current.plays(action);
        }
        fireEnded(new TurnEvent(this,current,opponent,round,turn, action));
        turn++;
    }
    protected void fireStarted(final TurnEvent te){
        for(GameListener listener: gameListeners)
            listener.onTurnStarted(te);
    }
    protected void fireEnded(final TurnEvent te){
        for(GameListener listener: gameListeners)
            listener.onTurnEnded(te);
    }
    protected void fireStarted(final RoundEvent re){
        for(GameListener listener: gameListeners)
            listener.onRoundStarted(re);
    }
    protected void fireEnded(final RoundEvent re){
        for(GameListener listener: gameListeners)
            listener.onRoundEnded(re);
    }
    protected void fireStarted(final GameEvent ge){
        for(GameListener listener: gameListeners)
            listener.onGameStarted(ge);
    }
    protected void fireEnded(final GameEvent ge){
        for(GameListener listener: gameListeners)
            listener.onGameEnded(ge);
    }
    protected void fireNoAction(final SkipEvent se){
        for(GameListener listener: gameListeners)
            listener.onNoAction(se);
    }
    protected void fireInvalidPiece(final SkipEvent se){
        for(GameListener listener: gameListeners)
            listener.onInvalidPiece(se);
    }
    protected void fireInvalidPosition(final SkipEvent se){
        for(GameListener listener: gameListeners)
            listener.onInvalidPosition(se);
    }
    protected void fireTimeout(final SkipEvent se){
        for(GameListener listener: gameListeners)
            listener.onTimeout(se);
    }
    protected void fireException(final SkipEvent se){
        for(GameListener listener: gameListeners)
            listener.onException(se);
    }
    
    @Override
    public void run() {
        executeGame();
    }
}
