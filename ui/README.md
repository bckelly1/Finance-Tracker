# Finance Tracker - React Frontend

A modern, responsive React application for tracking personal finances with integration to a Java Spring backend.

## Features

- **6 Distinct Views**: Transactions, Unknown Transactions, Accounts, Categories, Vendors, and Institutions
- **Responsive Design**: Scales dynamically across all screen sizes
- **Interactive Tables**: Sortable columns, search, and filtering
- **Category Management**: Edit transaction categories via dropdown
- **Real-time Updates**: Connects to Spring Boot API endpoints
- **Professional UI**: Refined design with sophisticated typography and color scheme

## Tech Stack

- React 18.2
- React Router 6.20 for navigation
- Modern CSS with CSS Variables
- Google Fonts (Playfair Display + DM Sans)

## Getting Started

### Prerequisites

- Node.js 16+ and npm
- Java Spring Boot backend running on `http://localhost:8080`

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm start
```

The application will open at `http://localhost:3000`

### API Integration

The app expects your Spring Boot backend to expose these endpoints:

- `GET /api/transactions` - List all transactions
- `PATCH /api/transactions/{id}` - Update a transaction
- `GET /api/categories` - List all categories
- `GET /api/accounts` - List all accounts
- `GET /api/institutions` - List all institutions
- `GET /api/vendors` - List all vendors
- `GET /api/unknown-transactions` - List uncategorized transactions

### Transaction API Response Format

Expected JSON format from `/api/transactions`:

```json
[
  {
    "id": 1,
    "date": "2024-02-14",
    "description": "Whole Foods Market",
    "amount": -87.54,
    "type": "debit",
    "category": "Groceries",
    "vendor": "Whole Foods",
    "mailId": "MAIL-001",
    "notes": "Weekly grocery shopping"
  }
]
```

## Project Structure

```
src/
├── App.jsx                    # Main app component with routing
├── App.css                    # Global styles and design system
├── index.js                   # Entry point
├── index.css                  # Base CSS reset
└── pages/
    ├── Transactions.jsx       # Main transactions view (fully featured)
    ├── Transactions.css       # Transactions-specific styles
    ├── Accounts.jsx          # Accounts view
    ├── Categories.jsx        # Categories view
    ├── Institutions.jsx      # Institutions view
    ├── UnknownTransactions.jsx # Unknown transactions view
    └── Vendors.jsx           # Vendors view
```

## Key Features

### Transactions Page

- **Search**: Filter by description, vendor, or notes
- **Category Filter**: Filter by specific category
- **Sorting**: Click column headers to sort (date, amount, description, etc.)
- **Editable Categories**: Change categories via dropdown (updates via API)
- **Summary Cards**: View total transactions, income, and expenses
- **Responsive Table**: Horizontal scroll on mobile devices

### Design System

- **Colors**: Warm neutrals with teal accent (#0f766e)
- **Typography**: Playfair Display (headings) + DM Sans (body)
- **Shadows**: Subtle elevation with layered shadows
- **Animations**: Smooth transitions and fade-in effects
- **Responsive**: Mobile-first with breakpoints at 768px and 1024px

## Development

### Building for Production

```bash
npm run build
```

This creates an optimized production build in the `build/` folder.

### Connecting to Your Backend

The `package.json` includes a proxy configuration:

```json
"proxy": "http://localhost:8080"
```

This proxies API requests to your Spring Boot backend during development. Update this if your backend runs on a different port.

### Demo Mode

If the API is unavailable, the app falls back to mock data so you can see the UI in action. An error banner will indicate when using demo data.

## Customization

### Changing Colors

Edit CSS variables in `src/App.css`:

```css
:root {
  --color-accent: #0f766e;        /* Primary accent color */
  --color-bg: #fafaf9;            /* Background color */
  --color-surface: #ffffff;       /* Card/surface color */
  /* ... more variables */
}
```

### Adding Authentication

Currently, API requests don't include authentication. To add auth:

1. Update the `fetch` calls in each page component
2. Add an `Authorization` header with your token
3. Consider using a context provider for auth state

Example:
```javascript
const response = await fetch('/api/transactions', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

## Browser Support

- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers (iOS Safari, Chrome Mobile)

## License

This project is part of a finance tracking application.