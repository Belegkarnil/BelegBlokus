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
import be.belegkarnil.game.board.blokus.event.GameEvent;
import be.belegkarnil.game.board.blokus.event.GameListener;
import be.belegkarnil.game.board.blokus.event.RoundEvent;
import be.belegkarnil.game.board.blokus.event.SkipEvent;
import be.belegkarnil.game.board.blokus.event.TurnEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

/**
 * This class act as a TCP network client which is called by a remote a {@link Strategy}
 * through the {@link NetworkServerStrategy}. This strategy receives events from the remote
 * server and also use its internal {@link Strategy} to notify which {@link Piece} to play.
 * to play.
 *
 * @author Belegkarnil
 */
public class NetworkClientStrategy extends Thread implements Strategy, GameListener {
	private final Socket socket;
	private final Strategy strategy;
	private GameListener mapping;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private boolean running,inStrategy;
	private final Object lockRunning, lockStrategy;
	private Thread task;
	
	public NetworkClientStrategy(String host, int serverPort, Strategy strategy) throws IOException {
		this.mapping	= null;
		this.strategy	= strategy;
		this.socket		= new Socket(host,serverPort);
		this.out			= new ObjectOutputStream(socket.getOutputStream());
		this.in			= new ObjectInputStream(socket.getInputStream());
		running			= false;
		inStrategy		= false;
		lockRunning		= new Object();
		lockStrategy	= new Object();
		task				= null;
		new Thread(this).start();
	}
	
	@Override
	public void run(){
		boolean keepGoing = true;
		while(keepGoing){
			try {
				String method	= in.readUTF();
				if(method.equals("action")){
					applyStrategy();
				}else{
					EventObject event	= (EventObject) in.readObject();
					fireEvent(event,method);
				}
			} catch (Exception e) {
				synchronized (lockRunning){
					running = false;
				}
				e.printStackTrace();
			}
			synchronized (lockRunning){
				keepGoing = running;
			}
		}
	}
	
	private void applyStrategy() {
		final List<Piece> pieces = new LinkedList<Piece>();
		final List<Piece> opponent = new LinkedList<Piece>();
		final Board board;
		final Board.Cell type;
		Board tmpBoard = null;
		Board.Cell tmpCell = Board.Cell.EMPTY;
		int count;
		
		synchronized (lockStrategy){
			inStrategy = true;
		}
		
		try {
			count = in.readInt();
			while (count > 0) {
				count--;
				pieces.add((Piece) in.readObject());
			}
			
			tmpBoard = (Board) in.readObject();
			tmpCell = (Board.Cell) in.readObject();
			
			count = in.readInt();
			while (count > 0) {
				count--;
				opponent.add((Piece) in.readObject());
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			board = tmpBoard;
			type	= tmpCell;
		}
		
		task = new Thread(){
			@Override
			public void run(){
				Piece action = action(pieces,board,type,opponent);
				synchronized (lockStrategy){
					try {
						out.writeObject(action);
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					inStrategy = false;
				}
			}
		};
		task.start();
	}
	
	private void fireEvent(EventObject event, String method) {
		if(event instanceof TurnEvent){
			switch (method){
				case "onTurnStarted":	this.onTurnStarted((TurnEvent) event); break;
				case "onTurnEnded":		this.onTurnEnded((TurnEvent) event);	break;
				default: throw new RuntimeException("Method "+method+" does not exists");
			}
		} else if (event instanceof RoundEvent) {
			switch (method){
				case "onRoundStarted":	this.onRoundStarted((RoundEvent)event);	break;
				case "onRoundEnded":		this.onRoundEnded((RoundEvent)event);		break;
				default: throw new RuntimeException("Method "+method+" does not exists");
			}
		} else if (event instanceof SkipEvent) {
			synchronized (lockStrategy){
				if(task != null){
					task.interrupt();
					try {
						task.join();
					} catch (InterruptedException e) { }
					task = null;
				}
				// ensure that action response is sent even if the server skip
				if(inStrategy){
					try {
						out.writeObject((Piece)null);
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				inStrategy = false;
			}
			switch (method){
				case "onNoAction":			this.onNoAction((SkipEvent) event);				break;
				case "onInvalidPiece":		this.onInvalidPiece((SkipEvent) event);		break;
				case "onInvalidPosition":	this.onInvalidPosition((SkipEvent) event);	break;
				case "onTimeout":				this.onTimeout((SkipEvent) event);				break;
				case "onException":			this.onException((SkipEvent) event);			break;
				default: throw new RuntimeException("Method "+method+" does not exists");
			}
		} else if (event instanceof GameEvent) {
			switch (method){
				case "onGameStarted":	this.onGameStarted((GameEvent) event);	break;
				case "onGameEnded":		this.onGameEnded((GameEvent) event);	break;
				default: throw new RuntimeException("Method "+method+" does not exists");
			}
		}
		throw new RuntimeException("Event Class "+event.getClass().getName()+" does not exists");
	}
	
	@Override
	public GameListener register() {
		mapping = this.strategy.register();
		return mapping;
	}
	
	@Override
	public GameListener unregister() {
		this.strategy.unregister();
		return mapping;
	}
	
	@Override
	public Piece action(List<Piece> pieces, Board board, Board.Cell type, List<Piece> opponent) {
		return strategy.action(pieces,board,type,opponent);
	}
	
	@Override
	public void onGameStarted(GameEvent ge) {
		if(mapping == null) return;
		mapping.onGameStarted(ge);
	}
	
	@Override
	public void onGameEnded(GameEvent ge) {
		if(mapping != null) mapping.onGameEnded(ge);
		synchronized (lockRunning){
			running = false;
		}
	}
	
	@Override
	public void onRoundStarted(RoundEvent re) {
		if(mapping == null) return;
		mapping.onRoundStarted(re);
	}
	
	@Override
	public void onRoundEnded(RoundEvent re) {
		if(mapping == null) return;
		mapping.onRoundEnded(re);
	}
	
	@Override
	public void onTurnStarted(TurnEvent te) {
		if(mapping == null) return;
		mapping.onTurnStarted(te);
	}
	
	@Override
	public void onTurnEnded(TurnEvent te) {
		if(mapping == null) return;
		mapping.onTurnEnded(te);
	}
	
	@Override
	public void onNoAction(SkipEvent se) {
		if(mapping == null) return;
		mapping.onNoAction(se);
	}
	
	@Override
	public void onInvalidPiece(SkipEvent se) {
		if(mapping == null) return;
		mapping.onInvalidPiece(se);
	}
	
	@Override
	public void onInvalidPosition(SkipEvent se) {
		if(mapping == null) return;
		mapping.onInvalidPosition(se);
	}
	
	@Override
	public void onTimeout(SkipEvent se) {
		if(mapping == null) return;
		mapping.onTimeout(se);
	}
	
	@Override
	public void onException(SkipEvent se) {
		if(mapping == null) return;
		mapping.onException(se);
	}
}
