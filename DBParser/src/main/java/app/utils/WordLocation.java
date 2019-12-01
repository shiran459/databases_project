package app.utils;

public class WordLocation {

    public int wordOffset;
    public int paragraphNum;
    public int paragraphOffset;

    public WordLocation(int wordOffset, int paragraphNum, int paragraphOffset) {
        this.wordOffset = wordOffset;
        this.paragraphNum = paragraphNum;
        this.paragraphOffset = paragraphOffset;
    }

    @Override
    public String toString() {
        return wordOffset + " | [" + paragraphNum + ":" + paragraphOffset + "]";
    }
}
