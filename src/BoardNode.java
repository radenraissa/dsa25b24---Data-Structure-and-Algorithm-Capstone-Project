//BoardNode.java
class BoardNode {
    private int value;
    private BoardNode next;
    private BoardNode prev;

    public BoardNode(int value) {
        this.value = value;
        this.next = null;
        this.prev = null;
    }

    public void setNext(BoardNode next) { this.next = next; }
    public void setPrev(BoardNode prev) { this.prev = prev; }
}

//Ladder.java
class Ladder {
    private int bottom;
    private int top;

    public Ladder(int bottom, int top) {
        this.bottom = bottom;
        this.top = top;
    }

    public int getBottom() { return bottom; }
    public int getTop() { return top; }
}