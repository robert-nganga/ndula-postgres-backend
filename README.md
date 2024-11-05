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
- **Response**: Returns the created shoe object or an error message.

#### 2. Get All Shoes
- **Endpoint**: `GET /shoes/all`
- **Description**: Retrieves all shoes with optional pagination.
- **Query Parameters**:
  - `page` (optional): Page number.
  - `pageSize` (optional): Number of items per page.
- **Response**: List of shoes.

#### 3. Get Shoe by ID
- **Endpoint**: `GET /shoes/{shoeId}`
- **Description**: Retrieves a specific shoe by its ID.
- **Path Parameter**: `shoeId` - Shoe ID.
- **Response**: Shoe details or error message.

#### 4. Delete Shoe (Admin Only)
- **Endpoint**: `DELETE /shoes/{shoeId}`
- **Description**: Deletes a shoe by its ID.
- **Path Parameter**: `shoeId` - Shoe ID.
- **Response**: Success message or error message.

---

### Order Routes

#### 1. Add Order
- **Endpoint**: `POST /orders/add`
- **Description**: Creates a new order for the authenticated user.
- **Request Body**: The order details (items and total amount).
- **Response**: Returns the created order object or an error message.

#### 2. Get All Orders
- **Endpoint**: `GET /orders/all`
- **Description**: Retrieves all orders for the authenticated user.
- **Response**: List of orders.

#### 3. Get Active Orders
- **Endpoint**: `GET /orders/active`
- **Description**: Retrieves active orders for the authenticated user.
- **Response**: List of active orders.

#### 4. Get Completed Orders
- **Endpoint**: `GET /orders/completed`
- **Description**: Retrieves completed orders for the authenticated user.
- **Response**: List of completed orders.

#### 5. Get Order by ID
- **Endpoint**: `GET /orders/{id}`
- **Description**: Retrieves a specific order by its ID.
- **Path Parameter**: `id` - Order ID.
- **Response**: Order details or error message.

---

### Review Routes

#### 1. Add Review
- **Endpoint**: `POST /reviews/add`
- **Description**: Adds a new review for a product.
- **Request Body**: The review details (rating, comment, shoeId).
- **Response**: Returns the added review object or an error message.

#### 2. Get Reviews for Shoe
- **Endpoint**: `GET /reviews/shoe/{shoeId}`
- **Description**: Retrieves reviews for a specific shoe.
- **Path Parameter**: `shoeId` - Shoe ID.
- **Query Parameters**:
  - `page` (optional): Page number.
  - `pageSize` (optional): Number of items per page.
  - `rating` (optional): Filter reviews by rating.
- **Response**: List of reviews.

#### 3. Get Featured Reviews for Shoe
- **Endpoint**: `GET /reviews/shoe/featured/{shoeId}`
- **Description**: Retrieves featured reviews for a specific shoe.
- **Path Parameter**: `shoeId` - Shoe ID.
- **Response**: List of featured reviews.

#### 4. Delete Review
- **Endpoint**: `DELETE /reviews/{reviewId}`
- **Description**: Deletes a review by its ID.
- **Path Parameter**: `reviewId` - Review ID.
- **Response**: Success message or error message.

---

### Wishlist Routes

#### 1. Get My Wishlist
- **Endpoint**: `GET /wishlist/my_wishlist`
- **Description**: Retrieves the wishlist for the authenticated user.
- **Response**: List of items in the wishlist.

#### 2. Add Item to Wishlist
- **Endpoint**: `POST /wishlist/add/{shoeId}`
- **Description**: Adds an item to the authenticated user's wishlist.
- **Path Parameter**: `shoeId` - Shoe ID.
- **Response**: Success message or error message.

#### 3. Remove Item from Wishlist
- **Endpoint**: `DELETE /wishlist/remove/{shoeId}`
- **Description**: Removes an item from the authenticated user's wishlist.
- **Path Parameter**: `shoeId` - Shoe ID.
- **Response**: Success message or error message.

#### 4. Clear Wishlist
- **Endpoint**: `DELETE /wishlist/clear`
- **Description**: Clears all items from the authenticated user's wishlist.
- **Response**: Success message or error message.

---

### Cart Routes

#### 1. Get My Cart
- **Endpoint**: `GET /cart/my_cart`
- **Description**: Retrieves the cart for the authenticated user.
- **Response**: List of items in the cart.

#### 2. Add Item to Cart
- **Endpoint**: `POST /cart/add/{shoeId}`
- **Description**: Adds an item to the authenticated user's cart.
- **Path Parameter**: `shoeId` - Shoe ID.
- **Response**: Success message or error message.

#### 3. Remove Item from Cart
- **Endpoint**: `DELETE /cart/remove/{shoeId}`
- **Description**: Removes an item from the authenticated user's cart.
- **Path Parameter**: `shoeId` - Shoe ID.
- **Response**: Success message or error message.

#### 4. Clear Cart
- **Endpoint**: `DELETE /cart/clear`
- **Description**: Clears all items from the authenticated user's cart.
- **Response**: Success message or error message.

