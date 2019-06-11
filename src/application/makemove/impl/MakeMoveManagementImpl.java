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
	private int uebrigeVersuche;
	private int augenzahl;
	private Spieler aktuellerSpieler;
	private Spieler gewinner;

	// für die Figur, wohin sie sich bewegen kann in diesem Zug, int = Endpunkt
	private Map<Figur, Integer> moeglicheSchritte;

	public MakeMoveManagementImpl(StateMachinePort stPort, List<Spieler> spielerliste) {
		System.out.println("Viel Spaß bei unserem Spiel! :)");
		System.out.println("Anzahl der Spieler: " + spielerliste.size());
		this.stateMachine = stPort.stateMachine();

		this.spielerliste = spielerliste;
		this.aktuellerSpieler = spielerliste.get(0);
		this.aktuelleRunde = 0;
		this.uebrigeVersuche = MAX_ANZAHL_VERSUCHE;
		this.moeglicheSchritte = new ArrayList<>();

	}

	@Override
	public void startNewRound() {
		this.stateMachine.setState(State.S.WurfState);
	}

	@Override
	public void throwDice() {
		uebrigeVersuche--;
		augenzahl = getRandomDiceNumber();

		if (hasMoves())
			this.stateMachine.setState(State.S.WahlState);
		else if (uebrigeVersuche == 0) {
			this.stateMachine.setState(State.S.InitialState);
			resetVariablenFuerNaechsteRunde();
		} else {
			this.stateMachine.setState(State.S.WurfState);
		}
	}

	@Override
	public void chooseMove(int optionId) {
		MoveOps mOps = null;
		try {
			mOps = moeglicheSchritte.get(optionId);
		} catch (IndexOutOfBoundsException e) {
			return;
		}

		resolveFieldConflicts(mOps.figureAttacker, mOps.figureDefender, mOps.destinationField);
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
		return this.uebrigeVersuche;
	}

	@Override
	public List<MoveOps> getMoveOps() {
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

	private void setAnzahlVersuche() {
		if (!istMinEineFigurAufSpielfeld())
			uebrigeVersuche = MAX_ANZAHL_VERSUCHE;
		else
			uebrigeVersuche = 1;
	}

	private void berechneMoeglicheSchritte() {
		// private Map<Figur, Integer> moeglicheSchritte;
		int endposition;
		boolean hasAtHome = false; // es sitzt eine figur auf dem heimatsfeld

		if (augenzahl == 6) {
			for (Figur f1 : aktuellerSpieler.getFiguren()) {
				if (f1.isHeimatsfeld()) {
					hasAtHome = true;
					endposition = aktuellerSpieler.getStartFeld();
				} else {
					endposition = (f1.getPosition() + augenzahl) % SPIELFELDGROESSE;
				}

				Figur f2 = figureByField.get(endposition);
				this.moeglicheSchritte.add(new MoveOps(f1, f2, endposition));
			}
			// if (hasAtHome) {
			// //wenn ni
			// for (Iterator<MoveOps> iterator = moeglicheSchritte.iterator();
			// iterator.hasNext();) {
			// MoveOps opt = iterator.next();
			// if (opt.figureAttacker.getPosition() != HOME_POSITION) {
			// iterator.remove();
			// }
			// }
			// }

		} else {
			for (Figur f1 : aktuellerSpieler.getFiguren()) {
				if (f1.getPosition() == HOME_POSITION)
					continue;

				endposition = (f1.getPosition() + augenzahl) % SPIELFELDGROESSE;
				Figur f2 = figureByField.get(endposition);
				this.moeglicheSchritte.add(new MoveOps(f1, f2, endposition));
			}
		}
	}

	private void setJailBreakMoveOps() {
		for (Figur f1 : aktuellerSpieler.getFiguren()) {
			Figur f2 = figureByField.get(aktuellerSpieler.getStartFeld());
			this.moeglicheSchritte.add(new MoveOps(f1, f2, aktuellerSpieler.getStartFeld()));
		}
	}

	private void resolveFieldConflicts(Figur attackerFig, Figur defenderFig, int destField) {
		if (defenderFig == null) {

			setFigureNewPosition(attackerFig, destField, true);
			this.stateMachine.setState(State.S.InitialState);
			resetVariablenFuerNaechsteRunde();

		} else if (attackerFig.getPlayerId() != defenderFig.getPlayerId()) {

			this.moeglicheSchritte.clear();
			this.moeglicheSchritte.add(new MoveOps(attackerFig, defenderFig, defenderFig.getPosition()));

			this.stateMachine.setState(State.S.ChooseQCategoryState);
		} else {
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
		uebrigeVersuche--;
		augenzahl = cheatDice;

		if (hasMoves())
			this.stateMachine.setState(State.S.WahlState);
		else if (uebrigeVersuche == 0) {
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
