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

import be.belegkarnil.game.board.blokus.Game;
import be.belegkarnil.game.board.blokus.Piece;
import be.belegkarnil.game.board.blokus.Player;
import be.belegkarnil.game.board.blokus.Point;
import be.belegkarnil.game.board.blokus.event.GameAdapter;
import be.belegkarnil.game.board.blokus.event.TurnEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is a GUI component of the Game. It is the {@link Piece} selection panel ({@link BlokusPanel}).
 *
 * @author Belegkarnil
 */
public class PiecesPanel extends BlokusPanel implements MouseListener, MouseWheelListener {
	private static class PiecePanel extends JPanel{
		public static final Color BORDER_COLOR = Color.WHITE;
		public static final Color UNSELECTED_COLOR = Color.BLACK;
		public static final Color SELECTED_COLOR = Color.BLUE;
		static final int SIZE = 40;
		public final Point[] positions;
		public final String name;
		private boolean selected;
		private final Object lock;
		
		public PiecePanel(Piece piece){
			final JButton button;
			if(piece == null){
				this.positions = new Point[0];
				this.name		= null;
				button = new JButton("Skip");
				setLayout(new BorderLayout());
				add(button,BorderLayout.CENTER);
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						firePieceSelected(null);
					}
				});
			}else {
				button = null;
				this.positions = new Point[piece.countTiles()];
				for (int i = 0; i < positions.length; i++) {
					this.positions[i] = piece.getOriginalTile(i);
				}
				this.name = piece.name();
			}
			this.selected	= false;
			this.lock		= new Object();
			int maxX = 0, maxY = 0;
			for(Point point:positions) {
				maxX = Math.max(maxX,point.getX());
				maxY = Math.max(maxY,point.getY());
			}
			if(button != null) {
				maxX++;
			}
			maxX = (maxX + 1) * SIZE;
			maxY = (maxY + 1) * SIZE;
			final Dimension size = new Dimension(maxX,maxY);
			setMinimumSize(size);
			setMaximumSize(size);
			setPreferredSize(size);
			setSize(size);
			
			//setFont(getFont().deriveFont(Font.BOLD,28));
		}
		public void setSelect(boolean selected){
			synchronized (lock){
				this.selected = selected;
			}
		}
		public boolean contains(Piece piece){
			if(piece == null) return contains((String)null);
			return contains(piece.name());
		}
		public boolean contains(String pieceName){
			if(this.name == null){
				return pieceName == null;
			}
			return pieceName.equals(this.name);
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Color ground = UNSELECTED_COLOR;
			synchronized (lock){
				if(selected) ground = SELECTED_COLOR;
			}
			if(positions.length > 0) {
				int x = 0, y = 0;
				for (Point point : positions) {
					g.setColor(ground);
					x = point.getX() * SIZE;
					y = point.getY() * SIZE;
					g.fillRect(x, y, SIZE, SIZE);
					
					g.setColor(BORDER_COLOR);
					g.drawRect(x, y, SIZE, SIZE);
				}
			}
		}
	}
	private final JPanel content;
	private List<PiecePanel> pieces;
	private int position;
	private JScrollPane scroll;
	public PiecesPanel(){
		this.position = 0;
		pieces = new LinkedList<>();
		setLayout(new BorderLayout());
		
		content = new JPanel();
		content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
		final Dimension dimension = new Dimension(6*PiecePanel.SIZE,0);// 5*SIZE = max piece length + 1 for spacing
		content.setMinimumSize(dimension);
		content.setPreferredSize(dimension);
		setBackground(Color.red);
		add(scroll = new JScrollPane(content,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
	}
	
	@Override
	void register(Game game) {
		game.addGameListener(new GameAdapter() {
			@Override
			public void onTurnStarted(TurnEvent te) {
				update(te.current);
			}
		});
		
	}
	
	@Override
	void onPieceSelected(String name) {
	
	}
	
	public void update(Player player){
		content.removeAll();
		pieces.clear();
		position = -1;
		
		List<Piece> pieces = player.getPieces();
		JComponent comp;
		int height = 0;
		
		content.add(comp = new PiecePanel(null));
		comp.addMouseListener(this);
		this.pieces.add((PiecePanel) comp);
		height += comp.getHeight();
		
		/*
		if(pieces.size()>0){
			content.add(comp = new PiecePanel(pieces.getFirst()));
			comp.addMouseListener(this);
			this.pieces.add((PiecePanel) comp);
			height += comp.getHeight();
		}*/
		for(Piece piece:pieces){
			content.add(comp = new JSeparator());
			height += comp.getHeight();
			content.add(comp = new PiecePanel(piece));
			comp.addMouseListener(this);
			this.pieces.add((PiecePanel) comp);
			height += comp.getHeight();
		}
		final Dimension dimension = new Dimension(content.getWidth(),height + PiecePanel.SIZE);
		content.setMinimumSize(dimension);
		content.setPreferredSize(dimension);
		content.revalidate();
		if(this.pieces.size() > 1) select(1);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() instanceof PiecePanel){
			final PiecePanel panel = (PiecePanel) e.getSource();
			select(getPosition(panel.name));
		}
	}
	
	public int getPosition(String pieceName){
		for(int i=0; i<pieces.size();i++)
			if(pieces.get(i).contains(pieceName))
				return i;
		return -1;
	}
	
	public void select(int position){
		if(position >= pieces.size() || position < 1) return;
		this.position = position;
		
		for(PiecePanel panel: pieces){
			panel.setSelect(false);
			panel.repaint();
		}
		
		final PiecePanel panel = pieces.get(position);
		panel.setSelect(true);
		panel.repaint();
		firePieceSelected(panel.name);
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		final int length = pieces.size();
		int incr = e.getWheelRotation();
		if(incr > 0) incr = 1;
		else if (incr < 0) incr = -1;
		
		int position = (this.position - 1) + (length - 1) + incr;
		position %= length - 1;
		position++;
		select(position);
		final PiecePanel panel = pieces.get(this.position);
		int yPos = 0;
		yPos += panel.getLocation().y;
		yPos -= panel.getHeight() >> 1;
		scroll.getVerticalScrollBar().setValue(yPos);
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
}
