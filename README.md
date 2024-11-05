# eCommerce App Backend

This is the backend for an eCommerce application built with the Ktor framework in Kotlin. It provides RESTful API endpoints to manage products (shoes), orders, reviews, carts, and wishlists, with secure JWT-based authentication for user-specific actions.

## Technologies Used

- **Ktor**: Framework for building backend applications.
- **Kotlin**: Primary language for backend development.
- **PostgreSQL**: Database for storing persistent data.
- **JWT Authentication**: Secure authentication for protected routes.
- **AWS S3**: Image storage for product images.
- **Docker**: Containerization for deployment.
- **Swagger/OpenAPI**: API documentation (assuming Swagger is integrated).

## Project Structure

- **Shoe Routes**: CRUD and query operations for products (shoes).
- **Order Routes**: Order creation and retrieval for users.
- **Review Routes**: Manage reviews for products.
- **Cart Routes**: Manage shopping cart items.
- **Wishlist Routes**: Manage wishlist items.

## Endpoints

---

### Authentication

**Note**: All endpoints require a JWT token in the `Authorization` header (e.g., `Authorization: Bearer <token>`), unless otherwise specified.

---

### Shoe Routes

#### 1. Add a New Shoe (Admin Only)
- **Endpoint**: `POST /shoes/add`
- **Description**: Adds a new product to the inventory.
- **Request Body**:
  ```json
  {
    "name": "Sneaker",
    "description": "High-quality sneaker",
    "price": 120.0,
    "brand": "BrandName",
    "size": 10,
    "color": "Red",
    "stock": 50,
    "imageUrl": "https://example.com/image.jpg"
  }
