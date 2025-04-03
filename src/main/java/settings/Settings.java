package settings;

import com.google.gson.annotations.SerializedName;

public class Settings {
    @SerializedName("bank")
    private String bank;

    @SerializedName("currency1")
    private String currency1;

    @SerializedName("currency2")
    private String currency2;

    @SerializedName("decimalPlaces")
    private int decimalPlaces;

    @SerializedName("notificationTime")
    private int notificationTime;



    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }

    public String getCurrency1() { return currency1; }
    public void setCurrency1(String currency1) { this.currency1 = currency1; }

    public String getCurrency2() { return currency2; }
    public void setCurrency2(String currency2) { this.currency2 = currency2; }

    public int getDecimalPlaces() { return decimalPlaces; }
    public void setDecimalPlaces(int decimalPlaces) { this.decimalPlaces = decimalPlaces; }

    public int getNotificationTime() { return notificationTime; }
    public void setNotificationTime(int notificationTime) { this.notificationTime = notificationTime; }

    @Override
    public String toString() {
        return "Settings{" +
                "bank='" + bank + '\'' +
                ", currency1='" + currency1 + '\'' +
                ", currency2='" + currency2 + '\'' +
                ", decimalPlaces=" + decimalPlaces +
                ", notificationTime=" + notificationTime +
                '}';
    }
}
