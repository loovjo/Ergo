package com.loovjo.ergoclient;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.loovjo.ergo.card.Card;
import com.loovjo.loo2D.scene.Scene;
import com.loovjo.loo2D.utils.Vector;

public class ClientScene implements Scene, Runnable {

	public Client client;

	public ArrayList<Card> cards = new ArrayList<Card>();

	public ArrayList<ArrayList<Card>> board = new ArrayList<ArrayList<Card>>();

	public ClientScene(Client cl) {
		this.client = cl;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		int gs = 0;

		while (true) {
			String command = client.waitForLine();
			System.out.println("\t" + client.name);
			System.out.println(command);
			
			if (gs == 2) {
				gs = 0;
				board.clear();
				for (int i = 0; i < command.split("\\|").length; i++) {
					String line = command.split("\\|")[i];
					System.out.println("Line: \"" + line + "\"");
					board.add(new ArrayList<Card>());
					for (char c : line.toCharArray()) {
						board.get(board.size() - 1).add(Card.getCardFromSign(c + ""));
					}
				}
				System.out.println(board);
			}

			if (gs == 1) {
				cards.clear();
				for (String sign : command.split(",")) {
					System.out.println(sign + ", " + Card.getCardFromSign(sign));
					cards.add(Card.getCardFromSign(sign));
				}
				gs++;
			}

			if (command.equals("gs"))
				gs = 1;

		}
	}

	@Override
	public void render(Graphics g, int width, int height) {
		int w = 30;
		int y = 50;
		
		g.setFont(new Font("Monospaced", Font.PLAIN, w));
		g.drawString("Player: " + client.name, 0, w);
		
		
		for (int i = 0; i < cards.size(); i++) {
			int x = (int) (i * (w * 1.1));
			g.setColor(Color.red);
			g.fillRect(x, y, w, w);
			g.setColor(Color.black);
			g.drawString(cards.get(i).getShortName(), x, (int) (y + w * 0.9));
		}
		y += w;
		for (int line = 0; line < board.size(); line++) {
			y += w * 1.1;
			for (int col = 0; col < board.get(line).size(); col++) {
				int x = (int) (col * (w * 1.1));
				g.setColor(Color.red);
				g.fillRect(x, y, w, w);
				g.setColor(Color.black);
				g.setFont(new Font("Monospaced", Font.PLAIN, w));
				g.drawString(board.get(line).get(col).getShortName(), x, (int) (y + w * 0.9));
			}
		}
	}

	@Override
	public void mousePressed(Vector pos, int button) {
		String line = JOptionPane.showInputDialog("What command?");
		client.send(line);
	}

	@Override
	public void mouseReleased(Vector pos, int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(Vector pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(int keyCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(int keyCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(char key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheal(MouseWheelEvent e) {
		// TODO Auto-generated method stub

	}

}
