package org.csps.backend.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(Long membershipId) {
        super("Student membership with ID " + membershipId + " not found");
    }
}