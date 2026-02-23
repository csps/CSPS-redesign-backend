# Admin Access Management Documentation

## Overview

This document describes the newly implemented admin access management functionality that allows granting and revoking admin privileges to students, along with position management capabilities.

## New Service Methods

### AdminService Interface

#### `grantAdminAccess(String studentId, AdminPosition position)`

**Purpose**: Grants admin access to an existing student by assigning them an admin position.

**Parameters**:

- `studentId` (String): The student ID of the student to grant admin access
- `position` (AdminPosition): The admin position to assign

**Returns**: `AdminResponseDTO` - The created admin record details

**Throws**:

- `StudentNotFoundException` - If student with given ID doesn't exist
- `IllegalArgumentException` - If student is already an admin
- `PositionAlreadyTakenException` - If position is already taken (except DEVELOPER)

**Security**: Requires ADMIN_EXECUTIVE role

#### `isStudentAlreadyAdmin(String studentId)`

**Purpose**: Checks if a student already has admin privileges.

**Parameters**:

- `studentId` (String): The student ID to check

**Returns**: `boolean` - true if student is already an admin, false otherwise

**Logic**: Compares `student.getUserAccount().getUserAccountId()` with existing admin user account IDs

**Throws**:

- `StudentNotFoundException` - If student with given ID doesn't exist

#### `getAvailablePositions()`

**Purpose**: Retrieves all admin positions that are currently available for assignment.

**Parameters**: None

**Returns**: `List<AdminPosition>` - List of available positions

**Logic**:

- Returns all positions except those already taken
- DEVELOPER position can be assigned multiple times
- Other positions are unique (one per position)

**Security**: Requires ADMIN_EXECUTIVE role

#### `revokeAdminAccess(Long adminId)`

**Purpose**: Revokes admin access from an admin user, converting them back to a student.

**Parameters**:

- `adminId` (Long): The admin ID to revoke access from

**Returns**: `AdminResponseDTO` - The deleted admin record details

**Process**:

1. Finds the admin record
2. Changes the associated user account role from ADMIN to STUDENT
3. Deletes the admin record from database

**Throws**:

- `AdminNotFoundException` - If admin with given ID doesn't exist

**Security**: Requires DEVELOPER role

## New Controller Endpoints

### AdminController

#### `POST /api/admin/grant-access`

**Purpose**: Grant admin access to a student

**Parameters**:

- `studentId` (RequestParam String): Student ID
- `position` (RequestParam AdminPosition): Admin position to assign

**Response**: `GlobalResponseBuilder<AdminResponseDTO>`

**Status Codes**:

- `201 CREATED` - Admin access granted successfully
- `400 BAD_REQUEST` - Invalid parameters or student already admin
- `404 NOT_FOUND` - Student not found
- `403 FORBIDDEN` - Insufficient permissions

**Security**: Requires ADMIN_EXECUTIVE role

**Example Request**:

```
POST /api/admin/grant-access?studentId=20210001&position=TREASURER
```

#### `GET /api/admin/available-positions`

**Purpose**: Get list of available admin positions

**Parameters**: None

**Response**: `GlobalResponseBuilder<List<AdminPosition>>`

**Status Codes**:

- `200 OK` - Positions retrieved successfully

**Security**: Requires ADMIN_EXECUTIVE role

**Example Response**:

```json
{
  "success": true,
  "message": "Available positions retrieved successfully",
  "data": ["PRESIDENT", "VP_INTERNAL", "SECRETARY", "DEVELOPER"]
}
```

#### `DELETE /api/admin/revoke-access/{adminId}`

**Purpose**: Revoke admin access from an admin user

**Parameters**:

- `adminId` (PathVariable Long): Admin ID to revoke

**Response**: `GlobalResponseBuilder<AdminResponseDTO>`

**Status Codes**:

- `200 OK` - Admin access revoked successfully
- `404 NOT_FOUND` - Admin not found
- `403 FORBIDDEN` - Insufficient permissions (not a developer)

**Security**: Requires DEVELOPER role

**Example Request**:

```
DELETE /api/admin/revoke-access/123
```

## Data Transfer Objects (DTOs)

### AdminResponseDTO

**Purpose**: Response DTO for admin-related operations

**Fields**:

- `position` (AdminPosition): The admin position
- `user` (UserResponseDTO): Associated user information

**Structure**:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminResponseDTO {
    private AdminPosition position;
    private UserResponseDTO user;
}
```

### UserResponseDTO

**Purpose**: User information DTO used within AdminResponseDTO

**Fields**:

- `userId` (Long): User profile ID
- `username` (String): Account username
- `firstName` (String): User's first name
- `lastName` (String): User's last name
- `middleName` (String): User's middle name
- `birthDate` (LocalDate): User's birth date
- `email` (String): User's email address
- `role` (UserRole): User's current role (ADMIN/STUDENT)

**Structure**:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private UserRole role;
}
```

## Admin Positions

The system supports the following admin positions:

### Executive Positions (ADMIN_EXECUTIVE role)

- `PRESIDENT`
- `VP_INTERNAL`
- `VP_EXTERNAL`
- `SECRETARY`

### Finance Positions (ADMIN_FINANCE role)

- `TREASURER`
- `ASSISTANT_TREASURER`

### General Positions (ADMIN role)

- `AUDITOR`
- `PIO`
- `PRO`
- `CHIEF_VOLUNTEER`
- `FIRST_YEAR_REPRESENTATIVE`
- `SECOND_YEAR_REPRESENTATIVE`
- `THIRD_YEAR_REPRESENTATIVE`
- `FOURTH_YEAR_REPRESENTATIVE`

### Special Positions

- `DEVELOPER` - Can be assigned multiple times, grants DEVELOPER role

## Implementation Details

### Admin Detection Logic

The system determines if a student is already an admin by comparing UserAccount IDs:

```java
// Check if any admin has the same user account ID as the student
return adminRepository.existsByUserAccountUserAccountId(
    student.getUserAccount().getUserAccountId()
);
```

### Position Availability

Positions are checked for availability except for DEVELOPER:

```java
List<AdminPosition> takenPositions = adminRepository.findAll().stream()
    .filter(admin -> admin.getPosition() != AdminPosition.DEVELOPER)
    .map(Admin::getPosition)
    .collect(Collectors.toList());
```

### Role Transitions

When granting admin access:

1. Student role remains STUDENT in database
2. User account role changes to ADMIN
3. Admin record is created linking to existing user account

When revoking admin access:

1. User account role changes back to STUDENT
2. Admin record is deleted from database

### Security Considerations

- Grant operations require ADMIN_EXECUTIVE role
- Revoke operations require DEVELOPER role (highest privilege)
- Position conflicts are prevented (except for DEVELOPER)
- Student existence is validated before operations

## Error Handling

### Custom Exceptions

- `StudentNotFoundException` - Student doesn't exist
- `AdminNotFoundException` - Admin doesn't exist
- `PositionAlreadyTakenException` - Position already assigned

### Validation

- Student ID existence check
- Admin status verification
- Position availability validation
- Role-based access control

## Usage Examples

### Grant Admin Access

```java
// Service call
AdminResponseDTO result = adminService.grantAdminAccess("20210001", AdminPosition.TREASURER);

// API call
POST /api/admin/grant-access?studentId=20210001&position=TREASURER
```

### Check Admin Status

```java
boolean isAdmin = adminService.isStudentAlreadyAdmin("20210001");
```

### Get Available Positions

```java
List<AdminPosition> positions = adminService.getAvailablePositions();
// Returns: [PRESIDENT, VP_INTERNAL, SECRETARY, DEVELOPER, ...]
```

### Revoke Admin Access

```java
AdminResponseDTO result = adminService.revokeAdminAccess(123L);
```
