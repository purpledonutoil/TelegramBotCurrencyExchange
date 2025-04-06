package storage;

import banking.Bank;
import banking.Currency;

import java.util.EnumSet;

public class UserSettings {
    public static final Bank DEFAULT_BANK = Bank.PRIVAT;
    public static final Currency DEFAULT_CURRENCY = Currency.USD;
    public static final int DEFAULT_NOTICE_TIME = -1;
    public static final int DEFAULT_ROUND_NUMBER = 2;

    private EnumSet<Bank> banks;
    private EnumSet<Currency> currencies;
    private int noticeTime = DEFAULT_NOTICE_TIME;
    private int roundNumber = DEFAULT_ROUND_NUMBER;

    public int getNoticeTime() {
        return noticeTime;
    }

    public void setNotificationTime(int hour) {
        this.noticeTime = hour;
    }
}
