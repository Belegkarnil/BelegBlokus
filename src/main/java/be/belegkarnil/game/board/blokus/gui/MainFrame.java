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
import be.belegkarnil.game.board.blokus.Player;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * This class is a GUI component of the Game. It is the main window ({@link JFrame}).
 *
 * @author Belegkarnil
 */
public class MainFrame extends JFrame{
	private final BoardPanel boardPanel;
	private final SettingsPanel settingsPanel;
	private final LogPanel logPanel;
	private final PiecesPanel piecesPanel;
	private final TimePanel timePanel;
	private final Board board;
	
	private static MainFrame instance = null;
	
	private MainFrame(){
		super("BelegBlokusDuo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel contentPane = new JPanel(new BorderLayout());
		
		board = new Board();
		
		boardPanel		= new BoardPanel(board);
		logPanel			= new LogPanel();
		settingsPanel	= new SettingsPanel(board);
		piecesPanel		= new PiecesPanel();
		timePanel		= new TimePanel();
		
		
		contentPane.add(boardPanel,BorderLayout.CENTER);
		contentPane.add(settingsPanel,BorderLayout.NORTH);
		contentPane.add(logPanel,BorderLayout.WEST);
		contentPane.add(piecesPanel,BorderLayout.EAST);
		contentPane.add(timePanel,BorderLayout.SOUTH);
		
		setContentPane(contentPane);
		addMouseWheelListener(piecesPanel);
	}
	
	
	public static MainFrame getInstance(){
		if(instance == null) instance = new MainFrame();
		return instance;
	}
}
