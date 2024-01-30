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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This enumeration is a core class of the Game and represents all available pieces.
 *
 * @author Belegkarnil
 */
public enum Piece implements Serializable{
    ONE(new Point[] { new Point(0,0) }),
    TWO(new Point[] { new Point(0,0),new Point(0,1) }),
    THREE_I(new Point[] { new Point(0,0),new Point(0,1),new Point(0,2) }),
    THREE_V(new Point[] { new Point(0,0),new Point(0,1),new Point(1,0) }),
    FOUR_I(new Point[] { new Point(0,0) ,new Point(0,1) ,new Point(0,2) ,new Point(0,3) }),
    FOUR_L(new Point[] { new Point(0,0),new Point(0,1),new Point(0,2),new Point(1,2) }),
    FOUR_N(new Point[] { new Point(0,0),new Point(1,0),new Point(1,1),new Point(2,1) }),
    FOUR_P(new Point[] { new Point(0,0),new Point(1,0),new Point(0,1),new Point(1,1) }),
    FOUR_T(new Point[] { new Point(0,0),new Point(1,0),new Point(2,0),new Point(1,1) }),
    FIVE_F(new Point[] { new Point(0,0),new Point(0,1),new Point(1,1),new Point(2,1),new Point(2,2) }),
    FIVE_I(new Point[] { new Point(0,0), new Point(0,1),new Point(0,2),new Point(0,3),new Point(0,4)}),
    FIVE_L(new Point[] { new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(1,3) }),
    FIVE_N(new Point[] { new Point(0,0),new Point(1,0),new Point(1,1),new Point(2,1),new Point(3,1) }),
    FIVE_P(new Point[] { new Point(0,0),new Point(1,0),new Point(0,1),new Point(1,1),new Point(2,0) }),
    FIVE_T(new Point[] { new Point(0,0),new Point(1,0),new Point(2,0),new Point(1,1),new Point(1,2) }),
    FIVE_U(new Point[] { new Point(0,0),new Point(0,1),new Point(1,1),new Point(2,1),new Point(2,0) }),
    FIVE_V(new Point[] { new Point(0,0),new Point(1,0),new Point(2,0),new Point(0,1),new Point(0,2) }),
    FIVE_W(new Point[] { new Point(0,0),new Point(1,0),new Point(1,1),new Point(2,1),new Point(2,2) }),
    FIVE_X(new Point[] { new Point(0,1),new Point(1,1),new Point(2,1),new Point(1,0),new Point(1,2) }),
    FIVE_Y(new Point[] { new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(1,2) }),
    FIVE_Z(new Point[] { new Point(0,0),new Point(1,0),new Point(1,1),new Point(1,2),new Point(2,2) });
    
    @Serial
    private static final long serialVersionUID = 1234567L;
    private final Point[][] shapes;
    private int shape;
    private final Point translation;
    private static Comparator<Point> POINT_COMPARATOR = null;
    private static Comparator<Point[]> POSITION_COMPARATOR = null;
    
    private static Comparator<Point> getPointComparator(){
        if(POINT_COMPARATOR == null){
            POINT_COMPARATOR = new Comparator<Point>() {
                @Override
                public int compare(Point a, Point b) {
                    // Row first, then column
                    final int y = Integer.compare(a.getY(),b.getY());
                    if(y != 0) return y;
                    return Integer.compare(a.getX(),b.getX());
                }
            };
        }
        return POINT_COMPARATOR;
    }
    
    private static Comparator<Point[]> getPositionComparator(){
        if(POSITION_COMPARATOR == null){
            POSITION_COMPARATOR = new Comparator<Point[]>() {
                public int compare(Point[] a, Point[] b) {
                    List<Point> first = new ArrayList<Point>(a.length);
                    List<Point> second = new ArrayList<Point>(b.length);
                    Collections.addAll(first, a);
                    Collections.addAll(second, b);
                    first.sort(getPointComparator());
                    second.sort(getPointComparator());
                    
                    int comp;
                    
                    while(!first.isEmpty() && !second.isEmpty()){
                        comp = getPointComparator().compare(first.removeFirst(),second.removeFirst());
                        if(comp != 0) return comp;
                    }
                    // At least one empty
                    if(! first.isEmpty()) return 1;
                    if(! second.isEmpty()) return -1;
                    
                    return 0;
                }
            };
        }
        return POSITION_COMPARATOR;
    }
    
    private Piece(final Point[] tiles){
        List<Point[]>  allShapes    = rotationAndSymmetry(tiles);
        Set<Point[]> uniqueShapes   = new TreeSet<Point[]>(getPositionComparator());
        uniqueShapes.addAll(allShapes);
        this.shapes                 = new Point[uniqueShapes.size()][];
        int pos = 0;
        for(Point[] point:uniqueShapes){
            this.shapes[pos] = point;
            pos++;
        }
        this.translation    = new Point(0,0);
    }
    /*
    private static void println(Point[] shape){
        char[][] repr = new char[shape.length][shape.length];
        final char WHITE = '□';
        final char BLACK = '▣';
        for(char[] row:repr) Arrays.fill(row,WHITE);
        for(Point p:shape) repr[p.getY()][p.getX()] = BLACK;
        for(char[] row:repr){
            System.out.println(row);
        }
        System.out.println();
    }
     */
    
    private static List<Point[]> rotationAndSymmetry(Point[] tiles) {
        List<Point[]> shapes = new LinkedList<Point[]>();
        normalize(tiles);
        shapes.add(tiles);
        shapes.add(horizontalSymmetry(tiles));
        shapes.add(verticalSymmetry(tiles));
        
        // From  Point[] to matrix
        int max=0;
        for(Point p:tiles){
            max = Math.max(max,Math.max(p.getY(),p.getX()));
        }
        max++;
        boolean[][] matrix = new boolean[max][max];
        for(boolean[] row:matrix) {
            Arrays.fill(row, false);
        }
        for(Point p:tiles){
            matrix[p.getY()][p.getX()] = true;
        }
        
        // For each rotation by 90 clockwise
        max >>= 1;
        boolean swap;
        for(int rot=0; rot<3; rot++){
            // 1. Rotate 90 clockwise
            // 1.a Reverse every individual row of the matrix
            for(boolean[] row:matrix){
                for(int i=0; i<max; i++){
                    swap                    = row[i];
                    row[i]                  = row[row.length - 1 - i];
                    row[row.length - 1 - i] = swap;
                }
            }
            // 1.b Perform Transpose of the matrix
            for(int i=1; i<matrix.length; i++){
                for(int j=0; j<i; j++){
                    swap            = matrix[i][j];
                    matrix[i][j]    = matrix[j][i];
                    matrix[j][i]    = swap;
                }
            }

            // 2. From matrix to Point[]
            final List<Point> current = new LinkedList<Point>();
            for(int y=0; y<matrix.length; y++){
                for(int x=0; x<matrix[y].length; x++){
                    if(matrix[y][x]) current.add(new Point(x,y));
                }
            }
            final Point[] result = new Point[current.size()];
            int i = 0;
            for(Point p:current){
                result[i] = p;
                i++;
            }
            normalize(result);
            shapes.add(result);
            shapes.add(horizontalSymmetry(result));
            shapes.add(verticalSymmetry(result));
        }
        
        return shapes;
    }
    
    private static Point[] horizontalSymmetry(Point[] tiles) {
        Point[] result = new Point[tiles.length];
        for(int i=0; i<tiles.length; i++){
            result[i] = new Point(tiles[i].getX(), -tiles[i].getY());
        }
        normalize(result);
        return result;
    }
    private static Point[] verticalSymmetry(Point[] tiles) {
        Point[] result = new Point[tiles.length];
        for(int i=0; i<tiles.length; i++){
            result[i] = new Point(-tiles[i].getX(), tiles[i].getY());
        }
        normalize(result);
        return result;
    }
    
    public int countTiles(){
        return this.shapes[this.shape].length;
    }
    
    public void next(){
        shape = (shape+1) % shapes.length;
    }
    public int countShapes(){
        return this.shapes.length;
    }
    public int getShape(){
        return shape;
    }
    public void translate(int dx, int dy){
        this.translation.translate(dx,dy);
    }
    public void move(int x, int y){
        this.translation.setLocation(x,y);
    }

    public Point[] getPositions(){
        Point[] copy = new Point[this.shapes[this.shape].length];
        for (int i = 0; i < copy.length; i++) copy[i] = (Point) this.shapes[this.shape][i].clone();
        
        final int dx = this.translation.getX();
        final int dy = this.translation.getY();
        for(Point p:copy){
            p.translate(dx, dy);
        }

        return copy;
    }
    private static void normalize(Point[] points){
        // FIXME Seems to does not work
        // Move the points to 0,0 as closest positive coordinates
        int x=Integer.MAX_VALUE;
        int y=Integer.MAX_VALUE;
        for(Point p:points){
            x = Math.min(x,p.getX());
            y = Math.min(y,p.getY());
        }
        
        x *= -1;
        y *= -1;

        for(Point p:points){
            p.translate(x,y);
        }
    }
    
	public void reset(){
        this.shape = 0;
        translation.setLocation(0,0);
   }
	public Point getOriginalTile(int i) {
        return (Point) this.shapes[0][i].clone();
	}
   
    public Point[] getCorners() {
        Point[] pos = getPositions();
        Set<Point> corners = new HashSet<Point>();
        for(Point p:pos){
            Point corner;
            corner = new Point(p.getX()-1,p.getY()-1);
            if(!isInShape(corner,pos)) corners.add(corner);
            corner = new Point(p.getX()+1,p.getY()-1);
            if(!isInShape(corner,pos)) corners.add(corner);
            corner = new Point(p.getX()+1,p.getY()+1);
            if(!isInShape(corner,pos)) corners.add(corner);
            corner = new Point(p.getX()-1,p.getY()+1);
            if(!isInShape(corner,pos)) corners.add(corner);
        }
        Point[] res = new Point[corners.size()];
        int i=0;
        for(Point corner: corners){
            res[i] = corner;
            i++;
        }
        return res;
    }
    
    private static boolean isInShape(Point corner, Point[] pos) {
        for(Point p:pos)
            if(p.equals(corner)) return true;
        return false;
    }
   
   
}


