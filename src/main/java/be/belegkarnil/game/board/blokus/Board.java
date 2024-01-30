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

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * This class is a core class of the Game and represents the Board.
 *
 * @author Belegkarnil
 */
public class Board implements Serializable{
    @Serial
    private static final long serialVersionUID = 1234567L;
    
    
    public static enum Cell implements Serializable{
        EMPTY,
        START_PLACE,
        FIRST_PLAYER,
        SECOND_PLAYER;
        private static final long serialVersionUID = 1234567L;
    }
    public static final int DEFAULT_SIZE = 14;//Blokus Duo
    private final Cell[][] data;
    private final int size;
    private Player firstPlayer, secondPlayer;
    public Board(int size){
        this.size           = size;
        this.data           = new Cell[size][size];
        this.firstPlayer    = null;
        this.secondPlayer   = null;
        this.initialize(null,null);
    }
    public Point[] getStartingPositions(){
        return new Point[]{new Point(4,4), new Point(this.size-4-1,this.size-4-1)};
    }
    public int getSize() {
        return this.size;
    }
    
    public void initialize(Player firstPlayer, Player secondPlayer){
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        if(this.firstPlayer != null)
            this.firstPlayer.initialize(Piece.values());
        if(this.secondPlayer != null)
            this.secondPlayer.initialize(Piece.values());
        for(int i=0; i<this.data.length; i++){
            Arrays.fill(this.data[i],Cell.EMPTY);
        }
        for(Point point:getStartingPositions())
            this.data[point.getY()][point.getX()] = Cell.START_PLACE;
    }

    public Board(){
        this(DEFAULT_SIZE);
    }

    void place(Piece piece, Player player){
        Cell type = player == firstPlayer ? Cell.FIRST_PLAYER : Cell.SECOND_PLAYER;
        for(Point point:piece.getPositions()){
            this.data[point.getY()][point.getX()] = type;
        }
    }
    public boolean isOutOfBounds(Point point) {
        if(point.getX() < 0 || point.getY() < 0) return true;
        return point.getX() >= size || point.getY() >= size;
    }
    public boolean isInBounds(Point point) {
        return !isOutOfBounds(point);
    }
    public boolean inCellEmpty(Point point) {
        return this.data[point.getY()][point.getX()] == Cell.EMPTY;
    }
    private boolean isOutOfBounds(Point[] positions){
        for(Point point:positions){
            if(point.getX() < 0 || point.getY() < 0) return true;
            if(point.getX() >= size || point.getY() >= size) return true;
        }
        return false;
    }
    private boolean isFreeArea(Point[] positions){
        for(Point point:positions){
            switch(data[point.getY()][point.getX()]){
                case FIRST_PLAYER :
                case SECOND_PLAYER: return false;
                default:
            }
        }
        return true;
    }
    private boolean isStartZone(Point[] positions){
        for(Point point:positions){
            if(data[point.getY()][point.getX()] == Cell.START_PLACE) return true;
        }
        return false;
    }
    private boolean inShape(Point[] positions, Point point){
        for(Point pos:positions)
            if(pos.equals((point))) return true;
        return false;
    }
    private boolean isCornerAdjacent(Point[] positions, Cell type){
        // assume all positions are available
        // compute all corner position and filter to remove out of bounds, remove all position inside the shape (i.e. positions)
        List<Point> corners = new ArrayList<Point>(positions.length<<2);
        for(Point point:positions){
            corners.add(new Point(point.getX()-1, point.getY()-1));
            corners.add(new Point(point.getX()+1, point.getY()+1));
            corners.add(new Point(point.getX()+1, point.getY()-1));
            corners.add(new Point(point.getX()-1, point.getY()+1));
        }
        for(int i=corners.size()-1; i >= 0; i--){
            if(isOutOfBounds(corners.get(i)) || inShape(positions,corners.get(i))) corners.remove(i);
        }
        for(Point point:corners){
            if(this.data[point.getY()][point.getX()] == type) return true;
        }
        return false;
    }
    private boolean isAdjacent(Point[] positions, Cell type) {
        // compute all side position and filter to remove out of bounds, remove all positions of the shape
        List<Point> adjacent = new ArrayList<Point>(positions.length<<2);
        for(Point point:positions){
            adjacent.add(new Point(point.getX()-1, point.getY()));
            adjacent.add(new Point(point.getX()+1, point.getY()));
            adjacent.add(new Point(point.getX(), point.getY()-1));
            adjacent.add(new Point(point.getX(), point.getY()+1));
        }
        for(int i=adjacent.size()-1; i >= 0; i--){
            if(isOutOfBounds(adjacent.get(i)) || inShape(positions,adjacent.get(i)) ) adjacent.remove(i);
        }
        for(Point point:adjacent){
            if(this.data[point.getY()][point.getX()] == type) return true;
        }
        return false;
    }
    public boolean canPlace(Piece piece, Player player){
        return canPlace(piece,player == firstPlayer ? Cell.FIRST_PLAYER : Cell.SECOND_PLAYER);
    }
    public boolean canPlace(Piece piece, Cell type){
        final Point[] points = piece.getPositions();
        if(isOutOfBounds(points)) return false;
        if(!isFreeArea(points)) return false;
        if(isStartZone(points)) return true;
        if(isAdjacent(points,type)) return false;
        return isCornerAdjacent(points,type);
    }
    public Cell getCellAt(int row, int col){
        return this.data[row][col];
    }
    public Cell[][] copy(){
        Cell[][] copy = new Cell[this.size][this.size];
        for(int i=0; i<copy.length; i++){
            for(int j=0; j<copy.length; j++){
                copy[i][j] = this.data[i][j];
            }
        }
        return copy;
    }
}
