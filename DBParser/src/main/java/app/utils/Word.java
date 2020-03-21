package app.utils;

import app.lib.wordStats.WordStats;

public class Word {

    public String value;
    public int id;
    public WordStats stats;

    public Word() {
        this.id = -1;
    }

    public Word(String value) {
        this.value = value;
    }

    public Word(String value, int id) {
        this.value = value;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Word)obj).id;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
