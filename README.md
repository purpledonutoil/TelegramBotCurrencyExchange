# TelegramBotCurrencyExchange

A Telegram bot written in Java that provides real-time currency exchange rates. Users can retrieve current buy and sell rates for USD and EUR from selected banks, configure notification settings, and customize data precision.

## Features

- **Real-time Exchange Rates**: Fetches current buy and sell rates for USD and EUR **relative to the Ukrainian Hryvnia (UAH)**.
- **Bank Selection**: Users can choose one or multiple data sources from the most popular banks in Ukraine (e.g., PrivatBank, Monobank).
- **Precision Configuration**: Adjust the number of decimal places displayed in rates.
- **Scheduled Notifications**: Set a daily time to receive automatic exchange rate updates.
- **Interactive Telegram UI**: All interactions are done through dynamically generated buttons in the chat — no need to remember or type commands (except for `/start`).

## Technologies Used

- **Java 17**
- **Gradle** – build automation
- **TelegramBots Java Library** – third-party library for working with the Telegram Bot API
- **External APIs** – for fetching currency exchange rates from Ukrainian banks

## Getting Started

### Prerequisites

- **Java 17** or higher
- **Gradle** installed on your system
- A Telegram bot name and token obtained from [BotFather](https://core.telegram.org/bots#botfather)

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/purpledonutoil/TelegramBotCurrencyExchange.git
   cd TelegramBotCurrencyExchange

2. **Configure the Bot**:
   - Create a `config.properties` file in the root of the project directory.
   - Add your Telegram bot token to this file:
   ```ini
   bot.name=YOUR_TELEGRAM_BOT_NAME
   bot.token=YOUR_TELEGRAM_BOT_TOKEN
   
3. **Build and Run the Project**:
     ```bash
     ./gradlew build
     java -jar build/libs/TelegramBotCurrencyExchange.jar

## Usage

To start using the bot, send the `/start` command in the chat. After that, all interactions are handled through **interactive buttons** — no need to type any further commands manually.

The bot provides a clear and intuitive interface with buttons to configure preferences and receive exchange rates.

### Main Menu

After sending `/start`, you’ll see a menu like this:

<img src="/screenshots/main-menu.jpg" alt="Main Menu" width="400"/>

### Get Info (Отримати інфо)

You can request the current exchange rates at any time via the **"Get Info"** button.

The response includes buy/sell rates for the selected currencies, based on your settings:

<img src="/screenshots/info-custom.jpg" alt="Info Custom" width="200"/>

If you haven't changed any settings, the bot will use its default configuration:

<img src="/screenshots/info-default.jpg" alt="Info Default" width="200"/>

### Settings (Налаштування)

<img src="/screenshots/settings.jpg" alt="Settings" width="220"/>

In the **Settings** section, you can:

#### Adjust the precision of exchange rates
<img src="/screenshots/precision-settings.jpg" alt="Precision Settings" width="270"/>

#### Select one or multiple banks as data sources
<img src="/screenshots/bank-settings.jpg" alt="Bank Settings" width="270"/>

#### Choose the currency (USD, EUR, or both)
<img src="/screenshots/currencies-settings.jpg" alt="Currencies Settings" width="270"/>

#### Enable or disable daily notifications, and choose the exact hour to receive rates
<img src="/screenshots/notification-settings.jpg" alt="Notification Settings" width="750"/>


## License
This project is provided for educational purposes and does not have a specific license. Feel free to use and modify it as needed.