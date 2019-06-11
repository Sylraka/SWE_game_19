package application.makemove.impl;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import application.statemachine.port.State;
import application.statemachine.port.StateMachine;
import application.statemachine.port.StateMachinePort;
import application.makemove.port.MakeMoveManagement;
import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;

public class MakeMoveManagementImpl implements MakeMoveManagement {
	private static final int MAX_ANZAHL_VERSUCHE = 3;
	private static final int SPIELFELDGROESSE = 48;
	private final List<Spieler> spielerliste;
	private StateMachine stateMachine;

	private int aktuelleRunde;
	private int anzahlWuerfe;
	private int augenzahl;
	private Spieler aktuellerSpieler;
	private Spieler gewinner;
	private int anzahlFigurenAufHeimatsfeld = 0;

	// für die Figur, wohin sie sich bewegen kann in diesem Zug, int = Endpunkt
	private Map<Figur, Integer> moeglicheSchritte;

	public MakeMoveManagementImpl(StateMachinePort stPort, List<Spieler> spielerliste) {
		System.out.println("Viel Spaß bei unserem Spiel! :)");
		System.out.println("Anzahl der Spieler: " + spielerliste.size());
		this.stateMachine = stPort.stateMachine();

		this.spielerliste = spielerliste;
		this.aktuellerSpieler = spielerliste.get(0);
		this.aktuelleRunde = 0;
		this.anzahlWuerfe = 0;
	}

	@Override
	public void startNewRound() {
		this.stateMachine.setState(State.S.WurfState);
	}

	@Override
	public void throwDice() {
		anzahlWuerfe++;
		augenzahl = getRandomDiceNumber();

		if (hasMoves())
			this.stateMachine.setState(State.S.WahlState);
		else if (anzahlWuerfe == 0) {
			this.stateMachine.setState(State.S.InitialState);
			resetVariablenFuerNaechsteRunde();
		} else {
			this.stateMachine.setState(State.S.WurfState);
		}
	}

	@Override
	public void bewegeFigur(Figur figur) {
		schliesseRundeAb(figur);
	}

	@Override
	public void endGame() {
		// TODO: Switch to next use case (Statistics) in next Sprint
		System.out.println("Game ended.");
		System.exit(0);
	}

	@Override
	public Spieler getWinner() {
		return this.gewinner;
	}

	@Override
	public int getRoundId() {
		return this.aktuelleRunde;
	}

	@Override
	public Spieler getCurrentPlayer() {
		return this.aktuellerSpieler;
	}

	@Override
	public int getDiceNumber() {
		return augenzahl;
	}

	@Override
	public List<Spieler> allPlayers() {
		return this.spielerliste;
	}

	@Override
	public int getTriesLeft() {
		return MAX_ANZAHL_VERSUCHE - this.anzahlWuerfe;
	}

	@Override
	public Map<Figur, Integer> getMoeglicheSchritte() {
		return this.moeglicheSchritte;
	}

	// stattdessen rufen wir den getter getStartFeld auf
	// private void initPlayingBoard() {
	// int currentStartMark = 0;
	// for (Spieler spieler : players) {
	// spieler.setStartField(currentStartMark);
	// currentStartMark += BOARD_SIZE / players.size();
	// }
	// }

	private boolean hasMoves() {
		this.moeglicheSchritte.clear();
		if (!istMinEineFigurAufSpielfeld()) {
			if (augenzahl != 6)
				return false;
			setJailBreakMoveOps();
		} else
			berechneMoeglicheSchritte();

		return true;
	}

	private boolean istMinEineFigurAufSpielfeld() {
		for (Figur f : aktuellerSpieler.getFiguren()) {
			if (!f.isHeimatsfeld())
				return true;
		}
		return false;
	}

	private void resetVariablenFuerNaechsteRunde() {
		moeglicheSchritte.clear();

		setNaechsteRunde();
		setNaechsterSpieler();

		setAnzahlVersuche();
		// TODO wollen wir das behalten?
		resetWuerfel();
	}

	private int getRandomDiceNumber() {
		return new Random().nextInt(6) + 1;
	}

	private void resetWuerfel() {
		augenzahl = 0;
	}

	private void setNaechsteRunde() {
		aktuelleRunde++;
	}

	private void setNaechsterSpieler() {
		if (aktuellerSpieler == null) {
			aktuellerSpieler = spielerliste.get(0);
			return;
		}

		int nextId = (spielerliste.indexOf(aktuellerSpieler) + 1) % spielerliste.size();
		aktuellerSpieler = spielerliste.get(nextId);
	}

	private void berechneMoeglicheSchritte() {
		// private Map<Figur, Integer> moeglicheSchritte;
		int endposition;

		if (augenzahl == 6 && anzahlFigurenAufHeimatsfeld > 0) {
			// dann setze die figur aufs startfeld

			for (Figur figur : aktuellerSpieler.getFiguren()) {
				if (figur.isHeimatsfeld()) {
					moeglicheSchritte.put(figur, aktuellerSpieler.getStartFeld());
				}
			}
			this.stateMachine.setState(State.S.WahlState);
		} else {
			if (istEineFigurImSpiel()) {
				// berechneMoeglicheSpielzüge(figurAktuellerSPieler)
				for (Figur figur : aktuellerSpieler.getFiguren()) {
					if (!figur.isHeimatsfeld()) {
						moeglicheSchritte.put(figur, (figur.getPosition() + augenzahl) % 48);
					}
				}
				// TODO: Einfügen einfache Variente!!!
				// if(einfacheVariante) {
				//
				// }
				// else {
				// entferneUnmoeglicheSchritte();
				// }
				this.stateMachine.setState(State.S.WahlState);
			} else {
				if (anzahlWuerfe == 3) {
					this.stateMachine.setState(State.S.InitialState);
				} else {
					// aktuellerSpieler würfelt nochmal???
				}
			}

		}

	}

	private boolean istEineFigurImSpiel() {
		return anzahlFigurenAufHeimatsfeld < 3;
	}

	private void berechneAnzahlFigurenAufHeimatsfeld() {
		anzahlFigurenAufHeimatsfeld = 0;
		for (Figur figur : aktuellerSpieler.getFiguren()) {
			if (figur.isHeimatsfeld()) {
				anzahlFigurenAufHeimatsfeld++;
			}
		}
	}

	private void setJailBreakMoveOps() {
		for (Figur f1 : aktuellerSpieler.getFiguren()) {
			Figur f2 = figureByField.get(aktuellerSpieler.getStartFeld());
			this.moeglicheSchritte.add(new MoveOps(f1, f2, aktuellerSpieler.getStartFeld()));
		}
	}

	private Spieler getSpielerAufFeld(int position) {
		for (Spieler spieler : spielerliste) {
			for (Figur figur : spieler.getFiguren()) {
				if (figur.getPosition() == position) {
					return spieler;
				}
			}
		}
		return null;
	}

	private void schliesseRundeAb(Figur figur) {

		int aktuellesZiel = moeglicheSchritte.get(figur);
		Spieler spielerAufFeld = getSpielerAufFeld(aktuellesZiel);

		if (spielerAufFeld == null) {

			// setFigureNewPosition(attackerFig, destField, true);
			this.stateMachine.setState(State.S.InitialState);
			resetVariablenFuerNaechsteRunde();

			// } else if (attackerFig.getPlayerId() != defenderFig.getPlayerId()) {

			this.moeglicheSchritte.clear();
			this.moeglicheSchritte.add(new MoveOps(attackerFig, defenderFig, defenderFig.getPosition()));

			this.stateMachine.setState(State.S.ChooseQCategoryState);
			// } else {
			applyCascadeResolution(attackerFig, defenderFig.getPosition());
		}

	}

	private void applyCascadeResolution(Figur fig, int destField) {
		Figur targetFig = figureByField.get(destField);
		while (targetFig != null) {
			destField = decreaseFieldNumb(destField);
			targetFig = figureByField.get(destField);
		}

		setFigureNewPosition(fig, destField, false);
		this.stateMachine.setState(State.S.InitialState);
		resetVariablenFuerNaechsteRunde();
	}

	private int decreaseFieldNumb(int destField) {
		if (destField == 0)
			return 47;
		else
			return --destField;
	}

	private Spieler getPlayerById(int playerId) {
		for (Spieler pl : spielerliste) {
			if (pl.getSpielerNummer() == playerId)
				return pl;
		}
		return null;
	}

	private void setFigureNewPosition(Figur figure, int position, boolean withRemove) {
		if (figure == null)
			return;

		System.out.format("Replaced figure#%d of player#%d from %d to %d\n", figure.getFigurNummer(),
				figure.getPlayerId(), figure.getPosition(), position);

		if (withRemove)
			figureByField.remove(figure.getPosition());

		getPlayerById(figure.getPlayerId()).getFiguren()[figure.getFigurNummer()].setPosition(position);

		Figur oldFig = figureByField.get(position);
		if (oldFig != null)
			figureByField.remove(position);

		figureByField.put(position, figure);
	}

	// TODO: delete, for debugging
	public void throwCheatDice(int cheatDice) {
		anzahlWuerfe++;
		augenzahl = cheatDice;

		if (hasMoves())
			this.stateMachine.setState(State.S.WahlState);
		// TODO Überprüfen, ob es richtig ist
		else if (anzahlWuerfe == 3) {
			this.stateMachine.setState(State.S.InitialState);
			resetVariablenFuerNaechsteRunde();
		} else {
			this.stateMachine.setState(State.S.WurfState);
		}
	}

	// TODO: delete, for debugging
	public Figur getFigureByField(int pos) {
		return this.figureByField.get(pos);
	}

}
