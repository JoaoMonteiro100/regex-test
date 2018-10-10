import java.util.regex.Pattern;

class Phrase {
    private Pattern p;
    //these variables refer to the indexes where this information can be found in the phrase
    private int number = -1; //if -1 but timeUnit != -1, then it's ONCE every timeUnit
    private int timeUnit = -1; //if timeUnit != -1 AND event != -1, then time refers to event (before/after/during)
    private boolean numberPerTime = false; //IF TRUE: "take it 2 times per hour" ---- IF FALSE: "take every 2 hours"
    private int event = -1;
    //when everything is -1 (boolean is irrelevant), it's an intangible expression (such as "while fasting" or "until the next appointment"), so it should be highlighted but we won't understand what it means

    public Phrase(Pattern p, int number, int time, boolean numberPerTime, int event) {
        this.p = p;
        this.number = number;
        this.timeUnit = time;
        this.numberPerTime = numberPerTime;
        this.event = event;
    }

    public Pattern getP() {
        return p;
    }

    public void setP(Pattern p) {
        this.p = p;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }

    public boolean getNumberPerTime() {
        return numberPerTime;
    }

    public void setNumberPerTime(boolean numberPerTime) {
        this.numberPerTime = numberPerTime;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}
