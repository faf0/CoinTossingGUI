package view;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import model.CoinTossingSimulation;

/**
 * Visualizes the coin tossing simulation with a 2-D plot. The horizontal axis
 * represents number of tosses and the vertical axis shows the cumulative
 * tossing outcome per time (one unit up for head and one unit down for tail).
 * 
 * @author Fabian Foerg
 */
public final class Graph extends Chart2D implements Observer {
    private static final long serialVersionUID = 1L;

    private static final Color HEAD_COLOR = Color.GREEN.darker();
    private static final Color TAIL_COLOR = Color.RED;

    private Stack<ITrace2D> traces;
    private int currentVertValue;
    private int currentHorValue;

    public Graph() {
        super();
        getAxisX().getAxisTitle().setTitle("# tosses");
        getAxisY().getAxisTitle().setTitle("# heads minus # tails");
        traces = new Stack<ITrace2D>();
        currentVertValue = 0;
        currentHorValue = 0;
    }

    @Override
    public void update(Observable ob, Object headOrTail) {
        final CoinTossingSimulation.COIN var = ((headOrTail != null) && headOrTail instanceof CoinTossingSimulation.COIN) ? (CoinTossingSimulation.COIN) headOrTail
                : null;

        update(var);
    }

    /**
     * A coin was tossed and this graph is updated correspondingly.
     * 
     * @param headOrTail
     *            the coin value that was tossed; is either a head or a tail.
     */
    public void update(CoinTossingSimulation.COIN headOrTail) {
        currentHorValue++;

        if (CoinTossingSimulation.COIN.HEAD.equals(headOrTail)) {
            currentVertValue++;

            if ((currentVertValue - 1) == 0) {

                // the number of heads was equal to the number of tails before
                ITrace2D trace = new Trace2DSimple();
                trace.setColor(HEAD_COLOR);
                trace.setName(null);
                addTrace(trace);
                trace.addPoint(currentHorValue - 1, currentVertValue - 1);
                trace.addPoint(currentHorValue, currentVertValue);
                traces.push(trace);
            } else {

                // we are already above or below the horizontal axis
                ITrace2D trace = null;

                if (traces.isEmpty()) {
                    trace = new Trace2DSimple();
                    trace.setColor(HEAD_COLOR);
                    trace.setName(null);
                    addTrace(trace);
                    trace.addPoint(currentHorValue - 1, currentVertValue - 1);
                    traces.push(trace);
                } else {
                    trace = traces.peek();
                }

                trace.addPoint(currentHorValue, currentVertValue);
            }
        } else {
            currentVertValue--;

            if ((currentVertValue + 1) == 0) {

                // the number of heads was equal to the number of tails before
                ITrace2D trace = new Trace2DSimple();
                trace.setColor(TAIL_COLOR);
                trace.setName(null);
                addTrace(trace);
                trace.addPoint(currentHorValue - 1, currentVertValue + 1);
                trace.addPoint(currentHorValue, currentVertValue);
                traces.push(trace);
            } else {

                // we are already above or below the horizontal axis
                ITrace2D trace = null;

                if (traces.isEmpty()) {
                    trace = new Trace2DSimple();
                    trace.setColor(TAIL_COLOR);
                    trace.setName(null);
                    addTrace(trace);
                    trace.addPoint(currentHorValue - 1, currentVertValue + 1);
                    traces.push(trace);
                } else {
                    trace = traces.peek();
                }

                trace.addPoint(currentHorValue, currentVertValue);
            }
        }
    }

    /**
     * Frees all resources consumed by this graph.
     */
    public void free() {
        traces.clear();
        removeAllTraces();
        destroy();
    }
}
