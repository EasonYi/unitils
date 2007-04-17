package org.unitils.reflectionassert;

import java.util.Stack;
import java.util.Date;
import java.util.Set;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SimpleCasesComparator extends ReflectionComparator {

    /**
     * todo javadoc
     *
     * @param chainedComparator
     */
    public SimpleCasesComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }

    /**
     * todo javadoc
     *
     * @param left
     * @param right
     * @return
     */
    public boolean canHandle(Object left, Object right) {
        return left == right || left == null || right == null || left.getClass().getName().startsWith("java.lang") ||
                (left instanceof Enum) && (right instanceof Enum) ||
                (left instanceof Date) && (right instanceof Date);
    }

    /**
     * todo javadoc
     *
     * @param left
     * @param right
     * @param fieldStack
     * @param traversedInstancePairs
     * @return
     */
    protected Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs) {
        // check if the same instance is referenced
        if (left == right) {
            return null;
        }
        // check if the left value is null
        if (left == null) {
            return new Difference("Left value null.", left, right, fieldStack);
        }
        // check if the right value is null
        if (right == null) {
            return new Difference("Right value null.", left, right, fieldStack);
        }
        // check if objects are equal
        if (!left.equals(right)) {
            return new Difference("Different object values.", left, right, fieldStack);
        }
        return null;
    }

    /**
     * Gets the double value for the given left Character or Number instance.
     *
     * @param object the Character or Number, not null
     * @return the value as a double
     */                              
    private double getDoubleValue(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        return (double) ((Character) object).charValue();
    }
}
