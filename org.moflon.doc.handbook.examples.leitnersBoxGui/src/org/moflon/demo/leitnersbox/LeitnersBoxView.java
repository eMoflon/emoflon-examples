package org.moflon.demo.leitnersbox;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class LeitnersBoxView {

	private LeitnersBoxController controller;
	private JMenu partitionsMenu;
	private JFrame frame;

	private Map<String, JMenu> submenus = new HashMap<>();

	public LeitnersBoxView(LeitnersBoxController controller) {
		controller.setView(this);
		this.controller = controller;
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LeitnersBoxView(new LeitnersBoxController())
						.createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		frame = new JFrame("Leitner's Box");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setJMenuBar(createMenuBar(frame));
		frame.setContentPane(createContentPane());

		frame.setSize(220, 220);
		frame.setVisible(true);

		controller.loadXmiFile();
	}

	public JMenu getPartitionsMenu() {
		return partitionsMenu;
	}

	private JMenuBar createMenuBar(final JFrame frame) {
		JMenuBar menuBar = new JMenuBar();

		partitionsMenu = new JMenu("Partitions");
		partitionsMenu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(partitionsMenu);

		return menuBar;
	}

	public Container createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);

		JLabel output = new JLabel(
				createImageIcon("icons/noun_project_870.png"));
		contentPane.add(output, BorderLayout.CENTER);

		contentPane
				.setToolTipText("Flash Cards designed by Rohan Gupta, "
						+ "Memory designed by Anne Marie Nguyen, from The Noun Project.");

		return contentPane;
	}

	private static ImageIcon createImageIcon(String filename) {
		if (new File(filename).exists()) {
			return new ImageIcon(filename);
		} else {
			System.err.println("Couldn't find file: " + filename);
			return null;
		}
	}

	void createPartition(String name) {
		JMenu submenu = new JMenu(name);
		submenu.setIcon(createImageIcon("icons/noun_project_4767.png"));
		submenus.put(name, submenu);
		partitionsMenu.add(submenu);
	}

	void createCard(String submenu, final String cardName, int partIndex,
			int cardIndex) {
		JMenu subCard = new JMenu(cardName);

		// Create Menu options for each action
		JMenuItem cardRemove = new JMenuItem();
		JMenuItem cardCheck = new JMenuItem();

		submenus.get(submenu).add(subCard);

		subCard.add(cardRemove);
		subCard.add(cardCheck);

		setRemoveCardAction(cardRemove, partIndex, cardIndex);
		setCheckCardAction(cardCheck, partIndex, cardIndex);

		cardRemove.setText("Remove Card");
		cardCheck.setText("Check Card");
	}

	@SuppressWarnings("serial")
	public void setRemoveCardAction(JMenuItem card, final int partitionIndex,
			final int cardIndex) {
		card.setAction(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.doActionRemoveCard(partitionIndex, cardIndex);
			}
		});
	}

	@SuppressWarnings("serial")
	public void setCheckCardAction(JMenuItem card, final int partIndex,
			final int cardIndex) {
		card.setAction(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.doActionCheckCard(partIndex, cardIndex);
			}

		});
	}

	public String getUserGuess() {
		String guess = JOptionPane.showInputDialog("Your Guess:");
		return guess;
	}

	public void refreshGUI() {
		saveModel();
		clearView();
		controller.loadXmiFile();
	}

	public void saveModel() {
		try {
			controller.resource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clearView() {
		submenus.clear();
		partitionsMenu.removeAll();
	}
}