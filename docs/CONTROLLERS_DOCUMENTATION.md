# Controllers Documentation

## Roles and Permissions

This API uses role-based access control with the following roles:

- **STUDENT**: Regular users who can access student-specific features like shopping cart, orders, and their profile.
- **ADMIN**: General administrators who can manage merchandise, view students, and access basic admin features.
- **ADMIN_FINANCE**: Finance administrators who can access sales statistics, transaction management, and finance dashboard.
- **ADMIN_EXECUTIVE**: Executive administrators with highest privileges, including managing other admins, students, and events.

Roles are hierarchical, with ADMIN_EXECUTIVE having access to all ADMIN and ADMIN_FINANCE permissions, and ADMIN having access to basic administrative functions.

### Admin Positions

Admins can have specific positions that grant different levels of access. The available positions and their corresponding roles are:

**ADMIN_EXECUTIVE positions** (highest privileges):

- PRESIDENT
- VP_INTERNAL
- VP_EXTERNAL
- SECRETARY

**ADMIN_FINANCE positions** (finance and sales management):

- TREASURER
- ASSISTANT_TREASURER

**ADMIN positions** (general administration):

- AUDITOR
- PIO
- PRO
- CHIEF_VOLUNTEER
- FIRST_YEAR_REPRESENTATIVE
- SECOND_YEAR_REPRESENTATIVE
- THIRD_YEAR_REPRESENTATIVE
- FOURTH_YEAR_REPRESENTATIVE
- DEVELOPER

## AdminController

**Base Path:** `/api/admin`

**Purpose:** Manages admin users and access control.

**Endpoints:**

- `POST /add` - Add a new admin (Requires ADMIN_EXECUTIVE role)
- `POST /setup` - Setup initial admin (Permit all)
- `DELETE /delete/{adminId}` - Delete an admin (Requires ADMIN_EXECUTIVE role)
- `POST /grant-access` - Grant admin access to a student by studentId and position (Requires ADMIN_EXECUTIVE role)
- `GET /available-positions` - Get list of available admin positions (Requires ADMIN_EXECUTIVE role)
- `DELETE /revoke-access/{adminId}` - Revoke admin access (Requires DEVELOPER role)

**Note:** For detailed documentation of admin access management methods and DTOs, see [ADMIN_ACCESS_MANAGEMENT.md](ADMIN_ACCESS_MANAGEMENT.md)

## AnnouncementController

**Base Path:** `/api/announcement`

**Purpose:** Handles announcements.

**Endpoints:**

- `GET /all` - Get all announcements (Requires ADMIN or STUDENT role)

## AuthController

**Base Path:** `/api/auth`

**Purpose:** Authentication and user profile management.

**Endpoints:**

- `POST /login` - User login
- `POST /logout` - User logout
- `POST /change-password` - Change password
- `POST /refresh` - Refresh access token
- `GET /profile` - Get student profile (Requires STUDENT role)
- `GET /admin/profile` - Get admin profile (Requires ADMIN role)

## CartController

**Base Path:** `/api/cart`

**Purpose:** Shopping cart management for students.

**Endpoints:**

- `GET /` - Get cart for authenticated student (Requires STUDENT role)
- `GET /total` - Get cart total price (Requires STUDENT role)
- `GET /count` - Get cart item count (Requires STUDENT role)
- `DELETE /` - Clear entire cart (Requires STUDENT role)
- `POST /` - Create cart (Throws exception, not implemented)

## CartItemController

**Base Path:** `/api/cart-items`

**Purpose:** Manage items in shopping cart.

**Endpoints:**

- `POST /` - Add item to cart (Requires STUDENT role)
- `GET /` - Get all items in cart (Requires STUDENT role)
- `PUT /{merchVariantItemId}` - Update cart item quantity (Requires STUDENT role)
- `DELETE /{merchVariantItemId}` - Remove item from cart (Requires STUDENT role)

## EventController

**Base Path:** `/api/event`

**Purpose:** Event management.

**Endpoints:**

- `GET /all` - Get all events (Requires ADMIN or STUDENT role)
- `GET /{id}` - Get event by ID (Requires ADMIN or STUDENT role)
- `GET /image/{s3ImageKey}` - Get event by S3 image key (Requires ADMIN or STUDENT role)
- `GET /` - Get event by date (Requires ADMIN or STUDENT role)
- `GET /upcoming` - Get upcoming events (Requires ADMIN or STUDENT role)
- `GET /by-month` - Get events by month (Requires ADMIN or STUDENT role)
- `GET /past` - Get past events (Requires ADMIN or STUDENT role)
- `POST /add` - Add new event (Requires ADMIN_EXECUTIVE role)
- `DELETE /{id}` - Delete event (Requires ADMIN_EXECUTIVE role)
- `PUT /{id}` - Update event (Requires ADMIN_EXECUTIVE role)
- `PATCH /{id}` - Patch event (Requires ADMIN_EXECUTIVE role)

## FinanceDashboardController

**Base Path:** `/api/dashboard`

**Purpose:** Finance dashboard data.

**Endpoints:**

- `GET /finance` - Get finance dashboard data (Requires ADMIN, ADMIN_FINANCE, or ADMIN_EXECUTIVE role)

## MerchController

**Base Path:** `/api/merch`

**Purpose:** Merchandise management.

**Endpoints:**

- `POST /post` - Create merchandise (Requires ADMIN role)
- `GET /` - Get all merchandise (Requires ADMIN or STUDENT role)
- `GET /summary` - Get merchandise summaries (Requires ADMIN or STUDENT role)
- `GET /type/{type}` - Get merchandise by type (Requires ADMIN or STUDENT role)
- `GET /{id}` - Get merchandise by ID (Requires ADMIN or STUDENT role)
- `PUT /{merchId}` - Update merchandise (Requires ADMIN role)
- `PATCH /{merchId}` - Patch merchandise (Requires ADMIN role)
- `DELETE /{merchId}` - Delete merchandise (Requires ADMIN role)

## MerchVariantController

**Base Path:** `/api/merch-variant`

**Purpose:** Merchandise variant management.

**Endpoints:**

- `POST /{merchId}/add` - Add variant to merchandise (Requires ADMIN role)
- `GET /merch/{merchId}` - Get variants by merchandise ID (Requires ADMIN or STUDENT role)
- `GET /{merchId}/find` - Find variant by key (Requires ADMIN or STUDENT role)
- `POST /{merchVariantId}/upload-image` - Upload variant image (Requires ADMIN role)
- `GET /all` - Get all variants (Requires ADMIN role)
- `DELETE /{merchVariantId}` - Delete variant (Requires ADMIN role)

## MerchVariantItemController

**Base Path:** `/api/merch-variant-item`

**Purpose:** Merchandise variant item management.

**Endpoints:**

- `POST /{merchVariantId}/add` - Add item to variant (Requires ADMIN role)
- `POST /{merchVariantId}/add-multiple` - Add multiple items to variant (Requires ADMIN role)
- `GET /{id}` - Get item by ID (Requires STUDENT or ADMIN role)
- `GET /variant/{merchVariantId}` - Get items by variant ID (Requires STUDENT or ADMIN role)
- `GET /variant/{merchVariantId}/size/{size}` - Get item by variant and size (Requires STUDENT or ADMIN role)
- `PATCH /{id}/stock` - Update stock quantity (Requires ADMIN role)
- `PATCH /{id}/price` - Update price (Requires ADMIN role)
- `DELETE /{id}` - Delete item (Requires ADMIN role)

## OrderController

**Base Path:** `/api/orders`

**Purpose:** Order management.

**Endpoints:**

- `POST /` - Create order (Requires STUDENT role)
- `GET /` - Get all orders (Requires ADMIN role)
- `GET /{orderId}` - Get order by ID (Requires STUDENT or ADMIN role)
- `GET /my-orders` - Get orders for authenticated student (Requires STUDENT role)
- `GET /sorted-by-date` - Get all orders sorted by date (Requires ADMIN role)
- `DELETE /{orderId}` - Delete order (Requires ADMIN role)

## OrderItemController

**Base Path:** `/api/order-items`

**Purpose:** Order item management.

**Endpoints:**

- `POST /` - Create order item (Requires ADMIN role)
- `GET /{id}` - Get order item by ID (Requires STUDENT or ADMIN role)
- `GET /` - Get order items by order ID (Requires STUDENT or ADMIN role)
- `GET /paginated` - Get paginated order items by order ID (Requires STUDENT or ADMIN role)
- `GET /status` - Get order items by status (Requires ADMIN or STUDENT role)
- `PATCH /{id}/status` - Update order item status (Requires ADMIN role)
- `DELETE /{id}` - Delete order item (Requires ADMIN role)

## SalesController

**Base Path:** `/api/sales`

**Purpose:** Sales and transaction management.

**Endpoints:**

- `GET /stats` - Get sales statistics (Requires ADMIN, ADMIN_FINANCE, or ADMIN_EXECUTIVE role)
- `GET /transactions` - Get transactions (Requires ADMIN, ADMIN_FINANCE, or ADMIN_EXECUTIVE role)
- `POST /transactions/{id}/approve` - Approve transaction (Requires ADMIN_FINANCE or ADMIN_EXECUTIVE role)
- `DELETE /transactions/{id}` - Reject transaction (Requires ADMIN_FINANCE or ADMIN_EXECUTIVE role)

## StudentController

**Base Path:** `/api/students`

**Purpose:** Student management.

**Endpoints:**

- `POST /` - Create student (Requires ADMIN_EXECUTIVE role)
- `GET /` - Get all students (Requires ADMIN role)
- `GET /{studentId}` - Get student by ID (Requires ADMIN role)

## StudentMembershipController

**Base Path:** `/api/student-memberships`

**Purpose:** Student membership management.

**Endpoints:**

- `POST /` - Create student membership (Requires ADMIN role)
- `GET /` - Get all student memberships (Requires ADMIN role)
- `GET /{membershipId}` - Get membership by ID (Requires ADMIN role)
- `GET /student/{studentId}` - Get memberships by student ID (Requires ADMIN role)

## UserController

**Base Path:** `/api/user`

**Purpose:** User management.

**Endpoints:**

- `GET /get` - Get all users (Requires ADMIN role)
