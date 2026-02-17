# Quick Setup Guide

## Step 1: Install Dependencies

```bash
cd finance-tracker
npm install
```

## Step 2: Configure Backend Proxy

If your Spring Boot backend runs on a different port than 8080, update `package.json`:

```json
"proxy": "http://localhost:YOUR_PORT"
```

## Step 3: Start Development Server

```bash
npm start
```

The app will automatically open at `http://localhost:3000`

## Step 4: Integrate with Your Backend

### Required API Endpoints

Your Spring Boot application should expose these REST endpoints:

#### Transactions
- **GET** `/api/transactions` - Returns array of transaction objects
- **PATCH** `/api/transactions/{id}` - Updates a transaction (expects JSON body)

#### Other Endpoints
- **GET** `/api/categories` - Returns array of category objects
- **GET** `/api/accounts` - Returns array of account objects
- **GET** `/api/institutions` - Returns array of institution objects
- **GET** `/api/vendors` - Returns array of vendor objects
- **GET** `/api/unknown-transactions` - Returns array of uncategorized transactions

### Enable CORS (if needed)

If your frontend and backend are on different domains, add CORS configuration to your Spring Boot app:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*");
    }
}
```

## Testing Without Backend

The app includes mock data for all pages, so you can test the UI immediately even if your backend isn't ready. When API calls fail, it automatically falls back to demo data.

## Building for Production

```bash
npm run build
```

This creates a production-ready build in the `build/` folder that you can serve with any static file server or integrate into your Spring Boot application's static resources.

## Troubleshooting

### "Failed to fetch" errors
- Check that your Spring Boot backend is running
- Verify the proxy setting in package.json
- Check CORS configuration if using different domains

### Blank page
- Open browser console (F12) to check for errors
- Ensure all dependencies installed correctly (`npm install`)
- Try clearing node_modules and reinstalling: `rm -rf node_modules && npm install`

### Styles not loading
- Hard refresh the browser (Ctrl+Shift+R or Cmd+Shift+R)
- Clear browser cache
- Check that CSS files are properly imported in components