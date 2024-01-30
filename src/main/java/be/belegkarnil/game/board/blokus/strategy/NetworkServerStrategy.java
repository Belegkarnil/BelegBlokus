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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EventObject;
import java.util.List;

/**
 * This class act as a TCP network server which calls a remote a {@link Strategy}
 * through the {@link NetworkClientStrategy}. This strategy send events to the remote
 * {@link Strategy} and also ask the remote {@link Strategy} which  {@link Piece}
 * to play.
 *
 * @author Belegkarnil
 */
public class NetworkServerStrategy implements Strategy, GameListener {
	private final ObjectInputStream in;
	private final ObjectOutputStream out;
	private final ServerSocket server;
	private final Socket client;
	private boolean hasToReadStrategy;
	private final Object lock;
	public NetworkServerStrategy(int listenPort) throws IOException {
		server	= new ServerSocket(listenPort);
		client	= server.accept();
		this.out	= new ObjectOutputStream(client.getOutputStream());
		this.in	= new ObjectInputStream(client.getInputStream());
		
		hasToReadStrategy	= false;
		lock					= new Object();
	}
	
	@Override
	public GameListener register() {
		return this;
	}
	
	@Override
	public GameListener unregister() {
		return this;
	}
	
	@Override
	public Piece action(List<Piece> pieces, Board board, Board.Cell type, List<Piece> opponent) {
		// TODO react to timeout
		try {
			out.writeUTF("action");
			out.writeInt(pieces.size());
			for (Piece p : pieces) out.writeObject(p);
			out.writeObject(board);
			out.writeObject(type);
			out.writeInt(opponent.size());
			for (Piece p : opponent) out.writeObject(p);
			out.flush();
			synchronized (lock) {
				hasToReadStrategy = true;
			}
			final Piece action = (Piece) in.readObject();
			synchronized (lock) {
				hasToReadStrategy = false;
			}
			return action;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	private void writeEvent(String method, EventObject event){
		try {
			out.writeUTF(method);
			out.writeObject(event);
			out.flush();
			if(event instanceof SkipEvent){
				synchronized (lock) {
					if(hasToReadStrategy) {
						try {
							in.readObject();
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							hasToReadStrategy = false;
						}
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onGameStarted(GameEvent ge) {
		writeEvent("onGameStarted",ge);
	}
	
	@Override
	public void onGameEnded(GameEvent ge) {
		writeEvent("onGameEnded",ge);
	}
	
	@Override
	public void onRoundStarted(RoundEvent re) {
		writeEvent("onRoundStarted",re);
	}
	
	@Override
	public void onRoundEnded(RoundEvent re) {
		writeEvent("onRoundEnded",re);
	}
	
	@Override
	public void onTurnStarted(TurnEvent te) {
		writeEvent("onTurnStarted",te);
	}
	
	@Override
	public void onTurnEnded(TurnEvent te) {
		writeEvent("onTurnEnded",te);
	}
	
	@Override
	public void onNoAction(SkipEvent se) {
		writeEvent("onNoAction",se);
	}
	
	@Override
	public void onInvalidPiece(SkipEvent se) {
		writeEvent("onInvalidPiece",se);
	}
	
	@Override
	public void onInvalidPosition(SkipEvent se) {
		writeEvent("onInvalidPosition",se);
	}
	
	@Override
	public void onTimeout(SkipEvent se) {
		writeEvent("onTimeout",se);
	}
	
	@Override
	public void onException(SkipEvent se) {
		writeEvent("onException",se);
	}
}
