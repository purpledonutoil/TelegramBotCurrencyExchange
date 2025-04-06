package storage;

import banking.Bank;
import banking.Currency;

import java.util.EnumSet;

public class UserSettings {
    public static final Bank DEFAULT_BANK = Bank.PRIVAT;
    public static final Currency DEFAULT_CURRENCY = Currency.USD;
    public static final int DEFAULT_NOTIFICATION_TIME = -1;
    public static final int DEFAULT_DECIMAL_PLACE = 2;

    private EnumSet<Bank> banks;
    private EnumSet<Currency> currencies;
    private int notificationTime;
    private int decimalPlace;

    public UserSettings() {
        this.banks = EnumSet.of(DEFAULT_BANK);
        this.currencies = EnumSet.of(DEFAULT_CURRENCY);
        this.notificationTime = DEFAULT_NOTIFICATION_TIME;
        this.decimalPlace = DEFAULT_DECIMAL_PLACE;
    }
}