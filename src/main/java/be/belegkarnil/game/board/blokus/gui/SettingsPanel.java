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

import be.belegkarnil.game.board.blokus.BelegBlokus;
import be.belegkarnil.game.board.blokus.Board;
import be.belegkarnil.game.board.blokus.Game;
import be.belegkarnil.game.board.blokus.Player;
import be.belegkarnil.game.board.blokus.Strategy;
import be.belegkarnil.game.board.blokus.event.GameAdapter;
import be.belegkarnil.game.board.blokus.event.GameEvent;
import be.belegkarnil.game.board.blokus.event.TurnEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.lang.reflect.Constructor;

/**
 * This class is a GUI component of the Game. It is the settings panel ({@link BlokusPanel}).
 *
 * @author Belegkarnil
 */
public class SettingsPanel extends BlokusPanel implements ActionListener {
	private static final String START_GAME_COMMAND = "STAT_GAME";
	private static final String LOAD_STRATEGY_COMMAND = "LOAD_STRATEGY";
	private static final String SELECT_FIRST_PLAYER_COMMAND = "SELECT_FIRST_PLAYER";
	private static final String SELECT_SECOND_PLAYER_COMMAND = "SELECT_SECOND_PLAYER";
	private JComboBox firstPlayerName, secondPlayerName;
	private JTextField timeout, winningRounds, skip;
	private JSlider speed;
	private final Board board;
	public SettingsPanel(Board board){
		this.board	= board;
		setLayout(new FlowLayout());
		createStrategyPanel();
		createConfigurationPanel();
		createActionPanel();
		
	}
	
	@Override
	void register(Game game) {
		firstPlayerName.setEnabled(false);
		secondPlayerName.setEnabled(false);
		timeout.setEnabled(false);
		winningRounds.setEnabled(false);
		skip.setEnabled(false);
		
		game.addGameListener(new GameAdapter() {
			@Override
			public void onTurnEnded(TurnEvent te){
				int delay = speed.getValue() * 1000;
				if(delay < 1) return;
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onGameEnded(GameEvent ge) {
				firstPlayerName.setEnabled(true);
				secondPlayerName.setEnabled(true);
				timeout.setEnabled(true);
				winningRounds.setEnabled(true);
				skip.setEnabled(true);
			}
		});
	}
	
	@Override
	void onPieceSelected(String name) {
	
	}
	
	private void createStrategyPanel(){
		//final JPanel panel = new JPanel(new GridLayout(2,2));
		final JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		
		panel.setBorder(BorderFactory.createTitledBorder("Strategies"));
		final JLabel first	= new JLabel("First player:");
		final JLabel second	= new JLabel("Second player:");
		
		
		firstPlayerName	= new JComboBox(BelegBlokus.listStrategies().toArray());
		secondPlayerName	= new JComboBox(BelegBlokus.listStrategies().toArray());
		firstPlayerName.setActionCommand(SELECT_FIRST_PLAYER_COMMAND);
		secondPlayerName.setActionCommand(SELECT_SECOND_PLAYER_COMMAND);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.Group yLabelGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
		hGroup.addGroup(yLabelGroup);
		
		GroupLayout.Group yFieldGroup = layout.createParallelGroup();
		hGroup.addGroup(yFieldGroup);
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		layout.setVerticalGroup(vGroup);
		
		yLabelGroup.addComponent(first);
		yLabelGroup.addComponent(second);
		
		yFieldGroup.addComponent(firstPlayerName);
		yFieldGroup.addComponent(secondPlayerName);
		
		vGroup.addGroup(layout.createParallelGroup().addComponent(first).addComponent(firstPlayerName));
		vGroup.addGroup(layout.createParallelGroup().addComponent(second).addComponent(secondPlayerName));
		
		/*
		panel.add(first);
		panel.add(firstPlayerName);
		panel.add(second);
		panel.add(secondPlayerName);
		
		 */
		add(panel);
	}
	
	private void loadStrategy(){
		System.out.println("Not yet impl: loadStrategy@"+getClass().getSimpleName());
		/*
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Java Strategy class", "class");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
			//URLClassLoader loader = new URLClassLoader(new URL[]{chooser.getSelectedFile().getAbsoluteFile().toURI().toURL()});
			System.out.println("TODO loadStrategy@"+getClass().getName());
			//Class myclass  = loader.loadClass("package.MyClass");
		}
		 */
	}
	private void createConfigurationPanel(){
		final JPanel config = new JPanel(new BorderLayout());
		
		final JPanel panel = new JPanel(new GridLayout(2,3));
		panel.setBorder(BorderFactory.createTitledBorder("Settings"));
		panel.add(new JLabel("Timeout (s):"));
		panel.add(new JLabel("# Winning rounds:"));
		panel.add(new JLabel("Skip limit:"));
		
		timeout 			= new JTextField(String.valueOf(Game.DEFAULT_TIMEOUT));
		winningRounds	= new JTextField(String.valueOf(Game.DEFAULT_NUMBER_OF_WINNING_ROUNDS));
		skip				= new JTextField(String.valueOf(Game.DEFAULT_SKIP_LIMIT));
		
		panel.add(timeout);
		panel.add(winningRounds);
		panel.add(skip);
		
		final JPanel speed = new JPanel(new BorderLayout());
		speed.setBorder(BorderFactory.createTitledBorder("Speed"));
		this.speed = new JSlider(0,30,2);
		this.speed.setPaintTrack(true);
		this.speed.setPaintTicks(true);
		this.speed.setPaintLabels(true);
		this.speed.setMajorTickSpacing(5);
		this.speed.setMinorTickSpacing(1);
		speed.add(this.speed,BorderLayout.CENTER);
		
		config.add(panel,BorderLayout.CENTER);
		config.add(speed,BorderLayout.SOUTH);
		add(config);
	}
	private void createActionPanel(){
		final JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Actions"));
		final JButton play = new JButton("Play");
		final JButton load = new JButton("Load Strategy");
		play.setActionCommand(START_GAME_COMMAND);
		play.addActionListener(this);
		load.setActionCommand(LOAD_STRATEGY_COMMAND);
		load.addActionListener(this);
		panel.add(load);
		panel.add(play);
		add(panel);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		System.out.println("actionPerformed@"+getClass().getSimpleName());
		
		switch(ae.getActionCommand()){
			case START_GAME_COMMAND: startGame(); break;
			case LOAD_STRATEGY_COMMAND: loadStrategy(); break;
		}
	}
	
	private void startGame() {
		int timeout = -1;
		int winningRounds = -1;
		int skip = -1;
		
		try {
			timeout = Integer.parseInt(this.timeout.getText());
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Timeout setting must be an integer", "Parsing error", JOptionPane.ERROR_MESSAGE);
		}
		try {
			winningRounds = Integer.parseInt(this.winningRounds.getText());
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Number of winning rounds setting must be an integer", "Parsing error", JOptionPane.ERROR_MESSAGE);
		}
		try {
			skip = Integer.parseInt(this.skip.getText());
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Skip setting must be an integer", "Parsing error", JOptionPane.ERROR_MESSAGE);
		}
		
		if(timeout <= 0 || winningRounds <= 0 || skip <= 0){
			JOptionPane.showMessageDialog(this, "Setting must be an integer greater than 0", "Parsing error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// TODO players
		Strategy firstStrategy = null, secondStrategy = null;
		try {
			Class<Strategy> klass = (Class<Strategy>) (firstPlayerName.getSelectedItem());
			Constructor<?> constructor = klass.getConstructor();
			firstStrategy = (Strategy) constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("TODO Joptionpane...");// TODO
		}
		try {
			Class<Strategy> klass = (Class<Strategy>) (secondPlayerName.getSelectedItem());
			Constructor<?> constructor = klass.getConstructor();
			secondStrategy = (Strategy) constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("TODO Joptionpane...");// TODO
		}
		if(firstStrategy == null || secondStrategy == null) return;
		
		// TODO freeze GUI settings
		
		// TODO create the game and pass it to Game class
		final Player first	= new Player(firstStrategy.getClass().getName(), firstStrategy);
		final Player second	= new Player(secondStrategy.getClass().getName(), secondStrategy);
		final Game game = new Game(board, first,second, timeout, winningRounds, skip);
		BlokusPanel.initGame(game);
		new Thread(game).start();
	}
}
