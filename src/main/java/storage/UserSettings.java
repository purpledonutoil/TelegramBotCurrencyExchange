package storage;
import banking.Bank;
import banking.Currency;

import java.util.EnumSet;
import java.util.Objects;

public class UserSettings {
    public static final Bank DEFAULT_BANK = Bank.PRIVAT;
    public static final Currency DEFAULT_CURRENCY = Currency.USD;
    public static final int DEFAULT_NOTICE_TIME = -1;
    public static final int DEFAULT_ROUND_NUMBER = 2;

    private EnumSet<Bank> banks;
    private EnumSet<Currency> currencies;
    private int noticeTime;
    private int roundNumber;

    public UserSettings() {
        this.banks = EnumSet.of(DEFAULT_BANK);
        this.currencies = EnumSet.of(DEFAULT_CURRENCY);
        this.noticeTime = DEFAULT_NOTICE_TIME;
        this.roundNumber = DEFAULT_ROUND_NUMBER;
    }

    public EnumSet<Bank> getBanks() {
        return EnumSet.copyOf(banks);
    }

    public EnumSet<Currency> getCurrencies() {
        return EnumSet.copyOf(currencies);
    }

    public int getNoticeTime() {
        return noticeTime;
    }

    public int getRoundNumber() {
        return roundNumber;
    }


    public int setNoticeTime(int noticeTime) {
        this.noticeTime = noticeTime;
        return this.noticeTime;
    }

    public int setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
        return this.roundNumber;
    }

    public void addCurrency(Currency currency) {
        currencies.add(Objects.requireNonNull(currency));
    }

    public void removeCurrency(Currency currency) {
        currencies.remove(currency);
        if (currencies.isEmpty()) {
            currencies.add(DEFAULT_CURRENCY);
        }
    }

    public void addBank(Bank bank) {
        banks.add(Objects.requireNonNull(bank));
    }

    public void removeBank(Bank bank) {
        banks.remove(bank);
        if (banks.isEmpty()) {
            banks.add(DEFAULT_BANK);
        }
    }
}
