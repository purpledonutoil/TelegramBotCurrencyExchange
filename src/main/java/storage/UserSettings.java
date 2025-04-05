package storage;
import banking.Bank;
import banking.Currency;

import java.util.EnumSet;
import java.util.Objects;

public class UserSettings {
    public static final Bank DEFAULT_BANK = Bank.PRIVAT;
    public static final Currency DEFAULT_CURRENCY = Currency.USD;
    public static final int DEFAULT_NOTICE_TIME = -1;
    public static final int DEFAULT_DECIMAL_PLACE = 2;

    private EnumSet<Bank> banks;
    private EnumSet<Currency> currencies;
    private int  notificationTime;
    private int decimalPlace;

    public UserSettings() {
        this.banks = EnumSet.of(DEFAULT_BANK);
        this.currencies = EnumSet.of(DEFAULT_CURRENCY);
        this. notificationTime = DEFAULT_NOTICE_TIME;
        this.decimalPlace = DEFAULT_DECIMAL_PLACE;
    }

    public EnumSet<Bank> getBanks() {
        return EnumSet.copyOf(banks);
    }

    public EnumSet<Currency> getCurrencies() {
        return EnumSet.copyOf(currencies);
    }

    public int getNotificationTime() {
        return  notificationTime;
    }

    public int getRoundNumber() {
        return decimalPlace;
    }


    public int setNotificationTime(int  notificationTime) {
        this. notificationTime =  notificationTime;
        return this. notificationTime;
    }

    public int setRoundNumber(int roundNumber) {
        this.decimalPlace = roundNumber;
        return this.decimalPlace;
    }

    public void addCurrency(Currency currency) {
        currencies.add(Objects.requireNonNull(currency));
    }

    public Currency removeCurrency(Currency currency) {
        currencies.remove(currency);
        if (currencies.isEmpty()) {
            currencies.add(DEFAULT_CURRENCY);
            return DEFAULT_CURRENCY;
        }
        return null;
    }

    public void addBank(Bank bank) {
        banks.add(Objects.requireNonNull(bank));
    }

    public Bank removeBank(Bank bank) {
        banks.remove(bank);
        if (banks.isEmpty()) {
            banks.add(DEFAULT_BANK);
            return DEFAULT_BANK;
        }
        return null ;
    }
}
