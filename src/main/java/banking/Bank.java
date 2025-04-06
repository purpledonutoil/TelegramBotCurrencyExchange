package banking;

public enum Bank {
    PRIVAT("ПриватБанк"),
    MONO("Монобанк"),
    NBU("НБУ");

    private final String title;

    Bank(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
