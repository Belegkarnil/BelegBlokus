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

/**
 * This class is a core class of the Game used to represent the position
 * of each {@link Piece}'s part and of the {@link Board}. It behaves similarly to the {@link java.awt.Point} but
 * keep coordinates with integers.
 *
 * @author Belegkarnil
 */
public class Point implements Serializable, Cloneable {
	@Serial
	private static final long serialVersionUID = 1234567L;
	private int x,y;
	public Point(int x, int y){
		this.x	= x;
		this.y	= y;
	}
	public Point(Point p){
		this(p.x,p.y);
	}
	
	public int getX(){
		return this.x;
	}
	public int getY(){
		return this.y;
	}
	public void setLocation(int x,int y){
		this.x	= x;
		this.y	= y;
	}
	public void setLocation(Point p){
		setLocation(p.x,p.y);
	}
	public void translate(int dx,int dy){
		this.x	+= dx;
		this.y	+= dy;
	}
	public void translate(Point p){
		translate(p.x,p.y);
	}
	
	@Override
	public boolean equals(Object o){
		if(! (o instanceof Point))return false;
		final Point p = (Point)o;
		return p.x == x && p.y == y;
	}
	@Override
	public Object clone(){
		return new Point(x,y);
	}
	@Override
	public int hashCode(){
		return Long.valueOf(((long) x) << 32 | y).hashCode();
	}
	@Override
	public String toString(){
		return getClass().getSimpleName()+"[x="+x+",y="+y+"]";
	}
}
