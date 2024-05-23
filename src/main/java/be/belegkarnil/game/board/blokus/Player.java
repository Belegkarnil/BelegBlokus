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

import be.belegkarnil.game.board.blokus.event.GameListener;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is a core class of the Game and represents the Player that owns its {@link Strategy}.
 *
 * @author Belegkarnil
 */
public class Player implements Externalizable {
    @Serial
    private static final long serialVersionUID = 1234567L;
    private String name;
    private Strategy strategy;
    private List<Piece> pieces;
    private int win, skip;
    public Player(String name, Strategy strategy){
        this.name       = name;
        this.strategy   = strategy;
        this.win        = 0;
        this.skip       = 0;
        this.pieces     = new LinkedList<Piece>();
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public String getName(){
        return name;
    }

    public int countWin(){
        return win;
    }

    public int countSkip(){
        return skip;
    }
    public int countScore(){
        int sum = 0;
        for(Piece piece : pieces) sum += piece.countTiles();
        return sum;
    }

    public void win(){
        win++;
    }
    public void skip(){
        skip++;
    }

    public List<Piece> getPieces(){
        return List.copyOf(this.pieces);
    }

    void initialize(Piece[] pieces){
        this.skip    = 0;
        this.pieces = new LinkedList<Piece>();
        Collections.addAll(this.pieces,pieces);
    }

    public boolean isValid(Piece action){
        return this.pieces.contains(action);// TODO check if correct equals ? hashCode?
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(win);
        out.writeInt(skip);
        out.writeInt(pieces.size());
        for(Piece p:pieces){
            out.writeObject(p);
        }
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.name   = in.readUTF();
        this.win    = in.readInt();
        this.skip   = in.readInt();
        
        int size = in.readInt();
        this.pieces = new LinkedList<Piece>();
        while(size > 0){
            size--;
            this.pieces.add((Piece) in.readObject());
        }
        
        this.strategy = new Strategy() {
            @Override
            public GameListener register() { return null; }
            @Override
            public GameListener unregister() { return null; }
            
            @Override
            public Piece action(List<Piece> pieces, Board board, Board.Cell type, List<Piece> opponent) {
                return null;
            }
        };
    }
	
	void plays(Piece action) {
        this.pieces.remove(action);
	}
}
