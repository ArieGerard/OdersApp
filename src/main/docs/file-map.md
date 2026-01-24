# File Map: Backend Routes to Thymeleaf Templates

This document serves as the contract between backend developers and frontend developers. All routes and their corresponding templates are documented here. **If a route is not in this file, it should not be implemented.**

## Authentication Routes

| HTTP Method | Route | Controller Method | Template | Model Attributes | Access |
|------------|-------|-------------------|----------|-------------------|--------|
| GET | `/` | `home()` | Redirects to `/login` | - | Public |
| GET | `/login` | `showLogin()` | `login.html` | `title`, `errorMessage` (optional), `logoutMessage` (optional), `successMessage` (optional) | Public |
| POST | `/processLogin` | Spring Security | N/A (handled by Spring Security) | - | Public |
| POST | `/logout` | Spring Security | N/A (redirects to `/login?logout=true` | - | Authenticated |
| GET | `/register` | `showRegistration()` | `register.html` | `title`, `registration` (RegistrationModel) | Public |
| POST | `/register` | `processRegistration()` | `register.html` (on error) or redirect to `/login?registered=true` | `title`, `registration` (RegistrationModel), validation errors | Public |

## Orders Management Routes

| HTTP Method | Route | Controller Method | Template | Model Attributes | Access |
|------------|-------|-------------------|----------|-------------------|--------|
| GET | `/orders` | `showAllOrders()` | `allOrders.html` | `title`, `orders` (Iterable<OrderModel>) | Authenticated |
| GET | `/orders/showOrder/{id}` | `showOneOrder()` | `oneOrder.html` | `title`, `order` (OrderModel) | Authenticated |
| GET | `/orders/newOrder` | `newOrder()` | `newOrder.html` | `title`, `order` (OrderModel - empty) | Authenticated |
| POST | `/orders/processNewOrder` | `processNewOrder()` | `newOrder.html` (on validation error) or redirect to `/orders` | `title`, `order` (OrderModel), validation errors | Authenticated |
| GET | `/orders/editOrder/{id}` | `editOrder()` | `editOrder.html` | `title`, `order` (OrderModel) | Authenticated |
| POST | `/orders/processEditOrder` | `doUpdate()` | Redirects to `/orders` | `order` (OrderModel) | Authenticated |
| GET | `/orders/deleteOrder/{id}` | `deleteOrder()` | Redirects to `/orders` (immediate deletion) | - | Authenticated |

## Admin User Management Routes

| HTTP Method | Route | Controller Method | Template | Model Attributes | Access |
|------------|-------|-------------------|----------|-------------------|--------|
| GET | `/admin/users` | `listUsers()` | `admin/users.html` | `title`, `users` (Iterable<UserModel>) | Admin Only |
| GET | `/admin/users/edit/{id}` | `editUser()` | `admin/editUser.html` | `title`, `user` (UserModel) | Admin Only |
| POST | `/admin/users/edit` | `processEdit()` | Redirects to `/admin/users` | `user` (UserModel) | Admin Only |
| GET | `/admin/users/delete/{id}` | `confirmDelete()` | `admin/deleteUser.html` | `title`, `user` (UserModel) | Admin Only |
| POST | `/admin/users/delete` | `processDelete()` | Redirects to `/admin/users` | `id` (RequestParam) | Admin Only |

## Template Files Reference

### Existing Templates (in `/src/main/resources/templates/`)
- `allOrders.html` - Lists all orders in a table
- `editOrder.html` - Form to edit an existing order
- `newOrder.html` - Form to create a new order
- `oneOrder.html` - Displays details of a single order

### Required Templates (not yet created)
- `login.html` - Login form page
- `register.html` - User registration form page
- `admin/users.html` - Admin user management list page
- `admin/editUser.html` - Admin form to edit user details
- `admin/deleteUser.html` - Admin confirmation page for user deletion

## Model Objects

### OrderModel
- `id` (int)
- `order_number` (String)
- `product_name` (String)
- `price` (double)
- `quantity` (int)

### UserModel
- `id` (int)
- `username` (String)
- `password` (String) - hashed when stored
- `role` (String) - `ROLE_USER` or `ROLE_ADMIN`
- `enabled` (boolean)

### RegistrationModel
- `username` (String) - 3-50 characters, required
- `password` (String) - 6-50 characters, required
- `confirmPassword` (String) - required, must match password

## Security Configuration

### Public Routes (No Authentication Required)
- `/`
- `/login`
- `/register`
- `/css/**`
- `/js/**`
- `/images/**`

### Authenticated Routes (Any logged-in user)
- `/orders/**`

### Admin-Only Routes (ROLE_ADMIN required)
- `/admin/**`

## Redirects

| From | To | Condition |
|------|-----|-----------|
| `/` | `/login` | Always |
| `/register` (POST success) | `/login?registered=true` | Registration successful |
| `/login` (POST success) | `/orders` | Login successful |
| `/login` (POST failure) | `/login?error=true` | Login failed |
| `/logout` | `/login?logout=true` | After logout |
| `/orders/processNewOrder` (POST success) | `/orders` | Order created successfully |
| `/orders/processNewOrder` (POST error) | `/orders/newOrder` | Validation errors |
| `/orders/processEditOrder` (POST) | `/orders` | Order updated |
| `/orders/deleteOrder/{id}` (GET) | `/orders` | Order deleted |
| `/admin/users/edit` (POST) | `/admin/users` | User updated |
| `/admin/users/delete` (POST) | `/admin/users` | User deleted |

## Notes

- All POST routes that process forms should redirect on success to avoid duplicate submissions
- Validation errors should return the user to the form with error messages displayed
- Spring Security handles `/processLogin` and `/logout` endpoints automatically
- The `title` attribute is consistently used across all templates for page headings
- Admin routes require the user to have `ROLE_ADMIN` role (checked by Spring Security)