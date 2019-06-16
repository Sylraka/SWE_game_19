package application.makemove.impl;

import java.lang.reflect.Array;
import java.util.*;

import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;
import application.makemove.port.MakeMoveManagement;
import application.statemachine.port.State;
import application.statemachine.port.StateMachine;
import application.statemachine.port.StateMachinePort;

public class MakeMoveManagementImpl implements MakeMoveManagement {
    private static final int MAX_ANZAHL_VERSUCHE = 3;
    private static final int SPIELFELDGROESSE = 48;
    private final List<Spieler> spielerliste;
    private StateMachine stateMachine;

    private boolean einfacheVariante;
    private int aktuelleRunde;
    private int anzahlWuerfe;
    private int augenzahl;
    private Spieler aktuellerSpieler;
    private Spieler gewinner;
    private int anzahlFigurenAufHeimatsfeld = 0;
    private Figur verteidiger;
    private Spieler verteidigerSpieler;

    // für die Figur, wohin sie sich bewegen kann in diesem Zug, int = Endpunkt
    private Map<Figur, Integer> moeglicheSchritte = new HashMap<Figur, Integer>();

    public MakeMoveManagementImpl(StateMachinePort stPort, List<Spieler> spielerliste) {
        this.stateMachine = stPort.stateMachine();

        this.spielerliste = spielerliste;
        this.aktuellerSpieler = spielerliste.get(0);
        this.aktuelleRunde = 0;
        this.anzahlWuerfe = 0;
    }

    @Override
    public void neueRundeStarten() {
        this.stateMachine.setState(State.S.WurfState);
    }

    @Override
    public void wuerfeln() {
        anzahlWuerfe++;
        augenzahl = getRandomAugenzahl();
        berechneMoeglicheSchritte();
    }

    /**
     * hat der Spieler eine Figur ausgewählt, die er bewegen möchte, wird diese
     * methode aufgerufen um die figur auf diese position zu setzten. ist das feld
     * leer, wird er draufgesetzt ist das feld besetzt, wird eine frage gestellt
     * &spieler wird drauf gesetzt -> richtige antwort: gegner auf startfeld ->
     * falsche antwort: gegner auf heimatfeld
     */
    @Override
    public void bewegeFigur(int figurNummer) {
        Figur figur = aktuellerSpieler.getFiguren()[figurNummer - 1];

        int aktuellesZiel = moeglicheSchritte.get(figur);
        Spieler spielerAufFeld = getSpielerAufFeld(aktuellesZiel);

        if (spielerAufFeld == null) { // auf dem Feld sitzt keine Figur
            resetVariablenFuerNaechsteRunde();
            this.stateMachine.setState(State.S.InitialState);
        } else { // Auf dem Feld sitzt eine andere Figur
            // Verteidiger wird zwischengespeichert
            for (Spieler verteidigerSpieler : spielerliste) {
                for (Figur verteidiger : verteidigerSpieler.getFiguren()) {
                    if (verteidiger.getPosition() == aktuellesZiel) {
                        this.verteidiger = verteidiger;
                        this.verteidigerSpieler = verteidigerSpieler;
                    }
                }
            }
            this.stateMachine.setState(State.S.FrageState);
        }
        figur.setPosition(aktuellesZiel);

    }

    @Override
    public void frageBeantworten(boolean isRichtig) {
        if (isRichtig) {
            if (eigeneFigurAufStartfeld()) {
                setFigurAufHeimatfeld(verteidiger);
            }
            setFigurAufStartfeld(verteidiger);
        } else {
            setFigurAufHeimatfeld(verteidiger);
        }

        resetVariablenFuerNaechsteRunde();
        this.moeglicheSchritte.clear();
        this.stateMachine.setState(State.S.InitialState);
    }

    @Override
    public void spielBeenden() {
        System.exit(0);
    }

    // Nur für Testzwecke, nicht implementiert
    @Override
    public Spieler getGewinner() {
        return this.gewinner;
    }

    @Override
    public int getAktuelleRunde() {
        return this.aktuelleRunde;
    }

    @Override
    public Spieler getAktuellerSpieler() {
        return this.aktuellerSpieler;
    }

    @Override
    public int getAugenzahl() {
        return augenzahl;
    }

    @Override
    public List<Spieler> getSpielerliste() {
        return this.spielerliste;
    }

    @Override
    public int getUebrigeAnzahlVersuche() {
        return MAX_ANZAHL_VERSUCHE - this.anzahlWuerfe;
    }

    @Override
    public Map<Figur, Integer> getMoeglicheSchritte() {
        return this.moeglicheSchritte;
    }

    @Override
    public boolean istEinfacheVariante() {
        return einfacheVariante;
    }

    @Override
    public void setEinfacheVariante(boolean istEinfacheVariante) {
        this.einfacheVariante = istEinfacheVariante;
    }

    private int getRandomAugenzahl() {
        return new Random().nextInt(6) + 1;
    }

    private void resetAnzahlWuerfe() {
        anzahlWuerfe = 0;
    }

    private void resetWuerfel() {
        augenzahl = 0;
    }

    private void setNaechsteRunde() {
        aktuelleRunde++;
    }

    private void resetVariablenFuerNaechsteRunde() {
        moeglicheSchritte.clear();

        setNaechsteRunde();
        setNaechsterSpieler();

        resetAnzahlWuerfe();
        resetWuerfel();
    }

    private void setNaechsterSpieler() {
        if (aktuellerSpieler == null) {
            aktuellerSpieler = spielerliste.get(0);
        } else {
            int nextId = (spielerliste.indexOf(aktuellerSpieler) + 1) % spielerliste.size();
            aktuellerSpieler = spielerliste.get(nextId);
        }
    }

    private boolean eigeneFigurAufStartfeld() {
        int startfeld = aktuellerSpieler.getStartFeld();

        for (Figur figur : aktuellerSpieler.getFiguren()) {
            if (startfeld == figur.getPosition()) {
                return true;
            }
        }
        return false;
    }

    private void berechneMoeglicheSchritte() {
        // private Map<Figur, Integer> moeglicheSchritte;
        berechneAnzahlFigurenAufHeimatsfeld();

        if (augenzahl == 6 && anzahlFigurenAufHeimatsfeld > 0 && !eigeneFigurAufStartfeld()) {
            // dann setze die figur aufs startfeld

            for (Figur figur : aktuellerSpieler.getFiguren()) {
                // wähle figur aus, die vom heimatfeld aufs startfeld soll
                if (figur.isHeimatsfeld()) {
                    moeglicheSchritte.put(figur, aktuellerSpieler.getStartFeld());
                }
            }

            this.stateMachine.setState(State.S.WahlState);
        } else { // augenzahl 1-5 oder alle figuren im spiel (keine auf heimatsfeld)
            if (istEineFigurImSpiel()) {
                // TODO berechneMoeglicheSpielzüge(figurAktuellerSPieler)
                for (Figur figur : aktuellerSpieler.getFiguren()) {
                    if (!figur.isHeimatsfeld()) {
                        moeglicheSchritte.put(figur, (figur.getPosition() + augenzahl) % SPIELFELDGROESSE);
                    }
                }
                if (!einfacheVariante) {
                    entferneUnmoeglicheSchritte();
                }
                this.stateMachine.setState(State.S.WahlState);
            } else {
                if (anzahlWuerfe == 3) {
                    resetVariablenFuerNaechsteRunde();
                    this.stateMachine.setState(State.S.InitialState);
                } else {
                    this.stateMachine.setState(State.S.WurfState);
                }
            }

        }

    }

    private void entferneUnmoeglicheSchritte() {
        List<Figur> unmoeglicheSchritte = new ArrayList<>();
        for (Map.Entry<Figur, Integer> moegSchritte : moeglicheSchritte.entrySet()) {
            int zielFeld = moegSchritte.getValue();
            for (Figur figur : aktuellerSpieler.getFiguren()) {
                if (figur.getPosition() == zielFeld) {
                    unmoeglicheSchritte.add(moegSchritte.getKey());
                }
            }
        }

        if (unmoeglicheSchritte.size() > 0) {
            for (Figur key : unmoeglicheSchritte) {
                moeglicheSchritte.remove(key);
            }
        }
    }

    private boolean istEineFigurImSpiel() {
        return anzahlFigurenAufHeimatsfeld < 3;
    }

    private void setFigurAufHeimatfeld(Figur verteidiger) {
        verteidiger.setPosition(-1);
    }

    private void setFigurAufStartfeld(Figur verteidiger) {
        verteidiger.setPosition(verteidigerSpieler.getStartFeld());

    }

    private boolean istAngetroffeneFigurEigene(Figur testFigur) {
        for (Figur figur : aktuellerSpieler.getFiguren()) {
            if (testFigur == figur) {
                return true;
            }
        }
        return false;
    }

    private void berechneAnzahlFigurenAufHeimatsfeld() {
        anzahlFigurenAufHeimatsfeld = 0;
        for (Figur figur : aktuellerSpieler.getFiguren()) {
            if (figur.isHeimatsfeld()) {
                anzahlFigurenAufHeimatsfeld++;
            }
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

    // TODO: Für Testzwecke
    public void zahlWuerfeln(int augenzahl) {
        anzahlWuerfe++;
        this.augenzahl = augenzahl;
        berechneMoeglicheSchritte();
    }
}
