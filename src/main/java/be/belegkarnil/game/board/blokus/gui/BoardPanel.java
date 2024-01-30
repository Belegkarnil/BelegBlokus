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
package be.belegkarnil.game.board.blokus.gui;

import be.belegkarnil.game.board.blokus.Board;
import be.belegkarnil.game.board.blokus.Game;
import be.belegkarnil.game.board.blokus.Piece;
import be.belegkarnil.game.board.blokus.Player;
import be.belegkarnil.game.board.blokus.Point;
import be.belegkarnil.game.board.blokus.event.GameAdapter;
import be.belegkarnil.game.board.blokus.event.RoundEvent;
import be.belegkarnil.game.board.blokus.event.TurnEvent;
import be.belegkarnil.game.board.blokus.strategy.HMIStrategy;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * This class is a GUI component of the Game. It is the {@link Board} panel ({@link BlokusPanel}).
 *
 * @author Belegkarnil
 */
public class BoardPanel extends BlokusPanel implements MouseListener {
	public static final int DEFAULT_CELL_SIZE = 40;
	public static final Color FIRST_PLAYER = Color.ORANGE;
	public static final Color SECOND_PLAYER = new Color(102,0,153);
	public static final Color EMPTY = Color.LIGHT_GRAY;
	public static final Color BORDER = Color.BLACK;
	public static final Color START_AREA = Color.GREEN.darker();
	
	public static final int BORDER_THICKNESS = 2;
	
	private final Board board;
	private final JPanel[][] cells;
	private Player hmi;
	private final Object lock;
	private Piece current;
	private Color color;
	
	public BoardPanel(Board board){
		this.board		= board;
		this.hmi			= null;
		this.current	= null;
		this.lock		= new Object();
		this.color		= FIRST_PLAYER;
		final int size = board.getSize();
		cells = new JPanel[size][size];
		
		JPanel content = new JPanel();
		
		content.setLayout(new GridBagLayout());
		/*final GridLayout layout = new GridLayout(size,size);
		layout.setHgap(BORDER_THICKNESS);
		layout.setVgap(BORDER_THICKNESS);
		 */
		setLayout(new BorderLayout());//SwingConstants.CENTER
		
		
		content.setBackground(BORDER);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill	= GridBagConstraints.HORIZONTAL;
		
		for(int y=0; y<size;y++){
			for(int x=0; x<size;x++){
				cells[y][x] = createCell();
				cells[y][x].addMouseListener(this);
				c.gridx = x;
				c.gridy = y;
				content.add(cells[y][x],c);
			}
		}
		content.setBorder(BorderFactory.createLineBorder(Color.black,BORDER_THICKNESS));
		
		final JPanel resizable = new JPanel(new FlowLayout());
		resizable.add(content);
		add(resizable,BorderLayout.CENTER);
		
		resizable.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int size = Math.min(resizable.getWidth(), resizable.getHeight()) / board.getSize() - 1;
				content.setSize(size*14,size*14);
				for(JPanel[] row:cells)
					for(JPanel cell:row) {
						cell.setPreferredSize(new Dimension(size, size));
						cell.setSize(size, size);
					}
				content.revalidate();
			}
		});
	}
	
	public void reset(){
		for(int i=0; i<cells.length; i++){
			for(int j=0; j<cells[i].length; j++){
				cells[i][j].setBackground(getColorFor(board.getCellAt(i,j)));
			}
		}
	}
	
	public void update(Point[] area){
		for(Point p:area){
			if(! board.isOutOfBounds(p)) {
				cells[p.getY()][p.getX()].setBackground(getColorFor(board.getCellAt(p.getY(), p.getX())));
			}
		}
	}
	
	private Color getColorFor(Board.Cell cell) {
		switch (cell){
			case FIRST_PLAYER: return FIRST_PLAYER;
			case SECOND_PLAYER: return SECOND_PLAYER;
			case START_PLACE: return START_AREA;
			default:	return EMPTY;
		}
	}
	
	private JPanel createCell() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
		panel.setBackground(EMPTY);
		final Dimension dimension = new Dimension(DEFAULT_CELL_SIZE, DEFAULT_CELL_SIZE);
		//panel.setMinimumSize(dimension);
		//panel.setMaximumSize(dimension);
		panel.setSize(dimension);
		panel.setPreferredSize(dimension);
		return panel;
	}
	
	@Override
	void register(Game game) {
		game.addGameListener(new GameAdapter() {
			@Override
			public void onRoundStarted(RoundEvent re){
				reset();
			}
			@Override
			public void onTurnStarted(TurnEvent te) {
				current	= null;
				if(te.current.getStrategy() instanceof HMIStrategy){
					synchronized (lock){
						hmi		= te.current;
						color		= (te.turn % 2 == 0 ? FIRST_PLAYER : SECOND_PLAYER).brighter();
					}
				}
			}
			@Override
			public void onTurnEnded(TurnEvent te) {
				reset();
				synchronized (lock){
					hmi = null;
				}
				if(te.action != null){
					update(te.action.getPositions());
				}
			}
		});
	}
	
	@Override
	void onPieceSelected(String name) {
		synchronized (lock){
			if(hmi != null){
				if(this.current != null){
					update(current.getPositions());
				}
				if(name == null){
					this.current = null;
					((HMIStrategy)(this.hmi.getStrategy())).setAction(null);
				}else {
					final List<Piece> pieces = hmi.getPieces();
					for (Piece piece : pieces) {
						if (piece.name().equals(name)) {
							this.current = piece;
							this.current.reset();
						}
					}
				}
			}
		}
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			if(current == null) return;
			if(!board.canPlace(current,this.hmi))return;
			synchronized (lock){
				if(this.hmi != null)
					((HMIStrategy)(this.hmi.getStrategy())).setAction(current);
			}
		} else if (e.getButton() == MouseEvent.BUTTON3){
			synchronized (lock){
				if(current != null){
					Point[] pos;
					pos = current.getPositions();
					update(pos);
					
					current.next();
					
					pos = current.getPositions();
					if(board.canPlace(current,hmi)) {
						for (Point p : pos) {
							cells[p.getY()][p.getX()].setBackground(color);
							cells[p.getY()][p.getX()].repaint();
						}
					}else{
						for (Point p : pos) {
							if(board.isInBounds(p)) {
								cells[p.getY()][p.getX()].setBackground(Color.BLUE);
								cells[p.getY()][p.getX()].repaint();
							}
						}
					}
					
				}
			}
		}
	}
	
	protected Point getCoordinate(Object panel){
		if(! (panel instanceof JPanel)) return new Point(-1,-1);
		final JPanel pan = (JPanel)panel;
		int row = -1, col = -1;
		for(int i=0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (cells[i][j] == pan) {
					row = i;
					col = j;
					j = cells[i].length;
				}
			}
			if(row>0) i = cells.length;
		}
		return new Point(col,row);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		final Point enteredAt = getCoordinate(e.getSource());
		if(enteredAt.getY() < 0) return;
		synchronized (lock){
			if(current != null) {
				Point[] pos;
				
				pos = current.getPositions();
				update(pos);
				
				current.move(enteredAt.getX(), enteredAt.getY());
				
				pos = current.getPositions();
				if(board.canPlace(current,hmi)) {
					for (Point p : pos) {
						cells[p.getY()][p.getX()].setBackground(color);
						cells[p.getY()][p.getX()].repaint();
					}
				}else{
					for (Point p : pos) {
						if(board.isInBounds(p)) {
							cells[p.getY()][p.getX()].setBackground(Color.BLUE);
							cells[p.getY()][p.getX()].repaint();
						}
					}
				}
			}
		}
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		final Point exitedAt = getCoordinate(e.getSource());
		if(exitedAt.getY() < 0) return;
		cells[exitedAt.getY()][exitedAt.getX()].repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent e) { }
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
}
