# Database Design & API Specification

## Database Tables

### 1. Users Table

```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. Categories Table

```sql
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. Products Table

```sql
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category_id INT,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

### 4. Cart Table

```sql
CREATE TABLE cart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    product_id INT,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE KEY unique_user_product (user_id, product_id)
);
```

### 5. Orders Table

```sql
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'confirmed', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending',
    shipping_address TEXT,
    payment_method VARCHAR(50),
    payment_status ENUM('pending', 'paid', 'failed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 6. Order Items Table

```sql
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### 7. Chat Messages Table

```sql
CREATE TABLE chat_messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    message TEXT NOT NULL,
    sender_type ENUM('customer', 'store') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 8. Store Information Table

```sql
CREATE TABLE store_info (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    address TEXT NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    phone VARCHAR(20),
    email VARCHAR(100),
    opening_hours TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## API Endpoints

### Authentication APIs

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/profile` - Get user profile
- `PUT /api/auth/profile` - Update user profile

### Product APIs

- `GET /api/products` - Get all products (with pagination)
- `GET /api/products/{id}` - Get product details
- `GET /api/categories` - Get all categories
- `GET /api/products/category/{categoryId}` - Get products by category

### Cart APIs

- `GET /api/cart` - Get user's cart items
- `POST /api/cart` - Add item to cart
- `PUT /api/cart/{itemId}` - Update cart item quantity
- `DELETE /api/cart/{itemId}` - Remove item from cart
- `DELETE /api/cart` - Clear cart

### Order APIs

- `POST /api/orders` - Create new order
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/{id}/status` - Update order status

### Chat APIs

- `GET /api/chat/messages` - Get chat messages
- `POST /api/chat/messages` - Send message
- `PUT /api/chat/messages/{id}/read` - Mark message as read

### Store APIs

- `GET /api/store/info` - Get store information
- `GET /api/store/location` - Get store location

## Response Format

```json
{
  "success": true,
  "message": "Success message",
  "data": {},
  "pagination": {
    "page": 1,
    "limit": 10,
    "total": 100,
    "totalPages": 10
  }
}
```

## Error Response Format

```json
{
  "success": false,
  "message": "Error message",
  "errors": [
    {
      "field": "email",
      "message": "Email is required"
    }
  ]
}
```
