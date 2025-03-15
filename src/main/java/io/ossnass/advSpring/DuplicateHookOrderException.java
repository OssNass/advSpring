package io.ossnass.advSpring;

/**
 * This exception is thrown when a hook with the same order and type already exists
 */
public class DuplicateHookOrderException extends RuntimeException {
    /**
     * Constructs a new {@link DuplicateHookOrderException} with the specified order
     *
     * @param order the order of the hook
     */
    public DuplicateHookOrderException(int order) {
        super("Order %d exists".formatted(order));
    }

    /**
     * Constructs a new {@link DuplicateHookOrderException} with the specified order and cause
     *
     * @param order the order of the hook
     * @param cause the cause of the exception
     */
    public DuplicateHookOrderException(int order, Throwable cause) {
        super("Order %d exists".formatted(order), cause);
    }
}
