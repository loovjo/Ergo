package com.loovjo.ergoclient;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import com.loovjo.ergo.card.Card;
import com.loovjo.loo2D.scene.Scene;
import com.loovjo.loo2D.utils.Vector;

public class ClientScene implements Scene, Runnable {

	private final int startY = 30;
	private final int startX = 10;
	private final int boardY = 80;
	private final int spaceBetweenCards = 40;
	private int cardSize = 30;

	public Client client;

	public ArrayList<Card> cards = new ArrayList<Card>();

	public ArrayList<ArrayList<Card>> board = new ArrayList<ArrayList<Card>>();

	public boolean myTurn = false;

	public PlayerCard holding = null;

	public int dragndropSignWidth = 0;
	
	public long lastClick = System.currentTimeMillis();
	
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

			if (gs == 3) {
				if (command.equals("P"))
					myTurn = true;
				else if (command.equals("NP"))
					myTurn = false;
				gs = 0;
			}

			if (gs == 2) {
				gs = 3;
				board.clear();
				board.add(new ArrayList<Card>());
				System.out.println("\t" + command + ", " + Arrays.toString(command.split("\\|")));
				for (char c : command.toCharArray()) {
					if (c == '|')
						board.add(new ArrayList<Card>());
					else
						board.get(board.size() - 1).add(Card.getCardFromSign(c + ""));

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

		Color background = Color.green.darker().darker();
		if (myTurn)
			background = background.brighter();
		g.setColor(background);
		g.fillRect(0, 0, width, height);
		g.setColor(background.darker());
		g.fillRect(0, boardY - 10, width, 10);
		g.setColor(Color.white);

		g.setFont(new Font("Monospaced", Font.PLAIN, cardSize));
		g.drawString("Player: " + client.name, 0, cardSize);
		for (PlayerCard card : getCardPoses()) {
			int x = (int) card.getPos().getX();
			int y = (int) card.getPos().getY();
			g.setColor(Color.blue);
			g.fillRect(x, y, cardSize, cardSize);
			g.setColor(Color.cyan.darker());
			g.drawRect(x, y, cardSize, cardSize);
			g.drawRect(x + 1, y + 1, cardSize - 2, cardSize - 2);
			g.setColor(Color.red);
			g.setFont(new Font("Monospaced", Font.PLAIN, cardSize));
			g.drawString(card.getCard().getShortName(), x, (int) (y + cardSize * 0.9));
		}
		// Drop 'n' discard box:
		if (holding != null) {
			g.setColor(Color.red);
			g.setFont(new Font("Helvetica", Font.PLAIN, 24));
			String text = "Drop 'n' discard";
			dragndropSignWidth = g.getFontMetrics().stringWidth(text);
			g.fillRoundRect(width - dragndropSignWidth - 3, -3, width, g.getFont().getSize() + 5, 5, 5);
			g.setColor(g.getColor().darker());
			g.drawRoundRect(width - dragndropSignWidth - 3, -3, width, g.getFont().getSize() + 5, 5, 5);
			g.drawString(text, width - dragndropSignWidth, g.getFont().getSize());
		}
	}

	public ArrayList<PlayerCard> getCardPoses() {
		ArrayList<PlayerCard> allCards = new ArrayList<PlayerCard>();

		int y = startY;
		int x = startX;

		for (int i = 0; i < cards.size(); i++) {
			allCards.add(new PlayerCard(cards.get(i), new Vector(x, y)));
			x += spaceBetweenCards;
		}
		y = boardY;
		for (int line = 0; line < board.size(); line++) {
			x = startX;
			for (int col = 0; col < board.get(line).size(); col++) {
				if (holding != null && line == getHoldingY() && col == getHoldingX())
					x += spaceBetweenCards;
				allCards.add(new PlayerCard(board.get(line).get(col), new Vector(x, y)));
				x += spaceBetweenCards;
			}
			y += spaceBetweenCards;
		}
		if (holding != null)
			allCards.add(holding);
		return allCards;
	}

	@Override
	public void mousePressed(Vector pos, int button) {
		lastClick = System.currentTimeMillis();
		
		PlayerCard temp = holding;
		PlayerCard carD = null;
		for (PlayerCard card : getCardPoses()) {
			if (card.getPos().getX() < pos.getX() && card.getPos().getX() + cardSize > pos.getX()
					&& card.getPos().getY() < pos.getY() && card.getPos().getY() + cardSize > pos.getY()) {
				carD = card;
			}
		}
		holding = carD;
		if (holding == null || holding == temp) {
			holding = temp;
			int cardX = getHoldingX();
			int cardY = getHoldingY();
			System.out.println("Row: " + cardX + ", Col: " + cardY);
			System.out.println(board);

			String send = holding.getCard().getShortName() + "," + cardY + "," + cardX;
			if (cardX < 0 || cardY < 0) {
				if (holding.getPos().getY() > startY && holding.getPos().getY() < startY + cardSize) {
					cards.add(holding.getCard());
					holding = null;
					return;
				}
				send = "discard " + holding.getCard().getShortName();
			}
			System.out.println("\t\tCommand: " + send);
			client.send(send);
			holding = null;
		} else {
			int cardIndex = (int) ((holding.getPos().getX() - startX) / spaceBetweenCards);
			cards.remove(cardIndex);

			holding.setPos(pos.sub(new Vector(cardSize, cardSize).div(2)));

		}

	}

	private int getHoldingY() {
		int cardY = Math.round((holding.getPos().getY() - boardY) / spaceBetweenCards );
		return cardY;
	}

	private int getHoldingX() {
		int cardX = Math.round((holding.getPos().getX() - startX) / spaceBetweenCards);
		return cardX;
	}

	@Override
	public void mouseReleased(Vector pos, int button) {
		if (System.currentTimeMillis() - lastClick > 300)
			mousePressed(pos, button);
	}

	@Override
	public void mouseMoved(Vector pos) {
		if (holding != null)
			holding.setPos(pos.sub(new Vector(cardSize, cardSize).div(2)));
	}

	@Override
	public void keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_SPACE) {
			String line = JOptionPane.showInputDialog("What command?");
			client.send(line);
		}
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
