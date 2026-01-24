# User Feature Design Document

## Feature Summary

The Users feature provides authentication, registration, and user management capabilities for the Orders Management Application. It consists of three main components:

### 1. User Registration
- **Purpose**: Allows new users to create accounts
- **Access**: Public (no authentication required)
- **Location**: `/register`
- **Features**:
  - Username input (3-50 characters, required)
  - Password input (6-50 characters, required)
  - Password confirmation (must match password)
  - Validation for username uniqueness
  - Automatic role assignment: `ROLE_USER` (default)
  - Automatic account enablement: `true` (default)
  - Password hashing using BCrypt (strength 12)
  - Success redirect to login page with confirmation message

### 2. Login/Logout
- **Purpose**: Authenticate users and manage sessions
- **Access**: Public (no authentication required)
- **Location**: `/login`
- **Features**:
  - Username and password authentication
  - Session-based authentication
  - Success redirect to `/orders` (main application)
  - Error handling with user-friendly messages
  - Logout functionality at `/logout`
  - Session invalidation and cookie deletion on logout
  - Support for error, logout, and registration success messages via query parameters

### 3. Admin User Management
- **Purpose**: Allow administrators to manage all user accounts
- **Access**: Admin only (`ROLE_ADMIN` required)
- **Location**: `/admin/users`
- **Features**:
  - List all users in the system
  - Edit user details (username, password, role, enabled status)
  - Delete users with confirmation
  - Password re-encoding if changed (detects if already hashed)

## Roles

### Regular User (`ROLE_USER`)
- **Default role** assigned to all new registrations
- **Permissions**:
  - Access to orders management (`/orders/**`)
  - Cannot access admin features (`/admin/**`)
- **User Flow**: Register → Login → Use Orders Management

### Admin User (`ROLE_ADMIN`)
- **Manually assigned** role (typically set in database or by another admin)
- **Permissions**:
  - All Regular User permissions
  - Access to user management (`/admin/**`)
  - Can view, edit, and delete any user account
- **User Flow**: Login (as admin) → Access User Management → View/Edit/Delete Users

## High-Level User Flows

### New User Registration Flow
1. User navigates to `/register` (or clicks "Register" link from login page)
2. User fills out registration form:
   - Username (3-50 characters)
   - Password (6-50 characters)
   - Confirm Password (must match)
3. System validates:
   - Field requirements met
   - Passwords match
   - Username is unique
4. If validation fails: Display errors on registration form
5. If validation succeeds:
   - Hash password with BCrypt
   - Create user with `ROLE_USER` and `enabled=true`
   - Redirect to `/login?registered=true`
   - Display success message on login page

### User Login Flow
1. User navigates to `/login` (or root `/` redirects here)
2. User enters username and password
3. Form submits to `/processLogin` (Spring Security endpoint)
4. System authenticates:
   - Validates credentials against database
   - Checks if account is enabled
5. If authentication fails: Redirect to `/login?error=true` with error message
6. If authentication succeeds:
   - Create session
   - Redirect to `/orders` (default success URL)
   - User can now access protected resources

### User Logout Flow
1. User clicks logout link/button (typically in navigation)
2. Request sent to `/logout` (Spring Security endpoint)
3. System:
   - Invalidates HTTP session
   - Deletes JSESSIONID cookie
   - Redirects to `/login?logout=true`
4. User sees logout confirmation message

### Admin User Management Flow
1. Admin user logs in with `ROLE_ADMIN` credentials
2. Admin navigates to `/admin/users`
3. System displays list of all users with:
   - User ID
   - Username
   - Role
   - Enabled status
   - Action buttons (Edit, Delete)
4. **Edit User Flow**:
   - Admin clicks "Edit" on a user
   - Navigate to `/admin/users/edit/{id}`
   - Form displays current user data
   - Admin modifies fields (username, password, role, enabled)
   - Submit to `/admin/users/edit` (POST)
   - System updates user (re-encrypts password if changed)
   - Redirect to `/admin/users`
5. **Delete User Flow**:
   - Admin clicks "Delete" on a user
   - Navigate to `/admin/users/delete/{id}` (confirmation page)
   - System displays user details for confirmation
   - Admin confirms deletion
   - Submit to `/admin/users/delete` (POST)
   - System deletes user from database
   - Redirect to `/admin/users`

## Security Features

- **Password Security**: All passwords are hashed using BCrypt with strength factor 12
- **Role-Based Access Control**: Admin routes require `ROLE_ADMIN`
- **Session Management**: Secure session handling with automatic cleanup
- **Input Validation**: Server-side validation for all user inputs
- **Username Uniqueness**: Prevents duplicate usernames during registration

## UI Components

### Login Page (`login.html`)
- Username input field
- Password input field
- Submit button
- Link to registration page
- Error message display area
- Success message display area (for registration confirmation, logout confirmation)

### Registration Page (`register.html`)
- Username input field (with validation)
- Password input field (with validation)
- Confirm Password input field (with validation)
- Submit button
- Link back to login page
- Validation error display area

### User Management Page (`admin/users.html`)
- Table/list of all users
- Columns: ID, Username, Role, Enabled Status
- Action buttons per user: Edit, Delete
- Navigation back to main application

### Edit User Page (`admin/editUser.html`)
- Form with user fields:
  - Username (editable)
  - Password (editable, will be re-encrypted if changed)
  - Role (editable: `ROLE_USER` or `ROLE_ADMIN`)
  - Enabled checkbox (editable)
- Submit button
- Cancel/Back button

### Delete User Confirmation Page (`admin/deleteUser.html`)
- Display user information for confirmation
- Warning message about deletion
- Confirm delete button
- Cancel button