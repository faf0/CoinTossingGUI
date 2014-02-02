package model;

import java.util.Observable;
import java.util.Random;

/**
 * Simulates coin tossing. The simulation counts the number of times that head
 * was on the winning side. Provides statistics for the simulation.
 * 
 * @author Fabian Foerg
 */
public final class CoinTossingSimulation extends Observable {
    public static enum COIN {
        HEAD, TAIL;
    }

    private int numberOfTosses;
    private int tosses;
    private int numberOfHeads;
    private int headWinnerSideTime;
    private Random random;
    private boolean interrupted;

    /**
     * Creates a new simulation using a pseudo-random number which is seeded.
     * 
     * @param numberOfTosses
     *            the number of tosses.
     * @param seed
     *            the seed for the pseudo-random number generator.
     */
    public CoinTossingSimulation(int numberOfTosses, long seed) {
        init(numberOfTosses);
        random.setSeed(seed);
    }

    /**
     * Creates a new simulation using a pseudo-random number generator.
     * 
     * @param numberOfTosses
     *            the number of tosses.
     */
    public CoinTossingSimulation(int numberOfTosses) {
        init(numberOfTosses);
    }

    /**
     * Actual initialization method for objects of this class.
     * 
     * @param numberOfTosses
     *            the number of tosses.
     */
    private void init(int numberOfTosses) {
        if (numberOfTosses < 1) {
            throw new IllegalArgumentException(
                    "numberOfTosses must be at least 1!");
        }

        this.numberOfTosses = numberOfTosses;
        tosses = 0;
        numberOfHeads = 0;
        headWinnerSideTime = 0;
        random = new Random();
        interrupted = false;
    }

    /**
     * Runs the simulation.
     */
    public void startGameSimulation() {
        boolean headIsLeading = true;

        // toss the coin "numberOfTosses" times
        for (tosses = 1; (tosses <= numberOfTosses) && !interrupted; tosses++) {
            if (random.nextBoolean()) {

                // coin shows head
                numberOfHeads++;
                int numberOfTails = tosses - numberOfHeads;

                if (numberOfHeads > numberOfTails) {
                    headWinnerSideTime++;
                    headIsLeading = true;
                } else if (numberOfHeads == numberOfTails) {
                    if (headIsLeading) {

                        // equal number of heads and tails, but we had more
                        // heads before, so this counts for head winner side
                        // time
                        headWinnerSideTime++;
                    }
                }

                setChanged();
                notifyObservers(COIN.HEAD);
            } else {

                // coin shows tail
                int numberOfTails = tosses - numberOfHeads;

                if (numberOfTails > numberOfHeads) {
                    headIsLeading = false;
                } else {

                    // numberOfTails <= numberOfHeads
                    // That means we had more heads before or still have more
                    // more heads.
                    // In any case, the heads are/were on the winning side.
                    headWinnerSideTime++;
                }

                setChanged();
                notifyObservers(COIN.TAIL);
            }
        }

        tosses--;
    }

    public void stopSimulation() {
        interrupted = true;
    }

    public int getNumberOfTosses() {
        return numberOfTosses;
    }

    public int getNumberOfTossesRealized() {
        return tosses;
    }

    public int getNumberOfHeads() {
        return numberOfHeads;
    }

    public int getHeadWinnerSideTimeAbsolute() {
        return headWinnerSideTime;
    }

    public double getHeadWinnerSideTimeRelative() {
        return (double) getHeadWinnerSideTimeAbsolute() / tosses;
    }

    public int getNumberOfTails() {
        return tosses - numberOfHeads;
    }

    public int getTailWinnerSideTimeAbsolute() {
        return tosses - headWinnerSideTime;
    }

    public double getTailWinnerSideTimeRelatve() {
        return (double) getTailWinnerSideTimeAbsolute() / tosses;
    }
}
