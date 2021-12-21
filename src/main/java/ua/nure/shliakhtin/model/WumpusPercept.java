package ua.nure.shliakhtin.model;

import lombok.Getter;

@Getter
public class WumpusPercept {

    private boolean stench;
    private boolean breeze;
    private boolean glitter;
    private boolean scream;

    public WumpusPercept setStench() {
        stench = true;

        return this;
    }

    public WumpusPercept setBreeze() {
        breeze = true;

        return this;
    }

    public WumpusPercept setGlitter() {
        glitter = true;

        return this;
    }


    public WumpusPercept setScream() {
        scream = true;

        return this;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (stench)
            result.append("There is Stench. ");
        if (breeze)
            result.append("There is Breeze. ");
        if (glitter)
            result.append("There is Glitter. ");
        if (scream)
            result.append("There is Scream. ");
        return result.toString();
    }
}
