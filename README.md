# Event Management System

A modern, professional JavaFX application for managing users, clients, events, and reservations, featuring a stylish UI, real-time statistics, and interactive dashboards.

## Features

- **User Authentication**
  - Modern login and registration screens
  - Email uniqueness validation
  - Secure password handling
  - Google People API integration for social login
  - SMS verification for enhanced security

- **User & Client Management**
  - Add, edit, and delete clients
  - View client details and search functionality
  - Profile management with Google People API
  - Contact information synchronization

- **Event Management**
  - List, create, edit, and delete events
  - Modern event cards with details, location, and available places
  - Reserve or cancel event participation
  - Real-time update of available places
  - Google Calendar integration for event scheduling

- **Reservation System**
  - Reserve events as a logged-in client
  - Cancel reservations
  - Automatic email confirmation and PDF ticket generation
  - SMS notifications for reservation updates
  - WhatsApp integration for instant notifications

- **Dashboard & Statistics**
  - Modern dashboard with charts:
    - Pie chart of user types
    - Bar chart of event reservation status
    - Bar chart of reservation status
  - Real-time statistics on users, clients, events, and reservations
  - Export functionality for reports

- **Navigation & Sidebar**
  - Responsive sidebar and navigation bar with integrated logo
  - Smooth resizing and fullscreen support
  - Dynamic menu based on user role

- **Event Extras**
  - View event location on Google Maps
  - Check weather forecast for event dates (OpenWeatherMap API integration)
  - Calendar view for events
  - Social media sharing integration

## Technologies & APIs Used

### Core Technologies
- **Java 11+**
- **JavaFX** (UI framework)
- **JDBC** (Database connectivity)
- **MySQL** (Database)
- **Maven** (Dependency management)

### External APIs & Services

#### Google Services
- **Google Maps API**
  - Location visualization
  - Geocoding for address conversion
  - Distance calculation
  - Route planning for events

- **Google People API**
  - Social login integration
  - Contact information synchronization
  - Profile management
  - Calendar integration

- **Google Calendar API**
  - Event scheduling
  - Calendar synchronization
  - Reminder management
  - Recurring events support

#### Weather Services
- **OpenWeatherMap API**
  - Real-time weather data
  - 5-day forecast
  - Weather alerts
  - Historical weather data

#### Communication Services
- **Email Service**
  - SMTP integration
  - HTML email templates
  - Bulk email support
  - Email tracking
  - Automated notifications

- **SMS Service**
  - Twilio integration
  - Bulk SMS support
  - SMS templates
  - Delivery status tracking
  - Two-factor authentication

- **WhatsApp Business API**
  - Instant notifications
  - Message templates
  - Media sharing
  - Group messaging support

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- MySQL database
- API Keys:
  - Google Maps API key
  - Google People API credentials
  - OpenWeatherMap API key
  - Twilio credentials
  - WhatsApp Business API access

### Setup

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd <project-directory>
   ```

2. **Configure the Database:**
   - Create a MySQL database and import the provided schema
   - Update your database connection settings in `com.esprit.utils.DataSource`

3. **Configure API Keys:**
   - Add your API keys to the configuration file:
     ```properties
     # Google APIs
     google.maps.api.key=your_maps_api_key
     google.people.client.id=your_client_id
     google.people.client.secret=your_client_secret
     
     # Weather API
     openweathermap.api.key=your_weather_api_key
     
     # Communication APIs
     twilio.account.sid=your_twilio_sid
     twilio.auth.token=your_twilio_token
     whatsapp.business.token=your_whatsapp_token
     ```

4. **Add Resources:**
   - Place your logo image in `/src/main/resources/images/logo.png`
   - (Optional) Add a background image for login/registration in `/src/main/resources/images/login-bg.jpg`

5. **Build and Run:**
   - Using your IDE: Import as a Maven/JavaFX project and run `Main.java`
   - Using Maven CLI:
     ```bash
     mvn clean javafx:run
     ```

## Project Structure

```
src/
  main/
    java/
      com/esprit/
        controllers/    # JavaFX controllers
        models/         # Data models (User, Client, Event, Reservation)
        services/       # Business logic and database access
        utils/          # Utility classes (DB connection, etc.)
        api/           # API integration classes
          google/      # Google services integration
          weather/     # Weather API integration
          sms/         # SMS service integration
          email/       # Email service integration
          whatsapp/    # WhatsApp integration
    resources/
      fxml/             # FXML UI layouts
      images/           # Image assets (logo, backgrounds)
      templates/        # Email and SMS templates
      config/          # Configuration files
```

## Key Features Implementation

### User Authentication
- Modern login and registration screens with gradient backgrounds
- Email validation and password security
- Session management for logged-in users
- Google People API integration for social login
- SMS verification for enhanced security

### Event Management
- Interactive event cards with real-time availability
- Weather integration for event locations
- Google Maps integration for venue viewing
- Calendar view for event scheduling
- Social media sharing capabilities

### Reservation System
- Real-time reservation status updates
- Email notifications for reservations
- SMS notifications for important updates
- WhatsApp integration for instant communication
- PDF ticket generation
- Automatic place availability management

### Dashboard
- Real-time statistics visualization
- Interactive charts for data analysis
- User-friendly interface for data presentation
- Export functionality for reports
- Customizable widgets

## Customization

- **UI Styling:** All FXML files use modern gradients, rounded corners, and professional button styles
- **Email & SMS Templates:** Customize templates in `/resources/templates/`
- **API Configuration:** Update API settings in the configuration files
- **Weather API:** Replace the OpenWeatherMap API key in `EvenementsFrontController.java`
- **Google Services:** Configure OAuth 2.0 credentials for Google services
- **Communication Services:** Set up your Twilio and WhatsApp Business accounts

## Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

## License

[MIT](LICENSE)

---

**Note:** This project is continuously being improved. Feel free to contribute or report any issues you encounter. 